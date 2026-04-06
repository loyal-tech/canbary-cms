package com.adopt.apigw.AirtelAppToCRM.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AirtelAppToCRMDTO {
    private String customerMsisdn;
    private String merchantMsisdn;
    private String username;
    private String password;
    private String accountNo;
    private String customerName;
    private String customerReference;
    private String walletBalance;
    private String status;
    private String firstName;
    private String lastName;
    private Integer custId;
    private Integer mvnoId;
    private Integer buId;
    private String currencyCode;
    private String dueDate;
    private String mobileNumber;
    private String custtype;
    public AirtelAppToCRMDTO(String customerMsisdn, String username, String password, String accountNo,String walletBalance, String firstName, String lastName, String status, Integer custId, Integer mvnoId, Integer buId,String custtype) {
        this.customerMsisdn = customerMsisdn;
        this.username = username;
        this.password = password;
        this.accountNo = accountNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.walletBalance = walletBalance;
        this.status=status;
        this.custId = custId;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.custtype = custtype;
    }
    public AirtelAppToCRMDTO(String customerMsisdn, String username, String password, String accountNo,String walletBalance, String firstName, String lastName, String status, String merchantMsisdn){
        this.customerMsisdn = customerMsisdn;
        this.username = username;
        this.password = password;
        this.accountNo = accountNo;
        this.walletBalance = walletBalance;
        this.firstName =firstName;
        this.lastName = lastName;
        this.status = status;
        this.merchantMsisdn = merchantMsisdn;
    }
    public AirtelAppToCRMDTO(String customerMsisdn, String username, String password, String accountNo,String walletBalance, String firstName, String lastName, String status, Integer custId, Integer mvnoId, Integer buId,String mobileNumber,String custtype) {
        this.customerMsisdn = customerMsisdn;
        this.username = username;
        this.password = password;
        this.accountNo = accountNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.walletBalance = walletBalance;
        this.status=status;
        this.custId = custId;
        this.mvnoId = mvnoId;
        this.buId = buId;
        this.mobileNumber =mobileNumber;
        this.custtype = custtype;
    }
}
