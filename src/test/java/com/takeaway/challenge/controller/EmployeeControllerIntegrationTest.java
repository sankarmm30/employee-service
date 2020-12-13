package com.takeaway.challenge.controller;

import com.takeaway.challenge.EmployeeServiceApp;
import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.response.EmployeeDetailsResponseDto;
import com.takeaway.challenge.dto.response.EmployeeResponseDto;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.repository.DepartmentEntityRepository;
import com.takeaway.challenge.repository.EmployeeEntityRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class EmployeeControllerIntegrationTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String CREATE_EMP_URL = "/takeaway/employee/create";
    private static final String UPDATE_EMP_URL = "/takeaway/employee/update/";
    private static final String DELETE_EMP_URL = "/takeaway/employee/delete/";
    private static final String GET_EMP_URL = "/takeaway/employee/details?employeeId=";
    private static final String DEPART_NAME = "HR";
    private static final String EMPLOYEE_ID = "testid1";
    private static final String EMPLOYEE_NAME = "testname1";
    private static final String EMPLOYEE_EMAIL = "test@email.com";
    private static final String EMPLOYEE_1_EMAIL = "test2@email.com";
    private static final String EMPLOYEE_2_EMAIL = "test2@email.com";
    private static final Long INVALID_DEPART_ID = -1L;

    private static final HttpHeaders HEADERS = new HttpHeaders();
    static {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private DepartmentEntityRepository departmentEntityRepository;

    @Autowired
    private EmployeeEntityRepository employeeEntityRepository;

    private DepartmentEntity departmentEntity;
    private EmployeeEntity employeeEntity;

    @Before
    @Transactional
    public void init() {

        this.employeeEntityRepository.deleteAll();
        this.departmentEntityRepository.deleteAll();

        departmentEntity = this.departmentEntityRepository.save(DepartmentEntity.builder().name(DEPART_NAME).build());

        // This will be used for update and delete scenarios
        employeeEntity = this.employeeEntityRepository.save(
                EmployeeEntity.builder()
                        .employeeId(EMPLOYEE_ID)
                        .name(EMPLOYEE_NAME)
                        .email(EMPLOYEE_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentEntity(departmentEntity).build());
    }

    @LocalServerPort
    private Integer port;

    @Test
    public void testCreateEmployeeValid() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(
                EmployeeRequestDto.builder()
                        .name(EMPLOYEE_NAME)
                        .email(EMPLOYEE_1_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentId(departmentEntity.getDepartId())
                        .build(), HEADERS);

        ResponseEntity<EmployeeResponseDto> response = restTemplate.exchange(
                BASE_URL + port + CREATE_EMP_URL, HttpMethod.POST, entity, EmployeeResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_CREATE_MESSAGE.getValue(), response.getBody().getMessage());
    }

    @Test
    public void testCreateEmployeeWithEmailAlreadyExists() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(
                EmployeeRequestDto.builder()
                        .name(EMPLOYEE_NAME)
                        .email(EMPLOYEE_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentId(departmentEntity.getDepartId())
                        .build(), HEADERS);

        ResponseEntity<EmployeeResponseDto> response = restTemplate.exchange(
                BASE_URL + port + CREATE_EMP_URL, HttpMethod.POST, entity, EmployeeResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testCreateEmployeeWithInvalidDepartment() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(
                EmployeeRequestDto.builder()
                        .name(EMPLOYEE_NAME)
                        .email(EMPLOYEE_1_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentId(INVALID_DEPART_ID)
                        .build(), HEADERS);

        ResponseEntity<EmployeeResponseDto> response = restTemplate.exchange(
                BASE_URL + port + CREATE_EMP_URL, HttpMethod.POST, entity, EmployeeResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
    }

    @Test
    public void testUpdateEmployeeValid() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(
                EmployeeRequestDto.builder()
                        .name(EMPLOYEE_NAME)
                        .email(EMPLOYEE_2_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentId(departmentEntity.getDepartId())
                        .build(), HEADERS);

        ResponseEntity<EmployeeResponseDto> response = restTemplate.exchange(
                BASE_URL + port + UPDATE_EMP_URL + employeeEntity.getEmployeeId(), HttpMethod.PUT, entity,
                EmployeeResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_UPDATE_MESSAGE.getValue(), response.getBody().getMessage());

        // Verify in the database

        Optional<EmployeeEntity> employeeEntityOptional = this.employeeEntityRepository.findByEmployeeId(response.getBody().getEmployeeId());

        Assert.assertTrue(employeeEntityOptional.isPresent());
        Assert.assertEquals(EMPLOYEE_2_EMAIL, employeeEntityOptional.get().getEmail());
    }

    @Test
    public void testDeleteEmployeeValid() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(null, HEADERS);

        ResponseEntity<EmployeeResponseDto> response = restTemplate.exchange(
                BASE_URL + port + DELETE_EMP_URL + employeeEntity.getEmployeeId(), HttpMethod.DELETE, entity,
                EmployeeResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getEmployeeId());
        Assert.assertEquals(ApiResponseMessage.EMP_DELETE_MESSAGE.getValue(), response.getBody().getMessage());

        // Verify in the database

        Optional<EmployeeEntity> employeeEntityOptional = this.employeeEntityRepository.findByEmployeeId(response.getBody().getEmployeeId());

        Assert.assertFalse(employeeEntityOptional.isPresent());
    }

    @Test
    public void testGetEmployeeDetailsValid() {

        // Given
        HttpEntity<EmployeeRequestDto> entity = new HttpEntity<EmployeeRequestDto>(null, HEADERS);

        ResponseEntity<EmployeeDetailsResponseDto> response = restTemplate.exchange(
                BASE_URL + port + GET_EMP_URL + employeeEntity.getEmployeeId(), HttpMethod.GET, entity,
                EmployeeDetailsResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getEmployeeId());
        Assert.assertEquals(employeeEntity.getEmployeeId(), response.getBody().getEmployeeId());
        Assert.assertEquals(employeeEntity.getName(), response.getBody().getName());
        Assert.assertEquals(employeeEntity.getEmail(), response.getBody().getEmail());
        Assert.assertEquals(employeeEntity.getDateOfBirth(), response.getBody().getDataOfBirth());
        Assert.assertNotNull(response.getBody().getDepartment());
        Assert.assertEquals(employeeEntity.getDepartmentEntity().getDepartId(), response.getBody().getDepartment().getDepartmentId());
        Assert.assertEquals(employeeEntity.getDepartmentEntity().getName(), response.getBody().getDepartment().getName());
    }
}
