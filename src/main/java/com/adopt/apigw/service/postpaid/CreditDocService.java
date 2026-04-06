package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.Creditdoc.CreditDocChargeRelMapper;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.creditdoc.CreditDocChargeRel;
import com.adopt.apigw.model.creditdoc.CreditDocTaxRel;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import com.adopt.apigw.modules.BankManagement.domain.QBankManagement;
import com.adopt.apigw.modules.BankManagement.repository.BankManagementRepository;
import com.adopt.apigw.modules.BankManagement.service.BankManagementService;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.CommonList.domain.CommonList;
import com.adopt.apigw.modules.CommonList.repository.CommonListRepository;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerChargeDBR;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.domain.QCustomerChargeDBR;
import com.adopt.apigw.modules.CustomerDBR.domain.QCustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerChargeDBRRepository;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingService;
import com.adopt.apigw.modules.Mvno.domain.QMvno;
import com.adopt.apigw.modules.RvenueClient.RevenueClient;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.Teams.repository.TeamHierarchyMappingRepo;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.staffLedgerDetails.Service.StaffLedgerDetailsService;
import com.adopt.apigw.modules.staffLedgerDetails.entity.StaffLedgerDetails;
import com.adopt.apigw.modules.staffLedgerDetails.repository.StaffLedgerDetailsRepository;
import com.adopt.apigw.modules.subscriber.model.PaymentHistoryDTO;
import com.adopt.apigw.modules.subscriber.service.SubscriberService;
import com.adopt.apigw.modules.workflow.domain.WorkflowAssignStaffMapping;
import com.adopt.apigw.modules.workflow.repository.WorkflowAssignStaffMappingRepo;
import com.adopt.apigw.modules.xmlConversion.PaymentDetailsXml;
import com.adopt.apigw.pojo.CreditDoc.CreditDocUpdateDTO;
import com.adopt.apigw.pojo.PaymentListPojo;
import com.adopt.apigw.pojo.RecordPayment;
import com.adopt.apigw.pojo.SearchPayment;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.common.*;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CustomerThreadService;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.*;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.adopt.apigw.core.utillity.log.ApplicationLogger.logger;

@Slf4j
@Service
public class CreditDocService extends AbstractService<CreditDocument, CreditDocumentPojo, Integer> {

    @Autowired
    private CreditDocRepository entityRepository;

    @Autowired
    private WorkflowAuditRepository workflowAuditRepository;

    @Autowired
    private CustomersService customerService;


    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private PartnerRepository partnerRepository;

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    private CreditDocService entityService;

    @Autowired
    private CustomerLedgerService ledgerService;

    @Autowired
    private CustomerLedgerDtlsService ledgerDtlsService;

    @Autowired
    private CustomerAddressService custAddrService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private CreditDocumentMapper creditDocumentMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerLedgerRepository ledgerRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocumentRepository;

    @Autowired
    private BatchPaymentMappingRepository batchPaymentMappingRepository;

    @Autowired
    private BankManagementRepository bankManagementRepository;

    @Autowired
    BankManagementService bankManagementService;

    @Autowired
    CommonListRepository commonListRepository;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    TeamHierarchyMappingRepo teamHierarchyMappingRepo;

    @Autowired
    TeamsRepository teamsRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    StaffUserMapper staffUserMapper;

    @Autowired
    StaffLedgerDetailsRepository staffLedgerDetailsRepository;

    @Autowired
    CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @Autowired
    WorkFlowQueryUtils workFlowQueryUtils;


    @Autowired
    CustomerDBRRepository customerDBRRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;


    @Autowired
    ClientServiceRepository clientServiceRepository;

    @Autowired
    StaffLedgerDetailsService staffLedgerDetailsService;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    PartnerCommissionService partnerCommissionService;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    BranchRepository branchRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private DbrService dbrService;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    private CreditDocChargeRelRepository creditDocChargeRelRepository;

    @Autowired
    private CreditDocChargeRelMapper creditDocChargeRelMapper;

    @Autowired
    private CreditDocTaxRepository creditDocTaxRepository;

    public static final String MODULE = "[CreditDocService]";

    public String PATH;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    EzBillServiceUtility ezBillServiceUtility;
    @Autowired
    private TaxRepository taxRepository;


    @Autowired
    CustomerChargeDBRRepository customerChargeDBRRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    private TaxService taxService;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private BatchPaymentRepository batchPaymentRepository;

    @Autowired
    @Lazy
    private CustomerInventoryMappingService customerInventoryMappingService;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustChargeDetailsRepository custchargedetailsrepository;

    @Autowired
    private CustomerPaymentRepository customerPaymentRepository;

    @Autowired
    private RevenueClient revenueClient;

    @Override
    protected JpaRepository<CreditDocument, Integer> getRepository() {
        return entityRepository;
    }

//    public Page<CreditDocument> searchEntity(String searchText,Integer pageNumber,int pageSize){
// 	   PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
// 	   return entityRepository.searchEntity(searchText,pageRequest);
// 	}

//    public List<CreditDocument>getAllActiveEntities(){
//    	return entityRepository.findByStatus("Y");
//    }

