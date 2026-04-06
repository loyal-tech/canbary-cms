package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.modules.InventoryManagement.PopManagement.model.PopManagementDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PopManagementMessage {

	private Long id;

	private String popName;

	private String latitude;

	private String longitude;

	private String status;

	private Boolean isDeleted;

	private Integer mvnoId;
	
	public PopManagementMessage(PopManagementDTO popManagementDTO) {
		this.id = popManagementDTO.getId();
		this.popName = popManagementDTO.getName();
		this.latitude = popManagementDTO.getLatitude();
		this.longitude = popManagementDTO.getLongitude();
		this.status = popManagementDTO.getStatus();
		this.isDeleted = popManagementDTO.getIsDeleted();
		this.mvnoId = popManagementDTO.getMvnoId();
	}

}
