package com.sandemo.hrms.service.impl;

import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.dto.response.DepartmentResponseDto;
import com.sandemo.hrms.exception.GenericClientRuntimeException;
import com.sandemo.hrms.factory.ValidationFactoryService;
import com.sandemo.hrms.model.DepartmentEntity;
import com.sandemo.hrms.service.DepartmentService;
import com.sandemo.hrms.util.Util;
import com.sandemo.hrms.constant.ApiResponseMessage;
import com.sandemo.hrms.exception.GenericServerRuntimeException;
import com.sandemo.hrms.repository.DepartmentEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This service provides the methods which are used to manage the department
 */
@Service("departmentService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger LOG = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private DepartmentEntityRepository departmentEntityRepository;
    private ValidationFactoryService validationFactoryService;

    public DepartmentServiceImpl(final DepartmentEntityRepository departmentEntityRepository,
                                 final ValidationFactoryService validationFactoryService) {

        this.departmentEntityRepository = departmentEntityRepository;
        this.validationFactoryService = validationFactoryService;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public DepartmentEntity createDepartment(final DepartmentRequestDto departmentRequestDto) {

        try {

            // Validating the input parameter
            this.validationFactoryService.validObject(departmentRequestDto);

            // Saving the DepartmentEntity
            return this.departmentEntityRepository.save(
                    DepartmentEntity.builder()
                            .name(departmentRequestDto.getName()).build());

        } catch (GenericClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while creating department",exception);

            throw new GenericServerRuntimeException("Unexpected error occurred", exception);
        }
    }

    @Override
    public Optional<DepartmentEntity> getDepartmentById(final Long departmentId) {

        if(Util.isNull(departmentId)) {
            return Optional.empty();
        }

        return this.departmentEntityRepository.findById(departmentId);
    }

    @Override
    public DepartmentResponseDto createDepartmentAndGetResponse(final DepartmentRequestDto departmentRequestDto) {

        DepartmentEntity departmentEntity = this.createDepartment(departmentRequestDto);

        LOG.debug("Department has been created with id: {}", departmentEntity.getDepartId());

        return DepartmentResponseDto.builder()
                .departmentId(departmentEntity.getDepartId())
                .message(ApiResponseMessage.DEP_CREATE_MESSAGE.getValue())
                .build();
    }
}
