package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustomerAddressPojoMessage {

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
}
