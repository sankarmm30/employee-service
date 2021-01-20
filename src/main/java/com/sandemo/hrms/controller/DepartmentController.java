package com.sandemo.hrms.controller;

import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.dto.response.DepartmentResponseDto;
import com.sandemo.hrms.service.DepartmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RestController("departmentController")
@RequestMapping(value = "/department")
@Api(value = "Department controller", description = "This controller provides endpoint for managing the department of the company")
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {

        this.departmentService = departmentService;
    }

    @ApiOperation(value = "Create department with the name provided",
            response = DepartmentResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DepartmentResponseDto> createDepartment(final @Valid @RequestBody DepartmentRequestDto departmentRequestDto) {

        return new ResponseEntity<>(
                this.departmentService.createDepartmentAndGetResponse(departmentRequestDto),
                HttpStatus.CREATED);
    }
}
