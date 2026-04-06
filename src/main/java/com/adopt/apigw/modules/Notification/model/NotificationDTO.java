package com.adopt.apigw.modules.Notification.model;

import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;

@Data
public class NotificationDTO extends Auditable implements IBaseDto {
    private Long id;
    private String name;
    private Boolean email_enabled;
    private Boolean sms_enabled;
    private String status;
    private String category;
    private String email_body;
    private String sms_body;
    private String template_id;
    private Boolean isDeleted = false;

    @JsonManagedReference
    @ToString.Exclude
    private NotificationConfigDTO notificationConfig;
    private Integer mvnoId;
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		// TODO Auto-generated method stub
		return mvnoId;
	}
}
