package com.takeaway.challenge.context;

import com.takeaway.challenge.EmployeeEventKey;
import com.takeaway.challenge.EmployeeEventValue;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.ProducerListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Avro Producer Configuration for Employee Event
 */
@Configuration
public class EmployeeEventKafkaProducerContext {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeEventKafkaProducerContext.class);

    private static final String KAFKA_PRODUCER_PREFIX = "kafka.producer.";
    private static final String DEFAULT_SCHEMA_REGISTRY_URL = "http://localhost:8081";
    private static final Boolean SPECIFIC_AVRO_READER_CONFIG_VALUE = true;

    @Autowired
    private Environment environment;

    public EmployeeEventKafkaProducerContext(Environment environment) {

        this.environment = environment;
    }

    @Bean
    public Map<String, Object> getProducerConfig() {

        Map<String, Object> props = new HashMap<>();

        // bootstrap.servers
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                environment.getRequiredProperty(KAFKA_PRODUCER_PREFIX + ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));

        // key.serializer & value.serializer
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);

        // schema.registry.url
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                environment.getProperty(KAFKA_PRODUCER_PREFIX + KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG,
                        DEFAULT_SCHEMA_REGISTRY_URL));

        // auto.register.schemas
        props.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS,
                environment.getProperty(KAFKA_PRODUCER_PREFIX + KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, Boolean.class, false));

        // specific.avro.reader
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
                environment.getProperty(KAFKA_PRODUCER_PREFIX + KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG,
                        Boolean.class, SPECIFIC_AVRO_READER_CONFIG_VALUE));

        // key.subject.name.strategy
        props.put(AbstractKafkaAvroSerDeConfig.KEY_SUBJECT_NAME_STRATEGY,
                environment.getProperty(KAFKA_PRODUCER_PREFIX + AbstractKafkaAvroSerDeConfig.KEY_SUBJECT_NAME_STRATEGY,
                        String.class, TopicRecordNameStrategy.class.getName()));

        // value.subject.name.strategy
        props.put(AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
                environment.getProperty(KAFKA_PRODUCER_PREFIX + AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY,
                        String.class, TopicRecordNameStrategy.class.getName()));

        return props;
    }

    @Bean("producerFactory")
    public ProducerFactory<EmployeeEventKey, EmployeeEventValue> producerFactory() {

        return new DefaultKafkaProducerFactory<>(getProducerConfig());
    }

    @Bean("kafkaTemplate")
    public KafkaTemplate<EmployeeEventKey, EmployeeEventValue> kafkaTemplate() {

        final KafkaTemplate<EmployeeEventKey, EmployeeEventValue> kafkaTemplate = new KafkaTemplate<>(producerFactory());

        // Setting up Producer Listener for logging the details upon success or error case.
        // Note: onError will be used to handle the fallback scenario

        kafkaTemplate.setProducerListener(new ProducerListener<EmployeeEventKey, EmployeeEventValue>() {

            @Override
            public void onSuccess(ProducerRecord<EmployeeEventKey, EmployeeEventValue> producerRecord, RecordMetadata recordMetadata) {

                LOG.info("The record with key {} on the topic {}, partition {} and offset {} was sent to Kafka",
                        producerRecord.key(), producerRecord.topic(), recordMetadata.partition(), recordMetadata.offset());
            }

            @Override
            public void onError(ProducerRecord<EmployeeEventKey, EmployeeEventValue> producerRecord, Exception exception) {

                LOG.warn("The record with key {} on the topic {} could not sent to Kafka",
                        producerRecord.key(), producerRecord.topic(), exception);
            }
        });

        return kafkaTemplate;
    }
}
