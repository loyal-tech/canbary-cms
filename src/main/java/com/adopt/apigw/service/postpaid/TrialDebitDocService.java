package com.adopt.apigw.service.postpaid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.rabbitMq.message.CustomerBillingMessage;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.utils.CommonConstants;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import com.adopt.apigw.model.postpaid.QTrialDebitDocument;
import com.adopt.apigw.model.postpaid.TrialDebitDocument;
import com.adopt.apigw.pojo.SearchTrialDebitDocs;
import com.adopt.apigw.pojo.api.SearchTrialDebitDocsPojo;
import com.adopt.apigw.pojo.api.TrialDebitDocumentPojo;
import com.adopt.apigw.repository.postpaid.TrialDebitDocRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.radius.AbstractService;
import com.google.common.collect.Lists;
import com.itextpdf.text.Document;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

@Service
public class TrialDebitDocService extends AbstractService<TrialDebitDocument, TrialDebitDocumentPojo, Integer> {

    @Autowired
    private TrialDebitDocRepository entityRepository;

    @Autowired
    private CustomersService customersService;
    @Autowired
    TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    private CustomersRepository customersRepository;

    @Override
    protected JpaRepository<TrialDebitDocument, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocument> getAllEntities(Integer pageNumber, int pageSize) {
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocument> searchByBillRunId(String billRunId) {
        return entityRepository.findByBillrunid(Integer.valueOf(billRunId));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '2')")
    public TrialDebitDocument getById(Integer id) {
        return entityRepository.getOne(id);
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '2')")
    public SearchTrialDebitDocs getSearchTrialDebitDocsForInvoice() {
        return new SearchTrialDebitDocs();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '1')")
    public Page<TrialDebitDocumentPojo> searchTrialInvoice(SearchTrialDebitDocsPojo searchTrialDebitDocsPojo, PaginationRequestDTO paginationDTO) throws Exception {
        pageRequest = generatePageRequest(paginationDTO.getPage(), paginationDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);

        LocalDate startDate = searchTrialDebitDocsPojo.getBillfromdate();
    	LocalDate endDate = searchTrialDebitDocsPojo.getBilltodate();

        QTrialDebitDocument qTrialDebitDocument = QTrialDebitDocument.trialDebitDocument;
        BooleanExpression exp = qTrialDebitDocument.isNotNull();

        if (searchTrialDebitDocsPojo.getBillrunid() != null) {
            exp = exp.and(qTrialDebitDocument.billrunid.eq(searchTrialDebitDocsPojo.getBillrunid()));
        }

        if (startDate != null) {
            exp = exp.and(qTrialDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
        }
        if (endDate !=null) {
        	exp = exp.and(qTrialDebitDocument.billdate.loe(endDate.atTime(23,59, 59)));
        }
        if (searchTrialDebitDocsPojo.getCustname() != null && !searchTrialDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
        	exp = exp.and(qTrialDebitDocument.customer.firstname.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustname()));
        }
        if (searchTrialDebitDocsPojo.getCustname() != null && !searchTrialDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
        	exp = exp.and(qTrialDebitDocument.customer.lastname.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustname()));
        }
        if (searchTrialDebitDocsPojo.getCustmobile() != null && !searchTrialDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
        	exp = exp.and(qTrialDebitDocument.customer.mobile.equalsIgnoreCase(searchTrialDebitDocsPojo.getCustmobile()));
        }

        if(searchTrialDebitDocsPojo.getCustomerid()!=null) {
        	exp = exp.and(qTrialDebitDocument.customer.eq(customersRepository.findById(searchTrialDebitDocsPojo.getCustomerid()).get()));
        }
        
        if (searchTrialDebitDocsPojo.getDocnumber() != null && !searchTrialDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
        	exp = exp.and(qTrialDebitDocument.docnumber.equalsIgnoreCase(searchTrialDebitDocsPojo.getDocnumber()));
        }

        Predicate builder1 = exp;
        Page response = entityRepository.findAll(builder1, pageRequest);
        List<TrialDebitDocumentPojo> trialDebitDocumentPojos = convertResponseModelIntoPojo(response.getContent());
        return new PageImpl<>(trialDebitDocumentPojos,pageRequest, response.getTotalElements());
    }

