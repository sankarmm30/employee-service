package com.takeaway.challenge.controller;

import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.dto.response.DepartmentResponseDto;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController("departmentController")
@RequestMapping(value = "/department")
@Api(value = "Department controller", description = "This controller provides endpoint for managing the department of the company")
public class DepartmentController {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentController.class);

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {

        this.departmentService = departmentService;
    }

    @ApiOperation(value = "Create department with the name provided",
            response = DepartmentResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentResponseDto> createDepartment(final @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {

        DepartmentEntity departmentEntity = this.departmentService.createDepartment(departmentRequestDto);

        LOG.debug("Department has been created with id: {}", departmentEntity.getDepartId());

        return new ResponseEntity<>(
                DepartmentResponseDto.builder()
                        .departmentId(departmentEntity.getDepartId())
                        .message(ApiResponseMessage.DEP_CREATE_MESSAGE.getValue())
                        .build(),
                HttpStatus.CREATED);
    }
}
