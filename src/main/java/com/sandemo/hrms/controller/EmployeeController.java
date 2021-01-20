package com.sandemo.hrms.controller;

import com.sandemo.hrms.dto.request.EmployeeRequestDto;
import com.sandemo.hrms.dto.request.PutEmployeeRequestDto;
import com.sandemo.hrms.dto.response.EmployeeDetailsResponseDto;
import com.sandemo.hrms.dto.response.EmployeeResponseDto;
import com.sandemo.hrms.service.EmployeeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RestController
@RequestMapping(value = "/employee")
@Api(value = "Employee controller", description = "This controller provides endpoint for managing the employee of the company")
public class EmployeeController {

    private EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    @ApiOperation(value = "Create employee with the details provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeResponseDto> createEmployee(final @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {

        return new ResponseEntity<>(
                this.employeeService.createEmployeeAndGetResponse(employeeRequestDto),
                HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update employee with the details provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/update/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDto> updateEmployee(final @PathVariable String employeeId,
                                                              final @Valid @RequestBody PutEmployeeRequestDto putEmployeeRequestDto) {

        return ResponseEntity.ok(this.employeeService.updateEmployeeAndGetResponse(employeeId, putEmployeeRequestDto));
    }

    @ApiOperation(value = "Delete employee with the id provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping(value = "/delete/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDto> deleteEmployee(final @PathVariable String employeeId) {

        return ResponseEntity.ok(this.employeeService.deleteEmployeeByIdAndGetResponse(employeeId));
    }

    @ApiOperation(value = "Get all details for the specific employee",
            response = EmployeeDetailsResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeDetailsResponseDto> getEmployeeDetails(final @RequestParam String employeeId) {

        return ResponseEntity.ok(this.employeeService.getEmployeeDetailsById(employeeId));
    }
}
