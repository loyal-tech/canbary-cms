package com.adopt.apigw.pojo.api;

import javax.persistence.Column;
import java.time.LocalDateTime;

public class BillRunPojo extends ParentPojo{
	
	private Integer id;

    private LocalDateTime createdate;

    private LocalDateTime rundate;

	private Integer billruncount;

	private Double amount;
    
    private String status;

    private LocalDateTime billrunfinishdate;

	private Boolean isDelete;
	
	private String type;

	private Integer mvnoId;

	private Integer lcoId;

	public Integer getLcoId() {
		return lcoId;
	}

	public void setLcoId(Integer lcoId) {
		this.lcoId = lcoId;
	}

	public Integer getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Integer mvnoId) {
		this.mvnoId = mvnoId;
	}

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "BillRunPojo [id=" + id + ", createdate=" + createdate + ", rundate=" + rundate + ", billruncount="
				+ billruncount + ", amount=" + amount + ", status=" + status + ", billrunfinishdate="
				+ billrunfinishdate +", type="+type+"]";
	}
}
