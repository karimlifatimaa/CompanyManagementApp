package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.model.Employee;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.repository.EmployeeRepository;
import com.woofly.companymanagementapp.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest employeeRequest) {
        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
        });

        Department department = departmentRepository.findById(employeeRequest.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeRequest.getDepartmentId()));

        Employee employee = new Employee();
        employee.setFullName(employeeRequest.getFullName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setSalary(employeeRequest.getSalary());
        employee.setPosition(employeeRequest.getPosition());
        employee.setDepartment(department); // Set the department

        Employee dbEmployee = employeeRepository.save(employee);

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setId(dbEmployee.getId());
        employeeResponse.setFullName(dbEmployee.getFullName());
        employeeResponse.setEmail(dbEmployee.getEmail());
        employeeResponse.setSalary(dbEmployee.getSalary());
        employeeResponse.setPosition(dbEmployee.getPosition());
        employeeResponse.setDepartmentId(dbEmployee.getDepartment().getId()); // Populate departmentId
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
        employeeResponse.setDepartmentId(employee.getDepartment().getId());
        return employeeResponse;
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest employeeRequest) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.findByEmail(employeeRequest.getEmail()).ifPresent(e -> {
            throw new EmployeeAlreadyExistsException("Employee with email " + employeeRequest.getEmail() + " already exists");
        });

        Department department = departmentRepository.findById(employeeRequest.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeRequest.getDepartmentId()));

        employee.setFullName(employeeRequest.getFullName());
        employee.setEmail(employeeRequest.getEmail());
        employee.setSalary(employeeRequest.getSalary());
        employee.setPosition(employeeRequest.getPosition());
        employee.setDepartment(department); // Update the department
        Employee updatedEmployee = employeeRepository.save(employee);

        EmployeeResponse response = new EmployeeResponse();
        response.setId(updatedEmployee.getId());
        response.setFullName(updatedEmployee.getFullName());
        response.setEmail(updatedEmployee.getEmail());
        response.setSalary(updatedEmployee.getSalary());
        response.setPosition(updatedEmployee.getPosition());
        response.setDepartmentId(updatedEmployee.getDepartment().getId()); // Populate departmentId

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
            if (employee.getDepartment() != null) {
                response.setDepartmentId(employee.getDepartment().getId());
            }
            return response;
        }).toList();
    }

    @Override
    public List<EmployeeResponse> findEmployeesByDepartmentId(Long departmentId) {
        departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + departmentId));

        List<Employee> employees = employeeRepository.findByDepartmentId(departmentId);

        return employees.stream().map(employee -> {
            EmployeeResponse response = new EmployeeResponse();
            response.setId(employee.getId());
            response.setFullName(employee.getFullName());
            response.setEmail(employee.getEmail());
            response.setSalary(employee.getSalary());
            response.setPosition(employee.getPosition());
            if (employee.getDepartment() != null) {
                response.setDepartmentId(employee.getDepartment().getId());
            }
            return response;
        }).toList();
    }
}
