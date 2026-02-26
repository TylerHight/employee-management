package com.ibm.fscc.employeeservice.controller;

import java.util.List;

import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

import com.ibm.fscc.employeeservice.dto.EmployeeDto;
import com.ibm.fscc.employeeservice.dto.EmployeeResponseDto;
import com.ibm.fscc.employeeservice.dto.EmployeeUpdateDto;
import com.ibm.fscc.employeeservice.services.EmployeeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(path = "/api/employees")
@Tag(name = "Employee Management", description = "APIs for managing employee records")
@SecurityRequirement(name = "bearer-jwt")
public class EmployeeController {

    private final Environment env;
    private final EmployeeService employeeService;

    public EmployeeController(Environment env, EmployeeService employeeService) {
        this.env = env;
        this.employeeService = employeeService;
    }

    @Operation(
        summary = "Get a paginated list of employees",
        description = "Retrieves a list of all employees in the system. Requires ADMIN role."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - valid login required"),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getEmployees(@RequestParam(required=false) String search, Pageable pageable) {
        return ResponseEntity.ok(employeeService.getEmployees(search, pageable));
    }

    @Operation(
        summary = "Get current user's employee profile",
        description = "Retrieves the employee profile for the currently authenticated user"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved employee profile"),
        @ApiResponse(responseCode = "404", description = "Employee profile not found", content = @Content),
        @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT required", content = @Content)
    })
    @GetMapping("/me")
    public EmployeeDto getCurrentEmployee(
            @Parameter(hidden = true) Authentication auth) {
        String currentUserId = auth.getName();
        return employeeService.getEmployeeByUserId(currentUserId);
    }

    @Operation(
        summary = "Create new employee",
        description = "Creates a new employee record. Requires ADMIN role. Generates a unique userId automatically."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Employee created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required", content = @Content),
        @ApiResponse(responseCode = "409", description = "Employee with this email already exists", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeDto> addEmployeeFromClient(
            @Parameter(description = "Employee data to create", required = true)
            @Valid @RequestBody EmployeeDto employeeDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.addEmployeeFromClient(employeeDto));
    }

    @Operation(
        summary = "Update employee (partial)",
        description = "Updates specific fields of an employee. ADMIN can update any employee, USER can only update their own profile."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Employee updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions", content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content),
        @ApiResponse(responseCode = "409", description = "Email already in use by another employee", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @PatchMapping("/{id}")
    public ResponseEntity<EmployeeResponseDto> updateEmployeePartial(
            @Parameter(description = "Employee ID", required = true, example = "1")
            @PathVariable long id,
            @Parameter(description = "Fields to update (only non-null fields will be updated)", required = true)
            @Valid @RequestBody EmployeeUpdateDto employeeUpdateDto) {
        return ResponseEntity.ok(employeeService.updateEmployeePartial(id, employeeUpdateDto));
    }

    @Operation(
        summary = "Delete employee by ID",
        description = "Deletes an employee record by ID. ADMIN can delete any employee, USER can only delete their own profile. Sends Kafka event on deletion."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions", content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
            @Parameter(description = "Employee ID to delete", required = true, example = "1")
            @PathVariable long id) {
        employeeService.deleteEmployeeById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Delete employee by email",
        description = "Deletes an employee record by email address. ADMIN can delete any employee, USER can only delete their own profile."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Employee deleted successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - insufficient permissions", content = @Content),
        @ApiResponse(responseCode = "404", description = "Employee not found", content = @Content)
    })
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/by-email/{email}")
    public ResponseEntity<Void> deleteEmployeeByEmail(
            @Parameter(description = "Employee email address", required = true, example = "john.doe@example.com")
            @PathVariable String email) {
        employeeService.deleteEmployeeByEmail(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(
        summary = "Service health check",
        description = "Public endpoint to verify the service is running and responsive"
    )
    @ApiResponse(responseCode = "200", description = "Service is healthy")
    @GetMapping(path = "/status/check")
    public String status() {
        return "Working on port " + env.getProperty("server.port") + "!";
    }
}
