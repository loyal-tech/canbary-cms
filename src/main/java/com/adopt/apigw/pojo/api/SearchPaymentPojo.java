package com.adopt.apigw.pojo.api;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter

public class SearchPaymentPojo {

	private String referenceno;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate payfromdate;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate paytodate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate recordfromdate;
    
	@DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recordtodate;

	private String idlist;
	
	private String emailreceipt;
	
	private String remarks;

	private String paystatus;

	private String paymode;
	
	private Integer customerid;
	
	private Integer partnerid;
	
	private String type;

	private String paytype;

	private Integer nextApprover;

	private Integer nextStaffId;
	private Integer approveId;

	private String mobileNumber;

	private String invoiceNumber;

	private String chequeNo;
	private String paydetails1;//bankName

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate chequedate;


	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate paymentdate;

	private String userName;

	private List<Long> buID;

	private String branchname;

	private String staff;

	private Integer staffId;

	private String creditDocumentNumber;

	private String receiptNo;

	private String destinationBank;

	private String partnerName;

	private Long serviceAreaId;

	private Integer page;

	private Integer pageSize;
	private Integer mvnoId;

}
