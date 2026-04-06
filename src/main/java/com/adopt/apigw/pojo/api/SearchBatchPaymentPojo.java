package com.adopt.apigw.pojo.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class SearchBatchPaymentPojo {

	List<String> status;
	Integer staff;
	Integer serviceArea;
	Integer branch;

	Integer partner;
	Integer destinationBank;
	String type;
	Boolean isInvoiceVoid;
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate fromDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate toDate;



}
