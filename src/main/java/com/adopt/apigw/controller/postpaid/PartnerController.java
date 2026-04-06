package com.adopt.apigw.controller.postpaid;

import java.util.List;
import java.util.TreeMap;

import javax.transaction.Transactional;

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
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.Location;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.LocationService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.StateService;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class PartnerController extends BaseController<Partner>{


	
	private static final String MODEL_DISP_NAME="Partner";
	private static final String MODEL_URI_NAME="partner";    
    private static final String RETURN_URI_INDEX="redirect:/partner/1";
    private static final String RETURN_URI_LIST="postpaid/partner/partnerlist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/partner/partnerform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private PartnerService entityService;
    
    @Autowired
    private TaxService taxService;
    
    @Autowired 
    private CountryService countryService;
    
    @Autowired 
    private StateService stateService;
    
    @Autowired 
    private CityService cityService;

    @Autowired 
    private StaffUserService staffUserService;
    
    @Autowired
    private LocationService locationService;
    
    @ModelAttribute("locationMap")
    public List<Location> getLocationList(){
    	return locationService.getAllActiveEntities();       	        
    }

    
    @ModelAttribute("countyList")
    public List<Country> getCountryList(){
    	return countryService.getAllActiveEntities();
    }
    @ModelAttribute("stateList")
    public List<State> getStateList(){
    	return stateService.getAllActiveEntities();
    }
    @ModelAttribute("cityList")
    public List<City> getCityList(){
    	return cityService.getAllActiveEntities();
    }
    
    @ModelAttribute("addrTypeMap")
    public TreeMap<String,String> getAddressTypeMap(){
    	return UtilsCommon.getAddressTypeMap();
    }
    
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getYesNoStatusMap();
    }
    
    @ModelAttribute("taxMap")
    List<Tax> getTaxMap(){
    	return taxService.getAllActiveEntities();
    }

    @ModelAttribute("commTypeMap")
    TreeMap<String, String> getCommTypeMap(){
    	return UtilsCommon.getPartnerCommTypes();
    }
    
    @ModelAttribute("billDateMap")
    TreeMap<Integer, String> getBillDateMap(){
    	return UtilsCommon.getBillDateMap();
    }  

//    @ModelAttribute("taxGroupMap")
//    TreeMap<String, String> getPostpaidPlanGroupMap(){
//    	return CommonUtils.getPostpaidPlanGroupMap();
//    }
//    
    @RequestMapping(value = {"/partner/{pageNumber}","/partner"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}

    	Page<Partner> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/partner/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getPartnerForAdd());
    	model.addAttribute("partnerMap",entityService.getAllActiveEntities());        
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping("/partner/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) throws Exception{
        model.addAttribute("entity", entityService.getPartnerForEdit(id));
    	model.addAttribute("partnerMap",entityService.getAllParentPartners(id));        
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/partner/save", method = RequestMethod.POST)
    @Transactional
    public String save(Partner bean,final RedirectAttributes ra) {
    	
    	String operation="edit";
    	String flashMsg="";
    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
    	
    	try{
    		Partner save = entityService.savePartner(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Partner Added Successfully";
	        	}else{
	        		flashMsg="Partner Updated Successfully";
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

    @RequestMapping("/partner/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
    	entityService.deletePartner(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }

}
