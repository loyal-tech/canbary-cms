package com.adopt.apigw.service.common;


import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.CreditDebitDocMapping;
import com.adopt.apigw.model.postpaid.CreditDocument;
import com.adopt.apigw.model.postpaid.DebitDocument;
import com.adopt.apigw.model.postpaid.QCreditDocument;
import com.adopt.apigw.modules.CreditTransactionMapping.CreditTansactionMappingRepository;
import com.adopt.apigw.modules.CreditTransactionMapping.CreditTransactionMapping;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CreditDocMessage;
import com.adopt.apigw.repository.common.BatchPaymentAssignmentRepository;
import com.adopt.apigw.repository.common.BatchPaymentRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CreditDebtMappingRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.postpaid.DebitDocRepository;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.DebitDocService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BatchPaymentAssignmentService extends AbstractService<BatchPaymentAssignment, BatchPaymentAssignmentPojo, Long> {

    @Autowired
    private BatchPaymentAssignmentRepository batchPaymentAssignmentRepository;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private StaffUserRepository staffUserRepository;

    @Autowired
    private BatchPaymentService batchPaymentService;

    @Autowired
    private CreditDocService creditDocService;

    @Autowired
    private BatchPaymentRepository batchPaymentRepository;

    @Autowired
    private ClientServiceSrv clientService;

    @Autowired
    private CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    private CreditDocRepository creditDocRepository;

    @Autowired
    private DebitDocRepository debitDocRepository;

    @Autowired
    private DebitDocService debitDocService;

    @Autowired
    private CreditTansactionMappingRepository creditTansactionMappingRepository;

    @Autowired
    private HierarchyService hierarchyService;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Override
    protected JpaRepository<BatchPaymentAssignment, Long> getRepository() {
        return batchPaymentAssignmentRepository;
    }

    public void assignBatchPayment(BatchPayment batchPayment, Integer staffId, String flag) {
        if (staffId != null) {
            StaffUser staff = staffUserRepository.findById(staffId).get();
            if (batchPayment != null && staff != null)
                saveBatchPaymentAssignment(batchPayment, staff, flag);
        }
    }

    public void saveBatchPaymentAssignment(BatchPayment batchPayment, StaffUser staffUser, String assignedStatus) {
        BatchPaymentAssignment batchPaymentAssignment = new BatchPaymentAssignment();
        batchPaymentAssignment.setAssignedDate(LocalDate.now());
        if (batchPayment != null && staffUser != null) {
            batchPaymentAssignment.setBatchPayment(batchPayment);
            batchPaymentAssignment.setStaffUser(staffUser);
            batchPaymentAssignment.setNextStaffUser(null);
            batchPaymentAssignment.setStatus("Pending");
//            batchPaymentAssignment.setRemark("Approved by : " + staffUser.getUsername());
            batchPaymentAssignment.setAssignedStatus(assignedStatus);
            batchPaymentAssignmentRepository.save(batchPaymentAssignment);
        }

//        String finalTeamAuthority=clientService.getValueByName("paymentFinalAuthority");
//        Set<Teams> teams=staffUser.getTeam();
//        if(finalTeamAuthority!=null)
//        {
//            if(teams.stream().filter(x->x.getName().equalsIgnoreCase(finalTeamAuthority)).count()>0)
//            {
//                List<Integer> batchMappingList=batchPayment.getBatchPaymentMappingList().stream().filter(y->y.getIs_deleted().equals(false)).map(x->x.getCreditDocument().getId()).collect(Collectors.toList());
//                batchPayment.setStatus("Approved");
//                batchMappingList.forEach(x->{
//                    SearchPaymentPojo searchPaymentPojo=new SearchPaymentPojo();
//                    searchPaymentPojo.setIdlist(x.toString());
//                    creditDocService.approvePayment(searchPaymentPojo);
//                });
//                batchPaymentRepository.save(batchPayment);
//            }
//        }
    }

    public List<BatchPaymentAssignment> getBatchPaymentAssignmentByBatchId(Long batchId) {
        List<BatchPaymentAssignment> assignmentList = new ArrayList<>();
        QBatchPaymentAssignment qBatchPaymentAssignment = QBatchPaymentAssignment.batchPaymentAssignment;
        BooleanExpression expression = qBatchPaymentAssignment.isNotNull();
        expression = expression.and(qBatchPaymentAssignment.batchPayment.id.eq(batchId)).and(qBatchPaymentAssignment.batchPayment.isDeleted.eq(false));
        assignmentList = (List<BatchPaymentAssignment>) batchPaymentAssignmentRepository.findAll(expression);
        assignmentList.forEach(x -> {
            x.getBatchPayment().setBatchPaymentMappingList(x.getBatchPayment().getBatchPaymentMappingList().stream().filter(data -> data.getIs_deleted().equals(false)).collect(Collectors.toList()));
        });
        return assignmentList;
    }


    public BatchPaymentAuditDetails convertBatchAssignmentToBatchAssignmentAudit(BatchPaymentAssignment paymentAssignment) {
        if (paymentAssignment != null) {
            BatchPaymentAuditDetails paymentAuditDetails = new BatchPaymentAuditDetails();
            paymentAuditDetails.setBatchId(paymentAssignment.getBatchPayment().getId());
            paymentAuditDetails.setBatchName(paymentAssignment.getBatchPayment().getBatchname());
            paymentAuditDetails.setStaffName(paymentAssignment.getStaffUser().getUsername());
            if (paymentAssignment.getStaffUser().getTeam() != null && paymentAssignment.getStaffUser().getTeam().size() > 0) {
                paymentAuditDetails.setTeamName(paymentAssignment.getStaffUser().getTeam().stream().findFirst().get().getName());
            } else
                paymentAuditDetails.setTeamName(null);

            paymentAuditDetails.setStatus(paymentAssignment.getStatus());
            paymentAuditDetails.setRemark(paymentAssignment.getRemark());
            return paymentAuditDetails;
        }
        return null;
    }

    public void batchAssignedToNextApprover(BatchAssignPojo batchAssignPojo) throws Exception {
        if (batchAssignPojo != null) {
            if (batchAssignPojo.getBatchId() != null) {
                Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId());
                if (batchPayment.isPresent()) {
                    if (batchAssignPojo.getNextStaffId() != null) {
                        Optional<StaffUser> nextStaffUser = staffUserRepository.findById(batchAssignPojo.getNextStaffId());
                        if (nextStaffUser.isPresent()) {
                            BatchPaymentAssignment assignment = new BatchPaymentAssignment();
                            assignment.setNextStaffUser(null);
                            assignment.setBatchPayment(batchPayment.get());
                            assignment.setAssignedDate(LocalDate.now());
                            assignment.setRemark("");
                            assignment.setStatus("Pending");
                            assignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_NOT_ASSIGNED);
                            assignment.setStaffUser(nextStaffUser.get());
                            batchPaymentAssignmentRepository.save(assignment);
                            if (batchAssignPojo.getStaffId() != null) {
                                Optional<StaffUser> staffUser = staffUserRepository.findById(getLoggedInUser().getStaffId());
                                if (staffUser.isPresent()) {
                                    List<BatchPaymentAssignment> batchPaymentAssignment = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), staffUser.get().getId());
                                    if (batchPaymentAssignment != null) {
                                        batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setNextStaffUser(nextStaffUser.get());
                                        batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                                        batchPaymentAssignmentRepository.save(batchPaymentAssignment.get(batchPaymentAssignment.size() - 1));
                                    }
                                }
                            }
                        }
                    } else
                        throw new Exception("StaffId Required");
                }
            } else
                throw new Exception("BatchId Required");
        }
    }

