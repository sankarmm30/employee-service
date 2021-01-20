package com.sandemo.hrms.service;

import com.sandemo.hrms.exception.GenericServerRuntimeException;
import com.sandemo.hrms.model.DepartmentEntity;
import com.sandemo.hrms.model.EmployeeEntity;
import com.sandemo.hrms.service.impl.EmployeeKafkaProducerServiceImpl;
import com.sandemo.hrms.EmployeeEventKey;
import com.sandemo.hrms.EmployeeEventType;
import com.sandemo.hrms.EmployeeEventValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

import java.time.LocalDate;
import java.time.ZonedDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 */
@RunWith(JUnit4.class)
public class EmployeeKafkaProducerServiceImplTest {

    private static final String TOPIC = "test";
    private static final String EMP_ID = "test";
    private static final String EMP_NAME = "Test Emp";
    private static final String EMP_EMAIL = "test@test.com";
    private static final String PUT_EMP_ID = "test1";
    private static final String DEPART_NAME = "Test Depart";
    private static final Long DEPART_ID = 1L;
    private static final Long EMP_ID_PK = 10L;

    @Mock
    private Environment environment;
    @Mock
    private KafkaTemplate<EmployeeEventKey, EmployeeEventValue> kafkaTemplate;

    @Mock
    private EmployeeEntity employeeEntityMock;

    @Mock
    private ListenableFuture<SendResult<EmployeeEventKey, EmployeeEventValue>> listenableFutureMock;

    @InjectMocks
    private EmployeeKafkaProducerServiceImpl employeeKafkaProducerService =
            new EmployeeKafkaProducerServiceImpl(environment, kafkaTemplate);

    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);

        Mockito.when(this.kafkaTemplate.send(Mockito.eq(TOPIC),
                Mockito.any(EmployeeEventKey.class), Mockito.any(EmployeeEventValue.class))).thenReturn(listenableFutureMock);

        Mockito.when(employeeEntityMock.getEmployeeId()).thenReturn(EMP_ID);
        Mockito.when(employeeEntityMock.getName()).thenReturn(EMP_NAME);
        Mockito.when(employeeEntityMock.getEmail()).thenReturn(EMP_EMAIL);
        Mockito.when(employeeEntityMock.getDateOfBirth()).thenReturn(LocalDate.now());
        Mockito.when(employeeEntityMock.getDepartmentEntity()).thenReturn(getDepartmentEntity());
        Mockito.when(employeeEntityMock.getCreatedAt()).thenReturn(ZonedDateTime.now());
        Mockito.when(employeeEntityMock.getUpdatedAt()).thenReturn(ZonedDateTime.now());
    }

    @Test
    public void testSendMessageWhenEmployeeCreatedValid() {

        employeeKafkaProducerService.sendMessage(employeeEntityMock, EmployeeEventType.CREATED);

        Assert.assertTrue("Always True", true);
    }

    @Test
    public void testSendMessageWhenEmployeeUpdatedValid() {

        employeeKafkaProducerService.sendMessage(employeeEntityMock, EmployeeEventType.UPDATED);

        Assert.assertTrue("Always True", true);
    }

    @Test
    public void testSendMessageWhenEmployeeDeletedValid() {

        employeeKafkaProducerService.sendMessage(employeeEntityMock, EmployeeEventType.DELETED);

        Assert.assertTrue("Always True", true);
    }

    @Test(expected = GenericServerRuntimeException.class)
    public void testSendMessageWhenEmployeeEntityIsNull() {

        employeeKafkaProducerService.sendMessage(null, EmployeeEventType.CREATED);
    }

    @Test(expected = GenericServerRuntimeException.class)
    public void testSendMessageWhenEmployeeEventTypeIsNull() {

        employeeKafkaProducerService.sendMessage(employeeEntityMock, null);
    }

    private DepartmentEntity getDepartmentEntity() {

        return DepartmentEntity.builder()
                .departId(DEPART_ID)
                .name(DEPART_NAME)
                .build();
    }
}
