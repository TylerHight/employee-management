package com.ibm.fscc.employeeservice.services

import com.ibm.fscc.common.exception.ResourceNotFoundException
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import com.ibm.fscc.employeeservice.dto.EmployeeDto
import com.ibm.fscc.employeeservice.dto.EmployeeUpdateDto
import com.ibm.fscc.employeeservice.exception.DuplicateEmployeeEmailException
import com.ibm.fscc.employeeservice.kafka.EmployeeAdminAddedProducer
import com.ibm.fscc.employeeservice.kafka.EmployeeDeletedProducer
import com.ibm.fscc.employeeservice.kafka.EmployeeEmailChangedProducer
import com.ibm.fscc.employeeservice.kafka.EmployeeRoleChangeProducer
import com.ibm.fscc.employeeservice.model.EmployeeEntity
import com.ibm.fscc.employeeservice.repository.EmployeeRepository
import com.ibm.fscc.employeeservice.services.impl.EmployeeServiceImpl
import com.ibm.fscc.kafka.dto.EmployeeEventDto
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import spock.lang.Specification
import org.springframework.data.jpa.domain.Specification
import spock.lang.Subject

/**
 * Spock tests for EmployeeService focusing on stable business logic
 * that will persist throughout development.
 */
class EmployeeServiceSpec extends spock.lang.Specification {

    EmployeeRepository employeeRepository = Mock()
    EmployeeRoleChangeProducer roleChangeProducer = Mock()
    EmployeeAdminAddedProducer adminAddedProducer = Mock()
    EmployeeDeletedProducer deletedProducer = Mock()
    EmployeeEmailChangedProducer emailChangedProducer = Mock()

    @Subject
    EmployeeServiceImpl employeeService = new EmployeeServiceImpl(
            employeeRepository,
            roleChangeProducer,
            adminAddedProducer,
            deletedProducer,
            emailChangedProducer
    )

    def setup() {
        // Setup security context for tests
        SecurityContextHolder.clearContext()
    }

    def cleanup() {
        SecurityContextHolder.clearContext()
    }

    // ========== EMPLOYEE CREATION TESTS ==========

    def "addEmployeeFromClient should generate UUID and save employee"() {
        given: "a valid employee DTO"
        def employeeDto = createValidEmployeeDto()

        and: "repository will save successfully"
        def savedEntity = createEmployeeEntity()
        employeeRepository.save(_ as EmployeeEntity) >> savedEntity

        when: "adding employee from client"
        def result = employeeService.addEmployeeFromClient(employeeDto)

        then: "employee is saved with generated UUID"
        result.userId != null
        result.email == employeeDto.email

        and: "Kafka event is sent"
        1 * adminAddedProducer.sendEmployeeAdminAdded(_ as EmployeeEventDto)
    }

    def "addEmployeeFromClient should set default role to USER when not provided"() {
        given: "an employee DTO without role"
        def employeeDto = createValidEmployeeDto()
        employeeDto.role = null

        and: "repository will save successfully"
        def savedEntity = createEmployeeEntity()
        savedEntity.role = "USER"
        employeeRepository.save(_ as EmployeeEntity) >> savedEntity

        when: "adding employee"
        def result = employeeService.addEmployeeFromClient(employeeDto)

        then: "role defaults to USER"
        result.role == "USER"
    }

    def "addEmployeeFromClient should throw DuplicateEmployeeEmailException for duplicate email"() {
        given: "an employee DTO"
        def employeeDto = createValidEmployeeDto()

        and: "repository throws DuplicateKeyException"
        employeeRepository.save(_ as EmployeeEntity) >> { throw new DuplicateKeyException("Duplicate email") }

        when: "adding employee with duplicate email"
        employeeService.addEmployeeFromClient(employeeDto)

        then: "DuplicateEmployeeEmailException is thrown"
        thrown(DuplicateEmployeeEmailException)
    }

    def "addEmployeeFromApprovedEvent should create employee with default values"() {
        given: "an approved event DTO"
        def eventDto = new EmployeeEventDto(
                userId: "user-123",
                firstName: "John",
                lastName: "Doe",
                email: "john.doe@example.com",
                role: "USER"
        )

        and: "no existing employee with that email"
        employeeRepository.findByEmail(eventDto.email) >> Optional.empty()

        and: "repository saves successfully"
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity entity -> entity }

        when: "adding employee from approved event"
        def result = employeeService.addEmployeeFromApprovedEvent(eventDto)