//    public void batchPaymentApprove(BatchAssignPojo batchAssignPojo) throws Exception {
//        if (batchAssignPojo != null) {
//            if (batchAssignPojo.getBatchId() != null) {
//                Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId());
//                if (batchPayment.isPresent()) {
//                    if (batchAssignPojo.getStaffId() != null) {
//                        Optional<StaffUser> staffUser = staffUserRepository.findById(batchAssignPojo.getStaffId());
//                        Optional<StaffUser> nextStaffUser = staffUserRepository.findById(batchAssignPojo.getNextStaffId());
//                        BatchPayment batchPayment1 = batchPaymentRepository.findById(batchAssignPojo.getBatchId()).get();
//                        hierarchyService.sendWorkflowAssignActionMessage(nextStaffUser.get().getCountryCode(), nextStaffUser.get().getPhone(), nextStaffUser.get().getEmail(), nextStaffUser.get().getMvnoId(), nextStaffUser.get().getUsername(), batchPayment1.getBatchname());
//                        if (staffUser.isPresent()) {
//                            List<BatchPaymentAssignment> batchPaymentAssignment = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), staffUser.get().getId());
//                            if (batchPaymentAssignment != null) {
//                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setStatus("Approved");
//                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setAssignedStatus("AssignedToOtherTeam");
//                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setRemark(batchAssignPojo.getRemark());
//                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setNextStaffUser(nextStaffUser.get());
//                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setRemark("AssignedToOtherTeam");
//                                batchPaymentAssignmentRepository.save(batchPaymentAssignment.get(batchPaymentAssignment.size() - 1));
//                                if (batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).getNextStaffUser() != null) {
//
//                                    StaffUser user = batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).getNextStaffUser();
//                                    List<BatchPaymentAssignment> nextData = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), user.getId());
//                                    if (user != null) {
//                                        BatchPaymentAssignment nextpaymentAssignment = new BatchPaymentAssignment();
//                                        nextpaymentAssignment.setBatchPayment(batchPayment.get());
//                                        nextpaymentAssignment.setAssignedDate(LocalDate.now());
//                                        nextpaymentAssignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
//                                        nextpaymentAssignment.setStaffUser(nextStaffUser.get());
//                                        nextpaymentAssignment.setStatus("Pending");
//                                        nextpaymentAssignment.setRemark("AssignedToOtherTeam");
//                                        nextpaymentAssignment.setNextStaffUser(staffUser.get());
//                                        batchPaymentAssignmentRepository.save(nextpaymentAssignment);
//                                    }
//                                }
//                                String finalTeamAuthority = clientService.getValueByName("paymentFinalAuthority");
////                                Set<Teams> teams = nextStaffUser.get().getTeam();
//                                Set<Teams> teams1 = staffUser.get().getTeam();
//                                if (finalTeamAuthority != null) {
//                                    if (teams1.stream().filter(x -> x.getName().equalsIgnoreCase(finalTeamAuthority)).count() > 0) {
//                                        List<Integer> batchMappingList = batchPayment.get().getBatchPaymentMappingList().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
//                                        batchPayment.get().setStatus("Approved");
//                                        StaffUser user = batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).getNextStaffUser();
//                                        List<BatchPaymentAssignment> nextData = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), user.getId());
//                                        if (!nextData.isEmpty()) {
//                                            if (teams1.stream().filter(x -> x.getName().equalsIgnoreCase(finalTeamAuthority)).count() > 0) {
//                                                BatchPaymentAssignment batchPaymentAssignment1 = nextData.get(nextData.size() - 1);
//                                                batchPaymentAssignment1.setAssignedStatus("Approved");
//                                                batchPaymentAssignment1.setStatus("Approved");
//                                                batchPaymentAssignmentRepository.save(batchPaymentAssignment1);
//                                                for (BatchPaymentMapping batchPaymentMapping : batchPayment.get().getBatchPaymentMappingList()) {
//
//                                                    if (batchPaymentMapping.getCreditDocument() != null) {
////                                                        paymentAdjustForBatch(batchPaymentMapping.getCreditDocument().getId().toString() , getDebitDocByCreditDoc(batchPaymentMapping.getCreditDocument().getId()).get().getId());
//                                                        Optional<DebitDocument> debitDocument = getDebitDocByCreditDoc(batchPaymentMapping.getCreditDocument().getId());
//                                                        if (debitDocument.get().getAdjustedAmount() == null) {
//                                                            debitDocument.get().setAdjustedAmount(0.00);
//                                                        }
//
//                                                        CreditDocument creditDocument = batchPaymentMapping.getCreditDocument();
//                                                        creditDocument.setStatus("approved");
//                                                        creditDocService.addLedgeAfterApproval(creditDocument);
//                                                        debitDocument.get().setAdjustedAmount(creditDocument.getAmount());
//                                                        if (debitDocument.get().getTotalamount() - debitDocument.get().getAdjustedAmount() == 0) {
//                                                            debitDocument.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
//                                                        }
//                                                        if (debitDocument.get().getTotalamount() - debitDocument.get().getAdjustedAmount() > 0) {
//                                                            debitDocument.get().setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
//                                                        }
//                                                        debitDocRepository.save(debitDocument.get());
//                                                        creditDocRepository.save(creditDocument);
//                                                        List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocIdAndCreditDocId(debitDocument.get().getId(), creditDocument.getId());
//
//                                                        creditDebitDocMappings.get(0).setAdjustedAmount(batchPaymentMapping.getCreditDocument().getAmount());
//                                                        creditDebtMappingRepository.saveAll(creditDebitDocMappings);
//                                                    }
//
//                                                }
//                                            }
//                                        }
//                                        batchMappingList.forEach(x -> {
//                                            SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
//                                            searchPaymentPojo.setIdlist(x.toString());
//                                            creditDocService.approvePayment(searchPaymentPojo);
//                                        });
//                                        batchPaymentRepository.save(batchPayment.get());
//                                    }
//                                }
//                            }
//                        } else
//                            throw new Exception("No StaffUser found with given id " + batchAssignPojo.getStaffId());
//                    } else
//                        throw new Exception("StaffId Required");
//                } else
//                    throw new Exception("No BatchPayment found with given id " + batchAssignPojo.getBatchId());
//            } else
//                throw new Exception("BatchId Required");
//        }
//    }


    public void batchPaymentApprove(BatchAssignPojo batchAssignPojo) throws Exception {
        if (batchAssignPojo == null || batchAssignPojo.getBatchId() == null) {
            throw new Exception("BatchId Required");
        }

        BatchPayment batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId()).orElse(null);
        if (Objects.isNull(batchPayment)) {
            throw new Exception("No BatchPayment found with given id " + batchAssignPojo.getBatchId());
        }

        if (batchAssignPojo.getStaffId() == null) {
            throw new Exception("StaffId Required");
        }

        StaffUser staffUser = staffUserRepository.findById(batchAssignPojo.getStaffId()).orElse(null);
        if (Objects.isNull(staffUser)) {
            throw new Exception("No StaffUser found with given id " + batchAssignPojo.getStaffId());
        }

        StaffUser nextStaffUser = staffUserRepository.findById(batchAssignPojo.getNextStaffId()).orElse(null);
        BatchPaymentAssignment lastAssignment = batchPaymentAssignmentRepository.findTopByBatchPaymentAndStaffUserOrderByAssignedDateDesc(
                batchPayment, staffUser
        ).orElse(null);

        if (nextStaffUser!=null) {
            hierarchyService.sendWorkflowAssignActionMessage(nextStaffUser.getCountryCode(), nextStaffUser.getPhone(), nextStaffUser.getEmail(), nextStaffUser.getMvnoId(), nextStaffUser.getUsername(), batchPayment.getBatchname(), (long) getLoggedInStaffId());

            if (lastAssignment != null) {
                lastAssignment.setStatus("Approved");
                lastAssignment.setAssignedStatus("AssignedToOtherTeam");
                lastAssignment.setRemark(batchAssignPojo.getRemark());
                lastAssignment.setNextStaffUser(nextStaffUser);
                lastAssignment.setRemark(batchAssignPojo.getRemark());
                batchPaymentAssignmentRepository.save(lastAssignment);

                if (lastAssignment.getNextStaffUser() != null) {
                    BatchPaymentAssignment nextpaymentAssignment = new BatchPaymentAssignment();
                    nextpaymentAssignment.setBatchPayment(batchPayment);
                    nextpaymentAssignment.setAssignedDate(LocalDate.now());
                    nextpaymentAssignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                    nextpaymentAssignment.setStaffUser(nextStaffUser);
                    nextpaymentAssignment.setStatus("Pending");
                    nextpaymentAssignment.setRemark(batchAssignPojo.getRemark());
                    nextpaymentAssignment.setNextStaffUser(staffUser);
                    batchPaymentAssignmentRepository.save(nextpaymentAssignment);
                }
            }
        }

        String finalTeamAuthority = clientService.getValueByName("paymentFinalAuthority",staffUser.getMvnoId());
        Set<Teams> teams1 = staffUser.getTeam();

        if (finalTeamAuthority != null && teams1.stream().anyMatch(x -> x.getName().equalsIgnoreCase(finalTeamAuthority))) {
            batchPayment.setStatus("Approved");

            if (lastAssignment != null) {
                lastAssignment.setAssignedStatus("Approved");
                lastAssignment.setStatus("Approved");
                batchPaymentAssignmentRepository.save(lastAssignment);
                Double adjustedAmount=0.0;
                for (BatchPaymentMapping batchPaymentMapping : batchPayment.getBatchPaymentMappingList()) {
                    if (batchPaymentMapping.getCreditDocument() != null) {
                        Optional<DebitDocument> debitDocumentOpt = getDebitDocByCreditDoc(batchPaymentMapping.getCreditDocument().getId());
                        if (debitDocumentOpt.isPresent()) {
                            DebitDocument debitDocument = debitDocumentOpt.get();
                            CreditDocument creditDocument = batchPaymentMapping.getCreditDocument();
                            creditDocService.addLedgeAfterApproval(creditDocument);
                            debitDocument.setAdjustedAmount(creditDocument.getAmount());

                            if (debitDocument.getTotalamount() - debitDocument.getAdjustedAmount() == 0) {
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.FULLY_PAID);
                                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.FULLY_ADJUSTED);
                            } else if (debitDocument.getTotalamount() - debitDocument.getAdjustedAmount() > 0) {
                                debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.PARTIALY_PAID);
                                creditDocument.setStatus(CommonConstants.CREDIT_DOC_STATUS.PARTIAL_ADJUSTED);
                            }

                            debitDocRepository.save(debitDocument);
                            creditDocRepository.save(creditDocument);

                            List<CreditDebitDocMapping> creditDebitDocMappings = creditDebtMappingRepository.findBydebtDocIdAndCreditDocId(debitDocument.getId(), creditDocument.getId());

                            if (!creditDebitDocMappings.isEmpty()) {
                                creditDebitDocMappings.get(0).setAdjustedAmount(batchPaymentMapping.getCreditDocument().getAmount());
                                creditDebtMappingRepository.saveAll(creditDebitDocMappings);
                            }

                            CreditDocMessage creditDocMessage = new CreditDocMessage(creditDocument, IterableUtils.toList(creditDebitDocMappings));
                        kafkaMessageSender.send(new KafkaMessageData(creditDocMessage, CreditDocMessage.class.getSimpleName()));
//                            messageSender.send(creditDocMessage, RabbitMqConstants.QUEUE_CREDIT_DOCUMENT_APPROVED_REVENUE);
                            Thread.sleep(1000);

                        }
                    }
                }
            }

            List<Integer> batchMappingList = batchPayment.getBatchPaymentMappingList()
                    .stream()
                    .filter(y -> !y.getIs_deleted())
                    .map(x -> x.getCreditDocument().getId())
                    .collect(Collectors.toList());
            batchMappingList.forEach(x -> {
                SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
                searchPaymentPojo.setIdlist(x.toString());
                creditDocService.approvePayment(searchPaymentPojo,searchPaymentPojo.getMvnoId());
            });

            batchPaymentRepository.save(batchPayment);
        }
    }
    public void batchPaymentReject(BatchAssignPojo batchAssignPojo) throws Exception {
        if (batchAssignPojo != null) {
            if (batchAssignPojo.getBatchId() != null) {
                Optional<StaffUser> nextStaffUser = staffUserRepository.findById(batchAssignPojo.getNextStaffId());
                BatchPayment batchPayment1 = batchPaymentRepository.findById(batchAssignPojo.getBatchId()).get();
                hierarchyService.sendWorkflowAssignActionMessage(nextStaffUser.get().getCountryCode(), nextStaffUser.get().getPhone(), nextStaffUser.get().getEmail(), nextStaffUser.get().getMvnoId(), nextStaffUser.get().getUsername(), batchPayment1.getBatchname(), (long) getLoggedInStaffId());
                Optional<BatchPayment> batchPayment = batchPaymentRepository.findById(batchAssignPojo.getBatchId());
                if (batchPayment.isPresent()) {
                    if (batchAssignPojo.getStaffId() != null) {
                        Optional<StaffUser> staffUser = staffUserRepository.findById(batchAssignPojo.getStaffId());
                        if (staffUser.isPresent()) {
                            List<BatchPaymentAssignment> batchPaymentAssignment = batchPaymentAssignmentRepository.findByBatchPaymentAndStaffUser(batchPayment.get().getId(), staffUser.get().getId());
                            if (batchPaymentAssignment != null) {
                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setStatus("Rejected");
                                batchPaymentAssignment.get(batchPaymentAssignment.size() - 1).setRemark(batchAssignPojo.getRemark());
                                batchPaymentAssignmentRepository.save(batchPaymentAssignment.get(batchPaymentAssignment.size() - 1));
                                StaffUser currentStaff = staffUserRepository.findById(getLoggedInUser().getStaffId()).get();
                                if (currentStaff != null && currentStaff.getUsername().equalsIgnoreCase(batchPayment.get().getCreateBy())) {
                                    List<Integer> batchMappingList = batchPayment.get().getBatchPaymentMappingList().stream().filter(y -> y.getIs_deleted().equals(false)).map(x -> x.getCreditDocument().getId()).collect(Collectors.toList());
                                    batchPayment.get().setStatus("Rejected");
                                    batchPaymentRepository.save(batchPayment.get());
                                    batchMappingList.forEach(x -> {
                                        SearchPaymentPojo searchPaymentPojo = new SearchPaymentPojo();
                                        searchPaymentPojo.setIdlist(x.toString());
                                        try {
                                            creditDocService.rejectPayment(searchPaymentPojo);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e.getMessage());
                                        }
                                    });
                                } else {
                                    List<BatchPaymentAssignment> previousBatchAssignment = batchPaymentAssignmentRepository.findPreviousAssigne(batchPayment.get().getId(), staffUser.get().getId());
                                    if (previousBatchAssignment != null && previousBatchAssignment.size() > 0) {
                                        BatchPaymentAssignment nextpaymentAssignment = new BatchPaymentAssignment();
                                        nextpaymentAssignment.setBatchPayment(batchPayment.get());
                                        nextpaymentAssignment.setAssignedDate(LocalDate.now());
                                        nextpaymentAssignment.setAssignedStatus(APIConstants.BATCH_PAYMENT_ASSIGNED);
                                        nextpaymentAssignment.setStaffUser(previousBatchAssignment.get(previousBatchAssignment.size() - 1).getStaffUser());
                                        nextpaymentAssignment.setStatus("Pending");
                                        nextpaymentAssignment.setNextStaffUser(currentStaff);
                                        batchPaymentAssignmentRepository.save(nextpaymentAssignment);
                                    }
                                }
                            }
                        } else
                            throw new Exception("No StaffUser found with given id " + batchAssignPojo.getStaffId());
                    } else
                        throw new Exception("StaffId Required");
                } else
                    throw new Exception("No BatchPayment found with given id " + batchAssignPojo.getBatchId());
            } else
                throw new Exception("BatchId Required");
        }
    }

    public void paymentAdjustForBatch(String creditDocumentId, Integer debitDocid) {

        QCreditDocument qCreditDocument = QCreditDocument.creditDocument;
        Integer creditdocId = Integer.parseInt(creditDocumentId);
        BooleanExpression credBoolexp = qCreditDocument.isNotNull().and(qCreditDocument.paytype.eq("Payment").and(qCreditDocument.id.eq(creditdocId)));
//         creditDocumentList
        List<Integer> creditDocIdList = new ArrayList<>();
        creditDocIdList.add(creditdocId);
        Optional<CreditDocument> creditDocument1 = creditDocRepository.findById(creditdocId);
        //Page<CreditDocument> response = (Page<CreditDocument>) creditDocRepository.findAll(credBoolexp);
        List<CreditDocument> list = creditDocRepository.findAllByIdIn(creditDocIdList);
        list.forEach(creditDocument -> {
            CreditDebitMappingPojo creditDebitMappingPojo = new CreditDebitMappingPojo();
            CreditDebitDataPojo creditDebitDataPojo = new CreditDebitDataPojo();
            List<CreditDebitDataPojo> creditDebitDataPojoList = new ArrayList<>();
            Optional<DebitDocument> debitDocument1 = debitDocRepository.findById(debitDocid);
            if (debitDocument1.isPresent()) {
                //  if (debitDocument1.get().getAdjustedAmount() < debitDocument1.get().getTotalamount()) {
                creditDebitMappingPojo.setInvoiceId(debitDocument1.get().getId());
                creditDebitDataPojo.setAmount(creditDocument.getAmount());
                creditDebitDataPojo.setId(creditDocument.getId());
                creditDebitDataPojoList.add(creditDebitDataPojo);
                creditDebitMappingPojo.setCreditDocumentList(creditDebitDataPojoList);

            }
            try {
                debitDocService.InvoicePaymentDone(creditDebitMappingPojo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        });
    }

    public Optional<DebitDocument> getDebitDocByCreditDoc(Integer creditId) {

        List<CreditDebitDocMapping> creditDebitDocMappingList = creditDebtMappingRepository.findByCreditDocId(creditId);
        Optional<DebitDocument> debitDocument = debitDocRepository.findById(creditDebitDocMappingList.get(0).getDebtDocId());
        return debitDocument;
    }

}
