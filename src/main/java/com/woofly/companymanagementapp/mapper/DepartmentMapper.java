package com.woofly.companymanagementapp.mapper;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = EmployeeMapper.class)
public interface DepartmentMapper {
    DepartmentResponse toDepartmentResponse(Department department);
    Department toDepartment(DepartmentRequest departmentRequest);
    void updateDepartmentFromRequest(DepartmentRequest departmentRequest, @MappingTarget Department department);
}
