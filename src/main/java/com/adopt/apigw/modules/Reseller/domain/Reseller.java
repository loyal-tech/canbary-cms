package com.adopt.apigw.modules.Reseller.domain;

import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.Reseller.module.ResellerDto;
import com.adopt.apigw.modules.Voucher.module.Auditable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TBLTRESELLER")
@ApiModel(value = "Reseller Entity",description = "This is reseller entity which is used to update reseller data")
public class Reseller extends Auditable<Long>
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@ApiModelProperty(notes = "The database generated Reseller Id")
    @Column (name="resellerid", nullable = false)
	private Long resellerId;
	
	@ApiModelProperty(notes = "This is distributer id of distributer entity")
	@Column (name="distributerid")
	private Long distributerId;
	
	@ApiModelProperty(notes = "Name of the reseller",required=true)
	@Column (name="resellername", nullable = false , length = 250)
	private String resellerName;
	
	@ApiModelProperty(notes = "Address of the reseller")
	@Column (name="address")
	private String address;
	
	@ApiModelProperty(notes = "Phone number of the reseller",required=true)
	@Column (name="phone", nullable = false , length = 15)
	private String phone;
	
	@ApiModelProperty(notes = "Email Id of the reseller",required=true)
	@Column (name="email", nullable = false , length = 250)
	private String email;
	
	@ApiModelProperty(notes = "GST number of the reseller")
	@Column (name="gstno")
	private String gstNo;
	
	@ApiModelProperty(notes = "Reseller type should be 'Prepaid' or 'Commission'")
	@Column (name="resellertype")
	private String resellerType;
	
	@ApiModelProperty(notes = "Credit limit of the reseller",required=true)
	@Column (name="creditlimit", nullable = false , length = 15)
	private Long creditLimit;
	
	@ApiModelProperty(notes = "Balance of the reseller")
	@Column (name="balance")
	private Long balance;
	
	@ApiModelProperty(notes = "Commission type should be 'Percentage' or 'Flat'")
	@Column (name="commissiontype")
	private String commissionType;
	
	@ApiModelProperty(notes = "This is commission value")
	@Column (name="commissionvalue")
	private String commissionValue;
	
	@ApiModelProperty(notes = "This is mvno id")
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	/*@ApiModelProperty(notes = "This is locationId")
	@Column (name="location_id", nullable = false)
	private Long locationId;*/

	@ManyToOne(optional = false)
	@JoinColumn(name = "location_id")
	private LocationMaster locationMaster;

	@ApiModelProperty(notes = "This is username")
	@Column (name="username", nullable = false)
	private String username;

	@ApiModelProperty(notes = "This is password")
	@Column (name="password", nullable = false)
	private String password;

	@ApiModelProperty(notes = "This is status")
	@Column (name="status", nullable = false)
	private String status;

	@ApiModelProperty(notes = "This is country code", required = false)
	@Column(name = "countrycode", nullable = true, length = 10)
	private String countryCode;

	public Reseller(ResellerDto resellerDto, LocationMaster locationMaster)
	{
		this.creditLimit = resellerDto.getCreditLimit();
		this.email = resellerDto.getEmail();
		this.resellerName= resellerDto.getResellerName();
		this.phone = resellerDto.getPhone();
		if(resellerDto.getBalance() != null)
			this.balance = resellerDto.getBalance();
		this.username = resellerDto.getUsername();
		this.locationMaster = locationMaster;
		this.status=resellerDto.getStatus();
		this.mvnoId=resellerDto.getMvnoId();
	}
}
