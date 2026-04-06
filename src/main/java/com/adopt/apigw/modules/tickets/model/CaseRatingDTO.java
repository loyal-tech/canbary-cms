package com.adopt.apigw.modules.tickets.model;

public class CaseRatingDTO {
	
	private Long caseId;
	
	private Integer rating;
	
	private String customerFeedback;

	public Long getCaseId() {
		return caseId;
	}

	public void setCaseId(Long caseId) {
		this.caseId = caseId;
	}
	
	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getCustomerFeedback() {
		return customerFeedback;
	}

	public void setCustomerFeedback(String customerFeedback) {
		this.customerFeedback = customerFeedback;
	}
}
