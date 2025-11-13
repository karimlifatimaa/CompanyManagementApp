package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.dto.response.EmployeeResponse;
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

            // Explicitly initialize the lazy collection within the transactional context

            departmentNotFound.getEmployees().size(); // Accessing the collection initializes it

    

            DepartmentResponse departmentResponse = new DepartmentResponse();

            departmentResponse.setId(departmentNotFound.getId());

            departmentResponse.setName(departmentNotFound.getName());

            departmentResponse.setLocation(departmentNotFound.getLocation());

            departmentResponse.setPhoneNumber(departmentNotFound.getPhoneNumber());

//            departmentResponse.setEmployees(departmentNotFound.getEmployees().stream()
//
//                    .map(employee -> {
//
//                        EmployeeResponse empResponse = new EmployeeResponse();
//
//                        empResponse.setId(employee.getId());
//
//                        empResponse.setFullName(employee.getFullName());
//
//                        empResponse.setPosition(employee.getPosition());
//
//                        empResponse.setEmail(employee.getEmail());
//
//                        empResponse.setSalary(employee.getSalary());
//
//                        // departmentId is not set here to avoid circular dependency in DepartmentResponse
//
//                        return empResponse;
//
//                    }).toList());

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
        return departmentRepository.findAll().stream().map(department -> {
            // Explicitly initialize the lazy collection within the transactional context
            department.getEmployees().size(); // Accessing the collection initializes it

            DepartmentResponse departmentResponse = new DepartmentResponse();
            departmentResponse.setId(department.getId());
            departmentResponse.setName(department.getName());
            departmentResponse.setLocation(department.getLocation());
            departmentResponse.setPhoneNumber(department.getPhoneNumber());
//            departmentResponse.setEmployees(department.getEmployees().stream()
//                    .map(employee -> {
//                        EmployeeResponse empResponse = new EmployeeResponse();
//                        empResponse.setId(employee.getId());
//                        empResponse.setFullName(employee.getFullName());
//                        empResponse.setPosition(employee.getPosition());
//                        empResponse.setEmail(employee.getEmail());
//                        empResponse.setSalary(employee.getSalary());
//                        // departmentId is not set here to avoid circular dependency in DepartmentResponse
//                        return empResponse;
//                    }).toList());
            return departmentResponse;
        }).toList();
    }

    public void deleteDepartment(Long id) {
        Department departmentById = findDepartmentById(id);
        departmentRepository.delete(departmentById);
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id).orElseThrow(() -> new DepartmentNotFoundException("Department not found"));
    }
}
