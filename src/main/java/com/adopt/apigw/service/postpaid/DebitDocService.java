package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.DebitDocumentDTOForAdjustment;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanMessage;
import com.adopt.apigw.modules.ChangePlanDTOs.ChangePlanMessageList;
import com.adopt.apigw.modules.ChangePlanDTOs.CustChargeDetailsRevenue;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerChargeDBR;
import com.adopt.apigw.modules.CustomerDBR.domain.CustomerDBR;
import com.adopt.apigw.modules.CustomerDBR.repository.CustomerDBRRepository;
import com.adopt.apigw.modules.Customers.SendCustomerPaymentDTO;
import com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping.CustomerInventoryMappingRepo;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerLedgerDetails;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerDetailsRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.customerDocDetails.service.CustomerDocDetailsService;
import com.adopt.apigw.modules.planUpdate.repository.CustomerPackageRepository;
import com.adopt.apigw.modules.subscriber.model.PurchasedHistoryMapper;
import com.adopt.apigw.modules.subscriber.service.ChargeThread;
import com.adopt.apigw.modules.subscriber.service.InvoiceCreationThread;
import com.adopt.apigw.modules.subscriber.service.InvoiceThread;
import com.adopt.apigw.modules.subscriber.service.ReceiptThread;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.DebitDocDetailDTO;
import com.adopt.apigw.pojo.FlagDTO;
import com.adopt.apigw.pojo.SearchDebitDocs;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.CustomerThreadService;
import com.adopt.apigw.service.SchedulerLockService;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.*;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class DebitDocService extends AbstractService<DebitDocument, DebitDocumentPojo, Integer> {

    private static final Logger logger = LoggerFactory.getLogger(DebitDocService.class);

    private static String MODULE = " [DebitDocController] ";

    @Autowired
    private DebitDocRepository entityRepository;

    @Autowired
    private BillRunService billRunService;

    @Autowired
    private CustomersService customersService;

    @Autowired
    private PurchasedHistoryMapper purchasedHistoryMapper;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired(required = true)
    CreditDebtMappingRepository creditDebitDocMapping;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    CustomerLedgerDtlsRepository customerLedgerDtlsRepository;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    PartnerLedgerDetailsRepository partnerLedgerDetailsRepository;

    @Autowired
    private CustomerLedgerDtlsService customerLedgerDtlsService;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;


    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    CustPlanMappingService custPlanMappingService;
    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    DebitDocStaffAssignRepo debitDocStaffAssignRepo;

    @Autowired
    ClientServiceSrv clientServiceSrv;

    @Autowired
    WorkflowAuditService workflowAuditService;
    @Autowired
    StaffUserService staffUserService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private TatUtils tatUtils;

    @Autowired
    CustomerDBRRepository customerDBRRepository;

    @Autowired
    DbrService dbrService;

    @Autowired
    CustomerChargeHistoryRepo customerChargeHistoryRepo;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    private CustChargeRepository custChargeRepository;
    @Autowired
    private CustChargeDetailsRepository custChargeDetailsRepository;

    @Autowired
    CustomerLedgerRepository customerLedgerRepository;

    @Autowired
    private TrialDebitDocRepository trialDebitDocRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private DebitDocDetailRepository debitDocDetailRepository;
    @Autowired
    private BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
    @Autowired
    private EzBillServiceUtility ezBillServiceUtility;

    @Autowired
    private CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    ChargeRepository chargeRepository;

    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    CustomerPackageRepository customerPackageRepository;

    @Autowired
    CreateDataSharedService createDataSharedService;

    @Autowired
    private NumberSequenceUtil numberSequenceUtil;
    @Autowired
    MvnoRepository mvnoRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;


    @Autowired
    PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    CustomerDocDetailsService customerDocDetailsService;

    @Autowired
    CustomerInventoryMappingRepo customerInventoryMappingRepo;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;


    @Override
    public JpaRepository<DebitDocument, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '1')")

    public List<DebitDocument> getAllEntities(Integer pageNumber, int pageSize) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findAll();
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '1')")
    public List<DebitDocument> searchByBillRunId(String billRunId) {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findByBillrunid(Integer.valueOf(billRunId));
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '2')")
    public boolean revertInvoice(String invoiceId) throws Exception {
//    	PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return billRunService.revertInvoice(invoiceId);
    }

//    public List<DebitDocument> getAllByCustomer(Integer customerid) {
//        Customers customer = customersService.get(customerid);
//        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//        BooleanExpression booleanExpression = qDebitDocument.isNotNull().and(qDebitDocument.customer.id.eq(customer.getId())).and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID")).and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("Cancelled")).and(qDebitDocument.totalamount.subtract(qDebitDocument.adjustedAmount.coalesce(0d)).ne((double) 0));
//        List<DebitDocument> debitDocuments = ((List<DebitDocument>) entityRepository.findAll(booleanExpression)).stream().map(debitDocument -> {
//            debitDocument.setPendingAmt(creditDocRepository.findTotalPendingAmountByDebitDocId(debitDocument.getId()));
//            return debitDocument;
//        }).collect(Collectors.toList());
//
//        return IterableUtils.toList(debitDocuments);
//    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '1')")
    public Page<DebitDocSearchPojo> searchInvoice(SearchDebitDocsPojo searchDebitDocsPojo, PaginationRequestDTO paginationDTO, boolean isInvoiceVoid) {
        pageRequest = generatePageRequest(paginationDTO.getPage(), paginationDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        LocalDate startDate = searchDebitDocsPojo.getBillfromdate();
        LocalDate endDate = searchDebitDocsPojo.getBilltodate();
        Page<DebitDocSearchPojo> debitDocSearchPojos = null;
        logger.info("date is ::::::" + startDate);
        QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
        QCustomers qCustomers = QCustomers.customers;
        String staffName = null;
        if (searchDebitDocsPojo.getStaffId() != null) {
            String staff = staffUserRepository.findNameById(searchDebitDocsPojo.getStaffId());
            if (staff != null && !staff.isEmpty()){
                staffName = staff;
            }
        }
        try {

            BooleanExpression exp = qDebitDocument.isNotNull();

            if(searchDebitDocsPojo.getCustomerid()==null || (searchDebitDocsPojo.getCustomerid()!=null && searchDebitDocsPojo.getCustomerid().intValue()!=1 && searchDebitDocsPojo.getCustomerid().intValue()!=2))
            {

                // TODO: pass mvnoID manually 6/5/2025
                if(getMvnoIdFromCurrentStaff(null)!=null && getMvnoIdFromCurrentStaff(null)!=1 && getBUIdsFromCurrentStaff()!=null && getBUIdsFromCurrentStaff().isEmpty()) {
                    // TODO: pass mvnoID manually 6/5/2025
                    exp = exp.and(qDebitDocument.customer.mvnoId.eq(1).or(qDebitDocument.customer.mvnoId.eq(getMvnoIdFromCurrentStaff(null))));
                }

                if (getBUIdsFromCurrentStaff().size() != 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    exp = exp.and(qDebitDocument.customer.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qDebitDocument.customer.buId.in(getBUIdsFromCurrentStaff())));

            }

            if (getLoggedInUser().getLco()) {
                exp = exp.and(qDebitDocument.lcoId.eq(getLoggedInUser().getPartnerId()));
            } else {
                exp = exp.and(qDebitDocument.lcoId.isNull());
            }

            if (searchDebitDocsPojo.getCustomerid() != null && (searchDebitDocsPojo.getCustomerid().equals(1) || searchDebitDocsPojo.getCustomerid().equals(2))) {
                if (isInvoiceVoid) {
                    exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID"));
                }
                if (searchDebitDocsPojo.getServiceAreaId() != null) {
                    exp = exp.and(qDebitDocument.customer.servicearea.id.eq(Long.valueOf(searchDebitDocsPojo.getServiceAreaId())));
                }
                if (searchDebitDocsPojo.getPartnerId() != null) {
                    exp = exp.and(qDebitDocument.customer.partner.id.eq(searchDebitDocsPojo.getPartnerId()));
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    exp = exp.and(qDebitDocument.staffid.eq(searchDebitDocsPojo.getStaffId()));
                }
                if (searchDebitDocsPojo.getStatus() != null && !searchDebitDocsPojo.getStatus().isEmpty()) {
                    exp = exp.and(qDebitDocument.paymentStatus.toLowerCase().in(searchDebitDocsPojo.getStatus().stream().map(x->x.toLowerCase()).collect(Collectors.toList())));
                }
                if (searchDebitDocsPojo.getBillrunid() != null) {
                    exp = exp.and(qDebitDocument.billrunid.eq(searchDebitDocsPojo.getBillrunid()));
                }
//                if (searchDebitDocsPojo.getCustomerid() != null && searchDebitDocsPojo.getBranchId() == null && searchDebitDocsPojo.getBusinessunit() == null) {
//                    exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
//                }

                Optional<Customers> customers = customersRepository.findById(searchDebitDocsPojo.getCustomerid());
                String customerTypeName = customersService.getCustomerType(customers.get().getCusttype());

                if(customerTypeName.equalsIgnoreCase(customers.get().getUsername()) && searchDebitDocsPojo.getBranchId() != null) {
                    if (searchDebitDocsPojo.getCustomerid() != null && searchDebitDocsPojo.getBranchId() == null || searchDebitDocsPojo.getBusinessunit() == null) {
                        List<String> customersList = customersRepository.findUsernameByBranchId(searchDebitDocsPojo.getBranchId());
                        List<DebitDocument> debitId = debitDocRepository.findAllByCustRefNameIn(customersList);
                        List<Integer> ids = debitId.stream().map(i -> i.getId()).collect(Collectors.toList());
                        exp = exp.and(qDebitDocument.id.in(ids));
                    }
                }else {
                    if (searchDebitDocsPojo.getBranchId() != null) {
                        exp = exp.and(qDebitDocument.customer.branch.eq(searchDebitDocsPojo.getBranchId()));
                    }
                }

                if (startDate != null) {
                    exp = exp.and(qDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
                }
                if (endDate != null) {
                    exp = exp.and(qDebitDocument.billdate.loe(endDate.atTime(23, 59, 59)));
                }
                if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.firstname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                        exp = exp.or(qDebitDocument.customer.lastname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    }
                }
                if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                }
                if (searchDebitDocsPojo.getStaffId() != null) {
                    exp = exp.and(qDebitDocument.createdById.eq(searchDebitDocsPojo.getStaffId()));
                }
                if (searchDebitDocsPojo.getServiceId() != null) {
                    exp = exp.and(qDebitDocument.postpaidPlan.serviceId.eq(searchDebitDocsPojo.getServiceId()));
                }
                if (searchDebitDocsPojo.getPlanId() != null) {
                    exp = exp.and(qDebitDocument.postpaidPlan.id.eq(searchDebitDocsPojo.getPlanId()));
                }
                //filter by bu
                if (searchDebitDocsPojo.getBusinessunit() != null) {
                    exp = exp.and(qDebitDocument.buId.eq(searchDebitDocsPojo.getBusinessunit()));
                }
                if (searchDebitDocsPojo.getCustmobile() != null && !searchDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.mobile.equalsIgnoreCase(searchDebitDocsPojo.getCustmobile()));
                }
                if (searchDebitDocsPojo.getDocnumber() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.docnumber.equalsIgnoreCase(searchDebitDocsPojo.getDocnumber()));
                }

                if (searchDebitDocsPojo.getAdjustedAmount() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.adjustedAmount.eq(searchDebitDocsPojo.getAdjustedAmount()));
                }
                exp = exp.and(qDebitDocument.isDelete.eq(false)).and(qDebitDocument.customer.isDeleted.eq(false));

                if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                }
                if (searchDebitDocsPojo.getCustomerid() != null) {
                    exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
                }
            } else {
                if (isInvoiceVoid) {
                    exp = exp.and(qDebitDocument.billrunstatus.notEqualsIgnoreCase("VOID"));
                }

                if (searchDebitDocsPojo.getBillrunid() != null) {
                    exp = exp.and(qDebitDocument.billrunid.eq(searchDebitDocsPojo.getBillrunid()));
                }

                if (startDate != null) {
                    exp = exp.and(qDebitDocument.billdate.goe(startDate.atTime(00, 00, 00)));
                }
                if (endDate != null) {
                    exp = exp.and(qDebitDocument.billdate.loe(endDate.atTime(23, 59, 59)));
                }
                if (searchDebitDocsPojo.getServiceAreaId() != null) {
                    exp = exp.and(qDebitDocument.customer.servicearea.id.eq(Long.valueOf(searchDebitDocsPojo.getServiceAreaId())));
                } else {
                    List<Long> ids = customersService.getServiceAreaIdsList();
                    if (getLoggedInUserId() != 1 && !ids.isEmpty()) {
                            exp = exp.and(qDebitDocument.customer.servicearea.id.in(ids));
                    }
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    exp = exp.and(qDebitDocument.customer.partner.id.eq(searchDebitDocsPojo.getPartnerId()));
                }

                if (searchDebitDocsPojo.getBranchId() != null) {
                    exp = exp.and(qDebitDocument.customer.branch.eq(searchDebitDocsPojo.getBranchId()));
                }

                if (searchDebitDocsPojo.getPartnerId() != null) {
                    exp = exp.and(qDebitDocument.staffid.eq(searchDebitDocsPojo.getStaffId()));
                }

                if (searchDebitDocsPojo.getStatus() != null && !searchDebitDocsPojo.getStatus().isEmpty()) {
                    exp = exp.and(qDebitDocument.paymentStatus.toLowerCase().in(searchDebitDocsPojo.getStatus().stream().map(x->x.toLowerCase()).collect(Collectors.toList())));
                }

                if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.firstname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    if (searchDebitDocsPojo.getCustname() != null && !searchDebitDocsPojo.getCustname().equalsIgnoreCase("")) {
                        exp = exp.or(qDebitDocument.customer.lastname.equalsIgnoreCase(searchDebitDocsPojo.getCustname()));
                    }
                }

                if (searchDebitDocsPojo.getType() != null && !searchDebitDocsPojo.getType().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.custtype.equalsIgnoreCase(searchDebitDocsPojo.getType()));
                }

                if (searchDebitDocsPojo.getCustmobile() != null && !searchDebitDocsPojo.getCustmobile().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.customer.mobile.equalsIgnoreCase(searchDebitDocsPojo.getCustmobile()));
                }

                if (searchDebitDocsPojo.getDocnumber() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.docnumber.equalsIgnoreCase(searchDebitDocsPojo.getDocnumber()));
                }

                if (searchDebitDocsPojo.getAdjustedAmount() != null && !searchDebitDocsPojo.getDocnumber().equalsIgnoreCase("")) {
                    exp = exp.and(qDebitDocument.adjustedAmount.eq(searchDebitDocsPojo.getAdjustedAmount()));
                }
                exp = exp.and(qDebitDocument.isDelete.eq(false)).and(qDebitDocument.customer.isDeleted.eq(false));


                if (searchDebitDocsPojo.getCustomerid() != null) {

                    exp = exp.and(qDebitDocument.customer.id.eq(searchDebitDocsPojo.getCustomerid()));
                }
            }

            JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);

            QueryResults<DebitDocSearchPojo> queryResults = queryFactory
                    .select(Projections.constructor(
                            DebitDocSearchPojo.class,
                            qDebitDocument.customer.id,
                            qDebitDocument.customer.title.concat(" ").concat(qDebitDocument.customer.firstname.concat(" ").concat(qDebitDocument.customer.lastname)),
                            qDebitDocument.paymentStatus,
                            qDebitDocument.adjustedAmount,
                            qDebitDocument.nextStaff,
                            qDebitDocument.nextTeamHierarchyMappingId,
                            qDebitDocument.billrunstatus,
                            qDebitDocument.createdate,
                            qDebitDocument.totalamount,
                            qDebitDocument.docnumber,
                            qDebitDocument.billdate,
                            qDebitDocument.createdByName,
                            qDebitDocument.customer.custtype,
                            qDebitDocument.billableToName,
                            qDebitDocument.id,
                            qDebitDocument.status,
                            qDebitDocument.custRefName,
                            qDebitDocument.remarks,
                            qCustomers.currency))
                    .from(qDebitDocument)
                    .join(qCustomers).on(qDebitDocument.customer.id.eq(qCustomers.id))
                    .where(exp)
                    .orderBy(qDebitDocument.id.desc())
                    .offset((paginationDTO.getPage() - 1) * paginationDTO.getPageSize())
                    .limit(paginationDTO.getPageSize())
                    .fetchResults();

            List<DebitDocSearchPojo> debitDocSearchPojo = queryResults.getResults();
            long totalRecords = queryResults.getTotal();

            return new PageImpl<>(debitDocSearchPojo, PageRequest.of(paginationDTO.getPage() - 1, paginationDTO.getPageSize()), totalRecords);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return debitDocSearchPojos;

    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '2')")
    public SearchDebitDocs getSearchDebitDocsForInvoiceList() {
        return new SearchDebitDocs();
    }

    public DebitDocumentPojo convertDebitDocumentModelToDebitDocumentPojo(DebitDocument debitDocument) throws Exception {

        DebitDocumentPojo pojo = null;
        if (debitDocument != null) {
            pojo = new DebitDocumentPojo();
            pojo.setId(debitDocument.getId());
            pojo.setDocnumber(debitDocument.getDocnumber());
            if (debitDocument.getCustomer() != null) {
                pojo.setCustomer(customersService.convertCustomersModelToCustomersPojo(debitDocument.getCustomer()));
            }
            pojo.setBilldate(debitDocument.getBilldate());
            pojo.setCreatedate(debitDocument.getCreatedate());
            pojo.setStartdate(debitDocument.getStartdate());
            pojo.setEndate(debitDocument.getEndate());
            pojo.setDuedate(debitDocument.getDuedate());
            pojo.setLatepaymentdate(debitDocument.getLatepaymentdate());
            pojo.setSubtotal(debitDocument.getSubtotal());
            pojo.setTax(debitDocument.getTax());
            pojo.setDiscount(debitDocument.getDiscount());
            pojo.setTotalamount(debitDocument.getTotalamount());
            pojo.setPreviousbalance(debitDocument.getPreviousbalance());
            pojo.setLatepaymentfee(debitDocument.getLatepaymentfee());
            pojo.setCurrentcredit(debitDocument.getCurrentcredit());
            pojo.setCurrentdebit(debitDocument.getCurrentdebit());
            pojo.setTotaldue(debitDocument.getTotaldue());
            pojo.setAmountinwords(debitDocument.getAmountinwords());
            pojo.setDueinwords(debitDocument.getDueinwords());
            pojo.setBillrunid(debitDocument.getBillrunid());
            pojo.setBillrunstatus(debitDocument.getBillrunstatus());
            pojo.setDocument(debitDocument.getDocument());
//            pojo.setAdjustedAmount(debitDocument.getAdjustedAmount());
            pojo.setCreatedByName(debitDocument.getCreatedByName());
            pojo.setLastModifiedByName(debitDocument.getLastModifiedByName());
            pojo.setBillableToName(debitDocument.getBillableToName());
            if (debitDocument.getPaymentStatus() != null && !"".endsWith(debitDocument.getPaymentStatus())) {
                pojo.setPaymentStatus(debitDocument.getPaymentStatus());
            }
            if (debitDocument.getCustomer() != null) {
                pojo.setCustid(debitDocument.getCustomer().getId());
                pojo.setCustomerName(debitDocument.getCustomer().getFullName());
                pojo.setCustType(debitDocument.getCustomer().getCusttype());
            }
            pojo.setCustRefName(debitDocument.getCustRefName());
            pojo.setRefundAbleAmount(getRefundAmount(pojo));
            pojo.setDebitDocumentTAXRels(getDebitTaxPojo(debitDocument.getId()));
            pojo.setNextStaff(debitDocument.getNextStaff());
            pojo.setNextTeamHierarchyMappingId(debitDocument.getNextTeamHierarchyMappingId());
            pojo.setPaymentStatus(getStatus(debitDocument));
            List<DebitDocDetails> details = new ArrayList<>();
            List<DebitDocDetails> debitDocDetails = debitDocRepository.debitDocDetailsByDebitDocId(debitDocument.getId());
            Map<Integer, List<DebitDocDetails>> collect = debitDocDetails.stream().collect(Collectors.groupingBy(DebitDocDetails::getChargeid));
            for (Map.Entry<Integer, List<DebitDocDetails>> entry : collect.entrySet()) {
                List<DebitDocDetails> list = entry.getValue();
                Double subtotal = list.stream().mapToDouble(x -> x.getSubtotal()).sum();
                Double total = list.stream().mapToDouble(x -> x.getTotalamount()).sum();
                Double discount = list.stream().mapToDouble(x -> x.getDiscount()).sum();
                Double tax = list.stream().mapToDouble(x -> x.getTax()).sum();
                list.get(0).setSubtotal(subtotal);
                list.get(0).setTotalamount(total);
                list.get(0).setDiscount(discount);
                list.get(0).setTax(tax);
                details.add(list.get(0));
            }
            List<DebitDocumentInventoryRel> debitDocumentInventoryRels = debitDocument.getDebitDocumentInventoryRels();
            if (!CollectionUtils.isEmpty(debitDocumentInventoryRels)) {
                pojo.setDebitDocumentInventoryRels(debitDocumentInventoryRels);
            }

            pojo.setDebitDocDetails(details);
            pojo.setStatus(debitDocument.getStatus());
            if (debitDocument.getPaymentowner() != null && debitDocument.getPaymentowner() != "") {
                pojo.setPaymentowner(debitDocument.getPaymentowner());
            } else if (debitDocument.getStaffid() != null) {
                StaffUser staffUser = staffUserRepository.findById(debitDocument.getStaffid()).get();
                pojo.setPaymentowner(staffUser.getUsername());
            }

            pojo.setIsPromiseToPayInOldCPR(debitDocument.getIsPromiseToPayInOldCPR());
            pojo.setPromiseToPayHoldDays(debitDocument.getPromiseToPayHoldDays());
            pojo.setPromiseEndDate(debitDocument.getPromiseEndDate());
            pojo.setPromiseStartDate(debitDocument.getPromiseStartDate());
            pojo.setIsCNEnable(debitDocument.getIsCNEnable());
            pojo.setPendingAmt(debitDocument.getPendingAmt());
            if (debitDocument.getInvoiceCancelRemarks() != null && !"".endsWith(debitDocument.getInvoiceCancelRemarks())) {
                pojo.setInvoiceCancelRemarks(debitDocument.getInvoiceCancelRemarks());
            }
        }
        return pojo;
    }

    private List<DebitDocumentTAXRel> getDebitTaxPojo(Integer id) {
        return debitDocRepository.getAllDebitDocTaxDetails(id);
    }

    private String getRefundAmount(DebitDocumentPojo pojo) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.00");
        DebitDocument debitDocument = debitDocRepository.findById(pojo.getId()).orElse(null);
        Double amount = getPendingRevenueWithTaxAtCurrentDate(debitDocument);
        if (amount != null)
            return amount.toString();
        else
            return "0.0";
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.DebitDocument', '1')")
    public List<DebitDocumentPojo> convertResponseModelIntoPojo(List<DebitDocument> debitDocumentList) throws Exception {
        List<DebitDocumentPojo> pojoListRes = new ArrayList<DebitDocumentPojo>();
        if (debitDocumentList != null && debitDocumentList.size() > 0) {
            for (DebitDocument debitDocument : debitDocumentList) {
                if (debitDocument.getBillrunstatus().equalsIgnoreCase("Exported")) {
                    debitDocument.setBillrunstatus("Printed");
                }
                pojoListRes.add(convertDebitDocumentModelToDebitDocumentPojo(debitDocument));
            }
        }
        return pojoListRes.stream().sorted(Comparator.comparing(DebitDocumentPojo::getId).reversed()).collect(Collectors.toList());
    }

    /*public List<PurchasedHistoryDTO> getByCustId(Integer custId) {
        List<DebitDocument> debitDocuments = entityRepository.getAllByCustomer_Id(custId);
        return debitDocuments.stream().map(data -> purchasedHistoryMapper
                .domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }*/

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Debit Document");
        List<DebitDocumentPojo> debitDocumentPojoList = new ArrayList<>();
        List<DebitDocument> debitDocumentList = getRepository().findAll();
        for (DebitDocument debitDocument : debitDocumentList)
            debitDocumentPojoList.add(convertDebitDocumentModelToDebitDocumentPojo(debitDocument));
        createExcel(workbook, sheet, DebitDocumentPojo.class, debitDocumentPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<DebitDocumentPojo> debitDocumentPojoList = new ArrayList<>();
        List<DebitDocument> debitDocumentList = getRepository().findAll();
        for (DebitDocument debitDocument : debitDocumentList)
            debitDocumentPojoList.add(convertDebitDocumentModelToDebitDocumentPojo(debitDocument));
        createPDF(doc, DebitDocumentPojo.class, debitDocumentPojoList, null);
    }

    public Optional<DebitDocument> getById(Integer id) {
        return entityRepository.findById(id);
    }


    public String InvoicePaymentDone(CreditDebitMappingPojo creditDebitDocMappingPojo) throws Exception {
        Integer invoiceId = creditDebitDocMappingPojo.getInvoiceId();
        List<CreditDebitDataPojo> creditDocumentList = creditDebitDocMappingPojo.getCreditDocumentList();
        DebitDocument debitDocument = debitDocRepository.findById(invoiceId).orElse(null);
        if (debitDocument != null) {
            Double amountToBePaid = 0d;
            if (debitDocument.getAdjustedAmount() == null) {
                amountToBePaid = debitDocument.getTotalamount();
            } else {
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
            }
            int i = 0;
            boolean adjusted = false;
            while (!adjusted && i < creditDocumentList.size()) {

                CreditDebitDocMapping creditDebitDocMappings = new CreditDebitDocMapping();
                CreditDocument creditDocument = creditDocRepository.findById(creditDocumentList.get(i).getId()).orElse(null);
                Double paymentAmount = 0d;
                if (creditDocument.getAdjustedAmount() != null) {
                    paymentAmount = creditDocument.getAmount() - creditDocument.getAdjustedAmount();
                } else {
                    paymentAmount = creditDocument.getAmount();
                }
                Double remainingAmountFromPayment = paymentAmount - amountToBePaid;
                if (remainingAmountFromPayment == 0) {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    if(Objects.isNull(creditDocument.getAdjustedAmount())){
                        creditDocument.setAdjustedAmount(0.0000);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);
                } else if (remainingAmountFromPayment < 0) {
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(paymentAmount);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + paymentAmount);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + creditDocument.getAmount());
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(paymentAmount);
                } else {
                    adjusted = true;
                    if (debitDocument.getAdjustedAmount() == null) {
                        debitDocument.setAdjustedAmount(amountToBePaid);
                    } else {
                        debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + amountToBePaid);
                    }
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                    creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                    creditDocument.setAdjustedAmount(creditDocument.getAdjustedAmount() + amountToBePaid);
                    creditDebitDocMappings.setDebtDocId(debitDocument.getId());
                    creditDebitDocMappings.setCreditDocId(creditDocument.getId());
                    creditDebitDocMappings.setAdjustedAmount(amountToBePaid);

                }
                i++;
                if (creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE) && creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED)
                        && creditDocument.getInvoiceId() != null) {
                    Optional<DebitDocument> oldDebitDoc = debitDocRepository.findById(creditDocument.getInvoiceId());
                    if (oldDebitDoc.isPresent() && creditDocument.getAmount().equals(oldDebitDoc.get().getTotalamount())) {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    } else {
                        oldDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                    }
                    debitDocRepository.save(oldDebitDoc.get());
                }
                creditDocRepository.save(creditDocument);
                creditDebtMappingRepository.save(creditDebitDocMappings);
                amountToBePaid = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();

                CustomerLedgerDtls ledgerDtls = new CustomerLedgerDtls();
                ledgerDtls.setCustomer(creditDocument.getCustomer());
                ledgerDtls.setDebitdocid(debitDocument.getId());
                ledgerDtls.setTranstype(CommonConstants.TRANS_TYPE_DEBIT);
                ledgerDtls.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls.setAmount(debitDocument.getAdjustedAmount());
                ledgerDtls.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsService.save(ledgerDtls);

                CustomerLedgerDtls ledgerDtls1 = new CustomerLedgerDtls();
                ledgerDtls1.setCustomer(creditDocument.getCustomer());
                ledgerDtls1.setDebitdocid(debitDocument.getId());
                ledgerDtls1.setCreditdocid(creditDocument.getId());
                ledgerDtls1.setTranstype(CommonConstants.TRANS_TYPE_CREDIT);
                ledgerDtls1.setTranscategory(CommonConstants.CREDIT_DOC_STATUS.ADJUSTMENT);
                ledgerDtls1.setAmount(debitDocument.getAdjustedAmount());
                ledgerDtls1.setPaymentRefNo(creditDocument.getCreditdocumentno());
                customerLedgerDtlsService.save(ledgerDtls1);
            }
            debitDocRepository.save(debitDocument);
            return "success";
        } else {
            return "Not found invoice with given id";
        }
    }


