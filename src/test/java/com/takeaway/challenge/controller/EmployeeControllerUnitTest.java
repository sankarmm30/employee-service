package com.takeaway.challenge.controller;

import com.takeaway.challenge.EmployeeEventType;
import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.exception.DepartmentNotFoundException;
import com.takeaway.challenge.exception.advice.GenericExceptionHandlerAdvice;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.service.EmployeeService;
import com.takeaway.challenge.service.KafkaProducerService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Optional;

@RunWith(JUnit4.class)
public class EmployeeControllerUnitTest {

    private static final String EMPLOYEE_ID = "test";
    private static final String EMPLOYEE_NAME = "testname";
    private static final String EMPLOYEE_EMAIL = "testname@test.com";
    private static final String DEPART_NAME = "HR";
    private static final String JSON_FIELD = "employeeId";
    private static final String CREATE_EMP_URL = "/employee/create";
    private static final String UPDATE_EMP_URL = "/employee/update/";
    private static final String DELETE_EMP_URL = "/employee/delete/";
    private static final String GET_EMP_URL = "/employee/details?employeeId=";
    private static final Long DEPART_ID = 1L;

    @Mock
    private EmployeeService employeeService;
    @Mock
    private KafkaProducerService employeeKafkaProducerService;

    @InjectMocks
    private EmployeeController employeeController = new EmployeeController(employeeService, employeeKafkaProducerService);

    @InjectMocks
    private GenericExceptionHandlerAdvice genericExceptionHandlerAdvice;

    @Mock
    private EmployeeEntity employeeEntityMock;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        RestAssuredMockMvc.standaloneSetup(employeeController, genericExceptionHandlerAdvice);

        Mockito.when(employeeEntityMock.getEmployeeId()).thenReturn(EMPLOYEE_ID);
        Mockito.when(employeeEntityMock.getDepartmentEntity()).thenReturn(getDepartmentEntity());

        Mockito.doNothing().when(employeeKafkaProducerService).sendMessage(Mockito.any(EmployeeEntity.class),
                Mockito.any(EmployeeEventType.class));
        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeRequestDto.class))).thenReturn(employeeEntityMock);
        Mockito.when(employeeService.updateEmployee(Mockito.anyString(),
                Mockito.any(PutEmployeeRequestDto.class))).thenReturn(employeeEntityMock);
        Mockito.doNothing().when(employeeService).deleteEmployeeById(Mockito.anyString());
        Mockito.when(employeeService.getEmployeeById(Mockito.anyString())).thenReturn(Optional.of(employeeEntityMock));
    }

    @Test
    public void testCreateEmployeeSuccess() {

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(EmployeeRequestDto.builder()
                        .email(EMPLOYEE_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .name(EMPLOYEE_NAME)
                        .departmentId(DEPART_ID).build())
                .when()
                .post(CREATE_EMP_URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(2),
                        JSON_FIELD, Matchers.equalTo(EMPLOYEE_ID));
    }

    @Test
    public void testCreateEmployeeBadRequest() {

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(EmployeeRequestDto.builder()
                        .email(null)
                        .dateOfBirth(LocalDate.now())
                        .name(EMPLOYEE_NAME)
                        .departmentId(DEPART_ID).build())
                .when()
                .post(CREATE_EMP_URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    @Test
    public void testCreateEmployeeWhenDepartmentNotFound() {

        Mockito.when(employeeService.createEmployee(Mockito.any(EmployeeRequestDto.class)))
                .thenThrow(new DepartmentNotFoundException());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(EmployeeRequestDto.builder()
                        .email(EMPLOYEE_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .name(EMPLOYEE_NAME)
                        .departmentId(DEPART_ID).build())
                .when()
                .post(CREATE_EMP_URL)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    @Test
    public void testUpdateEmployeeSuccess() {

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(PutEmployeeRequestDto.builder()
                        .email(EMPLOYEE_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .name(EMPLOYEE_NAME)
                        .departmentId(DEPART_ID).build())
                .when()
                .put(UPDATE_EMP_URL+EMPLOYEE_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(2),
                        JSON_FIELD, Matchers.equalTo(EMPLOYEE_ID));
    }

    @Test
    public void testDeleteEmployeeSuccess() {

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .delete(DELETE_EMP_URL+EMPLOYEE_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(2),
                        JSON_FIELD, Matchers.equalTo(EMPLOYEE_ID));
    }

    @Test
    public void testGetEmployeeDetailsSuccess() {

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_EMP_URL+EMPLOYEE_ID)
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(2),
                        JSON_FIELD, Matchers.equalTo(EMPLOYEE_ID));
    }

    @Test
    public void testGetEmployeeDetailsWhenNoDataFound() {

        Mockito.when(employeeService.getEmployeeById(Mockito.anyString())).thenReturn(Optional.empty());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_EMP_URL+EMPLOYEE_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    private DepartmentEntity getDepartmentEntity() {

        return DepartmentEntity.builder().departId(DEPART_ID).name(DEPART_NAME).build();
    }
}