        then: "employee is created with default values"
        result.userId == eventDto.userId
        result.role == "USER"
        result.address == "Unknown"
        result.city == "Unknown"
        result.state == "Unknown"
        result.zip == "00000"
    }

    def "addEmployeeFromApprovedEvent should throw exception for duplicate email (idempotency)"() {
        given: "an event DTO"
        def eventDto = new EmployeeEventDto(
                userId: "user-123",
                email: "existing@example.com"
        )

        and: "employee already exists with that email"
        employeeRepository.findByEmail(eventDto.email) >> Optional.of(createEmployeeEntity())

        when: "trying to add duplicate employee"
        employeeService.addEmployeeFromApprovedEvent(eventDto)

        then: "DuplicateEmployeeEmailException is thrown"
        thrown(DuplicateEmployeeEmailException)
    }

    // ========== EMPLOYEE RETRIEVAL TESTS ==========

    def "getEmployees should return list of all employees when no search criteria are provided"() {
        given:
        def entities = [createEmployeeEntity(), createEmployeeEntity()]
        def pageable = PageRequest.of(0, 10)

        employeeRepository.findAll(_ as Specification, _ as Pageable) >>
                new PageImpl(entities)

        when:
        def resultPage = employeeService.getEmployees(null, pageable)

        then:
        resultPage.content.size() == 2
    }

    def "getEmployeeByUserId should return employee when found"() {
        given: "an employee exists"
        def entity = createEmployeeEntity()
        employeeRepository.findByUserId(entity.userId) >> Optional.of(entity)

        when: "getting employee by userId"
        def result = employeeService.getEmployeeByUserId(entity.userId)

        then: "employee is returned"
        result.userId == entity.userId
        result.email == entity.email
    }

    def "getEmployeeByUserId should throw ResourceNotFoundException when not found"() {
        given: "no employee exists"
        employeeRepository.findByUserId("non-existent") >> Optional.empty()

        when: "getting non-existent employee"
        employeeService.getEmployeeByUserId("non-existent")

        then: "ResourceNotFoundException is thrown"
        def exception = thrown(ResourceNotFoundException)
        exception.message.contains("userId")
    }

    // ========== EMPLOYEE UPDATE TESTS ==========

    def "updateEmployeePartial should only update non-null fields"() {
        given: "an existing employee"
        def existingEntity = createEmployeeEntity()
        existingEntity.firstName = "John"
        existingEntity.lastName = "Doe"
        existingEntity.email = "john@example.com"

        and: "update DTO with only firstName"
        def updateDto = new EmployeeUpdateDto()
        updateDto.firstName = "Jane"

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository returns existing employee"
        employeeRepository.findById(1L) >> Optional.of(existingEntity)
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity entity -> entity }

        when: "updating employee"
        def result = employeeService.updateEmployeePartial(1L, updateDto)

        then: "only firstName is updated"
        result.firstName == "Jane"
        result.lastName == "Doe"  // unchanged
        result.email == "john@example.com"  // unchanged
    }

    def "updateEmployeePartial should send Kafka event when role changes"() {
        given: "an existing employee"
        def existingEntity = createEmployeeEntity()
        existingEntity.role = "USER"

        and: "update DTO with new role"
        def updateDto = new EmployeeUpdateDto()
        updateDto.role = "ADMIN"

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository operations"
        employeeRepository.findById(1L) >> Optional.of(existingEntity)
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity entity -> entity }

        when: "updating employee role"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "role change event is sent"
        1 * roleChangeProducer.sendRoleChange(_)
    }

    def "updateEmployeePartial should send Kafka event when email changes"() {
        given: "an existing employee"
        def existingEntity = createEmployeeEntity()
        existingEntity.email = "old@example.com"

        and: "update DTO with new email"
        def updateDto = new EmployeeUpdateDto()
        updateDto.email = "new@example.com"

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository operations"
        employeeRepository.findById(1L) >> Optional.of(existingEntity)
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity entity -> entity }

        when: "updating employee email"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "email changed event is sent"
        1 * emailChangedProducer.sendEmailChanged(_)
    }

    def "updateEmployeePartial should throw exception for duplicate email"() {
        given: "an existing employee"
        def existingEntity = createEmployeeEntity()

        and: "update DTO with duplicate email"
        def updateDto = new EmployeeUpdateDto()
        updateDto.email = "duplicate@example.com"

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository operations"
        employeeRepository.findById(1L) >> Optional.of(existingEntity)
        employeeRepository.save(_ as EmployeeEntity) >> { throw new DuplicateKeyException("Duplicate email") }

        when: "updating with duplicate email"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "DuplicateEmployeeEmailException is thrown"
        thrown(DuplicateEmployeeEmailException)
    }

    // ========== EMPLOYEE DELETION TESTS ==========

    def "deleteEmployeeById should send Kafka event before deletion"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository returns employee"
        employeeRepository.findById(1L) >> Optional.of(entity)

        when: "deleting employee"
        employeeService.deleteEmployeeById(1L)

        then: "Kafka event is sent"
        1 * deletedProducer.sendEmployeeDeleted(_)

        and: "employee is deleted"
        1 * employeeRepository.deleteById(1L)
    }

    def "deleteEmployeeById should throw ResourceNotFoundException when not found"() {
        given: "no employee exists"
        employeeRepository.findById(999L) >> Optional.empty()

        when: "deleting non-existent employee"
        employeeService.deleteEmployeeById(999L)

        then: "ResourceNotFoundException is thrown"
        thrown(ResourceNotFoundException)
    }

    def "deleteEmployeeByEmail should send Kafka event before deletion"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()

        and: "setup security context as admin"
        setupSecurityContext("admin-user", true)

        and: "repository returns employee"
        employeeRepository.findByEmail(entity.email) >> Optional.of(entity)

        when: "deleting employee by email"
        employeeService.deleteEmployeeByEmail(entity.email)

        then: "Kafka event is sent"
        1 * deletedProducer.sendEmployeeDeleted(_)

        and: "employee is deleted"
        1 * employeeRepository.deleteByEmail(entity.email)
    }

    // ========== AUTHORIZATION TESTS ==========

    def "admin can update any employee"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()
        entity.userId = "other-user"

        and: "current user is admin"
        setupSecurityContext("admin-user", true)

        and: "repository operations"
        employeeRepository.findById(1L) >> Optional.of(entity)
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity e -> e }

        when: "admin updates another user's employee"
        def updateDto = new EmployeeUpdateDto()
        updateDto.firstName = "Updated"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "update succeeds"
        notThrown(AccessDeniedException)
    }

    def "user can update their own employee"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()
        entity.userId = "user-123"

        and: "current user is the owner"
        setupSecurityContext("user-123", false)

        and: "repository operations"
        employeeRepository.findById(1L) >> Optional.of(entity)
        employeeRepository.save(_ as EmployeeEntity) >> { EmployeeEntity e -> e }

        when: "user updates their own employee"
        def updateDto = new EmployeeUpdateDto()
        updateDto.firstName = "Updated"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "update succeeds"
        notThrown(AccessDeniedException)
    }

    def "user cannot update another user's employee"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()
        entity.userId = "other-user"

        and: "current user is not admin and not owner"
        setupSecurityContext("user-123", false)

        and: "repository returns employee"
        employeeRepository.findById(1L) >> Optional.of(entity)

        when: "user tries to update another user's employee"
        def updateDto = new EmployeeUpdateDto()
        updateDto.firstName = "Updated"
        employeeService.updateEmployeePartial(1L, updateDto)

        then: "AccessDeniedException is thrown"
        thrown(AccessDeniedException)
    }

    def "user cannot delete another user's employee"() {
        given: "an existing employee"
        def entity = createEmployeeEntity()
        entity.userId = "other-user"

        and: "current user is not admin and not owner"
        setupSecurityContext("user-123", false)

        and: "repository returns employee"
        employeeRepository.findById(1L) >> Optional.of(entity)

        when: "user tries to delete another user's employee"
        employeeService.deleteEmployeeById(1L)

        then: "AccessDeniedException is thrown"
        thrown(AccessDeniedException)
    }

    // ========== HELPER METHODS ==========

    private EmployeeDto createValidEmployeeDto() {
        def dto = new EmployeeDto()
        dto.firstName = "John"
        dto.lastName = "Doe"
        dto.address = "123 Main St"
        dto.city = "Springfield"
        dto.state = "IL"
        dto.zip = "62701"
        dto.cellPhone = "555-1234"
        dto.homePhone = "555-5678"
        dto.email = "john.doe@example.com"
        dto.role = "USER"
        return dto
    }

    private EmployeeEntity createEmployeeEntity() {
        def entity = new EmployeeEntity()
        entity.id = 1L
        entity.userId = UUID.randomUUID().toString()
        entity.firstName = "John"
        entity.lastName = "Doe"
        entity.address = "123 Main St"
        entity.city = "Springfield"
        entity.state = "IL"
        entity.zip = "62701"
        entity.cellPhone = "555-1234"
        entity.homePhone = "555-5678"
        entity.email = "john.doe@example.com"
        entity.role = "USER"
        return entity
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