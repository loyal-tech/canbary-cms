package com.adopt.apigw.utils;

import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.pojo.api.CustomerLocationMappingDto;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.service.common.CaptivePortalCustomerService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CustomersUtil {

    @Autowired
    private CaptivePortalCustomerService customerService;

    public static JSONObject convertObjectToJson(Object object) {
        if(isJSONValid(object.toString())) {
            JSONObject json = new JSONObject(object.toString());
            System.out.println("groovy response: "+json.toString());
            return json;
        }
        return null;
    }

    public static boolean isJSONValid(String jsonStr) {
        try {
            new JSONObject(jsonStr);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(jsonStr);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static boolean isValidIPAddress(String ip) {

        // Regex for digit from 0 to 255.
        String zeroTo255 = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";

        // Regex for a digit from 0 to 255 and
        // followed by a dot, repeat 4 times.
        // this is the regex to validate an IP address.
        String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the IP address is empty
        // return false
        if (ip == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given IP address
        // and regular expression.
        Matcher m = p.matcher(ip);

        // Return if the IP address
        // matched the ReGex
        return m.matches();
    }

    public static String getClientIp() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        String ipAddress = null;
        for (NetworkInterface netint : Collections.list(nets)) {
            if (netint.isUp() && netint.getName().contains("wlan")) {
                for (InterfaceAddress ad : netint.getInterfaceAddresses()) {
                    if (isValidIPAddress(ad.getAddress().toString().replace("/", ""))) {
                        ipAddress = ad.getAddress().toString().replace("/", "");
                    }
                }
            }
        }
        return ipAddress;
    }

    public static boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            return false;
        }
        return date != null;
    }

    public void saveCustomer(JSONObject json, String username, String password, String mobileNo, Long mvnoId, String mac) {
        if(json.has("status")) {
            if(json.get("status").toString().equals("1")) {
                try {
                    JSONObject data = new JSONObject(json.get("data").toString());
                    if(username.contains("@")) {
                        data.put("email", username);
                        data.put("plan", "awfisMemberPlan");
                        data.put("subscriptionmode", CommonConstants.SubscriptionMode.MEMBER.toString());
                        data.put("mobile","9898989898");
                    } else {
                        data.put("email", "guest@awfis.com");
                        data.put("plan", "awfisGuestPlan");
                        data.put("subscriptionmode", CommonConstants.SubscriptionMode.GUEST.toString());
                        data.put("mobile", mobileNo);
//						data.put("mac", mac);
                    }
                    data.put("concurrentPolicy", "Default");
                    if(data.has("dedicatedbd")) {
                        if(data.get("dedicatedbd").toString().equals("1")) {
                            data.put("concurrentPolicy", "DedicatedDbPolicy");
                        }
                    }

                    data.put("username", username);
                    if(ValidateCrudTransactionData.validateStringTypeFieldValue(password))
                        data.put("password", password);
                    else
                        data.put("password", username.substring(0, username.indexOf("$")).trim());
                    if(json.has("cid")) {
                        data.put("cid", json.get("cid"));
                    }
                    if(json.has("locations")) {
                        data.put("locations", json.get("locations"));
                    }
                    customerService.saveCustomer(createCustomerDto(data), mvnoId.intValue() );
                } catch (Exception ex) {
                    System.out.println("exception: "+ex.getMessage());
                }
            }
        } if(json.has("error")) {
            throw new RuntimeException("Invalid login credentials.");
        }
    }

    public static CustomersPojo createCustomerDto(JSONObject json) {
        System.out.println("json: "+json.toString());
        CustomersPojo customerDto = new CustomersPojo();
        customerDto.setCountryCode("+91");
        customerDto.setStatus("Active");
//        customerDto.setSliceChunk(100L);
        customerDto.setFailcount(0);
        customerDto.setSubscriptionMode(json.getString("subscriptionmode"));
        customerDto.setCustomerType(customerDto.getSubscriptionMode());
        customerDto.setUsername(json.getString("username"));
        if(json.has("mobile"))
            customerDto.setMobile(json.getString("mobile"));
        if(json.has("email"))
            customerDto.setEmail(json.getString("email"));
        customerDto.setPassword(json.getString("password"));
        customerDto.setPlanName(json.getString("plan"));
//        customerDto.setConcurrentPolicy(json.getString("concurrentPolicy"));
        if(json.has("mac")) {
            CustMacMapppingPojo custMacMappping = new CustMacMapppingPojo();
            custMacMappping.setMacAddress(json.getString("mac"));
            List<CustMacMapppingPojo> list = new ArrayList<CustMacMapppingPojo>();
            list.add(custMacMappping);
            customerDto.setCustMacMapppingList(list);
        }
        if(json.has("valid_upto")) {
            if(isValidFormat("yyyy-MM-dd HH:mm:ss", json.getString("valid_upto")))
                customerDto.setValidUpto(json.getString("valid_upto"));
            else {
                customerDto.setValidUpto(json.getString("valid_upto")+ ":00");
            }
        }
        if(json.has("valid_from")) {
            if(isValidFormat("yyyy-MM-dd HH:mm:ss", json.getString("valid_from")))
                customerDto.setValidFrom(json.getString("valid_from"));
            else {
                customerDto.setValidFrom(json.getString("valid_from")+ ":00");
            }
        }
        if(json.has("cid")) {
            customerDto.setAddparam1(json.getString("cid"));
            customerDto.setCid(json.getString("cid"));
        }
        if(json.has("locations")) {
            List<Long> locationIds = new ArrayList<>() ;
            List<Object> spaceIds = json.getJSONArray("locations").toList();
            List<CustomerLocationMappingDto> locationMappingDtos = new ArrayList<>();
            for (Object object : spaceIds) {
                locationIds.add(object != null ? Long.parseLong(object.toString()) : null);
                CustomerLocationMappingDto locationMappingDto = new CustomerLocationMappingDto();
                locationMappingDto.setIsParentLocation(false);
                locationMappingDto.setLocationId(Long.parseLong(object.toString()));
                locationMappingDto.setIsActive(true);
                locationMappingDto.setIsDelete(false);
                locationMappingDtos.add(locationMappingDto);
            }
            customerDto.setCustomerLocations(locationMappingDtos);
            customerDto.setLocations(locationIds);
        }
        return customerDto;
    }
}
