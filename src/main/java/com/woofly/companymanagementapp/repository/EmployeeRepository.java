package com.woofly.companymanagementapp.repository;

import com.woofly.companymanagementapp.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
