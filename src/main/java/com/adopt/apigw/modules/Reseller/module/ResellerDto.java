package com.adopt.apigw.modules.Reseller.module;


import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "Reseller",description = "This is data transfer object for reseller which is used to create new reseller")
public class ResellerDto 
{
	@ApiModelProperty(notes = "This is distributer id of distributer entity",required=true)
	private Long distributerId;
	@ApiModelProperty(notes = "Name of the reseller",required=true)
	private String resellerName;
	@ApiModelProperty(notes = "Address of the reseller",required=true)
	private String address;
	@ApiModelProperty(notes = "Phone number of the reseller",required=true)
	private String phone;
	@ApiModelProperty(notes = "Email Id of the reseller",required=true)
	private String email;
	@ApiModelProperty(notes = "GST number of the reseller",required=true)
	private String gstNo;
	@ApiModelProperty(notes = "Reseller type should be 'Prepaid' or 'Commission'",required=true)
	private String resellerType;
	@ApiModelProperty(notes = "Credit limit of the reseller",required=true)
	private Long creditLimit;
	@ApiModelProperty(notes = "Balance of the reseller",required=true)
	private Long balance;
	@ApiModelProperty(notes = "Commission type should be 'Percentage' or 'Flat'",required=true)
	private String commissionType;
	@ApiModelProperty(notes = "This is commission value",required=true)
	private String commissionValue;
	@ApiModelProperty(notes = "This is location id of reseller",required=true)
	private LocationMaster locationMaster;
	@ApiModelProperty(notes = "This is username of reseller",required=true)
	private String username;
	@ApiModelProperty(notes = "This is password of reseller",required=true)
	private String password;
	@ApiModelProperty(notes = "This is mvnoID",required = true)
	private Long mvnoId;
	@ApiModelProperty(notes = "This is status")
	private String status;
}
