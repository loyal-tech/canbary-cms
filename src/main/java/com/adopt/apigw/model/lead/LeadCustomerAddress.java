package com.adopt.apigw.model.lead;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffInclude;

@Entity
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TBLMLEADSUBSCRIBERADDRESSREL")
public class LeadCustomerAddress {

	@Id
	@Column(name = "ADDRESSID", nullable = false, length = 40)
	private Integer id;

	@Column(name = "address_type")
	private String addressType;

	private String address1;

	private String address2;

	private String landmark;

	@Column(name = "area_id")
	private Integer areaId;

	@Column(name = "pincode_id")
	private Integer pincodeId;

	@Column(name = "city_id")
	private Integer cityId;

	@Column(name = "state_id")
	private Integer stateId;

	@Column(name = "country_id")
	private Integer countryId;

	@Column(name = "customer_id")
	private Integer customerId;

	@Column(name = "full_address")
	private String fullAddress;

	@Column(name = "is_delete")
	private Boolean isDelete;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_master_id")
	private LeadMaster leadMaster;

	@Column(name = "street_name")
	private String streetName;

	@Column(name = "house_no")
	private String houseNo;

	public LeadCustomerAddress(LeadCustomerAddressPojo leadCustomerAddressPojo) {
		this.id = leadCustomerAddressPojo.getId();
		this.addressType = leadCustomerAddressPojo.getAddressType();
		this.address1 = leadCustomerAddressPojo.getAddress1();
		this.address2 = leadCustomerAddressPojo.getAddress2();
		this.landmark = leadCustomerAddressPojo.getLandmark();
		this.areaId = leadCustomerAddressPojo.getAreaId();
		this.pincodeId = leadCustomerAddressPojo.getPincodeId();
		this.cityId = leadCustomerAddressPojo.getCityId();
		this.stateId = leadCustomerAddressPojo.getStateId();
		this.countryId = leadCustomerAddressPojo.getCountryId();
		this.customerId = leadCustomerAddressPojo.getCustomerId();
		this.fullAddress = leadCustomerAddressPojo.getFullAddress();
		this.isDelete = leadCustomerAddressPojo.getIsDelete();
		if (leadCustomerAddressPojo.getLeadMasterId() != null)
			this.leadMaster = new LeadMaster(leadCustomerAddressPojo.getLeadMasterId());
		this.streetName = leadCustomerAddressPojo.getStreetName();
		this.houseNo = leadCustomerAddressPojo.getHouseNo();
	}
}
