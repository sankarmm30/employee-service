package com.sandemo.hrms.service;

import com.sandemo.hrms.dto.request.DepartmentRequestDto;
import com.sandemo.hrms.dto.response.DepartmentResponseDto;
import com.sandemo.hrms.model.DepartmentEntity;

import java.util.Optional;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
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

    /**
     * This method is in charge of creating Department based on the input provided
     * and will return the DepartmentResponseDto
     *
     * @param departmentRequestDto
     * @return
     */
    DepartmentResponseDto createDepartmentAndGetResponse(final DepartmentRequestDto departmentRequestDto);
}
