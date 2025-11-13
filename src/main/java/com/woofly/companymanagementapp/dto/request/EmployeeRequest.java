package com.woofly.companymanagementapp.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class EmployeeRequest {
    @NotBlank(message = "Full name cannot be empty")
    @Size(min = 3,max = 50 , message = "Full name must be between 3 and 50 characters ")
    private String fullName;
    @NotBlank(message = "Position cannot be empty")
    private String position;
    @Email(message = "Email should be valid")
    private String email;
    @NotNull(message = "Salary cannot be null")
    @Positive(message = "Salary must be a positive value")
    private Double salary;
    @NotNull(message = "Department ID cannot be null")
    private Long departmentId;
}
