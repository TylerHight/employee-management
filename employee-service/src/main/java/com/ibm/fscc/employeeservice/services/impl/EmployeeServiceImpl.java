package com.ibm.fscc.employeeservice.services.impl;

import com.ibm.fscc.common.exception.ResourceNotFoundException;
import com.ibm.fscc.employeeservice.dto.EmployeeDto;
import com.ibm.fscc.employeeservice.dto.EmployeeResponseDto;
import com.ibm.fscc.employeeservice.dto.EmployeeUpdateDto;
import com.ibm.fscc.employeeservice.exception.DuplicateEmployeeEmailException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.ibm.fscc.employeeservice.kafka.EmployeeAdminAddedProducer;
import com.ibm.fscc.employeeservice.kafka.EmployeeDeletedProducer;
import com.ibm.fscc.employeeservice.kafka.EmployeeEmailChangedProducer;
import com.ibm.fscc.employeeservice.kafka.EmployeeRoleChangeProducer;
import com.ibm.fscc.employeeservice.model.EmployeeEntity;
import com.ibm.fscc.employeeservice.repository.EmployeeRepository;
import com.ibm.fscc.employeeservice.repository.specification.EmployeeSpecifications;
import com.ibm.fscc.employeeservice.services.EmployeeService;
import com.ibm.fscc.kafka.dto.EmployeeDeletedEvent;
import com.ibm.fscc.kafka.dto.EmployeeEmailChangedEvent;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;
import com.ibm.fscc.kafka.dto.EmployeeRoleChangeEvent;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final EmployeeRepository employeeRepository;
    private final EmployeeRoleChangeProducer employeeRoleChangeProducer;
    private final EmployeeAdminAddedProducer employeeAdminAddedProducer;
    private final EmployeeDeletedProducer employeeDeletedProducer;
    private final EmployeeEmailChangedProducer employeeEmailChangedProducer;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
            EmployeeRoleChangeProducer employeeRoleChangeProducer,
            EmployeeAdminAddedProducer employeeAdminAddedProducer,
            EmployeeDeletedProducer employeeDeletedProducer,
            EmployeeEmailChangedProducer employeeEmailChangedProducer) {
        this.employeeRepository = employeeRepository;
        this.employeeRoleChangeProducer = employeeRoleChangeProducer;
        this.employeeAdminAddedProducer = employeeAdminAddedProducer;
        this.employeeDeletedProducer = employeeDeletedProducer;
        this.employeeEmailChangedProducer = employeeEmailChangedProducer;
    }

    @Override
    public Page<EmployeeDto> getEmployees(String search, Pageable pageable) {
        return employeeRepository.findAll(EmployeeSpecifications.matchesSearch(search), pageable).map(this::toDto);
    }

    @Override
    public EmployeeDto getEmployeeByUserId(String userId) {
        EmployeeEntity employee = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "userId", userId));
        return toDto(employee);
    }

    @Override
    public EmployeeDto addEmployeeFromClient(@Valid EmployeeDto employeeDto) {
        try {
            EmployeeEntity entity = toEntityForCreate(employeeDto);
            entity.setUserId(UUID.randomUUID().toString());
            EmployeeEntity savedEntity = employeeRepository.save(entity);

            // Create and send Kafka event for admin-added employee
            EmployeeEventDto eventDto = new EmployeeEventDto();
            eventDto.setUserId(savedEntity.getUserId());
            eventDto.setFirstName(savedEntity.getFirstName());
            eventDto.setLastName(savedEntity.getLastName());
            eventDto.setEmail(savedEntity.getEmail());
            eventDto.setRole(savedEntity.getRole());

            employeeAdminAddedProducer.sendEmployeeAdminAdded(eventDto);
            logger.info("Admin-added employee event sent for userId: {} with role: {}",
                    savedEntity.getUserId(), savedEntity.getRole());

            return toDto(savedEntity);
        } catch (DuplicateKeyException e) {
            throw new DuplicateEmployeeEmailException(employeeDto.getEmail());
        }
    }

    @Transactional
    @Override
    public EmployeeDto addEmployeeFromApprovedEvent(EmployeeEventDto eventDto) {
        logger.info("Received EmployeeEventDto in employee-service with userId: {}", eventDto.getUserId());

        // Idempotency check - avoid duplicate employee records
        if (employeeRepository.findByEmail(eventDto.getEmail()).isPresent()) {
            logger.warn("Employee record already exists for email {}, skipping creation", eventDto.getEmail());
            throw new DuplicateEmployeeEmailException(eventDto.getEmail());
        }

        EmployeeEntity entity = new EmployeeEntity();
        entity.setUserId(eventDto.getUserId());
        entity.setFirstName(eventDto.getFirstName());
        entity.setLastName(eventDto.getLastName());
        entity.setEmail(eventDto.getEmail());

        // Set defaults for required fields not in the event
        entity.setRole("USER");
        entity.setAddress("Unknown");
        entity.setCity("Unknown");
        entity.setState("Unknown");
        entity.setZip("00000");
        entity.setCellPhone("000-000-0000");
        entity.setHomePhone("000-000-0000");

        employeeRepository.save(entity);

        return toDto(entity);
    }

    @Override
    public void deleteEmployeeById(long id) {
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        checkOwnerOrAdmin(employee);

        // Send employee deleted event before deleting
        EmployeeDeletedEvent event = new EmployeeDeletedEvent();
        event.setUserId(employee.getUserId());
        event.setEmail(employee.getEmail());
        employeeDeletedProducer.sendEmployeeDeleted(event);
        logger.info("Employee deleted event sent for userId: {} with email: {}",
                employee.getUserId(), employee.getEmail());

        employeeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteEmployeeByEmail(String email) {
        EmployeeEntity employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "email", email));
        checkOwnerOrAdmin(employee);

        // Send employee deleted event before deleting
        EmployeeDeletedEvent event = new EmployeeDeletedEvent();
        event.setUserId(employee.getUserId());
        event.setEmail(employee.getEmail());
        employeeDeletedProducer.sendEmployeeDeleted(event);
        logger.info("Employee deleted event sent for userId: {} with email: {}",
                employee.getUserId(), employee.getEmail());

        employeeRepository.deleteByEmail(email);
    }

    @Transactional
    @Override
    public EmployeeResponseDto updateEmployeePartial(long id, EmployeeUpdateDto updateDto) {
        EmployeeEntity existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        checkOwnerOrAdmin(existing);

        String oldRole = existing.getRole();
        String oldEmail = existing.getEmail();

        updateNonNullFields(updateDto, existing);

        try {
            EmployeeEntity saved = employeeRepository.save(existing);

            // If role changed, produce Kafka event
            if (updateDto.getRole() != null && !updateDto.getRole().isBlank() && !updateDto.getRole().equals(oldRole)) {
                EmployeeRoleChangeEvent event = new EmployeeRoleChangeEvent();
                event.setUserId(saved.getUserId());
                event.setNewRole(saved.getRole());
                employeeRoleChangeProducer.sendRoleChange(event);
            }

            // If email changed, produce Kafka event
            if (updateDto.getEmail() != null && !updateDto.getEmail().isBlank()
                    && !updateDto.getEmail().equals(oldEmail)) {
                EmployeeEmailChangedEvent event = new EmployeeEmailChangedEvent();
                event.setUserId(saved.getUserId());
                event.setOldEmail(oldEmail);
                event.setNewEmail(saved.getEmail());
                employeeEmailChangedProducer.sendEmailChanged(event);
                logger.info("Employee email changed event sent for userId: {} | oldEmail: {} | newEmail: {}",
                        saved.getUserId(), oldEmail, saved.getEmail());
            }

            return toResponseDto(saved);
        } catch (DuplicateKeyException e) {
            throw new DuplicateEmployeeEmailException(updateDto.getEmail());
        }
    }

    // ----- Mapping Helpers -----
    private EmployeeEntity toEntityForCreate(EmployeeDto dto) {
        EmployeeEntity entity = new EmployeeEntity();
        BeanUtils.copyProperties(dto, entity, "id", "userId");

        // Apply default role if not provided
        if (entity.getRole() == null || entity.getRole().isBlank()) {
            entity.setRole("USER");
        }
        return entity;
    }

    private EmployeeDto toDto(EmployeeEntity entity) {
        EmployeeDto dto = new EmployeeDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private EmployeeResponseDto toResponseDto(EmployeeEntity entity) {
        EmployeeResponseDto dto = new EmployeeResponseDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    private void updateNonNullFields(EmployeeUpdateDto source, EmployeeEntity target) {
        if (source.getFirstName() != null && !source.getFirstName().isBlank()) {
            target.setFirstName(source.getFirstName());
        }
        if (source.getLastName() != null && !source.getLastName().isBlank()) {
            target.setLastName(source.getLastName());
        }
        if (source.getAddress() != null && !source.getAddress().isBlank()) {
            target.setAddress(source.getAddress());
        }
        if (source.getCity() != null && !source.getCity().isBlank()) {
            target.setCity(source.getCity());
        }
        if (source.getState() != null && !source.getState().isBlank()) {
            target.setState(source.getState());
        }
        if (source.getZip() != null && !source.getZip().isBlank()) {
            target.setZip(source.getZip());
        }
        if (source.getCellPhone() != null && !source.getCellPhone().isBlank()) {
            target.setCellPhone(source.getCellPhone());
        }
        if (source.getHomePhone() != null && !source.getHomePhone().isBlank()) {
            target.setHomePhone(source.getHomePhone());
        }
        if (source.getEmail() != null && !source.getEmail().isBlank()) {
            target.setEmail(source.getEmail());
        }
        if (source.getRole() != null && !source.getRole().isBlank()) {
            target.setRole(source.getRole());
        }
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private void checkOwnerOrAdmin(EmployeeEntity employee) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUserId = auth.getName(); // from JWT
        if (!isAdmin(auth) && !currentUserId.equals(employee.getUserId())) {
            throw new AccessDeniedException("You are not allowed to access or modify this employee");
        }
    }

}
