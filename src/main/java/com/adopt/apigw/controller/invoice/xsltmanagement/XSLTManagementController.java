package com.adopt.apigw.controller.invoice.xsltmanagement;

import java.util.ArrayList;
import java.util.TreeMap;

import com.adopt.apigw.repository.postpaid.XsltManagementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.adopt.apigw.audit.AuditService;
import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.XsltManagement;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.service.postpaid.XsltManagementService;
import com.adopt.apigw.utils.CommonConstants;

@Controller
public class XSLTManagementController extends BaseController<XsltManagement>{

    private static final Logger logger = LoggerFactory.getLogger(XSLTManagementController.class);

	
	private static final String MODEL_DISP_NAME="Xslt";
	private static final String MODEL_URI_NAME="xsltmanagement";    
    private static final String RETURN_URI_INDEX="redirect:/xsltmanagement/1";
    private static final String RETURN_URI_LIST="postpaid/invoice/templatelist";
    private static final String RETURN_URI_ADD_EDIT="postpaid/invoice/templateform"; 
    private static final String SORT_BY_COLUMN="id"; 

    @Autowired
    private XsltManagementService xsltManagementService;
	@Autowired
	private XsltManagementRepository xsltManagementRepository;



	//@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_VIEW + "\")")
    @RequestMapping(value = {"/xsltmanagement/{pageNumber}","/xsltmanagement"}, method = RequestMethod.GET)
    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
    	if(pageNumber==null) {
    		pageNumber=1;
    	}
    	Page<XsltManagement> page = xsltManagementService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
        return RETURN_URI_LIST;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ADD + "\")")
    @RequestMapping("/xsltmanagement/add")
    public String add(Model model) {
        model.addAttribute("entity", new XsltManagement());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }


    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_EDIT + "\")")
    @RequestMapping("/xsltmanagement/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("entity", xsltManagementRepository.findById(id).get());
        model.addAttribute("pageuri", MODEL_URI_NAME);
        return RETURN_URI_ADD_EDIT;
    }

	//@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ADD + "\")")
    @RequestMapping(value = "/xsltmanagement/save", method = RequestMethod.POST)
    public String save(XsltManagement bean,final RedirectAttributes ra) {
    	logger.info("In save");
    	String operation="edit";
    	String flashMsg="";
    	String flashMsgType=CommonConstants.FLASH_MSG_TYPE_ERROR;
    	
    	try{
	    	if(bean !=null && bean.getId()==null){
	    		operation="add";
	    	}else {
	    	}
    	
  
    		XsltManagement save = xsltManagementService.save(bean);
	    	if(save !=null){
	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="XSLT Added Successfully";
	        	}else{
	        		flashMsg="XSLT Updated Successfully";
	        	}
	        }else{
	    		flashMsg="Error Performing operation, Please try after sometime !!!";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
    	
    	if(operation.equals("add")) {
    		return "redirect:/xsltmanagement/edit/"+bean.getId();
    	}else {
	        ra.addFlashAttribute("flashMsg", flashMsg);
	        ra.addFlashAttribute("flashMsgType", flashMsgType);
	        return RETURN_URI_INDEX;
	        
    	}
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_ALL + "\",\"" + AclConstants.OPERATION_XSLT_MANAGEMENT_DELETE + "\")")
    @RequestMapping("/xsltmanagement/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) {
    	xsltManagementService.delete(id);
        ra.addFlashAttribute("flashMsg", "DelSuccess");
        return RETURN_URI_INDEX;
    }
  
  }
