package com.adopt.apigw.modules.Notification.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.modules.CronJobs.CronjobConst;
import com.adopt.apigw.modules.TicketFollowUp.Domain.TicketFollowUp;
import com.adopt.apigw.modules.TicketFollowUp.Repository.TicketFollowUpRepository;
import com.adopt.apigw.modules.TicketFollowUp.Service.TicketFollowUpService;
import com.adopt.apigw.rabbitMq.message.TicketFollowUpMessage;
import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;
import com.adopt.apigw.modules.CafFollowUp.repository.CafFollowUpRepository;
import com.adopt.apigw.modules.CafFollowUp.service.CafFollowUpService;
import com.adopt.apigw.modules.Notification.domain.Notification;
import com.adopt.apigw.modules.Notification.mapper.NotificationMapper;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.repository.NotificationRepository;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CafFollowUpMessage;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.google.gson.Gson;
@Slf4j
@Service
public class NotificationService extends ExBaseAbstractService<NotificationDTO, Notification, Long> {

	public static final DateTimeFormatter FORMATOR = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");

	public static final String CAF_FOLLOW_UP_REMIDER_TIME_NAME = "cafFollowUpReminderTime";

	public static final String TICKET_FOLLOW_UP_REMIDER_TIME_NAME = "ticketFollowUpReminderTime";

	@Autowired
	private NotificationRepository repository;

	@Autowired
	private NotificationMapper mapper;

	@Autowired
	private CafFollowUpService cafFollowUpService;

	@Autowired
	private CafFollowUpRepository cafFollowUpRepository;

	@Autowired
	private NotificationTemplateRepository notificationTemplateRepository;

	@Autowired
	private ClientServiceSrv clientServiceSrv;

	@Autowired
	private MessageSender messageSender;

	@Autowired
	private KafkaMessageSender kafkaMessageSender;

	@Autowired
	private SchedulerLockService schedulerLockService;

	@Autowired
	private SchedulerAuditService schedulerAuditService;


//	@Autowired
//	private TicketFollowUpService ticketFollowUpService;

//	@Autowired
//	private TicketFollowUpRepository ticketFollowUpRepository;
	
	public NotificationService(NotificationRepository repository, NotificationMapper mapper) {
		super(repository, mapper);
		this.repository = repository;
		sortColMap.put("id", "notification_id");
	}

	public List<NotificationDTO> findNotificationByCategory(String category, String status, Boolean email_enabled,
			Boolean sms_enabled) {
		return this.repository.findAllByCategoryAndStatus(category, status).stream()
				.filter(data -> data.getEmail_enabled() == email_enabled && data.getSms_enabled() == sms_enabled)
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
	}

	public List<NotificationDTO> findNotificationByCategory(String category, String status) {
		return this.repository.findAllByCategoryAndStatus(category, status).stream()
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
	}

