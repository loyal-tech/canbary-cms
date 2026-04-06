package com.adopt.apigw.controller.radius.radiusprofile;

import com.adopt.apigw.repository.radius.RadProfCheckItemRepository;
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
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.model.radius.RadiusProfileCheckItem;
import com.adopt.apigw.service.radius.RadProfileCheckItemService;
import com.adopt.apigw.service.radius.RadiusProfileService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class RadiusProfileController extends BaseController<RadiusProfile>{


    private RadiusProfileService entityService;
    private RadProfileCheckItemService checkItemService;
    @Autowired
    private RadProfCheckItemRepository radProfCheckItemRepository;
    
    private static final String MODEL_DISP_NAME="Radius Profile";
    private static final String RETURN_URI_INDEX="redirect:/radprofile/1";
    private static final String RETURN_URI_LIST="radius/radiusprofile/radprofilelist";
    private static final String RETURN_URI_ADD_EDIT="radius/radiusprofile/radprofileform"; 
    private static final String SORT_BY_COLUMN="id"; 
    private static final String RETURN_URI_CONDITION="radius/radiusprofile/profilecond";
    
    @Autowired
    public void setEntityService(RadiusProfileService entityService) {
        this.entityService = entityService;
    }
    
    @Autowired
    public void setCheckItemService(RadProfileCheckItemService entityService) {
        this.checkItemService = entityService;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.RadiusProfile', '1')")    
    @RequestMapping(value = "/radprofile")
    public String index() {
        return RETURN_URI_INDEX;
    }
    
    @RequestMapping(value = "/radprofile/{pageNumber}", method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber,@RequestParam(name="s",defaultValue="")  String search,@ModelAttribute("flashMsg") String flashMsg, Model model) {
    	Page<RadiusProfile> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);

    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
    	setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	model.addAttribute("statusMap", UtilsCommon.getRadProfileStatusMap());
        return RETURN_URI_LIST;
    }

    @RequestMapping("/radprofile/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getRadiusProfileForAdd());
    	model.addAttribute("statusMap", UtilsCommon.getRadProfileStatusMap());
        return RETURN_URI_ADD_EDIT;

    }

    @RequestMapping("/radprofile/edit/{id}")
    public String edit(@PathVariable Integer id, @ModelAttribute("flashMsg") String flashMsg,Model model) throws Exception{
        model.addAttribute("entity", entityService.getRadiusProfileForEdit(id));
    	model.addAttribute("statusMap", UtilsCommon.getRadProfileStatusMap());
    	
    	if(flashMsg.contentEquals("AddSuccessCheckItem")) {
            model.addAttribute("successFlash", "Profile Condition  Added Successfully");
    	}else if (flashMsg.contentEquals("EditSuccessCheckItem")) {
            model.addAttribute("successFlash", "Profile Condition  Updated Successfully");
    	}else if (flashMsg.contentEquals("DelSuccessCheckItem")) { 
            model.addAttribute("successFlash", "Profile Condition  Deleted Successfully");    		
    	}
        return RETURN_URI_ADD_EDIT;

    }
    
    @RequestMapping(value = "/radprofile/save", method = RequestMethod.POST)
    public String save(RadiusProfile entity,RedirectAttributes ra) {
    	String operation="edit";
    	String flashMsg="";
    	
    	try{
	    	if(entity !=null && entity.getId()==null){
	    		operation="add";
	    	}
	    	
	    	RadiusProfile save = entityService.saveRadiusProfile(entity);
	        if(save !=null){
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="AddSuccess";
	        	}else{
	        		flashMsg="EditSuccess";
	        	}
	        }else{
	    		flashMsg="error";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
        ra.addFlashAttribute("flashMsg", flashMsg);
        return RETURN_URI_INDEX;
   	}

    @RequestMapping("/radprofile/delete/{id}")
    public String delete(@PathVariable Integer id) throws Exception{
    	entityService.deleteRadiusProfile(id);
        return RETURN_URI_INDEX;
    }
    
    @RequestMapping("/radprofile/addcond/{id}")
    public String addCondition(@PathVariable Integer id, Model model) throws Exception {
        model.addAttribute("entity", entityService.addCondition(id));
        return RETURN_URI_CONDITION;
    }
    
    @RequestMapping("/radprofile/editcond/{id}")
    public String editCondition(@PathVariable Integer id, Model model) throws Exception {
        model.addAttribute("entity", entityService.editCondition(id));        
        return RETURN_URI_CONDITION;
    }
    
    @RequestMapping("/radprofile/deletecond/{id}")
    public String deleteCondition(@PathVariable Integer id, RedirectAttributes ra,Model model) throws Exception {
    	Integer radProfileId = radProfCheckItemRepository.findById(id).get().getRadiusProfile().getId();
    	entityService.deleteCondition(id);
    	ra.addFlashAttribute("flashMsg", "DelSuccessCheckItem");
        return "redirect:/radprofile/edit/"+ radProfileId;    	        
    }
           
    @RequestMapping(path = {"/radprofile/replyitem"},params = "addreplyitem")
    public 	String addReplyItem(RadiusProfileCheckItem checkItem, Model model) {
    	if(checkItem.getReplyItems()==null) {
    		checkItem.setReplyItems(entityService.getCheckItemReplyItemList());
    	}    	
    	checkItem.getReplyItems().add(entityService.getCheckItemReplyItem());
    	model.addAttribute("entity", checkItem);
        return RETURN_URI_CONDITION;
    }
    
    @RequestMapping(path = {"/radprofile/replyitem"},params = "removeindex")
    public 	String removeReplyItem(RadiusProfileCheckItem checkItem, @RequestParam("removeindex") int index,Model model) {
    	model.addAttribute("entity", entityService.removeReplyItem(checkItem, index));
        return RETURN_URI_CONDITION;
    } 
    
    @RequestMapping(value = "/radprofile/replyitem", params="save")
    public String saveCheckItem(RadiusProfileCheckItem entity,RedirectAttributes ra,Model model) {
    	String operation="edit";
    	String flashMsg="";
    	
    	try{
	    	if(entity !=null && entity.getId()==null){
	    		operation="add";
	    	}else {
//	    		try {
//	    		checkItemService.deleteReplteItems(entity.getId());
//	    		}catch(Exception e) {
//	    			
//	    		}
	    	}
	    	
	    	RadiusProfileCheckItem save = checkItemService.saveRadiusProfileCheckItem(entity);
	        if(save !=null){
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="AddSuccessCheckItem";
	        	}else{
	        		flashMsg="EditSuccessCheckItem";
	        	}
	        }else{
	    		flashMsg="error";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
        ra.addFlashAttribute("flashMsg", flashMsg);
        return "redirect:/radprofile/edit/"+ entity.getRadiusProfile().getId();
   	}
}
