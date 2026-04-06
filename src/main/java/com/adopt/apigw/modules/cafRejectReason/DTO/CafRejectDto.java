package com.adopt.apigw.modules.cafRejectReason.DTO;

public class CafRejectDto {

    private Integer cafId;

    private Long rejectReasonId;

    private Long rejectSubReasonId;

    private String remark;

    public Integer getCafId() {
        return cafId;
    }

    public void setCafId(Integer cafId) {
        this.cafId = cafId;
    }

    public Long getRejectReasonId() {
        return rejectReasonId;
    }

    public void setRejectReasonId(Long rejectReasonId) {
        this.rejectReasonId = rejectReasonId;
    }

    public Long getRejectSubReasonId() {
        return rejectSubReasonId;
    }

    public void setRejectSubReasonId(Long rejectSubReasonId) {
        this.rejectSubReasonId = rejectSubReasonId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}