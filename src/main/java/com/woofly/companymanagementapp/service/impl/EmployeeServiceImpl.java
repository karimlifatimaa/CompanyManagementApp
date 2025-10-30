package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
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
        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
        });
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

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
        });

        employee.setFullName(employeeRequest.getFullName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setSalary(employeeRequest.getSalary());
        employee.setPosition(employeeRequest.getPosition());
        Employee updatedEmployee = employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        response.setId(updatedEmployee.getId());
        response.setFullName(updatedEmployee.getFullName());
        response.setEmail(updatedEmployee.getEmail());
        response.setSalary(updatedEmployee.getSalary());
        response.setPosition(updatedEmployee.getPosition());

        return response;
    }

    @Override
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);

    }

    @Override
    public List<EmployeeResponse> findAllEmployee() {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(employee -> {
            EmployeeResponse response = new EmployeeResponse();
            response.setId(employee.getId());
            response.setFullName(employee.getFullName());
            response.setEmail(employee.getEmail());
            response.setSalary(employee.getSalary());
            response.setPosition(employee.getPosition());
            return response;
        }).toList();
    }
}
