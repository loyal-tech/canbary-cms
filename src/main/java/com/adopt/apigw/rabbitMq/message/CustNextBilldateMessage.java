package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustNextBilldateMessage {

	private HashMap<Integer, LocalDate> custNextBillDateMap;
	private HashMap<Long, LocalDateTime> cprEndDateMap;
	private HashMap<Integer, LocalDate> custNextQuotaDateMap;

}
