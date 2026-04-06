package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustMacMapppingPojoMessage {

	private Integer id;

	private String macAddress;

	private Boolean isDeleted = false;

	private Long leadMasterId;
}
