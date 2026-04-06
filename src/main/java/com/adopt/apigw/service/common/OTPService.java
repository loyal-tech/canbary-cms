package com.adopt.apigw.service.common;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.modules.Template.domain.QTemplateNotification;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.pojo.UpdatePasswordResetDto;
import com.adopt.apigw.pojo.api.GenerateOtpDto;
import com.adopt.apigw.pojo.api.ValidateOtpDto;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.rabbitMq.message.CustomerOtpRegistrationMessage;
import com.adopt.apigw.rabbitMq.message.OtpMessage;
import com.adopt.apigw.repository.common.OTPRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.RandomStringGenerator;
import com.adopt.apigw.utils.ValidateCrudTransactionData;
import com.google.gson.Gson;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class OTPService extends AbstractService {
    private static final Logger logger = LoggerFactory.getLogger(OTPService.class);
    private static final String OTP_GENERATED = "OTP Generated";
    private static final String CUSTOMER_REGISTRATION="Customer Otp Registration";



    @Autowired
    private OTPManagmentService otpManagementService;
    @Autowired
    private OTPRepository otpRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private CustomersRepository customersRepository;

    @Transactional
    public void generateOTP(GenerateOtpDto generateOtpDto,Integer mvnoId) {
        try {
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getCountryCode()) && !generateOtpDto.getCountryCode().contains("+")) {
                System.out.println("----------Please enter valid country code with prefix '+' sign.----------");
                throw new IllegalArgumentException("Please enter valid country code with prefix '+' sign.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber()) && !ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                System.out.println("----------Please enter valid mobile number or email id.----------");
                throw new IllegalArgumentException("Please enter valid mobile number or email id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getProfile())) {
                System.out.println("----------Please enter valid otp profile name.----------");
                throw new IllegalArgumentException("Please enter valid otp profile name.");
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId()) && !ValidateCrudTransactionData.validateEmailAddress(generateOtpDto.getEmailId())) {
                System.out.println("----------Invalid email id.----------");
                throw new IllegalArgumentException("Invalid email id.");
            } else {
                OTPManagement management = Optional.ofNullable(otpManagementService.findByProfileName(generateOtpDto.getProfile(), false,mvnoId)).orElseThrow(() -> new RuntimeException(UrlConstants.EXPIRED_USER + " OTP profile not found"));
                System.out.println("---------- Found otp profile"+ management.toString()+" ----------");
                try {
                    Long buId;
                    if(getBUIdsFromCurrentStaff() != null && !getBUIdsFromCurrentStaff().isEmpty()){
                        buId = (Long)getBUIdsFromCurrentStaff().get(0);
                    } else {
                        buId = null;
                    }
//                    ForkJoinPool.commonPool().submit(() -> {
                    System.out.println("---------- calling generateOTPAsync ---------- ");
                        generateOTPAsync(management, generateOtpDto,mvnoId,buId);
//                    });
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    logger.error("Error while generate OTP: "+generateOtpDto.getMobileNumber());
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void generateOTPAsync(OTPManagement management, GenerateOtpDto generateOtpDto, Integer mvnoId, Long buId) {
        try {
            if (management.getGenerationType().equals("ALWAYS_NEW")) {

                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber())) {
                    System.out.println("---------- calling  createNewOTP ----------");
                    createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null);

                }
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                    System.out.println("---------- calling  createNewOTP ----------");
                    createNewOTP(null, null, management, generateOtpDto.getEmailId());

                }
            } else if (management.getGenerationType().equals("REUSE")) {
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber())) {
                    Optional<OTP> otpList = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getMobileNumber()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1);
                    if(otpList.isPresent()){
                        sendOtpGenerationMessage(otpList.get(),generateOtpDto.getEmailId() , mvnoId , buId);
                    }
                    else{
                        findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getMobileNumber()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null));
                    }

                }

                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {
                    Optional<OTP> otpList = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getEmailId()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1);
                    if(otpList.isPresent()){
                        sendOtpGenerationMessage(otpList.get(),generateOtpDto.getEmailId(),mvnoId,buId);
                    }
                    else{
                        findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(generateOtpDto.getEmailId()).stream().findFirst().filter(checkOTPValidity()).filter(otp -> otpRepository.updateValidTillTime(ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()), otp.getOtp()) == 1).map(otp -> otp.getOtp()).orElseGet(() -> createNewOTP(null, null, management, generateOtpDto.getEmailId()));
                    }
                }
            }
            else if (management.getGenerationType().equalsIgnoreCase(SubscriberConstants.OTP_CONSTANT_STATIC)) {


                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getMobileNumber())) {
                    saveStaticOTP(generateOtpDto.getCountryCode(), generateOtpDto.getMobileNumber(), management, null, management.getStaticOtp() ,mvnoId , buId);

                }

                if (ValidateCrudTransactionData.validateStringTypeFieldValue(generateOtpDto.getEmailId())) {

                    saveStaticOTP(generateOtpDto.getCountryCode(), null, management, generateOtpDto.getEmailId(), management.getStaticOtp() ,mvnoId , buId);
                }
            }
        } catch (Exception ex) {
            logger.error("Error While generate OTP: "+ex.getMessage());
            ex.printStackTrace();
            System.out.println(ex.getMessage());

        }
    }

    private Predicate<OTP> checkOTPValidity() {
        return otp -> otp.getOtpStatus() == OTPStatus.GENERATED && ZonedDateTime.now().isBefore(otp.getValidTillTime());
    }

    public String createNewOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId) {
        System.out.println("---------- calling  saveOTP ----------");
        return saveOTP(countryCode, mobileNumber, management, emailId).get(0).getOtp();
    }

    private List<OTP> saveOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId) {
        List<OTP> otpList = new ArrayList<>();
        String mobileEmailOTP = generateOTP(management);
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNumber))
            otpList.add(otpForMobileNo(countryCode, mobileNumber, management, mobileEmailOTP));
        else if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailId))
            otpList.add(otpForEmailId(emailId, management, mobileEmailOTP));
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        Long buId = null;
        if(getBUIdsFromCurrentStaff() != null && !getBUIdsFromCurrentStaff().isEmpty()){
            buId = (Long)getBUIdsFromCurrentStaff().get(0);
        }
        sendOtpGenerationMessage(otpList.get(0), emailId , mvnoId , buId);
        return otpList;
    }

    private OTP otpForMobileNo(String countryCode, String mobileNumber, OTPManagement management, String otpNumber) {
        try {
            System.out.println("---------- calling otpForMobileNo ---------- ");
            OTP otp = new OTP();
            otp.setMobile_email(mobileNumber);
            otp.setGeneratedTime(ZonedDateTime.now());
            otp.setValidTillTime(ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()));
            otp.setOtpStatus(OTPStatus.GENERATED);
            otp.setOtp(otpNumber);
            if (ValidateCrudTransactionData.validateStringTypeFieldValue(countryCode)) otp.setCountryCode(countryCode);
            else otp.setCountryCode(null);
            System.out.println("---------- Saving OTP for mobile -----------");
            return otpRepository.save(otp);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    private OTP otpForEmailId(String emailId, OTPManagement management, String otpNumber) {
        try {
            OTP otp = new OTP();
            otp.setMobile_email(emailId);
            otp.setGeneratedTime(ZonedDateTime.now());
            otp.setValidTillTime(ZonedDateTime.now().plusMinutes(management.getOtpValidityInMin()));
            otp.setOtpStatus(OTPStatus.GENERATED);
            otp.setOtp(otpNumber);
            return otpRepository.save(otp);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendOtpGenerationMessage(OTP otp, String emailId , Integer mvnoId , Long buId) {
        try {
          //  Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(OTP_GENERATED);
            QTemplateNotification qTemplateNotification=QTemplateNotification.templateNotification;
            BooleanExpression booleanExpression=qTemplateNotification.isNotNull();
            booleanExpression=booleanExpression.and(qTemplateNotification.templateName.eq(OTP_GENERATED));
            TemplateNotification templateNotification=templateRepository.findOne(booleanExpression).orElse(null);

//            templateNotification.getTemplateName();
            if (templateNotification!=null) {
                if (templateNotification.isSmsEventConfigured() || templateNotification.isEmailEventConfigured()) {
                    // Set message in queue to send notification after opt generated successfully.
                    String username = customersRepository.findCustomerNameByEmail(otp.getMobile_email())
                            .orElseGet(() -> customersRepository
                                    .findCustomerNameByPhone(otp.getMobile_email())
                                    .orElse(null));
                    Integer custId = Optional.ofNullable(username)
                            .map(customersRepository::findCustIdByUserName)
                            .orElse(null);
                    OtpMessage otpMessage = new OtpMessage(otp, OTP_GENERATED, RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, templateNotification, emailId,mvnoId,otp.getGeneratedTime().toString(), otp.getValidTillTime().toString(), buId,username,custId);
                    kafkaMessageSender.send(new KafkaMessageData(otpMessage,OtpMessage.class.getSimpleName(), KafkaConstant.OPT_FOR_PORTAL));

//                    messageSender.send(otpMessage, RabbitMqConstants.QUEUE_OTP_GENERATION);
                }
            } else {
                System.out.println("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateOTP(OTPManagement management) {
        return RandomStringGenerator.generate(getAllowedValues(management.getType()), management.getOtpLength());
    }

    private String getAllowedValues(List<FieldType> otpTypes) {
        return otpTypes.stream().map(otpType -> otpType.getAllowedValues()).collect(Collectors.joining());
    }

    public void validateOTP(ValidateOtpDto validateOtpDto) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber()) && !ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Please enter valid mobile number or email id.");
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && !ValidateCrudTransactionData.validateEmailAddress(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Invalid email id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getOtp())) {
                throw new IllegalArgumentException("Please enter valid otp.");
            } else {
                OTP matchedOtp = new OTP();
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber())) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getMobileNumber()).stream().findFirst().filter(otp -> OTPStatus.GENERATED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && (matchedOtp == null || matchedOtp.getOtp() == null || matchedOtp.getOtp().isEmpty())) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getEmailId()).stream().findFirst().filter(otp -> OTPStatus.GENERATED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (Objects.nonNull(matchedOtp)) {
                    matchedOtp.setOtpStatus(OTPStatus.USED);
                    otpRepository.save(matchedOtp);
                } else {
                    throw new RuntimeException( "OTP is invalid or OTP is expired.");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public void forgotPasswordOtp(ValidateOtpDto validateOtpDto) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber()) && !ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Please enter valid mobile number or email id.");
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && !ValidateCrudTransactionData.validateEmailAddress(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Invalid email id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getOtp())) {
                throw new IllegalArgumentException("Please enter valid otp.");
            } else {
                OTP matchedOtp = new OTP();
                List<Customers> customers = customersService.getCustomerByMobile(validateOtpDto.getMobileNumber());
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber())) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getMobileNumber()).stream().findFirst().filter(otp -> OTPStatus.USED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && matchedOtp == null) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getEmailId()).stream().findFirst().filter(otp -> OTPStatus.USED == otp.getOtpStatus()).filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (Objects.nonNull(matchedOtp)) {
                    matchedOtp.setOtpStatus(OTPStatus.USED);
                    UpdatePasswordResetDto updatePasswordResetDto = new UpdatePasswordResetDto();


                    boolean flag = false;
                    for(Customers customers1 : customers) {
                        if (customers1 != null) {
                            customers1.setPassword(matchedOtp.getOtp());
                            updatePasswordResetDto.setId(customers1.getId());
                            updatePasswordResetDto.setPassword(matchedOtp.getOtp());
                            updatePasswordResetDto.setMvnoId(customers1.getMvnoId().longValue());
                            flag = true;
                        }
                        if(flag)
                        {
                            updateCustomerInRadius(updatePasswordResetDto);
                        }
                        otpRepository.save(matchedOtp);
                    }
                    sendOtpCustomerRegistrationMessage(customers.get(0));
                } else {
                    throw new RuntimeException(UrlConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired.");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void updateCustomerInRadius(UpdatePasswordResetDto updatePasswordResetDto) {
        String spanId = UUID.randomUUID().toString().replaceAll("-", "");
        String traceId = UUID.randomUUID().toString().replaceAll("-", "");
        CustomMessage customMessage = new CustomMessage(updatePasswordResetDto, spanId, traceId);
        //messageSender.send(customMessage, RabbitMqConstants.QUEUE_UPDATE_CUSTOMER_PASSWORD);
        kafkaMessageSender.send(new KafkaMessageData(customMessage,customMessage.getClass().getSimpleName(),"UPDATE_CUSTOMER_PASSWORD"));
    }
    private void sendOtpCustomerRegistrationMessage(Customers customers) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository
                    .findByTemplateName(RabbitMqConstants.CUSTOMER_OTP_REGISTRATION_TEMPLATE);

            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured()
                        || optionalTemplate.get().isEmailEventConfigured()) {
                    CustomerOtpRegistrationMessage customerOtpRegistrationMessage = new CustomerOtpRegistrationMessage(RabbitMqConstants.CUSTOMER_OTP_REGISTRATION_TEMPLATE_HEADER, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, customers, (long) customersService.getLoggedInStaffId());
                    customerOtpRegistrationMessage.setEmailConfigured(false);
                     customerOtpRegistrationMessage.setSmsConfigured(true);
                    Gson gson = new Gson();
                    gson.toJson(customerOtpRegistrationMessage);
//                    messageSender.send(customerOtpRegistrationMessage, RabbitMqConstants.QUEUE_CUSTOMER_OTP_REGISTRATION);
                    kafkaMessageSender.send(new KafkaMessageData(customerOtpRegistrationMessage,CustomerOtpRegistrationMessage.class.getSimpleName() ));
                }
            } else {
                System.out.println("Message of otp Registration generated is not sent because '" + OTP_GENERATED + "' template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<OTP> findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(String mobileEmail) {
        QOTP qOtp = QOTP.oTP;
        BooleanExpression exp = qOtp.isNotNull();
        exp = exp.and(qOtp.mobile_email.eq(mobileEmail));
        return (List<OTP>) otpRepository.findAll(exp, new QSort(qOtp.generatedTime.desc()));
    }

    private List<OTP> validateOTP(String mobileEmail , String otp){
        return otpRepository.validateOtp(mobileEmail , "GENERATED" , ZonedDateTime.now() , otp);
    }

    private List<OTP> saveStaticOTP(String countryCode, String mobileNumber, OTPManagement management, String emailId , String staticOTP , Integer mvnoId  , Long buId) {
        List<OTP> otpList = new ArrayList<>();
        String mobileEmailOTP = staticOTP;
        if (ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNumber))
            otpList.add(otpForMobileNo(countryCode, mobileNumber, management, mobileEmailOTP));
        else if (ValidateCrudTransactionData.validateStringTypeFieldValue(emailId))
            otpList.add(otpForEmailId(emailId, management, mobileEmailOTP));
        ForkJoinPool.commonPool().submit(() -> {
            sendOtpGenerationMessage(otpList.get(0), emailId, mvnoId, buId);
        });
        return otpList;
    }
    public void pinOTP(ValidateOtpDto validateOtpDto) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber()) && !ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Please enter valid mobile number or email id.");
            } else if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && !ValidateCrudTransactionData.validateEmailAddress(validateOtpDto.getEmailId())) {
                throw new IllegalArgumentException("Invalid email id.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getOtp())) {
                throw new IllegalArgumentException("Please enter valid otp.");
            } else {
                OTP matchedOtp = new OTP();
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getMobileNumber())) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getMobileNumber()).stream().findFirst().filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (ValidateCrudTransactionData.validateStringTypeFieldValue(validateOtpDto.getEmailId()) && matchedOtp == null) {
                    matchedOtp = findByMvnoIdAndMobileEmailOrderByGeneratedTimeDesc(validateOtpDto.getEmailId()).stream().findFirst().filter(otp -> otp.getOtp().equals(validateOtpDto.getOtp())).filter(otp -> ZonedDateTime.now().isBefore(otp.getValidTillTime())).orElse(null);
//                            .orElseThrow(() -> new RuntimeException(WifiConstants.OTP_NOT_MATCH + "OTP is invalid or OTP is expired."));
                }
                if (Objects.nonNull(matchedOtp)) {
                    matchedOtp.setOtpStatus(OTPStatus.USED);
                    otpRepository.save(matchedOtp);
                } else {
                    throw new RuntimeException( "OTP is invalid or OTP is expired.");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected JpaRepository getRepository() {
        return null;
    }
}
