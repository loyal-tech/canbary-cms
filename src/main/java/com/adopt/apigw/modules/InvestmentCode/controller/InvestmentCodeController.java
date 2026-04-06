package com.adopt.apigw.modules.InvestmentCode.controller;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.PlanService;
import com.adopt.apigw.model.postpaid.QPlanService;
import com.adopt.apigw.modules.InvestmentCode.DTO.InvestmentCodeDto;
import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import com.adopt.apigw.modules.InvestmentCode.repository.InvestmentCodeRepository;
import com.adopt.apigw.modules.InvestmentCode.service.InvestmentCodeService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.servicePlan.domain.QServices;
import com.adopt.apigw.modules.servicePlan.repository.ServiceRepository;
import com.adopt.apigw.repository.postpaid.PlanServiceRepository;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
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
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.INVESTMENT_CODE)
public class InvestmentCodeController{ // extends ExBaseAbstractController<InvestmentCodeDto> {
//    @Autowired
//    AuditLogService auditLogService;
//
//    @Autowired
//    InvestmentCodeService investmentCodeService;
//
//    @Autowired
//    InvestmentCodeRepository investmentCodeRepository;
//
//    private static final Logger logger= LoggerFactory.getLogger(InvestmentCodeController.class);
//
//    private static String MODULE = " [InvestmentCodeController] ";
//    private final ServiceRepository serviceRepository;
//    private final PlanServiceRepository planServiceRepository;
//
//    public InvestmentCodeController(InvestmentCodeService service,
//                                    ServiceRepository serviceRepository,
//                                    PlanServiceRepository planServiceRepository) {
//        super(service);
//        this.serviceRepository = serviceRepository;
//        this.planServiceRepository = planServiceRepository;
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return " [InvestmentCodeController] ";
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_ADD + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody InvestmentCodeDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        MDC.put("type", "Create");
//        boolean flag = investmentCodeService.duplicateVerifyAtSave(entityDTO.getIcname());
//        boolean flagcode=investmentCodeService.duplicateVerifyAtSaveForCode(entityDTO.getIccode());
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        if (flag && flagcode) {
//            dataDTO = super.save(entityDTO, result, authentication, req);
//            InvestmentCodeDto investmentCodeDto= (InvestmentCodeDto) dataDTO.getData();
//            logger.info("BusinessVertical created Successfully With name "+ entityDTO.getIcname()+"  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } else if (!flag){
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.IC_NAME_EXITS);
//            logger.error("Unable to Create Ic Code with IC name " +entityDTO.getIcname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.IC_CODE_EXITS);
//            logger.error("Unable to Create Ic Code with IC name " +entityDTO.getIcname()+" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE);
//        }
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody InvestmentCodeDto entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        if(getMvnoIdFromCurrentStaff() != null) {
//            entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//        }
//        String oldname=investmentCodeService.getById(entityDTO.getId()).getIcname();
//        org.slf4j.MDC.put("type", "Update");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = investmentCodeService.duplicateVerifyAtEdit(entityDTO.getIcname(), entityDTO.getId());
//        if (flag) {
//            dataDTO = super.update(entityDTO, result, authentication, req);
//            InvestmentCodeDto investmentCodeDto = (InvestmentCodeDto) dataDTO.getData();
//        } else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.MESSAGE_FOR_MASTER_MANAGEMENT);
//        }
//        return dataDTO;
//    }
//
////    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\",\"" +
////            AclConstants.OPERATION_SUB_BUSINESS_UNIT_VIEW + "\",\"" +
////            AclConstants.OPERATION_SUB_BUSINESS_UNIT_ALL + "\",\"" +
////            AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination () {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<InvestmentCodeDto> list = investmentCodeService.getAllEntities().stream().filter(investmentCodeDto -> !investmentCodeDto.getIsDeleted() && investmentCodeDto.getStatus().equalsIgnoreCase("ACTIVE")).collect(Collectors.toList());
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
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\")")
//    @Override
//    public GenericDataDTO getAll (@RequestBody PaginationRequestDTO requestDTO){
//        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                genericDataDTO = investmentCodeService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//
//            else
//                genericDataDTO = investmentCodeService.search(requestDTO.getFilters()
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
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_VIEW + "\")")
//    public GenericDataDTO search (@RequestParam(required = false, defaultValue = "${request.defaultPage}") List<GenericSearchModel>
//                                          page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String
//                                          sortBy, @RequestBody Integer filter){
//        return investmentCodeService.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_INVESTMENT_CODE_ALL + "\",\"" + AclConstants.OPERATION_INVESTMENT_CODE_DELETE + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody InvestmentCodeDto entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        org.slf4j.MDC.put("type", "Delete");
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        boolean flag = investmentCodeService.deleteVerification(entityDTO.getId().intValue());
//        if (flag) {
//            dataDTO = super.delete(entityDTO, authentication, req);
//            InvestmentCodeDto investmentCodeDto= (InvestmentCodeDto) dataDTO.getData();
//            if (investmentCodeDto != null) {
//                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BUSINESS_VERTICALS,
//                //  AclConstants.OPERATION_BUSINESS_VERTICALS_DELETE, req.getRemoteAddr(), null, businessVerticalsDTO.getId(), businessVerticalsDTO.getVname());
//                logger.info("Region  With name " + entityDTO.getIcname() + " is deleted Successfully  :  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//            }
//        }else {
//            dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
//            dataDTO.setResponseMessage(DeleteContant.INVESTMENT_NAME_DELETE_EXIST);
//            logger.error("Unable to Delete Region With name: " + entityDTO.getIcname() + "  request: { From : {}}; Response : {{}}", req.getHeader("requestFrom"), HttpStatus.NOT_ACCEPTABLE.value());
//        }
//
//        org.slf4j.MDC.remove("type");
//        return dataDTO;
//
//    }
//    @GetMapping(value="/getIcNames")
//    public List<InvestmentCode> getIcNameByBuid(){
//        try {
//            List<Long> BuIds = investmentCodeService.getBUIdsFromCurrentStaff();
//            List<InvestmentCode> investmentCodeList = new ArrayList<>();
//            if (!BuIds.isEmpty()) {
//                investmentCodeList = investmentCodeService.getIcname(BuIds);
//                investmentCodeList = investmentCodeService.removebindedInvestmet(investmentCodeList);
//            } else {
//
//                investmentCodeList =   investmentCodeRepository.findAll().stream().filter(investmentCode -> investmentCode.getStatus().equalsIgnoreCase("Active") && !investmentCode.getIsDeleted()).collect(Collectors.toList());
//                investmentCodeList = investmentCodeService.removebindedInvestmet(investmentCodeList);
//            }
//            return investmentCodeList;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//

}
