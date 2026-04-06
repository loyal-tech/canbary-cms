package com.adopt.apigw.modules.customerDocDetails.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.modules.customerDocDetails.domain.CustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.domain.QCustomerDocDetails;
import com.adopt.apigw.modules.customerDocDetails.mapper.CustomerDocDetailsMapper;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.customerDocDetails.repository.CustomerDocDetailsRepository;
import com.adopt.apigw.modules.workflow.service.WorkflowAssignStaffMappingService;
import com.adopt.apigw.pojo.ClientServicePojo;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustDocumentVerificationMsg;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.*;
import com.adopt.apigw.service.postpaid.CustPlanMappingService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.StatusConstants;
import com.adopt.apigw.utils.ValidateCrudTransactionData;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerDocDetailsService extends ExBaseAbstractService<CustomerDocDetailsDTO, CustomerDocDetails, Long> {

    @Autowired
    private CustomerDocDetailsRepository customerDocDetailsRepository;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomerDocDetailsMapper customerDocDetailsMapper;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private CustomersService customersService;
    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private FileSystemService fileSystemService;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    StaffUserService staffUserService;

    private String path;

    @Autowired
    WorkflowAssignStaffMappingService workflowAssignStaffMappingService;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    NotificationTemplateRepository templateRepository;

    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;

    @Autowired
    private CustPlanMappingService custPlanMappingService;

    @Autowired
    private ClientServiceRepository clientServiceRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    StaffUserRepository staffUserRepository;


    public CustomerDocDetailsService(CustomerDocDetailsRepository customerDocDetailsRepository, CustomerDocDetailsMapper customerDocDetailsMapper) {
        super(customerDocDetailsRepository, customerDocDetailsMapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[CustomerDocDetailsService]";
    }

    private String PATH;

    @Override
    public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        return null;
    }

    public List<CustomerDocDetailsDTO> getAllEntities(Integer mvnoId) throws Exception {
        try {
            return customerDocDetailsRepository.findAllByIsDeleteFalse()
                    .stream().map(domain -> customerDocDetailsMapper.domainToDTO(domain, new CycleAvoidingMappingContext()))
                    .collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : "
                    + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public List<CustomerDocDetailsDTO> uploadDocument(List<CustomerDocDetailsDTO> customerDocDetailsList, MultipartFile[] files, Integer mvnoId)
            throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [uploadDocument()] ";
        // PATH="D:/";
//        Integer mvnoId = customerDocDetailsList.get(0).getCustomer().getMvnoId();
        // TODO: pass mvnoID manually 6/5/2025
        PATH = clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.CUST_DOC_PATH, mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        String panLength = clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.PAN_NUMBER_LENGTH , mvnoId);
        if(panLength == null || panLength.isEmpty()){
            panLength = "10";
        }
//        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue();
        List<CustomerDocDetailsDTO> finalResponseList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        Integer loggedInStaffMvnoId = mvnoId;
        try {
            for (CustomerDocDetailsDTO custDoc : customerDocDetailsList) {
//                custDoc.setDocumentNumber(custDoc.getDocumentNumber());
//                custDoc.setDocStatus("pending");

                if (null != custDoc.getCustId()) {
//                    if(custDoc.getStartDate().isAfter(custDoc.getEndDate())){
//                        throw new CustomValidationException(400,"End date must be greater than start date",null);
//                    }
                    if (custDoc.getStartDate() != null || custDoc.getEndDate() != null) {
                        if (custDoc.getStartDate().isAfter(custDoc.getEndDate())) {
                            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "End date must be greater than start date", null);
                        }
                    }
                    Customers customers = customersRepository.findById(custDoc.getCustId()).get();
                    if(loggedInStaffMvnoId !=1 && !customers.getMvnoId().equals(loggedInStaffMvnoId)){
                        throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "You do not have permission for this operation", null);
                    }
                    if (custDoc.getMode().equalsIgnoreCase(DocumentConstants.ONLINE)) {
                        if(custDoc.getDocumentNumber() != null) {
                            if (custDoc.getDocumentNumber().contains("*")) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Document number Invalid, It's not contain any *", null);
                            }
                            if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD) && custDoc.getDocumentNumber().length() != 12) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Adhar card number should be 12 digit long", null);
                            }
                            if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD) && custDoc.getDocumentNumber().length() != Integer.parseInt(panLength)) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Pan card number should be "+panLength+" digit long", null);
                            }
                            if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER) && (custDoc.getDocumentNumber().length() <= 3 || custDoc.getDocumentNumber().length() >= 15)) {
                                throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "GST number Should be between 3 to 15 digits long", null);
                            }
                        }
                        if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {
                            if(custDoc.getDocumentNumber() != null && customers.getAadhar() != null) {
                                if (ValidateCrudTransactionData.validateStringTypeFieldValue(customers.getAadhar())) {
                                    if (customers.getAadhar().equalsIgnoreCase(custDoc.getDocumentNumber()))
                                        throw new RuntimeException("Aadhar already exists! Please delete or update the same entity");
                                }
                                customers.setAadhar(updatedAadhar(customers.getId(), custDoc.getDocumentNumber()));
                            }
                        } else if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) {
                            if(custDoc.getDocumentNumber() != null && customers.getPan() != null) {
                                if (ValidateCrudTransactionData.validateStringTypeFieldValue(customers.getPan())) {
                                    if (customers.getPan().equalsIgnoreCase(custDoc.getDocumentNumber()))
                                        throw new RuntimeException("PAN already exists! Please delete or update the same entity");
                                }
                                customers.setPan(updatedPan(customers.getId(), custDoc.getDocumentNumber()));
                            }
                            else{
                                if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD) && customers.getPan().length() != Integer.parseInt(panLength)) {
                                    throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "Pan card number should be "+panLength+" digit long", null);
                                }
                            }
                        } else if (custDoc.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER)) {
                            if(custDoc.getDocumentNumber() != null && customers.getGst() != null) {
                                if (ValidateCrudTransactionData.validateStringTypeFieldValue(customers.getGst())) {
                                    if (customers.getGst().equalsIgnoreCase(custDoc.getDocumentNumber()))
                                        throw new RuntimeException("GST Number already exists! Please delete or update the same entity");
                                }
                                customers.setGst(updatedGst(customers.getId(), custDoc.getDocumentNumber()));
                            }
                        }
                        customersService.update(customers);
                    }
                    if (null != customers) {
                        //  custDoc.setMode(Docume  ntConstants.OFFLINE);
                        String subFolderName = File.separator + customers.getUsername().trim() + File.separator;
                        String path = PATH + subFolderName;
                        ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
                        if (null == custDoc.getDocId()) {
                            if (null != custDoc.getFilename()) {
                                MultipartFile file1 = fileUtility.getFileFromArray(custDoc.getFilename(), files);
                                if (null != file1) {
                                    custDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                    custDoc.setDocumentNumber(custDoc.getDocumentNumber());
                                    custDoc = super.saveEntity(custDoc);
                                    finalResponseList.add(custDoc);
                                }
                            }
                        } else {
                            CustomerDocDetailsDTO custDocDTO = getEntityById(custDoc.getDocId(),customers.getMvnoId());
                            if (null != custDocDTO) {
                                if (null != custDocDTO.getFilename()
                                        && null != custDoc.getFilename()
                                        && !custDocDTO.getFilename().equalsIgnoreCase(custDoc.getFilename())) {
                                    fileUtility.removeFileAtServer(custDocDTO.getUniquename(), path);
                                }
                                MultipartFile file1 = fileUtility.getFileFromArray(custDoc.getFilename(), files);
                                if (null != file1) {
                                    custDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                }
                                custDoc.setDocumentNumber(custDoc.getDocumentNumber());
                                custDoc = super.updateEntity(custDoc);
                            }
                            finalResponseList.add(custDoc);
                        }
                        if (custDoc.getNextTeamHierarchyMappingId() == null) {
                            custDoc = super.saveEntity(custDoc);
                            if (custDoc.getDocStatus() != null && !"".equals(custDoc.getDocStatus())) {
                                if (custDoc.getDocStatus().equalsIgnoreCase("pending")) {
                                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,mvnoId).equals("TRUE")) {
                                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(),customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, false, true, custDoc);
                                        int staffId = 0;
                                        if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                            staffId = Integer.parseInt(map.get("staffId"));
                                            StaffUser assignedStaff = staffUserService.get(staffId,customers.getMvnoId());
                                            custDoc.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                            custDoc.setNextStaff(staffId);
                                            hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(),null, assignedStaff.getId().longValue());
                                            workflowAuditService.saveAudit(Math.toIntExact(custDoc.getDocId()), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(custDoc.getDocId()),customers.getUsername(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                        } else {
                                            StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customers.getMvnoId());
                                            custDoc.setNextTeamHierarchyMappingId(null);
                                            custDoc.setNextStaff(currentStaff.getId());
                                            workflowAuditService.saveAudit(Math.toIntExact(custDoc.getDocId()), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(custDoc.getDocId()),customers.getUsername(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                        }
                                    } else {
                                        StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customers.getMvnoId());
                                        custDoc.setNextTeamHierarchyMappingId(null);
                                        custDoc.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(Math.toIntExact(custDoc.getDocId()), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(custDoc.getDocId()),customers.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    }
                                }
                                customerDocDetailsRepository.save(customerDocDetailsMapper.dtoToDomain(custDoc, new CycleAvoidingMappingContext()));
                            }
                        }
                    } else
                        throw new DataNotFoundException("Customer Not Found!");
                } else
                    throw new RuntimeException("Please Provide Customer");
            }

            return finalResponseList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Transactional
    public CustomerDocDetailsDTO uploadDocumentOnline(CustomerDocDetailsDTO customerDocDetails, Boolean isUpdate) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [uploadDocumentOnline()] ";
        CustomerDocDetailsDTO saveEntity = null;

        Optional<CustomerDocDetails> customerDocDetails1 = customerDocDetailsRepository.findById(customerDocDetails.getDocId());
        try {
            if (null != customerDocDetails.getCustId()) {
//                    if(customerDocDetails.getStartDate().isAfter(customerDocDetails.getEndDate())){
//                        throw new CustomValidationException(400,"End date must be greater than start date",null);
//                    }
                Customers customer = customersRepository.findById(customerDocDetails.getCustId()).get();
                if (null != customer) {
                    if (customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {
                        if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getAadhar()))
                            throw new RuntimeException("Aadhar already exists! Please delete or update the same entity");
                        customer.setAadhar(updatedAadhar(customer.getId(), customerDocDetails1.get().getDocumentNumber()));
                    } else if (customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) {
                        if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getPan()))
                            throw new RuntimeException("PAN already exists! Please delete or update the same entity");
                        customer.setPan(updatedPan(customer.getId(), customerDocDetails1.get().getDocumentNumber()));
                    } else if (customerDocDetails.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER)) {
                        if (!isUpdate && ValidateCrudTransactionData.validateStringTypeFieldValue(customer.getGst()))
                            throw new RuntimeException("GST Number already exists! Please delete or update the same entity");
                        customer.setGst(updatedGst(customer.getId(), customerDocDetails1.get().getDocumentNumber()));
                    }
                    customersService.update(customer);
                    if(customerDocDetails.getFilename() != null){
                        customerDocDetails.setFilename(customerDocDetails.getFilename());
                    }else {
                        customerDocDetails.setFilename(customerDocDetails1.get().getFilename());
                    }
                    customerDocDetails.setMode(DocumentConstants.ONLINE);
                    customerDocDetails.setDocStatus(customerDocDetails.getDocStatus());
                    customerDocDetails.setDocumentNumber(customerDocDetails1.get().getDocumentNumber());
                    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(customerDocDetails.getRemark()))
                        customerDocDetails.setRemark(null);
                    if (!ValidateCrudTransactionData.validateStringTypeFieldValue(customerDocDetails.getUniquename()))
                        customerDocDetails.setUniquename(null);

                    if (null == customerDocDetails.getDocId())
                        saveEntity = super.saveEntity(customerDocDetails);
                    else
                    if(customerDocDetails.getDocumentNumber() != null){
                        customerDocDetails.setDocumentNumber(customerDocDetails.getDocumentNumber());
                    }else {
                        customerDocDetails.setDocumentNumber(customerDocDetails1.get().getDocumentNumber());
                    }
                        saveEntity = super.saveEntity(customerDocDetails);
