package com.adopt.apigw.controller.postpaid;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

import com.adopt.apigw.model.postpaid.PartnerDebitDocument;
import com.adopt.apigw.repository.postpaid.PartnerCreditDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.pojo.SearchPartnerCreditDocs;
import com.adopt.apigw.service.common.FileSystemService;
import com.adopt.apigw.service.postpaid.PartnerCreditDocService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class PartnerCreditDocController extends BaseController<CustomerAddress>{


	
	private static final String MODEL_URI_NAME="pinvoice";    
    private static final String RETURN_URI_LIST="postpaid/pinvoice/pinvoicelist";
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private PartnerCreditDocService entityService;
    @Autowired
    private PartnerCreditDocumentRepository partnerCreditDocumentRepository;

        
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
        
    @RequestMapping(value = {"/pinvoice/search/{pageNumber}","/pinvoice/search"})
    public String list(@PathVariable(required = false) Integer pageNumber,@RequestParam(name="bid",defaultValue="")  String billRunId,@ModelAttribute("entity") SearchPartnerCreditDocs entity,  @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	
    	if(entity!=null) {
    		List<PartnerDebitDocument> debitDocList=null;
    		if(billRunId!=null && !"".equals(billRunId)) {
    			entity.setBillrunid(Integer.valueOf(billRunId));
    			debitDocList=entityService.searchByBillRunId(billRunId);
    		}else {
    			debitDocList=entityService.getAllEntities(pageNumber, CommonConstants.DB_PAGE_SIZE);
    		}
    		if(debitDocList==null || debitDocList.size() == 0) {
    			model.addAttribute("errorFlash","No results found");
    		}else {
    			entity.setDebitdoclist(debitDocList);
    		}
    	}else {
    		entity = new SearchPartnerCreditDocs();
    	} 
//		entity = new SearchPartnerCreditDocs();

        model.addAttribute("entity",entity);
        return RETURN_URI_LIST;
    }

    
    
    
    @RequestMapping(value = {"/pinvoice"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getSearchPartnerCreditDocsForInvoiceList());
        return RETURN_URI_LIST;
    }
    
    @RequestMapping(value = {"/pinvoice/download/{invoiceid}"}, method = RequestMethod.GET)
    public ResponseEntity<Resource>  downloadInvoice(@PathVariable Integer invoiceid,Model model,@RequestParam("mvnoId") Integer mvnoId)
    { 	 	   
    	PartnerDebitDocument doc = entityService.get(invoiceid,mvnoId);
    	
    	
 	   	Resource resource = null;              
        FileSystemService service=com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
        resource=service.getPartnerInvoice(doc.getBillrunid() + File.separator+ doc.getDocnumber() + ".pdf");
        //resource=service.getInvoice("12123");
        String contentType = "application/octet-stream";
        if(resource!=null && resource.exists()) {
        	 return ResponseEntity.ok()
        	 .contentType(MediaType.parseMediaType(contentType))
        	 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        	 .body(resource);        	 
        }else {
        	return ResponseEntity.notFound().build();
        }
    }

}
