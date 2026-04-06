package com.adopt.apigw.modules.subscriber.service;

import lombok.SneakyThrows;

import java.util.List;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.service.postpaid.BillRunService;

public class ReceiptThread implements Runnable {

    private BillRunService billRunService;
    private List<CreditDocument> creditDocumentList;

    public ReceiptThread(BillRunService billRunService, List<CreditDocument> creditDocumentList) {
        this.billRunService = billRunService;
        this.creditDocumentList = creditDocumentList;
    }

    //Call Bill Run
    public void billRunForCreditDoc(List<CreditDocument> creditDocumentList) throws Exception {
        try {
            if (null != creditDocumentList && 0 < creditDocumentList.size()) {
                for (CreditDocument creditDocument : creditDocumentList) {
                    Thread.sleep(2000);
                    billRunService.generatePaymentReceipt(creditDocument.getId().toString());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error("[ReceiptThread]" + " [billRunForCreditDoc()] " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @SneakyThrows
    @Override
    public void run() {
        billRunForCreditDoc(this.creditDocumentList);
    }
}
