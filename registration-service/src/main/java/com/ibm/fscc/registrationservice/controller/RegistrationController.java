package com.ibm.fscc.registrationservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ibm.fscc.registrationservice.dto.RegistrationRequestDto;
import com.ibm.fscc.registrationservice.dto.RegistrationResponseDto;
import com.ibm.fscc.registrationservice.service.RegistrationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping(path = "/api/registration")
public class RegistrationController {

    private final RegistrationService registrationService;
    private final Environment env;

    public RegistrationController(RegistrationService registrationService, Environment env) {
        this.registrationService = registrationService;
        this.env = env;
    }

    // View all registrations
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<Page<RegistrationResponseDto>> getAllEmployees(Pageable pageable) {
        return ResponseEntity.ok(registrationService.getAllRegistrations(pageable));
    }

    // View pending registrations
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<Page<RegistrationResponseDto>> getAllPendingEmployees(Pageable pageable) {
        return ResponseEntity.ok(registrationService.getAllPendingRegistrations(pageable));
    }

    // View approved registrations
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/approved")
    public ResponseEntity<Page<RegistrationResponseDto>> getAllApprovedEmployees(Pageable pageable) {
        return ResponseEntity.ok(registrationService.getAllApprovedEmployees(pageable));
    }

    // Get employee by email. Ownership check moved to service layer
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/{email}")
    public ResponseEntity<RegistrationResponseDto> getEmployeeByEmail(@PathVariable @Email String email) {
        return ResponseEntity.ok(
                registrationService.getEmployeeByEmail(email));
    }

    // Submit a registration
    @PostMapping()
    public ResponseEntity<RegistrationResponseDto> registerEmployee(
            @Valid @RequestBody RegistrationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registrationService.registerEmployee(request));
    }

    // Approve a registration
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{email}/approve")
    public ResponseEntity<RegistrationResponseDto> approveRegistration(@PathVariable @Email String email) {
        return ResponseEntity.ok(registrationService.approveRegistration(email));
    }

    // Decline a registration
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{email}/decline")
    public ResponseEntity<RegistrationResponseDto> declineRegistration(@PathVariable @Email String email) {
        return ResponseEntity.ok(registrationService.declineRegistrationByEmail(email));
    }

    // Delete a registration
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable @Email String email) {
        registrationService.deleteRegistrationByEmail(email);
        return ResponseEntity.noContent().build();
    }

    // Health/status check
    @GetMapping(path = "/status/check")
    public String status() {
        return "Working on port " + env.getProperty("server.port") + "!";
    }
}
