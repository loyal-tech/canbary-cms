package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.model.common.Auditable;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class PartnerPojo extends Auditable {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    @ApiModelProperty(notes = "Possible values: PERCUSTFLAT, PERCUSTPERC, PRICEBOOK")
    private String commtype;

    private Double commrelvalue = 0.0;

    private Double balance = 0.0;

    @NotNull
    private Integer commdueday;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextbilldate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastbilldate;

    private Integer taxid;

    private Double credit;

    @NotNull
    @ApiModelProperty(notes = "Possible values: home, office, other")
    private String addresstype;

    @NotNull
    private String address1;

    @NotNull
    private String address2;

    @NotNull
    private Integer city;

    @NotNull
    private Integer state;

    @NotNull
    private Integer country;

    @NotNull
    private String pincode;

    @NotNull
    private String mobile;

    // added country code
    private String countryCode;

    private String prcode;

    private String partnerType;

    @NotNull
    private String email;

    private List<Long> serviceAreaIds = new ArrayList<>();

    private Integer parentpartnerid;

    private Boolean isDelete = false;

    private List<String> serviceAreaNameList = new ArrayList<>();

    private String cityName;
    private String countryName;
    private String stateName;
    private String taxName;
    private String parentPartnerName;
    private Long pricebookId;
    private String pricebookname;
    private String cpName;
    private String cname;
    private String panName;

    private Double outcomeBalance = 0.0;
    private Long totalCustomerCount;
    private Long renewCustomerCount;
    private Long newCustomerCount;
    private String calendarType;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate ResetDate;
    
    @ApiModelProperty(notes = "Possible values: Balance,Revenue")
    private String commissionShareType;
    
    private Integer mvnoId;

    private Long buId;

    private Double creditConsume;

    private Integer displayId;
    private String displayName;
    private Long region ;
    private Long branch ;
    private Long bussinessvertical ;


    public Long getBuId() {
        return buId;
    }

    public void setBuId(Long buId) {
        this.buId = buId;
    }


    @Override
    public String toString() {
        return "PartnerPojo [id=" + id + ", name=" + name + ", status=" + status + ", commtype=" + commtype
                + ", commrelvalue=" + commrelvalue + ", commdueday=" + commdueday + ", nextbilldate=" + nextbilldate
                + ", lastbilldate=" + lastbilldate + ", taxid=" + taxid + ", addresstype=" + addresstype + ", address1="
                + address1 + ", address2=" + address2 + ", city=" + city + ", state=" + state + ", country=" + country
                + ", pincode=" + pincode + ", mobile=" + mobile + ", email=" + email + "]";
    }
}
