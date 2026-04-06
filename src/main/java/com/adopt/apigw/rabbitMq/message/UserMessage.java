package com.adopt.apigw.rabbitMq.message;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import com.adopt.apigw.pojo.api.StaffUserPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessage {

	private Integer id;

	private String username;

	private String password;

	private String firstname;

	private String lastname;

	private String email;

	private String phone;

	private Integer failcount = 0;

	private String status;

	private String last_login_time;
	
	private String countryCode;

	private String createdate;

	private String updatedate;

	private Integer partnerid;

	private Set<RoleMessage> roles = new HashSet<>();
	
	private Set<BusinessUnitMessage> businessUnitMessageList = new HashSet<>();

	private Set<TeamsMessage> teamMessageList = new HashSet<>();

	private String otp;

	private String otpvalidate;

	private Boolean isDelete;

	private Boolean sysstaff;

	private Long serviceareaId;

	private Long businessunitid;

	private Integer staffUserparentId;

	private Integer mvnoId;

	private Integer branchId;

	private String accessLevelGroupName;


	public UserMessage(StaffUserPojo obj) {
		this.id=obj.getId();
		this.username=obj.getUsername();
		this.password=obj.getPassword();
		this.firstname=obj.getFirstname();
		this.lastname=obj.getLastname();
		this.email=obj.getEmail();
		this.phone=obj.getPhone();
		this.failcount=obj.getFailcount();
		this.status=obj.getStatus();
		DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		//this.last_login_time=obj.getLast_login_time().format(formatter).toString();
		if(obj.getLast_login_time() != null) {
			this.last_login_time = obj.getLast_login_time().format(formatter).toString();
		}
		this.countryCode=obj.getCountryCode();
		this.createdate=obj.getCreatedate().format(formatter).toString();
		this.updatedate=obj.getUpdatedate().format(formatter).toString();
		this.partnerid=obj.getPartnerid();
		this.isDelete=obj.getIsDelete();
		this.sysstaff=obj.getSysstaff();
		this.serviceareaId=obj.getServiceAreaId();
		this.businessunitid=obj.getBusinessunitid();
		this.staffUserparentId=obj.getParentStaffId();
		this.branchId=obj.getBranchId();
		this.mvnoId=obj.getMvnoId();
		this.accessLevelGroupName= obj.getTacacsAccessLevelGroup();

	}

	public UserMessage(UserMessage userMessage) {
	}
}
