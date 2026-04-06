package com.adopt.apigw.controller.invoice.invoiceserver;

import java.util.List;

import com.adopt.apigw.repository.postpaid.InvoiceServerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.audit.AuditService;
import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.postpaid.InvoiceServer;
import com.adopt.apigw.model.radius.RadiusValidator;
import com.adopt.apigw.service.postpaid.InvoiceServerService;

@Controller
public class InvoiceServerController extends BaseController<InvoiceServer>{

    private static final Logger logger = LoggerFactory.getLogger(AuditService.class);

    private InvoiceServerService invoiceServerService;
	@Autowired
	private InvoiceServerRepository invoiceServerRepository;


    @Autowired
    public void setInvoiceServerService(InvoiceServerService invoiceServerService) {
        this.invoiceServerService = invoiceServerService;
    }
    

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '1')")    
    @RequestMapping(value = "/invoiceserver")
    public String index() {
        return "redirect:/invoiceserver/1";
    }
    
    @RequestMapping(value = "/invoiceserver/{pageNumber}", method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber,@RequestParam(name="s",defaultValue="")  String search,@ModelAttribute("flashMsg") String flashMsg, Model model) {
//    	InvoiceServer c = invoiceServerService.get(100);

    	
        Page<InvoiceServer> page = invoiceServerService.getList(pageNumber);

    	List<InvoiceServer> listServer=page.getContent();
       	
       	for (int i = 0; i < listServer.size(); i++) {
       		InvoiceServer invoiceServer=listServer.get(i);
	        RadiusValidator radiusValidator=null;
	    	String webPort=invoiceServer.getWebport();
	    	String serverIP=invoiceServer.getServerip();
	    	String strUR="http://"+serverIP+":"+webPort+"/billing-engine-1.0/billingprocess/statuscheck";
	    	logger.info("Validation URL:"+strUR);
	    	String respose=null;
	    	//radServer.setStatus("1");

	    	try {
	         	 HttpHeaders headers = new HttpHeaders();
		       	 Object msg=null;
		         RestTemplate restTemplate = new RestTemplate();
		         respose= restTemplate.getForObject(strUR,String.class);
		    }
	    	catch(Exception e){
	    		invoiceServer.setStatus("0");
	    	}
	    	 
	    	logger.info("Response:"+radiusValidator);
	    	if(respose!=null && respose.contains("{\"responseCode\":\"200\",\"responseMessage\":\"Success\",\"responseObject\":null}")) {
	    		invoiceServer.setStatus("1");
	
		    }else 
	    	{
		    	invoiceServer.setStatus("0");
	
	    	}
       	}
    	setPaginationParameters("Invoice Server", flashMsg, search, model, page);
        if(flashMsg !=null && !"".equalsIgnoreCase(flashMsg)){
        	if (flashMsg.equalsIgnoreCase("serverError")){
                model.addAttribute("errorFlash", "Please Make Sure Invoice is running");
        	}
        }        
        return "postpaid/invoiceserver/invoiceserverlist";
    }

    @RequestMapping("/invoiceserver/add")
    public String add(Model model) {
        model.addAttribute("invoiceserver", invoiceServerService.getInvoiceServerForAdd());
        return "postpaid/invoiceserver/invoiceserverform";

    }
    @RequestMapping("/invoiceserver/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) throws Exception{
        model.addAttribute("invoiceserver", invoiceServerService.getInvoiceServerForEdit(id));
        return "postpaid/invoiceserver/invoiceserverform";

    }
    
//    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '2')")        
    @RequestMapping(value = "/invoiceserver/save", method = RequestMethod.POST)
    public String save(InvoiceServer invoiceServer,RedirectAttributes ra) throws Exception{
    	String webPort=invoiceServer.getWebport();
    	String serverIP=invoiceServer.getServerip();
    	//RestTemplate restTemplate=new RestTemplate();
    	String strUR="http://"+serverIP+":"+webPort+"/billing-engine-1.0/billingprocess/statuscheck";
    	logger.info("Validation URL:"+strUR);

    	
    	String respose=null;
    	try {
	       	 HttpHeaders headers = new HttpHeaders();
	       	 Object msg=null;
	         RestTemplate restTemplate = new RestTemplate();
	         respose= restTemplate.getForObject(strUR,String.class);
	       	 //headers.setContentType(MediaType.parseMediaType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	       	 //HttpEntity<Object> request = new HttpEntity<>(msg, headers);
	    	 //radiusValidator=restTemplate.postForObject(strUR, request, RadiusValidator.class);  
	    	 //logger.info("Validate is "+response.toString());
    	}
    	catch(Exception e){
      		logger.info("Please Make Sure Invoice Server is running");
      		e.printStackTrace();
	        ra.addFlashAttribute("flashMsg", "serverError");
	        return "redirect:/invoiceserver/1";
    	}
    	 
    	logger.info("Response:"+respose);
    	if(respose!=null & respose.contains("{\"responseCode\":\"200\",\"responseMessage\":\"Success\",\"responseObject\":null}")) {
    		invoiceServer.setStatus("1");
    		InvoiceServer save = invoiceServerService.saveInvoiceServer(invoiceServer);
	        ra.addFlashAttribute("flashMsg", "success");
	        return "redirect:/invoiceserver/1";
	    }
    	else 
    	{
    		logger.info("Please Make Sure Invoice Server is running");
	        ra.addFlashAttribute("flashMsg", "serverError");
	        return "redirect:/invoiceserver/1";
    	}

    }

    @RequestMapping("/invoiceserver/delete/{id}")
    public String delete(@PathVariable Integer id) throws Exception{
    	invoiceServerService.deleteInvoiceServer(id);
        return "redirect:/invoiceserver";

    }
    
    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.InvoiceServer', '1')")            
    @RequestMapping("/invoiceserver/reload/{id}")
    public String reload(@PathVariable Integer id,RedirectAttributes ra) {
    	InvoiceServer invoiceServer=invoiceServerRepository.findById(id).get();
    	String wsResponse=null;
    	String webPort=invoiceServer.getWebport();
    	String serverIP=invoiceServer.getServerip();
    	RestTemplate restTemplate=new RestTemplate();
    	String strUR="http://"+serverIP+":"+webPort+"/billing-engine-1.0/billingprocess/reloadconf";
    	logger.info("Validation URL:"+strUR);
    	
    	try {
	       	 HttpHeaders headers = new HttpHeaders();
	       	 Object msg=null;
	       	 headers.setContentType(MediaType.parseMediaType(MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8"));
	       	 HttpEntity<Object> request = new HttpEntity<>(msg, headers);
	       	 wsResponse=restTemplate.postForObject(strUR,request,String.class);    
	   	}
	   	catch(Exception e){
	   			e.printStackTrace();
	     		logger.info("Please Make Sure Invoice Server is running");
		        ra.addFlashAttribute("flashMsg", "serverError");
		        return "redirect:/invoiceserver/1";
	   	}
	   	 
	   	logger.info("Response:"+wsResponse);
	   	if(wsResponse!=null) {
	   		InvoiceServer save = invoiceServerService.save(invoiceServer);
		        ra.addFlashAttribute("flashMsg", "success");
		        return "redirect:/invoiceserver/1";
		}
	   	else 
	   	{
	   		logger.info("Please Make Sure Invoice Server is running");
		        ra.addFlashAttribute("flashMsg", "serverError");
		        return "redirect:/invoiceserver/1";
	   	}
	 }
}