//    public List<ViewAdjustedPaymentPojo> FindAdjustedPaymentAgainstBill(Integer invoiceId) {
//        List<ViewAdjustedPaymentPojo> viewAdjustedPaymentPojos = new ArrayList<>();
//        List<CreditDebitDocMapping> creditDebitDocMappings = new ArrayList<>();
//        if (invoiceId != null) {
//            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
//            BooleanExpression booleanExpression1 = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.eq(invoiceId));
//            creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(booleanExpression1);
//            for (CreditDebitDocMapping creditDebitDocMapping : creditDebitDocMappings) {
//                CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMapping.getCreditDocId()).orElse(null);
//                if (Objects.nonNull(creditDocument)) {
//                    ViewAdjustedPaymentPojo viewAdjustedPaymentPojo = new ViewAdjustedPaymentPojo();
//                    viewAdjustedPaymentPojo.setAdjustedAmount(creditDebitDocMapping.getAdjustedAmount());
//                    viewAdjustedPaymentPojo.setReferenceNumber(creditDocument.getCreditdocumentno());
//                    viewAdjustedPaymentPojo.setAmount(creditDocument.getAmount());
//                    viewAdjustedPaymentPojo.setPaymode(creditDocument.getPaymode());
//                    viewAdjustedPaymentPojo.setPaymentdate(creditDocument.getPaymentdate());
//                    viewAdjustedPaymentPojo.setType(creditDocument.getType());
//                    viewAdjustedPaymentPojo.setStatus(creditDocument.getStatus());
//                    viewAdjustedPaymentPojos.add(viewAdjustedPaymentPojo);
//
//
//                }
//            }
//        }
//        viewAdjustedPaymentPojos = viewAdjustedPaymentPojos.stream().filter(viewAdjustedPaymentPojo -> (viewAdjustedPaymentPojo.getAdjustedAmount() != null /*&& viewAdjustedPaymentPojo.getAdjustedAmount() > 0*/)).collect(Collectors.toList());
//
//        return viewAdjustedPaymentPojos;
//    }

    public void voidInvoiceInBatch(List<Integer> invoiceIds,Integer mvnoId) {
        if (invoiceIds != null) {
            for (Integer id : invoiceIds) {
                voidInvoice(id, "customer terminated",mvnoId);
            }
        }
    }

    @Transactional
    public GenericDataDTO voidInvoice(Integer invoiceId, String invoiceCancelRemark,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        try {
            DebitDocument debitDocumentForCustomer = super.get(invoiceId,mvnoId);
            QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
            JPAQuery<DebitDocument> debitDocumentJPAQuery = new JPAQuery<>(entityManager);
            List<DebitDocument> lastInvoice = debitDocumentJPAQuery.from(qDebitDocument).where(qDebitDocument.customer.id.eq(debitDocumentForCustomer.getCustomer().getId()).and(qDebitDocument.billrunstatus.ne("VOID"))).orderBy(qDebitDocument.docnumber.desc()).limit(1).fetch();

            List<DebitDocument> debitDocumentList = new ArrayList<>();
            if (lastInvoice.size() == 0) {
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("No invoice available for void.");
                return genericDataDTO;
            } else if (debitDocumentForCustomer.getStartdate().toLocalDate().isBefore(LocalDate.now())) {
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("Invoice can not be void only on same day.");
                return genericDataDTO;
            } else {
                QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
                List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(qCreditDebitDocMapping.debtDocId.eq(debitDocumentForCustomer.getId()));
                if (creditDebitDocMappings.size() > 0) {
                    genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                    genericDataDTO.setResponseMessage("Invoice can not be void as some payment is adjusted to this invoice.");
                    return genericDataDTO;
                }
            }

            //subisu void function
            voidBilltoSubisuInvoice(invoiceId, invoiceCancelRemark,mvnoId);

            //Void function for Remove LCO PartnerLedger and Revert Commission add in Revenue
            if (debitDocumentForCustomer != null && debitDocumentForCustomer.getCustomer().getLcoId() != null)
                voidLcoPartnerLedgerAndRevertCommission(invoiceId);

            debitDocumentList.add(debitDocumentForCustomer);
            if (lastInvoice.get(0).getId().equals(debitDocumentForCustomer.getId())) {
                //Normal customer cpr data
                List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByDebitdocid(debitDocumentForCustomer.getId());
                if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                    if (custPlanMapppings.get(0).getBillTo().equalsIgnoreCase(Constants.ORGANIZATION) && custPlanMapppings.get(0).getIsInvoiceToOrg()) {

                        Set<Integer> custRefIds = custPlanMapppings.stream().map(CustPlanMappping::getId).filter(Objects::nonNull).collect(Collectors.toSet());
                        List<Integer> debitDocList = custPlanMappingRepository.findAllByCustRefId(custRefIds);
                        QDebitDocument qDebitDocumentForSubisu = QDebitDocument.debitDocument;
                        BooleanExpression expression = qDebitDocumentForSubisu.isNotNull().and(qDebitDocumentForSubisu.id.in(debitDocList)).and(qDebitDocumentForSubisu.billrunstatus.ne("VOID"));
                        List<DebitDocument> debitDocumentListForSubisu = (List<DebitDocument>) debitDocRepository.findAll(expression);
                        debitDocumentListForSubisu.stream().distinct();
                        debitDocumentList.addAll(debitDocumentListForSubisu);
                    }
                }

                for (DebitDocument debitDocument : debitDocumentList) {

                    List<TempPartnerLedgerDetail> tempPartnerLedgerDetail = tempPartnerLedgerDetailsRepository.findAllByInvoiceId(debitDocument.getId());
                    if (tempPartnerLedgerDetail != null && !tempPartnerLedgerDetail.isEmpty()) {
                        tempPartnerLedgerDetailsRepository.delete(tempPartnerLedgerDetail.get(0));
                    }

                    QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
                    List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(qCreditDebitDocMapping.debtDocId.eq(debitDocument.getId()));
                    double refundAmount = 0D;
                    if (creditDebitDocMappings.size() > 0) {
                        for (CreditDebitDocMapping debitDocMapping : creditDebitDocMappings) {
                            if (debitDocMapping.getAdjustedAmount() > 0) {
//                                refundAmount = refundAmount + debitDocMapping.getAdjustedAmount();
//                                creditDebtMappingRepository.delete(debitDocMapping);
                                CreditDocument creditDocument = creditDocRepository.getOne(debitDocMapping.getCreditDocId());
                                creditDocument.setAdjustedAmount(0d);
                                creditDocument.setPaytype("advance");
                                creditDocRepository.save(creditDocument);
                                debitDocMapping.setIsDeleted(true);
                                creditDebtMappingRepository.delete(debitDocMapping);

                            }
                        }
                    }

                    debitDocument.setBillrunstatus("VOID");
                    if (invoiceCancelRemark != null)
                        debitDocument.setInvoiceCancelRemarks(invoiceCancelRemark);
                    debitDocRepository.save(debitDocument);

                    //If invoice is direct charge
                    if (debitDocument.getIsDirectChargeInvoice()) {
                        List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocumentid(debitDocument.getId());
                        for (DebitDocDetails debitDocDet : debitDocDetails) {
                            List<CustChargeDetails> custChargeDetails = custChargeRepository.findAllByCustomerAndChargeidAndIsDeletedIsFalse(debitDocument.getCustomer(), debitDocDet.getChargeid());
                            if (!CollectionUtils.isEmpty(custChargeDetails)) {
                                custChargeDetails = custChargeDetails.stream().peek(custChargeDetails1 -> custChargeDetails1.setIsDeleted(true)).collect(Collectors.toList());
                                custChargeRepository.saveAll(custChargeDetails);
                            }
                        }
                    }

                    if (!Objects.equals(debitDocument.getCustomer().getPartner().getId(), CommonConstants.DEFAULT_PARTNER_ID)) {
                        QPartnerLedgerDetails partnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
                        List<PartnerLedgerDetails> partnerLedgerDetailsList = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAll(partnerLedgerDetails.debitDocId.eq(Long.valueOf(debitDocument.getId())));
                        double refundAmountForPartner = 0;
                        for (PartnerLedgerDetails details : partnerLedgerDetailsList) {
                            if (details.getCommission() > 0) {
                                refundAmountForPartner = refundAmountForPartner + details.getCommission();
                            }
                        }
                        if (refundAmountForPartner != 0) {
                            PartnerLedgerDetails reverseCommission = new PartnerLedgerDetails();
                            reverseCommission.setAmount(refundAmountForPartner);
                            reverseCommission.setDebitDocId(debitDocument.getId().longValue());
                            reverseCommission.setTranstype("DR");
                            reverseCommission.setCustid(debitDocument.getCustomer().getId());
                            reverseCommission.setPartner(debitDocument.getCustomer().getPartner());
                            reverseCommission.setIsDeleted(false);
                            reverseCommission.setCreateDate(LocalDateTime.now());
                            reverseCommission.setDescription("Commission reverted as the invoice " + debitDocument.getDocnumber() + " got void .");
                            reverseCommission.setTranscategory("Revert Commission");
                            partnerLedgerDetailsRepository.save(reverseCommission);
                        }
                    }
                }
                if (lastInvoice.get(0).getIsDirectChargeInvoice()) {
                    dbrService.removedbrByCPRStartDate(Long.valueOf(lastInvoice.get(0).getId()), lastInvoice.get(0).getStartdate().toLocalDate(), lastInvoice.get(0).getEndate().toLocalDate());
                    dbrService.removeDbrByCPRStartDateAtChargeLevel(Long.valueOf(lastInvoice.get(0).getId()), lastInvoice.get(0).getStartdate().toLocalDate(), lastInvoice.get(0).getEndate().toLocalDate());
                }
                List<CustPlanMappping> mapppings = custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getDebitdocid().intValue() == invoiceId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(mapppings)) {
                    for (CustPlanMappping mapping : mapppings) {
//                            mapping.setIsDelete(true);
                        mapping.setIsVoid(Boolean.TRUE);
                        mapping.setEndDate(LocalDateTime.now());
                        mapping.setExpiryDate(LocalDateTime.now());
                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
                            mapping.setStartDate(LocalDateTime.now());
                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
                        }
                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                        custPlanMappingService.save(mapping, "");
                        Optional<DebitDocument> debitDocument = debitDocRepository.findById(mapping.getDebitdocid().intValue());
                        if (debitDocument.isPresent()) {
                            dbrService.removedbrByCPRStartDate(mapping.getDebitdocid(), debitDocument.get().getStartdate().toLocalDate(), debitDocument.get().getEndate().toLocalDate());
                            dbrService.removeDbrByCPRStartDateAtChargeLevel(mapping.getDebitdocid(), debitDocument.get().getStartdate().toLocalDate(), debitDocument.get().getEndate().toLocalDate());
                        }
                    }
                    ezBillServiceUtility.deactivateService(mapppings, 13);
                }

                List<Integer> cprIds = mapppings.stream().map(x -> x.getId()).distinct().collect(Collectors.toList());
                if (cprIds != null && !cprIds.isEmpty()) {
                    cprIds.stream().forEach(cprId -> {
                        List<CustomerChargeHistory> chargeHistories = customerChargeHistoryRepo.findAllChargesByCprId(cprId);
                        if (chargeHistories != null && !chargeHistories.isEmpty()) {
                            chargeHistories.stream().forEach(data -> {
                                Optional<Charge> charge = chargeRepository.findById(data.getChargeId());
                                if (charge.isPresent() && charge.get().getChargetype().equalsIgnoreCase(CommonConstants.CHARGE_TYPE_NONRECURRING) && data.getIsFirstChargeApply()) {
                                    customerChargeHistoryRepo.delete(data);
                                }
                            });
                        }
                    });
                }

                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage("Invoice voided successfully.");

            } else {
                genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
                genericDataDTO.setResponseMessage("This invoice can not be void.");
                return genericDataDTO;
            }
            //remove ledger details entry
            QCustomerLedgerDtls qCustomerLedgerDtls = QCustomerLedgerDtls.customerLedgerDtls;
            BooleanExpression exp = qCustomerLedgerDtls.isNotNull();
            exp = exp.and(qCustomerLedgerDtls.debitdocid.eq(invoiceId)).and(qCustomerLedgerDtls.customer.id.eq(debitDocumentForCustomer.getCustomer().getId()));
            Optional<CustomerLedgerDtls> customerLedgerDtls = customerLedgerDtlsRepository.findOne(exp);
            if (customerLedgerDtls.isPresent()) {
                customerLedgerDtls.get().setIsVoid(true);
                customerLedgerDtls.get().setIsDelete(true);
                customerLedgerDtlsRepository.save(customerLedgerDtls.get());

                QCustomerLedger qCustomerLedger = QCustomerLedger.customerLedger;
                BooleanExpression expression = qCustomerLedger.isNotNull();
                expression = expression.and(qCustomerLedger.customer.id.eq(customerLedgerDtls.get().getCustomer().getId()));
                Optional<CustomerLedger> customerLedger = customerLedgerRepository.findOne(expression);
                if (customerLedger.isPresent()) {
                    customerLedger.get().setTotaldue(customerLedger.get().getTotaldue() - customerLedgerDtls.get().getAmount());
                    customerLedgerRepository.save(customerLedger.get());
                }
            }

        } catch (Exception e) {
            ApplicationLogger.logger.error("[CreditDocService]" + e.getMessage(), e);
            e.printStackTrace();
            return genericDataDTO;
        }
        return genericDataDTO;
    }


    private void voidLcoPartnerLedgerAndRevertCommission(Integer invoiceId) {
        Optional<DebitDocument> document = debitDocRepository.findById(invoiceId);
        if (document.isPresent()) {
            QPartnerLedgerDetails partnerLedgerDetails = QPartnerLedgerDetails.partnerLedgerDetails;
            BooleanExpression expression = partnerLedgerDetails.isNotNull().and(partnerLedgerDetails.debitDocId.eq(invoiceId.longValue()));
            List<PartnerLedgerDetails> details = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAll(expression);
            if (!CollectionUtils.isEmpty(details)) {
                Double commission = details.stream().mapToDouble(x -> x.getAmount()).sum();
                Partner partner = document.get().getCustomer().getPartner();
                partner.setCommrelvalue(partner.getCommrelvalue() - commission);
                partnerRepository.save(partner);
                details.stream().forEach(x -> {
                    x.setIsDeleted(true);
                    partnerLedgerDetailsRepository.save(x);
                });
            }
        }
    }


    private void voidBilltoSubisuInvoice(Integer invoiceId, String invoiceCancelRemark,Integer mvnoId) {
        try {
            List<CustPlanMappping> custPlanMapppings = custPlanMappingRepository.findAllByDebitdocid(invoiceId);
            List<Integer> cprids = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
            if (cprids.size() > 0) {
                for (Integer CustRefId : cprids) {
                    List<Integer> subisinvoiceId = custPlanMappingRepository.findAllByCustRefId(Collections.singleton(CustRefId));
                    if (subisinvoiceId.size() > 0) {
                        for (Integer id : subisinvoiceId) {
                            voidInvoice(id, invoiceCancelRemark,mvnoId);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GenericDataDTO getTaxDetailsOfInvoice(Integer invoiceId) {
        Map<String, String> dataMap = new HashMap<>();

        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        genericDataDTO.setData();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Data fetched successfully.");

        return genericDataDTO;

    }

    public List<DebitDocument> getAllByCustomerForCreditNote(Integer customerid) {
        Customers customer =  customersRepository.findById(customerid).get();
        List<DebitDocument> list = IterableUtils.toList(entityRepository.pendingDebitDocumentList(customerid));
        List<Integer> ids = list.stream().map(x -> x.getCustpackrelid()).collect(Collectors.toList());
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustPlanStatusAndIdIn("STOP", ids);
        List<Integer> custPlanIds = custPlanMapppingList.stream().map(x -> x.getId()).collect(Collectors.toList());
        list.removeIf(debitDocument -> custPlanIds.contains(debitDocument.getCustpackrelid()));
        list.removeIf(i -> i.getBillrunstatus().equalsIgnoreCase("Cancelled"));
//        list.removeIf(item -> {
//            Double pendingAmt = creditDocRepository.findTotalPendingAmountByDebitDocIdforCN(item.getId());
//           if(pendingAmt!=null) {
//               return (pendingAmt.equals(0.0));
//           }else {
//               return false;
//           }
//        });
        return list;
    }

    public Customers stopBilling(Integer custId) {

        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (!customers.isPresent()) {
                throw new RuntimeException("Customer not available!");
            }
            List<Integer> list = new ArrayList<>();
            list.add(custId);
            if (customers.get().getParentCustomers() == null) {
                List<Customers> customersList = customersService.getAllChildCustomerList(custId, customers.get().getInvoiceType());
                if (!CollectionUtils.isEmpty(customersList)) {
                    List<Integer> childCustList = customersList.stream().map(Customers::getId).collect(Collectors.toList());
                    list.addAll(childCustList);
                }
            }
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.in(list)).and(qCustPlanMappping.endDate.after(LocalDateTime.now())).and(qCustPlanMappping.isinvoicestop.eq(false)).and(qCustPlanMappping.isInvoiceCreated.ne(true));
            List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(planMappping -> planMappping.setIsinvoicestop(true));
                custPlanMappingRepository.saveAll(custPlanMapppings);
            }
            customers.get().setIsinvoicestop(true);
            customersRepository.save(customers.get());
            return customers.get();
        } catch (Exception ex) {
            throw new RuntimeException("Exception at start billing: " + ex.getMessage());
        }
    }

    public Customers startBilling(Integer custId) {
        try {
            Optional<Customers> customers = customersRepository.findById(custId);
            if (!customers.isPresent()) {
                throw new RuntimeException("Customer not available!");
            }
            List<Integer> list = new ArrayList<>();
            list.add(custId);
            if (customers.get().getParentCustomers() == null) {
                List<Customers> customersList = customersService.getAllChildCustomerList(custId, customers.get().getInvoiceType());
                if (!CollectionUtils.isEmpty(customersList)) {
                    List<Integer> childCustList = customersList.stream().map(Customers::getId).collect(Collectors.toList());
                    list.addAll(childCustList);
                }
            }
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.in(list)).and(qCustPlanMappping.isinvoicestop.eq(true)).and(qCustPlanMappping.isInvoiceCreated.eq(false));
            List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(planMappping -> planMappping.setIsinvoicestop(false));
                custPlanMappingRepository.saveAll(custPlanMapppings);
            }
            customers.get().setIsinvoicestop(false);
            customersRepository.save(customers.get());

            return customers.get();
        } catch (Exception ex) {
            throw new RuntimeException("Exception at start billing: " + ex.getMessage());
        }
    }

    public void createInvoiceAfterStartBilling(Customers customer) throws InterruptedException {
        Thread.sleep(2000);
        QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
        BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(customer.getId())).and(qCustPlanMappping.isInvoiceCreated.eq(false));
        List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
        if (custPlanMapppings.stream().filter(custPlanMappping -> custPlanMappping.getPlanGroup() != null).findAny().isPresent()) {
            List<CustPlanMappping> list = custPlanMapppings.stream().filter(UtilsCommon.distinctByKey(CustPlanMappping::getStartDate)).collect(Collectors.toList());
            for (CustPlanMappping custPlanMappping : list) {
                customer.setBillRunCustPackageRelId(custPlanMappping.getId());
                CustPlanMappping mappings = custPlanMappingRepository.findById(custPlanMappping.getId());
                String invoiceType = null;
                if (mappings != null)
                    invoiceType = mappings.getInvoiceType();
                Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService, "", null, invoiceType);
                Thread invoiceThread = new Thread(invoiceRunnable);
                invoiceThread.start();
            }
        } else {
            for (CustPlanMappping custPlanMappping : custPlanMapppings) {
                customer.setBillRunCustPackageRelId(custPlanMappping.getId());
                CustPlanMappping mappings = custPlanMappingRepository.findById(custPlanMappping.getId());
                String invoiceType = null;
                if (mappings != null)
                    invoiceType = mappings.getInvoiceType();
                Runnable invoiceRunnable = new InvoiceThread(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")), customer, customersService, "", null, invoiceType);
                Thread invoiceThread = new Thread(invoiceRunnable);
                invoiceThread.start();
            }
        }

    }

