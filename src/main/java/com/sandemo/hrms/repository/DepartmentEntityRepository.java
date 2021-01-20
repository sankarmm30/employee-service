package com.sandemo.hrms.repository;

import com.sandemo.hrms.model.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@Repository
public interface DepartmentEntityRepository extends JpaRepository<DepartmentEntity, Long> {

}
