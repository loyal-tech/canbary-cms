package com.adopt.apigw.modules.MvnoDiscountManagement;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.MvnoDiscountMessage;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO_DISCOUNT)
public class MvnoDiscountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(MvnoDiscountController.class);

    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private MvnoDiscountService mvnoDiscountService;

    @Autowired
    private Tracer tracer;

    @Autowired
    private StaffUserService staffUserService;

    @PostMapping("/save")
    public GenericDataDTO save(@RequestBody MvnoDiscountDTO mvnoDiscountDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        if(req.getAttribute(LogConstants.TRACE_ID) != null)
            MDC.put(LogConstants.TRACE_ID, ""+req.getAttribute(LogConstants.TRACE_ID));
        else
            MDC.put(LogConstants.TRACE_ID,""+ UUID.randomUUID());
        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.saveMvnoDiscount(mvnoDiscountDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            MvnoDiscountMessage message = new MvnoDiscountMessage("save", mvnoDiscountDTO);
//            messageSender.send(message, RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE);
            kafkaMessageSender.send(new KafkaMessageData(message,MvnoDiscountMessage.class.getSimpleName()));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+ APIConstants.SUCCESS);
        }catch (Exception ex){
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "save Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PutMapping("/update")
    public GenericDataDTO update(@RequestBody MvnoDiscountDTO mvnoDiscountDTO, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, ""+req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.updateMvnoDiscount(mvnoDiscountDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            MvnoDiscountMessage message = new MvnoDiscountMessage("update", mvnoDiscountDTO);
//            messageSender.send(message, RabbitMqConstants.QUEUE_SEND_MVNO_DISCOUNT_REVENUE);
            kafkaMessageSender.send(new KafkaMessageData(message,MvnoDiscountMessage.class.getSimpleName()));
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);

        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping("/all/{mvnoId}")
    public GenericDataDTO fetchAll(@PathVariable(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, ""+req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountService.fetchAllMvnoDiscountDetailByMvnoId(mvnoId);
            if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                genericDataDTO.setDataList(mvnoDiscountService.fetchAllMvnoDiscountDetailByMvnoId(mvnoId));
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Success");
            } else {
                genericDataDTO.setData(new ArrayList<>());
                genericDataDTO.setResponseCode(HttpStatus.NO_CONTENT.value());
                genericDataDTO.setResponseMessage("No Data Found!");
            }
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @DeleteMapping("/delete/{mvnoId}")
    public GenericDataDTO update(@PathVariable(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        MDC.put("type", "Create");
        MDC.put("userName",staffUserService.getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, ""+req.getAttribute(LogConstants.TRACE_ID));

        MDC.put("spanId",traceContext.spanIdString());
        try {
            genericDataDTO.setData(mvnoDiscountService.deleteAllMvnoDiscountByMvnoId(mvnoId));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS+ LogConstants.LOG_STATUS_CODE+APIConstants.SUCCESS);
        }catch (Exception ex){
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(ex.getMessage());
            LOGGER.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "update Mvno Discount" + LogConstants.REQUEST_BY + staffUserService.getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED+APIConstants.ERROR_MESSAGE + ex.getMessage()+ LogConstants.LOG_STATUS_CODE+APIConstants.EXPECTATION_FAILED);
        }finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

}