    @Autowired
    WorkflowAssignStaffMappingRepo workflowAssignStaffMappingRepo;

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public List<CreditDocument> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(creditDic -> (creditDic.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || creditDic.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1) && (getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(creditDic.getBuID()))).collect(Collectors.toList());
    }

    public CreditDocument covertPaymentReqToCreditDoc(RecordPayment payment,Integer mvnoId) throws Exception {

        CreditDocument doc = null;
        if (payment != null && payment.getCustomerid() != null) {
            doc = new CreditDocument();
            doc.setCustomer(customerService.getcustForCwsc(Integer.valueOf(payment.getCustomerid()),mvnoId));
            doc.setChequedate(payment.getChequedate());
            doc.setPaymentdate(payment.getPaymentdate());
            doc.setPaymode(payment.getPaymode());
            doc.setAmount(payment.getAmount());
            doc.setStatus(UtilsCommon.PAYMENT_STATUS_PENDING);
            doc.setIsDelete(false);
            doc.setRemarks(payment.getRemark());
            doc.setAdjustedAmount(Double.valueOf(UtilsCommon.INITIAL_PAYMENT_ADJUST));
            doc.setReciptNo(payment.getReciptNo());
            if (payment.getMvnoId() != null) {
                doc.setMvnoId(payment.getMvnoId());
            }
            if (UtilsCommon.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(doc.getPaymode())) {
                doc.setPaydetails1(payment.getBank());
                doc.setPaydetails2(payment.getChequeno());
                doc.setPaydetails3(payment.getChequedate().toString());
                doc.setBranchname(payment.getBranch());
            } else if (UtilsCommon.PAYMENT_MODE_DIRECTDEPOSIT.equalsIgnoreCase(doc.getPaymode().replaceAll("\\s", ""))) {
                doc.setBranchname(payment.getBranch());
            } else {
                doc.setPaydetails4(payment.getReferenceno());
            }
//            if (payment.getInvoiceId() != null) {
//                doc.setInvoiceId(payment.getInvoiceId());
//            }
            if (payment.getPaytype() != null) {
                doc.setPaytype(payment.getPaytype());
            }
            if (payment.getType() != null) {
                doc.setType(payment.getType());
            }

        }
        if (payment.getFilename() != null) {
            doc.setFilename(payment.getFilename());
        }
        if (payment.getUniquename() != null) {
            doc.setUniquename(payment.getUniquename());
        }
        if (payment.getBarteramount() != null) {
            doc.setBarteramount(payment.getBarteramount());
        }
        if (getLoggedInUser() != null) {
            if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
            else doc.setLcoid(null);
        }
        if (payment.getOnlinesource() != null) {
            CommonList commonList = commonListRepository.findByValue(payment.getOnlinesource());
            doc.setOnlinesource(commonList.getText());
        }
        doc.setTdsamount(payment.getTdsAmount());
        doc.setAbbsAmount(payment.getAbbsAmount());

        /*if (payment.getType().equalsIgnoreCase("creditnote")) {
            doc.setCreditdocumentno(getInvoiceNo());
        }*/

        if (payment.getType().equalsIgnoreCase("Payment")) {
//            doc.setCreditdocumentno(getPaymentInvoiceNo());
            Boolean isLCO = doc.getLcoid() != null ? true :false;
            doc.setCreditdocumentno(numberSequenceUtil.getPaymentNumber(isLCO, doc.getLcoid(), doc.getMvnoId()));
        }
        if (payment.getReferenceno() != null) {
            doc.setReferenceno(payment.getReferenceno());
        }
        if (payment.getPaymentreferenceno() != null) {
            doc.setPaymentreferenceno(payment.getPaymentreferenceno());
        }

        return doc;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocument> findCreditDocuments(SearchPayment search, PaginationRequestDTO requestDTO,Integer mvnoId) {
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression exp = qCreditDocument.isNotNull();



        if (search.getType() != null && !"null".equals(search.getType()) && !"".equals(search.getType())) {
            exp = exp.and(qCreditDocument.type.startsWithIgnoreCase(search.getType()));
            if (search.getType().equalsIgnoreCase("payment")) {
                exp = exp.or(qCreditDocument.paytype.startsWithIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL));
            }
        }


        if (search.getReferenceno() != null && !"null".equals(search.getReferenceno()) && !"".equals(search.getReferenceno()) && !search.getCreditDocumentNumber().equalsIgnoreCase("")) {
            exp = exp.and(qCreditDocument.referenceno.contains(search.getReferenceno()));
        }

        if (!StringUtils.isEmpty(search.getPaymode()) && !"-1".equalsIgnoreCase(search.getPaymode())) {
            exp = exp.and(qCreditDocument.paymode.eq(search.getPaymode()));
        }

        if (!StringUtils.isEmpty(search.getPaystatus()) && !"null".equals(search.getReferenceno()) && !"-1".equalsIgnoreCase(search.getPaystatus())) {
            exp = exp.and(qCreditDocument.status.equalsIgnoreCase(search.getPaystatus()));
        }
        if (search.getCustomerid() != null) {
            exp = exp.and(qCreditDocument.customer.eq(customerService.get(Integer.valueOf(search.getCustomerid()),mvnoId)));
        }
        if (search.getStaff() != null) {
            exp = exp.and(qCreditDocument.createdById.eq(search.getStaff()));
        }
        if (!StringUtils.isEmpty(search.getApproveId())) {
            exp = exp.and(qCreditDocument.approverid.eq(search.getApproveId()));
        }

        if (!StringUtils.isEmpty(search.getBranchname())) {
            exp = exp.and(qCreditDocument.branchname.equalsIgnoreCase(search.getBranchname()));
        }

        if (!StringUtils.isEmpty(search.getBuID()) && search.getBuID().size() > 0) {
            exp = exp.and(qCreditDocument.customer.buId.in(search.getBuID()));
        }

        //adi

        try {
            if (search.getUserName() != null) {
                exp = exp.and(qCreditDocument.customer.eq(customerService.getByUserName(String.valueOf(search.getUserName()))));
            }
        } catch (Exception ex) {
            ex.printStackTrace();

        }
        //adi

        if (search.getRecordfromdate() != null && search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.between(search.getRecordfromdate().atStartOfDay(), search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.before(search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordfromdate() != null) {
            exp = exp.and(qCreditDocument.createdate.after(search.getRecordfromdate().atStartOfDay()));
        }
        if (getLoggedInUser().getLco()) exp = exp.and(qCreditDocument.lcoid.eq(getLoggedInUser().getPartnerId()));
        else exp = exp.and(qCreditDocument.lcoid.isNull());

        if (search.getPayfromdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.after(search.getPayfromdate().minusDays(1)));
        }
        if (search.getPaytodate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.before(search.getPaytodate().plusDays(1)));
        }
        if (search.getPaymentdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.eq(search.getPaymentdate()));//adi
        }
        if (search.getChequedate() != null) {
            exp = exp.and(qCreditDocument.chequedate.eq(search.getChequedate()));
        }
        if (search.getPartner() != null) {
            exp = exp.and(qCreditDocument.customer.partner.id.eq(search.getPartner().getId()));
        }
        if (search.getMobileNumber() != null) {
            exp = exp.and(qCreditDocument.customer.mobile.eq(search.getMobileNumber()));
        }
        if (search.getInvoiceNumber() != null && !search.getInvoiceNumber().isEmpty()) {
//            List<DebitDocument> debitDocumentList = IterableUtils.toList(debitDocRepository.findAll(qDebitDocument.docnumber.eq(search.getInvoiceNumber())));
//            for (DebitDocument debitDocument : debitDocumentList) {
//                exp = exp.and(entity.debitDocumentList.contains(debitDocument));
//            }
            Optional<DebitDocument> debitDocument = Optional.of(new DebitDocument());
            BooleanExpression invoiceExp = qDebitDocument.isNotNull().and(qDebitDocument.docnumber.eq(search.getInvoiceNumber()));
            debitDocument = debitDocRepository.findOne(invoiceExp);
            if (debitDocument.isPresent()) {
                Integer invoiceId = debitDocument.get().getId();
                BooleanExpression credDebMapExp = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.eq(invoiceId));
                List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
                creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(credDebMapExp);
                List<Integer> list = creditDebitDocMappings.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
//            creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(invoiceId);
//            List<Integer> list = creditDebitDocMappings.stream().map(CreditDocument::getId).collect(Collectors.toList());
                exp = exp.and(qCreditDocument.id.in(list));
            } else {
                exp = exp.and(qCreditDocument.id.eq(Integer.MAX_VALUE));
            }

        }
        if (search.getChequeNo() != null && !search.getChequeNo().isEmpty()) {
            exp = exp.and(qCreditDocument.paydetails2.eq(search.getChequeNo()));
        }
        if (search.getReceiptNo() != null && !search.getReceiptNo().isEmpty()) {
            exp = exp.and(qCreditDocument.reciptNo.eq(search.getReceiptNo()));
        }
        if (search.getPaydetails1() != null && !search.getPaydetails1().isEmpty()) {
            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.bankname.equalsIgnoreCase(search.getPaydetails1()));
            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

            if (bankManagement != null) {
                exp = exp.and(qCreditDocument.bankManagement.eq(bankManagement.get().getId()));
            }
        }
        if (search.getDestinationBank() != null && !search.getDestinationBank().isEmpty()) {
            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.bankname.equalsIgnoreCase(search.getDestinationBank()));
            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

            if (bankManagement != null) {
                exp = exp.and(qCreditDocument.destinationBank.eq(bankManagement.get().getId()));
            }
        }
        if (search.getPartnerName() != null && !search.getPartnerName().isEmpty()) {
            QPartner qPartner = QPartner.partner;
            BooleanExpression be = qPartner.isNotNull().and(qPartner.isDelete.eq(false).and(qPartner.name.equalsIgnoreCase(search.getPartnerName())));
            exp = exp.and(qCreditDocument.customer.partner.name.eq(partnerRepository.findOne(be).get().getName()));
        } else {
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                exp = exp.and(qCreditDocument.customer.partner.id.eq(getLoggedInUserPartnerId()));
            }
        }
        if (search.getServiceAreaId() != null) {
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression be = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(qServiceArea.id.eq(search.getServiceAreaId())));
            exp = exp.and(qCreditDocument.customer.servicearea.id.eq(search.getServiceAreaId()));
        } else {
            if (getLoggedInUserId() != 1) {
                if (!getServiceAreaIdList().isEmpty())
                    exp = exp.and(qCreditDocument.customer.servicearea.id.in(getServiceAreaIdList()));
            }
        }
        exp = exp.and(qCreditDocument.isDelete.eq(false)).and(qCreditDocument.customer.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (getLoggedInMvnoId(null) != 1) exp = exp.and(qCreditDocument.mvnoId.eq(getLoggedInMvnoId(null)));
        if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff()) && getBUIdsFromCurrentStaff().size() > 0)
            exp = exp.and(qCreditDocument.buID.in(getBUIdsFromCurrentStaff()));
        Predicate builder1 = exp;
        PageRequest pageRequest = null;
        if (requestDTO != null) {
            if (requestDTO.getPage() != null && requestDTO.getPageSize() != null) {
                pageRequest = staffUserService.generatePageRequest(requestDTO.getPage(), requestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
            }
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1) {
            if (pageRequest != null) {
                return entityRepository.findAll(builder1, pageRequest);
            } else {
                return (Page<CreditDocument>) entityRepository.findAll(builder1);
            }
        }
        return (Page<CreditDocument>) Lists.newArrayList(entityRepository.findAll(builder1)).stream().filter(e -> e.getIsDelete() != true && e.getCustomer().getIsDeleted() != true).collect(Collectors.toList());
    }

    public CreditDocumentPojo convertCreditDocumentModelToCreditDocumentPojo(CreditDocument creditDocument) throws Exception {
        CreditDocumentPojo pojo = null;
        if (creditDocument != null) {
            pojo = new CreditDocumentPojo();
            pojo.setId(creditDocument.getId());
            if (creditDocument.getCustomer() != null) {
                pojo.setCustId(creditDocument.getCustomer().getId());
                pojo.setCustomerName(creditDocument.getCustomer().getFullName());
                pojo.setCustomer(customerService.convertCustomersModelToCustomersPojo(creditDocument.getCustomer()));
                pojo.setServiceAreaId(creditDocument.getCustomer().getServicearea().getId());
            }

            QBatchPaymentMapping batchPaymentMapping = QBatchPaymentMapping.batchPaymentMapping;
            BooleanExpression expression = batchPaymentMapping.isNotNull().and(batchPaymentMapping.creditDocument.id.eq(creditDocument.getId()).and(batchPaymentMapping.is_deleted.eq(false)));
            if (batchPaymentMappingRepository.count(expression) > 0) pojo.setBatchAssigned(true);
            else pojo.setBatchAssigned(false);
            pojo.setPaymode(creditDocument.getPaymode());
            pojo.setPaymentdate(creditDocument.getPaymentdate());
            if (UtilsCommon.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(creditDocument.getPaymode())) {
                pojo.setChequeNo(creditDocument.getPaydetails2());
                if (creditDocument.getPaydetails3() != null && !creditDocument.getPaydetails3().equalsIgnoreCase("")) {
                    pojo.setChequedate(LocalDate.parse(creditDocument.getPaydetails3(), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            } else if (UtilsCommon.PAYMENT_MODE_ONLINE.equalsIgnoreCase(creditDocument.getPaymode())) {
                pojo.setBankName(creditDocument.getPaydetails1());
                pojo.setBranch(creditDocument.getPaydetails2());
            }
            pojo.setPaydetails1(creditDocument.getPaydetails1());
            pojo.setPaydetails2(creditDocument.getPaydetails2());
            pojo.setPaydetails3(creditDocument.getPaydetails3());
            pojo.setPaydetails4(creditDocument.getPaydetails4());
            if (Objects.isNull(creditDocument.getAmount())) {
                pojo.setAmount(0);
            } else {
                pojo.setAmount(creditDocument.getAmount());
            }
            pojo.setStatus(creditDocument.getStatus());
            pojo.setApproverid(creditDocument.getApproverid());
            pojo.setRemarks(creditDocument.getRemarks());
            pojo.setReferenceno(creditDocument.getReferenceno());
            pojo.setTds_credit_doc_id(creditDocument.getTds_credit_doc_id());
            pojo.setTds_received(creditDocument.getTds_received());
            pojo.setTdsamount(creditDocument.getTdsamount());
            pojo.setTdsflag(creditDocument.getTdsflag());
            pojo.setTds_received_date(creditDocument.getTds_received_date());
            pojo.setIs_reversed(creditDocument.getIs_reversed());
            pojo.setResevrsed_date(creditDocument.getResevrsed_date());
            pojo.setResverse_debitdoc_id(creditDocument.getResverse_debitdoc_id());
            pojo.setBankName(creditDocument.getPaydetails1());
            pojo.setReciptNo(creditDocument.getReciptNo());
            pojo.setLcoId(creditDocument.getLcoid());
            pojo.setApproverid(creditDocument.getApproverid());

            pojo.setDocumentno(creditDocument.getCreditdocumentno());
            pojo.setBuId(getBUIdsFromCurrentStaff());
            if (creditDocument.getInvoiceId() != null) {
                pojo.setInvoiceId(creditDocument.getInvoiceId());
                Optional<DebitDocument> debitDocument = debitDocRepository.findById(creditDocument.getInvoiceId());
                if (debitDocument.isPresent()) pojo.setInvoiceNumber(debitDocument.get().getDocnumber());

            }
            if (creditDocument.getBuID() != null) {
                pojo.setBuId(customersService.getBUIdsFromCurrentStaff());
            }
            if (creditDocument.getPaytype() != null) {
                pojo.setPaytype(creditDocument.getPaytype());
            }

            if (creditDocument.getType() != null) {
                pojo.setType(creditDocument.getType());
            }
            if (creditDocument.getNextTeamHierarchyMappingId() != null) {
                pojo.setNextTeamHierarchyMappingId(creditDocument.getNextTeamHierarchyMappingId());
            }
            if (creditDocument.getAbbsAmount() != null) {
                pojo.setAbbsAmount(creditDocument.getAbbsAmount());
            }

            if (creditDocument.getCreatedByName() != null) pojo.setCreatedByName(creditDocument.getCreatedByName());
        }
        return pojo;
    }

    public CreditDocument convertCreditDocumentPojoToCreditDocumentModel(CreditDocumentPojo creditDocumentPojo) throws Exception {
        CreditDocument domain = null;
        if (creditDocumentPojo != null) {
            domain = new CreditDocument();
            domain.setId(creditDocumentPojo.getId());
            if (creditDocumentPojo.getCustomer() != null) {
                domain.setCustomer(customerMapper.dtoToDomain(creditDocumentPojo.getCustomer(), new CycleAvoidingMappingContext()));
            }
            domain.setPaymode(creditDocumentPojo.getPaymode());
            domain.setPaymentdate(creditDocumentPojo.getPaymentdate());
            domain.setAmount(creditDocumentPojo.getAmount());
            domain.setStatus(creditDocumentPojo.getStatus());
            domain.setApproverid(creditDocumentPojo.getApproverid());
            domain.setRemarks(creditDocumentPojo.getRemarks());
            domain.setReferenceno(creditDocumentPojo.getReferenceno());
            domain.setTds_credit_doc_id(creditDocumentPojo.getTds_credit_doc_id());
            domain.setTds_received(creditDocumentPojo.getTds_received());
            domain.setTdsamount(creditDocumentPojo.getTdsamount());
            domain.setTdsflag(creditDocumentPojo.getTdsflag());
            domain.setTds_received_date(creditDocumentPojo.getTds_received_date());
            domain.setIs_reversed(creditDocumentPojo.getIs_reversed());
            domain.setResevrsed_date(creditDocumentPojo.getResevrsed_date());
            domain.setResverse_debitdoc_id(creditDocumentPojo.getResverse_debitdoc_id());
            domain.setPaydetails1(creditDocumentPojo.getBankName());
            domain.setPaydetails2(creditDocumentPojo.getBranch());
            domain.setPaydetails3(creditDocumentPojo.getChequeNo());
            domain.setChequedate(creditDocumentPojo.getChequedate());
            domain.setReferenceno(creditDocumentPojo.getReferenceno());
            domain.setPaydetails4(creditDocumentPojo.getPaydetails4());
            domain.setReciptNo(creditDocumentPojo.getReciptNo());
            domain.setLcoid(creditDocumentPojo.getLcoId());
//            if (creditDocumentPojo.getInvoiceId() != null) {
//                domain.setInvoiceId(creditDocumentPojo.getInvoiceId());
//            }

            if (creditDocumentPojo.getPaytype() != null) {
                domain.setPaytype(creditDocumentPojo.getPaytype());
            }

            if (creditDocumentPojo.getType() != null) {
                domain.setType(creditDocumentPojo.getType());
            }
        }
        return domain;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public List<CreditDocumentPojo> convertResponseModelIntoPojo(List<CreditDocument> creditDocumentList) throws Exception {
        List<CreditDocumentPojo> pojoListRes = new ArrayList<CreditDocumentPojo>();
        if (creditDocumentList != null && creditDocumentList.size() > 0) {
            for (CreditDocument creditDocument : creditDocumentList) {
                if (creditDocument.getInvoiceId() != null) {
                    DebitDocument debitDoc = debitDocRepository.findById(creditDocument.getInvoiceId()).orElse(null);
                    if (debitDoc != null) {
//                    DebitDocument debitDocument = debitDocRepository.findById(creditDocument.getInvoiceId()).get();
                        creditDocument.setInvoiceNumber(debitDoc.getDocnumber());

                    }
                }
                pojoListRes.add(convertCreditDocumentModelToCreditDocumentPojo(creditDocument));
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocument> serachPayment(SearchPaymentPojo searchPaymentPojo, PaginationRequestDTO requestDTO,Integer mvnoId) {
        Page<CreditDocument> creditDocumentsList = null;
        try {
            if (searchPaymentPojo != null) {
                SearchPayment payment = this.convertSearchPaymentPojoToSearchPayment(searchPaymentPojo,mvnoId);
                creditDocumentsList = this.findCreditDocuments(payment, requestDTO,mvnoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return creditDocumentsList;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocumentSearchPojo> searchPayment(SearchPaymentPojo searchPaymentPojo, PaginationRequestDTO requestDTO,Integer mvnoId) {
        Page<CreditDocumentSearchPojo> creditDocumentsList = null;
        try {
            if (searchPaymentPojo != null) {
                creditDocumentsList = this.getCreditDocuments(searchPaymentPojo, requestDTO,mvnoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return creditDocumentsList;
    }

    @Override
    public Page<CreditDocument> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (null != searchModel.getFilterColumn()) {
                    if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                        return getCustbyIdOrPayFromOrPayToOrPayStatus(searchModel.getFilterValue(), pageRequest);
                    } else {
                        throw new RuntimeException("Please Provide Search Column!");
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }

        return null;
    }

    public Page<CreditDocument> getCustbyIdOrPayFromOrPayToOrPayStatus(String s1, PageRequest pageRequest) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAllByCustidContainingIgnoreCaseOrCust_NameAndIsDeleteIsFalse(s1, s1, s1, s1, s1, s1, pageRequest, getMvnoIdFromCurrentStaff(null));
    }

    public SearchPayment convertSearchPaymentPojoToSearchPayment(SearchPaymentPojo searchPaymentPojo,Integer mvnoId) {
        SearchPayment payment = null;
        if (searchPaymentPojo != null) {
            payment = new SearchPayment();
            payment.setReferenceno(searchPaymentPojo.getReferenceno());
            payment.setPayfromdate(searchPaymentPojo.getPayfromdate());
            if(searchPaymentPojo.getPaytodate()!=null){
                payment.setPaytodate(LocalDate.now());
            }else {
                payment.setPaytodate(searchPaymentPojo.getPaytodate());
            }
            payment.setRecordfromdate(searchPaymentPojo.getRecordfromdate());
            payment.setRecordtodate(searchPaymentPojo.getRecordtodate());
            payment.setIdlist(searchPaymentPojo.getIdlist());
            payment.setEmailreceipt(searchPaymentPojo.getEmailreceipt());
            payment.setRemarks(searchPaymentPojo.getRemarks());
            payment.setPaystatus(searchPaymentPojo.getPaystatus());
            payment.setNextApprover(searchPaymentPojo.getNextApprover());
            payment.setMobileNumber(searchPaymentPojo.getMobileNumber());
            payment.setInvoiceNumber(searchPaymentPojo.getInvoiceNumber());
            payment.setPaydetails1(searchPaymentPojo.getPaydetails1());
            payment.setChequedate(searchPaymentPojo.getChequedate());
            payment.setPaymentdate(searchPaymentPojo.getPaymentdate());
            payment.setUserName(searchPaymentPojo.getUserName());
            payment.setBranchname(searchPaymentPojo.getBranchname());
            payment.setBuID(searchPaymentPojo.getBuID());
            payment.setReceiptNo(searchPaymentPojo.getReceiptNo());
            payment.setDestinationBank(searchPaymentPojo.getDestinationBank());
            payment.setPartnerName(searchPaymentPojo.getPartnerName());
            payment.setCreditDocumentNumber(searchPaymentPojo.getCreditDocumentNumber());
            payment.setServiceAreaId(searchPaymentPojo.getServiceAreaId());

            if (searchPaymentPojo.getApproveId() != null) payment.setApproveId(searchPaymentPojo.getApproveId());

            if (searchPaymentPojo.getCustomerid() != null) {
                payment.setCustomer(customerService.get(searchPaymentPojo.getCustomerid(),mvnoId));
            }
            payment.setPaymode(searchPaymentPojo.getPaymode());
            if (searchPaymentPojo.getCustomerid() != null) {
                payment.setCustomerid(Integer.toString(searchPaymentPojo.getCustomerid()));
            }
            if (searchPaymentPojo.getPartnerid() != null) {
                payment.setPartner(partnerRepository.getOne(searchPaymentPojo.getPartnerid()));
            }
            if (searchPaymentPojo.getType() != null) {
                payment.setType(searchPaymentPojo.getType());
            }
            if (searchPaymentPojo.getChequeNo() != null) {
                payment.setChequeNo(searchPaymentPojo.getChequeNo());
            }
            if (searchPaymentPojo.getStaff() != null && !searchPaymentPojo.getStaff().isEmpty() && !searchPaymentPojo.getStaff().equalsIgnoreCase("null")) {
                payment.setStaff(Integer.parseInt(searchPaymentPojo.getStaff()));
            }
            if (searchPaymentPojo.getBuID() != null) {
                payment.setBuID(searchPaymentPojo.getBuID());
            }
            if (searchPaymentPojo.getReceiptNo() != null) {
                payment.setReceiptNo(searchPaymentPojo.getReceiptNo());
            }
            if (searchPaymentPojo.getDestinationBank() != null) {
                payment.setDestinationBank(searchPaymentPojo.getDestinationBank());
            }
            if (searchPaymentPojo.getPartnerName() != null) {
                payment.setPartnerName(searchPaymentPojo.getPartnerName());
            }
        }
        return payment;
    }

    public RecordPaymentPojo convertRecordPaymentModelToRecordPaymentPojo(RecordPayment recordPayment) {
        RecordPaymentPojo recordPaymentPojo = null;
        if (recordPayment != null) {
            recordPaymentPojo = new RecordPaymentPojo();
            recordPaymentPojo.setChequedate(recordPayment.getChequedate());
            recordPaymentPojo.setPaymentdate(recordPayment.getPaymentdate());
            recordPaymentPojo.setChequeno(recordPayment.getChequeno());
            recordPaymentPojo.setBank(recordPayment.getBank());
            recordPaymentPojo.setPaymode(recordPayment.getPaymode());
            if (Objects.isNull(recordPayment.getAmount())) {
                recordPaymentPojo.setAmount(0D);
            } else {
                recordPaymentPojo.setAmount(recordPayment.getAmount());
            }
            recordPaymentPojo.setPaymentreferenceno(recordPayment.getPaymentreferenceno());
            recordPaymentPojo.setBranch(recordPayment.getBranch());
            recordPaymentPojo.setReferenceno(recordPayment.getReferenceno());
            recordPaymentPojo.setRemark(recordPayment.getRemark());
            recordPaymentPojo.setReciptNo(recordPayment.getReciptNo());
            if (recordPayment.getCustomer() != null) {
                recordPaymentPojo.setCustomerid(recordPayment.getCustomer().getId());
            }

//            if (recordPayment.getInvoiceId() != null) {
//                recordPaymentPojo.setInvoiceId(recordPayment.getInvoiceId());
//            }

            if (recordPayment.getPaytype() != null) {
                recordPaymentPojo.setPaytype(recordPayment.getPaytype());
            }
            if (recordPayment.getType() != null) {
                recordPaymentPojo.setType(recordPayment.getType());
            }
            if (recordPayment.getFilename() != null) {
                recordPaymentPojo.setFilename(recordPayment.getFilename());
            }
            if (recordPayment.getUniquename() != null) {
                recordPaymentPojo.setUniquename(recordPayment.getUniquename());
            }
            if (recordPayment.getBarteramount() != null) {
                recordPaymentPojo.setBarteramount(recordPayment.getBarteramount());
            }
        }
        return recordPaymentPojo;
    }

//
//    public RecordPaymentPojo CovertCreditDocPojotoRecordPaymnetPojo(CreditDocumentPojo creditDocument) throws Exception {
//        RecordPaymentPojo doc = null;
//        if (creditDocument != null && creditDocument.getCustId() != null) {
//            doc = new RecordPaymentPojo();
//            doc.setCustomerid(creditDocument.getCustId());
//            doc.setPaymentdate(creditDocument.getPaymentdate());
//            doc.setPaymode(creditDocument.getPaymode());
//            doc.setAmount(creditDocument.getAmount());
//            doc.setPaymentreferenceno(creditDocument.getReferenceno());
//            //doc.setIsDelete(false);
//            doc.setRemark(creditDocument.getRemarks());
//            doc.setAmount(creditDocument.getAdjustedAmount());
//          //  if (payment.getMvnoId() != null) {
//         //       doc.setMvnoId(creditDocument.getMvnoId());
//         //   }
//            if (CommonUtils.PAYMENT_MODE_CHEQUE.equalsIgnoreCase(doc.getPaymode())) {
//                doc.setBank(creditDocument.getPaydetails1());
//                doc.setChequeno(creditDocument.getPaydetails2());
//                doc.setChequedate(LocalDate.parse(creditDocument.getPaydetails3()));
//            } else if (CommonUtils.PAYMENT_MODE_ONLINE.equalsIgnoreCase(doc.getPaymode())) {
//                doc.setBank(creditDocument.getPaydetails1());
//                doc.setBranch(creditDocument.getPaydetails2());
//            } else {
//                doc.setReferenceno(creditDocument.getPaydetails4());
//            }
//            if (creditDocument.getInvoiceId() != null) {
//                doc.setInvoiceId(creditDocument.getInvoiceId());
//            }
//            if (creditDocument.getType() != null) {
//                doc.setType(creditDocument.getType());
//            }
//        }
//        return doc;
//    }
//

    public RecordPayment convertRecordPaymentPojoToRecordPaymentModel(RecordPaymentPojo recordPaymentPojo) {
        RecordPayment recordPayment = null;
        if (recordPaymentPojo != null) {
            recordPayment = new RecordPayment();
            recordPayment.setChequedate(recordPaymentPojo.getChequedate());
            recordPayment.setPaymentdate(recordPaymentPojo.getPaymentdate());
            recordPayment.setChequeno(recordPaymentPojo.getChequeno());
            recordPayment.setBank(recordPaymentPojo.getBank());
            recordPayment.setBranch(recordPaymentPojo.getBranch());
            recordPayment.setCustomerid(Integer.toString(recordPaymentPojo.getCustomerid()));
            recordPayment.setPaymode(recordPaymentPojo.getPaymode());
            if (Objects.isNull(recordPaymentPojo.getAmount())) {
                recordPayment.setAmount(0D);
            } else {
                recordPayment.setAmount(recordPaymentPojo.getAmount());
            }
            recordPayment.setPaymentreferenceno(recordPaymentPojo.getPaymentreferenceno());
            recordPayment.setRemark(recordPaymentPojo.getRemark());
            recordPayment.setReciptNo(recordPaymentPojo.getReciptNo());
            if (recordPaymentPojo.getReferenceno() != null)
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            if (recordPaymentPojo.getCustomerid() != null) {
                recordPayment.setCustomer(customersRepository.findById(recordPaymentPojo.getCustomerid()).get());//customerService.get(recordPaymentPojo.getCustomerid()));
            }
//            if (recordPaymentPojo.getInvoiceId() != null) {
//                recordPayment.setInvoiceId(recordPaymentPojo.getInvoiceId());
//            }
            List<Integer> integerList=recordPaymentPojo.getInvoiceId();
            recordPaymentPojo.setInvoiceId(integerList);
            if (recordPaymentPojo.getPaytype() != null) {
                recordPayment.setPaytype(recordPaymentPojo.getPaytype());
            }
            if (recordPaymentPojo.getType() != null) {
                recordPayment.setType(recordPaymentPojo.getType());
            }
            if (recordPaymentPojo.getFilename() != null) {
                recordPayment.setFilename(recordPaymentPojo.getFilename());
            }
            if (recordPaymentPojo.getUniquename() != null) {
                recordPayment.setUniquename(recordPaymentPojo.getUniquename());
            }
            if (recordPaymentPojo.getBarteramount() != null) {
                recordPayment.setBarteramount(recordPaymentPojo.getBarteramount());
            }
            recordPayment.setMvnoId(recordPaymentPojo.getMvnoId());
            recordPayment.setTdsAmount(recordPaymentPojo.getTdsAmount());
            recordPayment.setAbbsAmount(recordPaymentPojo.getAbbsAmount());
            if (recordPaymentPojo.getOnlinesource() != null) {
                recordPayment.setOnlinesource(recordPaymentPojo.getOnlinesource());
            }
            if (recordPaymentPojo.getReferenceno() != null) {
                recordPayment.setReferenceno(recordPaymentPojo.getReferenceno());
            }
        }
        return recordPayment;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '2')")
    public SearchPayment getSearchPaymentForInvoiceList() {
        return new SearchPayment();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '2')")
    public RecordPayment getRecordPaymentForAddPayment() {
        return new RecordPayment();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '2')")
    public CreditDocument saveRecordPayment(RecordPayment recordPayment,Integer mvnoId) throws Exception {
        recordPayment.setMvnoId(getMvnoIdFromCurrentStaff(recordPayment.getCustomer().getId()));
        CreditDocument doc = covertPaymentReqToCreditDoc(recordPayment,mvnoId);
        CreditDocument save = null;
        if (doc != null) {
            if (Objects.isNull(doc.getReferenceno())) {
                doc.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
            }
            save = entityRepository.save(doc);
            return save;
        }
        return save;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '2')")
    public RecordPaymentPojo save(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked,CreditDebitDocMapping creditDebitDoc) throws Exception {
        try {

            Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
//        BankManagement bankManagement = validateBankManagement(pojo.getBankManagement());
            List<CreditDebitDocMapping> creditDebitDocMappingList = new ArrayList<>();
            String customerName = null;
            Integer custMvnoId = null;
            String mobileNumber = null;
            String emailId = null;
            String countryCode = null;
            StaffUser loggedInUser = staffUserRepository.findById(pojo.getLoggedInuserid()).orElse(null);
//        DebitDocument debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).orElse(null);
//        if(pojo.getType().equalsIgnoreCase("Payment") && debitDocument != null) {
//            String msg = checkPaymentValid(debitDocument);
//            if(!msg.equalsIgnoreCase("success")) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),msg, null);
//                            }
//                        }
//
//        if (pojo.getPaymode().equals(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
//            if (debitDocument != null) {
//                Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(pojo.getInvoiceId().get(0), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
//                if (totalCreditNoteGenerated == 0) {
//                    if (pojo.getAmount() > debitDocument.getTotalamount()) {
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
//                                        }
//                } else if (pojo.getAmount() + totalCreditNoteGenerated > debitDocument.getTotalamount()) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becuase invoice amount exceeds", null);
//                        }
//                QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//                BooleanExpression booleanExpression = qDebitDocument.isNotNull();
//                booleanExpression = booleanExpression.and(qDebitDocument.customer.id.eq(pojo.getCustomerid()));
//                booleanExpression = booleanExpression.and(qDebitDocument.startdate.after(LocalDateTime.now()));
//                List<DebitDocument> debitDocumentList = IterableUtils.toList(debitDocRepository.findAll(booleanExpression));
//                if(!debitDocumentList.isEmpty()){
//                    ifCreditNoteIsAllowed(pojo); /**If future plan is available then creditnote with same amount is not allowed**/
//                }
//
//
//
//                        }
//                    }
//        CreditDocument creditDocument = new CreditDocument(pojo);
//        pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//        if (pojo.getPaytype() != null && pojo.getPaytype().equals("Cheque")) {
//            if (bankManagement != null) {
//                if (!bankManagement.getStatus().equals("Active")) {
//                    throw new RuntimeException("Status change at run time");
//                    }
//                }
//
//            }
//        if (pojo.getBankManagement() != null && !pojo.getBankManagement().isEmpty()) {
//
//            creditDocument.setBankManagement(bankManagement.getId());
//
//
//            }
//        if (pojo.getDestinationBank() == null && pojo.getDestinationBank() != null) {
//            creditDocument.setDestinationBank(pojo.getDestinationBank());
//            }
//        if (pojo.getOnlinesource() != null) {
//            CommonList commonList = commonListRepository.findByValue(pojo.getOnlinesource());
//            creditDocument.setOnlinesource(commonList.getText());
//            }
//        if (pojo.getReferenceno() != null) {
//            creditDocument.setReferenceno(pojo.getReferenceno());
//            }
//
//
////        creditDocument.setCreditdocumentno(getInvoiceNo());
//
//
            RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo);
//        CreditDocument doc = this.covertPaymentReqToCreditDoc(obj);
//        if (!CollectionUtils.isEmpty(pojo.getInvoiceId())) {
//            doc.setInvoiceId(pojo.getInvoiceId().get(0));
//        }
//        if (getLoggedInUser().getLco()) doc.setLcoid(getLoggedInUser().getPartnerId());
//        else doc.setLcoid(null);
            //tyoe == withdrawl then doc.settype(DR)
            if (pojo.getCreditDocId() != null) {
                CreditDocument doc = creditDocRepository.findById(pojo.getCreditDocId()).orElse(null);

                if (doc != null) {
                    Integer creditDocid = doc.getId();
                    List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
                    if (pojo.getInvoiceId().size() != 0) {
                        if (pojo.getInvoiceId().stream().anyMatch(integer -> integer == UtilsCommon.PAYMENT_STATUS_ADVANCED)) {
                            pojo.setInvoiceId(null);
                        } else {
                            if (!CollectionUtils.isEmpty(pojo.getPaymentListPojos())) {
                                for (PaymentListPojo paymentListPojo : pojo.getPaymentListPojos()) {
                                    if (paymentListPojo.getInvoiceId() != null) {
                                        if(customers.getStatus() != null && (customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.ACTIVATION_PENDING) || customers.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS.NEW_ACTIVATION))) {
                                            String docNumber = trialDebitDocumentRepository.findDocnumberById(paymentListPojo.getInvoiceId());
                                            doc.setInvoiceNumber(docNumber != null ? docNumber : "");
                                        } else {
                                            DebitDocument debitDocument = debitDocRepository.findById(paymentListPojo.getInvoiceId()).orElse(null);
                                            if(debitDocument != null){
                                                doc.setInvoiceNumber(debitDocument.getDocnumber() != null ? debitDocument.getDocnumber() : "");
                                            }
                                        }
                                    }
                                    if(Objects.isNull(creditDebitDoc)){
                                        creditDebitDoc=new CreditDebitDocMapping();
                                    }
                                    CreditDebitDocMapping creditDebitDocMapping = creditDebitDoc;
                                    creditDebitDocMapping.setCreditDocId(doc.getId());
                                    creditDebitDocMapping.setAmount(pojo.getAmount());
                                    if (paymentListPojo.getInvoiceId() != UtilsCommon.PAYMENT_STATUS_ADVANCED) {
                                        creditDebitDocMapping.setDebtDocId(paymentListPojo.getInvoiceId());
                                        creditDebitDocMapping.setAmount(paymentListPojo.getAmountAgainstInvoice());
                                        if (paymentListPojo.getAbbsAmountAgainstInvoice() != null) {
                                            creditDebitDocMapping.setAbbsAmount(paymentListPojo.getAbbsAmountAgainstInvoice());
                                        }
                                        if (paymentListPojo.getTdsAmountAgainstInvoice() != null) {
                                            creditDebitDocMapping.setTdsAmount(paymentListPojo.getTdsAmountAgainstInvoice());
                                        }
                                        creditDocRepository.save(doc);
                                        if (isRevoked) creditDebitDocMapping.setAdjustedAmount(pojo.getAmount());
                                        creditDebtMappingRepository.save(creditDebitDocMapping);
                                        creditDebitDocMappings.add(creditDebtMappingRepository.save(creditDebitDocMapping));
                                    }
                                }
                            }

                            if (CollectionUtils.isEmpty(pojo.getPaymentListPojos())) {
                                for (int i = 0; i < pojo.getInvoiceId().size(); i++) {
                                    if (pojo.getInvoiceId() != null) {
                                        DebitDocument debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(i)).orElse(null);
                                        doc.setInvoiceNumber(debitDocument.getDocnumber());
                                    }
                                    CreditDebitDocMapping creditDebitDocMapping = creditDebitDoc;
                                    Integer debitDocid = pojo.getInvoiceId().get(i);
                                    if (doc.getAmount() != doc.getAdjustedAmount() && doc.getAdjustedAmount() < doc.getAmount()) {
                                        if (creditDocid != null && debitDocid != null) {
                                            if (debitDocid != UtilsCommon.PAYMENT_STATUS_ADVANCED) {
                                                creditDebitDocMapping.setCreditDocId(doc.getId());
                                                creditDebitDocMapping.setDebtDocId(debitDocid);
                                                creditDebitDocMapping.setAdjustedAmount(0d);
                                                if (isRevoked)
                                                    creditDebitDocMapping.setAdjustedAmount(pojo.getAmount());
                                                creditDebtMappingRepository.save(creditDebitDocMapping);
                                                creditDebitDocMappings.add(creditDebtMappingRepository.save(creditDebitDocMapping));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (Objects.nonNull(customers)) {
                        customerName = customers.getUsername();
                        custMvnoId = customers.getMvnoId();
                        mobileNumber = customers.getMobile();
                        emailId = customers.getEmail();
                        countryCode = customers.getCountryCode();
                    }
                    if (doc.getNextTeamHierarchyMappingId() == null && !isRevoked && customers.getLcoId() == null) {
                        if (Objects.equals(doc.getStatus(), UtilsCommon.PAYMENT_STATUS_PENDING)) {
                            StaffUser assignedUser = null;
                            if (clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,pojo.getMvnoId()).equals("TRUE")) {
                                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, CommonConstants.HIERARCHY_TYPE, false, true, doc);
                                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                    StaffUser assignedStaff = staffUserService.get(Integer.parseInt(map.get("staffId")),custMvnoId);
                                    doc.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                    doc.setApproverid(assignedStaff.getId());
                                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, doc.getId(), doc.getReferenceno(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                    doc.setXmldocument(assemblePaymentXML(doc, UtilsCommon.ADDR_TYPE_PRESENT,custMvnoId));
//                                TAT functionality
                                    if (assignedStaff.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedStaff, doc.getId(), null);
                                    }
                                } else {
                                    doc.setNextTeamHierarchyMappingId(null);
                                    doc.setApproverid(loggedInUser.getId());
                                    if (loggedInUser != null) {
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.REMOVE_INVENTORY, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + loggedInUser.getUsername());
                                        doc.setXmldocument(assemblePaymentXML(doc, UtilsCommon.ADDR_TYPE_PRESENT,custMvnoId));
                                    }
                                }
                                //   TAT functionality
                                if (assignedUser != null) {
                                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                            map.put("tat_id", map.get("current_tat_id"));
                                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, customers.getId(), null);
                                    }
                                }
                            } else {
                                doc.setApproverid(null);
                                doc.setNextTeamHierarchyMappingId(null);
                                doc.setApproverid(loggedInUser.getId());
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + loggedInUser.getUsername());
                                doc.setXmldocument(assemblePaymentXML(doc, UtilsCommon.ADDR_TYPE_PRESENT,custMvnoId));
                            }
                        }
                    } else {
                        if (doc.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                            adjustCreditNote(Optional.of(doc), creditDebitDocMappings,custMvnoId);
                        } else {
                            if (obj.getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
                                adjustWithDrawal(doc);
                            } else {
                                adjustPayment(Optional.of(doc), creditDebitDocMappings,false);
                            }
                        }
                        if (doc.getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT)) {
                            doc.setStatus(CommonConstants.PAYMENT_STATUS_APPROVED);
                        }
                        if (!doc.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)) {
                            addLedgeAfterApproval(doc); //add for jira no: ANG-4462: ledger amount wrong
                        }
                    }

                }
                doc = creditDocRepository.save(doc);
                doc.setPaymentdate(LocalDate.now());
                CreditDocMessage creditDocMessage = new CreditDocMessage(doc, creditDebitDocMappingList);
                //messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);


                obj.setReferenceno(doc.getReferenceno());
                obj.setRemark(doc.getRemarks());
                pojo = convertRecordPaymentModelToRecordPaymentPojo(obj);
                if (iswithdrawal) {
                    pojo.setType("DR");
                }
                if (isInvoiceVoid) {
                    if (isRevoked) addLedgerAndLedgerDetailEntry(pojo, doc.getId(), false);
                    else addLedgerAndLedgerDetailEntry(pojo, doc.getId(), isInvoiceVoid);
                }
                customerService.sendCustPaymentSuccessMessage(RabbitMqConstants.CUSTOMER_PAYMENT_SUCCESS, customers.getUsername(), pojo.getAmount(), pojo.getPaymode(), customers.getMvnoId(), customers.getCountryCode(), customers.getMobile(), customers.getEmail(), customers.getId(), pojo.getReciptNo(), String.valueOf(pojo.getPaymentdate()), customers.getBuId(), null, doc.getCreatedById().longValue(),doc.getId());
                if (doc != null) pojo.setCreditDocId(doc.getId());
                if (doc.getOnlinesource() != null) {
                    pojo.setOnlinesource(doc.getOnlinesource());
                }
                if (doc.getDestinationBank() != null) {
                    pojo.setDestinationBank(doc.getDestinationBank());
                }
            }
            return pojo;
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public String checkPaymentValid(DebitDocument debitDocument) {
        Integer custpackrelid = debitDocument.getCustpackrelid();
        if (custpackrelid != null) {
            CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(custpackrelid);
            if (custPlanMappping != null){
                if (!custPlanMappping.getBillTo().equalsIgnoreCase(CommonConstants.CUSTOMER) && custPlanMappping.getIsInvoiceToOrg()) {
                    List<Integer> debitdocIds = custPlanMappingRepository.findAllByCustRefId(Collections.singleton(custPlanMappping.getId()));
                    if (!CollectionUtils.isEmpty(debitdocIds)) {
                        if (debitDocRepository.existsByIdInAndStatus(debitdocIds, "pending")) {
                            return "As Organization invoice is pending, not able to do payment!";
                        }
                    }
                }
        }
    }

        return "Success";
    }


    public void validateRequest(RecordPaymentPojo pojo, Integer operation,Integer mvnoId) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && customerService.get(pojo.getCustomerid(),mvnoId) == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.customer.not.found"), null);
        }
    }

    public void validatePaymentActionRequest(SearchPaymentPojo pojo, Integer operation) {
        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && (pojo.getIdlist() == null || pojo.getIdlist().trim().length() <= 0)) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.improper.value.for.idList"), null);
        }
    }

//    public SearchPaymentPojo approvePayment(SearchPaymentPojo entity) {
//        try {
//            String flashMsg = "error";
//            CreditDocument doc = null;
//            CustomerLedger ledger = null;
//            CustomerLedgerDtls ledgerDtls = null;
//            try {
//                if (entity.getIdlist() != null) {
//                    String idList[] = entity.getIdlist().split(",");
//                    if (idList != null && idList.length > 0) {
//                        for (String id : idList) {
//                            doc = null;
//                            ledger = null;
//                            ledgerDtls = null;
//                            doc = entityService.get(Integer.valueOf(id));
//                            if (doc.getStatus().equals(CommonUtils.PAYMENT_STATUS_PENDING)) {
//                                if (doc.getInvoiceId() != null) {
//                                    Optional<DebitDocument> debitDocumentOptional = debitDocService.getById(doc.getInvoiceId());
//                                    if (debitDocumentOptional.isPresent()) {
//                                        DebitDocument debicDoc = debitDocumentOptional.get();
//                                        Double totalAmount = 0.00;
//                                        Double totalPayment = findTotalPaymentAmountByInvoice(doc.getInvoiceId());
//                                        if (totalPayment != null) {
//                                            totalAmount = doc.getAmount() + totalPayment;
//                                        } else {
//                                            totalAmount = doc.getAmount();
//                                        }
//                                        if (totalAmount >= debicDoc.getTotalamount()) {
//                                            debicDoc.setPaymentStatus(CommonUtils.PAYMENT_STATUS_APPROVED);
//                                        } else {
//                                            debicDoc.setPaymentStatus(CommonUtils.PAYMENT_STATUS_PARTIAL_APPROVED);
//                                        }
//                                        debitDocRepository.save(debicDoc);
//                                    }
//                                }
//                                doc.setStatus(CommonUtils.PAYMENT_STATUS_APPROVED);
//                                doc.setApproverid(getLoggedInUserId());
//                                doc.setRemarks(entity.getRemarks());
//                                doc.setXmldocument(assemblePaymentXML(doc, CommonUtils.ADDR_TYPE_PRESENT));
//                                doc = entityService.save(doc);
//                            }
//                        }
//                        flashMsg = "success";
//                    }
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                flashMsg = "error";
//            }
//
//            if (entity == null) {
//                entity = new SearchPaymentPojo();
//            }
//            if ("success".equalsIgnoreCase(flashMsg) && doc != null) {
//                entity.setPaystatus("approve");
//                return entity;
//            } else {
//                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//            }
//        } catch (CustomValidationException ce) {
//            ce.printStackTrace();
//        }
//        return entity;
//    }


    public GenericDataDTO approvePayment(SearchPaymentPojo entity,Integer mvnoId) {
        Double pendingAmount = 0d;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),mvnoId);
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        String idListArray[] = entity.getIdlist().split(",");
        BooleanExpression boolex = qCreditDocument.isNotNull().and(qCreditDocument.id.in(Arrays.stream(idListArray).map(s -> Integer.parseInt(s)).collect(Collectors.toList())).and(qCreditDocument.isDelete.eq(false)));
        Optional<CreditDocument> creditDocument = creditDocRepository.findOne(boolex);
        if (creditDocument.get().getPaydetails1() != null) {
            if (creditDocument.get().getPaydetails1().equals("Flutter Wave")) {
                StaffUser staffuser = staffUserService.getRepository().findById(creditDocument.get().getCreatedById()).orElse(null);
                creditDocument.get().setApproverid(staffuser.getId());
            }
        }
        Customers customers = creditDocument.get().getCustomer();
        String flashMsg = "error";
        CreditDocument doc = null;
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(Integer.valueOf(entity.getIdlist())));
        creditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAll(booleanExpression)).stream().sorted((o1, o2) -> o1.getDebtDocId().compareTo(o2.getDebtDocId())).collect(Collectors.toList());
        try {
            if (entity.getIdlist() != null  ) {
                String idList[] = entity.getIdlist().split(",");
                if (idList.length > 0) {
                    for (String id : idList) {
                        doc = null;
                        doc = entityService.getEntityForUpdateAndDeleteForCWSC(Integer.valueOf(id),customers.getMvnoId().intValue());
                        StaffUser assignedUser = null;

                        if (doc.getStatus().equals(UtilsCommon.PAYMENT_STATUS_PENDING)) {
                            // TODO: pass mvnoID manually 6/5/2025
                            if (clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoId).equals("TRUE")) {
                                Map<String, String> map = new HashMap<>();
                                if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                    map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, true, false, creditDocument.get());
                                } else {
                                    map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, true, false, creditDocument.get());
                                }
                                int staffId = 0;
                                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                    StaffUser assignedStaff = staffUserService.get(staffId,customers.getMvnoId());
                                    creditDocument.get().setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                    creditDocument.get().setApproverid(Integer.valueOf(map.get("nextTatMappingId")));

                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                    } else {
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                    }
                                } else if (creditDocument.get().getStatus().equalsIgnoreCase(UtilsCommon.PAYMENT_STATUS_PENDING)) {
                                    if (creditDocument.get().getPaytype().equals(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT)) {
                                        creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                                    }
                                    creditDocument.get().setNextTeamHierarchyMappingId(null);
                                    creditDocument.get().setApproverid(null);
                                    customers.setWalletbalance(customers.getWalletbalance()-creditDocument.get().getAmount());
                                    customersRepository.save(customers);


                                    if (creditDocument.get().getType().equalsIgnoreCase("creditnote")) {
//                                        creditDocument.get().setCreditdocumentno(getInvoiceNo());
                                        Boolean isLCO = customers.getLcoId() != null ? true :false;
                                        creditDocument.get().setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, customers.getLcoId(), customers.getMvnoId()));
                                    }