	public NotificationDTO findByName(String name) {
		return this.repository.findAllByName(name).stream()
				.map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).findAny().orElse(null);
	}

	public GenericDataDTO getByName(String name, PageRequest pageRequest) {
		String SUBMODULE = getModuleNameForLog() + " [getByName()] ";
		try {
			GenericDataDTO genericDataDTO = new GenericDataDTO();
			Page<Notification> notificationList = repository.findAllByNameOrStatus(pageRequest, name);
			if (null != notificationList && 0 < notificationList.getSize()) {
				makeGenericResponse(genericDataDTO, notificationList);
			}
			return genericDataDTO;
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
		return null;
	}

	@Override
	public GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy,
			Integer sortOrder,Integer mvnoId) {
		String SUBMODULE = getModuleNameForLog() + " [search()] ";
		try {
			PageRequest pageRequest = generatePageRequest(page, pageSize, sortBy, sortOrder);
			if (null != filterList && 0 < filterList.size()) {
				for (GenericSearchModel searchModel : filterList) {
					if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
						return getByName(searchModel.getFilterValue(), pageRequest);
					}
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
		return null;
	}

//    /*    @Override
//    public BroadcastDTO saveEntity(BroadcastDTO entity) throws Exception {
//        Broadcast broadcast= this.broadcastRepository.save(mapper.dtoToDomain(entity,new CycleAvoidingMappingContext()));
//        for(BroadcastPortsDTO broadcastPorts1:entity.getBroadcastPortsList()){
//            BroadcastPorts tempBroadcastPorts=new BroadcastPorts();
//            tempBroadcastPorts.setBroadcast(broadcast);
//            tempBroadcastPorts.setPortid(broadcastPorts1.getPortid());
//            this.broadcastPortsRepository.save(tempBroadcastPorts);
//        }
//        this.validateRequest(entity);
//        return entity;
//    }
//
//    public void validateRequest(BroadcastDTO entity) throws Exception {
//        String type = entity.getType();
//        Integer plan_id = Integer.parseInt(entity.getPlanid().toString());
//        Integer servicearea_id = Integer.parseInt(entity.getServiceareaid().toString());
//        Integer networkdevice_id = Integer.parseInt(entity.getNetworkdeviceid().toString());
//        Integer slot_id = Integer.parseInt(entity.getSlotid().toString());
//        Integer customer_id = Integer.parseInt(entity.getCustomer_id().toString());
//        Long template_id = entity.getTemplateid();
//        List<BroadcastPortsDTO> portsList = entity.getBroadcastPortsList();
//        LocalDate expirydate1 = entity.getExpirydate1();
//        LocalDate expirydate2 = entity.getExpirydate2();
//        String expirycondition = entity.getExpiry_condition();
//        Integer exprywithin = entity.getExpirywithin();
//
//        List<Customers> customersList=null;
//
//        if(entity.getCustcondition().equals("location")){
//            if(networkdevice_id==-1){
//                customersList = customersRepository.getAllCustomerByNetworkDevice(servicearea_id);
//            }
//            else if(slot_id==-1){
//                customersList = customersRepository.getAllCustomerBySlot(servicearea_id,networkdevice_id);
//            }
//            else{
//                List<Integer> integerList = new ArrayList<>();
//                if(portsList!=null && portsList.size()>0){
//                    for(BroadcastPortsDTO tempPort : portsList){
//                        integerList.add(tempPort.getPortid());
//                    }
//                }
//                customersList =  customersRepository.getAllCustomerForLocation(servicearea_id,networkdevice_id,slot_id,integerList);
//            }
//        }
//        if(entity.getCustcondition().equals("plan")) {
//            customersList=new ArrayList<>();
//            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findByPlanId(plan_id);
//            for(CustPlanMappping tempCustomerMapping:custPlanMapppingList){
//                customersList.add(tempCustomerMapping.getCustomer());
//            }
//        }
//        if(entity.getCustcondition().equals("customer")) {
//            List<Integer> integerList = new ArrayList<>();
//            integerList.add(customer_id);
//            customersList = this.customersRepository.getAllCustomersById(integerList);
//        }
//        if(entity.getCustcondition().equals("expiry")) {
//            if(exprywithin!=null && exprywithin!=0){
//                LocalDate currentDate = LocalDate.now();
//                LocalDate pastDate = currentDate.minusDays(exprywithin);
//                customersList = this.customersRepository.getAllCustomerByExpiryWithIn(pastDate,currentDate);
//            }
//            else if(expirycondition.equals("lessthan")){
//                customersList = this.customersRepository.getAllCustomerByExpiryLessthan(expirydate1);
//            }
//            else if(expirycondition.equals("greaterthan")){
//                customersList = this.customersRepository.getAllCustomerByExpiryGreaterthan(expirydate1);
//            }
//            else if(expirycondition.equals("equal")){
//                customersList = this.customersRepository.getAllCustomerByExpiryEqual(expirydate1);
//            }
//            else if(expirycondition.equals("between")){
//                customersList = this.customersRepository.getAllCustomerByExpiryWithIn(expirydate1,expirydate2);
//            }
//        }
//        if(type.equalsIgnoreCase("sms")){
//            smsSchedulerService.sendSMS(customersList,template_id);
//        }
//        else{
//            schedulerService.sendEmail(customersList,template_id);
//        }
//    }*/

	@Override
	public String getModuleNameForLog() {
		return "[Notification Service]";
	}

	@Scheduled(cron = "${cronJobTimeForOverDueCafFollowUp}")
	public void sendCafFollowUpOverDueNotification() {
		System.out.println("***** cronJobTimeForOverDueCafFollowUp Started !!! *****");
		String SUBMODULE = getModuleNameForLog() + " [sendCafFollowUpOverDueNotification()] ";
		log.info("XXXXXXXXXXXX----------CRON TIME_FOR_OVER_DUE_CAF_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		SchedulerAudit schedulerAudit = new SchedulerAudit();
		schedulerAudit.setStartTime(LocalDateTime.now());
		schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_OVER_DUE_CAF_FOLLOWUP_SCHEDULER);
		if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_OVER_DUE_CAF_FOLLOWUP)) {
			schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_OVER_DUE_CAF_FOLLOWUP);
			try {
				Pageable pageRequest = PageRequest.of(0, 200);
				Page<CafFollowUp> onePage = this.cafFollowUpService.findByIsMissedAndIsSendAndStatus(pageRequest);
				pageRequest = pageRequest.next();
				onePage.forEach(cafFollowUpEntity -> {
					if (cafFollowUpEntity.getFollowUpDatetime().isBefore(LocalDateTime.now())) {
						// send reminder notification
						String customerName = "";
						if (!StringUtils.isEmpty(cafFollowUpEntity.getCustomers().getLastname()))
							customerName += cafFollowUpEntity.getCustomers().getFirstname() + " "
									+ cafFollowUpEntity.getCustomers().getLastname();
						else
							customerName += cafFollowUpEntity.getCustomers().getFirstname();
						String staffPersonName = cafFollowUpEntity.getStaffUser().getFirstname() + " "
								+ cafFollowUpEntity.getStaffUser().getLastname();
						String followUpDateTime = cafFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
                        Integer customer_id = cafFollowUpEntity.getCustomers().getId();
                        String customer_type = cafFollowUpEntity.getCustomers().getCusttype();
						if (cafFollowUpEntity.getCustomers().getBuId() != null) {
							sendStaffNotificationForOverDue(staffPersonName, customerName,
									cafFollowUpEntity.getStaffUser().getEmail(), cafFollowUpEntity.getStaffUser().getPhone(),
									cafFollowUpEntity.getCustomers().getMvnoId(), null, followUpDateTime, cafFollowUpEntity.getCustomers().getBuId(), cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(),customer_id,customer_type);
							if (cafFollowUpEntity.getStaffUser().getStaffUserparent() != null) {
								StaffUser parentStaffuser = cafFollowUpEntity.getStaffUser().getStaffUserparent();
								String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();
								sendParentStaffNotificationForOverDue(staffPersonName, customerName, parentStaffuser.getEmail(),
										parentStaffuser.getPhone(), cafFollowUpEntity.getCustomers().getMvnoId(), null, followUpDateTime,
										parentStaffName, cafFollowUpEntity.getCustomers().getBuId(), cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(),customer_id,customer_type);
							}
						} else {
							sendStaffNotificationForOverDue(staffPersonName, customerName,
									cafFollowUpEntity.getStaffUser().getEmail(), cafFollowUpEntity.getStaffUser().getPhone(),
									cafFollowUpEntity.getCustomers().getMvnoId(), null, followUpDateTime, null, cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(),customer_id,customer_type);
							if (cafFollowUpEntity.getStaffUser().getStaffUserparent() != null) {
								StaffUser parentStaffuser = cafFollowUpEntity.getStaffUser().getStaffUserparent();
								String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();
								sendParentStaffNotificationForOverDue(staffPersonName, customerName, parentStaffuser.getEmail(),
										parentStaffuser.getPhone(), cafFollowUpEntity.getCustomers().getMvnoId(), null, followUpDateTime,
										parentStaffName, null, cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(),customer_id,customer_type);
							}
						}


						cafFollowUpEntity.setIsMissed(true);
						cafFollowUpEntity.setIsSend(true);
						this.cafFollowUpRepository.save(cafFollowUpEntity);
					}
				});
				System.out.println("***** cronJobTimeForOverDueCafFollowUp Ended !!! *****");
				schedulerAudit.setEndTime(LocalDateTime.now());
				schedulerAudit.setDescription("Over-due CAF FollowUp Scheduler Run Success");
				schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
				schedulerAudit.setTotalCount(null);
			} catch (Exception ex) {
				schedulerAudit.setEndTime(LocalDateTime.now());
				schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
				schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
				ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
				log.error("**********Scheduler Showing ERROR***********");
			} finally {
				schedulerAuditService.saveEntity(schedulerAudit);
				schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_OVER_DUE_CAF_FOLLOWUP);
				log.info("XXXXXXXXXXXX---------- Over-due CAF FollowUp Scheduler Locked released ---------XXXXXXXXXXXX");
			}
		} else {
			schedulerAudit.setEndTime(LocalDateTime.now());
			schedulerAudit.setDescription("Over-due CAF FollowUp Scheduler Lock held by another instance");
			schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
			schedulerAuditService.saveEntity(schedulerAudit);
			log.warn("XXXXXXXXXXXX----------Over-due CAF FollowUp Scheduler Locked held by another instance---------XXXXXXXXXXXX");
		}
	}
	@Scheduled(cron = "${cronJobTimeForReminderCafFollowUp}")
	public void sendCafFollowUpReminderNotification() {
		System.out.println("***** cronJobTimeForReminderCafFollowUp Started !!! *****");
		String SUBMODULE = getModuleNameForLog() + " [sendCafFollowUpReminderNotification()] ";
		log.info("XXXXXXXXXXXX----------CRON TIME_FOR_REMINDER_CAF_FOLLOWUP_SCHEDULER START---------XXXXXXXXXXXX");
		SchedulerAudit schedulerAudit = new SchedulerAudit();
		schedulerAudit.setStartTime(LocalDateTime.now());
		schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.TIME_FOR_REMINDER_CAF_FOLLOWUP_SCHEDULER);
		if (!schedulerLockService.isSchedulerLocked(CronjobConst.TIME_FOR_REMINDER_CAF_FOLLOWUP)) {
			schedulerLockService.acquireSchedulerLock(CronjobConst.TIME_FOR_REMINDER_CAF_FOLLOWUP);
			try {
				ClientService clientService = clientServiceSrv.getByName(CAF_FOLLOW_UP_REMIDER_TIME_NAME);
				Integer cafFollowUpTime = null;
				if (clientService == null) {
					cafFollowUpTime = 15;
				} else {
					cafFollowUpTime = Integer.parseInt(clientService.getValue());
				}

				LocalDateTime toTime = LocalDateTime.now().plusMinutes(cafFollowUpTime);
				Pageable pageRequest = PageRequest.of(0, 200);
				Page<CafFollowUp> onePage = this.cafFollowUpRepository.findByFollowUpDatetimeBetween(LocalDateTime.now(),
						toTime, pageRequest);
				pageRequest = pageRequest.next();
				onePage.forEach(cafFollowUpEntity -> {
					if (!cafFollowUpEntity.getStatus().equalsIgnoreCase("Closed")) {
						Long followTime = Duration.between(LocalDateTime.now(), cafFollowUpEntity.getFollowUpDatetime())
								.toMinutes();
						// send reminder notification
						String customerName = "";
						if (!StringUtils.isEmpty(cafFollowUpEntity.getCustomers().getLastname()))
							customerName += cafFollowUpEntity.getCustomers().getFirstname() + " "
									+ cafFollowUpEntity.getCustomers().getLastname();
						else
							customerName += cafFollowUpEntity.getCustomers().getFirstname();
						String staffPersonName = cafFollowUpEntity.getStaffUser().getFirstname() + " "
								+ cafFollowUpEntity.getStaffUser().getLastname();
						String followUpDateTime = cafFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
                        Integer customer_id = cafFollowUpEntity.getCustomers().getId();
                        String customer_type = cafFollowUpEntity.getCustomers().getCusttype();
						if (cafFollowUpEntity.getCustomers().getBuId() != null) {
							sendCustomerNotificationForReminder(staffPersonName, customerName,
									cafFollowUpEntity.getCustomers().getEmail(), cafFollowUpEntity.getCustomers().getMobile(),
									cafFollowUpEntity.getCustomers().getMvnoId().intValue(), followTime.intValue(),
									followUpDateTime, cafFollowUpEntity.getCustomers().getBuId(), cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(), customer_id,customer_type);
							sendStaffNotificationForReminder(staffPersonName, customerName,
									cafFollowUpEntity.getStaffUser().getEmail(), cafFollowUpEntity.getStaffUser().getPhone(),
									cafFollowUpEntity.getCustomers().getMvnoId().intValue(), followTime.intValue(),
									followUpDateTime, cafFollowUpEntity.getCustomers().getBuId(), cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(), customer_id,customer_type);
						} else {
							sendCustomerNotificationForReminder(staffPersonName, customerName,
									cafFollowUpEntity.getCustomers().getEmail(), cafFollowUpEntity.getCustomers().getMobile(),
									cafFollowUpEntity.getCustomers().getMvnoId().intValue(), followTime.intValue(),
									followUpDateTime, null, cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(), customer_id,customer_type);
							sendStaffNotificationForReminder(staffPersonName, customerName,
									cafFollowUpEntity.getStaffUser().getEmail(), cafFollowUpEntity.getStaffUser().getPhone(),
									cafFollowUpEntity.getStaffUser().getMvnoId().intValue(), followTime.intValue(),
									followUpDateTime, null, cafFollowUpEntity.getCustomers().getUsername(),cafFollowUpEntity.getStaffUser().getId().longValue(),customer_id,customer_type);
						}
						cafFollowUpEntity.setSendReminderNotification(true);
						this.cafFollowUpRepository.save(cafFollowUpEntity);
					}
				});
				schedulerAudit.setEndTime(LocalDateTime.now());
				schedulerAudit.setDescription("Reminder Caf-Followup Scheduler Run Success");
				schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
				schedulerAudit.setTotalCount(null);
				System.out.println("***** cronJobTimeForReminderCafFollowUp Ended !!! *****");
			} catch (Exception ex) {
				schedulerAudit.setEndTime(LocalDateTime.now());
				schedulerAudit.setDescription(ExceptionUtils.getRootCauseMessage(ex));
				schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
				ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
				log.error("**********Scheduler Showing ERROR***********");
			} finally {
				schedulerAuditService.saveEntity(schedulerAudit);
				schedulerLockService.releaseSchedulerLock(CronjobConst.TIME_FOR_REMINDER_CAF_FOLLOWUP);
				log.info("XXXXXXXXXXXX---------- Reminder Caf-Followup Scheduler Locked released ---------XXXXXXXXXXXX");
			}
		} else {
			schedulerAudit.setEndTime(LocalDateTime.now());
			schedulerAudit.setDescription("Reminder Caf-Followup Scheduler Lock held by another instance");
			schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
			schedulerAuditService.saveEntity(schedulerAudit);
			log.warn("XXXXXXXXXXXX----------Reminder Caf-Followup Scheduler Locked held by another instance---------XXXXXXXXXXXX");
		}
	}

	public void sendStaffNotificationForOverDue(String staffPersonName, String customername, String email, String phone,
			Integer mvnoId, Integer followUpTime, String followUpDateTime, Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId, custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForStaff(TemplateNotification templateNotification, String email, String phone,
			String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
			String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured,Long buId,String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {

					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null,buId,custUsername,staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF));
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_STAFF);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}

	}

	public void sendParentStaffNotificationForOverDue(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String parentStaffPersonName, Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendParentStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true, true,buId, custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true,
							false,buId,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, customername,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, false,
							true,buId,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForParentStaff(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
			String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
			boolean isSmsConfigured, Long buId,String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForParentStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName, null,buId,custUsername,staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_OVER_DUE_PARENT_STAFF));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendCustomerNotificationForReminder(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime,Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendCustomerNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId, custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}
	
	public void sendReminderNotificationForCustomer(TemplateNotification templateNotification, String email,
			String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
			String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
			boolean isSmsConfigured, Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForCustomer()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null,buId,custUsername,staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(),KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_CUSTOMER));

				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}
	
	public void sendStaffNotificationForReminder(String staffPersonName, String customername, String email,
			String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,buId,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,buId,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.CAF_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,buId,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendReminderNotificationForStaff(TemplateNotification templateNotification, String email, String phone,
												 String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
												 String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured, Long buId, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null,buId,custUsername, staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName(), KafkaConstant.SALES_CRMS_BSS_CAF_FOLLOW_UP_REMINDER_STAFF));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}


	//TicketFollowUP Schedulers



