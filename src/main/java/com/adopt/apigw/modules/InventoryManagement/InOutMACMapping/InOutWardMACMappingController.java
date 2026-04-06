package com.adopt.apigw.modules.InventoryManagement.InOutMACMapping;

import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.InventoryManagement.outward.*;
import com.adopt.apigw.utils.APIConstants;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@Api(value = "InwardController", description = "REST APIs related to IN/OUT Ward mac mapping  Entity!!!!", tags = "in-out-ward-MAC-mapping-management")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.IN_OUT_WARD_MAC_MAPPING)
public class InOutWardMACMappingController extends ExBaseAbstractController<InOutWardMACMapingDTO> {

    @Autowired
    InOutWardMACService inOutWardMACService;

    @Autowired
    OutwardServiceImpl outwardService;

    @Autowired
    private OutwardRepository outwardRepository;

    @Autowired
    private OutwardMapper outwardMapper;
    private static String MODULE = " [InOutWardMACMappingController] ";
    private static final Logger logger = LoggerFactory.getLogger(InOutWardMACMappingController.class);

    public InOutWardMACMappingController(InOutWardMACService inOutWardMACService) {
        super(inOutWardMACService);
    }

    @Override
    public String getModuleNameForLog() {
        return "[InOutWardMACMappingController]";
    }

    @Override
    @PostMapping("/save")
    public GenericDataDTO save(@RequestBody InOutWardMACMapingDTO dto, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Successful");
            inOutWardMACService.saveEntity(dto);
            logger.info("Inoutward mac mapping created successfully : request: {From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
        }catch (CustomValidationException e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error("Unable to create inoutward mac mapping :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to create inoutward mac mapping :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        }
        return genericDataDTO;
    }

        @GetMapping("/getInwardMacMapping")
    public GenericDataDTO getInwardMacMapping(@RequestParam(name = "inwardId") Long inwardId, Authentication authentication, HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getByInwardId(inwardId));
            logger.info("Fetching inoutward mac mapping by inward id " + inwardId + " : request { From : {}}; Response : {{}}",  req.getHeader("requestFrom"), APIConstants.SUCCESS);
        }catch (CustomValidationException e) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(e.getMessage());
            logger.error("Unable to fetch inoutward mac mapping by inward id " + inwardId +" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            logger.error("Unable to fetch inoutward mac mapping by inward id " + inwardId +" :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
        }
        return genericDataDTO;
    }

//    @Transactional
//    @PostMapping("/updateMACMappingList")
//    public GenericDataDTO saveMACMappingList(@RequestBody List<InOutWardMACMapping> list, Authentication authentication, HttpServletRequest req) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            Outward outwardDto = outwardRepository.findById(list.get(0).getOutwardId()).get();
//            OutwardDto outwardDto1 = outwardMapper.domainToDTO(outwardDto , new CycleAvoidingMappingContext());
//            boolean hasSerial = outwardDto1.getProductId().getProductCategory().isHasSerial();
//            boolean isTrackable = outwardDto.getProductId().getProductCategory().isHasTrackable();
//            boolean hasMac = outwardDto.getProductId().getProductCategory().isHasMac();
//            if (hasMac || hasSerial) {
//                inOutWardMACService.validateUpdateMacMappingList(list, outwardDto1, hasMac, hasSerial);
//                inOutWardMACService.checkItemsForInwardOfOutward(list);
//            }
////            if (!hasSerial && isTrackable){
////                inOutWardMACService.checkNonSerializedItemsForInwardOfOutward(list, outwardDto);
////            }
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Successful");
//            logger.info("Update inoutward mac mapping list successfully : request: {From : {}}; Response : {{}}", req.getHeader("requestFrom"), APIConstants.SUCCESS);
//        } catch (CustomValidationException ex) {
////            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            logger.error("Unable to update inoutward mac mapping list :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
//        } catch (Exception e) {
////            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + e.getMessage(), e);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(e.getMessage());
//            logger.error("Unable to update inoutward mac mapping list :  request: { From : {}}; Response : {{}};Error :{} ;", req.getHeader("requestFrom"),HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage());
//        }
//        return genericDataDTO;
//    }

