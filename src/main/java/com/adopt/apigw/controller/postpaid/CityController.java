package com.adopt.apigw.controller.postpaid;

import org.springframework.stereotype.Controller;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.City;

@Controller
public class CityController extends BaseController<City>{

//
//
//	private static final String MODEL_DISP_NAME="City";
//	private static final String MODEL_URI_NAME="city";
//    private static final String RETURN_URI_INDEX="redirect:/city/1";
//    private static final String RETURN_URI_LIST="postpaid/address/citylist";
//    private static final String RETURN_URI_ADD_EDIT="postpaid/address/cityform";
//    private static final String SORT_BY_COLUMN="id";
//
//    @Autowired
//    private CityService entityService;
//
//    @Autowired
//    private CountryService countryService;
//
//    @Autowired
//    private StateService stateService;
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
//    @RequestMapping(value = {"/city/{pageNumber}","/city"}, method = RequestMethod.GET)
//    public String list(@PathVariable(required = false) Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsgType") String flashMsgType,@ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	if(pageNumber==null) {
//    		pageNumber=1;
//    	}
//
//    	Page<City> page =null;
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
//    @RequestMapping("/city/add")
//    public String add(Model model) {
//        model.addAttribute("entity", entityService.getCityForAdd());
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping("/city/edit/{id}")
//    public String edit(@PathVariable Integer id, Model model) throws Exception{
//        model.addAttribute("entity", entityService.getCityForEdit(id));
//        model.addAttribute("pageuri", MODEL_URI_NAME);
//        return RETURN_URI_ADD_EDIT;
//    }
//
//    @RequestMapping(value = "/city/save", method = RequestMethod.POST)
//    public String save(City bean,final RedirectAttributes ra) {
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
//    		City save = entityService.saveCity(bean);
//	    	if(save !=null){
//	    		flashMsgType=CommonConstants.FLASH_MSG_TYPE_SUCCESS;
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="City Added Successfully";
//	        	}else{
//	        		flashMsg="City Updated Successfully";
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
//    @RequestMapping("/city/delete/{id}")
//    public String delete(@PathVariable Integer id,final RedirectAttributes ra) throws Exception{
//    	entityService.deleteCity(id);
//        ra.addFlashAttribute("flashMsg", "DelSuccess");
//        return RETURN_URI_INDEX;
//    }
//
//    @RequestMapping(value = "/city/searchbystate/{sid}", method = RequestMethod.GET)
//    public @ResponseBody List<City> findCityByState(@PathVariable Integer sid) {
//    	if(sid !=null) {
//    		State s= stateService.get(sid);
//    		if(s!=null)
//    			return entityService.findByState(s);
//    		else
//    			return null;
//    	}else {
//    		return null;
//    	}
//    }
}