//	@Scheduled(cron = "${cronJobTimeForOverDueTicketFollowUp}")
//	public void sendTicketFollowUpOverDueNotification() {
//		System.out.println("***** cronJobTimeForOverDueTicketFollowUp Started !!! *****");
//		String SUBMODULE = getModuleNameForLog() + " [sendTicketFollowUpOverDueNotification()] ";
//		try {
//			Pageable pageRequest = PageRequest.of(0, 200);
//			Page<TicketFollowUp> onePage = this.ticketFollowUpService.findByIsMissedAndIsSendAndStatus(pageRequest);
//			pageRequest = pageRequest.next();
//			onePage.forEach(tikeFollowUpEntity -> {
//				if (tikeFollowUpEntity.getFollowUpDatetime().isBefore(LocalDateTime.now())) {
//					// send reminder notification
//					String caseNumber = "";
//					if (!StringUtils.isEmpty(tikeFollowUpEntity.getTicket().getCaseNumber()))
//						caseNumber += tikeFollowUpEntity.getTicket().getCaseNumber() ;
//					else
//						caseNumber += tikeFollowUpEntity.getTicket().getCaseId();
//					String staffPersonName = tikeFollowUpEntity.getStaffUser().getFirstname() + " "
//							+ tikeFollowUpEntity.getStaffUser().getLastname();
//					String followUpDateTime = tikeFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
//					sendStaffNotificationForTicketOverDue(staffPersonName, caseNumber,
//							tikeFollowUpEntity.getStaffUser().getEmail(), tikeFollowUpEntity.getStaffUser().getPhone(),
//							tikeFollowUpEntity.getStaffUser().getMvnoId(), null, followUpDateTime);
//					if (tikeFollowUpEntity.getStaffUser().getStaffUserparent() != null) {
//						StaffUser parentStaffuser = tikeFollowUpEntity.getStaffUser().getStaffUserparent();
//						String parentStaffName = parentStaffuser.getFirstname() + " " + parentStaffuser.getLastname();
//						sendParentStaffNotificationForTicketOverDue(staffPersonName, caseNumber, parentStaffuser.getEmail(),
//								parentStaffuser.getPhone(), parentStaffuser.getMvnoId(), null, followUpDateTime,
//								parentStaffName);
//					}
//
//					tikeFollowUpEntity.setIsMissed(true);
//					tikeFollowUpEntity.setIsSend(true);
//					this.ticketFollowUpRepository.save(tikeFollowUpEntity);
//				}
//			});
//			System.out.println("***** cronJobTimeForOverDueTicketFollowUp Ended !!! *****");
//		} catch (Exception ex) {
//			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//		}
//	}

