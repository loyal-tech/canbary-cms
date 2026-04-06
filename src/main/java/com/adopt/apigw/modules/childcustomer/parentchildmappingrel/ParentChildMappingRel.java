package com.adopt.apigw.modules.childcustomer.parentchildmappingrel;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "tblparentchildmappingrel")
public class ParentChildMappingRel {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "parent_username", length = 255)
    private String parentUsername;

    @Column(name = "child_username", length = 255)
    private String childUsername;

    @Column(name = "create_by_staff_id")
    private Long createdByStaff;

    @Column(name = "mvno_id")
    private Long mvno;

    @Column(name = "parent_cust_id")
    private Long parentCustomer;

    @Column(name = "child_cust_id")
    private Long childCustomer;

    @Column(name = "isparent")
    private Boolean isparent;

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "parent_firstname", length = 255)
    private String parentFirstName;

    @Column(name = "parent_lastname", length = 255)
    private String parentLastName;

    @Column(name = "child_firstname", length = 255)
    private String childFirstName;

    @Column(name = "child_lastname", length = 255)
    private String childLastName;

    @Column(name = "child_email", length = 255)
    private String childEmail;

    @Column(name = "child_mobile", length = 255)
    private String childMobile;

    @Column(name = "status")
    private String status;

    @Column(name = "is_delete")
    private Boolean isDelete;

    @Column(name = "child_password", length = 255)
    private String childPassword;
    @Column(name = "parent_accountnumber")
    private String parentAccountNumber;

    @Column(name = "is_parent_wallet_usable")
    private Boolean isParentWalletUsable;

    public ParentChildMappingRel(){

    }

    public ParentChildMappingRel(Long id, String parentUsername, String childUsername, Long createdByStaff, Long mvno, Long parentCustomer, Long childCustomer, Boolean isparent, Long partnerId, String parentFirstName, String parentLastName, String childFirstName, String childLastName, String childEmail, String childMobile, String status, Boolean isDelete,String parentAccountNumber) {
        this.id = id;
        this.parentUsername = parentUsername;
        this.childUsername = childUsername;
        this.createdByStaff = createdByStaff;
        this.mvno = mvno;
        this.parentCustomer = parentCustomer;
        this.childCustomer = childCustomer;
        this.isparent = isparent;
        this.partnerId = partnerId;
        this.parentFirstName = parentFirstName;
        this.parentLastName = parentLastName;
        this.childFirstName = childFirstName;
        this.childLastName = childLastName;
        this.childEmail = childEmail;
        this.childMobile = childMobile;
        this.status = status;
        this.isDelete = isDelete;
        this.parentAccountNumber=parentAccountNumber;
    }





}