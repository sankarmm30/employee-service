package com.takeaway.challenge.service.impl;

import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.exception.DepartmentNotFoundException;
import com.takeaway.challenge.exception.EmailIdAlreadyExistsException;
import com.takeaway.challenge.exception.EmployeeNotFoundException;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import com.takeaway.challenge.repository.EmployeeEntityRepository;
import com.takeaway.challenge.service.DepartmentService;
import com.takeaway.challenge.service.EmployeeService;
import com.takeaway.challenge.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service("employeeService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class EmployeeServiceImpl implements EmployeeService {

    private EmployeeEntityRepository employeeEntityRepository;
    private DepartmentService departmentService;

    public EmployeeServiceImpl(final EmployeeEntityRepository employeeEntityRepository,
                               final DepartmentService departmentService) {

        this.employeeEntityRepository = employeeEntityRepository;
        this.departmentService = departmentService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity createEmployee(EmployeeRequestDto employeeRequestDto) {

        // Validate if the email id is already exists
        if(getEmployeeByEmail(employeeRequestDto.getEmail()).isPresent()) {

            throw new EmailIdAlreadyExistsException();
        };

        DepartmentEntity departmentEntity =
                this.departmentService.getDepartmentById(employeeRequestDto.getDepartmentId())
                        .orElseThrow(DepartmentNotFoundException::new);

        return this.employeeEntityRepository.save(
                EmployeeEntity.builder()
                        .employeeId(UUID.randomUUID().toString())
                        .name(employeeRequestDto.getName())
                        .email(employeeRequestDto.getEmail().toLowerCase())      //Email id will be stored in lower case
                        .dateOfBirth(employeeRequestDto.getDateOfBirth())
                        .departmentEntity(departmentEntity)
                        .createdAt(ZonedDateTime.now())
                        .build()
        );
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public EmployeeEntity updateEmployee(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto) {

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

        return this.employeeEntityRepository.save(
                updateEmployeeAttribute(employeeEntity, putEmployeeRequestDto));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void deleteEmployeeById(final String employeeId) {

        EmployeeEntity employeeEntity = getEmployeeById(employeeId).orElseThrow(EmployeeNotFoundException::new);

        this.employeeEntityRepository.delete(employeeEntity);
    }

    @Override
    public Optional<EmployeeEntity> getEmployeeById(final String employeeId) {

        return this.employeeEntityRepository.findByEmployeeId(employeeId.toLowerCase());
    }

    @Override
    public Optional<EmployeeEntity> getEmployeeByEmail(final String email) {

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
