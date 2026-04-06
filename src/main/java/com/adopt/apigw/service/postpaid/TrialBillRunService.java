package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.TrialBillRun;
import com.adopt.apigw.pojo.SearchTrialBillRun;
import com.adopt.apigw.pojo.TriggerTrialBillRun;
import com.adopt.apigw.pojo.api.SearchTrialBillRunPojo;
import com.adopt.apigw.pojo.api.TrialBillRunPojo;
import com.adopt.apigw.repository.postpaid.TrialBillRunRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.OptionalBooleanBuilder;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.adopt.apigw.model.postpaid.QTrialBillRun;
import com.querydsl.core.types.Predicate;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class TrialBillRunService extends AbstractService<TrialBillRun, TrialBillRunPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(TrialBillRunService.class);

    @Autowired
    private TrialBillRunRepository entityRepository;

    @Override
    protected JpaRepository<TrialBillRun, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<TrialBillRun> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '1')")
    public List<TrialBillRun> getAllActiveEntities() {
        return entityRepository.findByStatus("Y");
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '1')")
    public List<TrialBillRun> getAllEntities() {
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '1')")
    public List<TrialBillRun> findBillRunDetails(SearchTrialBillRun sBillRun) {

        QTrialBillRun billRun = QTrialBillRun.trialBillRun;

        Predicate builder = new OptionalBooleanBuilder(billRun.isNotNull())
                .notEmptyAnd(billRun.status::equalsIgnoreCase, sBillRun.getBillrunstatus())
                .notEmptyAnd(billRun.id::eq, sBillRun.getBillrunid())
                //  			.notEmptyAnd(DateFormatUtils. billRun.createdate::after, sBillRun.getBillfromdate())
                //  			.notEmptyAnd(billRun.createdate::before, sBillRun.getBilltodate())
                //  			.
                .build();

        return (List<TrialBillRun>) entityRepository.findAll(builder);
//    	return (List<TrialBillRun>) entityRepository.findAll(billRunStatusMatches);

    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '1')")
    public List<TrialBillRun> findById(Integer billRunId) {
        List<Integer> myList = new ArrayList<Integer>();
        myList.add(billRunId);

        return entityRepository.findAllById(myList);
    }

    public String performBillRun(String billRunDate, Customers customer) throws Exception {
        String billRunId = null;
        String billRunURL = null;

        if (customer == null) {

//    		billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebill/{billrundate}";

            billRunURL = CommonConstants.TRIAL_BILL_RUN_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                    .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                    .replace("{billrundate}", billRunDate);

        } else {
            billRunURL = CommonConstants.TRIAL_BILL_RUN_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                    .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                    .replace("{billrundate}", billRunDate)
                    .replace("{custid}", String.valueOf(customer.getId()));


            //.replace("{custid}", String.valueOf(customer.getId()));
            //billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebillcust/{billrundate}/{custid}";
//        	billRunURL=billRunURL.replace("{billrundate}", billRunDate).replace("{custid}", String.valueOf(customer.getId()));
//        	billRunURL=billRunURL.replace("{billrundate}", billRunDate).replace("{custid}", "2");
        }

        logger.info("performBillRun() BillURL:" + billRunURL);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .writeTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build();
        Request httpRequest = new Request.Builder()
                .url(billRunURL)
                .build();
        try {
            Response resposne = client.newCall(httpRequest).execute();
            ObjectMapper mapper = new ObjectMapper();
            String responseJson = resposne.body().string();
            logger.info("performBillRun() Response:" + responseJson);
            Map<String, String> map = mapper.readValue(responseJson, Map.class);
            if (map != null) {
                Object obj = map.get("responseObject");
                billRunId = String.valueOf(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return billRunId;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '2')")
    public boolean generateInvoice(String billRunId) throws Exception {
        String generatePDFURL = CommonConstants.TRIAL_GERERATE_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{billrunid}", billRunId);

        //String generatePDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatepdf/{billrunid}";
        //generatePDFURL=generatePDFURL.replace("{billrunid}",billRunId);

        logger.info("genreateInvoices() generatePDFURL:" + generatePDFURL);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(generatePDFURL)
                    .build();
            Response resposne = client.newCall(httpRequest).execute();
            logger.info("genreateInvoices() resposne:" + resposne.body().string());
            bStatus = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '2')")
    public boolean revertbillrun(String billRunId) throws Exception {
        String revertbillrunrul = CommonConstants.TRIAL_REVERT_BILLRUN_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{billrunid}", billRunId);

        //String generatePDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatepdf/{billrunid}";
        //generatePDFURL=generatePDFURL.replace("{billrunid}",billRunId);

        logger.info("revertbilrun() revertbillrunrul:" + revertbillrunrul);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();
            Request httpRequest = new Request.Builder()
                    .url(revertbillrunrul)
                    .build();
            Response resposne = client.newCall(httpRequest).execute();
            logger.info("revertbillrun() resposne:" + resposne.body().string());
            bStatus = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }

//    public boolean emailInvoices(String billRunId) throws Exception{
//    	String emailPDFURL=CommonConstants.EMAIL_INVOICE_URL.replace("{server}", CommonUtils.getInvoiceServer().getServerip())
//				   .replace("{port}",CommonUtils.getInvoiceServer().getWebport())
//				   .replace("{billrunid}",billRunId);
//
////    	String emailPDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/emailpdf/{billrunid}";
////    	emailPDFURL=emailPDFURL.replace("{billrunid}",billRunId);
//
//    	logger.info("emailInvoices() emailPDFURL:"+emailPDFURL);
//
//    	boolean bStatus=false;
//    	try {
//
//        	OkHttpClient client = new OkHttpClient.Builder()
//        		    .connectTimeout(100, TimeUnit.SECONDS)
//        		    .writeTimeout(100, TimeUnit.SECONDS)
//        		    .readTimeout(100, TimeUnit.SECONDS)
//        		    .build();
//        	
//    		Request httpRequest = new Request.Builder()
//	    			.url(emailPDFURL)
//	    			.build();
//    		Response resposne = client.newCall(httpRequest).execute();
//        	logger.info("emailInvoices() resposne:"+resposne.body().string());
//
//    		bStatus=true;   		
//    	}catch(Exception e) {
//    		e.printStackTrace();
//    		throw e;
//    	}
//    	return bStatus;
//    }

    public List<TrialBillRun> searchTrialBillRun(SearchTrialBillRunPojo searchTrialBillRunPojo) {
    	LocalDate startDate = null;
        LocalDate endDate = null;
        if (searchTrialBillRunPojo.getBillfromdate() != null) {
            startDate = LocalDate.parse(searchTrialBillRunPojo.getBillfromdate());
        }
        if (searchTrialBillRunPojo.getBilltodate() != null) {
            endDate = LocalDate.parse(searchTrialBillRunPojo.getBilltodate());
        }

        logger.info("date is ::::::" + startDate);
        QTrialBillRun qTrialBillRun = QTrialBillRun.trialBillRun;

        Predicate builder1 = qTrialBillRun.isNotNull()
                .andAnyOf(
                        // qTrialBillRun.createdate.between(startDate, endDate),
                        (searchTrialBillRunPojo.getBillrunid() != null ? qTrialBillRun.id.eq(searchTrialBillRunPojo.getBillrunid()) : null),
                        (startDate != null ? qTrialBillRun.createdate.eq(startDate.atStartOfDay()) : null),
                        (endDate != null ? qTrialBillRun.createdate.eq(endDate.atStartOfDay()) : null),
                        (searchTrialBillRunPojo.getBillrunstatus() != null ? qTrialBillRun.status.equalsIgnoreCase(searchTrialBillRunPojo.getBillrunstatus()) : null));
        return (List<TrialBillRun>) entityRepository.findAll(builder1);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '2')")
    public SearchTrialBillRun getSearchTrialBillRunForAdd() {
        return new SearchTrialBillRun();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '2')")
    public TriggerTrialBillRun getTriggerTrialBillRunForAdd() {
        return new TriggerTrialBillRun();
    }


    public TrialBillRunPojo convertTrialBillRunModelToTrialBillRunPojo(TrialBillRun trialBillRun) {

        TrialBillRunPojo pojo = null;
        if (trialBillRun != null) {
            pojo = new TrialBillRunPojo();
            pojo.setId(trialBillRun.getId());
            pojo.setAmount(trialBillRun.getAmount());
            pojo.setBillruncount(trialBillRun.getBillruncount());
            pojo.setBillrunfinishdate(trialBillRun.getBillrunfinishdate());
            pojo.setCreatedate(trialBillRun.getCreatedate());
            pojo.setRundate(trialBillRun.getRundate());
            pojo.setStatus(trialBillRun.getStatus());
        }
        return pojo;
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialBillRun', '1')")
    public List<TrialBillRunPojo> convertResponseModelIntoPojo(List<TrialBillRun> trialBillRunList) {
        List<TrialBillRunPojo> pojoListRes = new ArrayList<TrialBillRunPojo>();
        if (trialBillRunList != null && trialBillRunList.size() > 0) {
            for (TrialBillRun trialBillRun : trialBillRunList) {
                pojoListRes.add(convertTrialBillRunModelToTrialBillRunPojo(trialBillRun));
            }
        }
        return pojoListRes;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Trial Bill Run");
        List<TrialBillRunPojo> trialBillRunPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, TrialBillRunPojo.class, trialBillRunPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<TrialBillRunPojo> trialBillRunPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, TrialBillRunPojo.class, trialBillRunPojos, null);
    }
}
