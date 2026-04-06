package com.adopt.apigw.modules.paymentGatewayMaster.controller;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.MtnPayment.model.DebitCompletedRequest;
import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
import com.adopt.apigw.modules.PriceGroup.controller.PriceBookController;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.pojo.CreditDoc.CreditDocUpdateDTO;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.repository.postpaid.DebitDocDetailRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.repository.postpaid.DebitDocumentTAXRelRepository;
import com.adopt.apigw.service.common.BatchPaymentService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import brave.Tracer;
import brave.propagation.TraceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.paymentGatewayMaster.dto.PaymentGatewayDTO;
import com.adopt.apigw.modules.paymentGatewayMaster.service.PaymentGatewayService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PAYMENT_GATEWAY)
public class PaymentGatewayController extends ExBaseAbstractController<PaymentGatewayDTO> {
    private static String MODULE = " [PaymentGatewayController] ";
    @Autowired
    private PaymentGatewayService paymentGatewayService;

    @Autowired
    private BatchPaymentService batchPaymentService;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;

    @Autowired
    private DebitDocumentTAXRelRepository debitDocumentTAXRelRepository;

    @Autowired
    private MtnPaymentService mtnPaymentService;

    @Autowired
    private Tracer tracer;

    private final Logger log = LoggerFactory.getLogger(PriceBookController.class);


    public PaymentGatewayController(PaymentGatewayService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return " [PaymentGatewayController] ";
    }

    @Override
    public ValidationData validateSave(PaymentGatewayDTO dto) {
        String SUBMODULE = getModuleNameForLog() + " [validate save] ";
           GenericDataDTO genericDataDTO = new GenericDataDTO();
        PaymentGatewayDTO paymentGatewayDTO = paymentGatewayService.getPGByName(dto.getName());
        ApplicationLogger.logger.debug(SUBMODULE + paymentGatewayDTO);
        ValidationData validationData = new ValidationData();
        if (null == paymentGatewayDTO || null == paymentGatewayDTO.getId()) {
            validationData.setMessage("Valid");
            validationData.setValid(true);
          //  logger.info("Validating Payment Details with name "+dto.getName()+" :  request: { From : {} }; Response : {{}}",getModuleNameForLog(), validationData.getMessage(),APIConstants.SUCCESS);
            return validationData;
        }
        validationData.setMessage("PaymentGateway is already exists!");
        validationData.setValid(false);
//        logger.error("Payment Already Exists "+dto.getName()+"  :  request: { From : {}}; Response : {{};}",getModuleNameForLog(), validationData.getMessage(),APIConstants.FAIL);
//        MDC.remove("type");
        return validationData;
    }

    @Override
    public ValidationData validateUpdate(PaymentGatewayDTO dto) {
        MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [validate update] ";
        PaymentGatewayDTO transactionModeDTO = paymentGatewayService.getPGByName(dto.getName());
        ApplicationLogger.logger.debug(SUBMODULE + transactionModeDTO);
        ValidationData validationData = new ValidationData();
        if (null == transactionModeDTO || dto.getId().equals(transactionModeDTO.getId())) {
            validationData.setMessage("Valid");
            validationData.setValid(true);
//            logger.info("Payment Is successfully validated for "+dto.getName()+" :  request: { From : {}}; Response : {{}}",getModuleNameForLog(),validationData.getMessage(),APIConstants.SUCCESS);
            return validationData;
        }
        validationData.setMessage("PaymentGateway is already exists!");
        validationData.setValid(false);
//        logger.error("Unable to validate payment details for "+dto.getName()+"  :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),validationData.getMessage(),APIConstants.FAIL);
        MDC.remove("type");
        return validationData;
    }

    @GetMapping(value = "/getPGForPartner")
    public GenericDataDTO getPGForPartner() {
        MDC.put("type", "Fetch");
        String SUBMODULE = "Payment gateway" + " [getPGForPartner()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(this.paymentGatewayService.getPGForPartner());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
//            logger.info("fetching pg for partner :  request: { From : {}}; Response : {{};{}}",getModuleNameForLog(),  genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
           // ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            logger.error("Unable to fetch pg for partner  :  request: { From : {},}; Response : {{} {};Exception:{}}",getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getStackTrace() );
        }
        MDC.remove("type");
        return genericDataDTO;
    }

