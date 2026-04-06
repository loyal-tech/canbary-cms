package com.adopt.apigw.pojo.NewCustPojos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCreditDocPojo {
    private Integer id;
    private String paymode;
    private LocalDate paymentdate;
    private Double amount;
    private String referenceno;
}
