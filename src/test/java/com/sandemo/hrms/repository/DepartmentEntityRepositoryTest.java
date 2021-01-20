package com.sandemo.hrms.repository;

import com.sandemo.hrms.model.DepartmentEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
@PropertySource("classpath:application-test.properties")
public class DepartmentEntityRepositoryTest {

    private static final String NAME_HR = "HR";
    private static final String NAME_FINANCE = "Finance";

    @Autowired
    private DepartmentEntityRepository departmentEntityRepository;

    @Before
    public void init() {

        this.departmentEntityRepository.save(DepartmentEntity.builder().name(NAME_HR).build());
        this.departmentEntityRepository.save(DepartmentEntity.builder().name(NAME_FINANCE).build());
    }

    @Test
    public void testFindAll() {

        List<DepartmentEntity> departmentEntities = this.departmentEntityRepository.findAll();

        Assert.assertFalse(departmentEntities.isEmpty());
        Assert.assertEquals(2, departmentEntities.size());
    }
}
