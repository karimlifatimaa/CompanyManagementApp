package com.woofly.companymanagementapp.service;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse createEmployee(EmployeeRequest employeeRequest) throws EmployeeAlreadyExistsException;
    EmployeeResponse findEmployeeById(Long id) throws EmployeeNotFoundException;
    EmployeeResponse updateEmployee(Long id,EmployeeRequest employeeRequest) throws EmployeeNotFoundException;
    void deleteEmployee(Long id) throws EmployeeNotFoundException;
    Page<EmployeeResponse> findAllEmployee(Pageable pageable);
    Page<EmployeeResponse> findEmployeesByDepartmentId(Long departmentId, Pageable pageable);

}
