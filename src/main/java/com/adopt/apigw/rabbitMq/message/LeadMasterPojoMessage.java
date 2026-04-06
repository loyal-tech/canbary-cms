package com.adopt.apigw.rabbitMq.message;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.adopt.apigw.model.lead.LeadMaster;
import com.adopt.apigw.model.lead.LeadMasterPojo;
import com.adopt.apigw.model.lead.LeadSourcePojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeadMasterPojoMessage {

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

	private String nextBillDate;

	private String lastBillDate;

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

	private String lastpasswordchangestring;

	private List<LeadCustPlanMapppingPojoMessage> planMappingList = new ArrayList<>();

	private List<LeadCustomerAddressPojoMessage> addressList = new ArrayList<>();

	private List<LeadCustChargeDetailsPojoMessage> overChargeList = new ArrayList<>();

	private List<LeadCustChargeDetailsPojoMessage> indiChargeList = new ArrayList<>();

	private List<LeadCustMacMapppingPojoMessage> custMacMapppingList = new ArrayList<>();

	private List<LeadDocDetailsDTOMessage> leadDocDetailsList = new ArrayList<>();

	private List<Integer> radiusprofileIds = new ArrayList<Integer>();

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

	private String firstActivationDate;

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

	private Long leadSubSourceId;

	private Long rejectReasonId;

	private Long rejectSubReasonId;

	private String reasonToChangeServiceProvider;

	private String previousVendor;

	private String servicerType;

	private String leadStatus;

	private String createdBy;

	private String lastModifiedBy;

	private String rejectedBy;

	private String approvedBy;

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

	private String rejectLeadTime;

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

	private String expiry;

	private Double amount;

	private String feedback;

	private String gender;

	private Long branchId;

	private Long popManagementId;

	private String dateOfBirth;

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
	private String nextfollowupdate;
	private String nextfollowuptime;
	private Boolean isLeadFromCWSC;
	private String blockNo;

	public LeadMasterPojoMessage(LeadMaster leadMaster) {
		this.id = leadMaster.getId();
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
		this.nextBillDate = String.valueOf(leadMaster.getNextBillDate());
		this.lastBillDate = String.valueOf(leadMaster.getLastBillDate());
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
		this.newpassword = leadMaster.getNewpassword();
		this.oldpassword3 = leadMaster.getOldpassword3();
		this.selfcarepwd = leadMaster.getSelfcarepwd();
		this.lastpasswordchangestring = leadMaster.getLastpasswordchangestring();
		this.leadStatus = ("Converted");

		if(leadMaster.getIsLeadQuickInv()!= null)
			this.isLeadQuickInv = leadMaster.getIsLeadQuickInv()==1?true:false;
		if(leadMaster.getLeadDepartment()!=null){
			this.leadDepartment = leadMaster.getLeadDepartment();
		}
		if(leadMaster.getNextfollowupdate()!=null){
			this.nextfollowupdate=leadMaster.getNextfollowupdate().toString();
		}
		if(leadMaster.getNextfollowuptime()!=null){
			this.nextfollowuptime=leadMaster.getNextfollowuptime().toString();
		}
	}


	public LeadMasterPojoMessage(LeadMasterPojo leadMaster) {

	}
}
