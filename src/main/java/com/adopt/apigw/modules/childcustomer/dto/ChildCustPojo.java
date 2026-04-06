package com.adopt.apigw.modules.childcustomer.dto;

import com.adopt.apigw.pojo.api.CustomersPojo;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ChildCustPojo {
    private Long id;
    private String firstName;

    public ChildCustPojo() {
    }

    private String lastName;
    private String userName;
    private String password;
    private String email;
    private Long parentCustId;
    private Double wallet;
    private String status;
    private Boolean isDeleted;
    private Boolean isParent;
    private String mobileNumber;
    private String accountNumber;
    private Integer mvnoId;
    private Boolean isParentWalletUsable;

    public ChildCustPojo(CustomersPojo pojo) {
        this.id =pojo.getId().longValue();
        this.firstName = pojo.getFirstname();
        this.lastName = pojo.getLastname();
        this.userName = pojo.getLoginUsername() != null ? pojo.getLoginUsername():pojo.getUsername();
        this.password = pojo.getLoginPassword() != null ? pojo.getLoginPassword():pojo.getPassword();
        this.email = pojo.getEmail();
        this.wallet = pojo.getWalletbalance();
        this.status = pojo.getStatus();
        this.parentCustId= Long.valueOf(pojo.getId());
        this.isParent= true;
        this.isDeleted = pojo.getIsDeleted()!= null ? pojo.getIsDeleted() : false;
        this.mobileNumber = pojo.getMobile();
        this.accountNumber=pojo.getAcctno();
        this.mvnoId=pojo.getMvnoId();
        this.isParentWalletUsable = false;
    }
}
