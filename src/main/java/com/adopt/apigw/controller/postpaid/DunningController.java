package com.adopt.apigw.controller.postpaid;

import java.util.ArrayList;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.DunningRule;
import com.adopt.apigw.model.postpaid.DunningRuleAction;
import com.adopt.apigw.service.postpaid.DunningRuleService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class DunningController extends BaseController<DunningRule>{


	
	private static final String MODEL_DISP_NAME="Dunning Rule";
	private static final String MODEL_URI_NAME="dunning";    
    private static final String RETURN_URI_INDEX="redirect:/dunning/1";
    private static final String RETURN_URI_LIST="postpaid/dunning/dunninglist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/dunning/dunningform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private DunningRuleService entityService;
        
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }

    @ModelAttribute("creditClassMap")
    TreeMap<String, String> getCreditClassMap(){
    	return UtilsCommon.getCreditClassMap();
    }

    @ModelAttribute("dunningActionMap")
    TreeMap<String, String> getDunningActionMap(){
    	return UtilsCommon.getDunningActionMap();
    }

    
    @RequestMapping(value = {"/dunning/{pageNumber}","/dunning"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
  
    	Page<DunningRule> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/dunning/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getDunningRuleForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/dunning/edit/{id}")
    public String edit(@PathVariable Integer id, Model model,Integer mvnoId) {
        model.addAttribute("entity", entityService.getDunningRuleForEdit(id,mvnoId));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")
    @RequestMapping(value = "/dunning/save", method = RequestMethod.POST)
    public String save(DunningRule bean,final RedirectAttributes ra) {
    	
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
    	  
    		DunningRule save = entityService.saveDunningRule(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Dunning Rule Added Successfully";
	        	}else{
	        		flashMsg="Dunning Rule Updated Successfully";
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

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '4')")
    @RequestMapping("/dunning/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) {
    	entityService.delete(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")        
    @RequestMapping(path = {"/dunning/addaction"})
    public 	String addSlab(DunningRule dunning, Model model) {
    	if(dunning.getActionList()==null) {
    		dunning.setActionList(new ArrayList<DunningRuleAction>());
    	}    	
    	dunning.getActionList().add(new DunningRuleAction());
    	model.addAttribute("entity", dunning);
        model.addAttribute("pageuri", MODEL_URI_NAME);
    	return RETURN_URI_ADD_EDIT;
    }
    
    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DunningRule', '2')")        
    @RequestMapping(path = {"/dunning/removeaction"},params = "removeindex")
    public 	String deleteSlab(DunningRule dunning, @RequestParam("removeindex") int index,Model model) {
    	dunning.getActionList().remove(index);
    	model.addAttribute("entity", dunning);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    } 

//    
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Dunning', '1')")
//    @RequestMapping(value = {"/dunning/{pageNumber}","/dunning"}, method = RequestMethod.GET)
//    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	if(pageNumber==null) {
//    		pageNumber=1;
//    	}
//    	Page<Dunning> page =Page.empty();
//    
//    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
//        return RETURN_URI_LIST;
//    }
//
//    
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Dunning', '2')")
//    @RequestMapping("/dunning/add")
//    public String add(Model model) {
//        model.addAttribute("entity", new Dunning());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
    
}
