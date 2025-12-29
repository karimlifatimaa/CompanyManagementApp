package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import com.woofly.companymanagementapp.service.DepartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class DepartmentServiceIntegrationTest {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private DepartmentRepository departmentRepository;

    private DepartmentRequest departmentRequest;

    @BeforeEach
    void setUp() {
        departmentRepository.deleteAll();
        departmentRequest = new DepartmentRequest();
        departmentRequest.setName("Finance");
        departmentRequest.setLocation("Headquarters");
        departmentRequest.setPhoneNumber("1234567890");
    }

    @Test
    void createDepartment_shouldSaveDepartment() {
        DepartmentResponse created = departmentService.createDepartment(departmentRequest);

        assertThat(created).isNotNull();
        assertThat(created.getName()).isEqualTo("Finance");

        Department found = departmentRepository.findById(created.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Finance");
    }

    @Test
    void createDepartment_shouldThrowException_whenNameExists() {
        departmentService.createDepartment(departmentRequest);

        DepartmentRequest sameNameRequest = new DepartmentRequest();
        sameNameRequest.setName("Finance");
        sameNameRequest.setLocation("New Location");
        sameNameRequest.setPhoneNumber("0987654321");

        assertThrows(DepartmentAlreadyExistsException.class, () -> {
            departmentService.createDepartment(sameNameRequest);
        });
    }

    @Test
    void getDepartmentById_shouldReturnCorrectDepartment() {
        DepartmentResponse created = departmentService.createDepartment(departmentRequest);

        DepartmentResponse found = departmentService.getDepartmentById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getName()).isEqualTo(departmentRequest.getName());
    }

    @Test
    void getDepartmentById_shouldThrowException_whenNotFound() {
        assertThrows(DepartmentNotFoundException.class, () -> {
            departmentService.getDepartmentById(999L);
        });
    }

    @Test
    void updateDepartment_shouldUpdateSuccessfully() {
        DepartmentResponse created = departmentService.createDepartment(departmentRequest);

        DepartmentRequest updateRequest = new DepartmentRequest();
        updateRequest.setName("New Finance");
        updateRequest.setLocation("New Location");
        updateRequest.setPhoneNumber(created.getPhoneNumber());

        DepartmentResponse updated = departmentService.updateDepartment(created.getId(), updateRequest);

        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("New Finance");
        assertThat(updated.getLocation()).isEqualTo("New Location");

        Department fromDb = departmentRepository.findById(created.getId()).get();
        assertThat(fromDb.getName()).isEqualTo("New Finance");
    }

    @Test
    void deleteDepartment_shouldRemoveDepartment() {
        DepartmentResponse created = departmentService.createDepartment(departmentRequest);
        Long departmentId = created.getId();

        assertThat(departmentRepository.findById(departmentId)).isPresent();

        departmentService.deleteDepartment(departmentId);

        assertThat(departmentRepository.findById(departmentId)).isNotPresent();
    }

    @Test
    void getAllDepartments_shouldReturnPagedResult() {
        departmentService.createDepartment(departmentRequest);

        DepartmentRequest anotherRequest = new DepartmentRequest();
        anotherRequest.setName("Marketing");
        anotherRequest.setLocation("Branch Office");
        anotherRequest.setPhoneNumber("1122334455");
        departmentService.createDepartment(anotherRequest);

        Page<DepartmentResponse> departments = departmentService.getAllDepartments(PageRequest.of(0, 5));

        assertThat(departments).isNotNull();
        assertThat(departments.getTotalElements()).isEqualTo(2);
        assertThat(departments.getContent()).hasSize(2);
        assertThat(departments.getContent().get(0).getName()).isEqualTo("Finance");
    }
}
