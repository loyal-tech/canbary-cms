package com.adopt.apigw.pojo.api;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class ChequeDetailsPojo
{
    private String amount;

    private String branch;


    private String chequedate;

    private String chequeNo;


}
