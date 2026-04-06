package com.adopt.apigw.soap;


//import com.adopt.apigw.modules.MtnPayment.service.MtnPaymentService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.ws.server.endpoint.annotation.Endpoint;
//@Endpoint
//public class MtnDebitCompletedRequestEndpoint {
//    private static final String NAMESPACE_URI = "http://www.ericsson.com/em/emm/callback/v1_2";
//
//    @Autowired
//    MtnPaymentService mtnPaymentService;


//    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "debitcompletedrequest")
//    @ResponsePayload
//    @Transactional
//    public debitcompletedresponse getdebit(@RequestPayload debitcompletedrequest request) throws Exception {
//        debitcompletedresponse response = new debitcompletedresponse();
//        try {
//            if (request.getStatus().equalsIgnoreCase("SUCCESSFUL")) {
//                String debitcompetedresponse = mtnPaymentService.debitcompleted(request.getExternaltransactionid(), request.getTransactionid(), request.getStatus());
//                if (debitcompetedresponse.equalsIgnoreCase("Success")) {
//                    response.setStatus("200");
//                    response.setMessage("SUCCESS");
//                    response.setTransactionid(request.getTransactionid());
//                } else if (debitcompetedresponse.equalsIgnoreCase("customernotfound")) {
//                    response.setStatus("452");
//                    response.setMessage("TRANSACTION NOT FOUND GIVEN TRANSACTION AND EXTERNAL TRANSACTION ID");
//                    response.setTransactionid(request.getTransactionid());
//                } else if (debitcompetedresponse.equalsIgnoreCase("IntiatePayment")) {
//                    response.setStatus("453");
//                    response.setMessage("NO PENDING PAYMENT FOUND");
//                    response.setTransactionid(request.getTransactionid());
//                } else if (debitcompetedresponse.equalsIgnoreCase("paymentdonewithsuccess")) {
//                    response.setStatus("454");
//                    response.setMessage("PAYMENT ALREADY DONE WITH TRANSACTIONAL AND EXTERNALTRANSACTION ID");
//                    response.setTransactionid(request.getTransactionid());
//                } else {
//                    response.setStatus("500");
//                    response.setMessage("ERROR PROCESSING DEBIT COMPLETED REQUEST");
//                    response.setTransactionid(request.getTransactionid());
//                }
//            } else {
//                response.setStatus("400");
//                response.setMessage("SUCCESSFUL RESPONSE NOT FOUND");
//                response.setTransactionid(request.getTransactionid());
//            }
//        } catch (Exception e) {
//            response.setStatus("417");
//            response.setMessage("EXPECTATION FAILED");
//            response.setTransactionid(request.getTransactionid());
//            throw new RuntimeException(e);
//        }
//        return response;
//    }
//}



