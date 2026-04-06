package com.adopt.apigw.controller.postpaid;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.postpaid.Discount;
import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.pojo.InternationalizationPojo;
import com.adopt.apigw.pojo.XMLRequest;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.DiscountService;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.utils.UtilsCommon;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Controller
public class TempController extends BaseController<Tax>{


	
	private static final String MODEL_DISP_NAME="Tax";
	private static final String MODEL_URI_NAME="tax";    
    private static final String RETURN_URI_INDEX="redirect:/tax/1";
    private static final String RETURN_URI_LIST="postpaid/tax/taxlist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/tax/taxform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private TaxService taxService;

    
    @Autowired
    private DiscountService discService;
    
  
    @Autowired
	private ClientServiceSrv clientServiceSrv;
    
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
    
    @ModelAttribute("taxMap")
    List<Tax> getTaxMap(){
    	return taxService.getAllActiveEntities();
    }

    @ModelAttribute("discMap")
    List<Discount> getDiscMap(){
    	return discService.getAllActiveEntities();
    }
    
 
  
    @ModelAttribute("chargeTypeMap")
    TreeMap<String, String> getChargeTypeMap(){
    	return UtilsCommon.getChargeTypeMap();
    }

    @ModelAttribute("internationalizationsMap")
    TreeMap<String, String> getInternationalizationsMap(){
    	return UtilsCommon.getInternationalizationsMap();
    }
    
    @RequestMapping(value = "/internationalization/save", method = RequestMethod.POST)
    public String save(InternationalizationPojo internationalizationPojo,final RedirectAttributes ra) {
    	String flashMsg="";
    	try{
	    	
    		ClientService save = clientServiceSrv.getByName("inmode");
    		save.setValue(internationalizationPojo.getInternationalizationValue());
    		save = clientServiceSrv.save(save);
	    	if(save !=null){
	        	flashMsg="Internationalization Saved Successfully";	       
	            ra.addFlashAttribute("successFlash", flashMsg);
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	            ra.addFlashAttribute("errorFlash", flashMsg);
	        }
    	}catch(Exception e){
    		flashMsg="error";
            ra.addFlashAttribute("errorFlash", flashMsg);
    	}
		return "redirect:/sysconfig";
    }
    
    @RequestMapping(value = {"/sysconfig"}, method = RequestMethod.GET)
    public String list(Model model) {
			ClientService clientService = clientServiceSrv.getByName("inmode");
			if(clientService != null) {
				InternationalizationPojo iPojo= new InternationalizationPojo();
				iPojo.setInternationalizationValue(clientService.getValue());
				model.addAttribute("internationalizationPojo", iPojo);
			}else {
				model.addAttribute("internationalizationPojo", new InternationalizationPojo());
			}
    		model.addAttribute("xmlrequest", new XMLRequest());
    		return "postpaid/system/systemparam";
    }
    
    
    @RequestMapping(value = {"/sysconfig/testxml"}, method = RequestMethod.POST)
    public String list(XMLRequest request, Model model) {

    		boolean isError=false;
    		try {
    			
    	    	OkHttpClient client = new OkHttpClient.Builder()
    	    		    .connectTimeout(100, TimeUnit.SECONDS)
    	    		    .writeTimeout(100, TimeUnit.SECONDS)
    	    		    .readTimeout(100, TimeUnit.SECONDS)
    	    		    .build();
    	    	
    	    	RequestBody body = RequestBody.create(MediaType.parse("application/xml; charset=utf-8"),request.getRequest());
    	    	
    	    	Request httpRequest = new Request.Builder()
    	    						  .url(request.getUrl())
    	    						  .post(body)
    	    						  .build();
    	    	try {
    	    		Response response = client.newCall(httpRequest).execute();
    	    		if(response!=null ) {
    	    			String responseBody=response.body().string();
    	            	System.out.println("[testxml] resposne:"+responseBody);
    	            	request.setResponse(responseBody);
    	    		}else {
    	    			System.out.println("[testxml] resposne is null");
    	    		}
    	    	}catch(Exception e) {
    	    		model.addAttribute("errorFlash",e.getMessage());
    	    		isError=true;
    	    		e.printStackTrace();
    	    		throw e;
    	    	}

    		}catch(Exception e) {
    			model.addAttribute("errorFlash",e.getMessage());
    			isError=true;
    		}
    		
    		if(!isError) {
    			model.addAttribute("successFlash","Success");
    		}
    		model.addAttribute("xmlrequest",request);
    		return "postpaid/system/systemparam";
    }

    @RequestMapping(value = {"/payparam"}, method = RequestMethod.GET)
    public String listpayment(Model model) {
    		return "postpaid/payment/paymentparam";
    }

    @RequestMapping(value = {"/gateway"}, method = RequestMethod.GET)
    public String listpg(Model model) {
    		return "postpaid/payment/gatewaylist";
    }

