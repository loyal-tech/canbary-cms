package com.adopt.apigw.controller.postpaid;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.pojo.RecordPayment;
import com.adopt.apigw.pojo.SearchPayment;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CreditDocMessage;
import com.adopt.apigw.repository.postpaid.CreditDebtMappingRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.adopt.apigw.utils.CurrencyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.jdo.annotations.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class PaymentController extends BaseController<BillRun> {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);


	private static final String MODEL_DISP_NAME = "Manage Payments";
	private static final String MODEL_URI_NAME = "payment";
	private static final String RETURN_URI_LIST = "postpaid/payment/paymenthistory";
	private static final String SORT_BY_COLUMN = "id";

	@Autowired
	private CreditDocService entityService;

	@Autowired
	private CustomersService custService;

	@Autowired
	private CustomerLedgerService ledgerService;

    @Autowired
    private CustomerLedgerDtlsService ledgerDtlsService;

    @Autowired
    private StaffUserService staffUserSerivce;

    @Autowired
    private PartnerService partnerService;
    
    @Autowired
    private CustomerAddressService custAddrService;
	@Autowired
	CreditDebtMappingRepository creditDebtMappingRepository;
	@Autowired
	MessageSender messageSender;
	@Autowired
	private KafkaMessageSender kafkaMessageSender;
    
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("paymentStatusMap")
    TreeMap<String, String> getPaymentStatusMap(){
    	return UtilsCommon.getPaymentStatusMap();
    }

    @ModelAttribute("paymentModeMap")
    TreeMap<String, String> getPaymentModeMap(){
    	return UtilsCommon.getPaymentModeMap();
    }
    
    @ModelAttribute("custList")
    List<Customers> getCustomerList(){
    	return custService.getAllCustomers();
    }

    @ModelAttribute("staffList")
    List<StaffUser> getStaffList(){
    	return staffUserSerivce.getAllUsers();
    }

    @ModelAttribute("partnerList")
    List<Partner> getPartnerList(){
    	return partnerService.getAllPartners();
    }
    //@PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '1')")
    @RequestMapping(value = {"/payment/search/{pageNumber}","/payment/search"})
    public String list(@PathVariable(required = false) Integer pageNumber,@ModelAttribute("entity") SearchPayment entity,@RequestParam(name="bid",defaultValue="")  String billRunId,  @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model,@RequestParam("mvnoId") Integer mvnoId) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}    	    	
    	if(entity==null) {
    		entity=new SearchPayment();
    	}
    	entity.setPaymentlist(entityService.findCreditDocuments(entity,null,mvnoId).stream().collect(Collectors.toList()));
    	
    	if(flashMsgType!=null && flashMsg!=null) {
    		if(flashMsgType.equals("error")) {
    			model.addAttribute("errorFlash",flashMsg);
    		}
    	}
        model.addAttribute("entity",entity);
        return RETURN_URI_LIST;
    }
    
    @RequestMapping(value = {"/payment"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getSearchPaymentForInvoiceList());
        return RETURN_URI_LIST;
    }
    
    @RequestMapping(value = {"/payment/add"}, method = RequestMethod.GET)
    public String addpayment(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getRecordPaymentForAddPayment());
        return "postpaid/payment/recordpayment";
    }
    
    
    @RequestMapping(value = "/payment/save", method = RequestMethod.POST)
    public String savePayment(RecordPayment entity , Model model,@RequestParam("mvnoId") Integer mvnoId) {
    	String flashMsg="error";    	
    	CreditDocument doc = null;
    	try{
			doc = entityService.saveRecordPayment(entity,mvnoId);
			if(doc!=null){
        		flashMsg="success";
        	}else {
	    		flashMsg="error";
        	}
	        
    	}catch(Exception e){
    		e.printStackTrace();
    		flashMsg="error";
    	}

    	if("success".equalsIgnoreCase(flashMsg) && doc !=null) {
    		entity=new RecordPayment();
    		model.addAttribute("successFlash","Payment recorded successufully. Reference no: " + doc.getReferenceno());
    	}else {
    		model.addAttribute("errorFlash","Error Processing Request, Please try after sometime...");    		
    	}
    	model.addAttribute("entity",entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return "postpaid/payment/recordpayment";

    }
    
    
    @Transactional
    @RequestMapping(value = "/payment/approve", method = RequestMethod.POST)
    public String approvePayment(SearchPayment entity , Model model,@RequestParam("mvnoId") Integer mvnoId) {
    	String flashMsg="error";    	
    	CreditDocument doc = null;
		CustomerLedger ledger=null;
		CustomerLedgerDtls ledgerDtls=null;
    	try{

    		if(entity.getIdlist()!=null) {
    			
    			String idList[] = entity.getIdlist().split(",");
    			if(idList !=null && idList.length > 0) {
	    			for (String id : idList) {
						doc=null;ledger=null;ledgerDtls=null;					
						doc=entityService.get(Integer.valueOf(id),mvnoId);
						logger.info("Processing ApprovePayment for:" + doc.getId() + "#" + doc.getCustomer().getAcctno() + "#" + doc.getCustomer().getPhone());					
						if(doc.getStatus().equals(UtilsCommon.PAYMENT_STATUS_PENDING)) {
							//Update Ledger
							ledger=ledgerService.getCustomerLeger(doc.getCustomer()).get(0);
		    				ledger.setTotalpaid(ledger.getTotalpaid() + doc.getAmount());
		    				ledger=ledgerService.save(ledger);
		    				
		    				//Create entry for Ledger Details
		    				ledgerDtls=new CustomerLedgerDtls();
		    				ledgerDtls.setAmount(doc.getAmount());
		    				ledgerDtls.setCreditdocid(doc.getId());
		    				ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
		    				ledgerDtls.setCustomer(doc.getCustomer());
		    				ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
		    				ledgerDtls = ledgerDtlsService.save(ledgerDtls);
		    				
		    				doc.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
		    				doc.setApproverid(getLoggedInUserId());
		    				doc.setRemarks(entity.getRemarks());
		    				doc.setXmldocument(assemblePaymentXML(doc));
		    				doc=entityService.save(doc);
							CreditDocMessage creditDocMessage = new CreditDocMessage(doc,null);
							kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//							messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS);
//							messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);
							kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
						}
					}
	    			flashMsg="success";
    			}
    		}

    	}catch(Exception e){
    		e.printStackTrace();
    		flashMsg="error";
    	}

    	if(entity==null) {
    		entity=new SearchPayment();
    	}else {
    		entity.setIdlist(null);
    		entity.setRemarks(null);
    	}
    	
    	entity.setPaymentlist(entityService.findCreditDocuments(entity,null,mvnoId).stream().collect(Collectors.toList()));
    	if("success".equalsIgnoreCase(flashMsg) && doc !=null) {
    		model.addAttribute("successFlash","Payment processed sucessfully");
    	}else {
    		model.addAttribute("errorFlash","Error Processing Request, Please try after sometime...");    		
    	}
    	model.addAttribute("entity",entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_LIST;

    }
    
    
    
    @Transactional
    @RequestMapping(value = "/payment/reject", method = RequestMethod.POST)
    public String rejectPayment(SearchPayment entity , Model model,@RequestParam("mvnoId") Integer mvnoId) {
    	String flashMsg="error";    	
    	CreditDocument doc = null;
    	try{

    		if(entity.getIdlist()!=null) {
    			
    			String idList[] = entity.getIdlist().split(",");
    			if(idList !=null && idList.length > 0) {
	
	    			for (String id : idList) {
						doc=null;
						doc=entityService.get(Integer.valueOf(id),mvnoId);
						logger.info("Processing RejectPayment for:" + doc.getId() + "#" + doc.getCustomer().getAcctno() + "#" + doc.getCustomer().getPhone());
						if(doc.getStatus().equals(UtilsCommon.PAYMENT_STATUS_PENDING)) {
		    				doc.setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
		    				doc.setApproverid(getLoggedInUserId());
		    				doc.setRemarks(entity.getRemarks());
		    				doc=entityService.save(doc);
						}
					}
	    			flashMsg="success";
    			}
    		}

    	}catch(Exception e){
    		e.printStackTrace();
    		flashMsg="error";
    	}

    	if(entity==null) {
    		entity=new SearchPayment();
    	}else {
    		entity.setIdlist(null);
    		entity.setRemarks(null);
    	}
    	
    	entity.setPaymentlist(entityService.findCreditDocuments(entity,null,mvnoId).stream().collect(Collectors.toList()));
    	if("success".equalsIgnoreCase(flashMsg) && doc !=null) {
    		model.addAttribute("successFlash","Payment processed sucessfully");
    	}else {
    		model.addAttribute("errorFlash","Error Processing Request, Please try after sometime...");    		
    	}
    	model.addAttribute("entity",entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_LIST;

    }
    
    @PostMapping("/payment/bulksave") // //new annotation since 4.3
    public String saveBulkPayment(@RequestParam("file") MultipartFile file,Model model) {
    	
    	String TMP_DIR = System.getProperty("java.io.tmpdir");
    	
    	String flashMsgType="error";
    	String flashMsg=null;

    	if (file.isEmpty()) {
    		flashMsg="Ooops, Please select file to process";
        }else {
	    	BufferedReader br = null;
	        try {
	
	            // Get the file and save it somewhere
	            byte[] bytes = file.getBytes();
	            Path path = Paths.get(TMP_DIR + file.getOriginalFilename());
	            Files.write(path, bytes);
	            logger.info("File is "+path.toString());
	            String line = "";
	            String cvsSplitBy = ",";

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	            br = new BufferedReader(new FileReader(path.toString()));
	            while ((line = br.readLine()) != null) {
	                // use comma as separator
	                String[] paymentDetails = line.split(cvsSplitBy);
	                logger.info("customer "+paymentDetails);
	                CreditDocument doc =new CreditDocument();
	        		doc.setCustomer(custService.getCustomerFromAcctno(paymentDetails[0]).get(0));
	                LocalDate localDate = LocalDate.parse(paymentDetails[1], formatter);
	        		doc.setPaymentdate(localDate);
	        		doc.setPaymode(paymentDetails[2]);
	        		doc.setAmount(Double.valueOf(paymentDetails[3]));
	        		doc.setStatus(UtilsCommon.PAYMENT_STATUS_PENDING);
	        		if(UtilsCommon.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(paymentDetails[2])){
	        			doc.setPaydetails1(paymentDetails[4]);
	        			doc.setPaydetails2(paymentDetails[5]);
	        			doc.setPaydetails3(paymentDetails[6]);
	        		}else {
	        			doc.setPaydetails1(paymentDetails[7]);
	        		}
	    			doc.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
	                doc= entityService.save(doc);
	            }
	            flashMsgType="success";           
	        } catch (Exception e) {
	            e.printStackTrace();
	            flashMsgType="error";
	        }finally{
	        	try{
	        		if(br!=null){
	        			br.close();
	        		}
	        	}catch(Exception e){
	        		e.printStackTrace();
	        	}
	        }	        
        }
    	
    	model.addAttribute("entity",new RecordPayment());
        model.addAttribute("pageuri", MODEL_URI_NAME);            
    	if("success".equalsIgnoreCase(flashMsgType)) {
    		model.addAttribute("successFlash","Payment recorded successufully");
    	}else {
    		if(flashMsg==null) {
    			flashMsg="Error Processing Request, Please try after sometime...";
    		}
    		model.addAttribute("errorFlash",flashMsg);    		
    	}
        return "postpaid/payment/recordpayment";

    }


	@RequestMapping(value = {"/payment/download/{paymentid}"}, method = RequestMethod.GET)
	public ResponseEntity<Resource> downloadInvoice(@PathVariable Integer paymentid, Model model) {
		//DebitDocument doc = entityService.get(invoiceid);
		Resource resource = null;
		FileSystemService service = com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
		resource = service.getPaymentReceipt(paymentid + File.separator + paymentid + ".pdf");
		String contentType = "application/octet-stream";
		if (resource != null && resource.exists()) {
			return ResponseEntity.ok()
					.contentType(MediaType.parseMediaType(contentType))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}
    
    public String assemblePaymentXML(CreditDocument doc) {
    	String paymentXML = new String(CommonConstants.PAY_RECEIPT);

    	CustomerAddress homeAddress = custAddrService.findByAddressTypeAndCustomer(UtilsCommon.ADDR_TYPE_HOME, doc.getCustomer());
    	paymentXML=paymentXML.replace(CommonConstants.PR_RECEPIT_ID, String.valueOf(doc.getId()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_CUST_ID, String.valueOf(doc.getCustomer().getId()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_RECEIPT_NO, String.valueOf(doc.getId()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_RECEIPT_DATE, doc.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
    	paymentXML=paymentXML.replace(CommonConstants.PR_PAY_AMOUNT, String.valueOf(doc.getAmount()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_PAY_AMOUNT_WORDS, CurrencyUtil.convert(Math.round(doc.getAmount())));
    	
    	if(doc.getPaydetails1()!=null)
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, String.valueOf(doc.getPaydetails1()));
    	else
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, "-");
    	
    	if(doc.getPaydetails2()!=null)
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS2, String.valueOf(doc.getPaydetails2()));
    	else
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS2,"-");
    	
    	if(doc.getPaydetails3()!=null)
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, String.valueOf(doc.getPaydetails3()));
    	else
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, "-");

    	if(doc.getPaydetails4()!=null)
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, String.valueOf(doc.getPaydetails4()));
    	else
    		paymentXML=paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, "-");

    	paymentXML=paymentXML.replace(CommonConstants.PR_PAY_REFNO, String.valueOf(doc.getReferenceno()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_PHONE, String.valueOf(doc.getCustomer().getPhone()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_OUTSTANDING, String.valueOf(doc.getCustomer().getOutStandingAmount()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS_TYPE, "Home");
    	if(homeAddress!=null) {
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS1, String.valueOf(homeAddress.getAddress1()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(homeAddress.getAddress2()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(homeAddress.getAddress2()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_CITY, String.valueOf(homeAddress.getCity().getName()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_STATE, String.valueOf(homeAddress.getState().getName()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_COUNTRY, String.valueOf(homeAddress.getCountry().getName()));
	    	paymentXML=paymentXML.replace(CommonConstants.PR_PIN, String.valueOf(homeAddress.getPincode()));
    	}else {
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS1, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_CITY, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_STATE, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_COUNTRY, "-");
	    	paymentXML=paymentXML.replace(CommonConstants.PR_PIN, "-");	
    	}
    	paymentXML=paymentXML.replace(CommonConstants.PR_SUBSCR_ID, String.valueOf(doc.getCustomer().getId()));
    	paymentXML=paymentXML.replace(CommonConstants.PR_CUST_EMAIL, String.valueOf(doc.getCustomer().getEmail()));

    	return  paymentXML;
    }
    
}
