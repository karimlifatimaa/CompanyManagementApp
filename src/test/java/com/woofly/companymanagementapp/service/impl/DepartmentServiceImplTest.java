package com.woofly.companymanagementapp.service.impl;

import com.woofly.companymanagementapp.dto.request.DepartmentRequest;
import com.woofly.companymanagementapp.dto.response.DepartmentResponse;
import com.woofly.companymanagementapp.exception.DepartmentAlreadyExistsException;
import com.woofly.companymanagementapp.exception.DepartmentNotFoundException;
import com.woofly.companymanagementapp.mapper.DepartmentMapper;
import com.woofly.companymanagementapp.model.Department;
import com.woofly.companymanagementapp.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private DepartmentRequest departmentRequest;
    private Department department;
    private DepartmentResponse departmentResponse;

    @BeforeEach
    void setUp() {
        departmentRequest = new DepartmentRequest();
        departmentRequest.setName("IT");
        departmentRequest.setLocation("Baku");
        departmentRequest.setPhoneNumber("123456789");

        department = new Department();
        department.setId(1L);
        department.setName("IT");

        departmentResponse = new DepartmentResponse();
        departmentResponse.setId(1L);
        departmentResponse.setName("IT");
    }

    @Test
    void createDepartment_shouldCreateDepartment_whenDepartmentDoesNotExist() {
        // 1. Hazırlıq (Arrange)
        // Deponun 'findByName' metodu çağırılanda boş bir Optional qaytarmasını təyin edirik
        when(departmentRepository.findByName(departmentRequest.getName())).thenReturn(Optional.empty());

        when(departmentMapper.toDepartment(departmentRequest)).thenReturn(department);
        when(departmentRepository.save(department)).thenReturn(department);
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        // 2. Çağırış (Action)
        // Test edilən əsas metodu çağırırıq
        DepartmentResponse result = departmentService.createDepartment(departmentRequest);

        // 3. Yoxlama (Assertion/Verification)
        // Nəticənin boş olmadığını və gözlənilən dəyərə bərabər olduğunu yoxlayırıq
        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentRepository, times(1)).findByName(departmentRequest.getName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void createDepartment_shouldThrowException_whenDepartmentExists() {
        // 1. Hazırlıq (Arrange)
        when(departmentRepository.findByName(departmentRequest.getName())).thenReturn(Optional.of(department));

        // 2. Çağırış və Yoxlama (Act & Assert)
        assertThrows(DepartmentAlreadyExistsException.class, () -> departmentService.createDepartment(departmentRequest));

        //  3. Yoxlama (Verify)
        verify(departmentRepository, times(1)).findByName(departmentRequest.getName());
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void getDepartmentById_shouldReturnDepartment_whenDepartmentExists() {
        // 1. Hazırlıq (Arrange)
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        // 2. Çağırış (Act)
        DepartmentResponse result = departmentService.getDepartmentById(1L);

        // 3. Yoxlama (Assert)
        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void getDepartmentById_shouldThrowException_whenDepartmentDoesNotExist() {
        // 1. Hazırlıq (Arrange)
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        // 2. Çağırış və Yoxlama (Act & Assert)
        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(1L));

        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    void updateDepartment_shouldUpdateDepartment_whenDepartmentExists() {
        // 1. Hazırlıq (Arrange)
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findByName(departmentRequest.getName())).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        // 2. Çağırış (Act)
        DepartmentResponse result = departmentService.updateDepartment(1L, departmentRequest);

        // 3. Yoxlama (Assert/Verify)
        assertNotNull(result);
        assertEquals(departmentResponse, result);
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void updateDepartment_shouldThrowException_whenDepartmentDoesNotExist() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDepartment(1L, departmentRequest));

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void updateDepartment_shouldThrowException_whenDepartmentNameAlreadyExists() {
        Department anotherDepartment = new Department();
        anotherDepartment.setId(2L);
        anotherDepartment.setName("HR");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.findByName(departmentRequest.getName())).thenReturn(Optional.of(anotherDepartment));

        assertThrows(DepartmentAlreadyExistsException.class, () -> departmentService.updateDepartment(1L, departmentRequest));

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void getAllDepartments_shouldReturnAllDepartments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> departmentPage = new PageImpl<>(Collections.singletonList(department), pageable, 1);
        when(departmentRepository.findAll(pageable)).thenReturn(departmentPage);
        when(departmentMapper.toDepartmentResponse(department)).thenReturn(departmentResponse);

        Page<DepartmentResponse> result = departmentService.getAllDepartments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(departmentResponse, result.getContent().get(0));
        verify(departmentRepository, times(1)).findAll(pageable);
    }

    @Test
    void deleteDepartment_shouldDeleteDepartment_whenDepartmentExists() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).delete(department);

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    void deleteDepartment_shouldThrowException_whenDepartmentDoesNotExist() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.deleteDepartment(1L));

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
