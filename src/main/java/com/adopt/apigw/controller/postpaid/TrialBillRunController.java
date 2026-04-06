package com.adopt.apigw.controller.postpaid;

import java.time.format.DateTimeFormatter;
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
import com.adopt.apigw.model.postpaid.TrialBillRun;
import com.adopt.apigw.pojo.SearchTrialBillRun;
import com.adopt.apigw.pojo.TriggerTrialBillRun;
import com.adopt.apigw.service.postpaid.TrialBillRunService;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class TrialBillRunController extends BaseController<TrialBillRun>{


	
	private static final String MODEL_DISP_NAME="Trial BilRun Details";
	private static final String MODEL_URI_NAME="trialbillrun";    
    private static final String RETURN_URI_LIST="postpaid/invoice/trialbillrunhistory";
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private TrialBillRunService entityService;

    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("billRunStatusMap")
    TreeMap<String, String> getBillRujStatusMap(){
    	return UtilsCommon.getBillRunStatusMap();
    }
        
    //@PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '1')")
    @RequestMapping(value = {"/trialbillrun/search/{pageNumber}","/trialbillrun/search"})
    public String list(@PathVariable(required = false) Integer pageNumber,@ModelAttribute("entity") SearchTrialBillRun entity,@RequestParam(name="bid",defaultValue="")  String billRunId,  @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
//    	if((billRunId !=null && !"".equals(billRunId) || (entity!=null && entity.getBillrunid()!=null)) ) {
//    		
//    		if("".equals(billRunId) && entity!=null && entity.getBillrunid() !=null) {
//    			billRunId=String.valueOf(entity.getBillrunid());
//    		}
//    		
//    		billRunList=entityService.findById(Integer.valueOf(billRunId));
//    		entity = new SearchTrialBillRun();
//    		entity.setBillrunid(Integer.valueOf(billRunId));
//    		if(billRunList==null || billRunList.size() == 0) {
//    			model.addAttribute("errorFlash","No results found");	
//    		}else {
//    			entity.setBillrunlist(billRunList);
//    		}
//    	}else if(entity!=null) {
//    		billRunList=entityService.getAllEntities();
//    		if(billRunList==null || billRunList.size() == 0) {
//    			model.addAttribute("errorFlash","No results found");	
//    		}else {
//    			entity.setBillrunlist(billRunList);
//    		}
//    	}else {
//    		entity = new SearchTrialBillRun();
//    	} 
    	
    	if(entity==null) {
    		entity=new SearchTrialBillRun();
    	}
    	entity.setBillrunlist(entityService.findBillRunDetails(entity));
    	
    	if(flashMsgType!=null && flashMsg!=null) {
    		if(flashMsgType.equals("error")) {
    			model.addAttribute("errorFlash",flashMsg);
    		}
    	}
        model.addAttribute("entity",entity);
        return RETURN_URI_LIST;
    }
    
    //@PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '1')")
    @RequestMapping(value = {"/trialbillrun/generatepdf"}, method = RequestMethod.GET)
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
        return "redirect:/trialbillrun/search";

    }
    
    //@PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '1')")
    @RequestMapping(value = {"/trialbillrun/revertbillrun"}, method = RequestMethod.GET)
    public String revertbillrun(@RequestParam(name="bid",defaultValue="")  String billRunId,  RedirectAttributes redirectAttributes,Model model) {

    	boolean bError=true;
    	try {
	    	if(billRunId!=null) {
	    		boolean bStatus=entityService.revertbillrun(billRunId);
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
        return "redirect:/trialbillrun/search";

    }
    
//  //@PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '1')")
//    @RequestMapping(value = {"/trialbillrun/emailpdf"}, method = RequestMethod.GET)
//    public String emailPDFs(@RequestParam(name="bid",defaultValue="")  String billRunId,  RedirectAttributes redirectAttributes,Model model) {
//
//    	boolean bError=true;
//    	try {
//	    	if(billRunId!=null) {
//	    		boolean bStatus=entityService.emailInvoices(billRunId);
//	    		if(bStatus) {
//	    			bError=false;
//	    		}
//	    	}
//    	}catch(Exception e) {
//    		bError=true;
//    	}
//    	if(bError) {
//    		redirectAttributes.addAttribute("flashMsgType","Error");    		
//    		redirectAttributes.addAttribute("flashMessage","Error performing operation.Please try after sometime..");    		
//    	}
//        return "redirect:/trialbillrun/search";
//
//    }
        
    @RequestMapping(value = {"/trialbillrun"}, method = RequestMethod.GET)
    public String invoicelist(Model model) {
        model.addAttribute("pageuri", MODEL_URI_NAME);
        model.addAttribute("entity",entityService.getSearchTrialBillRunForAdd());
        return RETURN_URI_LIST;
    }
    
    
    @RequestMapping(value = {"/trtrialbillrun"}, method = RequestMethod.GET)
    public String trialbillrun(Model model) {
        model.addAttribute("pageuri", "Bill Run");
        model.addAttribute("entity",entityService.getTriggerTrialBillRunForAdd());
        return "postpaid/invoice/trialbillrun";
    }
    
//    @RequestMapping(value = {"/trialtrialbillrun"}, method = RequestMethod.GET)
//    public String trialtrialbillrun(Model model) {
//    	TriggerTrialBillRun entity = new TriggerTrialBillRun();
//        model.addAttribute("pageuri", "Trial Bill Run");
//        model.addAttribute("entity",entity);
//        return "postpaid/invoice/trialbillrun";
//    }
    
    @RequestMapping(value = {"/trialbillrunevent"},method = RequestMethod.POST)
    public String list(@ModelAttribute("entity") TriggerTrialBillRun entity,  Model model) {
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
    		return "postpaid/invoice/trialbillrun";

    	}else {
        	return "redirect:/trialbillrun/search?bid=" + billRunId;

    	}
    }
}
