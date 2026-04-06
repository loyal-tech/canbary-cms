package com.adopt.apigw.modules.TicketFollowupDetail.controller;
//
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.GenericSearchDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
//import com.adopt.apigw.modules.TicketFollowupDetail.service.TicketFollowupDetailService;
//import com.adopt.apigw.modules.acl.constants.AclConstants;
//import com.adopt.apigw.modules.auditLog.service.AuditLogService;
//import com.adopt.apigw.repository.common.StaffUserRepository;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//import java.util.ArrayList;
//import java.util.List;
//
//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TICKET_FOLLOWUP_DEATAILS)
public class TicketFollowupDetailController {
//
//    @Autowired
//    AuditLogService auditLogService;
//
//    @Autowired
//    TicketFollowupDetailService service;
//
//    @Autowired
//    StaffUserRepository staffUserRepository;
//
//    public TicketFollowupDetailController(TicketFollowupDetailService service) {
//        super(service);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO) {
//        return super.getAll(requestDTO);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO getEntityById(@PathVariable String id, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = super.getEntityById(id, req);
//        TicketFollowupDetailDTO dto = (TicketFollowupDetailDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TICKET_FOLLOWUP_DEATAILS,
//                AclConstants.OPERATION_TICKET_FOLLOWUP_DEATAILS_VIEW, req.getRemoteAddr(), null, dto.getId(), dto.getRemark());
//        MDC.remove("type");
//        return dataDTO;
//
//    }
//
//    @Override
//    public GenericDataDTO getAllWithoutPagination() {
//        return super.getAllWithoutPagination();
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody TicketFollowupDetailDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "create");
//        if(entityDTO.getCustId() == null) {
//			if(getStaffId() != null) {
//				entityDTO.setStaffId(getStaffId());
//			}
//    	}
//        GenericDataDTO dataDTO = new GenericDataDTO();
//        dataDTO.setData(service.saveEntity(entityDTO));
//        TicketFollowupDetailDTO dto = (TicketFollowupDetailDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TICKET_FOLLOWUP_DEATAILS,
//                AclConstants.OPERATION_TICKET_FOLLOWUP_DEATAILS_ADD, req.getRemoteAddr(), null, dto.getId(), dto.getRemark());
//        MDC.remove("type");
//        //add notification over here for follow up
//
//
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO search(@RequestParam(required = false, defaultValue = "${request.defaultPage}") Integer page
//            , @RequestParam(required = false, defaultValue = "${request.defaultPageSize}") Integer pageSize
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortOrder}") Integer sortOrder
//            , @RequestParam(required = false, defaultValue = "${request.defaultSortBy}") String sortBy, @RequestBody GenericSearchDTO filter) {
//        return super.search(page, pageSize, sortOrder, sortBy, filter);
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO update(@Valid @RequestBody TicketFollowupDetailDTO entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        if(entityDTO.getCustId() == null) {
//			if(getStaffId() != null) {
//				entityDTO.setStaffId(getStaffId());
//			}
//    	}
//    	GenericDataDTO dataDTO = super.update(entityDTO, result, authentication, req);
//        TicketFollowupDetailDTO dto = (TicketFollowupDetailDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TICKET_FOLLOWUP_DEATAILS,
//                AclConstants.OPERATION_TICKET_FOLLOWUP_DEATAILS_EDIT, req.getRemoteAddr(), null, dto.getId(), dto.getRemark());
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @Override
//    public GenericDataDTO delete(@RequestBody TicketFollowupDetailDTO entityDTO, Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO dataDTO = super.delete(entityDTO, authentication, req);
//        TicketFollowupDetailDTO dto = (TicketFollowupDetailDTO) dataDTO.getData();
//        auditLogService.addAuditEntry(AclConstants.ACL_CLASS_TICKET_FOLLOWUP_DEATAILS,
//                AclConstants.OPERATION_TICKET_FOLLOWUP_DEATAILS_DELETE, req.getRemoteAddr(), null, dto.getId(), dto.getRemark());
//        MDC.remove("type");
//        return dataDTO;
//    }
//
//    @PreAuthorize("validatePermission(\"" + AclConstants.OPERATION_CASE_ALL + "\",\"" + AclConstants.OPERATION_CASE_EDIT + "\")")
//    @GetMapping("/getAllByCaseId/{caseId}")
//    public GenericDataDTO getAllEntityByCaseId(@PathVariable Long caseId, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//    	GenericDataDTO genericDataDTO = new GenericDataDTO();
//    	genericDataDTO.setResponseCode(HttpStatus.OK.value());
//        genericDataDTO.setResponseMessage("Success");
//        genericDataDTO.setDataList(service.getAllByCaseId(caseId));
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//    @GetMapping("/getAllTeamNameByStaffId/{staffId}")
//    public List<String> teamList(@PathVariable Long staffId){
//        MDC.put("type", "Fetch");
//        List<String>teamsList=new ArrayList<>();
//        try {
//            return service.getTeamListByStaffId(staffId);
//        }catch (Exception e){
//            teamsList.add("No records Found");
//            e.getStackTrace();
//        }
//        MDC.remove("type");
//        return teamsList;
//    }
//    @Override
//    public String getModuleNameForLog() {
//        return "[TICKET_FOLLOWUP_DEATAILS_Controller]";
//    }
//
}
