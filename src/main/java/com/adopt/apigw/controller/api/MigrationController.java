package com.adopt.apigw.controller.api;

import antlr.collections.List;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.soap.Services.ChangePlanService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping(UrlConstants.BASE_API_URL)
public class MigrationController extends ApiBaseController {

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    private ChangePlanService changePlanService;

    @Autowired
    private CustomerRequestExcelGenerator customerRequestExcelGenerator;

    private static String MODULE = " [MigrationController] ";

    private static String MIGRATION_FILE_LOCATION = "MIGRATION_FILE_LOCATION";
    private final Logger log = LoggerFactory.getLogger(APIController.class);


    //	private static final String OTP = "otp";
    public Integer MAX_PAGE_SIZE;
    public Integer PAGE;
    public Integer PAGE_SIZE;
    public Integer SORT_ORDER;
    public String SORT_BY;

    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        try {
            CustomerRequest customer = CustomerRequestExcelGenerator.createDummyCustomer(1);

            // Write workbook to ByteArrayOutputStream
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Workbook workbook = CustomerRequestExcelGenerator.generateExcelToStream(customer);
            workbook.write(out);
            workbook.close();

            ByteArrayResource resource = new ByteArrayResource(out.toByteArray());

            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=customer.xlsx")
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
            loggedInUserId = -1;
        }
        return loggedInUserId;
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



}
