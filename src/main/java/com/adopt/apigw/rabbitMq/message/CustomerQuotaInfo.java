package com.adopt.apigw.rabbitMq.message;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerQuotaInfo 
{
	private String userName;
	private Long mvnoId;
	private Double timeBasedTotalQuota;
	private Double timeBasedUsedQuota;
	private Double timeBasedUnusedQuota;
	private Double volumeBasedTotalQuota;
	private Double volumeBasedUsedQuota;
	private Double volumeBasedUnusedQuota;
	private String planName;
	private String planType;
	private String messageId;
	private String message;
	private Date messageDate;
	private Integer custpackageid;
	private String quotaType;
	private Boolean skipQuotaReset;
	private Double timeBasedSessionUsedQuota;
	private Double volumeBasedSessionUsedQuota;
	private Integer custId;
	private Integer planId;
	private String quotaUnit;
	
	public CustomerQuotaInfo()
	{
		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		this.message = "Customer's used data updates";
	}
}
