//package com.adopt.apigw.modules.SubArea.Controller;
//
//
//import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.exceptions.DataNotFoundException;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.modules.SubArea.DTO.SubAreaDTO;
//import com.adopt.apigw.modules.SubArea.Service.SubAreaService;
//import com.adopt.apigw.service.common.ClientServiceSrv;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_AREA)
//public class SubAreaController extends ExBaseAbstractController<SubAreaDTO> {
//
//    @Autowired
//    SubAreaService subAreaService;
//
//    @Autowired
//    ClientServiceSrv clientServiceSrv;
//
//    @Autowired
//    CreateDataSharedService createDataSharedService;
//
//    private static final Logger logger = LoggerFactory.getLogger(ExBaseAbstractController.class);
//
//    public SubAreaController(SubAreaService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[SubAreaController]";
//    }
//
//
//
//    @Override
//    @PostMapping
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO){
//        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            requestDTO = setDefaultPaginationValues(requestDTO);
//            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
//
//                genericDataDTO = subAreaService.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
//                        , requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder()
//                        , requestDTO.getFilters());
//
//            else
//                genericDataDTO = subAreaService.search(requestDTO.getFilters()
//                        , requestDTO.getPage(), requestDTO.getPageSize()
//                        , requestDTO.getSortBy()
//                        , requestDTO.getSortOrder());
//
//
//            if (null != genericDataDTO&& genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
//                //                  logger.info("Fetching data :  request: { From : {}}; Response : {Code{},Message:{};}",getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
//                return genericDataDTO;
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
//                genericDataDTO.setResponseMessage("No records found.");
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                //                   logger.info("Unable to fetch all Entities   :  request: { module : {}}; Response : {Code{},Message:{};}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
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
//
//    @Override
//    @GetMapping("{id}")
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        logger.info("Fetching All Entities by id "+id+" :  request: {Module:{} }; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        try {
//            genericDataDTO.setData(subAreaService.getEntityById(new Long(id)));
//            genericDataDTO.setTotalRecords(1);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error(e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//            if (e instanceof DataNotFoundException) {
//                genericDataDTO.setResponseMessage("Data Not Found");
//            } else {
//                genericDataDTO.setResponseMessage(e.getMessage());
//            }
//            logger.error("Unable to fetch Entity by id "+id+"  :  request: { Module : {}};  Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
//            genericDataDTO.setTotalRecords(0);
//            genericDataDTO.setDataList(null);
//        }
//        return genericDataDTO;
//    }
//
//
//    @Override
//    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO save(@Valid @RequestBody SubAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Successfully Created");
//        // logger.info("Creating New Entity with MVNO Id "+entityDTO.getMvnoId()+" :  request: { Module : {}, }; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        try {
//            if (result.hasErrors()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                logger.error("Unable Create New Records With MVNO Id "+entityDTO.getMvnoId()+"  :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            ValidationData validation = validateSave(entityDTO);
//            if (!validation.isValid()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(validation.getMessage());
//                logger.error("Unable Create New Records With MVNO Id "+entityDTO.getMvnoId()+":  request: { Module : {}}}; Response : {Code{},Message:{};};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto :: " + entityDTO);
//            SubAreaDTO dtoData = subAreaService.saveEntity(entityDTO);
//            createDataSharedService.sendEntitySaveDataForAllMicroService(dtoData);
//            genericDataDTO.setData(dtoData);
//            genericDataDTO.setTotalRecords(1);
//            logger.info("Creating New Entity with MVNO Id "+entityDTO.getMvnoId()+" :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
//            logger.error("Unable to Create Entity With MVNO id "+entityDTO.getMvnoId()+" :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//
//        return genericDataDTO;
//    }
//
//
//    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO update(@Valid @RequestBody SubAreaDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//
//        try {
//            if (result.hasErrors()) {
////               ApplicationLogger.logger.debug("Base Controller Error"+result.getFieldErrors());
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
//                logger.error("Unable to fetch Update Entity "+entityDTO.getIdentityKey()+"  :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            ValidationData validation = validateUpdate(entityDTO);
//            if (!validation.isValid()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(validation.getMessage());
//                logger.error("Unable to update   "+getModuleNameForLog()+" With  id "+entityDTO.getIdentityKey()+"  :  request: { Module : {}}; Response : {Code{};}", getModuleNameForLog(),genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//
//            SubAreaDTO dtoData = subAreaService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
////            entityDTO.setMvnoId(dtoData.getMvnoId());
//            // String updatedValues = CommonUtils.getUpdatedDiff(dtoData,entityDTO);
//            SubAreaDTO dto=subAreaService.updateEntity(entityDTO);
//            entityDTO.setIsDeleted(true);
//            genericDataDTO.setData(dto);
//            createDataSharedService.updateEntityDataForAllMicroService(dto);
//
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Success");
//            genericDataDTO.setTotalRecords(1);
//            //logger.info("Updating All  "+updatedValues+" :  request: { module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
//                //  ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//                logger.error("Unable to Update "+getModuleNameForLog() +" by id  :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            } else if (ex instanceof CustomValidationException){
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to update "+getModuleNameForLog() +" by id  :  request: { From : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//
//            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
//                logger.error("Unable to Update "+getModuleNameForLog() +" by id  :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            }
//        }
//        return genericDataDTO;
//    }
//
//
//    @Override
//    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public GenericDataDTO delete(@RequestBody SubAreaDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            SubAreaDTO dtoData = subAreaService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey());
//            ApplicationLogger.logger.info(getModuleNameForLog() + " [DELETE] " + dtoData);
////            entityDTO.setMvnoId(dtoData.getMvnoId());
//            subAreaService.deleteEntity(entityDTO);
//            genericDataDTO.setData(entityDTO);
//            genericDataDTO.setTotalRecords(1);
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successfully Deleted");
//            logger.info("Deleting  Entity by id "+entityDTO.getIdentityKey()+" :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            //   logger.info(getModuleNameForLog()+"is  deleted with "+entityDTO+"   :  request: { From : {}, Request Url : {}}; Response : {{}}", req.getHeader("requestFrom"),req.getRequestURL(),APIConstants.SUCCESS);
//
//        } catch (Exception ex) {
//            if (ex instanceof DataNotFoundException) {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
//                genericDataDTO.setResponseMessage("Not Found");
//                logger.error("Unable to delete Entity by id "+entityDTO.getIdentityKey()+" :  request: { module : {}}; Response : {Code{},Message:{};;Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            }  else if (ex instanceof CustomValidationException){
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage(ex.getMessage());
//                logger.error("Unable to Delete Entity by id "+entityDTO.getIdentityKey()+" :  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            } else {
//                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
//                logger.error("Unable to Delete Entity by id  "+entityDTO.getIdentityKey()+":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//            }
//        }
//        return genericDataDTO;
//    }
//
//
//    @GetMapping(path = "/all")
//    public GenericDataDTO getAllWithoutPagination() {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<SubAreaDTO> list = subAreaService.getAllEntities();
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
//    }
//
//
//    @PostMapping(value = "/search")
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        String SUBMODULE = getModuleNameForLog() + " [search()] ";
//        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
//        try {
////            if (genericDataDTO.getResponseCode() == 406)
////            {
////                List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff() || d.getMvnoId() == null ).collect(Collectors.toList());
////                genericDataDTO.setDataList(list);
////                genericDataDTO.setTotalRecords(list.size());
////                return genericDataDTO;
////            }
//            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
//                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//                genericDataDTO.setResponseMessage("Please provide search criteria!");
//                logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//                return genericDataDTO;
//            }
//            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
//                pageSize = MAX_PAGE_SIZE;
//            genericDataDTO = subAreaService.search(filter.getFilter(), page, pageSize, sortBy, sortOrder);
//
//            if (null != genericDataDTO) {
//
//                if(genericDataDTO.getDataList().isEmpty())
//                {
//                    genericDataDTO = new GenericDataDTO();
//                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                    genericDataDTO.setResponseMessage("No Record Found!");
//                    genericDataDTO.setDataList(new ArrayList<>());
//                    genericDataDTO.setTotalRecords(0);
//                    genericDataDTO.setPageRecords(0);
//                    genericDataDTO.setCurrentPageNumber(1);
//                    genericDataDTO.setTotalPages(1);
//                    logger.info("Fetching data with  filter "+filter.getFilter()+":  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//                }
//                return genericDataDTO;
//
//            } else {
//                genericDataDTO = new GenericDataDTO();
//                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
//                genericDataDTO.setResponseMessage("No Record Found!");
//                genericDataDTO.setDataList(new ArrayList<>());
//                genericDataDTO.setTotalRecords(0);
//                genericDataDTO.setPageRecords(0);
//                genericDataDTO.setCurrentPageNumber(1);
//                genericDataDTO.setTotalPages(1);
//                logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//
//            }
//        } catch (Exception ex) {
//            genericDataDTO = new GenericDataDTO();
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
//            genericDataDTO.setTotalRecords(0);
//            logger.error("Unable to Search data by  "+filter.getFilter()+":  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//}
