package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustomerLedgerPojo;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Setter
@Getter
public class CustomersDetailsPojo {

    private Integer id;

    private String username;

    private String firstname;

    private String lastname;

    private String title;
    private String custname;

    private String status;
    private String custtype;

    private List<CustPlanMappping> planMappingList = new ArrayList<>();

    private Integer mvnoId;
    private Integer parentCustomerId;
    private Boolean hasChildCust = false;
    private String invoiceType;
//* CustId
//* Cust Full Name
//* Parent CustId
//* Cust Type
//* status
//* hasChildCust
//* invoiceType


//    public CustomersDetailsPojo(Integer id, String username, String firstname, String lastname, String title, String custname, String status, String custtype, Object[] planMappingList, Integer mvnoId, Integer parentCustomerId) {
//        this.id = id;
//        this.username = username;
//        this.firstname = firstname;
//        this.lastname = lastname;
//        this.title = title;
//        this.custname = custname;
//        this.status = status;
//        this.custtype = custtype;
//        this.planMappingList = new ArrayList(Arrays.asList(planMappingList));
//        this.mvnoId = mvnoId;
//        this.parentCustomerId = parentCustomerId;
//    }

    public CustomersDetailsPojo(Integer id, String username, String firstname, String lastname, String title, String custname, String status, String custtype, Collection<?> planMappingList, Integer mvnoId, Integer parentCustomerId) {
        this.id = id;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.title = title;
        this.custname = custname;
        this.status = status;
        this.custtype = custtype;
        for (Object item : planMappingList) {
            if (item instanceof CustPlanMappping) {
                this.planMappingList.add((CustPlanMappping) item);
            }
        }
//        this.planMappingList = planMappingList;
        this.mvnoId = mvnoId;
        this.parentCustomerId = parentCustomerId;
    }
}
