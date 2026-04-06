package com.adopt.apigw.controller.postpaid;

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
import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class TaxController extends BaseController<Tax>{


	
	private static final String MODEL_DISP_NAME="Tax";
	private static final String MODEL_URI_NAME="tax";    
    private static final String RETURN_URI_INDEX="redirect:/tax/1";
    private static final String RETURN_URI_LIST="postpaid/tax/taxlist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/tax/taxform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private TaxService taxService;

    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("taxTypeMap")
    TreeMap<String, String> getTaxTypeMap(){
    	return UtilsCommon.getTaxTypeMap();
    }
    
    @ModelAttribute("taxGroupMap")
    TreeMap<String, String> getTaxGroupMap(){
    	return UtilsCommon.getTaxGroupMap();
    }
    
//    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.Tax', '1')")
    @RequestMapping(value = {"/tax/{pageNumber}","/tax"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
  
    	Page<Tax> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = taxService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = taxService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/tax/add")
    public String add(Model model) {
        model.addAttribute("entity", taxService.getTaxForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/tax/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", taxService.getTaxForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/tax/save", method = RequestMethod.POST)
    public String save(Tax bean,final RedirectAttributes ra) {
    	
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
    	   	
    		Tax save = taxService.saveTax(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Tax Added Successfully";
	        	}else{
	        		flashMsg="Tax Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	
    	if(operation.equals("add")) {
    		//Automatically open edit when adding new entity
    		return "redirect:/tax/edit/"+bean.getId();
    	}else {
	        ra.addFlashAttribute("flashMsg", flashMsg);
	        ra.addFlashAttribute("flashMsgType", flashMsgType);
	        return RETURN_URI_INDEX;
	        
    	}
    }

    @RequestMapping("/tax/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception {
    	taxService.deleteTax(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }

    @RequestMapping(path = {"/tax/addslab"})
    public 	String addSlab(Tax tax, Model model) {
    	if(tax.getSlabList()==null) {
    		tax.setSlabList(taxService.getTaxTypeSlabList());
    	}    	
    	tax.getSlabList().add(taxService.getTaxTypeSlab());
    	model.addAttribute("entity", tax);
        model.addAttribute("pageuri", MODEL_URI_NAME);
    	return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping(path = {"/tax/removeslab"},params = "removeindex")
    public 	String deleteSlab(Tax tax, @RequestParam("removeindex") int index,Model model) {
    	model.addAttribute("entity",taxService.deleteSlab(tax, index));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    } 

    @RequestMapping(path = {"/tax/addtier"})
    public 	String addTier(Tax tax, Model model) {
    	if(tax.getTieredList()==null) {
    		tax.setTieredList(taxService.getTaxTypeTierList());
    	}    	
    	tax.getTieredList().add(taxService.getTaxTypeTier());
    	model.addAttribute("entity", tax);
        model.addAttribute("pageuri", MODEL_URI_NAME);

    	return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping(path = {"/tax/removetier"},params = "removeindex")
    public 	String deleteTier(Tax tax, @RequestParam("removeindex") int index,Model model) {
    	model.addAttribute("entity", taxService.deleteTier(tax,index));
        model.addAttribute("pageuri", MODEL_URI_NAME);

    	return RETURN_URI_ADD_EDIT;
    } 
}
