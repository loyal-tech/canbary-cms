package com.adopt.apigw.pojo;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class TriggerTrialBillRun {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate billrundate;

	public LocalDate getBillrundate() {
		return billrundate;
	}

	public void setBillrundate(LocalDate billrundate) {
		this.billrundate = billrundate;
	}
		
}
