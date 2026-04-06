package com.adopt.apigw.controller.radius.plan;

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

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.model.radius.Plan;
import com.adopt.apigw.service.radius.PlanService;
import com.adopt.apigw.utils.UtilsCommon;

@Controller
public class PlanController extends BaseController<Plan> {

    PlanService planService;
    @Autowired
    public void setPlanService(PlanService planService) {
        this.planService = planService;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.Plan', '1')")
    @RequestMapping(value = "/Plan")
    public String index() {
        return "redirect:/Plan/1";
    }

    @RequestMapping(value = "/Plan/{pageNumber}", method = RequestMethod.GET)
    public String list(@PathVariable Integer pageNumber, @RequestParam(name="s",defaultValue="")  String search , @ModelAttribute("flashMsg") String flashMsg, Model model) {
        int dbPageSize=10;
        int dispPageSize=5;
        Page<Plan> page =null;
        if(search!=null && !"".equalsIgnoreCase(search)){
            page = planService.findPlan(search.toLowerCase().trim(), pageNumber,dbPageSize);
        }else{
            page = planService.getList(pageNumber,dbPageSize);
        }

        setPaginationParameters("Plan", flashMsg, search, model, page,dbPageSize,dispPageSize);
        return "radius/Plan/planlist";
    }

    @RequestMapping("/Plan/delete/{id}")
    public String delete(@PathVariable Integer id) {
        planService.deletePlan(id);
        return "redirect:/Plan/1";
    }


    @RequestMapping("/Plan/add")
    public String add(Model model) {
        model.addAttribute("Plan", planService.getPlanForAdd());
        model.addAttribute("planTypeMap", UtilsCommon.getPlanTypeMap());
        model.addAttribute("planStatusMap", UtilsCommon.getCGStatusMap());
        return "radius/Plan/planform";
    }

    @RequestMapping("/Plan/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        model.addAttribute("Plan", planService.getPlanForEdit(id));
        model.addAttribute("planTypeMap", UtilsCommon.getPlanTypeMap());
        model.addAttribute("planStatusMap", UtilsCommon.getCGStatusMap());
        return "radius/Plan/planform";

    }
//    @PreAuthorize("hasPermission('com.adopt.apigw.model.radius.Plan', '2')")
    @RequestMapping(value = "/Plan/save", method = RequestMethod.POST)
    public String save(Plan plan, final RedirectAttributes ra) {
        String operation="edit";
        String flashMsg="";
        try{
            if(plan !=null && plan.getId()==null){
                operation="add";
            }
            Plan save = planService.savePlan(plan);
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
        return "redirect:/Plan/1";
    }

}
