package com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adopt.apigw.constants.MenuConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.model.ReportProblemDTO;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.repository.ReportProblemRepository;
import com.adopt.apigw.modules.reports.recentrenewal.ReportProblem.service.ReportProblemService;
import com.adopt.apigw.utils.APIConstants;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.REPORT_PROBLEM)
public class ReportProblemController extends ExBaseAbstractController2<ReportProblemDTO> {
    private static  final Logger logger = LoggerFactory.getLogger(ReportProblemController.class);
    private static String MODULE = " [ReportController] ";
    @Autowired
    private ReportProblemService reportProblemService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private ReportProblemRepository reportProblemRepository;

    public ReportProblemController(ReportProblemService service) {
        super(service);
    }

    @PostMapping(value = "/savereport" )
    public GenericDataDTO saveReport(@RequestBody ReportProblemDTO reportProblem) throws Exception {
        return reportProblemService.saveReport(reportProblem);
    }

    @GetMapping("/phno")
    public GenericDataDTO getReportProblemByphno(Long phno) throws Exception {
        String SUB_MODULE = getModuleNameForLog() + "[getReportProblemByphno]";
        ApplicationLogger.logger.info(SUB_MODULE);

        return getReportProblemByPhno(phno);
    }

    @PreAuthorize("validatePermission(\"" + MenuConstants.REPORTED_PROBLEM + "\")")
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_REPORT_PROBLEM_ALL + "\",\"" + AclConstants.OPERATION_REPORT_PROBLEM_VIEW + "\")")
    @PostMapping("/pagination")
    public GenericDataDTO reportSearch(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        String SUBMODULE = getModuleNameForLog() + " [searchreport()] ";
        this.MAX_PAGE_SIZE = pageSize;
        try {
            if (null == filter || null == filter.getFilter() || 0 == filter.getFilter().size()) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage("Please provide search criteria!");
                logger.error("unable to fetch recode,  request: { From : {},}; Response : {{}{};}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
                return genericDataDTO;
            }

                pageSize = MAX_PAGE_SIZE;
            genericDataDTO = reportProblemService.searchreport(filter.getFilter(), page, pageSize, sortBy, sortOrder);

                genericDataDTO = reportProblemService.searchreport(filter.getFilter(), page, pageSize, sortBy, sortOrder);
            logger.info("Fetching Records is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
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
            logger.error("unable to fetch Records:  request: { From : {},}; Response : {{}},exception{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("key");
        return genericDataDTO;
    }

    private GenericDataDTO getReportProblemByPhno(Long phno) throws Exception {
        MDC.put("type", "Fetch");

        String SUB_MODULE = getModuleNameForLog() + "[getReportProblemByPhno]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        try {
            List<ReportProblemDTO> list = reportProblemService.getReportProblemByno(phno);
            ApplicationLogger.logger.info(SUB_MODULE);
            //List<ReportProblemDTO> list = reportProblemService.getReportProblemByno(phno);
            List<ReportProblemDTO> sortedList = list.stream().collect(Collectors.toList());
            genericDataDTO.setDataList(sortedList);
            genericDataDTO.setTotalRecords(sortedList.size());
            logger.info("Fetching Records By Phone number  "+phno +" is successfull :  request: { From : {}}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
            return genericDataDTO;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUB_MODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage("Failed to load data");
            logger.error("unable to fetch records By phonenumber "+phno+":  request: { From : {},}; Response : {{}}Exception:{}", getModuleNameForLog(),genericDataDTO.getResponseCode(),ex.getStackTrace());
        }
        MDC.remove("key");
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }
}
