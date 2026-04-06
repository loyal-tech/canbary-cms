package com.adopt.apigw.modules.qosPolicy.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.qosPolicy.model.QOSPolicyDTO;
import com.adopt.apigw.modules.qosPolicy.service.QOSPolicyService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.QOS_POLICY)
public class QOSPolicyController extends ExBaseAbstractController2<QOSPolicyDTO> {
       private static final Logger logger = LoggerFactory.getLogger(QOSPolicyController.class);

    private static String MODULE = " [QOSPolicyController] ";
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private QOSPolicyService qosPolicyService;

    @Autowired
    private Tracer tracer;

    @Autowired
    MvnoRepository mvnoRepository;

    public QOSPolicyController(QOSPolicyService service) {
        super(service);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_VIEW + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY + "\")")

@Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO,HttpServletRequest req,@RequestParam Integer mvnoId) {
    TraceContext traceContext = tracer.currentSpan().context();
    MDC.put("type", "Fetch");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    GenericDataDTO genericDataDTO = new GenericDataDTO();
    try {
        genericDataDTO = super.getAll(requestDTO,req,mvnoId);
        if(!genericDataDTO.getDataList().isEmpty()){
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"fetch Qos policy"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
        }
        else {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Qos policy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
        }
    }catch (Exception ex){
        logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"fetch Qos policy"+ LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");
    }
    return genericDataDTO;
}


    //    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_VIEW + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY + "\")")

@Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        QOSPolicyDTO qosPolicy = (QOSPolicyDTO) dataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_QOS_POLICY,
                AclConstants.OPERATION_QOS_POLICY_VIEW, req.getRemoteAddr(), null, qosPolicy.getId(), qosPolicy.getName());
        return dataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }


//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_VIEW + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY + "\")")

@Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter,HttpServletRequest req, @RequestParam Integer mvnoId)
    {
    TraceContext traceContext =tracer.currentSpan().context();
    MDC.put("type", "Search");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put("traceId", traceContext.traceIdString());
    MDC.put("spanId", traceContext.spanIdString());
    GenericDataDTO genericDataDTO = new GenericDataDTO();
    try{
        genericDataDTO = qosPolicyService.qosSearch(filter.getFilter(),page, pageSize,sortBy, sortOrder, mvnoId);
       if(!genericDataDTO.getDataList().isEmpty()){
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"Search Qos policy By Keyword : "+filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            return genericDataDTO;
       }
        else {
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Qos policy By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
        }
        }catch (Exception ex){
        logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"Search Qos policy By Keyword : "+ filter.getFilter().get(0).getFilterValue() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS +APIConstants.EXPECTATION_FAILED+ LogConstants.LOG_NO_RECORD_FOUND+ APIConstants.ERROR_MESSAGE+ex.getMessage()+ LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

    }finally {
        MDC.remove("type");
        MDC.remove("userName");
        MDC.remove("traceId");
        MDC.remove("spanId");

    }
    return qosPolicyService.qosSearch(filter.getFilter(),page, pageSize,sortBy, sortOrder, mvnoId);
}

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_ADD + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY_CREATE +"\")")

@Override
    public GenericDataDTO save(@Valid @RequestBody QOSPolicyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    TraceContext traceContext = tracer.currentSpan().context();
    HashMap<String, Object> response = new HashMap<>();
    MDC.put("type", "Create");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    Integer respCode = APIConstants.FAIL;

    try {
            boolean flag = qosPolicyService.duplicateVerifyAtSave(entityDTO.getName(),mvnoId);
            QOSPolicyDTO qosPolicy = null;
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
            	if(mvnoId != null) {
                    // TODO: pass mvnoID manually 6/5/2025
            		entityDTO.setMvnoId(mvnoId);
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
            	}
//                dataDTO = super.save(entityDTO, result, authentication, req);
//                qosPolicy = (QOSPolicyDTO) dataDTO.getData();
                qosPolicy = qosPolicyService.saveEntity(entityDTO);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                dataDTO.setResponseMessage("Successfully Created");
                dataDTO.setData(qosPolicy);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_QOS_POLICY,
                        AclConstants.OPERATION_QOS_POLICY_ADD, req.getRemoteAddr(), null, qosPolicy.getId(), qosPolicy.getName());
                respCode = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create QOS policy"+LogConstants.LOG_BY_NAME + qosPolicy.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseMessage(MessageConstants.QOS_POLICY_NAME_EXITS);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR +"create QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED  +"Input size is Exceeded"+ LogConstants.LOG_STATUS_CODE+respCode);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [SAVE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseMessage(ex.getMessage());
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

            }
            else {
                ApplicationLogger.logger.error(ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                dataDTO.setResponseMessage(ex.getMessage());
                respCode = APIConstants.INTERNAL_SERVER_ERROR;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"create QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);

            }
        }

         finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_EDIT + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY_EDIT + "\")")