//    public void sendOrganizatonBillForApprovAL(Long invoiceId, Integer loggedInUserId) {
//        if (invoiceId != null) {
//            StaffUser assignedStaff = new StaffUser();
//            StaffUser loggedInUser = staffUserService.get(loggedInUserId);
//            DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
//            if (debitDocument != null) {
//                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
//                CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
//                OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
//                organizationBillDTO.setDebitDocument(debitDocument);
//                if(actualCustomerPlanMapping!=null) {
//                    organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());
//                }
//                debitDocument.setStatus(CommonConstants.PAYMENT_STATUS_PENDDING);
//              //  Boolean planapprove = debitDocument.getPostpaidPlan().getRequiredApproval();
//                Boolean planapprove=true;
//                if (custPlanMappping.getPlanGroup() != null)
//                    planapprove = custPlanMappping.getPlanGroup().getRequiredApproval();
//                if (planapprove) {
//                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
//                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, false, true, organizationBillDTO);
//                        if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
//                            debitDocument.setNextStaff(loggedInUserId);
//                            debitDocument.setNextTeamHierarchyMappingId(loggedInUserId);
//                            String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
//                            hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action);
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
//                        } else {
//                            assignedStaff = staffUserService.get(Integer.valueOf(map.get("staffId")));
//                            debitDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId")));
//                            debitDocument.setNextStaff(Integer.valueOf(map.get("staffId")));
//                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + assignedStaff.getUsername());
//                        }
//                    } else {
////                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(getLoggedInUserId(), organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, false, true, organizationBillDTO);
////                        if (map.containsKey("assignableStaff") && map.containsKey("nextTeamHierarchyMappingId")) {
////                            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//                        debitDocument.setNextTeamHierarchyMappingId(null);
//                        debitDocument.setNextStaff(loggedInUser.getId());
////                            for (StaffUserPojo staffUserPojo : staffUserPojos) {
////                                DebitDocumentStaffAssignMapping debitDocumentStaffAssignMapping = new DebitDocumentStaffAssignMapping();
////                                debitDocumentStaffAssignMapping.setDebitDocId(debitDocument.getId());
////                                debitDocumentStaffAssignMapping.setStaffId(staffUserPojo.getId());
////                                debitDocStaffAssignRepo.save(debitDocumentStaffAssignMapping);
//                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
//                        String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
//                        hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action);
////                            }
////                        } else {
////                            debitDocument.setNextStaff(loggedInUserId);
////                            debitDocument.setNextTeamHierarchyMappingId(null);
////                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
////                        }
//                    }
//                } else {
////                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
////                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
//               //     CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_CATEGORY_PAYMENT, "Automatic Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);
//                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount());
//                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
//                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
//                }
//            }
//
//            debitDocRepository.save(debitDocument);
//
//        }
//    }
    public void sendOrganizatonBillForApprovAL(Long invoiceId, Integer loggedInUserId,DebitDocument debitDocument) {
        if (invoiceId != null) {
            StaffUser assignedStaff = new StaffUser();
            StaffUser loggedInUser = staffUserService.get(loggedInUserId,getMvnoIdFromCurrentStaff(debitDocument.getCustomer().getMvnoId()));
            debitDocument.setCustomer(customersRepository.findById(1).get());
            if (debitDocument != null) {
                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
                CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
                OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
                organizationBillDTO.setDebitDocument(debitDocument);
                if(actualCustomerPlanMapping!=null) {
                    organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());
                }
                debitDocument.setStatus(CommonConstants.PAYMENT_STATUS_PENDDING);
                //  Boolean planapprove = debitDocument.getPostpaidPlan().getRequiredApproval();
                Boolean planapprove=true;
               if (custPlanMappping.getPlanGroup() != null)
                    planapprove = custPlanMappping.getPlanGroup().getRequiredApproval();
                if (planapprove) {
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, false, true, organizationBillDTO);
                        if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                            debitDocument.setNextStaff(loggedInUserId);
                            debitDocument.setNextTeamHierarchyMappingId(loggedInUserId);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
                            hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
                        } else {
                            assignedStaff = staffUserService.get(Integer.valueOf(map.get("staffId")),getMvnoIdFromCurrentStaff(debitDocument.getCustomer().getMvnoId()));
                            debitDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId")));
                            debitDocument.setNextStaff(Integer.valueOf(map.get("staffId")));
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + assignedStaff.getUsername());
                        }
                    } else {
//                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(getLoggedInUserId(), organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, false, true, organizationBillDTO);
//                        if (map.containsKey("assignableStaff") && map.containsKey("nextTeamHierarchyMappingId")) {
//                            List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
                        debitDocument.setNextTeamHierarchyMappingId(null);
                        debitDocument.setNextStaff(loggedInUser.getId());
//                            for (StaffUserPojo staffUserPojo : staffUserPojos) {
//                                DebitDocumentStaffAssignMapping debitDocumentStaffAssignMapping = new DebitDocumentStaffAssignMapping();
//                                debitDocumentStaffAssignMapping.setDebitDocId(debitDocument.getId());
//                                debitDocumentStaffAssignMapping.setStaffId(staffUserPojo.getId());
//                                debitDocStaffAssignRepo.save(debitDocumentStaffAssignMapping);
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
                        hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
//                            }
//                        } else {
//                            debitDocument.setNextStaff(loggedInUserId);
//                            debitDocument.setNextTeamHierarchyMappingId(null);
//                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + "\n" + "Assigned to :- " + loggedInUser.getUsername());
//                        }
                    }
                } else {
                    debitDocument.setNextStaff(loggedInUserId);
//                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
//                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                    CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_CATEGORY_PAYMENT, "Automatic Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);
                    debitDocument.setAdjustedAmount(creditDocumentForOrg.getAmount());
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                    debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                }
            }
            debitDocument.setNextStaff(loggedInUserId);
            debitDocRepository.save(debitDocument);
            AppproveOrgInvoiceMessage appproveOrgInvoiceMessage=new AppproveOrgInvoiceMessage();
            appproveOrgInvoiceMessage.setDebitdocId(debitDocument.getId());
//            messageSender.send(appproveOrgInvoiceMessage,RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE);
            kafkaMessageSender.send(new KafkaMessageData(appproveOrgInvoiceMessage,AppproveOrgInvoiceMessage.class.getSimpleName()));

        }
    }

    @Transactional
    public GenericDataDTO approveDebitDoc(Long invoiceId, boolean isApproveRequest, String remark,Integer mvnoId) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),mvnoId);
        StaffUser assignedStaff = new StaffUser();
        if (invoiceId != null) {
            DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
            if (debitDocument != null) {
                QCustPlanMappping qMapping = QCustPlanMappping.custPlanMappping;
                BooleanExpression expression = qMapping.isNotNull().and(qMapping.debitdocid.eq(invoiceId));
                List<CustPlanMappping> customerPlanMappingsForOrg = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);

                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
                CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());

                OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
                organizationBillDTO.setDebitDocument(debitDocument);
                organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());

                StaffUser assignedUser = null;
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                    if (getLoggedInUserId() != debitDocument.getNextStaff()) {
                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "You are not eligible for approval", null);
                    }
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, organizationBillDTO);
                    if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                        debitDocument.setNextStaff(null);
                        debitDocument.setNextTeamHierarchyMappingId(null);
                        if (!isApproveRequest) {
                            CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                            debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                            debitDocRepository.save(debitDocument);

                            Long debitDocIds = actualCustomerPlanMapping.getDebitdocid();
                            List<CustPlanMappping> custPlanMappping1 = new ArrayList<>();
                            if (debitDocIds != null) {
                                custPlanMappping1 = custPlanMappingRepository.findAllByDebitdocid(debitDocIds.intValue());
                                Boolean flag = true;
                                if (custPlanMappping1.size() > 0) {
                                    long startTime = System.currentTimeMillis();
                                    for (CustPlanMappping custplanids : custPlanMappping1) {
                                        Optional<DebitDocument> customerDebitDoc = debitDocRepository.findById(custplanids.getDebitdocid().intValue());
                                        if (customerDebitDoc.isPresent() && flag) {
                                            CreditDocument creditDocumentForCust = createCreditNote(customerDebitDoc.get(), CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                                            customerDebitDoc.get().setAdjustedAmount(creditDocumentForCust.getAmount());
                                            debitDocRepository.save(customerDebitDoc.get());
                                            customerDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                                            customerDebitDoc.get().setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                            flag = false;
                                        }

                                        custplanids.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                        custplanids.setStartDate(LocalDateTime.now());
                                        custplanids.setEndDate(LocalDateTime.now());
                                        custplanids.setExpiryDate(LocalDateTime.now());
                                        custPlanMappingRepository.save(custplanids);
                                    }
                                    long endTime = System.currentTimeMillis();
                                    long totalTime = endTime - startTime;

                                    System.out.println("API call took " + totalTime + " milliseconds.");
                                }
                            } else {
                                if (customerPlanMappingsForOrg.size() > 0) {
                                    List<Integer> cprIds = customerPlanMappingsForOrg.stream().map(x -> x.getCustomerCpr()).collect(Collectors.toList());
                                    qMapping = QCustPlanMappping.custPlanMappping;
                                    expression = qMapping.isNotNull().and(qMapping.id.in(cprIds));
                                    List<CustPlanMappping> customerPlanMappingsForCustomer = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);

                                    for (CustPlanMappping mappping : customerPlanMappingsForCustomer) {
                                        mappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                        mappping.setStartDate(LocalDateTime.now());
                                        mappping.setEndDate(LocalDateTime.now());
                                        mappping.setExpiryDate(LocalDateTime.now());
                                        custPlanMappingRepository.save(mappping);
                                    }
                                }
                            }

                            debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.REJCTED);
                            debitDocRepository.save(debitDocument);
                        } else {
                            CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_CATEGORY_PAYMENT, "Automatic Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);
                            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                            debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                        }
                    } else {
                        assignedStaff = staffUserService.get(Integer.valueOf(map.get("staffId")),mvnoId);
                        assignedUser = assignedStaff;
                        debitDocument.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId")));
                        debitDocument.setNextStaff(Integer.valueOf(map.get("staffId")));
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), assignedStaff.getId(), assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + "Assigned to :- " + assignedStaff.getUsername());
                    }

                    if (map.containsKey("nextTatMappingId")) {
                        if (assignedUser.getStaffUserparent() != null && !CollectionUtils.isEmpty(map)) {
                            if (map.get("current_tat_id") != null && map.get("current_tat_id") != "null")
                                map.put("tat_id", map.get("current_tat_id"));
                            tatUtils.saveOrUpdateDataForTatMatrix(map, assignedUser, debitDocument.getId(), null);
                        }
                    }
                } else {
                    if (!isApproveRequest && debitDocument.getNextTeamHierarchyMappingId() == null) {
                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId());
                        debitDocument.setNextStaff(null);
                        debitDocument.setNextTeamHierarchyMappingId(null);
                        // CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                        debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                        debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocRepository.save(debitDocument);


                        Long debitDocIds = actualCustomerPlanMapping.getDebitdocid();
                        List<CustPlanMappping> custPlanMappping1 = new ArrayList<>();
                        if (debitDocIds != null) {
                            custPlanMappping1 = custPlanMappingRepository.findAllByDebitdocid(debitDocIds.intValue());
                            Boolean flag = true;
                            if (custPlanMappping1.size() > 0) {
                                for (CustPlanMappping custplanids : custPlanMappping1) {
                                    Optional<DebitDocument> customerDebitDoc = debitDocRepository.findById(custplanids.getDebitdocid().intValue());
                                    if (customerDebitDoc.isPresent() && flag) {
                                        if(customerDebitDoc.get().getCreatedate()==null){
                                            customerDebitDoc.get().setCreatedate(LocalDateTime.now());
                                        }
                                        CreditDocument creditDocumentForCust = createCreditNote(customerDebitDoc.get(), CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                                        //customerDebitDoc.get().setAdjustedAmount(creditDocumentForCust.getAmount());
                                        debitDocRepository.save(customerDebitDoc.get());
                                        customerDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                                        customerDebitDoc.get().setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                        flag = false;
                                    }

                                    custplanids.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                    custplanids.setStartDate(LocalDateTime.now());
                                    custplanids.setEndDate(LocalDateTime.now());
                                    custplanids.setExpiryDate(LocalDateTime.now());
                                    custPlanMappingRepository.save(custplanids);
                                }
                            }
                        } else {
                            if (customerPlanMappingsForOrg.size() > 0) {
                                List<Integer> cprIds = customerPlanMappingsForOrg.stream().map(x -> x.getCustomerCpr()).collect(Collectors.toList());
                                qMapping = QCustPlanMappping.custPlanMappping;
                                expression = qMapping.isNotNull().and(qMapping.id.in(cprIds));
                                List<CustPlanMappping> customerPlanMappingsForCustomer = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);

                                for (CustPlanMappping mappping : customerPlanMappingsForCustomer) {
                                    mappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                    mappping.setStartDate(LocalDateTime.now());
                                    mappping.setEndDate(LocalDateTime.now());
                                    mappping.setExpiryDate(LocalDateTime.now());
                                    custPlanMappingRepository.save(mappping);
                                }
                            }
                        }
                        debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.REJCTED);
                        debitDocRepository.save(debitDocument);
                    } else {
                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, debitDocument.getNextTeamHierarchyMappingId() == null, organizationBillDTO);
                        if (map.containsKey("assignableStaff") && map.containsKey("nextTeamHierarchyMappingId")) {
                            genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + (isApproveRequest ? "Approve" : "Rejected") + " by :- " + loggedInUser.getUsername());
                        } else {
                            debitDocument.setNextStaff(null);
                            debitDocument.setNextTeamHierarchyMappingId(null);
                            if (!isApproveRequest) {
                               // CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                                debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                debitDocRepository.save(debitDocument);


                                Long debitDocIds = actualCustomerPlanMapping.getDebitdocid();
                                List<CustPlanMappping> custPlanMappping1 = new ArrayList<>();
                                if (debitDocIds != null) {
                                    custPlanMappping1 = custPlanMappingRepository.findAllByDebitdocid(debitDocIds.intValue());
                                    Boolean flag = true;
                                    if (custPlanMappping1.size() > 0) {
                                        for (CustPlanMappping custplanids : custPlanMappping1) {
                                            Optional<DebitDocument> customerDebitDoc = debitDocRepository.findById(custplanids.getDebitdocid().intValue());
                                            if (customerDebitDoc.isPresent() && flag) {
                                               CreditDocument creditDocumentForCust = createCreditNote(customerDebitDoc.get(), CommonConstants.TRANS_CREDIT_NOTE, CommonConstants.TRANS_CREDIT_NOTE, "Automatic CreditNote for business promotion invoice..", CommonConstants.PAYMENT_MODE.CREDIT_NOTE1, false);
                                                customerDebitDoc.get().setAdjustedAmount(debitDocument.getAdjustedAmount());
                                                debitDocRepository.save(customerDebitDoc.get());
                                                customerDebitDoc.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
                                                customerDebitDoc.get().setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                                                flag = false;
                                            }

                                            custplanids.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                            custplanids.setStartDate(LocalDateTime.now());
                                            custplanids.setEndDate(LocalDateTime.now());
                                            custplanids.setExpiryDate(LocalDateTime.now());
                                            custPlanMappingRepository.save(custplanids);
                                        }
                                    }
                                } else {
                                    if (customerPlanMappingsForOrg.size() > 0) {
                                        List<Integer> cprIds = customerPlanMappingsForOrg.stream().map(x -> x.getCustomerCpr()).collect(Collectors.toList());
                                        qMapping = QCustPlanMappping.custPlanMappping;
                                        expression = qMapping.isNotNull().and(qMapping.id.in(cprIds));
                                        List<CustPlanMappping> customerPlanMappingsForCustomer = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);

                                        for (CustPlanMappping mappping : customerPlanMappingsForCustomer) {
                                            mappping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                                            mappping.setStartDate(LocalDateTime.now());
                                            mappping.setEndDate(LocalDateTime.now());
                                            mappping.setExpiryDate(LocalDateTime.now());
                                            custPlanMappingRepository.save(mappping);
                                        }
                                    }
                                }
                                debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.REJCTED);
                                debitDocRepository.save(debitDocument);
                            } else {
                             //   CreditDocument creditDocumentForOrg = createCreditNote(debitDocument, "invoice", CommonConstants.TRANS_CATEGORY_PAYMENT, "Automatic Payment for business promotion invoice..", CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION, false);
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                                debitDocument.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                                debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                            }
                        }
                    }
                }
                genericDataDTO.setResponseCode(HttpStatus.OK.value());
                genericDataDTO.setResponseMessage(isApproveRequest ? "Approved successfully" : "Rejected Successfully");
                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + (isApproveRequest ? "Approve" : "Rejected") + " by :- " + loggedInUser.getUsername());
                debitDocRepository.save(debitDocument);
                AppproveOrgInvoiceMessage appproveOrgInvoiceMessage=new AppproveOrgInvoiceMessage();
                appproveOrgInvoiceMessage.setDebitdocId(debitDocument.getId());
                appproveOrgInvoiceMessage.setIsApproveRequest(isApproveRequest);
