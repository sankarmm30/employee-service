package com.takeaway.challenge.controller;

import com.takeaway.challenge.EmployeeServiceApp;
import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.dto.response.DepartmentResponseDto;
import com.takeaway.challenge.dto.response.GenericExceptionResponse;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EmployeeServiceApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class DepartmentControllerIntegrationTest {

    private static final String BASE_URL = "http://localhost:";
    private static final String CREATE_DEPART_URL = "/takeaway/department/create";
    private static final String DEPART_NAME = "HR";

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private static final HttpHeaders HEADERS = new HttpHeaders();
    static {
        HEADERS.setContentType(MediaType.APPLICATION_JSON);
    }

    @LocalServerPort
    private Integer port;

    @Test
    public void testCreateDepartmentValid() {

        // Given
        HttpEntity<DepartmentRequestDto> entity = new HttpEntity<DepartmentRequestDto>(
                DepartmentRequestDto.builder().name(DEPART_NAME).build(), HEADERS);

        ResponseEntity<DepartmentResponseDto> response = restTemplate.exchange(
                BASE_URL + port + CREATE_DEPART_URL, HttpMethod.POST, entity, DepartmentResponseDto.class);

        // Result
        Assert.assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertNotNull(response.getBody().getDepartmentId());
        Assert.assertEquals(ApiResponseMessage.DEP_CREATE_MESSAGE.getValue(), response.getBody().getMessage());
    }

    @Test
    public void testCreateDepartmentWithNameIsNull() {

        // Given
        HttpEntity<DepartmentRequestDto> entity = new HttpEntity<DepartmentRequestDto>(
                DepartmentRequestDto.builder().name(null).build(), HEADERS);

        ResponseEntity<GenericExceptionResponse> response = restTemplate.exchange(
                BASE_URL + port + CREATE_DEPART_URL, HttpMethod.POST, entity, GenericExceptionResponse.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertFalse(response.getBody().getErrors().isEmpty());
        Assert.assertEquals(1, response.getBody().getErrors().size());
        Assert.assertEquals("Name cannot be blank", response.getBody().getErrors().get(0));
    }

    @Test
    public void testCreateDepartmentRequesrBodyIsNull() {

        // Given
        HttpEntity<DepartmentRequestDto> entity = new HttpEntity<DepartmentRequestDto>(null, HEADERS);

        ResponseEntity<GenericExceptionResponse> response = restTemplate.exchange(
                BASE_URL + port + CREATE_DEPART_URL, HttpMethod.POST, entity, GenericExceptionResponse.class);

        // Result
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        Assert.assertNotNull(response.getBody());
        Assert.assertFalse(response.getBody().getErrors().isEmpty());
        Assert.assertEquals(1, response.getBody().getErrors().size());
    }
}
