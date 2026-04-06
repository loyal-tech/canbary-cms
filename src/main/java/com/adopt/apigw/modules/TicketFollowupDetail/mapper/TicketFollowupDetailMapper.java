package com.adopt.apigw.modules.TicketFollowupDetail.mapper;
//
//import org.mapstruct.AfterMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.MappingTarget;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.adopt.apigw.core.mapper.IBaseMapper;
//import com.adopt.apigw.core.utillity.log.ApplicationLogger;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.Pincode.mapper.PincodeMapper;
//import com.adopt.apigw.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
//import com.adopt.apigw.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
//import com.adopt.apigw.modules.tickets.model.CaseDTO;
//import com.adopt.apigw.modules.tickets.service.CaseService;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.StaffUserService;
//
//@Mapper(uses = PincodeMapper.class)
public abstract class TicketFollowupDetailMapper  {
//
//	String MODULE = " [class] ";
//
//	@Autowired
//    StaffUserService staffUserService;
//
//    @Autowired
//    CustomersService customersService;
//
//    @Autowired
//    CaseService caseService;
//
//    @AfterMapping
//    void afterMapping(@MappingTarget TicketFollowupDetailDTO ticketFollowupDetailDTO, TicketFollowupDetail ticketFollowupDetail) {
//        try {
//            if (ticketFollowupDetail != null) {
//                if (ticketFollowupDetail.getCaseId() != null) {
//                    CaseDTO caseDb = caseService.getEntityById(ticketFollowupDetail.getCaseId());
//                    if(caseDb != null) {
//                    	ticketFollowupDetailDTO.setCaseTitle(caseDb.getCaseTitle());
//                        ticketFollowupDetailDTO.setCaseId(caseDb.getCaseId());
//                	}
//                }
//                if (ticketFollowupDetail.getCustId() != null) {
//                	Customers customers = customersService.get(ticketFollowupDetail.getCustId());
//                	if(customers != null) {
//                		ticketFollowupDetailDTO.setCustomersName(customers.getFullName());
//                    	ticketFollowupDetailDTO.setCustId(customers.getId());
//                	}
//                }
//                if (ticketFollowupDetail.getStaffId() != null) {
//                	StaffUser staffUser = staffUserService.get(ticketFollowupDetail.getStaffId());
//                	if(staffUser != null) {
//                    	ticketFollowupDetailDTO.setStaffUserName(staffUser.getFullName());
//                    	ticketFollowupDetailDTO.setStaffId(staffUser.getId());
//                	}
//                }
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
//            ex.printStackTrace();
//        }
//    }
}
