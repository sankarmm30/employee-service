package com.sandemo.hrms.service.impl;

import com.sandemo.hrms.dto.request.EmployeeRequestDto;
import com.sandemo.hrms.dto.request.PutEmployeeRequestDto;
import com.sandemo.hrms.dto.response.DepartmentDto;
import com.sandemo.hrms.dto.response.EmployeeDetailsResponseDto;
import com.sandemo.hrms.dto.response.EmployeeResponseDto;
import com.sandemo.hrms.model.DepartmentEntity;
import com.sandemo.hrms.model.EmployeeEntity;
import com.sandemo.hrms.service.DepartmentService;
import com.sandemo.hrms.service.EmployeeService;
import com.sandemo.hrms.service.KafkaProducerService;
import com.sandemo.hrms.util.Util;
import com.sandemo.hrms.EmployeeEventType;
import com.sandemo.hrms.constant.ApiResponseMessage;
import com.sandemo.hrms.exception.DepartmentNotFoundException;
import com.sandemo.hrms.exception.EmailIdAlreadyExistsException;
import com.sandemo.hrms.exception.EmployeeNotFoundException;
import com.sandemo.hrms.exception.GenericClientRuntimeException;
import com.sandemo.hrms.exception.GenericServerRuntimeException;
import com.sandemo.hrms.factory.ValidationFactoryService;
import com.sandemo.hrms.repository.EmployeeEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This service provides the methods which are used to manage the employee data
 */
