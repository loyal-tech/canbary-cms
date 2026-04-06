package com.adopt.apigw.modules.TimeBasePolicy.controller;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.*;
import com.adopt.apigw.core.controller.ExBaseAbstractController2;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDTO;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import com.adopt.apigw.modules.TimeBasePolicy.service.TimeBasePolicyService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.TimeBasePolicyDetailsListMessage;
import com.adopt.apigw.rabbitMq.message.TimeBasePolicyDetailsMessage;
import com.adopt.apigw.rabbitMq.message.TimeBasePolicyMessage;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.UtilsCommon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TIME_BASE_POLICY)
public class TimeBasePolicyController extends ExBaseAbstractController2<TimeBasePolicyDTO> {

    @Autowired
    AuditLogService auditLogService;

    private static final Logger logger = LoggerFactory.getLogger(TimeBasePolicyController.class);
    @Autowired
    TimeBasePolicyService timeBasePolicyService;

    @Autowired
    private MessageSender messageSender;

    @Autowired
    private Tracer tracer;

    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;


    public TimeBasePolicyController(TimeBasePolicyService service) {
        super(service);
    }

    @Override
    public String getModuleNameForLog() {
        return "[TimeBasePolicyController]";
    }

    //Get All Time Base Policy With Pagination
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY + "\")")

    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = super.getAll(requestDTO, req,mvnoId);
            if (!genericDataDTO.getDataList().isEmpty()) {
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Timebase policy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
            } else {
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Timebase policy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }
        } catch (Exception ex) {
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Timebase policy" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED + LogConstants.LOG_NO_RECORD_FOUND + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Save Time Base Policy
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ADD + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY_CREATE + "\")")

    @Override
    public GenericDataDTO save(@Valid @RequestBody TimeBasePolicyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            TraceContext traceContext = tracer.currentSpan().context();
            HashMap<String, Object> response = new HashMap<>();
            MDC.put("type", "Create");
            MDC.put("userName", getLoggedInUser().getUsername());
            MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
            MDC.put("spanId", traceContext.spanIdString());
            Integer respCode = APIConstants.FAIL;
            boolean flag = timeBasePolicyService.duplicateVerifyAtSave(entityDTO.getName(),mvnoId);
            TimeBasePolicyDTO timeBasePolicyDTO = null;
            if (flag) {
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoId(mvnoId);
                    // TODO: pass mvnoID manually 6/5/2025
                    entityDTO.setMvnoName(mvnoRepository.findMvnoNameById(mvnoId.longValue()));
                    entityDTO.setMvnoId(mvnoId);

                    if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 1) {
                        dataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                        dataDTO.setResponseMessage(Constants.AVOID_SAVE_MULTIPLE_BU);
                        throw new CustomValidationException(APIConstants.FAIL, Constants.AVOID_SAVE_MULTIPLE_BU, null);

                    }
                    if (getBUIdsFromCurrentStaff().size() == 1) {
                        entityDTO.setBuId(getBUIdsFromCurrentStaff().get(0));
                    }
                }
                dataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
                timeBasePolicyDTO = (TimeBasePolicyDTO) dataDTO.getData();
                TimeBasePolicyMessage timeBasePolicyMessage = new TimeBasePolicyMessage(timeBasePolicyDTO);
                //messageSender.send(timeBasePolicyMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY);
                kafkaMessageSender.send(new KafkaMessageData(timeBasePolicyMessage,timeBasePolicyMessage.getClass().getSimpleName()));
                List<TimeBasePolicyDetailsDTO> timeBasePolicyDetailsList = timeBasePolicyDTO.getTimeBasePolicyDetailsList();
                TimeBasePolicyDetailsListMessage timeBasePolicyDetailsListMessage = new TimeBasePolicyDetailsListMessage();
                List<TimeBasePolicyDetailsMessage> timeBasePolicyDetailsMessageList = new ArrayList<>();
                for (TimeBasePolicyDetailsDTO timeBasePolicyDetails : timeBasePolicyDetailsList) {
                    TimeBasePolicyDetailsMessage timeBasePolicyDetailMessage = new TimeBasePolicyDetailsMessage(timeBasePolicyDetails);
                    timeBasePolicyDetailsMessageList.add(timeBasePolicyDetailMessage);

                }
                timeBasePolicyDetailsListMessage.setTimeBasePolicyDetailsMessageList(timeBasePolicyDetailsMessageList);
                //messageSender.send(timeBasePolicyDetailsListMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS);
                kafkaMessageSender.send(new KafkaMessageData(timeBasePolicyDetailsListMessage,timeBasePolicyDetailsListMessage.getClass().getSimpleName()));


                auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TIME_BASE_POLICY,
                        AclConstants.OPERATION_TIME_BASE_POLICY_ADD, req.getRemoteAddr(), null, entityDTO.getId(), entityDTO.getName());
                respCode = APIConstants.SUCCESS;
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.TIME_BASE_POLICY_NAME_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "create Time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + "Input size is Exceeded" + LogConstants.LOG_STATUS_CODE + respCode);
            }

        } catch (CustomValidationException e) {
            logger.error("Customvalidation Exception" + e.getMessage());
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    //Search Time Base Policy
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY + "\")")

    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter, HttpServletRequest req, @RequestParam Integer mvnoId) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Search");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO = timeBasePolicyService.timeBaseSearch(filter.getFilter(),page, pageSize,sortBy, sortOrder, mvnoId);
            if (!genericDataDTO.getDataList().isEmpty()) {
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Timebase policy By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + APIConstants.SUCCESS);
                return genericDataDTO;
            } else {
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Timebase policy By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + APIConstants.NULL_VALUE);
            }
        } catch (Exception ex) {
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Search Timebase policy By Keyword : " + filter.getFilter().get(0).getFilterValue() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + APIConstants.EXPECTATION_FAILED + LogConstants.LOG_NO_RECORD_FOUND + APIConstants.ERROR_MESSAGE + ex.getMessage() + LogConstants.LOG_STATUS_CODE + HttpStatus.NOT_ACCEPTABLE.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");

        }
        return genericDataDTO;
    }

    //Update Time Base Policy
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_EDIT + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY_EDIT + "\")")

    @Override
    public GenericDataDTO update(@Valid @RequestBody TimeBasePolicyDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Update");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer respCode = APIConstants.FAIL;
        GenericDataDTO dataDTO = new GenericDataDTO();
        try {
            // TODO: pass mvnoID manually 6/5/2025
//            if (getMvnoIdFromCurrentStaff(null) != null) {
//                // TODO: pass mvnoID manually 6/5/2025
                entityDTO.setMvnoId(mvnoId);
//            }
            TimeBasePolicyDTO td = timeBasePolicyService.getEntityById(entityDTO.getId(),mvnoId);
            String updatedValues = UtilsCommon.getUpdatedDiff(td, entityDTO);
            boolean flag = timeBasePolicyService.duplicateVerifyAtEdit(entityDTO.getName(), entityDTO.getId().intValue(),mvnoId);
            if (flag) {
                dataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
                TimeBasePolicyDTO timeBasePolicyDTO = (TimeBasePolicyDTO) dataDTO.getData();
                TimeBasePolicyMessage timeBasePolicyMessage = new TimeBasePolicyMessage(timeBasePolicyDTO);
                //messageSender.send(timeBasePolicyMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY);
                kafkaMessageSender.send(new KafkaMessageData(timeBasePolicyMessage,timeBasePolicyMessage.getClass().getSimpleName()));
                List<TimeBasePolicyDetailsDTO> timeBasePolicyDetailsList = timeBasePolicyDTO.getTimeBasePolicyDetailsList();
                TimeBasePolicyDetailsListMessage timeBasePolicyDetailsListMessage = new TimeBasePolicyDetailsListMessage();
                List<TimeBasePolicyDetailsMessage> timeBasePolicyDetailsMessageList = new ArrayList<>();
                for (TimeBasePolicyDetailsDTO timeBasePolicyDetails : timeBasePolicyDetailsList) {
                    TimeBasePolicyDetailsMessage timeBasePolicyDetailMessage = new TimeBasePolicyDetailsMessage(timeBasePolicyDetails);
                    timeBasePolicyDetailsMessageList.add(timeBasePolicyDetailMessage);

                }
                timeBasePolicyDetailsListMessage.setTimeBasePolicyDetailsMessageList(timeBasePolicyDetailsMessageList);
                //messageSender.send(timeBasePolicyDetailsListMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY_DETAILS);
                kafkaMessageSender.send(new KafkaMessageData(timeBasePolicyDetailsListMessage,timeBasePolicyDetailsListMessage.getClass().getSimpleName()));
                if (timeBasePolicyDTO != null) {
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TIME_BASE_POLICY,
                            AclConstants.OPERATION_TIME_BASE_POLICY_EDIT, req.getRemoteAddr(), null, timeBasePolicyDTO.getId(), timeBasePolicyDTO.getName());
                    respCode = APIConstants.SUCCESS;
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " update Time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + "update timebase policy details " + updatedValues + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                }
            } else {
                dataDTO.setResponseCode(HttpStatus.NOT_ACCEPTABLE.value());
                dataDTO.setResponseMessage(MessageConstants.TIME_BASE_POLICY_NAME_EXITS);
                respCode = HttpStatus.NOT_ACCEPTABLE.value();
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_NOT_CREATED + LogConstants.LOG_INFO + "time based policy with same name already exist" + LogConstants.LOG_STATUS_CODE + respCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, e.getMessage());
            logger.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + e.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return dataDTO;
    }

    //Get Time Base Policy by ID
    // @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_VIEW + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY + "\")")

    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req ,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        MDC.put("type", "Fetch");
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        TimeBasePolicyDTO timeBasePolicyDTO = (TimeBasePolicyDTO) dataDTO.getData();
        for (int i = 0; i < timeBasePolicyDTO.getTimeBasePolicyDetailsList().size(); i++) {
            String qosname = timeBasePolicyService.getid(timeBasePolicyDTO.getTimeBasePolicyDetailsList().get(i).getQqsid());
            timeBasePolicyDTO.getTimeBasePolicyDetailsList().get(i).setQos_name(qosname);
        }

        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TIME_BASE_POLICY,
                AclConstants.OPERATION_TIME_BASE_POLICY_VIEW, req.getRemoteAddr(), null, timeBasePolicyDTO.getId(), timeBasePolicyDTO.getName());
        return dataDTO;

    }

    //Get All Time Base Policy Without Pagination
    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

    //Delete Time Base Policy
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_TIME_BASE_POLICY_ALL + "\",\"" + AclConstants.OPERATION_TIME_BASE_POLICY_DELETE + "\")")
    @PreAuthorize("validatePermission(\"" + MenuConstants.TIME_POLICY_DELETE + "\")")

    @Override
    public GenericDataDTO delete(@RequestBody TimeBasePolicyDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = new GenericDataDTO();
        TraceContext traceContext = tracer.currentSpan().context();
        HashMap<String, Object> response = new HashMap<>();
        MDC.put("type", "Delete");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        Integer respCode = APIConstants.FAIL;
        try {
            boolean flag = timeBasePolicyService.deleteVerification(entityDTO.getId().intValue());
            if (flag) {
                dataDTO = super.delete(entityDTO, authentication, req);
                TimeBasePolicyDTO timeBasePolicyDTO = (TimeBasePolicyDTO) dataDTO.getData();
                if (timeBasePolicyDTO != null) {
                    timeBasePolicyDTO.setIsDeleted(true);
                    TimeBasePolicyMessage timeBasePolicyMessage = new TimeBasePolicyMessage(timeBasePolicyDTO);
                    //messageSender.send(timeBasePolicyMessage, RabbitMqConstants.QUEUE_APIGW_CREATE_TIME_BASE_POLICY);
                    kafkaMessageSender.send(new KafkaMessageData(timeBasePolicyMessage,timeBasePolicyMessage.getClass().getSimpleName()));
                    auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TIME_BASE_POLICY,
                            AclConstants.OPERATION_TIME_BASE_POLICY_DELETE, req.getRemoteAddr(), null, timeBasePolicyDTO.getId(), timeBasePolicyDTO.getName());
                    respCode = APIConstants.SUCCESS;
                    logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + respCode);
                }
            } else {
                dataDTO.setResponseCode(HttpStatus.METHOD_NOT_ALLOWED.value());
                dataDTO.setResponseMessage(DeleteContant.TIME_BASE_POLICY_EXIST);
                logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_STATUS_CODE + respCode);
            }
        } catch (Exception ex) {
            //		ApplicationLogger.logger.error(MODULE + ex.getStackTrace(), ex);
            ex.printStackTrace();
            respCode = HttpStatus.EXPECTATION_FAILED.value();
            response.put(APIConstants.ERROR_TAG, HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            logger.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "delete time based policy" + LogConstants.LOG_BY_NAME + entityDTO.getName() + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + respCode);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return dataDTO;
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
