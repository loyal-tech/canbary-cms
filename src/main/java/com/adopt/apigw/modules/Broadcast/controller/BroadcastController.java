package com.adopt.apigw.modules.Broadcast.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.Broadcast.model.BroadcastDTO;
import com.adopt.apigw.modules.Broadcast.service.BroadcastService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.BROADCAST)
public class BroadcastController extends ExBaseAbstractController<BroadcastDTO> {

    @Autowired
    private AuditLogService auditLogService;
    private static String MODULE = " [BroadcastController] ";
    public BroadcastController(BroadcastService service) {
        super(service);
    }


    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_VIEW + "\")")
    @Override
    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.getAll(requestDTO, req,mvnoId);
    }

//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_VIEW + "\")")
    @Override
    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.getEntityById(id, req,mvnoId);
        BroadcastDTO broadcast = (BroadcastDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BROAD_CAST,
//                AclConstants.OPERATION_BROAD_CAST_VIEW, req.getRemoteAddr(), null, broadcast.getId(), broadcast.getBody());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_ADD + "\")")
    @Override
    public GenericDataDTO save(@RequestBody BroadcastDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.save(entityDTO, result, authentication, req,mvnoId);
        BroadcastDTO broadcast = (BroadcastDTO) dataDTO.getData();
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BROAD_CAST,
         //       AclConstants.OPERATION_BROAD_CAST_ADD, req.getRemoteAddr(), null, broadcast.getId(), broadcast.getEmailsubject());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_EDIT + "\")")
    @Override
    public GenericDataDTO update(@RequestBody BroadcastDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception {
        GenericDataDTO dataDTO = super.update(entityDTO, result, authentication, req,mvnoId);
        BroadcastDTO broadcast = (BroadcastDTO) dataDTO.getData();
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BROAD_CAST,
        //        AclConstants.OPERATION_BROAD_CAST_EDIT, req.getRemoteAddr(), null, broadcast.getId(), broadcast.getBody());
        return dataDTO;
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_DELETE + "\")")
    @Override
    public GenericDataDTO delete(@RequestBody BroadcastDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
        BroadcastDTO broadcast = (BroadcastDTO) dataDTO.getData();
        //auditLogService.addAuditEntry(AclConstants.ACL_CLASS_BROAD_CAST,
        //    AclConstants.OPERATION_BROAD_CAST_EDIT, req.getRemoteAddr(), null, broadcast.getId(), broadcast.getBody());
        return dataDTO;
    }

    @Override
    public String getModuleNameForLog() {
        return "Broadcast Controller";
    }

    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_VIEW + "\")")
    @Override
    public GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId) {
        return super.getAllWithoutPagination(mvnoId);
    }

    @Deprecated
    //@PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_BROAD_CAST_ALL + "\",\"" + AclConstants.OPERATION_BROAD_CAST_VIEW + "\")")
    @Override
    public GenericDataDTO search(Integer page, Integer pageSize, Integer sortOrder, String sortBy, GenericSearchDTO filter  , HttpServletRequest req, @RequestParam Integer mvnoId) {
        return super.search(page, pageSize, sortOrder, sortBy, filter ,req,mvnoId) ;
    }
}
