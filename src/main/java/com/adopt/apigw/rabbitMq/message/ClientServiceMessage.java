package com.adopt.apigw.rabbitMq.message;

import lombok.Data;

@Data
public class ClientServiceMessage {

    private Integer id;

    private String name;

    private String value;

	private Integer mvnoId;

	public ClientServiceMessage(Integer id, String name, String value,Integer mvnoId) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.mvnoId = mvnoId;
	}
	public ClientServiceMessage() {}
    
}
