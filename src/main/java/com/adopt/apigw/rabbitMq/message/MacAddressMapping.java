package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MacAddressMapping {
	private Long macAddressId;
    private Long customerId;
    private String macAddress;
    private Timestamp createDate;
    private Timestamp lastModificationDate;
    private String createdBy;
    private String lastModifiedBy;
	private Integer custsermappingid;
	private Timestamp macRetentionDate;
    private String normalizeMac;

}