    @GetMapping("/getAllMACMappingByInwardId")
    public GenericDataDTO getAllMACMappingByInwardId(@RequestParam(name = "inward_id") Long inwardId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACMappingByInwardId(inwardId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }


    @GetMapping("/getAllMACByExstingMacType")
    public GenericDataDTO getAllMACByExstingMacType(@RequestParam(name = "inward_id") Long inwardId,@RequestParam(name="inOutMappingId") Long inOutMappingId,@RequestParam(name="inventoryType")String inventoryType) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACByExisitingMacType(inwardId,inOutMappingId,inventoryType));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @GetMapping("/getAllMACMappingByExternalId")
    public GenericDataDTO getAllMACMappingByExternalId(@RequestParam(name = "external_id") Long externalId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllMACMappingByExternalId(externalId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

//    @PostMapping("/saveMACMappingCustomer")
//    public GenericDataDTO saveMACMappingCustomer(@RequestBody List<CustMacMappping> custMacMappingList) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.saveMACMappingCustomer(custMacMappingList);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    @GetMapping("/deleteMacMapInCustomer")
    public GenericDataDTO deleteMacMappInCustomer(@RequestParam(name = "customerId") Integer customerId, @RequestParam(name = "macAddress") String macAddress) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.deleteMacMapInCustomer(customerId, macAddress);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }


    @GetMapping("/deletemac")
    public GenericDataDTO deletemac(@RequestParam(name = "itemId") Long itemId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.deleteMac(itemId);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }





//    @GetMapping("/removeMappingWithCustomerInventory")
//    public GenericDataDTO removeMappingWithCustomerInventory(@RequestParam(name = "mappingId") Long mappingId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setData(inOutWardMACService.removeMappingWithCustomerInventory(mappingId));
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

//    @GetMapping("/removeInventory")
//    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam("remark")String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.removeInventory(mappingId, customerInventoryId,customerId,isflag,remark);
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }

    @GetMapping("/removeInventory")
    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId,@RequestParam(name="nextstaff")Integer nextstaff,@RequestParam("remark")String remark,@RequestParam("isApprove") boolean isApprove) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
          //  genericDataDTO.setResponseCode(HttpStatus.OK.value());
          //  genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            //inOutWardMACService.removeInventory(mappingId, customerInventoryId,customerId,isflag,remark);
          return  inOutWardMACService.removeInventoryWorkFlowNew(mappingId,customerInventoryId,customerId,nextstaff,remark,isApprove);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            return genericDataDTO;
        }

    }

    @GetMapping("/generateRemoveInventoryRequest")
    public GenericDataDTO generateRemoveInventoryRequest(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam(name="revisedcharge", required = false)Long revisedcharge) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return  inOutWardMACService.genearateRemoveInventoryRequest(mappingId,customerInventoryId,customerId, isflag, revisedcharge);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            return genericDataDTO;
        }

    }


//    @GetMapping("/removeInventory")
//    public GenericDataDTO removeInventory(@RequestParam(name = "macMappingId") Long mappingId, @RequestParam(name = "customerInventoryId") Long customerInventoryId,@RequestParam(name = "customerId") Long customerId, @RequestParam(name = "isflag") boolean isflag,@RequestParam("remark")String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            inOutWardMACService.removeInventory(mappingId, customerInventoryId,customerId,isflag,remark,isApproveRequest);
//            //genericDataDTO.setDataList(inOutWardMACService.getAllAssemblyInventory(assemblyId));
//
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
//            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
//            genericDataDTO.setResponseMessage(ex.getMessage());
//        }
//        return genericDataDTO;
//    }
//


    @GetMapping("/getAllAssemblyInventory")
    public GenericDataDTO getAllAssemblyInventory(@RequestParam(name = "assemblyId") Long assemblyId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.getAllAssemblyInventory(assemblyId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }


    @GetMapping("/getbyinwardid")
    public GenericDataDTO getbyinwardid(@RequestParam(name = "inwardId") Long inwardId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyinwardid(inwardId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @GetMapping("/inwardOfOutwardId")
    public GenericDataDTO getByinwardOfOutwardId(@RequestParam(name = "inwardId") Long inwardOfOutwardId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyinwardOfOutwardId(inwardOfOutwardId));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @GetMapping("/getbyoutwardid")
    public GenericDataDTO getbyoutwardid(@RequestParam(name = "id") Long id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setDataList(inOutWardMACService.findbyoutwardid(id));

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

    @GetMapping("/removeInventoryfromowner")
    public GenericDataDTO removeInventoryfromowner(@RequestParam(name = "macMappingId") Long mappingId,@RequestParam("isflag") boolean isflag){
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            inOutWardMACService.removeInventoryfrompop(mappingId,isflag);

        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
            genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
        }
        return genericDataDTO;
    }

}


