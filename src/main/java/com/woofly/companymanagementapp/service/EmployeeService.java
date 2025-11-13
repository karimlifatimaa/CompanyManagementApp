package com.woofly.companymanagementapp.service;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest employeeRequest) throws EmployeeAlreadyExistsException;
    EmployeeResponse findEmployeeById(Long id) throws EmployeeNotFoundException;
    EmployeeResponse updateEmployee(Long id,EmployeeRequest employeeRequest) throws EmployeeNotFoundException;
    void deleteEmployee(Long id) throws EmployeeNotFoundException;
    List<EmployeeResponse> findAllEmployee();
    List<EmployeeResponse> findEmployeesByDepartmentId(Long departmentId);

}
