package com.woofly.companymanagementapp.service;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.model.Employee;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest employeeRequest);
    EmployeeResponse findEmployeeById(Long id);
    EmployeeResponse updateEmployee(Long id,EmployeeRequest employeeRequest);
    EmployeeResponse deleteEmployee(Long id);
    List<EmployeeResponse> findAllEmployee();

}
