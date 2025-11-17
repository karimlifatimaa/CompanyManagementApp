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
import com.woofly.companymanagementapp.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) {
        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
        });

        Department department = departmentRepository.findById(employeeRequest.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeRequest.getDepartmentId()));

        Employee employee = employeeMapper.toEmployee(employeeRequest);
        employee.setDepartment(department);

        Employee dbEmployee = employeeRepository.save(employee);

        return employeeMapper.toEmployeeResponse(dbEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse findEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        return employeeMapper.toEmployeeResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            if (!e.getId().equals(id)) {
                throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
            }
        });

        Department department = departmentRepository.findById(employeeRequest.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeRequest.getDepartmentId()));

        employeeMapper.updateEmployeeFromRequest(employeeRequest, employee);
        employee.setDepartment(department);
        Employee updatedEmployee = employeeRepository.save(employee);

        return employeeMapper.toEmployeeResponse(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findAllEmployee(Pageable pageable) {
        return employeeRepository.findAll(pageable)
                .map(employeeMapper::toEmployeeResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeResponse> findEmployeesByDepartmentId(Long departmentId, Pageable pageable) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException("Department not found with id: " + departmentId);
        }

        return employeeRepository.findByDepartmentId(departmentId, pageable)
                .map(employeeMapper::toEmployeeResponse);
    }
}
