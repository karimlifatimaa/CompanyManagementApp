package com.woofly.companymanagementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.service.DepartmentService;
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

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentRequest departmentRequest;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        departmentRequest = new DepartmentRequest();
        departmentRequest.setName("IT");
        departmentRequest.setLocation("Baku");
        departmentRequest.setPhoneNumber("123456789");

        departmentResponse = new DepartmentResponse();
        departmentResponse.setId(1L);
        departmentResponse.setName("IT");
    }

    @Test
    void createDepartment_shouldReturnCreated() throws Exception {
        given(departmentService.createDepartment(any(DepartmentRequest.class))).willReturn(departmentResponse);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    void createDepartment_shouldReturnConflict() throws Exception {
        given(departmentService.createDepartment(any(DepartmentRequest.class))).willThrow(new DepartmentAlreadyExistsException(""));

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getDepartment_shouldReturnDepartment() throws Exception {
        given(departmentService.getDepartmentById(1L)).willReturn(departmentResponse);

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    void getDepartment_shouldReturnNotFound() throws Exception {
        given(departmentService.getDepartmentById(1L)).willThrow(new DepartmentNotFoundException(""));

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDepartments_shouldReturnDepartments() throws Exception {
        Page<DepartmentResponse> departmentPage = new PageImpl<>(Collections.singletonList(departmentResponse), PageRequest.of(0, 10), 1);
        given(departmentService.getAllDepartments(any())).willReturn(departmentPage);

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].name").value("IT"));
    }

    @Test
    void updateDepartment_shouldReturnOk() throws Exception {
        given(departmentService.updateDepartment(eq(1L), any(DepartmentRequest.class))).willReturn(departmentResponse);

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("IT"));
    }

    @Test
    void updateDepartment_shouldReturnNotFound() throws Exception {
        given(departmentService.updateDepartment(eq(1L), any(DepartmentRequest.class))).willThrow(new DepartmentNotFoundException(""));

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(departmentRequest)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteDepartment_shouldReturnNoContent() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDepartment_shouldReturnNotFound() throws Exception {
        doThrow(new DepartmentNotFoundException("")).when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNotFound());
    }
}
