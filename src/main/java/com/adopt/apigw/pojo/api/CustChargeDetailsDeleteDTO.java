package com.adopt.apigw.pojo.api;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class CustChargeDetailsDeleteDTO {

	private Integer id;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd-HH-mm-ss")
	private LocalDateTime endDate;
	
	private boolean isSoftDelete;
}
