package com.adopt.apigw.kafka;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KafkaMessageData {
    Object data;
    String dataType;
    String eventType;

    public KafkaMessageData(Object data, String dataType) {
        this.data = data;
        this.dataType = dataType;
    }
}
