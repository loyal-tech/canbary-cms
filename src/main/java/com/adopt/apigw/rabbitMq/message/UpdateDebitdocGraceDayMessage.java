package com.adopt.apigw.rabbitMq.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDebitdocGraceDayMessage {
    private Integer debitDocId;
    private Integer debitDocGraceDays;
}
