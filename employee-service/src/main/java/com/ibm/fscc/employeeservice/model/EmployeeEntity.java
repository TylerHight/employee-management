package com.ibm.fscc.employeeservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "employees")
public class EmployeeEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 36)
    private String userId;
    
    @Column(nullable = false, length = 50)
    private String firstName;
    
    @Column(nullable = false, length = 50)
    private String lastName;
    
    @Column(nullable = false, length = 200)
    private String address;
    
    @Column(nullable = false, length = 100)
    private String city;
    
    @Column(nullable = false, length = 50)
    private String state;
    
    @Column(nullable = false, length = 10)
    private String zip;
    
    @Column(nullable = false, length = 15)
    private String cellPhone;
    
    @Column(nullable = false, length = 15)
    private String homePhone;
    
    @Column(unique = true, nullable = false, length = 100)
    private String email;
    
    @Column(nullable = false, length = 50)
    private String role = "USER";
}