    public TrialDebitDocumentPojo convertTrialDebitDocumentModelToTrialDebitDocumentPojo(TrialDebitDocument trialDebitDocument) throws Exception {

        TrialDebitDocumentPojo pojo = null;
        if (trialDebitDocument != null) {
            pojo = new TrialDebitDocumentPojo();
            pojo.setId(trialDebitDocument.getId());
            pojo.setDocnumber(trialDebitDocument.getDocnumber());
            if (trialDebitDocument.getCustomer() != null) {
                pojo.setCustomerPojo(customersService.convertCustomersModelToCustomersPojo(trialDebitDocument.getCustomer()));
            }
            pojo.setBilldate(trialDebitDocument.getBilldate());
            pojo.setCreatedate(trialDebitDocument.getCreatedate());
            pojo.setStartdate(trialDebitDocument.getStartdate());
            pojo.setEndate(trialDebitDocument.getEndate());
            pojo.setDuedate(trialDebitDocument.getDuedate());
            pojo.setLatepaymentdate(trialDebitDocument.getLatepaymentdate());
            pojo.setSubtotal(trialDebitDocument.getSubtotal());
            pojo.setTax(trialDebitDocument.getTax());
            pojo.setDiscount(trialDebitDocument.getDiscount());
            pojo.setTotalamount(trialDebitDocument.getTotalamount());
            pojo.setPreviousbalance(trialDebitDocument.getPreviousbalance());
            pojo.setLatepaymentfee(trialDebitDocument.getLatepaymentfee());
            pojo.setCurrentcredit(trialDebitDocument.getCurrentcredit());
            pojo.setCurrentdebit(trialDebitDocument.getCurrentdebit());
            pojo.setTotaldue(trialDebitDocument.getTotaldue());
            pojo.setAmountinwords(trialDebitDocument.getAmountinwords());
            pojo.setDueinwords(trialDebitDocument.getDueinwords());
            pojo.setBillrunid(trialDebitDocument.getBillrunid());
            pojo.setBillrunstatus(trialDebitDocument.getBillrunstatus());
            pojo.setDocument(trialDebitDocument.getDocument());
            pojo.setCreatedByName(trialDebitDocument.getCreatedByName());
            pojo.setLastModifiedByName(trialDebitDocument.getLastModifiedByName());
            pojo.setBillableToName(trialDebitDocument.getBillableToName());
            if (trialDebitDocument.getCustomer() != null) {
                pojo.setCustid(trialDebitDocument.getCustomer().getId());
                pojo.setCustomerName(trialDebitDocument.getCustomer().getFullName());
                pojo.setCustType(trialDebitDocument.getCustomer().getCusttype());
            }

            pojo.setDebitDocDetails(trialDebitDocument.getTrialDebitDocumentDetails());


            pojo.setPaymentStatus("Pending");

            pojo.setDebitDocDetails(trialDebitDocument.getTrialDebitDocumentDetails());


        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.TrialDebitDocument', '1')")
    public List<TrialDebitDocumentPojo> convertResponseModelIntoPojo(List<TrialDebitDocument> trialDebitDocumentList) throws Exception {
        List<TrialDebitDocumentPojo> pojoListRes = new ArrayList<TrialDebitDocumentPojo>();
        if (trialDebitDocumentList != null && trialDebitDocumentList.size() > 0) {
            for (TrialDebitDocument trialDebitDocument : trialDebitDocumentList) {
                pojoListRes.add(convertTrialDebitDocumentModelToTrialDebitDocumentPojo(trialDebitDocument));
            }
        }
        return pojoListRes;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Trial Debit Doc");
        List<TrialDebitDocumentPojo> trialDebitDocumentPojos =  convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, TrialDebitDocumentPojo.class, trialDebitDocumentPojos, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<TrialDebitDocumentPojo> trialDebitDocumentPojos =  convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, TrialDebitDocumentPojo.class, trialDebitDocumentPojos, null);
    }

    public void saveTrialDebitDoc(CustomerBillingMessage message )
    {
        TrialDebitDocument trialDebitDocument = new TrialDebitDocument();
        trialDebitDocRepository.save(trialDebitDocument);
    }
}
