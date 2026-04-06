package com.adopt.apigw.controller.radius.vouchermaster;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.radius.VoucherBatch;
import com.adopt.apigw.model.radius.VoucherMaster;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.radius.VoucherBatchService;
import com.adopt.apigw.service.radius.VoucherMasterService;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class VoucherController  extends BaseController<VoucherMaster> {
	
	private static final Logger logger = LoggerFactory.getLogger(VoucherController.class);


    VoucherMasterService voucherMasterService;
    VoucherBatchService voucherBatchService;
    PostpaidPlanService planService;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    
    @Autowired
    public void setVoucherMaster(VoucherMasterService voucherMasterService) { this.voucherMasterService = voucherMasterService;
    }
    @Autowired
    public void setVoucherBatch(VoucherBatchService voucherBatchService) { this.voucherBatchService = voucherBatchService;
    }
    @Autowired
    public void setPostpaidPlanService(PostpaidPlanService planService) {
        this.planService = planService;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '1')")
    @RequestMapping(value = "/Voucher")
    public String index() {
        return "redirect:/Voucher/1";
    }

    @RequestMapping("/Voucher/add")
    public String add(Model model) {
        model.addAttribute("VoucherMaster", voucherMasterService.getVoucherMasterForAdd());
        model.addAttribute("VoucherMasterPlanList", planService.getAllPrepaidPlans());
        return "radius/voucher/voucherform";
    }

    @RequestMapping("/VoucherMaster/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("VoucherMaster", voucherMasterService.getVoucherMasterForEdit(id));
        model.addAttribute("VoucherMasterPlanList", planService.getAllPrepaidPlans());
        return "radius/voucher/voucherform";
    }



    @RequestMapping(value = "/Voucher/save", method = RequestMethod.POST)
    public String save(VoucherMaster voucherMaster, final RedirectAttributes ra) {
        String operation="edit";
        String flashMsg="";
        try{
            if(voucherMaster !=null ){
                operation="add";
            }
            VoucherMaster save = voucherMasterService.saveVoucherMaster(voucherMaster);
            for(int i=0;i<voucherMaster.getVcQty();i++)
            {
                voucherBatchService.save(getObject(UtilsCommon.getResponse(voucherMaster.getNumeric(),voucherMaster.getUppercase(),voucherMaster.getLowercase(),voucherMaster.getVoucherlength()),voucherMaster));
            }
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
        return "redirect:/Voucher/1";
    }


    public VoucherBatch getObject(String code,VoucherMaster voucherMaster) { return new VoucherBatch(code,voucherMaster.getId(),voucherMaster.getPlid(), LocalDate.now().plusDays(voucherMaster.getVouchervalidity())); }

    @RequestMapping(value = "/Voucher/{pageNumber}", method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsg") String flashMsg, Model model) {
        int dbPageSize=10;
        int dispPageSize=5;
        Page<VoucherMaster> page =null;
        if(search!=null && !"".equalsIgnoreCase(search)){

            page = voucherMasterService.findVoucherMaster(search.toLowerCase().trim(), pageNumber,dbPageSize);
        }else{
            page = voucherMasterService.getList(pageNumber,dbPageSize);
        }

        setPaginationParameters("Plan", flashMsg, search, model, page,dbPageSize,dispPageSize);
        return "radius/voucher/voucherlist";
    }
    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '2')")
    @PostMapping("/uploadVoucherBulk") // //new annotation since 4.3
    public String singleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        String TMP_DIR = System.getProperty("java.io.tmpdir");

        if (file.isEmpty()) {
            redirectAttributes.addAttribute("message", "Please select a file to upload");
            return "redirect:uploadStatus";
        }
        String flashMsg="success";
        BufferedReader br = null;
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(TMP_DIR + file.getOriginalFilename());
            Files.write(path, bytes);
            logger.info("File is "+path.toString());
            String line = "";
            String cvsSplitBy = ",";

            br = new BufferedReader(new FileReader(path.toString()));
            while ((line = br.readLine()) != null) {

                // use comma as separator
                String[] customer = line.split(cvsSplitBy);

                logger.info("VoucherMaster "+customer);
                VoucherMaster voucherMaster=new VoucherMaster();
                voucherMaster.setVcName(customer[0]);
                voucherMaster.setPlan (postpaidPlanRepo.findById((Integer.valueOf(customer[1]))).get());
                voucherMaster.setVcQty(Integer.valueOf(customer[2]));
                voucherMaster.setVoucherlength(Integer.valueOf(customer[3]));
                voucherMaster.setVouchervalidity(Integer.valueOf(customer[4]));
                voucherMaster.setLowercase(customer[5]);
                voucherMaster.setUppercase(customer[6]);
                voucherMaster.setNumeric(customer[7]);
                VoucherMaster save = voucherMasterService.save(voucherMaster);
            }
            flashMsg="FileUploadSucess";

        } catch (Exception e) {
            e.printStackTrace();
            flashMsg="error";

        }finally{
            try{
                if(br!=null){
                    br.close();
                }
            }catch(Exception e){

            }
        }
        redirectAttributes.addFlashAttribute("flashmsg",flashMsg);
        return "redirect:/Voucher/1";
    }
    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherMaster', '4')")
    @RequestMapping("/Voucher/delete/{id}") 
    public String delete(@PathVariable Integer id) { planService.delete(id);return "redirect:/Voucher/1"; }

}
