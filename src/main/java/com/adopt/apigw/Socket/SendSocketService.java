package com.adopt.apigw.Socket;

import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.SendSocketMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SendSocketService {

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    public void SendMessageToCommonForSocket(Object object , String url){
        SendSocketMessage sendSocketMessage = new SendSocketMessage();
        sendSocketMessage.setUrl(url);
        sendSocketMessage.setObject(object);
        //messageSender.send(sendSocketMessage , RabbitMqConstants.QUEUE_SEND_SOCKET_MESSAGE_TO_COMMON);
        kafkaMessageSender.send(new KafkaMessageData(sendSocketMessage,sendSocketMessage.getClass().getSimpleName()));
    }

}
