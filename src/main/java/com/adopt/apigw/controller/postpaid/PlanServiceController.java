package com.adopt.apigw.controller.postpaid;

import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class PlanServiceController extends BaseController<PlanService>{


	
	private static final String MODEL_DISP_NAME="Service";
	private static final String MODEL_URI_NAME="service";    
    private static final String RETURN_URI_INDEX="redirect:/service/1";
    private static final String RETURN_URI_LIST="postpaid/service/servicelist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/service/serviceform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private PlanServiceService entityService;

    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("servicelist")
    List<PlanService> getServiceList(){
    	return entityService.getAllServices();
    }
        
    @RequestMapping(value = {"/service/{pageNumber}","/service"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
  
    	Page<PlanService> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/service/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getPlanServiceForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/service/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", entityService.getPlanServiceForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/service/save", method = RequestMethod.POST)
    public String save(PlanService bean,final RedirectAttributes ra) {
    	
    	String operation="edit";
    	String flashMsg="";
    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
    	
    	try{
	    	if(bean !=null && bean.getId()==null){
	    		operation="add";
//	    		bean.setCreatedById(getLoggedInUserId());
	    	}else {
//	    		bean.setLastModifiedById(getLoggedInUserId());
	    	}
    	
    		PlanService save = entityService.savePlanService(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Service Added Successfully";
	        	}else{
	        		flashMsg="Service Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	    	
        ra.addFlashAttribute("flashMsg", flashMsg);
        ra.addFlashAttribute("flashMsgType", flashMsgType);
        return RETURN_URI_INDEX;
	        
    	
    }

    @RequestMapping("/service/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
    	entityService.deletePlan(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
}
