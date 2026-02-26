package com.ibm.fscc.registrationservice.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.ibm.fscc.registrationservice.model.RegistrationEntity;

@Repository
public interface RegistrationRepository extends MongoRepository<RegistrationEntity, String> {
    Optional<RegistrationEntity> findByEmail(String email);

    @Query(value = "{ 'userId': ?0 }", fields = "{ 'email' : 1 }")
    Optional<RegistrationEntity> findEmailByUserId(String userId);

    boolean existsByEmail(String email);

    void deleteByEmail(String email);

    Page<RegistrationEntity> findAll(Pageable pageable);

    Page<RegistrationEntity> findByStatus(String status, Pageable pageable);
}
