package com.adopt.apigw.modules.Communication.Constants;

public class CommunicationConstant {
    private static String MODULE = " [CommunicationConstant] ";
    //Communication Channel
    public static String COMMUNICATION_CHANNEL_SMS = "SMS";
    public static String COMMUNICATION_CHANNEL_EMAIL = "EMAIL";
    public static String COMMUNICATION_CHANNEL_BOTH = "BOTH";

    // Thread Pool Configuration
    public static Integer CORE_POOL_SIZE = 10;
    public static Integer MAX_POOL_SIZE = 20;
    public static Integer THREAD_ALIVE_TIME = 5000;

    //EMAIL CREDENTIALS

    public static String COMM_EMAIL_PORT = "587";
    public static String COMM_EMAIL_HOST = "smtp.gmail.com";
    public static String COMM_EMAIL_USERNAME = "notification@adoptnettech.com";
    public static String COMM_EMAIL_PASSWORD = "Nat123";

    //SMS CREDENTIALS
    public static String SOURCE = "ADOPT";
    public static   String COMM_USERNAME = "test";
    public static String COMM_PASSWORD = "test";
    public static String COMM_ENTITYID = "12345345";
    public static final String SMSURL = "http://test.com:8080/bulksms/bulksms?username=" +
            CommunicationConstant.COMM_USERNAME + "&password=" + CommunicationConstant.COMM_PASSWORD + "&type=0&dlr=1&destination={destination}&source=" + CommunicationConstant.SOURCE + "&message={msg}" +
            "&entityid=" + CommunicationConstant.COMM_ENTITYID + "&tempid={templateid}";

    //Template Variables

    public static String EMAIL = "email";
    public static String MOBILE = "mobile";
    public static String DESTINATION = "destination";
    public static String USERNAME = "userName";
    public static String PASSWORD = "password";
    public static String COMPLAIN_NO = "complainNo";
    public static String CONTACT_NO = "contactNo";
    public static String WHATSAPP_NO_1 = "whatsappNo1";
    public static String WHATSAPP_NO_2 = "whatsappNo2";
    public static String CONTACT_EMAIL = "contactEmail";
    public static String PLAN_NAME = "planName";
    public static String USAGE = "usage";
    public static String EXPIRY = "expiry";
    public static String DATE = "date";
    public static String IP = "ip";
    public static String GSTIN = "gstin";
    public static String AMOUNT = "amount";
    public static String CHARGE_NAME = "chargeName";
    public static String REGISTRATION_DATE = "registerDate";
    public static String OTP = "otp";

    // Notification Id

    public static Long COMPLAIN_RESOLUTION = 1L;
    public static Long REGISTRATION = 2L;
    public static Long WIND_AND_RAINFALL = 3L;
    public static Long TICKET_CLOSED = 4L;
    public static Long SPEED_REDUCED = 5L;
    public static Long PACKGE_RENEWED_1 = 6L;
    public static Long INTERNET_ENABLED = 7L;
    public static Long PARTNER_AGGREEMENT = 8L;
    public static Long STATIC_IP_EXPIRE = 9L;
    public static Long PROFILE_UPDATE = 10L;
    public static Long ACC_SUSPENDED = 11L;
    public static Long CHARGE_RECIEVED = 12L;
    public static Long INTERNET_DISABLED = 13L;
    public static Long ACC_TERMINATED = 14L;
    public static Long ACC_REGISTERED = 15L;
    public static Long PACKGE_RENEWED_2 = 16L;
    public static Long OPEN_COMPLAINT = 17L;
    public static Long PASSWORD_OTP= 18L;
    
}
