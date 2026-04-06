package com.adopt.apigw.utils;

import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.persistence.Transient;
import java.time.LocalDate;

@Component
public class NumberSequenceUtil {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private static final Logger log = LoggerFactory.getLogger(NumberSequenceUtil.class);

    public String getConnectionNumber(Boolean isLCO, Integer partnerId, Integer mvnoId) {
        log.debug("Initializing connection number generation process");
        //TODO commented function for test performance
        String currconnectionNo = "";
//        try {
//            LocalDate current_date = LocalDate.now();
//
//            String query = "SELECT nextvalconnection('connectionno_"+mvnoId+"')";
//            log.debug("query to get next connection number : "+ query);
//            if(isLCO)
//                query = "SELECT nextvalconnection('connectionno_"+mvnoId+"_"+partnerId+"')";
//                log.debug("query to get next connection number : "+ query);
//
//            synchronized (NumberSequenceUtil.class){
//                currconnectionNo= jdbcTemplate.queryForObject(query, String.class).trim();}
//                log.debug("Current connection number : "+ currconnectionNo);
//
//        } catch (Exception ex) {
//            log.error("Error while generating connection number : "+ ex.getMessage());
//            ex.printStackTrace();
//        }
        log.debug("connection number generation process completed");
        return currconnectionNo;
    }

//    public String getConnectionNumberGenerate(Boolean isLCO, Integer partnerId, Integer mvnoId,String mvnoName) {
//        log.debug("Initializing connection number generation process");
//        String currconnectionNo = "";
//        try {
//            String key = isLCO ? "connectionno_" + mvnoId + "_" + partnerId : "connectionno_" + mvnoId + mvnoName + "connectionno_";
//            String query = "SELECT adoptconvergebss.nextvalconnection(?)";
//
//            log.debug("query to get next connection number using key: " + key);
//
//            synchronized (NumberSequenceUtil.class) {
//                currconnectionNo = jdbcTemplate.queryForObject(query, new Object[]{key}, String.class);
//            }
//
//            if (currconnectionNo != null) {
//                currconnectionNo = currconnectionNo.trim();
//                log.debug("Current connection number: " + currconnectionNo);
//            } else {
//                log.warn("Connection number function returned null for key: " + key);
//            }
//
//        } catch (Exception ex) {
//            log.error("Error while generating connection number: " + ex.getMessage(), ex);
//        }
//
//        log.debug("Connection number generation process completed");
//        return currconnectionNo;
//    }

    public String getConnectionNumberGenerate(Boolean isLCO, Integer partnerId, Integer mvnoId, String mvnoName) {
        log.debug("Initializing connection number generation process");

        String connectionCore = "";
        try {
            String keyBase;
            if (mvnoName != null && !mvnoName.trim().isEmpty()) {
                keyBase = mvnoName.trim();
            } else if (mvnoId != null) {
                keyBase = "MVNO_" + mvnoId;
            } else {
                keyBase = "DEFAULT";
            }

            log.debug("Using sequence key base: {}", keyBase);

            String query = "SELECT adoptconvergebss.nextvalconnection(?)";

            synchronized (NumberSequenceUtil.class) {
                connectionCore = jdbcTemplate.queryForObject(query, new Object[]{keyBase}, String.class);
            }

            if (connectionCore != null) {
                connectionCore = connectionCore.trim();
                log.debug("Generated core connection number: {}", connectionCore);
            } else {
                log.warn("Connection number function returned null for key: {}", keyBase);
                return null;
            }
            String prefix = (mvnoName != null) ? mvnoName.trim() : "UNKNOWN";
            String finalConnNo = prefix + "-" + connectionCore;

            log.info("Final connection number generated: {}", finalConnNo);
            return finalConnNo;

        } catch (Exception ex) {
            log.error("Error while generating connection number: {}", ex.getMessage(), ex);
            return null;
        }
    }

    public String getCreditNoteNumber(Boolean isLCO, Integer partnerId, Integer mvnoId) {
        String currinvoiceNo = null;
        try {
            String query = "SELECT nextvalcreditnote('creditnoteno_" + mvnoId + "')";
            if (isLCO)
                query = "SELECT nextvalcreditnote('creditnoteno_" + mvnoId + "_" + partnerId + "')";

            synchronized (NumberSequenceUtil.class) {
                currinvoiceNo = jdbcTemplate.queryForObject(query, String.class).trim();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return currinvoiceNo;
    }

    public String getPaymentNumber(Boolean isLCO, Integer partnerId, Integer mvnoId) {
        String currinvoiceNo = null;
        try {
            String query = "SELECT nextvalpayment('paymentno_" + mvnoId + "')";
            if (isLCO)
                query = "SELECT nextvalpayment('paymentno_" + mvnoId + "_" + partnerId + "')";

            synchronized (NumberSequenceUtil.class) {
                currinvoiceNo = jdbcTemplate.queryForObject(query, String.class).trim();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return currinvoiceNo;
    }

    @Transient
    public void createSequenceNumberFunctionForMVNO(Mvno pojo) {
        //Payment number
        try {
            String queryForInsertPayment = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('paymentno_" + pojo.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
        //CN number
        try {
            String queryForInsertPayment = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('creditnoteno_" + pojo.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {

            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
        try {
            String queryForInsertConnectionno = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('connectionno_" + pojo.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertConnectionno);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
    }

    @Transient
    public void createInvoiceFunctionForPartner(Partner partner) {

        //Invoice number
        try {
            String queryForInsertSequence = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoiceno_" + partner.getMvnoId() + "_" + partner.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertSequence);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
        //Trial Invoice number
        try {
            String queryForInsertSequenceTrial = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('invoicenotrail_" + partner.getMvnoId() + "_" + partner.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertSequenceTrial);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
    }

    @Transient
    public void createSequenceNumberFunctionForPartner(Partner partner) {
        try {
            String queryForInsertConnectionno = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('connectionno_" + partner.getMvnoId() + "_" + partner.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertConnectionno);
        } catch (Exception ex) {
            System.out.println("Error to create Partner Sequence no function" + ex.getMessage());
        }

        //Payment number
        try {
            String queryForInsertPayment = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('paymentno_" + partner.getMvnoId() + "_" + partner.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
        //CN number
        try {
            String queryForInsertPayment = "INSERT INTO sequence" +
                    "    ( name, increment, min_value, max_value, cur_value ) " +
                    "VALUES " +
                    "    ('creditnoteno_" + partner.getMvnoId() + "_" + partner.getId() + "', 1, 1,9999999,1);";
            jdbcTemplate.execute(queryForInsertPayment);
        } catch (Exception ex) {
            System.out.println("Error to create Mvno sequence no function" + ex.getMessage());
        }
    }
}
