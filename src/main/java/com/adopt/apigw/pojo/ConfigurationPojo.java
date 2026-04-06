package com.adopt.apigw.pojo;

import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.model.common.ClientService;

public class ConfigurationPojo {

	private Integer id;
	
	private List<ClientService> clientServiceList = new  ArrayList<ClientService>();

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public List<ClientService> getClientServiceList() {
		return clientServiceList;
	}

	public void setClientServiceList(List<ClientService> clientServiceList) {
		this.clientServiceList = clientServiceList;
	}
}
