package com.adopt.apigw.modules.Reseller.controller;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.modules.Reseller.domain.Reseller;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.module.ResellerDto;
import com.adopt.apigw.modules.Reseller.service.ResellerService;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "Reseller Management", description = "REST APIs related to Reseller Entity!!!!", tags = "Reseller")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+"/Reseller")
public class ResellerController {
    private final Logger log = LoggerFactory.getLogger(ResellerController.class);
    private static final String RESELLER_LIST = "resellerList";
    private static final String RESELLER = "reseller";
    private static final String PLAN_LIST = "planList";
    private static final String CUSTOMER_LIST = "customerList";

    @Autowired
    ResellerService resellerService;
    @Autowired
    APIResponseController apiResponseController;

    @Autowired
    PostpaidPlanService planService;

    @Autowired
    CustomersService customerService;

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @ApiOperation(value = "Get list of all resellers in the system")
    @GetMapping("/resellers")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllReseller(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Long mvnoId
            , @RequestParam(name = "resellerName", required = false) String resellerName, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {

            PageableResponse<Reseller> pageableResponse = resellerService.getAllReseller(mvnoId, paginationDTO, resellerName);
            Integer responseCode = 0;
            if (CollectionUtils.isEmpty(pageableResponse.getData())) {
                responseCode = APIConstants.NULL_VALUE;
                if(!StringUtils.isEmpty(resellerName)){
                    response.put(APIConstants.ERROR_MESSAGE, "No Reseller found with the name : " + resellerName);
                    snmpCounters.incrementSearchResellerListFailure();
                }else{
                    response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                    snmpCounters.incrementGetAllResellerListFailure();
                }
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put("resellers", pageableResponse);
                if (!StringUtils.isEmpty(resellerName)) {
                    log.debug("Request to fetch All Reseller by name: " + resellerName + " by: " + MDC.get("username"));
                    snmpCounters.incrementSearchResellerListSuccess();
                } else {
                    log.debug("Request to Fetch All Resellers by " + MDC.get("username"));
                    snmpCounters.incrementGetAllResellerListSuccess();
                }
            }
                return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {

            Integer responseCode = APIConstants.FAIL;
            log.error("Error while fetching resellers: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
            if(!StringUtils.isBlank(resellerName)){
                snmpCounters.incrementSearchResellerListFailure();
            }else{
                snmpCounters.incrementGetAllResellerListFailure();
            }
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of all resellers")
    @GetMapping("/getAllResellers")
//    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllResellers(PaginationDTO paginationDTO, @RequestParam(name = "mvnoId", required = true) Long mvnoId
            , @RequestParam(name = "resellerName", required = false) String resellerName, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            PageableResponse<Reseller> pageableResponse = resellerService.getAllReseller(mvnoId, paginationDTO, resellerName);
            response.put("resellers", pageableResponse);
            if (!StringUtils.isEmpty(resellerName)) {
                log.debug("Request to fetch All Reseller by name: " + resellerName + " by: " + MDC.get("username"));
                snmpCounters.incrementSearchResellerListSuccess();
            } else {
                log.debug("Request to Fetch All Resellers by " + MDC.get("username"));
                snmpCounters.incrementGetAllResellerListSuccess();
            }
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {

            Integer responseCode = APIConstants.FAIL;
            log.error("Error while fetching resellers: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            e.printStackTrace();
            if(!StringUtils.isBlank(resellerName)){
                snmpCounters.incrementSearchResellerListFailure();
            }else{
                snmpCounters.incrementGetAllResellerListFailure();
            }
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of all resellers in the system")
    @GetMapping("/findAllReseller")
    @PreAuthorize("@roleAccesses.hasPermission('plan','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllReseller(@RequestParam(name = "mvnoId", required = true) Long mvnoId, @RequestParam(name = "locationId", required = false) Long locationId,
                                                               HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            List<Reseller> resellerList = resellerService.findAllResellers(mvnoId, locationId);
            Integer responseCode = APIConstants.SUCCESS;
//			    log.debug("Request For Fetch All Plans");
            response.put(RESELLER_LIST, resellerList);
            snmpCounters.incrementFindAllResellerListSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error("Error while fetch Resellers: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementFindAllResellerListFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get reseller based on the given reseller id")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','readAccess',#request.getHeader('requestFrom'))")
    @GetMapping("/findResellerById")
    public ResponseEntity<Map<String, Object>> findResellerById(
            @RequestParam(name = "resellerId", required = true) Long resellerId,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            Reseller resellerVo = resellerService.findResellerById(resellerId, mvnoId, false);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(RESELLER, resellerVo);
//			log.debug("Request For Fetch reseller by id: "+resellerId);
            log.debug("Reseller has been fetched successfully of id : " + resellerId + " by "
                    + MDC.get("username"));
            snmpCounters.incrementFindResellerByIdSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while fetch reseller by Id: " + resellerId + " " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementFindResellerByIdFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Add new reseller")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PostMapping("/addReseller")
    public ResponseEntity<Map<String, Object>> addReseller(@RequestBody ResellerDto reseller,
                                                           @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_CREATE);
        try {
            Reseller resellerVo = resellerService.saveReseller(reseller, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(RESELLER, resellerVo);
            response.put(APIConstants.MESSAGE, "Reseller has been added successfully.");
            log.info("Reseller has been added successfully by " + MDC.get("userName") + " : "
                    + resellerVo.getResellerName());
            snmpCounters.incrementCreateResellerSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while creating reseller by name : " + reseller.getResellerName() + " " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateResellerFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

//    @ApiOperation(value = "Add new Manage balance")
//    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
//    @PostMapping("/addManageBalance")
//    public ResponseEntity<Map<String, Object>> addManageBalance(@RequestBody Reseller manageBalance, @RequestParam(name = "remark", required = true) String remark,
//                                                                @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        ResponseEntity responseEntity = null;
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_CREATE);
//        try {
//            //ManageBalance resellerVo =
//            resellerService.saveManageBalance(manageBalance, remark, mvnoId);
//            Integer responseCode = APIConstants.SUCCESS;
//            // response.put(RESELLER, resellerVo);
//            responseEntity = apiResponseController.apiResponse(responseCode, response);
//            response.put(APIConstants.MESSAGE, "Reseller Balance has been updated successfully.");
//            log.info("Reseller Balance has been updated successfully by " + MDC.get("userName"));
//            snmpCounters.incrementCreateManageBalanceForResellerSuccess();
//            return apiResponseController.apiResponse(responseCode, response);
//        } catch (Exception e) {
//            log.error("Error while manage balance of reseller : " + manageBalance.getResellerName() + " " + e.getMessage());
//            Integer responseCode = APIConstants.FAIL;
//            responseEntity = apiResponseController.apiResponse(responseCode, response);
//            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
//            snmpCounters.incrementCreateManageBalanceForResellerFailure();
//            return apiResponseController.apiResponse(responseCode, response);
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//    }

//    @ApiOperation(value = "Add new Reseller balance")
//    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
//    @PostMapping("/addBalance")
//    public ResponseEntity<Map<String, Object>> addManageBalance(@RequestBody AddBalance addBalance,
//                                                                @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        ResponseEntity responseEntity = null;
//
//
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_CREATE);
//        try {
//            //ManageBalance resellerVo =
//            resellerService.saveAddBalance(addBalance, mvnoId);
//            Integer responseCode = APIConstants.SUCCESS;
//            responseEntity = apiResponseController.apiResponse(responseCode, response);
//            // response.put(RESELLER, resellerVo);
//            response.put(APIConstants.MESSAGE, "Reseller Balance has been updated successfully.");
//            log.info("Reseller Balance has been updated successfully by " + MDC.get("userName"));
//            snmpCounters.incrementCreateAddBalanceForResellerSuccess();
//            return apiResponseController.apiResponse(responseCode, response);
//        } catch (Exception e) {
//            log.error("Error while manage balance of reseller" + e.getMessage());
//            Integer responseCode = APIConstants.FAIL;
//            responseEntity = apiResponseController.apiResponse(responseCode, response);
//            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
//            snmpCounters.incrementCreateAddBalanceForResellerFailure();
//            return apiResponseController.apiResponse(responseCode, response);
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//    }

    @ApiOperation(value = "Update existing reseller")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
    @PutMapping("/updateReseller")
    public ResponseEntity<Map<String, Object>> updateReseller(@RequestBody Reseller reseller,
                                                              @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Reseller resellerVo = resellerService.updateReseller(reseller, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(RESELLER, resellerVo);
            response.put(APIConstants.MESSAGE, "Reseller has been updated successfully.");
            snmpCounters.incrementUpdateResellerSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateResellerFailure();
            return apiResponseController.apiResponse(responseCode, response);
        }
    }

    @ApiOperation(value = "Delete existing reseller based on the given reseller id")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','deleteAccess',#request.getHeader('requestFrom'))")
    @DeleteMapping("/deleteReseller")
    public ResponseEntity<Map<String, Object>> deleteReseller(
            @RequestParam(name = "resellerId", required = true) Long resellerId,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            resellerService.deleteResellerById(resellerId, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, "Reseller has been deleted successfully.");
            snmpCounters.incrementDeleteResellerSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementDeleteResellerFailure();
            return apiResponseController.apiResponse(responseCode, response);
        }
    }

    @ApiOperation(value = "Get list of resellers in the system by reseller name")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','readAccess',#request.getHeader('requestFrom'))")
    @GetMapping("/searchResellers")
    public ResponseEntity<Map<String, Object>> searchResellers(
            @RequestParam(name = "resellerName", required = false) String resellerName,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            List<Reseller> resellerList = resellerService.searchResellers(resellerName, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(RESELLER_LIST, resellerList);
//			log.debug("Request For Fetch All resellers");
            log.debug("Reseller's has been fetched successfully of name '" + resellerName + "' by "
                    + MDC.get("username"));
            snmpCounters.incrementSearchResellerListSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while fetching reseller by name '" + resellerName + "' - " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSearchResellerListFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

//    @PostMapping("/login")
//    @ApiOperation(value = "Login reseller based on the given Username and Password.")
//    public ResponseEntity<Map<String, Object>> validateCustomer(@RequestBody LoginDto login,
//                                                                @RequestParam(name = "mvnoId", required = true) Long mvnoId)
////			@RequestParam(name = "cid", required = false) String cid,
////			@RequestParam(name = "mac", required = false) String mac)
//    {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_LOGIN);
//        try {
//            response.put("Reseller", resellerService.validateLoginUser(login, mvnoId));
////			response.put("Reseller", resellerService.validateLoginUser(login, mvnoId, cid, mac));
//            response.put(APIConstants.MESSAGE, "Reseller Login successfully.");
//            log.debug("Reseller Login successfully : '" + login.getUserName() + "'");
//            snmpCounters.incrementValidLoginUserForResellerSuccess();
//            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Error while login '" + login.getUserName() + "' : " + e.getMessage());
//            ResponseEntity responseEntity = null;
//            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
//            apiResponseController.buildErrorMessageForResponse(response, e);
//            if (e.getMessage().contains(APIConstants.QUOTA_USED)) {
//                response.put(APIConstants.ERROR_MESSAGE, "Your current quota is consumed and can not login");
//                responseEntity = apiResponseController.apiResponse(404, response);
//            } else if (e.getMessage().contains("Inactive")) {
//                responseEntity = apiResponseController.apiResponse(404, response);
//            } else if (e.getMessage().contains("Expired")) {
//                response.put(APIConstants.ERROR_MESSAGE, "User is expired");
//                responseEntity = apiResponseController.apiResponse(404, response);
//            } else {
//                responseEntity = apiResponseController.apiResponse(APIConstants.FAIL, response);
//            }
//            snmpCounters.incrementValidLoginUserForResellerFailure();
//            return responseEntity;
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//    }

//    @ApiOperation(value = "Update reseller password")
//    @PutMapping("/changePassword")
//    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody ResellerChangePasswordDto passwordDto,
//                                                              @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
//        Map<String, Object> response = new HashMap<>();
//        try {
//            resellerService.changePassword(passwordDto, mvnoId);
//            response.put(APIConstants.MESSAGE, "Password has been updated successfully.");
//            log.debug("Reseller password successfully updated: " + passwordDto.getUsername());
//            snmpCounters.incrementChangeResellerPasswordSuccess();
//            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
//        } catch (Exception e) {
//            log.error("Error while change reseller password " + passwordDto.getUsername() + " " + e.getMessage());
//            apiResponseController.buildErrorMessageForResponse(response, e);
//            snmpCounters.incrementChangeResellerPasswordFailure();
//            return apiResponseController.apiResponse(APIConstants.FAIL, response);
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//    }

    /*
     * @GetMapping("/logout")
     *
     * @ApiOperation(value = "Logout reseller based on the given Username") public
     * ResponseEntity<Map<String, Object>> validateCustomer(@RequestParam(name =
     * "userName") String userName,
     *
     * @RequestParam(name = "mvnoId", required = true) Long mvnoId) { Map<String,
     * Object> response = new HashMap<>(); MDC.put(APIConstants.TYPE,
     * APIConstants.TYPE_LOGIN); try { resellerService.validateLogoutUser(userName,
     * mvnoId); response.put(APIConstants.MESSAGE,
     * "Reseller Logout successfully.");
     * log.debug("Reseller Logout successfully : '" + userName +"'"); return
     * apiResponseController.apiResponse(APIConstants.SUCCESS, response); } catch
     * (Exception e) { log.error("Error while logout : '" + userName + "' : " +
     * e.getMessage()); response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
     * apiResponseController.buildErrorMessageForResponse(response, e); return
     * apiResponseController.apiResponse(APIConstants.FAIL, response); } finally {
     * MDC.remove(APIConstants.TYPE); } }
     */

    /*@ApiOperation(value = "Get list of plans in the system by location id and mvno id")
    @GetMapping("/searchPlanByLocationIdAndMvnoId")
    @PreAuthorize("@roleAccesses.hasPermission('plan','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> searchPlanByLocationAndMvnoId(
            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
            @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            List<Plan> planList = planService.searchByLocationIdAndMvnoId(locationId, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            log.debug("Request For Fetch All Plans");
            response.put(PLAN_LIST, planList);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error("Error while fetch plans: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }
*/
//    @ApiOperation(value = "Get list of customers in the system by location id and mvno id")
//    @GetMapping("/searchCustomerByLocationAndMvnoId")
//    @PreAuthorize("@roleAccesses.hasPermission('reseller','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> searchCustomerByLocationAndMvnoId(
//            @RequestParam(name = "mvnoId", required = true) Long mvnoId,
//            @RequestParam(name = "locationId", required = false) Long locationId, HttpServletRequest request) {
//        Map<String, Object> response = new HashMap<>();
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
//        try {
//            List<Customer> customerList = customerService.searchCustomerByLocationAndMvnoId(locationId, mvnoId);
//            Integer responseCode = APIConstants.SUCCESS;
//            log.debug("Request For Fetch All Customers by location and mvnoId");
//            response.put(CUSTOMER_LIST, customerList);
//            return apiResponseController.apiResponse(responseCode, response);
//        } catch (Exception e) {
//            Integer responseCode = APIConstants.FAIL;
//            log.error("Error while fetch plans: " + e.getMessage());
//            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
//            return apiResponseController.apiResponse(responseCode, response);
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//    }

    @ApiOperation(value = "Get list of resellers in the system by locationId")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','readAccess',#request.getHeader('requestFrom'))")
    @GetMapping("/searchResellersByLocationId")
    public ResponseEntity<Map<String, Object>> searchResellersByLocationId(
            @RequestParam(name = "locationId", required = false) Long locationId,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            List<Reseller> resellerList = resellerService.searchResellersByLocationId(locationId, mvnoId);
            Integer responseCode = APIConstants.SUCCESS;
            if (resellerList.isEmpty()) {
                throw new RuntimeException("There is no resellers find with the given Location");
            }
            response.put(RESELLER_LIST, resellerList);
            log.debug("Reseller's has been fetched successfully of lo '" + "" + "' by "
                    + MDC.get("username"));
            snmpCounters.incrementSearchResellerByLocationIdListSuccess();
            return apiResponseController.apiResponse(responseCode, response);

        } catch (Exception e) {
            log.error("Error while fetching reseller by location '" + "" + "' - " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSearchResellerByLocationIdListFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }

    }

    @ApiOperation(value = "Change status of  reseller based on the given reseller id")
    @GetMapping("/changeStatus")
    @PreAuthorize("@roleAccesses.hasPermission('reseller','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> changeStatus(
            @RequestParam(name = "resellerId", required = true) Long policyId,
            @RequestParam(name = "status", required = true) String status,
            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            String msg = resellerService.changeStatus(policyId, status, mvnoId);
            response.put(APIConstants.MESSAGE, msg);
            log.info(msg);
            snmpCounters.incrementChangeResellerStatusSuccess();
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
            log.error("Error while changing status of  reseller: " + e.getMessage());
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementChangeResellerStatusFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }


}
