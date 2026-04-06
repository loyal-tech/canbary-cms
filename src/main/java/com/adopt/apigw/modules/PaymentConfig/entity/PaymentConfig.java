package com.adopt.apigw.modules.PaymentConfig.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.*;

import com.adopt.apigw.modules.PaymentConfigMapping.entity.PaymentConfigMapping;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblmpaymentconfig")
public class PaymentConfig {

	@Id
	@ApiModelProperty(notes = "The database generated Payment config Id")
    @Column (name="id", nullable = false)
	private Long paymentConfigId;
	
	@ApiModelProperty(notes = "This is Payment",required = true)
    @Column (name="payment_config_name",nullable = false)
    private String paymentConfigName;

	@ApiModelProperty(notes = "This is make payment gateway profile active inadtive",required = true)
	@Column(name="is_active",nullable = false)
	private Boolean isActive;

	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(targetEntity = PaymentConfigMapping.class, cascade = CascadeType.ALL)
	@JoinColumn(name = "payment_config_id")
	private List<PaymentConfigMapping> paymentConfigMappingList;

	@ApiModelProperty(notes = "This is mvno id", required = true)
    @Column (name="mvnoid", nullable = false)
    private Long mvnoId;

	@ApiModelProperty(notes = "This is soft delete flag", required = true)
	@Column (name="is_delete", nullable = false)
	private Boolean isDelete;

	@Column(name = "create_date")
	private LocalDateTime createDate;

}	
