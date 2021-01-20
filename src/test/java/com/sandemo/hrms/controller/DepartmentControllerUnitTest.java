package com.sandemo.hrms.controller;

import com.sandemo.hrms.constant.ApiResponseMessage;
import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.dto.response.DepartmentResponseDto;
import com.sandemo.hrms.exception.GenericClientRuntimeException;
import com.sandemo.hrms.exception.GenericServerRuntimeException;
import com.sandemo.hrms.exception.advice.GenericExceptionHandlerAdvice;
import com.sandemo.hrms.service.DepartmentService;
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

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class DepartmentControllerUnitTest {

    private static final String DEPART_NAME = "HR";
    private static final String URL = "/department/create";
    private static final String JSON_FIELD = "departmentId";
    private static final Long DEPART_ID = 1L;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController = new DepartmentController(departmentService);

    @InjectMocks
    private GenericExceptionHandlerAdvice genericExceptionHandlerAdvice;

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        RestAssuredMockMvc.standaloneSetup(departmentController, genericExceptionHandlerAdvice);
    }

    @Test
    public void testCreateDepartmentSuccess() {

        Mockito.when(departmentService.createDepartmentAndGetResponse(Mockito.any(DepartmentRequestDto.class)))
                .thenReturn(getDepartmentResponseDto());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(DepartmentRequestDto.builder().name(DEPART_NAME).build())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(2),
                        JSON_FIELD, Matchers.equalTo(DEPART_ID.intValue()));
    }

    @Test
    public void testCreateDepartmentWhenBadRequest() {

        Mockito.when(departmentService.createDepartmentAndGetResponse(Mockito.any(DepartmentRequestDto.class)))
                .thenReturn(getDepartmentResponseDto());

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(DepartmentRequestDto.builder().name(null).build())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    @Test
    public void testCreateDepartmentWhenUnexpectedException() {

        Mockito.when(departmentService.createDepartmentAndGetResponse(Mockito.any(DepartmentRequestDto.class)))
                .thenThrow(new GenericServerRuntimeException("Unexpected error"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(DepartmentRequestDto.builder().name(DEPART_NAME).build())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    @Test
    public void testCreateDepartmentWhenClientRuntimeException() {

        Mockito.when(departmentService.createDepartmentAndGetResponse(Mockito.any(DepartmentRequestDto.class)))
                .thenThrow(new GenericClientRuntimeException("Unexpected error"));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(DepartmentRequestDto.builder().name(DEPART_NAME).build())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    @Test
    public void testCreateDepartmentWhenClientRuntimeExceptionWithCause() {

        Mockito.when(departmentService.createDepartmentAndGetResponse(Mockito.any(DepartmentRequestDto.class)))
                .thenThrow(new GenericClientRuntimeException("Unexpected error", new IllegalArgumentException()));

        RestAssuredMockMvc.given()
                .contentType(ContentType.JSON)
                .body(DepartmentRequestDto.builder().name(DEPART_NAME).build())
                .when()
                .post(URL)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("", Matchers.aMapWithSize(5));
    }

    private DepartmentResponseDto getDepartmentResponseDto() {

        return DepartmentResponseDto.builder()
                .departmentId(DEPART_ID)
                .message(ApiResponseMessage.DEP_CREATE_MESSAGE.getValue())
                .build();
    }
}
