package com.adopt.apigw.model.lead;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.InventoryManagement.PopManagement.domain.PopManagement;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.pojo.LeadCustPlanMapppingPojo;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.DiffInclude;
import org.javers.core.metamodel.annotation.TypeName;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tblmleadmaster")
public class LeadMaster {

	@Id
	@Column(name = "lead_master_id", nullable = false)
	private Long id;

	@Column(name = "username")
	private String username;

	@Column(name = "password")
	private String password;

	@Column(name = "firstname")
	private String firstname;

	@Column(name = "lastname")
	private String lastname;

	@Column(name = "email")
	private String email;

	@Column(name = "title")
	private String title;

	@Column(name = "custname")
	private String custname;

	@Column(name = "contactperson")
	private String contactperson;

	@Column(name = "pan")
	private String pan;

	@Column(name = "gst")
	private String gst;

	@Column(name = "aadhar")
	private String aadhar;

	@Column(name = "status")
	private String status;

	@Column(name = "failcount")
	private Integer failcount;

	@Column(name = "acctno")
	private String acctno;

	@Column(name = "cust_type")
	private String custtype;

	@Column(name = "phone")
	private String phone;

	@Column(name = "billday")
	private Integer billday;

	@Column(name = "partnerid")
	private Integer partnerid;

	@Column(name = "onuid")
	private String onuid;

	@Column(name = "next_bill_date")
	private LocalDate nextBillDate;

	@Column(name = "last_bill_date")
	private LocalDate lastBillDate;

	@Column(name = "addresstype")
	private String addresstype;

	@Column(name = "address1")
	private String address1;

	@Column(name = "address2")
	private String address2;

	@Column(name = "city")
	private Integer city;

	@Column(name = "state")
	private Integer state;

	@Column(name = "country")
	private Integer country;

	@Column(name = "pincode")
	private Integer pincode;

	@Column(name = "area")
	private Integer area;

	@Column(name = "outstanding")
	private Double outstanding;

	@Column(name = "oldpassword1")
	private String oldpassword1;

	@Column(name = "newpassword")
	private String newpassword;

	@Column(name = "oldpassword2")
	private String oldpassword2;

	@Column(name = "oldpassword3")
	private String oldpassword3;

	@Column(name = "selfcarepwd")
	private String selfcarepwd;

	@CreationTimestamp
	@Column(name = "last_password_change")
	private LocalDateTime last_password_change;

	@Column(name = "lastpasswordchangestring")
	private String lastpasswordchangestring;

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<LeadCustPlanMappping> planMappingList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<LeadCustomerAddress> addressList = new ArrayList<>();

	@Column(name = "radiusprofile_ids")
	private String radiusprofileIds;

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<LeadCustChargeDetails> overChargeList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<LeadCustChargeDetails> indiChargeList = new ArrayList<>();

	@JsonManagedReference
	@LazyCollection(LazyCollectionOption.FALSE)
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	private List<LeadCustMacMappping> custMacMapppingList = new ArrayList<>();
	