//	@Scheduled(cron = "${cronJobTimeForReminderTicketFollowUp}")
//	public void sendTicketFollowUpReminderNotification() {
//		String SUBMODULE = getModuleNameForLog() + " [sendCafFollowUpReminderNotification()] ";
//		System.out.println("***** cronJobTimeForReminderTicketFollowUp Started !!! *****");
//		try {
//			ClientService clientService = clientServiceSrv.getByName(TICKET_FOLLOW_UP_REMIDER_TIME_NAME);
//			Integer ticketFollowUpTime = null;
//			if (clientService == null) {
//				ticketFollowUpTime = 15;
//			}else{
//				ticketFollowUpTime = Integer.parseInt(clientService.getValue());
//			}
//			LocalDateTime toTime = LocalDateTime.now().plusMinutes(ticketFollowUpTime);
//			Pageable pageRequest = PageRequest.of(0, 200);
//			Page<TicketFollowUp> onePage = this.ticketFollowUpRepository.findByFollowUpDatetimeBetween(LocalDateTime.now(),
//					toTime, pageRequest);
//			pageRequest = pageRequest.next();
//			onePage.forEach(ticketFollowUpEntity -> {
//				if (!ticketFollowUpEntity.getStatus().equalsIgnoreCase("Closed")) {
//					Long followTime = Duration.between(LocalDateTime.now(), ticketFollowUpEntity.getFollowUpDatetime())
//							.toMinutes();
//					// send reminder notification
//					String caseNumber = "";
//					if (!StringUtils.isEmpty(ticketFollowUpEntity.getTicket().getCaseNumber()))
//						caseNumber += ticketFollowUpEntity.getTicket().getCaseNumber();
//					else
//						caseNumber += ticketFollowUpEntity.getTicket().getCaseId();
//					String staffPersonName = ticketFollowUpEntity.getStaffUser().getFirstname() + " "
//							+ ticketFollowUpEntity.getStaffUser().getLastname();
//					String followUpDateTime = ticketFollowUpEntity.getFollowUpDatetime().format(FORMATOR);
//					sendCustomerNotificationForTicketReminder(staffPersonName, caseNumber,
//							ticketFollowUpEntity.getTicket().getCustomers().getEmail(), ticketFollowUpEntity.getTicket().getCustomers().getMobile(),
//							ticketFollowUpEntity.getTicket().getCustomers().getMvnoId().intValue(), followTime.intValue(),
//							followUpDateTime,ticketFollowUpEntity.getTicket().getCustomers().getUsername());
//					sendStaffNotificationForTicketReminder(staffPersonName, caseNumber,
//							ticketFollowUpEntity.getStaffUser().getEmail(), ticketFollowUpEntity.getStaffUser().getPhone(),
//							ticketFollowUpEntity.getStaffUser().getMvnoId().intValue(), followTime.intValue(),
//							followUpDateTime, ticketFollowUpEntity.getTicket().getCustomers().getUsername());
//					ticketFollowUpEntity.setSendReminderNotification(true);
//					this.ticketFollowUpRepository.save(ticketFollowUpEntity);
//				}
//			});
//			System.out.println("***** cronJobTimeForReminderTicketFollowUp Ended !!! *****");
//		} catch (Exception ex) {
//			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//		}
//	}

	public void sendStaffNotificationForTicketOverDue(String staffPersonName, String customername, String email, String phone,
												Integer mvnoId, Integer followUpTime, String followUpDateTime) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForTicketStaff(TemplateNotification templateNotification, String email, String phone,
												String headerName, String caseNumber, Integer mvnoId, Integer followUpTime, String followUpDateTime,
												String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					Long buId = null;
					if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
						buId =  getBUIdsFromCurrentStaff().get(0);
					}
					TicketFollowUpMessage ticketFollowUpMessage = new TicketFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, caseNumber, staffPersonName, parentStaffPersonName, null,buId);
					ticketFollowUpMessage.setEmailConfigured(isEmailConfigured);
					ticketFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(ticketFollowUpMessage);
					kafkaMessageSender.send(new KafkaMessageData(ticketFollowUpMessage, TicketFollowUpMessage.class.getSimpleName()));
