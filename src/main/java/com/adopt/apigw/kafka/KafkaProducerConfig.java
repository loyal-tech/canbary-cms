package com.adopt.apigw.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaProducerConfig {

    @Value(value = "${kafka-url}")
    private String kafkaUrl;

    @Value(value = "${kafka.max.request.size}")
    private String maxRequestSize;
    //KAFKA-PRODUCER


//    @Bean
//    public KafkaProducer<String , Object> kafkaProducer() {
//        Map<String, Object> config = new HashMap<>();
//        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
//        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 100000); //max batch size to be send through kafka
//        config.put(ProducerConfig.RETRIES_CONFIG,1);   // maximum retries if the connection failure occur
//        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG,1000);  // the amount of time to wait to attempt to retry the fail partition
//        config.put(ProducerConfig.ACKS_CONFIG,"all"); //The number of acknowledgments the producer requires the leader to have received before considering a request complete
//        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
//        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
//        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 3000);
//        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 2000);
//        config.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 4000);
//        JsonSerializer<Object> serializer = new JsonSerializer<>();
//        //serializer.setUseTypeMapperForKey(true);
//        return new KafkaProducer<>(config, new StringSerializer(), serializer);
//    }

    @Bean
    public KafkaProducer<String, KafkaMessageData> kafkaProducer() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384); // Default Kafka batch size
        config.put(ProducerConfig.RETRIES_CONFIG, 3); // Increased retries
        config.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1000);
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.LINGER_MS_CONFIG, 10);
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        config.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, 120000); // 2 minutes
        config.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30000);
        config.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 4000);
        config.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, maxRequestSize);

        return new KafkaProducer<>(config, new StringSerializer(), new JsonSerializer<>());
    }
}
