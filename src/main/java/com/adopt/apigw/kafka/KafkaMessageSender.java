package com.adopt.apigw.kafka;

import com.adopt.apigw.utils.ApplicationContextProvider;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KafkaMessageSender {

    KafkaProducer kafkaProducer;

    @Autowired
    private KafkaProducerConfig kafkaProducerConfig;
    private static Logger log = LoggerFactory.getLogger("adoptcustomerqueue");


    public String send(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(KafkaConstant.KAFKA_CMS_TOPIC, KafkaConstant.KAFKA_CMS_TOPIC + 1, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }


    public String sendCustomerData(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA, KafkaConstant.SEND_CUSTOMER_CREATE_AND_UPDATE_DATA + 1, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }


    public String sendCustomerChangePlanData(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);
            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(KafkaConstant.KAFKA_CMS_CHANGE_PLAN_TOPIC, KafkaConstant.KAFKA_CMS_CHANGE_PLAN_TOPIC + 1, message);

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }

    public String sendRevenueInvoiceRequest(KafkaMessageData message) {
        try {
            kafkaProducer = ApplicationContextProvider.getApplicationContext().getBean("kafkaProducer", KafkaProducer.class);

            ProducerRecord<String, KafkaMessageData> record = new ProducerRecord<>(
                    KafkaConstant.KAFKA_REVENUE_INVOICE_TOPIC,
                    KafkaConstant.KAFKA_REVENUE_INVOICE_TOPIC + 1,
                    message
            );

            kafkaProducer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        exception.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            log.error("error:{}");
        }
        log.debug("Send msg  " + message);
        return "Message Published";
    }



}
