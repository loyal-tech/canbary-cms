package com.adopt.apigw.modules.SectorMaster.Controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.SectorMaster.Domain.SectorMaster;
import com.adopt.apigw.modules.SectorMaster.Mapper.SectorMasterMapper;
import com.adopt.apigw.modules.SectorMaster.Model.SectorMasterDTO;
import com.adopt.apigw.modules.SectorMaster.Service.SectorMasterService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController

@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SECTOR_MASTER)
public class SectorMasterController extends ExBaseAbstractController<SectorMasterDTO> {
    public SectorMasterController(SectorMasterService service) {
        super(service);
    }


    @Autowired
    SectorMasterMapper sectorMasterMapper;
    @Override
    public String getModuleNameForLog() {
        return "[SectorMasterController]";
    }

    private static String MODULE = " [SectorMasterController] ";

    private static final Logger logger = LoggerFactory.getLogger(SectorMasterController.class);

    @Autowired
    SectorMasterService sectorMasterService;
    @Autowired
    private Tracer tracer;

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_CREATE + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody SectorMasterDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != null) {
            // TODO: pass mvnoID manually 6/5/2025
            entityDTO.setMvnoId(mvnoId);
        }
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Create");
        MDC.put("userName", sectorMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            boolean flag = sectorMasterService.duplicateVerifyAtSave(entityDTO.getSname());
            if (flag /*&& flagforUcode*/) {
                dataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
                SectorMasterDTO sectorMasterDTO = (SectorMasterDTO) dataDTO.getData();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.SECTOR_NAME_EXITS);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +MessageConstants.SECTOR_NAME_EXITS+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
            }
        }catch (Exception e){
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +MessageConstants.SECTOR_NAME_EXITS+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {

            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_EDIT + "\")")
    @Override
    @PutMapping("/update")
    public GenericDataDTO update(@Valid @RequestBody SectorMasterDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Update");
        MDC.put("userName", sectorMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId!= null) {
                // TODO: pass mvnoID manually 6/5/2025
                entityDTO.setMvnoId(mvnoId);
            }
    //        String oldname=sectorMasterService.getById(entityDTO.getId()).getSname();
            SectorMaster sectorMasterold = new SectorMaster();
            sectorMasterold = sectorMasterService.getById(entityDTO.getId());
            // TODO: pass mvnoID manually 6/5/2025
            Integer currentMvnoId = mvnoId;
            Integer dataMvnoId = entityDTO.getMvnoId();
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){
                boolean flag = sectorMasterService.duplicateVerifyAtEdit(entityDTO.getSname(), entityDTO.getId());
                if (flag) {
                    String updatedvalues = UtilsCommon.getUpdatedDiff(sectorMasterMapper.domainToDTO(sectorMasterold, new CycleAvoidingMappingContext()), entityDTO);
                    dataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
                    SectorMasterDTO sectorMasterDTO = (SectorMasterDTO) dataDTO.getData();
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Sector " + updatedvalues + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                } else {
                    dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                    dataDTO.setResponseMessage(MessageConstants.SECTOR_NAME_EXITS);
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR+MessageConstants.SECTOR_NAME_EXITS+LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                }
            }else {
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Update Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+"Permission Denied. Unable to Delete the Record"+ LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                return dataDTO;
            }
        }catch (Exception e){
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Create Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody SectorMasterDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Delete");
        MDC.put("userName", sectorMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {

            // TODO: pass mvnoID manually 6/5/2025
            Integer currentMvnoId = getMvnoIdFromCurrentStaff(null);
            Integer dataMvnoId =entityDTO.getMvnoId();
            if(currentMvnoId==1 || dataMvnoId.equals(currentMvnoId)){
                dataDTO = super.delete(entityDTO, authentication, req);
                SectorMasterDTO regionDTO = (SectorMasterDTO) dataDTO.getData();
                if (regionDTO != null) {
                    // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
                    //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete Sector" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                } else {
                    dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                    dataDTO.setResponseMessage(MessageConstants.SECTOR_NAME_EXITS);
                    logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasMAster" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+LogConstants.LOG_ERROR+MessageConstants.SECTOR_NAME_EXITS + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                }
            }else{
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasMAster" + LogConstants.LOG_BY_NAME + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +LogConstants.LOG_ERROR+"Permission Denied. Unable to Delete the Record"+ LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
                return dataDTO;
            }
        }catch (Exception e){
            dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            dataDTO.setResponseMessage(e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Delete CasMaster" + LogConstants.LOG_BY_NAME + entityDTO.getSname() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +e.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_MGMT + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req,@RequestParam  Integer mvnoId) {

        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", sectorMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = sectorMasterService.search(filter.getFilter(), page, pageSize, sortBy,sortOrder,req,mvnoId);
            if (genericDataDTO.getDataList().isEmpty()) {
                genericDataDTO.setResponseCode(APIConstants.FAIL);
                genericDataDTO.setResponseMessage(LogConstants.LOG_NOT_FOUND);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Sector Master by keyword  by keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR +LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            } else {
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage(LogConstants.LOG_SUCCESS);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Sector Master by keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            }
        } catch (CustomValidationException ce) {
            ce.printStackTrace();
            genericDataDTO.setResponseCode(ce.getErrCode());
            genericDataDTO.setResponseMessage(ce.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Sector Master by keyword  by keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ce.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Sector Master by keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {

        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<SectorMasterDTO> list = sectorMasterService.getAllEntities(mvnoId).stream().filter(x -> !x.getIsDeleted()).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());
            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}" + getModuleNameForLog() + genericDataDTO.getResponseCode() + genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}" + getModuleNameForLog() + genericDataDTO.getResponseCode() + genericDataDTO.getResponseCode() + ex.getStackTrace());

        }
        return genericDataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_MGMT + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        SectorMasterDTO sectorMasterDTO = (SectorMasterDTO) dataDTO.getData();
        // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
        // AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getVname());
        MDC.remove("type");
        return dataDTO;
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.SECTOR_MGMT + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", sectorMasterService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);
            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())

                genericDataDTO = sectorMasterService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters(),mvnoId);

            else
                genericDataDTO = sectorMasterService.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder(),mvnoId);


            if (null != genericDataDTO) {
                //logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Sector" + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch All Sector" + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetch Active CasMaster" + LogConstants.REQUEST_BY + sectorMasterService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + APIConstants.FAIL);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;

        // return super.getAll(requestDTO);
    }

}
