package com.adopt.apigw.model.lead;

import com.adopt.apigw.rabbitMq.message.LeadCustomerAddressPojoMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadCustomerAddressPojo {

	private Integer id;

	private String addressType;

	private String address1;

	private String address2;

	private String landmark;

	private Integer areaId;

	private Integer pincodeId;

	private Integer cityId;

	private Integer stateId;

	private Integer countryId;

	private Integer customerId;

	private String fullAddress;

	private Boolean isDelete;

	private Long leadMasterId;
	
	private String streetName;

	private String houseNo;
	
	public LeadCustomerAddressPojo(LeadCustomerAddress leadCustomerAddress) {
		this.id = leadCustomerAddress.getId();
		this.addressType = leadCustomerAddress.getAddressType();
		this.address1 = leadCustomerAddress.getAddress1();
		this.address2 = leadCustomerAddress.getAddress2();
		this.landmark = leadCustomerAddress.getLandmark();
		this.areaId = leadCustomerAddress.getAreaId();
		this.pincodeId = leadCustomerAddress.getPincodeId();
		this.cityId = leadCustomerAddress.getCityId();
		this.stateId = leadCustomerAddress.getStateId();
		this.countryId = leadCustomerAddress.getCountryId();
		this.customerId = leadCustomerAddress.getCustomerId();
		this.fullAddress = leadCustomerAddress.getFullAddress();
		this.isDelete = leadCustomerAddress.getIsDelete();
		if(leadCustomerAddress.getLeadMaster() != null)
			this.leadMasterId = leadCustomerAddress.getLeadMaster().getId();
		this.streetName = leadCustomerAddress.getStreetName();
		this.houseNo = leadCustomerAddress.getHouseNo();
	}
	
	public LeadCustomerAddressPojo(LeadCustomerAddressPojoMessage leadCustomerAddress) {
		this.id = leadCustomerAddress.getId();
		this.addressType = leadCustomerAddress.getAddressType();
		this.address1 = leadCustomerAddress.getAddress1();
		this.address2 = leadCustomerAddress.getAddress2();
		this.landmark = leadCustomerAddress.getLandmark();
		this.areaId = leadCustomerAddress.getAreaId();
		this.pincodeId = leadCustomerAddress.getPincodeId();
		this.cityId = leadCustomerAddress.getCityId();
		this.stateId = leadCustomerAddress.getStateId();
		this.countryId = leadCustomerAddress.getCountryId();
		this.customerId = leadCustomerAddress.getCustomerId();
		this.fullAddress = leadCustomerAddress.getFullAddress();
		this.isDelete = leadCustomerAddress.getIsDelete();
		this.leadMasterId = leadCustomerAddress.getLeadMasterId();
		this.streetName = leadCustomerAddress.getStreetName();
		this.houseNo = leadCustomerAddress.getHouseNo();
	}
}
