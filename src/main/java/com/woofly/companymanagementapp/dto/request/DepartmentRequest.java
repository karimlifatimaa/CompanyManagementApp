package com.woofly.companymanagementapp.dto.request;

import lombok.Data;

@Data
public class DepartmentRequest {
    private String name;
    private String location;
    private String phoneNumber;
}
