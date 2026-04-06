package com.adopt.apigw.modules.MtnPayment.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "debitrequest", namespace = "http://www.ericsson.com/em/emm/financial/v1_1")
@XmlAccessorType(XmlAccessType.FIELD)
public class DebitRequest {

    @XmlElement(name = "fromfri")
    private String fromFri;

    @XmlElement(name = "tofri")
    private String toFri;

    @XmlElement(name = "amount")
    private Amount amount;

    @XmlElement(name = "externaltransactionid")
    private String externalTransactionId;

    @XmlElement(name = "frommessage")
    private String fromMessage;

    @XmlElement(name = "tomessage")
    private String toMessage;

	private Long orderId;

	private Integer planId;

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Integer getPlanId() {
		return planId;
	}

	public void setPlanId(Integer planId) {
		this.planId = planId;
	}

	public String getFromFri() {
		return fromFri;
	}

	public void setFromFri(String fromFri) {
		this.fromFri = fromFri;
	}

	public String getToFri() {
		return toFri;
	}

	public void setToFri(String toFri) {
		this.toFri = toFri;
	}

	public Amount getAmount() {
		return amount;
	}

	public void setAmount(Amount amount) {
		this.amount = amount;
	}

	public String getExternalTransactionId() {
		return externalTransactionId;
	}

	public void setExternalTransactionId(String externalTransactionId) {
		this.externalTransactionId = externalTransactionId;
	}

	public String getFromMessage() {
		return fromMessage;
	}

	public void setFromMessage(String fromMessage) {
		this.fromMessage = fromMessage;
	}

	public String getToMessage() {
		return toMessage;
	}

	public void setToMessage(String toMessage) {
		this.toMessage = toMessage;
	}
    
    
}
