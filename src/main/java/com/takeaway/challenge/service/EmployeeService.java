package com.takeaway.challenge.service;

import com.takeaway.challenge.dto.request.EmployeeRequestDto;
import com.takeaway.challenge.dto.request.PutEmployeeRequestDto;
import com.takeaway.challenge.model.EmployeeEntity;

/**
 * This interface is in charge of managing the Employee data
 */
public interface EmployeeService {

    /**
     * This method is in charge of creating Employee data based on the input provided
     * and will return the newly created Employee entity.
     *
     * @param employeeRequestDto
     * @return
     */
    EmployeeEntity createEmployee(final EmployeeRequestDto employeeRequestDto);

    /**
     * This method is in charge of updating Employee attributes on the input provided
     * and will return the newly created Employee entity.
     *
     * @param putEmployeeRequestDto
     * @return
     */
    EmployeeEntity updateEmployee(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto);

    /**
     * This method is in charge of deleting Employee record by employeeId.
     *
     * @param employeeId
     */
    void deleteEmployeeById(final String employeeId);

    /**
     * This method is in charge of fetching the Employee record based on employeeId.
     *
     * @param employeeId
     * @return
     */
    EmployeeEntity getEmployeeById(final String employeeId);
}
