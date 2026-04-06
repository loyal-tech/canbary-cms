package com.adopt.apigw.pojo.api;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

public class InvoiceServerPojo extends ParentPojo{

	private Integer id;

	@NotNull
    private String serverip;
    
	@NotNull
    private String webport;

	private String servertype;
	@NotNull
    private String status;

    @CreationTimestamp
    private LocalDateTime createdate;

	public String getServertype() {
		return servertype;
	}

	public void setServertype(String servertype) {
		this.servertype = servertype;
	}

	@UpdateTimestamp
    private LocalDateTime updatedate;

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

	public String getServerip() {
		return serverip;
	}

	public void setServerip(String serverip) {
		this.serverip = serverip;
	}

	public String getWebport() {
		return webport;
	}

	public void setWebport(String webport) {
		this.webport = webport;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDateTime getCreatedate() {
		return createdate;
	}

	public void setCreatedate(LocalDateTime createdate) {
		this.createdate = createdate;
	}

	public LocalDateTime getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(LocalDateTime updatedate) {
		this.updatedate = updatedate;
	}

	@Override
	public String toString() {
		return "InvoiceServerPojo [id=" + id + ", serverip=" + serverip + ", webport=" + webport + ", status=" + status
				+ ", createdate=" + createdate + ", updatedate=" + updatedate + "]";
	}
}
