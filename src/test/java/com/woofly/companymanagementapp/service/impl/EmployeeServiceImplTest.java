package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.mapper.EmployeeMapper;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.model.Employee;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeRequest employeeRequest;
    private Employee employee;
    private EmployeeResponse employeeResponse;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employeeRequest = new EmployeeRequest();
        employeeRequest.setEmail("test@test.com");
        employeeRequest.setDepartmentId(1L);
        employeeRequest.setFullName("Test User");
        employeeRequest.setPosition("Developer");
        employeeRequest.setSalary(5000.0);

        employee = new Employee();
        employee.setId(1L);
        employee.setEmail("test@test.com");
        employee.setDepartment(department);

        employeeResponse = new EmployeeResponse();
        employeeResponse.setId(1L);
        employeeResponse.setEmail("test@test.com");
    }

    @Test
    void createEmployee_shouldCreateEmployee_whenDataIsValid() {
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.empty());
        when(departmentRepository.findById(employeeRequest.getDepartmentId())).thenReturn(Optional.of(department));
        when(employeeMapper.toEmployee(employeeRequest)).thenReturn(employee);
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toEmployeeResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.createEmployee(employeeRequest);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void createEmployee_shouldThrowException_whenEmailExists() {
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.of(employee));

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.createEmployee(employeeRequest));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void createEmployee_shouldThrowException_whenDepartmentNotFound() {
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.empty());
        when(departmentRepository.findById(employeeRequest.getDepartmentId())).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.createEmployee(employeeRequest));

        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void findEmployeeById_shouldReturnEmployee_whenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeMapper.toEmployeeResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.findEmployeeById(1L);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
    }

    @Test
    void findEmployeeById_shouldThrowException_whenEmployeeDoesNotExist() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.findEmployeeById(1L));
    }

    @Test
    void updateEmployee_shouldUpdateEmployee_whenDataIsValid() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(employeeRequest.getDepartmentId())).thenReturn(Optional.of(department));
        when(employeeRepository.save(employee)).thenReturn(employee);
        when(employeeMapper.toEmployeeResponse(employee)).thenReturn(employeeResponse);

        EmployeeResponse result = employeeService.updateEmployee(1L, employeeRequest);

        assertNotNull(result);
        assertEquals(employeeResponse, result);
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void updateEmployee_shouldThrowException_whenEmployeeNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(1L, employeeRequest));
    }

    @Test
    void updateEmployee_shouldThrowException_whenEmailExistsForAnotherEmployee() {
        Employee anotherEmployee = new Employee();
        anotherEmployee.setId(2L);
        anotherEmployee.setEmail("test@test.com");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.of(anotherEmployee));

        assertThrows(EmployeeAlreadyExistsException.class, () -> employeeService.updateEmployee(1L, employeeRequest));
    }

    @Test
    void updateEmployee_shouldThrowException_whenDepartmentNotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByEmail(employeeRequest.getEmail())).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(employeeRequest.getDepartmentId())).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.updateEmployee(1L, employeeRequest));
    }

    @Test
    void deleteEmployee_shouldDeleteEmployee_whenEmployeeExists() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    void deleteEmployee_shouldThrowException_whenEmployeeDoesNotExist() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    void findAllEmployee_shouldReturnAllEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(employeeMapper.toEmployeeResponse(employee)).thenReturn(employeeResponse);

        Page<EmployeeResponse> result = employeeService.findAllEmployee(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeResponse, result.getContent().get(0));
    }

    @Test
    void findEmployeesByDepartmentId_shouldReturnEmployees_whenDepartmentExists() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.existsById(1L)).thenReturn(true);
        Page<Employee> employeePage = new PageImpl<>(Collections.singletonList(employee), pageable, 1);
        when(employeeRepository.findByDepartmentId(1L, pageable)).thenReturn(employeePage);
        when(employeeMapper.toEmployeeResponse(employee)).thenReturn(employeeResponse);

        Page<EmployeeResponse> result = employeeService.findEmployeesByDepartmentId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(employeeResponse, result.getContent().get(0));
    }

    @Test
    void findEmployeesByDepartmentId_shouldThrowException_whenDepartmentDoesNotExist() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.existsById(1L)).thenReturn(false);

        assertThrows(DepartmentNotFoundException.class, () -> employeeService.findEmployeesByDepartmentId(1L, pageable));
    }
}
