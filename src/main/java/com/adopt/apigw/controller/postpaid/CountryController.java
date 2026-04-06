package com.adopt.apigw.controller.postpaid;

import org.springframework.stereotype.Controller;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.Country;

@Controller
public class CountryController extends BaseController<Country>{

//
//
//	private static final String MODEL_DISP_NAME="Country";
//	private static final String MODEL_URI_NAME="country";
//    private static final String RETURN_URI_INDEX="redirect:/country/1";
//    private static final String RETURN_URI_LIST="postpaid/address/countrylist";
//    private static final String RETURN_URI_ADD_EDIT="postpaid/address/countryform";
//    private static final String SORT_BY_COLUMN="id";
//
//    @Autowired
//    private CountryService entityService;
//
//    @ModelAttribute("statusMap")
//    TreeMap<String, String> getStatusMap(){
//    	return CommonUtils.getYesNoStatusMap();
//    }
//
//    @RequestMapping(value = {"/country/{pageNumber}","/country"}, method = RequestMethod.GET)
//    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	if(pageNumber==null) {
//    		pageNumber=1;
//    	}
//
//    	Page<Country> page =null;
//    	if(search!=null && !"".equalsIgnoreCase(search)){
//    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
//    	}else{
//    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC,null);
//    	}
//        //setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
//    	setPageParameters(true, true,true,flashMsgType, flashMsg, MODEL_DISP_NAME, MODEL_URI_NAME, search, model, page);
//
//        return RETURN_URI_LIST;
//    }
//
//    @RequestMapping("/country/add")
//    public String add(Model model) {
//        model.addAttribute("entity", entityService.getCountryForAdd());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//
//    @RequestMapping("/country/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) throws Exception{
//        model.addAttribute("entity", entityService.getCountryForEdit(id));
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping(value = "/country/save", method = RequestMethod.POST)
//    public String save(Country bean,final RedirectAttributes ra) {
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
//    		Country save = entityService.saveCountry(bean);
//	    	if(save !=null){
//	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="Country Added Successfully";
//	        	}else{
//	        		flashMsg="Country Updated Successfully";
//	        	}
//	        }else{
//	    		flashMsg="Error Performing operation, Please try after sometime !!!";
//	        }
//    	}catch(Exception e){
//    		flashMsg="error";
//    	}
//
//        ra.addFlashAttribute("flashMsg", flashMsg);
//        ra.addFlashAttribute("flashMsgType", flashMsgType);
//        return RETURN_URI_INDEX;
//
//
//    }
//
//    @RequestMapping("/country/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
//    	entityService.deleteCountry(id);
//        ra.addFlashAttribute("flashMsg", "DelSuccess");
//        return RETURN_URI_INDEX;
//    }
}
