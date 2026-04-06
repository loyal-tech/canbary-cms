package com.adopt.apigw.modules.linkacceptance.domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable2;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmleasecircuit")
public class LinkAcceptance extends Auditable2 implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "circuit_name")
    private String circuitName;
    @Column(name = "circuit_status")
    private String circuitStatus;
    @Column(name = "caf_no")
    private Long cafNo;
    @Column(name = "upload_caf")
    private String uploadCAF;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "account_number")
    private Long accountNumber;
    @Column(name = "type_of_link")
    private String typeOfLink;  //dropdowm value not clear.
    @Column(name = "planservice_id")
    private Long planService;
    @Column(name = "link_installation_date")
    private LocalDate linkInstallationDate;
    @Column(name = "link_acceptance_date")
    private LocalDate linkAcceptanceDate;
    @Column(name = "purchase_order_date")
    private LocalDate purchaseOrderDate;
//    @Column(name = "circle_name")
//    private String circleName;
//    @Column(name = "order_login_date")
//    private LocalDate orderLoginDate;
    @Column(name = "partner_id")
    private Long partner;
    @Column(name = "expiry_date")
    private LocalDate expiryDate;
    @Column(name = "distance")
    private Long distance;
    @Column(name = "distance_unit")
    private String distanceUnit;
    @Column(name = "bandwidth")
    private Long bandwidth;
    @Column(name = "uploadQOS")
    private String UploadQOS;
    @Column(name = "downloadQOS")
    private String downloadQOS;
    @Column(name = "link_router_location")
    private String LinkRouterLocation;
    @Column(name = "link_port_type")
    private String linkPortType;
    @Column(name = "link_router_ip")
    private Long linkRouterIp;
    @Column(name = "link_port_on_router")
    private String linkPortOnRouter;
    @Column(name = "vlan_id")
    private Long vLANId;
    @Column(name = "bandwidth_type")
    private String bandwidthType;
    @Column(name = "link_router_name")
    private String linkRouterName;
    @Column(name = "circuit_billing_id")
    private Long circuitBillingId;
    @Column(name = "pop")
    private String pop;      //(Select POP dropdown);
    @Column(name = "associated_level")
    private String associatedLevel;
    @Column(name = "location_level1")
    private String locationLevel1;
    @Column(name = "location_level2")
    private String locationLevel2;
    @Column(name = "location_level3")
    private String locationLevel3;
    @Column(name = "location_level4")
    private String locationLevel4;
    @Column(name = "base_station_id1")
    private Long baseStationId1;
    @Column(name = "base_station_id2")
    private Long baseStationId2;
//    @Column(name = "organisation_circle")
//    private String organisationCircle;
//    @Column(name = "termination_circle")
//    private String terminationCircle;
//    @Column(name = "organisation_address")
//    private String organisationAddress;
    @Column(name = "termination_address")
    private String terminationAddress;
//    @Column(name = "organisation_address2")
//    private String organisationAddress2;
//    @Column(name = "termination_address2")
//    private String terminationAddress2;
    @Column(name = "note")
    private String note;
    @Column(name = "contact_person")
    private String contactPerson;
    @Column(name = "contact_person1")
    private String contactPerson1;
//    @Column(name = "contact_person2")
//    private String contactPerson2;
    @Column(name = "mobile_number")
    private String mobileNumber;
    @Column(name = "mobile_number1")
    private String mobileNumber1;
//    @Column(name = "mobile_number2")
//    private String mobileNumber2;
    @Column(name = "landline_number")
    private String landlineNumber;
    @Column(name = "landline_number1")
    private String landlineNumber1;
//    @Column(name = "landline_number2")
//    private String landlineNumber2;
    @Column(name = "email_id")
    private String emailId;
    @Column(name = "email_id1")
    private String emailId1;
//    @Column(name = "email_id2")
//    private String emailId2;
    @Column(name = "remarks")
    private String remarks;