    @RequestMapping(value = {"/gateway/add"}, method = RequestMethod.GET)
    public String addpg(Model model) {
    		return "postpaid/payment/gatewayform";
    }

   
    
//    @RequestMapping(value = {"/payment"}, method = RequestMethod.GET)
//    public String paymenthistory(Model model) {
//		model.addAttribute("paystatus","-1");
//		model.addAttribute("paymode","-1");
//
//    	return "postpaid/payment/paymenthistory";
//    }
    
//    @RequestMapping(value = {"/payment/search"})
//    public String searchpayment( @RequestParam(name="paystatus", required = false)  String paystatus ,@RequestParam(name="paymode", required = false)  String paymentmode, Model model) {	
//    		logger.info("PayStatus:" + paystatus);
//    		logger.info("PayMode:" + paymentmode);
//    		
//    		model.addAttribute("paystatus",paystatus);
//    		model.addAttribute("paymode",paymentmode);
//    			
//    		return "postpaid/payment/paymenthistory";
//    }
    
    @RequestMapping(value = {"/invoice/disputecharge/{invoiceid}"}, method = RequestMethod.GET)
    public String disputeCharge(Model model) {
    		return "postpaid/invoice/chargedispute";
    }
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")
//    @RequestMapping("/tax/add")
//    public String add(Model model) {
//        model.addAttribute("entity", new Tax());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//    
//    
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")
//    @RequestMapping("/tax/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) {
//        model.addAttribute("entity", taxService.get(id));
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")
//    @RequestMapping(value = "/tax/save", method = RequestMethod.POST)
//    public String save(Tax bean,final RedirectAttributes ra) {
//    	
//    	String operation="edit";
//    	String flashMsg="";
//    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
//    	
//    	try{
//	    	if(bean !=null && bean.getId()==null){
//	    		operation="add";
////	    		bean.setCreatedById(getLoggedInUserId());
//	    	}else {
////	    		bean.setLastModifiedById(getLoggedInUserId());
//	    	}
//    	
//    		if(bean.getTaxType().equals(CommonConstants.TAX_TYPE_SLAB)) {
//	    		for (TaxTypeSlab  item: bean.getSlabList()) {
//	    			item.setTax(bean);
//	    		}
//    		}else if (bean.getTaxType().equals(CommonConstants.TAX_TYPE_TIER)) {
//	    		for (TaxTypeTier  item: bean.getTieredList()) {
//	    			item.setTax(bean);
//	    		}
//    		}
//
//    		Tax save = taxService.save(bean);
//	    	if(save !=null){
//	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="Tax Added Successfully";
//	        	}else{
//	        		flashMsg="Tax Updated Successfully";
//	        	}
//	        }else{
//	    		flashMsg="Error Performing operation, Please try after sometime !!!";
//	        }
//    	}catch(Exception e){
//    		flashMsg="error";
//    	}
//    	
//    	if(operation.equals("add")) {
//    		//Automatically open edit when adding new entity
//    		return "redirect:/tax/edit/"+bean.getId();
//    	}else {
//	        ra.addFlashAttribute("flashMsg", flashMsg);
//	        ra.addFlashAttribute("flashMsgType", flashMsgType);
//	        return RETURN_URI_INDEX;
//	        
//    	}
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '4')")
//    @RequestMapping("/tax/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) {
//    	taxService.delete(id);
//        ra.addFlashAttribute("flashMsg", "DelSuccess");
//        return RETURN_URI_INDEX;
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")        
//    @RequestMapping(path = {"/tax/addslab"})
//    public 	String addSlab(Tax tax, Model model) {
//    	if(tax.getSlabList()==null) {
//    		tax.setSlabList(new ArrayList<TaxTypeSlab>());
//    	}    	
//    	tax.getSlabList().add(new TaxTypeSlab());
//    	model.addAttribute("entity", tax);
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//    	return RETURN_URI_ADD_EDIT;
//    }
//    
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")        
//    @RequestMapping(path = {"/tax/removeslab"},params = "removeindex")
//    public 	String deleteSlab(Tax tax, @RequestParam("removeindex") int index,Model model) {
//    	tax.getSlabList().remove(index);
//    	model.addAttribute("entity", tax);
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    } 
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")        
//    @RequestMapping(path = {"/tax/addtier"})
//    public 	String addTier(Tax tax, Model model) {
//    	if(tax.getTieredList()==null) {
//    		tax.setTieredList(new ArrayList<TaxTypeTier>());
//    	}    	
//    	tax.getTieredList().add(new TaxTypeTier());
//    	model.addAttribute("entity", tax);
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//
//    	return RETURN_URI_ADD_EDIT;
//    }
//    
//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.postpaid.Tax', '2')")        
//    @RequestMapping(path = {"/tax/removetier"},params = "removeindex")
//    public 	String deleteTier(Tax tax, @RequestParam("removeindex") int index,Model model) {
//    	tax.getTieredList().remove(index);
//    	model.addAttribute("entity", tax);
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//
//    	return RETURN_URI_ADD_EDIT;
//    } 
}
