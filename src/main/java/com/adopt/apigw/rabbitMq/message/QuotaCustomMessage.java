package com.adopt.apigw.rabbitMq.message;

import java.util.*;


import com.adopt.apigw.schedulers.UpdateCustomerQuotaDto;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id", scope = CustomMessage.class)
public class QuotaCustomMessage
{
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private String traceId;
	private String spanId;
	private String currentUser;
	private Map<String,Object> customerData;
	private Map<String,Object> data;
	private List<Long> locationIdList;
	private String quotaResetInterval;
	private String operation;

	public QuotaCustomMessage(UpdateCustomerQuotaDto updateCustomerDto, String spanId, String traceId) {
		Map<String, Object> map = new HashMap<>();
		map.put("custId" , updateCustomerDto.getCustId());
		map.put("mvnoId" , updateCustomerDto.getMvnoId());
		map.put("quotaDetailId" , updateCustomerDto.getQuotaDetailId());
		map.put("userName", updateCustomerDto.getUserName());
		map.put("usedQuota", updateCustomerDto.getUsedQuota());
		map.put("usedQuotaKB", updateCustomerDto.getUsedQuotaKB());
		map.put("usedTimeQuota", updateCustomerDto.getUsedTimeQuota());
		map.put("usedTimeQuotaSec", updateCustomerDto.getUsedTimeQuotaSec());
		map.put("skipQuotaUpdate", updateCustomerDto.getSkipQuotaUpdate());
		this.messageDate = new Date();
		this.messageId = UUID.randomUUID().toString();
		this.message = "Customer Password updated from BSS";
		this.customerData = map;
		this.sourceName = "Adopt Api Gateway";
		this.traceId = traceId;
		this.spanId = spanId;
	}
}
