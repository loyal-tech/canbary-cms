package com.adopt.apigw.nepaliCalendarUtils.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.service.CacheService;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.DurationFieldType;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.nepaliCalendarUtils.domain.NepaliDate;
import com.adopt.apigw.nepaliCalendarUtils.model.EnglishDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.model.NepaliDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.repository.NepaliDateRepository;

@Service
public class DateConverterService {
	
	@Autowired
	private NepaliDateRepository dateRepository;
    @Autowired
    private CacheService cacheService;

	//Change this when you get more previous database.
    public static final int START_ENGLISH_YEAR = 2022;
    public static final int START_ENGLISH_MONTH = 4;
    public static final int START_ENGLISH_DAY = 14;
    //Change this to equivalent nepali start date
    public static final int START_NEPALI_YEAR = 2079;
    public static final int START_NEPALI_MONTH = 1;
    public static final int START_NEPALI_DAY = 1;
    

    /*
        English units start with e
        Nepali units start with n
    */
    public NepaliDateDTO getNepaliDateDTO(int eYear, int eMonth, int eDay, String time) {
        int nDay = START_NEPALI_DAY, nMonth = START_NEPALI_MONTH, nYear = START_NEPALI_YEAR;
        DateTime start = new DateTime(START_ENGLISH_YEAR, START_ENGLISH_MONTH, START_ENGLISH_DAY, 0, 0);
        DateTime end = new DateTime(eYear, eMonth, eDay, 0, 0);
        int deltaDays = Days.daysBetween(start, end).getDays();
        for (int i = 0; i < deltaDays; i++) {
            if (nDay < getDaysInMonth(nYear, nMonth)) {
                nDay++;
            } else if (nMonth < 12) {
                nDay = 1;
                nMonth++;
            } else if (nMonth == 12) {
                nYear++;
                nMonth = 1;
                nDay = 1;
            }
        }
        return new NepaliDateDTO(nYear, nMonth, nDay, time);
    }


    public EnglishDateDTO getEnglishDateDTO(int nYear, int nMonth, int nDay, String time) {
        int l_day = START_NEPALI_DAY, l_month = START_NEPALI_MONTH, l_year = START_NEPALI_YEAR;
        int deltaDays = 0;
        boolean isReached = false;
        while (!isReached) {
            if (nYear == l_year && nMonth == l_month && nDay == l_day) {
                isReached = true;
                deltaDays--;
            }
            deltaDays++;
            if (l_day < getDaysInMonth(l_year, l_month)) {
                l_day++;
            } else if (l_month < 12) {
                l_day = 1;
                l_month++;
            } else if (l_month == 12) {
                l_year++;
                l_month = 1;
                l_day = 1;
            }
        }
        int sec = 00;
        int hour = 00;
        int min = 00;
        if(time != null) {
        	String[] timeArr = time.replaceAll("\\s+","").split(":");
        	hour = Integer.valueOf(timeArr[0]);
        	min = Integer.valueOf(timeArr[1]) - 15;
            //If min is less than 0
        	if(min < 0) {
                min = 60 + min;
                hour= hour - 1;
            }
            //If hour is less than 0
            if(hour < 0) {
                hour = 12 + hour;
            }
        	if(timeArr.length > 2)
        		sec = Integer.valueOf(timeArr[2]);
        }
        DateTime dateTime = new DateTime(START_ENGLISH_YEAR, START_ENGLISH_MONTH, START_ENGLISH_DAY, hour , min);
        return new EnglishDateDTO(dateTime.withFieldAdded(DurationFieldType.days(), deltaDays), hour, min, sec);
    }

