package com.adopt.apigw.soap.MtnDebitRequest;//package com.adopt.apigw.soap.MtnDebitRequest;
//
//import javax.xml.bind.annotation.*;
//
//@XmlRootElement(name = "debitcompletedrequest")
//public class debitcompletedrequest {
//
//    @XmlElement(required = true)
//    protected String transactionid;
//
//    @XmlElement(required = true)
//    protected String externaltransactionid;
//
//    protected receiverinfo receiverinfo;
//
//    @XmlElement(required = true)
//    protected String status;
//
//    // Getters and Setters (required for XML serialization/deserialization)
//
//    @XmlElement
//    public String getTransactionid() {
//        return transactionid;
//    }
//
//    public void setTransactionid(String transactionid) {
//        this.transactionid = transactionid;
//    }
//
//    @XmlElement
//    public String getExternaltransactionid() {
//        return externaltransactionid;
//    }
//
//    public void setExternaltransactionid(String externaltransactionid) {
//        this.externaltransactionid = externaltransactionid;
//    }
//
//    @XmlElement
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    @XmlElement
//    public com.adopt.apigw.soap.MtnDebitRequest.receiverinfo getReceiverinfo() {
//        return receiverinfo;
//    }
//
//    public void setReceiverinfo(com.adopt.apigw.soap.MtnDebitRequest.receiverinfo receiverinfo) {
//        this.receiverinfo = receiverinfo;
//    }
//}
