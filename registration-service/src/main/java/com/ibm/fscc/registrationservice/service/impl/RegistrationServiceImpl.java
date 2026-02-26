package com.ibm.fscc.registrationservice.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.ibm.fscc.common.exception.ResourceNotFoundException;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.registrationservice.common.KafkaTopics;
import com.ibm.fscc.registrationservice.dto.RegistrationRequestDto;
import com.ibm.fscc.registrationservice.dto.RegistrationResponseDto;
import com.ibm.fscc.registrationservice.dto.RegistrationStatus;
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator;
import com.ibm.fscc.registrationservice.exception.DuplicateEmailException;
import com.ibm.fscc.registrationservice.exception.InvalidStateTransitionException;
import com.ibm.fscc.registrationservice.exception.InvalidStatusException;
import com.ibm.fscc.registrationservice.exception.KafkaPublishException;
import com.ibm.fscc.registrationservice.model.RegistrationEntity;
import com.ibm.fscc.registrationservice.repository.RegistrationRepository;
import com.ibm.fscc.registrationservice.service.ProducerService;
import com.ibm.fscc.registrationservice.service.RegistrationService;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private static final Logger log = LoggerFactory.getLogger(RegistrationServiceImpl.class);

    private final RegistrationRepository registrationRepository;
    private final ProducerService producerService;
    private final KafkaMongoSyncHealthIndicator healthIndicator;

    public RegistrationServiceImpl(RegistrationRepository kafkaRepository, ProducerService producerService,
            KafkaMongoSyncHealthIndicator healthIndicator) {
        this.registrationRepository = kafkaRepository;
        this.producerService = producerService;
        this.healthIndicator = healthIndicator;
    }

    // Get all registrations, regardless of status
    @Override
    public Page<RegistrationResponseDto> getAllRegistrations(Pageable pageable) {
        return registrationRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public Page<RegistrationResponseDto> getAllPendingRegistrations(Pageable pageable) {
        return registrationRepository.findByStatus(RegistrationStatus.PENDING.name(), pageable).map(this::toDto);
    }

    @Override
    public Page<RegistrationResponseDto> getAllApprovedEmployees(Pageable pageable) {
        return registrationRepository.findByStatus(RegistrationStatus.APPROVED.name(), pageable).map(this::toDto);
    }

    @Override
    public RegistrationResponseDto getEmployeeByEmail(String email) {
        // Check if the request is from a user or admin
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        // Ownership check if the request is from a user
        if (!isAdmin) {
            String currentUserId = auth.getName();
            String currentUserEmail = registrationRepository.findEmailByUserId(currentUserId)
                    .map(RegistrationEntity::getEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("Registration", "userId", currentUserId));

            if (!currentUserEmail.equalsIgnoreCase(email)) {
                throw new AccessDeniedException("You are not allowed to access another user's registration");
            }
        }

        RegistrationEntity entity = registrationRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", email));
        return toDto(entity);
    }

    @Override
    @Transactional
    @Retryable(value = { DataAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public RegistrationResponseDto registerEmployee(RegistrationRequestDto request) {
        if (registrationRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        try {
            RegistrationEntity entity = new RegistrationEntity();
            BeanUtils.copyProperties(request, entity);

            entity.setUserId(UUID.randomUUID().toString());
            log.debug("[REGISTER] Generated UUID for {}: {}", request.getEmail(), entity.getUserId());
            entity.setStatus(RegistrationStatus.PENDING.name());
            entity.setStatusDate(LocalDateTime.now());

            RegistrationEntity savedEntity = registrationRepository.save(entity);
            log.debug("[REGISTER] Saved to DB - Email: {}, UUID: {}", savedEntity.getEmail(), savedEntity.getUserId());

            // No onboarding event yet — only publish on APPROVED
            log.info("Registered new employee as PENDING: {}", savedEntity.getEmail());

            return toDto(savedEntity);
        } catch (DuplicateKeyException e) {
            throw new DuplicateEmailException(request.getEmail());
        } catch (DataAccessException e) {
            log.error("Database error while registering employee: {}", request.getEmail(), e);
            healthIndicator.recordMongoError();
            throw e; // Will be retried by @Retryable
        }
    }

    @Override
    @Transactional
    @Retryable(value = { DataAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public RegistrationResponseDto declineRegistrationByEmail(String email) {
        RegistrationEntity entity = registrationRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", email));

        RegistrationStatus currentStatus = RegistrationStatus.valueOf(entity.getStatus());
        if (!isValidTransition(currentStatus, RegistrationStatus.DECLINED)) {
            throw new InvalidStateTransitionException(
                    "Cannot decline a registration in status: " + currentStatus);
        }

        try {
            entity.setStatus(RegistrationStatus.DECLINED.name());
            entity.setStatusDate(LocalDateTime.now());
            RegistrationEntity savedEntity = registrationRepository.save(entity);

            log.info("Declined registration for email: {}", entity.getEmail());

            // No onboarding event for DECLINED
            return toDto(savedEntity);
        } catch (DataAccessException e) {
            log.error("Database error while declining registration: {}", email, e);
            healthIndicator.recordMongoError();
            throw e; // Will be retried by @Retryable
        }
    }

    @Override
    @Transactional
    @Retryable(value = { DataAccessException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void deleteRegistrationByEmail(String email) {
        if (!registrationRepository.existsByEmail(email)) {
            throw new ResourceNotFoundException("Registration", "email", email);
        }

        try {
            registrationRepository.deleteByEmail(email);
            log.info("Deleted registration for email: {}", email);
        } catch (DataAccessException e) {
            log.error("Database error while deleting registration: {}", email, e);
            healthIndicator.recordMongoError();
            throw e; // Will be retried by @Retryable
        }
    }

    @Override
    @Transactional
    @Retryable(value = { DataAccessException.class,
            KafkaPublishException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public RegistrationResponseDto approveRegistration(String email) {
        RegistrationEntity entity = registrationRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Registration", "email", email));

        RegistrationStatus currentStatus = RegistrationStatus.valueOf(entity.getStatus());
        log.debug("Current status for {} is {}", email, currentStatus);

        if (!isValidTransition(currentStatus, RegistrationStatus.APPROVED)) {
            log.warn("Invalid transition attempt: {} -> APPROVED", currentStatus);
            throw new InvalidStateTransitionException(
                    "Cannot approve a registration in status: " + currentStatus);
        }

        try {
            // Update MongoDB first
            entity.setStatus(RegistrationStatus.APPROVED.name());
            entity.setStatusDate(LocalDateTime.now());
            RegistrationEntity savedEntity = registrationRepository.save(entity);
            log.info("Updated {} status to APPROVED", email);
            log.debug("[APPROVE] Retrieved from DB - Email: {}, UUID: {}", entity.getEmail(), entity.getUserId());

            // Build EmployeeEventDto for onboarding
            EmployeeEventDto approvedEvent = new EmployeeEventDto(
                    savedEntity.getUserId(),
                    savedEntity.getFirstName(),
                    savedEntity.getLastName(),
                    savedEntity.getEmail(),
                    "USER" // Set default role to USER
            );

            log.debug("[APPROVE] Sending Kafka Event - Email: {}, UUID: {}", approvedEvent.getEmail(),
                    approvedEvent.getUserId());

            // Publish onboarding event to employee-approved topic
            // This will throw KafkaPublishException if it fails, which will trigger a
            // rollback
            try {
                producerService.sendRegistrationEvent(KafkaTopics.EMPLOYEE_APPROVED.topicName(), approvedEvent);
            } catch (KafkaPublishException e) {
                // If Kafka publish fails after MongoDB update, we have a sync issue
                healthIndicator.recordSyncError(
                        "Kafka publish failed after MongoDB update for email: " + email,
                        e);
                throw e; // Will be retried by @Retryable
            }

            return toDto(savedEntity);
        } catch (DataAccessException e) {
            log.error("Database error while approving registration: {}", email, e);
            healthIndicator.recordMongoError();
            throw e; // Will be retried by @Retryable
        }
    }

    private RegistrationResponseDto toDto(RegistrationEntity entity) {
        RegistrationResponseDto dto = new RegistrationResponseDto();
        BeanUtils.copyProperties(entity, dto, "status");

        try {
            dto.setStatus(RegistrationStatus.valueOf(entity.getStatus()));
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidStatusException(entity.getStatus());
        }

        return dto;
    }

    private boolean isValidTransition(RegistrationStatus current, RegistrationStatus next) {
        switch (current) {
            case PENDING:
                return next == RegistrationStatus.APPROVED || next == RegistrationStatus.DECLINED;
            case APPROVED:
            case DECLINED:
            default:
                return false;
        }
    }
}
