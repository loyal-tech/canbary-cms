package com.adopt.apigw.modules.childcustomer.entity;

import com.adopt.apigw.modules.childcustomer.dto.ChildCustPojo;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
@Entity
@Data
@Table(name = "tblchildcustomer")
public class ChildCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "create_by_staff_id")
    private Long createByStaffId;

    @Column(name = "last_modify_by_staff_id")
    private Long lastModifyByStaffId;

    @Column(name = "created_by_name")
    private String createdByName;

    @Column(name = "updated_by_name")
    private String updatedByName;

    @Column(name = "create_date_time")
    private LocalDateTime createDateTime;

    @Column(name = "modify_date_time")
    private LocalDateTime modifyDateTime;

    @Column(name = "mvno_id")
    private Long mvnoId;

    @Column(name = "parent_cust_id")
    private Long parentCustId;

    @Column(name = "wallet")
    private Double wallet;

    @Column(name = "status")
    private String status;

    @Column(name = "bu_id")
    private Integer buId;

    @Column(name = "isdeleted")
    private Boolean isdeleted;

    @Column(name = "isparent")
    private Boolean isParent;

    @Column(name = "mobilenumber")
    private String mobileNumber;
    @Column(name = "parent_accountnumber")
    private String parentAccountNumber;

    public ChildCustomer(ChildCustPojo pojo) {
        this.firstName = pojo.getFirstName();
        this.lastName = pojo.getLastName();
        this.userName = pojo.getUserName();
        this.password = pojo.getPassword();
        this.email = pojo.getEmail();
        this.wallet = pojo.getWallet();
        this.status = pojo.getStatus();
        this.parentCustId=pojo.getParentCustId();
        this.isParent= pojo.getIsParent();
        this.isdeleted = pojo.getIsDeleted()!= null ? pojo.getIsDeleted() : false;
        this.mobileNumber = pojo.getMobileNumber();
        this.parentAccountNumber=pojo.getAccountNumber();
    }
    public ChildCustomer() {

    }

    public ChildCustomer(Long id, String userName, Boolean isdeleted, String createdByName, String email, String lastName,
                         String mobileNumber, String firstName, Double wallet, Boolean isParent, Long createByStaffId,
                         Integer buId, LocalDateTime createDateTime, Long parentCustId, String status) {
        this.id = id;
        this.userName = userName;
        this.isdeleted = isdeleted;
        this.createdByName = createdByName;
        this.email = email;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.firstName = firstName;
        this.wallet = wallet;
        this.isParent = isParent;
        this.createByStaffId = createByStaffId;
        this.buId = buId;
        this.createDateTime = createDateTime;
        this.parentCustId = parentCustId;
        this.status = status;
    }
    public ChildCustomer(Long id, String userName, String password) {
        this.id = id;
        this.userName = userName;
        this.password = password;
    }
}
