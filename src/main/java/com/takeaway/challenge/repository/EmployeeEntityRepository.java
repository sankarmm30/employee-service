package com.takeaway.challenge.repository;

import com.takeaway.challenge.model.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeEntityRepository extends JpaRepository<EmployeeEntity, Long> {

    Optional<EmployeeEntity> findByEmployeeId(final String employeeId);

    Optional<EmployeeEntity> findByEmail(final String email);
}
