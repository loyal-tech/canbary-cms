package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MvnoStatusMessage {

    List<Integer> objectList = new ArrayList<>();
    String status;
    Boolean mvnoDeactivationFlag;


}
