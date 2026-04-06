package com.adopt.apigw.controller.api;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.dto.ValidationData;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.postpaid.Department;
import com.adopt.apigw.pojo.api.DepartmentPojo;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.postpaid.DepartmentService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/department")
public class DepartmentController extends ApiBaseController {
    private static String MODULE = " [DepartmentController] ";
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    DepartmentService departmentService;


//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
//    @PostMapping("/list")
//    public ResponseEntity<?> getDepartmentList(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        Page<Department> departmentList = null;
//        MDC.put("type", "Fetch");
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            departmentList = departmentService.getList(requestDTO.getPage(), requestDTO.getPageSize(), requestDTO.getSortBy(), requestDTO.getSortOrder(), requestDTO.getFilters());
//            if (null != departmentList && 0 < departmentList.getSize())
//                response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList.getContent()));
//            else response.put("departmentList", new ArrayList<>());
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Fetching Department List" + "," + " :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("unable to fetch Department List :" + "," + "," + "  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to fetch Department List " + "," + "," + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ex.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response, departmentList);
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
//    @PostMapping("/search")
//    public ResponseEntity<?> searchDepartment(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req) {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        Page<Department> departmentList = null;
//        MDC.put("type", "Fetch");
//        try {
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            ValidationData validationData = validateSearchCriteria(requestDTO.getFilters());
//            if (validationData.isValid()) {
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                response.put(APIConstants.ERROR_TAG, validationData.getMessage());
//                logger.error("Unable to search department " + "," + "," + " :  request: { From : {}}; Response : {{}};Error :{};}", req.getHeader("requestFrom"), RESP_CODE, response);
//                return apiResponse(RESP_CODE, response);
//            }
//            departmentList = departmentService.search(requestDTO.getFilters(), requestDTO.getPage(), requestDTO.getPageSize(),
//                    requestDTO.getSortBy(), requestDTO.getSortOrder());
//            Integer Response = 0;
//            if (departmentList.isEmpty()) {
//                Response = APIConstants.NULL_VALUE;
//                response.put(APIConstants.MESSAGE, "No Records Found!");
//
//                response.put("departmentList", new ArrayList<>());
//
//                logger.info("Unable to search department " + "," + " :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), RESP_CODE, response);
//                return apiResponse(Response, response, departmentList);
//
//            }
//            if (null != departmentList && 0 < departmentList.getSize()) {
//                response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList.getContent()));
//            } else {
//                response.put("departmentList", new ArrayList<>());
//            }
//            logger.info("Department list is succesfully fetched " + "," + "  : request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Unable to search " + "," + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ce.getStackTrace());
//        } catch (RuntimeException re) {
//            re.printStackTrace();
//            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//            response.put(APIConstants.ERROR_TAG, re.getMessage());
//            logger.error("Unable to search department " + "," + ": request: { From : {}}; Response : {{}};Error :{}; Exception {} ", req.getHeader("requestFrom"), RESP_CODE, response, re.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Unable to search department " + "," + ":  request: { From : {}}; Response : {{}};Error :{};Exception: {}", req.getHeader("requestFrom"), requestDTO, RESP_CODE, response, e.getStackTrace());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response, departmentList);
//    }
//
//    @GetMapping("/all")
//    public ResponseEntity<?> getAllDepartmentList(HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "Fetch");
//        try {
//            List<Department> departmentList = departmentService.getAllActiveEntities();
//            response.put("departmentList", departmentService.convertResponseModelIntoPojo(departmentList).stream()
//                    .sorted(Comparator.comparing(DepartmentPojo::getId).reversed()).collect(Collectors.toList()));
//            RESP_CODE = APIConstants.SUCCESS;
//            logger.info("Department List is fetched " + "," + " :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Error While Fetching Department list " + "," + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Error While Fetching  Department list " + "," + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ex.getStackTrace());
//        }
//
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_VIEW + "\")")
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getDepartmentById(@PathVariable Integer id, HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "Fetch");
//        try {
//            Department department = departmentService.get(id);
//            if (department == null) {
//                RESP_CODE = APIConstants.NOT_FOUND;
//                response.put(APIConstants.ERROR_TAG, "Department Not Found!");
//                logger.info("Unable to fetch department with name " + department.getName() + ": " + "," + "  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"), RESP_CODE, response);
//                return apiResponse(RESP_CODE, response);
//            } else {
//                response.put("departmentData", departmentService.convertDepartmentModelToDepartmentPojo(department));
//                RESP_CODE = APIConstants.SUCCESS;
//                logger.info("Department details Found with name " + department.getName() + " : " + "," + " request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), req.getHeader("requestFrom"), RESP_CODE);
//
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Error While Featching :" + "," + "  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ce.getStackTrace());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Error While Featching " + "," + ":  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, response, ex.getStackTrace());
//        }
//
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_ADD + "\")")
//    @PostMapping("/save")
//    public ResponseEntity<?> createDepartment(@Valid @RequestBody DepartmentPojo pojo, HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "Create");
//
//        try {
//            departmentService.validateRequest(pojo, CommonConstants.OPERATION_ADD);
//            String url = req.getRequestURI();
//            if (pojo.getName().length() > 100) {
//                RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//                logger.error("Error While Creating  Department: " + "," + " request: { From : {}}; Response : {{}};Error : Input size is Exceeded : ", req.getHeader("requestFrom"), RESP_CODE);
//                response.put(APIConstants.ERROR_TAG, "Input size is Exceeded");
//                return apiResponse(RESP_CODE, response, null);
//            } else {
//                pojo = departmentService.save(pojo);
//                response.put("department", pojo);
//                response.put(APIConstants.MESSAGE, "Successfully Created");
//                RESP_CODE = APIConstants.SUCCESS;
//                req.getRequestURL();
//                logger.info("Department details Created with name " + pojo.getName() + " :" + "," + "request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//            }
//        } catch (DataIntegrityViolationException exc) {
//            exc.printStackTrace();
//            RESP_CODE = HttpStatus.NOT_ACCEPTABLE.value();
//            logger.error("Error While Creating  Department with name " + pojo.getName() + ": " + "," + "; request: { From : {}}; Response : {{}};Error {} : ", req.getHeader("requestFrom"), RESP_CODE, exc.getStackTrace());
//            response.put(APIConstants.ERROR_TAG, "Input Size Exceeded");
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//            logger.error("Error While Creating Department: name " + pojo.getName() + " :" + "," + " request: { From : {}}; Response : {{}};Error {} : ", req.getHeader("requestFrom"), RESP_CODE, ce.getStackTrace());
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            logger.error("Error While Creating  Department with name " + pojo.getName() + " :" + "," + " request: { From : {}}; Response : {{}};Error {} : ", req.getHeader("requestFrom"), RESP_CODE, ex.getStackTrace());
//        }
//        //}
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\""
////            + AclConstants.OPERATION_COUNTRY_EDIT + "\")")
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateDepartment(@Valid @RequestBody DepartmentPojo pojo, @PathVariable Integer id,
//                                              HttpServletRequest req) throws Exception {
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "Update");
//        try {
//            pojo.setId(id);
//            departmentService.validateRequest(pojo, CommonConstants.OPERATION_UPDATE);
//
//            pojo = departmentService.update(pojo, req);
//            response.put("department", pojo);
//            response.put(APIConstants.MESSAGE, "Successfully Updated");
//            RESP_CODE = APIConstants.SUCCESS;
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }
//
//    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_COUNTRY_ALL + "\",\"" + AclConstants.OPERATION_COUNTRY_DELETE + "\")")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteDepartment(@PathVariable Integer id, HttpServletRequest req) throws Exception {
//
//        Integer RESP_CODE = APIConstants.FAIL;
//        HashMap<String, Object> response = new HashMap<>();
//        MDC.put("type", "Delete");
//        try {
//            Department department = departmentService.getDepartmentForUpdateAndDelete(id);
//            if (department != null) {
//                DepartmentPojo pojo = departmentService.convertDepartmentModelToDepartmentPojo(department);
//                departmentService.validateRequest(pojo, CommonConstants.OPERATION_DELETE);
//                departmentService.deleteDepartment(id);
//                response.put(APIConstants.MESSAGE, "Successfully deleted");
//                RESP_CODE = APIConstants.SUCCESS;
//                logger.info("Deleting department with Name : " + pojo.getName() + " " + "," + "  request: {From : {}}; Response : {{}}", req.getHeader("requestFrom"), RESP_CODE);
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, "Department Not Found", null);
//            }
//
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            RESP_CODE = ce.getErrCode();
//
//            logger.error("Error While Deleting department with id : " + id + " " + "," + "request: {From : {}}; Response : {{}};Exception : {}", req.getHeader("requestFrom"), RESP_CODE, ce.getStackTrace());
//            response.put(APIConstants.ERROR_TAG, ce.getMessage());
//        } catch (Exception ex) {
//            if (ex instanceof RuntimeException) {
//                RESP_CODE = HttpStatus.METHOD_NOT_ALLOWED.value();
//                logger.error("Error While Deleting department with id : " + id + " " + "," + "request:  {From : {}}; Response : {{}};Exception : {}", req.getHeader("requestFrom"), RESP_CODE, ex.getStackTrace());
//                response.put(APIConstants.ERROR_TAG, ex.getMessage());
//            } else {
//                logger.error("Error While Deleting department with id : " + id + "request " + "," + ": {From : {}}; Response : {{}};Exception : {}", req.getHeader("requestFrom"), RESP_CODE, ex.getStackTrace());
//                ex.printStackTrace();
//                RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
//                response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            }
//        }
//        MDC.remove("type");
//        return apiResponse(RESP_CODE, response);
//    }
//
//    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
//        PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
//        PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
//        SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
//        SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
//        MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
//
//        if (null == requestDTO.getPage()) requestDTO.setPage(PAGE);
//        if (null == requestDTO.getPageSize()) requestDTO.setPageSize(PAGE_SIZE);
//        if (null == requestDTO.getSortBy()) requestDTO.setSortBy(SORT_BY);
//        if (null == requestDTO.getSortOrder()) requestDTO.setSortOrder(SORT_ORDER);
//        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
//            requestDTO.setPageSize(MAX_PAGE_SIZE);
//        return requestDTO;
//    }
//
//    public ValidationData validateSearchCriteria(List<GenericSearchModel> filterList) {
//        ValidationData validationData = new ValidationData();
//        if (null == filterList || 0 < filterList.size()) {
//            validationData.setValid(false);
//            validationData.setMessage("Please Provide Search Criteria");
//            return validationData;
//        }
//        validationData.setValid(true);
//        return validationData;
//    }
}
