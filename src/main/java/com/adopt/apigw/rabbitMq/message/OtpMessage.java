package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.model.common.OTP;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.MDC;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OtpMessage
{
	private String messageId;
	private String message;
	private Date messageDate;
	private String sourceName;
	private String traceId;
	private String spanId;
	private String currentUser;
	private String emailTemplate;
	private String smsTemplate;
	private String appendUrl;
	private Map<String,Object> otpData;
	private boolean isSmsConfigured;
	private boolean isEmailConfigured;
	private String timeframe;
	private String datetime;
	private Integer staffId;
	
    public OtpMessage(OTP otp, String message, String sourceName,TemplateNotification template, String emailId,Integer mvnoId,String timeframe, String datetime , Long buId,String userName,Integer custId)
	{
		Map<String,Object> map = new HashMap<>();
		map.put(RabbitMqConstants.OTP,otp.getOtp());
		map.put(RabbitMqConstants.MOBILE_NUMBER, otp.getMobile_email());
		map.put(RabbitMqConstants.COUNTRY_CODE, otp.getCountryCode());
 		map.put(RabbitMqConstants.EMAIL_ID, emailId);
		map.put(RabbitMqConstants.MVNO_ID,mvnoId);
		if(Objects.nonNull(buId)) {
			map.put(RabbitMqConstants.BU_ID, buId);
		}
		if(Objects.isNull(buId)){
			map.put(RabbitMqConstants.BU_ID, null);
		}
		this.setMessageDate(new Date());
		this.setMessageId(UUID.randomUUID().toString());
		this.setMessage(message);
		this.setEmailTemplate(template.getEmailTemplateData());
		this.setSmsTemplate(template.getSmsTemplateData());
		this.setAppendUrl(template.getAppendUrl());
		this.setOtpData(map);
		this.sourceName = sourceName;
		this.currentUser = (String) MDC.get(RabbitMqConstants.USER_NAME);
		map.put("username",userName);
		map.put("staffId",custId);
        this.isEmailConfigured = template.isEmailEventConfigured();
        this.isSmsConfigured = template.isSmsEventConfigured();
		this.staffId=custId;
		this.timeframe = timeframe;
		this.datetime = datetime;
	}
}
