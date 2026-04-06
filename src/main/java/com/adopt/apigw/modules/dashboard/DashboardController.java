package com.adopt.apigw.modules.dashboard;

import brave.Tracer;
import brave.propagation.TraceContext;
import com.adopt.apigw.constants.LogConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.CustomerPortal.controller.CustomerPortalController;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.tickets.domain.Case;
//import com.adopt.apigw.modules.tickets.service.CaseService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController(value = "dashboardRestController")
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.DASHBOARD)
public class DashboardController {
    private static final String MODULE = " [DashboardController] ";

    DashboardService dashboardService;

    @Autowired
    CustomersService customersService;
    @Autowired
    PostpaidPlanService postpaidPlanService;

    @Autowired
    PlanGroupService planGroupService;
    @Autowired
    CreditDocService creditDocService;

    //    @Autowired
//    CaseService caseService;
    @Autowired
    DebitDocService debitDocService;
    @Autowired
    CustPlanMappingService custPlanMappingService;
    @Autowired
    CustomerAddressService customerAddressService;
    @Autowired
    PartnerPaymentService partnerPaymentService;

    @Autowired
    CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    private Tracer tracer;

    @Autowired
    CustSpecialPlanMapppingService custSpecialPlanMapppingService;

    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    private final Logger log = LoggerFactory.getLogger(DashboardController.class);

