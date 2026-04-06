package com.adopt.apigw.core.controller;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.*;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.service.ExBaseService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ExBaseAbstractController2<DTO extends IBaseDto2> implements IBaseExController<DTO> {

    @Autowired
    ClientServiceSrv clientServiceSrv;
    @Autowired
    private CustomersRepository customersRepository;
    private ExBaseService<DTO, Long> service;
    private ExBaseAbstractService2 exBaseAbstractService2;

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;


    public ExBaseAbstractController2(ExBaseService service) {
        this.service = service;
    }
    private static final Logger logger = LoggerFactory.getLogger(ExBaseAbstractController2.class);
    public PaginationRequestDTO setDefaultPaginationValues(PaginationRequestDTO requestDTO) {
        this.PAGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
        this.PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE_SIZE).get(0).getValue());
        this.SORT_BY = clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORTBY).get(0).getValue();
        this.SORT_ORDER = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_SORT_ORDER).get(0).getValue());
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());

        if (null == requestDTO.getPage())
            requestDTO.setPage(PAGE);
        if (null == requestDTO.getPageSize())
            requestDTO.setPageSize(PAGE_SIZE);
        if (null == requestDTO.getSortBy())
            requestDTO.setSortBy(SORT_BY);
        if (null == requestDTO.getSortOrder())
            requestDTO.setSortOrder(SORT_ORDER);
        if (null != requestDTO.getPageSize() && requestDTO.getPageSize() > MAX_PAGE_SIZE)
            requestDTO.setPageSize(MAX_PAGE_SIZE);
        return requestDTO;
    }

    @Override
    @PostMapping
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req,@RequestParam Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + " [getAll()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            requestDTO = setDefaultPaginationValues(requestDTO);

            if (null == requestDTO.getFilters() || 0 == requestDTO.getFilters().size())
                genericDataDTO = service.getListByPageAndSizeAndSortByAndOrderBy(requestDTO.getPage()
                        , requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder()
                        , requestDTO.getFilters(),mvnoId);
            else
                genericDataDTO = service.search(requestDTO.getFilters()
                        , requestDTO.getPage(), requestDTO.getPageSize()
                        , requestDTO.getSortBy()
                        , requestDTO.getSortOrder(),mvnoId);


            if (null != genericDataDTO && genericDataDTO.getDataList() != null && !genericDataDTO.getDataList().isEmpty()) {
                logger.info("Fetching All Entities records:  request: { Module : {}}; Response : {Code :{}; Message : {}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No records found.");
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);
                logger.error("Unable to fetch all Entities No records found:  request: { Module : {}}; Response : {{};Exception:{}}", getModuleNameForLog(), genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
            logger.error("Unable to fetch all Entities:  request: { module : {}}; Response : {Code :{}; Message : {};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getMessage());
        }
        return genericDataDTO;
    }

    @Override
    @GetMapping("{id}")
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            genericDataDTO.setData(service.getEntityById(new Long(id),mvnoId));
            genericDataDTO.setTotalRecords(1);
            logger.info("Fetching All Entities by id "+id+" :  request: { module : {}}; Response : {Code :Code :{}; Message : {}; Message : {}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
        } catch (Exception e) {
            ApplicationLogger.logger.error(e.getMessage(), e);
            logger.error("Unable to fetch all Entities  by id "+id+"  :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),e.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
                logger.error("Unable to fetch all Entities  by id "+id+"  :  request: { From : {}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),e.getMessage());

            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
                logger.error("Unable to fetch all Entities  by id "+id+"   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),e.getMessage());
            }
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        return genericDataDTO;
    }

    @Override
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@Valid @RequestBody DTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ValidationData validation = validateSave(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ApplicationLogger.logger.info(getModuleNameForLog() + " entityDto :: " + entityDTO);
            DTO dtoData = service.saveEntity(entityDTO);
            genericDataDTO.setData(dtoData);
            genericDataDTO.setTotalRecords(1);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage("Failed to save data. Please try after some time");
            logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {{} {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
        }

        return genericDataDTO;
    }

    protected String getDefaultErrorMessages(List<FieldError> list) {

        if (null == list || list.size() < 1) {
            return "Something went wrong, Please try after some time";
        }
        String outputStr = "";
        String cm = "";
        for (FieldError fe : list) {
            outputStr = outputStr + cm + fe.getDefaultMessage() + ". Rejected Value: (" + fe.getRejectedValue() + ")";
            cm = " \n";

        }
        return outputStr;
    }

    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@Valid @RequestBody DTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            if (result.hasErrors()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(getDefaultErrorMessages(result.getFieldErrors()));
                logger.error("Unable to Update Entity by id "+entityDTO.getIdentityKey()+" :  request: { From : {}}; Response : {Code :{}; Message : {}}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }
            ValidationData validation = validateUpdate(entityDTO);
            if (!validation.isValid()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(validation.getMessage());
                logger.error("Unable to Update Entity by id "+entityDTO.getIdentityKey()+"   :  request: { From : {}}; Response : {Code :{}; Message : {};}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                return genericDataDTO;
            }

            DTO dtoData = service.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(),entityDTO.getMvnoId());
            String updatedValues = UtilsCommon.getUpdatedDiff(dtoData,entityDTO);
            genericDataDTO.setData(service.updateEntity(entityDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            genericDataDTO.setTotalRecords(1);
            logger.info("Updating Entity With   "+updatedValues+" :  request: { From : {}}; Response : {Code :{}; Message : {}}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());

        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Update Entity by id "+entityDTO.getIdentityKey()+"   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            } else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to fetch all Entities   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error("Unable to fetch all Entities   :  request: { From : {}{}}; Response : {{};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            }
        }
        return genericDataDTO;
    }

    @Override
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO delete(@RequestBody DTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            DTO dtoData = service.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(),entityDTO.getMvnoId());
            service.deleteEntity(entityDTO);
            genericDataDTO.setData(entityDTO);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Deleting Entity  With  id "+entityDTO.getIdentityKey()+" is Successfull :  request: { From : {}}; Response : {Code :{}; Message : {}}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());


        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                genericDataDTO.setResponseMessage("Not Found");
                logger.error("Unable to Delete Entity with id "+entityDTO.getIdentityKey()+"   :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            }  else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(ex.getMessage());
                logger.error("Unable to Delete Entity with id "+entityDTO.getIdentityKey()+"  :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}", req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [DELETE] " + ex.getMessage(), ex);
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Failed to delete data. Please try after some time");
                logger.error("Unable to Delete Entity with id "+entityDTO.getIdentityKey()+"  :  request: { From : {}}; Response : {Code :{}; Message : {};Exception:{}}",  req.getHeader("requestFrom"),genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage(),ex.getMessage());
            }
        }
        return genericDataDTO;
    }

    public ValidationData validateUpdate(DTO dto) {
        return new ValidationData();
    }

    public ValidationData validateSave(DTO dto) {
        return new ValidationData();
    }

    public abstract String getModuleNameForLog();

    @Override
    @GetMapping(path = "/all")
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
           // List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff().intValue() || d.getMvnoId() == 1 || getMvnoIdFromCurrentStaff() == 1 )&&(getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(d.getBuId())).collect(Collectors.toList());
            // TODO: pass mvnoID manually 6/5/2025
            List<DTO> list = service.getAllEntities(mvnoId).stream().filter(d -> (d.getMvnoId() == mvnoId.intValue() || d.getMvnoId() == 1 || mvnoId == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(d.getBuId()))).collect(Collectors.toList());
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(list.size());

            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
        }

        return genericDataDTO;
    }

    @GetMapping(value = "/excel")
    public void exportToExcel(HttpServletResponse response,@RequestParam("mvnoId") Integer mvnoId
    ) throws Exception {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Excel_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
        Workbook workbook = new XSSFWorkbook();
        service.excelGenerate(workbook,mvnoId);
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
    }

    @GetMapping(value = "/pdf")
    public void generatePdf(HttpServletResponse response,@RequestParam("mvnoId") Integer mvnoId
    ) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Pdf_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        Document pdfDoc = new Document();
        PdfWriter.getInstance(pdfDoc, response.getOutputStream());
        service.pdfGenerate(pdfDoc,mvnoId);
    }

   // @Deprecated
    @PostMapping(value = "/search")
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req,@RequestParam Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String SUBMODULE = getModuleNameForLog() + " [search()] ";
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
//            if (genericDataDTO.getResponseCode() == 406)
//            {
//                List<DTO> list = service.getAllEntities().stream().filter(d -> d.getMvnoId() == getMvnoIdFromCurrentStaff() || d.getMvnoId() == null ).collect(Collectors.toList());
//                genericDataDTO.setDataList(list);
//                genericDataDTO.setTotalRecords(list.size());
//                return genericDataDTO;
//            }
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                return genericDataDTO;
            }
            if (null != pageSize && pageSize > MAX_PAGE_SIZE)
                pageSize = MAX_PAGE_SIZE;
                genericDataDTO = service.search(filter.getFilter(), page, pageSize, sortBy, sortOrder,null);

            if (null != genericDataDTO) {

                if(genericDataDTO.getDataList().isEmpty())
                {
                    genericDataDTO = new GenericDataDTO();
                    genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                    genericDataDTO.setResponseMessage("No Record Found!");
                    genericDataDTO.setDataList(new ArrayList<>());
                    genericDataDTO.setTotalRecords(0);
                    genericDataDTO.setPageRecords(0);
                    genericDataDTO.setCurrentPageNumber(1);
                    genericDataDTO.setTotalPages(1);

                }
                return genericDataDTO;

            } else {
                genericDataDTO = new GenericDataDTO();
                genericDataDTO.setResponseCode(APIConstants.NULL_VALUE);
                genericDataDTO.setResponseMessage("No Record Found!");
                genericDataDTO.setDataList(new ArrayList<>());
                genericDataDTO.setTotalRecords(0);
                genericDataDTO.setPageRecords(0);
                genericDataDTO.setCurrentPageNumber(1);
                genericDataDTO.setTotalPages(1);

            }
        } catch (Exception ex) {
            genericDataDTO = new GenericDataDTO();
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setTotalRecords(0);
        }
        return genericDataDTO;
    }
    
    public Integer getMvnoIdFromCurrentStaff() {
    	Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
            	mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }
    public Integer getMvnoIdFromCurrentStaff(Integer custId) {
        //TODO: Change once API work on live BSS server
        Integer mvnoId = null;
        try {
            if(custId!=null){
                mvnoId = customersRepository.getCustomerMvnoIdByCustId(custId);

            }
//            else {
//                SecurityContext securityContext = SecurityContextHolder.getContext();
//                if (null != securityContext.getAuthentication()) {
//                    if(securityContext.getAuthentication().getPrincipal() != null)
//                        mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
//                }
//            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }

    public Integer getStaffId() {
    	Integer staffId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
            	staffId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getStaffId" + e.getMessage(), e);
        }
        return staffId;
    }

    public List<Long> getBUIdsFromCurrentStaff() {
        List<Long> mvnoIds = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoIds = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getBuIds();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("MVNO - getBUIdsFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoIds;
    }
}
