package com.adopt.apigw.controller.common.customer;

import java.util.List;
import java.util.TreeMap;

import com.adopt.apigw.repository.radius.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.CustomerAddressService;
import com.adopt.apigw.service.postpaid.StateService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class CustAddressController extends BaseController<CustomerAddress>{


	
	private static final String MODEL_DISP_NAME="Address";
	private static final String MODEL_URI_NAME="address";    
    private static final String RETURN_URI_INDEX="redirect:/address/1";
    private static final String RETURN_URI_LIST="radius/customers/addlist";
    private static final String RETURN_URI_ADD_EDIT="radius/customers/addrform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private CustomerAddressService entityService;

    @Autowired
    private CustomersService custService;

    @Autowired 
    private CountryService countryService;
    
    @Autowired 
    private StateService stateService;
    
    @Autowired 
    private CityService cityService;
    @Autowired
    private CustomersRepository customersRepository;
        
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
        
    @RequestMapping(value = {"/address/{custid}/{pageNumber}","/address/{custid}"}, method = RequestMethod.GET)
    public String list(@PathVariable Integer custid,@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	Page<CustomerAddress> page =null;

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	Customers cust = customersRepository.findById(custid).get();
   		page = entityService.searchByCustomer(cust, pageNumber,CommonConstants.DB_PAGE_SIZE);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        model.addAttribute("custid" + custid);
        return RETURN_URI_LIST;
    }

    @RequestMapping("/address/add/{custid}")
    public String add(@PathVariable Integer custid,Model model) throws Exception{
        model.addAttribute("entity", entityService.getCustomerAddressForAdd(custid));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/address/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) throws Exception{
        model.addAttribute("entity", entityService.getCustomerAddressForEdit(id));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/address/save", method = RequestMethod.POST)
    public String save(CustomerAddress bean,final RedirectAttributes ra) {
    	
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
    		CustomerAddress save = entityService.saveCustomerAddress(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Address Added Successfully";
	        	}else{
	        		flashMsg="Address Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	    	
        ra.addFlashAttribute("flashMsg", flashMsg);
        ra.addFlashAttribute("flashMsgType", flashMsgType);
        return "redirect:/address/" + bean.getCustomer().getId();
	        
    	
    }

    @RequestMapping("/address/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
    	entityService.deleteCustomerAddress(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
}
