package com.adopt.apigw.modules;

import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.modules.CustomerPortal.controller.CustomerPortalController;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.pojo.api.ChequeDetailsPojo;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/cms")
public class CreditDocController {

    private static String MODULE = " [CreditDocController] ";

    @Autowired
    private CustomersService customersService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    CreditDocService creditDocService;

    private static final Logger logger = LoggerFactory.getLogger(CreditDocController.class);

    @RequestMapping(value = "/documentForInvoice/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (null == customers.get()) {
                return ResponseEntity.notFound().build();
            }
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(docId.intValue());
            if (null == creditDocument) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getBarterDoc(customers.get().getUsername().trim(), creditDocument.get().getUniquename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  "+docId+" downloaded Successfully  :  request: { From : {} }; Response : {{}}",SUBMODULE, APIConstants.SUCCESS);
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                  logger.error("Unable to downloadDocument "+docId+" :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND,ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument "+docId+"   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE,HttpStatus.NOT_FOUND,ResponseEntity.notFound(),ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }

//    @PostMapping(value = "/getWithdrawPayments/{customerId}")
//    public GenericDataDTO voidInvoice(@PathVariable(name = "customerId") Integer customerId, @RequestBody PaginationRequestDTO paginationRequestDTO) {
//        Integer RESP_CODE = APIConstants.FAIL;
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            logger.info("Fetching All payments for customer id " + customerId + "  : Response : {{}}", genericDataDTO.getResponseCode());
//            genericDataDTO.setDataList(creditDocService.getWithdrawPayments(customerId,paginationRequestDTO));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successfully");
//        } catch (CustomValidationException ce) {
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getMessage());
//        } catch (Exception e) {
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable fetching all payments for customer id " + customerId +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }

    @PostMapping(value = "/getChequeDetail/{id}")
    public GenericDataDTO getChequePaymode(@PathVariable(name = "id") Integer id, @RequestBody PaginationRequestDTO paginationRequestDTO) {
        Integer RESP_CODE = APIConstants.FAIL;
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            logger.info("Fetching All payments for customer id " + id + "  : Response : {{}}", genericDataDTO.getResponseCode());
            genericDataDTO.setDataList(Collections.singletonList(creditDocService.getChequeDetails(id)));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successfully");
        } catch (CustomValidationException ce) {
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error("Unable fetching all payments for customer id " + id +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getMessage());
        } catch (Exception e) {
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error("Unable fetching all payments for customer id " + id +"  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getMessage());
        }
        MDC.remove("type");
        return genericDataDTO;
    }


//    @GetMapping("/getChequeDetail/{id}")
//    public ResponseEntity<?> getChequePaymode(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        //String SUBMODULE = MODULE + " [getChequekPaymode()] ";
//        MDC.put("type", "fetch");
//        //Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//
//            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
//            ChequeDetailsPojo chequeDetailsSavePojoList = creditDocService.getChequeDetails(id);
//            response.put("invoiceList", chequeDetailsSavePojoList);
//            RESP_CODE = APIConstants.SUCCESS;
//            //logger.info("Fetching invoice by customer " + customersService.get(customerid).getUsername() + "   :  request: { From : {}}; Response : {{}}", MODULE, RESP_CODE, response);
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to Fetch Invoice ByCustomer  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
//            response.put(APIConstants.ERROR_TAG, HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetch Invoice ByCustomer :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, RESP_CODE, response, e.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }

    @RequestMapping(value = "/documentForPayment/download/{docId}/{custId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPaymentDocument(@PathVariable Long docId, @PathVariable Integer custId) {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [downloadDocument()] ";
        Resource resource = null;
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (null == customers.get()) {
                return ResponseEntity.notFound().build();
            }
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(docId.intValue());
            if (null == creditDocument) {
                return ResponseEntity.notFound().build();
            }
            FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
            resource = service.getBarterDoc(customers.get().getUsername().trim(), creditDocument.get().getFilename());
            //resource=service.getInvoice("12123");
            String contentType = "application/octet-stream";
            if (resource != null && resource.exists()) {
                logger.info("Downloading document with  "+docId+" downloaded Successfully  :  request: { From : {} }; Response : {{}}",SUBMODULE, APIConstants.SUCCESS);
                System.out.println("dowload document");
                return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"").body(resource);
            } else {
                logger.error("Unable to downloadDocument "+docId+" :  request: { From : {}}; Response : {{}};Error :{} ;", SUBMODULE, HttpStatus.NOT_FOUND,ResponseEntity.notFound());
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            logger.error("Unable to downloadDocument "+docId+"   :  request: { From : {}}; Response : {{}};Error :{} ;exception: {}", SUBMODULE,HttpStatus.NOT_FOUND,ResponseEntity.notFound(),ex.getStackTrace());
            // ApplicationLogger.logger.error(SUBMODULE + ex.getStackTrace(), ex);
        }
        MDC.remove("type");
        return null;
    }


}
