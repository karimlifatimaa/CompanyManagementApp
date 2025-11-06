package com.woofly.companymanagementapp.repository;

import com.woofly.companymanagementapp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmail(String email);
    Employee findByFullName(String fullName);
}
