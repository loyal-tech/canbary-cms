package com.adopt.apigw.pojo.api;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

public class PartnerBillRunPojo extends ParentPojo{
	
	private Integer id;

    @CreationTimestamp
    private LocalDateTime createdate;

    private LocalDateTime rundate;

	private  Integer billruncount;

	private  Double amount;
    
    private String status;

    private LocalDateTime billrunfinishdate;

	private Boolean isDelete;

	public Boolean getDelete() {
		return isDelete;
	}

	public void setDelete(Boolean delete) {
		isDelete = delete;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public LocalDateTime getCreatedate() {
		return createdate;
	}

	public void setCreatedate(LocalDateTime createdate) {
		this.createdate = createdate;
	}

	public LocalDateTime getRundate() {
		return rundate;
	}

	public void setRundate(LocalDateTime rundate) {
		this.rundate = rundate;
	}

	public Integer getBillruncount() {
		return billruncount;
	}

	public void setBillruncount(Integer billruncount) {
		this.billruncount = billruncount;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getBillrunfinishdate() {
		return billrunfinishdate;
	}

	public void setBillrunfinishdate(LocalDateTime billrunfinishdate) {
		this.billrunfinishdate = billrunfinishdate;
	}

	@Override
	public String toString() {
		return "PartnerBillRunPojo [id=" + id + ", createdate=" + createdate + ", rundate=" + rundate
				+ ", billruncount=" + billruncount + ", amount=" + amount + ", status=" + status
				+ ", billrunfinishdate=" + billrunfinishdate + "]";
	}
}
