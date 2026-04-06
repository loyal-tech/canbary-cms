package com.adopt.apigw.nepaliCalendarUtils.model;

import java.util.Arrays;

import org.joda.time.DateTime;

public class EnglishDateDTO {

    public static final String WEEK_DAYS[] = {"Sunday", "Monday", "Tuesday",
            "Wednesday", "Thursday", "Friday", "Saturday"};

    private DateTime englishDate;
    
    private int hour;
    
    private int min;
    
    private int sec;

    public EnglishDateDTO(DateTime englishDate, int hour, int min, int sec){
        this.englishDate = englishDate;
        this.hour = hour;
        this.min = min;
        this.sec = sec;
    }

    public int getYear(){
        return englishDate.getYear();
    }

    public String getMonthAsText(){
        return englishDate.monthOfYear().getAsText();
    }

    public int getMonth(){
        return englishDate.getMonthOfYear();
    }

    public int getDate(){
        return englishDate.getDayOfMonth();
    }
    
    public int getHour() {
		return hour;
	}

	public int getMin() {
		return min;
	}

	public int getSec() {
		return sec;
	}

	public static int getWeekIndex(String weekDay) {
        return Arrays.asList(WEEK_DAYS).indexOf(weekDay) + 1;
    }
}
