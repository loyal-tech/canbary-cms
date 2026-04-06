package com.adopt.apigw.rabbitMq.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeadCustChargeDetailsPojoMessage {

	private Integer id;

	private Integer planid;

	private Integer chargeid;

	private String chargeName;

	private String chargetype;

	private Double validity = 0.0;

	private Double price = 0.0;

	private Double actualprice = 0.0;

	private Long leadMasterId;

	private String remarks;

	private String chargeDateString;

	private String startdateString;

	private String enddateString;

	private Double taxamount;

	private Boolean is_reversed = false;

	private LocalDateTime rev_date;

	private String revdateString;

	private Double rev_amt;

	private String rev_remarks;

	private Boolean isUsed;

	private Long purchaseEntityId;

	private Long ippooldtlsid;

	private Long debitdocid;

	private String createDateString;

	private String updateDateString;

	private String type;

	private Integer planValidity;

	private String unitsOfValidity;

	private Integer taxId;

	private Integer custPlanMapppingId;

	private String lastBillDate;

	private String nextBillDate;

	private Integer billingCycle;
}
