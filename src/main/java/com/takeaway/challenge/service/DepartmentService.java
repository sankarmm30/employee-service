package com.takeaway.challenge.service;

import com.takeaway.challenge.dto.request.DepartmentRequestDto;
import com.takeaway.challenge.model.DepartmentEntity;

import java.util.Optional;

/**
 * This interface is in charge of managing the Department data
 */
public interface DepartmentService {

    /**
     * This method is in charge of creating Department based on the input provided
     * and will return the newly created Department entity.
     *
     * @param departmentRequestDto
     * @return
     */
    DepartmentEntity createDepartment(final DepartmentRequestDto departmentRequestDto);

    /**
     * This method is in charge of fetching the Department record based on departmentId.
     *
     * @param departmentId
     * @return
     */
    Optional<DepartmentEntity> getDepartmentById(final Long departmentId);
}
