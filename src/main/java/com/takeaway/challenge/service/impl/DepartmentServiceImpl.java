package com.takeaway.challenge.service.impl;

import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.repository.DepartmentEntityRepository;
import com.takeaway.challenge.service.DepartmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service("departmentService")
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
public class DepartmentServiceImpl implements DepartmentService {

    private DepartmentEntityRepository departmentEntityRepository;

    public DepartmentServiceImpl(DepartmentEntityRepository departmentEntityRepository) {

        this.departmentEntityRepository = departmentEntityRepository;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public DepartmentEntity createDepartment(final DepartmentRequestDto departmentRequestDto) {

        return this.departmentEntityRepository.save(
                DepartmentEntity.builder()
                .name(departmentRequestDto.getName()).build());
    }

    @Override
    public Optional<DepartmentEntity> getDepartmentById(final Long departmentId) {

        return this.departmentEntityRepository.findById(departmentId);
    }
}
