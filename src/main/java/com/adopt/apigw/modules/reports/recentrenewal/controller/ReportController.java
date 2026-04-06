package com.adopt.apigw.modules.reports.recentrenewal.controller;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.modules.purchaseDetails.controller.PurchaseDetailsController;
import com.adopt.apigw.utils.APIConstants;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.reports.recentrenewal.model.ReportRequestModel;
import com.adopt.apigw.modules.reports.recentrenewal.service.ReportService;
import com.adopt.apigw.service.common.ClientServiceSrv;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL + UrlConstants.REPORT)
public class ReportController {
 
    @Autowired
    ClientServiceSrv clientServiceSrv;

    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    public static final String MODULE = " [ReportController] ";

    @Autowired
    private ReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_RECENT_RENEWAL_REPORT_VIEW + "\",\"" + AclConstants.OPERATION_RECENT_RENEWAL_REPORT_VIEW + "\")")
    @PostMapping(UrlConstants.RECENT_RENEWAL)
    public GenericDataDTO getRecentRenewalReport(@RequestBody ReportRequestModel requestModel) {
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        String SUBMODULE = MODULE + " [getRecentRenewalReport()] ";
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestModel) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.info("unable to fetch recent reneval report:  request: { From : {}, }; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == requestModel.getPage())
                requestModel.setPage(paginationRequestDTO.getPage());
            if (null == requestModel.getPageSize())
                requestModel.setPageSize(paginationRequestDTO.getPageSize());
            if (null != requestModel.getPageSize() && requestModel.getPageSize() > MAX_PAGE_SIZE)
                requestModel.setPageSize(MAX_PAGE_SIZE);
            logger.info("Fetching Recent reneval report is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            return reportService.getRecentRenewal(requestModel);
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            MDC.remove("type");
            logger.error("unable to fetch recent reneval report :  request: { From : {}, }; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            return genericDataDTO;
        }

    }

    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_RECENT_RENEWAL_REPORT_VIEW + "\",\"" + AclConstants.OPERATION_RECENT_RENEWAL_REPORT_VIEW + "\")")
    @PostMapping(UrlConstants.RECENT_RENEWAL + "/excel")
    public void exportToExcelForRecentRenewalReport(@RequestBody ReportRequestModel requestModel, HttpServletResponse response) {
        String SUBMODULE = MODULE + " [exportToExcelForRecentRenewalReport()] ";
        MDC.put("type", "Fetch");
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
            response.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=RecentRenewal_Excel_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);
            Workbook workbook = new XSSFWorkbook();
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == requestModel.getPage())
                requestModel.setPage(paginationRequestDTO.getPage());
            if (null == requestModel.getPageSize())
                requestModel.setPageSize(paginationRequestDTO.getPageSize());
            if (null != requestModel.getPageSize() && requestModel.getPageSize() > MAX_PAGE_SIZE)
                requestModel.setPageSize(MAX_PAGE_SIZE);
            reportService.excelGenerateForRecentRenewalReport(workbook, requestModel);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception ex) {
            logger.error("Unable to exportToExcelForRecentRenewalReport:  request: { From : {}, }; Response : {{}{};} Exception: {}", getModuleNameForLog(), APIConstants.FAIL,APIConstants.NULL_VALUE,ex.getStackTrace());
          //  ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        MDC.remove("type");
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CHARGE_DETAILS_REPORT_VIEW + "\",\"" + AclConstants.OPERATION_CHARGE_DETAILS_REPORT_VIEW + "\")")
    @PostMapping(UrlConstants.CHARGE_REPORT)
    public GenericDataDTO getChargeReport(@RequestBody ReportRequestModel requestModel) {
        MDC.put("type", "Fetch");
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        String SUBMODULE = MODULE + " [getChargeReport()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            if (null == requestModel) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please Provide Request!");
                logger.error("unable to get charge Report :  request: { From : {}, }; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;

        }
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == requestModel.getPage())
                requestModel.setPage(paginationRequestDTO.getPage());
            if (null == requestModel.getPageSize())
                requestModel.setPageSize(paginationRequestDTO.getPageSize());
            if (null != requestModel.getPageSize() && requestModel.getPageSize() > MAX_PAGE_SIZE)
                requestModel.setPageSize(MAX_PAGE_SIZE);
            return reportService.getChargeReport(requestModel);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            MDC.remove("type");
            logger.error("unable to get charge Report:  request: { From : {}, }; Response : {{}}Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),ex.getStackTrace());
            return genericDataDTO;
        }
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CHARGE_DETAILS_REPORT_VIEW + "\",\"" + AclConstants.OPERATION_CHARGE_DETAILS_REPORT_VIEW + "\")")
    @PostMapping(UrlConstants.CHARGE_REPORT + "/excel")
    public void exportToExcelForChargeReport(@RequestBody ReportRequestModel requestModel, HttpServletResponse response) {
        String SUBMODULE = MODULE + " [exportToExcelForChargeReport()] ";
        MDC.put("type", "Fetch");
        this.MAX_PAGE_SIZE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.MAX_PAGE_SIZE).get(0).getValue());
        try {
            response.setContentType("application/octet-stream");
            DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            String currentDateTime = dateFormatter.format(new Date());

            String headerKey = "Content-Disposition";
            String headerValue = "attachment; filename=Charge_Report_Excel_" + currentDateTime + ".xlsx";
            response.setHeader(headerKey, headerValue);
            Workbook workbook = new XSSFWorkbook();
            PaginationRequestDTO paginationRequestDTO = setDefaultPaginationValues(new PaginationRequestDTO());
            if (null == requestModel.getPage())
                requestModel.setPage(paginationRequestDTO.getPage());
            if (null == requestModel.getPageSize())
                requestModel.setPageSize(paginationRequestDTO.getPageSize());
            if (null != requestModel.getPageSize() && requestModel.getPageSize() > MAX_PAGE_SIZE)
                requestModel.setPageSize(MAX_PAGE_SIZE);
            reportService.excelGenerateForChargeReport(workbook, requestModel);
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
        } catch (Exception ex) {
            logger.error("unable to Export To excel:  request: { From : {}, }; Response : {{}{};}Exception :{}", getModuleNameForLog(),HttpStatus.EXPECTATION_FAILED,APIConstants.SUCCESS,ex.getStackTrace());
        //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
        }
        MDC.remove("type");
    }

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
    public String getModuleNameForLog() {
        return " [ReportController] ";
    }
}
