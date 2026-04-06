package com.adopt.apigw.rabbitMq.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceareaMessage {

	private Long id;

	private String name;

	private String status;

	private Boolean isDeleted;

	private Integer mvnoId;

	private String latitude;

	private String longitude;

	private Long areaId;
}
