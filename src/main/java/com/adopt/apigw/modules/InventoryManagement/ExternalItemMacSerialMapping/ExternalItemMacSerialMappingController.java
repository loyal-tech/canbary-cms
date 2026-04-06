package com.adopt.apigw.modules.InventoryManagement.ExternalItemMacSerialMapping;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapingDTO;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.InOutWardMACService;
import com.adopt.apigw.modules.InventoryManagement.InOutMACMapping.QInOutWardMACMapping;
import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import com.adopt.apigw.modules.InventoryManagement.outward.Outward;
import com.querydsl.core.types.dsl.BooleanExpression;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@Api(value = "ExternalItemMacSerialMappingController", description = "REST APIs related to External Item Mac Serial Mapping  Entity!!!!", tags = "external-item-mac-serial-mapping-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.EXTERNAL_ITEM_MAC_SERIAL_MAPPING)
public class ExternalItemMacSerialMappingController extends ExBaseAbstractController<ExternalItemMacSerialMappingDTO> {

    @Autowired
    public InOutWardMACService inOutWardMACService;

    @Autowired
    public ExternalItemMacSerialMappingService externalItemMacSerialMappingService;

    public ExternalItemMacSerialMappingController(ExternalItemMacSerialMappingService externalItemMacSerialMappingService) {
        super(externalItemMacSerialMappingService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[ExternalItemMacSerialMappingController]";
    }

//    @GetMapping("/getExternalItemGroupMacSerialMapping")
//    public GenericDataDTO getExternalItemGroupMacSerialMapping(@RequestParam(name = "externalItemId") Long externalItemId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(externalItemMacSerialMappingService.getByExternalItemId(externalItemId));
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/getAllMACMappingByExternalItemId")
//    public GenericDataDTO getAllMACMappingByExternalItemId(@RequestParam(name = "externalItemId") Long externalItemId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setDataList(externalItemMacSerialMappingService.getAllMACMappingByExternalItemId(externalItemId));
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    //Delete External Item Mac and Serial
//    @GetMapping("/deleteExternalItemMac")
//    public GenericDataDTO deleteExternalItemMac(@RequestParam(name = "itemId") Long itemId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            externalItemMacSerialMappingService.deleteExternalItemMac(itemId);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//    @Override
//    @PostMapping("/save")
//    public GenericDataDTO save(@RequestBody ExternalItemMacSerialMappingDTO dto, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            externalItemMacSerialMappingService.saveEntity(dto);
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
}