@Override
    public GenericDataDTO update(@Valid @RequestBody QOSPolicyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    TraceContext traceContext = tracer.currentSpan().context();
    HashMap<String, Object> response = new HashMap<>();
    MDC.put("type", "Update");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    Integer respCode = APIConstants.FAIL;

    QOSPolicyDTO qosPolicy = null;
        try {
            QOSPolicyDTO qp=qosPolicyService.getEntityForUpdateAndDelete(entityDTO.getId(),mvnoId);
//           String oldname= qosPolicyService.getEntityForUpdateAndDelete(entityDTO.getId()).getName();
            boolean flag = qosPolicyService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue(),mvnoId);
            String updatedValues = UtilsCommon.getUpdatedDiff(qp,entityDTO);
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
                if(mvnoId != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoId(mvnoId);
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
                }

    //            dataDTO = super.update(entityDTO, result, authentication, req);
    //            qosPolicy = (QOSPolicyDTO) dataDTO.getData();

                qosPolicy = qosPolicyService.updateEntity(entityDTO);
                dataDTO.setData(qosPolicy);
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_QOS_POLICY, AclConstants.OPERATION_QOS_POLICY_EDIT, req.getRemoteAddr(), null, qosPolicy.getId(), qosPolicy.getName());
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update QOS policy"+LogConstants.LOG_BY_NAME + qosPolicy.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+ " update Qos policy Details " + updatedValues + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
                dataDTO.setResponseCode(HttpStatus.OK.value());
                dataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseMessage(MessageConstants.QOS_POLICY_NAME_EXITS);
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"update QOS policy"+LogConstants.LOG_BY_NAME + qosPolicy.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED +   LogConstants.LOG_INFO  + "QosPolicy with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                dataDTO.setResponseMessage("Not Found");
                respCode = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"update QOS policy" +LogConstants.LOG_BY_NAME + qosPolicy.getName() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
            } else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                dataDTO.setResponseMessage(ex.getMessage());
                //logger.error("Unable to update QOS policy with  name "+qosPolicy.getName()+" :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"),APIConstants.FAIL,dataDTO.getResponseMessage(),ex.getStackTrace());

            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"update QOS policy "+LogConstants.LOG_BY_NAME + qosPolicy.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
            }
        }
         finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_QOS_POLICY_ALL + "\",\"" + AclConstants.OPERATION_QOS_POLICY_DELETE + "\")")
//@PreAuthorize("validatePermission(\"" + MenuConstants.QOS_POLICY_DELETE + "\")")

@Override
    public GenericDataDTO delete(@RequestBody QOSPolicyDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
    TraceContext traceContext = tracer.currentSpan().context();
    HashMap<String, Object> response = new HashMap<>();
    MDC.put("type", "Delete");
    MDC.put("userName", getLoggedInUser().getUsername());
    MDC.put("traceId",traceContext.traceIdString());
    MDC.put("spanId",traceContext.spanIdString());
    Integer respCode = APIConstants.FAIL;

    try{
            entityDTO = qosPolicyService.getEntityForUpdateAndDelete(entityDTO.getIdentityKey(),entityDTO.getMvnoId());
            boolean flag = qosPolicyService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req);
                QOSPolicyDTO qosPolicy = (QOSPolicyDTO) dataDTO.getData();
                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_QOS_POLICY,
                        AclConstants.OPERATION_QOS_POLICY_DELETE, req.getRemoteAddr(), null, qosPolicy.getId(), qosPolicy.getName());
                respCode = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+LogConstants.REQUEST_FOR+"delete QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                dataDTO.setResponseMessage("QOSPolicy In Use !");
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR +"delete QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() +LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + respCode);
            }
        } catch (Exception ex) {
            if (ex instanceof DataNotFoundException) {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
                dataDTO.setResponseMessage("Not Found");
                respCode = APIConstants.NOT_FOUND;
                logger.info(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+  LogConstants.REQUEST_FOR +"delete QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() +  LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS  + LogConstants.LOG_FAILED +   LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
            } else if (ex instanceof CustomValidationException){
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                respCode = HttpStatus.EXPECTATION_FAILED.value();
                dataDTO.setResponseCode(respCode);
                dataDTO.setResponseMessage(Constants.MVNO_DELETE_UPDATE_ERROR_MSG);
                logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
            } else {
                ApplicationLogger.logger.error(getModuleNameForLog() + " [UPDATE] " + ex.getMessage(), ex);
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                dataDTO.setResponseMessage("Failed to update data. Please try after some time");
                logger.error(LogConstants.REQUEST_FROM+ req.getHeader("requestFrom")+ LogConstants.REQUEST_FOR+"delete QOS policy"+LogConstants.LOG_BY_NAME + entityDTO.getName() +LogConstants.REQUEST_BY + getLoggedInUser().getUsername()+  LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+respCode);
            }
        }
         finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[QOSPolicyController]";
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

}
