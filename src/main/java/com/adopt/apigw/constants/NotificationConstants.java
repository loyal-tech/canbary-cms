package com.adopt.apigw.constants;

import org.springframework.http.HttpStatus;

public class NotificationConstants {
    private NotificationConstants() {
        throw new IllegalStateException("Utility class");
    }
    public static final Integer SUCCESS= HttpStatus.OK.value();
    public static final Integer FAIL=HttpStatus.BAD_REQUEST.value();
    public static final Integer INTERNAL_SERVER_ERROR=HttpStatus.INTERNAL_SERVER_ERROR.value();
    public static final String ERROR_TAG="ERROR";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String MESSAGE = "message";
    public static final String BLANK_STRING = "string";
    public static final String IN_ACTIVE = "Inactive";
    public static final String ACTIVE = "Active";
    public static final String VALIDATION_REASON = "ValidationReason";
    public static final String BASIC_STRING_MSG = "BasicStringMsg";
    public static final String BASIC_NUMERIC_MSG = "BasicNumericMsg";
    public static final String VALIDATION_REASON_BASIC_STRING_MESSAGE="Null, blank value and 'string' are not allowed";
    public static final String VALIDATION_REASON_BASIC_NUMERIC_MESSAGE="Null and 0 are not allowed";
    public static final String FROM_NUMBER="FROM_NUMBER";
    //public static final String FROM_EMAIL_ADDRESS="adoptnotificationfortesting@gmail.com";
    //public static final String FROM_EMAIL_PASSWORD="adopt@123";
    public static final String ACCOUNT_SID = "ACCOUNT_SID";
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String AUTH_PARAM = "mail.smtp.auth";
    public static final String STARTTLS_PARAM = "mail.smtp.starttls.enable";
    public static final String SSL_PARAM = "mail.smtp.ssl.enable";
    public static final String AUTH_TYPE_VALUE ="true";
    public static final String HOST_PARAM = "mail.smtp.host";
    public static final String PORT_PARAM = "mail.smtp.port";
    public static final String SENDER = "Adopt NetTech";
    public static final String WEB = "Adopt";
    public static final String LOGIN_FAILURE_EVENT = "Login Failure";
    public static final String LOGIN_SUCCESS_EVENT = "Login Success";
    public static final String CUSTOMER_REGISTRATION_SUCCESS_EVENT="Registration Success";
    public static final String CUSTOMER_REGISTRATION_FAILURE_EVENT="Registration Failure";
    public static final String OTP_GENERATED_EVENT="OTP Generated";
}
