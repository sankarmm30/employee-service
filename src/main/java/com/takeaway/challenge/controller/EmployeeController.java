package com.takeaway.challenge.controller;

import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.dto.response.DepartmentDto;
import com.takeaway.challenge.dto.response.EmployeeDetailsResponseDto;
import com.takeaway.challenge.dto.response.EmployeeResponseDto;
import com.takeaway.challenge.exception.EmployeeNotFoundException;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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

@RestController
@RequestMapping(value = "/employee")
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private static final String CREATE_MESSAGE = "Employee has been created";
    private static final String UPDATE_MESSAGE = "Employee details have been updated";
    private static final String DELETE_MESSAGE = "Employee has been deleted";

    private EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService) {

        this.employeeService = employeeService;
    }

    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeResponseDto> createEmployee(final @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {

        EmployeeEntity employeeEntity = this.employeeService.createEmployee(employeeRequestDto);

        LOG.debug("Created the Employee. employeeId: {}", employeeEntity.getEmployeeId());

        return new ResponseEntity<>(
                EmployeeResponseDto.builder()
                        .employeeId(employeeEntity.getEmployeeId())
                        .message(CREATE_MESSAGE).build()
                , HttpStatus.CREATED);
    }

    @PutMapping(value = "/update/{employeeId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeResponseDto> updateEmployee(final @PathVariable String employeeId,
                                                              final @Valid @RequestBody PutEmployeeRequestDto putEmployeeRequestDto) {

        EmployeeEntity employeeEntity = this.employeeService.updateEmployee(employeeId, putEmployeeRequestDto);

        LOG.debug("Updated the Employee data. employeeId: {}", employeeEntity.getEmployeeId());

        return ResponseEntity.ok(EmployeeResponseDto.builder()
                .employeeId(employeeEntity.getEmployeeId())
                .message(UPDATE_MESSAGE)
                .build());
    }

    @DeleteMapping(value = "/delete/{employeeId}", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeResponseDto> deleteEmployee(final @PathVariable String employeeId) {

        this.employeeService.deleteEmployeeById(employeeId);

        LOG.debug("Deleted the Employee data. employeeId: {}", employeeId.toLowerCase());

        return ResponseEntity.ok(EmployeeResponseDto.builder()
                .employeeId(employeeId)
                .message(DELETE_MESSAGE)
                .build());
    }

    @GetMapping(value = "/details", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeDetailsResponseDto> getEmployeeDetails(final @RequestParam String employeeId) {

        EmployeeEntity employeeEntity = this.employeeService.getEmployeeById(employeeId)
                .orElseThrow(EmployeeNotFoundException::new);

        return ResponseEntity.ok(buildEmployeeDetailsResponse(employeeEntity));
    }

    /**
     * This method is in charge of building the Employee details response from Employee entity
     *
     * @param employeeEntity
     * @return
     */
    private static EmployeeDetailsResponseDto buildEmployeeDetailsResponse(final EmployeeEntity employeeEntity) {

        LOG.debug("Building EmployeeDetailsResponseDto");

        return EmployeeDetailsResponseDto.builder()
                .employeeId(employeeEntity.getEmployeeId())
                .name(employeeEntity.getName())
                .email(employeeEntity.getEmail())
                .dataOfBirth(employeeEntity.getDateOfBirth())
                .department(DepartmentDto.builder()
                        .departmentId(employeeEntity.getDepartmentEntity().getDepartId())
                        .name(employeeEntity.getDepartmentEntity().getName())
                        .build())
                .createAt(employeeEntity.getCreatedAt())
                .lastUpdatedAt(employeeEntity.getUpdatedAt())
                .build();
    }
}
