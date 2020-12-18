package com.takeaway.challenge.service.impl;

import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.exception.DepartmentNotFoundException;
import com.takeaway.challenge.exception.EmailIdAlreadyExistsException;
import com.takeaway.challenge.exception.EmployeeNotFoundException;
import com.takeaway.challenge.exception.TakeAwayClientRuntimeException;
import com.takeaway.challenge.exception.TakeAwayServerRuntimeException;
import com.takeaway.challenge.factory.ValidationFactoryService;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.repository.EmployeeEntityRepository;
import com.takeaway.challenge.service.DepartmentService;
import com.takeaway.challenge.service.EmployeeService;
import com.takeaway.challenge.util.Util;
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

@Service("employeeService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private EmployeeEntityRepository employeeEntityRepository;
    private DepartmentService departmentService;
    private ValidationFactoryService validationFactoryService;

    public EmployeeServiceImpl(final EmployeeEntityRepository employeeEntityRepository,
                               final DepartmentService departmentService,
                               final ValidationFactoryService validationFactoryService) {

        this.employeeEntityRepository = employeeEntityRepository;
        this.departmentService = departmentService;
        this.validationFactoryService = validationFactoryService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity createEmployee(EmployeeRequestDto employeeRequestDto) {

        try{

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
        }catch (TakeAwayClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while creating employee",exception);

            throw new TakeAwayServerRuntimeException("Unexpected error occurred", exception);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity updateEmployee(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto) {

        try {

            if(!StringUtils.hasText(employeeId)) {

                throw new TakeAwayClientRuntimeException("The employeeId must not be null or empty for the update");
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

        } catch (TakeAwayClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while updating employee",exception);

            throw new TakeAwayServerRuntimeException("Unexpected error occurred while updating the employee record", exception);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void deleteEmployeeById(final String employeeId) {

        try {

            if(!StringUtils.hasText(employeeId)) {

                throw new TakeAwayClientRuntimeException("The employeeId must not be null or empty for the delete");
            }

            EmployeeEntity employeeEntity = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);

            this.employeeEntityRepository.delete(employeeEntity);

        } catch (TakeAwayClientRuntimeException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while deleting the employee",exception);

            throw new TakeAwayServerRuntimeException("Unexpected error occurred while deleting the employee record", exception);
        }
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
}
