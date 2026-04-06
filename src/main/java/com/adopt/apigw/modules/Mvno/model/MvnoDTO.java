package com.adopt.apigw.modules.Mvno.model;
import com.adopt.apigw.core.dto.IBaseDto;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Lob;

@Data
public class MvnoDTO extends Auditable implements IBaseDto {
 
	private Long id;
	
	private String name;

	private String username;

	private String address;

	private String fullName;

	private String password;

	private String suffix;
	
	private String description;
	
	private String email;
	
	private String phone;
	
	private String status;
	
	private String logfile;
	
	private String mvnoHeader;
	
	private String mvnoFooter;
	
    private Boolean isDelete = false;
	private byte[] profileImage;

	private String logo_file_name;
	private Integer mvnoPaymentDueDays;

	private Long profileId;

	private CustAccountProfile custAccountProfile;

	
    @JsonIgnore
    @Override
    public Long getIdentityKey() {
        return id;
    }

	@Override
	public Integer getMvnoId() {
		return null;
	}

	@Override
	public void setMvnoId(Integer mvnoId) {
	}

    public void getgracePeriodDays() {
    }
}