//                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
//                                        adjustCreditNote(creditDocument, creditDebitDocMappings);
//                                    } else {
//                                        if (creditDocument.get().getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
//                                            adjustWithDrawal(creditDocument.get());
//                                        } else {
//                                            adjustPayment(creditDocument, creditDebitDocMappings);
//                                        }
//                                    }
                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(),  doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    } else {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    }

//                                    addLedgeAfterApproval(creditDocument.get());
                                }
                                ///TAT Matrix
                                if (assignedUser != null) {
                                    if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                                        if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                            map.put("tat_id", map.get("current_tat_id"));
                                        tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, customers.getId(), null);
                                    }
                                }
                            } else {
                                Map<String, Object> NextApprovalmap;
                                if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                    NextApprovalmap = hierarchyService.getTeamForNextApprove(doc.getMvnoId(), doc.getBuID(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, true, false, doc);
                                } else {
                                    NextApprovalmap = hierarchyService.getTeamForNextApprove(doc.getMvnoId(), doc.getBuID(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, true, false, doc);
                                }
                                if (NextApprovalmap.containsKey("assignableStaff")) {
                                    genericDataDTO.setDataList((List<StaffUserPojo>) NextApprovalmap.get("assignableStaff"));
                                    // workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    } else {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    }
                                } else if (creditDocument.get().getStatus().equalsIgnoreCase(UtilsCommon.PAYMENT_STATUS_PENDING)) {
                                    if (creditDocument.get().getStatus().equalsIgnoreCase("pending")) {
                                        creditDocument.get().setStatus(CommonConstants.PAYMENT_STATUS_APPROVED);
                                    }

                                    if (creditDocument.get().getPaytype().equals(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT)) {
                                        creditDocument.get().setStatus(CommonConstants.PAYMENT_STATUS_APPROVED);
                                    }
                                    creditDocument.get().setNextTeamHierarchyMappingId(null);
                                    creditDocument.get().setApproverid(null);
                                    customers.setWalletbalance(customers.getWalletbalance()-creditDocument.get().getAmount());
                                    customersRepository.save(customers);


                                    /* Called method for send notification*/
                                    sendCustPaymentVerificationMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId(), creditDocument.get().getReciptNo(), creditDocument.get().getAmount(), String.valueOf(creditDocument.get().getPaymentdate()),customers.getBuId(),loggedInUser.getId().longValue(),customers.getCusttype());


                                    if (creditDocument.get().getType().equalsIgnoreCase("creditnote")) {
//                                        creditDocument.get().setCreditdocumentno(getInvoiceNo());
                                        Boolean isLCO = customers.getLcoId() != null ? true :false;
                                        creditDocument.get().setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, customers.getLcoId(), customers.getMvnoId()));
                                    }

                                    if (creditDocument.get().getType().equalsIgnoreCase("Payment")) {
//                                        creditDocument.get().setCreditdocumentno(getPaymentInvoiceNo());
                                        Boolean isLCO = customers.getLcoId() != null ? true :false;
                                        creditDocument.get().setCreditdocumentno(numberSequenceUtil.getPaymentNumber(isLCO, customers.getLcoId(), customers.getMvnoId()));
                                    }

                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                        //adjustCreditNote(creditDocument, creditDebitDocMappings);
                                    } else {
                                        if (creditDocument.get().getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
                                            adjustWithDrawal(creditDocument.get());
                                        } else {
                                            //adjustPayment(creditDocument, creditDebitDocMappings);
                                        }
                                    }
                                    if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    } else {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                    }
                                }
                            }
                            if (!creditDocument.get().getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)) {
                                addLedgeAfterApproval(creditDocument.get()); //add for jira no: ANG-4462: ledger amount wrong
                            }
                            creditDocRepository.save(creditDocument.get());
//                            doc.setRemarks(entity.getRemarks());
                            doc = entityService.save(doc);
                            if(doc.getStatus().equalsIgnoreCase(CommonConstants.PAYMENT_STATUS_APPROVED)) {
                                CreditDocMessage creditDocMessage = new CreditDocMessage(doc, IterableUtils.toList(creditDebitDocMappings));
                                creditDocMessage.setPaymentdate(LocalDate.now().toString());
                                if (!creditDocument.get().getPaytype().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)) {
                                    kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
                                }
//                                messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE);
                                custPackStatusUpdate(doc);
                                List<WorkflowAudit> workflowAudits=new ArrayList<>();
                                if (creditDocument.get().getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                                   workflowAudits.addAll(workflowAuditRepository.findAllByEntityIdAndEventName(doc.getId(),CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE));
                                } else {
                                    workflowAudits.addAll(workflowAuditRepository.findAllByEntityIdAndEventName(doc.getId(),CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT));
                                }

                                if(workflowAudits!=null && workflowAudits.size()>0){
                                    workflowAuditRepository.saveAll(workflowAudits.stream().peek(i->i.setEntityName(creditDocMessage.getCreditdocumentno())).collect(Collectors.toList()));
                                }
                                flashMsg = "success";
                            }
                            flashMsg = "success";
                        }

                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            flashMsg = e.getMessage();//"error";
        }
        if (entity == null) {
            entity = new SearchPaymentPojo();
        }
        if ("success".equalsIgnoreCase(flashMsg) && doc != null) {
            // TODO: pass mvnoID manually 6/5/2025
            String paymode = clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.Allow_Transfer_To_Wallet_Mode,mvnoId);
            String[] array = paymode.split(",");

            if (Arrays.stream(array).anyMatch(creditDocument.get().getPaymode()::equalsIgnoreCase) && (creditDocument.get().getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED) || creditDocument.get().getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT) || creditDocument.get().getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED) || creditDocument.get().getStatus().equalsIgnoreCase("Approved"))) {
                saveStaffLedgerDetails(creditDocument, loggedInUser);
            }
            entity.setPaystatus("approve");
            if (entity.getPaystatus().equals("approve")) {

            }
            return genericDataDTO;
        } else {
            throw new CustomValidationException(APIConstants.FAIL, flashMsg, null);
        }
    }

    public void custPackStatusUpdate(CreditDocument creditDocument) {
        try{
            if(creditDocument.getInvoiceId()!=null) {
                Optional<DebitDocument> debitDocument = debitDocRepository.findById(creditDocument.getInvoiceId());
                if (debitDocument.isPresent()) {
                    Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.get().getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                    List<CustPlanMappping> custPlanMapppings = IterableUtils.toList(custPlanMappingRepository.findAllByDebitdocid(debitDocument.get().getId()));
                    if ((totalCreditNoteGenerated >= debitDocument.get().getTotalamount()) && creditDocument.getPaytype().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)) {
                        custPlanMapppings.forEach(custPlanMappping -> {
                            CustChargeDetails custChargeDetails = custchargedetailsrepository.findCustChargeDetailsByCustPlanMapppingId(custPlanMappping.getId());
                            if (custChargeDetails != null)
                                if (custChargeDetails.getChargetype() == null || !custChargeDetails.getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_CUSTOMER_DIRECT)) {
                            custPlanMappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                            //ANG-4987: resolved
                            if (custPlanMappping.getStartDate().isAfter(LocalDateTime.now())) {
                                custPlanMappping.setStartDate(LocalDateTime.now().minusMinutes(1));
                                custPlanMappping.setEndDate(LocalDateTime.now());
                                custPlanMappping.setExpiryDate(LocalDateTime.now());
                            } else {
                                custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
                                custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
                            }
                            if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                                custPlanMappping.setStartDate(LocalDateTime.now());
                                custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                                custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                            }

                                custPlanMappingRepository.save(custPlanMappping);
                            }
                        });
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void addLedgerAndLedgerDetailEntry(RecordPaymentPojo recordPayment, Integer creditDocumentId, boolean isVoidInvoice) {
        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        Optional<Customers> customers = customersRepository.findById(recordPayment.getCustomerid());
        QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
        BooleanExpression booleanExpression = qCustomerLedger.isNotNull().and(qCustomerLedger.customer.id.eq(recordPayment.getCustomerid()));

        ledger = ledgerRepository.findOne(booleanExpression).orElse(null);
        if (Objects.nonNull(ledger)) {
            ledger.setTotalpaid(ledger.getTotalpaid() + recordPayment.getAmount());
            ledger.setTotaldue(ledger.getTotaldue() - recordPayment.getAmount());
            ledgerService.save(ledger);
        }

        ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(recordPayment.getAmount());
        ledgerDtls.setPaymentMode(recordPayment.getPaymode());
        ledgerDtls.setBank(recordPayment.getBank());
        ledgerDtls.setBranch(recordPayment.getBranch());
        ledgerDtls.setPaymentRefNo(recordPayment.getPaymentreferenceno());
        ledgerDtls.setCreditdocid(creditDocumentId);
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsVoid(isVoidInvoice);
        ledgerDtls.setIsDelete(isVoidInvoice);


        if (recordPayment.getPaytype().equalsIgnoreCase("advance")) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
        } else if (recordPayment.getPaytype().equalsIgnoreCase("invoice")) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_INVOICE);
        } else if (recordPayment.getPaytype().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CREDIT_NOTE);
        } else {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
        }

        if (recordPayment.getRemark() != null) ledgerDtls.setDescription(recordPayment.getRemark());
        if (customers.isPresent()) ledgerDtls.setCustomer(customers.get());
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        if (recordPayment.getType().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT))
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);

        ledgerDtlsService.save(ledgerDtls);
    }

//OLD LOGIC
//    public SearchPaymentPojo rejectPayment(SearchPaymentPojo entity) {
//        String flashMsg = "error";
//        CreditDocument doc = null;
//
//        try {
//            if (entity.getIdlist() != null) {
//
//                String idList[] = entity.getIdlist().split(",");
//                if (idList != null && idList.length > 0) {
//
//                    for (String id : idList) {
//                        doc = null;
//                        doc = entityService.get(Integer.valueOf(id));
//                        if (doc.getStatus().equals(CommonUtils.PAYMENT_STATUS_PENDING)) {
////                        	if(doc.getInvoiceId() != null) {
////                      			Optional<DebitDocument> debitDocumentOptional = debitDocService.getById(doc.getInvoiceId());
////                              	if(debitDocumentOptional.isPresent()){
////                            		DebitDocument debicDoc = debitDocumentOptional.get();
////                              		debicDoc.setPaymentStatus(CommonUtils.PAYMENT_STATUS_PENDING);
////                            		debitDocRepository.save(debicDoc);
////                              	}
////                      		}
//                            doc.setStatus(CommonUtils.PAYMENT_STATUS_REJECTED);
//                            doc.setApproverid(getLoggedInUserId());
//                            doc.setRemarks(entity.getRemarks());
//                            doc = entityService.save(doc);
//                        }
//                    }
//                    flashMsg = "success";
//                }
//            }
//
//        } catch (CustomValidationException e) {
//            e.printStackTrace();
//            flashMsg = "error";
//        }
//
//        if (entity == null) {
//            entity = new SearchPaymentPojo();
//        }
//
//        if ("success".equalsIgnoreCase(flashMsg) && doc != null) {
//            return entity;
//        } else {
//            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.object.not.found"), null);
//        }
//    }

    public GenericDataDTO rejectPayment(SearchPaymentPojo entity) throws CustomValidationException, Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),getMvnoIdFromCurrentStaff(entity.getCustomerid()));
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        BooleanExpression boolex = qCreditDocument.isNotNull().and(qCreditDocument.id.in(Integer.valueOf(entity.getIdlist()))).and(qCreditDocument.isDelete.eq(false));
        Optional<CreditDocument> creditDocument = creditDocRepository.findOne(boolex);
        Customers customers = creditDocument.get().getCustomer();
        String flashMsg = "error";
        CreditDocument doc = null;
        Iterable<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(Integer.valueOf(entity.getIdlist())));
        creditDebitDocMappings = creditDebtMappingRepository.findAll(booleanExpression);
        try {
            if (entity.getIdlist() != null) {
                String idList[] = entity.getIdlist().split(",");
                if (idList != null && idList.length > 0) {
                    for (String id : idList) {
                        doc = null;
                        doc = entityService.getEntityForUpdateAndDelete(Integer.valueOf(id),customers.getMvnoId());
                        if (doc.getStatus().equals(UtilsCommon.PAYMENT_STATUS_PENDING)) {
                            if (clientServiceRepository.findValueByNameandMvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,getLoggedInMvnoId(customers.getId())).equals("TRUE")) {
                                if (!loggedInUser.getUsername().equalsIgnoreCase("admin")) {
                                    Map<String, String> map = new HashMap<>();
                                    if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                        map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, true, false, creditDocument.get());
                                    } else {
                                        map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, true, false, creditDocument.get());
                                    }
                                    int staffId = 0;
                                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                        staffId = Integer.parseInt(map.get("staffId"));
                                        StaffUser staffUsers = staffUserService.get(staffId,customers.getMvnoId());
                                        creditDocument.get().setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                        creditDocument.get().setApproverid(Integer.valueOf(map.get("nextTatMappingId")));
                                        if (creditDocument.get().getPaytype().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), staffUsers.getId(), staffUsers.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUsers.getUsername());
                                        } else {
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Approved By :- " + loggedInUser.getUsername());
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), staffUsers.getId(), staffUsers.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUsers.getUsername());
                                        }
