package com.woofly.companymanagementapp.dto.request;

import lombok.Data;

@Data
public class EmployeeRequest {
    private String fullName;
    private String position;
    private String email;
    private Double salary;
}
