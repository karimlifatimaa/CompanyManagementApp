package com.woofly.companymanagementapp.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentRequest {
    @NotBlank(message = "Department name cannot be empty")
    private String name;
    @NotBlank(message = "Department location cannot be empty")
    private String location;
    @NotBlank(message = "Department phone number cannot be empty")
    private String phoneNumber;
}
