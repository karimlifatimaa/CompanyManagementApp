package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.service.DepartmentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        departmentRepository.findByName(departmentRequest.getName()).ifPresent(d -> {
            throw new DepartmentAlreadyExistsException("Department with name " + departmentRequest.getName() + " already exists");
        });
        Department department = new Department();
        department.setName(departmentRequest.getName());
        department.setLocation(departmentRequest.getLocation());
        department.setPhoneNumber(departmentRequest.getPhoneNumber());
        departmentRepository.save(department);

        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(department.getId());
        departmentResponse.setName(department.getName());
        departmentResponse.setLocation(department.getLocation());
        departmentResponse.setPhoneNumber(department.getPhoneNumber());

        return departmentResponse;
    }

    public DepartmentResponse getDepartmentById(Long id) {
        Department departmentNotFound = departmentRepository.findById(id).orElseThrow(
                () -> new DepartmentNotFoundException("Department not found")
        );
        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(departmentNotFound.getId());
        departmentResponse.setName(departmentNotFound.getName());
        departmentResponse.setLocation(departmentNotFound.getLocation());
        departmentResponse.setPhoneNumber(departmentNotFound.getPhoneNumber());
        return departmentResponse;

    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existDepartment = findDepartmentById(id);
        departmentRepository.findByName(departmentRequest.getName()).ifPresent(d -> {
            throw new DepartmentAlreadyExistsException("Department with name " + departmentRequest.getName() + " already exists");
        });
        existDepartment.setName(departmentRequest.getName());
        existDepartment.setLocation(departmentRequest.getLocation());
        existDepartment.setPhoneNumber(departmentRequest.getPhoneNumber());
        Department updated = departmentRepository.save(existDepartment);

        DepartmentResponse departmentResponse = new DepartmentResponse();
        departmentResponse.setId(updated.getId());
        departmentResponse.setName(updated.getName());
        departmentResponse.setLocation(updated.getLocation());
        departmentResponse.setPhoneNumber(updated.getPhoneNumber());
        return departmentResponse;


    }

    public List<DepartmentResponse> getAllDepartments() {
        return departmentRepository.findAll().stream().map(department ->
                new DepartmentResponse(
                        department.getId(),
                        department.getName(),
                        department.getLocation(),
                        department.getPhoneNumber())
        ).toList();
    }

    public void deleteDepartment(Long id) {
        Department departmentById = findDepartmentById(id);
        departmentRepository.delete(departmentById);
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
    }
}
