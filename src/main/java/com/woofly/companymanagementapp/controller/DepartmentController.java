package com.woofly.companymanagementapp.controller;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.service.impl.DepartmentServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Yeni department yaratmaq", description = "Yeni bir department yaradır.")
    @PostMapping()
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest departmentRequest) throws DepartmentAlreadyExistsException {
        DepartmentResponse department = departmentService.createDepartment(departmentRequest);
        return new ResponseEntity<>(department, HttpStatus.CREATED);
    }


    @Operation(summary = "Department ID ilə tapmaq", description = "Verilən ID-yə əsasən department məlumatını qaytarır.")
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable Long id) throws DepartmentNotFoundException {
        DepartmentResponse departmentById = departmentService.getDepartmentById(id);
        return new ResponseEntity<>(departmentById, HttpStatus.OK);
    }

    @Operation(summary = "Bütün departmentləri gətirmək", description = "Bütün departmentlərin siyahısını qaytarır.")
    @GetMapping()
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return new ResponseEntity<>(departments, HttpStatus.OK);
    }

    @Operation(summary = "Department məlumatını yeniləmək", description = "Verilən ID-yə əsasən department məlumatını yeniləyir.")
    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequest departmentRequest) throws DepartmentNotFoundException {
        DepartmentResponse department = departmentService.updateDepartment(id, departmentRequest);
        return new ResponseEntity<>(department, HttpStatus.OK);
    }

    @Operation(summary = "Department silmək", description = "Verilən ID-yə əsasən department məlumatını silir.")
    @DeleteMapping("/{id}")
    public ResponseEntity<DepartmentResponse> deleteDepartment(@PathVariable Long id) throws DepartmentNotFoundException {
        departmentService.deleteDepartment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
