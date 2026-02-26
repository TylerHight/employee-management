package com.ibm.fscc.registrationservice.service;

import org.springframework.data.domain.Pageable;

import com.ibm.fscc.registrationservice.dto.RegistrationRequestDto;
import com.ibm.fscc.registrationservice.dto.RegistrationResponseDto;

import org.springframework.data.domain.Page;

public interface RegistrationService {
    RegistrationResponseDto registerEmployee(RegistrationRequestDto registrationRequestDto);

    RegistrationResponseDto declineRegistrationByEmail(String email);

    void deleteRegistrationByEmail(String email);

    RegistrationResponseDto getEmployeeByEmail(String email);

    Page<RegistrationResponseDto> getAllRegistrations(Pageable pageable);

    Page<RegistrationResponseDto> getAllPendingRegistrations(Pageable pageable);

    Page<RegistrationResponseDto> getAllApprovedEmployees(Pageable pageable);

    RegistrationResponseDto approveRegistration(String email);
}
