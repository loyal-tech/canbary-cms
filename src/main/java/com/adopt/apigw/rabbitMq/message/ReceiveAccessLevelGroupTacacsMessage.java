package com.adopt.apigw.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveAccessLevelGroupTacacsMessage {

    private Long id;

    private String accessLevelGroupName;

    private String accessLevelGroupId;
}
