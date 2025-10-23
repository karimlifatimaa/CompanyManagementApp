package com.woofly.companymanagementapp.controller;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.service.impl.DepartmentServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentServiceImpl departmentService;

    public DepartmentController(DepartmentServiceImpl departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping("/create")
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponse department = departmentService.createDepartment(departmentRequest);
        return new ResponseEntity<>(department, HttpStatus.CREATED);
    }


    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long id) {
        DepartmentResponse departmentById = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(departmentById, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @RequestBody DepartmentRequest departmentRequest) {
        DepartmentResponse department = departmentService.updateDepartment(id, departmentRequest);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<DepartmentResponse> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
