package com.adopt.apigw.controller.postpaid;

import java.util.List;
import java.util.TreeMap;

import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL)
public class PostpaidPlanController extends BaseController<PostpaidPlan>{

	private static final String MODEL_DISP_NAME="Plan";
	private static final String MODEL_URI_NAME="plan";    
    private static final String RETURN_URI_INDEX="redirect:/plan/1";
    private static final String RETURN_URI_LIST="postpaid/plan/planlist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/plan/planform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private PostpaidPlanService entityService;
    
    @Autowired
    private TaxService taxService;
    
    @Autowired
    private PlanServiceService planSrvService;

    @Autowired
    private ChargeService chargeService;
    
    @ModelAttribute("statusMap")
    TreeMap<String, String> getStatusMap(){
    	return UtilsCommon.getPlanStatusMap();
    }
    
    @ModelAttribute("planGroupMap")
    TreeMap<String, String> gegetPlanGroupOptionMap(){
    	return UtilsCommon.getPlanGroupOptionMap();
    }
    
    @ModelAttribute("serviceMap")
    List<PlanService> getServiceList(){
    	return planSrvService.getAllServices();
    }
    
    @ModelAttribute("planCategoryMap")
    TreeMap<String, String> getPlanCategoryMap(){
    	return UtilsCommon.getPlanCategoryMap();
    }
    
    @ModelAttribute("quotaUnitMap")
    TreeMap<String, String> getQuotaUnitMap(){
    	return UtilsCommon.getQuotaUnitMap();
    }
    
    @ModelAttribute("chargeTypeMap")
    TreeMap<String, String> getChargeTypeMap(){
    	return UtilsCommon.getChargeTypeMap();
    }
    
    @ModelAttribute("bcMap")
    TreeMap<Integer, String> getBillingCycleMap(){
    	return UtilsCommon.getBillingCycleMap();
    }
    
    @ModelAttribute("taxMap")
    List<Tax> getTaxMap(){
    	return taxService.getAllActiveEntities();
    }

    @ModelAttribute("planTypeMap")
    TreeMap<String, String> getPlanTypeMap(){
    	return UtilsCommon.getPostpaidPlanTypeMap();
    }

    @ModelAttribute("prepaidChargeMap")
    List<Charge> getPrepaidChargeMap(){
    	return chargeService.getAllByChargeType(CommonConstants.CHARGE_TYPE_ADVANCE);
    }

    @ModelAttribute("postpaidChargeMap")
    List<Charge> getPostpaidChargeMap(){
    	List<Charge> localList=chargeService.getAllByChargeType(CommonConstants.CHARGE_TYPE_RECURRING);
    	localList.addAll(chargeService.getAllByChargeType(CommonConstants.CHARGE_TYPE_NONRECURRING));
    	return localList;
    }

