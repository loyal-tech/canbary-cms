package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.PartnerBillRun;
import com.adopt.apigw.pojo.SearchPartnerBillRun;
import com.adopt.apigw.pojo.TriggerBillRun;
import com.adopt.apigw.pojo.api.PartnerBillRunPojo;
import com.adopt.apigw.pojo.api.SearchPartnerBillRunPojo;
import com.adopt.apigw.repository.postpaid.PartnerBillRunRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;
import com.adopt.apigw.model.postpaid.QPartnerBillRun;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class PartnerBillRunService extends AbstractService<PartnerBillRun, PartnerBillRunPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(PartnerBillRunService.class);

    @Autowired
    private PartnerBillRunRepository entityRepository;

    @Override
    protected JpaRepository<PartnerBillRun, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<PartnerBillRun> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '1')")
    public List<PartnerBillRun> getAllActiveEntities() {
        return entityRepository.findByStatus("Y");
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '1')")
    public List<PartnerBillRun> getAllEntities() {
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '1')")
    public List<PartnerBillRun> findById(Integer billRunId) {
        List<Integer> myList = new ArrayList<Integer>();
        myList.add(billRunId);

        return entityRepository.findAllById(myList);
    }

    public String performBillRun(String billRunDate, Customers customer) throws Exception {
        String billRunId = null;
        String billRunURL = null;

        if (customer == null) {

//    		billRunURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/generatebill/{billrundate}";

            billRunURL = CommonConstants.PBILL_RUN_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                    .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                    .replace("{billrundate}", billRunDate);

        } else {
            billRunURL = CommonConstants.PARTNER_BILL_RUN_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
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

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '2')")
    public boolean generateInvoice(String billRunId) throws Exception {
        String generatePDFURL = CommonConstants.PGERERATE_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
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

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '2')")
    public boolean emailInvoices(String billRunId) throws Exception {
        String emailPDFURL = CommonConstants.PEMAIL_INVOICE_URL.replace("{server}", UtilsCommon.getInvoiceServer().getServerip())
                .replace("{port}", UtilsCommon.getInvoiceServer().getWebport())
                .replace("{billrunid}", billRunId);

//    	String emailPDFURL="http://167.71.236.158:40080/billing-engine-1.0/billingprocess/emailpdf/{billrunid}";
//    	emailPDFURL=emailPDFURL.replace("{billrunid}",billRunId);

        logger.info("emailInvoices() emailPDFURL:" + emailPDFURL);

        boolean bStatus = false;
        try {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(100, TimeUnit.SECONDS)
                    .writeTimeout(100, TimeUnit.SECONDS)
                    .readTimeout(100, TimeUnit.SECONDS)
                    .build();

            Request httpRequest = new Request.Builder()
                    .url(emailPDFURL)
                    .build();
            Response resposne = client.newCall(httpRequest).execute();
            logger.info("emailInvoices() resposne:" + resposne.body().string());

            bStatus = true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return bStatus;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '2')")
    public SearchPartnerBillRun getSearchPartnerBillRunForInvoiceList() {
        return new SearchPartnerBillRun();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '2')")
    public TriggerBillRun getTriggerBillRunForBillRun() {
        return new TriggerBillRun();
    }

    public PartnerBillRunPojo convertPartnerBillRunModelToPartnerBillRunPojo(PartnerBillRun partnerBillRun) {
        PartnerBillRunPojo pojo = null;
        if (partnerBillRun != null) {
            pojo = new PartnerBillRunPojo();
            pojo.setId(partnerBillRun.getId());
            pojo.setAmount(partnerBillRun.getAmount());
            pojo.setBillruncount(partnerBillRun.getBillruncount());
            pojo.setBillrunfinishdate(partnerBillRun.getBillrunfinishdate());
            pojo.setRundate(partnerBillRun.getRundate());
            pojo.setStatus(partnerBillRun.getStatus());
            pojo.setCreatedate(partnerBillRun.getCreatedate());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '1')")
    public List<PartnerBillRunPojo> convertResponseModelIntoPojo(List<PartnerBillRun> partnerBillRunList) {
        List<PartnerBillRunPojo> pojoListRes = new ArrayList<PartnerBillRunPojo>();
        if (partnerBillRunList != null && partnerBillRunList.size() > 0) {
            for (PartnerBillRun partnerBillRun : partnerBillRunList) {
                pojoListRes.add(convertPartnerBillRunModelToPartnerBillRunPojo(partnerBillRun));
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerBillRun', '1')")
    public List<PartnerBillRun> searchPartnerBillRun(SearchPartnerBillRunPojo searchPartnerBillRunPojo) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (searchPartnerBillRunPojo.getBillfromdate() != null) {
            startDate = searchPartnerBillRunPojo.getBillfromdate().atStartOfDay();
        }
        if (searchPartnerBillRunPojo.getBilltodate() != null) {
            endDate = searchPartnerBillRunPojo.getBilltodate().atStartOfDay();
        }

        QPartnerBillRun qPartnerBillRun = QPartnerBillRun.partnerBillRun;

        Predicate builder1 = qPartnerBillRun.isNotNull()
                .andAnyOf(
                        (searchPartnerBillRunPojo.getBillrunid() != null ? qPartnerBillRun.id.eq(searchPartnerBillRunPojo.getBillrunid()) : null),
                        (startDate != null ? qPartnerBillRun.createdate.eq(startDate) : null),
                        (endDate != null ? qPartnerBillRun.createdate.eq(endDate) : null),
                        (searchPartnerBillRunPojo.getBillrunstatus() != null ? qPartnerBillRun.status.equalsIgnoreCase(searchPartnerBillRunPojo.getBillrunstatus()) : null));
        return (List<PartnerBillRun>) entityRepository.findAll(builder1);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Partner Bill Run");
        List<PartnerBillRunPojo> partnerBillRunPojoList =  convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PartnerBillRunPojo.class, partnerBillRunPojoList, null);
    }
    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PartnerBillRunPojo> partnerBillRunPojoList =  convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PartnerBillRunPojo.class, partnerBillRunPojoList, null);
    }
}
