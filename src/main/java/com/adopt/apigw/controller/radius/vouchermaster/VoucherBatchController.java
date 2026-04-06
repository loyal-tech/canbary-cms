package com.adopt.apigw.controller.radius.vouchermaster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.radius.VoucherBatch;
import com.adopt.apigw.service.radius.PlanService;
import com.adopt.apigw.service.radius.VoucherBatchService;

@Controller
public class VoucherBatchController extends BaseController<VoucherBatch> {

    VoucherBatchService voucherBatchService;
    PlanService planService;

    @Autowired
    public void setVoucherBatch(VoucherBatchService voucherBatchService) {
        this.voucherBatchService = voucherBatchService;
    }

    @Autowired
    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '1')")
    @RequestMapping(value = "/VoucherBatch")
    public String index() {
        return "redirect:/VoucherBatch/1";
    }

    @RequestMapping("/VoucherBatch/add")
    public String add(Model model) {
        model.addAttribute("VoucherBatch", new VoucherBatch());
//        model.addAttribute("VoucherMasterPlanList", planService.getAllPlan());
        return "radius/voucher/voucherform";
    }
    @RequestMapping("/VoucherBatch/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
//        model.addAttribute("VoucherMaster", voucherMasterService.get(id));
//        model.addAttribute("VoucherMasterPlanList", planService.getAllPlan());
        return "radius/voucher/voucherform";

    }
    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '2')")
    @RequestMapping(value = "/VoucherBatch/save", method = RequestMethod.POST)
    public String save(VoucherBatch voucherMaster, final RedirectAttributes ra) {
        String operation="edit";
        String flashMsg="";
//
//        try{
//            if(voucherMaster !=null ){
//                operation="add";
//            }
//            VoucherBatch save = voucherMasterService.save(voucherMaster);
//            for(int i=0;i<voucherMaster.getVcQty();i++)
//            {
//                voucherBatchService.save(getObject(CommonUtils.getResponse(voucherMaster.getNumeric(),voucherMaster.getUppercase(),voucherMaster.getLowercase(),voucherMaster.getVoucherlength()),voucherMaster));
//            }
//            if(save !=null){
//                if(operation.equalsIgnoreCase("add")){
//                    flashMsg="AddSuccess";
//                }else{
//                    flashMsg="EditSuccess";
//                }
//            }else{
//                flashMsg="error";
//            }
//        }catch(Exception e){
//            flashMsg="error";
//        }
        ra.addFlashAttribute("flashMsg", flashMsg);
        return "redirect:/Voucher/1";
    }
//    public VoucherBatch getObject(String code,VoucherBatch voucherBatch)
//    {
//        return new VoucherBatch(code,voucherMaster.getId(),voucherMaster.getPlid(), LocalDateTime.now().plusDays(voucherMaster.getVouchervalidity()));
//    }
    
    @RequestMapping(value = "/VoucherBatch/{pageNumber}", method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsg") String flashMsg, Model model) {
        int dbPageSize=10;
        int dispPageSize=5;
        Page<VoucherBatch> page =null;

        if(search!=null && !"".equalsIgnoreCase(search)){
            page = voucherBatchService.findVoucherBatch(search.toLowerCase().trim(), pageNumber,dbPageSize);
        }else{
            page = voucherBatchService.getList(pageNumber,dbPageSize);
        }

        setPaginationParameters("Plan", flashMsg, search, model, page,dbPageSize,dispPageSize);
        return "radius/voucher/voucherbatchlist";
    }
    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.VoucherBatch', '4')")
    @RequestMapping("/VoucherBatch/delete/{id}")
    public String delete(@PathVariable Integer id) {
        planService.delete(id);
        return "redirect:/VoucherBatch/1";
    }

    @RequestMapping("/VoucherBatch/listById/{id}")
    public String listofvoucher(@PathVariable Integer id, @ModelAttribute("flashMsg") String flashMsg, Model model) {

        int dbPageSize=10;
        int dispPageSize=5;
        Page<VoucherBatch> page =null;
        Page<VoucherBatch> page1=null;
        if(id!=null ){
             page1 = new PageImpl<>(voucherBatchService.getVoucherBatchList(id));
            page = voucherBatchService.getVoucherBatchListByMasterId(id, 1,dbPageSize);
        }else{
            page = voucherBatchService.getList(1,dbPageSize);
        }

        setPaginationParameters("VoucherBatch", flashMsg, "", model, page1,dbPageSize,dispPageSize);
        return "radius/voucher/voucherbatchlist";
    }

}
