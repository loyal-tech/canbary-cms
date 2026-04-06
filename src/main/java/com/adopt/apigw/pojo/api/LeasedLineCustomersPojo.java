package com.adopt.apigw.pojo.api;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.adopt.apigw.model.common.Auditable;

import lombok.Data;

@Data
public class LeasedLineCustomersPojo extends Auditable{
	
	private Integer id;
	
	@NotNull
    private String name;
	
	@NotNull
    private String email;
	
	@NotNull
    private String businessName;
	
	@NotNull
    private String billingAddress;
	
	@NotNull
    private String technicalPersonName;
	
	@NotNull
    private String technicalPersonContactNo;
	
    private Boolean isDelete = false;
		
    private List<LeasedLineCircuitDetailsPojo> llcDetailsList = new ArrayList<>();
    
    private Integer mvnoId;

    
}