@Service("employeeService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeEntityRepository employeeEntityRepository;
    private DepartmentService departmentService;
    private ValidationFactoryService validationFactoryService;
    private KafkaProducerService employeeKafkaProducerService;

    public EmployeeServiceImpl(final EmployeeEntityRepository employeeEntityRepository,
                               final DepartmentService departmentService,
                               final ValidationFactoryService validationFactoryService,
                               final KafkaProducerService employeeKafkaProducerService) {

        this.employeeEntityRepository = employeeEntityRepository;
        this.departmentService = departmentService;
        this.validationFactoryService = validationFactoryService;
        this.employeeKafkaProducerService = employeeKafkaProducerService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity createEmployee(EmployeeRequestDto employeeRequestDto) {

        try {

            // Validating the input parameter
            this.validationFactoryService.validObject(employeeRequestDto);

            // Validate if the email id is already exists
            if(getEmployeeByEmail(employeeRequestDto.getEmail()).isPresent()) {

                throw new EmailIdAlreadyExistsException();
            }

            // Saving the EmployeeEntity
            DepartmentEntity departmentEntity =
                    this.departmentService.getDepartmentById(employeeRequestDto.getDepartmentId())
                            .orElseThrow(DepartmentNotFoundException::new);

            // Saving the EmployeeEntity
            return this.employeeEntityRepository.save(
                    EmployeeEntity.builder()
                            .employeeId(UUID.randomUUID().toString())                //UUID version 4 is used
                            .name(employeeRequestDto.getName())
                            .email(employeeRequestDto.getEmail().toLowerCase())      //Email id will be stored in lower case
                            .dateOfBirth(employeeRequestDto.getDateOfBirth())
                            .departmentEntity(departmentEntity)
                            .createdAt(ZonedDateTime.now())
                            .build()
            );
        } catch (GenericClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while creating employee",exception);

            throw new GenericServerRuntimeException("Unexpected error occurred", exception);
        }
    }

    @Override
    public EmployeeResponseDto createEmployeeAndGetResponse(EmployeeRequestDto employeeRequestDto) {

        EmployeeEntity employeeEntity = this.createEmployee(employeeRequestDto);

        LOG.debug("Created the employee with id: {}", employeeEntity.getEmployeeId());

        // Producing message into Kafka topic
        this.employeeKafkaProducerService.sendMessage(employeeEntity, EmployeeEventType.CREATED);

        return buildEmployeeResponseDto(employeeEntity.getEmployeeId(), ApiResponseMessage.EMP_CREATE_MESSAGE.getValue());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity updateEmployee(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto) {

        try {

            if(!StringUtils.hasText(employeeId)) {

                throw new GenericClientRuntimeException("The employeeId must not be null or empty for the update");
            }

            // Validating the input parameter
            this.validationFactoryService.validObject(putEmployeeRequestDto);

            // Fetching employee record by using employeeId
            EmployeeEntity employeeEntity = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);

            // Validating if the email id is already exists with another employee
            if(StringUtils.hasText(putEmployeeRequestDto.getEmail())) {

                Optional<EmployeeEntity> employeeEntityOptional=  getEmployeeByEmail(putEmployeeRequestDto.getEmail());

                if(employeeEntityOptional.isPresent()
                        && !employeeEntityOptional.get().getEmployeeId().equals(employeeEntity.getEmployeeId())) {

                    throw new EmailIdAlreadyExistsException();
                }
            }

            // Validating if the department exists in the database
            if(Util.isNotNull(putEmployeeRequestDto.getDepartmentId())) {

                DepartmentEntity departmentEntity =
                        this.departmentService.getDepartmentById(putEmployeeRequestDto.getDepartmentId())
                                .orElseThrow(DepartmentNotFoundException::new);

                employeeEntity.setDepartmentEntity(departmentEntity);
            }

            // Saving updated EmployeeEntity
            return this.employeeEntityRepository.save(
                    updateEmployeeAttribute(employeeEntity, putEmployeeRequestDto));

        } catch (GenericClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while updating employee",exception);

            throw new GenericServerRuntimeException("Unexpected error occurred while updating the employee record", exception);
        }
    }

    @Override
    public EmployeeResponseDto updateEmployeeAndGetResponse(String employeeId, PutEmployeeRequestDto putEmployeeRequestDto) {

        EmployeeEntity employeeEntity = this.updateEmployee(employeeId, putEmployeeRequestDto);

        LOG.debug("Updated the Employee data. employeeId: {}", employeeEntity.getEmployeeId());

        // Producing message into Kafka topic
        this.employeeKafkaProducerService.sendMessage(employeeEntity, EmployeeEventType.UPDATED);

        return buildEmployeeResponseDto(employeeEntity.getEmployeeId(), ApiResponseMessage.EMP_UPDATE_MESSAGE.getValue());
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void deleteEmployeeById(final String employeeId) {

        try {

            if(!StringUtils.hasText(employeeId)) {

                throw new GenericClientRuntimeException("The employeeId must not be null or empty for the delete");
            }

            EmployeeEntity employeeEntity = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);

            this.employeeEntityRepository.delete(employeeEntity);

        } catch (GenericClientRuntimeException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while deleting the employee",exception);

            throw new GenericServerRuntimeException("Unexpected error occurred while deleting the employee record", exception);
        }
    }

    @Override
    public EmployeeResponseDto deleteEmployeeByIdAndGetResponse(String employeeId) {

        this.deleteEmployeeById(employeeId);

        LOG.debug("Deleted the Employee data. employeeId: {}", employeeId.toLowerCase());

        // Producing message into Kafka topic
        this.employeeKafkaProducerService.sendMessage(
                EmployeeEntity.builder().employeeId(employeeId.toLowerCase()).build(), EmployeeEventType.DELETED);

        return buildEmployeeResponseDto(employeeId, ApiResponseMessage.EMP_DELETE_MESSAGE.getValue());
    }

    @Override
    public Optional<EmployeeEntity> getEmployeeById(final String employeeId) {

        if(!StringUtils.hasText(employeeId)) {

            return Optional.empty();
        }

        return this.employeeEntityRepository.findByEmployeeId(employeeId.toLowerCase());
    }

    @Override
    public Optional<EmployeeEntity> getEmployeeByEmail(final String email) {

        if(!StringUtils.hasText(email)) {

            return Optional.empty();
        }

        return this.employeeEntityRepository.findByEmail(email.toLowerCase());
    }

    @Override
    public EmployeeDetailsResponseDto getEmployeeDetailsById(final String employeeId) {

        if(!StringUtils.hasText(employeeId)) {

            throw new GenericClientRuntimeException("The employeeId must not be null or empty");
        }

        EmployeeEntity employeeEntity = this.getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);

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

    private EmployeeEntity updateEmployeeAttribute(final EmployeeEntity employeeEntity,
                                                   final PutEmployeeRequestDto putEmployeeRequestDto) {

        if(StringUtils.hasText(putEmployeeRequestDto.getEmail())) {

            employeeEntity.setEmail(putEmployeeRequestDto.getEmail().toLowerCase());
        }

        if(StringUtils.hasText(putEmployeeRequestDto.getName())) {

            employeeEntity.setName(putEmployeeRequestDto.getName());
        }

        if(Util.isNotNull(putEmployeeRequestDto.getDateOfBirth())) {

            employeeEntity.setDateOfBirth(putEmployeeRequestDto.getDateOfBirth());
        }

        employeeEntity.setUpdatedAt(ZonedDateTime.now());

        return employeeEntity;
    }

    private EmployeeResponseDto buildEmployeeResponseDto(final String employeeId, final String message) {

        return EmployeeResponseDto.builder()
                .employeeId(employeeId)
                .message(message).build();
    }
}