//                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + " Rejected By :- " + loggedInUser.getUsername());
//                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getReferenceno(), staffUsers.getId(), staffUsers.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + staffUsers.getUsername());

                                    } else {
                                        creditDocument.get().setNextTeamHierarchyMappingId(null);
                                        creditDocument.get().setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
                                        creditDebtMappingRepository.deleteAll(creditDebitDocMappings);
                                        if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        } else {
                                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        }

                                    }

                                } else {
                                    creditDocument.get().setNextTeamHierarchyMappingId(null);
                                    creditDocument.get().setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
                                    if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), null, "admin", CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- admin");
                                    } else {
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), null, "admin", CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- admin");
                                    }
                                }
                            } else {

                                /* for direct reject by creater */
                                if(creditDocument.get() != null  && creditDocument.get().getNextTeamHierarchyMappingId() == null ){
                                    if(creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, creditDocument.get().getId());
                                        creditDocument.get().setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());

                                    }else{
                                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, creditDocument.get().getId());
                                        creditDocument.get().setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
                                        creditDocument.get().setAbbsAmount(0.0);
                                        creditDocument.get().setTdsamount(0.0);
                                        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
                                            creditDebitDocMapping.setTdsAmount(0.0);
                                            creditDebitDocMapping.setAbbsAmount(0.0);
                                            creditDebtMappingRepository.save(creditDebitDocMapping);
                                        }
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                    }
                                }else{

                                    Map<String, Object> map;
                                    if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                        map = hierarchyService.getTeamForNextApprove(doc.getCustomer().getMvnoId(), doc.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, CommonConstants.HIERARCHY_TYPE, false, false, doc);
                                    } else {
                                        map = hierarchyService.getTeamForNextApprove(doc.getCustomer().getMvnoId(), doc.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, CommonConstants.HIERARCHY_TYPE, false, false, doc);
                                    }
                                    if (map.containsKey("assignableStaff")) {
                                        genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                                        if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        } else {
                                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        }
                                    } else {

                                        creditDocument.get().setNextTeamHierarchyMappingId(null);
                                        creditDocument.get().setStatus(UtilsCommon.PAYMENT_STATUS_REJECTED);
                                        creditDebtMappingRepository.deleteAll(creditDebitDocMappings);
                                        if (creditDocument.get().getType().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE1)) {
                                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.CREDIT_NOTE, doc.getId(), doc.getReferenceno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        } else {
                                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PAYMENT, doc.getId(), doc.getCreditdocumentno(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "remarks : " + entity.getRemarks() + " " + "Rejected By :- " + loggedInUser.getUsername());
                                        }
                                    }
                                }
                            }
                            creditDocRepository.save(creditDocument.get());
                            doc = entityService.save(doc);
                            CreditDocMessage creditDocMessage = new CreditDocMessage(doc, IterableUtils.toList(creditDebitDocMappings));
                            kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//                            messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE);
                        }
                    }
                    flashMsg = "success";
                }
            }

        } catch (CustomValidationException e) {
            e.printStackTrace();
            flashMsg = "error";
        }

        if (entity == null) {
            entity = new SearchPaymentPojo();
        }

        if ("success".equalsIgnoreCase(flashMsg) && doc != null) {
            return genericDataDTO;
        } else {
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        }
    }

    public String assemblePaymentXML(CreditDocument doc) {
        /*String paymentXML = new String(CommonConstants.PAY_RECEIPT);
        CustomerAddress homeAddress = custAddrService.findByAddressTypeAndCustomer(CommonUtils.ADDR_TYPE_HOME, doc.getCustomer());
        paymentXML = paymentXML.replace(CommonConstants.PR_RECEPIT_ID, String.valueOf(doc.getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_CUST_ID, String.valueOf(doc.getCustomer().getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_RECEIPT_NO, String.valueOf(doc.getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_RECEIPT_DATE, doc.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_AMOUNT, String.valueOf(doc.getAmount()));
        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_AMOUNT_WORDS, CurrencyUtil.convert(Math.round(doc.getAmount())));

        if (doc.getPaydetails1() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, String.valueOf(doc.getPaydetails1()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, "-");

        if (doc.getPaydetails2() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS2, String.valueOf(doc.getPaydetails2()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS2, "-");

        if (doc.getPaydetails3() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, String.valueOf(doc.getPaydetails3()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, "-");

        if (doc.getPaydetails4() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, String.valueOf(doc.getPaydetails4()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, "-");

        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_REFNO, String.valueOf(doc.getReferenceno()));
        paymentXML = paymentXML.replace(CommonConstants.PR_PHONE, String.valueOf(doc.getCustomer().getPhone()));
        paymentXML = paymentXML.replace(CommonConstants.PR_OUTSTANDING, String.valueOf(doc.getCustomer().getOutStandingAmount()));
        paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS_TYPE, "Home");
        if (homeAddress != null) {
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS1, String.valueOf(homeAddress.getAddress1()));
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(homeAddress.getAddress2()));
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(homeAddress.getAddress2()));
            paymentXML = paymentXML.replace(CommonConstants.PR_CITY, String.valueOf(homeAddress.getCity().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_STATE, String.valueOf(homeAddress.getState().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_COUNTRY, String.valueOf(homeAddress.getCountry().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_PIN, String.valueOf(homeAddress.getPincode()));
        } else {
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS1, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_CITY, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_STATE, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_COUNTRY, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_PIN, "-");
        }
        paymentXML = paymentXML.replace(CommonConstants.PR_SUBSCR_ID, String.valueOf(doc.getCustomer().getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_CUST_EMAIL, String.valueOf(doc.getCustomer().getEmail()));

        return paymentXML;*/
        return assemblePaymentXML(doc, UtilsCommon.ADDR_TYPE_PRESENT, null, null,null);
    }

    public String assemblePaymentXML(CreditDocument doc, String addressType, Integer mvnoId) {
        return assemblePaymentXML(doc, addressType, null, doc.getInvoiceId(),mvnoId);
    }

    public String assemblePaymentXML(CreditDocument doc, String addressType, CustomerAddress address, Integer debitDocId,Integer mvnoId) {
        try {
            if (debitDocId != null) {
                DebitDocument docDebit = debitDocService.get(debitDocId,mvnoId);
                return PaymentDetailsXml.getPaymentDetails(doc, addressType, address, docDebit);
            } else {
                return PaymentDetailsXml.getPaymentDetails(doc, addressType, address, null);
            }
        } catch (Exception ex) {
            logger.error("Error while assemble payment xml: " + ex.getMessage());
        }
        /*= new String(CommonConstants.PAY_RECEIPT);

        if (address != null)
            address = custAddrService.findByAddressTypeAndCustomer(addressType, doc.getCustomer());

        paymentXML = paymentXML.replace(CommonConstants.PR_RECEPIT_ID, String.valueOf(doc.getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_CUST_ID, String.valueOf(doc.getCustomer().getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_RECEIPT_NO, String.valueOf(doc.getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_RECEIPT_DATE, doc.getCreatedate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_AMOUNT, String.valueOf(doc.getAmount()));
        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_AMOUNT_WORDS, CurrencyUtil.convert(Math.round(doc.getAmount())));

        if (doc.getPaydetails1() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, String.valueOf(doc.getPaydetails1()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS1, "-");

        if (doc.getPaydetails2() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS2, String.valueOf(doc.getPaydetails2()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS2, "-");

        if (doc.getPaydetails3() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, String.valueOf(doc.getPaydetails3()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS3, "-");

        if (doc.getPaydetails4() != null)
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, String.valueOf(doc.getPaydetails4()));
        else
            paymentXML = paymentXML.replace(CommonConstants.PR_PAY_DETAILS4, "-");

        paymentXML = paymentXML.replace(CommonConstants.PR_PAY_REFNO, String.valueOf(doc.getReferenceno()));
        paymentXML = paymentXML.replace(CommonConstants.PR_PHONE, String.valueOf(doc.getCustomer().getPhone()));
        paymentXML = paymentXML.replace(CommonConstants.PR_OUTSTANDING, String.valueOf(doc.getCustomer().getOutStandingAmount()));
        paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS_TYPE, "Home");
        if (address != null) {
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS1, String.valueOf(address.getAddress1()));
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(address.getAddress2()));
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, String.valueOf(address.getAddress2()));
            paymentXML = paymentXML.replace(CommonConstants.PR_CITY, String.valueOf(address.getCity().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_STATE, String.valueOf(address.getState().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_COUNTRY, String.valueOf(address.getCountry().getName()));
            paymentXML = paymentXML.replace(CommonConstants.PR_PIN, String.valueOf(address.getPincode()));
        } else {
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS1, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_ADDRESS2, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_CITY, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_STATE, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_COUNTRY, "-");
            paymentXML = paymentXML.replace(CommonConstants.PR_PIN, "-");
        }
        paymentXML = paymentXML.replace(CommonConstants.PR_SUBSCR_ID, String.valueOf(doc.getCustomer().getId()));
        paymentXML = paymentXML.replace(CommonConstants.PR_CUST_EMAIL, String.valueOf(doc.getCustomer().getEmail()));*/
        return "";
    }

    public List<CreditDocument> getCreditDocByCustomer(Customers customers, Boolean Is_reversed) {
        return this.entityRepository.findAllByCustomer(customers).stream().filter(data -> data.getIs_reversed() == false && (data.getMvnoId() == getMvnoIdFromCurrentStaff(customers.getId()).intValue() || data.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(customers.getId()) == 1)).collect(Collectors.toList());
    }

    public List<CreditDocument> getTdspendingCreditDocByCustomer(Customers customers) {
        return this.entityRepository.findAllByCustomer(customers).stream().filter(data -> data.getTds_received() == false && (data.getMvnoId() == getMvnoIdFromCurrentStaff(customers.getId()).intValue() || data.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(customers.getId()) == 1)).collect(Collectors.toList());
    }

    public List<CreditDocument> getAllPaymentByCustomer(Customers customers) {
        return this.entityRepository.findAllByCustomer(customers).stream().collect(Collectors.toList()).stream().filter(creditDic -> creditDic.getMvnoId() == getMvnoIdFromCurrentStaff(customers.getId()).intValue() || creditDic.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(customers.getId()) == 1).collect(Collectors.toList());
    }

    public List<PaymentHistoryDTO> getByCustId(Integer custId) {
        List<CreditDocument> creditDocuments = creditDocRepository.getAllByCustomer_IdAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(custId, "CREDITNOTE", "creditnote").stream().filter(creditDic -> creditDic.getMvnoId() == getMvnoIdFromCurrentStaff(custId).intValue() || creditDic.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(custId) == 1).collect(Collectors.toList());
        for (CreditDocument creditdocument : creditDocuments) {
            List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
            BooleanExpression booleanExpression1 = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(creditdocument.getId()));
            creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(booleanExpression1);
            for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
                if (creditDebitDocMapping.getDebtDocId() != null) {
                    DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                    if (Objects.nonNull(debitDocument)) {
                        creditdocument.setInvoiceNumber(debitDocument.getDocnumber());
                    }
                }
            }
        }
        List<PaymentHistoryDTO> paymentHistories = creditDocuments.stream().map(data -> creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        for (PaymentHistoryDTO paymentHistory : paymentHistories) {
            if (paymentHistory.getAmount() != null && paymentHistory.getAdjustedAmount() != null) {
                paymentHistory.setUnsettledAmount(paymentHistory.getAmount() - paymentHistory.getAdjustedAmount());
            }
            if (paymentHistory.getBankManagement() != null) {
                BankManagement bank = bankManagementRepository.findById(paymentHistory.getBankManagement()).orElse(null);
                if (bank != null) {
                    paymentHistory.setBankName(bank.getBankname());
                }
            }
        }
        return paymentHistories;
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("CreditDoc");
        List<CreditDocumentPojo> creditDocumentPojoList = new ArrayList<>();
        List<CreditDocument> creditDocumentList = entityRepository.findAll();
        for (CreditDocument doc : creditDocumentList)
            creditDocumentPojoList.add(convertCreditDocumentModelToCreditDocumentPojo(doc));
        createExcel(workbook, sheet, CreditDocumentPojo.class, creditDocumentPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CreditDocumentPojo> creditDocumentPojoList = new ArrayList<>();
        List<CreditDocument> creditDocumentList = entityRepository.findAll();
        for (CreditDocument creditDocument : creditDocumentList)
            creditDocumentPojoList.add(convertCreditDocumentModelToCreditDocumentPojo(creditDocument));
        createPDF(doc, CreditDocumentPojo.class, creditDocumentPojoList, null);
    }

    public List<CreditDocument> getAllByCustomer_IdOrderByIdDesc(Integer id) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.getAllByCustomer_IdOrderByIdDesc(id).stream().filter(creditDic -> creditDic.getMvnoId() == getMvnoIdFromCurrentStaff(null) || creditDic.getMvnoId() == null).collect(Collectors.toList());
    }

    public Double findTotalPaymentAmountByInvoice(Integer invoiceId) {
        return entityRepository.findTotalPaymentAmountByInvoice(invoiceId);
    }

    public Double findTotalPaymentAmountByInvoices(Integer invoiceId) {
        return entityRepository.findTotalPaymentAmountByInvoices(invoiceId);
    }

    public List<CreditDocument> FindPaymentToMap(Integer invoiceId) {
        List<CreditDocument> creditDocumentList = new ArrayList<>();
        if (invoiceId != null) {
            Integer customerId = debitDocRepository.findById(invoiceId).get().getCustomer().getId();
            QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
            BooleanExpression booleanExpression = qCreditDocument.isNotNull().and(qCreditDocument.customer.id.eq(customerId)).and(qCreditDocument.status.ne("rejected")).and(qCreditDocument.paytype.ne(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL)).and(qCreditDocument.amount.subtract(qCreditDocument.adjustedAmount).gt(0));
            creditDocumentList = IterableUtils.toList(creditDocRepository.findAll(booleanExpression));

        }

        return creditDocumentList;
    }


    public List<ViewAdjustedInvoicePojo> FindInvoiceToPayment(Integer paymentId) {
        List<ViewAdjustedInvoicePojo> ViewAdjustedInvoicePojos = new ArrayList<>();
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        if (paymentId != null) {
            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
            BooleanExpression booleanExpression1 = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.creditDocId.eq(paymentId));
            creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(booleanExpression1);

            for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
                if (creditDebitDocMapping.getDebtDocId() != null) {
                    DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                    CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getCreditDocId()).orElse(null);
                    if (Objects.nonNull(debitDocument)) {
                        ViewAdjustedInvoicePojo ViewAdjustedInvoicePojo = new ViewAdjustedInvoicePojo();
                        ViewAdjustedInvoicePojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
                        ViewAdjustedInvoicePojo.setTotalamount(debitDocument.getTotalamount());
                        ViewAdjustedInvoicePojo.setBilldate(debitDocument.getBilldate());
                        ViewAdjustedInvoicePojo.setInvoiceNumber(debitDocument.getDocnumber());
                        List<CreditDocChargeRel> creditDocChargeRels = creditDocChargeRelRepository.findAllByCreditDocument(creditDocument);
                        if (!CollectionUtils.isEmpty(creditDocChargeRels))
                            ViewAdjustedInvoicePojo.setCreditDocChargeRelDTOList(creditDocChargeRelMapper.domainToDTO(creditDocChargeRels, new CycleAvoidingMappingContext()));
                        if (creditDebitDocMapping.getCreditDocId() != null)
                            ViewAdjustedInvoicePojo.setDocnumber(creditDocument.getCreditdocumentno());
                        ViewAdjustedInvoicePojos.add(ViewAdjustedInvoicePojo);
                    }
                } else if (creditDebitDocMapping.getWithdrawId() != null) {
                    CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getWithdrawId()).orElse(null);
                    if (creditDocument != null) {
                        ViewAdjustedInvoicePojo ViewAdjustedInvoicePojo = new ViewAdjustedInvoicePojo();
                        ViewAdjustedInvoicePojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
                        ViewAdjustedInvoicePojo.setTotalamount(creditDocument.getAmount());
                        ViewAdjustedInvoicePojo.setBilldate(creditDocument.getCreatedate());
                        ViewAdjustedInvoicePojo.setDocnumber(creditDocument.getCreditdocumentno());
                        ViewAdjustedInvoicePojos.add(ViewAdjustedInvoicePojo);
                    }
                }
            }
        }
        // ViewAdjustedInvoicePojos = ViewAdjustedInvoicePojos.stream().filter(ViewAdjustedInvoicePojo -> ViewAdjustedInvoicePojo.getAdjustedAmount() > 0).collect(Collectors.toList());

        return ViewAdjustedInvoicePojos;
    }


    @Override
    public CreditDocument get(Integer id, Integer mvnoId) {
        CreditDocument creditDocument = super.get(id,mvnoId);
        if (creditDocument != null && (getMvnoIdFromCurrentStaff(creditDocument.getCustomer().getId()) == 1 || (creditDocument.getMvnoId() == getMvnoIdFromCurrentStaff(creditDocument.getCustomer().getId()).intValue() || creditDocument.getMvnoId() == 1)))
            return creditDocument;
        return null;
    }

    public CreditDocument getcwsc(Integer id,Integer mvnoId) {
        CreditDocument creditDocument = super.get(id,mvnoId);
        return creditDocument;
    }


    public CreditDocument getEntityForUpdateAndDelete(Integer id,Integer mvnoId) {
        CreditDocument creditDocument = get(id,mvnoId);
        if (creditDocument == null || !(getMvnoIdFromCurrentStaff() == 1 || getMvnoIdFromCurrentStaff() == creditDocument.getMvnoId().intValue()))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return creditDocument;
    }

    public CreditDocument getEntityForUpdateAndDeleteForCWSC(Integer id,Integer mvnoId) {
        CreditDocument creditDocument = getcwsc(id,mvnoId);
        return creditDocument;
    }

    private BankManagement validateBankManagement(String name) {
        try {
            if (name != null && !name.isEmpty()) {
                return bankManagementService.validateBankByName(name);
            }
            return null;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public Long getCountOfApprovalReuqestforPaymentByStaff(StaffUser staffUserTemp) {
        return creditDocRepository.findMinimumApprovalReuqestForPlanByStaff(staffUserTemp.getId());
    }

    private StaffLedgerDetails saveStaffLedgerDetails(Optional<CreditDocument> creditDocument, StaffUser loggedInUser) {
        try {
            StaffLedgerDetails staffLedgerDetails = new StaffLedgerDetails();
            staffLedgerDetails.setCustId(creditDocument.get().getCustomer().getId().longValue());
            staffLedgerDetails.setCreditDocId(creditDocument.get().getId().longValue());
            staffLedgerDetails.setAmount(creditDocument.get().getAmount());
            staffLedgerDetails.setBankId(creditDocument.get().getBankManagement());
            staffLedgerDetails.setChequedate(creditDocument.get().getChequedate());
            staffLedgerDetails.setChequeno(creditDocument.get().getPaydetails2());
            staffLedgerDetails.setStatus(CommonConstants.STAFF_WALLET_STATUS.PENDING);
            staffLedgerDetails.setAction("Collected");
            staffLedgerDetails.setTransactionType("CR");
            staffLedgerDetails.setStaff(staffUserRepository.findById(creditDocument.get().getCreatedById()).get());
            staffLedgerDetails.setRemarks(creditDocument.get().getRemarks());
            staffLedgerDetails.setDate(LocalDate.now());
            staffLedgerDetails.setPaymentMode(creditDocument.get().getPaymode());
            staffLedgerDetailsRepository.save(staffLedgerDetails);

            Double crAmount = 0.00;
            StaffUser staffUser = staffUserRepository.findById(creditDocument.get().getCreatedById()).orElse(null);
            Double collected = staffUser.getTotalCollected();
            if (staffLedgerDetails.getTransactionType().equalsIgnoreCase("CR")) {
                if (collected == null) {
                    collected = 0.0000;
                }
                crAmount += collected + staffLedgerDetails.getAmount();
            }
            staffUser.setTotalCollected(crAmount);
            Double transferredAmt = staffUser.getTotalTransferred();
            if (transferredAmt == null) {
                transferredAmt = 0.000;
                staffUser.setTotalTransferred(transferredAmt);
            }
            Double Available = crAmount - transferredAmt;
            staffUser.setAvailableAmount(Available);
            staffUserRepository.save(staffUser);
            return staffLedgerDetails;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public GenericDataDTO getPaymentApprovalsList(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qCreditDocument.isNotNull().and(qCreditDocument.isDelete.eq(false)).and(qCreditDocument.status.eq("pending")).and(qCreditDocument.approverid.in(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCreditDocument.mvnoId.eq(1).or(qCreditDocument.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCreditDocument.customer.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<CreditDocument> paginationList = entityRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            return creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext());
        }).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public void uploadDocument(RecordPaymentPojo pojo, MultipartFile file) throws Exception {
        String SUBMODULE = "Payment" + " [uploadDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).get(0).getValue();
        List<RecordPayment> finalResponseList = new ArrayList<>();
        try {
            Customers customers = customersService.getById(pojo.getCustomerid());
            String subFolderName = customers.getUsername().trim() + "/";
            String path = PATH + subFolderName;
            logger.debug(SUBMODULE + ":File Path:" + path);
            if (null != pojo.getFilename()) {
                System.out.println(file.getSize());

                MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                if (null != file1) {
                    pojo.setUniquename(fileUtility.saveFileToServer(file1, path));
                    pojo.setFilename(pojo.getFilename());

                }


            } else {
                if (null != pojo) {
                    if (null != pojo.getFilename() && null != pojo.getFilename() && !pojo.getFilename().equalsIgnoreCase(pojo.getFilename())) {
                        fileUtility.removeFileAtServer(pojo.getUniquename(), path);
                    }

                    MultipartFile file1 = fileUtility.getFileFromArrayForTicket(file);
                    if (null != file1) {
                        pojo.setUniquename(fileUtility.saveFileToServer(file1, path));
                    }
                    RecordPayment obj = convertRecordPaymentPojoToRecordPaymentModel(pojo);

                    finalResponseList.add(obj);
                }


            }

        } catch (Exception ex) {
            logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public void addLedgeAfterApproval(CreditDocument creditDocument) {

        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        Optional<Customers> customers = customersRepository.findById(creditDocument.getCustomer().getId());
        QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
        BooleanExpression booleanExpression = qCustomerLedger.isNotNull().and(qCustomerLedger.customer.id.eq(customers.get().getId()));
        Double paymentAmount = creditDocument.getAmount();

        ledger = ledgerRepository.findOne(booleanExpression).orElse(null);
        if (Objects.nonNull(ledger)) {
            ledger.setTotalpaid(ledger.getTotalpaid() + creditDocument.getAmount());
            ledger.setTotaldue(ledger.getTotaldue() - creditDocument.getAmount());
            ledgerService.save(ledger);
        }

        ledgerDtls = new CustomerLedgerDtls();
        if (creditDocument.getTdsamount() != null && creditDocument.getTdsamount() > 0) {
            paymentAmount = paymentAmount - creditDocument.getTdsamount();
        }
        if (creditDocument.getAbbsAmount() != null && creditDocument.getAbbsAmount() > 0) {
            paymentAmount = paymentAmount - creditDocument.getAbbsAmount();
        }
        ledgerDtls.setAmount(paymentAmount);
        ledgerDtls.setPaymentMode(creditDocument.getPaymode());
        if (creditDocument.getBankManagement() != null) {
            BankManagement bankManagement = bankManagementRepository.findById(creditDocument.getBankManagement()).orElse(null);
            if (bankManagement != null) {
                ledgerDtls.setBank(bankManagement.getBankname());
                ledgerDtls.setBranch(bankManagement.getBankcode());
            }
        }
        ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
        ledgerDtls.setCreditdocid(creditDocument.getId());
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsDelete(false);
        ledgerDtls.setDescription(creditDocument.getRemarks());
        if (creditDocument.getPaytype().equalsIgnoreCase("advance")) {
            if(customers.get()!=null && (customers.get().getId().intValue()==1 || customers.get().getId().intValue()==2))
                ledgerDtls.setTranscategory(CommonConstants.TRANS_BUSINESS_PROMOTION);
            else
                ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
        } else if (creditDocument.getPaytype().equalsIgnoreCase("invoice")) {
            if(customers.get()!=null && (customers.get().getId().intValue()==1 || customers.get().getId().intValue()==2))
                ledgerDtls.setTranscategory(CommonConstants.TRANS_BUSINESS_PROMOTION);
            else
                ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
        } else if (creditDocument.getPaytype().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)) {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CREDIT_NOTE);
        } else {
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_REFUND);
        }

        if (customers.isPresent()) ledgerDtls.setCustomer(customers.get());
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        if (creditDocument.getType().equalsIgnoreCase(CommonConstants.TRANS_TYPE_DEBIT)) {
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
        }

        ledgerDtls = ledgerDtlsService.save(ledgerDtls);

        if (creditDocument.getTdsamount() != null && creditDocument.getTdsamount() > 0) {
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
            customerLedgerDtls.setCreditdocid(creditDocument.getId());
            customerLedgerDtls.setCREATE_DATE(LocalDateTime.now());
            customerLedgerDtls.setIsDelete(false);
            customerLedgerDtls.setTranscategory("TDS");
            customerLedgerDtls.setTranstype("CR");
            customerLedgerDtls.setCustomer(creditDocument.getCustomer());
            customerLedgerDtls.setDescription(creditDocument.getRemarks());
            customerLedgerDtls.setAmount(creditDocument.getTdsamount());
            customerLedgerDtls.setPaymentMode(creditDocument.getPaymode());
            ledgerDtlsService.save(customerLedgerDtls);
        }
        if (creditDocument.getAbbsAmount() != null && creditDocument.getAbbsAmount() > 0) {
            CustomerLedgerDtls customerLedgerDtls = new CustomerLedgerDtls();
            customerLedgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
            customerLedgerDtls.setCreditdocid(creditDocument.getId());
            customerLedgerDtls.setCREATE_DATE(LocalDateTime.now());
            customerLedgerDtls.setIsDelete(false);
            customerLedgerDtls.setTranscategory("ABBS");
            customerLedgerDtls.setTranstype("CR");
            customerLedgerDtls.setCustomer(creditDocument.getCustomer());
            customerLedgerDtls.setDescription(creditDocument.getRemarks());
            customerLedgerDtls.setAmount(creditDocument.getAbbsAmount());
            customerLedgerDtls.setPaymentMode(creditDocument.getPaymode());
            ledgerDtlsService.save(customerLedgerDtls);
        }
    }


    boolean checkCreditNoteIsAllowedOrNot(DebitDocument debitDocument) {
        QCreditDebitDocMapping creditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = creditDebitDocMapping.isNotNull().and(creditDebitDocMapping.debtDocId.eq(debitDocument.getId()));

        return false;
    }

    public void createDBRForCreditNote(CreditDocument creditDocument, Double amount) {
        QCustomerDBR qCustomerDBR = QCustomerDBR.customerDBR;
        BooleanExpression exp1 = qCustomerDBR.isNotNull();
        exp1 = exp1.and(qCustomerDBR.custid.eq(creditDocument.getCustomer().getId().longValue())).and(qCustomerDBR.pendingamt.gt(0));
        List<CustomerDBR> customerDBRList = (List<CustomerDBR>) customerDBRRepository.findAll(exp1);
        CustomerDBR dbr = new CustomerDBR();
        dbr.setCustid(creditDocument.getCustomer().getId().longValue());
        dbr.setStartdate(LocalDate.now());
        dbr.setEnddate(LocalDate.now());
        dbr.setPendingamt(-creditDocument.getAmount());
        dbr.setCustname(creditDocument.getCustomer().getCustname());
        dbr.setStatus("Active");
        dbr.setCusttype(creditDocument.getCustomer().getCusttype());

        if (!CollectionUtils.isEmpty(customerDBRList)) {
            CustomerDBR customerDBR = customerDBRList.get(0);
            dbr.setPendingamt(-customerDBR.getDbr());
            dbr.setDbr(customerDBR.getDbr() - customerDBR.getOffer_price());
        } else {
            dbr.setDbr(-amount);
            dbr.setPendingamt(-amount);
        }

        customerDBRRepository.save(dbr);
    }

    public List<CreditDocument> getWithdrawPayments(Integer customerId, PaginationRequestDTO paginationRequestDTO) {
//        super.generatePageRequest(paginationRequestDTO.getPage(),paginationRequestDTO.getPageSize(),"createdate",1);
        List<CreditDocument> resultList = creditDocRepository.getWithdrawPayments(customerId, super.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", 1));
        return resultList.stream().map(x -> {
            BigDecimal remainingAmount = BigDecimal.valueOf(x.getRemainingAmount()).setScale(2, BigDecimal.ROUND_HALF_UP);
            x.setRemainingAmount(remainingAmount.doubleValue());
            return x;
        }).collect(Collectors.toList());
    }

    @Transactional
//    public RecordPaymentPojo withDrawal(RecordPaymentPojo pojo, boolean iswithdrawal, boolean isInvoiceVoid, boolean isRevoked) throws Exception {
//        Customers customers = customersRepository.findById(pojo.getCustomerid()).orElse(null);
//        if (customers != null) {
//            double withDrawalAmount = pojo.getAmount();
//            if (withDrawalAmount <= 0) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw -ve or 0 amount.", null);
//            }
//            if (pojo.getInvoiceId().size() == 0) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw as payment not selected.", null);
//            } else {
//                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
//                double totalRemainAmount = creditDocRepository.totalWithDrawAmount(pojo.getCustomerid());
//                double totalPendingAmount = creditDocRepository.totalPendingAmount(pojo.getCustomerid());
//                double remeaningAmount = totalRemainAmount - totalPendingAmount;
//
//                if (pojo.getAmount() - remeaningAmount > 0.1) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not withdraw more than remaining amount.", null);
//                } else {
//                    List<CreditDocument> creditDocumentList = IterableUtils.toList(creditDocRepository.findAll(qCreditDocument.id.in(pojo.getInvoiceId())));
//                    pojo.setPaytype(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL);
//                    pojo = save(pojo, true, false, false,null);
//                    for (CreditDocument creditDocument : creditDocumentList) {
//                        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
//                        creditDebitDocMapping.setCreditDocId(creditDocument.getId());
//                        creditDebitDocMapping.setWithdrawId(pojo.getCreditDocId());
//                        creditDebitDocMapping.setAdjustedAmount(0d);
//                        creditDebtMappingRepository.save(creditDebitDocMapping);
//                    }
//                }
//            }
//        }
//        return pojo;
//
//
//    }

    public void generateAutoApprovePayment(String payType, String type, String remarks, String mode, Double amount, Customers customers) {
        CreditDocument creditDocument = new CreditDocument();
        creditDocument.setAmount(amount);
        creditDocument.setCustomer(customers);
        creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
        creditDocument.setPaymentdate(LocalDate.now());
        creditDocument.setPaymode(mode);
        creditDocument.setPaytype(payType);
        creditDocument.setType(type);
        creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
        creditDocument.setIsDelete(false);
        creditDocument.setMvnoId(customers.getMvnoId());
        creditDocument.setBuID(customers.getBuId());
        creditDocument.setRemarks(remarks);
        creditDocument.setTdsamount(0d);
        creditDocument.setAbbsAmount(0d);
        if (getLoggedInUser().getLco()) creditDocument.setLcoid(getLoggedInUser().getPartnerId());
        else creditDocument.setLcoid(null);

        try {
            //add adjusted amount against invoice
            creditDocument = creditDocRepository.save(creditDocument);
            addLedgeAfterApproval(creditDocument);
//            dbrService.creditNoteDbrEntry(debitDocument, creditDocument.getAmount());


        } catch (Exception e) {
//            throw new RuntimeException("Exception when creating credit note for invoice: " + debitDocument.getId());
        }
    }

    public void adjustWithDrawal(CreditDocument creditDocument) {
        Double withDrawedAmount = creditDocument.getAmount();
        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.withdrawId.eq(creditDocument.getId()));
        creditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAll(booleanExpression));
        int i = 0;
        while (i < IterableUtils.toList(creditDebitDocMappings).size() && withDrawedAmount > 0 && creditDebitDocMappings.size() > 0) {
            CreditDocument selectedCreditDoc = creditDocRepository.findById(creditDebitDocMappings.get(i).getCreditDocId()).orElse(null);
            Double withDrawalableAmount = 0d;
            if (selectedCreditDoc.getAdjustedAmount() == null) {
                withDrawalableAmount = selectedCreditDoc.getAmount();
            } else {
                withDrawalableAmount = selectedCreditDoc.getAmount() - selectedCreditDoc.getAdjustedAmount();
            }
            Double remainingAmount = withDrawedAmount - withDrawalableAmount;
            if (remainingAmount == 0) {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawedAmount);
                selectedCreditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            } else if (remainingAmount < 0) {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawedAmount);
            } else {
                creditDebitDocMappings.get(i).setAdjustedAmount(withDrawalableAmount);
                selectedCreditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            }
            if (selectedCreditDoc.getAdjustedAmount() == null) {
                selectedCreditDoc.setAdjustedAmount(withDrawedAmount);
            } else {
                selectedCreditDoc.setAdjustedAmount(selectedCreditDoc.getAdjustedAmount() + withDrawedAmount);
            }
            withDrawedAmount = remainingAmount;
            creditDebtMappingRepository.save(creditDebitDocMappings.get(i));
            selectedCreditDoc = creditDocRepository.save(selectedCreditDoc);
            i++;
            if (selectedCreditDoc.getPaymode().equalsIgnoreCase("Credit Note") && (selectedCreditDoc.getAmount() - selectedCreditDoc.getAdjustedAmount() == 0)) {

                List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(selectedCreditDoc.getId());
                if (creditDebitDocMappingList.size() > 0) {
                    creditDebitDocMappingList.forEach(creditDebitDocMapping -> {
                        if (creditDebitDocMapping.getDebtDocId() != null) {
                            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
                            if (debitDocument != null) {
                                Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                                if (totalCreditNoteGenerated.equals(debitDocument.getTotalamount())) {
                                    debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                } else {
                                    double adjustedAmount = 0;
                                    List<CreditDebitDocMapping> creditDebitmappingForDebitDocument = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                                    for (CreditDebitDocMapping debitDocMapping : creditDebitmappingForDebitDocument) {
                                        CreditDocument creditDocument1 = creditDocRepository.findById(debitDocMapping.getCreditDocId()).orElse(null);
                                        if (!creditDocument.getPaymode().equalsIgnoreCase("Credit Note")) {
                                            adjustedAmount = adjustedAmount + debitDocMapping.getAdjustedAmount();
                                        }
                                    }
                                    if (debitDocument.getTotalamount().equals(adjustedAmount)) {
                                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                                    } else if (debitDocument.getTotalamount() > adjustedAmount) {
                                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                                    } else if (adjustedAmount == 0) {
                                        debitDocument.setPaymentStatus(null);
                                    }
                                }
                            }
                        }
                    });
                }


            }
            if (selectedCreditDoc.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && selectedCreditDoc.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED) && selectedCreditDoc.getInvoiceId() != null) {
                Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(selectedCreditDoc.getInvoiceId());
                if (oldDebitDoc.isPresent() && selectedCreditDoc.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                    oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                } else {
                    oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                }
                debitDocRepository.save(oldDebitDoc.get());
            }
        }
        CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument, creditDebitDocMappings);
        kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//        messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS);
        //messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);
    }

    public void adjustCreditNote(Optional<CreditDocument> creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings,Integer mvnoId) {
        for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMapping.getDebtDocId()).orElse(null);
            List<CustomerChargeDBR> customerChargeDBRList = dbrService.findAllCustomerChargedbrByDebitDoc(debitDocument);
            Double totalCreditNoteGenerated = creditDocRepository.checkCreditNoteIsAllowedOrNot(debitDocument.getId(), CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
            if (totalCreditNoteGenerated == 0) {
                if (creditDocument.get().getAmount() > debitDocument.getTotalamount()) {
                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note becauae invoice amount exceeds", null);
                }
            } else if (totalCreditNoteGenerated > debitDocument.getTotalamount()) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note because invoice amount exceeds", null);
            }

            if (creditDocument.isPresent() && creditDocument.get().getCustomer().getId().intValue() == 1) {
                if (creditDocument.get().getInvoiceId() != null)
                {
                    if (debitDocument.getIsCNEnable()) {
                        creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                        if(creditDocument.get().getAdjustedAmount()!=null)
                            creditDocument.get().setAdjustedAmount(creditDocument.get().getAdjustedAmount()+creditDocument.get().getAmount());
                        else
                            creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount());
                        creditDocRepository.save(creditDocument.get());

                        creditDebitDocMapping.setAdjustedAmount(creditDocument.get().getAmount());
                        creditDebitDocMapping.setIsDeleted(false);
                        creditDebitDocMapping.setDebtDocId(debitDocument.getId());
                        creditDebitDocMapping.setCreditDocId(creditDocument.get().getId());
                        creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

                        List<CreditDebitDocMapping> debitDocMapping=creditDebtMappingRepository.findByCreditDocId(creditDocument.get().getId());

                        if(debitDocMapping!=null && !debitDocMapping.isEmpty())
                        {
                            debitDocMapping.get(0).setAdjustedAmount(creditDocument.get().getAmount());
                            creditDebtMappingRepository.save(debitDocMapping.get(0));
                        }
                        if(debitDocument.getAdjustedAmount()!=null)
                            debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+creditDocument.get().getAmount());
                        else
                            debitDocument.setAdjustedAmount(creditDocument.get().getAmount());
                        reversalPaymentForOrg(creditDocument.get().getAmount(), debitDocument,true,mvnoId);
                        return;
                    }
                }
                return;
            }

            DecimalFormat df = new DecimalFormat("#.00");
            List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
            Double pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
            Double pendingRevenueWithTax = debitDocService.getPendingRevenueWithTaxAtCurrentDate(debitDocument);
            Double creditAmountExcludeTax = dbrService.getCreditNotePriceExcludingTax(debitDocument, creditDocument.get().getAmount());

            //if(creditDocument.get().getAmount() > pendingRevenueWithTax)
            //{
            // throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Can not generate credit note because Pending Revenue amount exceeds", null);
            // }

            List<CustPlanMappping> custPlanMapppings = IterableUtils.toList(custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId()));
            List<Long> cprids = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId().longValue()).collect(Collectors.toList());


            if ((creditDocument.get().getAmount() + totalCreditNoteGenerated >= debitDocument.getTotalamount()) || (pendingRevenueWithTax.doubleValue() - creditDocument.get().getAmount().doubleValue() < 0)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    custPlanMappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                    //ANG-4987: resolved
                    if (custPlanMappping.getStartDate().isAfter(LocalDateTime.now())) {
                        custPlanMappping.setStartDate(LocalDateTime.now().minusMinutes(1));
                        custPlanMappping.setEndDate(LocalDateTime.now());
                        custPlanMappping.setExpiryDate(LocalDateTime.now());
                    } else {
                        custPlanMappping.setEndDate(LocalDateTime.now().minusMinutes(1));
                        custPlanMappping.setExpiryDate(LocalDateTime.now().minusMinutes(1));
                    }
                    if (custPlanMappping.getStartDate().isAfter(custPlanMappping.getEndDate())) {
                        custPlanMappping.setStartDate(LocalDateTime.now());
                        custPlanMappping.setEndDate(custPlanMappping.getStartDate().plusSeconds(1));
                        custPlanMappping.setExpiryDate(custPlanMappping.getStartDate().plusSeconds(1));
                    }

                    custPlanMappingService.update(custPlanMappping, "");
                });
                ezBillServiceUtility.deactivateService(custPlanMapppings, 13);
            }

            Double amountToBePaid = 0d;
            Double remainingAmount = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            remainingAmount = creditDocument.get().getAmount() - amountToBePaid;
