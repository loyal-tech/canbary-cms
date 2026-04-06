package com.adopt.apigw.modules.Region.controller;


import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.Branch.service.BranchService;
import com.adopt.apigw.modules.BusinessUnit.controller.BusinessUnitController;
import com.adopt.apigw.modules.Region.Mapper.RegionMapper;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.Region.model.RegionDTO;
import com.adopt.apigw.modules.Region.service.RegionService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.stylesheets.LinkStyle;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.Region)
public class RegionController { //extends ExBaseAbstractController<RegionDTO> {
//
//    @Autowired
//    AuditLogService auditLogService;
//    private static String MODULE = " [BusinessVerticalsController] ";
//
//    @Autowired
//    private MessageSender messageSender;
//
//    private static final Logger logger= LoggerFactory.getLogger(BusinessUnitController.class);
//    @Autowired
//    RegionService regionService;
//
//
//    @Autowired
//    CreateDataSharedService createDataSharedService;
//
//
//    @Autowired
//    RegionMapper regionMapper;
//
//    public RegionController(RegionService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[BusinessVerticalController]";
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody RegionDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Create");
//        boolean flag = regionService.duplicateVerifyAtSave(entityDTO.getRname());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag /*&& flagforUcode*/) {
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
//            Region region = new Region();
//
//            region = regionMapper.dtoToDomain(regionDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.sendEntitySaveDataForAllMicroService(region);
//            logger.info("Region created Successfully With name "+ entityDTO.getRname()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } else{
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.REGION_NAME_EXITS);
//            logger.error("Unable to Create Region with region name " +entityDTO.getRname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody RegionDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        String oldname=regionService.getById(entityDTO.getId()).getRname();
//        org.slf4j.MDC.put("type", "Update");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = regionService.duplicateVerifyAtEdit(entityDTO.getRname(), entityDTO.getId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            RegionDTO businessVerticalsDTO = (RegionDTO) dataDTO.getData();
//            Region region = new Region();
//            region = regionMapper.dtoToDomain(businessVerticalsDTO,new CycleAvoidingMappingContext());
//            createDataSharedService.updateEntityDataForAllMicroService(region);
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.SAME_REGION_ALREADY_EXITS);
//        }
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody RegionDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        org.slf4j.MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = regionService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//            RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
//            if (regionDTO != null) {
//
//                Region region = new Region();
//                region.setIsDeleted(true);
//                region = regionMapper.dtoToDomain(regionDTO,new CycleAvoidingMappingContext());
//                createDataSharedService.updateEntityDataForAllMicroService(region);
//                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//                logger.info("Region  With name " + entityDTO.getRname() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//            }
//         }else {
//                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//                dataDTO.setResponseMessage(DeleteContant.REGION_DELETE_EXIST);
//                logger.error("Unable to Delete Region With name: " + entityDTO.getRname() + "  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), HttpStatus.NOT_ACCEPTABLE.value());
//            }
//
//            org.slf4j.MDC.remove("type");
//        return dataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination () {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<RegionDTO> list = regionService.getAllEntities().stream().filter(regionDTO -> !regionDTO.getIsDeleted() && regionDTO.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//
//        }
//
//        return genericDataDTO;
//
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
//    @Override
//    public GenericDataDTO getEntityById (@PathVariable String id, HttpServletRequest req) throws Exception {
//        org.slf4j.MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        RegionDTO regionDTO = (RegionDTO) dataDTO.getData();
//       // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//               // AclConstants.OPERATION_BUSINESS_VERTICALS_VIEW, req.getRemoteAddr(), null, businessUnitDTO.getId(), businessUnitDTO.getVname());
//        org.slf4j.MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll (@RequestBody PaginationRequestDTO requestDTO){
//
//        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                genericDataDTO = regionService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//
//            else
//                genericDataDTO = regionService.search(requestDTO.getFilters()
//                        , requestDTO.getPage(), requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//
//
//            if (null != genericDataDTO) {
//                //logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                //logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to fetch all Entities   :  request: { Module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        return genericDataDTO;
//
//       // return super.getAll(requestDTO);
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REGION_ALL + "\",\"" + AclConstants.OPERATION_REGION_VIEW + "\")")
//    public GenericDataDTO search (@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String
//                                          sortBy, @RequestParam List<GenericSearchModel> filterList){
//        return regionService.search(filterList,page,pageSize,sortBy,sortOrder);
//    }
//    @PostMapping("/getAllRegionByBranchId")
//    public GenericDataDTO getAllRegionByServiceArea(@RequestBody List<Long> branchId, HttpServletRequest req){
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            RegionService regionService = SpringContext.getBean(RegionService.class);
//            genericDataDTO.setDataList(regionService.getAllRegionByServiceArea(branchId));
//            logger.info("Fetching Branch list  :  request: { MODULE : {}}; Response : {{}}", MODULE, APIConstants.SUCCESS);
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Region list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}",HttpStatus.METHOD_NOT_ALLOWED.value(),APIConstants.FAIL,ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
}
