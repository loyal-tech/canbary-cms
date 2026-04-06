package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CafClosedMessage {

    List<Integer> custIds;
}
