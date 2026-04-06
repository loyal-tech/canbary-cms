package com.adopt.apigw.modules.PartnerLedger.service;

import com.adopt.apigw.MicroSeviceDataShare.SavePartnerPaymentMessage;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.mapper.PartnerPaymentMapper;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerBalanceDTO;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerGetDTO;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.xmlConversion.PaymentDetailsXml;
import com.adopt.apigw.pojo.api.CustomerCafAssignmentPojo;
import com.adopt.apigw.pojo.api.StaffUserPojo;

import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.postpaid.CreditDocService;
import com.adopt.apigw.service.postpaid.PartnerCommissionService;
import com.adopt.apigw.service.postpaid.PartnerService;
import com.adopt.apigw.service.postpaid.TempPartnerLedgerDetailsRepository;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UtilsCommon;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerPaymentService extends ExBaseAbstractService<PartnerPaymentDTO, PartnerPayment, Long> {

    public PartnerPaymentService(PartnerPaymentRepository repository, PartnerPaymentMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private PartnerPaymentMapper partnerPaymentMapper;

    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private WorkflowAuditService workflowAuditService;

    @Autowired
    private PartnerCreditDocumentRepository partnerCredRepo;
    @Autowired
    private PartnerCreditDocRepository partnerCreditDocRepository;

    @Autowired
    PartnerRepository partnerrepo;

    @Autowired
    PartnerLedgerService partnerLedgerService;

    @Autowired
    PartnerLedgerDetailsService partnerLedgerDetailsService;

    @Autowired
    TempPartnerLedgerDetailsRepository tempPartnerLedgerDetailsRepository;

    @Autowired
    DebitDocRepository debitDocRepository;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    CreditDebtMappingRepository creditDebtMappingRepository;

    @Autowired
    CreditDocService creditDocService;

    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    CustomersRepository customersRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    public PartnerPaymentDTO addBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        PartnerPayment partnerPayment = new PartnerPayment();
        PartnerPaymentDTO partnerPaymentDTO = new PartnerPaymentDTO();
        PartnerPaymentDTO partnerPaymentDTOSaved = new PartnerPaymentDTO();
        Partner partner = partnerrepo.findById(dto.getPartner_id()).get();
        partnerPaymentDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(dto.getAmount())));
        partnerPaymentDTO.setPartnerId(dto.getPartner_id());
        partnerPaymentDTO.setPaymentmode(dto.getPaymentmode());
        if (partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE_TYPE_CHEQUE)) {
            partnerPaymentDTO.setChequenumber(dto.getChequenumber());
            partnerPaymentDTO.setChequedate(dto.getChequedate());
            partnerPaymentDTO.setBank_name(dto.getBank_name());
            partnerPaymentDTO.setBranch_name(dto.getBranch_name());
        }
        if (dto.getPaymentdate() != null) {
            partnerPaymentDTO.setPaymentdate(dto.getPaymentdate());
        }
        partnerPaymentDTO.setRefno(dto.getRefno());
        partnerPaymentDTO.setCredit(dto.getCredit());
        partnerPaymentDTO.setRemarks(dto.getDescription());
        if(dto.getPaymentmode()!=null && dto.getOnlinesource()!=null){
            if(dto.getPaymentmode().equalsIgnoreCase("Online") && dto.getOnlinesource().equalsIgnoreCase("PhonePe")){
                partnerPaymentDTO.setStatus("Payment Awaited");
            }
            else {
                partnerPaymentDTO.setStatus("NewActivation");
            }
        }
        else{
            partnerPaymentDTO.setStatus("NewActivation");
        }
        if ( dto.getDestinationBank() != null) {
            partnerPaymentDTO.setDestinationBank(dto.getDestinationBank());
        }
        if ( dto.getSourceBank() != null) {
            partnerPaymentDTO.setSourceBank(dto.getSourceBank());
        }
        if (dto.getOnlinesource() != null) {
            partnerPaymentDTO.setOnlinesource(dto.getOnlinesource());
        }
        partnerPayment = partnerPaymentMapper.dtoToDomain(partnerPaymentDTO, new CycleAvoidingMappingContext());
        if(dto.getCredit()!=null && dto.getCredit()>0 && dto.getAmount().doubleValue()==0.0d){

            PartnerPayment partnerPayment1=new PartnerPayment();
            partnerPayment1.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_CREDIT);
            partnerPayment1.setAmount(dto.getCredit().doubleValue());
            partnerPayment1.setPartner(partnerPayment.getPartner());
            partnerPayment1.setPaymentdate(partnerPayment.getPaymentdate());
            partnerPayment1.setNextStaff(getLoggedInUserId());
            partnerPayment1.setStatus(partnerPayment.getStatus());
            partnerPayment1.setNextTeamHierarchyMappingId(partnerPayment.getNextTeamHierarchyMappingId());
            partnerPayment1.setChequedate(partnerPayment.getChequedate());
            partnerPayment1.setChequenumber(partnerPayment.getChequenumber());
            partnerPayment1.setRemarks(partnerPayment.getRemarks());
            partnerPayment1.setRefno(partnerPayment.getRefno());
            partnerPayment1.setBank_name(partnerPayment.getBank_name());
            partnerPayment1.setBranch_name(partnerPayment.getBranch_name());
            partnerPayment1.setOrderid(partnerPayment.getOrderid());
            partnerPayment1.setPaymentstatus(partnerPayment.getPaymentstatus());
            partnerPayment1.setPaymentmode(partnerPayment.getPaymentmode());
            if ( partnerPayment.getDestinationBank() != null) {
                partnerPayment1.setDestinationBank(dto.getDestinationBank());
            }
            if ( partnerPayment.getSourceBank() != null) {
                partnerPayment1.setSourceBank(dto.getSourceBank());
            }
            if (partnerPayment.getOnlinesource() != null) {
                partnerPayment1.setOnlinesource(dto.getOnlinesource());
            }
            partnerPaymentDTOSaved = partnerPaymentMapper.domainToDTO(partnerPaymentRepository.save(partnerPayment1), new CycleAvoidingMappingContext());
           // assignpayment(partnerPaymentDTOSaved,partner);
        }
        partnerPayment.setNextStaff(getLoggedInUserId());
        if(dto.getAmount()>0) {
            partnerPayment.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_BALANCE);
            partnerPaymentDTOSaved = partnerPaymentMapper.domainToDTO(partnerPaymentRepository.save(partnerPayment), new CycleAvoidingMappingContext());
          //  assignpayment(partnerPaymentDTOSaved,partner);
        }
        partnerPaymentDTOSaved.setMvnoId(partner.getMvnoId());
       return partnerPaymentDTOSaved;
    }

    public void assignpayment(PartnerPaymentDTO partnerPaymentDTOSaved,Partner partner){
        if (partnerPaymentDTOSaved.getNextTeamHierarchyMappingId() == null) {
            if (partnerPaymentDTOSaved.getStatus() != null && !"".equals(partnerPaymentDTOSaved.getStatus())) {
                if (partnerPaymentDTOSaved.getStatus().equalsIgnoreCase("NewActivation")) {
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN).equals("TRUE")) {
                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(partner.getMvnoId(), partner.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, false, true, partnerPaymentDTOSaved);
                        int staffId = 0;
                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                            staffId = Integer.parseInt(map.get("staffId"));
                            partnerPaymentDTOSaved.setStatus("NewActivation");
                            StaffUser assignedStaff = staffUserRepository.findById(staffId).get();
                            partnerPaymentDTOSaved.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            partnerPaymentDTOSaved.setNextStaff(staffId);
                            String action = CommonConstants.WORKFLOW_MSG_ACTION.PAYMENT + " with payment amount : " + " ' " + partnerPaymentDTOSaved.getAmount() + " ' " + "and " + "reference number : " + " ' " + partnerPaymentDTOSaved.getRefno() + " '";
                            hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action,assignedStaff.getId().longValue());
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTOSaved.getId().intValue(), partner.getName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                        } else {
                            StaffUser currentStaff = staffUserRepository.findById(getLoggedInUserId()).get();
                            partnerPaymentDTOSaved.setNextTeamHierarchyMappingId(null);
                            partnerPaymentDTOSaved.setNextStaff(currentStaff.getId());
                            //partnerPaymentDTOSaved.setStatus("Active");
                            partner.setCommrelvalue(partner.getCommrelvalue() - partnerPaymentDTOSaved.getAmount());
                            partnerrepo.save(partner);
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTOSaved.getId().intValue(), partner.getName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        }
                    } else {
                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(partner.getMvnoId(), partner.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, false, true, partnerPaymentDTOSaved);
                        if (map.containsKey("assignableStaff")) {
                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),partner.getMvnoId());
                            partnerPaymentDTOSaved.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTeamHierarchyMappingId").toString()));
                            partnerPaymentDTOSaved.setNextStaff(currentStaff.getId());
                            partnerPaymentDTOSaved.setStatus("NewActivation");
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTOSaved.getId().intValue(), partner.getName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        } else {
                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),partner.getMvnoId());
                            partnerPaymentDTOSaved.setNextTeamHierarchyMappingId(null);
                            partnerPaymentDTOSaved.setNextStaff(currentStaff.getId());
                           // partnerPaymentDTOSaved.setStatus("Active");
                            partnerPaymentDTOSaved.setStatus("NewActivation");
                            partnerrepo.save(partner);
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTOSaved.getId().intValue(), partner.getName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                        }
                    }
                }
                partnerPaymentRepository.save(partnerPaymentMapper.dtoToDomain(partnerPaymentDTOSaved, new CycleAvoidingMappingContext()));
            }
        }
    }
    public void reverseBalance(PartnerLedgerBalanceDTO dto) throws Exception {
        PartnerPayment partnerPayment = new PartnerPayment();
        PartnerPaymentDTO partnerPaymentDTO = new PartnerPaymentDTO();
        partnerPaymentDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(dto.getAmount())));
        partnerPaymentDTO.setPartnerId(dto.getPartner_id());
        if (dto.getPaymentdate() != null) {
            partnerPaymentDTO.setPaymentdate(dto.getPaymentdate());
        }
        //     partnerPaymentDTO.setRefno(dto.getRefno());
        partnerPaymentDTO.setTranscategory(CommonConstants.TRANS_CATEGORY_REVERSE_BALANCE);
        partnerPaymentDTO.setRemarks(dto.getDescription());
        partnerPayment = partnerPaymentMapper.dtoToDomain(partnerPaymentDTO, new CycleAvoidingMappingContext());
        partnerPaymentRepository.save(partnerPayment);
    }

    public List<PartnerPaymentDTO> getByTime(PartnerLedgerGetDTO dto) throws Exception {
        List<PartnerPaymentDTO> partnerPaymentDTOList;
        List<PartnerPayment> partnerPaymentList;
        if (dto.getSTART_DATE() != null && dto.getEND_DATE() != null) {
            partnerPaymentList = partnerPaymentRepository.findAllByStartDateAndEndDateAndPartnerId(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getPartner_id());
        } else {
            partnerPaymentList = partnerPaymentRepository.findAllByPartner_Id(dto.getPartner_id());
        }
        partnerPaymentDTOList = partnerPaymentList.stream().map(data -> partnerPaymentMapper
                .domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());

        return partnerPaymentDTOList;
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public GenericDataDTO updatePartnerPaymentAssignment(CustomerCafAssignmentPojo pojo) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (pojo.getPartnerPaymentId() != null && pojo.getStaffId() != null)
        {
            PartnerPayment partnerPayment = partnerPaymentRepository.findById(pojo.getPartnerPaymentId().longValue()).orElse(null);
            PartnerPaymentDTO partnerPaymentDTO = partnerPaymentMapper.domainToDTO(partnerPayment, new CycleAvoidingMappingContext());
            StaffUser staffUser = staffUserRepository.findById(pojo.getStaffId()).get();
            StaffUser loggedInUser = staffUserRepository.findById(getLoggedInUserId()).get();
            StringBuilder approvedByName = new StringBuilder();
            if (!staffUser.getUsername().equalsIgnoreCase("admin")) {
                if (clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,staffUser.getMvnoId()).equals("TRUE")) {
                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(partnerPayment.getPartner().getMvnoId(), partnerPayment.getPartner().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"), false, partnerPaymentDTO);
                    if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                        partnerPaymentDTO.setNextTeamHierarchyMappingId(null);
                        partnerPaymentDTO.setNextStaff(null);
                        if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved"))
                            partnerPaymentDTO.setStatus(SubscriberConstants.ACTIVE);
                        else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected"))
                            partnerPaymentDTO.setStatus(SubscriberConstants.REJECT);
                        PartnerPaymentDTO dto=saveEntity(partnerPaymentDTO);
                        String action = CommonConstants.WORKFLOW_MSG_ACTION.PARTNER_BALANCE + " for customer : " + " ' " + partnerPayment.getPartner().getName() + " '";
                        hierarchyService.sendWorkflowAssignActionMessage(loggedInUser.getCountryCode(), loggedInUser.getPhone(), loggedInUser.getEmail(), loggedInUser.getMvnoId(), loggedInUser.getFullName(), action,loggedInUser.getId().longValue());
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTO.getId().intValue(), partnerPayment.getPartner().getName(), staffUser.getId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                    } else {
                        partnerPaymentDTO.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                        partnerPaymentDTO.setNextStaff(Integer.valueOf(map.get("staffId")));
                        StaffUser assigned = staffUserRepository.findById(Integer.valueOf(map.get("staffId"))).get();
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTO.getId().intValue(), partnerPayment.getPartner().getName(), staffUser.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(),
                                "Remarks  : " + pojo.getRemark() + "\n" + "Assigned to :- " + assigned.getUsername());
                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPaymentDTO.getId().intValue(), partnerPayment.getPartner().getName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                    }
                } else {

                    Map<String, Object> map;
                    map = hierarchyService.getTeamForNextApprove(partnerPayment.getPartner().getMvnoId(), partnerPayment.getPartner().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"),false , partnerPaymentDTO);
                    if (map.containsKey("assignableStaff")) {
                        genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
//                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPayment.getId().intValue(), partnerPayment.getPartner().getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                    } else {
                        partnerPaymentDTO.setNextStaff(null);
                        partnerPaymentDTO.setNextTeamHierarchyMappingId(null);
                        if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                            partnerPaymentDTO.setStatus(SubscriberConstants.ACTIVE);
                        } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected"))
                            partnerPaymentDTO.setStatus(SubscriberConstants.REJECT);
                        PartnerPaymentDTO dto=saveEntity(partnerPaymentDTO);
                    }
                    workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.PARTNER_BALANCE, partnerPayment.getId().intValue(), partnerPayment.getPartner().getName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                }
            } else {
                approvedByName.append("Administrator");
                if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved"))
                    partnerPaymentDTO.setStatus(SubscriberConstants.ACTIVE);
                else
                    partnerPaymentDTO.setStatus(SubscriberConstants.REJECT);
                partnerPaymentDTO.setNextTeamHierarchyMappingId(null);
                partnerPaymentDTO.setNextStaff(null);
                PartnerPaymentDTO dto=saveEntity(partnerPaymentDTO);
            }


            Partner partner = partnerrepo.getOne(partnerPayment.getPartner().getId());
            if(partnerPaymentDTO.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE))
            {
//                if (partnerPaymentDTO.getAmount() != null && partnerPaymentDTO.getAmount()>0 && partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_BALANCE)) {
//                    if (partner.getCreditConsume() == 0) {
//                        partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
//                    } else if (partner.getCreditConsume() < (partner.getBalance() + partnerPaymentDTO.getAmount())) {
//                        partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount() - partner.getCreditConsume());
//                        partner.setCreditConsume(0.0d);
//                        if(partner.getPartnerType().equals(CommonConstants.PARTNER_TYPE_FRANCHISE))
//                            adjustPaymentAndAddCommissionAgainstPwscPartner(partner);
//                    } else if (partner.getCreditConsume() > (partner.getBalance() + partnerPaymentDTO.getAmount())) {
//                        partner.setBalance(0.0d);
//                        partner.setCreditConsume(partner.getCreditConsume() - (partner.getBalance() + partnerPaymentDTO.getAmount()));
//                    }
//                    partner.setCredit(partnerPaymentDTO.getCredit() + partner.getCredit());
//                    partnerrepo.save(partner);
//
//                    PartnerLedgerBalanceDTO dto1 = new PartnerLedgerBalanceDTO();
//                    dto1.setCredit(partnerPaymentDTO.getCredit());
//                    dto1.setPartner_id(partnerPaymentDTO.getPartnerId());
//                    dto1.setAmount(partnerPaymentDTO.getAmount());
//                    dto1.setPaymentdate(LocalDate.now());
//                    partnerLedgerService.addBalance(dto1);
//                    partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partner.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
//                }
//
//
//                if (partnerPaymentDTO.getAmount() != null && partnerPaymentDTO.getAmount()>0 && partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.TRANS_CATEGORY_ADD_CREDIT))
//                {
//                    partner.setCredit(partnerPaymentDTO.getAmount() + partner.getCredit());
//                    partnerrepo.save(partner);
//                }
//
//                if(partnerPaymentDTO.getTranscategory().equalsIgnoreCase("Withdraw") ) {
//                    partner.setCommrelvalue(partner.getCommrelvalue() - partnerPayment.getAmount());
//                    partnerrepo.save(partner);
//                    PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
//                    dto.setPartner_id(partnerPaymentDTO.getPartnerId());
//                    dto.setPaymentdate(LocalDate.now());
//                    dto.setAmount(partnerPaymentDTO.getAmount());
//                    partnerLedgerService.addBalance(dto);
//                    partnerrepo.save(partner);
//                    partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerPaymentDTO.getAmount(), partnerPaymentDTO.getPartnerId(), CommonConstants.WITHDRAW_COMMISSION, "Commission Withdraw");
//                }
//
//                if(partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.BALANCE_TRANSFER) ){
//                    if (partnerPayment.getAmount() <= partner.getBalance()) {
//                        partner.setBalance(partner.getBalance() - partnerPaymentDTO.getAmount());
//                        if (partner.getCommrelvalue() != null)
//                            partner.setCommrelvalue(partner.getCommrelvalue() + partnerPaymentDTO.getAmount());
//                        else partner.setCommrelvalue(partnerPaymentDTO.getAmount());
//                        PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
//                        dto.setPartner_id(partnerPaymentDTO.getPartnerId());
//                        dto.setPaymentdate(LocalDate.now());
//                        dto.setAmount(partnerPaymentDTO.getAmount());
//                        partnerLedgerService.addBalance(dto);
//                        partnerrepo.save(partner);
//                        partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerPaymentDTO.getAmount(), partnerPaymentDTO.getPartnerId(), CommonConstants.BALANCE_TRANSFER, "Deduct Balance From Partner Wallet");
//
//                    }
//                }
//                if(partnerPaymentDTO.getTranscategory().equalsIgnoreCase(CommonConstants.COMMISSION_TRANSFER) ){
//                    if (partnerPayment.getAmount() <= partner.getCommrelvalue()) {
//                        partner.setCommrelvalue(partner.getCommrelvalue() - partnerPaymentDTO.getAmount());
//                        if (partner.getBalance() != null)
//                            partner.setBalance(partner.getBalance() + partnerPaymentDTO.getAmount());
//                        else partner.setBalance(partnerPaymentDTO.getAmount());
//                    }
//                    partnerrepo.save(partner);
//                    partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerPaymentDTO.getAmount(), partnerPaymentDTO.getPartnerId(), CommonConstants.COMMISSION_TRANSFER, "Transfer Commission into Balance");
//                }

                SavePartnerPaymentMessage savePartnerPaymentMessage=new SavePartnerPaymentMessage();
                savePartnerPaymentMessage.setPartnerPayment(new PartnerPayment(partnerPayment));
                savePartnerPaymentMessage.setPartnerPaymentDTO(new PartnerPaymentDTO(partnerPaymentDTO));
                //messageSender.send(savePartnerPaymentMessage, SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_PARTNER);
//                messageSender.send(savePartnerPaymentMessage, SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(savePartnerPaymentMessage,SavePartnerPaymentMessage.class.getSimpleName()));
            }
        }
        return genericDataDTO;
    }


    public GenericDataDTO getPartnerPaymentApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPartnerPayment.isNotNull().and(qPartnerPayment.isDeleted.eq(false)).and(qPartnerPayment.status.eq(SubscriberConstants.NEW_ACTIVATION)).and(qPartnerPayment.nextStaff.eq(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(1).or(qPartnerPayment.partner.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<PartnerPayment> paginationList = partnerPaymentRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                data.setPartnerName(data.getPartner().getName());
                return data;
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
    }

    public GenericDataDTO getAllPartnerPayment(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder,String requestFrom , Integer mvnoId) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPartnerPayment.isNotNull().and(qPartnerPayment.isDeleted.eq(false));
        if(requestFrom.equals("pw"))
        {
            List<Integer> childPartnerIds = getChildPartnerIds(getLoggedInUserPartnerId());
            if (!childPartnerIds.isEmpty()) {
                booleanExpression = booleanExpression.and(qPartnerPayment.partner.id.in(childPartnerIds));
            }
        }
        else
        {
            if(getLoggedInUser().getPartnerId()!=1 && !getLoggedInUser().getLco())
            {
                booleanExpression = booleanExpression.and(qPartnerPayment.partner.id.eq(getLoggedInUserPartnerId()));
            }
            else
            {
                // TODO: pass mvnoID manually 6/5/2025
                if (mvnoId != 1 && getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().isEmpty())
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.in(1, mvnoId));
                    // TODO: pass mvnoID manually 6/5/2025
                else if (mvnoId != 1 && getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
                    // TODO: pass mvnoID manually 6/5/2025
                    booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(mvnoId).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff())));
                }
            }

            if(getLoggedInUser()!=null && getLoggedInUser().getLco())
                booleanExpression = booleanExpression.and(qPartnerPayment.partner.id.eq(getLoggedInUserPartnerId()));
        }

        Page<PartnerPayment> paginationList = partnerPaymentRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                data.setPartnerName(data.getPartner().getName());
                return data;
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
    }

    private List<Integer> getChildPartnerIds(int parentpartnerid) {
        if(parentpartnerid != -1)
            return partnerrepo.getChildPartnerIdFromParentPartnerId(parentpartnerid);
        return new ArrayList<>();
    }

    public GenericDataDTO getAllPartnerCredit(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerCreditDocument qPartnerCreditDocument = QPartnerCreditDocument.partnerCreditDocument;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qPartnerCreditDocument.isNotNull().and(qPartnerCreditDocument.isDelete.eq(false));
//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qPartnerCreditDocument.partner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(1).or(qPartnerPayment.partner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff()))));
//        }

        Page<PartnerCreditDocument> paginationList = partnerCredRepo.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }


    public GenericDataDTO getAllPartnerInvoice(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "billdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerDebitDocument qPartnerDebitDocument = QPartnerDebitDocument.partnerDebitDocument ;
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        BooleanExpression booleanExpression = qPartnerDebitDocument.isNotNull().and(qPartnerDebitDocument.isDelete.eq(false));
//        if (getMvnoIdFromCurrentStaff() != 1)
//            booleanExpression = booleanExpression.and(qPartnerCreditDocument.partner.mvnoId.in(1, getMvnoIdFromCurrentStaff()));
//        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
//            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(1).or(qPartnerPayment.partner.mvnoId.eq(getMvnoIdFromCurrentStaff()).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff()))));
//        }

        Page<PartnerDebitDocument> paginationList = partnerCreditDocRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    public void adjustPaymentAndAddCommissionAgainstPwscPartner(Partner partner)
    {
        List<TempPartnerLedgerDetail> list=tempPartnerLedgerDetailsRepository.findAllByPartner_Id(partner.getId());
        if(list!=null && !list.isEmpty())
        {
            list=list.stream().filter(x-> x.getPaymentStatus()==2).collect(Collectors.toList());
            list.stream().forEach(record -> {
                if(record.getCustid()!=null)
                {
                    Optional<Customers> customers = customersRepository.findById(record.getCustid());
                    if (customers.isPresent()) {
                        Optional<DebitDocument> debitDocument=debitDocRepository.findById(record.getDebitDocId().intValue());
                        if(debitDocument.isPresent())
                        {
                            DebitDocument document=debitDocument.get();
                            Double amount=document.getTotalamount();
                            if(document.getAdjustedAmount()!=null)
                                amount=document.getTotalamount() - document.getAdjustedAmount();
                            else
                                amount=document.getTotalamount();

                            Optional<StaffUser> staffUser=staffUserRepository.findById(record.getStaffUserId());
                            if(staffUser.isPresent()) {
                                adjustPaymentAgainstInvoiceAmount(customers.get(), amount, record.getDebitDocId(), staffUser.get());
                                partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(customers.get(),amount,record.getDebitDocId());
                                //partnerCommissionService.addPartnerLedgerDetailAgainstInvoiceAmount(amount, customers.get(), partner,record.getDebitDocId());
                                List<TempPartnerLedgerDetail> list1=new ArrayList<>();
                                list1.add(record);
                                partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(list1);
                                tempPartnerLedgerDetailsRepository.delete(record);
                            }
                        }
                    }
                }
            });
        }
    }

    // public boolean adjustPaymentAgainstInvoiceAmount(Customers customers, Double totalInvoiceAmount, Long invoiceId, StaffUser staffUser)
    // {
    //     Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
    //     if(document.isPresent()) {
    //         CreditDocument creditDocument = new CreditDocument();
    //         creditDocument.setAdjustedAmount(0.0d);
    //         creditDocument.setAmount(totalInvoiceAmount);
    //         creditDocument.setCustomer(customers);
    //         creditDocument.setStatus(CommonUtils.PAYMENT_STATUS_APPROVED);
    //         creditDocument.setLcoid(customers.getLcoId());
    //         creditDocument.setPaymentdate(LocalDate.now());
    //         creditDocument.setType(CommonUtils.PAYMENT_TYPE);
    //         creditDocument.setCreatedate(LocalDateTime.now());
    //         creditDocument.setIsDelete(false);
    //         creditDocument.setTdsflag(false);
    //         creditDocument.setPaydetails4("Received By Partner : "+customers.getPartner().getName());
    //         creditDocument.setPaytype(com.adopt.apigw.modules.subscriber.model.Constants.ADVANCE);
    //         creditDocument.setApproverid(staffUser.getId());
    //         creditDocument.setReferenceno(String.valueOf(CommonUtils.getUniqueNumber()));
    //         creditDocument.setPaymode(CommonConstants.PAYMENT_MODE_TYPE_CASH);
    //         creditDocument.setTds_received(false);
    //         creditDocument.setCreatedById(staffUser.getId());
    //         creditDocument.setCreatedByName(staffUser.getFullName());
    //         creditDocument.setMvnoId(staffUser.getMvnoId());
    //         creditDocument.setLastModifiedById(staffUser.getId());
    //         creditDocument.setLastModifiedByName(staffUser.getFullName());
    //         DebitDocument debitDocument= debitDocRepository.findById(invoiceId.intValue()).get();
    //         creditDocument=creditDocRepository.save(creditDocument);
    //         creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument, CommonUtils.ADDR_TYPE_PRESENT,null,debitDocument));
    //         creditDocument=creditDocRepository.save(creditDocument);

    //         CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
    //         creditDebitDocMapping.setAdjustedAmount(0.0d);
    //         creditDebitDocMapping.setIsDeleted(false);
    //         creditDebitDocMapping.setDebtDocId(invoiceId.intValue());
    //         creditDebitDocMapping.setCreditDocId(creditDocument.getId());
    //         creditDebitDocMapping=creditDebtMappingRepository.save(creditDebitDocMapping);
    //         if(debitDocument.getAdjustedAmount()!=null)
    //             debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+totalInvoiceAmount);
    //         else
    //             debitDocument.setAdjustedAmount(totalInvoiceAmount);
    //         debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
    //         debitDocument=debitDocRepository.save(debitDocument);
    //         creditDocService.addLedgerAndLedgerDetailEntry(creditDocument,customers,false);
    //         return true;
    //     }
    //     return false;
    // }



    // public void adjustPaymentAndAddCommissionAgainstPwscPartner(Partner partner)
    // {
    //     List<TempPartnerLedgerDetail> list=tempPartnerLedgerDetailsRepository.findAllByPartner_Id(partner.getId());
    //     if(list!=null && !list.isEmpty())
    //     {
    //         list=list.stream().filter(x-> x.getPaymentStatus()==2).collect(Collectors.toList());
    //         list.stream().forEach(record -> {
    //             if(record.getCustid()!=null)
    //             {
    //                 Optional<Customers> customers = customersRepository.findById(record.getCustid());
    //                 if (customers.isPresent()) {
    //                     Optional<DebitDocument> debitDocument=debitDocRepository.findById(record.getDebitDocId().intValue());
    //                     if(debitDocument.isPresent())
    //                     {
    //                         DebitDocument document=debitDocument.get();
    //                         Double amount=document.getTotalamount();
    //                         if(document.getAdjustedAmount()!=null)
    //                             amount=document.getTotalamount() - document.getAdjustedAmount();
    //                         Optional<StaffUser> staffUser=staffUserRepository.findById(record.getStaffUserId());
    //                         if(staffUser.isPresent()) {
    //                             adjustPaymentAgainstInvoiceAmount(customers.get(), amount, record.getId(), staffUser.get());
    //                             //partnerCommissionService.updatePartnerBalanceAgainstInvoiceAmount(customers.get(),amount, record.getDebitDocId());
    //                             partnerCommissionService.addPartnerLedgerDetailAgainstInvoiceAmount(amount, customers.get(), partner,record.getDebitDocId());
    //                             List<TempPartnerLedgerDetail> list1=new ArrayList<>();
    //                             list1.add(record);
    //                             partnerCommissionService.addPartnerLedgerDetailAgainstCommissionAmount(list1);
    //                             tempPartnerLedgerDetailsRepository.delete(record);
    //                         }
    //                     }
    //                 }
    //             }
    //         });
    //     }
    // }

    public boolean adjustPaymentAgainstInvoiceAmount(Customers customers, Double totalInvoiceAmount, Long invoiceId, StaffUser staffUser)
    {
        Optional<DebitDocument> document=debitDocRepository.findById(invoiceId.intValue());
        if(document.isPresent()) {
            CreditDocument creditDocument = new CreditDocument();
            creditDocument.setAdjustedAmount(0.0d);
            creditDocument.setAmount(totalInvoiceAmount);
            creditDocument.setCustomer(customers);
            creditDocument.setStatus(UtilsCommon.PAYMENT_STATUS_APPROVED);
            creditDocument.setLcoid(customers.getLcoId());
            creditDocument.setPaymentdate(LocalDate.now());
            creditDocument.setType(UtilsCommon.PAYMENT_TYPE);
            creditDocument.setCreatedate(LocalDateTime.now());
            creditDocument.setIsDelete(false);
            creditDocument.setTdsflag(false);
            creditDocument.setPaydetails4("Received By Partner : "+customers.getPartner().getName());
            creditDocument.setPaytype(com.adopt.apigw.modules.subscriber.model.Constants.ADVANCE);
            creditDocument.setApproverid(staffUser.getId());
            creditDocument.setReferenceno(String.valueOf(UtilsCommon.getUniqueNumber()));
            creditDocument.setPaymode(CommonConstants.PAYMENT_MODE_TYPE_CASH);
            creditDocument.setTds_received(false);
            creditDocument.setCreatedById(staffUser.getId());
            creditDocument.setCreatedByName(staffUser.getFullName());
            creditDocument.setMvnoId(staffUser.getMvnoId());
            creditDocument.setLastModifiedById(staffUser.getId());
            creditDocument.setLastModifiedByName(staffUser.getFullName());
            DebitDocument debitDocument= debitDocRepository.findById(invoiceId.intValue()).get();
            creditDocument.setXmldocument(PaymentDetailsXml.getPaymentDetails(creditDocument, UtilsCommon.ADDR_TYPE_PRESENT,null,debitDocument));
            creditDocument.setAdjustedAmount(totalInvoiceAmount);
            creditDocument=creditDocRepository.save(creditDocument);

            CreditDebitDocMapping creditDebitDocMapping = new CreditDebitDocMapping();
            creditDebitDocMapping.setAdjustedAmount(creditDocument.getAdjustedAmount());
            creditDebitDocMapping.setIsDeleted(false);
            creditDebitDocMapping.setDebtDocId(invoiceId.intValue());
            creditDebitDocMapping.setCreditDocId(creditDocument.getId());
            creditDebitDocMapping=creditDebtMappingRepository.save(creditDebitDocMapping);
            if(debitDocument.getAdjustedAmount()!=null)
                debitDocument.setAdjustedAmount(debitDocument.getAdjustedAmount()+totalInvoiceAmount);
            else
                debitDocument.setAdjustedAmount(totalInvoiceAmount);
            debitDocument.setPaymentStatus(CommonConstants.DEBIT_DOC_STATUS.CLEAR);
            debitDocument=debitDocRepository.save(debitDocument);
            creditDocService.addLedgerAndLedgerDetailEntry(creditDocument,customers,false);
            return true;
        }
        return false;
    }


    public GenericDataDTO searchPartnerPayment(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "paymentdate", CommonConstants.SORT_ORDER_DESC);
        QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qPartnerPayment.isNotNull().and(qPartnerPayment.isDeleted.eq(false));
        if(filters.get(0).getFilterValue()!=null)
            booleanExpression=booleanExpression.and((qPartnerPayment.partner.name.likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.partner.name.likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.transcategory.likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.paymentmode.likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.amount.stringValue().likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.paymentdate.stringValue().likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")).or(qPartnerPayment.status.likeIgnoreCase("%" + filters.get(0).getFilterValue() + "%")));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartnerPayment.partner.mvnoId.eq(1).or(qPartnerPayment.partner.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPartnerPayment.partner.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<PartnerPayment> paginationList = partnerPaymentRepository.findAll(booleanExpression, pageRequest);

        List<PartnerPayment> sortedList = paginationList.getContent().stream().sorted(Comparator.comparing(PartnerPayment::getCreatedate).reversed()).collect(Collectors.toList());

        genericDataDTO.setDataList(sortedList.stream().map(data -> {
            try {
                data.setPartnerName(data.getPartner().getName());
                return data;
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
    }


    public void updatePartnerOnlinePayment(CustomerPayment pojo) throws Exception {
        if (pojo.getPartnerPaymentId() != null)
        {
            PartnerPayment partnerPayment = partnerPaymentRepository.findById(pojo.getPartnerPaymentId().longValue()).orElse(null);
            PartnerPaymentDTO partnerPaymentDTO = partnerPaymentMapper.domainToDTO(partnerPayment, new CycleAvoidingMappingContext());

            StringBuilder approvedByName = new StringBuilder();
            approvedByName.append("Administrator");
                if (pojo.getStatus().equalsIgnoreCase("Success"))
                    partnerPaymentDTO.setStatus(SubscriberConstants.ACTIVE);
                else
                    partnerPaymentDTO.setStatus(SubscriberConstants.PAYMENT_FAILED);
                partnerPaymentDTO.setNextTeamHierarchyMappingId(null);
                partnerPaymentDTO.setNextStaff(null);
                PartnerPaymentDTO dto=saveEntity(partnerPaymentDTO);

            Partner partner = partnerrepo.getOne(partnerPayment.getPartner().getId());
            if(partnerPaymentDTO.getStatus().equalsIgnoreCase(SubscriberConstants.ACTIVE))
            {
                SavePartnerPaymentMessage savePartnerPaymentMessage=new SavePartnerPaymentMessage();
                savePartnerPaymentMessage.setPartnerPayment(new PartnerPayment(partnerPayment));
                savePartnerPaymentMessage.setPartnerPaymentDTO(new PartnerPaymentDTO(partnerPaymentDTO));
                //messageSender.send(savePartnerPaymentMessage, SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_PARTNER);
//                messageSender.send(savePartnerPaymentMessage, SharedDataConstants.QUEUE_PARTNER_APPROVE_PAYMENT_SHARE_REVENUE);
                kafkaMessageSender.send(new KafkaMessageData(savePartnerPaymentMessage,SavePartnerPaymentMessage.class.getSimpleName()));
            }
        }

    }
}
