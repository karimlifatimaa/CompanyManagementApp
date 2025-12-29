package com.woofly.companymanagementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
class DepartmentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private DepartmentRequest departmentRequest;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        departmentRequest = new DepartmentRequest();
        departmentRequest.setName("Integration Test Department");
        departmentRequest.setLocation("Test Location");
        departmentRequest.setPhoneNumber("987654321");
    }

    @Test
    void createDepartment_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Integration Test Department")));
    }

    @Test
    void createDepartment_shouldReturnConflict_whenDepartmentExists() throws Exception {
        departmentRepository.save(objectMapper.convertValue(departmentRequest, Department.class));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getDepartment_shouldReturnDepartment() throws Exception {
        Department department = departmentRepository.save(objectMapper.convertValue(departmentRequest, Department.class));

        mockMvc.perform(get("/api/departments/" + department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(department.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Integration Test Department")));
    }

    @Test
    void getDepartment_shouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/api/departments/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDepartments_shouldReturnDepartments() throws Exception {
        departmentRepository.save(objectMapper.convertValue(departmentRequest, Department.class));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("Integration Test Department")));
    }

    @Test
    void updateDepartment_shouldReturnOk() throws Exception {
        Department department = departmentRepository.save(objectMapper.convertValue(departmentRequest, Department.class));

        DepartmentRequest updatedRequest = new DepartmentRequest();
        updatedRequest.setName("Updated Department");
        updatedRequest.setLocation("Updated Location");
        updatedRequest.setPhoneNumber("123456789");

        mockMvc.perform(put("/api/departments/" + department.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Department")));
    }

    @Test
    void updateDepartment_shouldReturnNotFound() throws Exception {
        DepartmentRequest updatedRequest = new DepartmentRequest();
        updatedRequest.setName("Updated Department");
        updatedRequest.setLocation("Updated Location");
        updatedRequest.setPhoneNumber("123456789");

        mockMvc.perform(put("/api/departments/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteDepartment_shouldReturnNoContent() throws Exception {
        Department department = departmentRepository.save(objectMapper.convertValue(departmentRequest, Department.class));

        mockMvc.perform(delete("/api/departments/" + department.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDepartment_shouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/api/departments/999"))
                .andExpect(status().isNotFound());
    }
}
