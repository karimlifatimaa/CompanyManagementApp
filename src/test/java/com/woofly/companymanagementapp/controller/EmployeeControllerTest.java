package com.woofly.companymanagementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequest employeeRequest;
    private EmployeeResponse employeeResponse;

    @BeforeEach
    void setUp() {
        employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("Test User");
        employeeRequest.setPosition("Developer");
        employeeRequest.setEmail("test@test.com");
        employeeRequest.setSalary(5000.0);
        employeeRequest.setDepartmentId(1L);

        employeeResponse = new EmployeeResponse();
        employeeResponse.setId(1L);
        employeeResponse.setFullName("Test User");
        employeeResponse.setEmail("test@test.com");
    }

    @Test
    void createEmployee_shouldReturnCreated() throws Exception {
        given(employeeService.createEmployee(any(EmployeeRequest.class))).willReturn(employeeResponse);

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    void createEmployee_shouldReturnConflict() throws Exception {
        given(employeeService.createEmployee(any(EmployeeRequest.class))).willThrow(new EmployeeAlreadyExistsException(""));

        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void findEmployeeById_shouldReturnEmployee() throws Exception {
        given(employeeService.findEmployeeById(1L)).willReturn(employeeResponse);

        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Test User"));
    }

    @Test
    void findEmployeeById_shouldReturnNotFound() throws Exception {
        given(employeeService.findEmployeeById(1L)).willThrow(new EmployeeNotFoundException(""));

        mockMvc.perform(get("/api/employee/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void findAllEmployees_shouldReturnEmployees() throws Exception {
        Page<EmployeeResponse> employeePage = new PageImpl<>(Collections.singletonList(employeeResponse), PageRequest.of(0, 10), 1);
        given(employeeService.findAllEmployee(any())).willReturn(employeePage);

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].fullName").value("Test User"));
    }

    @Test
    void findEmployeesByDepartmentId_shouldReturnEmployees() throws Exception {
        Page<EmployeeResponse> employeePage = new PageImpl<>(Collections.singletonList(employeeResponse), PageRequest.of(0, 10), 1);
        given(employeeService.findEmployeesByDepartmentId(eq(1L), any())).willReturn(employeePage);

        mockMvc.perform(get("/api/employee/department/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    void findEmployeesByDepartmentId_shouldReturnNotFound() throws Exception {
        given(employeeService.findEmployeesByDepartmentId(eq(1L), any())).willThrow(new DepartmentNotFoundException(""));

        mockMvc.perform(get("/api/employee/department/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateEmployee_shouldReturnOk() throws Exception {
        given(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).willReturn(employeeResponse);

        mockMvc.perform(put("/api/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void updateEmployee_shouldReturnNotFound() throws Exception {
        given(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).willThrow(new EmployeeNotFoundException(""));

        mockMvc.perform(put("/api/employee/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employee/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEmployee_shouldReturnNotFound() throws Exception {
        doThrow(new EmployeeNotFoundException("")).when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employee/1"))
                .andExpect(status().isNotFound());
    }
}
