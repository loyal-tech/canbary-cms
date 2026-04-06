package com.adopt.apigw.modules.NetworkDevices.controller;

import com.adopt.apigw.utils.UtilsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.DeleteContant;
import com.adopt.apigw.constants.MessageConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.NetworkDevices.model.SloatModel.OLTSlotDetailDTO;
import com.adopt.apigw.modules.NetworkDevices.service.SlotService.OLTSlotService;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.utils.APIConstants;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.OLT_SLOT)
public class OLTSlotDetailController extends ExBaseAbstractController<OLTSlotDetailDTO> {
    public OLTSlotDetailController(OLTSlotService service) {
        super(service);
    }
    private static String MODULE = " [OLTSlotDetailController] ";
    @Autowired
    private OLTSlotService oltSlotService;

    @Autowired
    private AuditLogService auditLogService;
    private static final Logger logger = LoggerFactory.getLogger(OLTSlotDetailController.class);
    @GetMapping("/byNetworkId/{networkId}")
    public GenericDataDTO getEntityByNetworkId(@PathVariable Long networkId) {
        MDC.put("type", "Fetch");
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        logger.info("Fetching Entity  by network id "+networkId+" :  request: { From : {},}; Response : {{}{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
        try {
            return GenericDataDTO.getGenericDataDTO(oltSlotService.getEntityByNetworkId(networkId));
        } catch (Exception e) {
            //ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            if (e instanceof DataNotFoundException) {
                genericDataDTO.setResponseMessage("Data Not Found");
              //  logger.error("Unable to fetch bounded parents :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getMessage());
            } else {
                genericDataDTO.setResponseMessage(e.getMessage());
            }
            logger.error("Unable to fetch Entity by network id "+networkId+" :  request: { From : {}}; Response : {{} {};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO save(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try {
            boolean flag = oltSlotService.duplicateVerifyAtSaveInSloat(oltSlotDetailDTO.getName(), oltSlotDetailDTO.getNetworkId().intValue());
            if (flag) {
                OLTSlotDetailDTO oltSlotDetailDTO1 = oltSlotService.saveEntity(oltSlotDetailDTO);
                genericDataDTO.setData(oltSlotDetailDTO1);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Success");
                logger.info("creating new olts with name  "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                // auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICE_SLOAT,
                //  AclConstants.OPERATION_SLOAT_ADD, req.getRemoteAddr(), null, oltSlotDetailDTO1.getId(), oltSlotDetailDTO1.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.SLOT_NAME_EXITS);
                logger.error("Unable to create olts Wit name "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        }
         catch (Exception e) {
            if (e instanceof DataIntegrityViolationException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.PORT_NAME_EXITS);
                logger.error("Unable to create olts Wit name "+oltSlotDetailDTO.getName()+":  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }else{
                ApplicationLogger.logger.error(e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to create olts Wit name "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO update(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");
        try{
            String oldname=oltSlotService.getEntityById(oltSlotDetailDTO.getId(),oltSlotDetailDTO.getMvnoId()).getName();
            boolean flag = oltSlotService.duplicateVerifyEditInSloat(oltSlotDetailDTO.getName(), oltSlotDetailDTO.getNetworkId().intValue(), oltSlotDetailDTO.getId().intValue());
            if (flag) {
                OLTSlotDetailDTO oltSlotDetailDTO1 = oltSlotService.updateEntity(oltSlotDetailDTO);
                String updatedValues = UtilsCommon.getUpdatedDiff(oltSlotDetailDTO1,oltSlotDetailDTO);
                genericDataDTO.setData(oltSlotDetailDTO1);
                genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                genericDataDTO.setResponseMessage("Success");
                logger.info("updating  olts with "+updatedValues+" is successfull : request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
                //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_NETWORK_DEVICE_SLOAT,
                // AclConstants.OPERATION_SLOAT_ADD, req.getRemoteAddr(), null, oltSlotDetailDTO1.getId(), oltSlotDetailDTO1.getName());
            } else {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.SLOT_NAME_EXITS);
                logger.error("Unable to update olts with name "+oldname+" bounded parents :  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
            }
        }
        catch(Exception e){
            if (e instanceof DataIntegrityViolationException) {
                genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                genericDataDTO.setResponseMessage(MessageConstants.PORT_NAME_EXITS);
                logger.error("Unable to update olts with name "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }else{
                ApplicationLogger.logger.error(e.getMessage(), e);
                genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                logger.error("Unable to update olts with name "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
            }
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO delete(@Valid @RequestBody OLTSlotDetailDTO oltSlotDetailDTO,  Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Fetch");

        try {
            if(oltSlotDetailDTO.getId()!=null){
              boolean flag=  oltSlotService.deleteVerification(oltSlotDetailDTO.getId().intValue());
              if(flag){
                  genericDataDTO.setResponseMessage("Suceess");
                  genericDataDTO.setResponseCode(APIConstants.SUCCESS);
                  logger.info("Deleting Olts sloat details with name "+ oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
              }else{
                  genericDataDTO.setResponseMessage(DeleteContant.SLOT_DELETE_EXIST);
                  genericDataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                  logger.error("Unable to delete Olts sloat with name "+oltSlotDetailDTO.getName()+":  request: { From : {}}; Response : {{};}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode());
              }
            }
        }catch(Exception e){
            ApplicationLogger.logger.error(e.getMessage(), e);
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            logger.error("Unable to delete Olts sloat with name "+oltSlotDetailDTO.getName()+" :  request: { From : {}}; Response : {{}{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        MDC.remove("type");
        return genericDataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[OLTSlotDetail Controller]";
    }
}
