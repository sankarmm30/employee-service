package com.takeaway.challenge.context;

import com.takeaway.challenge.EmployeeEventKey;
import com.takeaway.challenge.EmployeeEventValue;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.confluent.kafka.serializers.subject.TopicRecordNameStrategy;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SslConfigs;
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

    private static final String BOOTSTRAP_SERVERS_PROP_NAME = "kafka.producer.bootstrap.servers";
    private static final String SECURITY_ENABLE_PROP_NAME = "kafka.producer.security.enable";
    private static final String SPECIFIC_AVRO_READER_CONFIG_VALUE = "true";
    private static final String DEFAULT_SECURITY_PROTOCOL = "SSL";
    private static final String DEFAULT_SSL_ENABLED_PROTOCOLS = "TLSv1.2,TLSv1.1,TLSv1";
    private static final String DEFAULT_SSL_TRUSTSTORE_TYPE = "JKS";
    private static final String DEFAULT_SSL_KEYSTORE_TYPE = "JKS";
    private static final String DEFAULT_SCHEMA_REGISTRY_URL = "http://localhost:8081";

    @Autowired
    private Environment environment;

    public EmployeeEventKafkaProducerContext(Environment environment) {

        this.environment = environment;
    }

    @Bean
    public Map<String, Object> getProducerConfig() {

        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, environment.getRequiredProperty(BOOTSTRAP_SERVERS_PROP_NAME));
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, environment.getProperty(KafkaAvroDeserializerConfig.SCHEMA_REGISTRY_URL_CONFIG, DEFAULT_SCHEMA_REGISTRY_URL));
        props.put(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, environment.getProperty(KafkaAvroDeserializerConfig.AUTO_REGISTER_SCHEMAS, Boolean.class, false));
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, SPECIFIC_AVRO_READER_CONFIG_VALUE);
        props.put(AbstractKafkaAvroSerDeConfig.KEY_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class.getName());
        props.put(AbstractKafkaAvroSerDeConfig.VALUE_SUBJECT_NAME_STRATEGY, TopicRecordNameStrategy.class.getName());

        if(environment.getProperty(SECURITY_ENABLE_PROP_NAME, Boolean.class, false)) {
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, environment.getProperty(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, DEFAULT_SECURITY_PROTOCOL));
            props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, environment.getProperty(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, ""));
            props.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, environment.getProperty(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, DEFAULT_SSL_ENABLED_PROTOCOLS));
            props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, environment.getProperty(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, DEFAULT_SSL_TRUSTSTORE_TYPE));
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, environment.getProperty(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG));
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, environment.getProperty(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG));
            props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, environment.getProperty(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, DEFAULT_SSL_KEYSTORE_TYPE));
            props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, environment.getProperty(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG));
            props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, environment.getProperty(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG));
            props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, environment.getProperty(SslConfigs.SSL_KEY_PASSWORD_CONFIG));
        }
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