//            if all amount from credit note adjusted with invoice
            if (remainingAmount == 0) {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                if (debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount()) || (pendingRevenueWithTax.doubleValue() == creditDocument.get().getAmount().doubleValue()))
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount());
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDebitDocMapping.setAdjustedAmount(creditDocument.get().getAmount());
            }
//            when amount from credit note is greater than pending amount of invoice
            else if (remainingAmount > 0) {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount() - remainingAmount);
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
                creditDebitDocMapping.setAdjustedAmount(amountToBePaid);
            }
//            when amount from credit note is fully adjusted but invoice has some amount left to adjust
            else {
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(creditDocument.get().getAmount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.get().getAmount());
                }
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount());
                creditDebitDocMapping.setAdjustedAmount(creditDocument.get().getAmount());
            }

//           if total amount of invoice adjusted through credit note or total amount of credit note generated for same invoice set invoice status cancelled

            if (creditDocument.get().getAmount() + totalCreditNoteGenerated == debitDocument.getTotalamount() || (pendingRevenueWithTax.doubleValue() == creditDocument.get().getAmount().doubleValue())) {
                debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                if (cprids.size() > 0) {
                    List<Integer> debitdocids = custPlanMappingRepository.findAllByCustRefId(cprids);
                    if (debitdocids.size() > 0) {
                        List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debitdocids);
                        debitDocuments.stream().forEach(i -> i.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED));
                        debitDocRepository.saveAll(debitDocuments);
                    }
                }
            }
//          if after all adjustment there is some amount left in credit note set inovice as payable
            if (remainingAmount > 0) {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
            }
            creditDebtMappingRepository.save(creditDebitDocMapping);
            debitDocRepository.save(debitDocument);
            try {
                if (!creditDocument.get().getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)) {
                    setCreditNotdataToTable(customerChargeDBRList, creditDocument.get(), debitDocument);
                }
            } catch (Exception ex) {
                logger.error("Error while adding CN charge rel data: " + ex.getMessage());
            }
            try {
                sendDataToNAV(creditDocument, debitDocument);
            } catch (Exception e) {
                logger.error("Error in integration" + e.getStackTrace());
            }
            dbrService.creditNoteDbrEntry(debitDocument, creditDocument.get().getAmount(), true);
            CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument.get(), creditDebitDocMappings);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//            messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS);
            //messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);
            debitDocService.adjustBillToSubisuInvoiceWithCreditNote(creditDocument.get().getAmount(),debitDocument,mvnoId);
        }
    }


    public DebitDocument adjustOldAdjustedData(DebitDocument debitDocument, CreditDocument creditDocument) {

        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        BooleanExpression booleanExpression = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.eq(debitDocument.getId()));
        List<CreditDebitDocMapping> oldCreditDebitDocMappings = IterableUtils.toList(creditDebtMappingRepository.findAll(booleanExpression)).stream().sorted((o1, o2) -> o1.getId().compareTo(o2.getId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(oldCreditDebitDocMappings)) {
            List<Integer> cdids = oldCreditDebitDocMappings.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());

            if (debitDocument.getAdjustedAmount() != null && debitDocument.getAdjustedAmount() > 0 && debitDocument.getTotalamount().equals(creditDocument.getAmount())) {
                debitDocument.setAdjustedAmount(0d);
                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                BooleanExpression expression = qCreditDocument.isNotNull().and(qCreditDocument.type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_PAYMENT)).and(qCreditDocument.id.in(cdids));
                List<CreditDocument> creditDocuments = (List<CreditDocument>) creditDocRepository.findAll(expression);
                if (!CollectionUtils.isEmpty(creditDocuments)) {
                    List<CreditDebitDocMapping> paymentDebitMap = new ArrayList<>();
                    creditDocuments = creditDocuments.stream().peek(creditDoc -> {
                        creditDoc.setAdjustedAmount(0d);
                        creditDoc.setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                        CreditDebitDocMapping debitDocMapping = oldCreditDebitDocMappings.stream().filter(cdm -> cdm.getCreditDocId().equals(creditDoc.getId())).findFirst().get();
                        paymentDebitMap.add(debitDocMapping);
                    }).collect(Collectors.toList());
                    creditDocRepository.saveAll(creditDocuments);
                    if (!CollectionUtils.isEmpty(paymentDebitMap))
                        creditDebtMappingRepository.deleteInBatch(paymentDebitMap);
                }

            } else if (debitDocument.getAdjustedAmount() != null && debitDocument.getAdjustedAmount() > 0 && debitDocument.getTotalamount() > creditDocument.getAmount()) {
                if (debitDocument.getAdjustedAmount() > creditDocument.getAmount() && debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount()))
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() - creditDocument.getAmount());

//                if(oldCreditDebitDocMappings.get(0).getAdjustedAmount() > creditDocument.getAmount())
//                    oldCreditDebitDocMappings.get(0).setAdjustedAmount(oldCreditDebitDocMappings.get(0).getAdjustedAmount() - creditDocument.getAmount());
//                else
//                    if(debitDocument.getTotalamount().equals(creditDocument.getAmount())) {
////                    oldCreditDebitDocMappings.get(0).setAdjustedAmount(0d);
////                    oldCreditDebitDocMappings.get(0).setDebtDocId(null);
//                }
                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                BooleanExpression expression = qCreditDocument.isNotNull().and(qCreditDocument.type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_PAYMENT)).and(qCreditDocument.id.eq(oldCreditDebitDocMappings.get(0).getCreditDocId()));
                Optional<CreditDocument> creditDocuments = creditDocRepository.findOne(expression);
                if (creditDocuments.isPresent()) {
                    if (creditDocuments.get().getAdjustedAmount() > creditDocument.getAmount()) {
                        creditDocuments.get().setAdjustedAmount(creditDocuments.get().getAdjustedAmount() - creditDocument.getAmount());
                        if (creditDocuments.get().getAmount() > creditDocuments.get().getAdjustedAmount()) {
                            creditDocuments.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                        } else if (creditDocuments.get().getAdjustedAmount() == 0) {
                            creditDocuments.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                        }
                        creditDocRepository.save(creditDocuments.get());
                    }

                }
                creditDebtMappingRepository.save(oldCreditDebitDocMappings.get(0));
            }
        }
        return debitDocument;
    }

    public void adjustPayment(Optional<CreditDocument> creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings,Boolean isAdjusted ) {
        Double paymentAmount2 = creditDocument.get().getAmount();
        int i = 0;
        while (i < IterableUtils.toList(creditDebitDocMappings).size() && paymentAmount2 > 0 && creditDebitDocMappings.size() > 0) {
            DebitDocument debitDocument = debitDocRepository.findById(creditDebitDocMappings.get(i).getDebtDocId()).orElse(null);
            Double amountToBePaid = 0d;
            Double paymentAmount = creditDebitDocMappings.get(i).getAmount();
            DecimalFormat df = new DecimalFormat("0.00000");

            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }

            amountToBePaid = Double.parseDouble(df.format(amountToBePaid));

            Double remainingAmountFromPayment = paymentAmount - amountToBePaid;

            remainingAmountFromPayment = Double.parseDouble(df.format(remainingAmountFromPayment));

//            if payment is fully adjusted
            if (remainingAmountFromPayment == 0.0d) {
                changeStatusDisableToActive(creditDocument, creditDebitDocMappings);
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);

                QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
                BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
                exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(debitDocument.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
                List<TempPartnerLedgerDetail> details = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);

                if (details != null && !details.isEmpty()) {
                    tempPartnerLedgerDetailsRepository.deleteAll(details);
                    partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details);
                }

                creditDebitDocMappings.get(i).setAdjustedAmount(paymentAmount);
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }
                /**New Method for disable to active if payment adjusted**/


            }
