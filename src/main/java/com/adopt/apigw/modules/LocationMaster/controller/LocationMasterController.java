package com.adopt.apigw.modules.LocationMaster.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterMappingDto;
import com.adopt.apigw.modules.LocationMaster.module.UpdateLocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.service.LocationMasterService;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Voucher.module.APIResponseController;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api(value = "LocationMaster", description = "REST APIs related to LocationMaster Entity!!!!", tags = "LocationMaster")
@RestController
@RequestMapping(UrlConstants.BASE_API_URL+"/LocationMaster")
public class LocationMasterController {

    private static String MODULE = " [LocationMasterController] ";
    private final Logger log = LoggerFactory.getLogger(LocationMasterController.class);
    private static final String LOCATIONMASTER_LIST = "locationMasterList";
    private static final String LOCATIONMASTER = "locationMaster";

    @Autowired
    LocationMasterService locationMasterService;
    @Autowired
    APIResponseController apiResponseController;
    @Autowired
    private Tracer tracer;

    private final SNMPCounters snmpCounters = new SNMPCounters();

    @ApiOperation(value = "Get list of locationMaster in the system")
    @GetMapping("/getAllLocationMaster")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findAllLocationMaster(@RequestParam(name = "name")String name , PaginationDTO paginationDTO, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PageableResponse<LocationMaster> pageableResponse = locationMasterService.findAllLocationMaster(getLoggedInUser().getMvnoId().longValue(), name, paginationDTO);
            Integer responseCode = 0;
            if (pageableResponse.getData().isEmpty()) {
                responseCode = APIConstants.NULL_VALUE;
               if (!StringUtils.isEmpty(name)){
                    response.put(APIConstants.ERROR_MESSAGE, "No record found for location name: "+ name);
                   responseCode = APIConstants.NULL_VALUE;
                   log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Location Master"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementSearchLocationByNameFailure();
               } else {
                   response.put("status" ,APIConstants.NO_CONTENT_FOUND);
                   response.put("message","No Records Found");
                   responseCode = APIConstants.NULL_VALUE;
                   log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Location Master" + LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                    snmpCounters.incrementGetLocationListFailure();
               }
                return apiResponseController.apiResponse(HttpStatus.NO_CONTENT.value(),response);
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put(LOCATIONMASTER_LIST, pageableResponse);
                if (!StringUtils.isEmpty(name)) {
                    log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Location Master" + LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE +responseCode);
                    snmpCounters.incrementSearchLocationByNameSuccess();
                } else {
                   // log.debug("Request to Fetch All LocationMaster records by : " + MDC.get("username"));
                    snmpCounters.incrementGetLocationListSuccess();
                }
            }
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Location Master" + LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE +responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            if(!StringUtils.isBlank(name))
                snmpCounters.incrementSearchLocationByNameFailure();
            else
                snmpCounters.incrementGetLocationListFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get LocationMaste based on the given Location id")
    @GetMapping("/findLocationMasterById")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findlocationMasterById(
            @RequestParam(name = "locationMasterId", required = true) Long locationMasterId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
        try {
            LocationMaster locationM = locationMasterService.findlocationMasterById(locationMasterId, getLoggedInUser().getMvnoId().longValue());
            Integer responseCode = APIConstants.SUCCESS;
            response.put(LOCATIONMASTER, locationM);
            snmpCounters.incrementSearchLocationByIdSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.error("Error while fetch locations by Id: " + locationMasterId + " " + e.getMessage());
            Integer responseCode = APIConstants.FAIL;
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSearchLocationByIdFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @ApiOperation(value = "Get list of locations based on the given location name")
    @GetMapping("/findLocation")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findLocation(
            @RequestParam(name = "name", required = false) String name, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            List<LocationMaster> locationList = locationMasterService.findLocation(name, getLoggedInUser().getMvnoId().longValue());
            Integer responseCode = 0;
            if (locationList.isEmpty()) {
                responseCode = APIConstants.NULL_VALUE;
                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch all active locations"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementSearchLocationByNameFailure();
            } else {
                responseCode = APIConstants.SUCCESS;
                response.put(LOCATIONMASTER_LIST, locationList);
                log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch all active locations"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
                snmpCounters.incrementSearchLocationByNameSuccess();
            }
            response.put(LOCATIONMASTER_LIST, locationList);
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all active locations"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementSearchLocationByNameFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Get list of Active location in the system")
    @GetMapping("/activeLocation")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','readAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> findActiveLocation(
            String name, PaginationDTO paginationDTO, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            PageableResponse<LocationMaster> pageableResponse = locationMasterService.findAllLocationMaster(getLoggedInUser().getMvnoId().longValue(), name, paginationDTO);
            List<LocationMaster> locationList = pageableResponse.getData();
//            List<LocationMaster> activeLocationList = new ArrayList<>();
              locationList.stream().filter(activeList->activeList.getStatus().equalsIgnoreCase("Active")).collect(Collectors.toList());
//            for (LocationMaster activeLocation : locationList) {
//                if (activeLocation.getStatus().equals("Active")) {
//                    activeLocationList.add(activeLocation);
//                }
//            }
            response.put(LOCATIONMASTER_LIST, locationList);

            Integer responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + request.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch all active locations"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            snmpCounters.incrementGetActiveLocationListSuccess();
            return apiResponseController.apiResponse(APIConstants.SUCCESS, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM + request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch all active locations"+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementGetActiveLocationListFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Add new LocationMaster")
    @PostMapping("/addLocationMaster")
//    @PreAuthorize("@roleAccesses.hasermission('locationMaster','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> addLocationMaster(@RequestBody LocationMasterDto locationMaster,
                                                                 HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        Integer responseCode = APIConstants.FAIL;
        MDC.put("type", "Create");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            long mvnoId = getLoggedInUser().getMvnoId().longValue() == 1
                    ? locationMaster.getMvnoName().longValue()
                    : getLoggedInUser().getMvnoId().longValue();
            LocationMaster locationM = locationMasterService.saveLocationMaster(locationMaster, mvnoId);
            responseCode = APIConstants.SUCCESS;
            response.put(LOCATIONMASTER, locationM);
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Location Master"+LogConstants.LOG_BY_NAME+locationMaster.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.MESSAGE, "LocationMaster has been added successfully.");
            snmpCounters.incrementCreateLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Create Location Master"+LogConstants.LOG_BY_NAME+locationMaster.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementCreateLocationFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Update existing location data")
    @PutMapping("/updateLocation")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateLocation(@RequestBody UpdateLocationMasterDto locationDto,
                                                              HttpServletRequest request) {
        TraceContext traceContext = tracer.currentSpan().context();
        Integer RESP_CODE = APIConstants.SUCCESS;
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        LocationMasterDto locationMasterDto = new LocationMasterDto();
        Map<String, Object> response = new HashMap<>();
        try {
            LocationMaster locationM = locationMasterService.updateLocation(locationDto, getLoggedInUser().getMvnoId().longValue());
            Integer responseCode = APIConstants.SUCCESS;
            response.put(LOCATIONMASTER, locationM);
            RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Location master"+LogConstants.LOG_BY_NAME+locationMasterDto.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.MESSAGE, "Location has been updated successfully.");
            snmpCounters.incrementUpdateLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            RESP_CODE = HttpStatus.EXPECTATION_FAILED.value();
            Integer responseCode = APIConstants.FAIL;
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update Location master"+LogConstants.LOG_BY_NAME+locationMasterDto.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementUpdateLocationFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @ApiOperation(value = "Delete location master based on the given locationMaster id")
    @DeleteMapping("/deleteLocation")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','deleteAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> deleteLocation(
            @RequestParam(name = "locationMasterId", required = true) Long locationMasterId, HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        LocationMasterDto locationMasterDto = new LocationMasterDto();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            locationMasterService.deleteLocationById(locationMasterId, getLoggedInUser().getMvnoId().longValue());
            Integer responseCode = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete Location master"+LogConstants.LOG_BY_NAME+locationMasterDto.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.MESSAGE, "Location has been deleted successfully.");
            snmpCounters.incrementDeleteLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);
        } catch (Exception e) {
            Integer responseCode = APIConstants.FAIL;
            MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
            log.error(LogConstants.REQUEST_FROM+ request.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete Location master"+LogConstants.LOG_BY_NAME+locationMasterDto.getName()+ LogConstants.REQUEST_BY +  getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + responseCode);
            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
            snmpCounters.incrementDeleteLocationFailure();
            return apiResponseController.apiResponse(responseCode, response);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @GetMapping("/updateLocationStatus")
    @ApiOperation(value = "Update location status based on the given name and status")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> updateLocationStatus(@RequestParam(name = "name", required = true) String name, @RequestParam(name = "status", required = true) String status, HttpServletRequest request) {

        Map<String, Object> response = new HashMap<>();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, request.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);

        try {

            String message = locationMasterService.updateLocationStatus(name, status, getLoggedInUser().getMvnoId().longValue());
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, message);
            log.debug(message);
            snmpCounters.incrementChangeStatusLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while changing wifi location status " + name + " " + e.getMessage());
            snmpCounters.incrementChangeStatusLocationFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);

        } finally {
            MDC.remove(APIConstants.TYPE);
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
    }

    @GetMapping("/getMacFromLocations")
    @ApiOperation(value = "Update location status based on the given name and status")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllMacFromLocations(
            @RequestParam(name = "locationId", required = true) List<Long> locationId,
            @RequestParam(name = "isParentLocation", required = false) boolean isParentLocation) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);

        try {
            List<LocationMasterMappingDto> message = locationMasterService.getAllMacFromLocations(locationId, isParentLocation);
            Integer responseCode = APIConstants.SUCCESS;
            response.put(APIConstants.MESSAGE, message);
//            log.debug(message);
            log.debug("message"+ message);
            snmpCounters.incrementChangeStatusLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while fetching Location Macs " + e.getMessage());
            snmpCounters.incrementChangeStatusLocationFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);

        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @GetMapping("/getLocationFromMac")
    @ApiOperation(value = "get Location from map")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','createUpdateAccess',#request.getHeader('requestFrom'))")
    public ResponseEntity<Map<String, Object>> getAllMacFromLocations(
            @RequestParam(name = "mac", required = true) String mac) {

        Map<String, Object> response = new HashMap<>();
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);

        try {
            List<LocationMaster> locationMasters = locationMasterService.getLocationFromMac(mac);
            Integer responseCode = APIConstants.SUCCESS;
            response.put("locationList", locationMasters);
//            log.debug(locationMasters);
            snmpCounters.incrementChangeStatusLocationSuccess();
            return apiResponseController.apiResponse(responseCode, response);

        } catch (Exception e) {
            apiResponseController.buildErrorMessageForResponse(response, e);
            log.error("Error while fetching Location Macs " + e.getMessage());
            snmpCounters.incrementChangeStatusLocationFailure();
            return apiResponseController.apiResponse(APIConstants.FAIL, response);

        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }
    
//    @ApiOperation(value = "Get list of Active location by plan Id in the system")
//    @GetMapping("/getlocationbyplanid")
//    @PreAuthorize("@roleAccesses.hasPermission('locationMaster','readAccess',#request.getHeader('requestFrom'))")
//    public ResponseEntity<Map<String, Object>> findActiveLocationByPlan(@RequestParam(name = "planId", required = true) Long planId,
//            @RequestParam(name = "mvnoId", required = true) Long mvnoId, HttpServletRequest request) {
//    	Map<String, Object> response = new HashMap<>();
//        MDC.put(APIConstants.TYPE, APIConstants.TYPE_FETCH);
//        try {
//            List<LocationMaster> locationList = locationMasterService.findLocationByPlan(planId,mvnoId);
//            Integer responseCode = 0;
//            if (locationList.isEmpty()) {
//                responseCode = APIConstants.NULL_VALUE;
//                response.put(APIConstants.ERROR_MESSAGE, "No Records Found!");
//                log.info("No LocationMaster Records Found by planId: " + planId );
//                snmpCounters.incrementSearchLocationByNameFailure();
//            } else {
//                responseCode = APIConstants.SUCCESS;
//                response.put(LOCATIONMASTER_LIST, locationList);
//                log.info("Request to Fetch All LocationMaster records by planId: " + planId );
//                snmpCounters.incrementSearchLocationByNameSuccess();
//            }
//            response.put(LOCATIONMASTER_LIST, locationList);
//            return apiResponseController.apiResponse(responseCode, response);
//        }
//        catch (Exception e) {
//            log.error("Error while fetch locations by planId: " + planId + " "
//                    + e.getMessage());
//            Integer responseCode = APIConstants.FAIL;
//            response.put(APIConstants.ERROR_MESSAGE, e.getMessage());
//            snmpCounters.incrementSearchLocationByNameFailure();
//            return apiResponseController.apiResponse(responseCode, response);
//        } finally {
//            MDC.remove(APIConstants.TYPE);
//        }
//
//    }
    

}
