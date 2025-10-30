package com.woofly.companymanagementapp.controller;

import com.woofly.companymanagementapp.dto.request.EmployeeRequest;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
import com.woofly.companymanagementapp.exception.EmployeeAlreadyExistsException;
import com.woofly.companymanagementapp.exception.EmployeeNotFoundException;
import com.woofly.companymanagementapp.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }


    @Operation(summary = "Yeni employee yaratmaq", description = "Yeni bir employee yaradır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uğurla yaradıldı"),
            @ApiResponse(responseCode = "400", description = "Yanlış müraciət (Validasiya xətası)"),
            @ApiResponse(responseCode = "500", description = "Server xətası")
    })
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest employeeRequest) throws EmployeeAlreadyExistsException {
        EmployeeResponse employee = employeeService.createEmployee(employeeRequest);
        return new ResponseEntity<>(employee, HttpStatus.CREATED);
    }

    @Operation(summary = "Employee ID ilə tapmaq", description = "Verilən ID-yə əsasən employee məlumatını qaytarır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee tapıldı"),
            @ApiResponse(responseCode = "404", description = "Employee tapılmadı"),
            @ApiResponse(responseCode = "500", description = "Server xətası")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findEmployeeById(@PathVariable Long id) throws EmployeeNotFoundException {
        EmployeeResponse employee = employeeService.findEmployeeById(id);
        return ResponseEntity.ok(employee);
    }


    @Operation(summary = "Bütün employeeləri gətirmək", description = "Bütün employee siyahısını qaytarır.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Uğurlu cavab"),
            @ApiResponse(responseCode = "204", description = "Heç bir employee tapılmadı"),
            @ApiResponse(responseCode = "500", description = "Server xətası")
    })
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employees = employeeService.findAllEmployee();
        return ResponseEntity.ok(employees);
    }

    @Operation(summary = "Employee məlumatını yeniləmək", description = "Verilən ID-yə əsasən employee məlumatını yeniləyir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee uğurla yeniləndi"),
            @ApiResponse(responseCode = "400", description = "Yanlış müraciət (Validasiya xətası)"),
            @ApiResponse(responseCode = "404", description = "Employee tapılmadı"),
            @ApiResponse(responseCode = "500", description = "Server xətası")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest employeeRequest
    ) throws EmployeeNotFoundException {
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, employeeRequest);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(summary = "Employee silmək", description = "Verilən ID-yə əsasən employee məlumatını silir.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) throws EmployeeNotFoundException {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build(); // 204 — no response body
    }
}
