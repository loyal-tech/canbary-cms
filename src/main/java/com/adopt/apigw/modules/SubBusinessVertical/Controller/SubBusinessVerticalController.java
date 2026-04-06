package com.adopt.apigw.modules.SubBusinessVertical.Controller;


import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.adopt.apigw.modules.SubBusinessVertical.Repository.SubBusinessVerticalRepository;
import com.adopt.apigw.modules.SubBusinessVertical.Service.SubBusinessVerticalService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.SUB_BUSINESS_VERICAL)
public class SubBusinessVerticalController{ // extends ExBaseAbstractController<SubBusinessVerticalDTO> {
//
//    private static final Logger logger= LoggerFactory.getLogger(SubBusinessVerticalController.class);
//
//    public SubBusinessVerticalController(SubBusinessVerticalService service) {
//        super(service);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[SubBusinessVerticalController]";
//    }
//
//    @Autowired
//    SubBusinessVerticalRepository subBusinessVerticalRepository;
//
//    @Autowired
//    SubBusinessVerticalService subBusinessVerticalService;
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_DELETE + "\")")
//    @DeleteMapping(value = "/delete")
//    public GenericDataDTO delete(@RequestParam(name = "id") Long id) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        subBusinessVerticalRepository.deleteById(id);
//        dataDTO.setResponseCode(HttpStatus.OK.value());
//        dataDTO.setResponseMessage("SubBusinessVertical Deleted Successfully");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ADD+ "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody SubBusinessVerticalDTO subBusinessVerticalDTO,BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        Boolean flag = subBusinessVerticalService.duplicateVerifyAtSave(subBusinessVerticalDTO.getSbvname());
//        if (flag) {
//            dataDTO = super.save(subBusinessVerticalDTO, result, authentication, req);
//        }
//        else {
//            dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            dataDTO.setResponseMessage(MessageConstants.SUB_BUSINESS_VERTICAL_NAME_EXITS);
//        }
//            return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_VIEW+ "\")")
//    @Override
//    public GenericDataDTO getEntityById (@PathVariable String id, HttpServletRequest req) throws Exception {
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        SubBusinessVerticalDTO subBusinessVerticalDTO = (SubBusinessVerticalDTO) dataDTO.getData();
//        return dataDTO;
//    }
//
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_ALL + "\",\"" + AclConstants.OPERATION_SUB_BUSINESS_VERTICAL_VIEW+ "\")")
//    @Override
//    public GenericDataDTO getAllWithoutPagination () {
//
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        try {
//            List<SubBusinessVerticalDTO> list = subBusinessVerticalService.getAllEntities().stream().filter(subBusinessVerticalDTO -> !subBusinessVerticalDTO.getIsDeleted()).collect(Collectors.toList());
//            genericDataDTO.setDataList(list);
//            genericDataDTO.setTotalRecords(list.size());
//            logger.info("Fetching ALL DATA without pagination :  request: { Module : {}}; Response : {Code{},Message:{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
//            return genericDataDTO;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
//            genericDataDTO.setResponseMessage("Failed to load data");
//            logger.error("Unable to load data  request: { module : {}}; Response : {Code{},Message:{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),ex.getStackTrace());
//        }
//        return genericDataDTO;
//    }
}