//					messageSender.send(ticketFollowUpMessage, RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_STAFF);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}

	}

	public void sendParentStaffNotificationForTicketOverDue(String staffPersonName, String caseNumber, String email,
													  String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String parentStaffPersonName) {
		String SUBMODULE = getModuleNameForLog() + " [sendParentStaffNotificationForOverDue()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_FOR_PARENT_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true, true);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, true,
							false);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendOverDueNotificationForTicketParentStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_OVERDUE_TEMPLATE_HEADER_FOR_PARENT_STAFF, caseNumber,
							mvnoId, followUpTime, followUpDateTime, staffPersonName, parentStaffPersonName, false,
							true);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendOverDueNotificationForTicketParentStaff(TemplateNotification templateNotification, String email,
													  String phone, String headerName, String caseNumber, Integer mvnoId, Integer followUpTime,
													  String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
													  boolean isSmsConfigured) {
		String SUBMODULE = getModuleNameForLog() + " [sendOverDueNotificationForParentStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					Long buId = null;
					if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
						buId =  getBUIdsFromCurrentStaff().get(0);
					}
					TicketFollowUpMessage ticketFollowUpMessage = new TicketFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, caseNumber, staffPersonName, parentStaffPersonName, null,buId);
					ticketFollowUpMessage.setEmailConfigured(isEmailConfigured);
					ticketFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(ticketFollowUpMessage);
