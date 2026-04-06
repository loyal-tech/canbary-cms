package com.adopt.apigw.pojo.api;

public class DunningRuleActionPojo {

	private Integer id;

    private Integer days;


    private String action;

    private Integer dunningRuleId;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDays() {
		return days;
	}

	public void setDays(Integer days) {
		this.days = days;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getDunningRuleId() {
		return dunningRuleId;
	}

	public void setDunningRuleId(Integer dunningRuleId) {
		this.dunningRuleId = dunningRuleId;
	}
}
