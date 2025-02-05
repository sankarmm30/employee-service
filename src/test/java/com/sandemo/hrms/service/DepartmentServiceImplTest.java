package com.sandemo.hrms.service;

import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.dto.response.DepartmentResponseDto;
import com.sandemo.hrms.exception.GenericClientRuntimeException;
import com.sandemo.hrms.exception.GenericServerRuntimeException;
import com.sandemo.hrms.factory.ValidationFactoryService;
import com.sandemo.hrms.model.DepartmentEntity;
import com.sandemo.hrms.repository.DepartmentEntityRepository;
import com.sandemo.hrms.service.impl.DepartmentServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.util.Optional;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class DepartmentServiceImplTest {

    private static final String NAME = "Test";
    private static final Long ID = 1L;
    private static final Long ID_TWO = 2L;

    @Mock
    private DepartmentEntityRepository departmentEntityRepository;

    private ValidationFactoryService validationFactoryService = new ValidationFactoryService(
            Validation.buildDefaultValidatorFactory().getValidator());

    @InjectMocks
    DepartmentService departmentService = new DepartmentServiceImpl(departmentEntityRepository, validationFactoryService);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        Mockito.when(departmentEntityRepository.findById(Mockito.eq(ID))).thenReturn(Optional.of(getDepartmentEntity()));
        Mockito.when(departmentEntityRepository.findById(Mockito.eq(ID_TWO))).thenReturn(Optional.empty());
        Mockito.when(departmentEntityRepository.save(Mockito.any(DepartmentEntity.class))).thenReturn(getDepartmentEntity());
    }

    @Test
    public void testCreateDepartmentValid() {

        DepartmentEntity departmentEntity = departmentService.createDepartment(
                DepartmentRequestDto.builder().name(NAME).build());

        Assert.assertNotNull(departmentEntity);
        Assert.assertEquals(NAME, departmentEntity.getName());
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCreateDepartmentWhenNameIsNull() {

        departmentService.createDepartment(
                DepartmentRequestDto.builder().name(null).build());
    }

    @Test(expected = GenericClientRuntimeException.class)
    public void testCreateDepartmentWhenInputParamIsNull() {

        departmentService.createDepartment(null);
    }

    @Test(expected = GenericServerRuntimeException.class)
    public void testCreateDepartmentWhenUnexpectedException() {

        Mockito.when(departmentEntityRepository.save(Mockito.any(DepartmentEntity.class)))
                .thenThrow(new IllegalStateException());

        departmentService.createDepartment(DepartmentRequestDto.builder().name(NAME).build());
    }

    @Test
    public void testGetDepartmentByIdValid() {

        Optional<DepartmentEntity> departmentEntityOptional = departmentService.getDepartmentById(ID);

        Assert.assertTrue(departmentEntityOptional.isPresent());
        Assert.assertEquals(NAME, departmentEntityOptional.get().getName());
    }

    @Test
    public void testGetDepartmentByIdWhenIdIsNull() {

        Optional<DepartmentEntity> departmentEntityOptional = departmentService.getDepartmentById(null);

        Assert.assertFalse(departmentEntityOptional.isPresent());
    }

    @Test
    public void testGetDepartmentByIdWhenDepartmentNotExists() {

        Optional<DepartmentEntity> departmentEntityOptional = departmentService.getDepartmentById(ID_TWO);

        Assert.assertFalse(departmentEntityOptional.isPresent());
    }

    @Test
    public void testCreateDepartmentAndGetResponseValid() {

        DepartmentResponseDto departmentResponseDto = departmentService.createDepartmentAndGetResponse(
                DepartmentRequestDto.builder().name(NAME).build());

        Assert.assertNotNull(departmentResponseDto);
        Assert.assertEquals(ID, departmentResponseDto.getDepartmentId());
    }

    @Test(expected = GenericClientRuntimeException.class)
    public void testCreateDepartmentAndGetResponseWhenInputParamIsNull() {

        departmentService.createDepartmentAndGetResponse(null);
    }

    private DepartmentEntity getDepartmentEntity() {

        return DepartmentEntity.builder()
                .departId(ID)
                .name(NAME)
                .build();
    }
}
