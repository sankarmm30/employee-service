package com.takeaway.challenge.controller;

import com.takeaway.challenge.EmployeeEventType;
import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.dto.response.DepartmentDto;
import com.takeaway.challenge.dto.response.EmployeeDetailsResponseDto;
import com.takeaway.challenge.dto.response.EmployeeResponseDto;
import com.takeaway.challenge.exception.EmployeeNotFoundException;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.service.EmployeeService;
import com.takeaway.challenge.service.KafkaProducerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@RestController
@RequestMapping(value = "/employee")
@Api(value = "Employee controller", description = "This controller provides endpoint for managing the employee of the company")
public class EmployeeController {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeController.class);

    private EmployeeService employeeService;
    private KafkaProducerService employeeKafkaProducerService;

    public EmployeeController(final EmployeeService employeeService, final KafkaProducerService employeeKafkaProducerService) {

        this.employeeService = employeeService;
        this.employeeKafkaProducerService = employeeKafkaProducerService;
    }

    @ApiOperation(value = "Create employee with the details provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PostMapping(value = "/create", produces = "application/json", consumes = "application/json")
    public ResponseEntity<EmployeeResponseDto> createEmployee(final @Valid @RequestBody EmployeeRequestDto employeeRequestDto) {

        EmployeeEntity employeeEntity = this.employeeService.createEmployee(employeeRequestDto);

        LOG.debug("Created the Employee. employeeId: {}", employeeEntity.getEmployeeId());

        this.employeeKafkaProducerService.sendMessage(employeeEntity, EmployeeEventType.CREATED);

        return new ResponseEntity<>(
                EmployeeResponseDto.builder()
                        .employeeId(employeeEntity.getEmployeeId())
                        .message(ApiResponseMessage.EMP_CREATE_MESSAGE.getValue()).build()
                , HttpStatus.CREATED);
    }

    @ApiOperation(value = "Update employee with the details provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PutMapping(value = "/update/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDto> updateEmployee(final @PathVariable String employeeId,
                                                              final @Valid @RequestBody PutEmployeeRequestDto putEmployeeRequestDto) {

        EmployeeEntity employeeEntity = this.employeeService.updateEmployee(employeeId, putEmployeeRequestDto);

        LOG.debug("Updated the Employee data. employeeId: {}", employeeEntity.getEmployeeId());

        this.employeeKafkaProducerService.sendMessage(employeeEntity, EmployeeEventType.UPDATED);

        return ResponseEntity.ok(EmployeeResponseDto.builder()
                .employeeId(employeeEntity.getEmployeeId())
                .message(ApiResponseMessage.EMP_UPDATE_MESSAGE.getValue())
                .build());
    }

    @ApiOperation(value = "Delete employee with the id provided",
            response = EmployeeResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @DeleteMapping(value = "/delete/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmployeeResponseDto> deleteEmployee(final @PathVariable String employeeId) {

        this.employeeService.deleteEmployeeById(employeeId);

        LOG.debug("Deleted the Employee data. employeeId: {}", employeeId.toLowerCase());

        this.employeeKafkaProducerService.sendMessage(
                EmployeeEntity.builder().employeeId(employeeId.toLowerCase()).build(),
                EmployeeEventType.DELETED);

        return ResponseEntity.ok(EmployeeResponseDto.builder()
                .employeeId(employeeId)
                .message(ApiResponseMessage.EMP_DELETE_MESSAGE.getValue())
                .build());
    }

    @ApiOperation(value = "Get all details for the specific employee",
            response = EmployeeDetailsResponseDto.class, produces = MediaType.APPLICATION_JSON_VALUE)
    @GetMapping(value = "/details", produces = MediaType.APPLICATION_JSON_VALUE)
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
