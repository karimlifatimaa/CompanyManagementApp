package com.woofly.companymanagementapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.model.Employee;
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
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    private Department department;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();

        department = new Department();
        department.setName("HR");
        department.setLocation("Main Office");
        department.setPhoneNumber("111222333");
        department = departmentRepository.save(department);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setFullName("John Doe");
        employeeRequest.setPosition("Recruiter");
        employeeRequest.setEmail("john.doe@example.com");
        employeeRequest.setSalary(60000.0);
        employeeRequest.setDepartmentId(department.getId());
    }

    @Test
    void createEmployee_shouldReturnCreated() throws Exception {
        mockMvc.perform(post("/api/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    @Test
    void findEmployeeById_shouldReturnEmployee() throws Exception {
        Employee employee = employeeRepository.save(employeeMapper(employeeRequest));

        mockMvc.perform(get("/api/employee/" + employee.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employee.getId().intValue())))
                .andExpect(jsonPath("$.fullName", is("John Doe")));
    }

    @Test
    void findAllEmployees_shouldReturnEmployees() throws Exception {
        employeeRepository.save(employeeMapper(employeeRequest));

        mockMvc.perform(get("/api/employee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("John Doe")));
    }

    @Test
    void findEmployeesByDepartmentId_shouldReturnEmployees() throws Exception {
        employeeRepository.save(employeeMapper(employeeRequest));

        mockMvc.perform(get("/api/employee/department/" + department.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].fullName", is("John Doe")));
    }

    @Test
    void updateEmployee_shouldReturnOk() throws Exception {
        Employee employee = employeeRepository.save(employeeMapper(employeeRequest));

        employeeRequest.setFullName("John Smith");

        mockMvc.perform(put("/api/employee/" + employee.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("John Smith")));
    }

    @Test
    void deleteEmployee_shouldReturnNoContent() throws Exception {
        Employee employee = employeeRepository.save(employeeMapper(employeeRequest));

        mockMvc.perform(delete("/api/employee/" + employee.getId()))
                .andExpect(status().isNoContent());
    }

    private Employee employeeMapper(EmployeeRequest request) {
        Employee employee = new Employee();
        employee.setFullName(request.getFullName());
        employee.setPosition(request.getPosition());
        employee.setEmail(request.getEmail());
        employee.setSalary(request.getSalary());
        employee.setDepartment(department);
        return employee;
    }
}
