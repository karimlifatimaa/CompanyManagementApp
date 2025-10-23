package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.model.Employee;
import com.woofly.companymanagementapp.repository.EmployeeRepository;
import com.woofly.companymanagementapp.service.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) {
        Employee employee = new Employee();
        employee.setFullName(employeeRequest.getFullName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setSalary(employeeRequest.getSalary());
        employee.setPosition(employeeRequest.getPosition());

        Employee dbEmployee = employeeRepository.save(employee);

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(dbEmployee.getId());
        employeeResponse.setFullName(dbEmployee.getFullName());
        employeeResponse.setEmail(dbEmployee.getEmail());
        employeeResponse.setSalary(dbEmployee.getSalary());
        employeeResponse.setPosition(dbEmployee.getPosition());
        return employeeResponse;
    }

    @Override
    public EmployeeResponse findEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(
                        () -> new EmployeeNotFoundException( "Employee not found")
                );

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(employee.getId());
        employeeResponse.setFullName(employee.getFullName());
        employeeResponse.setEmail(employee.getEmail());
        employeeResponse.setSalary(employee.getSalary());
        employeeResponse.setPosition(employee.getPosition());
        return employeeResponse;
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest) {
        return null;
    }

    @Override
    public EmployeeResponse deleteEmployee(Long id) {
        return null;
    }

    @Override
    public List<EmployeeResponse> findAllEmployee() {
        return List.of();
    }
}
