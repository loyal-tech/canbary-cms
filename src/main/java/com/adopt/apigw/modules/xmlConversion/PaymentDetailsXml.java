package com.adopt.apigw.modules.xmlConversion;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.utils.APIConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.CustomerAddress;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.CustomerAddressService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CurrencyUtil;

import java.time.format.DateTimeFormatter;

@Component
public class PaymentDetailsXml {

    @Autowired
    private DebitDocService debitDocService;

    public static String getPaymentDetails(CreditDocument doc, String addressType, CustomerAddress address,DebitDocument docDebit) {

        try {
            StringBuilder stringBuilder = new StringBuilder();
            CustomerAddressService custAddrService = SpringContext.getBean(CustomerAddressService.class);
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
            String version="NEW";
            if (null == address)
                address = custAddrService.findByAddressTypeAndCustomer(addressType, doc.getCustomer(),version);

            //System.out.println("Doc is "+doc+":address:"+address.);

            if (null != doc && null != address) {
                String fullName = "-";
                if (null != doc.getCustomer()) {
                    if (null != doc.getCustomer().getTitle() && !doc.getCustomer().getTitle().isEmpty() && doc.getCustomer().getTitle()
                            .trim().length() > 0) {
                        fullName = doc.getCustomer().getTitle();
                    }
                    if (null != doc.getCustomer().getFirstname() && !doc.getCustomer().getFirstname().isEmpty()
                            && doc.getCustomer().getFirstname().trim().length() > 0) {
                        fullName += " " + doc.getCustomer().getFirstname();
                    }
                    if (null != doc.getCustomer().getLastname() && !doc.getCustomer().getLastname().isEmpty()
                            && doc.getCustomer().getLastname().trim().length() > 0) {
                        fullName += " " + doc.getCustomer().getLastname();
                    }
                }
                stringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                        " <receipt>" +
                        "     <id>" + doc.getId() + "</id>" +
                        "     <customerId>" + doc.getCustomer().getId() + "</customerId>" +
                        "     <customerName>" + fullName + "</customerName>" +
                        "     <number>" + doc.getId() + "</number>" +
                        "     <createDate>" + doc.getCreatedate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</createDate>" +
                        "     <payment>" + doc.getAmount() + "</payment>" +
                        "     <totalAmountInWords>" + CurrencyUtil.convert(Math.round(doc.getAmount())) + "</totalAmountInWords>");
                if (null != doc.getPaydetails1()) {
                    stringBuilder.append("<paymentdetails1>" + doc.getPaydetails1() + "</paymentdetails1>");
                } else {
                    stringBuilder.append("<paymentdetails1>" + "-" + "</paymentdetails1>");
                }
                if (null != doc.getPaydetails2()) {
                    stringBuilder.append("<paymentdetails2>" + doc.getPaydetails2() + "</paymentdetails2>");
                } else {
                    stringBuilder.append("<paymentdetails2>" + "-" + "</paymentdetails2>");
                }
                if (null != doc.getPaydetails3()) {
                    stringBuilder.append("<paymentdetails3>" + doc.getPaydetails3() + "</paymentdetails3>");
                } else {
                    stringBuilder.append("<paymentdetails3>" + "-" + "</paymentdetails3>");
                }
                if (null != doc.getPaydetails4()) {
                    stringBuilder.append("<paymentdetails4>" + doc.getPaydetails4() + "</paymentdetails4>");
                } else {
                    stringBuilder.append("<paymentdetails4>" + "-" + "</paymentdetails4>");
                }
                if (null != doc.getPaymentdate()) {
                    stringBuilder.append("<paymentDate>" + doc.getPaymentdate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) + "</paymentDate>");
                } else {
                    stringBuilder.append("<paymentDate>" + "-" + "</paymentDate>");
                }
                if(docDebit!=null) {
                    stringBuilder.append("<invoiceid>" + docDebit.getDocnumber() + "</invoiceid>");
                    stringBuilder.append("<invoicevalue>" + docDebit.getTotalamount() + "</invoicevalue>");
                    stringBuilder.append("<invoicedue>" + docDebit.getDuedate() + "</invoicedue>");
                    stringBuilder.append("<invoicedate>" + docDebit.getBilldate() + "</invoicedate>");
                }
                else {
                    stringBuilder.append("<invoiceid>-</invoiceid>");
                    stringBuilder.append("<invoicevalue>-</invoicevalue>");
                    stringBuilder.append("<invoicedue>-</invoicedue>");
                    stringBuilder.append("<invoicedate>-</invoicedate>");
                }
                if (null != doc.getCreatedById()) {
                    StaffUser staffUser = staffUserService.get(doc.getCreatedById(),address.getCustomer().getMvnoId());
                    if (null != staffUser)
                        stringBuilder.append("<createBy>" + staffUser.getFullName() + "</createBy>");
                    else
                        stringBuilder.append("<createBy>" + "-" + "</createBy>");
                } else {
                    stringBuilder.append("<createBy>" + "-" + "</createBy>");
                }
                if (null != doc.getPaymode()) {
                    stringBuilder.append("<payMode>" + doc.getPaymode() + "</payMode>");
                } else {
                    stringBuilder.append("<payMode>" + "-" + "</payMode>");
                }
                stringBuilder.append("<referenceno>" + doc.getReferenceno() + "</referenceno>" +
                        "<customerInformation>" +
                        "         <accountnumber xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <accounttype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <authorizationpolicyname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <balance xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <birthdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <brand xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <country xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <createdate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <cui xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <customertype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <email>" + doc.getCustomer().getEmail() + "</email>" +
                        "         <encryptiontype xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <expirydate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <failureattempt xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <firstlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <firstname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <gatewayaddress xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <gender xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <hotspotname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <imei xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <imsi xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastlogintime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastlogouttime xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastmodifieddate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <lastname xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <laststatuschangedate xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <location xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <msisdn xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <outstandingbalance>" + doc.getCustomer().getOutStandingAmount() + "</outstandingbalance>" +
                        "         <phone>" + doc.getCustomer().getPhone() + "</phone>" +
                        "         <qos xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <status xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <subscriberpackage xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <timebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedtotalquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedunusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "         <volumebasedusedquota xsi:nil=\"true\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>" +
                        "     </customerInformation>" +
                        "     <addressDetail>");
                if (null != address.getAddress1()) {
                    stringBuilder.append("<address1>" + address.getAddress1() + "</address1>");
                } else {
                    stringBuilder.append("<address1>" + "-" + "</address1>");
                }
                if (null != address.getAddress2()) {
                    stringBuilder.append("<address2>" + address.getAddress2() + "</address2>");
                } else {
                    stringBuilder.append("<address2>" + "-" + "</address2>");
                }
                if (null != address.getArea()) {
                    stringBuilder.append("<area>" + address.getArea().getName() + "</area>");
                } else {
                    stringBuilder.append("<area>" + "-" + "</area>");
                }
                if (null != address.getLandmark()) {
                    stringBuilder.append("<landmark>" + address.getLandmark() + "</landmark>");
                } else {
                    stringBuilder.append("<landmark>" + "-" + "</landmark>");
                }
                if (null != address.getAddressType()) {
                    stringBuilder.append("<addresstype>" + address.getAddressType() + "</addresstype>");
                } else {
                    stringBuilder.append("<addresstype>" + "-" + "</addresstype>");
                }
                stringBuilder.append("<city>" + (null != address.getCity() ? address.getCity().getName() : "-") + "</city>" +
                        "         <pincode>" + (null != address.getPincode() ? address.getPincode().getPincode() : "-") + "</pincode>" +
                        "         <state>" + (null != address.getState() ? address.getState().getName() : "-") + "</state>" +
                        "         <country>" + (null != address.getCountry() ? address.getCountry().getName() : "-") + "</country>" +
                        "         <subscriberid>" + doc.getCustomer().getId() + "</subscriberid>" +
                        "     </addressDetail>" +
                  /*  "     <planInformation>" +
                    "         <description>{PLAN_DESC}</description>" +
                    "         <displayname>{PLAN_DISP_NAME}</displayname>" +
                    "         <name>{PLAN_NAME}</name>" +
                    "         <postpaidplanid>{PLAN_ID}</postpaidplanid>" +
                    "     </planInformation>" +*/
                        "     <email>" + doc.getCustomer().getEmail() + "</email>" +
                        "     <phone>" + doc.getCustomer().getPhone() + "</phone>" +
                        " </receipt>");
            }
            return stringBuilder.toString();
        }catch (Exception ex) {
            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, ex.getStackTrace());
        }
        return null;
    }
}