//    @PostMapping(value = "/record/bulkpayment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> createRecordPayment(@Valid @RequestParam String recordpaymentDtos, @RequestParam(value = "file", required = false)MultipartFile file , @RequestParam(value = "batchname" , required = false) String batchname
//    ) throws Exception {
//        MDC.put("type", "Crete");
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        Gson gson = new Gson();
//        List<RecordPaymentPojo> recordPaymentPojoList =  new ArrayList<>();
//        ObjectMapper mapper = new ObjectMapper();
//        recordPaymentPojoList = mapper.registerModule(new JavaTimeModule())
//                .readValue(recordpaymentDtos, new TypeReference<List<RecordPaymentPojo>>(){});
//         Double totalAmount =0.0;
//        BatchPaymentPojo batchPaymentPojo = new BatchPaymentPojo();
//        batchPaymentPojo.setBatchname(batchname);
//        List<BatchPaymentMappingPojo> batchPaymentMappingPojoList = new ArrayList<>();
//         try {
//            for(RecordPaymentPojo pojo : recordPaymentPojoList) {
//                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
//                if (file != null) {
//                    creditDocService.uploadDocument(pojo, file);
//                }
//                if(pojo.getPaytype() == null){
//                    pojo.setPaytype("invoice");
//                }
//                pojo = creditDocService.save(pojo, false, false, false,null);
//                totalAmount +=pojo.getAmount();
//                BatchPaymentMappingPojo batchPaymentMappingPojo = new BatchPaymentMappingPojo();
//                batchPaymentMappingPojo.setCredit_doc_id(pojo.getCreditDocId().longValue());
//                batchPaymentMappingPojoList.add(batchPaymentMappingPojo);
//            }
//            batchPaymentPojo.setBatchPaymentMappingList(batchPaymentMappingPojoList);
//            batchPaymentPojo.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
//             boolean status = batchPaymentService.isPaymentBatchAlreadyExists(batchname);
//             if(!status){
//                 batchPaymentService.save(batchPaymentPojo);
//             }
//             else{
//                 throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value() , "Batch with this name already exist" , null);
//             }
//
//            //	workflowAuditService.workFlowAuditPayment(pojo, getLoggedInUserId());
//            response.put("totalamount", totalAmount);
//            RESP_CODE = APIConstants.SUCCESS;
//              logger.info("bulk record payment  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE);
//        } catch (CustomValidationException ce) {
//            //		ApplicationLogger.logger.error(MODULE + ce.getStackTrace(), ce);
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to createRecordPayment  for " + 23 + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception ex) {
//            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to createRecordPayment  for " + 23 + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return paymentGatewayService.apiResponse(RESP_CODE, response);
//    }

  //  @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PAYMENT_SYSTEM_ALL + "\",\"" + AclConstants.OPERATION_PAYMENT_SYSTEM_VIEW + "\")")