    public int getDaysInMonth(int year, int month){
        String cacheKey = cacheKeys.NEPALIDATE_MONTH + month;
        Optional<NepaliDate> nepalidate = Optional.empty();

        try {
            nepalidate = (Optional<NepaliDate>) cacheService.getFromCache(cacheKey, Optional.class);

            if (!nepalidate.isPresent()) {
                nepalidate = dateRepository.findByYear(String.valueOf(year));

                if (!nepalidate.isPresent()) {
                    throw new RuntimeException("No value present for year: " + year + " and month: " + month);
                }
                cacheService.putInCache(cacheKey, nepalidate);
            }
            String dates = nepalidate.get().getDays();
            String[] datesArr = dates.replaceAll("\\s+", "").split(",");

            return Integer.valueOf(datesArr[month - 1]);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    public NepaliDateDTO getNepaliDateFromEnglishDate(String englishDateStr) {
	  String time = englishDateStr.substring(englishDateStr.indexOf(" "), englishDateStr.length());
	  String date = englishDateStr.substring(0, englishDateStr.indexOf(" "));
	  String[] array = date.split("-");
      int year = Integer.valueOf(array[2]);
      int month = Integer.valueOf(array[1]);
      int day = Integer.valueOf(array[0]);
      NepaliDateDTO nepaliDate = getNepaliDateDTO(year, month, day, time);
      return nepaliDate;
  }

  public EnglishDateDTO getEnglishDateDTOFromNepaliDate(String nepaliDateDTOStr) {
	  String time = nepaliDateDTOStr.substring(nepaliDateDTOStr.indexOf(" "), nepaliDateDTOStr.length());
	  String date = nepaliDateDTOStr.substring(0, nepaliDateDTOStr.indexOf(" "));
      String[] array = date.split("-");
      int year = Integer.valueOf(array[2]);
      int month = Integer.valueOf(array[1]);
      int day = Integer.valueOf(array[0]);
      EnglishDateDTO EnglishDateDTO = getEnglishDateDTO(year, month, day, time);
      return EnglishDateDTO;
  }
  
  public NepaliDateDTO calculateEndDateNepaliByMonth(NepaliDateDTO date, int month) {
      int daysInMonth = getDaysInMonth(date.getSaal(), date.getMahina());
      for(int i=1; i < month; i++) {
    	  daysInMonth = daysInMonth + getDaysInMonth(date.getSaal(), date.getMahina());
      }
      int endDay = daysInMonth;
      int endMonth = date.getMahina() + month;
      int endSaal = date.getSaal();
      int nextDay = date.getGatey() + daysInMonth;
      if(nextDay > daysInMonth) {
          endDay = nextDay - daysInMonth;
          if(endMonth > 12) {
              endMonth = 1;
              endSaal = endSaal + 1;
          }
      }
      return new NepaliDateDTO(endSaal, endMonth, endDay, date.getTime(), true);
  }
  
  public NepaliDateDTO calculateEndDateNepaliByDay(NepaliDateDTO date, int day) {
      int daysInMonth = getDaysInMonth(date.getSaal(), date.getMahina());
      int endDay = date.getGatey() + day;
      
      int endMonth = date.getMahina();
      int endSaal = date.getSaal();
      int nextDay = date.getGatey() + day;
      int calday = nextDay;
      while (nextDay >= daysInMonth){
          daysInMonth = getDaysInMonth(date.getSaal(), date.getMahina());
          endDay = nextDay - daysInMonth;
          nextDay -= daysInMonth;
          endMonth = endMonth+1;
          if(endMonth > 12) {
              endMonth = 1;
              endSaal = endSaal + 1;
          }
      }
//      if(nextDay > daysInMonth) {
//          endDay = nextDay - daysInMonth;
//          endMonth = date.getMahina()+1;
//          if(endMonth > 12) {
//              endMonth = 1;
//              endSaal = endSaal + 1;
//          }
//      }
      return new NepaliDateDTO(endSaal, endMonth, endDay, date.getTime(), true);
  }
  
  public NepaliDateDTO calculateEndDateNepaliByYear(NepaliDateDTO date, int year) {
      int endDay = date.getGatey();
      
      int endMonth = date.getMahina();
      int endSaal = date.getSaal() + year;
      
      return new NepaliDateDTO(endSaal, endMonth, endDay, date.getTime(), true);
  }
  
  
  public String LocalDateTimeToString(LocalDateTime localDateTime) {
	  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy hh:mm:ss");
	  return localDateTime.format(formatter);
  }

    public int getDaysInYear(int year) {
        String cacheKey = cacheKeys.NEPALIDATE_YEAR + year;
        int days = 0;
        Optional<NepaliDate> nepalidate = Optional.empty();
        try {
            nepalidate = (Optional<NepaliDate>) cacheService.getFromCache(cacheKey, Optional.class);
            if (!nepalidate.isPresent()) {
                nepalidate = dateRepository.findByYear(String.valueOf(year));
                if (!nepalidate.isPresent()) {
                    throw new RuntimeException("No value present for year: " + year);
                }
                cacheService.putInCache(cacheKey, nepalidate);
            }
            String dates = nepalidate.get().getDays();
            String[] datesArr = dates.replaceAll("\\s+", "").split(",");
            for (String day : datesArr) {
                days = days + Integer.valueOf(day);
            }
            return days;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error processing the Nepali date: " + e.getMessage());
        }

        return 0;
    }
}
