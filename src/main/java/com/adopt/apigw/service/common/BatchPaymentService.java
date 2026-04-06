package com.adopt.apigw.service.common;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.QCreditDebitDocMapping;
import com.adopt.apigw.model.postpaid.QDebitDocument;
import com.adopt.apigw.modules.BankManagement.domain.BankManagement;
import com.adopt.apigw.modules.BankManagement.domain.QBankManagement;
import com.adopt.apigw.modules.BankManagement.repository.BankManagementRepository;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.repository.common.BatchPaymentAssignmentRepository;
import com.adopt.apigw.repository.common.BatchPaymentMappingRepository;
import com.adopt.apigw.repository.common.BatchPaymentRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BatchPaymentService extends AbstractService<BatchPayment, BatchPaymentPojo, Long> {

    @Autowired
    private BatchPaymentRepository batchPaymentRepository;

    @Autowired
    private BatchPaymentMappingRepository batchPaymentMappingRepository;

    @Autowired
    private BatchPaymentAssignmentRepository batchPaymentAssignmentRepository;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private BatchPaymentAssignmentService batchPaymentAssignmentService;

    @Autowired
    private BatchPaymentMappingService batchPaymentMappingService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BankManagementRepository bankManagementRepository;

    public static final String MODULE = "[BatchPaymentService]";

    @Override
    protected JpaRepository<BatchPayment, Long> getRepository() {
        return batchPaymentRepository;
    }


    public boolean isPaymentBatchAlreadyExists(String paymentBatchName) {
        QBatchPayment batchPayment = QBatchPayment.batchPayment;
        BooleanExpression expression = batchPayment.isNotNull().and(batchPayment.batchname.eq(paymentBatchName).and(batchPayment.isDeleted.eq(false)));
        Long count = batchPaymentRepository.count(expression);
        return count > 0;
    }

    public GenericDataDTO save(BatchPaymentPojo pojo)  throws Exception{

        String SUBMODULE = MODULE + "[save()]";
        GenericDataDTO genericDataDTO=new GenericDataDTO();
        try {
            BatchPayment obj = convertBatchPaymentPojoToBatchPaymentModel(pojo);
            if (getBUIdsFromCurrentStaff().size() == 1)
                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
            obj = saveBatchPayment(obj);
            if (pojo.getAssignedStatus().equals(APIConstants.BATCH_PAYMENT_ASSIGNED)) {
                batchPaymentAssignmentService.assignBatchPayment(obj, getLoggedInUser().getStaffId(), pojo.getAssignedStatus());
            }
            genericDataDTO.setResponseCode(APIConstants.SUCCESS);
            genericDataDTO.setResponseMessage("Success");
        }catch (Exception e){
            genericDataDTO.setResponseCode(APIConstants.FAIL);
            genericDataDTO.setResponseMessage(e.getMessage());
            e.printStackTrace();

        }
        return genericDataDTO;

    }

    public BatchPayment convertBatchPaymentPojoToBatchPaymentModel(BatchPaymentPojo batchPaymentPojo) throws Exception {
        String SUBMODULE = MODULE + "[covertBatchPaymentPojoToBatchPaymentModel()]";
        List<BatchPaymentMapping> batchPaymentMapping = new ArrayList<>();
        BatchPayment batchPayment = new BatchPayment();
        try{

        if (batchPaymentPojo != null) {
            batchPayment.setBatchname(batchPaymentPojo.getBatchname());
            batchPayment.setIsDeleted(false);
            batchPayment.setStatus("Pending");
            if (getLoggedInUser() != null && getLoggedInUser().getStaffId() != null) {
                StaffUser staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).get();
                batchPayment.setCreateBy(staffUser.getUsername());
            }
            if (batchPaymentPojo.getBatchPaymentMappingList() != null && batchPaymentPojo.getBatchPaymentMappingList().size() > 0) {
                List<Integer> allBatchPaymentMappingList = batchPaymentMappingRepository.findAll().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
                List<Integer> creditDocumentIds = creditDocRepository.findAllCreditDocID();

                for (BatchPaymentMappingPojo oldMapping : batchPaymentPojo.getBatchPaymentMappingList()) {
                    if (creditDocumentIds.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString()))) {
                        if (allBatchPaymentMappingList.contains(Integer.parseInt(oldMapping.getCredit_doc_id().toString())))
                            throw new CustomValidationException(APIConstants.FAIL,"Unable save, Found duplicate CreditDocument entry under Batch Payment Mapping.",null);
                    } else
                        throw new CustomValidationException(APIConstants.FAIL,"Unable save, No Such a CreditDocument found with Id " + oldMapping.getCredit_doc_id()+"+",null);
                    BatchPaymentMapping mapping = new BatchPaymentMapping();
                    CreditDocument document = new CreditDocument();
                    document.setId(Integer.parseInt(oldMapping.getCredit_doc_id().toString()));
                    mapping.setCreditDocument(document);
                    mapping.setBatchPayment(batchPayment);
                    batchPaymentMapping.add(mapping);
                }
                batchPayment.setBatchPaymentMappingList(batchPaymentMapping);
                return batchPayment;
            } else
                throw new Exception("No CreditDocument Selected");
        }
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }

        return null;
    }

    public BatchPayment saveBatchPayment(BatchPayment batchPayment) throws Exception {
        String SUBMODULE = MODULE + "[saveBatchPayment()]";
        try {
            BatchPayment save = batchPaymentRepository.save(batchPayment);
            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public BatchPayment getById(Long batchPaymentId) {
        String SUBMODULE = MODULE + "[getById()]";
        try {
            Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchPaymentId);
            return batchPayment.get();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<BatchPaymentDetailPojo> getBatchPaymentDetailListByStaffId(Long staffId) {
        String SUBMODULE = MODULE + "[getBatchPaymentDetailListByStaffId()]";
        List<BatchPaymentDetailPojo> list = new ArrayList<>();
        List<BatchPaymentAssignment> batchPaymentAssignments = new ArrayList<>();
        try {
            if (staffId != null && getLoggedInUser().getStaffId().toString().equals(staffId.toString())) {
                batchPaymentAssignments = batchPaymentAssignmentRepository.findByStaffId(staffId);
                batchPaymentAssignments = getAssignment(batchPaymentAssignments);
                batchPaymentAssignments.forEach(data -> {
                    if (!data.getBatchPayment().getIsDeleted()) {
                        BatchPaymentDetailPojo detailPojo = new BatchPaymentDetailPojo();
                        detailPojo.setBatchId(data.getBatchPayment().getId());
                        detailPojo.setBatchName(data.getBatchPayment().getBatchname());
                        detailPojo.setAssignee(data.getStaffUser().getUsername());
                        if (data.getAssignedStatus().equalsIgnoreCase("AssignedToOtherTeam")) {
                            detailPojo.setAssignmentStatus("AssignedToOtherTeam");
                        } else {
                            detailPojo.setAssignmentStatus(data.getStatus());
                        }
                        detailPojo.setBatchStatus(data.getBatchPayment().getStatus());
                        detailPojo.setCreatedBy(data.getBatchPayment().getCreateBy());
                        detailPojo.setStaffId(data.getStaffUser().getId());
                        if (data.getRemark() != null) {
                            detailPojo.setRemarks(data.getRemark());
                        }
                        detailPojo.setNextStaffId(data.getNextStaffUser() != null ? data.getNextStaffUser().getId() : null);
                        List<BatchPaymentMapping> mappings = data.getBatchPayment().getBatchPaymentMappingList().stream().filter(x -> x.getIs_deleted().equals(false)).collect(Collectors.toList());
                        detailPojo.setInvoiceCount(String.valueOf(mappings.size()));
                        detailPojo.setTotalAmount(Double.toString(mappings.stream().mapToDouble(x -> x.getCreditDocument().getAmount()).sum()));
                        detailPojo.setCreditDocumentList(mappings.stream().map(x -> batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(x)).collect(Collectors.toList()));
                        if(mappings.get(0).getCreditDocument().getFilename() != null && mappings.get(0).getCreditDocument().getFilename().length() > 0){
                            detailPojo.setFilename(mappings.get(0).getCreditDocument().getFilename());
                        }
                        if(mappings.get(0).getCreditDocument() != null){
                            detailPojo.setCreditDocId(mappings.get(0).getCreditDocument().getId());
                            detailPojo.setCustId(mappings.get(0).getCreditDocument().getCustomer().getId());
                        }
                        if(data.getNextStaffUser() != null){
                           // detailPojo.setAssignedName(data.getNextStaffUser().getUsername());
                            detailPojo.setNextstaffname(data.getNextStaffUser().getUsername());
                        }
                        list.add(detailPojo);

                    }
                });
            }
            return list;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    private List<BatchPaymentDetailPojo> convetBatchPaymentToPojo(List<BatchPaymentAssignment> batchPayments) {
        List<BatchPaymentDetailPojo> list = new ArrayList<>();
        batchPayments.forEach(data -> {
            BatchPaymentDetailPojo detailPojo = new BatchPaymentDetailPojo();
            detailPojo.setBatchId(data.getBatchPayment().getId());
            detailPojo.setBatchName(data.getBatchPayment().getBatchname());
            detailPojo.setAssignee(data.getStaffUser().getUsername());
            if (data.getAssignedStatus().equalsIgnoreCase("AssignedToOtherTeam")) {
                detailPojo.setAssignmentStatus("AssignedToOtherTeam");
            } else {
                detailPojo.setAssignmentStatus(data.getStatus());
            }
            detailPojo.setBatchStatus(data.getBatchPayment().getStatus());
            detailPojo.setCreatedBy(data.getBatchPayment().getCreateBy());
            detailPojo.setStaffId(data.getStaffUser().getId());
            if (data.getRemark() != null) {
                detailPojo.setRemarks(data.getRemark());
            }
            detailPojo.setNextStaffId(data.getNextStaffUser() != null ? data.getNextStaffUser().getId() : null);
            detailPojo.setNextstaffname(data.getNextStaffUser() != null ? data.getNextStaffUser().getUsername() : null);
            List<BatchPaymentMapping> mappings = data.getBatchPayment().getBatchPaymentMappingList().stream().filter(x -> x.getIs_deleted().equals(false)).collect(Collectors.toList());
            detailPojo.setInvoiceCount(String.valueOf(mappings.size()));
            detailPojo.setTotalAmount(Double.toString(mappings.stream().mapToDouble(x -> x.getCreditDocument().getAmount()).sum()));
            detailPojo.setCreditDocumentList(mappings.stream().map(x -> batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(x)).collect(Collectors.toList()));
            list.add(detailPojo);
        });
        return list;
    }

    public List<BatchPaymentDetailPojo> serachBatch(SearchBatchPaymentPojo searchBatchPaymentPojo, PaginationRequestDTO requestDTO) {
        List<BatchPaymentDetailPojo> batchPaymentDetailPojos = new ArrayList<>();
        try {
            if (searchBatchPaymentPojo != null) {
//                SearchPayment payment = this.convertSearchPaymentPojoToSearchPayment(searchPaymentPojo);
                batchPaymentDetailPojos = findBatchPayments(searchBatchPaymentPojo, requestDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return batchPaymentDetailPojos;
    }

    public List<BatchPaymentDetailPojo> findBatchPayments(SearchBatchPaymentPojo search, PaginationRequestDTO requestDTO) {
        List<BatchPaymentDetailPojo> batchPaymentPojos = new ArrayList<>();
        try {
            QBatchPaymentMapping qBatchPaymentMapping = QBatchPaymentMapping.batchPaymentMapping;
//        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
            QDebitDocument qDebitDocument = QDebitDocument.debitDocument;
            QCreditDebitDocMapping qCreditDebitDocMapping = QCreditDebitDocMapping.creditDebitDocMapping;
            BooleanExpression exp = qBatchPaymentMapping.isNotNull();


//        if (search.getType() != null && !"null".equals(search.getType()) && !"".equals(search.getType())) {
//            exp = exp.and(qBatchPaymentMapping.creditDocument.type.startsWithIgnoreCase(search.getType()));
//            if (search.getType().equalsIgnoreCase("payment")) {
//                exp = exp.or(qBatchPaymentMapping.creditDocument.paytype.startsWithIgnoreCase(CommonConstants.CREDIT_DOC_STATUS.WITHDRAWAL));
//            }
//        }

            System.out.println("search.getStatus() :::: " + search.getStatus());
            if (search.getStatus() != null && search.getStatus().size() > 0) {
                exp = exp.and(qBatchPaymentMapping.batchPayment.status.in(search.getStatus()));
            }
            if (search.getStaff() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.createdById.eq(search.getStaff()));
            }

            if (search.getBranch() != null && !StringUtils.isEmpty(search.getBranch())) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.customer.branch.eq(Long.valueOf(search.getBranch())));
            }

            if (search.getFromDate() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.paymentdate.after(search.getFromDate().minusDays(1)));
            }
            if (search.getToDate() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.paymentdate.before(search.getToDate().plusDays(1)));
            }
//            if (search.getFromDate() != null && search.getToDate() != null) {
//                exp = exp.and(qBatchPaymentMapping.creditDocument.createdate.between(search.getFromDate().atStartOfDay(), search.getToDate().plusDays(1).atStartOfDay().minusSeconds(1)));
//            } else if (search.getToDate() != null) {
//                exp = exp.and(qBatchPaymentMapping.creditDocument.createdate.before(search.getToDate().plusDays(1).atStartOfDay().minusSeconds(1)));
//            } else if (search.getFromDate() != null) {
//                exp = exp.and(qBatchPaymentMapping.creditDocument.createdate.after(search.getFromDate().atStartOfDay()));
//            }

            if (search.getPartner() != null) {
                exp = exp.and(qBatchPaymentMapping.creditDocument.customer.partner.id.eq(search.getPartner()));
            }
            if (search.getDestinationBank() != null) {
                QBankManagement qBankManagement = QBankManagement.bankManagement;
                BooleanExpression be = qBankManagement.isNotNull().and(qBankManagement.isDeleted.eq(false)).and(qBankManagement.id.eq(Long.valueOf(search.getDestinationBank())));
                Optional<BankManagement> bankManagement = bankManagementRepository.findOne(be);

                if (bankManagement != null) {
                    exp = exp.and(qBatchPaymentMapping.creditDocument.destinationBank.eq(bankManagement.get().getId()));
                }
            }

            if (search.getServiceArea() != null) {
                if (qBatchPaymentMapping.creditDocument.customer.servicearea != null)
                    exp = exp.and(qBatchPaymentMapping.creditDocument.customer.servicearea.id.eq(Long.valueOf(search.getServiceArea())));
            } else {
                if (getLoggedInUserId() != 1) {
                    if (getServiceAreaIdList() != null && !getServiceAreaIdList().isEmpty()) {
//                        QCustomers qCustomers = QCustomers.customers;
//                        BooleanExpression custEx = qCustomers.isNotNull().and()
                        QServiceArea qServiceArea = qBatchPaymentMapping.creditDocument.customer.servicearea;
                        if (qBatchPaymentMapping.creditDocument.customer.servicearea != null)
                            exp = exp.and(qServiceArea.isNotNull().and(qServiceArea.id.in(getServiceAreaIdList())));
                    }
                }
            }
            exp = exp.and(qBatchPaymentMapping.creditDocument.isDelete.eq(false)).and(qBatchPaymentMapping.creditDocument.customer.isDeleted.eq(false));
            exp=exp.and(qBatchPaymentMapping.is_deleted.eq(false));
            // TODO: pass mvnoID manually 6/5/2025
            if (getLoggedInMvnoId(null) != 1)
                // TODO: pass mvnoID manually 6/5/2025
                exp = exp.and(qBatchPaymentMapping.creditDocument.mvnoId.eq(getLoggedInMvnoId(null)));
            if (!CollectionUtils.isEmpty(getBUIdsFromCurrentStaff()) && getBUIdsFromCurrentStaff().size() > 0)
                exp = exp.and(qBatchPaymentMapping.creditDocument.buID.in(getBUIdsFromCurrentStaff()));
            List<BatchPaymentMapping> batchPaymentMappings = (List<BatchPaymentMapping>) batchPaymentMappingRepository.findAll(exp);
            List<Long> batchIds = batchPaymentMappings.stream().map(batchPaymentMapping -> batchPaymentMapping.getBatchPayment().getId()).distinct().collect(Collectors.toList());

            QBatchPaymentAssignment qBatchPaymentAssignment = QBatchPaymentAssignment.batchPaymentAssignment;
            BooleanExpression expression = qBatchPaymentAssignment.isNotNull();
            if (search.getStaff() != null) {
                expression = expression.and(qBatchPaymentAssignment.batchPayment.id.in(batchIds).and(qBatchPaymentAssignment.staffUser.id.eq(search.getStaff())));
            } else {
                expression = expression.and(qBatchPaymentAssignment.batchPayment.id.in(batchIds).and(qBatchPaymentAssignment.staffUser.id.eq(getLoggedInUser().getStaffId())));
            }
            List<BatchPaymentAssignment> batchPaymentAssignments = (List<BatchPaymentAssignment>) batchPaymentAssignmentRepository.findAll(expression);
            batchPaymentPojos = convetBatchPaymentToPojo(getAssignment(batchPaymentAssignments));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        QBatchPayment qBatchPayment = QBatchPayment.batchPayment;
//        BooleanExpression batchExp = qBatchPayment.isNotNull().and(qBatchPayment.id.in(batchIds));
//        Predicate builder1 = batchExp;
//        if (getMvnoIdFromCurrentStaff() != 1) {
//            if (pageRequest != null) {
//                return batchPaymentRepository.findAll(batchExp, pageRequest).stream().collect(Collectors.toList());
//            } else {
//                return (List<BatchPayment>) batchPaymentRepository.findAll(builder1);
//            }
//        }
        return batchPaymentPojos;
    }

    private List<BatchPaymentAssignment> getAssignment(List<BatchPaymentAssignment> batchPaymentAssignments) {
        List<BatchPaymentAssignment> assignmentList = new ArrayList<>();
        List<Long> batchIdList = batchPaymentAssignments.stream().map(x -> x.getBatchPayment().getId()).distinct().collect(Collectors.toList());
        batchIdList.forEach(x -> {
            List<BatchPaymentAssignment> tmp = batchPaymentAssignments.stream().filter(y -> y.getBatchPayment().getId().equals(x)).collect(Collectors.toList());
            assignmentList.add(tmp.get(tmp.size() - 1));
        });
        return assignmentList;
    }

    public List<BatchPaymentAuditDetails> getBatchPaymentAuditDetail(Long batchId) throws Exception {
        String SUBMODULE = MODULE + "[getBatchPaymentAuditDetail()]";
        List<BatchPaymentAuditDetails> details = new ArrayList<>();
        if (batchId != null) {
            List<BatchPaymentAssignment> assignmentList = batchPaymentAssignmentService.getBatchPaymentAssignmentByBatchId(batchId);
            assignmentList.forEach(data -> {
                details.add(batchPaymentAssignmentService.convertBatchAssignmentToBatchAssignmentAudit(data));
            });
        } else
            throw new Exception("Batch Payment Id required");
        return details;
    }

    public Boolean deleteBatchPayment(Long batchId) throws Exception {
        String SUBMODULE = MODULE + "[deleteBatchPayment()]";
        if (batchId != null) {
            StaffUser staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).get();
            BatchPayment batchPayment = batchPaymentRepository.getOne(batchId);
            if (batchPayment != null) {
                if (staffUser.getUsername().equals(batchPayment.getCreateBy())) {
                    List<BatchPaymentMapping> mappings = batchPayment.getBatchPaymentMappingList();
                    mappings.forEach(x -> {
                        x.setIs_deleted(true);
                       // batchPaymentMappingRepository.save(x);
                    });

                    batchPayment.setIsDeleted(true);
                    batchPaymentRepository.save(batchPayment);
                } else
                    throw new Exception("You are not Authorized user to delete Batch");
            } else
                throw new Exception("BatchPayment with " + batchId + " Not found");
        } else
            throw new Exception("BatchId Required");
        return true;
    }

    public Boolean deleteBatchPaymentMappingById(Long batchPaymentMappingId) throws Exception {
        String SUBMODULE = MODULE + "[deleteBatchPaymentMappingById()]";
        if (batchPaymentMappingId != null) {
            StaffUser staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).get();
            Optional<BatchPaymentMapping> batchPaymentMapping = batchPaymentMappingRepository.findById(batchPaymentMappingId);
            if (batchPaymentMapping.isPresent()) {
                if (staffUser.getUsername().equals(batchPaymentMapping.get().getBatchPayment().getCreateBy())) {
                    batchPaymentMapping.get().setIs_deleted(true);
                    batchPaymentMappingRepository.save(batchPaymentMapping.get());
                } else
                    throw new Exception("You are not Authorized user to delete BatchMapping");
            } else
                throw new Exception("BatchPaymentMapping with " + batchPaymentMappingId + " Not found");
        } else
            throw new Exception("BatchPayment Mapping Id Required");
        return true;
    }


    public boolean addBatchPaymentMappingInExistingBatch(BatchPaymentPojo batchPaymentPojo) throws Exception {
        String SUBMODULE = MODULE + "[addBatchPaymentMappingInExistingBatch()]";
        if (batchPaymentPojo != null && batchPaymentPojo.getId() != null) {
            Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchPaymentPojo.getId());
            if (batchPayment.isPresent()) {
                StaffUser staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId()).get();
                if (staffUser != null && staffUser.getUsername().equals(batchPayment.get().getCreateBy())) {
                    if (batchPaymentPojo.getBatchPaymentMappingList() != null && batchPaymentPojo.getBatchPaymentMappingList().size() > 0) {
                        batchPaymentPojo.getBatchPaymentMappingList().forEach(data ->
                        {
                            BatchPaymentMapping mapping = new BatchPaymentMapping();
                            mapping.setBatchPayment(batchPayment.get());
                            CreditDocument document = new CreditDocument();
                            document.setId(Integer.parseInt(data.getCredit_doc_id().toString()));
                            mapping.setCreditDocument(document);
                            batchPayment.get().getBatchPaymentMappingList().add(mapping);
                        });
                        batchPaymentRepository.save(batchPayment.get());
                    } else
                        throw new Exception("CreditDoucment List need to be required");
                } else
                    throw new Exception("You are not Authorized user to Add BatchMapping");
            } else
                throw new Exception("Batch Payment Mapping not found");
        }
        return true;
    }

    public List<CreditPojo> getMappingList(Long batchId) throws Exception {
        List<CreditPojo> list = new ArrayList<>();
        if (batchId != null) {
            Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchId);
            if (batchPayment.isPresent()) {
                if (!batchPayment.get().getIsDeleted()) {
                    batchPayment.get().getBatchPaymentMappingList().stream().filter(x -> !x.getIs_deleted()).forEach(data -> {
                        list.add(batchPaymentMappingService.convertCreditDocumentIntoCreditPojo(data));
                    });
                } else
                    throw new Exception("No Batch found with Id " + batchId);
            } else
                throw new Exception("No Batch found with Id " + batchId);
        } else
            throw new Exception("BatchId Required");
        return list;
    }

    public List<BatchStaffListDTO> getStaffListByBatchId(Long batchId) throws Exception {
        List<BatchStaffListDTO> batchStaffListDTOS = new ArrayList<>();
        if (batchId != null) {
            List<BatchPaymentAssignment> assignmentList = batchPaymentAssignmentService.getBatchPaymentAssignmentByBatchId(batchId);
            List<Integer> list = assignmentList.stream().map(x -> x.getStaffUser().getId()).distinct().collect(Collectors.toList());
            QStaffUser user = QStaffUser.staffUser;
            BooleanExpression expression = user.isNotNull();
            expression = expression.and(user.id.notIn(list));
            List<StaffUser> staffUsers = (List<StaffUser>) staffUserRepository.findAll(expression);
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0) {
                //staffUsers.stream().filter(staff -> (staff.getMvnoId() == getMvnoIdFromCurrentStaff() && staff.getMvnoId() != 1));
                // TODO: pass mvnoID manually 6/5/2025
                staffUsers = staffUserRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                //return staffUsers;
            } else {
                // TODO: pass mvnoID manually 6/5/2025
                staffUsers = staffUserRepository.findAllUsername(Arrays.asList(getMvnoIdFromCurrentStaff(null), 1), getBUIdsFromCurrentStaff());
                //return staffUsers;
            }
            staffUsers.forEach(x -> {
                BatchStaffListDTO dto = new BatchStaffListDTO();
                dto.setStaffId(x.getId());
                dto.setUsername(x.getUsername());
                dto.setFirstName(x.getFirstname());
                dto.setLastName(x.getLastname());
                dto.setFullName(x.getFullName());
                batchStaffListDTOS.add(dto);
            });
            return batchStaffListDTOS;
        } else
            throw new Exception("BatchId Required");
    }


}
