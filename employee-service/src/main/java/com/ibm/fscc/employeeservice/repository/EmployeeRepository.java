package com.ibm.fscc.employeeservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ibm.fscc.employeeservice.model.EmployeeEntity;

public interface EmployeeRepository extends
        JpaRepository<EmployeeEntity, Long>,
        JpaSpecificationExecutor<EmployeeEntity> {

    boolean existsByEmail(String email);

    long deleteByEmail(String email);

    Optional<EmployeeEntity> findByUserId(String userId);

    Optional<EmployeeEntity> findByEmail(String email);

    @Query("SELECT e.email FROM EmployeeEntity e WHERE e.id = :id")
    Optional<String> findEmailById(@Param("id") Long id);
}
