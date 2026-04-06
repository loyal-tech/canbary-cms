package com.adopt.apigw.service.common;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.utils.UtilsCommon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AcctNumCreationService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    CustomersRepository customersRepository;

    public AcctNumCreationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String getNewCustomerAccountNo(CustAccountProfile custAccountProfile, Integer mvnoId) throws Exception {
        String result;
        if (custAccountProfile.getType().equalsIgnoreCase("timestamp")) {
            result = timestampType(custAccountProfile.getPrefix(), custAccountProfile.isYear(), custAccountProfile.isMonth(), custAccountProfile.isDay());
        } else if (custAccountProfile.getType().equalsIgnoreCase("number")) {
            result = numberType(custAccountProfile.getPrefix(), custAccountProfile.getStartFrom(), mvnoId);
        } else {
            ApplicationLogger.logger.error("Provide Specific Profile Type");
            return null;
        }
        return result;
    }

    public String timestampType(String prefix, boolean year, boolean month, boolean day) {
        try {
            long timestamp = UtilsCommon.getUniqueNumber();
            LocalDate local = LocalDate.now();
            int count = (year ? 1 : 0) + (month ? 1 : 0) + (day ? 1 : 0);

            // Convert the year to "yy" format
            String yearPart = String.valueOf(local.getYear()).substring(2);

            switch (count) {
                case 3:
                    return prefix + yearPart + local.getMonthValue() + local.getDayOfMonth() + timestamp;
                case 2:
                    if (year && month) return prefix + yearPart + local.getMonthValue() + timestamp;
                    if (year && day) return prefix + yearPart + local.getDayOfMonth() + timestamp;
                    if (month && day) return prefix + local.getMonthValue() + local.getDayOfMonth() + timestamp;
                case 1:
                    if (year) return prefix + yearPart + timestamp;
                    if (month) return prefix + local.getMonthValue() + timestamp;
                    if (day) return prefix + local.getDayOfMonth() + timestamp;
                default:
                    return "No conditions Matched.";
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error in performing for processTimestamp during account number generation...");
            e.getStackTrace();
            return null;
        }
    }

    public String numberType(String prefix, String startFrom, Integer mvnoId) throws Exception {
        String accountNumber = null;
        String number = null;
        try {
            // If not in local cache, fallback to Redis
            String redisKey = "customerAccountNumber-" + mvnoId;
            String currentNumber = redisTemplate.opsForValue().get(redisKey);
            ApplicationLogger.logger.debug(":::::::::::::::  Current Cust Account number for key ::::::::::::::: {} ::: {}", redisKey, currentNumber);
            // If the key doesn't exist (first-time generation), initialize it to startFrom
            if (currentNumber == null) {
                // Set the initial account number using Redis SETNX or similar command
                String latestAccountNum = customersRepository.findLatestCustomerByMvnoId(mvnoId);
                ApplicationLogger.logger.debug(":::::::::::::::  Latest Cust Account number from db ::::::::::::::: {}", latestAccountNum);
                if (latestAccountNum == null || latestAccountNum.isEmpty()) {
                    number = startFrom; // Initialize the current number with the startFrom value

//                    redisTemplate.opsForValue().set(redisKey, number);
                } else if (latestAccountNum.startsWith(prefix)) {
                    String numericPart = latestAccountNum.substring(prefix.length());
                    Long convertnumber = Long.parseLong(numericPart) + 1;
                    number = String.format("%0" + numericPart.length() + "d", convertnumber);
//                    redisTemplate.opsForValue().set(redisKey, number);
                } else {
                    ApplicationLogger.logger.error("Last Account Num Prefix Does not Match with Current Profile");
                    number = startFrom; // Initialize the current number with the startFrom value
                }

            } else {
                // Increment the current account number by 1 for subsequent generations
                Long convertnumber = Long.parseLong(currentNumber) + 1;
                number = String.format("%0" + startFrom.length() + "d", convertnumber);
            }
            ApplicationLogger.logger.debug("::::::::::::::: Cust Account number storing in redis ::::::::::::::: {}", number);
            redisTemplate.opsForValue().set(redisKey, number);
            // Generate the account number by concatenating the prefix and the current number
            accountNumber = prefix + number;
        } catch (Exception e) {
            ApplicationLogger.logger.error("Error generating account number Due to Redis for mvnoId : " + mvnoId);
            try {
                /* in case if redis is unavailable */
                String latestAccountNum = customersRepository.findLatestCustomerByMvnoId(mvnoId);
                ApplicationLogger.logger.debug(":::::::::::::::  Catch Latest Cust Account number from db ::::::::::::::: {}", latestAccountNum);
                if (latestAccountNum == null || latestAccountNum.isEmpty()) {
                    number = startFrom;
                } else if (latestAccountNum.startsWith(prefix)) {
                    String numericPart = latestAccountNum.substring(prefix.length());
                    Long convertnumber = Long.parseLong(numericPart) + 1;
                    number = String.format("%0" + numericPart.length() + "d", convertnumber);
                } else {
                    ApplicationLogger.logger.error("Last Account Num Prefix Does not Match with Current Profile");
                    return null;
                }
                ApplicationLogger.logger.debug(":::::::::::::::  Catch Generate Cust Account number from db ::::::::::::::: {}", number);
                accountNumber = prefix + number;

            } catch (Exception e1) {
                ApplicationLogger.logger.error("Error generating account number: ", e1);

            }

        }
        return accountNumber;
    }

}
