package com.woofly.companymanagementapp.dto.response;

import lombok.Data;

@Data
public class EmployeeResponse {
    private Long id;
    private String fullName;
    private String position;
    private String email;
    private Double salary;
}
