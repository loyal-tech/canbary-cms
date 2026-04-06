package com.adopt.apigw.modules.TicketFollowUp.Service;
//
//
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.dto.PaginationRequestDTO;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.exception.CustomValidationException;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUp;
//import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUpAudit;
//import com.adopt.apigw.modules.TicketFollowUp.Mapper.TicketFollowUpMapper;
//import com.adopt.apigw.modules.TicketFollowUp.Model.TicketFollowUpDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Repository.TicketFollowUpAuditRepository;
//import com.adopt.apigw.modules.TicketFollowUp.Repository.TicketFollowUpRepository;
//import com.adopt.apigw.modules.tickets.domain.Case;
//import com.adopt.apigw.modules.tickets.repository.CaseRepository;
//import com.adopt.apigw.repository.common.StaffUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Optional;
//
//@Service
public class TicketFollowUpService  {
//
//
//    public TicketFollowUpService(JpaRepository<TicketFollowUp, Long> repository, TicketFollowUpMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketFollowUpService]";
//    }
//
//
//    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");
//
//
//    @Autowired
//    private TicketFollowUpRepository ticketFollowUpRepository;
//
//    @Autowired
//    private TicketFollowUpAuditRepository ticketFollowUpAuditRepository;
//
//    @Autowired
//    private StaffUserRepository staffUserRepository;
//
//    @Autowired
//    private CaseRepository caseRepository;
//
//
//    @Transactional
//    public GenericDataDTO save(TicketFollowUpDTO ticketFollowUpDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            TicketFollowUp ticketFollowUp = this.getMapper().dtoToDomain(ticketFollowUpDTO,
//                    new CycleAvoidingMappingContext());
//            ticketFollowUp.setStatus("Pending");
//            TicketFollowUp savedTicketFollowUp = this.ticketFollowUpRepository.save(ticketFollowUp);
//            // add schedule followup audit
//            String name = savedTicketFollowUp.getStaffUser().getFirstname() + " did " + savedTicketFollowUp.getFollowUpName()
//                    + " for ticket on " + dateFormat.format(savedTicketFollowUp.getCreatedOn());
//            addAudit(savedTicketFollowUp, savedTicketFollowUp.getStaffUser(), name,
//                    savedTicketFollowUp.getFollowUpName() + " has been Created");
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Ticket FollowUp has been schedule successfully");
//            genericDataDTO
//                    .setData(this.getMapper().domainToDTO(savedTicketFollowUp, new CycleAvoidingMappingContext()));
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[TicketFollowUpService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
//
//    @Transactional
//    public GenericDataDTO reSchedule(TicketFollowUpDTO ticketFollowUpDTO, Long followUpId, String remarks,
//                                     Integer staffUserId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            // close exsting followUp
//            TicketFollowUp exstingTicketFollowUp = this.ticketFollowUpRepository.findById(followUpId).get();
//            exstingTicketFollowUp.setStatus("Closed");
//            exstingTicketFollowUp.setRemarks(remarks);
//            this.ticketFollowUpRepository.save(exstingTicketFollowUp);
//            GenericDataDTO savedGenericDataDTO = save(ticketFollowUpDTO);
//            TicketFollowUpDTO savedTicketFollowUpDTO = (TicketFollowUpDTO) savedGenericDataDTO.getData();
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Ticket FollowUp has been reschedule successfully");
//            genericDataDTO.setData(savedTicketFollowUpDTO);
//            // add close and reSchedule followup audit
//            Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
//            if (optionalStaffUser.isPresent()) {
//                StaffUser staffUser = optionalStaffUser.get();
//                String closeAuditName = staffUser.getFirstname() + " closed  " + exstingTicketFollowUp.getFollowUpName()
//                        + " for ticket on " + dateFormat.format(LocalDateTime.now());
//                addAudit(exstingTicketFollowUp, staffUser, closeAuditName,
//                        exstingTicketFollowUp.getFollowUpName() + " has been Closed");
//                String reScheduleAuditName = staffUser.getFirstname() + " reschedule  "
//                        + exstingTicketFollowUp.getFollowUpName() + " for ticket on "
//                        + dateFormat.format(LocalDateTime.now());
//                addAudit(exstingTicketFollowUp, staffUser, reScheduleAuditName,
//                        exstingTicketFollowUp.getFollowUpName() + " has been Reschedule");
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[TicketFollowUpService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
//
//    @Transactional
//    public GenericDataDTO closefollowup(Long followUpId, String remarks, Integer staffUserId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            // close exsting followUp
//            TicketFollowUp exstingTicketFollowUp = this.ticketFollowUpRepository.findById(followUpId).get();
//            exstingTicketFollowUp.setStatus("Closed");
//            exstingTicketFollowUp.setRemarks(remarks);
//            this.ticketFollowUpRepository.save(exstingTicketFollowUp);
//            // add close followup audit
//            Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
//            if (optionalStaffUser.isPresent()) {
//                StaffUser staffUser = optionalStaffUser.get();
//                String closeAuditName = staffUser.getFirstname() + " closed  " + exstingTicketFollowUp.getFollowUpName()
//                        + " for ticket on " + dateFormat.format(LocalDateTime.now());
//                addAudit(exstingTicketFollowUp, staffUser, closeAuditName,
//                        exstingTicketFollowUp.getFollowUpName() + " has been Closed");
//            }
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Ticket FollowUp has been closed successfully");
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[TicketFollowUpService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
//
//    public GenericDataDTO getAllByCaseId(Integer caseId, PaginationRequestDTO paginationRequestDTO) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            PageRequest pageRequest = generatePageRequest(paginationRequestDTO.getPage(),
//                    paginationRequestDTO.getPageSize(), paginationRequestDTO.getSortBy(),
//                    paginationRequestDTO.getSortOrder());
//            Case aCase = caseRepository.findById(Long.valueOf(caseId)).orElse(null);
//            if (aCase != null) {
//                Page<TicketFollowUp> ticketFollowUpPage = this.ticketFollowUpRepository.findByTicket(aCase, pageRequest);
//                Page<TicketFollowUpDTO> ticketFollowUpDTOPage = ticketFollowUpPage
//                        .map(data -> this.getMapper().domainToDTO(data, new CycleAvoidingMappingContext()));
//                genericDataDTO.setDataList(ticketFollowUpDTOPage.getContent());
//                genericDataDTO.setCurrentPageNumber(paginationRequestDTO.getPage());
//                genericDataDTO.setTotalPages(ticketFollowUpDTOPage.getTotalPages());
//                genericDataDTO.setTotalRecords(ticketFollowUpDTOPage.getTotalElements());
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Fetching All ticket follow up with id " + caseId);
//            } else {
//                throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Ticket with ticket id" + caseId + " not found!", null);
//            }
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[TicketFollowUpService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
//
//    public void addAudit(TicketFollowUp ticketFollowUp, StaffUser staffUser, String name, String auditName) {
//        TicketFollowUpAudit ticketFollowUpAudit = new TicketFollowUpAudit();
//        ticketFollowUpAudit.setName(name);
//        ticketFollowUpAudit.setAuditName(auditName);
//        ticketFollowUpAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
//        ticketFollowUpAudit.setTicketId(Math.toIntExact(ticketFollowUp.getTicket().getCaseId()));
//        ticketFollowUpAuditRepository.save(ticketFollowUpAudit);
//    }
//
//    public TicketFollowUp get(Long id) {
//        return this.ticketFollowUpRepository.findById(id).orElse(null);
//    }
//
//    public Page<TicketFollowUp> findByIsMissedAndIsSendAndStatus(Pageable pageable) {
//        return this.ticketFollowUpRepository.findByIsMissedAndIsSendAndStatus(false, false, "Pending", pageable);
//    }
//
//    public GenericDataDTO generateNameOfTheFollowUp(Integer caseId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            String generatedNameOfTheTicketFollowUp = "";
//            Optional<Case> aCase = this.caseRepository.findById(Long.valueOf(caseId));
//            if (aCase.isPresent()) {
//                TicketFollowUp ticketFollowUp = this.ticketFollowUpRepository.findTopByOrderByIdDesc();
//                if (ticketFollowUp != null) {
//                    int num = ticketFollowUp.getId().intValue() + 1;
//                    generatedNameOfTheTicketFollowUp = aCase.get().getCaseNumber() + "_TicketFollowup" + num;
//                } else {
//                    generatedNameOfTheTicketFollowUp = aCase.get().getCaseNumber() + "_TicketFollowup" + 1;
//                }
//                genericDataDTO.setData(generatedNameOfTheTicketFollowUp);
//                genericDataDTO.setResponseCode(HttpStatus.OK.value());
//                genericDataDTO.setResponseMessage("Successfully");
//            } else {
//                genericDataDTO.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//                genericDataDTO.setResponseMessage("ticket not found for ID : " + caseId);
//            }
//            return genericDataDTO;
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[TicketFollowUpService]" + e.getMessage(), e);
//        }
//        return null;
//    }
//
//
}
