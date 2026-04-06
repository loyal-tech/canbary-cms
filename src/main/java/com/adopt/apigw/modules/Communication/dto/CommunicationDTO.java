package com.adopt.apigw.modules.Communication.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;

@Data
public class CommunicationDTO extends Auditable<Integer> implements IBaseDto {
    private Long id;
    private String email;
    private String subject;
    private String emailBody;
    private LocalDateTime scheduledTime;
    private String uuid;
    private String destination;
    private String source;
    private String smsMessage;
    private String templateId;
    private String channel;
    private Integer mvnoId;
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return mvnoId;
	}
}
