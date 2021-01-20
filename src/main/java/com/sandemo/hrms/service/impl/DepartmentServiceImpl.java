package com.takeaway.challenge.service.impl;

import com.takeaway.challenge.constant.ApiResponseMessage;
import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.dto.response.DepartmentResponseDto;
import com.takeaway.challenge.exception.TakeAwayClientRuntimeException;
import com.takeaway.challenge.exception.TakeAwayServerRuntimeException;
import com.takeaway.challenge.factory.ValidationFactoryService;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.repository.DepartmentEntityRepository;
import com.takeaway.challenge.service.DepartmentService;
import com.takeaway.challenge.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.util.Optional;

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

        } catch (TakeAwayClientRuntimeException | ConstraintViolationException exception) {

            throw exception;

        } catch (Exception exception) {

            LOG.error("Exception while creating department",exception);

            throw new TakeAwayServerRuntimeException("Unexpected error occurred", exception);
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
