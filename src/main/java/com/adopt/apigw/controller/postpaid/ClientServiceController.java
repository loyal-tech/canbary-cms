package com.adopt.apigw.controller.postpaid;

import com.adopt.apigw.constants.CacheConstant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.base.BaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.pojo.ConfigurationPojo;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.radius.CustomACLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ClientServiceController extends BaseController<ClientService> {

    private static final String RETURN_URI_LIST = "postpaid/system/configuration";

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private CustomACLService aclService;

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping("/system/configuration")
    public String add(Model model) {
        ConfigurationPojo config = new ConfigurationPojo();
        config.setClientServiceList(clientServiceSrv.getAllEntity());
        model.addAttribute("configurationPojo", config);
        return RETURN_URI_LIST;
    }

    @RequestMapping(value = "/system/configuration/save", method = RequestMethod.POST)
    public String save(ConfigurationPojo configurationPojo, final RedirectAttributes ra) {
        String operation = "edit";
        String flashMsg = "";
        try {
            if (configurationPojo != null && configurationPojo.getId() == null) {
                operation = "add";
            } else {
            }
            List<ClientService> save = clientServiceSrv.saveAllEntity(configurationPojo.getClientServiceList());
            if (save != null) {
                aclService.reloadCache();
                if (operation.equalsIgnoreCase("add")) {
                    flashMsg = "Configuration Saved Successfully";
                } else {
                    flashMsg = "Configuration Updated Successfully";
                }
                ra.addFlashAttribute("successFlash", flashMsg);
            } else {
                flashMsg = "Error Performing operation, Please try after sometime !!!";
                ra.addFlashAttribute("errorFlash", flashMsg);
            }
        } catch (Exception e) {
            flashMsg = "error";
            ra.addFlashAttribute("errorFlash", flashMsg);
        }
        return "redirect:/system/configuration";
    }

    @GetMapping("/system/configuration" + UrlConstants.CLEAR_CACHE)
    public GenericDataDTO clearCache() {
        String SUB_MODULE = this.getModuleNameForLog() + "[clearCache]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            ApplicationLogger.logger.info(SUB_MODULE);
            cacheManager.getCache(CacheConstant.CLIENT_SRV).clear();
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to clear cache");
        }
        return genericDataDTO;
    }

    public String getModuleNameForLog() {
        return "[ClientService]";
    }
}