//    @PreAuthorize("validatePermission(\"" + MenuConstants.SEARCH_CREDIT_NOTE+ "\",\"" + MenuConstants.search_payment+ "\")")
    @GetMapping("/payment/search")
    public ResponseEntity<?> searchPaymentWithPagination(@ModelAttribute SearchPaymentPojo entity, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId",traceContext.spanIdString());        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        Page<CreditDocumentSearchPojo> creditDocList = null;
        PaginationRequestDTO requestDTO = new PaginationRequestDTO();
        requestDTO.setPage(entity.getPage());
        requestDTO.setPageSize(entity.getPageSize());
        try {
            if (entity != null) {
                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
                creditDocList =creditDocService.searchPayment(entity,requestDTO,mvnoId);
                if (null != creditDocList && 0 < creditDocList.getSize()) {
                    response.put("creditDocumentPojoList", creditDocList.getContent());
                } else {
                    response.put("creditDocumentPojoList", new ArrayList<>());
                }
                RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Payment"  + LogConstants.REQUEST_BY +getLoggedInUser().getFirstName()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Payment"  + LogConstants.REQUEST_BY +  getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "search Payment" + LogConstants.REQUEST_BY +  getLoggedInUser().getFirstName() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return paymentGatewayService.apiResponse(RESP_CODE, response,creditDocList);
    }

    @PostMapping("/editbatchpayment")
    public ResponseEntity<?> editBatchPayment(@RequestBody List<CreditDocUpdateDTO> creditDocUpdateDTOList) throws Exception {
        MDC.put("type", "fetch");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (!creditDocUpdateDTOList.isEmpty()) {
                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
                creditDocService.updateCreditDocument(creditDocUpdateDTOList);
                response.put("msg", "credit document update successfully");
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
        }
        MDC.remove("type");
        return paymentGatewayService.apiResponse(RESP_CODE, response);
    }


//    @GetMapping("/invoiceDetails/{invoiceId}/{custId}")
//    public ResponseEntity<?> getChargeListByTypeAndCategory(@PathVariable Integer invoiceId,
//                                                            @PathVariable Integer custId) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try{
//            DebitDocSearchPojo debitDocSearchPojo = paymentGatewayService.getInvoiceDetails(invoiceId,custId);
//            response.put("invoiceDetails" , debitDocSearchPojo);
//
//            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(invoiceId);
//            response.put("debitDocDetails" , debitDocDetails);
//
//            List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelRepository.findAllByDebitdocumentid(invoiceId);
//            Set<String> taxnames = debitDocumentTAXRels.stream().map(DebitDocumentTAXRel::getTaxname).collect(Collectors.toSet());
//            List<DebitDocumentTAXRelPojo> debitDocumentTAXRelDtos = new ArrayList<>();
//            for(String taxname: taxnames) {
//                DebitDocumentTAXRel documentTAXRel =  debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).findFirst().get();
//                Double amount = debitDocumentTAXRels.stream().filter(debitDocumentTAXRel -> debitDocumentTAXRel.getTaxname().equalsIgnoreCase(taxname)).mapToDouble(DebitDocumentTAXRel::getAmount).sum();
//                DebitDocumentTAXRelPojo debitDocumentTAXRelDto = new DebitDocumentTAXRelPojo(taxname, documentTAXRel.getPercentage(), amount,parseInt(documentTAXRel.getChargeid()));
//                debitDocumentTAXRelDtos.add(debitDocumentTAXRelDto);
//            }
//            response.put("debitDocumentTAXRels" , debitDocumentTAXRelDtos);
//
//
//            RESP_CODE = APIConstants.SUCCESS;
//
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        return paymentGatewayService.apiResponses(RESP_CODE, response);
//
//    }

    @PostMapping("/gettransactionstatus")
    public ResponseEntity<?> getTicketRemark(@RequestBody DebitCompletedRequest debitCompletedRequest) throws Exception {
        MDC.put("type", "get");
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        try {
            if (debitCompletedRequest != null) {
                response.put("istransactionsuccess",mtnPaymentService.IsTransactionStatusSuccess(debitCompletedRequest.getTransactionid() , debitCompletedRequest.getExternaltransactionid()));
                response.put(APIConstants.MESSAGE,"treansaction status found");
                RESP_CODE = APIConstants.SUCCESS;
//                logger.info("get transaction status for " + debitCompletedRequest.getTransactionid() + "  :  request: { From : {}}; Response : {{}}", "TicketRemark", RESP_CODE, response);
                return  paymentGatewayService.apiResponse(RESP_CODE , response);
            }
        } catch (CustomValidationException ce) {
//            logger.error("Transaction status" + ce.getStackTrace(), ce);
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.MESSAGE, ce.getMessage());

//            logger.error("Unable to get transaction status for " + debitCompletedRequest.getTransactionid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ce.getStackTrace());
        } catch (Exception ex) {
//            logger.error("Transaction status" + ex.getStackTrace(), ex);
            ex.printStackTrace();
            RESP_CODE = HttpStatus.INTERNAL_SERVER_ERROR.value();
            response.put(APIConstants.MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to get transaction status for " + debitCompletedRequest.getTransactionid() + " :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", "TicketRemark", RESP_CODE, response, ex.getStackTrace());
        }
        MDC.remove("type");
        return paymentGatewayService.apiResponse(RESP_CODE , response);
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

}
