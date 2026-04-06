package com.adopt.apigw.modules.placeOrder.controller;

import com.adopt.apigw.modules.paymentGatewayMaster.controller.PaymentGatewayController;
import com.adopt.apigw.utils.APIConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.constants.UrlConstants;
import com.adopt.apigw.core.controller.ExBaseAbstractController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.placeOrder.model.OrderDTO;
import com.adopt.apigw.modules.placeOrder.service.OrderService;
import com.adopt.apigw.modules.placeOrder.service.PurchaseThread;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.service.PurchaseDetailsService;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.postpaid.CustomerLedgerDtlsService;
import com.adopt.apigw.service.postpaid.CustomerLedgerService;
import com.adopt.apigw.utils.PropertyReaderUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping(path = UrlConstants.BASE_API_URL + UrlConstants.PLACE_ORDER)
public class OrderController extends ExBaseAbstractController<OrderDTO> {
    private static String MODULE = " [OrderController] ";
    @Autowired
    private OrderService orderService;
    @Autowired
    private PurchaseDetailsService purchaseDetailsService;
    @Autowired
    private SubscriberService subscriberService;
    @Autowired
    private CustomersService customersService;
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private PartnerLedgerService partnerLedgerService;
    @Autowired
    private PartnerPaymentService partnerPaymentService;
    @Autowired
    private CustomerLedgerService customerLedgerService;
    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;
    @Autowired
    private AuditLogService auditLogService;

    public OrderController(OrderService service) {
        super(service);
    }
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    @PostMapping("/place")
    public GenericDataDTO placeOrder(@RequestBody OrderDTO requestDTO,@RequestHeader(value="rf",defaultValue = "bss") String requestFrom,HttpServletRequest req) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            genericDataDTO.setData(orderService.placeOrder(requestDTO,requestFrom,req));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
            logger.info("Order With customer Id  "+requestDTO.getCustId()+" is successfull :  request: { From : {}, Request Url : {}}; Response : {{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            logger.error("unable to fetch Payment with user Id : "+requestDTO.getCustId()+" :  request: { From : {}, Request Url : {}}; Response : {{};Exception:{}}", getModuleNameForLog(),genericDataDTO.getResponseMessage(),genericDataDTO.getResponseCode(),e.getStackTrace());
        }
        return genericDataDTO;
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = {MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public void processOrder(@RequestParam Map<String, Object> response, HttpServletResponse httpResponse, HttpServletRequest request) throws IOException {
        ApplicationLogger.logger.info("CONT PG RES :: " + response);
        Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
        String serverurl = properties.getProperty(PGConstants.PG_CONFIG_PAYU_SERVER_URL);
        try {
            PurchaseDetailsDTO dto = orderService.processPayment(response, request);
            Runnable purchaseRunnable = new PurchaseThread(dto, purchaseDetailsService
                    , subscriberService, customersService, orderService, partnerLedgerDetailsService
                    , partnerLedgerService, partnerPaymentService, customerLedgerService, customerLedgerDtlsService, request);

            SecurityContext context = SecurityContextHolder.getContext();
            DelegatingSecurityContextRunnable wrappedRunnable =
                    new DelegatingSecurityContextRunnable(purchaseRunnable, context);

            Thread invoiceThread = new Thread(wrappedRunnable);
            invoiceThread.start();
            final String clientURL = serverurl + PGConstants.CLIENT_REDIRECT_URL.replace("{TXNID}", dto.getTransid());
            httpResponse.sendRedirect(clientURL);
            logger.info("Fetching  profile with name "+dto.getCustName()+" is successfull :   Response : {{}}", getModuleNameForLog(), APIConstants.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("unable to fetch pprocess for customer  "+response+" :   Response : {{}{};Exception: {}}", getModuleNameForLog(),APIConstants.FAIL,e.getStackTrace());
            httpResponse.sendRedirect(serverurl + PGConstants.CLIENT_REDIRECT_URL.replace("{TXNID}", null));
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[(OrderController)]";
    }
}
