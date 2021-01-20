package com.takeaway.challenge.service;

import com.takeaway.challenge.EmployeeEventType;
import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.dto.response.EmployeeDetailsResponseDto;
import com.takeaway.challenge.dto.response.EmployeeResponseDto;
import com.takeaway.challenge.exception.DepartmentNotFoundException;
import com.takeaway.challenge.exception.EmailIdAlreadyExistsException;
import com.takeaway.challenge.exception.EmployeeNotFoundException;
import com.takeaway.challenge.exception.TakeAwayClientRuntimeException;
import com.takeaway.challenge.exception.TakeAwayServerRuntimeException;
import com.takeaway.challenge.factory.ValidationFactoryService;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.repository.EmployeeEntityRepository;
import com.takeaway.challenge.service.impl.EmployeeServiceImpl;
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
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Optional;

@RunWith(JUnit4.class)
public class EmployeeServiceImplTest {

    private static final String EMP_ID = "test";
    private static final String EMP_NAME = "Test Emp";
    private static final String EMP_EMAIL = "test@test.com";
    private static final String PUT_EMP_ID = "test1";
    private static final String DEPART_NAME = "Test Depart";
    private static final Long DEPART_ID = 1L;
    private static final Long EMP_ID_PK = 10L;

    @Mock
    private EmployeeEntityRepository employeeEntityRepository;
    @Mock
    private DepartmentService departmentService;
    @Mock
    private KafkaProducerService employeeKafkaProducerService;

    private ValidationFactoryService validationFactoryService = new ValidationFactoryService(
            Validation.buildDefaultValidatorFactory().getValidator());

