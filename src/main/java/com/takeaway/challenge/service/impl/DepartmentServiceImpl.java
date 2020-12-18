package com.takeaway.challenge.service.impl;

import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.factory.ValidationFactoryService;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.repository.DepartmentEntityRepository;
import com.takeaway.challenge.service.DepartmentService;
import com.takeaway.challenge.util.Util;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("departmentService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class DepartmentServiceImpl implements DepartmentService {

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

        this.validationFactoryService.validObject(departmentRequestDto);

        return this.departmentEntityRepository.save(
                DepartmentEntity.builder()
                .name(departmentRequestDto.getName()).build());
    }

    @Override
    public Optional<DepartmentEntity> getDepartmentById(final Long departmentId) {

        if(Util.isNull(departmentId)) {
            return Optional.empty();
        }

        return this.departmentEntityRepository.findById(departmentId);
    }
}
