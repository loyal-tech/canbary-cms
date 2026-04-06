package com.adopt.apigw.controller.api;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.adopt.apigw.constants.MenuConstants;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.DiscountRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.adopt.apigw.audit.AuditResponse;
import com.adopt.apigw.audit.AuditSearchRequest;
import com.adopt.apigw.audit.AuditService;
import com.adopt.apigw.audit.EntityPojo;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.model.radius.RadiusProfileCheckItem;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.BillRunService;
import com.adopt.apigw.service.postpaid.ChargeService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.CustomerAddressService;
import com.adopt.apigw.service.postpaid.DiscountService;
import com.adopt.apigw.service.postpaid.DunningRuleService;
import com.adopt.apigw.service.postpaid.InvoiceServerService;
import com.adopt.apigw.service.postpaid.LocationService;
import com.adopt.apigw.service.postpaid.PartnerBillRunService;
import com.adopt.apigw.service.postpaid.PartnerCreditDocService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.PlanServiceService;
import com.adopt.apigw.service.postpaid.PostpaidPlanService;
import com.adopt.apigw.service.postpaid.TaxService;
import com.adopt.apigw.service.postpaid.TrialBillRunService;
import com.adopt.apigw.service.postpaid.TrialDebitDocService;
import com.adopt.apigw.service.radius.RadProfileCheckItemService;
import com.adopt.apigw.service.radius.RadiusProfileService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.core.JsonProcessingException;

//@RestController
//@RequestMapping("api/v1")

public class APIController2 extends ApiBaseController {

    private static final Logger logger = LoggerFactory.getLogger(APIController2.class);

    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    PlanServiceRepository planServiceRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    private DiscountRepository discountRepository;

