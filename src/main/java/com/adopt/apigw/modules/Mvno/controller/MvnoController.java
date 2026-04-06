package com.adopt.apigw.modules.Mvno.controller;


import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.mapper.MvnoMapper;
import com.adopt.apigw.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.Mvno.model.MvnoDTO;
import com.adopt.apigw.modules.Mvno.service.MvnoService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.MvnoMessage;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.MVNO)
public class MvnoController extends ExBaseAbstractController<MvnoDTO> {
    private static String MODULE = " [MvnoController] ";
    @Autowired
    AuditLogService auditLogService;

    @Autowired
    private MessageSender messageSender;
@Autowired
private KafkaMessageSender kafkaMessageSender;
    @Autowired
    MvnoMapper mvnoMapper;
    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private MvnoService mvnoService;

    @Autowired
    private Tracer tracer;
    
    public MvnoController(MvnoService service) {
        super(service);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_VIEW, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;

    }

    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

    @Deprecated
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_VIEW + "\")")
    @Override
    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter , HttpServletRequest req,@RequestParam Integer mvnoId) {
        return super.search(page, pageSize, sortOrder, sortBy, filter , req,mvnoId);
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_ADD + "\")")
    @Override
    public GenericDataDTO save(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
        //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.sendEntitySaveDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_ADD, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_EDIT + "\")")
    @Override
    public GenericDataDTO update(@Valid @RequestBody MvnoDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
      //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),false, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.updateEntityDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_EDIT, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_MVNO_ALL + "\",\"" + AclConstants.OPERATION_MVNO_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody MvnoDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
        MvnoDTO mvnoDTO = (MvnoDTO) dataDTO.getData();
      //send message
        MvnoMessage mvnoMessage = new MvnoMessage(mvnoDTO.getId(),mvnoDTO.getName(),mvnoDTO.getUsername(),mvnoDTO.getPassword(),mvnoDTO.getSuffix(),mvnoDTO.getDescription(),
        		mvnoDTO.getEmail(),mvnoDTO.getPhone(),mvnoDTO.getStatus(),mvnoDTO.getLogfile(),mvnoDTO.getMvnoHeader(),mvnoDTO.getMvnoFooter(),true, mvnoDTO.getProfileImage(), mvnoDTO.getLogo_file_name());
//        this.messageSender.send(mvnoMessage, RabbitMqConstants.QUEUE_APIGW_SEND_MVNO);
        kafkaMessageSender.send(new KafkaMessageData(mvnoMessage,MvnoMessage.class.getSimpleName()));
        Mvno mvno = mvnoMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
        createDataSharedService.deleteEntityDataForAllMicroService(mvno);
        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_MVNO,
                AclConstants.OPERATION_MVNO_DELETE, req.getRemoteAddr(), null, mvnoDTO.getId(), mvnoDTO.getName());
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoController]";
    }


    //This api is only for testing the mvno deactivation functionality and not used any where.
    @GetMapping("/updateMvnoStatus")
    ResponseEntity<?> updateMvnoStatus (@RequestParam(required = true) Integer mvnoId){

        Set<Long> mvnoids=  new HashSet<>();
        mvnoids.add(mvnoId.longValue());
        mvnoService.changeMvnoStatus(mvnoids,"InActive");
        return null;

    }

}
