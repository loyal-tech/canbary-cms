package com.adopt.apigw.controller.postpaid;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.model.postpaid.PartnerBillRun;
import com.adopt.apigw.pojo.SearchPartnerBillRun;
import com.adopt.apigw.pojo.TriggerBillRun;
import com.adopt.apigw.service.postpaid.PartnerBillRunService;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class PartnerBillRunController extends BaseController<CustomerAddress>{


	
	private static final String MODEL_DISP_NAME="BilRun Details";
	private static final String MODEL_URI_NAME="billrun";    
    private static final String RETURN_URI_LIST="postpaid/pinvoice/pbillrunhistory";
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private PartnerBillRunService entityService;

    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("billRunStatusMap")
    TreeMap<String, String> getBillRujStatusMap(){
    	return UtilsCommon.getBillRunStatusMap();
    }
        
    @RequestMapping(value = {"/pbillrun/search/{pageNumber}","/pbillrun/search"})
    public String list(@PathVariable(required = false) Integer pageNumber,@ModelAttribute("entity") SearchPartnerBillRun entity,@RequestParam(name="bid",defaultValue="")  String billRunId,  @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	
    	List<PartnerBillRun> billRunList=null;

    	if((billRunId!=null && !"".equals(billRunId) || (entity!=null && entity.getBillrunid()!=null)) ) {
    		
    		if("".equals(billRunId) && entity!=null && entity.getBillrunid() !=null) {
    			billRunId=String.valueOf(entity.getBillrunid());
    		}
    		
    		billRunList=entityService.findById(Integer.valueOf(billRunId));
    		entity = new SearchPartnerBillRun();
    		entity.setBillrunid(Integer.valueOf(billRunId));
    		if(billRunList==null || billRunList.size() == 0) {
    			model.addAttribute("errorFlash","No results found");	
    		}else {
    			entity.setBillrunlist(billRunList);
    		}
    	}else if(entity!=null) {
    		billRunList=entityService.getAllEntities();
    		if(billRunList==null || billRunList.size() == 0) {
    			model.addAttribute("errorFlash","No results found");	
    		}else {
    			entity.setBillrunlist(billRunList);
    		}
    	}else {
    		entity = new SearchPartnerBillRun();
    	} 
    	
    	if(flashMsgType!=null && flashMsg!=null) {
    		if(flashMsgType.equals("error")) {
    			model.addAttribute("errorFlash",flashMsg);
    		}
    	}
        model.addAttribute("entity",entity);
        return RETURN_URI_LIST;
    }
    
    @RequestMapping(value = {"/pbillrun/generatepdf"}, method = RequestMethod.GET)
    public String generatePDFs(@RequestParam(name="bid",defaultValue="")  String billRunId,  RedirectAttributes redirectAttributes,Model model) {

    	boolean bError=true;
    	try {
	    	if(billRunId!=null) {
	    		boolean bStatus=entityService.generateInvoice(billRunId);
	    		if(bStatus) {
	    			bError=false;
	    		}
	    	}
    	}catch(Exception e) {
    		e.printStackTrace();
    		bError=true;
    	}
    	if(bError) {
    		redirectAttributes.addAttribute("flashMsgType","Error");    		
    		redirectAttributes.addAttribute("flashMessage","Error performing operation.Please try after sometime..");    		
    	}
        return "redirect:/pbillrun/search";

    }
    
    @RequestMapping(value = {"/pbillrun/emailpdf"}, method = RequestMethod.GET)
    public String emailPDFs(@RequestParam(name="bid",defaultValue="")  String billRunId,  RedirectAttributes redirectAttributes,Model model) {

    	boolean bError=true;
    	try {
	    	if(billRunId!=null) {
	    		boolean bStatus=entityService.emailInvoices(billRunId);
	    		if(bStatus) {
	    			bError=false;
	    		}
	    	}
    	}catch(Exception e) {
    		bError=true;
    	}
    	if(bError) {
    		redirectAttributes.addAttribute("flashMsgType","Error");    		
    		redirectAttributes.addAttribute("flashMessage","Error performing operation.Please try after sometime..");    		
    	}
        return "redirect:/pbillrun/search";

    }
        
    @RequestMapping(value = {"/pbillrun"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getSearchPartnerBillRunForInvoiceList());
        return RETURN_URI_LIST;
    }
    
    
    @RequestMapping(value = {"/ptrbillrun"}, method = RequestMethod.GET)
    public String billrun(Model model) {
        model.addAttribute("pageuri", "Bill Run");
        model.addAttribute("entity",entityService.getTriggerBillRunForBillRun());
        return "postpaid/pinvoice/pbillrun";
    }
    
    @RequestMapping(value = {"/pbillrunevent"},method = RequestMethod.POST)
    public String list(@ModelAttribute("entity") TriggerBillRun entity,  Model model) {
    	boolean bError=true;String billRunId=null;
    	try {
    		if(entity!=null && entity.getBillrundate()!=null) {
	    		String billRunDate=entity.getBillrundate().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	    		billRunId=entityService.performBillRun(billRunDate, null);
	    		if(billRunId!=null) {
	    			bError=false;
	    		}
    		}
    	}catch(Exception e) {
    		bError=true;
    	}

    	if(bError) {
    		model.addAttribute("errorFlash","Error...Please after sometime");    		
        	model.addAttribute("entity",entity);
    		return "postpaid/pinvoice/pbillrun";

    	}else {
    		//model.addAttribute("successFlash","Bill Run completed successfully.");  
        	return "redirect:/pbillrun/search?bid=" + billRunId;

    	}
    }
//    @RequestMapping(value = {"/invoice/download/{invoiceid}"}, method = RequestMethod.GET)
//    public ResponseEntity<Resource>  downloadInvoice(@PathVariable Integer invoiceid,Model model)
//    { 	 	   
//    	DebitDocument doc = entityService.get(invoiceid);
//    	
//    	
// 	   	Resource resource = null;              
//        FileSystemService service=com.adopt.apigw.spring.SpringContext.getBean(FileSystemService.class);
//        resource=service.getInvoice(doc.getBillrunid() + File.separator+ doc.getDocnumber() + ".pdf");
//        //resource=service.getInvoice("12123");
//        String contentType = "application/octet-stream";
//        if(resource!=null && resource.exists()) {
//        	 return ResponseEntity.ok()
//        	 .contentType(MediaType.parseMediaType(contentType))
//        	 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//        	 .body(resource);        	 
//        }else {
//        	return ResponseEntity.notFound().build();
//        }
//    }

}
