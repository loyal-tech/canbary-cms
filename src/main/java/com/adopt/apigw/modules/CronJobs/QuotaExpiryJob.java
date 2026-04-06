package com.adopt.apigw.modules.CronJobs;

import com.adopt.apigw.schedulerAudit.SchedulerAudit;
import com.adopt.apigw.schedulerAudit.SchedulerAuditService;
import com.adopt.apigw.service.SchedulerLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.adopt.apigw.constants.NotificationConfigConstant;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.mapper.postpaid.CustQuotaDtlsMapper;
import com.adopt.apigw.model.postpaid.PostpaidPlan;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.Notification.domain.NotificationRepeat;
import com.adopt.apigw.modules.Notification.model.NotificationConfigDTO;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.repository.NotificationRepeatRepository;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.ippool.model.CustomIpExpiryModel;
import com.adopt.apigw.modules.ippool.model.CustomPlanExpiryModel;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.radius.CustomersRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class QuotaExpiryJob {


    @Autowired
    private NotificationService service;

    @Autowired
    private CustQuotaRepository quotaDtlsRepository;

    @Autowired
    private CustQuotaDtlsMapper quotaDtlsMapper;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private CustomRepository<CustomIpExpiryModel> customRepository;

    @Autowired
    private CustomRepository<CustomPlanExpiryModel> customRepository1;

    @Autowired
    private NotificationRepeatRepository repeatRepository;

    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private SchedulerLockService schedulerLockService;

    @Autowired
    private SchedulerAuditService schedulerAuditService;

    @Scheduled(cron = "${quotaexpiryjob}}")
    public void cronJobSch() throws Exception {
        log.info("XXXXXXXXXXXX----------Quota Expiry Scheduler START---------XXXXXXXXXXXX");
        SchedulerAudit schedulerAudit = new SchedulerAudit();
        schedulerAudit.setStartTime(LocalDateTime.now());
        schedulerAudit.setSchedulerName(CronjobConst.SCHEDULER_AUDIT.QUOTA_EXPIRY_SCHEDULER);
        if (!schedulerLockService.isSchedulerLocked(CronjobConst.QUOTA_EXPIRY_JOB)) {
            schedulerLockService.acquireSchedulerLock(CronjobConst.QUOTA_EXPIRY_JOB);
            try {
                List<NotificationDTO> notificationDTOList = service.findNotificationByCategory("configurable", "ACTIVE");
                if (notificationDTOList != null && notificationDTOList.size() > 0) {
                    notificationDTOList.forEach(notificationDTO -> {
                        if (notificationDTO != null) {
                            NotificationConfigDTO configDTO = notificationDTO.getNotificationConfig();
                            if (configDTO != null) {
                                if (configDTO.getConfig_entity().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ENTITY_CUSTOMER)) {
                                    if (configDTO.getConfig_attribute().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_QUOTA)) {
                                        if (configDTO.getConfig_atrr_type().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_TYPE_VAL_ABBS)) {
                                            if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_LESSTHAN)) {
                                                Double quota = Double.parseDouble(configDTO.getAttr_value());
                                                List<CustQuotaDtlsPojo> getUsedQuota = quotaDtlsRepository.findAllByUsedQuotaLessThan(quota).stream().map(data -> quotaDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                                                List<CustQuotaDtlsPojo> tempCustQuotaDtlsPojoList = new ArrayList<>();
                                                getUsedQuota.forEach(quotaDtlsDTO -> {
                                                    List<NotificationRepeat> notificationRepeat = repeatRepository.findBySubscriberidAndNotificationidAndPackrelid(quotaDtlsDTO.getCustomer().getId().longValue(), notificationDTO.getId(), quotaDtlsDTO.getCustPlanMappping().getId().longValue());
                                                    if (notificationRepeat.size() <= 0) {
                                                        tempCustQuotaDtlsPojoList.add(quotaDtlsDTO);
                                                    }
                                                });
                                                List<Map<String, String>> mapList = this.quotaExpiryMapBuilder(tempCustQuotaDtlsPojoList, null);
                                                CommunicationHelper communicationHelper = new CommunicationHelper();
                                                try {
                                                    communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
                                                    List<NotificationRepeat> notificationRepeatList = new ArrayList<>();
                                                    tempCustQuotaDtlsPojoList.forEach(data -> {
                                                        NotificationRepeat notificationRepeat = new NotificationRepeat(data.getCustomer().getId().longValue(), data.getCustPlanMappping().getId().longValue(), notificationDTO.getId());
                                                        notificationRepeatList.add(notificationRepeat);
                                                    });
                                                    if (notificationRepeatList.size() > 0) {
                                                        repeatRepository.saveAll(notificationRepeatList);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_GREATERTHAN)) {
                                                Double quota = Double.parseDouble(configDTO.getAttr_value());
                                                List<CustQuotaDtlsPojo> getUsedQuota = quotaDtlsRepository.findAllByUsedQuotaGreaterThan(quota).stream().map(data -> quotaDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                                                List<CustQuotaDtlsPojo> tempCustQuotaDtlsPojoList = new ArrayList<>();
                                                getUsedQuota.forEach(quotaDtlsDTO -> {
                                                    List<NotificationRepeat> notificationRepeat = repeatRepository.findBySubscriberidAndNotificationidAndPackrelid(quotaDtlsDTO.getCustomer().getId().longValue(), notificationDTO.getId(), quotaDtlsDTO.getCustPlanMappping().getId().longValue());
                                                    if (notificationRepeat.size() <= 0) {
                                                        tempCustQuotaDtlsPojoList.add(quotaDtlsDTO);
                                                    }
                                                });
                                                List<Map<String, String>> mapList = this.quotaExpiryMapBuilder(tempCustQuotaDtlsPojoList, null);
                                                CommunicationHelper communicationHelper = new CommunicationHelper();
                                                try {
                                                    communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
                                                    List<NotificationRepeat> notificationRepeatList = new ArrayList<>();
                                                    tempCustQuotaDtlsPojoList.forEach(data -> {
                                                        NotificationRepeat notificationRepeat = new NotificationRepeat(data.getCustomer().getId().longValue(), data.getCustPlanMappping().getId().longValue(), notificationDTO.getId());
                                                        notificationRepeatList.add(notificationRepeat);
                                                    });
                                                    if (notificationRepeatList.size() > 0) {
                                                        repeatRepository.saveAll(notificationRepeatList);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        } else if (configDTO.getConfig_atrr_type().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_TYPE_VAL_PERC)) {
                                            if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_LESSTHAN)) {
                                                Integer percent = Integer.parseInt(configDTO.getAttr_value());
                                                List<CustQuotaDtlsPojo> getPercentQuota = quotaDtlsRepository.getQuotaByLessPercent(percent).stream().map(data -> quotaDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                                                List<CustQuotaDtlsPojo> tempCustQuotaDtlsPojoList = new ArrayList<>();
                                                getPercentQuota.forEach(quotaDtlsDTO -> {
                                                    List<NotificationRepeat> notificationRepeat = repeatRepository.findBySubscriberidAndNotificationidAndPackrelid(quotaDtlsDTO.getCustomer().getId().longValue(), notificationDTO.getId(), quotaDtlsDTO.getCustPlanMappping().getId().longValue());
                                                    if (notificationRepeat.size() <= 0) {
                                                        tempCustQuotaDtlsPojoList.add(quotaDtlsDTO);
                                                    }
                                                });
                                                List<Map<String, String>> mapList = this.quotaExpiryMapBuilder(tempCustQuotaDtlsPojoList, percent.toString() + " percent");
                                                CommunicationHelper communicationHelper = new CommunicationHelper();
                                                try {
                                                    communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
                                                    List<NotificationRepeat> notificationRepeatList = new ArrayList<>();
                                                    tempCustQuotaDtlsPojoList.forEach(data -> {
                                                        NotificationRepeat notificationRepeat = new NotificationRepeat(data.getCustomer().getId().longValue(), data.getCustPlanMappping().getId().longValue(), notificationDTO.getId());
                                                        notificationRepeatList.add(notificationRepeat);
                                                    });
                                                    if (notificationRepeatList.size() > 0) {
                                                        repeatRepository.saveAll(notificationRepeatList);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_GREATERTHAN)) {
                                                Integer percent = Integer.parseInt(configDTO.getAttr_value());
                                                List<CustQuotaDtlsPojo> getPercentQuota = quotaDtlsRepository.getQuotaByGreaterPercent(percent).stream().map(data -> quotaDtlsMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
                                                List<CustQuotaDtlsPojo> tempCustQuotaDtlsPojoList = new ArrayList<>();
                                                getPercentQuota.forEach(quotaDtlsDTO -> {
                                                    List<NotificationRepeat> notificationRepeat = repeatRepository.findBySubscriberidAndNotificationidAndPackrelid(quotaDtlsDTO.getCustomer().getId().longValue(), notificationDTO.getId(), quotaDtlsDTO.getCustPlanMappping().getId().longValue());
                                                    if (notificationRepeat.size() <= 0) {
                                                        tempCustQuotaDtlsPojoList.add(quotaDtlsDTO);
                                                    }
                                                });
                                                List<Map<String, String>> mapList = this.quotaExpiryMapBuilder(tempCustQuotaDtlsPojoList, percent.toString() + " percent");
                                                CommunicationHelper communicationHelper = new CommunicationHelper();
                                                try {
                                                    communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
                                                    List<NotificationRepeat> notificationRepeatList = new ArrayList<>();
                                                    tempCustQuotaDtlsPojoList.forEach(data -> {
                                                        NotificationRepeat notificationRepeat = new NotificationRepeat(data.getCustomer().getId().longValue(), data.getCustPlanMappping().getId().longValue(), notificationDTO.getId());
                                                        notificationRepeatList.add(notificationRepeat);
                                                    });
                                                    if (notificationRepeatList.size() > 0) {
                                                        repeatRepository.saveAll(notificationRepeatList);
                                                    }
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
//                            else if (configDTO.getConfig_attribute().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_PLAN_EXPIRY)) {
//                                if (configDTO.getConfig_atrr_type().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_TYPE_DATE)) {
//                                    if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_BEFORE)) {
//                                        int daysBefore = Integer.parseInt(configDTO.getAttr_value());
//                                        LocalDate localDateTime = LocalDate.now().plusDays(daysBefore);
//                                        List<String> dates = this.calculateSameDay(localDateTime);
//                                        List<CustomPlanExpiryModel> planExpiryModels = customRepository1.getResultOfQuery(IpExpiryScript.getPlanExpiredUser(dates.get(0).toString(), dates.get(1).toString()), CustomPlanExpiryModel.class);
//                                        List<Map<String, String>> mapList = this.planExpiryMapBuilder(planExpiryModels);
//                                        CommunicationHelper communicationHelper = new CommunicationHelper();
//                                        try {
//                                            communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_AFTER)) {
//                                        int daysBefore = Integer.parseInt(configDTO.getAttr_value());
//                                        LocalDate localDateTime = LocalDate.now().minusDays(daysBefore);
//                                        List<String> dates = this.calculateSameDay(localDateTime);
//                                        List<CustomPlanExpiryModel> planExpiryModels = customRepository1.getResultOfQuery(IpExpiryScript.getPlanExpiredUser(dates.get(0).toString(), dates.get(1).toString()), CustomPlanExpiryModel.class);
//                                        List<Map<String, String>> mapList = this.planExpiryMapBuilder(planExpiryModels);
//                                        CommunicationHelper communicationHelper = new CommunicationHelper();
//                                        try {
//                                            communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }
//                            }
//                            else if (configDTO.getConfig_attribute().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_IP_EXPIRY)) {
//                                if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_BEFORE)) {
//                                    int daysBefore = Integer.parseInt(configDTO.getAttr_value());
//                                    String localDate = LocalDate.now().plusDays(daysBefore).toString();
//                                    List<CustomIpExpiryModel> ipExpiryModels = customRepository.getResultOfQuery(IpExpiryScript.getIpExpiredUser(LocalDate.now().plusDays(daysBefore).toString()), CustomIpExpiryModel.class);
//                                    List<Map<String, String>> mapList = this.ipExpiryMapBuilder(ipExpiryModels);
//                                    CommunicationHelper communicationHelper = new CommunicationHelper();
//                                    try {
//                                        communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                                else if (configDTO.getAtrr_condi().equalsIgnoreCase(NotificationConfigConstant.CONFIG_ATTR_COND_AFTER)) {
//                                    int daysBefore = Integer.parseInt(configDTO.getAttr_value());
//                                    List<CustomIpExpiryModel> ipExpiryModels = customRepository.getResultOfQuery(IpExpiryScript.getIpExpiredUser(LocalDate.now().minusDays(daysBefore).toString()), CustomIpExpiryModel.class);
//                                    List<Map<String, String>> mapList = this.ipExpiryMapBuilder(ipExpiryModels);
//                                    CommunicationHelper communicationHelper = new CommunicationHelper();
//                                    try {
//                                        communicationHelper.generateCommunicationDetails(notificationDTO.getId(), mapList);
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            }
                                }
                            }
                        }
                    });
                    schedulerAudit.setEndTime(LocalDateTime.now());
                    schedulerAudit.setDescription("Quota Expiry Scheduler Success");
                    schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_SUCCESS);
                    schedulerAudit.setTotalCount(notificationDTOList.size());
                }
            } catch (Exception ex) {
                schedulerAudit.setEndTime(LocalDateTime.now());
                schedulerAudit.setDescription(ex.getMessage());
                schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_FAILURE);
                log.error(ex.toString(), ex);
                log.error("**********Scheduler Showing ERROR***********");
            } finally {
                schedulerAuditService.saveEntity(schedulerAudit);
                schedulerLockService.releaseSchedulerLock(CronjobConst.QUOTA_EXPIRY_JOB);
                log.info("XXXXXXXXXXXX---------- Quota Expiry Scheduler Locked released ---------XXXXXXXXXXXX");
            }
        } else {
            schedulerAudit.setEndTime(LocalDateTime.now());
            schedulerAudit.setDescription("Quota Expiry Scheduler Lock held by another instance");
            schedulerAudit.setStatus(CronjobConst.SCHEDULER_AUDIT.SCHEDULER_STATUS_LOCKED);
            schedulerAuditService.saveEntity(schedulerAudit);
            log.warn("XXXXXXXXXXXX----------Quota Expiry Scheduler Locked held by another instance---------XXXXXXXXXXXX");
        }
    }

//    List<String> calculateSameDay(LocalDate localDateTime) {
//        List<String> dates = new ArrayList<>();
//        StringBuilder dayStart = new StringBuilder(localDateTime.toString());
//        dayStart.append(" 00:00:00");
//        StringBuilder dayEnd = new StringBuilder(localDateTime.toString());
//        dayEnd.append(" 23:59:59");
//        dates.add(dayStart.toString());
//        dates.add(dayEnd.toString());
//        return dates;
//    }
//
//    public List<Map<String, String>> ipExpiryMapBuilder(List<CustomIpExpiryModel> customIpExpiryModels) {
//        List<Map<String, String>> mapList = new ArrayList<>();
//        customIpExpiryModels.forEach(data -> {
//            Map<String, String> sms = new HashMap<>();
//            sms.put(CommunicationConstant.DESTINATION, data.getMobile());
//            sms.put(CommunicationConstant.IP, data.getIp_address().replace(":", ""));
//            sms.put(CommunicationConstant.EXPIRY, data.getEnddate().toString());
//            sms.put(CommunicationConstant.USERNAME, data.getUsername());
//            sms.put(CommunicationConstant.EMAIL, data.getEmail());
//            mapList.add(sms);
//        });
//        return mapList;
//    }
//
//    public List<Map<String, String>> planExpiryMapBuilder(List<CustomPlanExpiryModel> customPlanExpiryModels) {
//        List<Map<String, String>> mapList = new ArrayList<>();
//        customPlanExpiryModels.forEach(data -> {
//            Map<String, String> sms = new HashMap<>();
//            sms.put(CommunicationConstant.DESTINATION, data.getMobile());
//            sms.put(CommunicationConstant.PLAN_NAME, data.getPlanname());
//            sms.put(CommunicationConstant.USERNAME, data.getUsername());
//            sms.put(CommunicationConstant.EMAIL, data.getEmail());
//            mapList.add(sms);
//        });
//        return mapList;
//    }

    public List<Map<String, String>> quotaExpiryMapBuilder
            (List<CustQuotaDtlsPojo> custQuotaDtlsPojos, String
                    usage) {
        List<Map<String, String>> mapList = new ArrayList<>();
        custQuotaDtlsPojos.forEach(data -> {
            PostpaidPlan postpaidPlan = postpaidPlanRepo.findById(data.getCustPlanMappping().getPlanId()).orElse(null);
            if (postpaidPlan != null) {
                Map<String, String> sms = new HashMap<>();
                sms.put(CommunicationConstant.DESTINATION, data.getCustomer().getMobile());
                sms.put(CommunicationConstant.PLAN_NAME, postpaidPlan.getName());
                sms.put(CommunicationConstant.EMAIL, data.getCustomer().getEmail());
                if (usage == null) {
                    sms.put(CommunicationConstant.USAGE, data.getUsedQuota().toString() + " " + data.getQuotaUnit());
                } else {
                    sms.put(CommunicationConstant.USAGE, usage);
                }
                mapList.add(sms);
            }
        });
        return mapList;
    }
}
