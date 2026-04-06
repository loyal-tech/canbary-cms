package com.adopt.apigw.controller.postpaid;

import org.springframework.stereotype.Controller;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.State;

@Controller
public class StateController extends BaseController<State>{

//
//
//	private static final String MODEL_DISP_NAME="State";
//	private static final String MODEL_URI_NAME="state";
//    private static final String RETURN_URI_INDEX="redirect:/state/1";
//    private static final String RETURN_URI_LIST="postpaid/address/statelist";
//    private static final String RETURN_URI_ADD_EDIT="postpaid/address/stateform";
//    private static final String SORT_BY_COLUMN="id";
//
//    @Autowired
//    private StateService entityService;
//
//    @Autowired
//    private CountryService countryService;
//
//    @ModelAttribute("statusMap")
//    TreeMap<String, String> getStatusMap(){
//    	return CommonUtils.getYesNoStatusMap();
//    }
//
//    @ModelAttribute("countryList")
//    List<Country> getCountryList(){
//    	return countryService.getAllActiveEntities();
//    }
//
//    @RequestMapping(value = {"/state/{pageNumber}","/state"}, method = RequestMethod.GET)
//    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	if(pageNumber==null) {
//    		pageNumber=1;
//    	}
//
//    	Page<State> page =null;
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
//    @RequestMapping("/state/add")
//    public String add(Model model) {
//        model.addAttribute("entity", entityService.getStateForAdd());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//
//    @RequestMapping("/state/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) throws Exception{
//        model.addAttribute("entity", entityService.getStateForEdit(id));
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping(value = "/state/save", method = RequestMethod.POST)
//    public String save(State bean,final RedirectAttributes ra) {
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
//    		State save = entityService.saveState(bean);
//	    	if(save !=null){
//	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="State Added Successfully";
//	        	}else{
//	        		flashMsg="State Updated Successfully";
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
//    @RequestMapping("/state/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
//    	entityService.deleteState(id);
//        ra.addFlashAttribute("flashMsg", "DelSuccess");
//        return RETURN_URI_INDEX;
//    }
//
//    @RequestMapping(value = "/state/searchbycountry/{cid}", method = RequestMethod.GET)
//    public @ResponseBody List<State> findStatebyCountry(@PathVariable Integer cid) {
//    	if(cid !=null) {
//    		Country c= countryService.get(cid);
//    		if(c!=null)
//    			return entityService.findByCountry(c);
//    		else
//    			return null;
//    	}else {
//    		return null;
//    	}
//    }
}
