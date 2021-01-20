package com.sandemo.hrms.service;

import com.sandemo.hrms.dto.request.EmployeeRequestDto;
import com.sandemo.hrms.dto.request.PutEmployeeRequestDto;
import com.sandemo.hrms.dto.response.EmployeeDetailsResponseDto;
import com.sandemo.hrms.dto.response.EmployeeResponseDto;
import com.sandemo.hrms.model.EmployeeEntity;

import java.util.Optional;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
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
     * This method is in charge of creating Employee data based on the input provided, produce message into kafka topic
     * and return response
     *
     * @param employeeRequestDto
     * @return
     */
    EmployeeResponseDto createEmployeeAndGetResponse(final EmployeeRequestDto employeeRequestDto);

    /**
     * This method is in charge of updating Employee attributes on the input provided
     * and will return the newly created Employee entity.
     *
     * @param putEmployeeRequestDto
     * @return
     */
    EmployeeEntity updateEmployee(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto);

    /**
     * This method is in charge of updating Employee attributes on the input provided, produce message into kafka topic
     * and return response
     *
     * @param employeeId
     * @param putEmployeeRequestDto
     * @return
     */
    EmployeeResponseDto updateEmployeeAndGetResponse(final String employeeId, final PutEmployeeRequestDto putEmployeeRequestDto);

    /**
     * This method is in charge of deleting Employee record by employeeId.
     *
     * @param employeeId
     */
    void deleteEmployeeById(final String employeeId);

    /**
     * This method is in charge of deleting Employee record by employeeId, produce message into kafka topic
     * and return response
     *
     * @param employeeId
     * @return
     */
    EmployeeResponseDto deleteEmployeeByIdAndGetResponse(final String employeeId);

    /**
     * This method is in charge of fetching the Employee record by employeeId.
     *
     * @param employeeId
     * @return
     */
    Optional<EmployeeEntity> getEmployeeById(final String employeeId);

    /**
     * This method is in charge of fetching the Employee record by email.
     *
     * @param email
     * @return
     */
    Optional<EmployeeEntity> getEmployeeByEmail(final String email);

    /**
     * This method is in charge of fetching the Employee details by employeeId and return employee details response.
     *
     * @return
     */
    EmployeeDetailsResponseDto getEmployeeDetailsById(final String employeeId);
}
