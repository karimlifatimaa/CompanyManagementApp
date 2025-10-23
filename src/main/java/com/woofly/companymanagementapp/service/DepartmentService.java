package com.woofly.companymanagementapp.service;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    void deleteDepartment(Long id);
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest);
    DepartmentResponse getDepartmentById(Long id);
    DepartmentResponse createDepartment(DepartmentRequest departmentRequest);
}