	@JsonManagedReference
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "leadMaster")
	@LazyCollection(LazyCollectionOption.FALSE)
	private List<LeadDocDetails> leadDocDetailsList = new ArrayList<>();

	@Column(name = "flash_msg")
	private String flashMsg;

	@Column(name = "mactelflag")
	private Boolean mactelflag;

	@Column(name = "mobile")
	private String mobile;

	@Column(name = "country_code")
	private String countryCode;

	@Column(name = "cafno")
	private String cafno;

	@Column(name = "altmobile")
	private String altmobile;

	@Column(name = "altphone")
	private String altphone;

	@Column(name = "altemail")
	private String altemail;

	@Column(name = "fax")
	private String fax;

	@Column(name = "resellerid")
	private Integer resellerid;

	@Column(name = "salesrepid")
	private Integer salesrepid;

	@Column(name = "voicesrvtype")
	private String voicesrvtype;

	@Column(name = "voiceprovision")
	private Boolean voiceprovision;

	@Column(name = "didno")
	private String didno;

	@Column(name = "childdidno")
	private String childdidno;

	@Column(name = "intercomno")
	private String intercomno;

	@Column(name = "intercomgrp")
	private String intercomgrp;

	@Column(name = "onlinerenewalflag")
	private Boolean onlinerenewalflag;

	@Column(name = "voipenableflag")
	private Boolean voipenableflag;

	@Column(name = "custcategory")
	private String custcategory;

	@Column(name = "walletbalance")
	private Double walletbalance;

	@Column(name = "networktype")
	private String networktype;

	public Integer getSlaTime() {
		return slaTime;
	}

	public void setSlaTime(Integer slaTime) {
		this.slaTime = slaTime;
	}

	public String getSlaUnit() {
		return slaUnit;
	}

	public void setSlaUnit(String slaUnit) {
		this.slaUnit = slaUnit;
	}

	public LocalDate getNextfollowupdate() {
		return nextfollowupdate;
	}

	public void setNextfollowupdate(LocalDate nextfollowupdate) {
		this.nextfollowupdate = nextfollowupdate;
	}

	public LocalTime getNextfollowuptime() {
		return nextfollowuptime;
	}

	public void setNextfollowuptime(LocalTime nextfollowuptime) {
		this.nextfollowuptime = nextfollowuptime;
	}

	@Column(name = "defaultpoolid")
	private Long defaultpoolid;

	@Column(name = "serviceareaid")
	private Long serviceareaid;

	@Column(name = "networkdevicesid")
	private Long networkdevicesid;

	@Column(name = "oltslotid")
	private Long oltslotid;

	@Column(name = "oltportid")
	private Long oltportid;

	@Column(name = "strconntype")
	private String strconntype;

	@Column(name = "stroltname")
	private String stroltname;

	@Column(name = "strslotname")
	private String strslotname;

	@Column(name = "strportname")
	private String strportname;

	@Column(name = "OldBNGRouterinterface")
	private String OldBNGRouterinterface;

	@Column(name = "OldVSIName")
	private String OldVSIName;

	@Column(name = "ASN_Number")
	private String ASNNumber;

	@Column(name = "BNG_routerinterface")
	private String BNGRouterinterface;

	@Column(name = "BNGRoutername")
	private String BNGRoutername;

	@Column(name = "IPPrefixes")
	private String IPPrefixes;

	@Column(name = "IPV6Prefixes")
	private String IPV6Prefixes;

	@Column(name = "LANIP")
	private String LANIP;

	@Column(name = "LANIPV6")
	private String LANIPV6;

	@Column(name = "LLAccount_id")
	private String LLAccountid;

	@Column(name = "LLConnection_type")
	private String LLConnectiontype;

	@Column(name = "LLExpiry_date")
	private String LLExpirydate;

	@Column(name = "LLMedium")
	private String LLMedium;

	@Column(name = "LLService_id")
	private String LLServiceid;

	@Column(name = "MACADDRESS")
	private String MACADDRESS;

	@Column(name = "Peer_ip")
	private String Peerip;

	@Column(name = "POOLIP")
	private String POOLIP;

	@Column(name = "QOS")
	private String QOS;

	@Column(name = "RDExport")
	private String RDExport;

	@Column(name = "RDValue")
	private String RDValue;

	@Column(name = "VLANID")
	private String VLANID;

	@Column(name = "VRFName")
	private String VRFName;

	@Column(name = "VSIID")
	private String VSIID;

	@Column(name = "VSIName")
	private String VSIName;

	@Column(name = "WANIP")
	private String WANIP;

	@Column(name = "WANIPV6")
	private String WANIPV6;

	@Column(name = "billentityname")
	private String billentityname;

	@Column(name = "addparam1")
	private String addparam1;

	@Column(name = "addparam2")
	private String addparam2;

	@Column(name = "addparam3")
	private String addparam3;

	@Column(name = "addparam4")
	private String addparam4;

	@Column(name = "purchaseorder")
	private String purchaseorder;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "allowed_ip_address")
	private String allowedIPAddress;

	@Column(name = "OldWANIP")
	private String OldWANIP;

	@Column(name = "OldLLAccountid")
	private String OldLLAccountid;

	@Column(name = "firstActivationDate")
	private LocalDateTime firstActivationDate;

	@Column(name = "is_deleted")
	private boolean isDeleted;

	@Column(name = "create_date_string")
	private String createDateString;

	@Column(name = "update_date_string")
	private String updateDateString;

	@Column(name = "latitude")
	private String latitude;

	@Column(name = "longitude")
	private String longitude;

	@Column(name = "url")
	private String url;

	@Column(name = "gis_code")
	private String gisCode;

	@Column(name = "salesremark")
	private String salesremark;

	@Column(name = "servicetype")
	private String servicetype;

	@Column(name = "is_cust_caf")
	private String isCustCaf;

	@Column(name = "previous_caf_approver")
	private Integer previousCafApprover;

	@Column(name = "next_caf_approver")
	private Integer nextCafApprover;

	@Column(name = "servicearea_name")
	private String serviceareaName;

	@Column(name = "caf_approve_status")
	private String cafApproveStatus;

	@Column(name = "mvno_id")
	private Long mvnoId;

	@Column(name = "tin_no")
	private String tinNo;

	@Column(name = "passport_no")
	private String passportNo;

	@Column(name = "dunning_category")
	private String dunningCategory;

	@Column(name = "plangroupid")
	private Integer plangroupid;

	@Column(name = "parent_customer_id")
	private Integer parentCustomerId;

	@Column(name = "parent_customer_name")
	private String parentCustomerName;

	@Column(name = "invoice_type")
	private String invoiceType;

	@Column(name = "calendar_type")
	private String calendarType;

	@Column(name = "discount")
	private Double discount;

	@Column(name = "bu_id")
	private Long buId;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_source_id")
	private LeadSource leadSource;

	@Column(name = "lead_sub_source_id")
	private Integer leadSubSourceId;
	
	@Column(name = "reject_reason_id")
	private Integer rejectReasonId;
	
	@Column(name = "reject_sub_reason_id")
	private Integer rejectSubReasonId;

	@Column(name = "reason_to_change_service_provider")
	private String reasonToChangeServiceProvider;

	@Column(name = "previous_vendor")
	private String previousVendor;

	@Column(name = "servicer_type")
	private String servicerType;

	@Column(name = "lead_status")
	private String leadStatus;

	@CreationTimestamp
	@Column(name = "created_on")
	private LocalDateTime createdOn;

	@UpdateTimestamp
	@Column(name = "last_modified_on")
	private LocalDateTime lastModifiedOn;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "last_modified_by")
	private String lastModifiedBy;

	@Column(name = "rejected_on")
	private LocalDateTime rejectedOn;

	@Column(name = "rejected_by")
	private String rejectedBy;

	@Column(name = "approved_on")
	private LocalDateTime approvedOn;

	@Column(name = "approved_by")
	private String approvedBy;

	@Column(name = "re_open_on")
	private LocalDateTime reOpenOn;

	@Column(name = "re_open_by")
	private String reOpenBy;

	@Column(name ="next_approve_staff_id")
	private Integer nextApproveStaffId;

	@Column(name = "next_team_mapping_id")
	private Integer nextTeamMappingId;
		
	@Column(name = "lead_category")
	private String leadCategory;
	
	@Column(name = "heard_about_subisu_from")
	private String heardAboutSubisuFrom;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_partner_id")
	private Partner partner;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_customer_id")
	private Customers customers;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_staff_id")
	private StaffUser staffUser;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_branch_id")
	private Branch leadBranch;
	
	@Column(name = "lead_agent_id")
	private Long leadAgentId;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "lead_service_area_id")
	private ServiceArea serviceArea;
	
	@Column(name = "feasibility")
	private String feasibility;
	
	@Column(name = "feasibility_remark")
	private String feasibilityRemark;
	
	@Column(name = "feasibility_required")
	private String feasibilityRequired;
	
	@Column(name = "reject_lead_time")
	private LocalDateTime rejectLeadTime;
		
	@Column(name = "lead_type")
	private String leadType;
	
	@Column(name = "existing_customer_id")
	private Long existingCustomerId;
		
	@Column(name = "final_approved")
	private boolean finalApproved;
	
	@Column(name = "plan_type")
	private String planType;

	@Column(name = "lead_no")
	private String leadNo;
	
	@Column(name = "present_check_for_payment")
	private boolean presentCheckForPayment = false;
	
	@Column(name = "present_check_for_permanent")
	private boolean presentCheckForPermanent = false;
	
	@Column(name = "lead_customer_category")
	private String leadCustomerCategory;
	
	@Column(name = "lead_customer_type")
	private String leadCustomerType;
	
	@Column(name = "lead_customer_sub_type")
	private String leadCustomerSubType;
	
	@Column(name = "lead_customer_sector")
	private String leadCustomerSector;
	
	@Column(name = "lead_customer_sub_sector")
	private String leadCustomerSubSector;
	
	@Column(name = "valley_type")
	private String valleyType;
	
	@Column(name = "inside_valley")
	private String insideValley;
	
	@Column(name = "outside_valley")
	private String outsideValley;
	
	@Column(name = "competitor_duration")
	private String competitorDuration;
	
	@Column(name = "expiry")
	private LocalDate expiry;
	
	@Column(name = "amount")
	private Double amount;
	
	@Column(name = "feedback")
	private String feedback;
	
	@Column(name = "gender")
	private String gender;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "branch_id")
	private Branch branch;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "pop_id")
	private PopManagement popManagement;
	
	@Column(name = "date_of_birth")
	private LocalDate dateOfBirth;
	
	@Column(name = "secondary_contact_details")
	private String secondaryContactDetails;
	
	@Column(name = "secondary_phone")
	private String secondaryPhone;
	
	@Column(name = "secondary_email")
	private String secondaryEmail;
	
	@Column(name = "previous_amount")
	private Double previousAmount;
	
	@Column(name = "previous_month")
	private String previousMonth;
	
	@Column(name = "lead_origin_type")
	private String leadOriginType;
	
	@Column(name = "require_service_type")
	private String requireServiceType;
	
	@Column(name = "landline_number")
	private String landlineNumber;

	@Column(name = "p_contact_phno")
	private String pcontactphno;

	@Column(name = "s_contact_name")
	private String scontactname;

	@Column(name = "business_verticals")
	private String businessverticals;

	@Column(name = "sub_business_verticals")
	private String subbusinessverticals;

	@Column(name = "connection_type")
	private String connectiontype;

	@Column(name = "link_type")
	private String linktype;

	@Column(name = "circuit_area")
	private String circuitarea;

	@Column(name = "closure_date")
	private LocalDate closuredate;

	@Column(name = "circuit_id")
	private Long circuitid;

	@Column(name = "circuit_name")
	private String circuitname;

	@Column(name = "lead_variety")
	private String leadvariety;

	@Column(name = "is_lead_quickinv")
	private Integer isLeadQuickInv;

	@Column(name = "lead_department")
	private String leadDepartment;

	public String getAltmobile1() {
		return altmobile1;
	}

	public void setAltmobile1(String altmobile1) {
		this.altmobile1 = altmobile1;
	}

	public String getAltmobile2() {
		return altmobile2;
	}

	public void setAltmobile2(String altmobile2) {
		this.altmobile2 = altmobile2;
	}

	public String getAltmobile3() {
		return altmobile3;
	}

	public void setAltmobile3(String altmobile3) {
		this.altmobile3 = altmobile3;
	}

	public String getAltmobile4() {
		return altmobile4;
	}

	public void setAltmobile4(String altmobile4) {
		this.altmobile4 = altmobile4;
	}

	@Column(name = "altmobile1")
	private String altmobile1;

	@Column(name = "altmobile2")
	private String altmobile2;

	@Column(name = "altmobile3")
	private String altmobile3;

	@Column(name = "altmobile4")
	private String altmobile4;

	@Column(name = "sla_time")
	private Integer slaTime;

	@DiffIgnore
	@Column(name = "sla_unit")
	private String slaUnit;

	@DiffIgnore
	@Column(name = "nextfollowupdate")
	private LocalDate nextfollowupdate;
	@DiffIgnore
	@Column(name = "nextfollowuptime")
	private LocalTime nextfollowuptime;
	@Column(name="blockno")
	private String blockNo;
	
	public LeadMaster(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCustname() {
		return custname;
	}

	public void setCustname(String custname) {
		this.custname = custname;
	}

	public String getContactperson() {
		return contactperson;
	}

	public void setContactperson(String contactperson) {
		this.contactperson = contactperson;
	}

	public String getPan() {
		return pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public String getGst() {
		return gst;
	}

	public void setGst(String gst) {
		this.gst = gst;
	}

	public String getAadhar() {
		return aadhar;
	}

	public void setAadhar(String aadhar) {
		this.aadhar = aadhar;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getFailcount() {
		return failcount;
	}

	public void setFailcount(Integer failcount) {
		this.failcount = failcount;
	}

	public String getAcctno() {
		return acctno;
	}

	public void setAcctno(String acctno) {
		this.acctno = acctno;
	}

	public String getCusttype() {
		return custtype;
	}

	public void setCusttype(String custtype) {
		this.custtype = custtype;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getBillday() {
		return billday;
	}

	public void setBillday(Integer billday) {
		this.billday = billday;
	}

	public Integer getPartnerid() {
		return partnerid;
	}

	public void setPartnerid(Integer partnerid) {
		this.partnerid = partnerid;
	}

	public String getOnuid() {
		return onuid;
	}

	public void setOnuid(String onuid) {
		this.onuid = onuid;
	}

	public LocalDate getNextBillDate() {
		return nextBillDate;
	}

	public void setNextBillDate(LocalDate nextBillDate) {
		this.nextBillDate = nextBillDate;
	}

	public LocalDate getLastBillDate() {
		return lastBillDate;
	}

	public void setLastBillDate(LocalDate lastBillDate) {
		this.lastBillDate = lastBillDate;
	}

	public String getAddresstype() {
		return addresstype;
	}

	public void setAddresstype(String addresstype) {
		this.addresstype = addresstype;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public Integer getCity() {
		return city;
	}

	public void setCity(Integer city) {
		this.city = city;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getCountry() {
		return country;
	}

	public void setCountry(Integer country) {
		this.country = country;
	}

	public Integer getPincode() {
		return pincode;
	}

	public void setPincode(Integer pincode) {
		this.pincode = pincode;
	}

	public Integer getArea() {
		return area;
	}

	public void setArea(Integer area) {
		this.area = area;
	}

	public Double getOutstanding() {
		return outstanding;
	}

	public void setOutstanding(Double outstanding) {
		this.outstanding = outstanding;
	}

	public String getOldpassword1() {
		return oldpassword1;
	}

	public void setOldpassword1(String oldpassword1) {
		this.oldpassword1 = oldpassword1;
	}

	public String getNewpassword() {
		return newpassword;
	}

	public void setNewpassword(String newpassword) {
		this.newpassword = newpassword;
	}

	public String getOldpassword2() {
		return oldpassword2;
	}

	public void setOldpassword2(String oldpassword2) {
		this.oldpassword2 = oldpassword2;
	}

	public String getOldpassword3() {
		return oldpassword3;
	}

	public void setOldpassword3(String oldpassword3) {
		this.oldpassword3 = oldpassword3;
	}

	public String getSelfcarepwd() {
		return selfcarepwd;
	}

	public void setSelfcarepwd(String selfcarepwd) {
		this.selfcarepwd = selfcarepwd;
	}

	public LocalDateTime getLast_password_change() {
		return last_password_change;
	}

	public void setLast_password_change(LocalDateTime last_password_change) {
		this.last_password_change = last_password_change;
	}

	public String getLastpasswordchangestring() {
		return lastpasswordchangestring;
	}

	public void setLastpasswordchangestring(String lastpasswordchangestring) {
		this.lastpasswordchangestring = lastpasswordchangestring;
	}

	public List<LeadCustPlanMappping> getPlanMappingList() {
		return planMappingList;
	}

	public void setPlanMappingList(List<LeadCustPlanMappping> planMappingList) {
		this.planMappingList = planMappingList;
	}

	public List<LeadCustomerAddress> getAddressList() {
		return addressList;
	}

	public void setAddressList(List<LeadCustomerAddress> addressList) {
		this.addressList = addressList;
	}

	public String getRadiusprofileIds() {
		return radiusprofileIds;
	}

	public void setRadiusprofileIds(String radiusprofileIds) {
		this.radiusprofileIds = radiusprofileIds;
	}

	public List<LeadCustChargeDetails> getOverChargeList() {
		return overChargeList;
	}

	public void setOverChargeList(List<LeadCustChargeDetails> overChargeList) {
		this.overChargeList = overChargeList;
	}

	public List<LeadCustChargeDetails> getIndiChargeList() {
		return indiChargeList;
	}

	public void setIndiChargeList(List<LeadCustChargeDetails> indiChargeList) {
		this.indiChargeList = indiChargeList;
	}

	public List<LeadCustMacMappping> getCustMacMapppingList() {
		return custMacMapppingList;
	}

	public void setCustMacMapppingList(List<LeadCustMacMappping> custMacMapppingList) {
		this.custMacMapppingList = custMacMapppingList;
	}

	public List<LeadDocDetails> getLeadDocDetailsList() {
		return leadDocDetailsList;
	}

	public void setLeadDocDetailsList(List<LeadDocDetails> leadDocDetailsList) {
		this.leadDocDetailsList = leadDocDetailsList;
	}

	public String getFlashMsg() {
		return flashMsg;
	}

	public void setFlashMsg(String flashMsg) {
		this.flashMsg = flashMsg;
	}

	public Boolean getMactelflag() {
		return mactelflag;
	}

	public void setMactelflag(Boolean mactelflag) {
		this.mactelflag = mactelflag;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCafno() {
		return cafno;
	}

	public void setCafno(String cafno) {
		this.cafno = cafno;
	}

	public String getAltmobile() {
		return altmobile;
	}

	public void setAltmobile(String altmobile) {
		this.altmobile = altmobile;
	}

	public String getAltphone() {
		return altphone;
	}

	public void setAltphone(String altphone) {
		this.altphone = altphone;
	}

	public String getAltemail() {
		return altemail;
	}

	public void setAltemail(String altemail) {
		this.altemail = altemail;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public Integer getResellerid() {
		return resellerid;
	}

	public void setResellerid(Integer resellerid) {
		this.resellerid = resellerid;
	}

	public Integer getSalesrepid() {
		return salesrepid;
	}

	public void setSalesrepid(Integer salesrepid) {
		this.salesrepid = salesrepid;
	}

	public String getVoicesrvtype() {
		return voicesrvtype;
	}

	public void setVoicesrvtype(String voicesrvtype) {
		this.voicesrvtype = voicesrvtype;
	}

	public Boolean getVoiceprovision() {
		return voiceprovision;
	}

	public void setVoiceprovision(Boolean voiceprovision) {
		this.voiceprovision = voiceprovision;
	}

	public String getDidno() {
		return didno;
	}

	public void setDidno(String didno) {
		this.didno = didno;
	}

	public String getChilddidno() {
		return childdidno;
	}

	public void setChilddidno(String childdidno) {
		this.childdidno = childdidno;
	}

	public String getIntercomno() {
		return intercomno;
	}

	public void setIntercomno(String intercomno) {
		this.intercomno = intercomno;
	}

	public String getIntercomgrp() {
		return intercomgrp;
	}

	public void setIntercomgrp(String intercomgrp) {
		this.intercomgrp = intercomgrp;
	}

	public Boolean getOnlinerenewalflag() {
		return onlinerenewalflag;
	}

	public void setOnlinerenewalflag(Boolean onlinerenewalflag) {
		this.onlinerenewalflag = onlinerenewalflag;
	}

	public Boolean getVoipenableflag() {
		return voipenableflag;
	}

	public void setVoipenableflag(Boolean voipenableflag) {
		this.voipenableflag = voipenableflag;
	}

	public String getCustcategory() {
		return custcategory;
	}

	public void setCustcategory(String custcategory) {
		this.custcategory = custcategory;
	}

	public Double getWalletbalance() {
		return walletbalance;
	}

	public void setWalletbalance(Double walletbalance) {
		this.walletbalance = walletbalance;
	}

	public String getNetworktype() {
		return networktype;
	}

	public void setNetworktype(String networktype) {
		this.networktype = networktype;
	}

	public Long getDefaultpoolid() {
		return defaultpoolid;
	}

	public void setDefaultpoolid(Long defaultpoolid) {
		this.defaultpoolid = defaultpoolid;
	}

	public Long getServiceareaid() {
		return serviceareaid;
	}

	public void setServiceareaid(Long serviceareaid) {
		this.serviceareaid = serviceareaid;
	}

	public Long getNetworkdevicesid() {
		return networkdevicesid;
	}

	public void setNetworkdevicesid(Long networkdevicesid) {
		this.networkdevicesid = networkdevicesid;
	}

	public Long getOltslotid() {
		return oltslotid;
	}

	public void setOltslotid(Long oltslotid) {
		this.oltslotid = oltslotid;
	}

	public Long getOltportid() {
		return oltportid;
	}

	public void setOltportid(Long oltportid) {
		this.oltportid = oltportid;
	}

	public String getStrconntype() {
		return strconntype;
	}

	public void setStrconntype(String strconntype) {
		this.strconntype = strconntype;
	}

	public String getStroltname() {
		return stroltname;
	}

	public void setStroltname(String stroltname) {
		this.stroltname = stroltname;
	}

	public String getStrslotname() {
		return strslotname;
	}

	public void setStrslotname(String strslotname) {
		this.strslotname = strslotname;
	}

	public String getStrportname() {
		return strportname;
	}

	public void setStrportname(String strportname) {
		this.strportname = strportname;
	}

	public String getOldBNGRouterinterface() {
		return OldBNGRouterinterface;
	}

	public void setOldBNGRouterinterface(String oldBNGRouterinterface) {
		OldBNGRouterinterface = oldBNGRouterinterface;
	}

	public String getOldVSIName() {
		return OldVSIName;
	}

	public void setOldVSIName(String oldVSIName) {
		OldVSIName = oldVSIName;
	}

	public String getASNNumber() {
		return ASNNumber;
	}

	public void setASNNumber(String aSNNumber) {
		ASNNumber = aSNNumber;
	}

	public String getBNGRouterinterface() {
		return BNGRouterinterface;
	}

	public void setBNGRouterinterface(String bNGRouterinterface) {
		BNGRouterinterface = bNGRouterinterface;
	}

	public String getBNGRoutername() {
		return BNGRoutername;
	}

	public void setBNGRoutername(String bNGRoutername) {
		BNGRoutername = bNGRoutername;
	}

	public String getIPPrefixes() {
		return IPPrefixes;
	}

	public void setIPPrefixes(String iPPrefixes) {
		IPPrefixes = iPPrefixes;
	}

	public String getIPV6Prefixes() {
		return IPV6Prefixes;
	}

	public void setIPV6Prefixes(String iPV6Prefixes) {
		IPV6Prefixes = iPV6Prefixes;
	}

	public String getLANIP() {
		return LANIP;
	}

	public void setLANIP(String lANIP) {
		LANIP = lANIP;
	}

	public String getLANIPV6() {
		return LANIPV6;
	}

	public void setLANIPV6(String lANIPV6) {
		LANIPV6 = lANIPV6;
	}

	public String getLLAccountid() {
		return LLAccountid;
	}

	public void setLLAccountid(String lLAccountid) {
		LLAccountid = lLAccountid;
	}

	public String getLLConnectiontype() {
		return LLConnectiontype;
	}

	public void setLLConnectiontype(String lLConnectiontype) {
		LLConnectiontype = lLConnectiontype;
	}

	public String getLLExpirydate() {
		return LLExpirydate;
	}

	public void setLLExpirydate(String lLExpirydate) {
		LLExpirydate = lLExpirydate;
	}

	public String getLLMedium() {
		return LLMedium;
	}

	public void setLLMedium(String lLMedium) {
		LLMedium = lLMedium;
	}

	public String getLLServiceid() {
		return LLServiceid;
	}

	public void setLLServiceid(String lLServiceid) {
		LLServiceid = lLServiceid;
	}

	public String getMACADDRESS() {
		return MACADDRESS;
	}

	public void setMACADDRESS(String mACADDRESS) {
		MACADDRESS = mACADDRESS;
	}

	public String getPeerip() {
		return Peerip;
	}

	public void setPeerip(String peerip) {
		Peerip = peerip;
	}

	public String getPOOLIP() {
		return POOLIP;
	}

	public void setPOOLIP(String pOOLIP) {
		POOLIP = pOOLIP;
	}

	public String getQOS() {
		return QOS;
	}

	public void setQOS(String qOS) {
		QOS = qOS;
	}

	public String getRDExport() {
		return RDExport;
	}

	public void setRDExport(String rDExport) {
		RDExport = rDExport;
	}

	public String getRDValue() {
		return RDValue;
	}

	public void setRDValue(String rDValue) {
		RDValue = rDValue;
	}

	public String getVLANID() {
		return VLANID;
	}

	public void setVLANID(String vLANID) {
		VLANID = vLANID;
	}

	public String getVRFName() {
		return VRFName;
	}

	public void setVRFName(String vRFName) {
		VRFName = vRFName;
	}

	public String getVSIID() {
		return VSIID;
	}

	public void setVSIID(String vSIID) {
		VSIID = vSIID;
	}

	public String getVSIName() {
		return VSIName;
	}

	public void setVSIName(String vSIName) {
		VSIName = vSIName;
	}

	public String getWANIP() {
		return WANIP;
	}

	public void setWANIP(String wANIP) {
		WANIP = wANIP;
	}

	public String getWANIPV6() {
		return WANIPV6;
	}

	public void setWANIPV6(String wANIPV6) {
		WANIPV6 = wANIPV6;
	}

	public String getBillentityname() {
		return billentityname;
	}

	public void setBillentityname(String billentityname) {
		this.billentityname = billentityname;
	}

	public String getAddparam1() {
		return addparam1;
	}

	public void setAddparam1(String addparam1) {
		this.addparam1 = addparam1;
	}

	public String getAddparam2() {
		return addparam2;
	}

	public void setAddparam2(String addparam2) {
		this.addparam2 = addparam2;
	}

	public String getAddparam3() {
		return addparam3;
	}

	public void setAddparam3(String addparam3) {
		this.addparam3 = addparam3;
	}

	public String getAddparam4() {
		return addparam4;
	}

	public void setAddparam4(String addparam4) {
		this.addparam4 = addparam4;
	}

	public String getPurchaseorder() {
		return purchaseorder;
	}

	public void setPurchaseorder(String purchaseorder) {
		this.purchaseorder = purchaseorder;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getAllowedIPAddress() {
		return allowedIPAddress;
	}

	public void setAllowedIPAddress(String allowedIPAddress) {
		this.allowedIPAddress = allowedIPAddress;
	}

	public String getOldWANIP() {
		return OldWANIP;
	}

	public void setOldWANIP(String oldWANIP) {
		OldWANIP = oldWANIP;
	}

	public String getOldLLAccountid() {
		return OldLLAccountid;
	}

	public void setOldLLAccountid(String oldLLAccountid) {
		OldLLAccountid = oldLLAccountid;
	}

	public LocalDateTime getFirstActivationDate() {
		return firstActivationDate;
	}

	public void setFirstActivationDate(LocalDateTime firstActivationDate) {
		this.firstActivationDate = firstActivationDate;
	}

	public boolean isDeleted() {
		return isDeleted;
	}

	public void setDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getCreateDateString() {
		return createDateString;
	}

	public void setCreateDateString(String createDateString) {
		this.createDateString = createDateString;
	}

	public String getUpdateDateString() {
		return updateDateString;
	}

	public void setUpdateDateString(String updateDateString) {
		this.updateDateString = updateDateString;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getGisCode() {
		return gisCode;
	}

	public void setGisCode(String gisCode) {
		this.gisCode = gisCode;
	}

	public String getSalesremark() {
		return salesremark;
	}

	public void setSalesremark(String salesremark) {
		this.salesremark = salesremark;
	}

	public String getServicetype() {
		return servicetype;
	}

	public void setServicetype(String servicetype) {
		this.servicetype = servicetype;
	}

	public String getIsCustCaf() {
		return isCustCaf;
	}

	public void setIsCustCaf(String isCustCaf) {
		this.isCustCaf = isCustCaf;
	}

	public Integer getPreviousCafApprover() {
		return previousCafApprover;
	}

	public void setPreviousCafApprover(Integer previousCafApprover) {
		this.previousCafApprover = previousCafApprover;
	}

	public Integer getNextCafApprover() {
		return nextCafApprover;
	}

	public void setNextCafApprover(Integer nextCafApprover) {
		this.nextCafApprover = nextCafApprover;
	}

	public String getServiceareaName() {
		return serviceareaName;
	}

	public void setServiceareaName(String serviceareaName) {
		this.serviceareaName = serviceareaName;
	}

	public String getCafApproveStatus() {
		return cafApproveStatus;
	}

	public void setCafApproveStatus(String cafApproveStatus) {
		this.cafApproveStatus = cafApproveStatus;
	}

	public Long getMvnoId() {
		return mvnoId;
	}

	public void setMvnoId(Long mvnoId) {
		this.mvnoId = mvnoId;
	}

	public String getTinNo() {
		return tinNo;
	}

	public void setTinNo(String tinNo) {
		this.tinNo = tinNo;
	}

	public String getPassportNo() {
		return passportNo;
	}

	public void setPassportNo(String passportNo) {
		this.passportNo = passportNo;
	}

	public String getDunningCategory() {
		return dunningCategory;
	}

	public void setDunningCategory(String dunningCategory) {
		this.dunningCategory = dunningCategory;
	}

	public Integer getPlangroupid() {
		return plangroupid;
	}

	public void setPlangroupid(Integer plangroupid) {
		this.plangroupid = plangroupid;
	}

	public Integer getParentCustomerId() {
		return parentCustomerId;
	}

	public void setParentCustomerId(Integer parentCustomerId) {
		this.parentCustomerId = parentCustomerId;
	}

	public String getParentCustomerName() {
		return parentCustomerName;
	}

	public void setParentCustomerName(String parentCustomerName) {
		this.parentCustomerName = parentCustomerName;
	}

	public String getInvoiceType() {
		return invoiceType;
	}

	public void setInvoiceType(String invoiceType) {
		this.invoiceType = invoiceType;
	}

	public String getCalendarType() {
		return calendarType;
	}

	public void setCalendarType(String calendarType) {
		this.calendarType = calendarType;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Long getBuId() {
		return buId;
	}

	public void setBuId(Long buId) {
		this.buId = buId;
	}

	public LeadSource getLeadSource() {
		return leadSource;
	}

	public void setLeadSource(LeadSource leadSource) {
		this.leadSource = leadSource;
	}

	public Integer getLeadSubSourceId() {
		return leadSubSourceId;
	}

	public void setLeadSubSourceId(Integer leadSubSourceId) {
		this.leadSubSourceId = leadSubSourceId;
	}

	

	public Integer getRejectReasonId() {
		return rejectReasonId;
	}

	public void setRejectReasonId(Integer rejectReasonId) {
		this.rejectReasonId = rejectReasonId;
	}

	public Integer getRejectSubReasonId() {
		return rejectSubReasonId;
	}

	public void setRejectSubReasonId(Integer rejectSubReasonId) {
		this.rejectSubReasonId = rejectSubReasonId;
	}

	public String getReasonToChangeServiceProvider() {
		return reasonToChangeServiceProvider;
	}

	public void setReasonToChangeServiceProvider(String reasonToChangeServiceProvider) {
		this.reasonToChangeServiceProvider = reasonToChangeServiceProvider;
	}

	public String getPreviousVendor() {
		return previousVendor;
	}

	public void setPreviousVendor(String previousVendor) {
		this.previousVendor = previousVendor;
	}

	public String getServicerType() {
		return servicerType;
	}

	public void setServicerType(String servicerType) {
		this.servicerType = servicerType;
	}

	public String getLeadStatus() {
		return leadStatus;
	}

	public void setLeadStatus(String leadStatus) {
		this.leadStatus = leadStatus;
	}

	public LocalDateTime getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDateTime createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDateTime getLastModifiedOn() {
		return lastModifiedOn;
	}

	public void setLastModifiedOn(LocalDateTime lastModifiedOn) {
		this.lastModifiedOn = lastModifiedOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		return lastModifiedBy;
	}

	public void setLastModifiedBy(String lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}

	public LocalDateTime getRejectedOn() {
		return rejectedOn;
	}

	public void setRejectedOn(LocalDateTime rejectedOn) {
		this.rejectedOn = rejectedOn;
	}

	public String getRejectedBy() {
		return rejectedBy;
	}

	public void setRejectedBy(String rejectedBy) {
		this.rejectedBy = rejectedBy;
	}

	public LocalDateTime getApprovedOn() {
		return approvedOn;
	}

	public void setApprovedOn(LocalDateTime approvedOn) {
		this.approvedOn = approvedOn;
	}

	public String getApprovedBy() {
		return approvedBy;
	}

	public void setApprovedBy(String approvedBy) {
		this.approvedBy = approvedBy;
	}

	public LocalDateTime getReOpenOn() {
		return reOpenOn;
	}

	public void setReOpenOn(LocalDateTime reOpenOn) {
		this.reOpenOn = reOpenOn;
	}

	public String getReOpenBy() {
		return reOpenBy;
	}

	public void setReOpenBy(String reOpenBy) {
		this.reOpenBy = reOpenBy;
	}

	public Integer getNextApproveStaffId() {
		return nextApproveStaffId;
	}

	public void setNextApproveStaffId(Integer nextApproveStaffId) {
		this.nextApproveStaffId = nextApproveStaffId;
	}

	public Integer getNextTeamMappingId() {
		return nextTeamMappingId;
	}

	public void setNextTeamMappingId(Integer nextTeamMappingId) {
		this.nextTeamMappingId = nextTeamMappingId;
	}

	public String getLeadCategory() {
		return leadCategory;
	}

	public void setLeadCategory(String leadCategory) {
		this.leadCategory = leadCategory;
	}

	public String getHeardAboutSubisuFrom() {
		return heardAboutSubisuFrom;
	}

	public void setHeardAboutSubisuFrom(String heardAboutSubisuFrom) {
		this.heardAboutSubisuFrom = heardAboutSubisuFrom;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Customers getCustomers() {
		return customers;
	}

	public void setCustomers(Customers customers) {
		this.customers = customers;
	}

	public StaffUser getStaffUser() {
		return staffUser;
	}

	public void setStaffUser(StaffUser staffUser) {
		this.staffUser = staffUser;
	}

	public Branch getLeadBranch() {
		return leadBranch;
	}

	public void setLeadBranch(Branch leadBranch) {
		this.leadBranch = leadBranch;
	}

	public Long getLeadAgentId() {
		return leadAgentId;
	}

	public void setLeadAgentId(Long leadAgentId) {
		this.leadAgentId = leadAgentId;
	}

	public ServiceArea getServiceArea() {
		return serviceArea;
	}

	public void setServiceArea(ServiceArea serviceArea) {
		this.serviceArea = serviceArea;
	}

	public String getFeasibility() {
		return feasibility;
	}

	public void setFeasibility(String feasibility) {
		this.feasibility = feasibility;
	}

	public String getFeasibilityRemark() {
		return feasibilityRemark;
	}

	public void setFeasibilityRemark(String feasibilityRemark) {
		this.feasibilityRemark = feasibilityRemark;
	}

	public String getFeasibilityRequired() {
		return feasibilityRequired;
	}

	public void setFeasibilityRequired(String feasibilityRequired) {
		this.feasibilityRequired = feasibilityRequired;
	}

	public LocalDateTime getRejectLeadTime() {
		return rejectLeadTime;
	}

	public void setRejectLeadTime(LocalDateTime rejectLeadTime) {
		this.rejectLeadTime = rejectLeadTime;
	}

	public String getLeadType() {
		return leadType;
	}

	public void setLeadType(String leadType) {
		this.leadType = leadType;
	}

	public Long getExistingCustomerId() {
		return existingCustomerId;
	}

	public void setExistingCustomerId(Long existingCustomerId) {
		this.existingCustomerId = existingCustomerId;
	}

	public boolean isFinalApproved() {
		return finalApproved;
	}

	public void setFinalApproved(boolean finalApproved) {
		this.finalApproved = finalApproved;
	}

	public String getPlanType() {
		return planType;
	}

	public void setPlanType(String planType) {
		this.planType = planType;
	}

	public String getLeadNo() {
		return leadNo;
	}

	public void setLeadNo(String leadNo) {
		this.leadNo = leadNo;
	}

	public boolean isPresentCheckForPayment() {
		return presentCheckForPayment;
	}

	public void setPresentCheckForPayment(boolean presentCheckForPayment) {
		this.presentCheckForPayment = presentCheckForPayment;
	}

	public boolean isPresentCheckForPermanent() {
		return presentCheckForPermanent;
	}

	public void setPresentCheckForPermanent(boolean presentCheckForPermanent) {
		this.presentCheckForPermanent = presentCheckForPermanent;
	}
	
	public LeadMaster(LeadMasterPojo leadMasterPojo) {
		this.id = leadMasterPojo.getId();
		this.mvnoId= leadMasterPojo.getMvnoId();
		this.username = leadMasterPojo.getUsername();
		this.password = leadMasterPojo.getPassword();
		this.firstname = leadMasterPojo.getFirstname();
		this.lastname = leadMasterPojo.getLastname();
		this.email = leadMasterPojo.getEmail();
		this.title = leadMasterPojo.getTitle();
		this.custname = leadMasterPojo.getCustname();
		this.contactperson = leadMasterPojo.getContactperson();
		this.pan = leadMasterPojo.getPan();
		this.gst = leadMasterPojo.getGst();
		this.aadhar = leadMasterPojo.getAadhar();
		this.status = leadMasterPojo.getStatus();
		this.failcount = leadMasterPojo.getFailcount();
		this.acctno = leadMasterPojo.getAcctno();
		this.custtype = leadMasterPojo.getCusttype();
		this.phone = leadMasterPojo.getPhone();
		this.billday = leadMasterPojo.getBillday();
		this.partnerid = leadMasterPojo.getPartnerid();
		this.onuid = leadMasterPojo.getOnuid();
		this.nextBillDate = leadMasterPojo.getNextBillDate();
		this.lastBillDate = leadMasterPojo.getLastBillDate();
		this.addresstype = leadMasterPojo.getAddresstype();
		this.address1 = leadMasterPojo.getAddress1();
		this.address2 = leadMasterPojo.getAddress2();
		this.city = leadMasterPojo.getCity();
		this.state = leadMasterPojo.getState();
		this.country = leadMasterPojo.getCountry();
		this.pincode = leadMasterPojo.getPincode();
		this.area = leadMasterPojo.getArea();
		this.outstanding = leadMasterPojo.getOutstanding();
		this.oldpassword1 = leadMasterPojo.getOldpassword1();
		this.oldpassword2 = leadMasterPojo.getOldpassword2();
		this.oldpassword3 = leadMasterPojo.getOldpassword3();
		this.selfcarepwd = leadMasterPojo.getSelfcarepwd();
		this.createdBy = leadMasterPojo.getCreatedBy();
		this.last_password_change = leadMasterPojo.getLast_password_change();
		this.lastpasswordchangestring = leadMasterPojo.getLastpasswordchangestring();
		this.nextApproveStaffId=leadMasterPojo.getNextApproveStaffId();
		this.nextTeamMappingId=leadMasterPojo.getNextTeamMappingId();
		this.slaTime=leadMasterPojo.getSlaTime();
		this.slaUnit=leadMasterPojo.getSlaUnit();
		if(leadMasterPojo.getNextfollowuptime()!=null){
			this.nextfollowuptime=leadMasterPojo.getNextfollowuptime();
		}
		if(leadMasterPojo.getNextfollowupdate()!=null){
		this.nextfollowupdate=leadMasterPojo.getNextfollowupdate();
		}


		if (leadMasterPojo.getPlanMappingList() != null && leadMasterPojo.getPlanMappingList().size() > 0) {
			List<LeadCustPlanMappping> leadCustPlanMapppingList = new ArrayList<LeadCustPlanMappping>();
			for (LeadCustPlanMapppingPojo leadCustPlanMapppingPojo : leadMasterPojo.getPlanMappingList()) {
				leadCustPlanMapppingList.add(new LeadCustPlanMappping(leadCustPlanMapppingPojo));
			}
			this.planMappingList = leadCustPlanMapppingList;
		}
		if (leadMasterPojo.getAddressList() != null && leadMasterPojo.getAddressList().size() > 0) {
			List<LeadCustomerAddress> leadCustomerAddressList = new ArrayList<LeadCustomerAddress>();
			for (LeadCustomerAddressPojo leadCustomerAddressPojo : leadMasterPojo.getAddressList()) {
				leadCustomerAddressList.add(new LeadCustomerAddress(leadCustomerAddressPojo));
			}
			this.addressList = leadCustomerAddressList;
		}
		if (leadMasterPojo.getRadiusprofileIds() != null && leadMasterPojo.getRadiusprofileIds().size() > 0) {
			List<String> Ids = leadMasterPojo.getRadiusprofileIds().stream().map(e -> String.valueOf(e))
					.collect(Collectors.toList());
			this.radiusprofileIds = String.join(",", Ids);
		}
		
		if (leadMasterPojo.getOverChargeList() != null && leadMasterPojo.getOverChargeList().size() > 0) {
			List<LeadCustChargeDetails> leadCustChargeDetailsList = new ArrayList<LeadCustChargeDetails>();
			for (LeadCustChargeDetailsPojo leadCustChargeDetailsPojo : leadMasterPojo.getOverChargeList()) {
				leadCustChargeDetailsList.add(new LeadCustChargeDetails(leadCustChargeDetailsPojo));
			}
			this.overChargeList = leadCustChargeDetailsList;
		}
		if (leadMasterPojo.getLeadDocDetailsList() != null && leadMasterPojo.getLeadDocDetailsList().size() > 0) {
			List<LeadDocDetails> leadDocDetailsList = new ArrayList<LeadDocDetails>();
			for (LeadDocDetailsDTO leadDocDetailsDTO : leadMasterPojo.getLeadDocDetailsList()) {
				leadDocDetailsList.add(new LeadDocDetails(leadDocDetailsDTO));
			}
			this.leadDocDetailsList = leadDocDetailsList;
		}
		if (leadMasterPojo.getCustMacMapppingList() != null && leadMasterPojo.getCustMacMapppingList().size() > 0) {
			List<LeadCustMacMappping> leadCustMacMapppingList = new ArrayList<LeadCustMacMappping>();
			for (LeadCustMacMapppingPojo leadCustMacMapppingPojo : leadMasterPojo.getCustMacMapppingList()) {
				leadCustMacMapppingList.add(new LeadCustMacMappping(leadCustMacMapppingPojo));
			}
			this.custMacMapppingList = leadCustMacMapppingList;
		}
		this.flashMsg = leadMasterPojo.getFlashMsg();
		this.mactelflag = leadMasterPojo.getMactelflag();
		this.mobile = leadMasterPojo.getMobile();
		this.countryCode = leadMasterPojo.getCountryCode();
		this.cafno = leadMasterPojo.getCafno();
		this.altmobile = leadMasterPojo.getAltmobile();
		this.altphone = leadMasterPojo.getAltphone();
		this.altemail = leadMasterPojo.getAltemail();
		this.fax = leadMasterPojo.getFax();
		this.resellerid = leadMasterPojo.getResellerid();
		this.salesrepid = leadMasterPojo.getSalesrepid();
		this.voicesrvtype = leadMasterPojo.getVoicesrvtype();
		this.voiceprovision = leadMasterPojo.getVoiceprovision();
		this.childdidno = leadMasterPojo.getChilddidno();
		this.didno = leadMasterPojo.getDidno();
		this.intercomno = leadMasterPojo.getIntercomno();
		this.intercomgrp = leadMasterPojo.getIntercomgrp();
		this.onlinerenewalflag = leadMasterPojo.getOnlinerenewalflag();
		this.voipenableflag = leadMasterPojo.getVoipenableflag();
		this.custcategory = leadMasterPojo.getCustcategory();
		this.walletbalance = leadMasterPojo.getWalletbalance();
		this.networktype = leadMasterPojo.getNetworktype();
		this.defaultpoolid = leadMasterPojo.getDefaultpoolid();
		this.serviceareaid = leadMasterPojo.getServiceareaid();
		this.networkdevicesid = leadMasterPojo.getNetworkdevicesid();
		this.oltslotid = leadMasterPojo.getOltslotid();
		this.oltportid = leadMasterPojo.getOltportid();
		this.strconntype = leadMasterPojo.getStrconntype();
		this.stroltname = leadMasterPojo.getStroltname();
		this.strslotname = leadMasterPojo.getStrslotname();
		this.strportname = leadMasterPojo.getStrportname();
		this.OldBNGRouterinterface = leadMasterPojo.getOldBNGRouterinterface();
		this.OldVSIName = leadMasterPojo.getOldVSIName();
		this.ASNNumber = leadMasterPojo.getASNNumber();
		this.BNGRouterinterface = leadMasterPojo.getBNGRouterinterface();
		this.BNGRoutername = leadMasterPojo.getBNGRoutername();
		this.IPPrefixes = leadMasterPojo.getIPPrefixes();
		this.IPV6Prefixes = leadMasterPojo.getIPV6Prefixes();
		this.LANIP = leadMasterPojo.getLANIP();
		this.LANIPV6 = leadMasterPojo.getLANIPV6();
		this.LLAccountid = leadMasterPojo.getLLAccountid();
		this.LLConnectiontype = leadMasterPojo.getLLConnectiontype();
		this.LLExpirydate = leadMasterPojo.getLLExpirydate();
		this.LLMedium = leadMasterPojo.getLLMedium();
		this.LLServiceid = leadMasterPojo.getLLServiceid();
		this.MACADDRESS = leadMasterPojo.getMACADDRESS();
		this.Peerip = leadMasterPojo.getPeerip();
		this.POOLIP = leadMasterPojo.getPOOLIP();
		this.QOS = leadMasterPojo.getQOS();
		this.RDExport = leadMasterPojo.getRDExport();
		this.RDValue = leadMasterPojo.getRDValue();
		this.VLANID = leadMasterPojo.getVLANID();
		this.VRFName = leadMasterPojo.getVRFName();
		this.VSIID = leadMasterPojo.getVSIID();	
		this.VSIName = leadMasterPojo.getVSIName();
		this.WANIP = leadMasterPojo.getWANIP();
		this.WANIPV6 = leadMasterPojo.getWANIPV6();
		this.billentityname = leadMasterPojo.getBillentityname();
		this.addparam1 = leadMasterPojo.getAddparam1();
		this.addparam2 = leadMasterPojo.getAddparam2();
		this.addparam3 = leadMasterPojo.getAddparam3();
		this.addparam4 = leadMasterPojo.getAddparam4();
		this.purchaseorder = leadMasterPojo.getPurchaseorder();
		this.remarks = leadMasterPojo.getRemarks();
		this.allowedIPAddress = leadMasterPojo.getAllowedIPAddress();
		this.OldWANIP = leadMasterPojo.getOldWANIP();
		this.OldLLAccountid = leadMasterPojo.getOldLLAccountid();
		this.firstActivationDate = leadMasterPojo.getFirstActivationDate();
		this.isDeleted = leadMasterPojo.isDeleted();
		this.createDateString = leadMasterPojo.getCreateDateString();
		this.updateDateString = leadMasterPojo.getUpdateDateString();
		this.latitude = leadMasterPojo.getLatitude();
		this.longitude = leadMasterPojo.getLongitude();
		this.url = leadMasterPojo.getUrl();
		this.gisCode = leadMasterPojo.getGisCode();
		this.salesremark = leadMasterPojo.getSalesremark();
		this.servicetype = leadMasterPojo.getServicetype();
		this.isCustCaf = leadMasterPojo.getIsCustCaf();
		this.previousCafApprover = leadMasterPojo.getPreviousCafApprover();
		this.nextCafApprover = leadMasterPojo.getNextCafApprover();
		this.serviceareaName  = leadMasterPojo.getServiceareaName();
		this.cafApproveStatus = leadMasterPojo.getCafApproveStatus();
		this.tinNo = leadMasterPojo.getTinNo();
		this.passportNo = leadMasterPojo.getPassportNo();
		this.dunningCategory = leadMasterPojo.getDunningCategory();
		this.plangroupid = leadMasterPojo.getPlangroupid();
		this.parentCustomerId = leadMasterPojo.getParentCustomerId();
		this.parentCustomerName = leadMasterPojo.getParentCustomerName();
		this.invoiceType = leadMasterPojo.getInvoiceType();
		this.calendarType = leadMasterPojo.getCalendarType();
		this.discount = leadMasterPojo.getDiscount();
		if(leadMasterPojo.getLeadSourcePojo() != null) {			
			this.leadSource = new LeadSource(leadMasterPojo.getLeadSourcePojo());
		}
		this.leadSubSourceId = leadMasterPojo.getLeadSubSourceId();
		this.rejectReasonId = leadMasterPojo.getRejectReasonId();
		this.rejectSubReasonId = leadMasterPojo.getRejectSubReasonId();
		this.reasonToChangeServiceProvider = leadMasterPojo.getReasonToChangeServiceProvider();
		this.previousVendor = leadMasterPojo.getPreviousVendor();
		this.servicerType = leadMasterPojo.getServicerType();
		this.leadStatus = leadMasterPojo.getLeadStatus();
	    this.leadCategory = leadMasterPojo.getLeadCategory();
	    this.heardAboutSubisuFrom = leadMasterPojo.getHeardAboutSubisuFrom();
	    if(leadMasterPojo.getLeadPartnerId() != null) {
	    	this.partner = new Partner(leadMasterPojo.getLeadPartnerId());
	    }
	    if(leadMasterPojo.getLeadCustomersId() != null) {
	    	this.customers = new Customers(leadMasterPojo.getLeadCustomersId());
	    }
	    if(leadMasterPojo.getLeadStaffUserId() != null) {
	    	this.staffUser = new StaffUser(leadMasterPojo.getLeadStaffUserId());
	    }
	    if(leadMasterPojo.getLeadBranchId() != null) {
	    	this.leadBranch = new Branch(leadMasterPojo.getLeadBranchId());
	    }
	    this.leadAgentId = leadMasterPojo.getLeadAgentId();
	    if(leadMasterPojo.getLeadServiceAreaId() != null) {	    	
	    	this.serviceArea = new ServiceArea(leadMasterPojo.getLeadServiceAreaId());
	    }
	    this.feasibility = leadMasterPojo.getFeasibility();
	    this.feasibilityRemark = leadMasterPojo.getFeasibilityRemark();
	    this.feasibilityRequired = leadMasterPojo.getFeasibilityRequired();
	    this.rejectLeadTime = leadMasterPojo.getRejectLeadTime();
	    this.leadType = leadMasterPojo.getLeadType();
	    this.existingCustomerId = leadMasterPojo.getExistingCustomerId();
	    this.finalApproved = leadMasterPojo.isFinalApproved();
	    this.planType = leadMasterPojo.getPlanType();
	    this.buId = leadMasterPojo.getBuId();
	    this.nextApproveStaffId = leadMasterPojo.getNextApproveStaffId();
		this.leadNo = leadMasterPojo.getLeadNo();
		this.nextTeamMappingId = leadMasterPojo.getNextTeamMappingId();
		this.presentCheckForPayment = leadMasterPojo.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMasterPojo.isPresentCheckForPermanent();
		this.dateOfBirth = leadMasterPojo.getDateOfBirth();
		this.secondaryContactDetails = leadMasterPojo.getSecondaryContactDetails();
		this.secondaryPhone = leadMasterPojo.getSecondaryPhone();
		this.secondaryEmail = leadMasterPojo.getSecondaryEmail();
		this.previousAmount = leadMasterPojo.getPreviousAmount();
		this.previousMonth = leadMasterPojo.getPreviousMonth();
		this.leadOriginType = leadMasterPojo.getLeadOriginType();
		this.requireServiceType = leadMasterPojo.getRequireServiceType();
		this.landlineNumber = leadMasterPojo.getLandlineNumber();
		this.pcontactphno = leadMasterPojo.getPcontactphno();
		this.scontactname = leadMasterPojo.getScontactname();
		this.businessverticals = leadMasterPojo.getBusinessverticals();
		this.subbusinessverticals = leadMasterPojo.getSubbusinessverticals();
		this.connectiontype = leadMasterPojo.getConnectiontype();
		this.linktype = leadMasterPojo.getLinktype();
		this.circuitarea = leadMasterPojo.getCircuitarea();
		this.closuredate = leadMasterPojo.getClosuredate();
		this.circuitid = leadMasterPojo.getCircuitid();
		this.circuitname = leadMasterPojo.getCircuitname();
		this.leadvariety = leadMasterPojo.getLeadvariety();
		this.altmobile1=leadMasterPojo.getAltmobile1();
		this.altmobile2=leadMasterPojo.getAltmobile2();
		this.altmobile3=leadMasterPojo.getAltmobile3();
		this.altmobile4=leadMasterPojo.getAltmobile4();
		this.blockNo = leadMasterPojo.getBlockNo();
		if(leadMasterPojo.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMasterPojo.getIsLeadQuickInv()==true?1:0;
		if(leadMasterPojo.getLeadDepartment()!=null){
			this.leadDepartment = leadMasterPojo.getLeadDepartment();
		}
	}
	
	public LeadMaster(LeadMasterPojo leadMasterPojo,Long id) {
		this.id = leadMasterPojo.getId();
		this.mvnoId = leadMasterPojo.getMvnoId();
		this.username = leadMasterPojo.getUsername();
		this.password = leadMasterPojo.getPassword();
		this.firstname = leadMasterPojo.getFirstname();
		this.lastname = leadMasterPojo.getLastname();
		this.email = leadMasterPojo.getEmail();
		this.title = leadMasterPojo.getTitle();
		this.custname = leadMasterPojo.getCustname();
		this.contactperson = leadMasterPojo.getContactperson();
		this.pan = leadMasterPojo.getPan();
		this.gst = leadMasterPojo.getGst();
		this.aadhar = leadMasterPojo.getAadhar();
		this.status = leadMasterPojo.getStatus();
		this.failcount = leadMasterPojo.getFailcount();
		this.acctno = leadMasterPojo.getAcctno();
		this.custtype = leadMasterPojo.getCusttype();
		this.phone = leadMasterPojo.getPhone();
		this.billday = leadMasterPojo.getBillday();
		this.partnerid = leadMasterPojo.getPartnerid();
		this.onuid = leadMasterPojo.getOnuid();
		this.nextBillDate = leadMasterPojo.getNextBillDate();
		this.lastBillDate = leadMasterPojo.getLastBillDate();
		this.addresstype = leadMasterPojo.getAddresstype();
		this.address1 = leadMasterPojo.getAddress1();
		this.address2 = leadMasterPojo.getAddress2();
		this.city = leadMasterPojo.getCity();
		this.state = leadMasterPojo.getState();
		this.country = leadMasterPojo.getCountry();
		this.pincode = leadMasterPojo.getPincode();
		this.area = leadMasterPojo.getArea();
		this.outstanding = leadMasterPojo.getOutstanding();
		this.oldpassword1 = leadMasterPojo.getOldpassword1();
		this.oldpassword2 = leadMasterPojo.getOldpassword2();
		this.oldpassword3 = leadMasterPojo.getOldpassword3();
		this.selfcarepwd = leadMasterPojo.getSelfcarepwd();
		this.createdBy = leadMasterPojo.getCreatedBy();
		this.last_password_change = leadMasterPojo.getLast_password_change();
		this.lastpasswordchangestring = leadMasterPojo.getLastpasswordchangestring();
		this.nextApproveStaffId=leadMasterPojo.getNextApproveStaffId();
		this.nextTeamMappingId=leadMasterPojo.getNextTeamMappingId();
		if (leadMasterPojo.getRadiusprofileIds() != null && leadMasterPojo.getRadiusprofileIds().size() > 0) {
			List<String> Ids = leadMasterPojo.getRadiusprofileIds().stream().map(e -> String.valueOf(e))
					.collect(Collectors.toList());
			this.radiusprofileIds = String.join(",", Ids);
		}
		this.flashMsg = leadMasterPojo.getFlashMsg();
		this.mactelflag = leadMasterPojo.getMactelflag();
		this.mobile = leadMasterPojo.getMobile();
		this.countryCode = leadMasterPojo.getCountryCode();
		this.cafno = leadMasterPojo.getCafno();
		this.altmobile = leadMasterPojo.getAltmobile();
		this.altphone = leadMasterPojo.getAltphone();
		this.altemail = leadMasterPojo.getAltemail();
		this.fax = leadMasterPojo.getFax();
		this.resellerid = leadMasterPojo.getResellerid();
		this.salesrepid = leadMasterPojo.getSalesrepid();
		this.voicesrvtype = leadMasterPojo.getVoicesrvtype();
		this.voiceprovision = leadMasterPojo.getVoiceprovision();
		this.childdidno = leadMasterPojo.getChilddidno();
		this.didno = leadMasterPojo.getDidno();
		this.intercomno = leadMasterPojo.getIntercomno();
		this.intercomgrp = leadMasterPojo.getIntercomgrp();
		this.onlinerenewalflag = leadMasterPojo.getOnlinerenewalflag();
		this.voipenableflag = leadMasterPojo.getVoipenableflag();
		this.custcategory = leadMasterPojo.getCustcategory();
		this.walletbalance = leadMasterPojo.getWalletbalance();
		this.networktype = leadMasterPojo.getNetworktype();
		this.defaultpoolid = leadMasterPojo.getDefaultpoolid();
		this.serviceareaid = leadMasterPojo.getServiceareaid();
		this.networkdevicesid = leadMasterPojo.getNetworkdevicesid();
		this.oltslotid = leadMasterPojo.getOltslotid();
		this.oltportid = leadMasterPojo.getOltportid();
		this.strconntype = leadMasterPojo.getStrconntype();
		this.stroltname = leadMasterPojo.getStroltname();
		this.strslotname = leadMasterPojo.getStrslotname();
		this.strportname = leadMasterPojo.getStrportname();
		this.OldBNGRouterinterface = leadMasterPojo.getOldBNGRouterinterface();
		this.OldVSIName = leadMasterPojo.getOldVSIName();
		this.ASNNumber = leadMasterPojo.getASNNumber();
		this.BNGRouterinterface = leadMasterPojo.getBNGRouterinterface();
		this.BNGRoutername = leadMasterPojo.getBNGRoutername();
		this.IPPrefixes = leadMasterPojo.getIPPrefixes();
		this.IPV6Prefixes = leadMasterPojo.getIPV6Prefixes();
		this.LANIP = leadMasterPojo.getLANIP();
		this.LANIPV6 = leadMasterPojo.getLANIPV6();
		this.LLAccountid = leadMasterPojo.getLLAccountid();
		this.LLConnectiontype = leadMasterPojo.getLLConnectiontype();
		this.LLExpirydate = leadMasterPojo.getLLExpirydate();
		this.LLMedium = leadMasterPojo.getLLMedium();
		this.LLServiceid = leadMasterPojo.getLLServiceid();
		this.MACADDRESS = leadMasterPojo.getMACADDRESS();
		this.Peerip = leadMasterPojo.getPeerip();
		this.POOLIP = leadMasterPojo.getPOOLIP();
		this.QOS = leadMasterPojo.getQOS();
		this.RDExport = leadMasterPojo.getRDExport();
		this.RDValue = leadMasterPojo.getRDValue();
		this.VLANID = leadMasterPojo.getVLANID();
		this.VRFName = leadMasterPojo.getVRFName();
		this.VSIID = leadMasterPojo.getVSIID();	
		this.VSIName = leadMasterPojo.getVSIName();
		this.WANIP = leadMasterPojo.getWANIP();
		this.WANIPV6 = leadMasterPojo.getWANIPV6();
		this.billentityname = leadMasterPojo.getBillentityname();
		this.addparam1 = leadMasterPojo.getAddparam1();
		this.addparam2 = leadMasterPojo.getAddparam2();
		this.addparam3 = leadMasterPojo.getAddparam3();
		this.addparam4 = leadMasterPojo.getAddparam4();
		this.purchaseorder = leadMasterPojo.getPurchaseorder();
		this.remarks = leadMasterPojo.getRemarks();
		this.allowedIPAddress = leadMasterPojo.getAllowedIPAddress();
		this.OldWANIP = leadMasterPojo.getOldWANIP();
		this.OldLLAccountid = leadMasterPojo.getOldLLAccountid();
		this.firstActivationDate = leadMasterPojo.getFirstActivationDate();
		this.isDeleted = leadMasterPojo.isDeleted();
		this.createDateString = leadMasterPojo.getCreateDateString();
		this.updateDateString = leadMasterPojo.getUpdateDateString();
		this.latitude = leadMasterPojo.getLatitude();
		this.longitude = leadMasterPojo.getLongitude();
		this.url = leadMasterPojo.getUrl();
		this.gisCode = leadMasterPojo.getGisCode();
		this.salesremark = leadMasterPojo.getSalesremark();
		this.servicetype = leadMasterPojo.getServicetype();
		this.isCustCaf = leadMasterPojo.getIsCustCaf();
		this.previousCafApprover = leadMasterPojo.getPreviousCafApprover();
		this.nextCafApprover = leadMasterPojo.getNextCafApprover();
		this.serviceareaName  = leadMasterPojo.getServiceareaName();
		this.cafApproveStatus = leadMasterPojo.getCafApproveStatus();
		this.tinNo = leadMasterPojo.getTinNo();
		this.passportNo = leadMasterPojo.getPassportNo();
		this.dunningCategory = leadMasterPojo.getDunningCategory();
		this.plangroupid = leadMasterPojo.getPlangroupid();
		this.parentCustomerId = leadMasterPojo.getParentCustomerId();
		this.parentCustomerName = leadMasterPojo.getParentCustomerName();
		this.invoiceType = leadMasterPojo.getInvoiceType();
		this.calendarType = leadMasterPojo.getCalendarType();
		this.discount = leadMasterPojo.getDiscount();
		if(leadMasterPojo.getLeadSourcePojo() != null) {			
			this.leadSource = new LeadSource(leadMasterPojo.getLeadSourcePojo());
		}
		this.leadSubSourceId = leadMasterPojo.getLeadSubSourceId();
		this.rejectReasonId = leadMasterPojo.getRejectReasonId();
		this.rejectSubReasonId = leadMasterPojo.getRejectSubReasonId();
		this.reasonToChangeServiceProvider = leadMasterPojo.getReasonToChangeServiceProvider();
		this.previousVendor = leadMasterPojo.getPreviousVendor();
		this.servicerType = leadMasterPojo.getServicerType();
		this.leadStatus = leadMasterPojo.getLeadStatus();
	    this.leadCategory = leadMasterPojo.getLeadCategory();
	    this.heardAboutSubisuFrom = leadMasterPojo.getHeardAboutSubisuFrom();
	    if(leadMasterPojo.getLeadPartnerId() != null) {
	    	this.partner = new Partner(leadMasterPojo.getLeadPartnerId());
	    }
	    if(leadMasterPojo.getLeadCustomersId() != null) {
	    	this.customers = new Customers(leadMasterPojo.getLeadCustomersId());
	    }
	    if(leadMasterPojo.getLeadStaffUserId() != null) {
	    	this.staffUser = new StaffUser(leadMasterPojo.getLeadStaffUserId());
	    }
	    if(leadMasterPojo.getLeadBranchId() != null) {
	    	this.leadBranch = new Branch(leadMasterPojo.getLeadBranchId());
	    }
	    this.leadAgentId = leadMasterPojo.getLeadAgentId();
	    if(leadMasterPojo.getLeadServiceAreaId() != null) {	    	
	    	this.serviceArea = new ServiceArea(leadMasterPojo.getLeadServiceAreaId());
	    }
	    this.feasibility = leadMasterPojo.getFeasibility();
	    this.feasibilityRemark = leadMasterPojo.getFeasibilityRemark();
	    this.feasibilityRequired = leadMasterPojo.getFeasibilityRequired();
	    this.rejectLeadTime = leadMasterPojo.getRejectLeadTime();
	    this.leadType = leadMasterPojo.getLeadType();
	    this.existingCustomerId = leadMasterPojo.getExistingCustomerId();
	    this.finalApproved = leadMasterPojo.isFinalApproved();
	    this.planType = leadMasterPojo.getPlanType();
	    this.buId = leadMasterPojo.getBuId();
	    this.nextApproveStaffId = leadMasterPojo.getNextApproveStaffId();
		this.leadNo = leadMasterPojo.getLeadNo();
		this.nextTeamMappingId = leadMasterPojo.getNextTeamMappingId();
		this.presentCheckForPayment = leadMasterPojo.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMasterPojo.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMasterPojo.getLeadCustomerCategory();
		this.leadCustomerType = leadMasterPojo.getLeadCustomerType();
		this.leadCustomerSubType = leadMasterPojo.getLeadCustomerSubType();
		this.leadCustomerSector = leadMasterPojo.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMasterPojo.getLeadCustomerSubSector();
		this.valleyType = leadMasterPojo.getValleyType();
		this.insideValley = leadMasterPojo.getInsideValley();
		this.outsideValley = leadMasterPojo.getOutsideValley();
		this.competitorDuration = leadMasterPojo.getCompetitorDuration();
		this.expiry = leadMasterPojo.getExpiry();
		this.amount = leadMasterPojo.getAmount();
		this.feedback = leadMasterPojo.getFeedback();
		this.gender = leadMasterPojo.getGender();
		if(leadMasterPojo.getBranchId() != null) {	    	
	    	this.branch = new Branch(leadMasterPojo.getBranchId());
	    }
		if(leadMasterPojo.getPopManagementId() != null) {	    	
			this.popManagement = new PopManagement(leadMasterPojo.getPopManagementId());
		}
		this.dateOfBirth = leadMasterPojo.getDateOfBirth();
		this.secondaryContactDetails = leadMasterPojo.getSecondaryContactDetails();
		this.secondaryPhone = leadMasterPojo.getSecondaryPhone();
		this.secondaryEmail = leadMasterPojo.getSecondaryEmail();
		this.previousAmount = leadMasterPojo.getPreviousAmount();
		this.previousMonth = leadMasterPojo.getPreviousMonth();
		this.leadOriginType = leadMasterPojo.getLeadOriginType();
		this.requireServiceType = leadMasterPojo.getRequireServiceType();
		this.landlineNumber = leadMasterPojo.getLandlineNumber();
		this.pcontactphno = leadMasterPojo.getPcontactphno();
		this.scontactname = leadMasterPojo.getScontactname();
		this.businessverticals = leadMasterPojo.getBusinessverticals();
		this.subbusinessverticals = leadMasterPojo.getSubbusinessverticals();
		this.connectiontype = leadMasterPojo.getConnectiontype();
		this.linktype = leadMasterPojo.getLinktype();
		this.circuitarea = leadMasterPojo.getCircuitarea();
		this.closuredate = leadMasterPojo.getClosuredate();
		this.circuitid = leadMasterPojo.getCircuitid();
		this.circuitname = leadMasterPojo.getCircuitname();
		this.leadvariety = leadMasterPojo.getLeadvariety();
		this.altmobile1=leadMasterPojo.getAltmobile1();
		this.altmobile2=leadMasterPojo.getAltmobile2();
		this.altmobile3=leadMasterPojo.getAltmobile3();
		this.altmobile4=leadMasterPojo.getAltmobile4();
		this.blockNo = leadMasterPojo.getBlockNo();
		if(leadMasterPojo.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMasterPojo.getIsLeadQuickInv()==true?1:0;
		if(leadMasterPojo.getLeadDepartment()!=null){
			this.leadDepartment = leadMasterPojo.getLeadDepartment();
		}
	}

	public String getLeadCustomerCategory() {
		return leadCustomerCategory;
	}

	public void setLeadCustomerCategory(String leadCustomerCategory) {
		this.leadCustomerCategory = leadCustomerCategory;
	}

	public String getLeadCustomerType() {
		return leadCustomerType;
	}

	public void setLeadCustomerType(String leadCustomerType) {
		this.leadCustomerType = leadCustomerType;
	}

	public String getLeadCustomerSubType() {
		return leadCustomerSubType;
	}

	public void setLeadCustomerSubType(String leadCustomerSubType) {
		this.leadCustomerSubType = leadCustomerSubType;
	}

	public String getLeadCustomerSector() {
		return leadCustomerSector;
	}

	public void setLeadCustomerSector(String leadCustomerSector) {
		this.leadCustomerSector = leadCustomerSector;
	}

	public String getLeadCustomerSubSector() {
		return leadCustomerSubSector;
	}

	public void setLeadCustomerSubSector(String leadCustomerSubSector) {
		this.leadCustomerSubSector = leadCustomerSubSector;
	}

	public String getValleyType() {
		return valleyType;
	}

	public void setValleyType(String valleyType) {
		this.valleyType = valleyType;
	}

	public String getInsideValley() {
		return insideValley;
	}

	public void setInsideValley(String insideValley) {
		this.insideValley = insideValley;
	}

	public String getOutsideValley() {
		return outsideValley;
	}

	public void setOutsideValley(String outsideValley) {
		this.outsideValley = outsideValley;
	}

	public String getCompetitorDuration() {
		return competitorDuration;
	}

	public void setCompetitorDuration(String competitorDuration) {
		this.competitorDuration = competitorDuration;
	}

	public LocalDate getExpiry() {
		return expiry;
	}

	public void setExpiry(LocalDate expiry) {
		this.expiry = expiry;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Branch getBranch() {
		return branch;
	}

	public void setBranch(Branch branch) {
		this.branch = branch;
	}

	public PopManagement getPopManagement() {
		return popManagement;
	}

	public void setPopManagement(PopManagement popManagement) {
		this.popManagement = popManagement;
	}

	public LocalDate getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(LocalDate dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getSecondaryContactDetails() {
		return secondaryContactDetails;
	}

	public void setSecondaryContactDetails(String secondaryContactDetails) {
		this.secondaryContactDetails = secondaryContactDetails;
	}

	public String getSecondaryPhone() {
		return secondaryPhone;
	}

	public void setSecondaryPhone(String secondaryPhone) {
		this.secondaryPhone = secondaryPhone;
	}

	public String getSecondaryEmail() {
		return secondaryEmail;
	}

	public void setSecondaryEmail(String secondaryEmail) {
		this.secondaryEmail = secondaryEmail;
	}

	public Double getPreviousAmount() {
		return previousAmount;
	}

	public void setPreviousAmount(Double previousAmount) {
		this.previousAmount = previousAmount;
	}

	public String getPreviousMonth() {
		return previousMonth;
	}

	public void setPreviousMonth(String previousMonth) {
		this.previousMonth = previousMonth;
	}

	public String getLeadOriginType() {
		return leadOriginType;
	}

	public void setLeadOriginType(String leadOriginType) {
		this.leadOriginType = leadOriginType;
	}

	public String getRequireServiceType() {
		return requireServiceType;
	}

	public void setRequireServiceType(String requireServiceType) {
		this.requireServiceType = requireServiceType;
	}

	public String getLandlineNumber() {
		return landlineNumber;
	}

	public void setLandlineNumber(String landlineNumber) {
		this.landlineNumber = landlineNumber;
	}

	public String getPcontactphno() {
		return pcontactphno;
	}

	public void setPcontactphno(String pcontactphno) {
		this.pcontactphno = pcontactphno;
	}

	public String getScontactname() {
		return scontactname;
	}

	public void setScontactname(String scontactname) {
		this.scontactname = scontactname;
	}

	public String getBusinessverticals() {
		return businessverticals;
	}

	public void setBusinessverticals(String businessverticals) {
		this.businessverticals = businessverticals;
	}

	public String getSubbusinessverticals() {
		return subbusinessverticals;
	}

	public void setSubbusinessverticals(String subbusinessverticals) {
		this.subbusinessverticals = subbusinessverticals;
	}

	public String getConnectiontype() {
		return connectiontype;
	}

	public void setConnectiontype(String connectiontype) {
		this.connectiontype = connectiontype;
	}

	public String getLinktype() {
		return linktype;
	}

	public void setLinktype(String linktype) {
		this.linktype = linktype;
	}

	public String getCircuitarea() {
		return circuitarea;
	}

	public void setCircuitarea(String circuitarea) {
		this.circuitarea = circuitarea;
	}

	public LocalDate getClosuredate() {
		return closuredate;
	}

	public void setClosuredate(LocalDate closuredate) {
		this.closuredate = closuredate;
	}

	public Long getCircuitid() {
		return circuitid;
	}

	public void setCircuitid(Long circuitid) {
		this.circuitid = circuitid;
	}

	public String getCircuitname() {
		return circuitname;
	}

	public void setCircuitname(String circuitname) {
		this.circuitname = circuitname;
	}

	public String getLeadvariety() {
		return leadvariety;
	}

	public void setLeadvariety(String leadvariety) {
		this.leadvariety = leadvariety;
	}

	public Integer getIsLeadQuickInv() {
		return isLeadQuickInv;
	}

	public void setIsLeadQuickInv(Integer isLeadQuickInv) {
		this.isLeadQuickInv = isLeadQuickInv;
	}

	public String getLeadDepartment() {
		return leadDepartment;
	}

	public void setLeadDepartment(String leadDepartment) {
		this.leadDepartment = leadDepartment;
	}


}
