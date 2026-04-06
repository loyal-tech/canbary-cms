package com.adopt.apigw.kafka;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value(value = "${kafka-url}")
    private String kafkaUrl;

    @Value(value = "${max.poll.records}")
    private String maxPollRecords;

    @Value(value = "${fetch.max.bytes}")
    private String fetchMaxBytes;

    @Value(value = "${fetch.min.bytes}")
    private String fetchMinBytes;

    @Value(value = "${max.poll.interval}")
    private String maxPollInterval;


    @Bean
    public KafkaAdmin adminClient() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,kafkaUrl);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ConsumerFactory<String, KafkaMessageData> kafkaConsumerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(false);

        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        configProps.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer.getClass());
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
        configProps.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG,maxPollRecords);
        configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,fetchMaxBytes);
        configProps.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG,fetchMinBytes);

        configProps.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 30000);
        configProps.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 6000);
        //configProps.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "5242880"); // Increase fetch size for large messages
        configProps.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, 2621440); // Increase partition fetch size
        configProps.put(ConsumerConfig.FETCH_MAX_WAIT_MS_CONFIG, 500); // Increase partition fetch size

//        configProps.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000"); // Default is 30 seconds
//        configProps.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, "5");
        // Set a short polling interval to ensure the consumer polls every second
//        configProps.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollInterval);
        return new DefaultKafkaConsumerFactory<>(configProps, new StringDeserializer(), deserializer);
    }
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, KafkaMessageData> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.setErrorHandler((thrownException, data) -> {
            System.err.println("Error while processing: " + data + ", exception: " + thrownException);
        });
//        factory.setBatchListener(true);
        return factory;
    }

//    public Map<String, Object> packetDataPropsPrimary() {
//        Map<String, Object> config = new HashMap<>();
//
//        // Create and configure the deserializer explicitly
//        JsonDeserializer<KafkaMessageData> deserializer = new JsonDeserializer<>(KafkaMessageData.class, false);
//        deserializer.setRemoveTypeHeaders(false);
//        deserializer.addTrustedPackages("*");
//        deserializer.setUseTypeMapperForKey(false);
//
//        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
//        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass());
//        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
//        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, deserializer.getClass());
//        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.COMBINED_GROUP);
//        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, true);
//        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
//        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
//
//        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), deserializer).getConfigurationProperties();
//    }


    public Map<String, Object> packetDataPropsPrimary() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
//        config.put("spring.deserializer.value.delegate.class", JsonDeserializer.class.getName());
//        config.put("spring.json.trusted.packages", "*");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaConstant.COMBINED_GROUP);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // Reliability and performance-related configs
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // safer manual commit
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        config.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, fetchMaxBytes);
        config.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, fetchMinBytes);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, "10000");
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "600000");
        return config;
    }
}