//            if payment is fully adjusted but some amount still left in invoice to be adjusted
            else if (remainingAmountFromPayment < 0) {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(paymentAmount);
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                }
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDebitDocMappings.get(i).setAdjustedAmount(paymentAmount);
            }
//          if after adjustment some amount left in payment to be adjusted
            else {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                QTempPartnerLedgerDetail qTempPartnerLedgerDetail1 = QTempPartnerLedgerDetail.tempPartnerLedgerDetail;
                BooleanExpression exp1 = qTempPartnerLedgerDetail1.isNotNull();
                exp1 = exp1.and(qTempPartnerLedgerDetail1.debitDocId.eq(debitDocument.getId().longValue())).and(qTempPartnerLedgerDetail1.isDeleted.eq(false));
                List<TempPartnerLedgerDetail> details = (List<TempPartnerLedgerDetail>) tempPartnerLedgerDetailsRepository.findAll(exp1);

                if (details != null && !details.isEmpty()) {
                    tempPartnerLedgerDetailsRepository.deleteAll(details);
                    partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(details);
                }

                if (debitDocument.getAdjustedAmount() == null) {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    creditDebitDocMappings.get(i).setAdjustedAmount(debitDocument.getTotalamount());
                } else {
                    creditDebitDocMappings.get(i).setAdjustedAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                }

            }

            paymentAmount = remainingAmountFromPayment;
            debitDocument = debitDocRepository.save(debitDocument);
            creditDebtMappingRepository.save(creditDebitDocMappings.get(i));
            i++;
            if (paymentAmount < 0) {
                creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount());
            } else {
                creditDocument.get().setAdjustedAmount(creditDocument.get().getAmount() - paymentAmount);
            }
        }
        if(!isAdjusted) {
            CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument.get(), creditDebitDocMappings);
            kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
        }
//        messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_SUCCESS);
        //messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_KPI);
    }

    public ChequeDetailsPojo getChequeDetails(Integer id) {
        CreditDocument creditDocument = creditDocRepository.findById(id).get();
        ChequeDetailsPojo chequeDetailsPojo = new ChequeDetailsPojo();
        if (creditDocument != null) {


            chequeDetailsPojo.setChequedate(creditDocument.getPaydetails3());
            chequeDetailsPojo.setChequeNo(creditDocument.getPaydetails2());
            chequeDetailsPojo.setBranch(creditDocument.getPaydetails1());
            chequeDetailsPojo.setAmount(String.valueOf(creditDocument.getAmount()));

        }

        return chequeDetailsPojo;
    }

    public void addLedgerAndLedgerDetailEntry(CreditDocument creditDocument, Customers customers, boolean isVoidInvoice) {
        CustomerLedger ledger = null;
        CustomerLedgerDtls ledgerDtls = null;

        QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
        BooleanExpression booleanExpression = qCustomerLedger.isNotNull().and(qCustomerLedger.customer.id.eq(customers.getId()));

        if (creditDocument.getAdjustedAmount() > 0.0d) {
            ledger = ledgerRepository.findOne(booleanExpression).orElse(null);
            if (Objects.nonNull(ledger)) {
                ledger.setTotalpaid(ledger.getTotalpaid() + creditDocument.getAmount());
                ledger.setTotaldue(ledger.getTotaldue() - creditDocument.getAmount());
                ledgerService.save(ledger);
            }

            ledgerDtls = new CustomerLedgerDtls();
            ledgerDtls.setAmount(creditDocument.getAmount());
            ledgerDtls.setPaymentMode(creditDocument.getPaymode());
            ledgerDtls.setBank(null);
            ledgerDtls.setBranch(null);
            ledgerDtls.setPaymentRefNo(creditDocument.getReciptNo());
            ledgerDtls.setCreditdocid(creditDocument.getId());
            ledgerDtls.setCREATE_DATE(LocalDateTime.now());
            ledgerDtls.setIsVoid(isVoidInvoice);
            ledgerDtls.setIsDelete(isVoidInvoice);
            ledgerDtls.setTranscategory(CommonConstants.TRANS_CATEGORY_PAYMENT);
            ledgerDtls.setDescription(creditDocument.getPaydetails4());
            ledgerDtls.setCustomer(customers);
            ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
            ledgerDtlsService.save(ledgerDtls);
        }
    }

//    public String getInvoiceNo() {
//        String currinvoiceNo = null;
//        String newInvoiceNo = null;
//        try {
//            Resource resource = null;
//            LocalDate current_date = LocalDate.now();
//            int current_Year = current_date.getYear();
//
//            currinvoiceNo = creditDocRepository.getFuction();
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("CN");
//            sb.append(current_Year);
//            sb.append("-");
//            while (sb.length() < 14 - currinvoiceNo.length()) {
//                sb.append('0');
//            }
//            sb.append(currinvoiceNo);
//            newInvoiceNo = sb.toString();
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return newInvoiceNo;
//    }