    List<Charge> getAavancedChargeMap(){
    	List<Charge> localList=chargeService.getAllByChargeType(CommonConstants.CHARGE_TYPE_ADVANCE);
    	return localList;
    }
    
//    @ModelAttribute("taxGroupMap")
//    TreeMap<String, String> getPostpaidPlanGroupMap(){
//    	return CommonUtils.getPostpaidPlanGroupMap();
//    }
//    
    @RequestMapping(value = {"/plan/{pageNumber}","/plan"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {

    	if(pageNumber==null) {
    		pageNumber=1;
    	}
  
    	Page<PostpaidPlan> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	}
        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        
        return RETURN_URI_LIST;
    }

    @RequestMapping("/plan/add")
    public String add(Model model) {
        model.addAttribute("entity", entityService.getPostpaidPlanForAdd());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }
    
    
    @RequestMapping("/plan/edit/{id}")
    public String edit(@PathVariable Integer id, Model model,@RequestParam("mvnoId") Integer mvnoId) throws Exception{
    	PostpaidPlan entity = entityService.getPostpaidPlanForEdit(id,mvnoId);
    	if(entity.getPlanGroup() != null && !"".equals(entity.getPlanGroup())) { 
    		if(entity.getPlanGroup().equalsIgnoreCase("ADDON")) {
            	model.addAttribute("postpaidChargeMap",getAavancedChargeMap());
        	}
    	    model.addAttribute("isPlanGroup",true);       	
    	}
        model.addAttribute("entity", entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(value = "/plan/save", method = RequestMethod.POST)
    public String save(PostpaidPlan bean,final RedirectAttributes ra) {
    	
    	String operation="edit";
    	String flashMsg="";
    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
    	
    	try{
	    	if(bean !=null && bean.getId()==null){
	    		operation="add";
	    	}
    	
    		PostpaidPlan save = entityService.savePostpaidPlan(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="Plan Added Successfully";
	        	}else{
	        		flashMsg="Plan Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	
    	if(operation.equals("add")) {
    		//Automatically open edit when adding new entity
    		return "redirect:/plan/edit/"+bean.getId();
    	}else {
	        ra.addFlashAttribute("flashMsg", flashMsg);
	        ra.addFlashAttribute("flashMsgType", flashMsgType);
	        return RETURN_URI_INDEX;
	        
    	}
    }

    @RequestMapping("/plan/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra,@RequestParam ("mvnoId") Integer mvnoId) throws Exception{
    	entityService.deletePostpaidPlan(id,mvnoId);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
    
    @RequestMapping("/plangroup/change")
    public String chnagePlanGroupType(PostpaidPlan entity, Model model) {
    	if(entity.getPlanGroup() != null) {
    		if(entity.getPlanGroup().equalsIgnoreCase("ADDON")) {
            	model.addAttribute("postpaidChargeMap",getAavancedChargeMap());
        	}
    	}        model.addAttribute("entity", entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

    @RequestMapping(path = {"/plan/addcharge"})
    public 	String addSlab(PostpaidPlan entity, Model model) {
    	if(entity.getChargeList()==null) {
    		entity.setChargeList(entityService.getPostpaidPlanChargeList());
    	}    	
    	entity.getChargeList().add(entityService.getPostpaidPlanCharge());
    	if(entity.getPlanGroup() != null) {
    		if(entity.getPlanGroup().equalsIgnoreCase("ADDON")) {
            	model.addAttribute("postpaidChargeMap",getAavancedChargeMap());
        	}
    	}
    	System.out.println(entity.getId());
    	if(entity.getPlanGroup() != null && !"".equals(entity.getPlanGroup())) { 
    	    model.addAttribute("isPlanGroup",true);       	
    	}
    	model.addAttribute("entity", entity);
        model.addAttribute("pageuri", MODEL_URI_NAME);
    	return RETURN_URI_ADD_EDIT;
    }
    
    @RequestMapping(path = {"/plan/removecharge"},params = "removeindex")
    public 	String deleteSlab(PostpaidPlan entity, @RequestParam("removeindex") int index,Model model) {
    	if(entity.getPlanGroup() != null && !"".equals(entity.getPlanGroup())) { 
    		if(entity.getPlanGroup().equalsIgnoreCase("ADDON")) {
            	model.addAttribute("postpaidChargeMap",getAavancedChargeMap());
        	}
    	    model.addAttribute("isPlanGroup",true);       	
    	}
    	model.addAttribute("entity", entityService.deleteSlab(entity, index));
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

	@PostMapping("/plan/assignServiceArea")
	public GenericDataDTO assignToStaff(@RequestBody AssignServiceArea assignServiceArea, HttpServletRequest req){
		GenericDataDTO genericDataDTO = new GenericDataDTO();
		Integer RESP_CODE = APIConstants.FAIL;
		entityService.assignPlanToServiceArea(assignServiceArea.getServiceAreaId(), assignServiceArea.getPlanIds());
		try {
			genericDataDTO.setResponseCode(HttpStatus.OK.value());
			genericDataDTO.setResponseMessage("Success");
			//genericDataDTO.setDataList(serviceAreaService.getAllServicebyServiceAreaId(serviceAreaId));
			ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Assign Service Area to Plan"+ LogConstants.REQUEST_BY + planSrvService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS +LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
		} catch (Exception ex) {
			genericDataDTO = new GenericDataDTO();
			genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
			genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
			genericDataDTO.setTotalRecords(0);
			RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
			ApplicationLogger.logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Assign Service Area to Plan" +LogConstants.LOG_BY_NAME+ assignServiceArea.getPlanIds() + LogConstants.REQUEST_BY +planSrvService.getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+RESP_CODE);
		}finally {
			MDC.remove("type");
			MDC.remove("userName");
			MDC.remove("traceId");
			MDC.remove("spanId");
		}
		return genericDataDTO;
	}

}