    @GetMapping(value = "/customerDetails/typeWiseCount")
    public GenericDataDTO typeWiseCustomerCount(@RequestParam(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [typeWiseCustomerCount()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, String> dataMap = dashboardService.typeWiseCustomerCount(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Fetching Type wise customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch customer details with typewise " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } catch (Exception ex) {
            //        ApplicationLogger.SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " fetch monthWiseTimeUsages " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/customerDetails/getStatusWiseCount")
    public GenericDataDTO getStatusWiseCount(@RequestParam(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getStatusWiseCount()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, String> dataMap = dashboardService.getStatusWiseCount(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Fetching Status wise customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching Status wise customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
        } catch (Exception ex) {
            //          ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Fetching Status wise customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/customerDetails/getNewlyActivatedCustomer")
    public GenericDataDTO getNewlyActivatedCustomer(@RequestParam(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getNewlyActivatedCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, String> dataMap = dashboardService.getNewlyActivatedCustomer(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Fetching newly activtded customers " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + " Fetching newly activtded customers " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.getNewlyActivatedCustomer(mvnoId));
        } catch (Exception ex) {
            //       ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Fetching newly activtded customers " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/customerDetails/getPlanWiseCustomer")
    public GenericDataDTO getPlanWiseCustomer(@RequestParam(name = "mvnoId") Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getPlanWiseCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Integer> dataMap = dashboardService.getPlanWiseCustomer(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Type wise plan customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Type wise plan customer " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
//            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.getPlanWiseCustomer(mvnoId));
        } catch (Exception ex) {
            //       ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch Type wise plan" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Payment details Graph APIs
    @GetMapping(value = "/paymentDetails/getMonthWiseCollection")
    public GenericDataDTO getMonthWiseCollection(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year", required = false) String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getMonthWiseCollection()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.getMonthWiseCollection(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Month wise collection" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Month wise collection " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.getMonthWiseCollection(mvnoId, year));
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch Month wise collection " + mvnoId + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/paymentDetails/pendingApprovalPayments")
    public GenericDataDTO pendingApprovalPayments(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [pendingApprovalPayments()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.pendingApprovalPayments(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch pending Approval Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch pending Approval Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.pendingApprovalPayments(mvnoId));
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch pending Approval Payments" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/paymentDetails/nextTenDaysReceivablePayment")
    public GenericDataDTO nextTenDaysReceivablePayment(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [nextTenDaysReceivablePayment()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.nextTenDaysReceivablePayment(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "Fetch next TenDays Receivable Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch next TenDays Receivable Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }

            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            //            genericDataDTO.setData(dashboardService.nextTenDaysReceivablePayment(mvnoId));
        } catch (Exception ex) {
            //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch next TenDays Receivable Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // Ticket Details Graph APIs
    @GetMapping(value = "/ticketDetails/totalOpenTickets")
    public GenericDataDTO totalOpenTickets(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [totalOpenTickets()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Long> dataMap = dashboardService.totalOpenTickets(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.totalOpenTickets(mvnoId));
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/ticketDetails/monthWiseTicketCount")
    public GenericDataDTO monthWiseTicketCount(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year", required = false) String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTicketCount()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Map<String, Long>> dataMap = dashboardService.monthWiseTicketCount(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Ticketse" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseTicketCount(mvnoId, year));
        } catch (Exception ex) {
            //       ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch total Open Tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/ticketDetails/staffWiseTicketCount")
    public GenericDataDTO staffWiseTicketCount(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [staffWiseTicketCount()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Map<String, String>> dataMap = dashboardService.staffWiseTicketCount(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch staff Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch staff Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
//            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.staffWiseTicketCount(mvnoId));
        } catch (Exception ex) {
            //       ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch staff Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/ticketDetails/teamWiseTicketCount")
    public GenericDataDTO teamWiseTicketCount(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [teamWiseTicketCount()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Map<String, String>> dataMap = dashboardService.teamWiseTicketCount(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch team Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch team Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.teamWiseTicketCount(mvnoId));
        } catch (Exception ex) {
            //        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch team Wise Ticket Count" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/ticketDetails/overDueTicketList")
    public GenericDataDTO overDueTicketList(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [overDueTicketList()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Case> dataMap = dashboardService.overDueTicketList(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch overdue tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch overdue tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.overDueTicketList(mvnoId));
        } catch (Exception ex) {
            //     ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch overdue tickets" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


    //Payment Details Graph APIs
    @GetMapping(value = "/paymentDetails/nextTenDaysRenewableCustomer")
    public GenericDataDTO nextTenDaysRenewableCustomer(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [nextTenDaysRenewableCustomer()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Customers> dataMap = dashboardService.nextTenDaysRenewableCustomer(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch next TenDays Renewable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch next TenDays Renewable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.nextTenDaysRenewableCustomer(mvnoId));
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch next TenDays Renewable Customer" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/paymentDetails/partnerWisePayment")
    public GenericDataDTO partnerWisePayment(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [partnerWisePayment()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.partnerWisePayment(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch partner Wise Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch partner Wise Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setData(dashboardService.partnerWisePayment(mvnoId));
        } catch (Exception ex) {
            ///   ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch partner Wise Payment" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    // Radius Graph APIs

    @GetMapping(value = "/radius/monthWiseVolumeUsages")
    public GenericDataDTO monthWiseVolumeUsages(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year", required = false) Integer year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseVolumeUsages()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.monthWiseVolumeUsages(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWise Volume Usages" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWise Volume Usagesv" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
//            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseVolumeUsages(mvnoId, year));
        } catch (Exception ex) {
            //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWise Volume Usages" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }

        return genericDataDTO;
    }

    @GetMapping(value = "/radius/monthWiseTimeUsages")
    public GenericDataDTO monthWiseTimeUsages(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year", required = false) Integer year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTimeUsages()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.monthWiseTimeUsages(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWise TimeUsages" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWiseTimeUsages" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
//            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseTimeUsages(mvnoId, year));
        } catch (Exception ex) {
            //    ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch monthWiseTimeUsages" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/radius/connectedUser")
    public GenericDataDTO connectedUser(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [connectedUser()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Long dataMap = dashboardService.connectedUser(mvnoId);
            genericDataDTO.setData(dataMap);
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            Integer RESP_CODE = APIConstants.SUCCESS;
            log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch connected customers" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            genericDataDTO.setResponseMessage("No data found.");

            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.connectedUser(mvnoId));
        } catch (Exception ex) {
            //  ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch connected customers" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }


//    APIs for commission graphs

    @GetMapping(value = "/commission/monthWiseAGRPayable")
    public GenericDataDTO monthWiseAGRPayable(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year") String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTimeUsages()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.monthWiseAGRPayable(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise AGRPayable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise AGRPayable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseAGRPayable(mvnoId, year));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise AGRPayable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/commission/monthWiseTDSPayable")
    public GenericDataDTO monthWiseTDSPayable(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year") String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTDSPayable()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.monthWiseTDSPayable(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise TDS Payable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise TDS Payable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseTDSPayable(mvnoId, year));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise TDS Payable" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/commission/partnerWiseTDSDetails")
    public GenericDataDTO partnerWiseTDSDetails(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year") String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [partnerWiseTDSDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.partnerWiseTDSDetails(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.partnerWiseTDSDetails(mvnoId, year));
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/commission/monthWiseTotalDetails")
    public GenericDataDTO monthWiseTotalDetails(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year") String year, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTotalDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Map<String, Double>> dataMap = dashboardService.monthWiseTotalDetails(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise Total Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise Total Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.monthWiseTotalDetails(mvnoId, year));
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch month Wise Total Details" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/commission/topFivePartnerCommissionWise")
    public GenericDataDTO topFivePartnerCommissionWise(@RequestParam(name = "mvnoId", required = false) Long mvnoId, @RequestParam(name = "year") String year, HttpServletRequest req) throws Exception {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [monthWiseTotalDetails()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            Map<String, Double> dataMap = dashboardService.topFivePartnerCommissionWise(mvnoId, year);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch top Five Partner Commission Wise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch top Five Partner Commission Wise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setData(dashboardService.topFivePartnerCommissionWise(mvnoId, year));
        } catch (Exception ex) {
            //ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch top Five Partner Commission Wise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    //Inventory Details Graph APIs
    @GetMapping(value = "/inventory/availableInventoryProductWise")
    public GenericDataDTO availableInventoryProductWise(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [availableInventoryProductWise()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Map<String, String>> dataMap = dashboardService.availableInventoryProductWise(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);

            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.availableInventoryProductWise(mvnoId));
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/inventory/staffAndProductWiseInventories")
    public GenericDataDTO staffAndProductWiseInventories(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [staffAndProductWiseInventories()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Map<String, String>> dataMap = dashboardService.staffAndProductWiseInventories(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.staffAndProductWiseInventories(mvnoId));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch available Inventory ProductWise" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/inventory/inventoryAlert")
    public GenericDataDTO inventoryAlert(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [staffAndProductWiseInventories()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Map<String, String>> dataMap = dashboardService.inventoryAlert(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.inventoryAlert(mvnoId));
        } catch (Exception ex) {
            /// ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch check elegebility" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @GetMapping(value = "/inventory/wareHouseAndProductWiseInventories")
    public GenericDataDTO wareHouseAndProductWiseInventories(@RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [staffAndProductWiseInventories()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            List<Map<String, String>> dataMap = dashboardService.wareHouseAndProductWiseInventories(mvnoId);
            if (dataMap.size() > 0) {
                genericDataDTO.setData(dataMap);
                genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
                Integer RESP_CODE = APIConstants.SUCCESS;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_SUCCESS + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            } else {
                genericDataDTO.setResponseMessage("No data found.");
                Integer RESP_CODE = APIConstants.NOT_FOUND;
                log.info(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_NOT_FOUND + LogConstants.LOG_INFO + LogConstants.LOG_NO_RECORD_FOUND + LogConstants.LOG_STATUS_CODE + RESP_CODE);
            }
            //Get operations
//            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setDataList(dashboardService.wareHouseAndProductWiseInventories(mvnoId));
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getCustomersApprovals")
    public GenericDataDTO getCustomersApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getCustomersApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customersService.getCustomersApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getPlanApprovalsList")
    public GenericDataDTO getPlanApprovalsList(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        String SUBMODULE = MODULE + " [getPlanApprovalsList()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return postpaidPlanService.getPlanApprovalsList(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder(),mvnoId);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch getPlanApprovalsList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getPlanGroupApprovalsList")
    public GenericDataDTO getPlanGroupApprovalsList(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId) {
        String SUBMODULE = MODULE + " [getPlanGroupApprovalsList()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return planGroupService.getPlanGroupApprovalsList(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder(),mvnoId);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch getPlanGroupApprovalsList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getPaymentApprovalsList")
    public GenericDataDTO getPaymentApprovalsList(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getPaymentApprovalsList()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return creditDocService.getPaymentApprovalsList(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch getPaymentApprovalsList" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getCustomersApprovalsForTermination")
    public GenericDataDTO getCustomersApprovalsForTermination(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getCustomersApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customersService.getCustomersApprovalsForTermination(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

//    @PostMapping(value = "/approval/getTicketApprovals")
//    public GenericDataDTO getTicketApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO,HttpServletRequest req) {
//        String SUBMODULE = MODULE + " [getCustomersApprovals()] ";
//        TraceContext traceContext = tracer.currentSpan().context();
//        MDC.put("type", "Fetch");
//        MDC.put("userName", getLoggedInUser().getUsername());
//MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));//        MDC.put("spanId",traceContext.spanIdString());
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            return caseService.getTicketApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
//        } catch (Exception ex) {
//            genericDataDTO.setResponseMessage(ex.getMessage());
//            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//            Integer  RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
//             log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "  Unable to fetch  wareHouse And ProductWiseInventories " + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
//        }
//         finally {
//            MDC.remove("type");
//            MDC.remove("userName");
//            MDC.remove("traceId");
//            MDC.remove("spanId");
//        }
//        return genericDataDTO;
//    }

    @PostMapping(value = "/approval/getBillToOrgApprovals")
    public GenericDataDTO getBillToOrgApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getBillToOrgApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return debitDocService.getBillToOrgApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getChangeDiscountApprovals")
    public GenericDataDTO getChangeDiscountApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getChangeDiscountApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return custPlanMappingService.getChangeDiscountApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getShiftLocationApprovals")
    public GenericDataDTO getShiftLocationApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getShiftLocationApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customerAddressService.getShiftLocationApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getPartnerPaymentApprovals")
    public GenericDataDTO getPartnerPaymentApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getChangeDiscountApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return partnerPaymentService.getPartnerPaymentApprovals(paginationRequestDTO.getFilters(), paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(), paginationRequestDTO.getSortOrder());
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getLeadApprovals")
    public GenericDataDTO getLeadApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getChangeDiscountApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return dashboardService.getLeadApprovals(paginationRequestDTO);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch lead approvals" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getCustomerDocForApprovals")
    public GenericDataDTO getCustomerDocForApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getCustomerApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return customerDocDetailsService.getCustomerDocApprovals(paginationRequestDTO);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch customer approvals" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/inventory/getProductQtyByStaff")
    public GenericDataDTO getProductQtyByStaff(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getProductCategoryByOwnerIdAndOwnerType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return dashboardService.getProductQtyByStaff(paginationRequestDTO, mvnoId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch staff And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/inventory/getProductQtyByWarehouse")
    public GenericDataDTO getProductQtyByWarehouse(@RequestBody PaginationRequestDTO paginationRequestDTO, @RequestParam(name = "mvnoId", required = false) Long mvnoId, HttpServletRequest req) {
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        String SUBMODULE = MODULE + " [getProductCategoryByOwnerIdAndOwnerType()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return dashboardService.getProductQtyByWarehouse(paginationRequestDTO, mvnoId);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch staff And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    @PostMapping(value = "/approval/getSpecialPlanMappingApprovals")
    public GenericDataDTO getSpecialPlanMappingApprovals(@RequestBody PaginationRequestDTO paginationRequestDTO, HttpServletRequest req) {
        String SUBMODULE = MODULE + " [getSpecialPlanMappingApprovals()] ";
        TraceContext traceContext = tracer.currentSpan().context();
        MDC.put("type", "Fetch");
        MDC.put("userName", getLoggedInUser().getUsername());
        MDC.put(LogConstants.TRACE_ID, req.getHeader(LogConstants.TRACE_ID));
        MDC.put("spanId", traceContext.spanIdString());
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            return custSpecialPlanMapppingService.getSpecialPlanMappingApprovals(paginationRequestDTO);
        } catch (Exception ex) {
            genericDataDTO.setResponseMessage(ex.getMessage());
            genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            Integer RESP_CODE = APIConstants.INTERNAL_SERVER_ERROR;
            log.error(LogConstants.REQUEST_FROM + req.getHeader("requestFrom") + LogConstants.REQUEST_FOR + "fetch  wareHouse And ProductWiseInventories" + LogConstants.REQUEST_BY + getLoggedInUser().getUsername() + LogConstants.LOG_STATUS + LogConstants.LOG_FAILED + LogConstants.LOG_ERROR + ex.getMessage() + LogConstants.LOG_STATUS_CODE + RESP_CODE);
        } finally {
            MDC.remove("type");
            MDC.remove("userName");
            MDC.remove("traceId");
            MDC.remove("spanId");
        }
        return genericDataDTO;
    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser loggedInUser = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInUser = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
        }
        return loggedInUser;
    }

}