//    public String getPaymentInvoiceNo(){
//        String currinvoiceNo = null;
//        String newInvoiceNo = null;
//        try {
//            Resource resource = null;
//            LocalDate current_date = LocalDate.now();
//            int current_Year = current_date.getYear();
//            try {
//                currinvoiceNo = creditDocRepository.getPaymentFuction();
//            }
//            catch (Exception e){
//                logger.error("Payment Function not found ");
//            }
//
//            StringBuilder sb = new StringBuilder();
//            sb.append("PY");
//            sb.append(current_Year);
//            sb.append("-");
//            if(currinvoiceNo != null) {
//                while (sb.length() < 14 - currinvoiceNo.length()) {
//                    sb.append('0');
//                }
//                sb.append(currinvoiceNo);
//                newInvoiceNo = sb.toString();
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return newInvoiceNo;
//    }

    public void sendDataToNAV(Optional<CreditDocument> creditDocument, DebitDocument debitDocument) {
        Map<String, Double> result = new HashMap<>();
        Map<String, Double> taxOnCreditNote = calculateTaxOnCreditNote(debitDocument, creditDocument.get());
        Double withoutTaxAmount = creditDocument.get().getAmount();
        double discount = (creditDocument.get().getAmount() * debitDocument.getDiscount()) / debitDocument.getTotalamount();
        for (Map.Entry<String, Double> entry : taxOnCreditNote.entrySet()) {
            withoutTaxAmount = withoutTaxAmount - entry.getValue();
            result.put("TAX-" + entry.getKey(), entry.getValue());
        }
        for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
            Charge charge = chargeRepository.findById(debitDocDetails.getChargeid()).orElse(null);
            if (!(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time")) && withoutTaxAmount > 0) {
                QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
                BooleanExpression booleanExpression = qCustomerChargeDBR.isNotNull().and(qCustomerChargeDBR.custid.eq(Long.valueOf(debitDocument.getCustomer().getId()))).and(qCustomerChargeDBR.invoiceId.eq(Long.valueOf(debitDocDetails.getDebitdocumentid()))).and(qCustomerChargeDBR.chargeId.eq(Long.valueOf(debitDocDetails.getChargeid()))).and(qCustomerChargeDBR.startdate.eq(LocalDate.now().minusDays(1)));
                List<CustomerChargeDBR> customerChargeDBRList = IterableUtils.toList(customerChargeDBRRepository.findAll(booleanExpression));
                if (customerChargeDBRList.size() > 0) {
                    CustomerChargeDBR customerChargeDBR = customerChargeDBRList.get(0);
                    result.put("PREPAID#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode() + "#" + (charge.getPushableLedgerId() == null || charge.getPushableLedgerId().isEmpty() ? debitDocDetails.getLedgerId() : charge.getPushableLedgerId()), withoutTaxAmount < customerChargeDBR.getPendingamt() ? customerChargeDBR.getPendingamt() - withoutTaxAmount : customerChargeDBR.getPendingamt());
                    withoutTaxAmount = withoutTaxAmount - customerChargeDBR.getPendingamt();
                } else {
                    withoutTaxAmount = withoutTaxAmount - debitDocDetails.getSubtotal();
                    result.put("PREPAID#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode() + "#" + (charge.getPushableLedgerId() == null || charge.getPushableLedgerId().isEmpty() ? debitDocDetails.getLedgerId() : charge.getPushableLedgerId()), debitDocDetails.getSubtotal());
                }
            }
        }
        if (withoutTaxAmount > 0) {
            for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                Charge charge = chargeRepository.findById(debitDocDetails.getChargeid()).orElse(null);
                if (!(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") ||
                        debitDocDetails.getChargetype().equalsIgnoreCase("One-time")) && withoutTaxAmount > 0) {
                    QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
                    BooleanExpression booleanExpression = qCustomerChargeDBR.isNotNull().and(qCustomerChargeDBR.custid.eq(Long.valueOf(debitDocument.getCustomer().getId()))).and(qCustomerChargeDBR.invoiceId.eq(Long.valueOf(debitDocDetails.getDebitdocumentid()))).and(qCustomerChargeDBR.chargeId.eq(Long.valueOf(debitDocDetails.getChargeid()))).and(qCustomerChargeDBR.startdate.eq(LocalDate.now().minusDays(1)));
                    List<CustomerChargeDBR> customerChargeDBRList = IterableUtils.toList(customerChargeDBRRepository.findAll(booleanExpression));
                    if (customerChargeDBRList.size() > 0) {
                        CustomerChargeDBR customerChargeDBR = customerChargeDBRList.get(0);
                        result.put("REVENUE#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode() + "#" + (charge.getPushableLedgerId() == null || charge.getPushableLedgerId().isEmpty() ? debitDocDetails.getLedgerId() : charge.getPushableLedgerId()), withoutTaxAmount < customerChargeDBR.getCumm_revenue() ? customerChargeDBR.getCumm_revenue() - withoutTaxAmount : customerChargeDBR.getCumm_revenue());
                        withoutTaxAmount = withoutTaxAmount - customerChargeDBR.getCumm_revenue();
                    }
                }
            }
        }
        if (withoutTaxAmount > 0) {
            for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
                Charge charge = chargeRepository.findById(debitDocDetails.getChargeid()).orElse(null);
                QCustomerChargeDBR qCustomerChargeDBR = QCustomerChargeDBR.customerChargeDBR;
                BooleanExpression booleanExpression = qCustomerChargeDBR.isNotNull().and(qCustomerChargeDBR.custid.eq(Long.valueOf(debitDocument.getCustomer().getId()))).and(qCustomerChargeDBR.invoiceId.eq(Long.valueOf(debitDocDetails.getDebitdocumentid()))).and(qCustomerChargeDBR.chargeId.eq(Long.valueOf(debitDocDetails.getChargeid()))).and(qCustomerChargeDBR.startdate.eq(LocalDate.now().minusDays(1)));
                List<CustomerChargeDBR> customerChargeDBRList = IterableUtils.toList(customerChargeDBRRepository.findAll(booleanExpression));
                if (debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time") && withoutTaxAmount > 0) {
                    if (customerChargeDBRList.size() > 0) {
                        CustomerChargeDBR customerChargeDBR = customerChargeDBRList.get(0);
                        result.put("REVENUE#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode() + "#" + (charge.getPushableLedgerId() == null || charge.getPushableLedgerId().isEmpty() ? debitDocDetails.getLedgerId() : charge.getPushableLedgerId()), withoutTaxAmount < customerChargeDBR.getCumm_revenue() ? customerChargeDBR.getCumm_revenue() - withoutTaxAmount : customerChargeDBR.getCumm_revenue());
                        withoutTaxAmount = withoutTaxAmount - customerChargeDBR.getCumm_revenue();
                    } else {
                        withoutTaxAmount = withoutTaxAmount - debitDocDetails.getSubtotal();
                        result.put("REVENUE#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode() + "#" + (charge.getPushableLedgerId() == null || charge.getPushableLedgerId().isEmpty() ? debitDocDetails.getLedgerId() : charge.getPushableLedgerId()), debitDocDetails.getSubtotal());
                    }
                }
            }
        }
////        if (withoutTaxAmount > dbr.getPendingamt()) {
//            amountToBeDeductedFromPrepaid = dbr.getPendingamt();
//            Double prepaidPerCharge = amountToBeDeductedFromPrepaid / (int) debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails ->).count();
//            for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
//                if () {
//
//                }
//            } withoutTaxAmount = withoutTaxAmount - amountToBeDeductedFromPrepaid;
//            if (recurringRevenue > withoutTaxAmount && withoutTaxAmount > 0) {
//                Double recurringRevenuePerCharge = withoutTaxAmount / (int) debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails -> !(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))).count();
//                for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
//                    if (!(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))) {
//
//                    }
//                }
//            } else {
//                withoutTaxAmount = withoutTaxAmount - recurringRevenue;
//                Double recurringRevenuePerCharge = recurringRevenue / (int) debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails -> !(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))).count();
//                for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
//                    if (!(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))) {
//                        result.put("REVENUE#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode(), recurringRevenuePerCharge);
//                    }
//                }
//                if (withoutTaxAmount > 0) {
//                    Double recurringOneTimeRevenuePerCharge = withoutTaxAmount / (int) debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails -> (debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))).count();
//
//                }
//            }
//        } else {
//            amountToBeDeductedFromPrepaid = dbr.getPendingamt();
//            Double prepaidPerCharge = amountToBeDeductedFromPrepaid / (int) debitDocument.getDebitDocDetailsList().stream().filter(debitDocDetails -> !(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))).count();
//            for (DebitDocDetails debitDocDetails : debitDocument.getDebitDocDetailsList()) {
//                if (!(debitDocDetails.getChargetype().equalsIgnoreCase("NON_RECURRING") || debitDocDetails.getChargetype().equalsIgnoreCase("One-time"))) {
//                    result.put("PREPAID#" + debitDocDetails.getChargename() + "#" + debitDocDetails.getLedgerId() + "#" + debitDocDetails.getIcCode(), prepaidPerCharge);
//                }
//            }
//        }
        if (discount > 0) {
            result.put("DISCOUNT#DISCOUNT#9215200#" + CommonConstants.COMMON_DATA_FOR_NAV.ICCODE + "#9215200", discount);
        }
        CreditNoteMessageIntegrationSystem creditNoteMessageIntegrationSystem = new CreditNoteMessageIntegrationSystem(result, creditDocument.get().getCreditdocumentno(), creditDocument.get().getId(), creditDocument.get().getAmount(), creditDocument.get().getCustomer().getId());
        //messageSender.send(creditNoteMessageIntegrationSystem, RabbitMqConstants.QUEUE_INTEGRATION_SYSTEM_CREDIT_NOTE_GEN);

    }

    Map<String, Double> calculateTaxOnCreditNote(DebitDocument debitDocument, CreditDocument creditDocument) {
        DebitDocumentTAXRelService debitDocumentTAXRelService = SpringContext.getBean(DebitDocumentTAXRelService.class);
        Map<String, Double> taxOnCreditNote = new HashMap<>();
        List<DebitDocumentTAXRel> debitDocumentTAXRels = debitDocumentTAXRelService.getTotalTaxByType(debitDocument.getId());
        for (DebitDocumentTAXRel debitDocumentTAXRel : debitDocumentTAXRels) {
            Double tax = (creditDocument.getAmount() * debitDocumentTAXRel.getAmount()) / debitDocument.getTotalamount();
            taxOnCreditNote.put(debitDocumentTAXRel.getTaxname().concat("#" + debitDocumentTAXRel.getTaxname()).concat("#" + debitDocumentTAXRel.getTaxLedgerId() + "#").concat(CommonConstants.COMMON_DATA_FOR_NAV.ICCODE).concat("#" + debitDocumentTAXRel.getTaxLedgerId()), tax);
        }
        return taxOnCreditNote;
    }


    /* method for send notification payment verification*/
    public void sendCustPaymentVerificationMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId, String reciptNo, Double paymentAmount, String paymentDate,Long buId,Long staffId,String custtype) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_PAYMENT_VERIFICATION_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustPaymentVerificationMsg custPaymentVerificationMsg = new CustPaymentVerificationMsg(username, mobileNumber, emailId, status, mvnoId, reciptNo, paymentAmount, paymentDate, RabbitMqConstants.CUSTOMER_PAYMENT_VERIFICATION_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId,custtype);
                    Gson gson = new Gson();
                    gson.toJson(custPaymentVerificationMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custPaymentVerificationMsg,CustPaymentVerificationMsg.class.getSimpleName()));
//                    messageSender.send(custPaymentVerificationMsg, RabbitMqConstants.QUEUE_CUSTOMER_PAYMENT_VERIFICATION_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Payment Verification is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * @param custServiceIds
     * @return refundable total amount
     * @author Yogesh Patil
     */

    public Double getRefundableAmountByService(List<Long> custServiceIds) {
        Double amount = 0d;
        if (!CollectionUtils.isEmpty(custServiceIds)) {
            try {
                List<CustomerServiceMapping> customerServiceMappings = customerServiceMappingRepository.findAllByIdIn(custServiceIds.stream().mapToInt(Long::intValue).boxed().collect(Collectors.toList()));
                if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                    for (CustomerServiceMapping customerServiceMapping : customerServiceMappings) {
                        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustServiceMappingIdIn(Collections.singletonList(customerServiceMapping.getId()));
                        List<PlanGroup> planGroups = custPlanMapppingList.stream().map(CustPlanMappping::getPlanGroup).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(planGroups)) {
                            List<CustPlanMappping> custPlanMappPgList = custPlanMappingRepository.findAllByPlanGroupIn(planGroups);
                            if (!CollectionUtils.isEmpty(custPlanMappPgList)) {
                                custPlanMapppingList.addAll(custPlanMappPgList);
                                custPlanMappPgList = custPlanMappPgList.stream().filter(UtilsCommon.distinctByKey(CustPlanMappping::getId)).collect(Collectors.toList());
                            }
                        }
//                        custPlanMapppingList.removeIf(custPlanMappping -> custPlanMappping.getIsHold() || (custPlanMappping.getIsVoid() != null && custPlanMappping.getIsVoid()));//.stream().filter(custPlanMappping -> custPlanMappping.getIsHold() || custPlanMappping.getIsVoid()).collect(Collectors.toList());
                        List<Long> debitDocIds = custPlanMapppingList.stream().map(CustPlanMappping::getDebitdocid).collect(Collectors.toList());
                        if (!CollectionUtils.isEmpty(debitDocIds)) {
                            List<Integer> debList = debitDocIds.stream().mapToInt(Long::intValue).boxed().collect(Collectors.toList());
                            List<DebitDocument> debitDocuments = debitDocRepository.findAllByIdIn(debList);
                            debitDocuments = debitDocuments.stream().filter(debitDocument -> !debitDocument.getBillrunstatus().equalsIgnoreCase(StatusConstants.INVOICE_STATUS.VOID)).collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(debitDocuments)) {
                                for (DebitDocument debitDocument : debitDocuments) {
                                    String remarks = "Refund paid amount against stop service for invoice" + debitDocument.getDocnumber();
                                    CreditDocument creditDocument = creatCreditNotAsPerService(debitDocument, Collections.singletonList(customerServiceMapping), remarks, Boolean.TRUE, null);
                                    if(creditDocument != null) {
                                        amount=amount+creditDocument.getAmount();
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Exception to get refundable Amount: " + ex.getMessage());
            }
        }
        return amount;
    }

    @Transactional
    public CreditDocument creatCreditNotAsPerService(DebitDocument debitDocument, List<CustomerServiceMapping> customerServiceMappings, String remarks, Boolean forViewOnly, List<Long> custInvMappingIds) {
        try {
            Double cnAmount = 0d;
            Double remainingAmount = debitDocument.getTotalamount();
            if (debitDocument.getAdjustedAmount() != null) {
                remainingAmount = remainingAmount - debitDocument.getAdjustedAmount();
            }
            LocalDate currentDate = LocalDate.now();
            List<Integer> cprIds = new ArrayList<>();
            DecimalFormat df = new DecimalFormat("#.00");
            List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();
            if (!CollectionUtils.isEmpty(customerServiceMappings)) {
                List<Integer> custServiceIds = customerServiceMappings.stream().map(CustomerServiceMapping::getId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(custServiceIds)) {
                    //CN as per service
                    cprIds = custPlanMappingRepository.getAllByCustServiceMappingIdIn(custServiceIds);
                    if (!CollectionUtils.isEmpty(cprIds)) {
                        for(Integer cprId: cprIds) {
                            List<CustomerChargeDBR> customerChargeDBR = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(currentDate, debitDocument, Collections.singletonList(Long.valueOf(cprId)));
                            if(!CollectionUtils.isEmpty(customerChargeDBR)) {
                                customerChargeDBRList.addAll(customerChargeDBR);
                            }
                        }
                        if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                            cnAmount = customerChargeDBRList.stream().mapToDouble(CustomerChargeDBR::getDbr).sum();
                        } else {
                            List<CustomerDBR> customerDBRS = dbrService.getCustomerDBRListBetweenStartDateAndEndDateAndByService(currentDate, debitDocument, cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
                            if (!CollectionUtils.isEmpty(customerChargeDBRList))
                                cnAmount = customerDBRS.stream().mapToDouble(CustomerDBR::getDbr).sum();
                        }
                    }
                } else {
                    //CN as per invoice
                    customerChargeDBRList = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDate(currentDate, debitDocument);
                    if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        LocalDate finalCurrentDate = currentDate;
                        cnAmount = Double.parseDouble(df.format(customerChargeDBRList.stream().filter(x -> x.getStartdate().equals(finalCurrentDate)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                        cnAmount = Double.parseDouble(df.format(cnAmount));
                    } else {
                        List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(currentDate, debitDocument);
                        if (!CollectionUtils.isEmpty(customerDBRList)) {
                            LocalDate finalCurrentDate1 = currentDate;
                            cnAmount = Double.parseDouble(df.format(customerChargeDBRList.stream().filter(x -> x.getStartdate().equals(finalCurrentDate1)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                            cnAmount = Double.parseDouble(df.format(cnAmount));
                        }
                    }
                }
            }
            if(!CollectionUtils.isEmpty(custInvMappingIds)) {
                List<CustomerChargeDBR> invChargeDbr = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDateAndcustInvMappId(LocalDate.now(), debitDocument, custInvMappingIds);
                if(!CollectionUtils.isEmpty(invChargeDbr)) {
                    LocalDate finalCurrentDate1 = currentDate;
                    Double invcnAmount = Double.parseDouble(df.format(customerChargeDBRList.stream().filter(x -> x.getStartdate().equals(finalCurrentDate1)).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
                    cnAmount = cnAmount + invcnAmount;
                    customerChargeDBRList.addAll(invChargeDbr);
                }
            }
            if (cnAmount != 0) {
                Double invoiceWithoutTax = debitDocument.getTotalamount() - debitDocument.getTax() + debitDocument.getDiscount();
                Double newDiscount = cnAmount * (debitDocument.getDiscount() / invoiceWithoutTax);

                Double percentage = (debitDocument.getTax() * 100.0d) / (debitDocument.getTotalamount() - debitDocument.getTax());
                Double prorateTaxAmount = ((cnAmount - newDiscount) * percentage) / 100.0d;
                cnAmount = cnAmount - newDiscount + prorateTaxAmount;

                CreditDocument creditDocument = new CreditDocument();
                creditDocument.setAmount(cnAmount);
                //Adjust fraction less than 0.1
                if (remainingAmount - cnAmount < 0.1 && remainingAmount != 0) {
                    creditDocument.setAmount(remainingAmount);
                }
                creditDocument.setInvoiceId(debitDocument.getId());
                creditDocument.setTdsamount(0d);
                creditDocument.setAbbsAmount(0d);
                creditDocument.setCustomer(debitDocument.getCustomer());
                creditDocument.setPaymode(CommonConstants.PAYMENT_MODE.CREDIT_NOTE);
                creditDocument.setPaymentdate(LocalDate.now());
                creditDocument.setPaytype("creditnote");
                creditDocument.setType(CommonConstants.TRANS_CREDIT_NOTE);
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.setIsDelete(false);
                creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
                creditDocument.setBuID(debitDocument.getCustomer().getBuId());
                creditDocument.setRemarks(remarks + "\n Payment adjusted :-" + cnAmount);
                creditDocument.setLcoid(debitDocument.getLcoId());
//                creditDocument.setCreditdocumentno(getInvoiceNo());
                Boolean isLCO = debitDocument.getCustomer().getLcoId() != null ? true :false;
                creditDocument.setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, debitDocument.getCustomer().getLcoId(), debitDocument.getCustomer().getMvnoId()));

                creditDocument.setCreatedById(debitDocument.getCreatedById());
                creditDocument.setCreatedByName(debitDocument.getCreatedByName());
//                creditDocument.setXmldocument(assemblePaymentXML(creditDocument, CommonUtils.ADDR_TYPE_PRESENT));
                //AdjustmentAmount
                if (!forViewOnly) {
                    creditDocument = creditDocRepository.save(creditDocument);
                    adjustCNAmountAgainstDebitDoc(debitDocument, creditDocument);
                    addLedgeAfterApproval(creditDocument);
                    Double creditAmountExcludeTax = dbrService.getCreditNotePriceExcludingTax(debitDocument, creditDocument.getAmount());
                    if (!CollectionUtils.isEmpty(cprIds)) {
                        dbrService.removeDbrByCPRListAndInvoiceIdStartDateAtChargeLevel(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());
                        dbrService.removedbrByCPRListAndInvoiceIdStartDate(cprIds.stream().map(Integer::longValue).collect(Collectors.toList()), Long.valueOf(debitDocument.getId()), currentDate, debitDocument.getEndate().toLocalDate());
                    }
                    dbrService.addDbrEntry(debitDocument, debitDocument.getId().longValue(), creditAmountExcludeTax);
                    dbrService.revertPartnerLedgerAndDetail(debitDocument,creditDocument.getAmount());
                    // Add tax, discount and charge in table
                    if(!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        setCreditNotdataToTable(customerChargeDBRList, creditDocument, debitDocument);
                    }
                    try {
                        sendDataToNAV(Optional.of(creditDocument), debitDocument);
                    } catch (Exception ex) {
                        logger.error("Error in integration" + ex.getStackTrace());
                    }
                }
                return creditDocument;
            } else {
                logger.error("CN Amount get 0 so CN not created! for invoice number: " + debitDocument.getDocnumber());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Error while creating CN for debitDoc: " + debitDocument.getDocnumber() + " exception: " + ex.getMessage());
        }
        return null;
    }

    /**
     * @param debitDocument
     * @param creditDocument
     * @return
     * @author Yogesh Patil
     */
    @Transactional
    public CreditDocument adjustCNAmountAgainstDebitDoc(DebitDocument debitDocument, CreditDocument creditDocument) {
        Double debitDocRemainingAmount = debitDocument.getTotalamount();
        Double cnRemainingAmount = creditDocument.getAmount();
        boolean isFirstCN = false;
        //DebitDoc adjusted amount
        if (debitDocument.getAdjustedAmount() != null) {
            debitDocRemainingAmount = debitDocRemainingAmount - debitDocument.getAdjustedAmount();
        } else {
            debitDocument.setAdjustedAmount(0d);
            isFirstCN = true;
        }
        //CN adjusted amount
        if (creditDocument.getAdjustedAmount() != null) {
            cnRemainingAmount = cnRemainingAmount - creditDocument.getAdjustedAmount();
        } else {
            creditDocument.setAdjustedAmount(0d);
        }

        CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
        creditDebitDocMapping.setDebtDocId(debitDocument.getId());
        creditDebitDocMapping.setCreditDocId(creditDocument.getId());
        creditDebitDocMapping.setIsDeleted(Boolean.FALSE);

        //CN is partialy
        if (debitDocRemainingAmount > cnRemainingAmount) {
            //Full CN adjusted
            debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + cnRemainingAmount);
            creditDebitDocMapping.setAdjustedAmount(cnRemainingAmount);
            creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + cnRemainingAmount);
        }//CN is more than invoice amount
        else if (debitDocRemainingAmount < cnRemainingAmount) {
            //Partial CN adjusted
            debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
            creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
            creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
        }//CN amount is equal to invoice amount
        else {
            //Full CN adjusted
            creditDebitDocMapping.setAdjustedAmount(debitDocRemainingAmount);
            creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + debitDocRemainingAmount);
            debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + debitDocRemainingAmount);
        }
        //status
        if (creditDocument.getAmount().equals(creditDocument.getAdjustedAmount()))
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
        else
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

        if (debitDocument.getAdjustedAmount() != null) {
            if (debitDocRemainingAmount <= creditDocument.getAmount() || debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount())) {
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
            } else if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount() && !isFirstCN)
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
            if (debitDocument.getAdjustedAmount() == 0d)
                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
        } else {
            debitDocument.setAdjustedAmount(0.0);
            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
        }
//        if (debitDocument.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.CLEAR) && creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.ADJUSTED)) {
//            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PAYABLE);
//        }
        creditDebtMappingRepository.save(creditDebitDocMapping);
        debitDocRepository.save(debitDocument);
        return creditDocRepository.save(creditDocument);
    }

    @Transactional
    public void setCreditNotdataToTable(List<CustomerChargeDBR> customerChargeDBRS, CreditDocument document, DebitDocument debitDocument) {
        try {
            Set<Long> chargeIds = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId() != null).map(CustomerChargeDBR::getChargeId).collect(Collectors.toSet());
            List<CreditDocChargeRel> creditDocChargeRels = new ArrayList<>();
            Double discountInPer = 0d;
            if(debitDocument.getCustpackrelid() != null) {
                discountInPer = custPlanMappingRepository.findDiscountById(debitDocument.getCustpackrelid());
            }
            for(Long chargeId: chargeIds) {
                Double totalChargeAmount = customerChargeDBRS.stream().filter(customerChargeDBR -> customerChargeDBR.getChargeId().equals(chargeId)).mapToDouble(CustomerChargeDBR::getDbr).sum();
                Double discountAmount = getAmountFromPer(totalChargeAmount, discountInPer);
                //totalAmount after discount
                totalChargeAmount = totalChargeAmount - discountAmount;
                Charge charge = chargeRepository.findById(chargeId.intValue()).get();
                Double taxAmount = taxService.getTaxAmountFromChargeAndPrice(charge, totalChargeAmount);
                Double totalAmountForCharge = totalChargeAmount + taxAmount;

                CreditDocChargeRel creditDocChargeRel = calculateCNChargeRelData(debitDocument, totalAmountForCharge, document, discountInPer, charge);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception while adding CN charge rel amount");
        }
    }

    public CreditDocChargeRel calculateCNChargeRelData(DebitDocument debitDocument, Double chargeAdjustedAmount, CreditDocument document, Double disPer, Charge charge) {
        CreditDocChargeRel creditDocChargeRel = new CreditDocChargeRel();
        try {

            Double invoiceAmount = debitDocument.getTotalamount();
            Double cnPerFromInvoice = (document.getAmount() * 100)/invoiceAmount;
            Double factor = (chargeAdjustedAmount * 100)/invoiceAmount;
            //charge amount
            Double chargeFact1 = (invoiceAmount * (factor/100));
            Double chargeFact2 = (cnPerFromInvoice/100);
            Double flatamount = chargeFact1 * chargeFact2;
            Double totalTax = 0.0;
            //Tax calculation
            List<CreditDocTaxRel> creditDocTaxRelList = calculatechargeTaxDetails(document, charge, flatamount, creditDocChargeRel);
            if(!CollectionUtils.isEmpty(creditDocTaxRelList)) {
                totalTax = creditDocTaxRelList.stream().mapToDouble(CreditDocTaxRel::getTaxAmount).sum();
//                creditDocChargeRel.setCreditDocTaxRel(creditDocTaxRelList);
            }
            //final amount
            Double taxTotalAmount = flatamount - totalTax;
            //Discount
            Double discount = (taxTotalAmount*disPer)/100;

            //save data to entity
            creditDocChargeRel.setChargeAmount(taxTotalAmount);
            creditDocChargeRel.setDiscount(discount);
            creditDocChargeRel.setTaxAmount(totalTax);
            creditDocChargeRel.setTotalAmount(flatamount);
            creditDocChargeRel.setCreditDocument(document);
            creditDocChargeRel.setCharge(charge);
            creditDocChargeRel.setDebitDocId(debitDocument.getId());
            creditDocChargeRel = creditDocChargeRelRepository.save(creditDocChargeRel);
            CreditDocChargeRel finalCreditDocChargeRel = creditDocChargeRel;
            creditDocTaxRelList.forEach(creditDocTaxRel -> {
                creditDocTaxRel.setCreditDocChargeRel(finalCreditDocChargeRel);
            });
            creditDocTaxRepository.saveAll(creditDocTaxRelList);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("Exception when save CN detail: "+ex.getMessage());
        }
        return creditDocChargeRel;
    }

    public List<CreditDocTaxRel> calculatechargeTaxDetails(CreditDocument creditDocument, Charge charge, Double chargeFlatamount, CreditDocChargeRel creditDocChargeRel) {
        Double totalTaxAmount = 0.0;
        List<CreditDocTaxRel> creditDocTaxRelList = new ArrayList<>();

        Optional<Tax>  primaryTax = taxRepository.findById(charge.getTax().getId());
        if(primaryTax.isPresent())
        {
            Tax tierTax=primaryTax.get();
            List<TaxTypeTier> taxTypeTiers=  tierTax.getTieredList();
            for (TaxTypeTier taxTypeTier: taxTypeTiers) {
                Double chargeWithPerAmt = (100 + taxTypeTier.getRate())/100;
                Double chargeWithFlatAmt = chargeFlatamount/chargeWithPerAmt;
                Double totalTax = chargeFlatamount - chargeWithFlatAmt;
                CreditDocTaxRel creditDocTaxRel = new CreditDocTaxRel(charge, creditDocument, totalTax, creditDocChargeRel);
                creditDocTaxRelList.add(creditDocTaxRel);
            }
        }
        return creditDocTaxRelList;
    }


    public double getChargeAmount(Double invoiceAmt, Double factCharge, Double CNAmount) {
        double amount = (invoiceAmt * (factCharge / 100));
        return amount * (CNAmount / 100);
    }

    public double findPerForTwoAmount(Double lowAmount, Double higherAmount) {
        return (lowAmount * 100) / higherAmount;
    }

    public Double getAmountFromPer(Double amount, Double disPer) {
        return (amount * disPer) / 100;
    }

    public void changeStatusDisableToActive(Optional<CreditDocument> creditDocument, List<CreditDebitDocMapping> creditDebitDocMappings) {
        List<DebitDocument> debitDocumentList = debitDocRepository.findAllByIdIn(creditDebitDocMappings.stream().filter(creditDebitDocMapping -> creditDebitDocMapping.getCreditDocId().equals(creditDocument.get().getId())).map(creditDebitDocMapping -> creditDebitDocMapping.getDebtDocId()).collect(Collectors.toList()));
        if (!debitDocumentList.isEmpty()) {
            List<Integer> cprids = custPlanMappingRepository.getAllByCustPlanMappingIdInDebitDocIds(debitDocumentList.stream().map(debitDocument -> debitDocument.getId()).map(integer -> integer.longValue()).collect(Collectors.toList()));
            if (!cprids.isEmpty()) {
                List<Integer> finalcprids = new ArrayList<>();
                for (Integer cprid : cprids) {
                    CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(cprid);
                    if (custPlanMappping.getCustPlanStatus().equals(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE)) {
                        finalcprids.add(custPlanMappping.getId());
                    }
                }
                if (!finalcprids.isEmpty()) {
                    List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(finalcprids);
                    if (!CollectionUtils.isEmpty(serviceMappingIds)) {
                        String remark = "Payment Done";
                        custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE, remark, false);
                    }
                }
            }
//             List<Integer> inventoryServiceMappingIds = customerInventoryMappingService.getServiceInventoryMapping(debitDocumentList.get(0).getCustomer().getId());
//             if(!CollectionUtils.isEmpty(inventoryServiceMappingIds)){
//                 String remark = "Payment Done";
//                 custPlanMappingService.changeStatusOfCustServices(inventoryServiceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE, remark, false);
//
//            }
        }
    }

    public void reversalPaymentForOrg(Double creditNoteAmount, DebitDocument document,Boolean flag,Integer mvnoId) {
        if (document != null) {
            if(document!=null && document.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.APPROVED))
            {
                CreditDocument creditDocument = new CreditDocument();
                creditDocument.setAdjustedAmount(-creditNoteAmount);
                creditDocument.setAmount(-creditNoteAmount);
                creditDocument.setCustomer(document.getCustomer());
                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                creditDocument.setLcoid(null);
                creditDocument.setPaymentdate(LocalDate.now());
                creditDocument.setType(CommonConstants.TRANS_CATEGORY_PAYMENT);
                creditDocument.setCreatedate(LocalDateTime.now());
                creditDocument.setIsDelete(false);
                creditDocument.setTdsflag(false);
//                creditDocument.setCreditdocumentno(getPaymentInvoiceNo());
                Boolean isLCO = document.getLcoId() != null ? true :false;
                creditDocument.setCreditdocumentno(numberSequenceUtil.getPaymentNumber(isLCO, document.getLcoId(), document.getCustomer().getMvnoId()));
                creditDocument.setPaytype("invoice");
                creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
                creditDocument.setPaymode(CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION);
                creditDocument.setTds_received(false);
                if(getLoggedInUser() != null) {
                    creditDocument.setLastModifiedById(getLoggedInUser().getStaffId());
                    creditDocument.setLastModifiedByName(getLoggedInUser().getFullName());
                    creditDocument.setCreatedById(getLoggedInUser().getStaffId());
                    creditDocument.setCreatedByName(getLoggedInUser().getFullName());
                    creditDocument.setMvnoId(getLoggedInUser().getMvnoId());
                }
                creditDocument.setBuID(document.getCustomer().getBuId());
                creditDocument.setXmldocument(assemblePaymentXML(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT,mvnoId));
                creditDocument = creditDocRepository.save(creditDocument);

                CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
                creditDebitDocMapping.setAdjustedAmount(-creditNoteAmount);
                creditDebitDocMapping.setIsDeleted(false);
                creditDebitDocMapping.setDebtDocId(document.getId());
                creditDebitDocMapping.setCreditDocId(creditDocument.getId());
                creditDebitDocMapping = creditDebtMappingRepository.save(creditDebitDocMapping);

                if(document.getAdjustedAmount()!=null)
                    document.setAdjustedAmount(document.getAdjustedAmount()-creditNoteAmount);
                else
                    document.setAdjustedAmount(-creditNoteAmount);
                document.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                debitDocRepository.save(document);

                addLedgerAndLedgerDetailEntryForOrg(creditDocument,creditNoteAmount,document.getCustomer(),document);
            }


            List<CreditDebitDocMapping> creditDebitDocMappings=creditDebtMappingRepository.findBydebtDocId(document.getId());
            Boolean isFullCreditNote=false;
            if(!CollectionUtils.isEmpty(creditDebitDocMappings))
            {
                List<CreditDocument> creditDocuments=creditDocRepository.findAllByIdIn(creditDebitDocMappings.stream().map(x->x.getCreditDocId()).collect(Collectors.toList()));
                creditDocuments=creditDocuments.stream().filter(x->x.getType().equalsIgnoreCase(CommonConstants.TRANS_CREDIT_NOTE)).collect(Collectors.toList());
                List<CreditDocument> finalCreditDocuments = creditDocuments;
                creditDebitDocMappings=creditDebitDocMappings.stream().filter(x-> finalCreditDocuments.stream().map(y->y.getId()).collect(Collectors.toList()).contains(x.getCreditDocId())).collect(Collectors.toList());
                Double amount=creditDebitDocMappings.stream().filter(x->x.getAdjustedAmount()!=null).mapToDouble(x->(x.getAdjustedAmount())).sum();
                if(amount!=null && amount.doubleValue()==document.getTotalamount().doubleValue())
                    isFullCreditNote=true;
            }
            if(isFullCreditNote)
            {
                if(flag)
                {
                    List<CustPlanMappping> mappping = custPlanMappingRepository.findAllByDebitdocid(document.getId());
                    if(mappping!=null && !mappping.isEmpty())
                    {
                        mappping.stream().forEach(record->{
                            if(record.getCustomerCpr()!=null)
                            {
                                CustPlanMappping custPlan = custPlanMappingRepository.findById(record.getCustomerCpr());
                                custPlan.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                custPlan.setStartDate(LocalDateTime.now().minusMinutes(1));
                                custPlan.setEndDate(LocalDateTime.now());
                                custPlan.setExpiryDate(LocalDateTime.now());
                                custPlanMappingRepository.save(custPlan);
                            }
                        });
                    }
                }
                document.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                document.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                debitDocRepository.save(document);
            }
        }
    }


    public void addLedgerAndLedgerDetailEntryForOrg(CreditDocument creditDocument, Double creditNoteAmount, Customers customers,DebitDocument document) {

        CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(creditNoteAmount);
        ledgerDtls.setPaymentMode(CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION);
        ledgerDtls.setBank(null);
        ledgerDtls.setBranch(null);
        //ledgerDtls.setCreditdocid(creditDocument.getId());
        ledgerDtls.setDebitdocid(document.getId());
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsVoid(false);
        ledgerDtls.setIsDelete(false);
        ledgerDtls.setTranscategory(CommonConstants.TRANS_REVERSAL_BUSINESS_PROMOTION);
        ledgerDtls.setDescription(creditDocument.getPaydetails4());
        ledgerDtls.setCustomer(customers);
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
        ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
        ledgerDtls.setDescription("Reversal Payment for Business Promotion Invoice.");
        ledgerDtlsService.save(ledgerDtls);
    }

    public void addLedgerAndLedgerDetailEntryForCreditNoteOrg(CreditDocument creditDocument, Double creditNoteAmount, Customers customers,DebitDocument document) {

        CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
        ledgerDtls.setAmount(creditNoteAmount);
        ledgerDtls.setPaymentMode("Credit Note");
        ledgerDtls.setBank(null);
        ledgerDtls.setBranch(null);
        //ledgerDtls.setDebitdocid(document.getId());
        ledgerDtls.setCreditdocid(creditDocument.getId());
        ledgerDtls.setCREATE_DATE(LocalDateTime.now());
        ledgerDtls.setIsVoid(false);
        ledgerDtls.setIsDelete(false);
        ledgerDtls.setTranscategory(CommonConstants.TRANS_CREDIT_NOTE);
        ledgerDtls.setDescription(creditDocument.getPaydetails4());
        ledgerDtls.setCustomer(customers);
        ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
        ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
        ledgerDtls.setDescription("CreditNote for Business Promotion Invoice.");
        ledgerDtlsService.save(ledgerDtls);
    }

    public void adjustCreditNoteForBillToSubisu(Double newAdjustAmount, DebitDocument debitDocument,Integer mvnoId) {
        if(debitDocument!=null && (debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING) || debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.APPROVED)))
        {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(newAdjustAmount);
            creditDocument.setAmount(newAdjustAmount);
            creditDocument.setCustomer(debitDocument.getCustomer());
            creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
            creditDocument.setLcoid(debitDocument.getCustomer().getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType("creditnote");
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
//            creditDocument.setCreditdocumentno(getInvoiceNo());
            Boolean isLCO = debitDocument.getCustomer().getLcoId() != null ? true :false;
            creditDocument.setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, debitDocument.getCustomer().getLcoId(), debitDocument.getCustomer().getMvnoId()));
            creditDocument.setPaydetails4(null);
            creditDocument.setPaytype("creditnote");
            creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
            creditDocument.setPaymode(CommonConstants.TRANS_CREDIT_NOTE1);
            creditDocument.setTds_received(false);
            creditDocument.setCreatedById(debitDocument.getCreatedById());
            creditDocument.setCreatedByName(debitDocument.getCreatedByName());
            creditDocument.setLastModifiedById(debitDocument.getLastModifiedById());
            creditDocument.setLastModifiedByName(debitDocument.getLastModifiedByName());
            creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT,null,debitDocument));
            creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
            creditDocument.setBuID(debitDocument.getCustomer().getBuId());
            creditDocument=creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setAdjustedAmount(newAdjustAmount);
            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping=creditDebtMappingRepository.save(creditDebitDocMapping);

            if(!debitDocument.getStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING))
                reversalPaymentForOrg(newAdjustAmount,debitDocument,false,mvnoId);

            addLedgerAndLedgerDetailEntryForCreditNoteOrg(creditDocument,creditDocument.getAmount(),debitDocument.getCustomer(),debitDocument);

            if(debitDocument.getAdjustedAmount()!=null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+newAdjustAmount);
            else
                debitDocument.setAdjustedAmount(newAdjustAmount);
            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
            debitDocument=debitDocRepository.save(debitDocument);
        }
    }

    
    public void ifCreditNoteIsAllowed(RecordPaymentPojo pojo){
        DebitDocument debitDocument = debitDocRepository.findById(pojo.getInvoiceId().get(0)).get();
        if(Objects.nonNull(debitDocument)){
            if(debitDocument.getStartdate().isBefore(LocalDateTime.now()) || debitDocument.getStartdate().isEqual(LocalDateTime.now())){
                if(Objects.isNull(debitDocument.getAdjustedAmount())){
                    debitDocument.setAdjustedAmount(0.0);
                }
                List<Integer> cprIds = custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId()).stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
                Double cnAmount = 0.0;
                if(!cprIds.isEmpty()){
                    List<Integer> custServiceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIds(cprIds);
                    List<CustomerChargeDBR> customerChargeDBRList = new ArrayList<>();
                    customerChargeDBRList = dbrService.getCustomerChargeDBRListBetweenStartDateAndEndDateAndByService(LocalDateTime.now().toLocalDate(), debitDocument, cprIds.stream().mapToLong(Integer::longValue).boxed().collect(Collectors.toList()));
                    if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
                        cnAmount = customerChargeDBRList.stream().mapToDouble(CustomerChargeDBR::getDbr).sum();
                    }

                }
                if(debitDocument.getCustomer().getId()!=1 && debitDocument.getCustomer().getId()!=2)
                {
                    if(cnAmount - pojo.getAmount() <= 0.1){
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(),"Please cancel Future Plan Invoice First.", null);
                    }
                }
            }
        }
    }

    public void updateCreditDocument(List<CreditDocUpdateDTO> creditDocUpdateDTOList){
        for(CreditDocUpdateDTO creditDocUpdateDTO : creditDocUpdateDTOList){
            CreditDocument creditDocument = creditDocRepository.findById(creditDocUpdateDTO.getCreditDocId()).get();
            creditDocument.setAmount(creditDocUpdateDTO.getAmount());
            creditDocRepository.save(creditDocument);

        }
    }


    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CreditDocument', '1')")
    public Page<CreditDocumentSearchPojo> getCreditDocuments(SearchPaymentPojo search, PaginationRequestDTO requestDTO,Integer mvnoId) {
        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
        QCustomers qCustomers = QCustomers.customers;
        QStaffUser qStaffUser = QStaffUser.staffUser;
        QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
        QMvno qMvno = QMvno.mvno;
        BooleanExpression exp = qCreditDocument.isNotNull();
        List<WorkflowAssignStaffMapping> workflowAssignStaffMapping=
                workflowAssignStaffMappingRepo.findByEventNameAndStaffId("payment",getLoggedInUserId());
        List<Integer> idList = workflowAssignStaffMapping.stream()
                .map(WorkflowAssignStaffMapping::getEntityId)
                .collect(Collectors.toList());

        if (search.getType() != null && !"null".equals(search.getType()) && !"".equals(search.getType())) {
            exp = exp.and(qCreditDocument.type.startsWithIgnoreCase(search.getType()));
            if (search.getType().equalsIgnoreCase("payment")) {
                exp = exp.or(qCreditDocument.paytype.startsWithIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL));
            }
        }


        if (search.getReferenceno() != null && !"null".equals(search.getReferenceno()) && !"".equals(search.getReferenceno())) {
            exp = exp.and(qCreditDocument.referenceno.contains(search.getReferenceno()));
        }
        if (search.getCreditDocumentNumber() != null && !"null".equals(search.getCreditDocumentNumber()) && !"".equals(search.getCreditDocumentNumber())) {
            exp = exp.and(qCreditDocument.creditdocumentno.equalsIgnoreCase(search.getCreditDocumentNumber()));
        }
        if (!StringUtils.isEmpty(search.getPaymode()) && !"-1".equalsIgnoreCase(search.getPaymode())) {
            exp = exp.and(qCreditDocument.paymode.eq(search.getPaymode()));
        }

        if (!StringUtils.isEmpty(search.getPaystatus()) && !"null".equals(search.getReferenceno()) && !"-1".equalsIgnoreCase(search.getPaystatus())) {
                exp = exp.and(qCreditDocument.status.equalsIgnoreCase(search.getPaystatus()));
        }
        if (search.getCustomerid() != null) {
            exp = exp.and(qCreditDocument.customer.id.eq(search.getCustomerid()));
        }
        if (search.getStaffId() != null && !StringUtils.isEmpty(search.getStaffId())){
            exp = exp.and(qCreditDocument.createdById.eq(search.getStaffId()));
        }
        if (!StringUtils.isEmpty(search.getApproveId())) {
            exp = exp.and(qCreditDocument.approverid.eq(search.getApproveId()));
        }

        if (!StringUtils.isEmpty(search.getBranchname())) {

            Branch branch=branchRepository.findByNameEqualsIgnoreCaseAndIsDeletedIsFalse(search.getBranchname());
            //exp = exp.and(qCreditDocument.branchname.equalsIgnoreCase(search.getBranchname()));
            exp = exp.and(qCreditDocument.customer.branch.eq(branch.getId()));
        }

        if (!StringUtils.isEmpty(search.getBuID()) && search.getBuID().size() > 0) {
            exp = exp.and(qCreditDocument.customer.buId.in(search.getBuID()));
        }
