package com.adopt.apigw.modules.TicketFollowUp.Service;
//
//import com.adopt.apigw.core.dto.GenericDataDTO;
//import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
//import com.adopt.apigw.core.mapper.IBaseMapper;
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUpAudit;
//import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUpRemark;
//import com.adopt.apigw.modules.TicketFollowUp.Mapper.TicketFollowUpRemarkMapper;
//import com.adopt.apigw.modules.TicketFollowUp.Model.TicketFollowUpRemarkDTO;
//import com.adopt.apigw.modules.TicketFollowUp.Repository.TicketFollowUpAuditRepository;
//import com.adopt.apigw.modules.TicketFollowUp.Repository.TicketFollowUpRemarkRepository;
//import com.adopt.apigw.repository.common.StaffUserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//
//import javax.transaction.Transactional;
//import java.util.List;
//import java.util.Optional;
//
//
//@Service
public class TicketFollowUpRemarkService {
//
//
//    public TicketFollowUpRemarkService(JpaRepository<TicketFollowUpRemark, Long> repository, IBaseMapper<TicketFollowUpRemarkDTO, TicketFollowUpRemark> mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketFollowUpRemarkService]";
//    }
//
//
//
//
//    @Autowired
//    private TicketFollowUpRemarkRepository ticketFollowUpRemarkRepository;
//
//    @Autowired
//    private TicketFollowUpRemarkMapper ticketFollowUpRemarkMapper;
//
//    @Autowired
//    private StaffUserRepository staffUserRepository;
//
//    @Autowired
//    private TicketFollowUpAuditRepository ticketFollowUpAuditRepository;
//
//    @Transactional
//    public GenericDataDTO save(TicketFollowUpRemarkDTO ticketFollowUpRemarkDTO, Integer staffUserId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            TicketFollowUpRemark ticketFollowUpRemark = this.ticketFollowUpRemarkMapper.dtoToDomain(ticketFollowUpRemarkDTO,
//                    new CycleAvoidingMappingContext());
//            TicketFollowUpRemark savedCafFollowUpRemark = this.ticketFollowUpRemarkRepository.save(ticketFollowUpRemark);
//            // add followup remark audit
//            Optional<StaffUser> optionalStaffUser = this.staffUserRepository.findById(staffUserId);
//            if (optionalStaffUser.isPresent()) {
//                StaffUser staffUser = optionalStaffUser.get();
//                String name = staffUser.getFirstname() + " added follow up remark in "
//                        + savedCafFollowUpRemark.getTicketFollowUp().getFollowUpName() + ".Remark: "
//                        + savedCafFollowUpRemark.getRemark() + ".";
//                addCafFollowUpRemarkAudit(Math.toIntExact(savedCafFollowUpRemark.getTicketFollowUp().getTicket().getCaseId()), staffUser,
//                        name, "Followup Remark Added");
//            }
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("CafFollowUp Remark has been created successfully");
//            genericDataDTO.setData(this.ticketFollowUpRemarkMapper.domainToDTO(savedCafFollowUpRemark,
//                    new CycleAvoidingMappingContext()));
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
//
//    public void addCafFollowUpRemarkAudit(Integer ticketId, StaffUser staffUser, String name, String auditName) {
//        TicketFollowUpAudit ticketFollowUpAudit = new TicketFollowUpAudit();
//        ticketFollowUpAudit.setName(name);
//        ticketFollowUpAudit.setAuditName(auditName);
//        ticketFollowUpAudit.setStaffName(staffUser.getFirstname() + " " + staffUser.getLastname());
//        ticketFollowUpAudit.setTicketId(ticketId);
//        this.ticketFollowUpAuditRepository.save(ticketFollowUpAudit);
//    }
//
//    public GenericDataDTO getAllByTicketFollowUpId(Long cafFollowUpId) {
//        GenericDataDTO genericDataDTO = new GenericDataDTO();
//        try {
//            List<TicketFollowUpRemark> ticketFollowUpRemarkList = this.ticketFollowUpRemarkRepository.findByTicketFollowUpId(cafFollowUpId);
//            genericDataDTO.setDataList(this.ticketFollowUpRemarkMapper.domainToDTO(ticketFollowUpRemarkList, new CycleAvoidingMappingContext()));
//            genericDataDTO.setResponseCode(HttpStatus.OK.value());
//            genericDataDTO.setResponseMessage("Fetching All CafFollowUpRemark With cafFollowUpId " + cafFollowUpId);
//        } catch (Exception e) {
//            ApplicationLogger.logger.error("[CafFollowUpRemarkService]" + e.getMessage(), e);
//            e.printStackTrace();
//            return genericDataDTO;
//        }
//        return genericDataDTO;
//    }
}
