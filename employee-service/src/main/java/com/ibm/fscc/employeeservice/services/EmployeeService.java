package com.ibm.fscc.employeeservice.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ibm.fscc.employeeservice.dto.EmployeeDto;
import com.ibm.fscc.employeeservice.dto.EmployeeResponseDto;
import com.ibm.fscc.employeeservice.dto.EmployeeUpdateDto;
import com.ibm.fscc.kafka.dto.EmployeeEventDto;

public interface EmployeeService {

    Page<EmployeeDto> getEmployees(String search, Pageable pageable);

    EmployeeDto getEmployeeByUserId(String userId);

    EmployeeDto addEmployeeFromClient(EmployeeDto employeeDto);

    EmployeeDto addEmployeeFromApprovedEvent(EmployeeEventDto eventDto);

    void deleteEmployeeById(long id);

    void deleteEmployeeByEmail(String Email);

    EmployeeResponseDto updateEmployeePartial(long id, EmployeeUpdateDto employeeUpdateDto);

}
