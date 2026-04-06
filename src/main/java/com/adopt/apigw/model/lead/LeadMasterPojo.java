package com.adopt.apigw.model.lead;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.javers.core.metamodel.annotation.DiffIgnore;
import org.springframework.format.annotation.DateTimeFormat;

import com.adopt.apigw.pojo.LeadCustPlanMapppingPojo;
import com.adopt.apigw.rabbitMq.message.LeadCustChargeDetailsPojoMessage;
import com.adopt.apigw.rabbitMq.message.LeadCustMacMapppingPojoMessage;
import com.adopt.apigw.rabbitMq.message.LeadCustPlanMapppingPojoMessage;
import com.adopt.apigw.rabbitMq.message.LeadCustomerAddressPojoMessage;
import com.adopt.apigw.rabbitMq.message.LeadDocDetailsDTOMessage;
import com.adopt.apigw.rabbitMq.message.LeadMasterPojoMessage;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeadMasterPojo {

	private Long id;

	private String username;

	private String password;

	private String firstname;

	private String lastname;

	private String email;

	private String title;

	private String custname;

	private String contactperson;

	private String pan;

	private String gst;

	private String aadhar;

	private String status;

	private Integer failcount;

	private String acctno;

	private String custtype;

	private String phone;

	private Integer billday;

	private Integer partnerid;

	private String onuid;

	private LocalDate nextBillDate;

	private LocalDate lastBillDate;

	private String addresstype;

	private String address1;

	private String address2;

	private Integer city;

	private Integer state;

	private Integer country;

	private Integer pincode;

	private Integer area;

	private Double outstanding;

	private String oldpassword1;

	private String newpassword;

	private String oldpassword2;

	private String oldpassword3;

	private String selfcarepwd;

	private LocalDateTime last_password_change;

	private String lastpasswordchangestring;

	private List<LeadCustPlanMapppingPojo> planMappingList = new ArrayList<>();

	private List<LeadCustomerAddressPojo> addressList = new ArrayList<>();

	private List<Integer> radiusprofileIds = new ArrayList<>();

	private List<LeadCustChargeDetailsPojo> overChargeList = new ArrayList<>();

	private List<LeadCustChargeDetailsPojo> indiChargeList = new ArrayList<>();

	private List<LeadCustMacMapppingPojo> custMacMapppingList = new ArrayList<>();

	private List<LeadDocDetailsDTO> leadDocDetailsList = new ArrayList<>();

	private String flashMsg;

	private Boolean mactelflag;

	private String mobile;

	private String countryCode;

	private String cafno;

	private String altmobile;

	private String altphone;

	private String altemail;

	private String fax;

	private Integer resellerid;

	private Integer salesrepid;

	private String voicesrvtype;

	private Boolean voiceprovision;

	private String didno;

	private String childdidno;

	private String intercomno;

	private String intercomgrp;

	private Boolean onlinerenewalflag;

	private Boolean voipenableflag;

	private String custcategory;

	private Double walletbalance;

	private String networktype;

	private Long defaultpoolid;

	private Long serviceareaid;

	private Long networkdevicesid;

	private Long oltslotid;

	private Long oltportid;

	private String strconntype;

	private String stroltname;

	private String strslotname;

	private String strportname;

	private String OldBNGRouterinterface;

	private String OldVSIName;

	private String ASNNumber;

	private String BNGRouterinterface;

	private String BNGRoutername;

	private String IPPrefixes;

	private String IPV6Prefixes;

	private String LANIP;

	private String LANIPV6;

	private String LLAccountid;

	private String LLConnectiontype;

	private String LLExpirydate;

	private String LLMedium;

	private String LLServiceid;

	private String MACADDRESS;

	private String Peerip;

	private String POOLIP;

	private String QOS;

	private String RDExport;

	private String RDValue;

	private String VLANID;

	private String VRFName;

	private String VSIID;

	private String VSIName;

	private String WANIP;

	private String WANIPV6;

	private String billentityname;

	private String addparam1;

	private String addparam2;

	private String addparam3;

	private String addparam4;

	private String purchaseorder;

	private String remarks;

	private String allowedIPAddress;

	private String OldWANIP;

	private String OldLLAccountid;

	private LocalDateTime firstActivationDate;

	private boolean isDeleted = false;

	private String createDateString;

	private String updateDateString;

	private String latitude;

	private String longitude;

	private String url;

	private String gisCode;

	private String salesremark;

	private String servicetype;

	private String isCustCaf;

	private Integer previousCafApprover;

	private Integer nextCafApprover;

	private String serviceareaName;

	private String cafApproveStatus;

	private Long mvnoId;

	private String tinNo;

	private String passportNo;

	private String dunningCategory;

	private Integer plangroupid;

	private Integer parentCustomerId;

	private String parentCustomerName;

	private String invoiceType;

	private String calendarType;

	private Double discount;

	private Long buId;

	private LeadSourcePojo leadSourcePojo;

	private Integer leadSubSourceId;

	private Integer rejectReasonId;

	private Integer rejectSubReasonId;

	private String reasonToChangeServiceProvider;

	private String previousVendor;

	private String servicerType;

	private String leadStatus;

	private String createdBy;

	private String lastModifiedBy;

	private LocalDateTime rejectedOn;

	private String rejectedBy;

	private LocalDateTime approvedOn;

	private String approvedBy;

	private LocalDateTime reOpenOn;

	private String reOpenBy;

	private Integer nextApproveStaffId;

	private Integer nextTeamMappingId;

	private String leadCategory;

	private String heardAboutSubisuFrom;

	private Integer leadPartnerId;

	private Integer leadCustomersId;

	private Integer leadStaffUserId;

	private Long leadBranchId;

	private Long leadAgentId;

	private Long leadServiceAreaId;

	private String feasibility;

	private String feasibilityRemark;

	private String feasibilityRequired;

	private LocalDateTime rejectLeadTime;

	private String leadType;

	private Long existingCustomerId;

	private boolean finalApproved;

	private String planType;

	private String leadNo;

	private boolean presentCheckForPayment;

	private boolean presentCheckForPermanent;

	private String leadCustomerCategory;

	private String leadCustomerType;

	private String leadCustomerSubType;

	private String leadCustomerSector;

	private String leadCustomerSubSector;

	private String valleyType;

	private String insideValley;

	private String outsideValley;

	private String competitorDuration;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate expiry;

	private Double amount;

	private String feedback;

	private String gender;

	private Long branchId;

	private Long popManagementId;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dateOfBirth;
	
	private String secondaryContactDetails;
	
	private String secondaryPhone;
	
	private String secondaryEmail;

	private Double previousAmount;
	
	private String previousMonth;
	
	private String leadOriginType;
	
	private String requireServiceType;
	
	private String landlineNumber;

	private String pcontactphno;
	private String scontactname;

	private String businessverticals;

	private String subbusinessverticals;

	private String connectiontype;

	private String linktype;

	private String circuitarea;

	private LocalDate closuredate;

	private Long circuitid;

	private String circuitname;

	private String leadvariety;


    private String altmobile1;


    private String altmobile2;


    private String altmobile3;


    private String altmobile4;

	private Integer currentLoggedInStaffId;

	private Boolean isLeadQuickInv;

	private String leadDepartment;

	private Integer slaTime;

	private String slaUnit;

	private LocalDate nextfollowupdate;

	private LocalTime nextfollowuptime;

	private Boolean isLeadFromCWSC;

	private String blockNo;

	public boolean isLeadFromCWSC() {
		return isLeadFromCWSC;
	}

	public void setLeadFromCWSC(boolean leadFromCWSC) {
		isLeadFromCWSC = leadFromCWSC;
	}


	
	public LeadMasterPojo(LeadMaster leadMaster) {
		this.id = leadMaster.getId();
		this.mvnoId = leadMaster.getMvnoId();
		this.username = leadMaster.getUsername();
		this.password = leadMaster.getPassword();
		this.firstname = leadMaster.getFirstname();
		this.lastname = leadMaster.getLastname();
		this.email = leadMaster.getEmail();
		this.title = leadMaster.getTitle();
		this.custname = leadMaster.getCustname();
		this.contactperson = leadMaster.getContactperson();
		this.pan = leadMaster.getPan();
		this.gst = leadMaster.getGst();
		this.aadhar = leadMaster.getAadhar();
		this.status = leadMaster.getStatus();
		this.failcount = leadMaster.getFailcount();
		this.acctno = leadMaster.getAcctno();
		this.custtype = leadMaster.getCusttype();
		this.phone = leadMaster.getPhone();
		this.billday = leadMaster.getBillday();
		this.partnerid = leadMaster.getPartnerid();
		this.onuid = leadMaster.getOnuid();
		this.nextBillDate = leadMaster.getNextBillDate();
		this.lastBillDate = leadMaster.getLastBillDate();
		this.addresstype = leadMaster.getAddresstype();
		this.address1 = leadMaster.getAddress1();
		this.address2 = leadMaster.getAddress2();
		this.city = leadMaster.getCity();
		this.state = leadMaster.getState();
		this.country = leadMaster.getCountry();
		this.pincode = leadMaster.getPincode();
		this.area = leadMaster.getArea();
		this.outstanding = leadMaster.getOutstanding();
		this.oldpassword1 = leadMaster.getOldpassword1();
		this.oldpassword2 = leadMaster.getOldpassword2();
		this.oldpassword3 = leadMaster.getOldpassword3();
		this.selfcarepwd = leadMaster.getSelfcarepwd();
		this.createdBy = leadMaster.getCreatedBy();
		this.last_password_change = leadMaster.getLast_password_change();
		this.lastpasswordchangestring = leadMaster.getLastpasswordchangestring();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.slaUnit=leadMaster.getSlaUnit();
		this.slaTime=leadMaster.getSlaTime();
		this.nextfollowupdate=leadMaster.getNextfollowupdate();
		this.nextfollowuptime=leadMaster.getNextfollowuptime();
		if (leadMaster.getPlanMappingList() != null && leadMaster.getPlanMappingList().size() > 0) {
			List<LeadCustPlanMapppingPojo> leadCustPlanMapppingPojoList = new ArrayList<LeadCustPlanMapppingPojo>();
			for (LeadCustPlanMappping leadCustPlanMappping : leadMaster.getPlanMappingList()) {
				leadCustPlanMapppingPojoList.add(new LeadCustPlanMapppingPojo(leadCustPlanMappping));
			}
			this.planMappingList = leadCustPlanMapppingPojoList;
		}
		if (leadMaster.getAddressList() != null && leadMaster.getAddressList().size() > 0) {
			List<LeadCustomerAddressPojo> leadCustomerAddressPojoList = new ArrayList<LeadCustomerAddressPojo>();
			for (LeadCustomerAddress leadCustomerAddress : leadMaster.getAddressList()) {
				leadCustomerAddressPojoList.add(new LeadCustomerAddressPojo(leadCustomerAddress));
			}
			this.addressList = leadCustomerAddressPojoList;
		}
		if (leadMaster.getRadiusprofileIds() != null && !leadMaster.getRadiusprofileIds().equalsIgnoreCase("")) {
			List<String> Ids = Lists.newArrayList(Splitter.on(" , ").split(leadMaster.getRadiusprofileIds()));
			List<Integer> radiusProfileIds = Ids.stream().map(n -> Integer.parseInt(n)).collect(Collectors.toList());
			this.radiusprofileIds = radiusProfileIds;
		}

		if (leadMaster.getOverChargeList() != null && leadMaster.getOverChargeList().size() > 0) {
			List<LeadCustChargeDetailsPojo> leadCustChargeDetailsPojoList = new ArrayList<LeadCustChargeDetailsPojo>();
			for (LeadCustChargeDetails leadCustChargeDetails : leadMaster.getOverChargeList()) {
				leadCustChargeDetailsPojoList.add(new LeadCustChargeDetailsPojo(leadCustChargeDetails));
			}
			this.overChargeList = leadCustChargeDetailsPojoList;
		}
//		if (leadMaster.getLeadDocDetailsList() != null && leadMaster.getLeadDocDetailsList().size() > 0) {
//			List<LeadDocDetailsDTO> leadDocDetailsDTOList = new ArrayList<LeadDocDetailsDTO>();
//			for (LeadDocDetails leadDocDetails : leadMaster.getLeadDocDetailsList()) {
//				leadDocDetailsDTOList.add(new LeadDocDetailsDTO(leadDocDetails));
//			}
//			this.leadDocDetailsList = leadDocDetailsDTOList;
//		}
		if (leadMaster.getCustMacMapppingList() != null && leadMaster.getCustMacMapppingList().size() > 0) {
			List<LeadCustMacMapppingPojo> leadCustMacMapppingPojoList = new ArrayList<LeadCustMacMapppingPojo>();
			for (LeadCustMacMappping leadCustMacMappping : leadMaster.getCustMacMapppingList()) {
				leadCustMacMapppingPojoList.add(new LeadCustMacMapppingPojo(leadCustMacMappping));
			}
			this.custMacMapppingList = leadCustMacMapppingPojoList;
		}
		this.flashMsg = leadMaster.getFlashMsg();
		this.mactelflag = leadMaster.getMactelflag();
		this.mobile = leadMaster.getMobile();
		this.countryCode = leadMaster.getCountryCode();
		this.cafno = leadMaster.getCafno();
		this.altmobile = leadMaster.getAltmobile();
		this.altphone = leadMaster.getAltphone();
		this.altemail = leadMaster.getAltemail();
		this.fax = leadMaster.getFax();
		this.resellerid = leadMaster.getResellerid();
		this.salesrepid = leadMaster.getSalesrepid();
		this.voicesrvtype = leadMaster.getVoicesrvtype();
		this.voiceprovision = leadMaster.getVoiceprovision();
		this.childdidno = leadMaster.getChilddidno();
		this.didno = leadMaster.getDidno();
		this.intercomno = leadMaster.getIntercomno();
		this.intercomgrp = leadMaster.getIntercomgrp();
		this.onlinerenewalflag = leadMaster.getOnlinerenewalflag();
		this.voipenableflag = leadMaster.getVoipenableflag();
		this.custcategory = leadMaster.getCustcategory();
		this.walletbalance = leadMaster.getWalletbalance();
		this.networktype = leadMaster.getNetworktype();
		this.defaultpoolid = leadMaster.getDefaultpoolid();
		this.serviceareaid = leadMaster.getServiceareaid();
		this.networkdevicesid = leadMaster.getNetworkdevicesid();
		this.oltslotid = leadMaster.getOltslotid();
		this.oltportid = leadMaster.getOltportid();
		this.strconntype = leadMaster.getStrconntype();
		this.stroltname = leadMaster.getStroltname();
		this.strslotname = leadMaster.getStrslotname();
		this.strportname = leadMaster.getStrportname();
		this.OldBNGRouterinterface = leadMaster.getOldBNGRouterinterface();
		this.OldVSIName = leadMaster.getOldVSIName();
		this.ASNNumber = leadMaster.getASNNumber();
		this.BNGRouterinterface = leadMaster.getBNGRouterinterface();
		this.BNGRoutername = leadMaster.getBNGRoutername();
		this.IPPrefixes = leadMaster.getIPPrefixes();
		this.IPV6Prefixes = leadMaster.getIPV6Prefixes();
		this.LANIP = leadMaster.getLANIP();
		this.LANIPV6 = leadMaster.getLANIPV6();
		this.LLAccountid = leadMaster.getLLAccountid();
		this.LLConnectiontype = leadMaster.getLLConnectiontype();
		this.LLExpirydate = leadMaster.getLLExpirydate();
		this.LLMedium = leadMaster.getLLMedium();
		this.LLServiceid = leadMaster.getLLServiceid();
		this.MACADDRESS = leadMaster.getMACADDRESS();
		this.Peerip = leadMaster.getPeerip();
		this.POOLIP = leadMaster.getPOOLIP();
		this.QOS = leadMaster.getQOS();
		this.RDExport = leadMaster.getRDExport();
		this.RDValue = leadMaster.getRDValue();
		this.VLANID = leadMaster.getVLANID();
		this.VRFName = leadMaster.getVRFName();
		this.VSIID = leadMaster.getVSIID();
		this.VSIName = leadMaster.getVSIName();
		this.WANIP = leadMaster.getWANIP();
		this.WANIPV6 = leadMaster.getWANIPV6();
		this.billentityname = leadMaster.getBillentityname();
		this.addparam1 = leadMaster.getAddparam1();
		this.addparam2 = leadMaster.getAddparam2();
		this.addparam3 = leadMaster.getAddparam3();
		this.addparam4 = leadMaster.getAddparam4();
		this.purchaseorder = leadMaster.getPurchaseorder();
		this.remarks = leadMaster.getRemarks();
		this.allowedIPAddress = leadMaster.getAllowedIPAddress();
		this.OldWANIP = leadMaster.getOldWANIP();
		this.OldLLAccountid = leadMaster.getOldLLAccountid();
		this.firstActivationDate = leadMaster.getFirstActivationDate();
		this.isDeleted = leadMaster.isDeleted();
		this.createDateString = leadMaster.getCreateDateString();
		this.updateDateString = leadMaster.getUpdateDateString();
		this.latitude = leadMaster.getLatitude();
		this.longitude = leadMaster.getLongitude();
		this.url = leadMaster.getUrl();
		this.gisCode = leadMaster.getGisCode();
		this.salesremark = leadMaster.getSalesremark();
		this.servicetype = leadMaster.getServicetype();
		this.isCustCaf = leadMaster.getIsCustCaf();
		this.previousCafApprover = leadMaster.getPreviousCafApprover();
		this.nextCafApprover = leadMaster.getNextCafApprover();
		this.serviceareaName = leadMaster.getServiceareaName();
		this.cafApproveStatus = leadMaster.getCafApproveStatus();
		this.tinNo = leadMaster.getTinNo();
		this.passportNo = leadMaster.getPassportNo();
		this.dunningCategory = leadMaster.getDunningCategory();
		this.plangroupid = leadMaster.getPlangroupid();
		this.parentCustomerId = leadMaster.getParentCustomerId();
		this.parentCustomerName = leadMaster.getParentCustomerName();
		this.invoiceType = leadMaster.getInvoiceType();
		this.calendarType = leadMaster.getCalendarType();
		this.discount = leadMaster.getDiscount();
		if (leadMaster.getLeadSource() != null) {
			this.leadSourcePojo = new LeadSourcePojo(leadMaster.getLeadSource());
		}
		this.leadSubSourceId = leadMaster.getLeadSubSourceId();
		this.rejectReasonId = leadMaster.getRejectReasonId();
		this.rejectSubReasonId = leadMaster.getRejectSubReasonId();
		this.reasonToChangeServiceProvider = leadMaster.getReasonToChangeServiceProvider();
		this.previousVendor = leadMaster.getPreviousVendor();
		this.servicerType = leadMaster.getServicerType();
		this.leadStatus = leadMaster.getLeadStatus();
		this.leadCategory = leadMaster.getLeadCategory();
		this.heardAboutSubisuFrom = leadMaster.getHeardAboutSubisuFrom();
		if (leadMaster.getPartner() != null) {
			this.leadPartnerId = leadMaster.getPartner().getId();
		}
		if (leadMaster.getCustomers() != null) {
			this.leadCustomersId = leadMaster.getCustomers().getId();
		}
		if (leadMaster.getStaffUser() != null) {
			this.leadStaffUserId = leadMaster.getStaffUser().getId();
		}
		if (leadMaster.getBranch() != null) {
			this.leadBranchId = leadMaster.getBranch().getId();
		}
		this.leadAgentId = leadMaster.getLeadAgentId();
		if (leadMaster.getServiceArea() != null) {
			this.leadServiceAreaId = leadMaster.getServiceArea().getId();
		}
		this.feasibility = leadMaster.getFeasibility();
		this.feasibilityRemark = leadMaster.getFeasibilityRemark();
		this.feasibilityRequired = leadMaster.getFeasibilityRequired();
		this.rejectLeadTime = leadMaster.getRejectLeadTime();
		this.leadType = leadMaster.getLeadType();
		this.existingCustomerId = leadMaster.getExistingCustomerId();
		this.finalApproved = leadMaster.isFinalApproved();
		this.planType = leadMaster.getPlanType();
		this.buId = leadMaster.getBuId();
		this.nextApproveStaffId = leadMaster.getNextApproveStaffId();
		this.leadNo = leadMaster.getLeadNo();
		this.nextTeamMappingId = leadMaster.getNextTeamMappingId();
		this.presentCheckForPayment = leadMaster.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMaster.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMaster.getLeadCustomerCategory();
		this.leadCustomerType = leadMaster.getLeadCustomerType();
		this.leadCustomerSubType = leadMaster.getLeadCustomerSubType();
		this.leadCustomerSector = leadMaster.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMaster.getLeadCustomerSubSector();
		this.valleyType = leadMaster.getValleyType();
		this.insideValley = leadMaster.getInsideValley();
		this.outsideValley = leadMaster.getOutsideValley();
		this.competitorDuration = leadMaster.getCompetitorDuration();
		this.expiry = leadMaster.getExpiry();
		this.amount = leadMaster.getAmount();
		this.feedback = leadMaster.getFeedback();
		this.gender = leadMaster.getGender();
		if (leadMaster.getBranch() != null) {
			this.branchId = leadMaster.getBranch().getId();
		}
		if (leadMaster.getPopManagement() != null) {
			this.popManagementId = leadMaster.getPopManagement().getId();
		}
		this.dateOfBirth = leadMaster.getDateOfBirth();
		this.secondaryContactDetails = leadMaster.getSecondaryContactDetails();
		this.secondaryPhone = leadMaster.getSecondaryPhone();
		this.secondaryEmail = leadMaster.getSecondaryEmail();
		this.previousAmount = leadMaster.getPreviousAmount();
		this.previousMonth = leadMaster.getPreviousMonth();
		this.leadOriginType = leadMaster.getLeadOriginType();
		this.requireServiceType = leadMaster.getRequireServiceType();
		this.landlineNumber = leadMaster.getLandlineNumber();
		this.pcontactphno = leadMaster.getPcontactphno();
		this.scontactname = leadMaster.getScontactname();
		this.businessverticals = leadMaster.getBusinessverticals();
		this.subbusinessverticals = leadMaster.getSubbusinessverticals();
		this.connectiontype = leadMaster.getConnectiontype();
		this.linktype = leadMaster.getLinktype();
		this.circuitarea = leadMaster.getCircuitarea();
		this.closuredate = leadMaster.getClosuredate();
		this.circuitid = leadMaster.getCircuitid();
		this.circuitname = leadMaster.getCircuitname();
		this.leadvariety = leadMaster.getLeadvariety();
		this.altmobile1=leadMaster.getAltmobile1();
		this.altmobile2=leadMaster.getAltmobile2();
		this.altmobile3=leadMaster.getAltmobile3();
		this.altmobile4=leadMaster.getAltmobile4();
		if(leadMaster.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMaster.getIsLeadQuickInv()==1?true:false;
		if(leadMaster.getLeadDepartment()!=null){
			this.leadDepartment = leadMaster.getLeadDepartment();
		}
	}

	public LeadMasterPojo(LeadMasterPojoMessage leadMasterPojoMessage) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatterForDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter timeformatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");

		this.id = leadMasterPojoMessage.getId();
		this.username = leadMasterPojoMessage.getUsername();
		this.password = leadMasterPojoMessage.getPassword();
		this.firstname = leadMasterPojoMessage.getFirstname();
		this.lastname = leadMasterPojoMessage.getLastname();
		this.email = leadMasterPojoMessage.getEmail();
		this.title = leadMasterPojoMessage.getTitle();
		this.custname = leadMasterPojoMessage.getCustname();
		this.contactperson = leadMasterPojoMessage.getContactperson();
		this.pan = leadMasterPojoMessage.getPan();
		this.gst = leadMasterPojoMessage.getGst();
		this.aadhar = leadMasterPojoMessage.getAadhar();
		this.status = leadMasterPojoMessage.getStatus();
		this.failcount = leadMasterPojoMessage.getFailcount();
		this.acctno = leadMasterPojoMessage.getAcctno();
		this.custtype = leadMasterPojoMessage.getCusttype();
		this.phone = leadMasterPojoMessage.getPhone();
		this.billday = leadMasterPojoMessage.getBillday();
		this.partnerid = leadMasterPojoMessage.getPartnerid();
		this.onuid = leadMasterPojoMessage.getOnuid();
		this.mvnoId = leadMasterPojoMessage.getMvnoId();
		if (leadMasterPojoMessage.getNextBillDate() != null) {
			this.nextBillDate = LocalDate.parse(leadMasterPojoMessage.getNextBillDate(), formatter);
		}
		if (leadMasterPojoMessage.getLastBillDate() != null) {
			this.lastBillDate = LocalDate.parse(leadMasterPojoMessage.getLastBillDate(), formatter);
		}
		this.addresstype = leadMasterPojoMessage.getAddresstype();
		this.address1 = leadMasterPojoMessage.getAddress1();
		this.address2 = leadMasterPojoMessage.getAddress2();
		this.city = leadMasterPojoMessage.getCity();
		this.state = leadMasterPojoMessage.getState();
		this.country = leadMasterPojoMessage.getCountry();
		this.pincode = leadMasterPojoMessage.getPincode();
		this.area = leadMasterPojoMessage.getArea();
		this.outstanding = leadMasterPojoMessage.getOutstanding();
		this.oldpassword1 = leadMasterPojoMessage.getOldpassword1();
		this.oldpassword2 = leadMasterPojoMessage.getOldpassword2();
		this.oldpassword3 = leadMasterPojoMessage.getOldpassword3();
		this.selfcarepwd = leadMasterPojoMessage.getSelfcarepwd();
		this.createdBy = leadMasterPojoMessage.getCreatedBy();
		this.lastpasswordchangestring = leadMasterPojoMessage.getLastpasswordchangestring();
		this.nextApproveStaffId = leadMasterPojoMessage.getNextApproveStaffId();
		this.nextTeamMappingId = leadMasterPojoMessage.getNextTeamMappingId();
		if (leadMasterPojoMessage.getPlanMappingList() != null
				&& leadMasterPojoMessage.getPlanMappingList().size() > 0) {
			List<LeadCustPlanMapppingPojo> leadCustPlanMapppingPojoList = new ArrayList<LeadCustPlanMapppingPojo>();
			for (LeadCustPlanMapppingPojoMessage leadCustPlanMapppingPojoMessage : leadMasterPojoMessage
					.getPlanMappingList()) {
				leadCustPlanMapppingPojoList.add(new LeadCustPlanMapppingPojo(leadCustPlanMapppingPojoMessage));
			}
			this.planMappingList = leadCustPlanMapppingPojoList;
		}
		if (leadMasterPojoMessage.getAddressList() != null && leadMasterPojoMessage.getAddressList().size() > 0) {
			List<LeadCustomerAddressPojo> leadCustomerAddressPojoList = new ArrayList<LeadCustomerAddressPojo>();
			for (LeadCustomerAddressPojoMessage leadCustomerAddressPojoMessage : leadMasterPojoMessage
					.getAddressList()) {
				leadCustomerAddressPojoList.add(new LeadCustomerAddressPojo(leadCustomerAddressPojoMessage));
			}
			this.addressList = leadCustomerAddressPojoList;
		}

		this.radiusprofileIds = leadMasterPojoMessage.getRadiusprofileIds();

		if (leadMasterPojoMessage.getOverChargeList() != null && leadMasterPojoMessage.getOverChargeList().size() > 0) {
			List<LeadCustChargeDetailsPojo> leadCustChargeDetailsPojoList = new ArrayList<LeadCustChargeDetailsPojo>();
			for (LeadCustChargeDetailsPojoMessage leadCustChargeDetailsPojoMessage : leadMasterPojoMessage
					.getOverChargeList()) {
				leadCustChargeDetailsPojoList.add(new LeadCustChargeDetailsPojo(leadCustChargeDetailsPojoMessage));
			}
			this.overChargeList = leadCustChargeDetailsPojoList;
		}
//		if (leadMasterPojoMessage.getLeadDocDetailsList() != null
//				&& leadMasterPojoMessage.getLeadDocDetailsList().size() > 0) {
//			List<LeadDocDetailsDTO> leadDocDetailsDTOList = new ArrayList<LeadDocDetailsDTO>();
//			for (LeadDocDetailsDTOMessage leadDocDetailsDTOMessage : leadMasterPojoMessage.getLeadDocDetailsList()) {
//				leadDocDetailsDTOList.add(new LeadDocDetailsDTO(leadDocDetailsDTOMessage));
//			}
//			this.leadDocDetailsList = leadDocDetailsDTOList;
//		}
		if (leadMasterPojoMessage.getCustMacMapppingList() != null
				&& leadMasterPojoMessage.getCustMacMapppingList().size() > 0) {
			List<LeadCustMacMapppingPojo> leadCustMacMapppingPojoList = new ArrayList<LeadCustMacMapppingPojo>();
			for (LeadCustMacMapppingPojoMessage leadCustMacMapppingPojoMessage : leadMasterPojoMessage
					.getCustMacMapppingList()) {
				leadCustMacMapppingPojoList.add(new LeadCustMacMapppingPojo(leadCustMacMapppingPojoMessage));
			}
			this.custMacMapppingList = leadCustMacMapppingPojoList;
		}
		this.flashMsg = leadMasterPojoMessage.getFlashMsg();
		this.mactelflag = leadMasterPojoMessage.getMactelflag();
		this.mobile = leadMasterPojoMessage.getMobile();
		this.countryCode = leadMasterPojoMessage.getCountryCode();
		this.cafno = leadMasterPojoMessage.getCafno();
		this.altmobile = leadMasterPojoMessage.getAltmobile();
		this.altphone = leadMasterPojoMessage.getAltphone();
		this.altemail = leadMasterPojoMessage.getAltemail();
		this.fax = leadMasterPojoMessage.getFax();
		this.resellerid = leadMasterPojoMessage.getResellerid();
		this.salesrepid = leadMasterPojoMessage.getSalesrepid();
		this.voicesrvtype = leadMasterPojoMessage.getVoicesrvtype();
		this.voiceprovision = leadMasterPojoMessage.getVoiceprovision();
		this.childdidno = leadMasterPojoMessage.getChilddidno();
		this.didno = leadMasterPojoMessage.getDidno();
		this.intercomno = leadMasterPojoMessage.getIntercomno();
		this.intercomgrp = leadMasterPojoMessage.getIntercomgrp();
		this.onlinerenewalflag = leadMasterPojoMessage.getOnlinerenewalflag();
		this.voipenableflag = leadMasterPojoMessage.getVoipenableflag();
		this.custcategory = leadMasterPojoMessage.getCustcategory();
		this.walletbalance = leadMasterPojoMessage.getWalletbalance();
		this.networktype = leadMasterPojoMessage.getNetworktype();
		this.defaultpoolid = leadMasterPojoMessage.getDefaultpoolid();
		this.serviceareaid = leadMasterPojoMessage.getServiceareaid();
		this.networkdevicesid = leadMasterPojoMessage.getNetworkdevicesid();
		this.oltslotid = leadMasterPojoMessage.getOltslotid();
		this.oltportid = leadMasterPojoMessage.getOltportid();
		this.strconntype = leadMasterPojoMessage.getStrconntype();
		this.stroltname = leadMasterPojoMessage.getStroltname();
		this.strslotname = leadMasterPojoMessage.getStrslotname();
		this.strportname = leadMasterPojoMessage.getStrportname();
		this.OldBNGRouterinterface = leadMasterPojoMessage.getOldBNGRouterinterface();
		this.OldVSIName = leadMasterPojoMessage.getOldVSIName();
		this.ASNNumber = leadMasterPojoMessage.getASNNumber();
		this.BNGRouterinterface = leadMasterPojoMessage.getBNGRouterinterface();
		this.BNGRoutername = leadMasterPojoMessage.getBNGRoutername();
		this.IPPrefixes = leadMasterPojoMessage.getIPPrefixes();
		this.IPV6Prefixes = leadMasterPojoMessage.getIPV6Prefixes();
		this.LANIP = leadMasterPojoMessage.getLANIP();
		this.LANIPV6 = leadMasterPojoMessage.getLANIPV6();
		this.LLAccountid = leadMasterPojoMessage.getLLAccountid();
		this.LLConnectiontype = leadMasterPojoMessage.getLLConnectiontype();
		this.LLExpirydate = leadMasterPojoMessage.getLLExpirydate();
		this.LLMedium = leadMasterPojoMessage.getLLMedium();
		this.LLServiceid = leadMasterPojoMessage.getLLServiceid();
		this.MACADDRESS = leadMasterPojoMessage.getMACADDRESS();
		this.Peerip = leadMasterPojoMessage.getPeerip();
		this.POOLIP = leadMasterPojoMessage.getPOOLIP();
		this.QOS = leadMasterPojoMessage.getQOS();
		this.RDExport = leadMasterPojoMessage.getRDExport();
		this.RDValue = leadMasterPojoMessage.getRDValue();
		this.VLANID = leadMasterPojoMessage.getVLANID();
		this.VRFName = leadMasterPojoMessage.getVRFName();
		this.VSIID = leadMasterPojoMessage.getVSIID();
		this.VSIName = leadMasterPojoMessage.getVSIName();
		this.WANIP = leadMasterPojoMessage.getWANIP();
		this.WANIPV6 = leadMasterPojoMessage.getWANIPV6();
		this.billentityname = leadMasterPojoMessage.getBillentityname();
		this.addparam1 = leadMasterPojoMessage.getAddparam1();
		this.addparam2 = leadMasterPojoMessage.getAddparam2();
		this.addparam3 = leadMasterPojoMessage.getAddparam3();
		this.addparam4 = leadMasterPojoMessage.getAddparam4();
		this.purchaseorder = leadMasterPojoMessage.getPurchaseorder();
		this.remarks = leadMasterPojoMessage.getRemarks();
		this.allowedIPAddress = leadMasterPojoMessage.getAllowedIPAddress();
		this.OldWANIP = leadMasterPojoMessage.getOldWANIP();
		this.OldLLAccountid = leadMasterPojoMessage.getOldLLAccountid();
		if (leadMasterPojoMessage.getFirstActivationDate() != null) {
			this.firstActivationDate = LocalDateTime.parse(leadMasterPojoMessage.getFirstActivationDate(), formatter);
		}
		this.isDeleted = leadMasterPojoMessage.isDeleted();
		this.createDateString = leadMasterPojoMessage.getCreateDateString();
		this.updateDateString = leadMasterPojoMessage.getUpdateDateString();
		this.latitude = leadMasterPojoMessage.getLatitude();
		this.longitude = leadMasterPojoMessage.getLongitude();
		this.url = leadMasterPojoMessage.getUrl();
		this.gisCode = leadMasterPojoMessage.getGisCode();
		this.salesremark = leadMasterPojoMessage.getSalesremark();
		this.servicetype = leadMasterPojoMessage.getServicetype();
		this.isCustCaf = leadMasterPojoMessage.getIsCustCaf();
		this.previousCafApprover = leadMasterPojoMessage.getPreviousCafApprover();
		this.nextCafApprover = leadMasterPojoMessage.getNextCafApprover();
		this.serviceareaName = leadMasterPojoMessage.getServiceareaName();
		this.cafApproveStatus = leadMasterPojoMessage.getCafApproveStatus();
		this.tinNo = leadMasterPojoMessage.getTinNo();
		this.passportNo = leadMasterPojoMessage.getPassportNo();
		this.dunningCategory = leadMasterPojoMessage.getDunningCategory();
		this.plangroupid = leadMasterPojoMessage.getPlangroupid();
		this.parentCustomerId = leadMasterPojoMessage.getParentCustomerId();
		this.parentCustomerName = leadMasterPojoMessage.getParentCustomerName();
		this.invoiceType = leadMasterPojoMessage.getInvoiceType();
		this.calendarType = leadMasterPojoMessage.getCalendarType();
		this.discount = leadMasterPojoMessage.getDiscount();
		this.leadSourcePojo = leadMasterPojoMessage.getLeadSourcePojo();
		if (leadMasterPojoMessage.getLeadSubSourceId() != null) {
			this.leadSubSourceId = leadMasterPojoMessage.getLeadSubSourceId().intValue();
		}
		if (leadMasterPojoMessage.getRejectReasonId() != null) {
			this.rejectReasonId = leadMasterPojoMessage.getRejectReasonId().intValue();
		}
		if (leadMasterPojoMessage.getRejectSubReasonId() != null) {
			this.rejectSubReasonId = leadMasterPojoMessage.getRejectSubReasonId().intValue();
		}
		this.reasonToChangeServiceProvider = leadMasterPojoMessage.getReasonToChangeServiceProvider();
		this.previousVendor = leadMasterPojoMessage.getPreviousVendor();
		this.servicerType = leadMasterPojoMessage.getServicerType();
		this.leadStatus = leadMasterPojoMessage.getLeadStatus();
		this.leadCategory = leadMasterPojoMessage.getLeadCategory();
		this.heardAboutSubisuFrom = leadMasterPojoMessage.getHeardAboutSubisuFrom();
		this.leadPartnerId = leadMasterPojoMessage.getLeadPartnerId();
		this.leadCustomersId = leadMasterPojoMessage.getLeadCustomersId();
		this.leadStaffUserId = leadMasterPojoMessage.getLeadStaffUserId();
		this.leadBranchId = leadMasterPojoMessage.getLeadBranchId();
		this.leadAgentId = leadMasterPojoMessage.getLeadAgentId();
		this.leadServiceAreaId = leadMasterPojoMessage.getLeadServiceAreaId();
		this.feasibility = leadMasterPojoMessage.getFeasibility();
		this.feasibilityRemark = leadMasterPojoMessage.getFeasibilityRemark();
		this.feasibilityRequired = leadMasterPojoMessage.getFeasibilityRequired();
		if (leadMasterPojoMessage.getRejectLeadTime() != null) {
			this.rejectLeadTime = LocalDateTime.parse(leadMasterPojoMessage.getRejectLeadTime(), formatter);
		}
		this.leadType = leadMasterPojoMessage.getLeadType();
		this.existingCustomerId = leadMasterPojoMessage.getExistingCustomerId();
		this.finalApproved = leadMasterPojoMessage.isFinalApproved();
		this.planType = leadMasterPojoMessage.getPlanType();
		this.buId = leadMasterPojoMessage.getBuId();
		this.nextApproveStaffId = leadMasterPojoMessage.getNextApproveStaffId();
		this.leadNo = leadMasterPojoMessage.getLeadNo();
		this.nextTeamMappingId = leadMasterPojoMessage.getNextTeamMappingId();
		this.presentCheckForPayment = leadMasterPojoMessage.isPresentCheckForPayment();
		this.presentCheckForPermanent = leadMasterPojoMessage.isPresentCheckForPermanent();
		this.leadCustomerCategory = leadMasterPojoMessage.getLeadCustomerCategory();
		this.leadCustomerType = leadMasterPojoMessage.getLeadCustomerType();
		this.leadCustomerSubType = leadMasterPojoMessage.getLeadCustomerSubType();
		this.leadCustomerSector = leadMasterPojoMessage.getLeadCustomerSector();
		this.leadCustomerSubSector = leadMasterPojoMessage.getLeadCustomerSubSector();
		this.valleyType = leadMasterPojoMessage.getValleyType();
		this.insideValley = leadMasterPojoMessage.getInsideValley();
		this.outsideValley = leadMasterPojoMessage.getOutsideValley();
		this.competitorDuration = leadMasterPojoMessage.getCompetitorDuration();
		if (leadMasterPojoMessage.getExpiry() != null) {
			this.expiry = LocalDate.parse(leadMasterPojoMessage.getExpiry(), formatterForDate);
		}
		this.amount = leadMasterPojoMessage.getAmount();
		this.feedback = leadMasterPojoMessage.getFeedback();
		this.gender = leadMasterPojoMessage.getGender();
		this.branchId = leadMasterPojoMessage.getBranchId();
		this.popManagementId = leadMasterPojoMessage.getPopManagementId();
		if (leadMasterPojoMessage.getDateOfBirth() != null) {
			this.dateOfBirth = LocalDate.parse(leadMasterPojoMessage.getDateOfBirth(), formatterForDate);
		}
		this.secondaryContactDetails = leadMasterPojoMessage.getSecondaryContactDetails();
		this.secondaryPhone = leadMasterPojoMessage.getSecondaryPhone();
		this.secondaryEmail = leadMasterPojoMessage.getSecondaryEmail();
		this.previousAmount = leadMasterPojoMessage.getPreviousAmount();
		this.previousMonth = leadMasterPojoMessage.getPreviousMonth();
		this.leadOriginType = leadMasterPojoMessage.getLeadOriginType();
		this.requireServiceType = leadMasterPojoMessage.getRequireServiceType();
		this.landlineNumber = leadMasterPojoMessage.getLandlineNumber();
		this.pcontactphno = leadMasterPojoMessage.getPcontactphno();
		this.scontactname = leadMasterPojoMessage.getScontactname();
		this.businessverticals = leadMasterPojoMessage.getBusinessverticals();
		this.subbusinessverticals = leadMasterPojoMessage.getSubbusinessverticals();
		this.connectiontype = leadMasterPojoMessage.getConnectiontype();
		this.linktype = leadMasterPojoMessage.getLinktype();
		this.circuitarea = leadMasterPojoMessage.getCircuitarea();
		this.closuredate = leadMasterPojoMessage.getClosuredate();
		this.circuitid = leadMasterPojoMessage.getCircuitid();
		this.circuitname = leadMasterPojoMessage.getCircuitname();
		this.leadvariety = leadMasterPojoMessage.getLeadvariety();
		this.altmobile1=leadMasterPojoMessage.getAltmobile1();
		this.altmobile2=leadMasterPojoMessage.getAltmobile2();
		this.altmobile3=leadMasterPojoMessage.getAltmobile3();
		this.altmobile4=leadMasterPojoMessage.getAltmobile4();
		this.currentLoggedInStaffId = leadMasterPojoMessage.getCurrentLoggedInStaffId();
		this.blockNo = leadMasterPojoMessage.getBlockNo();
		if(leadMasterPojoMessage.getLeadDepartment()!=null){
			this.leadDepartment = leadMasterPojoMessage.getLeadDepartment();
		}
		if(leadMasterPojoMessage.getIsLeadFromCWSC()!=null){
			this.isLeadFromCWSC = leadMasterPojoMessage.getIsLeadFromCWSC();
		}

//		if(leadMasterPojoMessage.getNextfollowuptime()!=null) {
//				this.setNextfollowuptime(LocalTime.parse(leadMasterPojoMessage.getNextfollowuptime(), timeformatter1));
//			}
//		if(leadMasterPojoMessage.getNextfollowupdate()!=null) {
//			this.setNextfollowupdate(LocalDate.parse(leadMasterPojoMessage.getNextfollowupdate(), formatterForDate));
//		}

	}
}
