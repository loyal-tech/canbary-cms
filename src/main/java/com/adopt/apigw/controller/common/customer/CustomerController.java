package com.adopt.apigw.controller.common.customer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.adopt.apigw.repository.radius.CustomersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.Charge;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.CustChargeDetails;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.model.radius.CustReplyItem;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.pojo.CustChargeOverride;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.CustChargeService;
import com.adopt.apigw.service.postpaid.CustMacMapppingService;
import com.adopt.apigw.service.postpaid.CustomerAddressService;
import com.adopt.apigw.service.postpaid.CustomerLedgerService;
import com.adopt.apigw.service.postpaid.PartnerCommissionService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.postpaid.StateService;
import com.adopt.apigw.service.radius.CustReplyItemService;
import com.adopt.apigw.service.radius.RadiusProfileService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

@Controller
public class CustomerController extends BaseController<Customers> {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);


    private CustomersService customerService;
    @Autowired
    private CustomersRepository customersRepository;
    private CustReplyItemService replyItemService;
    private RadiusProfileService radProfileService;

    @Autowired
    public void setCustomerService(CustomersService customerService) {
        this.customerService = customerService;
    }

    @Autowired
    public void setCustReplyItemService(CustReplyItemService replyItemService) {
        this.replyItemService = replyItemService;
    }

    @Autowired
    public void setRadiusProfileService(RadiusProfileService service) {
        this.radProfileService = service;
    }

    @Autowired
    private PostpaidPlanService postpaidPlanService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;

    @Autowired
    private CityService cityService;

    @Autowired
    private PartnerCommissionService partnerCommService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CustomerAddressService custAddressService;

    @Autowired
    private CustomerLedgerService custLegerService;

    @Autowired
    private CustChargeService custChargeService;

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private CustMacMapppingService custMacMapppingService;

    @ModelAttribute("countyList")
    public List<Country> getCountryList() {
        return countryService.getAllActiveEntities();
    }

    @ModelAttribute("stateList")
    public List<State> getStateList() {
        return stateService.getAllActiveEntities();
    }

    @ModelAttribute("cityList")
    public List<City> getCityList() {
        return cityService.getAllActiveEntities();
    }

    @ModelAttribute("addrTypeMap")
    public TreeMap<String, String> getAddressTypeMap() {
        return UtilsCommon.getAddressTypeMap();
    }

    @ModelAttribute("custlist")
    public List<Customers> getCustomers() {
        return customerService.getAllCustomers();

    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '1')")
    @RequestMapping(value = "/customers")
    public String index() {
        return "redirect:/customers/1";
    }

    @ModelAttribute("custTypeMap")
    TreeMap<String, String> getCustTypeMap() {
        return UtilsCommon.getCustTypeMap();
    }

    @ModelAttribute("invoiceOptionMap")
    TreeMap<String, String> getInvoiceOptionMap() {
        return UtilsCommon.getInvoiceOptionMap();
    }

    @ModelAttribute("billDateMap")
    TreeMap<Integer, String> getBillDateMap() {
        return UtilsCommon.getBillDateMap();
    }

    @ModelAttribute("postpaidplanList")
    List<PostpaidPlan> getPostpaidPlanList() {
        return postpaidPlanService.getAllPostpaidPlans();
    }

    @ModelAttribute("prepaidplanList")
    List<PostpaidPlan> getPrePaidPlanList() {
        return postpaidPlanService.getAllPrepaidPlans();
    }


    @ModelAttribute("custStatusMap")
    public TreeMap<String, String> getCustomerStatusMap() {
        return UtilsCommon.getCustStatusMap();
    }

    @ModelAttribute("custStatusMapWhileNotRegister")
    public TreeMap<String, String> getCustStatusMapWhileNotRegister() {
        return UtilsCommon.getCustStatusMapWhileNotRegister();
    }

    @ModelAttribute("radProfileMap")
    public List<RadiusProfile> getRadProfileList() {
        return radProfileService.getAllActiveEntities();
    }

    @ModelAttribute("custmerMap")
    public List<Customers> getCustomersist() {
        return customerService.getAllActiveEntities();
    }

    @RequestMapping(value = {"/customers/{pageNumber}"}, method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber, @RequestParam(name = "s", defaultValue = "") String search, @ModelAttribute("flashMsg") String flashMsg, Model model) {
        Page<Customers> page = null;
        logger.info("Search is " + search);
        if (search != null && !"".equalsIgnoreCase(search)) {
            page = customerService.searchCustomers(search.toLowerCase().trim(), pageNumber, CommonConstants.DB_PAGE_SIZE);
        } else {
            page = customerService.getList(pageNumber, CommonConstants.DB_PAGE_SIZE, "id", CommonConstants.SORT_ORDER_ASC, null);
        }
        logger.info("Page is " + page.getTotalElements());
        setPaginationParameters("Customer", flashMsg, search, model, page);

        //  	model.addAttribute("custStatusMap", CommonUtils.getCustStatusMap());
        //  	model.addAttribute("radProfileMap", radProfileService.getAllActiveEntities());
        if (flashMsg != null && !"".equalsIgnoreCase(flashMsg)) {
            if (flashMsg.equalsIgnoreCase("FileUploadSucess")) {
                model.addAttribute("successFlash", "File uploaded successfully");
            }
        }
        return "radius/customers/custlist";
    }

    @RequestMapping("/customers/add")
    public String add(Model model) {

        model.addAttribute("customer", customerService.getCustomerForAdd());
        return "radius/customers/custform";

    }


    @RequestMapping("/customers/changepassword")
    public String changepassword(Model model, final RedirectAttributes ra, @ModelAttribute("flashMsg") String flashMsg) {
        logger.info("In Change Password");
        model.addAttribute("customer", new Customers());
        if (flashMsg.equalsIgnoreCase("ERROR")) {
            model.addAttribute("infoFlash", "Password Change Failed");
        }
        if (flashMsg.equalsIgnoreCase("SUCCESS")) {
            model.addAttribute("infoFlash", "Password Change Success");
        }
        return "radius/customers/changepassword";

    }

