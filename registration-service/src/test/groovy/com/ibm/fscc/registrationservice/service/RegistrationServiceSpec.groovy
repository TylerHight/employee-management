package com.ibm.fscc.registrationservice.services

import java.time.LocalDateTime
import java.util.Optional
import java.util.UUID

import com.ibm.fscc.registrationservice.repository.RegistrationRepository
import com.ibm.fscc.registrationservice.model.RegistrationEntity
import com.ibm.fscc.registrationservice.service.impl.RegistrationServiceImpl
import com.ibm.fscc.registrationservice.service.ProducerService
import com.ibm.fscc.registrationservice.config.KafkaMongoSyncHealthIndicator
import com.ibm.fscc.registrationservice.exception.DuplicateEmailException
import com.ibm.fscc.registrationservice.exception.InvalidStateTransitionException
import com.ibm.fscc.common.exception.ResourceNotFoundException
import com.ibm.fscc.registrationservice.dto.RegistrationRequestDto
import com.ibm.fscc.registrationservice.dto.RegistrationResponseDto
import com.ibm.fscc.registrationservice.dto.RegistrationStatus
import com.ibm.fscc.registrationservice.common.KafkaTopics
import com.ibm.fscc.kafka.dto.EmployeeEventDto

import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageImpl
import org.springframework.dao.DuplicateKeyException
import org.springframework.dao.DataAccessException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

import spock.lang.Specification
import spock.lang.Subject

class RegistrationServiceSpec extends Specification {

    RegistrationRepository registrationRepository = Mock()
    ProducerService producerService = Mock()
    KafkaMongoSyncHealthIndicator healthIndicator = Mock()

    @Subject
    RegistrationServiceImpl registrationService = new RegistrationServiceImpl(
        registrationRepository,
        producerService,
        healthIndicator
    )

