package com.adopt.apigw.modules.PartnerLedger.service;

import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.PartnerBillingMessage;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class PartnerInvoiceCreationThread implements Runnable{
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private LocalDate invoiceDate;
    MessageSender messageSender;

    public PartnerInvoiceCreationThread(LocalDate invoiceDate,MessageSender messageSender) {
        this.invoiceDate=invoiceDate;
        this.messageSender=messageSender;
    }

    @Override
    public void run() {
        generateInvoice(this.invoiceDate);
    }

    public void generateInvoice(LocalDate invoiceDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            String invoiceDateString = invoiceDate.format(formatter);
            PartnerBillingMessage message = new PartnerBillingMessage(invoiceDateString);
            kafkaMessageSender.send(new KafkaMessageData(message, PartnerBillingMessage.class.getSimpleName()));
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
