package com.sandemo.hrms.service;

import com.sandemo.hrms.model.EmployeeEntity;
import com.sandemo.hrms.EmployeeEventType;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * This interface is in charge of managing the Kafka producer functionality
 */
public interface KafkaProducerService {

    /**
     * This method is in charge for building the message and sending to Kafka topic
     *
     * @param employeeEntity
     * @param employeeEventType
     */
    void sendMessage(final EmployeeEntity employeeEntity, final EmployeeEventType employeeEventType);
}
