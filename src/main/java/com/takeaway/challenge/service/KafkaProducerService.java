package com.takeaway.challenge.service;

import com.takeaway.challenge.EmployeeEventType;
import com.takeaway.challenge.model.EmployeeEntity;

/**
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
