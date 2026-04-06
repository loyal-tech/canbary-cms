package com.adopt.apigw.controller.api;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class APITemplateController extends ApiBaseController {

    @Autowired
    private APIController apiController;

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_STAFF_USER_ALL + "\",\""
            + AclConstants.OPERATION_STAFF_USER_VIEW + "\")")
    @GetMapping("/template/staffuser/{id}")
    public ResponseEntity<?> getTemplateStaffById(@PathVariable Integer id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getStaffById(id, req,mvnoId).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("Staff"));
        return apiResponse(HttpStatus.OK.value(), temp);
    }

    @GetMapping("/template/planservice/all")
    public ResponseEntity<?> getTemplatePlanServiceList(HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getPlanServiceList(req,mvnoId).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("serviceList"));
        return apiResponse(HttpStatus.OK.value(), temp);
    }

    @GetMapping("/template/postpaidplan/all")
    public ResponseEntity<?> getTemplateAllPlanList(@RequestParam(defaultValue = "NORMAL") String type, @RequestParam(defaultValue = Constants.PLAN_GROUP_ALL, required = false) String planGroup, HttpServletRequest req, @RequestParam ("mvnoId")Integer mvnoId) throws Exception {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllPlanList(type, planGroup, req,mvnoId).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("postpaidplanList"));
        return apiResponse(HttpStatus.OK.value(), temp);
    }
//
//    @GetMapping("/template/country/all")
//    public ResponseEntity<?> getTemplateAllCountryList(HttpServletRequest req) throws Exception {
//        HashMap<String, Object> temp = new HashMap<>();
//        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllCountryList(req).getBody();
//        temp.put(CommonConstants.DATA_LIST, responseMap.get("countryList"));
//        return apiResponse(HttpStatus.OK.value(), temp);
//    }
//
//    @GetMapping("/template/state/all")
//    public ResponseEntity<?> getTemplateAllStateList(HttpServletRequest req) throws Exception {
//        HashMap<String, Object> temp = new HashMap<>();
//        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllStateList(req).getBody();
//        temp.put(CommonConstants.DATA_LIST, responseMap.get("stateList"));
//        return apiResponse(HttpStatus.OK.value(), temp);
//    }
//
//    @GetMapping("/template/city/all")
//    public ResponseEntity<?> getTemplateAllCityList(HttpServletRequest req) throws Exception {
//        HashMap<String, Object> temp = new HashMap<>();
//        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllCityList(req).getBody();
//        temp.put(CommonConstants.DATA_LIST, responseMap.get("cityList"));
//        return apiResponse(HttpStatus.OK.value(), temp);
//
//    }

    @GetMapping("/template/charge/all")
    public ResponseEntity<?> getTemplateAllChargeList(HttpServletRequest req) throws Exception {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllChargeList(req).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("chargelist"));
        return apiResponse(HttpStatus.OK.value(), temp);

    }

    @GetMapping("/template/partner/all")
    public ResponseEntity<?> getTemplateAllPartnerListWithoutPagination(HttpServletRequest req) throws Exception {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getAllPartnerListWithoutPagination(req).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("partnerlist"));
        return apiResponse(HttpStatus.OK.value(), temp);

    }

    @GetMapping("/template/charge/ByType/{chargeType}")
    public ResponseEntity<?> getTemplateChargeListByType(@PathVariable String chargeType, @RequestParam(name = "serviceId", required = false) Integer serviceId, HttpServletRequest req , @RequestParam ("mvnoId") Integer mvnoId) {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getChargeListByType(chargeType,serviceId, req,mvnoId).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("chargelist"));
        return apiResponse(HttpStatus.OK.value(), temp);

    }

    @GetMapping("/template/charge/getChargeForCustomer")
    public ResponseEntity<?> getTemplateChargeListByCategory(HttpServletRequest req) {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.getChargeListByCategory(req).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("chargelist"));
        return apiResponse(HttpStatus.OK.value(), temp);
    }

    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_PLAN_GROUP_ALL + "\",\"" + AclConstants.OPERATION_PLAN_GROUP_VIEW + "\")")

    @GetMapping("/template/planGroupMappings")
    public ResponseEntity<?> findTemplateAllPlanGroups(@RequestParam String mode,
                                                       @RequestParam(name = "planCategory", required = false) String planCategory, @RequestParam(required = false) Integer custId, @RequestParam(required = false) String accessibility,HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        HashMap<String, Object> temp = new HashMap<>();
        Map<String, Object> responseMap = (Map<String, Object>) apiController.findAllPlanGroups(mode, planCategory, custId, accessibility,null,req,mvnoId).getBody();
        temp.put(CommonConstants.DATA_LIST, responseMap.get("planGroupList"));
        return apiResponse(HttpStatus.OK.value(), temp);
    }

}