    @InjectMocks
    EmployeeService employeeService = new EmployeeServiceImpl(employeeEntityRepository, departmentService,
            validationFactoryService, employeeKafkaProducerService);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        Mockito.when(this.departmentService.getDepartmentById(Mockito.eq(DEPART_ID))).thenReturn(Optional.of(getDepartmentEntity()));
        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.empty());
        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.empty());

        Mockito.when(employeeEntityRepository.save(Mockito.any(EmployeeEntity.class))).thenReturn(getEmployeeEntity());

        Mockito.doNothing().when(employeeEntityRepository).delete(Mockito.any(EmployeeEntity.class));

        Mockito.doNothing().when(employeeKafkaProducerService).sendMessage(Mockito.any(EmployeeEntity.class),
                Mockito.any(EmployeeEventType.class));
    }

    @Test
    public void testCreateEmployeeValid() {

        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.empty());

        EmployeeEntity employeeEntity = employeeService.createEmployee(getEmployeeRequestDto());

        Assert.assertNotNull(employeeEntity);
        Assert.assertEquals(EMP_ID_PK, employeeEntity.getEmployeeIdPk());
        Assert.assertEquals(EMP_ID, employeeEntity.getEmployeeId());
    }

    @Test(expected = EmailIdAlreadyExistsException.class)
    public void testCreateEmployeeWhenEmailAlreadyExists() {

        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.of(getEmployeeEntity()));

        employeeService.createEmployee(getEmployeeRequestDto());
    }

    @Test(expected = DepartmentNotFoundException.class)
    public void testCreateEmployeeWhenDepartmentIsNotExists() {

        Mockito.when(this.departmentService.getDepartmentById(Mockito.eq(DEPART_ID))).thenReturn(Optional.empty());

        employeeService.createEmployee(getEmployeeRequestDto());
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testCreateEmployeeWhenEmployeeRequestIsNull() {

        employeeService.createEmployee(null);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testCreateEmployeeWhenEmployeeAttributesAreIsNull() {

        employeeService.createEmployee(EmployeeRequestDto.builder()
                .name(null)
                .email(null)
                .dateOfBirth(null)
                .departmentId(null)
                .build());
    }

    @Test(expected = TakeAwayServerRuntimeException.class)
    public void testCreateEmployeeWhenUnexpectedException() {

        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.empty());

        Mockito.when(employeeEntityRepository.save(Mockito.any(EmployeeEntity.class)))
                .thenThrow(new IllegalStateException("Test Exception"));

        employeeService.createEmployee(getEmployeeRequestDto());
    }

    @Test
    public void testCreateEmployeeAndGetResponseValid() {

        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.empty());

        EmployeeResponseDto employeeResponseDto = employeeService.createEmployeeAndGetResponse(getEmployeeRequestDto());

        Assert.assertNotNull(employeeResponseDto);
        Assert.assertEquals(EMP_ID, employeeResponseDto.getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_CREATE_MESSAGE.getValue(), employeeResponseDto.getMessage());
    }

    @Test
    public void testUpdateEmployeeValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeEntity employeeEntity = employeeService.updateEmployee(EMP_ID,
                getPutEmployeeRequestDto(EMP_NAME, EMP_EMAIL, LocalDate.now(), DEPART_ID));

        Assert.assertNotNull(employeeEntity);
        Assert.assertEquals(EMP_ID_PK, employeeEntity.getEmployeeIdPk());
        Assert.assertEquals(EMP_ID, employeeEntity.getEmployeeId());
    }

    @Test
    public void testUpdateEmployeeOnlyDepartment() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeEntity employeeEntity = employeeService.updateEmployee(EMP_ID,
                getPutEmployeeRequestDto(null, null, null, DEPART_ID));

        Assert.assertNotNull(employeeEntity);
    }

    @Test
    public void testUpdateEmployeeWhenEmailAlreadyExistsWithSameEmployee() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));
        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeEntity employeeEntity = employeeService.updateEmployee(EMP_ID,
                getPutEmployeeRequestDto(EMP_NAME, EMP_EMAIL, null, null));

        Assert.assertNotNull(employeeEntity);
    }

    @Test(expected = EmailIdAlreadyExistsException.class)
    public void testUpdateEmployeeWhenEmailAlreadyExistsWithAnotherEmployee() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));
        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(
                Optional.of(EmployeeEntity.builder().employeeId(PUT_EMP_ID).build()));

        employeeService.updateEmployee(EMP_ID, getPutEmployeeRequestDto(EMP_NAME, EMP_EMAIL, null, null));
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void testUpdateEmployeeWhenEmployeeNotFound() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.empty());

        employeeService.updateEmployee(EMP_ID, getPutEmployeeRequestDto(EMP_NAME, null, null, null));
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testUpdateEmployeeWhenEmployeeIdIsNull() {

        employeeService.updateEmployee(null, getPutEmployeeRequestDto(EMP_NAME, null, null, null));
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testUpdateEmployeeWhenRequestDtoIsNull() {

        employeeService.updateEmployee(EMP_ID, null);
    }

    @Test(expected = ConstraintViolationException.class)
    public void testUpdateEmployeeWhenRequestDtoValidationFailed() {

        employeeService.updateEmployee(EMP_ID, getPutEmployeeRequestDto(null, null, null, null));
    }

    @Test(expected = DepartmentNotFoundException.class)
    public void testUpdateEmployeeWhenDepartmentNotExists() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        Mockito.when(this.departmentService.getDepartmentById(Mockito.eq(DEPART_ID))).thenReturn(Optional.empty());

        employeeService.updateEmployee(EMP_ID, getPutEmployeeRequestDto(null, null, null, DEPART_ID));
    }

    @Test(expected = TakeAwayServerRuntimeException.class)
    public void testUpdateEmployeeWhenUnexpectedException() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        Mockito.when(employeeEntityRepository.save(Mockito.any(EmployeeEntity.class)))
                .thenThrow(new IllegalStateException("Test Exception"));

        employeeService.updateEmployee(EMP_ID, getPutEmployeeRequestDto(EMP_NAME, EMP_EMAIL, LocalDate.now(), DEPART_ID));
    }

    @Test
    public void testUpdateEmployeeAndGetResponseValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeResponseDto employeeResponseDto = employeeService.updateEmployeeAndGetResponse(EMP_ID,
                getPutEmployeeRequestDto(EMP_NAME, EMP_EMAIL, LocalDate.now(), DEPART_ID));

        Assert.assertNotNull(employeeResponseDto);
        Assert.assertEquals(EMP_ID, employeeResponseDto.getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_UPDATE_MESSAGE.getValue(), employeeResponseDto.getMessage());
    }

    @Test
    public void testDeleteEmployeeByIdValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        employeeService.deleteEmployeeById(EMP_ID);

        Assert.assertTrue("Always True", true);
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void testDeleteEmployeeByIdWhenEmployeeNotFound() {

        employeeService.deleteEmployeeById(EMP_ID);

        Assert.assertTrue("Always True", true);
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testDeleteEmployeeByIdWhenInputEmployeeIdIsNull() {

        employeeService.deleteEmployeeById(null);
    }

    @Test(expected = TakeAwayServerRuntimeException.class)
    public void testDeleteEmployeeByIdWhenUnexpectedException() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        Mockito.doThrow(new IllegalStateException("Test Exception"))
                .when(employeeEntityRepository).delete(Mockito.any(EmployeeEntity.class));

        employeeService.deleteEmployeeById(EMP_ID);
    }

    @Test
    public void testDeleteEmployeeByIdAndGetResponseValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeResponseDto employeeResponseDto = employeeService.deleteEmployeeByIdAndGetResponse(EMP_ID);

        Assert.assertNotNull(employeeResponseDto);
        Assert.assertEquals(EMP_ID, employeeResponseDto.getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_DELETE_MESSAGE.getValue(), employeeResponseDto.getMessage());
    }

    @Test
    public void testGetEmployeeByIdValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        Optional<EmployeeEntity> employeeEntityOptional = employeeService.getEmployeeById(EMP_ID);

        Assert.assertTrue(employeeEntityOptional.isPresent());
    }

    @Test
    public void testGetEmployeeByIdWhenEmployeeIdIsNull() {

        Optional<EmployeeEntity> employeeEntityOptional = employeeService.getEmployeeById(null);

        Assert.assertFalse(employeeEntityOptional.isPresent());
    }

    @Test
    public void testGetEmployeeByEmailValid() {

        Mockito.when(employeeEntityRepository.findByEmail(Mockito.eq(EMP_EMAIL))).thenReturn(Optional.of(getEmployeeEntity()));

        Optional<EmployeeEntity> employeeEntityOptional = employeeService.getEmployeeByEmail(EMP_EMAIL);

        Assert.assertTrue(employeeEntityOptional.isPresent());
    }

    @Test
    public void testGetEmployeeByEmailEmployeeIdIsNull() {

        Optional<EmployeeEntity> employeeEntityOptional = employeeService.getEmployeeByEmail(null);

        Assert.assertFalse(employeeEntityOptional.isPresent());
    }

    @Test
    public void testGetEmployeeDetailsByIdValid() {

        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID))).thenReturn(Optional.of(getEmployeeEntity()));

        EmployeeDetailsResponseDto employeeDetailsResponseDto = employeeService.getEmployeeDetailsById(EMP_ID);

        Assert.assertNotNull(employeeDetailsResponseDto);
        Assert.assertEquals(EMP_ID, employeeDetailsResponseDto.getEmployeeId());
    }

    @Test(expected = EmployeeNotFoundException.class)
    public void testGetEmployeeDetailsByIdWhenNoDataFound() {

        // when
        Mockito.when(employeeEntityRepository.findByEmployeeId(Mockito.eq(EMP_ID)))
                .thenReturn(Optional.empty());

        employeeService.getEmployeeDetailsById(EMP_ID);
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testGetEmployeeDetailsByIdWhenEmployeeIdIsNull() {

        employeeService.getEmployeeDetailsById(null);
    }

    @Test(expected = TakeAwayClientRuntimeException.class)
    public void testGetEmployeeDetailsByIdWhenEmployeeIdIsEmpty() {

        employeeService.getEmployeeDetailsById("");
    }

    private DepartmentEntity getDepartmentEntity() {

        return DepartmentEntity.builder()
                .departId(DEPART_ID)
                .name(DEPART_NAME)
                .build();
    }

    private EmployeeEntity getEmployeeEntity() {

        return EmployeeEntity.builder()
                .employeeIdPk(EMP_ID_PK)
                .employeeId(EMP_ID)
                .name(EMP_NAME)
                .email(EMP_EMAIL)
                .departmentEntity(getDepartmentEntity())
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    private EmployeeRequestDto getEmployeeRequestDto() {

        return EmployeeRequestDto.builder()
                .name(EMP_NAME)
                .email(EMP_EMAIL)
                .dateOfBirth(LocalDate.now())
                .departmentId(DEPART_ID)
                .build();
    }

    private PutEmployeeRequestDto getPutEmployeeRequestDto(final String name, final String email, final LocalDate dateOfBirth,
                                                           final Long departId) {

        return PutEmployeeRequestDto.builder()
                .name(name)
                .email(email)
                .dateOfBirth(dateOfBirth)
                .departmentId(departId)
                .build();
    }
}
