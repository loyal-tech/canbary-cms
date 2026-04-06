package com.adopt.apigw.audit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.spring.SpringContext;
import com.fasterxml.jackson.core.JsonProcessingException;

@Controller
public class AuditController extends BaseController<AuditResponse> {

    private static final String RETURN_URI_LIST = "temp/auditlist";

    private static final String RETURN_URI_LIST1 = "temp/shadowlist";


    @Autowired
    private AuditService auditService;

    @Autowired
    private StaffUserService staffUserService;

    @ModelAttribute("allEntityPojo")
    public List<EntityPojo> getEntityPojoList() {
        return auditService.getAllEntityPojo();
    }

    @ModelAttribute("userList")
    public List<StaffUserPojo> getAllUsersList() throws Exception {
        return staffUserService.convertResponseModelIntoPojo(staffUserService.getAllActiveEntities());
    }


    @RequestMapping(value = {"/audit/{pageNumber}", "/audit"})
    public String auditList(@ModelAttribute(value = "auditSearchRequest") AuditSearchRequest auditSearchRequest, Model model) throws ClassNotFoundException {
        if (auditSearchRequest.getFromDate() != null && auditSearchRequest.getToDate() != null)
            model.addAttribute("list", auditService.getCodSnapshot(auditSearchRequest));
        return RETURN_URI_LIST;
    }

    @RequestMapping(value = "/entity/shadows")
    public String getShadowByEntity(@RequestParam(value = "entityId") Integer entityId, @RequestParam(value = "metadataId") Double metadataId, @RequestParam(value = "classPath") String classPath, Model model) throws ClassNotFoundException, JsonProcessingException {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        AuditSearchRequest auditSearchRequest = new AuditSearchRequest();
        auditSearchRequest.setEntityId(entityId);
        auditSearchRequest.setClassPath(classPath);
        auditSearchRequest.setMetadataId(metadataId);
        model.addAttribute("list", auditService.getShadowByEntity(auditSearchRequest));
        return RETURN_URI_LIST1;
    }
}