//    @Column(name = "trai_rate")
//    private Long traiRate;
    @Column(name = "otc_charges_file")
    private String otcChargesFile;
    @Column(name = "service_charger_file")
    private String serviceChargerFile;
    //Items :
    //Add Items
    @Column(name = "static_or_pooled_ip")
    private String staticOrPooledIP;
    @Column(name = "charge_type_file")
    private String chargeTypeFile;
    @Column(name = "billing_cycle")
    private String billingCycle;
    @Column(name = "billing_type")
    private String billingType;
    @Column(name = "billable")
    private String billable; //CircuitOrAccount;
    @Column(name = "billing_group")
    private String billingGroup;
    @Column(name = "payable")
    private String payable; //circuit or account;
    @Column(name = "enable_processing")
    private String enableProcessing; //- Yes or No
    @Column(name = "deposite")
    private String deposite;
    @Column(name = "po_number")
    private String poNumber;
    @Column(name = "bill_remark")
    private String billRemark;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "organisation")
    private String organisation;
    @Column(name = "address1")
    private String address1;
    @Column(name = "address2")
    private String address2;
    @Column(name = "city")
    private String city;
    @Column(name = "zipcode")
    private String zipcode;
    @Column(name = "state")
    private String state;
    @Column(name = "country")
    private String country;
    @Column(name = "status")
    private String status;
    @Column(columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;
    //@Column(name = "MVNOID")
    //private Integer mvnoId;

    @Column(name = "buid")
    private Long buId;
    @Column(name = "custid")
    private Integer custId;
//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "custid")
//    private Customers customer;
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return this.isDeleted;
    }

    @Override
    public void setBuId(Long buId) {
       this.buId=buId;
    }

    public LinkAcceptance(CustomerServiceMapping customerServiceMappingData) {

        setId(customerServiceMappingData.getLeaseCircuitId());
        setCircuitName(customerServiceMappingData.getLeaseCircuitName());
        setCircuitStatus(customerServiceMappingData.getCircuitStatus());
        setCafNo(customerServiceMappingData.getCafNo());
        setUploadCAF(customerServiceMappingData.getUploadCAF());
        setCustomerName(customerServiceMappingData.getCustomerName());
        setAccountNumber(customerServiceMappingData.getAccountNumber());
        setTypeOfLink(customerServiceMappingData.getTypeOfLink());
        setLinkInstallationDate(customerServiceMappingData.getLinkInstallationDate());
        setLinkAcceptanceDate(customerServiceMappingData.getLinkAcceptanceDate());
        setPurchaseOrderDate(customerServiceMappingData.getPurchaseOrderDate());
//        setCircleName(customerServiceMappingData.getCircleName());
//        setOrderLoginDate(customerServiceMappingData.getOrderLoginDate());
        setPartner(customerServiceMappingData.getPartner());
        setExpiryDate(customerServiceMappingData.getExpiryDate());
        setDistance(customerServiceMappingData.getDistance());
        setDistanceUnit(customerServiceMappingData.getDistanceUnit());
        setBandwidth(customerServiceMappingData.getBandwidth());
        setUploadQOS(customerServiceMappingData.getUploadQOS());
        setDownloadQOS(customerServiceMappingData.getDownloadQOS());
        setLinkRouterLocation(customerServiceMappingData.getLinkRouterLocation());
        setLinkPortType(customerServiceMappingData.getLinkPortType());
        setLinkRouterIp(customerServiceMappingData.getLinkRouterIp());
        setLinkPortOnRouter(customerServiceMappingData.getLinkPortOnRouter());
        setVLANId(customerServiceMappingData.getVLANId());
        setBandwidthType(customerServiceMappingData.getBandwidthType());
        setLinkRouterName(customerServiceMappingData.getLinkRouterName());
        setCircuitBillingId(customerServiceMappingData.getCircuitBillingId());
        setPop(customerServiceMappingData.getPop());
        setAssociatedLevel(customerServiceMappingData.getAssociatedLevel());
        setLocationLevel1(customerServiceMappingData.getLocationLevel1());
        setLocationLevel2(customerServiceMappingData.getLocationLevel2());
        setLocationLevel3(customerServiceMappingData.getLocationLevel3());
        setLocationLevel4(customerServiceMappingData.getLocationLevel4());
        setBaseStationId1(customerServiceMappingData.getBaseStationId1());
        setBaseStationId2(customerServiceMappingData.getBaseStationId2());
//        setOrganisationCircle(customerServiceMappingData.getOrganisationCircle());
//        setTerminationCircle(customerServiceMappingData.getTerminationCircle());
        setTerminationAddress(customerServiceMappingData.getTerminationAddress());
//        setOrganisationAddress2(customerServiceMappingData.getOrganisationAddress2());
//        setTerminationAddress2(customerServiceMappingData.getTerminationAddress2());
        setNote(customerServiceMappingData.getNote());
        setContactPerson(customerServiceMappingData.getContactPerson());
        setContactPerson1(customerServiceMappingData.getContactPerson1());
//        setContactPerson2(customerServiceMappingData.getContactPerson2());
        setMobileNumber(customerServiceMappingData.getMobileNumber());
        setMobileNumber1(customerServiceMappingData.getMobileNumber1());
//        setMobileNumber2(customerServiceMappingData.getMobileNumber2());
        setLandlineNumber(customerServiceMappingData.getLandlineNumber());
        setLandlineNumber1(customerServiceMappingData.getLandlineNumber1());
//        setLandlineNumber2(customerServiceMappingData.getLandlineNumber2());
        setEmailId(customerServiceMappingData.getEmailId());
        setEmailId1(customerServiceMappingData.getEmailId1());
//        setEmailId2(customerServiceMappingData.getEmailId2());
//        setTraiRate(customerServiceMappingData.getTraiRate());
        setOtcChargesFile(customerServiceMappingData.getOtcChargesFile());
        setServiceChargerFile(customerServiceMappingData.getServiceChargerFile());
        setStaticOrPooledIP(customerServiceMappingData.getStaticOrPooledIP());
        setChargeTypeFile(customerServiceMappingData.getChargeTypeFile());
        setBillingCycle(customerServiceMappingData.getBillingCycle());
        setBillingType(customerServiceMappingData.getBillingType());
        setBillable(customerServiceMappingData.getBillable());
        setBillingGroup(customerServiceMappingData.getBillingGroup());
        setPayable(customerServiceMappingData.getPayable());
        setEnableProcessing(customerServiceMappingData.getEnableProcessing());
        setDeposite(customerServiceMappingData.getDeposite());
        setPoNumber(customerServiceMappingData.getPoNumber());
        setBillRemark(customerServiceMappingData.getBillRemark());
        setFullName(customerServiceMappingData.getFullName());
        setOrganisation(customerServiceMappingData.getOrganisation());
        setAddress1(customerServiceMappingData.getAddress1());
        setAddress2(customerServiceMappingData.getAddress2());
        setCity(customerServiceMappingData.getCity());
        setZipcode(customerServiceMappingData.getZipcode());
        setState(customerServiceMappingData.getState());
        setCountry(customerServiceMappingData.getCountry());
        setStatus(customerServiceMappingData.getStatus());
        setMvnoId(customerServiceMappingData.getMvnoId());
        setBuId(customerServiceMappingData.getBuId());
    }
}
