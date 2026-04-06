package com.adopt.apigw.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTimeUtil {

	public DateTimeUtil() {

	}

	public long getElapsedTime(Date startDate, Date endDate) {

		long seconds = (endDate.getTime() - startDate.getTime()) / 1000;

		return seconds;
	}

	public LocalDate convertDateToDifferenFormat(DateTimeFormatter inputFormatter, DateTimeFormatter outputFormatter, String strDate) {
		try {
			LocalDate da = LocalDate.parse(strDate, inputFormatter);
			System.out.println("==Date is ==" + da);

			String strDateTime = outputFormatter.format(da);
			System.out.println("==String date is : " + strDateTime);
			return LocalDate.parse(strDateTime);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public LocalDateTime convertDateTimeToDifferenFormat(DateTimeFormatter outputFormatter, String strDate) {
		try {

//			strDate = strDate.substring(0,19).replace("T"," ");
			return getLocaldateTimefromString(strDate);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static LocalDateTime getLocaldateTimefromString(String strDateTime) {
		LocalDateTime result = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
		DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
		DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
		DateTimeFormatter formatter5 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		DateTimeFormatter formatter6 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		DateTimeFormatter formatter7 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
		DateTimeFormatter formatter8 = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSSS");
		DateTimeFormatter formatter9 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");



		if(isValidDateFormat(formatter, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter);
		} else if(isValidDateFormat(formatter2, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter2);
		} else if(isValidDateFormat(formatter3, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter3);
		} else if(isValidDateFormat(formatter4, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter4);
		} else if(isValidDateFormat(formatter5, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter5);
		} else if(isValidDateFormat(formatter6, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter6);
		} else if(isValidDateFormat(formatter7, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter7);
		}else if(isValidDateFormat(formatter8, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter8);
		}else if(isValidDateFormat(formatter9, strDateTime)) {
			return LocalDateTime.parse(strDateTime, formatter9);
		}

		return result;
	}

	public static boolean isValidDateFormat(DateTimeFormatter formatter, String strDate) {
		try {
			LocalDateTime.parse(strDate, formatter);
			return true;
		} catch (Exception ex) {
			return false;
		}

	}
}
