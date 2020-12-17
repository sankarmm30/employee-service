package com.takeaway.challenge.repository;

import com.takeaway.challenge.model.DepartmentEntity;
import com.takeaway.challenge.model.EmployeeEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest(excludeAutoConfiguration = FlywayAutoConfiguration.class)
@PropertySource("classpath:application-test.properties")
public class EmployeeEntityRepositoryTest {

    private static final String DEPART_HR = "HR";
    private static final String EMPLOYEE_1_ID = "test1";
    private static final String EMPLOYEE_1_NAME = "testname1";
    private static final String EMPLOYEE_1_EMAIL = "email1@email.com";
    private static final String EMPLOYEE_2_ID = "test2";
    private static final String EMPLOYEE_2_NAME = "testname2";
    private static final String EMPLOYEE_2_EMAIL = "email2@email.com";

    @Autowired
    private EmployeeEntityRepository employeeEntityRepository;

    @Autowired
    private DepartmentEntityRepository departmentEntityRepository;

    @Before
    public void init() {

        this.employeeEntityRepository.deleteAll();
        this.departmentEntityRepository.deleteAll();

        DepartmentEntity departmentEntity = departmentEntityRepository.save(
                DepartmentEntity.builder().name(DEPART_HR).build());

        this.employeeEntityRepository.save(
                EmployeeEntity.builder()
                        .employeeId(EMPLOYEE_1_ID)
                        .name(EMPLOYEE_1_NAME)
                        .email(EMPLOYEE_1_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentEntity(departmentEntity).build());

        this.employeeEntityRepository.save(
                EmployeeEntity.builder()
                        .employeeId(EMPLOYEE_2_ID)
                        .name(EMPLOYEE_2_NAME)
                        .email(EMPLOYEE_2_EMAIL)
                        .dateOfBirth(LocalDate.now())
                        .departmentEntity(departmentEntity).build());
    }

    @Test
    public void testFindAll() {

        List<EmployeeEntity> employeeEntities = this.employeeEntityRepository.findAll();

        Assert.assertFalse(employeeEntities.isEmpty());
        Assert.assertEquals(2, employeeEntities.size());
    }

    @Test
    public void testFindByEmployeeId() {

        Optional<EmployeeEntity> employeeEntityOptional = this.employeeEntityRepository.findByEmployeeId(EMPLOYEE_1_ID);

        Assert.assertTrue(employeeEntityOptional.isPresent());
        Assert.assertEquals(EMPLOYEE_1_NAME, employeeEntityOptional.get().getName());
        Assert.assertEquals(EMPLOYEE_1_EMAIL, employeeEntityOptional.get().getEmail());
        Assert.assertEquals(DEPART_HR, employeeEntityOptional.get().getDepartmentEntity().getName());
    }

    @Test
    public void testFindByEmail() {

        Optional<EmployeeEntity> employeeEntityOptional = this.employeeEntityRepository.findByEmail(EMPLOYEE_2_EMAIL);

        Assert.assertTrue(employeeEntityOptional.isPresent());
        Assert.assertEquals(EMPLOYEE_2_NAME, employeeEntityOptional.get().getName());
        Assert.assertEquals(DEPART_HR, employeeEntityOptional.get().getDepartmentEntity().getName());
    }
}