//                messageSender.send(appproveOrgInvoiceMessage,RabbitMqConstants.QUEUE_APPROVE_ORG_INVOICE_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(appproveOrgInvoiceMessage,AppproveOrgInvoiceMessage.class.getSimpleName()));

                if (debitDocument.getDocnumber() != null) {
                    logger.info("Successfully update invoice with number : - " + debitDocument.getDocnumber() + " for bill to organization..", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                } else {
                    logger.info("Successfully update invoice with Id: - " + debitDocument.getId() + " for bill to organization..", MODULE, genericDataDTO.getResponseCode(), genericDataDTO.getResponseMessage());
                }
            }
        }
        return genericDataDTO;
    }

    @Transactional
//    public GenericDataDTO assignEveryOneFromAvailableStaff(Long invoiceId, boolean isApproveRequest, String remark) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        StaffUser loggedInUser = staffUserService.get(getLoggedInUserId());
//        StaffUser assignedStaff = new StaffUser();
//        if (invoiceId != null) {
//            DebitDocument debitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
//            if (debitDocument != null) {
//                List<DebitDocumentStaffAssignMapping> debitDocumentStaffAssignMappings = debitDocStaffAssignRepo.findAllByDebitDocId(debitDocument.getId());
//                CustPlanMappping custPlanMappping = custPlanMappingRepository.findById(debitDocument.getCustpackrelid());
//                CustPlanMappping actualCustomerPlanMapping = custPlanMappingRepository.findById(custPlanMappping.getCustRefId());
//                OrganizationBillDTO organizationBillDTO = new OrganizationBillDTO();
//                organizationBillDTO.setDebitDocument(debitDocument);
//                organizationBillDTO.setActualCustomers(actualCustomerPlanMapping.getCustomer());
//                if (debitDocument.getNextStaff() != null && getLoggedInUserId() != debitDocument.getNextStaff()) {
//                    throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "You are not eligible for approval", null);
//                } else if (debitDocumentStaffAssignMappings.size() > 0) {
//                    if (debitDocumentStaffAssignMappings.stream().noneMatch(debitDocumentStaffAssignMapping -> debitDocumentStaffAssignMapping.getStaffId().equals(getLoggedInUserId()))) {
//                        throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "You are not eligible for approval", null);
//                    }
//                }
//                Map<String, Object> map = hierarchyService.getTeamForNextApprove(organizationBillDTO.getActualCustomers().getMvnoId(), organizationBillDTO.getActualCustomers().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, debitDocument.getNextTeamHierarchyMappingId() == null, organizationBillDTO);
//                if (map.containsKey("assignableStaff") && map.containsKey("nextTeamHierarchyMappingId")) {
//                    List<StaffUserPojo> staffUserPojos = (List<StaffUserPojo>) map.get("assignableStaff");
//                    debitDocument.setNextTeamHierarchyMappingId((Integer) map.get("nextTeamHierarchyMappingId"));
//                    debitDocStaffAssignRepo.deleteAllByDebitDocId(debitDocument.getId());
//                    for (int i = 0; i <= staffUserPojos.size(); i++) {
//                        DebitDocumentStaffAssignMapping debitDocumentStaffAssignMapping = new DebitDocumentStaffAssignMapping();
//                        debitDocumentStaffAssignMapping.setDebitDocId(debitDocument.getId());
//                        debitDocumentStaffAssignMapping.setStaffId(staffUserPojos.get(i).getId());
//                        debitDocStaffAssignRepo.save(debitDocumentStaffAssignMapping);
//                        String action = CommonConstants.WORKFLOW_MSG_ACTION.BILL_TO_ORGANIZATION + " ' " + organizationBillDTO.getDebitDocument().getDocnumber() + " '";
//                        hierarchyService.sendWorkflowAssignActionMessage(staffUserPojos.get(i).getCountryCode(), staffUserPojos.get(i).getPhone(), staffUserPojos.get(i).getEmail(), staffUserPojos.get(i).getMvnoId(), staffUserPojos.get(i).getFullName(), action);
//
//                    }
//                } else {
//                    debitDocument.setNextStaff(null);
//                    debitDocument.setNextTeamHierarchyMappingId(null);
//                    if (!isApproveRequest) {
//                        Customers customers = actualCustomerPlanMapping.getCustomer();
//                        customers.setStatus(SubscriberConstants.IN_ACTIVE);
//                        customersRepository.save(customers);
//                    }
//                }
//
//                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.BILL_TO_ORGANIZATION, debitDocument.getId(), debitDocument.getCustRefName(), loggedInUser.getId(), loggedInUser.getUsername(), isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remark + "\n" + (isApproveRequest ? "Approve" : "Rejected") + " by :- " + loggedInUser.getUsername());
//                debitDocRepository.save(debitDocument);
//
//            }
//            genericDataDTO.setData(debitDocument);
//        }
//        return genericDataDTO;
//    }

