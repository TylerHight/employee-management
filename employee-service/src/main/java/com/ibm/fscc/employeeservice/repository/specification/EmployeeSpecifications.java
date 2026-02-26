package com.ibm.fscc.employeeservice.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.ibm.fscc.employeeservice.model.EmployeeEntity;

public class EmployeeSpecifications {

    public static Specification<EmployeeEntity> matchesSearch(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            String like = "%" + search.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("firstName")), like),
                    cb.like(cb.lower(root.get("lastName")), like),
                    cb.like(cb.lower(root.get("email")), like),
                    cb.like(cb.lower(root.get("userId")), like),
                    cb.like(cb.lower(root.get("city")), like),
                    cb.like(cb.lower(root.get("state")), like),
                    cb.like(cb.lower(root.get("zip")), like),
                    cb.like(cb.lower(root.get("cellPhone")), like),
                    cb.like(cb.lower(root.get("homePhone")), like),
                    cb.like(cb.lower(root.get("role")), like)
            );
        };
    }

}
