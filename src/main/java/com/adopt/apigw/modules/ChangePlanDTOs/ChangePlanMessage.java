package com.adopt.apigw.modules.ChangePlanDTOs;

import com.adopt.apigw.model.postpaid.CustChargeInstallment;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.FlagDTO;
import com.adopt.apigw.pojo.api.CreditDocumentPaymentPojo;
import com.adopt.apigw.pojo.api.RecordPaymentPojo;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ChangePlanMessage {

    String type;

    Integer renewalId;

    List<CustPlanMappingRevenue> newCustPlanMappingRevenues;

    List<CustChargeDetailsRevenue> custChargeDetailsRevenues;

    List<CustomerChargeHistoryRevenue> customerChargeHistoryRevenues;

    List<CustomerServiceMappingRevenue> customerServiceMappingRevenues;

    List<CustPlanMappingRevenue> oldCustPlanMappingRevenues;

    List<Integer> custChargeIds;
    private Integer createdById;

    private Integer parentId;

    private List<Integer> childIds;

    private List<Long> buId;

    private Integer mvnoId;

    private Integer lcoId;

    private String paySource;

    private Boolean isLco;

    private Integer getCreatedById;

    private String getCreatedByName;

    private RecordPaymentPojo recordPaymentDTO;

    private AdditionalInformationDTO additionalInformationDTO;

    List<Integer> overrideChargeIds;

    private  boolean changePlanNextBillDate;

    private Boolean isMvnoCustomer;

    private List<Integer> debitDocDetailIds;

    private String ispFromDate;

    private String ispToDate;

    private Boolean isAutoPaymentRequired=false;
    private List<CreditDocumentPaymentPojo> creditDocumentPaymentPojoList;

    private Integer payingChildId;

    private List<CustChargeInstallment> custChargeInstallments;

    private FlagDTO flagDTO;
}
