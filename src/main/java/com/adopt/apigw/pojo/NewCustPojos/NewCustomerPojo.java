package com.adopt.apigw.pojo.NewCustPojos;

import com.adopt.apigw.pojo.api.CustomerLocationMappingDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCustomerPojo {
    private Integer id;
    private String custname;
    private Long serviceareaid;
    private String custtype;

    private String mobile;

    private String countryCode;
    private String email;
    private String organisation;
    private String businessType;
    private String title;
    private String firstname;
    private String lastname;
    private String status;
    private Boolean isInvoiceToOrg;
    private Boolean istrialplan;
    private Integer plangroupid;
    private Integer parentCustomerId;
    private Boolean hasChildCust = false;
    private Boolean isDunningEnable;
    private Boolean isNotificationEnable;
    private String username;
    private Long popid;
    private Long oltid;
    private Long branch; // Changed to Long to match CustomersPojo
    private Integer partnerid;
    private Integer currentAssigneeId;
    private LocalDate nextfollowupdate;
    private LocalDateTime birthDate; // Changed to LocalDateTime to match CustomersPojo
    private Integer billableCustomerId;
    private String parentQuotaType;
    private String parentExperience;
    private String parentCustomerName;
    private String customerType;
    private String customerSector;
    private String contactperson;
    private String cafno;
    private String acctno;
    private String calendarType;
    private Integer billday;
    private List<CustomerLocationMappingDto> customerLocations;
    private List<NewAddressListPojo> addressList = new ArrayList<>();
    private List<NewCustPlanMappingPojo> planMappingPojoList = new ArrayList<>();
    private Date charge_date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextBillDate;

    private String invoiceType;
    private LocalDateTime firstActivationDate;
    private String pan;
    private String fax;
    private String customerbillingid;
    private String dunningCategory;
    private String customerSubSector;
    private Double flatAmount;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextQuotaResetDate;

    private LocalDate nearestMacRetentionDate;
    private String blockNo;
    private Double walletbalance;
    private String serviceareaName;
    private String branchName;
    private String regionName;
    private String buVerticals;
    private String valleyType;
    private String latitude;
    private String longitude;
    private String framedIpBind;
    private String nasIpAddress;
    private String nasPort;
    private String ipPoolNameBind;
    private String vlan_id;
    private String framedIp;
    private String framedIpv6Address;
    private Integer maxconcurrentsession;
    private String framedroute;
    private String framedIPNetmask;
    private String framedIPv6Prefix;
    private String gatewayIP;
    private String primaryDNS;
    private String primaryIPv6DNS;
    private String secondaryDNS;
    private String secondaryIPv6DNS;
    private String delegatedprefix;
    private String nasPortId;
    private Boolean mac_provision;
    private Boolean mac_auth_enable;
    private Integer mvnoId;
    private Long buId;
    private Integer partnerPaymentId;

    public NewCustomerPojo(Integer id, String custname, Long serviceareaid, String custtype, String mobile, String countryCode, String email, String organisation, String businessType, String title,
                           String firstname, String lastname, String status, Boolean istrialplan, Integer plangroupid, Integer parentCustomerId,
                           Boolean isDunningEnable, Boolean isNotificationEnable, String username, Long popid, Long oltid, Long branch, Integer partnerid, Integer currentAssigneeId, LocalDate nextfollowupdate,
                           LocalDateTime birthDate, Integer billableCustomerId, String parentExperience, String parentCustomerName, String customerType, String customerSector,
                           String contactperson, String cafno, String acctno, String calendarType, Integer billday, LocalDate nextBillDate, String invoiceType, LocalDateTime firstActivationDate,
                           String pan, String fax, String customerbillingid, String dunningCategory, String customerSubSector, Double flatAmount, LocalDate nextQuotaResetDate,
                           String blockNo, Double walletbalance, String serviceareaName, String valleyType, String latitude, String longitude, String framedIpBind,
                           String nasIpAddress, String nasPort, String ipPoolNameBind, String vlan_id, String framedIp, String framedIpv6Address, Integer maxconcurrentsession, String framedroute, String framedIPNetmask,
                           String framedIPv6Prefix, String gatewayIP, String primaryDNS, String primaryIPv6DNS, String secondaryDNS, String secondaryIPv6DNS, String delegatedprefix, String nasPortId, Boolean mac_provision,
                           Boolean mac_auth_enable, Integer mvnoId, Long buId) {
        this.id = id;
        this.custname = custname;
        this.serviceareaid = serviceareaid;
        this.custtype = custtype;
        this.mobile = mobile;
        this.countryCode = countryCode;
        this.email = email;
        this.organisation = organisation;
        this.businessType = businessType;
        this.title = title;
        this.firstname = firstname;
        this.lastname = lastname;
        this.status = status;
        this.istrialplan = istrialplan;
        this.plangroupid = plangroupid;
        this.parentCustomerId = parentCustomerId;
        this.isDunningEnable = isDunningEnable;
        this.isNotificationEnable = isNotificationEnable;
        this.username = username;
        this.popid = popid;
        this.oltid = oltid;
        this.branch = branch;
        this.partnerid = partnerid;
        this.currentAssigneeId = currentAssigneeId;
        this.nextfollowupdate = nextfollowupdate;
        this.birthDate = birthDate;
        this.billableCustomerId = billableCustomerId;
//        this.parentQuotaType = parentQuotaType;
        this.parentExperience = parentExperience;
        this.parentCustomerName = parentCustomerName;
        this.customerType = customerType;
        this.customerSector = customerSector;
        this.contactperson = contactperson;
        this.cafno = cafno;
        this.acctno = acctno;
        this.calendarType = calendarType;
        this.billday = billday;
//        this.charge_date = charge_date;
        this.nextBillDate = nextBillDate;
        this.invoiceType = invoiceType;
        this.firstActivationDate = firstActivationDate;
        this.pan = pan;
        this.fax = fax;
        this.customerbillingid = customerbillingid;
        this.dunningCategory = dunningCategory;
        this.customerSubSector = customerSubSector;
        this.flatAmount = flatAmount;
        this.nextQuotaResetDate = nextQuotaResetDate;
//        this.nearestMacRetentionDate = nearestMacRetentionDate;
        this.blockNo = blockNo;
        this.walletbalance = walletbalance;
        this.serviceareaName = serviceareaName;
//        this.branchName = branchName;
//        this.regionName = regionName;
//        this.buVerticals = buVerticals;
        this.valleyType = valleyType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.framedIpBind = framedIpBind;
        this.nasIpAddress = nasIpAddress;
        this.nasPort = nasPort;
        this.ipPoolNameBind = ipPoolNameBind;
        this.vlan_id = vlan_id;
        this.framedIp = framedIp;
        this.framedIpv6Address = framedIpv6Address;
        this.maxconcurrentsession = maxconcurrentsession;
        this.framedroute = framedroute;
        this.framedIPNetmask = framedIPNetmask;
        this.framedIPv6Prefix = framedIPv6Prefix;
        this.gatewayIP = gatewayIP;
        this.primaryDNS = primaryDNS;
        this.primaryIPv6DNS = primaryIPv6DNS;
        this.secondaryDNS = secondaryDNS;
        this.secondaryIPv6DNS = secondaryIPv6DNS;
        this.delegatedprefix = delegatedprefix;
        this.nasPortId = nasPortId;
        this.mac_provision = mac_provision;
        this.mac_auth_enable = mac_auth_enable;
        this.mvnoId = mvnoId;
        this.buId = buId;
//        this.partnerPaymentId = partnerPaymentId; not in use
    }
}
