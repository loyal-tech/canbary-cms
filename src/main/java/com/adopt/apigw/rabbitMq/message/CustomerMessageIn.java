package com.adopt.apigw.rabbitMq.message;

import com.adopt.apigw.pojo.api.CustomersPojo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class CustomerMessageIn {
    private Integer id;
    private String username;
    private String firstname;
    private String lastname;
    private String email;
    private String mobile;
    private Integer mvnoId;
    private Long buId;
    private Long servicearea;
    private Long branch;
    private String status;
    private String countryCode;
    private String blockNo;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastmodifiedDate;

    private String createbyname;
    private String updatebyname;
    private Integer createdByStaffId;
    private Integer lastModifiedByStaffId;
    private String accountNumber;
    private String customerType;
    private Boolean isorgcust = false;
    private String pan;
    private Integer parentcustid;

    private String olt;
    private String pop;

    public CustomerMessageIn() {
    }

    public CustomerMessageIn(CustomersPojo customer) {
        this.id = customer.getId();
        this.username = customer.getUsername();
        this.firstname = customer.getFirstname();
        this.lastname = customer.getLastname();
        this.email = customer.getEmail();
        this.mobile = customer.getMobile();
        this.mvnoId = customer.getMvnoId();
        this.buId = customer.getBuId();
        this.servicearea = customer.getServiceareaid();
        this.branch = customer.getBranch();
        this.status = customer.getStatus();
        this.countryCode = customer.getCountryCode();

        this.createdByStaffId = customer.getCreatedById();
        this.lastModifiedByStaffId = customer.getLastModifiedById();

        this.createbyname = customer.getCreatedByName();
        this.updatebyname = customer.getLastModifiedByName();

        this.createdDate = customer.getCreatedate();
        this.lastmodifiedDate = customer.getLastStatusChangeDate();

        this.accountNumber = customer.getAcctno();
        this.customerType = customer.getCustomerType();
        this.isorgcust = customer.getIsorgcust();

        this.pan = customer.getPan();

        if (customer.getParentCustomers() != null) {
            this.parentcustid = customer.getParentCustomers().getId();
        } else {
            this.parentcustid = null;
        }
        this.olt = customer.getOltName();
        this.pop = customer.getPopName();
        this.blockNo = customer.getBlockNo();

    }
}
