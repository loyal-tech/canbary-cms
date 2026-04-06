package com.adopt.apigw.controller.common.role;
//package com.adopt.apigw.controller.common.role;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.ModelAttribute;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.adopt.apigw.controller.base.BaseController;
//import com.adopt.apigw.modules.role.domain.Role;
//import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
//import com.adopt.apigw.modules.role.service.RoleService;
//import com.adopt.apigw.service.radius.CustomACLService;
//import com.adopt.apigw.utils.CommonConstants;
//import com.adopt.apigw.utils.CommonUtils;
//
//import net.sf.ehcache.Cache;
//import net.sf.ehcache.CacheManager;
//
//@Controller
//public class RoleController extends BaseController<Role>{
//
//
//    private RoleService entityService;
//
//    private static final String MODEL_DISP_NAME="Role";
//    private static final String RETURN_URI_INDEX="redirect:/roles/1";
//    private static final String RETURN_URI_LIST="common/roles/rolelist";
//    private static final String RETURN_URI_ADD_EDIT="common/roles/roleform";
//    private static final String SORT_BY_COLUMN="id";
//
//    @Autowired
//    public void setEntityService(RoleService entityService) {
//        this.entityService = entityService;
//    }
//
//    @Autowired
//	CustomACLService aclService;
//
//
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.modules.role.domain.Role', '1')")
//    @RequestMapping(value = "/roles")
//    public String index() {
//        return RETURN_URI_INDEX;
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.modules.role.domain.Role', '1')")
//    @RequestMapping(value = "/roles/{pageNumber}", method = RequestMethod.GET)
//    public String list(@PathVariable Integer pageNumber,@RequestParam(name="s",defaultValue="")  String search,@ModelAttribute("flashMsg") String flashMsg, Model model) {
//    	Page<Role> page =null;
//    	if(search!=null && !"".equalsIgnoreCase(search)){
//    		page = entityService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
//
//    	}else{
//    		page = entityService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,SORT_BY_COLUMN,CommonConstants.SORT_ORDER_ASC);
//    	}
//    	setPaginationParameters(MODEL_DISP_NAME, flashMsg, search, model, page);
//    	model.addAttribute("statusMap", CommonUtils.getEntityStatusMap());
//        return RETURN_URI_LIST;
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.modules.role.domain.Role', '2')")
//    @RequestMapping("/roles/add")
//    public String add(Model model) {
//
//    	Role entity = new Role();
//    	fulfilACLList(entity);
//    	CacheManager cacheManager = CacheManager.getInstance();
//		Cache cache = cacheManager.getCache("domainsCache");
//
//
//    	model.addAttribute("entity", entity);
//    	model.addAttribute("statusMap", CommonUtils.getEntityStatusMap());
//    	model.addAttribute("permissionMap", CommonUtils.getPermissionMap());
//    	model.addAttribute("domainsMap", cache);
//        return RETURN_URI_ADD_EDIT;
//
//    }
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.modules.role.domain.Role', '2')")
//    @RequestMapping("/roles/edit/{id}")
//    public String edit(@PathVariable Integer id, @ModelAttribute("flashMsg") String flashMsg,Model model) {
//
//    	CacheManager cacheManager = CacheManager.getInstance();
//		Cache cache = cacheManager.getCache("domainsCache");
//
//		Role entity = entityService.get(id);
//    	fulfilACLList(entity);
//
//    	model.addAttribute("entity", entity);
//    	model.addAttribute("statusMap", CommonUtils.getEntityStatusMap());
//    	model.addAttribute("permissionMap", CommonUtils.getPermissionMap());
//    	model.addAttribute("domainsMap", cache);
//
//        return RETURN_URI_ADD_EDIT;
//
//    }
//
//    @RequestMapping(value = "/roles/save", method = RequestMethod.POST)
//    public String save(Role entity,RedirectAttributes ra) {
//    	String operation="edit";
//    	String flashMsg="";
//
//    	try{
//	    	if(entity !=null && entity.getId()==null){
//	    		operation="add";
//	    	}
//
//	    	Role save = entityService.saveRole(entity);
//	        if(save !=null){
//	        	if(operation.equalsIgnoreCase("add")){
//	        		flashMsg="AddSuccess";
//	        	}else{
//	        		flashMsg="EditSuccess";
//	        	}
//	        }else{
//	    		flashMsg="error";
//	        }
//    	}catch(Exception e){
//    		flashMsg="error";
//    	}
//        ra.addFlashAttribute("flashMsg", flashMsg);
//        return RETURN_URI_INDEX;
//   	}
//
//    @PreAuthorize("hasPermission('com.adopt.apigw.modules.role.domain.Role', '4')")
//    @RequestMapping("/roles/delete/{id}")
//    public String delete(@PathVariable Integer id) {
//    	entityService.delete(id);
//        return RETURN_URI_INDEX;
//    }
//
//    @SuppressWarnings("unchecked")
//	public void fulfilACLList(Role role){
//
//    	List<CustomACLEntry> mylist = null;
//    	CacheManager cacheManager;
//    	CustomACLEntry aclentry=null;
//
//    	List<String> aclKeyList = new ArrayList<String>();
//    	boolean newList=false;
//    	try{
//    		if(role !=null){
//    			cacheManager = CacheManager.getInstance();
//    			if(role.getAclEntry()==null){
//    				mylist=new ArrayList<CustomACLEntry>();
//    				newList=true;
//    			}else{
//    				mylist=role.getAclEntry();
//    			}
//
//    			for(CustomACLEntry myEntry: mylist){
//    				aclKeyList.add(String.valueOf(myEntry.getClassid()));
//    			}
//
//    			Cache  domainsCache = cacheManager.getCache("domainsCache");
//    			for (Object objKey : domainsCache.getKeys()) {
//    				aclentry=null;
//    				if(!aclKeyList.contains(objKey.toString())){
//    					aclentry=new CustomACLEntry(Integer.parseInt(objKey.toString()), role, Integer.parseInt(CommonConstants.PERMISSION_NONE)); //0 - NON PERMISSION
//    					mylist.add(aclentry);
//    				}
//				}
//    			Collections.sort(mylist);
//    			if(newList){
//    				role.setAclEntry(mylist);
//    			}
//    		}
//
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//    }
//
//    @RequestMapping(value="/reloadcache")
//    public String reloadcache(){
//    	aclService.reloadCache();
//        return RETURN_URI_INDEX;
//
//    }
//}
