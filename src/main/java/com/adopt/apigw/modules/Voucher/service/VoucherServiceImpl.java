package com.adopt.apigw.modules.Voucher.service;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.FieldType;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.QPostpaidPlan;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.Voucher.domain.QVoucher;
import com.adopt.apigw.modules.Voucher.domain.Voucher;
import com.adopt.apigw.modules.Voucher.module.*;
import com.adopt.apigw.modules.Voucher.repository.VoucherRepository;
import com.adopt.apigw.modules.VoucherBatch.domain.BSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.domain.QBSSVoucherBatch;
import com.adopt.apigw.modules.VoucherBatch.repository.BSSVoucherBatchRepository;
import com.adopt.apigw.modules.VoucherBatch.service.VoucherBatchService;
import com.adopt.apigw.modules.VoucherConfiguration.domain.QVoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.domain.VoucherConfiguration;
import com.adopt.apigw.modules.VoucherConfiguration.service.VoucherConfigurationService;
import com.adopt.apigw.modules.servicePlan.domain.Services;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.VoucherCodeMessage;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.EncryptVoucher;
import com.adopt.apigw.utils.RandomStringGenerator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class VoucherServiceImpl implements VoucherService {

    private static final Log log = LogFactory.getLog(VoucherServiceImpl.class);
    private static final String SEND_VOUCHER_CODE = "Voucher Code";
    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private SimpleEncryptor simpleEncryptor;

    @Autowired
    private APIResponseController responseController;

    @Autowired
    private PostpaidPlanService planService;

    @Autowired
    private VoucherConfigurationService voucherConfigurationService;

    @Autowired
    private VoucherBatchService voucherBatchService;

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    private NotificationTemplateRepository templateRepository;

    @Autowired
    BSSVoucherBatchRepository voucherBatchRepository;


    @Autowired
    PostpaidPlanRepo planRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private  EncryptVoucher encryptVoucher;
    @Transactional
    @Override
    public void generateBatch(Long batchId, Long configId, Long mvnoId) {
        try {
            log.info("Genearating voucher batch for " + batchId);
            BSSVoucherBatch voucherBatch = voucherBatchService.findVoucherBatchById(batchId, mvnoId);
            VoucherConfiguration configuration = voucherConfigurationService.findById(configId, mvnoId);
//            LongStream.range(0, configuration.getNoOfVoucher()).forEach(l -> saveVoucher(voucherBatch, configuration, mvnoId));
            if(getBUIdsFromCurrentStaff().size()==1){
                List<Voucher> list = saveVoucher(voucherBatch, configuration, mvnoId,getBUIdsFromCurrentStaff().get(0), configuration.getNoOfVoucher()).stream().collect(
                        Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(Voucher::getCode)))).stream().collect(Collectors.toList());
                //find out all new codes
                List<String> codeList = list.stream()
                        .map(Voucher::getCode)
                        .collect(Collectors.toList());
                //fetch all existing vouchers
                List<String> existingList = voucherRepository.findByCodeIn(codeList);

                //remove object if new code exists in existing vouchers
                if(!CollectionUtils.isEmpty(existingList))
                    list.removeIf( v-> existingList.contains(v.getCode()));
                Long diff = Long.valueOf(configuration.getNoOfVoucher().intValue() - list.size());
                System.out.println(" before add all list size: "+list.size());
                if(diff > 0) {
                    list = generateRemainingVoucher(list, voucherBatch, configuration, mvnoId, getBUIdsFromCurrentStaff().get(0), diff);
                }
                list.sort(Comparator.comparingLong((Voucher v) -> {
                    String code = v.getSerial_number(); // e.g. "card2_00000000001"
                    if (code == null || !code.contains("_")) return Long.MIN_VALUE;

                    try {
                        String lastPart = code.substring(code.lastIndexOf("_") + 1).trim();
                        return Long.parseLong(lastPart);
                    } catch (Exception e) {
                        return Long.MIN_VALUE;
                    }
                }));

                System.out.println(" after add all list size: "+list.size());
                voucherRepository.saveAll(list);
            }
            else if(getBUIdsFromCurrentStaff().size()==0){
                List<Voucher> list = saveVoucher(voucherBatch, configuration, mvnoId,null, configuration.getNoOfVoucher()).stream().collect(
                        Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(Voucher::getCode)))).stream().collect(Collectors.toList());

                //find out all new codes
                List<String> codeList = list.stream()
                        .map(Voucher::getCode)
                        .collect(Collectors.toList());
                //fetch all existing vouchers
                List<String> existingList = voucherRepository.findByCodeIn(codeList);

                //remove object if new code exists in existing vouchers
                if(!CollectionUtils.isEmpty(existingList))
                    list.removeIf( v-> existingList.contains(v.getCode()));
                Long diff = Long.valueOf(configuration.getNoOfVoucher().intValue() - list.size());
