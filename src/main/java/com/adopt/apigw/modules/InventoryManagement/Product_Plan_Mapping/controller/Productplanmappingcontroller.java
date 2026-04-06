package com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.controller;

import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.controller.api.ApiBaseController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.domain.Productplanmapping;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.service.ProductplanmappingService;
import com.adopt.apigw.service.postpaid.PlanGroupService;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/v1")
public class Productplanmappingcontroller extends ApiBaseController {
    private static String MODULE = " [Productplanmappingcontroller] ";
    private static final Logger logger = LoggerFactory.getLogger(Productplanmappingcontroller.class);
    @Autowired
    private ProductplanmappingService mappingService;
    @Autowired
    private PlanGroupService planGroupService;

    @GetMapping("/getproductfromplan")
    public List<Productplanmapping> getproductfromplan(@RequestParam("id") Long id) throws Exception {
        List<Productplanmapping> list = new ArrayList<>();
        if(Objects.nonNull(id)){
            list = mappingService.getallfromplan(id);
        }
        return list;
    }
    // Get Product Category Details By PlanId
    @GetMapping("/getProductCategoryByPlanId")
    public GenericDataDTO getProductCategoryByPlanId(@RequestParam("mappingId") Long mappingId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(mappingService.getProductCategoryByPlanId(mappingId));
        } catch (Exception ex){
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    // Get Product By PlanId
    @GetMapping("/getProductByPlanId")
    public GenericDataDTO getProductByPlanId(@RequestParam("mappingId") Integer mappingId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(mappingService.getProductByPlanId(mappingId));
        } catch (Exception ex){
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    //Delete Product Plan and Plan Group Mapping by Plan Group Id and Plan Id
    @DeleteMapping("/deleteProductPlanGroupMapping")
    public ResponseEntity<?> deleteProductPlanGroupMapping(@RequestParam(name = "planGroupId", required = true) Long planGroupId, @RequestParam (name = "planId", required = true) Long planId, HttpServletRequest request) {
        MDC.put("type", "delete");
        HashMap<String, Object> response = new HashMap<>();
        try {
            planGroupService.deleteProductPlanGroupMapping(planGroupId, planId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "DB Mapping has been deleted successfully.");
            logger.info("deleting PlanGroupMappingById with name   :   request: { From : {}}; Response : {{}}", MODULE, responseCode, response);
            MDC.remove("type");
            return apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.error("Unable to deletePlanGroupMappingById with name :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", MODULE, responseCode, response, e.getStackTrace());
            MDC.remove("type");
            return apiResponse(responseCode, response);
        }
    }

}