    def setup() {
        SecurityContextHolder.clearContext()
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    // ========== REGISTRATION CREATION TESTS ==========

    def "registerEmployee should create PENDING registration successfully"() {
        given: "a valid registration request"
        def dto = createValidRegistrationRequestDto()
        registrationRepository.existsByEmail(dto.email) >> false
        registrationRepository.save(_ as RegistrationEntity) >> { RegistrationEntity e -> e }

        when: "registering the employee"
        def result = registrationService.registerEmployee(dto)

        then: "registration is saved as PENDING"
        result.status == RegistrationStatus.PENDING
        result.email == dto.email
    }

    def "registerEmployee should throw DuplicateEmailException for duplicate email"() {
        given: "a registration request DTO"
        def dto = createValidRegistrationRequestDto()
        registrationRepository.existsByEmail(dto.email) >> true

        when: "adding registration with duplicate email"
        registrationService.registerEmployee(dto)

        then: "DuplicateEmailException is thrown"
        thrown(DuplicateEmailException)
    }

    // ========== REGISTRATION RETRIEVAL TESTS ==========

    def "getAllRegistrations should return list of all registrations"() {
        given: "multiple registrations of various statuses exist"
        def entities = [createRegistrationEntity('PENDING'), createRegistrationEntity('APPROVED')]
        def page = new PageImpl<>(entities)
        registrationRepository.findAll(_ as Pageable) >> page

        when: "getting all registrations"
        def result = registrationService.getAllRegistrations(Pageable.unpaged())

        then: "all registrations are returned"
        result.content.size() == 2
    }

    def "getAllPendingRegistrations should return only PENDING registrations"() {
        given: "registration repository returns only PENDING entities"
        def entities = [createRegistrationEntity('PENDING'), createRegistrationEntity('PENDING')]
        def page = new PageImpl<>(entities)
        registrationRepository.findByStatus('PENDING', _ as Pageable) >> page

        when: "fetching pending registrations"
        def result = registrationService.getAllPendingRegistrations(Pageable.unpaged())

        then: "all returned are PENDING"
        result.content.size() == 2
        result.content.every { it.status == RegistrationStatus.PENDING }
    }

    // ========== REGISTRATION UPDATE TESTS ==========

    def "approveRegistration should update status and send Kafka event"() {
        given: "a PENDING registration"
        def entity = createRegistrationEntity('PENDING')
        setupSecurityContext("admin-user", true)

        registrationRepository.findByEmail(entity.email) >> Optional.of(entity)
        registrationRepository.save(_ as RegistrationEntity) >> { RegistrationEntity e ->
            e.status = RegistrationStatus.APPROVED.name()
            e
        }

        when: "approving the registration"
        def result = registrationService.approveRegistration(entity.email)

        then: "status is updated and Kafka event is sent"
        result.status == RegistrationStatus.APPROVED
        1 * producerService.sendRegistrationEvent(KafkaTopics.EMPLOYEE_APPROVED.topicName(), _ as EmployeeEventDto)
    }

    def "approveRegistration should throw InvalidStateTransitionException when status is not PENDING"() {
        given: "an APPROVED registration"
        def entity = createRegistrationEntity('APPROVED')
        registrationRepository.findByEmail(entity.email) >> Optional.of(entity)

        when: "trying to approve again"
        registrationService.approveRegistration(entity.email)

        then: "InvalidStateTransitionException is thrown"
        thrown(InvalidStateTransitionException)
    }

    def "declineRegistrationByEmail should update status to DECLINED"() {
        given: "a PENDING registration"
        def entity = createRegistrationEntity('PENDING')
        registrationRepository.findByEmail(entity.email) >> Optional.of(entity)
        registrationRepository.save(_ as RegistrationEntity) >> { RegistrationEntity e ->
            e.status = RegistrationStatus.DECLINED.name()
            e
        }

        when: "declining registration"
        def result = registrationService.declineRegistrationByEmail(entity.email)

        then: "status is DECLINED"
        result.status == RegistrationStatus.DECLINED
    }

    // ========== REGISTRATION DELETE TESTS ==========

    def "deleteRegistrationByEmail should delete existing registration"() {
        given: "an existing registration"
        def email = "john.doe@example.com"
        registrationRepository.existsByEmail(email) >> true

        when: "deleting by email"
        registrationService.deleteRegistrationByEmail(email)

        then: "delete operation is invoked"
        1 * registrationRepository.deleteByEmail(email)
    }

    def "deleteRegistrationByEmail should throw ResourceNotFoundException if email does not exist"() {
        given: "a non-existent email"
        def email = "missing@example.com"
        registrationRepository.existsByEmail(email) >> false

        when: "trying to delete"
        registrationService.deleteRegistrationByEmail(email)

        then: "ResourceNotFoundException is thrown"
        thrown(ResourceNotFoundException)
    }

    // ========== HELPER METHODS ==========

    private RegistrationEntity createRegistrationEntity(String status) {
        def entity = new RegistrationEntity()
        entity.id = UUID.randomUUID().toString()
        entity.userId = UUID.randomUUID().toString()
        entity.firstName = 'John'
        entity.lastName = 'Doe'
        entity.email = 'john.doe@example.com'
        entity.status = status
        entity.statusDate = LocalDateTime.now()
        return entity
    }

    private RegistrationRequestDto createValidRegistrationRequestDto() {
        def dto = new RegistrationRequestDto()
        dto.firstName = 'John'
        dto.lastName = 'Doe'
        dto.email = 'john.doe@example.com'
        return dto
    }

    private void setupSecurityContext(String userId, boolean isAdmin) {
        def authorities = isAdmin ?
                [new SimpleGrantedAuthority("ROLE_ADMIN")] as Collection<GrantedAuthority> :
                [new SimpleGrantedAuthority("ROLE_USER")] as Collection<GrantedAuthority>

        def authentication = Mock(Authentication) {
            getName() >> userId
            getAuthorities() >> authorities
        }

        def securityContext = Mock(SecurityContext) {
            getAuthentication() >> authentication
        }

        SecurityContextHolder.setContext(securityContext)
    }
}
