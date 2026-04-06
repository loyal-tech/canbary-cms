package com.adopt.apigw.modules.mvnoDocDetails.service.impl;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.fileUtillity.FileUtility;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.mapper.MvnoMapper;
import com.adopt.apigw.modules.Mvno.model.MvnoDTO;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.Mvno.service.MvnoService;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.customerDocDetails.model.CustomerDocDetailsDTO;
import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.mapper.MvnoDocDetailsMapper;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import com.adopt.apigw.modules.mvnoDocDetails.repository.MvnoDocDetailsRepository;
import com.adopt.apigw.modules.mvnoDocDetails.service.DocDetailsService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.spring.LoggedInUserService;
import com.adopt.apigw.utils.CommonConstants;
import com.itextpdf.text.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocDetailsServiceImpl implements DocDetailsService {


    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    private LoggedInUserService loggedInUserService;

    @Autowired
    private MvnoService mvnoService;
    @Autowired
    private MvnoMapper mvnoMapper;

    @Autowired
    private FileUtility fileUtility;

    @Autowired
    private MvnoDocDetailsRepository docDetailsRepository;

    @Autowired
    private MvnoDocDetailsMapper docDetailsMapper;

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private StaffUserService staffUserService;

    @Autowired
    private WorkflowAuditService workflowAuditService;
    @Autowired
    private MvnoRepository mvnoRepository;

    @Override
    public List<MvnoDocDetailsDTO> uploadDocument(List<MvnoDocDetailsDTO> mvnoDocDetailsList, Long mvnoId, MultipartFile[] files) {

        String SUBMODULE = "[DocDetailsServiceImpl]" + " [uploadDocument()] ";
        // PATH="D:/";
        String PATH = clientServiceSrv.getClientSrvByName(ClientServiceConstant.MVNO_DOC_PATH).get(0).getValue();
        List<MvnoDocDetailsDTO> finalResponseList = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
//        Integer loggedInStaffMvnoId = loggedInUserService.getMvnoIdFromCurrentStaff(null);
        try {
            Mvno mvnodomain = mvnoRepository.findById(mvnoId).get();
            MvnoDTO mvno = mvnoMapper.domainToDTO(mvnodomain , new CycleAvoidingMappingContext());
            for (MvnoDocDetailsDTO mvnoDoc : mvnoDocDetailsList) {
                mvnoDoc.setMvnoId(mvnoId.intValue());
                mvnoDoc.setDocStatus("pending");
                if (null != mvnoDoc.getMvnoId()) {
                    if (mvnoDoc.getStartDate() != null || mvnoDoc.getEndDate() != null) {
                        if (mvnoDoc.getStartDate().isAfter(mvnoDoc.getEndDate())) {
                            throw new CustomValidationException(HttpStatus.NOT_ACCEPTABLE.value(), "End date must be greater than start date", null);
                        }
                    }
                    if (null != mvno) {
                        String subFolderName = mvno.getUsername().trim() + "_" + mvno.getId() + File.separator;
                        String path = PATH + subFolderName;
                        ApplicationLogger.logger.debug(SUBMODULE + ":File Path:" + path);
                        if (null == mvnoDoc.getDocId()) {
                            if (null != mvnoDoc.getFilename()) {
                                MultipartFile file1 = fileUtility.getFileFromArray(mvnoDoc.getFilename(), files);
                                if (null != file1) {
                                    mvnoDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                    mvnoDoc.setDocumentNumber(mvnoDoc.getDocumentNumber());
                                    MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDoc, new CycleAvoidingMappingContext());
                                    mvnoDocDetails = docDetailsRepository.save(mvnoDocDetails);
                                    mvnoDoc.setDocId(mvnoDocDetails.getDocId());
                                    finalResponseList.add(mvnoDoc);
                                }
                            }
                        } else {
                            MvnoDocDetailsDTO mvnoDocDTO = getEntityById(mvnoDoc.getDocId());
                            if (null != mvnoDocDTO) {
                                if (null != mvnoDocDTO.getFilename()
                                        && null != mvnoDoc.getFilename()
                                        && !mvnoDocDTO.getFilename().equalsIgnoreCase(mvnoDoc.getFilename())) {
                                    fileUtility.removeFileAtServer(mvnoDocDTO.getUniquename(), path);
                                }
                                MultipartFile file1 = fileUtility.getFileFromArray(mvnoDoc.getFilename(), files);
                                if (null != file1) {
                                    mvnoDoc.setUniquename(fileUtility.saveFileToServer(file1, path));
                                }
                                mvnoDoc.setDocumentNumber(mvnoDoc.getDocumentNumber());
                                mvnoDoc = updateEntity(mvnoDoc);
                                finalResponseList.add(mvnoDoc);
                            } else {
                                throw new DataNotFoundException("Mvno Doc Details Not Found!");
                            }
                        }
                        if (mvnoDoc.getNextTeamHierarchyMappingId() == null) {
                            if (mvnoDoc.getDocStatus() != null && !"".equals(mvnoDoc.getDocStatus())) {
                                if (mvnoDoc.getDocStatus().equalsIgnoreCase("pending")) {

                                        StaffUser currentStaff = staffUserService.get(loggedInUserService.getLoggedInUser().getStaffId(),mvnoId.intValue());
                                        mvnoDoc.setNextTeamHierarchyMappingId(null);
                                        mvnoDoc.setNextStaff(currentStaff.getId());
                                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.MVNO_DOCUMENT_VERIFICATION, mvnoId.intValue(), mvno.getUsername(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());

                                }
                                MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDoc, new CycleAvoidingMappingContext());
                                mvnoDocDetails.setMvno(mvnoService.getMapper().dtoToDomain(mvno, new CycleAvoidingMappingContext()));
                                docDetailsRepository.save(mvnoDocDetails);
                            }
                        }
                    } else
                        throw new DataNotFoundException("Mvno Not Found!");
                } else
                    throw new RuntimeException("Please Provide Mvno Details");
            }
            return finalResponseList;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        
    }

    @Override
    public MvnoDocDetailsDTO updateEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO){
        mvnoDocDetailsDTO.setStartDate(LocalDate.parse(mvnoDocDetailsDTO.getStartDateAsString()));
        mvnoDocDetailsDTO.setEndDate(LocalDate.parse(mvnoDocDetailsDTO.getEndDateAsString()));
        MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDocDetailsDTO, new CycleAvoidingMappingContext());
        Optional<Mvno> mvno=mvnoRepository.findById(mvnoDocDetailsDTO.getMvnoId().longValue());
        mvnoDocDetails.setMvno(mvno.get());
        docDetailsRepository.save(mvnoDocDetails);
        return docDetailsMapper.domainToDTO(mvnoDocDetails, new CycleAvoidingMappingContext());
    }

    @Override
    public MvnoDocDetailsDTO saveEntity(MvnoDocDetailsDTO mvnoDocDetailsDTO) {
        mvnoDocDetailsDTO.setStartDate(LocalDate.parse(mvnoDocDetailsDTO.getStartDateAsString()));
        mvnoDocDetailsDTO.setEndDate(LocalDate.parse(mvnoDocDetailsDTO.getEndDateAsString()));
        MvnoDocDetails mvnoDocDetails = docDetailsMapper.dtoToDomain(mvnoDocDetailsDTO, new CycleAvoidingMappingContext());
        Optional<Mvno> mvno=mvnoRepository.findById(mvnoDocDetailsDTO.getMvnoId().longValue());
        mvnoDocDetails.setMvno(mvno.get());
        docDetailsRepository.save(mvnoDocDetails);
        return docDetailsMapper.domainToDTO(mvnoDocDetails, new CycleAvoidingMappingContext());
    }

    @Override
    public MvnoDocDetailsDTO getEntityById(Long docId){
        Optional<MvnoDocDetails> mvnoDocDetails = docDetailsRepository.findById(docId);
        if(mvnoDocDetails.isPresent()) {
            return docDetailsMapper.domainToDTO(mvnoDocDetails.get(), new CycleAvoidingMappingContext());
        }
        return null;
    }

    @Override
    public List<MvnoDocDetailsDTO> findDocsByEntityId(Long mvnoId) {
        try {
            List<MvnoDocDetailsDTO> mvnoDocDetailsDTOS = docDetailsRepository.findAllByMvnoIdAndIsDeleteFalse(mvnoId);
            if(!CollectionUtils.isEmpty(mvnoDocDetailsDTOS)) {
                return mvnoDocDetailsDTOS;//docDetailsMapper.domainToDTO(mvnoDocDetailsDTOS, new CycleAvoidingMappingContext());
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
        return null;
    }

    @Override
    public boolean isDocPending(Long mvnoId) {
        return false;
    }

    @Override
    public String deleteDocument(List<Long> docIdList, Long mvnoId) {
        return null;
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) {

    }

    @Override
    public List<MvnoDocDetails> getMvnoDocumentforDunning(Integer dateDiff , Integer mvnoId){
        List<MvnoDocDetails> getMvnoDocforDunningList = docDetailsRepository.getMvnoDocumentForDunning(dateDiff );
        LocalDate currentDate = java.time.LocalDate.now();
        List<MvnoDocDetails> filteredList = getMvnoDocforDunningList.stream()
                .filter(doc -> doc.getEndDate().equals(currentDate.plusDays(dateDiff)))
                .collect(Collectors.toList());
        return filteredList;
    }

    @Override
    public GenericDataDTO getDocApprovals(Long docId, Boolean isApproveRequest, String remarks) {
        try {
            if(loggedInUserService.getLoggedInUser().getMvnoId() != 1) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Only SuperAdmin staff can approve/reject Mvno Documents..!!",null);
            }
            Optional<MvnoDocDetails> mvnoDocDetails = docDetailsRepository.findById(docId);
            if(!mvnoDocDetails.isPresent()) {
                throw new CustomValidationException(HttpStatus.BAD_REQUEST.value(), "Given Document Not Available..!!",null);
            }
            GenericDataDTO genericDataDTO = new GenericDataDTO();
            String approvedByName = "Administrator";
            if (isApproveRequest) {
                mvnoDocDetails.get().setDocStatus(SubscriberConstants.VERIFIED);
                Mvno mvno = mvnoDocDetails.get().getMvno();
                mvno.setStatus(SubscriberConstants.ACTIVE);
                mvnoService.saveEntity(mvnoService.getMapper().domainToDTO(mvno, new CycleAvoidingMappingContext()));
                genericDataDTO.setResponseMessage("Mvno Doc Approved successfully..!");
            } else {
                mvnoDocDetails.get().setDocStatus(SubscriberConstants.REJECT);
                genericDataDTO.setResponseMessage("Mvno Doc Rejected successfully..!");
            }
            mvnoDocDetails.get().setNextTeamHierarchyMappingId(null);
            mvnoDocDetails.get().setNextStaff(null);
            docDetailsRepository.save(mvnoDocDetails.get());
            genericDataDTO.setData(docDetailsMapper.domainToDTO(mvnoDocDetails.get(), new CycleAvoidingMappingContext()));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            return genericDataDTO;
        } catch (CustomValidationException e) {
            throw new CustomValidationException(e.getErrCode(), e.getMessage(), e);
        }catch (Exception exception) {
            throw new RuntimeException(exception);
        }

    }
}