//                System.out.println(" before add all list size: "+list.size());
                if(diff > 0) {
                    list = generateRemainingVoucher(list, voucherBatch, configuration, mvnoId, null, diff);
                }
                System.out.println(" after add all list size: "+list.size());
                list.sort(Comparator.comparingLong((Voucher v) -> {
                    String code = v.getSerial_number(); // e.g. "card2_00000000001"
                    if (code == null || !code.contains("_")) return Long.MIN_VALUE;

                    try {
                        String lastPart = code.substring(code.lastIndexOf("_") + 1).trim();
                        return Long.parseLong(lastPart);
                    } catch (Exception e) {
                        return Long.MIN_VALUE;
                    }
                }));

                voucherRepository.saveAll(list);
            }else{
                throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);
            }


        } catch (Throwable e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private List<Voucher> generateRemainingVoucher(List<Voucher> existingVouchers, BSSVoucherBatch voucherBatch, VoucherConfiguration configuration, Long mvnoId, Long buId, Long diff) {
        int i = 1;
        while (diff > 0) {
            Long noOfVouchers = diff;
            System.out.println("noOfVouchers: "+noOfVouchers);
            List<Voucher> vouchers =
                    saveVoucher(voucherBatch, configuration, mvnoId,null, noOfVouchers).stream().collect(
                            Collectors.toCollection(()->new TreeSet<>(Comparator.comparing(Voucher::getCode)))).stream().collect(Collectors.toList());
            //find out all new codes
            existingVouchers.addAll(vouchers);
            List<String> codeList = existingVouchers.stream()
                    .map(Voucher::getCode)
                    .collect(Collectors.toList());
            //fetch all existing vouchers
            List<String> existingList = voucherRepository.findByCodeIn(codeList);

            //remove object if new code exists in existing vouchers
            if(!CollectionUtils.isEmpty(existingList)) {
                existingVouchers.removeIf( v-> existingList.contains(v.getCode()));
            }

            diff = configuration.getNoOfVoucher() - existingVouchers.size();
            System.out.println(" creating remaining vouchers diff "+diff);
            System.out.println(" Loop number: "+i);
            i++;
            if(configuration.getNoOfVoucher() < i) {
                throw new RuntimeException("voucher code combination not available, Please change voucher code combination");
            }
        }
        return existingVouchers;
    }

    private List<Voucher> saveVoucher(BSSVoucherBatch voucherBatch, VoucherConfiguration configuration, Long mvnoId, Long buId, Long noOfVouchers) {
        try {
             Set<String> existingBatches = new HashSet<>();
            List<Voucher> vouchers=voucherRepository.getVoucherBatch(voucherBatch.getVoucherBatchId());
            if(vouchers.size()>0 && Objects.nonNull(vouchers.get(vouchers.size()-1).getSerial_number())){
                existingBatches.addAll(vouchers.stream().map(i->i.getSerial_number()).collect(Collectors.toList()));
            }else{
                existingBatches.add(voucherBatch.getBatchName()+"_"+0000000);
            }

            List<Voucher> list = new ArrayList<Voucher>();
            for (int i = 0; i < noOfVouchers; i++) {
                String code = generateCode(configuration);
                Voucher voucher = new Voucher();
                voucher.setVoucherBatch(voucherBatch);
                voucher.setStatus(VoucherStatus.GENERATED);
                voucher.setSerial_number(generateNextUniqueBatch(voucherBatch.getBatchName(),existingBatches));
                try {
                    voucher.setCode(encryptVoucher.encrypt(code));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                voucher.setCreatedOn(LocalDateTime.now());
                voucher.setCreatedBy("admin admin");
                voucher.setMvnoId(mvnoId);
                voucher.setBuId(buId);
                voucher.setBatchName(voucherBatch.getBatchName());
                voucher.setCreatedByStaffId(planService.getLoggedInUser().getStaffId());
                list.add(voucher);
            }
            return list;
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private String generateCode(VoucherConfiguration configuration) {
        String code =  RandomStringGenerator.generate(getAllowedValues(configuration.getVoucherCodeFormat()), configuration.getVoucherCodeLength());
        return Stream.of(configuration.getPrefix(), code, configuration.getSuffix()).filter(Objects::nonNull).collect(Collectors.joining());

    }

    private String getAllowedValues(List<FieldType> otpTypes) {
        return otpTypes.stream().map(FieldType::getAllowedValues).collect(Collectors.joining());
    }

    @Override
    public ResponseEntity<Map<String, Object>> validateVoucher(String code, Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_VALIDATE);
        try {
            QVoucher qVoucher = QVoucher.voucher;
            BooleanExpression expressionForVoucher = qVoucher.isNotNull();
            if (mvnoId != 1) {
                expressionForVoucher = expressionForVoucher.and(qVoucher.mvnoId.eq(mvnoId));
            }
            try {
                code=  encryptVoucher.encrypt(code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            expressionForVoucher = expressionForVoucher.and(qVoucher.code.eq(code));
            Voucher voucher = voucherRepository.findOne(expressionForVoucher).orElseThrow(() -> new RuntimeException("Voucher Code not found"));
            Long voucherBatchId = voucherRepository.getVoucherBatchById(code);
            BSSVoucherBatch voucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchId);
            if (voucher.getStatus() == VoucherStatus.INVALID) {
                response.put(APIConstants.MESSAGE, " Voucher Verificaton Failed");
                log.info("Voucher Verificaton Failed: '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(470, response);
            } else if (voucher.getStatus() == VoucherStatus.USED) {
                response.put(APIConstants.MESSAGE, " Voucher code is already in used");
                log.info("Voucher is already activated '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(472, response);
            } else if (voucher.getStatus() == VoucherStatus.EXPIRED) {
                response.put(APIConstants.MESSAGE, "Voucher Validity Expired");
                log.info("Voucher Validity Expired '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(474, response);
            } else if (voucher.getStatus() == VoucherStatus.BLOCKED) {
                response.put(APIConstants.MESSAGE, "Voucher is Blocked");
                log.info("Voucher is Blocked '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(487, response);
            } else if (voucher.getStatus() == VoucherStatus.INACTIVE) {
                response.put(APIConstants.MESSAGE, "Voucher is Inactive");
                log.error("Voucher is Inactive '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(488, response);
            } else if (voucher.getStatus() == VoucherStatus.GENERATED) {
                response.put(APIConstants.MESSAGE, "Voucher is Inactive");
                log.error("Voucher is Inactive '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(488, response);
            }else if (voucherBatch.getExpirydate().isBefore(LocalDateTime.now())) {
                response.put(APIConstants.MESSAGE, "Voucher is Expired");
                log.error("Voucher is Expired '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(490, response);
            } else if (voucher.getStatus() == VoucherStatus.SCRAPPED) {
                response.put(APIConstants.MESSAGE, "Voucher is Scrapped");
                log.error("Voucher is Scrapped '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(489, response);
            } else if (voucher.getStatus() == VoucherStatus.ACTIVE) {
                PostpaidPlan postpaidPlan = voucherBatchRepository.findByPlanByVoucherBatch(voucherBatchId);
                if(Objects.nonNull(postpaidPlan)) {
                    Services services = serviceRepository.findById(Long.valueOf(postpaidPlan.getServiceId())).get();
                    postpaidPlan.setServiceName(services.getServiceName());
                    response.put(APIConstants.MESSAGE, " Valid voucher");
                    response.put("plan", postpaidPlan);
                    response.put("serviceName", services.getServiceName());
                }else{
                    response.put("plan", null);
                    response.put("serviceName", null);
                }
                response.put("voucher",voucher);
                voucher.setStatus(VoucherStatus.USED);
                Long voucherBatchById =  voucherRepository.getVoucherBatchById(code);
                BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                voucher.setVoucherBatch(bssVoucherBatch);
                voucher.setVoucherUsedDate(LocalDateTime.now());
                voucherRepository.save(voucher);
                log.info("Voucher code is verified and it is valid '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(200, response);
            } else {
                response.put(APIConstants.MESSAGE, "Server error");
                log.error("Server error when validate voucher ");
                return responseController.apiResponse(490, response);
            }
        } catch (Throwable e) {
            log.equals("Error while validate voucher code: " + code);
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> verifyVoucher(String code, Long mvnoId) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_VALIDATE);
        try {
            QVoucher qVoucher = QVoucher.voucher;
            BooleanExpression expressionForVoucher = qVoucher.isNotNull();
            if (mvnoId != 1) {
                expressionForVoucher = expressionForVoucher.and(qVoucher.mvnoId.eq(mvnoId));
            }
            try {
                expressionForVoucher = expressionForVoucher.and(qVoucher.code.eq(encryptVoucher.encrypt(code)));
                Voucher voucher = voucherRepository.findOne(expressionForVoucher).orElseThrow(() -> new RuntimeException("Voucher Code not found"));
                Long voucherBatchId = voucherRepository.getVoucherBatchById(encryptVoucher.encrypt(code));
                BSSVoucherBatch   voucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchId);
                voucher.setVoucherBatch(voucherBatch);
            if (voucher.getStatus() == VoucherStatus.INVALID) {
                response.put(APIConstants.MESSAGE, " Voucher Verificaton Failed");
                log.info("Voucher Verificaton Failed: '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(470, response);
            } else if (voucher.getStatus() == VoucherStatus.USED) {
                response.put(APIConstants.MESSAGE, " Voucher code is already in used");
                log.info("Voucher is already activated '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(APIConstants.NOT_FOUND, response);
            } else if (voucher.getStatus() == VoucherStatus.EXPIRED) {
                response.put(APIConstants.MESSAGE, "Voucher Validity Expired");
                log.info("Voucher Validity Expired '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(474, response);
            } else if (voucher.getStatus() == VoucherStatus.BLOCKED) {
                response.put(APIConstants.MESSAGE, "Voucher is Blocked");
                log.info("Voucher is Blocked '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(487, response);
            } else if (voucher.getStatus() == VoucherStatus.INACTIVE) {
                response.put(APIConstants.MESSAGE, "Voucher is Inactive");
                log.error("Voucher is Inactive '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(488, response);
            } else if (voucher.getStatus() == VoucherStatus.GENERATED) {
                response.put(APIConstants.MESSAGE, "Voucher is Inactive");
                log.error("Voucher is Inactive '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(488, response);
            }else if (voucherBatch.getExpirydate().isBefore(LocalDateTime.now())) {
                response.put(APIConstants.MESSAGE, "Voucher is Expired");
                log.error("Voucher is Expired '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(490, response);
            } else if (voucher.getStatus() == VoucherStatus.SCRAPPED) {
                response.put(APIConstants.MESSAGE, "Voucher is Scrapped");
                log.error("Voucher is Scrapped '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(489, response);
            } else if (voucher.getStatus() == VoucherStatus.ACTIVE) {
                PostpaidPlan postpaidPlan = voucherBatchRepository.findByPlanByVoucherBatch(voucherBatchId);
                if (Objects.nonNull(postpaidPlan)) {
                    Services services = serviceRepository.findById(Long.valueOf(postpaidPlan.getServiceId())).get();
                    postpaidPlan.setServiceName(services.getServiceName());
                    response.put(APIConstants.MESSAGE, "Valid voucher");
                    response.put("plan", postpaidPlan);
                }else{
                    response.put("plan", null);
                }
                response.put("voucher", voucher);
                log.info("Voucher code is verified and it is valid '" + voucher.getCode() + "' by: " + MDC.get("username"));
                return responseController.apiResponse(200, response);
            } else {
                response.put(APIConstants.MESSAGE, "Server error");
                log.error("Server error when validate voucher ");
                return responseController.apiResponse(490, response);
            }
            } catch (Exception e) {
                throw new CustomValidationException(APIConstants.NOT_FOUND,e.getMessage(),null);
            }
        } catch (Throwable e) {
            log.equals("Error while validate voucher code: " + code);
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }




    @Override
    public void sendSms(Long id, String countryCode, String mobileNo, String code, Long mvnoId) {
        try {
            String voucherCode = "";
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(mobileNo)) {
                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Please enter valid Mobile No.");
            }
            try {
                voucherCode=encryptVoucher.decrypt(code);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            Optional<TemplateNotification> optionalTemplate = templateRepository
                    .findByTemplateName(RabbitMqConstants.CUSTOMER_VOUCHER_TEMPLATE);
            if(optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    Long buId = null;
                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
                        buId = getBUIdsFromCurrentStaff().get(0);
                    }
                    System.out.println("Voucher Message send"+voucherCode);
                    log.info("Voucher Message send"+voucherCode);
                    /** Send Voucher Message  for customer**/
                    VoucherCodeMessage voucherCodeMessage = new VoucherCodeMessage(countryCode, mobileNo, voucherCode, mvnoId, RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, RabbitMqConstants.CUSTOMER_VOUCHER_TEMPLATE, optionalTemplate.get() , buId,null);
                    kafkaMessageSender.send(new KafkaMessageData(voucherCodeMessage, VoucherCodeMessage.class.getSimpleName()));
//                    messageSender.send(voucherCodeMessage, RabbitMqConstants.QUEUE_SEND_VOUCHERCODE);
                    System.out.println("Voucher Message send");
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String changeStatusToActive(List<Long> voucherIdList, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (voucherIdList.isEmpty()) {
                throw new RuntimeException("Please select Voucher.");
            } else {
                int successCount = 0;
                int failedCount = 0;
                String msg = "";
                for (int i = 0; i < voucherIdList.size(); i++) {
                    Optional<Voucher> voucher = voucherRepository.findById(voucherIdList.get(i));
                    if (voucher.isPresent()) {
                        Voucher voucherVo = voucher.get();
                        if (voucherVo.getStatus() == VoucherStatus.GENERATED || voucherVo.getStatus() == VoucherStatus.BLOCKED) {
                            voucherVo.setStatus(VoucherStatus.ACTIVE);
                           Long voucherBatchById =  voucherRepository.getVoucherBatchById(voucher.get().getCode());
                           BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                           voucherVo.setVoucherBatch(bssVoucherBatch);
                            voucherRepository.save(voucherVo);
                            successCount = successCount + 1;
                        } else {
//                            count = count + 1;
//							throw new RuntimeException(
//									"Only Generated voucher can be Active. Please try differnet voucher code");
                            failedCount = failedCount + 1;
                        }
                    }
                }
                if (successCount > 0 && failedCount >0) {
                    msg = "Voucher Status has been change to Active. " + failedCount  + " voucher status is not changed because Only Generated/Blocked voucher can be Active";
                } else if(successCount > 0 && failedCount == 0) {
                    msg = "Voucher Status has been change to Active. ";
                }else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                            "Only Generated voucher can be Active. Please try differnet voucher code", null);
                }
                return msg;
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String changeStatusToBlock(List<Long> voucherIdList, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (voucherIdList.isEmpty()) {
                throw new RuntimeException("Please select Voucher.");
            } else {
                int successCount = 0;
                int failedCount = 0;
                String msg = "";
                for (int i = 0; i < voucherIdList.size(); i++) {
                    Optional<Voucher> voucher = voucherRepository.findById(voucherIdList.get(i));
                    if (voucher.isPresent()) {
                        Voucher voucherVo = voucher.get();
                        if (voucherVo.getStatus() == VoucherStatus.GENERATED) {
                            voucherVo.setStatus(VoucherStatus.BLOCKED);
                            Long voucherBatchById = voucherRepository.getVoucherBatchById(voucher.get().getCode());
                            BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                            voucherVo.setVoucherBatch(bssVoucherBatch);
                            voucherRepository.save(voucherVo);
                            successCount = successCount + 1;
                        } else if (voucherVo.getStatus() == VoucherStatus.ACTIVE) {
                            voucherVo.setStatus(VoucherStatus.BLOCKED);
                            Long voucherBatchById = voucherRepository.getVoucherBatchById(voucher.get().getCode());
                            BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                            voucherVo.setVoucherBatch(bssVoucherBatch);
                            voucherRepository.save(voucherVo);
                            successCount = successCount + 1;
                        } else {
//                            count = count + 1;
//							throw new RuntimeException(
//									"Only Generated and Active voucher can be Block. Please try differnet voucher code");
                            failedCount = failedCount + 1;
                        }
                    }
                }
                if (successCount > 0 && failedCount > 0) {
                    msg = "Voucher Status has been change to Blocked. " + failedCount + " voucher status is not changed because Only Generated and Active voucher can be Block.";
                } else if (successCount > 0 && failedCount == 0) {
                    msg = "Voucher Status has been change to Blocked.";
                } else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                            "Only Generated and Active voucher can be Block. Please try differnet voucher code", null);
                }
                return msg;
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String changeStatusToUnblock(List<Long> voucherIdList, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (voucherIdList.isEmpty()) {
                throw new RuntimeException("Please select Voucher.");
            } else {
                int successCount = 0;
                int failedCount = 0;
                String msg = "";
                for (Long aLong : voucherIdList) {
                    Optional<Voucher> voucher = voucherRepository.findById(aLong);
                    if (voucher.isPresent()) {
                        Voucher voucherVo = voucher.get();
                        if (voucherVo.getStatus() == VoucherStatus.BLOCKED) {
                            voucherVo.setStatus(VoucherStatus.ACTIVE);
                            Long voucherBatchById = voucherRepository.getVoucherBatchById(voucher.get().getCode());
                            BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                            voucherVo.setVoucherBatch(bssVoucherBatch);
                            voucherRepository.save(voucherVo);
                            successCount = successCount + 1;
                        } else {
                            failedCount = failedCount + 1;
                        }
                    }
                }
                if (successCount > 0 && failedCount >0) {
                    msg = "Voucher Status has been change to Active. " + failedCount + " voucher status is not changed because Only Blocked voucher can be Unblock.";
                } else if(successCount > 0 && failedCount == 0) {
                    msg = "Voucher Status has been change to Active. ";
                }else{
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
									"Only Blocked voucher can be Unblock. Please try differnet voucher code", null);
                }
                return msg;
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String changeStatusToScrap(List<Long> voucherIdList, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (voucherIdList.isEmpty()) {
                throw new RuntimeException("Please select Voucher.");
            } else {
                int successCount = 0;
                int failedCount = 0;
                String msg = "";
                ;
                for (Long aLong : voucherIdList) {
                    Optional<Voucher> voucher = voucherRepository.findById(aLong);
                    if (voucher.isPresent()) {
                        Voucher voucherVo = voucher.get();
                        if (voucherVo.getStatus() == VoucherStatus.USED) {
                            failedCount = failedCount + 1;
                            msg = "Used voucher can not be Scrap. Please try differnet voucher code";
                        } else if (voucherVo.getStatus() == VoucherStatus.SCRAPPED) {
                            msg = "Scrap voucher can not be Scrap. Please try differnet voucher code";
                        } else {
                            voucherVo.setStatus(VoucherStatus.SCRAPPED);
                            Long voucherBatchById =  voucherRepository.getVoucherBatchById(voucher.get().getCode());
                            BSSVoucherBatch bssVoucherBatch = voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchById);
                            voucherVo.setVoucherBatch(bssVoucherBatch);
                            voucherRepository.save(voucherVo);
                            successCount = successCount + 1;
                        }
                    }
                }
                if (successCount > 0 && failedCount >0) {
                    msg = "Voucher Status has been change to Scrapped. " + failedCount + " voucher status is not changed because Only Used voucher can not be Scrap.";
                } else if(successCount > 0 && failedCount == 0) {
                    msg = "Voucher Status has been change to Scrapped. ";
                }else {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),
                            msg, null);
                }
                return msg;
            }
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), null);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Voucher> getAllVouchers(Long mvnoId, PaginationDTO paginationDTO, Long resellerId)
    {
        try
        {
            log.info("getting all vouchers");
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId))
            {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            else
            {
                QVoucher qVoucher = QVoucher.voucher;
                QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
                BooleanExpression expForVoucherBach = qVoucherBatch.isNotNull();
                BooleanExpression expForVoucher = qVoucher.isNotNull();
                //List<Long> planIds = planService.getPlans(mvnoId, locationId).stream().map(Plan::getPlanId).collect(Collectors.toList());
                //BooleanExpression expForVoucherBatch = qVoucherBatch.isNotNull().and(qVoucherBatch.plan.planId.in(planIds));
                //List<Long> voucherBatchId = Lists.newArrayList(voucherBatchRepository.findAll(expForVoucherBatch)).stream().map(VoucherBatch::getVoucherBatchId).collect(Collectors.toList());
                if(resellerId != null)
                {
                    expForVoucherBach = expForVoucherBach.and(qVoucherBatch.reseller.resellerId.eq(resellerId));
                }
                if (mvnoId != 1) {
                    expForVoucherBach = expForVoucherBach.and(qVoucherBatch.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                }

                if(getLoggedInUserPartnerId()!=1)
                {
                    expForVoucherBach = expForVoucherBach.and(qVoucherBatch.createdByStaffId.eq(getLoggedInUser().getStaffId()));
                }

                List<BSSVoucherBatch> voucherBatchList = (List<BSSVoucherBatch>) voucherBatchRepository.findAll(expForVoucherBach);
                List<Long> voucherBatchIdList = new ArrayList<>();
                for (BSSVoucherBatch voucherBatch : voucherBatchList)
                {
                    voucherBatchIdList.add(voucherBatch.getVoucherBatchId());
                }
                expForVoucher = expForVoucher.and(qVoucher.voucherBatch.voucherBatchId.in(voucherBatchIdList));
                if (mvnoId != 1) {
                    expForVoucher = expForVoucher.and(qVoucher.mvnoId.in(ValidateCrudTransactionData.validateMvnoId(mvnoId), 1));
                }
                if(getBUIdsFromCurrentStaff().size()!=0){
                    expForVoucher = expForVoucher.and(qVoucher.buId.in(getBUIdsFromCurrentStaff()));
                }
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
                if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                    expForVoucher = expForVoucher.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qVoucher.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
                }
                if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                    expForVoucher = expForVoucher.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qVoucher.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
                }
                Page<Voucher> vouchers = voucherRepository.findAll(expForVoucher, pageable);
                vouchers.forEach(voucher ->
                {
                    voucher.setCode(voucher.getCode());
                    if(voucher.getVoucherBatch().getExpirydate().isBefore(LocalDateTime.now()) &&(voucher.getStatus().equals(VoucherStatus.ACTIVE) || voucher.getStatus().equals(VoucherStatus.GENERATED))){
                      voucher.setStatus(VoucherStatus.EXPIRED);
                      Long voucherBatchId = voucherRepository.getVoucherBatchById(voucher.getCode());
                      BSSVoucherBatch bssVoucherBatch =  voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchId);
                      voucher.setVoucherBatch(bssVoucherBatch);
                      voucherRepository.save(voucher);

                  }

                });
                return vouchers;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Voucher> findVouchers(String batchName, String status, Long mvnoId, PaginationDTO paginationDTO) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                List<Voucher> voucherList = new ArrayList<>();
                QVoucher qVoucher = QVoucher.voucher;
                BooleanExpression boolExp = qVoucher.isNotNull();
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Page<Voucher> page = null;
                voucherBatchService.findAllVoucherBatch(mvnoId);
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
                if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qVoucher.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
                }
                if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qVoucher.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
                }
                if ((!ValidateCrudTransactionData.validateStringTypeFieldValue(batchName) || batchName.equalsIgnoreCase("null")) && (!ValidateCrudTransactionData.validateStringTypeFieldValue(status) || status.equalsIgnoreCase("null"))) {
                    if (mvnoId == 1) {
                        page = voucherRepository.findAll(boolExp, pageable);
                        //voucherList = voucherRepository.findAll();
                    } else {
                        boolExp = boolExp.and(qVoucher.mvnoId.eq(mvnoId)).or(qVoucher.mvnoId.eq(1L));
                        if(getBUIdsFromCurrentStaff().size()!=0){
                            boolExp = boolExp.and(qVoucher.buId.in(getBUIdsFromCurrentStaff()));
                        }
                        page = voucherRepository.findAll(boolExp, pageable);
                        //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                    }
                } else {
//                    if ((ValidateCrudTransactionData.validateStringTypeFieldValue(batchName) && batchName != "null") && (ValidateCrudTransactionData.validateStringTypeFieldValue(status) && status != "null")) {
//                        List<VoucherBatch> voucherBatchList = voucherBatchService.searchVoucherBatch(batchName, mvnoId);
//                        Set<Voucher> voucherSet = new HashSet<>();
//                        for (VoucherBatch voucherBatch : voucherBatchList) {
//                        	page = findVouchersByBatchId(voucherBatch.getVoucherBatchId(), mvnoId,paginationDTO);
//                           // vouchers.removeIf(voucher -> !voucher.getStatus().getName().equals(status));
//                            //voucherSet.addAll(vouchers);
//                        }
//                        //page = vouchers;
//
//                    } else
                    if ((ValidateCrudTransactionData.validateStringTypeFieldValue(batchName) && batchName != "null")) {
                        if(getLoggedInUserPartnerId()!=1)
                        {
                            boolExp = boolExp.and(qVoucher.createdByStaffId.eq(getLoggedInUser().getStaffId()));
                        }
                        
                        if (mvnoId == 1) {
                            boolExp = boolExp.and(qVoucher.batchName.contains(batchName));
                            page = voucherRepository.findAll(boolExp, pageable);
                            //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                        } else {
                            boolExp = boolExp.and(qVoucher.batchName.contains(batchName)).and(qVoucher.mvnoId.eq(mvnoId).or(qVoucher.mvnoId.eq(1L)));
                            page = voucherRepository.findAll(boolExp, pageable);
                            //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                        }
                    }  if ((ValidateCrudTransactionData.validateStringTypeFieldValue(status) && status != "null"))
                    {
                        if(getLoggedInUserPartnerId()!=1)
                        {
                            boolExp = boolExp.and(qVoucher.createdByStaffId.eq(getLoggedInUser().getStaffId()));
                        }

                        if (mvnoId == 1) {
                            boolExp = boolExp.and(qVoucher.status.eq(VoucherStatus.valueOf(status)));
                            page = voucherRepository.findAll(boolExp, pageable);
                        } else {
                            boolExp = boolExp.and(qVoucher.status.eq(VoucherStatus.valueOf(status)).and(qVoucher.mvnoId.eq(mvnoId).or(qVoucher.mvnoId.eq(1L))));
                            page = voucherRepository.findAll(boolExp, pageable);
                        }
                    }
                }

                if (Objects.nonNull(page))
                    page.forEach(voucher ->{
                        Long voucherBatchId = voucherRepository.getVoucherBatchById(voucher.getCode());
                        BSSVoucherBatch bssVoucherBatch =  voucherBatchRepository.findVoucherBatchByBatchId(voucherBatchId);
                        voucher.setVoucherBatch(bssVoucherBatch);

                    });
                return page;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Voucher> findVouchersByBatchId(Long batchId, Long mvnoId, PaginationDTO paginationDTO) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                List<Voucher> voucherList = new ArrayList<>();
                voucherBatchService.findAllVoucherBatch(mvnoId);
                QVoucher qVoucher = QVoucher.voucher;
                BooleanExpression boolExp = qVoucher.isNotNull();
                if (paginationDTO.getPage() > 0) {
                    paginationDTO.setPage(paginationDTO.getPage() - 1);
                }
                Page<Voucher> page = null;
                Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastModifiedOn"));
                if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qVoucher.lastModifiedOn.after((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
                }
                if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                    boolExp = boolExp.and(qVoucher.lastModifiedOn.eq((Expression<? super LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qVoucher.lastModifiedOn.before((Expression<LocalDateTime>) Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
                }
                if (!ValidateCrudTransactionData.validateLongTypeFieldValue(batchId)) {
                    if (mvnoId == 1) {
                        page = voucherRepository.findAll(boolExp, pageable);
                        //voucherList = voucherRepository.findAll();
                    } else {
                        boolExp = boolExp.and(qVoucher.mvnoId.eq(mvnoId)).or(qVoucher.mvnoId.eq(1L));
                        page = voucherRepository.findAll(boolExp, pageable);
                        //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                    }
                } else {
                    if ((ValidateCrudTransactionData.validateLongTypeFieldValue(batchId) && batchId != 0)) {
                        if (mvnoId == 1) {
                            boolExp = boolExp.and(qVoucher.voucherBatch.voucherBatchId.eq(batchId));
                            page = voucherRepository.findAll(boolExp, pageable);
                            //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                        } else {
                            boolExp = boolExp.and(qVoucher.voucherBatch.voucherBatchId.eq(batchId)).and(qVoucher.mvnoId.eq(mvnoId).or(qVoucher.mvnoId.eq(1L)));
                            page = voucherRepository.findAll(boolExp, pageable);
                            //voucherList = (List<Voucher>) voucherRepository.findAll(boolExp);
                        }
                    }

                }
                BSSVoucherBatch bssVoucherBatch =  voucherBatchRepository.findVoucherBatchByBatchId(batchId);
                if (Objects.nonNull(page))
                    page.forEach(voucher -> {voucher.setVoucherBatch(bssVoucherBatch);
                                             if(voucher.getVoucherBatch().getExpirydate().isBefore(LocalDateTime.now()) && (voucher.getStatus().equals(VoucherStatus.ACTIVE) || voucher.getStatus().equals(VoucherStatus.GENERATED))){
                                                 voucher.setStatus(VoucherStatus.EXPIRED);
                                                 voucher.setVoucherBatch(bssVoucherBatch);
                                                 voucherRepository.save(voucher);
                                             }
                    });
                return page;
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Integer countByBatchId(Long batchId) {
        QVoucher qVoucher = QVoucher.voucher;
        BooleanExpression expressionForVoucher = qVoucher.isNotNull().and(qVoucher.voucherBatch.voucherBatchId.eq(batchId));
        return Math.toIntExact(Stream.of(voucherRepository.findAll(expressionForVoucher)).count());
    }

    @Override
    public List<Map<String, String>> dataToExport(String batchName, String status,Long mvnoId) {
        QVoucher qVoucher = QVoucher.voucher;
        BooleanExpression expForVoucher = qVoucher.isNotNull();
        if (StringUtils.isNotBlank(batchName)) {
            expForVoucher = expForVoucher.and(qVoucher.batchName.contains(batchName));
        }
        if (StringUtils.isNotBlank(status)) {
            expForVoucher = expForVoucher.and(qVoucher.status.eq(VoucherStatus.valueOf(status)));
        }
        if (StringUtils.isNotBlank(batchName) || StringUtils.isNotBlank(status)) {
            Long foundMvno = new JPAQuery<>(entityManager).select(qVoucher.mvnoId).from(qVoucher).where(expForVoucher).fetchFirst();
            if (!foundMvno.equals(mvnoId)) {
                throw new CustomValidationException(HttpStatus.UNAUTHORIZED.value(), "You Are Not Authorized to Download", null);
            }
        }
        if (mvnoId != null) {
            expForVoucher = expForVoucher.and(qVoucher.mvnoId.in(mvnoId,1L));
        }
        QBSSVoucherBatch qVoucherBatch = QBSSVoucherBatch.bSSVoucherBatch;
        QPostpaidPlan qPlan = QPostpaidPlan.postpaidPlan;
        QVoucherConfiguration qVoucherConfiguration = QVoucherConfiguration.voucherConfiguration;
        JPAQuery<Customers> query = new JPAQuery<>(entityManager);
        List<Map<String, String>> dataToExport = new ArrayList<>();
//        List<Tuple> queryResult = query.select(qVoucher.code, qPlan.plantype,qVoucher.status,  qPlan.name, qPlan.offerprice, qPlan.startDate, qPlan.endDate).from(qVoucher, qVoucherBatch, qVoucherConfiguration, qPlan).where(qVoucherBatch.voucherBatchId.eq(qVoucher.voucherBatch.voucherBatchId).and(qVoucherConfiguration.id.eq(qVoucherBatch.voucherConfiguration.id)).and(qVoucherConfiguration.plan.id.eq(qPlan.id)).and(expForVoucher)).fetch();
        List<Tuple> queryResult = query
                .select(
                        qVoucher.code,
                        qPlan.plantype,
                        qVoucher.status,
                        qPlan.name,
                        qVoucherBatch.price,
                        qPlan.startDate,
                        qPlan.endDate,
                        qVoucherBatch.expirydate,
                        qVoucher.createdOn,
                        qVoucher.serial_number)
                .from(qVoucher)
                .leftJoin(qVoucherBatch).on(qVoucherBatch.voucherBatchId.eq(qVoucher.voucherBatch.voucherBatchId))
                .leftJoin(qVoucherConfiguration).on(qVoucherConfiguration.id.eq(qVoucherBatch.voucherConfiguration.id))
                .leftJoin(qPlan).on(qVoucherConfiguration.plan.id.eq(qPlan.id)) // <-- left join here
                .where(expForVoucher)
                .fetch();

        if (!queryResult.isEmpty()) {
            queryResult.forEach(result -> {
                Map<String, String> map = new HashMap<>();
                if (result.get(qVoucher.code) != null && result.get(qVoucher.code).matches(".*[+/=].*")) {

                    try {
                        map.put("Voucher Code", encryptVoucher.decrypt(result.get(qVoucher.code)));
                    } catch (Exception e) {
                        e.getMessage();
                        System.out.println("Decryption failed or skipped for: " + result.get(qVoucher.code));
                    }
                }else{
                    map.put("Voucher Code", result.get(qVoucher.code));
                }
                map.put("Serial Number", result.get(qVoucher.serial_number) != null ? result.get(qVoucher.serial_number).toString() : "-");
                map.put("Status", result.get(qVoucher.status).toString());
                map.put("Plan Name", result.get(qPlan.name) != null ? result.get(qPlan.name) : "WALLET");
                map.put("Voucher Type", result.get(qPlan.plantype) != null ? result.get(qPlan.plantype) : "WALLET");
                map.put("Price", result.get(qVoucherBatch.price).toString());
                map.put("Valid From", result.get(qVoucher.createdOn).toString() );
                map.put("Valid To", result.get(qVoucherBatch.expirydate).toString());
                dataToExport.add(map);
            });
        } else {
            throw new CustomValidationException(APIConstants.NO_CONTENT_FOUND,"No data found.",null);
        }
        return dataToExport;
    }
    @Override
    public void addVoucherId(Long id) {

    }

    public List<java.lang.Long> getBUIdsFromCurrentStaff() {
        List<java.lang.Long> mvnoIds = new ArrayList<java.lang.Long>();
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }

    @Override
    public Voucher getVoucher(String voucherCode, Integer mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(voucherCode)) {
                throw new IllegalArgumentException( "Voucher code is mandatory. Please enter valid voucher code." );
            } else {
                voucherBatchService.findAllVoucherBatch(Long.valueOf(mvnoId));
                QVoucher qVoucher = QVoucher.voucher;
                BooleanExpression boolExp = qVoucher.isNotNull();
                boolExp = boolExp.and(qVoucher.code.eq(encryptVoucher.encrypt(voucherCode)));
                if(mvnoId != 1)
                    boolExp = boolExp.and(qVoucher.mvnoId.in(mvnoId, 1L));
                Optional<Voucher> optionalVoucher = voucherRepository.findOne(boolExp);
                if (!optionalVoucher.isPresent()) {
                    throw new IllegalArgumentException("No voucher found with voucher code : '" + voucherCode + "'. Please enter valid voucher code." );
                } else {
                    validateVoucher(optionalVoucher.get().getCode(), Long.valueOf(mvnoId));
                    return optionalVoucher.get();
                }
//				Optional<Voucher> voucherOptional = voucherRepository.findByCodeAndMvnoId(simpleEncryptor.encrypt(voucherCode),mvnoId);
//				if (!voucherOptional.isPresent())
//				{
//					throw new IllegalArgumentException("No voucher found with voucher code : '" + voucherCode+ "'. Please enter valid voucher code."+WifiConstants.NOT_PUT_IN_QUEUE);
//				}
//				else
//				{
//					validateVoucher(voucherOptional);
//					return voucherOptional.get();
//				}
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }




    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
            partnerId = -1;
        }
        return partnerId;
    }


    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("[VoucherBatchService]" + e.getStackTrace(), e);
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    @Override
    public ResponseEntity<Map<String, Object>> changeStatus(Long voucherId, Long aLong) {
        Map<String, Object> result=new HashMap<>();
        Optional<Voucher> voucher=voucherRepository.findById(voucherId);
        if(voucher.isPresent()){
            voucher.get().setStatus(VoucherStatus.USED);
            voucherRepository.save(voucher.get());
            result.put(voucherId.toString(),voucher.get());
        }

        return (ResponseEntity<Map<String, Object>>) result;
    }
    public static String generateNextUniqueBatch( String batchName,Set<String>existingBatches) {
        String PREFIX = batchName + "_";
        int NUMBER_LENGTH = 11;
        long maxNumber = 0;

        // Find the max serial number for the given batch name
        for (String batch : existingBatches) {
            if (batch.startsWith(PREFIX)) {
                String numberPart = batch.substring(PREFIX.length());
                try {
                    long num = Long.parseLong(numberPart);
                    if (num > maxNumber) {
                        maxNumber = num;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }

        // Generate the next number
        long nextNumber = maxNumber + 1;
        String paddedNumber = String.format("%0" + NUMBER_LENGTH + "d", nextNumber);
        String newBatch = PREFIX + paddedNumber;

        // Optional: add to set
        existingBatches.add(newBatch);
        return newBatch;
    }

}

