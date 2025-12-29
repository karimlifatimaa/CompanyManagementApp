package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.repository.EmployeeRepository;
import com.woofly.companymanagementapp.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class EmployeeServiceIntegrationTest {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    private Department department;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        department = new Department();
        department.setName("Technology");
        department.setLocation("Tech Park");
        department.setPhoneNumber("555444333");
        department = departmentRepository.save(department);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Jane Doe");
        employeeRequest.setPosition("Software Engineer");
        employeeRequest.setEmail("jane.doe@example.com");
        employeeRequest.setSalary(90000.0);
        employeeRequest.setDepartmentId(department.getId());
    }

    @Test
    void createEmployee_shouldSaveEmployee() {
        EmployeeResponse created = employeeService.createEmployee(employeeRequest);

        assertThat(created).isNotNull();
        assertThat(created.getFullName()).isEqualTo("Jane Doe");
        assertThat(created.getDepartmentId()).isEqualTo(department.getId());
        assertThat(employeeRepository.findById(created.getId())).isPresent();
    }

    @Test
    void createEmployee_shouldThrowException_whenEmailExists() {
        employeeService.createEmployee(employeeRequest);

        EmployeeRequest sameEmailRequest = new EmployeeRequest();
        sameEmailRequest.setFullName("Another Name");
        sameEmailRequest.setEmail("jane.doe@example.com");
        sameEmailRequest.setPosition("QA");
        sameEmailRequest.setSalary(80000.0);
        sameEmailRequest.setDepartmentId(department.getId());

        assertThrows(EmployeeAlreadyExistsException.class, () -> {
            employeeService.createEmployee(sameEmailRequest);
        });
    }

    @Test
    void findEmployeeById_shouldReturnCorrectEmployee() {
        EmployeeResponse created = employeeService.createEmployee(employeeRequest);

        EmployeeResponse found = employeeService.findEmployeeById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getEmail()).isEqualTo(employeeRequest.getEmail());
    }

    @Test
    void findEmployeeById_shouldThrowException_whenNotFound() {
        assertThrows(EmployeeNotFoundException.class, () -> {
            employeeService.findEmployeeById(999L);
        });
    }

    @Test
    void updateEmployee_shouldUpdateSuccessfully() {
        EmployeeResponse created = employeeService.createEmployee(employeeRequest);

        EmployeeRequest updateRequest = new EmployeeRequest();
        updateRequest.setFullName("Jane Smith");
        updateRequest.setPosition("Senior Software Engineer");
        updateRequest.setSalary(120000.0);
        updateRequest.setEmail(created.getEmail());
        updateRequest.setDepartmentId(department.getId());

        EmployeeResponse updated = employeeService.updateEmployee(created.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getFullName()).isEqualTo("Jane Smith");
        assertThat(updated.getSalary()).isEqualTo(120000.0);
    }

    @Test
    void deleteEmployee_shouldRemoveEmployee() {
        EmployeeResponse created = employeeService.createEmployee(employeeRequest);
        Long employeeId = created.getId();

        assertThat(employeeRepository.findById(employeeId)).isPresent();
        employeeService.deleteEmployee(employeeId);
        assertThat(employeeRepository.findById(employeeId)).isNotPresent();
    }

    @Test
    void findAllEmployee_shouldReturnPagedResult() {
        employeeService.createEmployee(employeeRequest);

        Page<EmployeeResponse> employees = employeeService.findAllEmployee(PageRequest.of(0, 5));

        assertThat(employees).isNotNull();
        assertThat(employees.getTotalElements()).isEqualTo(1);
        assertThat(employees.getContent().get(0).getFullName()).isEqualTo("Jane Doe");
    }

    @Test
    void findEmployeesByDepartmentId_shouldReturnCorrectEmployees() {
        // Create another department
        Department anotherDepartment = new Department();
        anotherDepartment.setName("Sales");
        anotherDepartment.setLocation("Sales Office");
        anotherDepartment.setPhoneNumber("123123123");
        departmentRepository.save(anotherDepartment);

        // Employee in first department
        employeeService.createEmployee(employeeRequest);

        // Employee in second department
        EmployeeRequest anotherEmployeeRequest = new EmployeeRequest();
        anotherEmployeeRequest.setFullName("John Smith");
        anotherEmployeeRequest.setPosition("Sales Manager");
        anotherEmployeeRequest.setEmail("john.smith@example.com");
        anotherEmployeeRequest.setSalary(100000.0);
        anotherEmployeeRequest.setDepartmentId(anotherDepartment.getId());
        employeeService.createEmployee(anotherEmployeeRequest);

        // Find employees in the first department
        Page<EmployeeResponse> employees = employeeService.findEmployeesByDepartmentId(department.getId(), PageRequest.of(0, 5));

        assertThat(employees).isNotNull();
        assertThat(employees.getTotalElements()).isEqualTo(1);
        assertThat(employees.getContent().get(0).getFullName()).isEqualTo("Jane Doe");
    }
}