    /*
     * listing - Get /taxes
     * Creation - Post /taxes
     * Updated - Put /taxes/12
     * Deletion - Delete /taxes/12
     */

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            List<StaffUser> dbStaffUserList = staffUserService.getStaffUserFromUsername(pojo.getUsername());
            if (dbStaffUserList == null || dbStaffUserList.size() <= 0) {
                logger.info("Username or Password not matched");
                response.put(CommonConstants.RESPONSE_MESSAGE, "Username or Password not matched");
                RESP_CODE = APIConstants.FAIL;
            } else {
                StaffUser dbstaffuser = (StaffUser) dbStaffUserList.get(0);
                logger.info("Entered Password:" + pojo.getPassword() + ":In DB Password:" + dbstaffuser.getPassword());
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                if (encoder.matches(pojo.getPassword(), dbstaffuser.getPassword())) {
                    logger.info("Login Success");
                    LocalDateTime ldt = LocalDateTime.now();
                    dbstaffuser.setLast_login_time(ldt);
                    dbstaffuser.setFailcount(0);
                    staffUserService.save(dbstaffuser);
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Login Success.");
                    RESP_CODE = APIConstants.SUCCESS;
                } else {
                    logger.info("Password Not Match");
                    int intFailCount = dbstaffuser.getFailcount();
                    intFailCount++;
                    dbstaffuser.setFailcount(intFailCount);
                    staffUserService.save(dbstaffuser);
                    response.put(CommonConstants.RESPONSE_MESSAGE, "Password Not Match");
                    RESP_CODE = APIConstants.FAIL;
                }
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/taxes")
    public ResponseEntity<?> getActiveTaxList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            List<Tax> taxList = taxService.getAllActiveEntities();
            response.put("taxlist", taxService.convertResponseModelIntoPojo(taxList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/taxes")
    public ResponseEntity<?> createTax(@Valid @RequestBody TaxPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            taxService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = taxService.save(pojo);
            response.put("tax", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/taxes/{id}")
    public ResponseEntity<?> updateTax(@Valid @RequestBody TaxPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            pojo.setId(id);
            taxService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = taxService.save(pojo);
            response.put("tax", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/taxes/{id}")
    public ResponseEntity<?> deleteTax(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            TaxService taxService = SpringContext.getBean(TaxService.class);
            Tax tax = taxService.get(id,mvnoId);
            if (tax != null) {
                TaxPojo pojo = taxService.convertTaxModelToTaxPojo(tax);
                taxService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                taxService.deleteTax(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.tax.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/planservice")
    public ResponseEntity<?> getPlanServiceList() throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PlanServiceService planserviceService = SpringContext.getBean(PlanServiceService.class);
            List<PlanService> serviceList = planserviceService.getAllServices();
            response.put("serviceList", planserviceService.convertResponseModelIntoPojo(serviceList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/planservice")
    public ResponseEntity<?> createPlanService(@Valid @RequestBody PlanPojo pojo,@RequestParam("mvnoId") Integer mvnoId) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PlanServiceService planserviceService = SpringContext.getBean(PlanServiceService.class);
            planserviceService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = planserviceService.save(pojo,mvnoId);
            response.put("plan", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/planservice/{id}")
    public ResponseEntity<?> updatePlanService(@Valid @RequestBody PlanPojo pojo, @PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PlanServiceService planserviceService = SpringContext.getBean(PlanServiceService.class);
            pojo.setId(id);
            planserviceService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = planserviceService.save(pojo,mvnoId);
            response.put("plan", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/planservice/{id}")
    public ResponseEntity<?> deletePlanService(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PlanServiceService planserviceService = SpringContext.getBean(PlanServiceService.class);
            PlanService planservice = planServiceRepository.findById(id).get();
            if (planservice != null) {
                PlanPojo pojo = planserviceService.convertPlanServiceModelToPlanServicePojo(planservice);
                planserviceService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                planserviceService.deletePlan(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.plan.service.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/charge")
    public ResponseEntity<?> getChargeList() throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
            response.put("chargelist", chargeService.getAllCharge());
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/charge")
    public ResponseEntity<?> createCharge(@Valid @RequestBody ChargePojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
            chargeService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = chargeService.save(pojo);
            response.put("charge", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/charge/{id}")
    public ResponseEntity<?> updateCharge(@Valid @RequestBody ChargePojo pojo, @PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
            pojo.setId(id);
            chargeService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = chargeService.save(pojo);
            response.put("charge", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/charge/{id}")
    public ResponseEntity<?> deleteCharge(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            ChargeService chargeService = SpringContext.getBean(ChargeService.class);
            Charge charge = chargeService.get(id,mvnoId);
            if (charge != null) {
                ChargePojo pojo = chargeService.convertChargeModelToChargePojo(charge);
                chargeService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                chargeService.deleteCharge(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.charge.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/postpaidplan")
    public ResponseEntity<?> getActivePostpaidPlanList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            response.put("postpaidplanList", postpaidPlanService.convertResponseModelIntoPojo(postpaidPlanService.getAllEntities()));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/postpaidplan")
    public ResponseEntity<?> createPostpaidPlan(@Valid @RequestBody PostpaidPlanPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            postpaidPlanService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = postpaidPlanService.save(pojo);
            response.put("postpaidplan", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/postpaidplan/{id}")
    public ResponseEntity<?> updatePostpaidPlan(@Valid @RequestBody PostpaidPlanPojo pojo, @PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            pojo.setId(id);
            postpaidPlanService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = postpaidPlanService.save(pojo);
            response.put("postpaidplan", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/postpaidplan/{id}")
    public ResponseEntity<?> deletePostpaidPlan(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PostpaidPlanService postpaidPlanService = SpringContext.getBean(PostpaidPlanService.class);
            PostpaidPlan plan = postpaidPlanService.get(id,mvnoId);
            if (plan != null) {
                PostpaidPlanPojo pojo = postpaidPlanService.convertPostpaidPlanModelToPostpaidPlanPojo(plan);
                postpaidPlanService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                postpaidPlanService.deletePostpaidPlan(id,mvnoId);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.postpaid.plan.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

//    @GetMapping("/country")
//    public ResponseEntity<?> getCountryList() throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CountryService countryService = SpringContext.getBean(CountryService.class);
//            List<Country> countryList = countryService.getAllEntities();
//            response.put("countryList", countryService.convertResponseModelIntoPojo(countryList));
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @GetMapping("/country/{id}")
//    public ResponseEntity<?> getCountryById(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CountryService countryService = SpringContext.getBean(CountryService.class);
//            Country country = countryService.get(id);
//            if (country == null) {
//                RESP_CODE = APIConstants.NOT_FOUND;
//            } else {
//                response.put("countryData", countryService.convertCountryModelToCountryPojo(country));
//                RESP_CODE = APIConstants.SUCCESS;
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @PostMapping("/country")
//    public ResponseEntity<?> createCountry(@Valid @RequestBody CountryPojo pojo) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CountryService countryService = SpringContext.getBean(CountryService.class);
//            countryService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = countryService.save(pojo);
//            response.put("country", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @PutMapping("/country/{id}")
//    public ResponseEntity<?> updateCountry(@Valid @RequestBody CountryPojo pojo, @PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CountryService countryService = SpringContext.getBean(CountryService.class);
//            pojo.setId(id);
//            countryService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = countryService.save(pojo);
//            response.put("country", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @DeleteMapping("/country/{id}")
//    public ResponseEntity<?> deleteCountry(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CountryService countryService = SpringContext.getBean(CountryService.class);
//            Country country = countryService.get(id);
//            if (country != null) {
//                CountryPojo pojo = countryService.convertCountryModelToCountryPojo(country);
//                countryService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//                countryService.deleteCountry(id);
//                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.country.deleted"));
//                RESP_CODE = APIConstants.SUCCESS;
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @GetMapping("/state")
//    public ResponseEntity<?> getStateList() throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StateService stateService = SpringContext.getBean(StateService.class);
//            List<State> stateList = stateService.getAllEntities();
//            response.put("stateList", stateService.convertResponseModelIntoPojo(stateList));
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @GetMapping("/state/{id}")
//    public ResponseEntity<?> getStateById(@PathVariable Integer id) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StateService stateService = SpringContext.getBean(StateService.class);
//            State state = stateService.get(id);
//            if (state == null) {
//                RESP_CODE = APIConstants.NOT_FOUND;
//            } else {
//                response.put("stateData", stateService.convertStateModelToStatePojo(state));
//                RESP_CODE = APIConstants.SUCCESS;
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @PostMapping("/state")
//    public ResponseEntity<?> createState(@Valid @RequestBody StatePojo pojo) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StateService stateService = SpringContext.getBean(StateService.class);
//            stateService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = stateService.save(pojo);
//            response.put("state", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @PutMapping("/state/{id}")
//    public ResponseEntity<?> updateState(@Valid @RequestBody StatePojo pojo, @PathVariable Integer id) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StateService stateService = SpringContext.getBean(StateService.class);
//            pojo.setId(id);
//            stateService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = stateService.save(pojo);
//            response.put("state", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @DeleteMapping("/state/{id}")
//    public ResponseEntity<?> deleteState(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StateService stateService = SpringContext.getBean(StateService.class);
//            State state = stateService.get(id);
//            if (state != null) {
//                StatePojo pojo = stateService.convertStateModelToStatePojo(state);
//                stateService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//                stateService.deleteState(id);
//                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.state.deleted"));
//                RESP_CODE = APIConstants.SUCCESS;
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @GetMapping("/city")
//    public ResponseEntity<?> getCityList() throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CityService cityService = SpringContext.getBean(CityService.class);
//            List<City> cityList = cityService.getAllEntities();
//            response.put("cityList", cityService.convertResponseModelIntoPojo(cityList));
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @GetMapping("/city/{id}")
//    public ResponseEntity<?> getCityById(@PathVariable Integer id) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CityService cityService = SpringContext.getBean(CityService.class);
//            City city = cityService.get(id);
//            if (city == null) {
//                RESP_CODE = APIConstants.NOT_FOUND;
//            } else {
//                response.put("data", cityService.convertCityModelToCityPojo(city));
//            }
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @PostMapping("/city")
//    public ResponseEntity<?> createCity(@Valid @RequestBody CityPojo pojo) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CityService cityService = SpringContext.getBean(CityService.class);
//            cityService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
////			cityService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = cityService.save(pojo);
//            response.put("city", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @PutMapping("/city/{id}")
//    public ResponseEntity<?> updateCity(@Valid @RequestBody CityPojo pojo, @PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CityService cityService = SpringContext.getBean(CityService.class);
//            pojo.setId(id);
//            cityService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = cityService.save(pojo);
//            response.put("city", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
//
//    @DeleteMapping("/city/{id}")
//    public ResponseEntity<?> deleteCity(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            CityService cityService = SpringContext.getBean(CityService.class);
//            City city = cityService.get(id);
//            if (city != null) {
//                CityPojo pojo = cityService.convertCityModelToCityPojo(city);
//                cityService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//                cityService.deleteCity(id);
//                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.city.deleted"));
//                RESP_CODE = APIConstants.SUCCESS;
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//            }
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

    @GetMapping("/discounts")
    public ResponseEntity<?> getActiveDiscountList(@RequestParam("mvnoId") Integer mvnoId) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DiscountService discountService = SpringContext.getBean(DiscountService.class);
            List<Discount> discountList = discountService.getAllEntities(mvnoId);
            response.put("discountList", discountService.convertResponseModelIntoPojo(discountList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }


    @PostMapping("/discounts")
    public ResponseEntity<?> createDiscount(@Valid @RequestBody DiscountPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DiscountService discountService = SpringContext.getBean(DiscountService.class);
            discountService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = discountService.save(pojo);
            response.put("discount", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/discounts/{id}")
    public ResponseEntity<?> updateDiscount(@Valid @RequestBody DiscountPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DiscountService discountService = SpringContext.getBean(DiscountService.class);
            pojo.setId(id);
            discountService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = discountService.save(pojo);
            response.put("discount", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/discounts/{id}")
    public ResponseEntity<?> deleteDiscount(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DiscountService discountService = SpringContext.getBean(DiscountService.class);
            Discount discount = discountRepository.findById(id).get();
            if (discount != null) {
                DiscountPojo pojo = discountService.covertDiscountModelToDiscountPojo(discount);
                discountService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                discountService.deleteDiscount(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.discount.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/customers")
    public ResponseEntity<?> getActiveCustomersList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customerService = SpringContext.getBean(CustomersService.class);
            response.put("customerList", customerService.convertResponseModelIntoPojo(customerService.getAllCustomers()));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/customers")
    public ResponseEntity<?> createCustomers(@Valid @RequestBody CustomersPojo pojo,@RequestHeader(value="rf",defaultValue = "bss") String requestFrom) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            customersService.validateRequest(pojo, CommonConstants.OPERATION_ADD);

            pojo = customersService.save(pojo,requestFrom,false);
            response.put("customer", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<?> updateCustomers(@Valid @RequestBody CustomersPojo pojo, @PathVariable Integer id,@RequestHeader(value="rf",defaultValue = "bss") String requestFrom) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            pojo.setId(id);
            customersService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = customersService.save(pojo,requestFrom,true);
            response.put("customer", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<?> deleteCustomers(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            Customers customers = customersService.get(id,getMvnoIdFromCurrentStaff(id));
            if (customers != null) {
                CustomersPojo pojo = customersService.convertCustomersModelToCustomersPojo(customers);
                customersService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                customersService.deleteCustomer(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.customer.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<?> getCustomersById(@PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customersService = SpringContext.getBean(CustomersService.class);
            Customers customers = customersService.get(id,getMvnoIdFromCurrentStaff(getMvnoIdFromCurrentStaff()));
            if (customers == null) {
                RESP_CODE = APIConstants.NOT_FOUND;
            } else {
                response.put("customers", customersService.convertCustomersModelToCustomersPojo(customers));
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }


//	@GetMapping("/customers/{id}")
//	public ResponseEntity<?> getCustomersById(@PathVariable Integer id) throws Exception{
//		Integer RESP_CODE=APIConstants.FAIL;
//		HashMap<String,Object> response = new HashMap<>();
//		try {
//			CustomersService customersService = SpringContext.getBean(CustomersService.class);
//			Customers customers  = customersService.get(id);
//			if(customers != null) {
//				List<Customers> customerList = customersService.findAllByCustomers(customers);
//				response.put("customerList", customersService.convertResponseModelIntoPojo(customerList));
//				RESP_CODE=APIConstants.SUCCESS;
//			}else {
//				RESP_CODE=APIConstants.FAIL;
//				throw new CustomValidationException(APIConstants.FAIL,"Customer is not found",null);
//			}
//
//		}catch(CustomValidationException ce) {
//			ce.printStackTrace();
//			RESP_CODE=ce.getErrCode();
//			response.put(APIConstants.ERROR_TAG, ce.getMessage());
//		}
//		return apiResponse(RESP_CODE,response);
//	}

    @GetMapping("/customeraddress/{custid}")
    public ResponseEntity<?> getCustomerAddressList(@PathVariable Integer custid) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomersService customerService = SpringContext.getBean(CustomersService.class);
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            Customers customer = customerService.get(custid,getMvnoIdFromCurrentStaff(custid));
            if (customer != null) {
                List<CustomerAddress> customerAddressList = customerAddressService.findAllByCustomers(customer);
                response.put("customeraddresslist", customerAddressService.convertResponseModelIntoPojo(customerAddressList));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                RESP_CODE = APIConstants.FAIL;
                throw new CustomValidationException(APIConstants.FAIL, "Customer is not found", null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }


    @PostMapping("/customeraddress")
    public ResponseEntity<?> createCustomerAddress(@Valid @RequestBody CustomerAddressPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            customerAddressService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = customerAddressService.save(pojo);
            response.put("customeraddress", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/customeraddress/{id}")
    public ResponseEntity<?> updateCustomerAddress(@Valid @RequestBody CustomerAddressPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            pojo.setId(id);
            customerAddressService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = customerAddressService.save(pojo);
            response.put("customeraddress", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/customeraddress/{id}")
    public ResponseEntity<?> deleteCustomerAddress(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            CustomerAddress customerAddress = customerAddressService.get(id,mvnoId);
            if (customerAddress != null) {
                CustomerAddressPojo pojo = customerAddressService.convertCustomerAddressModelToCustomerAddressPojo(customerAddress);
                customerAddressService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                customerAddressService.deleteCustomerAddress(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.customer.address.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public HashMap<String, Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        HashMap<String, Object> response = new HashMap<>();
        response.put(APIConstants.ERROR_TAG, errors);
        response.put("status", APIConstants.FAIL);
        response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
        return response;
    }

    @GetMapping("/radiusprofile")
    public ResponseEntity<?> getActiveRadiusProfileList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadiusProfileService radiusProfileService = SpringContext.getBean(RadiusProfileService.class);
            List<RadiusProfile> radiusProfileList = radiusProfileService.getAllActiveEntities();
            response.put("radiusprofileList", radiusProfileService.convertResponseModelIntoPojo(radiusProfileList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/radiusprofile")
    public ResponseEntity<?> createRadiusProfile(@Valid @RequestBody RadiusProfilePojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadiusProfileService radiusProfileService = SpringContext.getBean(RadiusProfileService.class);
            radiusProfileService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = radiusProfileService.save(pojo);
            response.put("radiusprofile", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/radiusprofile/{id}")
    public ResponseEntity<?> updateRadiusProfile(@Valid @RequestBody RadiusProfilePojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadiusProfileService radiusProfileService = SpringContext.getBean(RadiusProfileService.class);
            pojo.setId(id);
            radiusProfileService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = radiusProfileService.save(pojo);
            response.put("radiusprofile", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/radiusprofile/{id}")
    public ResponseEntity<?> deleteRadiusProfile(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadiusProfileService radiusProfileService = SpringContext.getBean(RadiusProfileService.class);
            RadiusProfile radiusProfile = radiusProfileService.get(id,mvnoId);
            if (radiusProfile != null) {
                RadiusProfilePojo pojo = radiusProfileService.convertRadiusProfileModelToRadiusProfilePojo(radiusProfile);
                radiusProfileService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                radiusProfileService.deleteRadiusProfile(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.radiusprofile.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/radiusprofilecheckitem/{radiusprofileid}")
    public ResponseEntity<?> getRadiusProfileCheckItemList(@PathVariable Integer radiusprofileid) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadProfileCheckItemService radProfileCheckItemService = SpringContext.getBean(RadProfileCheckItemService.class);
            List<RadiusProfileCheckItem> radiusProfileCheckItemList = radProfileCheckItemService.findAllByRadiusProfile(radiusprofileid);
            response.put("radiusProfileCheckItemList", radProfileCheckItemService.convertResponseModelIntoPojo(radiusProfileCheckItemList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/radiusprofilecheckitem")
    public ResponseEntity<?> createRadiusProfileCheckItem(@Valid @RequestBody RadiusProfileCheckItemPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadProfileCheckItemService radProfileCheckItemService = SpringContext.getBean(RadProfileCheckItemService.class);
            radProfileCheckItemService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = radProfileCheckItemService.save(pojo);
            response.put("radiusprofilecheckitem", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/radiusprofilecheckitem/{id}")
    public ResponseEntity<?> updateRadiusProfileCheckItem(@Valid @RequestBody RadiusProfileCheckItemPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadProfileCheckItemService radProfileCheckItemService = SpringContext.getBean(RadProfileCheckItemService.class);
            pojo.setId(id);
            radProfileCheckItemService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = radProfileCheckItemService.save(pojo);
            response.put("radiusprofilecheckitem", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/radiusprofilecheckitem/{id}")
    public ResponseEntity<?> deleteRadiusProfileCheckItem(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            RadProfileCheckItemService radProfileCheckItemService = SpringContext.getBean(RadProfileCheckItemService.class);
            RadiusProfileCheckItem radiusProfileCheckItem = radProfileCheckItemService.get(id,mvnoId);
            if (radiusProfileCheckItem != null) {
                RadiusProfileCheckItemPojo pojo = radProfileCheckItemService.convertRadiusProfileCheckItemModelToRadiusProfileCheckItemPojo(radiusProfileCheckItem);
                radProfileCheckItemService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                radProfileCheckItemService.delete(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.radiusprofile.checkitem.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/invoiceserver")
    public ResponseEntity<?> getActiveInvoiceServerList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            InvoiceServerService invoiceServerService = SpringContext.getBean(InvoiceServerService.class);
            List<InvoiceServer> invoiceserverList = invoiceServerService.getAllActiveEntities();

            for (InvoiceServer invoiceServer : invoiceserverList) {
                String webPort = invoiceServer.getWebport();
                String serverIP = invoiceServer.getServerip();
                String strUR = "http://" + serverIP + ":" + webPort + "/billing-engine-1.0/billingprocess/statuscheck";
                String respose = null;
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    respose = restTemplate.getForObject(strUR, String.class);
                } catch (Exception e) {
                    invoiceServer.setStatus("0");
                }
                if (respose != null && respose.contains("{\"responseCode\":\"200\",\"responseMessage\":\"Success\",\"responseObject\":null}")) {
                    invoiceServer.setStatus("1");
                } else {
                    invoiceServer.setStatus("0");
                }
            }
            response.put("invoiceserverList", invoiceServerService.convertResponseModelIntoPojo(invoiceserverList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/invoiceserver")
    public ResponseEntity<?> createInvoiceServer(@Valid @RequestBody InvoiceServerPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();

        String webPort = pojo.getWebport();
        String serverIP = pojo.getServerip();
        String strUR = "http://" + serverIP + ":" + webPort + "/billing-engine-1.0/billingprocess/statuscheck";
        String respose = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            respose = restTemplate.getForObject(strUR, String.class);
            if (respose != null & respose.contains("{\"responseCode\":\"200\",\"responseMessage\":\"Success\",\"responseObject\":null}")) {
                pojo.setStatus("1");
                InvoiceServerService invoiceServerService = SpringContext.getBean(InvoiceServerService.class);
                invoiceServerService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
                pojo = invoiceServerService.save(pojo);
                response.put("invoiceserver", pojo);
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                logger.info("Please Make Sure Invoice Server is running");
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.invoiceserver.server.error"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/invoiceserver/{id}")
    public ResponseEntity<?> updateInvoiceServer(@Valid @RequestBody InvoiceServerPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();

        String webPort = pojo.getWebport();
        String serverIP = pojo.getServerip();
        String strUR = "http://" + serverIP + ":" + webPort + "/billing-engine-1.0/billingprocess/statuscheck";
        String respose = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            respose = restTemplate.getForObject(strUR, String.class);
            if (respose != null & respose.contains("{\"responseCode\":\"200\",\"responseMessage\":\"Success\",\"responseObject\":null}")) {
                pojo.setStatus("1");
                InvoiceServerService invoiceServerService = SpringContext.getBean(InvoiceServerService.class);
                pojo.setId(id);
                invoiceServerService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
                pojo = invoiceServerService.save(pojo);
                response.put("invoiceserver", pojo);
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                logger.info("Please Make Sure Invoice Server is running");
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.invoiceserver.server.error"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/invoiceserver/{id}")
    public ResponseEntity<?> deleteInvoiceServer(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            InvoiceServerService invoiceServerService = SpringContext.getBean(InvoiceServerService.class);
            InvoiceServer invoiceServer = invoiceServerService.get(id,mvnoId);
            if (invoiceServer != null) {
                InvoiceServerPojo pojo = invoiceServerService.convertInvoiceServerModelToInvoiceServerPojo(invoiceServer);
                invoiceServerService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                invoiceServerService.deleteInvoiceServer(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.invoiceserver.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/billrun")
    public ResponseEntity<?> getActiveBillRunList() {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            List<BillRun> billRunList = billRunService.getAllActiveEntities();
            response.put("billRunlist", billRunService.convertResponseModelIntoPojo(billRunList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/billrun/generatepdf")
    public ResponseEntity<?> generatePDF(@RequestParam(name = "bid", defaultValue = "") String billRunId) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            boolean bStatus = billRunService.generateInvoice(billRunId);
            if (bStatus) {
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.generatepdf.success"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.billrun.error"));
                RESP_CODE = APIConstants.FAIL;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/billrun/emailpdf")
    public ResponseEntity<?> emailPdf(@RequestParam(name = "bid", defaultValue = "") String billRunId) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            BillRunService billRunService = SpringContext.getBean(BillRunService.class);
            boolean bStatus = billRunService.emailInvoices(billRunId);
            if (bStatus) {
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.emailpdf.success"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.billrun.error"));
                RESP_CODE = APIConstants.FAIL;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PostMapping("/invoice/search")
//    public ResponseEntity<?> searchInvoice(@ModelAttribute SearchDebitDocsPojo entity, @RequestBody PaginationRequestDTO requestDTO,@RequestParam (name = "isInvoiceVoid",defaultValue = "false") boolean isInvoiceVoid) {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            if (entity != null) {
//                DebitDocService debitDocService = SpringContext.getBean(DebitDocService.class);
//                //requestDTO = setDefaultPaginationValues(requestDTO);
//                Page<DebitDocument> debitDocList = debitDocService.searchInvoice(entity, requestDTO,isInvoiceVoid);
//                if (null != debitDocList && 0 < debitDocList.getSize()) {
//                    response.put("invoicesearchlist", debitDocService
//                            .convertResponseModelIntoPojo(debitDocList.getContent()));
//                } else {
//                    response.put("invoicesearchlist", new ArrayList<>());
//                }
//                RESP_CODE = APIConstants.SUCCESS;
//            }
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
//            response.put(APIConstants.ERROR_TAG, e.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }
@PreAuthorize("validatePermission(\"" + MenuConstants.postpaid_trial_bill_run_master +  "\")")
    @GetMapping("/trial/billrun/search")
    public ResponseEntity<?> searchTrialBillRunHistory(@ModelAttribute SearchTrialBillRunPojo sBillRun) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (sBillRun != null) {
                TrialBillRunService trialBillRunService = SpringContext.getBean(TrialBillRunService.class);
                List<TrialBillRunPojo> trialBillRunList = trialBillRunService.convertResponseModelIntoPojo(trialBillRunService.searchTrialBillRun(sBillRun));
                response.put("trialBillRunList", trialBillRunList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/trial/invoice/search")
    public ResponseEntity<?> searchTrialInvoice(@ModelAttribute SearchTrialDebitDocsPojo entity, @RequestBody PaginationRequestDTO requestDTO) {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (entity != null) {
                TrialDebitDocService trialDebitDocService = SpringContext.getBean(TrialDebitDocService.class);
                Page<TrialDebitDocumentPojo> trialDebitDocPojoList = trialDebitDocService.searchTrialInvoice(entity, requestDTO);
                response.put("trialDebitDocPojoList", trialDebitDocPojoList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            response.put(APIConstants.ERROR_TAG, e.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/partner")
    public ResponseEntity<?> getActivePartnerList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
            List<Partner> partnerlist = partnerService.getAllEntities();
            response.put("partnerlist", partnerService.convertResponseModelIntoPojo(partnerlist));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PostMapping("/partner")
//    public ResponseEntity<?> createPartner(@Valid @RequestBody PartnerPojo pojo) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
//            partnerService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            pojo = partnerService.save(pojo);
//            response.put("partner", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @PutMapping("/partner/{id}")
//    public ResponseEntity<?> updatePartner(@Valid @RequestBody PartnerPojo pojo, @PathVariable Integer id) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
//            pojo.setId(id);
//            partnerService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//            pojo = partnerService.save(pojo);
//            response.put("partner", pojo);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

//    @DeleteMapping("/partner/{id}")
//    public ResponseEntity<?> deletePartner(@PathVariable Integer id) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            PartnerService partnerService = SpringContext.getBean(PartnerService.class);
//            Partner partner = partnerService.get(id);
//            if (partner != null) {
//                PartnerPojo pojo = partnerService.convertPartnerModelToPartnerPojo(partner);
//                partnerService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//                partnerService.deletePartner(id);
//                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.partner.deleted"));
//                RESP_CODE = APIConstants.SUCCESS;
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

    @GetMapping("/partner/billrun/search")
    public ResponseEntity<?> searchPartnerBillRunHistory(@ModelAttribute SearchPartnerBillRunPojo pBillRun) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (pBillRun != null) {
                PartnerBillRunService partnerBillRunService = SpringContext.getBean(PartnerBillRunService.class);
                List<PartnerBillRunPojo> partnerBillRunList = partnerBillRunService.convertResponseModelIntoPojo(partnerBillRunService.searchPartnerBillRun(pBillRun));
                response.put("partnerBillRunList", partnerBillRunList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/partner/commission/search")
    public ResponseEntity<?> searchPartnerCommission(@ModelAttribute SearchPartnerCreditDocsPojo entity) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (entity != null) {
                PartnerCreditDocService partnerCreditDocService = SpringContext.getBean(PartnerCreditDocService.class);
                List<PartnerDebitDocumentPojo> partnerCreditDocumentPojoList = partnerCreditDocService.convertResponseModelIntoPojo(partnerCreditDocService.searchPartnerCommission(entity));
                response.put("partnerCreditDocumentPojoList", partnerCreditDocumentPojoList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/payment/search")
    public ResponseEntity<?> searchPayment(@ModelAttribute SearchPaymentPojo entity,@RequestParam("mvnoId") Integer mvnoId) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            if (entity != null) {
                CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
                List<CreditDocumentPojo> creditDocumentPojoList = creditDocService.convertResponseModelIntoPojo(creditDocService.serachPayment(entity,null,mvnoId).stream().collect(Collectors.toList()));
                response.put("creditDocumentPojoList", creditDocumentPojoList);
                RESP_CODE = APIConstants.SUCCESS;
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/record/payment")
    public ResponseEntity<?> createRecordPayment(@Valid @RequestBody RecordPaymentPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            CreditDocService creditDocService = SpringContext.getBean(CreditDocService.class);
            creditDocService.validateRequest(pojo, CommonConstants.OPERATION_ADD,getMvnoIdFromCurrentStaff(pojo.getCustomerid()));
            pojo = creditDocService.save(pojo, false,false, false,null);
            response.put("recordpayment", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/staffuser")
    public ResponseEntity<?> getAllStaffUsers() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            List<StaffUser> staffUserlist = staffUserService.getAllUsers();
            response.put("staffUserlist", staffUserService.convertResponseModelIntoPojo(staffUserlist));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/staffuser")
    public ResponseEntity<?> createStaffUser(@Valid @RequestBody StaffUserPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            staffUserService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = staffUserService.save(pojo);
            response.put("staffuser", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/staffuser/{id}")
    public ResponseEntity<?> updateStaffUser(@Valid @RequestBody StaffUserPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            pojo.setId(id);
            staffUserService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = staffUserService.save(pojo);
            response.put("staffuser", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/staffuser/{id}")
    public ResponseEntity<?> deleteStaffUser(@PathVariable Integer id) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            StaffUser staffUser = staffUserRepository.findById(id).get();
            if (staffUser != null) {
                StaffUserPojo pojo = staffUserService.convertStaffUserModelToStaffUserPojo(staffUser,staffUser.getMvnoId());
                staffUserService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                staffUserService.deleteStaffUser(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.staffuser.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

//    @PutMapping("/staffuser/changepassword")
//    public ResponseEntity<?> changePassword(@Valid @RequestBody UserPasswordChangePojo pojo) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        try {
//            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
//            staffUserService.changePassword(pojo);
//            response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.staffuser.changepassword.success"));
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        }
//        return apiResponse(RESP_CODE, response);
//    }

    @GetMapping("/accessdenied")
    public ResponseEntity<?> accessDenied() {
        Integer RESP_CODE = APIConstants.FORBIDDEN;
        HashMap<String, Object> response = new HashMap<>();
        response.put(CommonConstants.RESPONSE_MESSAGE, getLoggedInUser().getFirstName() + " you are not authorized");
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/dunningrule")
    public ResponseEntity<?> getDunningRuleList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DunningRuleService dunningRuleService = SpringContext.getBean(DunningRuleService.class);
            response.put("dunningrulelist", dunningRuleService.convertResponseModelIntoPojo(dunningRuleService.getAllEntities()));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/dunningrule")
    public ResponseEntity<?> createDunningRule(@Valid @RequestBody DunningRulePojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DunningRuleService dunningRuleService = SpringContext.getBean(DunningRuleService.class);
            dunningRuleService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = dunningRuleService.save(pojo);
            response.put("dunningrule", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/dunningrule/{id}")
    public ResponseEntity<?> updateDunningRule(@Valid @RequestBody DunningRulePojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DunningRuleService dunningRuleService = SpringContext.getBean(DunningRuleService.class);
            pojo.setId(id);
            dunningRuleService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = dunningRuleService.save(pojo);
            response.put("dunningrule", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/dunningrule/{id}")
    public ResponseEntity<?> deleteDunningRule(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            DunningRuleService dunningRuleService = SpringContext.getBean(DunningRuleService.class);
            DunningRule dunningRule = dunningRuleService.get(id,mvnoId);
            if (dunningRule != null) {
                DunningRulePojo pojo = dunningRuleService.convertDunningRuleModelToDunningRulePojo(dunningRule);
                dunningRuleService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
                dunningRuleService.deleteDunningRule(id,mvnoId);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.dunningrule.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }

        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }
/*
	@PostMapping(value = "/payroll/ndnpayslip",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource>  ndownloadPaySlip(@Valid @RequestBody PaySlipRequest request) throws IOException {
        Resource resource = null;
        FileSystemService service=SpringContext.getBean(FileSystemService.class);
        resource=service.getPayrollFile(request.getEmpid(),request.getYear(), request.getMonth());
    	String contentType = "application/octet-stream";
        if(resource!=null && resource.exists()) {

        	InputStream fileStream = resource.getURL().openStream();

        	HttpHeaders headers = new HttpHeaders();
        	  headers.setContentType(MediaType.parseMediaType("application/pdf"));
        	  headers.add("Access-Control-Allow-Origin", "*");
        	  headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
        	  headers.add("Access-Control-Allow-Headers", "Content-Type");
        	  headers.add("Content-Disposition", "filename=" + resource.getFilename());
        	  headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        	  headers.add("Pragma", "no-cache");
        	  headers.add("Expires", "0");
        	  headers.setContentLength(resource.contentLength());
        	  ResponseEntity<InputStreamResource> response = new ResponseEntity<InputStreamResource>(
        	    new InputStreamResource(fileStream), headers, HttpStatus.OK);
        	  return response;


//        	 return ResponseEntity.ok()
//        	 .contentType(MediaType.parseMediaType(contentType))
//        	 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
//        	 .body(resource);
        }else {
        	return ResponseEntity.notFound().build();
        }
	}

	@PostMapping(value = "/payroll/dnpayslip",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<Resource>  downloadPaySlip(@Valid @RequestBody PaySlipRequest request) throws IOException {
        Resource resource = null;
        FileSystemService service=SpringContext.getBean(FileSystemService.class);
        resource=service.getPayrollFile(request.getEmpid(),request.getYear(), request.getMonth());
    	String contentType = "application/octet-stream";
        if(resource!=null && resource.exists()) {
        	 return ResponseEntity.ok()
        	 .contentType(MediaType.parseMediaType(contentType))
        	 .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
        	 .body(resource);
        }else {
        	return ResponseEntity.notFound().build();
        }
	}
*/

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
        }
        return loggedInUser;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }


    @PostMapping("/audit/snapshotlist")
    public <T> ResponseEntity<?> getAuditSnapshotList(@RequestBody AuditSearchRequest auditSearchRequest) throws ClassNotFoundException {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        //return new ResponseEntity<List<AuditResponse>>(auditService.getCodSnapshot(auditSearchRequest), HttpStatus.OK);
        return new ResponseEntity<String>(auditService.Snapshot(), HttpStatus.OK);

    }

    @PostMapping("/audit/shadowslist")
    public <T> ResponseEntity<?> getAuditShadowList(@RequestBody AuditSearchRequest auditSearchRequest) throws ClassNotFoundException {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        return new ResponseEntity<String>(auditService.getShadowList(auditSearchRequest), HttpStatus.OK);
    }

    @RequestMapping(value = "/entity/shadows", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public <T> ResponseEntity<?> getShadowByEntity(@RequestBody AuditSearchRequest auditSearchRequest) throws ClassNotFoundException, JsonProcessingException {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        return new ResponseEntity<List<AuditResponse>>(auditService.getShadowByEntity(auditSearchRequest), HttpStatus.OK);
    }

    @RequestMapping(value = "/entity/changes", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public <T> ResponseEntity<?> getchangesByEntity(@RequestBody AuditSearchRequest auditSearchRequest) throws ClassNotFoundException, JsonProcessingException {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        return new ResponseEntity<List<AuditResponse>>(auditService.getChangesByEntity(auditSearchRequest), HttpStatus.OK);
    }

    @GetMapping("/user/search")
    public ResponseEntity<?> searchUser(@RequestParam(name = "s", defaultValue = "") String search) throws Exception {
        StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
        HashMap<String, Object> response = new HashMap<>();
        List<StaffUserPojo> user = null;
        if ("".equals(search)) {
            user = staffUserService.convertResponseModelIntoPojo(staffUserService.getAllActiveEntities());
            response.put("items", user);
        } else {
            user = staffUserService.searchUserCustom(search);
            response.put("items", user);
        }
        return apiResponse(APIConstants.SUCCESS, response);
    }

    @GetMapping("/entity")
    public ResponseEntity<?> getAllClass(@RequestParam(name = "s", defaultValue = "") String search) throws Exception {
        AuditService auditService = SpringContext.getBean(AuditService.class);
        HashMap<String, Object> response = new HashMap<>();
        if ("".equals(search)) {
            response.put("items", auditService.getAllEntityPojo());
        } else {
            List<EntityPojo> result = auditService.getAllEntityPojo().stream()
                    .filter(item -> item.getClassName().equalsIgnoreCase(search))
                    .collect(Collectors.toList());
            response.put("items", result);
        }


        return apiResponse(APIConstants.SUCCESS, response);
    }

    @GetMapping("/location")
    public ResponseEntity<?> getLocationList() throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocationService locationService = SpringContext.getBean(LocationService.class);
            List<Location> locationList = locationService.getAllActiveEntities();
            response.put("locationList", locationService.convertResponseModelIntoPojo(locationList));
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PostMapping("/location")
    public ResponseEntity<?> createLocation(@Valid @RequestBody LocationPojo pojo) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocationService locationService = SpringContext.getBean(LocationService.class);
            locationService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
            pojo = locationService.save(pojo);
            response.put("location", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @PutMapping("/location/{id}")
    public ResponseEntity<?> updateLocation(@Valid @RequestBody LocationPojo pojo, @PathVariable Integer id) throws Exception {

        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocationService locationService = SpringContext.getBean(LocationService.class);
            pojo.setId(id);
            locationService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
            pojo = locationService.save(pojo);
            response.put("location", pojo);
            RESP_CODE = APIConstants.SUCCESS;
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @DeleteMapping("/location/{id}")
    public ResponseEntity<?> deleteLocation(@PathVariable Integer id,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        Integer RESP_CODE = APIConstants.FAIL;
        HashMap<String, Object> response = new HashMap<>();
        try {
            LocationService locationService = SpringContext.getBean(LocationService.class);
            Location location = locationService.get(id,mvnoId);
            if (location != null) {
                locationService.deleteLocation(id);
                response.put(CommonConstants.RESPONSE_MESSAGE, messagesProperty.get("api.auth.location.deleted"));
                RESP_CODE = APIConstants.SUCCESS;
            } else {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            RESP_CODE = ce.getErrCode();
            response.put(APIConstants.ERROR_TAG, ce.getMessage());
        }
        return apiResponse(RESP_CODE, response);
    }

    @GetMapping("/gettaxtiergroups")
    public ResponseEntity<?> getTaxTierGroups() throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", UtilsCommon.getTaxGroupMap());
        return apiResponse(APIConstants.SUCCESS, response);
    }

    @GetMapping("/gettaxtypes")
    public ResponseEntity<?> getTaxTypes() throws Exception {
        HashMap<String, Object> response = new HashMap<>();
        response.put("data", UtilsCommon.getTaxTypeMap());
        return apiResponse(APIConstants.SUCCESS, response);
    }

}