//    @Transactional
//    public DebitDocument cancelAndRegenerateInvoice(Integer debitDocId, String invoiceCancelRemarks) {
//        try {
//            Random rnd = new Random();
//            int renewalId = rnd.nextInt(999999);
//            if (isPaymentPendingSent(debitDocId)) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Cancel And Regenaration not allowed as Payment is Pending for Approval!", null);
//            }
//            DebitDocument debitDocument = debitDocRepository.findById(debitDocId).orElse(null);
//            QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
//            JPAQuery<DebitDocument> debitDocumentJPAQuery = new JPAQuery<>(entityManager);
//            List<DebitDocument> lastInvoice = debitDocumentJPAQuery.from(qDebitDocument).where(qDebitDocument.customer.id.eq(debitDocument.getCustomer().getId()).and(qDebitDocument.billrunstatus.eq("Generated").or(qDebitDocument.billrunstatus.eq("Exported")))).orderBy(qDebitDocument.id.desc()).limit(1).fetch();
//            List<DebitDocument> debitDocumentList = new ArrayList<>();
//            if (lastInvoice.size() > 0 && !lastInvoice.contains(debitDocument)) {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only latest invoice can be regeneate and cancelled", null);
//            }
//            if (debitDocument == null) {
//                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invalid debit doc id: " + debitDocId, null);
//            }
//            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
//            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.debitdocid.eq(Long.valueOf(debitDocId)));
//            List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
//            QCustomerChargeHistory qCustomerChargeHistory = QCustomerChargeHistory.customerChargeHistory;
//            List<Integer> custPlanIds = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
//            BooleanExpression booleanExpression = qCustomerChargeHistory.isNotNull().and(qCustomerChargeHistory.custPlanMapppingId.in(custPlanIds));
//            List<CustomerChargeHistory> customerChargeHistories = IterableUtils.toList(customerChargeHistoryRepo.findAll(booleanExpression));
//            if (customerChargeHistories.size() > 0) {
//                customerChargeHistories.forEach(customerChargeHistory -> {
//                    customerChargeHistory.setIsFirstChargeApply(false);
//                    customerChargeHistoryRepo.save(customerChargeHistory);
//                });
//            }
//            List<CustChargeDetails> custChargeDetails = custChargeDetailsRepository.findAllByCustPlanMapppingIdIn(custPlanIds);
//
//            if (!CollectionUtils.isEmpty(custChargeDetails)) {
//                custChargeDetails.forEach(chargeDetails -> {
//                    chargeDetails.setIsUsed(false);
//                    chargeDetails.setDebitdocid(null);
//                });
//                custChargeDetailsRepository.saveAll(custChargeDetails);
//            }
//
//            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
//                custPlanMapppings.forEach(custPlanMappping -> {
//                    custPlanMappping.setDebitdocid(null);
//                    custPlanMappping.setIsInvoiceCreated(false);
//                    custPlanMappping.setRenewalId(renewalId);
//                });
//                custPlanMappingRepository.saveAll(custPlanMapppings);
//            }
//            debitDocument.setBillrunstatus("Cancelled");
//            Customers customers = debitDocument.getCustomer();
//            customers.setLastBillDate(LocalDate.now());
//            if (null != invoiceCancelRemarks) {
//                debitDocument.setInvoiceCancelRemarks(invoiceCancelRemarks);
//            }
//            return debitDocRepository.save(debitDocument);
//        } catch (CustomValidationException ex) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
//        } catch (RuntimeException ex) {
//            throw new RuntimeException("Exception at Cancel and Regenerate invoice for id: " + debitDocId + " ex: " + ex.getMessage());
//        }
//    }

    private boolean isPaymentPendingSent(Integer debitDocId) {
        try {
            List<Integer> creditDocids = new ArrayList<>();
            if (debitDocId != null) {
                QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
                BooleanExpression exp = qCreditDebitDocMapping.isNotNull().and(qCreditDebitDocMapping.debtDocId.eq(debitDocId));
                List<CreditDebitDocMapping> creditDebitDocMappings = (List<CreditDebitDocMapping>) creditDebtMappingRepository.findAll(exp);
                creditDocids = creditDebitDocMappings.stream().filter(i -> i.getCreditDocId() != null).map(i -> i.getCreditDocId()).collect(Collectors.toList());
                QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
                BooleanExpression exp2 = qCreditDocument.isNotNull().and(qCreditDocument.id.in(creditDocids)).and(qCreditDocument.status.equalsIgnoreCase("pending"));
                return creditDocRepository.exists(exp2);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public CreditDocument createCreditNote(DebitDocument debitDocument, String payType, String type, String remarks, String mode, boolean isDbrRequired) {
        List<CustomerChargeDBR> customerChargeDBRList = dbrService.findAllCustomerChargedbrByDebitDoc(debitDocument);
        Double pendingAmount = 0.0d;
        CreditDocument creditDocument = new CreditDocument();
        creditDocument.setAmount(debitDocument.getTotalamount());
        creditDocument.setCustomer(debitDocument.getCustomer());
        creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
        List<Integer> invoiceId = new ArrayList<>();
        invoiceId.add(debitDocument.getId());
        creditDocument.setInvoiceId(debitDocument.getId());
        creditDocument.setPaymentdate(LocalDate.now());
        creditDocument.setPaymode(mode);
        creditDocument.setPaytype(payType);
        creditDocument.setType(type);
        creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
        creditDocument.setIsDelete(false);
        creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
        creditDocument.setBuID(debitDocument.getCustomer().getBuId());
        creditDocument.setRemarks(remarks);
        creditDocument.setTdsamount(0d);
        creditDocument.setAbbsAmount(0d);
        creditDocument.setLcoid(debitDocument.getLcoId());
        creditDocument.setCreatedById(debitDocument.getCreatedById());
        creditDocument.setCreatedByName(debitDocument.getCreatedByName());
        if(creditDocument.getCreatedate()==null) {
            creditDocument.setCreatedate(LocalDateTime.now());
        }
        creditDocument.setXmldocument(this.creditDocService.assemblePaymentXML(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT,getMvnoIdFromCurrentStaff(customerChargeDBRList.get(0).getCustid().intValue())));
        if (type.equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_PAYMENT) && mode.equalsIgnoreCase(CommonConstants.PAYMENT_MODE.BUSINESS_PROMOTION)) {
//            String paymentNum = creditDocService.getPaymentInvoiceNo();
            Boolean isLCO = creditDocument.getLcoid() != null ? true :false;
            String paymentNum = numberSequenceUtil.getPaymentNumber(isLCO, creditDocument.getLcoid(), creditDocument.getMvnoId());
            if (paymentNum != null) {
                creditDocument.setCreditdocumentno(paymentNum);
            }
        } else {
//            creditDocument.setCreditdocumentno(creditDocService.getInvoiceNo());
            Boolean isLCO = debitDocument.getCustomer().getLcoId() != null ? true :false;
            creditDocument.setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, debitDocument.getCustomer().getLcoId(), debitDocument.getCustomer().getMvnoId()));
        }

        DecimalFormat df = new DecimalFormat("#.00");
        if (debitDocument != null) {
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByDebitdocidAndCustPlanStatus(debitDocument.getId(), CommonConstants.STOP_STATUS);
            List<Integer> custSerIds = custPlanMapppingList.stream().map(CustPlanMappping::getCustServiceMappingId).collect(Collectors.toList());
            List<Long> custServiceIds = custSerIds.stream().map(Integer::longValue).collect(Collectors.toList());
            pendingAmount = creditDocService.getRefundableAmountByService(custServiceIds);
            if (pendingAmount <= 0)
                pendingAmount = getPendingRevenueWithTaxAtCurrentDate(debitDocument);
            pendingAmount = Double.parseDouble(df.format(pendingAmount));
            creditDocument.setAmount(pendingAmount);
            creditDocument.setAdjustedAmount(pendingAmount);

            if(debitDocument.getInvoiceCancelRemarks()!=null)
            {
                pendingAmount = Double.parseDouble(df.format(debitDocument.getTotalamount()));
                creditDocument.setAmount(pendingAmount);
                creditDocument.setAdjustedAmount(pendingAmount);
            }
            
            if (debitDocument.getCustomer().getId().intValue() == 1 || debitDocument.getCustomer().getId().intValue() == 1) {
                if (debitDocument.getAdjustedAmount() != null)
                    creditDocument.setAmount(debitDocument.getTotalamount() - debitDocument.getAdjustedAmount());
                else
                    creditDocument.setAmount(debitDocument.getTotalamount());
                creditDocument.setAdjustedAmount(creditDocument.getAmount());
            }
        }
        try {
            //add adjusted amount against invoice
            if(creditDocument.getId()==null){
                creditDocument.setId(creditDocRepository.findlast()+1);
            }
            creditDocument = creditDocRepository.save(creditDocument);
            if (debitDocument.getAdjustedAmount() != null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + creditDocument.getAmount());
            else
                debitDocument.setAdjustedAmount(creditDocument.getAmount());
            debitDocRepository.save(debitDocument);
            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setId(creditDocRepository.findlast()+1);
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            creditDocService.addLedgeAfterApproval(creditDocument);
            dbrService.creditNoteDbrEntry(debitDocument, creditDocument.getAmount(), true);
            try {
                creditDocService.sendDataToNAV(Optional.of(creditDocument), debitDocument);
            } catch (Exception ex) {
                logger.error("Error in integration" + ex.getStackTrace());
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception when creating credit note for invoice: " + debitDocument.getId());
        }

        if (!CollectionUtils.isEmpty(customerChargeDBRList)) {
            try {
                creditDocService.setCreditNotdataToTable(customerChargeDBRList, creditDocument, debitDocument);
            } catch (Exception ex) {
                logger.error("Error while adding CN charge rel data: " + ex.getMessage());
            }
        }

        return creditDocument;
    }


    public void createInvoiceforDirectcharge(Customers customers, HashSet<Integer> oldDebitDocId, Integer paymentOwnerId) {

        List<CustChargeDetails> chargeDetailsList = custChargeRepository.findAllByCustomer(customers);
        chargeDetailsList = chargeDetailsList.stream().sorted(Comparator.comparing(CustChargeDetails::getId).reversed()).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(chargeDetailsList)) {
            CustChargeDetails custChargeDetails = chargeDetailsList.get(0);
            custChargeDetails.setIsUsed(false);
            List<Integer> list = new ArrayList<>();
            list.add(custChargeDetails.getId());
            Runnable chargeRunnable = new ChargeThread(customers.getId(), list, customersService, 0L, oldDebitDocId, paymentOwnerId, true, CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
            Thread invoiceThread = new Thread(chargeRunnable);
            invoiceThread.start();

//            if(customers.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)) {
//                Runnable chargeRunnable = new ChargeThread(customers.getId(), list, customersService, 0L,"",paymentOwnerId);
//                Thread invoiceThread = new Thread(chargeRunnable);
//                invoiceThread.start();
//            }

        }
    }

    public void createInvoice(Customers customers, HttpServletRequest req, Integer RESP_CODE, HashSet<Integer> oldDebitDocId,
                              String creditDocumentId, String paymentOwner, Integer paymentOwnerId, Boolean isCancelRegenerate, String type) {
        boolean isInvoiceCreated = false;
        try {

            // Generate Invoice
            if (null != customers.getPlanMappingList() && 0 < customers.getPlanMappingList().size() && !customers.getIstrialplan() && !customers.getIsinvoicestop()) {
//                Integer custPackRel = customers.getPlanMappingList().get(0).getId();//.stream().filter(custPlanMappping -> custPlanMappping.getDebitdocid().equals(invoiceId)).findAny().get().getId();
                customers.setBillRunCustPackageRelId(customers.getPlanMappingList().get(0).getId());
                CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
                List<CustPlanMappping> mappings = custPlanMappingRepository.findAllByCustomerId(customersPojo.getId());
                if (mappings != null && !mappings.isEmpty()) {
                    mappings = mappings.stream().filter(x -> !x.getIsInvoiceCreated()).collect(Collectors.toList());
                }

                customersPojo.setCustPackageId(mappings.stream().filter(custPlanMappping -> !custPlanMappping.getIsInvoiceCreated()).map(CustPlanMappping::getId).findFirst().get());
                customersPojo.setOldDebitDocId(oldDebitDocId);
                if (creditDocumentId != null && creditDocumentId.length() != 0) {
                    customersPojo.setCreditDocumentId(creditDocumentId);
                    customersPojo.setIsFromFlutterWave("true");
                }
                if (paymentOwner != null && paymentOwner.length() != 0) {
                    customersPojo.setPaymentOwner(paymentOwner);
                }
                if (paymentOwnerId != null) {
                    customersPojo.setPaymentOwnerId(paymentOwnerId);
                }

                if (customersPojo.getParentCustomers() != null) {


                    if (mappings != null && !mappings.isEmpty()) {
                        Boolean isGroup = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_GROUP)).collect(Collectors.toList()).size() > 0;
                        if (isGroup) {
                            Runnable chargeRunnable1 = new InvoiceCreationThread(customersPojo, customersService, CommonConstants.INVOICE_TYPE_GROUP, isCancelRegenerate, null, type);
                            Thread billchargeThread1 = new Thread(chargeRunnable1);
                            billchargeThread1.start();
                            isInvoiceCreated = true;
                        }

                        Boolean isIndependent = mappings.stream().filter(x -> x.getInvoiceType().equals(CommonConstants.INVOICE_TYPE_INDEPENDENT)).collect(Collectors.toList()).size() > 0;
                        if (isIndependent) {
                            Thread.sleep(2000);
                            Runnable chargeRunnable1 = new InvoiceCreationThread(customersPojo, customersService, CommonConstants.INVOICE_TYPE_INDEPENDENT, isCancelRegenerate, null, type);
                            Thread billchargeThread1 = new Thread(chargeRunnable1);
                            billchargeThread1.start();
                            isInvoiceCreated = true;
                        }
                    }
                } else {
                    Thread.sleep(2000);
                    Runnable invoiceRunnable = new InvoiceCreationThread(customersPojo, customersService, null, isCancelRegenerate, null, type);
                    Thread invoiceThread = new Thread(invoiceRunnable);
                    invoiceThread.start();
                    isInvoiceCreated = true;
                }
            }

            if (null != customers) {
                Runnable receiptRunnable = new ReceiptThread(billRunService, customers.getCreditDocuments());
                Thread receiptThread = new Thread(receiptRunnable);
                receiptThread.start();
            }

            // Generate Charge Invoice
            if (null != customers && null != customers.getOverChargeList() && 0 < customers.getOverChargeList().size() && !customers.getIstrialplan() && !customers.getIsinvoicestop()) {
                List<Integer> custChargeIdList = new ArrayList<>();
                customers.getOverChargeList().forEach(data -> custChargeIdList.add(data.getId()));
                Runnable chargeRunnable = new ChargeThread(customers.getId(), custChargeIdList, customersService, 0L, "", null);
                Thread billchargeThread = new Thread(chargeRunnable);
                billchargeThread.start();
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(MODULE + e.getStackTrace(), e);
//            logger.error("Unable to Create customer with name  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, null, e.getStackTrace());
            //	e.printStackTrace();
        }
        try {

            boolean invoice = false;

            List<CustPlanMappping> custPlanList = customers.getPlanMappingList();
            for (int i = 0; i < custPlanList.size(); i++) {
                CustPlanMappping custPlan = custPlanList.get(i);
                if (custPlan.getOfferPrice() > 0) {
                    invoice = true;
                }
            }

            if (invoice && !isInvoiceCreated && !customers.getIstrialplan() && !customers.getIsinvoicestop()) {
                CustomersPojo customersPojo = customerMapper.domainToDTO(customers, new CycleAvoidingMappingContext());
                Customers subscribe = customerMapper.dtoToDomain(customersPojo, new CycleAvoidingMappingContext());
                if (subscribe.getStatus().equalsIgnoreCase("Active")) {
                    //dbrService.addDbrForPrepaidCustomerCreation(subscribe.getId());

                    Runnable chargeRunnable = new InvoiceCreationThread(customersPojo, customersService, null, false, null, type);
                    Thread billchargeThread = new Thread(chargeRunnable);
                    billchargeThread.start();
//                	customersService.generatePrepaidInvoice(pojo);
                }
            }
        } catch (Exception e) {
//            logger.error("Unable to Create customer with name  :  request: { From : {}}; Response : {{}};Error :{} ;Exception:{}", req.getHeader("requestFrom"), RESP_CODE, null, e.getStackTrace());
            //e.printStackTrace();
        }
    }

    public GenericDataDTO getBillToOrgApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        try {
            PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
            QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
            BooleanExpression booleanExpression = qDebitDocument.isNotNull().and(qDebitDocument.isDelete.eq(false)).and(qDebitDocument.nextStaff.eq(getLoggedInUserId())).and(qDebitDocument.customer.id.eq(1));
            Page<DebitDocument> paginationList = debitDocRepository.findAll(booleanExpression, pageRequest);
            genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
                try {
                    return convertDebitDocumentModelToDebitDocumentPojo(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
            genericDataDTO.setTotalRecords(paginationList.getTotalElements());
            genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
            genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
            genericDataDTO.setTotalPages(paginationList.getTotalPages());
            return genericDataDTO;
        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.EXPECTATION_FAILED.value());
            genericDataDTO.setResponseMessage(HttpStatus.EXPECTATION_FAILED.getReasonPhrase());
            return genericDataDTO;

        }
    }

    @Transactional
    public void adjustOldDebitDocument(Long invoiceId, HashSet<Integer> oldDebitDocumentId) {
        List<DebitDocument> oldList = new ArrayList<>();
        for (Integer debitId : oldDebitDocumentId) {
            Optional<DebitDocument> oldDebitDocument = debitDocRepository.findById(debitId);
            if (oldDebitDocument.isPresent()) oldList.add(oldDebitDocument.get());
        }
        DebitDocument newDebitDocument = debitDocRepository.findById(invoiceId.intValue()).orElse(null);
        if (!CollectionUtils.isEmpty(oldList) && newDebitDocument != null) {
            List<Integer> creditDocumentList = new ArrayList<>();
            for (DebitDocument debitDocument : oldList) {
                boolean isDbrRequired = false;
                if (!Objects.equals(newDebitDocument.getCustpackrelid(), debitDocument.getCustpackrelid()) && !newDebitDocument.getCreatedate().toLocalDate().equals(debitDocument.getCreatedate().toLocalDate()))
                    isDbrRequired = true;

                List<CreditDebitDocMapping> debitDocMappingList = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                Double totalAdjustedAmount = debitDocMappingList.stream().filter(cdl -> cdl.getAdjustedAmount() != null && cdl.getDebtDocId().equals(debitDocument.getId())).mapToDouble(CreditDebitDocMapping::getAdjustedAmount).sum();
//                List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByDebitdocidAndCustPlanStatus(debitDocument.getId(), CommonConstants.STOP_STATUS);
                CreditDocument document = createCreditNote(debitDocument, "creditnote", CommonConstants.TRANS_CREDIT_NOTE, "Refund  paid amount against cancelled invoice " + debitDocument.getDocnumber() + "\n Payment adjusted :-" + debitDocument.getTotalamount(), "Credit Note", isDbrRequired);
                if (document.getAmount() <= debitDocument.getTotalamount()) {
                    debitDocument.setAdjustedAmount(document.getAmount());
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getTotalamount());
                }

                adjustBillToSubisuInvoiceWithCreditNote(debitDocument.getAdjustedAmount(), debitDocument,debitDocument.getCustomer().getMvnoId());

                if (debitDocument.getAdjustedAmount() < debitDocument.getTotalamount()) {
                    adjustOldInvoiceData(debitDocument, debitDocMappingList);
                }
                creditDocumentList = debitDocMappingList.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
                adjustPaymentWithNewInvoice(creditDocumentList, debitDocument.getTotalamount(), newDebitDocument, totalAdjustedAmount);
                creditDebtMappingRepository.deleteInBatch(debitDocMappingList);
                if (debitDocument.getAdjustedAmount() != null) {
                    if (debitDocument.getAdjustedAmount().equals(debitDocument.getTotalamount())) {
                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                        debitDocument.setBillrunstatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                    } else if (debitDocument.getAdjustedAmount() == 0d)
                        debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
                } else {
                    debitDocument.setAdjustedAmount(0.0);
                    debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
                }
                debitDocRepository.save(debitDocument);
            }
        }
    }

    public void adjustBillToSubisuInvoiceWithCreditNote(Double adjustedAmount, DebitDocument debitDocument,Integer mvnoId) {
        try {
            List<CustPlanMappping> custMappings = custPlanMappingRepository.findAllByDebitdocid(debitDocument.getId());
            if (!CollectionUtils.isEmpty(custMappings)) {
                List<Integer> cprIds = custMappings.stream().map(x -> x.getId()).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(cprIds)) {
                    List<CustPlanMappping> mapppings = custPlanMappingRepository.getAllCustPlanMappingByCustCPRList(cprIds);
                    if (!CollectionUtils.isEmpty(mapppings)) {
                        Long debitDocumentId = mapppings.get(0).getDebitdocid();
                        Optional<DebitDocument> subisuDocument = debitDocRepository.findById(debitDocumentId.intValue());
                        if (subisuDocument.isPresent()) {
                            Double newAdjustAmount = (adjustedAmount / debitDocument.getTotalamount()) * subisuDocument.get().getTotalamount();
                            if (newAdjustAmount > 0.0)
                                creditDocService.adjustCreditNoteForBillToSubisu(newAdjustAmount, subisuDocument.get(),mvnoId);
                        }
                    }
                }
            }
        }catch (Exception ex) {
            logger.error("Exception on adjust bill to organization amount: "+ex.getMessage());
        }
    }

    public void adjustOldInvoiceData(DebitDocument debitDocument, List<CreditDebitDocMapping> debitDocMappingList) {
        Double remainingAmount = debitDocument.getTotalamount();
        if (debitDocument.getAdjustedAmount() != null)
            remainingAmount = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
        int i = 0;
        int listSize = debitDocMappingList.size();

        while (remainingAmount > 0 && i < listSize) {
            Optional<CreditDocument> creditDocument = creditDocRepository.findById(debitDocMappingList.get(i).getCreditDocId());
            if (creditDocument.isPresent()) {
                Double cnTotalAMount = creditDocument.get().getAmount();
                if (cnTotalAMount <= remainingAmount) {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + cnTotalAMount);
                    creditDocument.get().setAdjustedAmount(cnTotalAMount);
                    creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    debitDocMappingList.get(i).setAdjustedAmount(debitDocument.getAdjustedAmount() + cnTotalAMount);
                } else {
                    debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount() + remainingAmount);
                    creditDocument.get().setAdjustedAmount(remainingAmount);
                    creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                    debitDocMappingList.get(i).setAdjustedAmount(debitDocument.getTotalamount() - remainingAmount);
                }
                CreditDebitDocMapping debitDocMapping = new CreditDebitDocMapping();
                debitDocMapping.setAdjustedAmount(creditDocument.get().getAdjustedAmount());
                debitDocMapping.setCreditDocId(creditDocument.get().getId());
                debitDocMapping.setDebtDocId(debitDocument.getId());

                if (creditDocument.get().getAmount() > creditDocument.get().getAdjustedAmount()) {
                    creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                } else if (creditDocument.get().getAdjustedAmount() == 0) {
                    creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                } else if (creditDocument.get().getAmount().equals(creditDocument.get().getAdjustedAmount())) {
                    creditDocument.get().setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                }
                creditDocRepository.save(creditDocument.get());

                creditDebitDocMapping.save(debitDocMapping);
                remainingAmount = debitDocument.getTotalamount() - debitDocument.getAdjustedAmount();
                i++;
            }
        }
    }

    public void adjustPaymentWithNewInvoice(List<Integer> creditDocumentList, Double invoiceAmount, DebitDocument newDebitDocument, Double totalAdjustedAmount) {
        Double newInvoicetotalAmount = newDebitDocument.getTotalamount();
        for (Integer crid : creditDocumentList) {
            if (newDebitDocument.getAdjustedAmount() != null)
                newInvoicetotalAmount = newDebitDocument.getTotalamount() - newDebitDocument.getAdjustedAmount();
            CreditDocument document = creditDocRepository.findById(crid).get();
            if (totalAdjustedAmount != null)
                invoiceAmount = document.getAmount() - totalAdjustedAmount;
            if (!document.getStatus().equalsIgnoreCase(UtilsCommon.PAYMENT_STATUS_PENDING)) {
                document.setAdjustedAmount(0d);
//                if (invoiceAmount < document.getAmount()) document.setAmount(document.getAmount() - invoiceAmount);
//                else document.setAmount(invoiceAmount - document.getAmount());

                CreditDebitDocMapping debitDocMapping = new CreditDebitDocMapping();
                debitDocMapping.setDebtDocId(newDebitDocument.getId());
                debitDocMapping.setIsDeleted(false);
                debitDocMapping.setCreditDocId(document.getId());
                if (newInvoicetotalAmount == 0d) {
                    document.setStatus(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                    document.setPaytype(CommonConstants.CREDIT_DOC_STATUS.ADVANCE_PAYMENT);
                } else {
                    if (newInvoicetotalAmount > document.getAmount() - document.getAdjustedAmount()) {
                        debitDocMapping.setAdjustedAmount(document.getAmount() - document.getAdjustedAmount());
                        newInvoicetotalAmount = newInvoicetotalAmount - debitDocMapping.getAdjustedAmount();
                        if (newDebitDocument.getAdjustedAmount() != null)
                            newDebitDocument.setAdjustedAmount(newDebitDocument.getAdjustedAmount() + debitDocMapping.getAdjustedAmount());
                        else
                            newDebitDocument.setAdjustedAmount(debitDocMapping.getAdjustedAmount());
                        document.setAdjustedAmount(document.getAmount() - document.getAdjustedAmount());
                    } else if (newInvoicetotalAmount <= document.getAmount() - document.getAdjustedAmount()) {
                        if (newDebitDocument.getAdjustedAmount() == null)
                            newDebitDocument.setAdjustedAmount(0.0);
                        newDebitDocument.setAdjustedAmount(newDebitDocument.getTotalamount());
                        debitDocMapping.setAdjustedAmount(newDebitDocument.getAdjustedAmount());
                        document.setAdjustedAmount(document.getAdjustedAmount() + debitDocMapping.getAdjustedAmount());
                    }
                    creditDebtMappingRepository.save(debitDocMapping);
                }
                if (document.getAmount() == document.getAdjustedAmount())
                    document.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                else if (document.getAmount() > document.getAdjustedAmount())
                    document.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);

                creditDocRepository.save(document);
            }

        }

        if (newDebitDocument.getAdjustedAmount() != null) {
            if (newDebitDocument.getAdjustedAmount() == newDebitDocument.getTotalamount())
                newDebitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
            else if (newDebitDocument.getAdjustedAmount() < newDebitDocument.getTotalamount())
                newDebitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
            if (newDebitDocument.getAdjustedAmount() == 0d)
                newDebitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
        } else {
            newDebitDocument.setAdjustedAmount(0.0);
            newDebitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.UNPAID);
        }
        debitDocRepository.save(newDebitDocument);
    }

    public String getStatus(DebitDocument debitDocument) {
        String status = debitDocument.getPaymentStatus();
        String creditNoteStatus = "";
//        boolean isCNFullAdjusted = false;
        List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
        boolean breakLoop = false;
        int i = 0;
        if (creditDebitDocMappings.size() > 0) {
            while (!breakLoop && i < creditDebitDocMappings.size()) {
                CreditDocument creditDocument = creditDocRepository.findById(creditDebitDocMappings.get(i).getCreditDocId()).orElse(null);
                if (creditDocument != null) {

                    boolean isCNDocValid = true;
                    if (creditDocument.getInvoiceId() != null) {
                        if (!creditDocument.getInvoiceId().equals(debitDocument.getId()) && creditDocument.getPaymode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE.CREDIT_NOTE)) {
                            isCNDocValid = false;
                        }
                    }

                    if (creditNoteStatus.isEmpty() && creditDocument.getPaymode().equalsIgnoreCase("Credit Note") && !creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING)
                            && !debitDocument.getBillrunstatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.CANCELLED) && isCNDocValid) {
                        creditNoteStatus = "CN";
                    } else if (debitDocument.getBillrunstatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.CANCELLED) && !debitDocument.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.CANCELLED) && isCNDocValid
                            && !debitDocument.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.UNPAID) && creditNoteStatus.isEmpty() && creditDocument.getPaymode().equalsIgnoreCase("Credit Note")) {
                        creditNoteStatus = "CN";
                    }
                    if (creditDebitDocMappings.get(i).getAdjustedAmount() == null) {
                        creditDebitDocMappings.get(i).setAdjustedAmount(0.00);
                    }
                    if (creditDocument != null && !creditDocument.getPaymode().equalsIgnoreCase("Credit Note") && creditDebitDocMappings.get(i).getAdjustedAmount() <= 0) {
                        if (creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING) && creditDocument.getNextTeamHierarchyMappingId() == null) {
                            status = CommonConstants.DEBIT_DOC_STATUS.PENDING_SENT;//.concat(creditNoteStatus);
                            breakLoop = true;
                        } else if (creditDocument.getStatus().equalsIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.PENDING) && creditDocument.getNextTeamHierarchyMappingId() != null) {
                            status = CommonConstants.DEBIT_DOC_STATUS.PENDING_ACCEPTED;
                        }
                    }
                }

                i++;
            }
        }
        if (status != null && !status.equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING_SENT) && !status.equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PENDING_ACCEPTED)) {
            if (debitDocument.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                status = CommonConstants.DEBIT_DOC_STATUS.CLEAR;
            } else if (debitDocument.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID)) {
                status = CommonConstants.DEBIT_DOC_STATUS.PARTIAL_PENDING;
            }
        }
        if (status != null && !creditNoteStatus.isEmpty()) {// && !isCNFullAdjusted) {
            return status.concat("/" + creditNoteStatus);
        } else if (!creditNoteStatus.isEmpty()) {// && !isCNFullAdjusted) {
            return CommonConstants.DEBIT_DOC_STATUS.UNPAID.concat("/" + creditNoteStatus);
        } else {
            return status;
        }

    }

    @Transactional
    public TrialDebitDocument cancelAndRegenerateForTrailInvoice(Integer debitDocId) {
        try {
            TrialDebitDocument trialDebitDocument = trialDebitDocRepository.findById(debitDocId).orElse(null);
            QTrialDebitDocument qTrialDebitDocument = QTrialDebitDocument.trialDebitDocument;
            JPAQuery<TrialDebitDocument> debitDocumentJPAQuery = new JPAQuery<>(entityManager);
            List<TrialDebitDocument> lastInvoice = debitDocumentJPAQuery.from(qTrialDebitDocument).where(qTrialDebitDocument.customer.id.eq(trialDebitDocument.getCustomer().getId()).and(qTrialDebitDocument.billrunstatus.eq("Generated").or(qTrialDebitDocument.billrunstatus.eq("Exported")))).orderBy(qTrialDebitDocument.docnumber.desc()).limit(1).fetch();
            List<TrialDebitDocument> debitDocumentList = new ArrayList<>();
            if (lastInvoice.size() > 0 && !lastInvoice.contains(trialDebitDocument)) {
                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Only latest invoice can be regeneate and cancelled", null);
            }
            if (trialDebitDocument == null) {
                throw new CustomValidationException(HttpStatus.NOT_FOUND.value(), "Invalid debit doc id: " + debitDocId, null);
            }
            QCustPlanMappping qCustPlanMappping = QCustPlanMappping.custPlanMappping;
            BooleanExpression expression = qCustPlanMappping.isNotNull().and(qCustPlanMappping.customer.id.eq(trialDebitDocument.getCustomer().getId()));
            List<CustPlanMappping> custPlanMapppings = (List<CustPlanMappping>) custPlanMappingRepository.findAll(expression);
            QCustomerChargeHistory qCustomerChargeHistory = QCustomerChargeHistory.customerChargeHistory;
            List<Integer> custPlanIds = custPlanMapppings.stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
            BooleanExpression booleanExpression = qCustomerChargeHistory.isNotNull().and(qCustomerChargeHistory.custPlanMapppingId.in(custPlanIds)).and(qCustomerChargeHistory.chargeType.eq(CommonConstants.CHARGE_TYPE_NONRECURRING));
            List<CustomerChargeHistory> customerChargeHistories = IterableUtils.toList(customerChargeHistoryRepo.findAll(booleanExpression));
            if (customerChargeHistories.size() > 0) {
                customerChargeHistories.forEach(customerChargeHistory -> {
                    customerChargeHistory.setIsFirstChargeApply(false);
                    customerChargeHistoryRepo.save(customerChargeHistory);
                });
            }
            List<CustChargeDetails> custChargeDetails = custChargeDetailsRepository.findAllByCustPlanMapppingIdIn(custPlanIds);

            if (!CollectionUtils.isEmpty(custChargeDetails)) {
                custChargeDetails.forEach(chargeDetails -> {
                    chargeDetails.setIsUsed(false);
                    chargeDetails.setDebitdocid(null);
                });
                custChargeDetailsRepository.saveAll(custChargeDetails);
            }

            if (!CollectionUtils.isEmpty(custPlanMapppings)) {
                custPlanMapppings.forEach(custPlanMappping -> {
                    custPlanMappping.setDebitdocid(null);
                    custPlanMappping.setIsInvoiceCreated(false);
                });
                custPlanMappingRepository.saveAll(custPlanMapppings);
            }
            trialDebitDocument.setBillrunstatus("Cancelled");
            Customers customers = trialDebitDocument.getCustomer();
            customers.setLastBillDate(LocalDate.now());
            return trialDebitDocRepository.save(trialDebitDocument);
        } catch (CustomValidationException ex) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), ex.getMessage(), null);
        } catch (RuntimeException ex) {
            throw new RuntimeException("Exception at Cancel and Regenerate invoice for id: " + debitDocId + " ex: " + ex.getMessage());
        }
    }

    public List<Customers> getCustomerByDebitDocId(Integer id) {
        List<DebitDocument> debitDocumentList = debitDocRepository.findAllById(id);
        List<Customers> customersList = new ArrayList<>();
        if (!debitDocumentList.isEmpty()) {
            customersList = customersRepository.findAllById(debitDocumentList.stream().map(debitDocument -> debitDocument.getCustomer().getId()).collect(Collectors.toList()));
        }
        if (debitDocumentList.isEmpty()) {
            List<TrialDebitDocument> debitDocumentList1 = trialDebitDocRepository.findAllById(id);
            customersList = customersRepository.findAllById(debitDocumentList1.stream().map(trialDebitDocument -> trialDebitDocument.getId()).collect(Collectors.toList()));
        }
        return customersList;
    }

    @Transactional
    public void adjustTrailOldDebitDocument(Long invoiceId, HashSet<Integer> oldDebitDocumentId) {
//        DebitDocument old = debitDocRepository.findById(oldDebitDocumentId.iterator().next()).orElse(null);
        List<TrialDebitDocument> oldList = new ArrayList<>();
        for (Integer debitId : oldDebitDocumentId) {
            Optional<TrialDebitDocument> oldDebitDocument = trialDebitDocRepository.findById(debitId);
            if (oldDebitDocument.isPresent()) oldList.add(oldDebitDocument.get());
        }
        TrialDebitDocument newDebitDocument = trialDebitDocRepository.findById(invoiceId.intValue()).orElse(null);
        if (!CollectionUtils.isEmpty(oldList) && newDebitDocument != null) {
            List<Integer> creditDocumentList = new ArrayList<>();
            for (TrialDebitDocument debitDocument : oldList) {
                boolean isDbrRequired = false;
                if (!Objects.equals(newDebitDocument.getCustpackrelid(), debitDocument.getCustpackrelid()) && !newDebitDocument.getCreatedate().equals(debitDocument.getCreatedate()))
                    isDbrRequired = true;

                List<CreditDebitDocMapping> debitDocMappingList = creditDebtMappingRepository.findBydebtDocId(debitDocument.getId());
                creditDocumentList = debitDocMappingList.stream().map(CreditDebitDocMapping::getCreditDocId).collect(Collectors.toList());
                creditDebtMappingRepository.deleteInBatch(debitDocMappingList);

                //CreditDocument document = createCreditNoteForTrail(debitDocument, "creditnote", CommonConstants.TRANS_CREDIT_NOTE, "Refund  paid amount against cancelled invoice " + debitDocument.getDocnumber() + "\n Payment adjusted :-" + debitDocument.getTotalamount(), "Automatic", isDbrRequired);
                // adjustPaymentWithNewInvoice(creditDocumentList, debitDocument.getTotalamount() - document.getAmount(), newDebitDocument);
                //debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CANCELLED);
                //debitDocument.setAdjustedAmount(document.getAmount());
                trialDebitDocRepository.save(debitDocument);
            }

//            dbrService.creditNoteDbrEntry(newDebitDocument, newDebitDocument.getAdjustedAmount());
        }
    }

    public CreditDocument createCreditNoteForTrail(TrialDebitDocument debitDocument, String payType, String type, String remarks, String mode, boolean isDbrRequired) {
        CreditDocument creditDocument = new CreditDocument();
        creditDocument.setAmount(debitDocument.getTotalamount());
        creditDocument.setCustomer(debitDocument.getCustomer());
        creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
        List<Integer> invoiceId = new ArrayList<>();
        invoiceId.add(debitDocument.getId());
        creditDocument.setInvoiceId(debitDocument.getId());
        creditDocument.setPaymentdate(LocalDate.now());
        creditDocument.setPaymode(mode);
        creditDocument.setPaytype(payType);
        creditDocument.setType(type);
        creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
        creditDocument.setIsDelete(false);
        creditDocument.setMvnoId(debitDocument.getCustomer().getMvnoId());
        creditDocument.setBuID(debitDocument.getCustomer().getBuId());
        creditDocument.setRemarks(remarks);
        creditDocument.setTdsamount(0d);
        creditDocument.setAbbsAmount(0d);
//        creditDocument.setCreditdocumentno(creditDocService.getInvoiceNo());
        Boolean isLCO = debitDocument.getCustomer().getLcoId() != null ? true :false;
        creditDocument.setCreditdocumentno(numberSequenceUtil.getCreditNoteNumber(isLCO, debitDocument.getCustomer().getLcoId(), debitDocument.getCustomer().getMvnoId()));

        DecimalFormat df = new DecimalFormat("#.00");
        if (debitDocument != null && isDbrRequired) {
            Double totalAmount = debitDocument.getTotalamount();
            LocalDateTime startDate = debitDocument.getStartdate();
            LocalDateTime endDate = debitDocument.getEndate();
            LocalDateTime todayDate = LocalDateTime.now();
            Long invoiceDays = ChronoUnit.DAYS.between(startDate, endDate);
            Long usedDays = ChronoUnit.DAYS.between(startDate, todayDate);
//            if (usedDays <= 0)
//                usedDays = 1l;
//            else
//                usedDays = usedDays + 1;
            Double dbr = 0d;
            if (!invoiceDays.equals(usedDays) && usedDays != 0) {
                Double edr = totalAmount / invoiceDays;
                dbr = edr * usedDays;
                creditDocument.setAmount(debitDocument.getTotalamount() - dbr);
            }
        }

        try {
            //add adjusted amount against invoice
            creditDocument = creditDocRepository.save(creditDocument);
            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setDebtDocId(debitDocument.getId());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAmount());
            creditDebtMappingRepository.save(creditDebitDocMapping);
            //  creditDocService.addLedgeAfterApproval(creditDocument);
            // dbrService.creditNoteDbrEntry(debitDocument, creditDocument.getAmount());

        } catch (Exception e) {
            throw new RuntimeException("Exception when creating credit note for invoice: " + debitDocument.getId());
        }
        return creditDocument;
    }

    public void sendBillGenData(Integer debitDocId, Boolean canelregenrate) {
        DebitDocument debitDocument = debitDocRepository.findById(debitDocId).orElse(null);
        if (debitDocument != null) {
            DebitDocumentMessage debitDocumentMessage = new DebitDocumentMessage(debitDocument);
            kafkaMessageSender.send(new KafkaMessageData(debitDocumentMessage,DebitDocumentMessage.class.getSimpleName()));
//            messageSender.send(debitDocumentMessage, RabbitMqConstants.QUEUE_DEBIT_DOCUMENT_SUCCESS, RabbitMqConstants.QUEUE_DEBIT_DOCUMENT_SUCCESS_KPI);
        }

        List<Integer> mappings =custPlanMappingRepository.findAllByDebitdocid1(debitDocId);
        if (mappings != null && !mappings.isEmpty()) {
            CustPlanMappingUpdateMessage message = new CustPlanMappingUpdateMessage(mappings.stream().collect(Collectors.toList()), debitDocId);
//            messageSender.send(message, RabbitMqConstants.QUEUE_CUST_PLAN_MAPPING_UPDATE);
            kafkaMessageSender.send(new KafkaMessageData(message, CustPlanMappingUpdateMessage.class.getSimpleName()));
        }
    }

    public Double getPendingRevenueWithTaxAtCurrentDate(DebitDocument debitDocument) {
        Double pendingRevenue = 0d;
        DecimalFormat df = new DecimalFormat("#.00");
        if(debitDocument == null)
            return 0d;
        List<CustomerDBR> customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(LocalDate.now(), debitDocument);
        pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(LocalDate.now())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));

        if (LocalDate.now().isBefore(debitDocument.getStartdate().toLocalDate())) {
            customerDBRList = dbrService.getCustomerDBRListBetweenStartDateAndEndDate(debitDocument.getStartdate().toLocalDate(), debitDocument);
            pendingRevenue = Double.parseDouble(df.format(customerDBRList.stream().filter(x -> x.getStartdate().equals(debitDocument.getStartdate().toLocalDate())).mapToDouble(x -> x.getPendingamt() + x.getDbr()).sum()));
            if (pendingRevenue > 0.0d)
                pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
        } else {
            if (pendingRevenue > 0.0d)
                pendingRevenue = (debitDocument.getTotalamount() / (debitDocument.getSubtotal() + debitDocument.getDiscount())) * pendingRevenue;
        }
        pendingRevenue = Double.parseDouble(df.format(pendingRevenue));
        return pendingRevenue;
    }

    public Boolean isAllInvoiceStatusClearedForCustomer(Customers customers) {
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (customers.getLcoId() != null)
            return true;
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> (!x.getBillrunstatus().equalsIgnoreCase("VOID"))).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                for (int i = 0; i < debitDocuments.size(); i++) {
                    DebitDocument document = debitDocuments.get(i);
                    List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocId(document.getId());
                    List<Integer> creditDocIdList = creditDebitDocMappings.stream().map(x -> x.getCreditDocId()).collect(Collectors.toList());
                    List<CreditDocument> creditDocuments = creditDocRepository.findAllByIdIn(creditDocIdList);
                    creditDocuments = creditDocuments.stream().filter(x -> (x.getStatus().equalsIgnoreCase(CommonConstants.PAYMENT_STATUS_PENDDING) || x.getStatus().equalsIgnoreCase(CommonConstants.CUSTOMER_STATUS_REJECTED))).collect(Collectors.toList());
                    creditDocIdList = creditDocuments.stream().map(x -> x.getId()).collect(Collectors.toList());

                    creditDocIdList.stream().forEach(id -> {
                        for (int j = 0; j < creditDebitDocMappings.size(); j++) {
                            if (creditDebitDocMappings.get(j).getCreditDocId().equals(id)) {
                                creditDebitDocMappings.remove(creditDebitDocMappings.get(j));
                            }
                        }
                    });

                    Double adjustedAmount = creditDebitDocMappings.stream().filter(x -> x.getAdjustedAmount() != null).mapToDouble(x -> x.getAdjustedAmount()).sum();
                    if (adjustedAmount < document.getTotalamount())
                        return false;
                }
            }
        }
        return true;
    }

    public Double getTransferableCommission(Customers customers, Partner partner) {
        AtomicReference<Double> transferableCommission = new AtomicReference<>(0.0);
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> !x.getIsDirectChargeInvoice()).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                debitDocuments.stream().forEach(x -> {
                    List<CustPlanMappping> mappings = (List<CustPlanMappping>) custPlanMappingRepository.findAllByDebitdocid(x.getId());
                    if (mappings != null && !mappings.isEmpty()) {
                        mappings.stream().forEach(data -> {
                            List<PartnerLedgerDetails> detailsList = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAllByDebitDocumentId(x.getId());
                            detailsList = detailsList.stream().filter(y->y.getPlanid().equalsIgnoreCase(data.getPlanId().toString())).collect(Collectors.toList());

                            if (detailsList != null && !detailsList.isEmpty()) {
                                List<PartnerLedgerDetails> detailsList1 = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_COMMISSION)).collect(Collectors.toList());
                                List<PartnerLedgerDetails> revertDetailsList = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERT_COMMISSION)).collect(Collectors.toList());

                                Double revertCommission = 0.0;
                                if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                    revertCommission = revertDetailsList.stream().mapToDouble(z -> z.getCommission()).sum();
                                else
                                    revertCommission = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();

                                Double finalRevertCommission = revertCommission;
                                detailsList1.stream().forEach(ledgerDetail -> {
                                    Double commission = 0.0;
                                    if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                        commission = ledgerDetail.getAmount();
                                    else
                                        commission = ledgerDetail.getCommission();

                                    commission = commission - finalRevertCommission;
                                    LocalDate planStartDate = data.getStartDate().toLocalDate();
                                    LocalDate planEndDate = data.getEndDate().toLocalDate();
                                    LocalDate todayDate = LocalDate.now();
                                    if (!todayDate.isBefore(planStartDate) && !todayDate.isAfter(planEndDate)) {
                                        Long planDays = ChronoUnit.DAYS.between(planStartDate, planEndDate);
                                        Long remainingDays = ChronoUnit.DAYS.between(todayDate, planEndDate);
                                        if (planDays.intValue() == 0)
                                            planDays = 1l;
                                        if (remainingDays.intValue() == 0)
                                            remainingDays = 1l;
                                        Double proCommission = (commission / planDays) * remainingDays;
                                        transferableCommission.updateAndGet(v -> v + proCommission);

                                    } else if (todayDate.isBefore(planStartDate)) {
                                        Double finalCommission = commission;
                                        transferableCommission.updateAndGet(v -> v + finalCommission);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return transferableCommission.get();
    }


    public Double getTransferableBalance(Customers customers, Partner partner) {
        AtomicReference<Double> transferableBalance = new AtomicReference<>(0.0);
        List<DebitDocument> debitDocuments = debitDocRepository.findAllByCustomer(customers);
        if (debitDocuments != null && !debitDocuments.isEmpty()) {
            debitDocuments = debitDocuments.stream().filter(x -> !x.getIsDirectChargeInvoice()).collect(Collectors.toList());
            if (debitDocuments != null && !debitDocuments.isEmpty()) {
                debitDocuments.stream().forEach(x -> {
                     List<CustPlanMappping> mappings = (List<CustPlanMappping>) custPlanMappingRepository.findAllByDebitdocid(x.getId());
                    if (mappings != null && !mappings.isEmpty()) {
                        mappings.stream().forEach(data -> {
                            List<PartnerLedgerDetails> detailsList = (List<PartnerLedgerDetails>) partnerLedgerDetailsRepository.findAllByDebitDocumentId(x.getId());
                            detailsList=detailsList.stream().filter(y->y.getPlanid().equalsIgnoreCase(data.getPlanId().toString())).collect(Collectors.toList());
                            if (detailsList != null && !detailsList.isEmpty()) {
                                List<PartnerLedgerDetails> detailsList1 = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)).collect(Collectors.toList());
                                List<PartnerLedgerDetails> revertDetailsList = detailsList.stream().filter(y -> y.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_REVERSE_BALANCE)).collect(Collectors.toList());

                                Double revertBalance = 0.0;
                                if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                    revertBalance = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();
                                else
                                    revertBalance = revertDetailsList.stream().mapToDouble(z -> z.getAmount()).sum();

                                Double finalRevertCommission = revertBalance;
                                detailsList1.stream().forEach(ledgerDetail -> {
                                    Double balance = 0.0;
                                    if (customers.getIs_from_pwc() && customers.getLcoId() != null)
                                        balance = ledgerDetail.getAmount();
                                    else
                                        balance = ledgerDetail.getAmount();

                                    balance = balance - finalRevertCommission;
                                    LocalDate planStartDate = data.getStartDate().toLocalDate();
                                    LocalDate planEndDate = data.getEndDate().toLocalDate();
                                    LocalDate todayDate = LocalDate.now();
                                    if (!todayDate.isBefore(planStartDate) && !todayDate.isAfter(planEndDate)) {
                                        Long planDays = ChronoUnit.DAYS.between(planStartDate, planEndDate);
                                        Long remainingDays = ChronoUnit.DAYS.between(todayDate, planEndDate);
                                        if (planDays.intValue() == 0)
                                            planDays = 1l;
                                        if (remainingDays.intValue() == 0)
                                            remainingDays = 1l;
                                        Double proCommission = (balance / planDays) * remainingDays;
                                        transferableBalance.updateAndGet(v -> v + proCommission);

                                    } else if (todayDate.isBefore(planStartDate)) {
                                        Double finalCommission = balance;
                                        transferableBalance.updateAndGet(v -> v + finalCommission);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
        return transferableBalance.get();
    }

    public void updateDebitDocumentForCNFlag(Long invoiceId) {
        Optional<DebitDocument> document = debitDocRepository.findById(invoiceId.intValue());
        if (document.isPresent()) {
            List<CustPlanMappping> mappping = custPlanMappingRepository.findAllByDebitdocid(invoiceId.intValue());
            if (mappping != null && !mappping.isEmpty()) {
                if (mappping.stream().filter(x -> x.getIsContainsCustomerInvoice()).map(x -> x.getIsContainsCustomerInvoice()).count() == 0) {
                    document.get().setIsCNEnable(true);
                    debitDocRepository.save(document.get());
                }
            }
        }
    }

    public boolean isPaymentInWorkflowForService(List<Integer> serviceMappingIds) {
        AtomicBoolean isCreditDocInProcess = new AtomicBoolean(false);
        List<CustomerServiceMapping> custServiceMappings = customerServiceMappingRepository.findAllByIdIn(serviceMappingIds);
        for (CustomerServiceMapping custServiceMapping : custServiceMappings) {
            List<CustPlanMappping> packageList = custPlanMappingRepository.findAllByCustServiceMappingId(custServiceMapping.getId());
            packageList = packageList.stream().filter(custPlanMappping -> custPlanMappping.getDebitdocid() != null).collect(Collectors.toList());
            if (packageList != null && !packageList.isEmpty()) {
                List<DebitDocument> debitDocumentList = debitDocRepository.findAllByIdIn(packageList.stream().map(custPlanMappping -> custPlanMappping.getDebitdocid()).map(aLong -> aLong.intValue()).collect(Collectors.toList()));
                isCreditDocInProcess.set(debitDocumentList.stream().anyMatch(debitDocument -> {
                    Double pendingAmt = creditDocRepository.findTotalPendingAmountByDebitDocId(debitDocument.getId());
                    return (pendingAmt != null && !pendingAmt.equals(0.0));
                }));
                if (isCreditDocInProcess.get()) {
                    break;
                }
            }
        }
        return isCreditDocInProcess.get();
    }



    public void createInvoice(Customers customers, String type, String paysource, RecordPaymentPojo recordPaymentDTO, AdditionalInformationDTO additionalInformationDTO, List<CustChargeDetailsPojo> custChargeDetailsPojos, boolean changePlanNextBillDate, Boolean isAutoPaymentRequired, List<CreditDocumentPaymentPojo> paymentPojoList, Integer payingChildCustId, FlagDTO flagDTO){
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customers.getId());
        custPlanMapppingList.sort(Comparator.comparingInt(CustPlanMappping::getId).reversed());
        Integer renewalId = custPlanMapppingList.get(0).getRenewalId();
        List<CustPlanMappping> newCustPlanMapppingList = custPlanMapppingList.stream()
                .filter(custPlanMappping -> {
                    Integer planRenewalId = custPlanMappping.getRenewalId();
                    Integer vasId = custPlanMappping.getVasId();
                    return (planRenewalId != null && planRenewalId.equals(renewalId))
                            || vasId != null;
                })
                .collect(Collectors.toList());

        List<CustPlanMappping> oldCustPlanMapppingList = new ArrayList<>();
        if(type.equalsIgnoreCase(CommonConstants.INVOICE_TYPE.CHANGE_PLAN)) {
            oldCustPlanMapppingList = custPlanMapppingList.stream()
                    .filter(custPlanMappping -> {
                        Integer planRenewalId = custPlanMappping.getRenewalId();
                        return (planRenewalId == null || !planRenewalId.equals(renewalId)) && custPlanMappping.getCustPlanStatus().equalsIgnoreCase("STOP");
                    })
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(oldCustPlanMapppingList) && customers.getCusttype().equalsIgnoreCase("Postpaid") && changePlanNextBillDate) {
                oldCustPlanMapppingList.sort((o1, o2) -> o2.getPlanId().compareTo(o1.getPlanId()));
                Long debitdocid = oldCustPlanMapppingList.get(0).getDebitdocid();
                if (oldCustPlanMapppingList.get(0).getPlanGroup() != null) {
                    Integer plangroupId = oldCustPlanMapppingList.get(0).getPlanGroup().getPlanGroupId();
                        if(debitdocid!=null) {
                            oldCustPlanMapppingList = oldCustPlanMapppingList.stream().filter(i -> i.getPlanGroup().getPlanGroupId().equals(plangroupId) && i.getDebitdocid().equals(debitdocid)).collect(Collectors.toList());
                        }else {
                            oldCustPlanMapppingList = oldCustPlanMapppingList.stream().filter(i -> i.getPlanGroup().getPlanGroupId().equals(plangroupId)).collect(Collectors.toList());
                        }
                    } else {
                    if (debitdocid!=null) {
                        oldCustPlanMapppingList = oldCustPlanMapppingList.stream().filter(i -> i.getDebitdocid().equals(debitdocid) && i.getCustPlanStatus().equalsIgnoreCase("STOP")).collect(Collectors.toList());
                    }else {
                        oldCustPlanMapppingList = oldCustPlanMapppingList.stream().filter(i ->  i.getCustPlanStatus().equalsIgnoreCase("STOP")).collect(Collectors.toList());
                    }
                }
                oldCustPlanMapppingList = oldCustPlanMapppingList.stream().peek(x->x.setCustPlanStatus("Active")).collect(Collectors.toList());
                custPlanMappingRepository.saveAll(oldCustPlanMapppingList);
            }
        }
        List<Integer> newCustPlanIds = newCustPlanMapppingList.stream().map(CustPlanMappping::getId).collect(Collectors.toList());
        Set<CustomerChargeHistory> customerChargeHistoryList = customerChargeHistoryRepo.findByCustPlanMapppingIdIn(newCustPlanIds);
        List<Integer> overrideChargeIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(custChargeDetailsPojos))
            overrideChargeIds = custChargeDetailsPojos.stream().map(CustChargeDetailsPojo::getChargeid).collect(Collectors.toList());

        createDataSharedService.sendChangePlanForAllMicroService(newCustPlanMapppingList,oldCustPlanMapppingList,customerChargeHistoryList,type,renewalId,null,null,null,paysource, recordPaymentDTO,additionalInformationDTO, overrideChargeIds,changePlanNextBillDate,isAutoPaymentRequired,paymentPojoList,payingChildCustId,null,flagDTO);

    }
    public void createInvoice(Set<Customers> customersList,String type,Integer parentId,List<Integer> childIds, RecordPaymentPojo recordPaymentDTO, List<CustChargeDetailsPojo> custChargeDetailsPojos,boolean changePlanNextBillDate,Boolean isAutoPaymentRequired,List<CreditDocumentPaymentPojo> paymentPojoList,Integer payingChildId){
        List<CustPlanMappping> newCustPlanMapppingList=new ArrayList<>();
        List<CustPlanMappping> oldCustPlanMapppingList = new ArrayList<>();
        Set<CustomerChargeHistory> customerChargeHistoryList =new HashSet<>();
        Integer renewalId = null;
        for (Customers customers : customersList) {
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByCustomerId(customers.getId());
            custPlanMapppingList.sort(Comparator.comparingInt(CustPlanMappping::getId).reversed());
            renewalId = custPlanMapppingList.get(0).getRenewalId();
            Integer finalRenewalId = renewalId;
            newCustPlanMapppingList.addAll(custPlanMapppingList.stream()
                    .filter(custPlanMappping -> {
                        Integer planRenewalId = custPlanMappping.getRenewalId();
                        return planRenewalId != null && planRenewalId.equals(finalRenewalId);
                    })
                    .collect(Collectors.toList()));

            if (type.equalsIgnoreCase(CommonConstants.INVOICE_TYPE.CHANGE_PLAN)) {
                Integer finalRenewalId1 = renewalId;
                oldCustPlanMapppingList.addAll(custPlanMapppingList.stream()
                        .filter(custPlanMappping -> {
                            Integer planRenewalId = custPlanMappping.getRenewalId();
                            return (planRenewalId == null || !planRenewalId.equals(finalRenewalId1)) && custPlanMappping.getCustPlanStatus().equalsIgnoreCase("STOP");
                        })
                        .collect(Collectors.toList()));
            }

            Set<Integer> newCustPlanIds = newCustPlanMapppingList.stream().map(CustPlanMappping::getId).collect(Collectors.toSet());
            customerChargeHistoryList.addAll(customerChargeHistoryRepo.findByCustPlanMapppingIdIn(newCustPlanIds));
        }
        List<Integer> overrideChargeIds = new ArrayList<>();
        if(!CollectionUtils.isEmpty(custChargeDetailsPojos)) {
            overrideChargeIds = custChargeDetailsPojos.stream().map(CustChargeDetailsPojo::getChargeid).collect(Collectors.toList());
        }
        createDataSharedService.sendChangePlanForAllMicroService(newCustPlanMapppingList,oldCustPlanMapppingList,customerChargeHistoryList,type,renewalId,null,parentId,childIds,"", recordPaymentDTO,null,overrideChargeIds,false,isAutoPaymentRequired,paymentPojoList,payingChildId,null,null);

    }

    public void adjustCustomerLedgerPayment(SendCustomerPaymentDTO sendCustomerPaymentDTO){
        SendOnlinePaymentRevenueMessage sendOnlinePaymentRevenueMessage = new SendOnlinePaymentRevenueMessage(sendCustomerPaymentDTO);
        Gson gson = new Gson();
        gson.toJson(sendOnlinePaymentRevenueMessage);
        kafkaMessageSender.send(new KafkaMessageData(sendOnlinePaymentRevenueMessage,SendOnlinePaymentRevenueMessage.class.getSimpleName()));
//        messageSender.send(sendOnlinePaymentRevenueMessage , RabbitMqConstants.QUEUE_SEND_CUSTOMER_ONLINE_PAYMENT);

    }


    public Double getTotalAmountDebitDocumentsByMvno(Integer mvnoId, Boolean isInvoiceVoid,Integer days) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        if(mvno.isPresent()) {
            LocalDateTime duedte=LocalDateTime.now().plusDays(days);
            if(mvno.get().getCustInvoiceRefId() != null) {
                List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                if(!CollectionUtils.isEmpty(lastInvoice)) {
                    DebitDocument debitDocument = lastInvoice.get(0);
                    LocalDateTime billDate = debitDocument.getBilldate();
                    if(isInvoiceVoid)
                        return debitDocRepository.getAmountByMvnoIdAndBillDate(mvnoId, billDate);
                    else{
                        return debitDocument.getSubtotal();
//                        return debitDocRepository.getAmountByMvnoIdAndStatusIsNotVoidAndBillDate(mvnoId);
                        }
                } else {
                    if(isInvoiceVoid)
                        return debitDocRepository.getAmountByMvnoId(mvnoId);
                    else
                        return debitDocRepository.getAmountByMvnoIdAndStatusIsNotVoid(mvnoId);
                }
            } else {
                throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Billing customer not available for Mvno", null);
            }
        } else {
            throw new CustomValidationException(HttpStatus.NO_CONTENT.value(), "Mvno Not available..!", null);
        }

    }

//  public List<DebitDocDetailDTO> getDebitDocDetailByChargeAndMvno(Integer mvnoId, Boolean isInvoiceVoid, LocalDateTime fromDate, LocalDateTime toDate) {
//        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
//        List<DebitDocDetailDTO> debitDocDetails = new ArrayList<>();
//        if(mvno.isPresent()) {
//            if (mvno.get().getCustInvoiceRefId() != null) {
//                if (toDate == null) {
//                    toDate = LocalDate.now().atTime(LocalTime.MAX);
//                }
//                if (fromDate == null || toDate == null) {
//                    logger.warn("Received null dates. Fetching last invoice for MVNO: " + mvno.get().getCustInvoiceRefId());
//                    List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
//
//                    if (!CollectionUtils.isEmpty(lastInvoice)) {
//                        fromDate = lastInvoice.get(0).getBilldate().toLocalDate().atStartOfDay();
//                        toDate = lastInvoice.get(0).getBilldate().toLocalDate().atTime(LocalTime.MAX);
//                    } else {
//                        fromDate = mvno.get().getCreatedate();
//                        toDate = LocalDateTime.now();
//                    }
//                }
//                debitDocDetails = debitDocRepository.getDebitDocDTOByMvnoAndBillDate(mvnoId, fromDate, toDate);
//                return debitDocDetails;
//            }
//        }
//        return debitDocDetails;
//    }


    public static List<DebitDocDetailDTO> convertToRequiredResponse(List<DebitDocDetailDTO> inputList) {
        // Grouping by chargeType and summing up totalAmount for each group-
        Map<String, Double> totalAmountsByChargeType = inputList.stream().filter(i -> i.getChargeType() != null)
                .collect(Collectors.groupingBy(DebitDocDetailDTO::getChargeType,
                        Collectors.summingDouble(DebitDocDetailDTO::getTotalAmount)));

        // Creating DebitDocDetailDTO objects for each group
        List<DebitDocDetailDTO> resultList = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totalAmountsByChargeType.entrySet()) {
            String chargeType = entry.getKey();
            double totalAmount = entry.getValue();
            List<Integer> debitDocDetailIds = inputList.stream()
                    .filter(dto ->dto.getChargeType() != null && dto.getChargeType().equals(chargeType) && dto.getDebitDocDetailId() != null)
                    .map(DebitDocDetailDTO::getDebitDocDetailId)
                    .collect(Collectors.toList());
            List<Integer> chargeId = inputList.stream()
                    .filter(dto ->dto.getChargeType() != null && dto.getChargeType().equals(chargeType) && dto.getChargeId() != null)
                    .map(DebitDocDetailDTO::getChargeId).collect(Collectors.toList());
            resultList.add(new DebitDocDetailDTO(chargeType, totalAmount, debitDocDetailIds,chargeId.get(0)));
        }
        return resultList;
    }

//    public List<DebitDocDetailDTO> getDebitDocDetailByChargeAndMvno(Integer mvnoId, Boolean isInvoiceVoid, LocalDateTime fromDate, LocalDateTime toDate) {
//        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
//        List<DebitDocDetailDTO> debitDocDetails = new ArrayList<>();
//        if(mvno.isPresent()) {
//            if (mvno.get().getCustInvoiceRefId() != null) {
//                if (toDate == null) {
//                    toDate = LocalDate.now().atTime(LocalTime.MAX);
//                }
//                if (fromDate == null) {
//                    List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
//                    if (!CollectionUtils.isEmpty(lastInvoice)) {
//                        fromDate = lastInvoice.get(0).getBilldate().toLocalDate().atStartOfDay();
//                    } else {
//                        fromDate = mvno.get().getCreatedate();
//                    }
//                }
//                debitDocDetails = debitDocRepository.getDebitDocDTOByMvnoAndBillDate(mvnoId, fromDate, toDate);
//                return debitDocDetails;
//            }
//        }
//        return debitDocDetails;
//    }

    public List<DebitDocDetailDTO> getDebitDocDetailByChargeAndMvno(Integer mvnoId, Boolean isInvoiceVoid, LocalDateTime fromDate, LocalDateTime toDate) {
        Optional<Mvno> mvno = mvnoRepository.findById(Long.valueOf(mvnoId));
        List<DebitDocDetailDTO> debitDocDetails = new ArrayList<>();

        if (mvno.isPresent() && mvno.get().getCustInvoiceRefId() != null) {
            if (toDate == null) {
                toDate = LocalDate.now().atTime(LocalTime.MAX);
            }
            if (fromDate == null) {
                List<DebitDocument> lastInvoice = debitDocRepository.lastInvoice(mvno.get().getCustInvoiceRefId());
                if (!CollectionUtils.isEmpty(lastInvoice)) {
                    fromDate = lastInvoice.get(0).getBilldate().toLocalDate().atStartOfDay();
                } else {
                    fromDate = mvno.get().getCreatedate();
                }
            }
            debitDocDetails = debitDocRepository.getDebitDocDTOByMvnoAndBillDate(mvnoId, fromDate, toDate);
        }

        return debitDocDetails;
    }


//    @Scheduled(cron = "${cronjobtimeforispautobill}")
//    public void cronjobtimeforispautobill() {
//        logger.info("XXXXXXXXXXXX----------CRON ISP_AUTO_BILL_SCHEDULER START---------XXXXXXXXXXXX");
//        SchedulerAudit schedulerAudit = new SchedulerAudit();
//        schedulerAudit.setStartTime(LocalDateTime.now());
//        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.ISP_AUTO_BILL_SCHEDULER);
//        if (!schedulerLockService.isSchedulerLocked(CronjobConst.ISP_AUTO_BILL)) {
//            schedulerLockService.acquireSchedulerLock(CronjobConst.ISP_AUTO_BILL);
//            try {
//                logger.info("*********cronjobtimeforispautobill**************");
////                this.ispInvoiceGenerate(null);
//                schedulerAudit.setEndTime(LocalDateTime.now());
//                schedulerAudit.setDescription("Isp Auto Bill Scheduler Run Success");
//                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
//                schedulerAudit.setTotalCount(null);
//            } catch (Exception ex) {
//                schedulerAudit.setEndTime(LocalDateTime.now());
//                schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
//                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
//                logger.error(ex.toString(), ex);
//                logger.error("**********Scheduler Showing ERROR***********");
//            } finally {
//                schedulerAuditService.saveEntity(schedulerAudit);
//                schedulerLockService.releaseSchedulerLock(CronjobConst.ISP_AUTO_BILL);
//                logger.info("XXXXXXXXXXXX---------- Isp Auto Bill Scheduler Locked released ---------XXXXXXXXXXXX");
//            }
//        } else {
//            schedulerAudit.setEndTime(LocalDateTime.now());
//            schedulerAudit.setDescription("Isp Auto Bill Scheduler Lock held by another instance");
//            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
//            schedulerAuditService.saveEntity(schedulerAudit);
//            logger.warn("XXXXXXXXXXXX----------Isp Auto Bill Scheduler Locked held by another instance---------XXXXXXXXXXXX");
//        }
//    }


    public Boolean ispInvoiceGenerate(LocalDate billDate) {
        boolean isAnyInvoiceGenerated = false;

        isAnyInvoiceGenerated = ispInvoiceGenerateMonthly(billDate);

        isAnyInvoiceGenerated = ispInvoiceGenerateBiMonthly(billDate);

        return isAnyInvoiceGenerated;
    }

//this code for monthli bill generation

   public Boolean ispInvoiceGenerateMonthly(LocalDate BillDate){
        try{
            List<ChangePlanMessage> changePlanMessageList = new ArrayList<>();
            LocalDate today = LocalDate.now();
            Integer day = today.getDayOfMonth();
            LocalDateTime firstDayOfLastMonth = today.minusMonths(1).withDayOfMonth(1).atStartOfDay();
            LocalDateTime lastDayOfLastMonth = today.minusMonths(1).withDayOfMonth(today.minusMonths(1).lengthOfMonth()).atTime(LocalTime.MAX);
            List<Integer> mvnoIds = null;
            Boolean isInvoiceGenerated = false;
            if (BillDate!=null){
                day = BillDate.getDayOfMonth();
                firstDayOfLastMonth = BillDate.minusMonths(1).withDayOfMonth(1).atStartOfDay();
                lastDayOfLastMonth =  BillDate.minusMonths(1).withDayOfMonth(BillDate.minusMonths(1).lengthOfMonth()).atTime(LocalTime.MAX);
                mvnoIds = mvnoRepository.findGenerationdayformonthly(day);
            }else {
                mvnoIds = mvnoRepository.findGenerationdayformonthly(day);
            }
            logger.info("today" + " " +today+" firstDayOfLastMonth" +" " +firstDayOfLastMonth + " lastDayOfLastMonth " + lastDayOfLastMonth );

            logger.info("mvnoIds" + mvnoIds);
            Charge charge = chargeRepository.findByNameAndChargetypeAndPrice("org_charge_for_mvno","CUSTOMER_DIRECT",0d);
            for (Integer mvnoId : mvnoIds) {
                List<Integer> chargeIdList=new ArrayList<>();
                List<DebitDocDetailDTO> debitDocDetailDTOS = new ArrayList<>();
                debitDocDetailDTOS = getDebitDocDetailByChargeAndMvno(mvnoId, false, firstDayOfLastMonth, lastDayOfLastMonth);
                Mvno mvno = mvnoRepository.findById(Long.valueOf(mvnoId)).orElse(null);
                if (mvno.getCustInvoiceRefId()!=null) {
                    Customers customers = customersRepository.findById(mvno.getCustInvoiceRefId()).orElse(null);
//                    List<CustChargeDetails> custChargeDetailsForRevenue = new ArrayList<>();
                    if (debitDocDetailDTOS.size() > 0) {
//                        for (DebitDocDetailDTO debitDocDetailDTO : debitDocDetailDTOS) {
                        CustChargeDetails custChargeDetails = new CustChargeDetails();
                        custChargeDetails.setCustomer(customers);
                        custChargeDetails.setChargeid(charge.getId());
                        custChargeDetails.setChargetype(debitDocDetailDTOS.get(0).getChargeType());
                        Double totalAmount = debitDocDetailDTOS.stream()
                                .mapToDouble(DebitDocDetailDTO::getTotalAmount)
                                .sum();
                        custChargeDetails.setActualprice(totalAmount);
                        Double price = totalAmount * (mvno.getIspCommissionPercentage() / 100);
                        custChargeDetails.setPrice(price);
                        custChargeDetails.setCreatedate(LocalDateTime.now());
                        custChargeDetails.setCharge_date(LocalDateTime.now());
                        custChargeDetails.setIs_reversed(false);
                        custChargeDetails.setIsUsed(false);
                        custChargeDetails.setType("one-time");
                        custChargeDetails.setBillingCycle(1);
                        custChargeDetails.setIsInvoiceToOrg(false);
                        custChargeDetails.setIsDeleted(false);
                        custChargeDetails.setBillTo("CUSTOMER");
                        custChargeDetails.setTaxId(charge.getTax().getId());
                        CustChargeDetails details = custChargeDetailsRepository.save(custChargeDetails);
                        chargeIdList.add(details.getId());
//                            custChargeDetailsForRevenue.add(details);

//                        }
                        ChangePlanMessage changePlanMessage = new ChangePlanMessage();
                        List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();
//                        for (CustChargeDetails data : custChargeDetailsForRevenue) {
                        CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(details);
                        custChargeDetailsRevenue.setIsRenew(false);
                        custChargeDetailsRevenues.add(custChargeDetailsRevenue);
//                        }
                        changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                        changePlanMessage.setCustChargeIds(chargeIdList);
                        changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                        if(getLoggedInUser()!=null && getLoggedInUser().getStaffId()!=null) {
                            changePlanMessage.setCreatedById(getLoggedInUser().getStaffId());
                        }
                        changePlanMessage.setIsMvnoCustomer(true);
                        changePlanMessage.setIspFromDate(firstDayOfLastMonth.toString());
                        changePlanMessage.setIspToDate(lastDayOfLastMonth.toString());
                        changePlanMessage.setDebitDocDetailIds(debitDocDetailDTOS.stream().map(i -> i.getDebitDocDetailId()).collect(Collectors.toList()));
                        changePlanMessageList.add(changePlanMessage);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(changePlanMessageList) && changePlanMessageList.size() > 0) {
                ChangePlanMessageList changePlanListMessage = new ChangePlanMessageList();
                changePlanListMessage.setChangePlanMessageList(changePlanMessageList);
                kafkaMessageSender.send(new KafkaMessageData(changePlanListMessage,ChangePlanMessageList.class.getSimpleName(),"DIRECT_CHARGLIST"));
                isInvoiceGenerated = true;
            }
            return isInvoiceGenerated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //thi service is used to generate isp invoice (bi-monthly billing)
    public Boolean ispInvoiceGenerateBiMonthly(LocalDate BillDate) {
        try {
            List<ChangePlanMessage> changePlanMessageList = new ArrayList<>();
            LocalDate today = (BillDate != null) ? BillDate : LocalDate.now();
            int day = today.getDayOfMonth();

            Integer ispDay = mvnoRepository.findIspDay();
            if (ispDay == null) {
                logger.error("ISP Day not found in Mvno table.");
                return false;
            }

            List<Integer> mvnoIds = mvnoRepository.findGenerationdayforBiMonthly(ispDay);
            if (mvnoIds.isEmpty()) {
                logger.info("No ISP invoices to generate for today: {}", today);
                return false;
            }

            Boolean isInvoiceGenerated = false;
            LocalDateTime firstDayOfBillingPeriod;
            LocalDateTime lastDayOfBillingPeriod;

            if (day == 16) {
                firstDayOfBillingPeriod = today.withDayOfMonth(1).atStartOfDay();
                lastDayOfBillingPeriod = today.withDayOfMonth(15).atTime(LocalTime.MAX);
            } else if(day == 1){
                LocalDate lastMonth = today.minusMonths(1);
                firstDayOfBillingPeriod = lastMonth.withDayOfMonth(16).atStartOfDay();
                lastDayOfBillingPeriod = lastMonth.withDayOfMonth(lastMonth.lengthOfMonth()).atTime(LocalTime.MAX);
            } else {
                return false;
            }

            logger.info("Generating invoices for period: {} - {}", firstDayOfBillingPeriod, lastDayOfBillingPeriod);

            Charge charge = chargeRepository.findByNameAndChargetypeAndPrice("org_charge_for_mvno","CUSTOMER_DIRECT",0d);
            for (Integer mvnoId : mvnoIds) {
                List<Integer> chargeIdList=new ArrayList<>();
                List<DebitDocDetailDTO> debitDocDetailDTOS = new ArrayList<>();
                debitDocDetailDTOS = getDebitDocDetailByChargeAndMvno(mvnoId, false, firstDayOfBillingPeriod, lastDayOfBillingPeriod);
                Mvno mvno = mvnoRepository.findById(Long.valueOf(mvnoId)).orElse(null);

                if (mvno.getCustInvoiceRefId()!=null) {
                    Customers customers = customersRepository.findById(mvno.getCustInvoiceRefId()).orElse(null);
                    if (debitDocDetailDTOS.size() > 0) {
                        CustChargeDetails custChargeDetails = new CustChargeDetails();
                        custChargeDetails.setCustomer(customers);
                        custChargeDetails.setChargeid(charge.getId());
                        custChargeDetails.setChargetype(debitDocDetailDTOS.get(0).getChargeType());
                        Double totalAmount = debitDocDetailDTOS.stream()
                                .mapToDouble(DebitDocDetailDTO::getTotalAmount)
                                .sum();
                        custChargeDetails.setActualprice(totalAmount);
                        Double price = totalAmount * (mvno.getIspCommissionPercentage() / 100);
                        custChargeDetails.setPrice(price);
                        custChargeDetails.setCreatedate(LocalDateTime.now());
                        custChargeDetails.setCharge_date(LocalDateTime.now());
                        custChargeDetails.setIs_reversed(false);
                        custChargeDetails.setIsUsed(false);
                        custChargeDetails.setType("one-time");
                        custChargeDetails.setBillingCycle(1);
                        custChargeDetails.setIsInvoiceToOrg(false);
                        custChargeDetails.setIsDeleted(false);
                        custChargeDetails.setBillTo("CUSTOMER");
                        custChargeDetails.setTaxId(charge.getTax().getId());
                        CustChargeDetails details = custChargeDetailsRepository.save(custChargeDetails);
                        chargeIdList.add(details.getId());

                        ChangePlanMessage changePlanMessage = new ChangePlanMessage();
                        List<CustChargeDetailsRevenue> custChargeDetailsRevenues = new ArrayList<>();

                        CustChargeDetailsRevenue custChargeDetailsRevenue = new CustChargeDetailsRevenue(details);
                        custChargeDetailsRevenue.setIsRenew(false);
                        custChargeDetailsRevenues.add(custChargeDetailsRevenue);

                        changePlanMessage.setCustChargeDetailsRevenues(custChargeDetailsRevenues);
                        changePlanMessage.setCustChargeIds(chargeIdList);
                        changePlanMessage.setType(CommonConstants.INVOICE_TYPE.CUSTOMER_CHARGE);
                        if(getLoggedInUser()!=null && getLoggedInUser().getStaffId()!=null) {
                            changePlanMessage.setCreatedById(getLoggedInUser().getStaffId());
                        }
                        changePlanMessage.setIsMvnoCustomer(true);
                        changePlanMessage.setIspFromDate(firstDayOfBillingPeriod.toString());
                        changePlanMessage.setIspToDate(lastDayOfBillingPeriod.toString());
                        changePlanMessage.setDebitDocDetailIds(debitDocDetailDTOS.stream().map(i -> i.getDebitDocDetailId()).collect(Collectors.toList()));
                        changePlanMessageList.add(changePlanMessage);
                    }
                }
            }

            if (!CollectionUtils.isEmpty(changePlanMessageList) && changePlanMessageList.size() > 0) {
                ChangePlanMessageList changePlanListMessage = new ChangePlanMessageList();
                changePlanListMessage.setChangePlanMessageList(changePlanMessageList);
                kafkaMessageSender.send(new KafkaMessageData(changePlanListMessage,ChangePlanMessageList.class.getSimpleName(),"DIRECT_CHARGLIST"));
                isInvoiceGenerated = true;
            }
            return isInvoiceGenerated;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }




    @Transactional
    public void saveDebitDocInCms(PrepaidInvoiceCharges dataMessage) {
        ApplicationLogger.logger.info("RabbitMq ReceivePrepaidCustomerInvoiceChargesDetail START:- "+LocalDateTime.now());
        try {
            DebitDocument debitDocument= null;

            if(dataMessage.getIsPaymentApproved()==false)
            {
                Customers customers = customersRepository.findById(dataMessage.getCustId()).get();
                customers.setWalletbalance(dataMessage.getWalletBalance());
                if (dataMessage.getNextBilldate()!=null && !dataMessage.getNextBilldate().isEmpty())
                {
                    LocalDate nextBilldate = LocalDate.parse(dataMessage.getNextBilldate());
                    LocalDate nextQuotaReset = customersService.getNextQuotaResetDate(customers, customers.getNextBillDate());
                    customers.setNextBillDate(nextBilldate);
                    if(nextQuotaReset != null) {
                        customers.setNextQuotaResetDate(nextQuotaReset);
                    } else {
                        customers.setNextQuotaResetDate(LocalDate.now());
                    }

                    // update nextQuotaResetDate in radius
                    CustomerNextQuotaUpdateMessage message = new CustomerNextQuotaUpdateMessage(customers);
                    kafkaMessageSender.send(new KafkaMessageData(message, message.getClass().getSimpleName()));

                    if (dataMessage.getChildIdNextBillDatePair()!=null && dataMessage.getChildIdNextBillDatePair().size()>0)
                    {
                        //List<Customers> childCusts = new ArrayList<>();
                        for (Map.Entry<Integer, String> childIdAndDate : dataMessage.getChildIdNextBillDatePair()) {
                            LocalDateTime nextBillDate = DateTimeUtil.getLocaldateTimefromString(childIdAndDate.getValue());
                            Customers childCustomer = customersRepository.findById(childIdAndDate.getKey()).get();
                            childCustomer.setNextBillDate(nextBillDate.toLocalDate());
                            LocalDate nextQuotaResetChild = customersService.getNextQuotaResetDate(childCustomer, childCustomer.getNextBillDate());
                            if(nextQuotaReset != null) {
                                childCustomer.setNextQuotaResetDate(nextQuotaResetChild);
                            } else {
                                childCustomer.setNextQuotaResetDate(LocalDate.now());
                            }
                            customersRepository.updateNextBillDate(childCustomer.getId(),childCustomer.getNextBillDate(),childCustomer.getNextQuotaResetDate());
                            //childCusts.add(childCustomer);
                        }
                        //customersRepository.saveAll(childCusts);
                    }
                }

                customersRepository.updateWalletAndNextBillDateAndNextQuotaResetDate(customers.getId(),customers.getWalletbalance(),customers.getNextBillDate(),customers.getNextQuotaResetDate());
                //customersRepository.save(customers);
                sendBillGenData(Math.toIntExact(dataMessage.getInvoiceId()),false);
                String orgainzationCustomerUsername = clientServiceSrv.getValueByNameAndmvnoId("ORGANIZATION",customers.getMvnoId());

                if (dataMessage.getIsCaf() == null)
                    dataMessage.setIsCaf("false");

                if (dataMessage.getCustomerUsername() != null || dataMessage.getCustomerName() != null)
                {
                    Integer userId = dataMessage.getLoggedInUserId() != null ? dataMessage.getLoggedInUserId() : customers.getCreatedById();
                    debitDocument=this.getdebitDocument(dataMessage.getDebitDocument());
                    debitDocument.setCreatedById(userId);
                    debitDocument.setNextStaff(userId);
                    debitDocument.setLastModifiedById(userId);
                    debitDocument.setDebitDocumentTAXRels(null);
                    debitDocument.setCustomer(null);
                    debitDocument.setPaymentStatus(dataMessage.getPaymentStatus());
                    debitDocument.setBillrunid(null);
                    debitDocument.setCreatedByName(dataMessage.getCreatedByName());
                    debitDocument.setIsDirectChargeInvoice(dataMessage.getIsDirectChargeInvoice());
                    debitDocument.setRemarks(dataMessage.getDebitDocument().getRemarks());
                    debitDocument.setBillrunstatus(dataMessage.getBillRunStatus());

                    Integer planId=null;
                    if(customers!=null)
                        debitDocument.setBuId(customers.getBuId());
                    if (debitDocument.getCustpackrelid()!=null  ) {
                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById1(debitDocument.getCustpackrelid());
                        if(custPlanMappping!=null && Objects.nonNull(custPlanMappping)) {
                            custPlanMappingRepository.updateCustPlanMapping(custPlanMappping.getId(),debitDocument.getId().longValue());
                            //PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(custPlanMappping.getPlanId()).orElse(null);
                            //debitDocument.setPostpaidPlan(postpaidPlan);
                            planId=custPlanMappping.getPlanId();
                        }
                    }

//                    if(debitDocument.getCreatedById()==null)
//                    {
//                        debitDocument.setCreatedById(customers.getCreatedById());
//                        debitDocument.setLastModifiedById(customers.getLastModifiedById());
//                    }

                    debitDocument  = debitDocRepository.save(debitDocument);
                    if(planId!=null)
                        debitDocRepository.updateCustomer(debitDocument.getId(),customers.getId(),planId);
                    else
                        debitDocRepository.updateCustomer(debitDocument.getId(),customers.getId());

                    List<DebitDocDetails> updatedList = new ArrayList<>();
                    if(dataMessage.getDebitDocument().getDebitDocDetailsList()!=null){
                        updatedList = this.getDebitdocDetails(dataMessage.getDebitDocument().getDebitDocDetailsList());
                        debitDocDetailRepository.saveAll(updatedList);
                    }

                    if (orgainzationCustomerUsername!=null && dataMessage.getCustomerUsername()!=null && (dataMessage.getCustomerUsername().equalsIgnoreCase(orgainzationCustomerUsername) || dataMessage.getCustomerName().equalsIgnoreCase(orgainzationCustomerUsername))) {
                        sendOrganizatonBillForApprovAL(dataMessage.getInvoiceId(), dataMessage.getLoggedInUserId(),debitDocument);
                        dataMessage.setOrgCust(true);
                    }

                    if (dataMessage.getDebitDocument()!=null && dataMessage.getDebitDocument().getUpdateDebitDpcDetailsIds()!=null)
                        updateDebitDocDetailsForMvnoInvoice(dataMessage.getDebitDocument().getUpdateDebitDpcDetailsIds(), debitDocument.getId());
                }

                if (dataMessage.getOldDebitDocumentId() != null) {
                    if (dataMessage.getOldDebitDocumentId().size() > 0 && !dataMessage.getIsCaf().equals("true")) {
                        adjustOldDebitDocument(dataMessage.getInvoiceId(), dataMessage.getOldDebitDocumentId());
                    }
                }

                //customersRepository.save(customers);

                List<CustPlanMappping> custPlanMapppingList = new ArrayList<>();
                if (dataMessage.getCustPackAndDebitDocIdPair()!=null && dataMessage.getCustPackAndDebitDocIdPair().size()>0)
                {
                    for (Map.Entry<Integer, Long> custPackAndDebitDocIdPair : dataMessage.getCustPackAndDebitDocIdPair()) {
                        CustPlanMappping custPlanMappping = custPlanMappingRepository.findById2(custPackAndDebitDocIdPair.getKey().intValue());
                        if(custPackAndDebitDocIdPair.getValue()!=null)
                        {
                            custPlanMappping.setDebitdocid(custPackAndDebitDocIdPair.getValue());
                        }else{
                            custPlanMappping.setDebitdocid(debitDocument.getId().longValue());
                        }
                        custPlanMapppingList.add(custPlanMappping);
                    }

                    if (dataMessage.getCustPackAndEndDatePair()!=null && dataMessage.getCustPackAndEndDatePair().size()>0){
                        for (Map.Entry<Integer, String> custPackAndEndDate : dataMessage.getCustPackAndEndDatePair()) {
                            for (CustPlanMappping custPlanMappping : custPlanMapppingList){
                                if(custPlanMappping.getId().equals(custPackAndEndDate.getKey())){
                                    LocalDateTime endDate = DateTimeUtil.getLocaldateTimefromString(custPackAndEndDate.getValue());
                                    custPlanMappping.setEndDate(endDate);
                                    custPlanMappping.setExpiryDate(endDate);
                                }
                            }
                        }
                    }

                    if(custPlanMapppingList!=null && !custPlanMapppingList.isEmpty())
                    {
                        custPlanMapppingList.stream().forEach(custPlanMappping -> {
                            custPlanMappingRepository.updateCustPlanMapping(custPlanMappping.getId(),custPlanMappping.getDebitdocid(),custPlanMappping.getEndDate(),custPlanMappping.getExpiryDate());
                        });
                    }
                }
            }

            if(debitDocument == null)
                debitDocument=debitDocRepository.findById(dataMessage.getDebitDocument().getId()).orElse(null);

            if(debitDocument!=null && dataMessage.getDebitDocument().getBillrunstatus().equalsIgnoreCase("Cancelled")){
                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
                        custChargeDetails.setIsDeleted(true);
                        custChargeDetailsRepository.save(custChargeDetails);
                    }
                }
            }

            if(debitDocument!=null &&  dataMessage.getPaymentStatus()!=null && dataMessage.getPaymentStatus().equalsIgnoreCase("Cancelled")){
                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllByDebitdocid(Long.valueOf(debitDocument.getId()));
                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
                    for (CustChargeDetails custChargeDetails: custChargeDetailsList) {
                        custChargeDetails.setEnddate(debitDocument.getCreatedate().minusMinutes(1));
                        custChargeDetails.setIsDeleted(true);
                        custChargeDetailsRepository.save(custChargeDetails);
                    }
                }
            }

            if(debitDocument != null && dataMessage.getIsVoid() != null && dataMessage.getIsVoid())
            {
                List<CustPlanMappping> mapppings = custPlanMappingRepository.findAllByDebitdocumentid(debitDocument.getId().longValue());
                debitDocument.setBillrunstatus("VOID");
                debitDocRepository.save(debitDocument);
                if (!CollectionUtils.isEmpty(mapppings)) {
                    for (CustPlanMappping mapping : mapppings) {
                        mapping.setIsVoid(Boolean.TRUE);
                        mapping.setEndDate(LocalDateTime.now());
                        mapping.setExpiryDate(LocalDateTime.now());
                        if (mapping.getStartDate().isAfter(mapping.getEndDate())) {
                            mapping.setStartDate(LocalDateTime.now());
                            mapping.setEndDate(mapping.getStartDate().plusSeconds(1));
                            mapping.setExpiryDate(mapping.getStartDate().plusSeconds(1));
                        }
                        mapping.setCustPlanStatus(CommonConstants.STOP_STATUS);
                        custPlanMappingService.save(mapping, CommonConstants.CHANGE_PLAN_MSG);
                    }
                }
            }
            else
            {
                if(dataMessage.getDebitDocument()!=null && dataMessage.getDebitDocument().getId()!=null) {
                    DebitDocument document=debitDocRepository.findById(dataMessage.getDebitDocument().getId()).orElse(null);
                    if(dataMessage.getIsPaymentApproved()!=false) {
                        document.setAdjustedAmount(dataMessage.getAdjustedAmount());
                        document.setBillrunstatus(dataMessage.getBillRunStatus());
                        document.setPaymentStatus(dataMessage.getPaymentStatus());
                        if (document.getPaymentStatus().equalsIgnoreCase(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)) {
                            document.setStatus(CommonConstants.DEBIT_DOC_STATUS.APPROVED);
                        }
                        document.setRemarks(dataMessage.getDebitDocument().getRemarks());
                        debitDocRepository.save(document);
                    }

                    if(document.getPaymentStatus().equalsIgnoreCase("Fully Paid") ){
                        List<CustPlanMappping> custPlanMapppingList1=custPlanMappingRepository.findAllByDebitdocid(document.getId());
                        List<Integer> cprIds = custPlanMapppingList1.stream().filter(list->list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE )).map(mappping -> mappping.getId() ).collect(Collectors.toList());
                        customerDocDetailsService.changeStatusDisableToActive(cprIds);
                    }
                }
            }

            if(!CollectionUtils.isEmpty(dataMessage.getChargeIds()) && debitDocument != null) {
                List<CustChargeDetails> custChargeDetailsList = custChargeDetailsRepository.findAllById(dataMessage.getChargeIds());
                if(!CollectionUtils.isEmpty(custChargeDetailsList)) {
                    DebitDocument finalDebitDocument = debitDocument;
                    custChargeDetailsList = custChargeDetailsList.stream().peek(custChargeDetails -> custChargeDetails.setDebitdocid(Long.valueOf(finalDebitDocument.getId()))).collect(Collectors.toList());
                    custChargeDetailsRepository.saveAll(custChargeDetailsList);
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error("RabbitMq receive Error receivePrepaidCustomerInvoiceChargesDetail() ", APIConstants.FAIL, e.getStackTrace());
        }

        ApplicationLogger.logger.info("RabbitMq receivePrepaidCustomerInvoiceChargesDetail END:- "+LocalDateTime.now());
    }

    private DebitDocument getdebitDocument(DebitDocument debitDocument) {

        DebitDocument debitDocument1=new DebitDocument();
        debitDocument1.setId(debitDocument.getId());
        debitDocument1.setDocument(debitDocument.getDocument());
        DateTimeFormatter formatter =  DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        debitDocument1.setLocalenddate( debitDocument.getLocalenddate());
        debitDocument1.setLocalstartdate(debitDocument.getLocalstartdate());
        debitDocument1.setStartdate(LocalDateTime.parse(debitDocument.getLocalstartdate(),formatter));

//        debitDocument1.setLocalenddate(debitDocument.getEndate().format(formatter).toString());
        debitDocument1.setDuedate(LocalDateTime.parse(debitDocument.getDuedateString(),formatter));
        //  debitDocument1.setLatepaymentdateString(debitDocument.getLatepaymentdate().format(formatter).toString());
        debitDocument1.setSubtotal(debitDocument.getSubtotal());
        debitDocument1.setTax(debitDocument.getTax());
        debitDocument1.setDiscount(debitDocument.getDiscount());
        debitDocument1.setTotalamount(debitDocument.getTotalamount());
        debitDocument1.setPreviousbalance(debitDocument.getPreviousbalance());
        debitDocument1.setLatepaymentfee(debitDocument.getLatepaymentfee());
        debitDocument1.setCurrentpayment(debitDocument.getCurrentpayment());
        debitDocument1.setCurrentdebit(debitDocument.getCurrentdebit());
        debitDocument1.setCurrentcredit(debitDocument.getCurrentcredit());
        debitDocument1.setTotaldue(debitDocument.getTotaldue());
        //       debitDocument.setTotalamountinwords(debitDocument.getAmountinwords());
        // debitDocument.dueinwords=debitDocument.getDueinwords();
        debitDocument1.setBillrunid(debitDocument.getBillrunid());
        //debitDocument1.setBillrunstatus(debitDocument.getBillrunstatus());
        debitDocument1.setStatus(debitDocument.getStatus());
        debitDocument1.setIsDelete(debitDocument.getIsDelete());
        debitDocument1.setCstchargeid(debitDocument.getCstchargeid());
        debitDocument1.setPaymentowner(debitDocument.getPaymentowner());
        debitDocument1.setDebitDocumentTAXRels(debitDocument.getDebitDocumentTAXRels());
        //       debitDocument1.setDebitDocDetailsList(debitDocument.getDebitDocDetailsList());
        debitDocument1.setDocnumber(debitDocument.getDocnumber());
        //debitDocument1.setTotalCustomerDiscount(debitDocument.getCustomer().getId().doubleValue());
        debitDocument1.setDocnumber(debitDocument.getDocnumber());
        debitDocument1.setCustRefName(debitDocument.getCustRefName());
        debitDocument1.setCustpackrelid(debitDocument.getCustpackrelid());
        debitDocument1.setEndate(LocalDateTime.parse(debitDocument.getLocalenddate(),formatter));
        debitDocument1.setRemarks(debitDocument.getRemarks());
        //  debitDocument.inventoryMappingId=debitDocument.getInventoryMappingId();
        return debitDocument1;
    }
    private List<DebitDocDetails> getDebitdocDetails(List<DebitDocDetails> debitDocDetailsList) {
        List<DebitDocDetails> updatedList = new ArrayList<>();
        for(DebitDocDetails debitDocDetail : debitDocDetailsList) {
            debitDocDetail.setStartdate(null);
            debitDocDetail.setEnddate(null);
            updatedList.add(debitDocDetail);
        }
        return updatedList;
    }

    public void updateDebitDocDetailsForMvnoInvoice(List<Integer> debitDocDetailsIds, Integer mvnoDocId) {
        try {
            List<DebitDocDetails> debitDocDetails = debitDocDetailRepository.findAllByDebitdocdetailidIn(debitDocDetailsIds);
            debitDocDetails= debitDocDetails.stream().peek(i->i.setMvnodebitdocumentid(mvnoDocId)).collect(Collectors.toList());
            debitDocDetailRepository.saveAll(debitDocDetails);
        } catch (Exception ex) {
            logger.error("Exception to update mvnoDocId in customer invoices: "+ex.getMessage());
        }
    }

    public void updateDebitdocGraceDay(UpdateDebitdocGraceDayMessage dataMessage) throws Exception {
        try {
            if(dataMessage.getDebitDocId() != null){
                Optional<DebitDocument> optional = debitDocRepository.findById(dataMessage.getDebitDocId());
                if (!optional.isPresent()) {
                    throw new Exception("DebitDocument ID  not found.");
                }
                DebitDocument doc = optional.get();
                doc.setDebitdocGraceDays(dataMessage.getDebitDocGraceDays() != null ? dataMessage.getDebitDocGraceDays() : 0);
                debitDocRepository.save(doc);
                logger.info("Grace days updated successfully: "+dataMessage.getDebitDocId());
            }
        } catch (Exception e){
            e.printStackTrace();
            logger.error("Exception to update mvnoDocId in customer invoices: "+dataMessage.getDebitDocId());
        }
    }


    @Transactional
    public void updateDebitDocuments(List<DebitDocumentDTOForAdjustment> debitDocs) {
        if (debitDocs.isEmpty()) return;

        List<Integer> idToAdjustedAmount = debitDocs.stream().filter(i->!i.getStatus().isEmpty() && i.getStatus().equalsIgnoreCase("Fully Paid") )
                .map(DebitDocumentDTOForAdjustment::getId) // extract id
                .collect(Collectors.toList());
        entityManager.createQuery("UPDATE DebitDocument d SET d.paymentStatus = :status,d.adjustedAmount = d.totalamount WHERE d.id IN :ids")
                .setParameter("status", CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID)
                .setParameter("ids", idToAdjustedAmount)
                .executeUpdate();

        for(DebitDocumentDTOForAdjustment document:debitDocs) {
                  if (document.getStatus().equalsIgnoreCase("Fully Paid")) {
                      List<CustPlanMappping> custPlanMapppingList1 = custPlanMappingRepository.findAllByDebitdocid(document.getId());
                      List<Integer> cprIds = custPlanMapppingList1.stream().filter(list -> list.getCustPlanStatus().equalsIgnoreCase(StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE)).map(mappping -> mappping.getId()).collect(Collectors.toList());
                      customerDocDetailsService.changeStatusDisableToActive(cprIds);

                  }
              }

//        entityManager.createQuery("UPDATE DebitDocument d SET d.adjustedAmount = CASE d.id " + idToAdjustedAmount.entrySet().stream().map(entry -> "WHEN " + entry.getKey() + " THEN " + entry.getValue()).collect(Collectors.joining(" ")) + " ELSE d.adjustedAmount END WHERE d.id IN (:ids)").setParameter("ids", idToAdjustedAmount.keySet()).executeUpdate();
    }

}
