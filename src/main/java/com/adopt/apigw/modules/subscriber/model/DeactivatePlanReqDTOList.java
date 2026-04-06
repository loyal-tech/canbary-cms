package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeactivatePlanReqDTOList {
    List<DeactivatePlanReqDTO> deactivatePlanReqDTOS;
    List<DeactivatePlanReqModel> deactivatePlanReqModels;
    private RecordPaymentPojo recordPayment;
    Long custId;
    boolean serviceStopBulkFlag;
    private DateOverrideDto dateOverrideDtos;
    public Boolean skipQuotaUpdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public LocalDate serviceResumeDate;
    public Integer holdDays;
    public Boolean generateCreditDoc = true;
    public LocalDate getServiceResumeDate() {
        return serviceResumeDate;
    }

    public void setServiceResumeDate(LocalDate serviceResumeDate) {
        this.serviceResumeDate = serviceResumeDate;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }
}
