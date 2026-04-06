package com.adopt.apigw.modules.Voucher.module;

import lombok.Data;

@Data
public class PaginationDTO {

	private String fromDate;

	private String toDate;

	private int size = Integer.MAX_VALUE;	//This is default size if get null

	private int page;
}
