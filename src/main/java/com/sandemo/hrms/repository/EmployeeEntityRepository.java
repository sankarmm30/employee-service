package com.sandemo.hrms.repository;

import com.sandemo.hrms.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Repository
public interface EmployeeEntityRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmployeeId(final String employeeId);

    Optional<EmployeeEntity> findByEmail(final String email);
}
