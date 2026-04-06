package com.adopt.apigw.nepaliCalendarUtils.model;

import org.json.JSONObject;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ApiModel(value = "Customer Entity", description = "This is Nepali date entity which is used to fetch nepali data")
public class NepaliDateDTO {

	private Long id;
	
	private int year;
	
	private int days;
	
	private int mahina, gatey, saal;
	private String time;
	
	JSONObject numberOfDaysPerYearJson = new JSONObject();
	
    public static final String MONTHS[] = {"Baisakh", "Jestha", "Asadh", "Shrawan",
            "Bhadra", "Ashwin", "Kartik", "Mangsir",
            "Poush", "Magh", "Falgun", "Chaitra"};

	public NepaliDateDTO(int saal, int mahina, int gatey, String time) {
		this.mahina = mahina;
        this.gatey = gatey;
        this.saal = saal;
        this.time = getTime(time);
	}
	
	public NepaliDateDTO(int saal, int mahina, int gatey, String time, boolean isEndDate) {
		this.mahina = mahina;
        this.gatey = gatey;
        this.saal = saal;
        if(isEndDate)
        	this.time = getTimeForEndDate(time);
	}
	
	public String getMahinaInWords() {
        return MONTHS[mahina - 1];
    }
    public String toString() {
    	if(time != null)
    		return String.format("%02d", gatey)+ "-"+String.format("%02d", mahina) + "-" + String.format("%02d", saal) + " " + time;
    	return String.format("%02d", gatey)+ "-"+String.format("%02d", mahina) + "-" + String.format("%02d", saal);
    }
    
    public String getTime(String time) {
    	if(time != null) {
        	String[] timeArr = time.replaceAll("\\s+","").split(":");
        	int hour = Integer.valueOf(timeArr[0]);
        	int min = Integer.valueOf(timeArr[1]) + 15;
        	int sec = 00;
        	if(timeArr.length > 2)
        		sec = Integer.valueOf(timeArr[2]);
        	if(min > 60) {
        		hour = hour + 1;
        		min = min - 60; 
        	}
        	
        	if(hour > 12) {
        		hour = 0;
        	}
        	time = String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        }
    	return time;
    }
    
    public String getTimeForEndDate(String time) {
    	if(time != null) {
    		String[] timeArr = time.replaceAll("\\s+","").split(":");
        	int hour = Integer.valueOf(timeArr[0]);
        	int min = Integer.valueOf(timeArr[1]);
        	int sec = 00;
        	if(timeArr.length > 2)
        		sec = Integer.valueOf(timeArr[2]);

        	time = String.format("%02d", hour) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        }
    	return time;
    }
	
}
