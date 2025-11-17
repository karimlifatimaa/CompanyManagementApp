package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.mapper.DepartmentMapper;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    public DepartmentResponse createDepartment(DepartmentRequest departmentRequest) {
        departmentRepository.findByName(departmentRequest.getName()).ifPresent(d -> {
            throw new DepartmentAlreadyExistsException("Department with name " + departmentRequest.getName() + " already exists");
        });
        Department department = departmentMapper.toDepartment(departmentRequest);
        Department savedDepartment = departmentRepository.save(department);
        return departmentMapper.toDepartmentResponse(savedDepartment);
    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
        return departmentMapper.toDepartmentResponse(department);
    }

    public DepartmentResponse updateDepartment(Long id, DepartmentRequest departmentRequest) {
        Department existDepartment = findDepartmentById(id);
        departmentRepository.findByName(departmentRequest.getName()).ifPresent(d -> {
            if (!d.getId().equals(id)) {
                throw new DepartmentAlreadyExistsException("Department with name " + departmentRequest.getName() + " already exists");
            }
        });
        departmentMapper.updateDepartmentFromRequest(departmentRequest, existDepartment);
        Department updated = departmentRepository.save(existDepartment);
        return departmentMapper.toDepartmentResponse(updated);
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable)
                .map(departmentMapper::toDepartmentResponse);
    }

    public void deleteDepartment(Long id) {
        Department departmentById = findDepartmentById(id);
        departmentRepository.delete(departmentById);
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
    }
}
