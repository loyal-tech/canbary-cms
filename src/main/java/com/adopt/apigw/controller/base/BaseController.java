package com.adopt.apigw.controller.base;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.CommonList.service.CommonListService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

@Controller
public class BaseController<T> {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @Autowired
    private CommonListService commonListService;

    public void setPageParameters(boolean setPaginationParams, boolean setSearchParams, boolean setFlashMsg, String flashMsgType, String flashMsg, String entity, String uri, String search, Model model, Page<T> page) {
        Integer dbPageSize = CommonConstants.DB_PAGE_SIZE;
        Integer dispPageSize = CommonConstants.DISP_PAGE_SIZE;
        setPageParameters(setPaginationParams, setSearchParams, flashMsgType, flashMsg, entity, uri, search, model, page, dbPageSize, dispPageSize);
    }

    public void setPageParameters(boolean setPaginationParams, boolean setSearchParams, String flashMsgType, String flashMsg, String entity, String uri, String search, Model model, Page<T> page, Integer databasePageSize, Integer displayPageSize) {

        model.addAttribute("pageuri", uri);

        if (setPaginationParams) {
            Integer dbPageSize = databasePageSize;
            Integer dispPageSize = displayPageSize;

            int current = page.getNumber() + 1;
            int begin = Math.max(1, current - dispPageSize + 1);
            int end = Math.min(begin + dispPageSize - 1, page.getTotalPages());
            int next = -1;
            int prev = -1;

            if (end < page.getTotalPages()) {
                next = current + 1;
            }
            if (current > 1) {
                prev = current - 1;
            }

            if (end < begin) {
                end = begin;
            }

            long recordStart = (((current - 1) * dbPageSize) + 1);
            long recordEnd = current * dbPageSize;
            if (recordEnd > page.getTotalElements())
                recordEnd = page.getTotalElements();

            String recordMsg = "Showing " + recordStart + " to " + recordEnd + " Entities of " + page.getTotalElements();
            model.addAttribute("list", page);
            model.addAttribute("beginIndex", begin);
            model.addAttribute("endIndex", end);
            model.addAttribute("currentIndex", current);
            model.addAttribute("nextIndex", next);
            model.addAttribute("prevIndex", prev);
            model.addAttribute("lastIndex", ((page.getTotalPages() == 0) ? 1 : page.getTotalPages()));
            model.addAttribute("recordMsg", recordMsg);
            model.addAttribute("searchtext", search);
            if (page.getTotalElements() == 0) {
                model.addAttribute("norecords", "false");
                model.addAttribute("noRecordMsg", "No Records Found.");
            }
        }

        if (setSearchParams) {
            if (search != null && !"".equalsIgnoreCase(search)) {
                model.addAttribute("search", "?s=" + search);
            } else {
                model.addAttribute("search", "");
            }
        }

        if (flashMsgType != null && !"".equals(flashMsgType) && flashMsg != null && !"".equalsIgnoreCase(flashMsg)) {
            if (flashMsgType.equals(CommonConstants.FLASH_MSG_TYPE_SUCCESS)) {
                model.addAttribute("successFlash", flashMsg);
            } else {
                model.addAttribute("errorFlash", flashMsg);
            }
        } else { //backward compatible
            if (flashMsg != null && !"".equalsIgnoreCase(flashMsg)) {
                if (flashMsg.equalsIgnoreCase("AddSuccess")) {
                    model.addAttribute("successFlash", entity + " Added Successfully");
                } else if (flashMsg.equalsIgnoreCase("DelSuccess")) {
                    model.addAttribute("successFlash", entity + " Deleted Successfully");
                } else if (flashMsg.equalsIgnoreCase("EditSuccess")) {
                    model.addAttribute("successFlash", entity + " Updated Successfully");
                } else if (flashMsg.equalsIgnoreCase("sucess")) {
                    model.addAttribute("successFlash", "Opereation Performed Successfully");
                } else if (flashMsg.equalsIgnoreCase("error")) {
                    model.addAttribute("errorFlash", "Error performing operation, Please try again later");
                } else if (flashMsg.equalsIgnoreCase("errorCustom")) {
                    model.addAttribute("errorFlash", "Error performing operation, Please try again later");
                }
            }
        }
    }


