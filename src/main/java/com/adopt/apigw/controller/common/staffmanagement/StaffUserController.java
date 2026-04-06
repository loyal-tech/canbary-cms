package com.adopt.apigw.controller.common.staffmanagement;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class StaffUserController extends BaseController<StaffUser>{
	
    private static final Logger logger = LoggerFactory.getLogger(StaffUserController.class);

	private StaffUserService staffUserService;
	
	private RoleService roleService;

	public static final String DOMAINNAME=StaffUser.class.getName() + Long.MAX_VALUE;
	
    @Autowired
    public void setStaffUser(StaffUserService staffUserService,RoleService roleService) {
        this.staffUserService = staffUserService;
        this.roleService=roleService;
    }
 
    @RequestMapping(value = "/staffuser")
    public String index() {
        return "redirect:/staffuser/1";
    }
    
    @RequestMapping(value = {"/staffuser/{pageNumber}"}, method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber,@RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsg") String flashMsg,   Model model) {

    	Page<StaffUser> page =null;
    	if(search!=null && !"".equalsIgnoreCase(search)){
    		page = staffUserService.searchEntity(search.toLowerCase().trim(), pageNumber,CommonConstants.DB_PAGE_SIZE);
    	}else{
    		page = staffUserService.getList(pageNumber,CommonConstants.DB_PAGE_SIZE,"id",CommonConstants.SORT_ORDER_ASC,null);
    	}
    	model.addAttribute("custStatusMap", UtilsCommon.getCustStatusMap());
    	setPaginationParameters("User", flashMsg, search, model, page);
    	return "common/staffuser/stafflist";
    }

    @RequestMapping("/staffuser/add")
    public String add(Model model,@RequestParam("mvnoId") Integer mvnoId
	) throws Exception {
        model.addAttribute("staffuser", staffUserService.getStaffUserForAdd());
        model.addAttribute("custStatusMap", UtilsCommon.getCustStatusMap());
    	model.addAttribute("roleMap", roleService.getAllEntities(mvnoId));
        return "common/staffuser/staffform";
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.StaffUser', '2')")
    @RequestMapping("/staffuser/changepassword")
    public String changepassword(Model model, final RedirectAttributes ra, @ModelAttribute("flashMsg") String flashMsg) {
    	logger.info("In Change Passowrd");
        model.addAttribute("staffuser", new StaffUser());
        if (flashMsg.equalsIgnoreCase("ERROR")){
            model.addAttribute("infoFlash", "Password Change Failed");
    	}
        if (flashMsg.equalsIgnoreCase("SUCCESS")){
            model.addAttribute("infoFlash", "Password Change Success");
    	}
          return "common/staffuser/changepassword";

    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.StaffUser', '2')")
    @RequestMapping(value = "/staffuser/updatepassword",method = RequestMethod.POST)
    public String updatepassword(StaffUser staffuser, final RedirectAttributes ra) {
    	logger.info("In updatepassword");
    	List dbStaffUserList=staffUserService.getStaffUserFromUsername(staffuser.getUsername());
    	if(dbStaffUserList==null || dbStaffUserList.size()<=0) {
    		logger.info("User Not Found");
    		String flashMsg="ERROR";
            ra.addFlashAttribute("flashMsg", flashMsg);
    	}
    	else {
    		StaffUser dbstaffuser=(StaffUser) dbStaffUserList.get(0);
    		logger.info("Entered Old Password:"+dbstaffuser.getPassword()+":In DB Password:"+dbstaffuser.getPassword());
    		if(dbstaffuser.getPassword().equals(dbstaffuser.getPassword())) {
    			logger.info("Password Matched. Changing Password");
    			
	        	PasswordEncoder encoder = new BCryptPasswordEncoder();
	        	staffuser.setNewpassword(encoder.encode(staffuser.getNewpassword()));

    			dbstaffuser.setPassword(staffuser.getNewpassword());
    			staffUserService.save(dbstaffuser);
    			String flashMsg="SUCCESS";
                ra.addFlashAttribute("flashMsg", flashMsg);
    		}
    		else {
      			String flashMsg="ERROR";
                ra.addFlashAttribute("flashMsg", flashMsg);
    			logger.info("Old and New Password dont match");
    		}
    	}
    	return "redirect:/staffuser/changepassword";
    }
    
   
    @RequestMapping("/staffuser/edit/{id}")
    public String edit(@PathVariable Integer id, Model model,@RequestParam("mvnoId") Integer mvnoId) throws Exception{
        model.addAttribute("staffuser", staffUserService.getStaffUserForEdit(id));
        model.addAttribute("custStatusMap", UtilsCommon.getCustStatusMap());
    	model.addAttribute("roleMap", roleService.getAllEntities(mvnoId));
        return "common/staffuser/staffform";
    }

    @PostMapping("/authenticateStaff")
    public String authenticateStaff(@ModelAttribute StaffUser staffUser, Model model,HttpServletRequest request)
    {
		logger.info("I am in authenticateStaff:"+staffUser.getUsername());
		
    	List dbStaffUserList=staffUserService.getStaffUserFromUsername(staffUser.getUsername());
    	if(dbStaffUserList==null || dbStaffUserList.size()<=0) {
    		logger.info("User Not Found");
    		model.addAttribute("staffuser",new StaffUser());
            model.addAttribute("errorFlash", "Username or Password not matched");
    		return "login";
    	}
    	else {
    		StaffUser dbstaffuser=(StaffUser) dbStaffUserList.get(0);
    		logger.info("Entered Password:"+staffUser.getPassword()+":In DB Password:"+dbstaffuser.getPassword());
    		if(staffUser.getPassword().equals(dbstaffuser.getPassword())) {
        		logger.info("Login Success");
        		LocalDateTime ldt = LocalDateTime.now();
        		dbstaffuser.setLast_login_time(ldt);
        		dbstaffuser.setFailcount(0);
        		staffUserService.save(dbstaffuser);
    			request.getSession().setAttribute("SESSIONDETAIL","SUCCESS");
    			request.getSession().setAttribute("USERNAME",staffUser.getUsername());
        		return "dashboard";
    		}
    		else
    		{
        		logger.info("Password Not Match");
        		model.addAttribute("staffuser",new StaffUser());
                model.addAttribute("errorFlash", "Username or Password not matched");

        		int intFailCount=dbstaffuser.getFailcount();
        		intFailCount++;
        		dbstaffuser.setFailcount(intFailCount);
    			staffUserService.save(dbstaffuser);

                return "login";
    		}
    	}
    }

    
	@GetMapping("/login")
    public String authenticateUserForm(Model model)
    {
		model.addAttribute("staffuser",new StaffUser());
		return "login";
    }
	
	@GetMapping("/logoutUser")
    public String logout(Model model,HttpServletRequest request)
    {
		request.getSession().invalidate();
		model.addAttribute("successFlash","Logout Successfully");
		return "index";
    }
    
    @RequestMapping(value = "/staffuser/save", method = RequestMethod.POST)
    public String save(StaffUser staffUser, final RedirectAttributes ra) {
    	String operation="edit";
    	String flashMsg="";
    	
    	try{
    		if(staffUser !=null && staffUser.getId()==null){
	    		operation="add";       		
    		}
	    	StaffUser save = staffUserService.saveStaffUser(staffUser);
	        if(save !=null){
	        	if(operation.equalsIgnoreCase("add")){
	        		flashMsg="AddSuccess";
	        	}else{
	        		flashMsg="EditSuccess";
	        	}
	        }else{
	    		flashMsg="error";
	        }
    	}catch(Exception e){
    		flashMsg="error";
    	}
        ra.addFlashAttribute("flashMsg", flashMsg);
        return "redirect:/staffuser/1";

    }

    @RequestMapping("/staffuser/delete/{id}")
    public String delete(@PathVariable Integer id,final RedirectAttributes ra) {
    	try{
    		staffUserService.deleteStaffUser(id);
    		ra.addFlashAttribute("flashMsg", "DelSuccess");
    	}catch(Exception e){
    		ra.addFlashAttribute("flashMsg", "error");
    	}
        return "redirect:/staffuser/1";

    }
    
}
