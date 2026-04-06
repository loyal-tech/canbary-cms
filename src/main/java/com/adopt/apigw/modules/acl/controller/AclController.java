package com.adopt.apigw.modules.acl.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.adopt.apigw.utils.APIConstants;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.acl.model.AclMenuDTO;
import com.adopt.apigw.modules.acl.service.AclService;
import com.adopt.apigw.spring.LoggedInUser;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.ACL)
public class AclController {

    private static String MODULE = " [AclController] ";

    @Autowired
    AclService aclService;

    @GetMapping(value = "/getModuleOperations")
    public GenericDataDTO getModuleOperations() throws Exception {
        String SUBMODULE = MODULE + " [getModuleOperations()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            //Get operations

            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getModuleOperations());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            ApplicationLogger.logger.info("getting module operation:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
            genericDataDTO.setCurrentPageNumber(1);
        } catch (Exception ex) {
            ///ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch MOdule operations :Request : : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
    
    
    @GetMapping(value = "/getAclMenu")
    public GenericDataDTO getAclMenu() throws Exception {
        String SUBMODULE = MODULE + " [getModuleOperations()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            //Get operations
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            List<AclMenuDTO> list = aclService.createAclMenuStructure();
            genericDataDTO.setDataList(list);
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            ApplicationLogger.logger.info("Fetching ACL menu:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch ACL menu: {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getAllRoleOperations")
    public GenericDataDTO getAllRoleOperations() throws Exception {
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getAllRoleOperations());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            ApplicationLogger.logger.info("Fetching All role operations:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        }
        catch (Exception ex){
        //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Fetching all role operations Response : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getRoleOperations")
    public GenericDataDTO getRoleOperations() throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        String roles = ((LoggedInUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getRolesList();
     //   ApplicationLogger.logger.info(roles);

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setDataList(aclService.getRoleOperations(roles));
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            ApplicationLogger.logger.info("Fetching All role operations:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());

        }
        catch (Exception ex){
       //     ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch role operations  : {{}};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getMenuStructure")
    public GenericDataDTO getMenuStructure() throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(aclService.getMenuStructure());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            ApplicationLogger.logger.info("Fetching All Menu Structure:  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        }
        catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @GetMapping(value = "/getAllMenu")
    public GenericDataDTO getAllMenu() throws Exception {
        MDC.put("type", "Fetch");
        String SUBMODULE = MODULE + " [getRoleOperations()]";
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(aclService.getAclMenuByOrder());
            genericDataDTO.setTotalRecords(1);
            genericDataDTO.setPageRecords(1);
            genericDataDTO.setTotalPages(1);
            genericDataDTO.setCurrentPageNumber(1);
            ApplicationLogger.logger.info("Fetching All Menu :  Response : {{}};message: {}", APIConstants.SUCCESS,genericDataDTO.getResponseMessage());
        }
        catch (Exception ex){
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            ApplicationLogger.logger.error("Unable to fetch by type  :code:{};message: {};exception:{}", APIConstants.FAIL,genericDataDTO.getResponseMessage(),ex.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }
}
