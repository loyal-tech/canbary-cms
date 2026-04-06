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
import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.service.postpaid.LocationService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class LocationController extends BaseController<Location>{
	
	private static final String MODEL_DISP_NAME="Location";
	private static final String MODEL_URI_NAME="location";    
    private static final String RETURN_URI_INDEX="redirect:/location/1";
    private static final String RETURN_URI_LIST="postpaid/location/locationlist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/location/locationform"; 
    private static final String SORT_BY_COLUMN="id";

    @Autowired
    private LocationService locationService;
    
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @RequestMapping(value = {"/location/{pageNumber}","/location"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	Page<Location> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = locationService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = locationService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }
    
    @RequestMapping("/location/add")
    public String add(Model model) {
        model.addAttribute("entity", locationService.getLocationForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping("/location/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) throws Exception{
        model.addAttribute("entity", locationService.getLocationForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/location/save", method = RequestMethod.POST)
    public String save(Location bean,final RedirectAttributes ra) {
    	String operation="edit";
    	String flashMsg="";
    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
    	try{
	    	if(bean !=null && bean.getId()==null){
	    		operation="add";
	    	}else {
	    	}
	    	Location save = locationService.save(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Location Added Successfully";
	        	}else{
	        		flashMsg="Location Updated Successfully";
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
    
    @RequestMapping("/location/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
    	locationService.delete(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
}