    public void setPaginationParameters(String entity, String flashMsg, String search, Model model, Page<T> page) {
        Integer dbPageSize = CommonConstants.DB_PAGE_SIZE;
        Integer dispPageSize = CommonConstants.DISP_PAGE_SIZE;
        setPaginationParameters(entity, flashMsg, search, model, page, dbPageSize, dispPageSize);

    }

    public void setPaginationParameters(String entity, String flashMsg, String search, Model model, Page<T> page, Integer databasePageSize, Integer displayPageSize) {

        Integer dbPageSize = databasePageSize;
        Integer dispPageSize = displayPageSize;

        int current = page.getNumber() + 1;
        int begin = Math.max(1, current - dispPageSize + 1);
        int end = Math.min(begin + dispPageSize - 1, page.getTotalPages());
        int next = -1;
        int prev = -1;

        if (end < page.getTotalPages()) {
            next = current + 1;
        }
        if (current > 1) {
            prev = current - 1;
        }

        if (end < begin) {
            end = begin;
        }

        long recordStart = (((current - 1) * dbPageSize) + 1);
        long recordEnd = current * dbPageSize;
        if (recordEnd > page.getTotalElements())
            recordEnd = page.getTotalElements();

        String recordMsg = "Showing " + recordStart + " to " + recordEnd + " Entities of " + page.getTotalElements();
        model.addAttribute("list", page);
        model.addAttribute("beginIndex", begin);
        model.addAttribute("endIndex", end);
        model.addAttribute("currentIndex", current);
        model.addAttribute("nextIndex", next);
        model.addAttribute("prevIndex", prev);
        model.addAttribute("lastIndex", ((page.getTotalPages() == 0) ? 1 : page.getTotalPages()));
        model.addAttribute("recordMsg", recordMsg);
        model.addAttribute("searchtext", search);
        if (page.getTotalElements() == 0) {
            model.addAttribute("norecords", "false");
            model.addAttribute("noRecordMsg", "No Records Found.");
        }

        if (search != null && !"".equalsIgnoreCase(search)) {
            model.addAttribute("search", "?s=" + search);
        } else {
            model.addAttribute("search", "");
        }


        //flashMsg="error";

        if (flashMsg != null && !"".equalsIgnoreCase(flashMsg)) {
            if (flashMsg.equalsIgnoreCase("AddSuccess")) {
                model.addAttribute("successFlash", entity + " Added Successfully");
            } else if (flashMsg.equalsIgnoreCase("DelSuccess")) {
                model.addAttribute("successFlash", entity + " Deleted Successfully");
            } else if (flashMsg.equalsIgnoreCase("EditSuccess")) {
                model.addAttribute("successFlash", entity + " Updated Successfully");
            } else if (flashMsg.equalsIgnoreCase("sucess")) {
                model.addAttribute("successFlash", "Opereation Performed Successfully");
            } else if (flashMsg.equalsIgnoreCase("error")) {
                model.addAttribute("errorFlash", "Error performing operation, Please try again later");

            }
        }
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public int getLoggedInUserId() {
        int loggedInUserId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUserId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getUserId();
            }
        } catch (Exception e) {
            loggedInUserId = -1;
        }
        return loggedInUserId;
    }

    public int getLoggedInUserPartnerId() {
        int partnerId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                partnerId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getPartnerId();
            }
        } catch (Exception e) {
            partnerId = -1;
        }
        return partnerId;
    }

    public ResponseEntity<?> apiResponse(Integer responseCode, HashMap<String, Object> response) {
        try {
            response.put("timestamp", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSSS").format(LocalDateTime.now()));
            response.put("status", responseCode);

            ApplicationLogger.logger.info("Final Response:" + new ObjectMapper().writeValueAsString(response));
            if (responseCode.equals(APIConstants.SUCCESS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else if (responseCode.equals(APIConstants.FAIL)) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (responseCode.equals(APIConstants.INTERNAL_SERVER_ERROR)) {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (response == null) {
                response = new HashMap<>();
            }
            response.put("status", APIConstants.INTERNAL_SERVER_ERROR);
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
