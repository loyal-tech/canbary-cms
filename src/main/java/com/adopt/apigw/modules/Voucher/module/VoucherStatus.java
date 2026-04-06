package com.adopt.apigw.modules.Voucher.module;

public enum VoucherStatus {

	GENERATED(1,"GENERATED"),
	USED(2,"USED"),
	EXPIRED(3,"EXPIRED"),
	ACTIVE(4,"ACTIVE"),
	BLOCKED(5,"BLOCKED"),
	SCRAPPED(6,"SCRAPPED"),
	INVALID(7,"INVALID"),
	INACTIVE(8,"INACTIVE");

	private int number;
	private String name;

	private VoucherStatus(int number, String name) {
		this.number = number;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
