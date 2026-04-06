package com.adopt.apigw.modules.PaymentConfigMapping.entity;

import java.sql.Timestamp;

import javax.persistence.*;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblmpaymentconfigmapping")
public class PaymentConfigMapping {

	@Id
	@ApiModelProperty(notes = "The database generated Payment config Id")
    @Column (name="id", nullable = false)
	private Long paymentConfigMappingId;

	@ApiModelProperty(notes = "This is payment config id")
	@Column(name = "payment_config_id", nullable = false)
	private Long paymentConfigId;
	
	@ApiModelProperty(notes = "This is Payment parameter name")
    @Column (name="payment_parameter_name")
    private String paymentParameterName;

	@ApiModelProperty(notes = "This is Payment parameter value")
	@Column (name="payment_parameter_value")
	private String paymentParameterValue;

	@ApiModelProperty(notes = "This is a describe parameter name")
	@Column (name="payment_parameter_description")
	private String paymentParameterDescription;

	@Transient
	private String parameterDisplayName;



}	
