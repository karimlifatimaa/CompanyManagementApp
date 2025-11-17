package com.woofly.companymanagementapp.service;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DepartmentService {
    void deleteDepartment(Long id) throws DepartmentNotFoundException;
    Page<DepartmentResponse> getAllDepartments(Pageable pageable);
    DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) throws DepartmentNotFoundException;
    DepartmentResponse getDepartmentById(Long id) throws DepartmentNotFoundException;
    DepartmentResponse createDepartment(DepartmentRequest departmentRequest) throws DepartmentAlreadyExistsException;
}
