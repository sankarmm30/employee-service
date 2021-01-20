package com.sandemo.hrms.controller;

import com.sandemo.hrms.constant.ApiResponseMessage;
import com.sandemo.hrms.dto.request.EmployeeRequestDto;
import com.sandemo.hrms.dto.request.PutEmployeeRequestDto;
import com.sandemo.hrms.dto.response.DepartmentDto;
import com.sandemo.hrms.dto.response.EmployeeDetailsResponseDto;
import com.sandemo.hrms.dto.response.EmployeeResponseDto;
import com.sandemo.hrms.exception.DepartmentNotFoundException;
import com.sandemo.hrms.exception.EmployeeNotFoundException;
import com.sandemo.hrms.exception.advice.GenericExceptionHandlerAdvice;
import com.sandemo.hrms.service.EmployeeService;
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
import java.time.ZonedDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
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

    @InjectMocks
    private EmployeeController employeeController = new EmployeeController(employeeService);

    @InjectMocks
    private GenericExceptionHandlerAdvice genericExceptionHandlerAdvice;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        RestAssuredMockMvc.standaloneSetup(employeeController, genericExceptionHandlerAdvice);

        Mockito.when(employeeService.createEmployeeAndGetResponse(Mockito.any(EmployeeRequestDto.class)))
                .thenReturn(getEmployeeResponseDto(ApiResponseMessage.EMP_CREATE_MESSAGE.getValue()));
        Mockito.when(employeeService.updateEmployeeAndGetResponse(Mockito.anyString(), Mockito.any(PutEmployeeRequestDto.class)))
                .thenReturn(getEmployeeResponseDto(ApiResponseMessage.EMP_UPDATE_MESSAGE.getValue()));
        Mockito.when(employeeService.deleteEmployeeByIdAndGetResponse(Mockito.anyString()))
                .thenReturn(getEmployeeResponseDto(ApiResponseMessage.EMP_DELETE_MESSAGE.getValue()));
        Mockito.when(employeeService.getEmployeeDetailsById(Mockito.anyString())).thenReturn(getEmployeeDetailsResponseDto());
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

        Mockito.when(employeeService.createEmployeeAndGetResponse(Mockito.any(EmployeeRequestDto.class)))
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
                .body("", Matchers.aMapWithSize(7),
                        JSON_FIELD, Matchers.equalTo(EMPLOYEE_ID));
    }

    @Test
    public void testGetEmployeeDetailsWhenNoDataFound() {

        Mockito.when(employeeService.getEmployeeDetailsById(Mockito.anyString())).thenThrow(new EmployeeNotFoundException());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_EMP_URL+EMPLOYEE_ID)
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    private EmployeeDetailsResponseDto getEmployeeDetailsResponseDto() {

        return EmployeeDetailsResponseDto.builder()
                .employeeId(EMPLOYEE_ID)
                .name(EMPLOYEE_NAME)
                .email(EMPLOYEE_EMAIL)
                .dataOfBirth(LocalDate.now())
                .department(DepartmentDto.builder()
                        .departmentId(DEPART_ID)
                        .name(DEPART_NAME)
                        .build())
                .createAt(ZonedDateTime.now())
                .lastUpdatedAt(ZonedDateTime.now())
                .build();
    }

    private EmployeeResponseDto getEmployeeResponseDto(final String message) {

        return EmployeeResponseDto.builder()
                .employeeId(EMPLOYEE_ID)
                .message(message).build();
    }
}
