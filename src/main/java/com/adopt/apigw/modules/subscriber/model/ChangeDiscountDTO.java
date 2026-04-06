package com.adopt.apigw.modules.subscriber.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ChangeDiscountDTO {
	
	private Integer id;

	private Integer custId;

	private Integer planId;

	private String planName;

	private Double discount;

	private Double newDiscount ;
	
	private LocalDateTime startDate;
	
	private LocalDateTime endDate;

	private Integer nextStaff =0;

	private String status;

	private Integer serviceId;

	private Integer custServiceMappingId;

	private String remarks;


	private String discountType;

	private String newDiscountType ;

	private LocalDate discountExpiryDate;

	private LocalDate newDiscountExpiryDate ;

	public ChangeDiscountDTO(Integer id, Integer custId, Integer planId, Double oldDiscount, Double newDiscount,
			LocalDateTime startDate, LocalDateTime endDate, Integer nextStaff, String status,Integer custServiceMappingId,String remarks) {
		this.id = id;
		this.custId = custId;
		this.planId = planId;
		this.discount = oldDiscount;
		this.newDiscount = newDiscount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.nextStaff = nextStaff;
		this.status = status;
		this.custServiceMappingId=custServiceMappingId;
		this.remarks=remarks;
	}

	public ChangeDiscountDTO(Integer id, Integer custId, Integer planId, Double discount, Double newDiscount,
							 LocalDateTime startDate, LocalDateTime endDate ) {
		this.id = id;
		this.custId = custId;
		this.planId = planId;
		this.discount = discount;
		this.newDiscount = newDiscount;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public ChangeDiscountDTO(Integer id, Integer custId,String discountType,String newDiscountType,LocalDate discountExpiryDate,LocalDate newDiscountExpiryDate, Double discount, Double newDiscount) {
		this.id = id;
		this.custId = custId;
		this.discount = discount;
		this.newDiscount = newDiscount;
		this.discountType=discountType;
		this.newDiscountType=newDiscountType;
		this.discountExpiryDate=discountExpiryDate;
		this.newDiscountExpiryDate=newDiscountExpiryDate;
	}
	

}