//                        saveEntity.setDocumentNumber(getMaskedDocuments(customerDocDetails.getDocSubType(), customerDocDetails.getDocumentNumber()));

                    if (saveEntity.getNextTeamHierarchyMappingId() == null) {
                        if (saveEntity.getDocStatus() != null && !"".equals(saveEntity.getDocStatus())) {
                            if (saveEntity.getDocStatus().equalsIgnoreCase("pending")) {
                                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,customer.getMvnoId()).equals("TRUE")) {
                                    Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(saveEntity.getCustomer().getMvnoId(), saveEntity.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, false, true, saveEntity);
                                    int staffId = 0;
                                    if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                        staffId = Integer.parseInt(map.get("staffId"));
                                        StaffUser assignedStaff = staffUserService.get(staffId,customer.getMvnoId());
                                        saveEntity.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                        saveEntity.setNextStaff(staffId);
                                        hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), null,assignedStaff.getId().longValue());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, saveEntity.getCustId(), saveEntity.getCustomer().getUsername(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                    } else {
                                        StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customer.getMvnoId());
                                        saveEntity.setNextTeamHierarchyMappingId(null);
                                        saveEntity.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, saveEntity.getCustId(), saveEntity.getCustomer().getUsername(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    }
                                } else {
                                    Map<String, Object> map = hierarchyService.getTeamForNextApprove(saveEntity.getCustomer().getId(), saveEntity.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, false, true, saveEntity);
                                    if (map.containsKey("assignableStaff")) {
                                        StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customer.getMvnoId());
                                        saveEntity.setNextTeamHierarchyMappingId(null);
                                        saveEntity.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, saveEntity.getCustId(), saveEntity.getCustomer().getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    } else {
                                        StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customer.getMvnoId());
                                        saveEntity.setNextTeamHierarchyMappingId(null);
                                        saveEntity.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, saveEntity.getCustId(), saveEntity.getCustomer().getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                    }
                                }
                            }
                            customerDocDetailsRepository.save(customerDocDetailsMapper.dtoToDomain(saveEntity, new CycleAvoidingMappingContext()));
                        }
                    }
                } else
                    throw new DataNotFoundException("Customer Not Found!");
            } else
                throw new RuntimeException("Please Provide Customer");
            return saveEntity;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<CustomerDocDetailsDTO> findDocsByCustomerId(Integer custId) {
        String SUBMODULE = getModuleNameForLog() + " [findDocsByCustomerId()] ";
        try {
            Customers customers = customersService.getById(custId);
            //System.out.println(customers.getPlanMappingList());
            List<CustomerDocDetailsDTO> customerDocDetailsDTOS = new ArrayList<>();
            List<CustomerDocDetails> docList = customerDocDetailsRepository.findAllByCustomer_idAndIsDeleteIsFalse(custId);
            if (null != docList && 0 < docList.size()) {
                customerDocDetailsDTOS = docList.stream().map(data -> customerDocDetailsMapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                        .collect(Collectors.toList());
            }
            try {
                for (CustomerDocDetailsDTO customerDocDetailsDTO : customerDocDetailsDTOS) {
                    if(Objects.nonNull(customerDocDetailsDTO.getNextStaff())){
                        Integer parentId = staffUserRepository
                                .findParentIdByStaffUserId(customerDocDetailsDTO.getNextStaff())
                                .orElse(null);

                        customerDocDetailsDTO.setParentStaffId(parentId);
                    }
                    if (customerDocDetailsDTO.getMode().equalsIgnoreCase("Online")) {
                        if (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
                            customerDocDetailsDTO.setDocumentNumber(getMaskedDocuments(DocumentConstants.AADHAR_CARD, customerDocDetailsDTO.getDocumentNumber()));
                        else if (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                            customerDocDetailsDTO.setDocumentNumber(getMaskedDocuments(DocumentConstants.PAN_CARD, customerDocDetailsDTO.getDocumentNumber()));
                        else if (customerDocDetailsDTO.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
                            customerDocDetailsDTO.setDocumentNumber(getMaskedDocuments(DocumentConstants.GST_NUMBER, customerDocDetailsDTO.getDocumentNumber()));
                    }
                }

            } catch (Exception ex1) {
                new Exception("Invalid Input Format");
            }
            return customerDocDetailsDTOS;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);

            throw ex;
        }
    }

    public boolean isCustDocPending(Integer custId) {
        String SUBMODULE = getModuleNameForLog() + " [isCustDocApproved()] ";
        boolean isCustDocStatusPending = false;
        try {
            List<CustomerDocDetails> docList = customerDocDetailsRepository.findAllByCustomer_idAndIsDeleteIsFalse(custId);
            if (null != docList && 0 < docList.size()) {
                List<CustomerDocDetails> docListTemp = docList.stream().filter(doc -> doc.getDocStatus().equalsIgnoreCase("pending")).collect(Collectors.toList());
                if (null != docListTemp && 0 < docListTemp.size()) {
                    isCustDocStatusPending = true;
                } else {
                    isCustDocStatusPending = false;
                }
            } else {
                isCustDocStatusPending = false;
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return isCustDocStatusPending;
    }

    public String deleteDocument(List<Long> docIdList, Integer custId) throws Exception {
        String SUBMODULE = getModuleNameForLog() + " [deleteDocument()] ";
        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue();
        try {
            Customers customers = customersRepository.findById(custId).get();
            if (null != customers) {
                String subFolderName = customers.getUsername().trim() + "/";
                String path = PATH + subFolderName;
                for (Long id : docIdList) {
                    CustomerDocDetailsDTO dbDTO = getEntityById(id,getMvnoIdFromCurrentStaff(custId));
                    if (null != dbDTO) {
                        fileUtility.removeFileAtServer(dbDTO.getUniquename(), path);
                        super.deleteEntity(dbDTO);
                    } else throw new DataNotFoundException("Document Not Found with id = " + id);
                }
                return SubscriberConstants.DELETED_SUCCESSFULLY;
            } else throw new DataNotFoundException("Customer Not Found!");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEntity(CustomerDocDetailsDTO entity) throws Exception {
        try {
            Customers customers = customersService.getById(entity.getCustId());
            if (entity.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || entity.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD))
                customers.setAadhar(null);
            else if (entity.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD))
                customers.setPan(null);
            else if (entity.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER))
                customers.setGst(null);
            entity.setIsDelete(true);
            customerDocDetailsRepository.save(customerDocDetailsMapper.dtoToDomain(entity, new CycleAvoidingMappingContext()));
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("CustDoc Details");
        createExcel(workbook, sheet, CustomerDocDetailsDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, CustomerDocDetailsDTO.class, null,mvnoId);
    }

    public CustomerDocDetails approveCustDoc(Long docId, String status) {
        CustomerDocDetails doc = customerDocDetailsRepository.findById(docId).get();
        doc.setDocStatus(status);
        return customerDocDetailsRepository.save(doc);
    }

    private String getMaskedDocuments(String documentType, String documentNumber) {
        try {
            if (documentType.equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || documentType.equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {

                if(documentNumber!=null && !documentNumber.equalsIgnoreCase("") && documentNumber.length()==12)
                    return DocumentConstants.AADHAR_STAR_PATTERN + documentNumber.substring(8);
            }
            if (documentType.equalsIgnoreCase(DocumentConstants.PAN_CARD)) {

                if(documentNumber!=null && !documentNumber.equalsIgnoreCase("") && documentNumber.length()==10)
                    return DocumentConstants.PAN_STAR_PATTERN + documentNumber.substring(6);
            }
            if (documentType.equalsIgnoreCase(DocumentConstants.GST_NUMBER)) {

                if(documentNumber!=null && !documentNumber.equalsIgnoreCase("") && documentNumber.length() >= 3 || documentNumber.length() <= 15)
                    return DocumentConstants.GST_STAR_PATTERN + documentNumber.substring(3);
            }
        } catch (Exception e) {
            return "Invalid Input format";
        }
        return "";
    }

    private String updatedAadhar(Integer customerId, String aadharNumber) {
        Customers customers = customersRepository.findById(customerId).get();
        if (!aadharNumber.contains("*"))
            return aadharNumber;
        return customers.getAadhar();
    }

    private String updatedPan(Integer customerId, String panNumber) {
        Customers customers = customersRepository.findById(customerId).get();
        if (!panNumber.contains("*"))
            return panNumber;
        return customers.getPan();
    }

    private String updatedGst(Integer customerId, String gstNumber) {
        Customers customers = customersRepository.findById(customerId).get();
        if (!gstNumber.contains("*"))
            return gstNumber;
        return customers.getGst();
    }

    @Override
    public boolean deleteVerification(Integer custId) throws Exception {
        boolean flag = false;
        Integer count = customerDocDetailsRepository.deleteVerify(custId);
        if (count > 0) {
            flag = true;
        }
        return flag;
    }

    public void saveCustDocFromLeadDoc(List<CustomerDocDetailsDTO> customerDocDetailsDTOList) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd");
            for (CustomerDocDetailsDTO dto : customerDocDetailsDTOList) {
                Customers customers = customersRepository.findById(dto.getCustId()).get();
                if (null != customers) {
                    if (dto.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAAR_CARD) || dto.getDocSubType().equalsIgnoreCase(DocumentConstants.AADHAR_CARD)) {
                        customers.setAadhar(dto.getDocumentNumber());
                    } else if (dto.getDocSubType().equalsIgnoreCase(DocumentConstants.PAN_CARD)) {
                        customers.setPan(dto.getDocumentNumber());
                    } else if (dto.getDocSubType().equalsIgnoreCase(DocumentConstants.GST_NUMBER)) {
                        customers.setGst(dto.getDocumentNumber());
                    }
                }
                customersService.update(customers);
                if (dto.getStartDateAsString() != null) {
                    LocalDate startDate = LocalDate.parse(dto.getStartDateAsString());
                    if (startDate != null)
                        dto.setStartDate(startDate);
                }
                if (dto.getStartDate() != null) {
                    LocalDate endDate = LocalDate.parse(dto.getEndDateAsString());
                    if (endDate != null)
                        dto.setEndDate(endDate);
                }
                List<CustomerDocDetails> customerDocDetailsList=customerDocDetailsRepository.findAllByCustomer_idAndIsDeleteIsFalse(customers.getId());
                customerDocDetailsRepository.deleteAll(customerDocDetailsList);
                dto.setCreatedById(customers.getCreatedById());
                dto.setLastModifiedById(customers.getCreatedById());
                dto.setCreatedByName(customers.getCreatedByName());
                dto.setLastModifiedByName(customers.getCreatedByName());
                CustomerDocDetails customerDocDetails = customerDocDetailsMapper.dtoToDomain(dto, new CycleAvoidingMappingContext());
                customerDocDetails.setCustomer(customers);
                customerDocDetails.setDocStatus("pending");
                customerDocDetails.setNextStaff(dto.getStaffId());
                customerDocDetails = customerDocDetailsRepository.save(customerDocDetails);
                Resource resource = null;
                if (customerDocDetails != null && customerDocDetails.getMode().equalsIgnoreCase("Offline")) {
                    resource = fileSystemService.getLeadDoc(dto.getLeadId(), customerDocDetails.getUniquename());
                    if (resource != null) {
                        //PATH = "E:\\Users\\adopt\\custdocpath\\";
                        PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH).get(0).getValue();
                        String subFolderName = customers.getUsername().trim() + "/";
                        String path = PATH + subFolderName + "/" + customerDocDetails.getUniquename();
                        File sourceFile = resource.getFile();
                        FileUtils.copyFile(sourceFile, new File(path));

                    }
                }
                if (customerDocDetails.getNextTeamHierarchyMappingId() == null) {
                    if (customerDocDetails.getDocStatus() != null && !"".equals(customerDocDetails.getDocStatus())) {
                        if (customerDocDetails.getDocStatus().equalsIgnoreCase("pending")) {
                            if (clientServiceSrv.getValueByNameAndmvnoId(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,customers.getMvnoId()).equals("TRUE")) {
                                Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().domainToDTO(customerDocDetails, new CycleAvoidingMappingContext()));
                                int staffId = 0;
                                if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                    staffId = Integer.parseInt(map.get("staffId"));
                                    StaffUser assignedStaff = staffUserService.get(staffId,customers.getMvnoId());
                                    customerDocDetails.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                    customerDocDetails.setNextStaff(staffId);
                                    hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), null,assignedStaff.getId().longValue());
                                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, customerDocDetails.getCustomer().getId(), customerDocDetails.getCustomer().getUsername(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                } else {
                                    StaffUser currentStaff = staffUserService.get(getLoggedInUserId(),customers.getMvnoId());
                                    customerDocDetails.setNextTeamHierarchyMappingId(null);
                                    customerDocDetails.setNextStaff(currentStaff.getId());
                                    workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, customerDocDetails.getCustomer().getId(), customerDocDetails.getCustomer().getUsername(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                }
                            } else {
                                Map<String, Object> map = hierarchyService.getTeamForNextApprove(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, false, true, getMapper().domainToDTO(customerDocDetails, new CycleAvoidingMappingContext()));
                                if (map.containsKey("assignableStaff")) {
                                    workflowAssignStaffMappingService.assignWorkflowToStaff(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, customerDocDetails, map);
                                } else {
                                    customerDocDetails.setNextTeamHierarchyMappingId(null);
                                    customerDocDetails.setNextStaff(null);
                                    customerDocDetails.setDocStatus("verified");
                                }
                            }
                        }
                        customerDocDetailsRepository.save(customerDocDetails);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GenericDataDTO updateUploadCustomerDocAssignment(Long docId, Boolean isApproveRequest, String remarks) {
        try {
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            if (docId != null && remarks != null) {
                CustomerDocDetails customerDocDetails = getRepository().getOne(docId);
                CustomerDocDetailsDTO customerDocDetailsDTO = customerDocDetailsMapper.domainToDTO(customerDocDetails, new CycleAvoidingMappingContext());
                StaffUser loggedInUser = staffUserService.get(getLoggedInUserId(),customerDocDetails.getCustomer().getMvnoId());
                StringBuilder approvedByName = new StringBuilder();
                if (!loggedInUser.getUsername().equalsIgnoreCase("admin")) {
                    if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,customerDocDetails.getCustomer().getMvnoId()).equals("TRUE")) {
                        Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, false, customerDocDetailsDTO);
                        if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                            customerDocDetailsDTO.setNextTeamHierarchyMappingId(null);
                            customerDocDetailsDTO.setNextStaff(null);
                            if (isApproveRequest) {
                                customerDocDetailsDTO.setDocStatus(SubscriberConstants.VERIFIED);
                                Customers customers = customerDocDetails.getCustomer();
                                List<Integer> cprIds = custPlanMappingRepository.findAllByCustomerId(customers.getId()).stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
                                changeStatusDisableToActive(cprIds);  /**Change status disable to active**/
                            } else {
                                customerDocDetailsDTO.setDocStatus(SubscriberConstants.REJECT);
                            }
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), loggedInUser.getId(), loggedInUser.getUsername(),
                                    isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remarks + "\n" + (isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED) + " by :- " + loggedInUser.getUsername());
                        } else {
                            customerDocDetailsDTO.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                            customerDocDetailsDTO.setNextStaff(Integer.valueOf(map.get("staffId")));
                            StaffUser assigned = staffUserService.get(Integer.valueOf(map.get("staffId"),getMvnoIdFromCurrentStaff(customerDocDetails.getCustomer().getId())),customerDocDetails.getCustomer().getMvnoId());
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), loggedInUser.getId(), loggedInUser.getUsername(),
                                    isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remarks + "\n" + (isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED) + " by :- " + loggedInUser.getUsername());
                            workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, map.get("eventName"), Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), assigned.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now().plusMinutes(1), " Assigned to :- " + assigned.getUsername());
                        }
                    } else {
                         if(customerDocDetailsDTO !=null && isApproveRequest.equals(false) && customerDocDetailsDTO.getNextTeamHierarchyMappingId() == null ){
                             hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, customerDocDetails.getDocId().intValue());
                             customerDocDetailsDTO.setDocStatus(SubscriberConstants.REJECT);
                             customerDocDetailsDTO.setNextStaff(null);
                             workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), loggedInUser.getId(), loggedInUser.getUsername(),
                                      CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remarks + "\n" +  (CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED) + " by :- " + loggedInUser.getUsername());

                         }else{


                        Map<String, Object> map = hierarchyService.getTeamForNextApprove(customerDocDetails.getCustomer().getMvnoId(), customerDocDetails.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, CommonConstants.HIERARCHY_TYPE, isApproveRequest, customerDocDetails.getNextTeamHierarchyMappingId() == null, customerDocDetailsDTO);
                        if (map.containsKey("assignableStaff")) {
                            genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                        } else {
                            customerDocDetailsDTO.setNextTeamHierarchyMappingId(null);
                            customerDocDetailsDTO.setNextStaff(null);
                            if (isApproveRequest) {
                                customerDocDetailsDTO.setDocStatus(SubscriberConstants.VERIFIED);
                                Customers customers = customerDocDetails.getCustomer();
                                List<Integer> cprIds = custPlanMappingRepository.findAllByCustomerId(customers.getId()).stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
                                changeStatusDisableToActive(cprIds);  /**Change status disable to active**/
                            } else {
                                customerDocDetailsDTO.setDocStatus(SubscriberConstants.REJECT);
                            }
                        }
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.DOCUMENT_VERIFICATION, Math.toIntExact(customerDocDetails.getDocId()), customerDocDetails.getCustomer().getUsername(), loggedInUser.getId(), loggedInUser.getUsername(),
                                isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + remarks + "\n" + (isApproveRequest ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED) + " by :- " + loggedInUser.getUsername());

                    }
                }
                } else {
                    approvedByName.append("Administrator");
                    if (isApproveRequest) {
                        customerDocDetailsDTO.setDocStatus(SubscriberConstants.VERIFIED);
                        Customers customers = customerDocDetails.getCustomer();
                        List<Integer> cprIds = custPlanMappingRepository.findAllByCustomerId(customers.getId()).stream().map(custPlanMappping -> custPlanMappping.getId()).collect(Collectors.toList());
                        changeStatusDisableToActive(cprIds);  /**Change status disable to active**/

                    } else {
                        customerDocDetailsDTO.setDocStatus(SubscriberConstants.REJECT);
                    }
                    customerDocDetailsDTO.setNextTeamHierarchyMappingId(null);
                    customerDocDetailsDTO.setNextStaff(null);
                    //  customerDocDetailsDTO.setStatus(SubscriberConstants.ACTIVE);
                }
                CustomerDocDetailsDTO customerDocDetailsDTOSaved = saveEntity(customerDocDetailsDTO);
                genericDataDTO.setData(customerDocDetailsDTOSaved);

                /* message send for customer document verification*/
                sendCustDocumentVerificationMessage(customerDocDetailsDTO.getCustomer().getUsername(), customerDocDetailsDTO.getCustomer().getMobile(), customerDocDetailsDTO.getCustomer().getEmail(), customerDocDetailsDTO.getCustomer().getStatus(), customerDocDetailsDTO.getCustomer().getMvnoId(),customerDocDetailsDTO.getCustomer().getBuId(),loggedInUser.getId().longValue());
                genericDataDTO.setResponseCode(HttpStatus.OK.value());

            }
            return genericDataDTO;
        } catch (Exception e) {
            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
        }

    }


    public GenericDataDTO getCustomerDocApprovals(PaginationRequestDTO paginationRequestDTO) {
        PageRequest pageRequest = staffUserService.generatePageRequest(paginationRequestDTO.getPage(), paginationRequestDTO.getPageSize(), "createdate", CommonConstants.SORT_ORDER_DESC);
        QCustomerDocDetails customerDocDetails = QCustomerDocDetails.customerDocDetails;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = customerDocDetails.isNotNull().and(customerDocDetails.isDelete.eq(false)).and(customerDocDetails.nextStaff.eq(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(customerDocDetails.customer.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && staffUserService.getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(customerDocDetails.customer.mvnoId.eq(1).or(customerDocDetails.customer.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(customerDocDetails.customer.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<CustomerDocDetails> paginationList = customerDocDetailsRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> {
            try {
                data.setCustomer(data.getCustomer());
                data.setUniquename(data.getCustomer().getUsername());
                data.setDocType(data.getDocType());
                data.setDocSubType(data.getDocSubType());
                data.setFilename(data.getFilename());
                data.setMode(data.getMode());
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

    /* send message for customer document verification*/
    public void sendCustDocumentVerificationMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId, Long buId,Long staffId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_DOCUMENT_VERIFICATION_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustDocumentVerificationMsg custDocumentVerificationMsg = new CustDocumentVerificationMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_DOCUMENT_VERIFICATION_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId);
                    Gson gson = new Gson();
                    gson.toJson(custDocumentVerificationMsg);
                    kafkaMessageSender.send(new KafkaMessageData(custDocumentVerificationMsg,CustDocumentVerificationMsg.class.getSimpleName()));
//                    messageSender.send(custDocumentVerificationMsg, RabbitMqConstants.QUEUE_CUSTOMER_DOCUMENT_VERIFICATION_NOTIFICATION);
                }
            } else {
                System.out.println("Message of Customer Document Verification is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void changeStatusDisableToActive(List<Integer> cprIds){
        List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findAllByIdIn(cprIds);
        if(!custPlanMapppingList.isEmpty()){
            custPlanMapppingList =custPlanMapppingList.stream().filter(custPlanMappping -> custPlanMappping.getCustPlanStatus().equalsIgnoreCase((StatusConstants.CUSTOMER_SERVICE_STATUS.DISABLE))).collect(Collectors.toList());
            if(!custPlanMapppingList.isEmpty()){
                List<Integer> serviceMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIdsAndActiveStatus(cprIds,StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE);
                if (!CollectionUtils.isEmpty(serviceMappingIds)) {
                    String remark = "Enable status with document upload";
                    custPlanMappingService.changeStatusOfCustServices(serviceMappingIds, StatusConstants.CUSTOMER_SERVICE_STATUS.ACTIVE, remark, false);
                }

                List<Integer> serviceHoldMappingIds = custPlanMappingRepository.getAllByCustServiceMappingIdInCprIdsAndHoldStatus(cprIds,StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                if (!CollectionUtils.isEmpty(serviceHoldMappingIds)) {
                  List<CustomerServiceMapping> customerServiceMappingList=customerServiceMappingRepository.findAllByIdIn(serviceHoldMappingIds);
                   List<CustPlanMappping> custPlanMapppings= custPlanMappingRepository.findAllByCustServiceMappingIdIn(serviceHoldMappingIds);
                    custPlanMapppings.stream().forEach(i -> {
                        i.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                        i.setCustPlanStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                    });
                    customerServiceMappingList.stream().forEach(i->{i.setStatus(StatusConstants.CUSTOMER_SERVICE_STATUS.HOLD);
                    });
                    custPlanMappingRepository.saveAll(custPlanMapppings);
                    customerServiceMappingRepository.saveAll(customerServiceMappingList);
                }
            }
        }
    }
    public CustomerDocDetailsDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        CustomerDocDetailsDTO dto = getEntityById(id,mvnoId);
        if (dto.getCustomer().getMvnoId() != null) {
            if (dto == null || !(getMvnoIdFromCurrentStaff(dto.getCustId()) == 1 || getMvnoIdFromCurrentStaff(dto.getCustId()).intValue() == dto.getCustomer().getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return dto;
        }
        return dto;
    }

    public CustomerDocDetailsDTO getEntityById(Long id,Integer mvnoId) throws Exception {
        try {
            CustomerDocDetails domain = (null == customerDocDetailsRepository.findById(id)) ? null : customerDocDetailsRepository.findById(id).get();
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException("[CustomerDocDetails]" + "--" + "Data not found for id " + id);
            }
            CustomerDocDetailsDTO dto = customerDocDetailsMapper.domainToDTO(customerDocDetailsRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found")), new CycleAvoidingMappingContext());
            if ((dto.getCustomer().getMvnoId()) != null) {
                if (dto != null && (getMvnoIdFromCurrentStaff(dto.getCustId()) == 1 || (dto.getCustomer().getMvnoId().intValue() == getMvnoIdFromCurrentStaff(dto.getCustId()).intValue() || dto.getCustomer().getMvnoId() == 1)))
                    return dto;
            }
            return dto;
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            //   ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public CustomerDocDetailsDTO updateEntity(CustomerDocDetailsDTO entity, MultipartFile[] file) throws Exception {
        entity.setMvnoId(getMvnoIdFromCurrentStaff(entity.getCustId()));
        CustomerDocDetails existingDoc = customerDocDetailsRepository.findById(entity.getDocId()).get();
        if(existingDoc != null){
            entity.setUniquename(existingDoc.getUniquename());
        }
        if(file.length > 0){
            PATH=clientServiceSrv.getClientSrvByName(ClientServiceConstant.CUST_DOC_PATH)
                    .stream()
                    .filter(i -> i.getMvnoId().equals(getMvnoIdFromCurrentStaff(entity.getCustId())))
                    .findFirst()
                    .map(ClientServicePojo::getValue)
                    .orElse(null);
            Customers customers = customersRepository.findById(entity.getCustId()).get();
            String subFolderName = File.separator + customers.getUsername().trim() + File.separator;
            String path = PATH + subFolderName;
            MultipartFile file1 = fileUtility.getFileFromArray(entity.getFilename(), file);
            if (null != file1) {
                entity.setUniquename(fileUtility.saveFileToServer(file1, path));
            }
        }
        CustomerDocDetails entityDomain = customerDocDetailsMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());

        //       ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "updating Entity. Data[" + entityDomain.toString() + "]");
        try {
            if(entity == null || !(getMvnoIdFromCurrentStaff(entity.getCustId()) == 1 || getMvnoIdFromCurrentStaff(entity.getCustId()).intValue() == entity.getMvnoId().intValue()))
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            return customerDocDetailsMapper.domainToDTO(customerDocDetailsRepository.save(entityDomain), new CycleAvoidingMappingContext());
        } catch (Exception ex) {
            // ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while saving Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

}
