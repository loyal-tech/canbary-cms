package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.model.postpaid.PartnerDebitDocument;
import com.adopt.apigw.model.postpaid.QPartnerDebitDocument;
import com.adopt.apigw.pojo.SearchPartnerCreditDocs;
import com.adopt.apigw.pojo.api.PartnerCommissionPojo;
import com.adopt.apigw.pojo.api.PartnerDebitDocumentPojo;
import com.adopt.apigw.pojo.api.SearchPartnerCreditDocsPojo;
import com.adopt.apigw.repository.postpaid.PartnerCreditDocRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;
import com.querydsl.core.types.Predicate;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PartnerCreditDocService extends AbstractService<PartnerDebitDocument, PartnerDebitDocumentPojo, Integer> {

    @Autowired
    private PartnerCreditDocRepository entityRepository;

    @Autowired
    private PartnerService partnerService;

    @Override
    protected JpaRepository<PartnerDebitDocument, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<PartnerCreditDocument> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

//    public List<PartnerCreditDocument>getAllActiveEntities(){
//    	return entityRepository.findByStatus("Y");
//    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerCreditDocument', '1')")
    public List<PartnerDebitDocument> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerCreditDocument', '1')")
    public List<PartnerDebitDocument> searchByBillRunId(String billRunId) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findByBillrunid(Integer.valueOf(billRunId));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerCreditDocument', '2')")
    public SearchPartnerCreditDocs getSearchPartnerCreditDocsForInvoiceList() {
        return new SearchPartnerCreditDocs();
    }

    public PartnerDebitDocumentPojo convertPartnerCreditDocumentModelToPartnerCreditDocumentPojo(PartnerDebitDocument partnerCreditDocument) throws Exception {

        PartnerDebitDocumentPojo pojo = null;
        if (partnerCreditDocument != null) {
            pojo = new PartnerDebitDocumentPojo();
            pojo.setId(partnerCreditDocument.getId());
            pojo.setDocnumber(partnerCreditDocument.getDocnumber());
            if (partnerCreditDocument.getPartner() != null) {
                pojo.setPartnerPojo(partnerService.convertPartnerModelToPartnerPojo(partnerCreditDocument.getPartner()));
            }
            pojo.setBilldate(partnerCreditDocument.getBilldate());
            pojo.setCreatedate(partnerCreditDocument.getCreatedate());
            pojo.setStartdate(partnerCreditDocument.getStartdate());
            pojo.setEndate(partnerCreditDocument.getEndate());
            pojo.setDuedate(partnerCreditDocument.getDuedate());
            pojo.setLatepaymentdate(partnerCreditDocument.getLatepaymentdate());
            pojo.setSubtotal(partnerCreditDocument.getSubtotal());
            pojo.setTax(partnerCreditDocument.getTax());
            pojo.setDiscount(partnerCreditDocument.getDiscount());
            pojo.setTotalamount(partnerCreditDocument.getTotalamount());
            pojo.setPreviousbalance(partnerCreditDocument.getPreviousbalance());
            pojo.setLatepaymentfee(partnerCreditDocument.getLatepaymentfee());
            pojo.setCurrentcredit(partnerCreditDocument.getCurrentcredit());
            pojo.setCurrentdebit(partnerCreditDocument.getCurrentdebit());
            pojo.setTotaldue(partnerCreditDocument.getTotaldue());
            pojo.setAmountinwords(partnerCreditDocument.getAmountinwords());
            pojo.setDueinwords(partnerCreditDocument.getDueinwords());
            pojo.setBillrunid(partnerCreditDocument.getBillrunid());
            pojo.setBillrunstatus(partnerCreditDocument.getBillrunstatus());
            pojo.setDocument(partnerCreditDocument.getDocument());
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerCreditDocument', '1')")
    public List<PartnerDebitDocumentPojo> convertResponseModelIntoPojo(List<PartnerDebitDocument> partnerCreditDocumentList) throws Exception {
        List<PartnerDebitDocumentPojo> pojoListRes = new ArrayList<PartnerDebitDocumentPojo>();
        if (partnerCreditDocumentList != null && partnerCreditDocumentList.size() > 0) {
            for (PartnerDebitDocument partnerCreditDocument : partnerCreditDocumentList) {
                pojoListRes.add(convertPartnerCreditDocumentModelToPartnerCreditDocumentPojo(partnerCreditDocument));
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.PartnerCreditDocument', '1')")
    public List<PartnerDebitDocument> searchPartnerCommission(SearchPartnerCreditDocsPojo searchPartnerCreditDocsPojo) {
        LocalDateTime startDate = null;
        LocalDateTime endDate = null;
        if (searchPartnerCreditDocsPojo.getBillfromdate() != null) {
            startDate = searchPartnerCreditDocsPojo.getBillfromdate().atStartOfDay();
        }
        if (searchPartnerCreditDocsPojo.getBilltodate() != null) {
            endDate = searchPartnerCreditDocsPojo.getBilltodate().atStartOfDay();
        }

        QPartnerDebitDocument qPartnerCreditDocument = QPartnerDebitDocument.partnerDebitDocument;

        Predicate builder1 = qPartnerCreditDocument.isNotNull()
                .andAnyOf(
                        // qDebitDocument.billdate.between(startDate, endDate),
                        (searchPartnerCreditDocsPojo.getBillrunid() != null ? qPartnerCreditDocument.billrunid.eq(searchPartnerCreditDocsPojo.getBillrunid()) : null),
                        (startDate != null ? qPartnerCreditDocument.startdate.eq(startDate) : null),
                        (endDate != null ? qPartnerCreditDocument.endate.eq(endDate) : null),
                        (searchPartnerCreditDocsPojo.getPartnername() != null ? qPartnerCreditDocument.partner.name.equalsIgnoreCase(searchPartnerCreditDocsPojo.getPartnername()) : null),
                        (searchPartnerCreditDocsPojo.getPartnermobile() != null ? qPartnerCreditDocument.partner.mobile.equalsIgnoreCase(searchPartnerCreditDocsPojo.getPartnermobile()) : null),
                        (searchPartnerCreditDocsPojo.getDocnumber() != null ? qPartnerCreditDocument.docnumber.equalsIgnoreCase(searchPartnerCreditDocsPojo.getDocnumber()) : null));
        return (List<PartnerDebitDocument>) entityRepository.findAll(builder1);
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Partner Credit Doc");
        List<PartnerDebitDocumentPojo> partnerCreditDocumentPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PartnerDebitDocumentPojo.class, partnerCreditDocumentPojos, null);
    }
    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PartnerDebitDocumentPojo> partnerCreditDocumentPojos = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PartnerDebitDocumentPojo.class, partnerCreditDocumentPojos, null);
    }
}
