package com.adopt.apigw.modules.TicketFollowUp.Controller;
//
//import com.adopt.apigw.constants.UrlConstants;
//import com.adopt.apigw.core.controller.ExBaseAbstractController;
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.service.ExBaseService;
//import com.adopt.apigw.exception.CustomValidationException;
//
//import com.adopt.apigw.modules.TicketFollowUp.Model.TicketFollowUpDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Model.TicketFollowUpRemarkDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Service.TicketFollowUpRemarkService;
//import com.adopt.apigw.modules.TicketFollowUp.Service.TicketFollowUpService;
//import com.adopt.apigw.utils.APIConstants;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.slf4j.MDC;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.Authentication;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.validation.Valid;
//
//
//@RestController
//@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.TICKET_FOLLOW_UP)
public class TicketFollowUpController  {
//
//
//    public TicketFollowUpController(TicketFollowUpService service) {
//        super(service);
//    }
//
//
//
//    @Autowired
//    private TicketFollowUpService ticketFollowUpService;
//
//    @Autowired
//    private TicketFollowUpRemarkService ticketFollowUpRemarkService;
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketFollowUpController]";
//    }
//
//    private static final Logger logger = LoggerFactory.getLogger(TicketFollowUpController.class);
//
//    @Override
//    public GenericDataDTO save(@Valid @RequestBody TicketFollowUpDTO entityDTO, BindingResult result,
//                               Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Schedule");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            entityDTO.setStaffUserId(getStaffId());
//            entityDTO.setCreatedBy(getStaffId());
//            genericDataDTO = ticketFollowUpService.save(entityDTO);
//            logger.info("Follow Up Schedule With Name " + entityDTO.getFollowUpName() + "  : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Schedule FollowUp With Name " + entityDTO.getFollowUpName() + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Schedule FollowUp With Name " + entityDTO.getFollowUpName() + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PostMapping("/reScheduleTicketfollowup")
//    public GenericDataDTO reSchedulefollowup(@Valid @RequestBody TicketFollowUpDTO entityDTO, @RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks, BindingResult result,
//                                             Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Re-Schedule");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            if(getMvnoIdFromCurrentStaff() != null) {
//                entityDTO.setMvnoId(getMvnoIdFromCurrentStaff());
//            }
//            entityDTO.setStaffUserId(getStaffId());
//            entityDTO.setCreatedBy(getStaffId());
//            genericDataDTO = ticketFollowUpService.reSchedule(entityDTO, followUpId, remarks,getStaffId());
//            logger.info("Follow Up Re-Schedule With Name " + entityDTO.getFollowUpName() + "  : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Re-Schedule FollowUp With Name " + entityDTO.getFollowUpName() + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Re-Schedule FollowUp With Name " + entityDTO.getFollowUpName() + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/closefollowup")
//    public GenericDataDTO closefollowup(@RequestParam("followUpId") Long followUpId, @RequestParam("remarks") String remarks, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Close");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketFollowUpService.closefollowup(followUpId, remarks,getStaffId());
//            logger.info("Follow Up Close successfully : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Close FollowUp :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Close FollowUp :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/findAll")
//    public GenericDataDTO getAllByCaseId(@RequestParam("caseId") Integer caseId,
//                                             @RequestParam(value = "page", defaultValue = "1", required = false) Integer page,
//                                             @RequestParam(value = "pageSize", defaultValue = "5", required = false) Integer pageSize,HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            PaginationRequestDTO paginationRequestDTO = new PaginationRequestDTO();
//            paginationRequestDTO.setPage(page);
//            paginationRequestDTO.setPageSize(pageSize);
//            paginationRequestDTO = setDefaultPaginationValues(paginationRequestDTO);
//            genericDataDTO = ticketFollowUpService.getAllByCaseId(caseId,paginationRequestDTO);
//            logger.info("Fetching All TicketFollowUp With id " + caseId + "  : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Fetching All TicketFollowUp With id " + caseId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetching All TicketFollowUp With id " + caseId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @PostMapping("/ticketFollowUp/remark")
//    public GenericDataDTO addTicketFollowUpRemark(@Valid @RequestBody TicketFollowUpRemarkDTO entityDTO, BindingResult result,
//                                               Authentication authentication, HttpServletRequest req) throws Exception {
//        MDC.put("type", "Schedule");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketFollowUpRemarkService.save(entityDTO, getStaffId());
//            logger.info("TicketFollowUp remark added successfully  : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to add ticketFollowUp remark  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to add ticketFollowUp remark  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/findAll/ticketFollowUpRemark/{ticketFollowUpId}")
//    public GenericDataDTO getAllTicketFollowUpRemarkByTicketFollowUpId(@PathVariable("ticketFollowUpId") Long ticketFollowUpId,HttpServletRequest req) throws Exception {
//        MDC.put("type", "Fetch");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketFollowUpRemarkService.getAllByTicketFollowUpId(ticketFollowUpId);
//            logger.info("Fetching All TicketFollowUpRemark With ticketFollowUp id " + ticketFollowUpId + "  : Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Fetching All TicketFollowUpRemark With ticketFollowUp id " + ticketFollowUpId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetching All TicketFollowUpRemark With ticketFollowUp id " + ticketFollowUpId + "  :   Response : {{};error{};exception:{}}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
//
//    @GetMapping("/generateNameOfTheTicketFollowUp/{caseId}")
//    public GenericDataDTO generateNameOfTheFollowUpByCaseId(@PathVariable("caseId") Integer caseId,HttpServletRequest req) throws Exception {
//        MDC.put("type", "Generate");
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            genericDataDTO = ticketFollowUpService.generateNameOfTheFollowUp(caseId);
//            logger.info("Fetching generatedNameOfTheFollowUp :  request: { From : {}}; Response : {{}}", genericDataDTO.getResponseCode());
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//            genericDataDTO.setResponseCode(ce.getErrCode());
//            genericDataDTO.setResponseMessage(ce.getMessage());
//            logger.error("Unable to Fetch generatedNameOfTheFollowUp  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), ce.getStackTrace());
//        } catch (Exception e) {
//            e.printStackTrace();
//            genericDataDTO.setResponseCode(APIConstants.INTERNAL_SERVER_ERROR);
//            genericDataDTO.setResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
//            logger.error("Unable to Fetch generatedNameOfTheFollowUp  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", genericDataDTO.getResponseCode(), genericDataDTO.getResponseCode(), e.getStackTrace());
//        }
//        MDC.remove("type");
//        return genericDataDTO;
//    }
}
