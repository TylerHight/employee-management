package com.ibm.fscc.registrationservice.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "employee_registration")
public class RegistrationEntity {

    @Id
    private String id;

    private String userId; // Shared stable identifier across services
    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String email;

    private String status;
    private LocalDateTime statusDate;
}