//					messageSender.send(ticketFollowUpMessage,
//							RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_OVER_DUE_PARENT_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(ticketFollowUpMessage,TicketFollowUpMessage.class.getSimpleName()));

				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendCustomerNotificationForTicketReminder(String staffPersonName, String customername, String email,
													String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendCustomerNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_FOR_CUSTOMER_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForTicketCustomer(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_CUSTOMER, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendReminderNotificationForTicketCustomer(TemplateNotification templateNotification, String email,
													String phone, String headerName, String customerName, Integer mvnoId, Integer followUpTime,
													String followUpDateTime, String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured,
													boolean isSmsConfigured,String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForCustomer()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					Long buId = null;
					if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
						buId =  getBUIdsFromCurrentStaff().get(0);
					}
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null,buId,custUsername,staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_CUSTOMER);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName()));

				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendStaffNotificationForTicketReminder(String staffPersonName, String customername, String email,
												 String phone, Integer mvnoId, Integer followUpTime, String followUpDateTime, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendStaffNotificationForReminder()] ";
		try {
			Optional<TemplateNotification> optionalTemplate = this.notificationTemplateRepository
					.findByTemplateName(RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_FOR_STAFF_TEMPLATE);
			if (optionalTemplate.isPresent()) {
				if (optionalTemplate.get().isEmailEventConfigured() && optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, true, custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isEmailEventConfigured()) {
					sendReminderNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, true, false,custUsername,staffId,customer_id,customer_type);
				} else if (optionalTemplate.get().isSmsEventConfigured()) {
					sendReminderNotificationForTicketStaff(optionalTemplate.get(), email, phone,
							RabbitMqConstants.TICKET_FOLLOW_UP_REMINDER_TEMPLATE_HEADER_FOR_STAFF, customername, mvnoId,
							followUpTime, followUpDateTime, staffPersonName, null, false, true,custUsername,staffId,customer_id,customer_type);
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}

	public void sendReminderNotificationForTicketStaff(TemplateNotification templateNotification, String email, String phone,
												 String headerName, String customerName, Integer mvnoId, Integer followUpTime, String followUpDateTime,
												 String staffPersonName, String parentStaffPersonName, boolean isEmailConfigured, boolean isSmsConfigured, String custUsername,Long staffId,Integer customer_id,String customer_type) {
		String SUBMODULE = getModuleNameForLog() + " [sendReminderNotificationForStaff()] ";
		try {
			if (templateNotification != null) {
				if (templateNotification.isEmailEventConfigured()) {
					Long buId = null;
					if(getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0){
						buId =  getBUIdsFromCurrentStaff().get(0);
					}
					CafFollowUpMessage cafFollowUpMessage = new CafFollowUpMessage(headerName, templateNotification,
							RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY, phone, email, mvnoId, followUpDateTime,
							followUpTime, customerName, staffPersonName, parentStaffPersonName,null, buId,custUsername,staffId,customer_id,customer_type);
					cafFollowUpMessage.setEmailConfigured(isEmailConfigured);
					cafFollowUpMessage.setSmsConfigured(isSmsConfigured);
					Gson gson = new Gson();
					gson.toJson(cafFollowUpMessage);
//					messageSender.send(cafFollowUpMessage,
//							RabbitMqConstants.QUEUE_TROUBLE_TICKET_FOLLOW_UP_REMINDER_STAFF);
					kafkaMessageSender.send(new KafkaMessageData(cafFollowUpMessage,CafFollowUpMessage.class.getSimpleName()));
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
		}
	}


}
