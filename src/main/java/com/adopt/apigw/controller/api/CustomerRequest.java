package com.adopt.apigw.controller.api;

import lombok.Data;

import java.util.List;
@Data
public class CustomerRequest {

    private String title;                     
    private String firstname;                 
    private String lastname;                  
    private String username;                  
    private String password;                  

    private String countryCode;               
    private String primaryMobile;               
    private String email;                     
    private String pan;                       

    private String custtype;                  
    private String contactperson;
    private String customerType;

    private Integer billday;                  
    private String status;                    

    private String serviceArea;               
    private String branchName;                
    private String partner;                   

    private String address;                   
    private String municipality;              
    private String ward;                      
    private String landmark;                
    private Integer earlybillday;             


    private String planCategory;
    private String billTo;
    private String ServiceName;
    private List<String> plannameList;
    private String calendarType;
    private String customerCategory;          
    private String CDCustomerType;            
    private String CDCustomerSubType;         
    private String customerSector;            
    private String customerSectorType;

    private String CAFNumber;                 
    private String DOB;
    private String secondaryMobile;           
    private String telephone;                 
    private String fax;                       

    private String dedicatedStaffUserName;    
    private String parentCustomer;            
    private String salesMark;                 
    private String parentExperience;          
    private String valleyType;                

    private String latitude;                  
    private String longitude;                 

    private String POP;                       
    private String OLT;                       
    private String masterDB;                  
    private String splitterDB;                
    private String staticIP;                  
    private String NASIP;
    private String NASPortValidate;
    private String planGroupName;
    private String invoiceType;               
    private String invoiceToOrganization;
    private String billableTo;
    private String discountType;              
    private String discountPercentage;        
    private String DExpiryDate;
    private String NewPriceWithDiscount;      
    private String areaName;
    private Integer mvnoId;
}
