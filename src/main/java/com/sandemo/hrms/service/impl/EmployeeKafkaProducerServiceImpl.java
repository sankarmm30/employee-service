package com.sandemo.hrms.service.impl;

import com.sandemo.hrms.model.EmployeeEntity;
import com.sandemo.hrms.service.KafkaProducerService;
import com.sandemo.hrms.util.Util;
import com.sandemo.hrms.EmployeeEventData;
import com.sandemo.hrms.EmployeeEventKey;
import com.sandemo.hrms.EmployeeEventType;
import com.sandemo.hrms.EmployeeEventValue;
import com.sandemo.hrms.constant.GlobalConstant;
import com.sandemo.hrms.exception.GenericServerRuntimeException;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This service is in charge of producing employee Kafka event
 */
@Service("employeeKafkaProducerService")
public class EmployeeKafkaProducerServiceImpl implements KafkaProducerService {

    private static final String TOPIC_NAME_PROPERTY = "kafka.producer.employee.topic";

    private Environment environment;
    private KafkaTemplate<EmployeeEventKey, EmployeeEventValue> kafkaTemplate;

    private String employeeEventTopic;

    public EmployeeKafkaProducerServiceImpl(final Environment environment,
                                            final KafkaTemplate<EmployeeEventKey, EmployeeEventValue> kafkaTemplate) {

        this.environment = environment;
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostConstruct
    private void init() {

        this.employeeEventTopic = this.environment.getRequiredProperty(TOPIC_NAME_PROPERTY);
    }

    /**
     * This method is in charge for building the message and sending it to Kafka topic.
     *
     * @param employeeEntity
     * @param employeeEventType
     */
    @Override
    public void sendMessage(final EmployeeEntity employeeEntity, final EmployeeEventType employeeEventType) {

        if(Util.isNull(employeeEntity) || Util.isNull(employeeEventType)) {

            throw new GenericServerRuntimeException("The method argument must not be null");
        }

        this.kafkaTemplate.send(employeeEventTopic,
                getEmployeeEventKey(employeeEntity.getEmployeeId()),
                getEmployeeEventValue(employeeEntity, employeeEventType));
    }

    /**
     * This method is in charge of building the Employee event key
     *
     * @param employeeId
     * @return
     */
    private EmployeeEventKey getEmployeeEventKey(final String employeeId) {

        return EmployeeEventKey.newBuilder().setEmployeeId(employeeId).build();
    }

    /**
     * This method is in charge of building the Employee event value.
     *
     * @param employeeEntity
     * @param employeeEventType
     * @return
     */
    private EmployeeEventValue getEmployeeEventValue(final EmployeeEntity employeeEntity,
                                                     final EmployeeEventType employeeEventType) {

        return EmployeeEventValue.newBuilder()
                .setTime(Util.getFormattedTimestamp(ZonedDateTime.now()))
                .setType(employeeEventType)
                .setAppName(GlobalConstant.APP_NAME)
                .setData(
                        employeeEventType.equals(EmployeeEventType.DELETED)
                                ? null :                                                // Event data will be null for the DELETE event
                                EmployeeEventData.newBuilder()
                                        .setName(employeeEntity.getName())
                                        .setEmail(employeeEntity.getEmail())
                                        .setDepartmentId(employeeEntity.getDepartmentEntity().getDepartId())
                                        .setDateOfBirth(Util.getFormattedDate(employeeEntity.getDateOfBirth()))
                                        .setCreatedAt(Util.getFormattedTimestamp(employeeEntity.getCreatedAt()))
                                        .setUpdatedAt(Util.getFormattedTimestamp(employeeEntity.getUpdatedAt()))
                                        .build()

                        )
                .build();
    }
}
