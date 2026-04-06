package com.adopt.apigw.modules.TicketFollowupDetail.service;
//
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import com.adopt.apigw.modules.Teams.repository.TeamUserMappingsRepocitory;
//import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
//import com.adopt.apigw.modules.Template.domain.TemplateNotification;
//import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
//import com.adopt.apigw.rabbitMq.MessageSender;
//import com.adopt.apigw.rabbitMq.RabbitMqConstants;
//import com.adopt.apigw.rabbitMq.message.SendFollowUpRemarkMsg;
//import com.adopt.apigw.rabbitMq.message.TicketPickMessageToTeam;
//import com.adopt.apigw.repository.common.StaffUserRepository;
//import com.google.gson.Gson;
//import org.apache.poi.ss.usermodel.Sheet;
//import org.apache.poi.ss.usermodel.Workbook;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.adopt.apigw.core.service.ExBaseAbstractService;
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.model.common.StaffUser;
//import com.adopt.apigw.modules.TicketFollowupDetail.domain.TicketFollowupDetail;
//import com.adopt.apigw.modules.TicketFollowupDetail.mapper.TicketFollowupDetailMapper;
//import com.adopt.apigw.modules.TicketFollowupDetail.model.TicketFollowupDetailDTO;
//import com.adopt.apigw.modules.TicketFollowupDetail.repository.TicketFollowupDetailRepository;
//import com.adopt.apigw.modules.tickets.model.CaseDTO;
//import com.adopt.apigw.modules.tickets.service.CaseService;
//import com.adopt.apigw.service.common.CustomersService;
//import com.adopt.apigw.service.common.StaffUserService;
//import com.itextpdf.text.Document;
//
//@Service
public class TicketFollowupDetailService  {
//
//    public TicketFollowupDetailService(TicketFollowupDetailRepository repository, TicketFollowupDetailMapper mapper) {
//        super(repository, mapper);
//    }
//
//    @Override
//    public String getModuleNameForLog() {
//        return "[TicketFollowupDetailService]";
//    }
//
//    @Autowired
//    private TicketFollowupDetailRepository ticketFollowupDetailRepository;
//
//    @Autowired
//    private StaffUserService staffUserService;
//
//    @Autowired
//    private CustomersService customersService;
//
//    @Autowired
//    private CaseService caseService;
//
//    @Autowired
//    private NotificationTemplateRepository templateRepository;
//
//    @Autowired
//    private MessageSender messageSender;
//
//    @Autowired
//    StaffUserRepository staffUserRepository;
//    @Autowired
//    TeamsRepository teamsRepository;
//    @Autowired
//    TeamUserMappingsRepocitory teamUserMappingsRepocitory;
//
//    @Override
//    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
//        Sheet sheet = workbook.createSheet("TicketFollowupDetail");
//        createExcel(workbook, sheet, TicketFollowupDetailDTO.class, getFields());
//    }
//
//    private Field[] getFields() throws NoSuchFieldException {
//        return new Field[]{
//                TicketFollowupDetailDTO.class.getDeclaredField("id"),
//                TicketFollowupDetailDTO.class.getDeclaredField("remarks"),
//        };
//    }
//
//    @Override
//    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
//        createPDF(doc, TicketFollowupDetailDTO.class, getFields());
//    }
//
//    public TicketFollowupDetail getById(Long id) {
//    	return ticketFollowupDetailRepository.findById(id).get();
//    }
//
//	public List<TicketFollowupDetail> getAllByCaseId(Long caseId) throws Exception {
//		List<TicketFollowupDetail> list = ticketFollowupDetailRepository.getAllByCaseId(caseId);
//		if(list != null && list.size() > 0) {
//			for(TicketFollowupDetail ticketFollowupDetail : list) {
//				if (ticketFollowupDetail.getCaseId() != null) {
//                    CaseDTO caseDb = caseService.getEntityById(ticketFollowupDetail.getCaseId());
//                    if(caseDb != null) {
//                    	ticketFollowupDetail.setCaseTitle(caseDb.getCaseTitle());
//                    	ticketFollowupDetail.setCaseId(caseDb.getCaseId());
//                	}
//                }
//                if (ticketFollowupDetail.getCustId() != null) {
//                	Customers customers = customersService.get(ticketFollowupDetail.getCustId());
//                	if(customers != null) {
//                		ticketFollowupDetail.setCustomersName(customers.getFullName());
//                		ticketFollowupDetail.setCustId(customers.getId());
//                	}
//                }
//                if (ticketFollowupDetail.getStaffId() != null) {
//                	StaffUser staffUser = staffUserService.get(ticketFollowupDetail.getStaffId());
//                	if(staffUser != null) {
//                		ticketFollowupDetail.setStaffUserName(staffUser.getFullName());
//                		ticketFollowupDetail.setStaffId(staffUser.getId());
//                	}
//                }
//			}
//		}
//		return list;
//	}
//
//
//    public void sendFollowUpRemarkMsg(String parentStaffPersonName, String ticketNumber, String remark, String staffPersonName, String parentMobileNumber, String parentEmailId, Integer mvnoId, String teamStaffName) {
//        try {
//            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.FOLLOWUP_REMARK_MSG);
//            if (optionalTemplate.isPresent()) {
//                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
//                    Long buId = null;
//                    if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
//                        buId =  getBUIdsFromCurrentStaff().get(0);
//                    }
//                    SendFollowUpRemarkMsg sendFollowUpRemarkMsg = new SendFollowUpRemarkMsg(parentMobileNumber,parentEmailId,RabbitMqConstants.SEND_FOLLOWUP_REMARK_MSG,optionalTemplate.get(),parentStaffPersonName,staffPersonName,remark,mvnoId,ticketNumber,teamStaffName,buId);
//                    Gson gson = new Gson();
//                    gson.toJson(sendFollowUpRemarkMsg);
//                    messageSender.send(sendFollowUpRemarkMsg, RabbitMqConstants.QUEUE_SEND_FOLLOWUP_REMARK_MSG);
//                }
//            } else {
////                 log.error("Message of otp generated is not sent because '" + OTP_GENERATED + "' template is not present.");
//                System.out.println("TAT Template not available.");
//            }
//
//
//        } catch (Throwable e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }
//
//    @Override
//    public TicketFollowupDetailDTO saveEntity(TicketFollowupDetailDTO entity) throws Exception {
//
//        CaseDTO dbObj = caseService.getEntityForUpdateAndDelete(entity.getCaseId());
//        StaffUser staffuser = staffUserRepository.findById(dbObj.getCurrentAssigneeId()).get();
//
//        if (dbObj.getCurrentAssigneeId() != null) {
//            // if (getLoggedInUser().getStaffId().equals(dbObj.getCurrentAssigneeId())) {
//            if (staffuser != null) {
//                //if (staffuser.getStaffUserparent() != null) {
//                //Message is from staff then sent it to parent
//                sendFollowUpRemarkMsg(staffuser.getStaffUserparent().getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffuser.getFirstname(), staffuser.getStaffUserparent().getPhone(), staffuser.getStaffUserparent().getEmail(), staffuser.getStaffUserparent().getMvnoId(), getLoggedInUser().getFullName());
//                // }
//
//                // }
//
////            } else if (getLoggedInUser().getStaffId().equals(staffuser.getStaffUserparent().getId())) {
////                //Message is from parent then sent it to staff
////                sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), staffuser.getStaffUserparent().getFirstname(), staffuser.getPhone(), staffuser.getEmail(), staffuser.getMvnoId());
////            }
////            else{
////                StaffUser otherStaff =staffUserRepository.findById(getLoggedInUserId()).get();
////                if (otherStaff != null){
////                    sendFollowUpRemarkMsg(staffuser.getFirstname(), dbObj.getCaseNumber(), entity.getRemark(), otherStaff.getFirstname(), staffuser.getPhone(), staffuser.getEmail(), staffuser.getMvnoId());
////                }
////            }
//            }
//        }
//
//        return super.saveEntity(entity);
//    }
//
//    public List<String> getTeamListByStaffId(Long staffId) {
//        List<String> teamnameList=new ArrayList<>();
//        List<Long>teamids=teamUserMappingsRepocitory.teamIds(staffId);
//        if(teamids.size()>0){
//            for (int i=0;i<teamids.size();i++) {
//                teamnameList.add(teamsRepository.findById(teamids.get(i)).get().getName());
//            }
//        }
//        return teamnameList;
//    }
}
