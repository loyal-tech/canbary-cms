package com.adopt.apigw.rabbitMq.message;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CreditDocMessageList {

    List<CreditDocMessage> creditDocMessageList = new ArrayList<>();
}