//        if(!idList.isEmpty()){
//            exp=exp.or(qCreditDocument.id.in(idList));
//        }

        try {
            if (search.getUserName() != null) {
                exp = exp.and(qCreditDocument.customer.eq(customerService.getByUserName(String.valueOf(search.getUserName()))));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (search.getRecordfromdate() != null && search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.between(search.getRecordfromdate().atStartOfDay(), search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordtodate() != null) {
            exp = exp.and(qCreditDocument.createdate.before(search.getRecordtodate().plusDays(1).atStartOfDay().minusSeconds(1)));
        } else if (search.getRecordfromdate() != null) {
            exp = exp.and(qCreditDocument.createdate.after(search.getRecordfromdate().atStartOfDay()));
        }
        if (getLoggedInUser().getLco()) exp = exp.and(qCreditDocument.lcoid.eq(getLoggedInUser().getPartnerId()));
        else exp = exp.and(qCreditDocument.lcoid.isNull());

        if (search.getPayfromdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.after(search.getPayfromdate().minusDays(1)));
        }
        if (search.getPaytodate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.before(search.getPaytodate().plusDays(1)));
        }
        if (search.getPaymentdate() != null) {
            exp = exp.and(qCreditDocument.paymentdate.eq(search.getPaymentdate()));//adi
        }
        if (search.getChequedate() != null) {
            exp = exp.and(qCreditDocument.chequedate.eq(search.getChequedate()));
        }
        if (search.getPartnerid() != null) {
            exp = exp.and(qCreditDocument.customer.partner.id.eq(search.getPartnerid()));
        }
        if (search.getMobileNumber() != null) {
            exp = exp.and(qCreditDocument.customer.mobile.eq(search.getMobileNumber()));
        }
        if (search.getInvoiceNumber() != null && !search.getInvoiceNumber().isEmpty()) {
            if (getLoggedInUser().getMvnoId() != 1) {
//                BooleanExpression invoiceExp = qDebitDocument.isNotNull().and(qDebitDocument.docnumber.eq(search.getInvoiceNumber())).and(qDebitDocument.customer.mvnoId.eq(getLoggedInMvnoId()));
//                List<DebitDocument> debitDocument = IterableUtils.toList(debitDocRepository.findAll(invoiceExp));
                Integer dbtDocId = debitDocRepository.findDebitDocumentsIdByDocumentNoAndMvnoId(search.getInvoiceNumber(), getLoggedInUser().getMvnoId());
                if (dbtDocId != null)
                    exp = exp.and(qCreditDocument.invoiceId.eq(dbtDocId));
            } else {
//                BooleanExpression invoiceExp = qDebitDocument.isNotNull().and(qDebitDocument.docnumber.eq(search.getInvoiceNumber()));
//                List<DebitDocument> debitDocument = IterableUtils.toList(debitDocRepository.findAll(invoiceExp));
                List<Integer> dbtDocId = debitDocRepository.findDebitDocumentIdsByDocumentNumber(search.getInvoiceNumber());
                if (!dbtDocId.isEmpty())
                    exp = exp.and(qCreditDocument.invoiceId.in(dbtDocId));
            }
//            if (debitDocument.iterator().hasNext()) {
//                List<Integer> invoiceId = StreamSupport.stream(debitDocument.spliterator(),false).map(DebitDocument::getId).collect(Collectors.toList());
//                BooleanExpression credDebMapExp = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.in(invoiceId));
//                List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
//                creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(credDebMapExp);
//                List<Integer> list = creditDebitDocMappings.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
//
//                exp = exp.and(qCreditDocument.id.in(list));
//            } else {
//                exp = exp.and(qCreditDocument.id.eq(Integer.MAX_VALUE));
//            }
        }
        if (search.getChequeNo() != null && !search.getChequeNo().isEmpty()) {
            exp = exp.and(qCreditDocument.paydetails2.eq(search.getChequeNo()));
        }
        if (search.getReceiptNo() != null && !search.getReceiptNo().isEmpty()) {
            exp = exp.and(qCreditDocument.reciptNo.eq(search.getReceiptNo()));
        }
        if (search.getPaydetails1() != null && !search.getPaydetails1().isEmpty()) {
            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.bankname.equalsIgnoreCase(search.getPaydetails1()));
            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

            if (bankManagement != null) {
                exp = exp.and(qCreditDocument.bankManagement.eq(bankManagement.get().getId()));
            }
        }
        if (search.getDestinationBank() != null && !search.getDestinationBank().isEmpty()) {
            QBankManagement qBankManagement = QBankManagement.bankManagement;
            BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.bankname.equalsIgnoreCase(search.getDestinationBank()));
            Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

            if (bankManagement != null) {
                exp = exp.and(qCreditDocument.destinationBank.eq(bankManagement.get().getId()));
            }
        }
        if (search.getPartnerName() != null && !search.getPartnerName().isEmpty()) {
            QPartner qPartner = QPartner.partner;
            BooleanExpression be = qPartner.isNotNull().and(qPartner.isDelete.eq(false).and(qPartner.name.equalsIgnoreCase(search.getPartnerName())));
            exp = exp.and(qCreditDocument.customer.partner.name.eq(partnerRepository.findOne(be).get().getName()));
        } else {
            if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                exp = exp.and(qCreditDocument.customer.partner.id.eq(getLoggedInUserPartnerId()));
            }
        }
        if (search.getServiceAreaId() != null) {
            QServiceArea qServiceArea = QServiceArea.serviceArea;
            BooleanExpression be = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(qServiceArea.id.eq(search.getServiceAreaId())));
            exp = exp.and(qCreditDocument.customer.servicearea.id.eq(search.getServiceAreaId()));
        } else {
            List<Long> ids = customerService.getServiceAreaIdsList();
            if (getLoggedInUserId() != 1 && !ids.isEmpty()) {
                exp = exp.and(qCreditDocument.customer.servicearea.id.in(ids));
            }
        }
        exp = exp.and(qCreditDocument.isDelete.eq(false)).and(qCreditDocument.customer.isDeleted.eq(false));
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
        {
            // TODO: pass mvnoID manually 6/5/2025

            exp = exp.and(qCreditDocument.mvnoId.eq(mvnoId).or(qCreditDocument.mvnoId.eq(1)));

        }
        if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff()) && getBUIdsFromCurrentStaff().size() > 0)
            exp = exp.and(qCreditDocument.buID.in(getBUIdsFromCurrentStaff()));

        Predicate builder1 = exp;
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUSTOMER_INVOICE_DOC_PATH).get(0).getValue();
        // TODO: pass mvnoID manually 6/5/2025
        if (search.getMvnoId() != 1) {
            Integer customPageSize =requestDTO.getPageSize();
            Integer pageNumber= requestDTO.getPage();
            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

                QueryResults<CreditDocumentSearchPojo> queryResults = queryFactory
                    .select(Projections.constructor(
                            CreditDocumentSearchPojo.class,
                            qCreditDocument.id,
                            qCreditDocument.customer.title.concat(" ").concat(qCreditDocument.customer.firstname).concat(" ").concat(qCreditDocument.customer.lastname),
                            qCreditDocument.amount,
                            qCreditDocument.creditdocumentno,
                            qDebitDocument.docnumber,  // Include docnumber from debitdocument
                            qCreditDocument.paydetails2,
                            qCreditDocument.tdsamount,
                            qCreditDocument.abbsAmount,
                            qCreditDocument.referenceno,
                            qCreditDocument.paymode,
                            qCreditDocument.type,
                            qCreditDocument.paymentdate,
                            qCreditDocument.status,
                            qCreditDocument.approverid,
                            qCreditDocument.nextTeamHierarchyMappingId,
                            qCreditDocument.createdByName,
                            qCreditDocument.remarks,
                            qCreditDocument.filename,
                            qCreditDocument.customer.id,
                            qCreditDocument.batchAssigned,
                            qMvno.name,
                            qCustomers.currency,
                            qStaffUser.staffUserparent.id
                            ))
                    .from(qCreditDocument)
                        .join(qCustomers).on(qCreditDocument.customer.id.eq(qCustomers.id))
                    .leftJoin(qDebitDocument).on(qCreditDocument.invoiceId.eq(qDebitDocument.id))// Join with debitdocument table
                         .leftJoin(qMvno).on(qCustomers.mvnoId.eq(qMvno.id.intValue()))
                        .leftJoin(qStaffUser).on(qCreditDocument.createdById.eq(qStaffUser.id.intValue()))
                    .where(exp)
                    .orderBy(qCreditDocument.id.desc())
                    .offset((pageNumber - 1) * customPageSize)
                    .limit(customPageSize)
                    .fetchResults();



            List<CreditDocumentSearchPojo> creditDocumentPojos = queryResults.getResults();
            //creditDocumentPojos.forEach(creditDocumentSearchPojo -> {if(creditDocumentSearchPojo.getFilename()!=null){creditDocumentSearchPojo.setFilename(PATH+creditDocumentSearchPojo.getFilename());}});
            long totalRecords = queryResults.getTotal();
            List<Integer> creditDocIds = creditDocumentPojos.stream()
                    .map(CreditDocumentSearchPojo::getId)
                    .collect(Collectors.toList());

            List<Object[]> invoiceResults = creditDocRepository.findInvoiceIdsByCreditDocIds(creditDocIds);

            Map<Integer, Integer> creditDocIdToInvoiceIdMap = new HashMap<>();
            for (Object[] row : invoiceResults) {
                Integer creditDocId = ((Number) row[0]).intValue();
                Integer invoiceId = row[1] != null ? ((Number) row[1]).intValue() : null;
                creditDocIdToInvoiceIdMap.put(creditDocId, invoiceId);
            }

            List<Integer> invoiceIds = creditDocIdToInvoiceIdMap.values().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, List<String>> invoiceNumberMap = new HashMap<>();
            if (!invoiceIds.isEmpty()) {
                try {
                    String token = getToken();
                    ResponseEntity<Map<Integer, List<String>>> response = revenueClient
                            .getInvoiceNumber("Bearer " + token, invoiceIds);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        invoiceNumberMap = response.getBody();
                    }
                } catch (Exception e) {
                    logger.error("Error calling RevenueClient: {}", e.getMessage(), e);
                }
            }

            Map<Integer, List<String>> finalInvoiceNumberMap = invoiceNumberMap;
            creditDocumentPojos.forEach(pojo -> {
                Integer creditDocId = pojo.getId();
                Integer invoiceId = creditDocIdToInvoiceIdMap.get(creditDocId);
                if (invoiceId != null && finalInvoiceNumberMap.containsKey(invoiceId)) {
                    List<String> docNums = finalInvoiceNumberMap.get(invoiceId);
                    if (docNums != null && !docNums.isEmpty()) {
                        pojo.setInvoiceNumber(docNums.get(0));
                    }
                }
            });

            return new PageImpl<>(creditDocumentPojos, PageRequest.of(pageNumber - 1, customPageSize), totalRecords);
        }else {

        Integer customPageSize =requestDTO.getPageSize();
        Integer pageNumber= requestDTO.getPage();
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

            QueryResults<CreditDocumentSearchPojo> queryResults = queryFactory
                    .select(Projections.constructor(
                            CreditDocumentSearchPojo.class,
                            qCreditDocument.id,
                            qCreditDocument.customer.title.concat(" ").concat(qCreditDocument.customer.firstname).concat(" ").concat(qCreditDocument.customer.lastname),
                            qCreditDocument.amount,
                            qCreditDocument.creditdocumentno,
                            qDebitDocument.docnumber,  // Include docnumber from debitdocument
                            qCreditDocument.paydetails2,
                            qCreditDocument.tdsamount,
                            qCreditDocument.abbsAmount,
                            qCreditDocument.referenceno,
                            qCreditDocument.paymode,
                            qCreditDocument.type,
                            qCreditDocument.paymentdate,
                            qCreditDocument.status,
                            qCreditDocument.approverid,
                            qCreditDocument.nextTeamHierarchyMappingId,
                            qCreditDocument.createdByName,
                            qCreditDocument.remarks,
                            qCreditDocument.filename,
                            qCreditDocument.customer.id,
                            qCreditDocument.batchAssigned,
                            qMvno.name,
                            qCustomers.currency,
                            qStaffUser.staffUserparent.id))
                    .from(qCreditDocument)
                    .join(qCustomers).on(qCreditDocument.customer.id.eq(qCustomers.id))
                    .leftJoin(qDebitDocument).on(qCreditDocument.invoiceId.eq(qDebitDocument.id))// Join with debitdocument table
                    .leftJoin(qMvno).on(qCustomers.mvnoId.eq(qMvno.id.intValue()))
                    .leftJoin(qStaffUser).on(qCreditDocument.createdById.eq(qStaffUser.id.intValue()))
                    .where(exp)
                    .orderBy(qCreditDocument.id.desc())
                    .offset((pageNumber - 1) * customPageSize)
                    .limit(customPageSize)
                    .fetchResults();



            List<CreditDocumentSearchPojo> creditDocumentPojos = queryResults.getResults();
      //  creditDocumentPojos.forEach(creditDocumentSearchPojo -> {if(creditDocumentSearchPojo.getFilename()!=null){creditDocumentSearchPojo.setFilename(PATH+creditDocumentSearchPojo.getFilename());}});
        long totalRecords = queryResults.getTotal();
            List<Integer> creditDocIds = creditDocumentPojos.stream()
                    .map(CreditDocumentSearchPojo::getId)
                    .collect(Collectors.toList());


            List<Object[]> invoiceResults = creditDocRepository.findInvoiceIdsByCreditDocIds(creditDocIds);

            Map<Integer, Integer> creditDocIdToInvoiceIdMap = new HashMap<>();
            for (Object[] row : invoiceResults) {
                Integer creditDocId = ((Number) row[0]).intValue();
                Integer invoiceId = row[1] != null ? ((Number) row[1]).intValue() : null;
                creditDocIdToInvoiceIdMap.put(creditDocId, invoiceId);
            }

            List<Integer> invoiceIds = creditDocIdToInvoiceIdMap.values().stream()
                    .filter(Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());

            Map<Integer, List<String>> invoiceNumberMap = new HashMap<>();
            if (!invoiceIds.isEmpty()) {
                try {
                    String token = getToken();
                    ResponseEntity<Map<Integer, List<String>>> response = revenueClient
                            .getInvoiceNumber("Bearer " + token, invoiceIds);

                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        invoiceNumberMap = response.getBody();
                    }
                } catch (Exception e) {
                    logger.error("Error calling RevenueClient: {}", e.getMessage(), e);
                }
            }

            Map<Integer, List<String>> finalInvoiceNumberMap = invoiceNumberMap;
            creditDocumentPojos.forEach(pojo -> {
                Integer creditDocId = pojo.getId();
                Integer invoiceId = creditDocIdToInvoiceIdMap.get(creditDocId);
                if (invoiceId != null && finalInvoiceNumberMap.containsKey(invoiceId)) {
                    List<String> docNums = finalInvoiceNumberMap.get(invoiceId);
                    if (docNums != null && !docNums.isEmpty()) {
                        pojo.setInvoiceNumber(docNums.get(0));
                    }
                }
            });

            return new PageImpl<>(creditDocumentPojos, PageRequest.of(pageNumber - 1, customPageSize), totalRecords);
        }

    }

    public String getToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getCredentials() instanceof String) {
            return (String) authentication.getCredentials();
        }
        return null;
    }

    public void savecreditDoc(CreditDocMessageList messages) {
        try{
            for (CreditDocMessage message : messages.getCreditDocMessageList()) {
                CreditDocument creditDocument = new CreditDocument(message, customersRepository.findById(message.getCustomer()).get());
                log.debug(" credit doc before saving:"+ creditDocument);
                log.debug("From CreditDocMessage CreatedByName:" + message.getCreatedByName());
                creditDocument.setCreatedByName(message.getCreatedByName());
                log.debug("CreditDocMessage before saving to staffuser CreatedByName:" + message.getCreatedByName());
                StaffUser loggedInUser = staffUserRepository.findById(message.getLoggedInuserid()).orElse(null);
                if(Objects.nonNull(loggedInUser)){
                    creditDocument.setApproverid(message.getLoggedInuserid());
                }
                log.debug("CreditDocMessage after saving to staffuser CreatedByName:" + message.getCreatedByName());
                log.debug(" credit doc before saving:"+ creditDocument);
                creditDocument = creditDocRepository.save(creditDocument);
                log.debug(" credit doc after saving:"+ creditDocument);

                RecordPaymentPojo recordPaymentPojo = new RecordPaymentPojo(creditDocument);
                    recordPaymentPojo.setInvoiceId(Collections.singletonList(creditDocument.getInvoiceId()));
                recordPaymentPojo.setLoggedInuserid(message.getLoggedInuserid());
                List<PaymentListPojo> paymentListPojoList=new ArrayList<>();
                PaymentListPojo paymentListPojo=new PaymentListPojo();
                    paymentListPojo.setInvoiceId(creditDocument.getInvoiceId());
                    paymentListPojo.setAmountAgainstInvoice(message.getAmount());
                    paymentListPojo.setAbbsAmountAgainstInvoice(message.getAbbsAmount());
                    paymentListPojo.setTdsAmountAgainstInvoice(message.getTdsamount());
                paymentListPojoList.add(paymentListPojo);

                recordPaymentPojo.setPaymentListPojos(paymentListPojoList);
                if(Objects.nonNull(message.getCreditDebitDocMappingList())){
                    this.save(recordPaymentPojo, false, false, false,message.getCreditDebitDocMappingList().get(0));
                }else{
                    this.save(recordPaymentPojo, false, false, false,null);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCPR(VoidInvoiceMessage message) {
        List<CustPlanMappping> custPlanMapppingList=custPlanMappingRepository.findAllByIdIn(message.getCprIdlist());
        List<CustPlanMappping> custPlanMapppings=new ArrayList<>();
        for(CustPlanMappping custPlanMappping : custPlanMapppingList){
            custPlanMappping.setStatus(CommonConstants.STOP_STATUS);
            custPlanMappping.setIsVoid(Boolean.TRUE);
            custPlanMappping.setEndDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
            custPlanMappping.setExpiryDate(LocalDateTime.now().minus(1, ChronoUnit.SECONDS));
            custPlanMapppings.add(custPlanMappping);
        }
        custPlanMappingRepository.saveAll(custPlanMapppings);
    }

    public void processCreditDocIds(CreditDocIdsMessages message) {
        try {
               List<CreditDocument> creditDocument =  creditDocRepository.findAllByIdIn(message.getCreditDocumentIds());
               List<CreditDocument> creditDocumentProcessed = new ArrayList<>();
               if (message.getAction().equalsIgnoreCase("Approve")) {
                   creditDocumentProcessed = creditDocument.stream().peek(i -> i.setBatchAssigned(true)).collect(Collectors.toList());
               }else {
                    creditDocumentProcessed = creditDocument.stream().peek(i -> i.setBatchAssigned(false)).collect(Collectors.toList());
               }
               creditDocRepository.saveAll(creditDocumentProcessed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void processCreditDoc(ListOfCreditDocForBatch message) {
        try {
            Customers customers = customersRepository.findById(message.getCreditDocMessageList().get(0).getCustomer()).get();
            List<CreditDocMessage> messages = message.getCreditDocMessageList();
            for (CreditDocMessage creditDocMessage: messages) {
                creditDocRepository.findById(creditDocMessage.getId());
                CreditDocument creditDoc = new CreditDocument(creditDocMessage, customers);
                creditDocRepository.save(creditDoc);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void savecreditDebitDocMappings(List<CreditDebitDocMapping> creditDebitDocMappingList) {
        try{
            creditDebtMappingRepository.saveAll(creditDebitDocMappingList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateBudPaymentData(BudPayPaymentMessage message) {
        try {
                CustomerPayment customerPayment = customerPaymentRepository.findByCustIdAndAndOrderId(message.getCustomerId(),Long.valueOf(message.getReferenceNumber()));
                customerPayment.setCreditDocumentId(message.getCreditDocId());
                customerPaymentRepository.save(customerPayment);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
