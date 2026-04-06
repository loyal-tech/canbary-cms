package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.BillRunMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.BillRun;
import com.adopt.apigw.pojo.SearchBillRun;
import com.adopt.apigw.pojo.TriggerBillRun;
import com.adopt.apigw.pojo.api.BillRunPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustomerBillingMessage;
import com.adopt.apigw.repository.postpaid.BillRunRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.itextpdf.text.Document;
import com.adopt.apigw.model.postpaid.QBillRun;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BillRunService extends AbstractService<BillRun, BillRunPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(BillRunService.class);

    @Autowired
    private BillRunRepository entityRepository;
    @Autowired
    private BillRunMapper billRunMapper;
    @Autowired
    MessageSender messageSender;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomersService customersService;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @Override
    protected JpaRepository<BillRun, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<BillRun> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> getAllActiveEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) == 1)
            return entityRepository.findByStatus("Y");
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findByStatusAndMvnoIdIn("Y", Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> getAllEntities(Integer mvnoId) {
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId == 1)
            return entityRepository.findAll();
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll(Arrays.asList(mvnoId, 1));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> findBillRunDetails(SearchBillRun search) {

        /*
         * QBillRun billRun = QBillRun.billRun;
         *
         * Predicate builder = new OptionalBooleanBuilder(billRun.isNotNull())
         * .notEmptyAnd(billRun.status::equalsIgnoreCase, sBillRun.getBillrunstatus())
         * .notEmptyAnd(billRun.id::eq, sBillRun.getBillrunid()) //
         * .notEmptyAnd(DateFormatUtils. billRun.createdate::after,
         * sBillRun.getBillfromdate()) // .notEmptyAnd(billRun.createdate::before,
         * sBillRun.getBilltodate()) // . .build();
         *
         * return (List<BillRun>) entityRepository.findAll(builder); // return
         * (List<BillRun>) entityRepository.findAll(billRunStatusMatches);
         *
         */

        QBillRun entity = QBillRun.billRun;
        BooleanExpression exp = entity.isNotNull();
        if (search.getBillrunid() != null) {
            exp = exp.and(entity.id.eq(search.getBillrunid()));
        }
        if (!StringUtils.isEmpty(search.getBillrunstatus()) && !"-1".equalsIgnoreCase(search.getBillrunstatus())) {
            exp = exp.and(entity.status.eq(search.getBillrunstatus()));
        }
        if (search.getBillfromdate() != null && search.getBilltodate() != null) {
            exp = exp.and(entity.createdate.between(search.getBillfromdate().atStartOfDay(), search.getBilltodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getBilltodate() != null) {
            exp = exp.and(entity.createdate.before(search.getBilltodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getBillfromdate() != null) {
            exp = exp.and(entity.createdate.after(search.getBillfromdate().atStartOfDay()));
        }
        Predicate builder1 = exp;
        return (List<BillRun>) entityRepository.findAll(builder1);

    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '4')")
    public void deleteBillRun(Integer id) throws Exception {
        entityRepository.deleteById(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '1')")
    public List<BillRun> findById(Integer billRunId) {
        List<Integer> myList = new ArrayList<Integer>();
        myList.add(billRunId);

        return entityRepository.findAllById(myList);
    }

    public String performBillRun(String billRunDate, Customers customer,Integer loggedInUserId , String paymentOwner,String createdByName,String updateByname,Integer paymentOwnerId,String invoiceType, Integer renewalId) throws Exception {
        String billRunId = null;
        String billRunURL = null;

        if (customer == null) {

//    		billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebill/{billrundate}";

            CustomerBillingMessage message = new CustomerBillingMessage(billRunDate, null, null, null, null,null,loggedInUserId,null,createdByName,updateByname , "" , "false",paymentOwner,paymentOwnerId);
            message.getData().put("invoiceType",invoiceType);
            message.getData().put("renewalId",renewalId);
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));

        } else {

            if (customer.getBillRunCustPackageRelId() != null) {
                CustomerBillingMessage message = new CustomerBillingMessage(billRunDate, customer.getId(), customer.getBillRunCustPackageRelId(), null, null,null,loggedInUserId,null,createdByName,updateByname , "" , "false",paymentOwner,paymentOwnerId);
                message.getData().put("invoiceType",invoiceType);
                message.getData().put("renewalId",renewalId);
//                messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
                kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
            } else {
                CustomerBillingMessage message = new CustomerBillingMessage(billRunDate, customer.getId(), null, null, null,null,loggedInUserId,null,createdByName,updateByname ,"","false",paymentOwner,paymentOwnerId);
                message.getData().put("invoiceType",invoiceType);
                message.getData().put("renewalId",renewalId);
//                messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
                kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
            }


            //.replace("{custid}", String.valueOf(customer.getId()));
            //billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebillcust/{billrundate}/{custid}";
//        	billRunURL=billRunURL.replace("{billrundate}", billRunDate).replace("{custid}", String.valueOf(customer.getId()));
//        	billRunURL=billRunURL.replace("{billrundate}", billRunDate).replace("{custid}", "2");
        }
        return billRunId;
    }

    public String generatebillcharge(Integer custId, List<Integer> chargeId,Integer loggedInUserId,String createByName,String updatedByName, String paymentOwner,Integer paymentOwnerId,Boolean isCancelRegenerate, String type) throws Exception  {
        String chargeBillRunId = null;
        String chargeRunURL = null;
        Customers customers=customersRepository.findById(custId).get();
        if (chargeId != null) {
            CustomerBillingMessage message = new CustomerBillingMessage(null, custId, null, null, null,null,loggedInUserId,null,createByName,updatedByName , "" , "false",paymentOwner,paymentOwnerId);
            if(customers.getCusttype().equalsIgnoreCase("prepaid"))
                message.getData().put("type","prepaid");
            else
                message.getData().put("type","postpaid");
            message.getData().put("isCancelRegenerate", isCancelRegenerate);

            String chargeIdList="(";
            for(int i=0;i<chargeId.size();i++)
            {
                chargeIdList+=chargeId.get(i).toString();
                if(i!=chargeId.size()-1)
                    chargeIdList+=", ";
            }
            chargeIdList+=")";

            message.getData().put("chargeId", chargeIdList);
            message.setType(type);
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
        }
        return chargeBillRunId;
    }

    public String generatebillcharge(Boolean isCaf,Integer custId, List<Integer> chargeId,Integer loggedInUserId,String createByName,String updatedByName, String paymentOwner,Integer paymentOwnerId, Boolean isCancelRegenerate, String type) throws Exception  {
        String chargeBillRunId = null;
        String chargeRunURL = null;
        Customers customers=customersRepository.findById(custId).get();
        if (chargeId != null) {
            CustomerBillingMessage message = new CustomerBillingMessage(null, custId, null, null, null,null,loggedInUserId,null,createByName,updatedByName , "" , "false",paymentOwner,paymentOwnerId);
            if(customers.getCusttype().equalsIgnoreCase("prepaid"))
                message.getData().put("type","prepaid");
            else
                message.getData().put("type","postpaid");
            message.setType(type);
            message.getData().put("isCancelRegenerate", isCancelRegenerate);
            message.getData().put("chargeId", chargeId);
            if(isCaf)
                message.getData().put("isCafFDC",isCaf);
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
        }
        return chargeBillRunId;
    }

    public String generatebillchargeWithInvoiceId(Integer custId, List<Integer> chargeId, HashSet<Integer> oldDebitDocId,Integer loggedInUserId,String paymentOwner,Integer paymentOwnerId, Boolean isCancelRegenerate,String createdByName, String type) throws Exception {
        String chargeBillRunId = null;
        String chargeRunURL = null;
        Customers customers=customersRepository.findById(custId).get();
        if (chargeId != null) {
            CustomerBillingMessage message = new CustomerBillingMessage(null, custId, null, null, null,null,loggedInUserId,oldDebitDocId,createdByName,null , "" , "false",paymentOwner,paymentOwnerId);
            if(customers.getCusttype().equalsIgnoreCase("prepaid"))
                message.getData().put("type","prepaid");
            else
                message.getData().put("type","postpaid");

            message.getData().put("isCancelRegenerate", isCancelRegenerate);
            String chargeIdList="(";
            for(int i=0;i<chargeId.size();i++)
            {
                chargeIdList+=chargeId.get(i).toString();
                if(i!=chargeId.size()-1)
                    chargeIdList+=", ";
            }
            chargeIdList+=")";
            message.getData().put("chargeId", chargeIdList);
            message.setType(type);
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
            kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
            message.getData().put("isCancelRegenerate", isCancelRegenerate);
        }
        return chargeBillRunId;
    }

//    public String generatebillcharge(Boolean isCaf,Integer custId, String chargeId,Integer loggedInUserId,String createByName,String updatedByName) throws Exception  {
//        String chargeBillRunId = null;
//        String chargeRunURL = null;
//        Customers customers=customersRepository.findById(custId).get();
//        if (chargeId != null) {
//            CustomerBillingMessage message = new CustomerBillingMessage(null, custId, null, chargeId, null,null,loggedInUserId,null,createByName,updatedByName , "" , "false","");
//            if(customers.getCusttype().equalsIgnoreCase("prepaid"))
//                message.getData().put("type","prepaid");
//            else
//                message.getData().put("type","postpaid");
//
//            if(isCaf)
//                message.getData().put("isCafFDC",isCaf);
//            messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
//        }
//        return chargeBillRunId;
//    }

    public String generatePaymentReceipt(String creditDocId) throws Exception {
        String generatePaymentURL = null;

        if (creditDocId != null) {

//    		billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebill/{billrundate}";

            generatePaymentURL = CommonConstants.PAYMENT_RECEIPT_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                    .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                    .replace("{creditdocid}", creditDocId);

        }

        logger.info("generateReceipt() URL:" + generatePaymentURL);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        Request httpRequest = new Request.Builder()
                .url(generatePaymentURL)
                .build();
        try {


            Response response = client.newCall(httpRequest).execute();
            if (response != null) {
                String responseBody = response.body().string();
                logger.info("generatePaymentReceipt() resposne:" + responseBody);

                HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
                if (responseMap.get("responseCode").toString().equalsIgnoreCase("200")) {
                    Object obj = responseMap.get("responseObject");
                    generatePaymentURL = String.valueOf(obj);
                }
            } else {
                logger.info("generatePaymentReceipt() resposne is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return creditDocId;
    }

    public boolean generateInvoice(String billRunId) throws Exception {
        String generatePDFURL = CommonConstants.GERERATE_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{billrunid}", billRunId);

        //String generatePDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatepdf/{billrunid}";
        //generatePDFURL=generatePDFURL.replace("{billrunid}",billRunId);

        logger.info("genreateInvoices() generatePDFURL:" + generatePDFURL);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(generatePDFURL)
                    .build();
            Response response = client.newCall(httpRequest).execute();
            if (response != null) {
                String responseBody = response.body().string();
                logger.info("genreateInvoices() resposne:" + responseBody);

                HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
                if (responseMap.get("responseCode").toString().equalsIgnoreCase("200")) {
                    bStatus = true;
                }
            } else {
                logger.info("genreateInvoices() resposne is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }


    public boolean revertInvoice(String invoiceId) throws Exception {
        String revertInvocieURL = CommonConstants.REVERT_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{invoiceid}", invoiceId);

        //String generatePDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatepdf/{billrunid}";
        //generatePDFURL=generatePDFURL.replace("{billrunid}",billRunId);

        logger.info("Revert Invoice() generateInvoiceURL:" + revertInvocieURL);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(revertInvocieURL)
                    .build();
            Response response = client.newCall(httpRequest).execute();
            if (response != null) {
                String responseBody = response.body().string();
                logger.info("RevertInvoices() resposne:" + responseBody);

                HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
                if (responseMap.get("responseCode").toString().equalsIgnoreCase("200")) {
                    bStatus = true;
                }
            } else {
                logger.info("RevertInvoices() resposne is null");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }

    public boolean emailInvoices(String billRunId) throws Exception {
        String emailPDFURL = CommonConstants.EMAIL_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{billrunid}", billRunId);

//    	String emailPDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/emailpdf/{billrunid}";
//    	emailPDFURL=emailPDFURL.replace("{billrunid}",billRunId);

        logger.info("emailInvoices() emailPDFURL:" + emailPDFURL);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(emailPDFURL)
                    .build();
            Response response = client.newCall(httpRequest).execute();
            if (response != null) {
                String responseBody = response.body().string();
                logger.info("emailInvoices() resposne:" + responseBody);

                HashMap<String, Object> responseMap = (HashMap<String, Object>) UtilsCommon.convertJsonToHashMap(responseBody);
                if (responseMap.get("responseCode").toString().equalsIgnoreCase("200")) {
                    bStatus = true;
                }
            } else {
                logger.info("emailInvoices() resposne is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '2')")
    public SearchBillRun getSearchBillRunForInvoice() {
        return new SearchBillRun();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '2')")
    public TriggerBillRun getTriggerBillRunForInvoice() {
        return new TriggerBillRun();
    }


    public BillRunPojo convertBillRunModelToBillRunPojo(BillRun billRun) {
        BillRunPojo pojo = null;
        if (billRun != null) {
            pojo = new BillRunPojo();
            pojo.setId(billRun.getId());
            pojo.setAmount(billRun.getAmount());
            pojo.setBillruncount(billRun.getBillruncount());
            pojo.setBillrunfinishdate(billRun.getBillrunfinishdate());
            pojo.setRundate(billRun.getRundate());
            pojo.setStatus(billRun.getStatus());
            pojo.setCreatedate(billRun.getCreatedate());
            pojo.setDelete(billRun.getIsDelete());
            pojo.setType(billRun.getType());
            pojo.setMvnoId(billRun.getMvnoId());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '1')")
    public List<BillRunPojo> convertResponseModelIntoPojo(List<BillRun> billRunServerList) {
        List<BillRunPojo> pojoListRes = new ArrayList<BillRunPojo>();
        if (billRunServerList != null && billRunServerList.size() > 0) {
            for (BillRun billRun : billRunServerList) {
                pojoListRes.add(convertBillRunModelToBillRunPojo(billRun));
            }
        }
        return pojoListRes;
    }

    public Page<BillRun> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList, String type) {
        QBillRun qBillRun = QBillRun.billRun;
            BooleanExpression booleanExpression = qBillRun.isNotNull().and(qBillRun.type.contains(type).and(qBillRun.isDelete.eq(false)));
            if (getLoggedInUser().getLco())
                booleanExpression = booleanExpression.and(qBillRun.lcoId.eq(getLoggedInUser().getPartnerId()));
            else
                booleanExpression = booleanExpression.and(qBillRun.lcoId.isNull());
            pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
            if (null == filterList || 0 == filterList.size()) {
                return entityRepository.findAll(booleanExpression, pageRequest);
            }
            return null;
        }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '2')")
    public BillRunPojo save(BillRunPojo pojo) throws Exception {
        BillRun oldObj = null;
        if (pojo.getId() != null) {
            oldObj = get(pojo.getId(),pojo.getMvnoId());
        }
        // TODO: pass mvnoID manually 6/5/2025
        pojo.setMvnoId(pojo.getMvnoId());
        BillRun obj = convertBillRunPojoToBillRunModel(pojo);
        if(oldObj!=null) {
            log.info("BillRun update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
        }
        obj = saveBillRun(obj);
        pojo = convertBillRunModelToBillRunPojo(obj);
        return pojo;
    }

    public BillRun convertBillRunPojoToBillRunModel(BillRunPojo billRunPojo) throws Exception {
        BillRun billRun = null;
        if (billRunPojo != null) {
            billRun = new BillRun();
            if (billRunPojo.getId() != null) {
                billRun.setId(billRunPojo.getId());
            }
            billRun.setAmount(billRunPojo.getAmount());
            billRun.setBillruncount(billRunPojo.getBillruncount());
            billRun.setRundate(billRunPojo.getRundate());
            billRun.setStatus(billRunPojo.getStatus());
            billRun.setBillrunfinishdate(billRunPojo.getBillrunfinishdate());
            billRun.setCreatedate(billRunPojo.getCreatedate());
            billRun.setIsDelete(billRunPojo.getDelete());
            billRun.setType(billRunPojo.getType());
            billRun.setMvnoId(billRunPojo.getMvnoId());
        }
        return billRun;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.BillRun', '2')")
    public BillRun saveBillRun(BillRun billRun) throws Exception {
        String operation = "edit";
        if (billRun != null && billRun.getId() == null) {
            operation = "add";
        }
        BillRun save = entityRepository.save(billRun);
        return save;
    }


    public void validateRequest(BillRunPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }
        if (!(pojo.getStatus().equalsIgnoreCase("Y") || pojo.getStatus().equalsIgnoreCase("N"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("BillRun");
        List<BillRunPojo> billRunPojoList = entityRepository.findAll().stream()
                .map(data -> billRunMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createExcel(workbook, sheet, BillRunPojo.class, billRunPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<BillRunPojo> billRunPojoList = entityRepository.findAll().stream()
                .map(data -> billRunMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        createPDF(doc, BillRunPojo.class, billRunPojoList, null);
    }

    @Override
    public BillRun get(Integer id,Integer mvnoId) {
        BillRun billRun = super.get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (billRun != null && (getMvnoIdFromCurrentStaff(null) == 1 || (billRun.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || billRun.getMvnoId() == 1)))
            return billRun;
        return null;
    }

    public BillRun getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        BillRun billRun = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (billRun == null || !(mvnoId == 1 || mvnoId.intValue() == billRun.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return billRun;
    }

    public String generateBillForInventory(Integer custId,Integer loggedInuserId, Long inventoryMappingId, Long inventoryItemId, String itemCondition, String createdByName, String updatedByName,Long planId,Integer paymentOwnerId) throws Exception {
        Thread.sleep(2000);
        String chargeBillRunId = null;
        CustomerBillingMessage message = new CustomerBillingMessage(null, custId, null, null, inventoryMappingId,null,getLoggedInUserId(),null,createdByName,updatedByName , "" , "false","",paymentOwnerId);
        Map<String, Object> data = message.getData();
        data.put("inventoryItemId", inventoryItemId);
        data.put("itemCondition", itemCondition);
        data.put("currentUserLoggedInId",loggedInuserId);
        data.put("planId",planId);
        message.setData(data);
//        messageSender.send(message, RabbitMqConstants.QUEUE_BILLING_INVOICE);
        kafkaMessageSender.send(new KafkaMessageData(message, CustomerBillingMessage.class.getSimpleName()));
        return chargeBillRunId;
    }

}
