package com.woofly.companymanagementapp.mapper;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.model.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    EmployeeResponse toEmployeeResponse(Employee employee);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "id", ignore = true)
    Employee toEmployee(EmployeeRequest employeeRequest);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEmployeeFromRequest(EmployeeRequest employeeRequest, @MappingTarget Employee employee);
}