//    @RequestMapping(value = "/customers/updatepassword",method = RequestMethod.POST)
//    public String updatepassword(Customers customer, final RedirectAttributes ra) {
//    	logger.info("In updatepassword");
//    	List dbCustomerList=customerService.getCustomerFromUsername(customer.getUsername());
//    	if(dbCustomerList==null || dbCustomerList.size()<=0) {
//    		logger.info("User Not Found");
//    		String flashMsg="ERROR";
//            ra.addFlashAttribute("flashMsg", flashMsg);
//    	}
//    	else {
//    		Customers dbcustomer=(Customers) dbCustomerList.get(0);
//    		logger.info("Entered Old Password:"+customer.getPassword()+":In DB Password:"+dbcustomer.getPassword()+":New Password:"+customer.getNewpassword());
//    		if(dbcustomer.getPassword().equals(customer.getPassword())) {
//    			logger.info("Password Matched. Changing Password");
//    			dbcustomer.setPassword(customer.getNewpassword());
//    			customerService.save(dbcustomer);
//    			String flashMsg="SUCCESS";
//                ra.addFlashAttribute("flashMsg", flashMsg);
//    		}
//    		else {
//      			String flashMsg="ERROR";
//                ra.addFlashAttribute("flashMsg", flashMsg);
//                logger.info("Old and New Password dont match");
//    		}
//    	}
//    	return "redirect:/customers/changepassword";
//
//    }

    @RequestMapping(value = "/customers/updatepassword", method = RequestMethod.POST)
    public String updatepassword(Customers customer, final RedirectAttributes ra) {
        logger.info("In updatepassword");
        List dbCustomerList = customerService.getCustomerFromUsernameAPI(customer.getUsername());
        if (dbCustomerList == null || dbCustomerList.size() <= 0) {
            logger.info("User Not Found");
            String flashMsg = "ERROR";
            ra.addFlashAttribute("flashMsg", flashMsg);
        } else {
            Customers dbcustomer = (Customers) dbCustomerList.get(0);
            logger.info("Entered Old Password:" + customer.getPassword() + ":In DB Password:" + dbcustomer.getPassword() + ":New Password:" + customer.getNewpassword());
            if (dbcustomer.getPassword().equals(customer.getPassword())) {
                logger.info("Password Matched. Changing Password");
                System.out.println("PasswordConstraintValidator" + customerService.passwordValidation(customer.getNewpassword()));
                if (customerService.passwordValidation(customer.getNewpassword())) {
                    logger.info("Password Policy Match. Changing Password");
                    if (!customer.getNewpassword().equalsIgnoreCase(dbcustomer.getOldpassword1()) && !customer.getNewpassword().equalsIgnoreCase(dbcustomer.getOldpassword2()) && !customer.getNewpassword().equalsIgnoreCase(dbcustomer.getOldpassword3())) {
                        if (dbcustomer.getOldpassword1() == null) {
                            dbcustomer.setOldpassword1(customer.getNewpassword());
                        } else if (dbcustomer.getOldpassword2() == null) {
                            dbcustomer.setOldpassword2(customer.getNewpassword());
                        } else if (dbcustomer.getOldpassword3() == null) {
                            dbcustomer.setOldpassword3(customer.getNewpassword());
                        } else {
                            dbcustomer.setOldpassword1(dbcustomer.getOldpassword2());
                            dbcustomer.setOldpassword2(dbcustomer.getOldpassword3());
                            dbcustomer.setOldpassword3(customer.getNewpassword());
                        }
                        dbcustomer.setFailcount(0);
                        LocalDateTime localDateTime = LocalDateTime.now();
                        dbcustomer.setLast_password_change(localDateTime);
                        dbcustomer.setPassword(customer.getNewpassword());
                        customerService.save(dbcustomer);
                        String flashMsg = "SUCCESS";
                        ra.addFlashAttribute("flashMsg", flashMsg);
                    } else {
                        String flashMsg = "ERROR";
                        ra.addFlashAttribute("flashMsg", flashMsg);
                        logger.info("Password Should Different from Last 3 Password");
                    }
                } else {
                    String flashMsg = "ERROR";
                    ra.addFlashAttribute("flashMsg", flashMsg);
                    logger.info("Password is not matched as per Policy");
                }
            } else {
                String flashMsg = "ERROR";
                ra.addFlashAttribute("flashMsg", flashMsg);
                logger.info("Old and New Password dont match");
            }
        }
        return "redirect:/customers/changepassword";
    }


    public TreeMap<String, Object> getReplyItemsforCustomer(Integer custId, Integer pageNumber) {
        TreeMap<String, Object> replyMap = new TreeMap<String, Object>();
        List<CustReplyItem> page = replyItemService.getLisByCustIdt(custId);
        replyMap.put("list", page);
        return replyMap;
    }

    @RequestMapping("/customers/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        TreeMap<String, Object> replyItemMap = getReplyItemsforCustomer(id, 1);
        model.addAttribute("replyitems", replyItemMap.get("list"));
        model.addAttribute("customer", customerService.getCustomerForEdit(id));
//        model.addAttribute("custStatusMap", CommonUtils.getCustStatusMap());
//    	model.addAttribute("radProfileMap", radProfileService.getAllActiveEntities());   
        model.addAttribute("custmerMap", customerService.getAllParentCustomers(id));
        return "radius/customers/custform";

    }

    @RequestMapping(value = "/customers/save", method = RequestMethod.POST)
    public String save(Customers customer, final RedirectAttributes ra) {
        String operation = "edit";
        String flashMsg = "";

        try {
            if (customer != null && customer.getId() == null) {
                operation = "add";
            }
            Customers save = customerService.saveCustomer(customer);
            if (save != null) {
                if (operation.equalsIgnoreCase("add")) {
                    flashMsg = "AddSuccess";
                } else {
                    flashMsg = "EditSuccess";
                }
            } else {
                flashMsg = "error";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            flashMsg = "error";
        }
        ra.addFlashAttribute("flashMsg", flashMsg);
        return "redirect:/customers/1";

    }

    /*
    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping(value = "/customers/save", method = RequestMethod.POST)
    public String save(Customers customer, final RedirectAttributes ra) {
    	String operation="edit";
    	String flashMsg="";
    	
    	try{
	    	if(customer !=null && customer.getId()==null){
	    		operation="add";
	    		customer.setPartnerid(getLoggedInUserPartnerId());    		
	    	}else {	    			    		
	    		if(customer.getPassword()==null || "".equalsIgnoreCase(customer.getPassword()) || "null".equalsIgnoreCase(customer.getPassword())) {
		    		Customers tempCust=customerService.get(customer.getId());
		    		if(tempCust!=null) {
		    			customer.setPassword(tempCust.getPassword());
		    		}
	    		}
	    	}
	    	
	    	CustPlanMappping mapping=null;
	    	for (int i=0; i<customer.getPlanMappingList().size();i++) {
	    		mapping=customer.getPlanMappingList().get(i);
    			mapping.setCustomer(customer);
	    		if(mapping.getId()==null && mapping.getEndDate()!=null) {
	    			customer.getPlanMappingList().remove(i);
	    		}else if(mapping.getId()==null && mapping.getEndDate()==null) {
	    			mapping.setStartDate(LocalDate.now());
	    		}
			}

	    	if("add".equals(operation)) {
	    		if(customer.getCusttype().equals(CommonConstants.CUST_TYPE_POSTPAID)) {
	    	    	customer.setNextBillDate(customerService.getNextBillDate(customer));	    	   
	    		}else {
	    			customer.setNextBillDate(LocalDate.now());
	    		}
	    	}
	    	
	    	
	    	Customers save = null;
	    	if(customer.getNextBillDate()!=null) {	
		        save = customerService.save(customer);
	    	}else {
	    		logger.info("Issue is custoemr data, Next bill date is null");
	    	}
	    	
	        if(save !=null){
	        	if(operation.equalsIgnoreCase("add")){
	        		//Patner commission
	        		if(getLoggedInUserPartnerId()!=CommonConstants.DEFAULT_PARTNER_ID) {
	        				partnerCommService.setPartnerCommission(save, partnerService.get(save.getPartnerid()));
	        		}
	        		//Customer Primary address
	        		CustomerAddress primaryAddress = new CustomerAddress();
	        		primaryAddress.setAddress1(save.getAddress1());
	        		primaryAddress.setAddress2(save.getAddress2());
	        		primaryAddress.setCityId(save.getCity());
	        		primaryAddress.setCity(cityService.get(save.getCity()));
	        		primaryAddress.setStateId(save.getState());
	        		primaryAddress.setState(stateService.get(save.getState()));
	        		primaryAddress.setCountryId(save.getCountry());
	        		primaryAddress.setCountry(countryService.get(save.getCountry()));
	        		primaryAddress.setCustomer(save);
	        		primaryAddress.setPincode(save.getPincode());
	        		primaryAddress.setAddressType(save.getAddresstype());	        		
	        		custAddressService.save(primaryAddress);
	        		
	        		//Customer Ledger Entry
	        		CustomerLedger custLeger = new CustomerLedger();
	        		custLeger.setCustomer(save);
	        		custLeger=custLegerService.save(custLeger);
	        		
	        		flashMsg="AddSuccess";
	        		
	        		//Generate & Email Bill Prepaid Customers
		    		if(customer.getCusttype().equals(CommonConstants.CUST_TYPE_PREPAID)) {
		    			customerService.generateAndEmailInvoice(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), save);
		    		}
	        	}else{
	        		flashMsg="EditSuccess";
	        	}
	        }else{
	    		flashMsg="error";
	        }
    	}catch(Exception e){
    		e.printStackTrace();
    		flashMsg="error";
    	}
        ra.addFlashAttribute("flashMsg", flashMsg);
        return "redirect:/customers/1";

    }*/

    @RequestMapping("/customers/delete/{id}")
    public String delete(@PathVariable Integer id, final RedirectAttributes ra) {
        try {
            customerService.deleteCustomer(id);
            ra.addFlashAttribute("flashMsg", "DelSuccess");
        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("flashMsg", "error");
        }
        return "redirect:/customers/1";

    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @PostMapping("/upload") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        String TMP_DIR = System.getProperty("java.io.tmpdir");

        if (file.isEmpty()) {
            redirectAttributes.addAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        String flashMsg = "success";
        BufferedReader br = null;
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(TMP_DIR + file.getOriginalFilename());
            Files.write(path, bytes);
            logger.info("File is " + path.toString());
            String line = "";
            String cvsSplitBy = ",";

            br = new BufferedReader(new FileReader(path.toString()));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] customer = line.split(cvsSplitBy);

                logger.info("customer " + customer);
                Customers customers = new Customers();
                customers.setUsername(customer[0]);
                customers.setPassword(customer[1]);
                customers.setFirstname(customer[2]);
                customers.setLastname(customer[3]);
                customers.setEmail(customer[4]);
                customers.setCreatedate(LocalDateTime.now());
                Customers save = customerService.save(customers);
            }
            flashMsg = "FileUploadSucess";

        } catch (Exception e) {
            e.printStackTrace();
            flashMsg = "error";

        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {

            }
        }
        redirectAttributes.addFlashAttribute("flashmsg", flashMsg);
        return "redirect:/customers/1";
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '1')")
    @RequestMapping(value = "/customerdownload/", method = RequestMethod.GET)
    public void customerdownload(@RequestParam(name = "s", defaultValue = "") String username, @ModelAttribute("flashMsg") String flashMsg, Model model, HttpServletResponse response,@RequestParam("mvnoId") Integer mvnoId) {
        logger.info("In Download Customer : " + username);
        try {
            String filename = "customerprofile.csv";

            response.setContentType("text/csv");
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + filename + "\"");

            //create a csv writer
            StatefulBeanToCsv<Customers> writer = new StatefulBeanToCsvBuilder<Customers>(response.getWriter())
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();
            writer.write(customerService.downloadcustomer(username.toLowerCase().trim(),mvnoId));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping("/customers/replyitems/save")
    public @ResponseBody CustReplyItem saveReplyItems(@RequestBody CustReplyItem cust) {
        String tempid = cust.getTempid();
        CustReplyItem c = replyItemService.save(cust);
        c.setTempid(tempid);
        return c;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping("/customers/replyitems/delete")
    public @ResponseBody void deleteReplyItems(@RequestBody CustReplyItem cust) {
        replyItemService.delete(cust.getId());
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping(path = {"/customers/addplan"})
    public String addPlan(com.adopt.apigw.model.common.Customers entity, Model model) {
        if (entity.getPlanMappingList() == null) {
            entity.setPlanMappingList(new ArrayList<CustPlanMappping>());
        }
        entity.getPlanMappingList().add(new CustPlanMappping());
        model.addAttribute("customer", entity);
        return "radius/customers/custform";
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping(path = {"/customers/removeplan"}, params = "removeindex")
    public String deletePlna(com.adopt.apigw.model.common.Customers entity, @RequestParam("removeindex") int index, Model model) {
        entity.getPlanMappingList().get(index).setEndDate(LocalDateTime.now());
        model.addAttribute("customer", entity);
        return "radius/customers/custform";
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping("/customers/search")
    public ResponseEntity<?> searchCustomers(@RequestParam(name = "s", defaultValue = "") String search) {
        List<Customers> custs = customerService.searchCustomersCustom(search);
        HashMap<String, Object> response = new HashMap<>();
        response.put("items", custs);
        return apiResponse(APIConstants.SUCCESS, response);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.common.Customers', '2')")
    @RequestMapping(value = {"/odbillrun"}, method = RequestMethod.POST)
    public String odbillrun(@RequestParam("odcustid") Integer custid, @RequestParam("odbillrundate") String billrundate, Model model, RedirectAttributes ra) {
        boolean bError = true;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate bDate = LocalDate.parse(billrundate, formatter);
            Customers cust = customersRepository.findById(custid).get();

            customerService.generateAndEmailInvoice(bDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")), cust, customerService.getLoggedInUserId(), null, null, "", null, null, null);

            ra.addFlashAttribute("flashMsg", "success");

        } catch (Exception e) {
            ra.addFlashAttribute("flashMsg", "error");
            bError = true;
        }
        return "redirect:/customers/1";
    }

    @RequestMapping("/customers/managemac/{id}")
    public String manageCustomersMac(@PathVariable Integer id, Model model) {
        Customers customer = customersRepository.findById(id).get();
        CustMacMapppingPojo custMacMapppingPojo = new CustMacMapppingPojo();
        custMacMapppingPojo.setCustid(customer.getId());
        custMacMapppingPojo.setCustMacMapppingList(customer.getCustMacMapppingList());
        model.addAttribute("customer", customer);
        model.addAttribute("custMacMapppingPojo", custMacMapppingPojo);
        return "radius/customers/managemac";
    }

    @RequestMapping(path = {"/customers/managemac/add"})
    public String addManageCustomersMac(CustMacMapppingPojo entity, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        CustMacMappping custMacMappping = new CustMacMappping();
        if (entity.getCustMacMapppingList() == null) {
            entity.setCustMacMapppingList(new ArrayList<CustMacMappping>());
        }
        entity.getCustMacMapppingList().add(custMacMappping);
        model.addAttribute("customer", cust);
        model.addAttribute("custMacMapppingPojo", entity);
        return "radius/customers/managemac";
    }

    @RequestMapping(path = {"/customers/managemac/save"})
    public String saveManageCustomersMac(CustMacMapppingPojo entity, final RedirectAttributes ra) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        for (CustMacMappping entityTemp : entity.getCustMacMapppingList()) {
            entityTemp.setCustomer(cust);
        }
        custMacMapppingService.saveAll(entity.getCustMacMapppingList());
        ra.addFlashAttribute("successFlash", "Manage MAC Saved Successfully");
        return "redirect:/customers/managemac/" + cust.getId();
    }

    @RequestMapping(path = {"/customers/managemac/remove"}, params = "removeindex")
    public String deleteManageCustomersMac(CustMacMapppingPojo entity, @RequestParam("removeindex") int index, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        entity.getCustMacMapppingList().remove(index);
        model.addAttribute("custMacMapppingPojo", entity);
        return "redirect:/customers/managemac/" + cust.getId();
    }

    @RequestMapping("/customers/dcharges/{id}")
    public String directCharge(@PathVariable Integer id, Model model) {
        Customers customer = customersRepository.findById(id).get();
        List<Charge> chargesByCustomer = chargeService.findAllByChargetype(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT);
        CustChargeOverride cco = new CustChargeOverride();
        cco.setCustid(customer.getId());
        List<CustChargeDetails> chargListByType = customer.getOverChargeList().stream()
                .filter(c -> c.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT))
                .collect(Collectors.toList());
        cco.setChargeList(customer.getIndiChargeList());
        model.addAttribute("customer", customer);
        model.addAttribute("directcharge", cco);
        model.addAttribute("chargesList", chargesByCustomer);
        return "radius/customers/directcharge";
    }

    @RequestMapping(path = {"/customers/dcharges/add"})
    public String addDirectCharge(CustChargeOverride entity, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        List<Charge> chargesByCustomer = chargeService.findAllByChargetype(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT);
        CustChargeDetails custChargeDetails = new CustChargeDetails();
        if (entity.getChargeList() == null) {
            entity.setChargeList(new ArrayList<CustChargeDetails>());
        }
        entity.getChargeList().add(custChargeDetails);
        model.addAttribute("customer", cust);
        model.addAttribute("directcharge", entity);
        model.addAttribute("chargesList", chargesByCustomer);
        return "radius/customers/directcharge";
    }

    @RequestMapping(path = {"/customers/dcharges/save"})
    public String saveDirectCharge(CustChargeOverride entity, final RedirectAttributes ra) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        for (CustChargeDetails data : entity.getChargeList()) {
            data.setCustomer(cust);
            data.setChargetype(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT);
            custChargeService.save(data);
        }
        ra.addFlashAttribute("successFlash", "Direct Charges Successfully");
        return "redirect:/customers/dcharges/" + cust.getId();
    }

    @RequestMapping(path = {"/customers/dcharges/remove"}, params = "removeindex")
    public String deleteDirectCharge(CustChargeOverride entity, @RequestParam("removeindex") int index, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        entity.getChargeList().remove(index);
        model.addAttribute("directcharge", entity);
        return "redirect:/customers/dcharges/" + cust.getId();
    }

    @RequestMapping("/customers/coverride/{id}")
    public String chargeOverride(@PathVariable Integer id, Model model) {
        Customers cust = customersRepository.findById(id).get();
        List<CustPlanMappping> custPlanList = cust.getPlanMappingList();
        if (custPlanList != null && custPlanList.size() > 0) {
            for (CustPlanMappping custPlanMappping : custPlanList) {
                if (custPlanMappping.getPlanId() != null) {
                    custPlanMappping.setPostpaidPlan(postpaidPlanService.get(custPlanMappping.getPlanId(),cust.getMvnoId()));
                }
            }
        }
        for (CustChargeDetails temp : cust.getOverChargeList()) {
            if (temp.getPlanid() != null) {
                PostpaidPlan plan = postpaidPlanService.get(temp.getPlanid(),cust.getMvnoId());
                temp.setListOfChargeByPlan(plan.getChargeList());
            }
        }
        CustChargeOverride cco = new CustChargeOverride();
        cco.setCustid(cust.getId());
        List<CustChargeDetails> chargListByType = cust.getOverChargeList().stream()
                .filter(c -> !c.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT))
                .collect(Collectors.toList());
        cco.setChargeList(cust.getOverChargeList());
        model.addAttribute("customer", cust);
        model.addAttribute("override", cco);
        model.addAttribute("planList", custPlanList);
        return "radius/customers/chargeoverride";

    }

    @RequestMapping(path = {"/customers/coverride/add"})
    public String addChargeOverride(CustChargeOverride entity, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        List<CustPlanMappping> custPlanList = cust.getPlanMappingList();
        CustChargeDetails custChargeDetails = new CustChargeDetails();
        if (custPlanList != null && custPlanList.size() > 0) {
            for (CustPlanMappping custPlanMappping : custPlanList) {
                if (custPlanMappping.getPlanId() != null) {
                    PostpaidPlan plan = postpaidPlanService.get(custPlanMappping.getPlanId(),cust.getMvnoId());
                    custPlanMappping.setPostpaidPlan(plan);
                }
            }
        }
        if (entity.getChargeList() == null) {
            entity.setChargeList(new ArrayList<CustChargeDetails>());
        } else {
            for (CustChargeDetails temp : entity.getChargeList()) {
                if (temp.getPlanid() != null) {
                    PostpaidPlan plan = postpaidPlanService.get(temp.getPlanid(),cust.getMvnoId());
                    temp.setListOfChargeByPlan(plan.getChargeList());
                }
            }
        }
        entity.getChargeList().add(custChargeDetails);
        model.addAttribute("customer", cust);
        model.addAttribute("override", entity);
        model.addAttribute("planList", custPlanList);
        return "radius/customers/chargeoverride";
    }

    @RequestMapping(path = {"/customers/coverride/chargelist"})
    public String getchargeListByplan(CustChargeOverride entity, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        List<CustPlanMappping> custPlanList = cust.getPlanMappingList();
        if (custPlanList != null && custPlanList.size() > 0) {
            for (CustPlanMappping custPlanMappping : custPlanList) {
                if (custPlanMappping.getPlanId() != null) {
                    PostpaidPlan plan = postpaidPlanService.get(custPlanMappping.getPlanId(),cust.getMvnoId());
                    custPlanMappping.setPostpaidPlan(plan);
                }
            }
        }
        for (CustChargeDetails temp : entity.getChargeList()) {
            if (temp.getPlanid() != null) {
                PostpaidPlan plan = postpaidPlanService.get(temp.getPlanid(),cust.getMvnoId());
                temp.setListOfChargeByPlan(plan.getChargeList());
            }
        }
        entity.setChargeList(entity.getChargeList());
        model.addAttribute("customer", cust);
        model.addAttribute("override", entity);
        model.addAttribute("planList", custPlanList);
        return "radius/customers/chargeoverride";
    }

    @RequestMapping(path = {"/customers/coverride/remove"}, params = "removeindex")
    public String deletePlna(CustChargeOverride entity, @RequestParam("removeindex") int index, Model model) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        //	List<CustPlanMappping> custPlanList=cust.get;

        entity.getChargeList().remove(index);
        model.addAttribute("override", entity);
        //  model.addAttribute("planList", custPlanList);
        return "redirect:/customers/coverride/" + cust.getId();
    }

    @RequestMapping(path = {"/customers/coverride/save"})
    public String saveChargeOverride(CustChargeOverride entity, final RedirectAttributes ra) {
        Customers cust = customersRepository.findById(entity.getCustid()).get();
        for (CustChargeDetails data : entity.getChargeList()) {
            data.setCustomer(cust);
            data.setChargetype(chargeService.get(data.getChargeid(),cust.getMvnoId()).getChargetype());
            custChargeService.save(data);
        }
        List<CustPlanMappping> custPlanList = cust.getPlanMappingList();
        if (custPlanList != null && custPlanList.size() > 0) {
            for (CustPlanMappping custPlanMappping : custPlanList) {
                if (custPlanMappping.getPlanId() != null) {
                    PostpaidPlan plan = postpaidPlanService.get(custPlanMappping.getPlanId(),cust.getMvnoId());
                    custPlanMappping.setPostpaidPlan(plan);
                }
            }
        }
        ra.addFlashAttribute("successFlash", "Charges Overriden successfully");
        return "redirect:/customers/coverride/" + cust.getId();
    }


}
