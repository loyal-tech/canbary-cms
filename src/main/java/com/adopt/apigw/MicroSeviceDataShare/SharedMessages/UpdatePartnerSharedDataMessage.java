package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedger;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdatePartnerSharedDataMessage {

    private Integer id;
    private String name;
    private String prcode;
    private String status;
    private String commtype;
    private Double commrelvalue;
    private Double balance;
    private Integer commdueday;
    private String nextbilldate;
    private String lastbilldate;
    private Integer taxid;
    private String addresstype;
    private String address1;
    private String address2;
    private Double credit;
    private Integer city;
    private Integer state;
    private Integer country;
    private String pincode;
    private String mobile;
    private String countryCode;
    private String email;
    private String partnerType;
    private String cpName;
    private String cname;
    private String panName;
    private List<ServiceArea> serviceAreaList;
    private Partner parentPartner;
    private Long priceBookId;
    private PartnerLedger partnerLedger;
    private List<PartnerLedgerDetails> partnerLedgerDetails;
    private List<PartnerPayment> partnerPayments;
    private Boolean isDelete;
    private Integer mvnoId;
    private String commissionShareType;
    private Long buId;
    private Long newCustomerCount = 0L;
    private Long renewCustomerCount = 0L;
    private Long totalCustomerCount = 0L;
    private String calendarType;
    private String resetDate;
    private Double creditConsume = 0d;
    private Long region ;
    private Long branch ;
    private Long bussinessvertical ;
    private String dunningActivateFor;
    private String lastDunningDate;
    private Boolean isDunningEnable;
    private String dunningAction;
    private Integer parentPartnerId;
    private Integer createdById;
    private Integer lastModifiedById;

    private List<Long> serviceAreaIds;
    private Boolean isVisibleToIsp;


    public UpdatePartnerSharedDataMessage(Partner partner){
        this.id = partner.getId();
        this.mvnoId = partner.getMvnoId();
        this.buId = partner.getBuId();
        this.email = partner.getEmail();
        this.mobile = partner.getMobile();
        this.partnerType = partner.getPartnerType();
        this.isDelete = partner.getIsDelete();
        this.name = partner.getName();
        this.city = partner.getCity();
        this.country = partner.getCountry();
        this.state = partner.getState();
        this.branch = partner.getBranch();
        this.region = partner.getRegion();
        this.status = partner.getStatus();
        this.createdById = partner.getCreatedById();
        this.lastModifiedById = partner.getLastModifiedById();
        this.isVisibleToIsp=partner.getIsVisibleToIsp();
    }

    public UpdatePartnerSharedDataMessage(){

    }
}
