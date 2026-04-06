package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.PartnerAmountMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SavePartnerSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdatePartnerSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedServices.CreateDataSharedService;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.BusinessUnit.service.BusinessUnitService;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.PartnerLedger.domain.PartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerLedger;
import com.adopt.apigw.modules.PartnerLedger.domain.QPartnerPayment;
import com.adopt.apigw.modules.PartnerLedger.mapper.PartnerPaymentMapper;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerLedgerBalanceDTO;
import com.adopt.apigw.modules.PartnerLedger.model.PartnerPaymentDTO;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerLedgerRepository;
import com.adopt.apigw.modules.PartnerLedger.repository.PartnerPaymentRepository;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerDetailsService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerLedgerService;
import com.adopt.apigw.modules.PartnerLedger.service.PartnerPaymentService;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.auditLog.model.AuditForResponseModel;
import com.adopt.apigw.nepaliCalendarUtils.model.EnglishDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.model.NepaliDateDTO;
import com.adopt.apigw.nepaliCalendarUtils.service.DateConverterService;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.PartnerMessage;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.common.StaffUserServiceAreaMappingRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.*;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import io.swagger.models.auth.In;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PartnerService extends AbstractService<Partner, PartnerPojo, Integer> {

    public static final String MODULE = " [PartnerService] ";
    private static final Logger log = LoggerFactory.getLogger(APIController.class);
    @Autowired
    PincodeRepository pincodeRepository;
    @Autowired
    PartnerCreditDocumentRepository partnerCreditDocumentRepository;
    @Autowired
    PartnerCreditDocRepository partnerCreditDocRepository;
    @Autowired
    PartnerService partnerService;
    @Autowired
    StaffUserRepository staffUserRepository;
    @Autowired
    StaffUserServiceAreaMappingRepository staffUserServiceAreaMappingRepository;
    @Autowired
    CreateDataSharedService createDataSharedService;
    @Autowired
    CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;
    @Autowired
    CustomerMapper customerMapper;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private NumberSequenceUtil numberSequenceUtil;
    @Autowired
    private PartnerRepository entityRepository;
    @Autowired
    private MessagesPropertyConfig messagesProperty;
    @Autowired
    private TaxService taxService;
    @Autowired
    private CountryService countryService;
    @Autowired
    private StateService stateService;
    @Autowired
    private CityService cityService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private BusinessUnitService businessUnitService;
    @Autowired
    private ServiceAreaRepository serviceAreaRepository;
    @Autowired
    private PriceBookRepository priceBookRepository;
    @Autowired
    private PartnerLedgerService partnerLedgerService;
    @Autowired
    private PartnerCommissionRepository commissionRepository;
    @Lazy
    @Autowired
    private PartnerLedgerDetailsService partnerLedgerDetailsService;
    @Autowired
    private PartnerPaymentService partnerPaymentService;
    @Autowired
    private PartnerLedgerRepository partnerLedgerRepository;
    @Autowired
    private PartnerPaymentRepository partnerPaymentRepository;
    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private DateConverterService dateConverterService;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private PartnerPaymentMapper partnerPaymentMapper;
    public PartnerService() {
        sortColMap.put("areaName", "srn.concatname");
        sortColMap.put("id", "partnerid");
        sortColMap.put("name", "PARTNERNAME");
    }

    @Override
    protected JpaRepository<Partner, Integer> getRepository() {
        return entityRepository;
    }

    public Page<Partner> getList(Integer pageNumber, Integer customPageSize, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        pageRequest = generatePageRequest(pageNumber, customPageSize, sortBy, sortOrder);
        if (null == filterList || 0 == filterList.size())
            // TODO: pass mvnoID manually 6/5/2025
            if (mvnoId == 1) return entityRepository.findAll(pageRequest);
        if (null == filterList || 0 == filterList.size())
            if (getBUIdsFromCurrentStaff() == null || getBUIdsFromCurrentStaff().size() == 0)
                // TODO: pass mvnoID manually 6/5/2025
                return entityRepository.findAll(pageRequest, Arrays.asList(1, mvnoId));
                // TODO: pass mvnoID manually 6/5/2025
            else return entityRepository.findAll(pageRequest, mvnoId, getBUIdsFromCurrentStaff());
        else return search(filterList, pageNumber, customPageSize, sortBy, sortOrder,mvnoId);
    }

    public Page<Partner> searchEntity(String searchText, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        if (getBUIdsFromCurrentStaff().size() == 0)
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.searchEntity(searchText, pageRequest, getMvnoIdFromCurrentStaff(null));
        else
            // TODO: pass mvnoID manually 6/5/2025
            return entityRepository.findAllByNameAndIsDeleteIsFalse(searchText, pageRequest, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
    }

    public List<Partner> getAllActiveEntities() {
        // TODO: pass mvnoID manually 6/5/2025
        List<Partner> partners = entityRepository.findByStatusAndIsDeleteIsFalse(CommonConstants.ACTIVE_STATUS).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || partner.getMvnoId() == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue()).collect(Collectors.toList());
        if (!super.getLoggedInUser().getLco())
            partners = partners.stream().filter(x -> !x.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO)).collect(Collectors.toList());
        return partners;
    }

    public List<Partner> getAllEntities() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }

            if (!getLoggedInUser().getLco())
                aBoolean = aBoolean.and(qPartner.partnerType.ne(CommonConstants.PARTNER_TYPE_LCO));

            aBoolean = aBoolean.or(qPartner.id.in(1));
            // TODO: pass mvnoID manually 6/5/2025
            return IterableUtils.toList(entityRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
//        return entityRepository.findAll().stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff() || partner.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<Partner> getAllEntities(String type) {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }

            if (type.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_LCO));

            if (type.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_FRANCHISE))
                aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_FRANCHISE));

            aBoolean = aBoolean.or(qPartner.id.in(1));
            // TODO: pass mvnoID manually 6/5/2025
            return IterableUtils.toList(entityRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
//        return entityRepository.findAll().stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff() || partner.getMvnoId() == null).collect(Collectors.toList());
    }

    public List<Partner> getAllPartners() {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.findAll().stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff(null) || partner.getMvnoId() == null).collect(Collectors.toList());
    }

    public void deletePartner(Integer id) throws Exception {
        String SUBMODULE = MODULE + " [deletePartner()] ";
        try {

            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            BooleanExpression exp = qPartnerServiceAreaMapping.isNotNull();
            exp = exp.and(qPartnerServiceAreaMapping.partnerId.eq(id));
            List<PartnerServiceAreaMapping> partnerServiceAreaMapping = (List<PartnerServiceAreaMapping>) partnerServiceAreaMappingRepo.findAll(exp);
            partnerServiceAreaMappingRepo.deleteAll(partnerServiceAreaMapping);

            Partner partner = entityRepository.findById(id).orElse(null);
            partner.setIsDelete(true);
            entityRepository.save(partner);

            if (partner.getIsDelete().equals(true)) {
                List<StaffUser> staffUser = staffUserService.getActiveStaffUserFromUsername(partner.getEmail());
                if (staffUser != null) {
                    if (!staffUser.isEmpty()) staffUserService.deleteStaffUser(staffUser.get(0).getId());
                }

            }
            PartnerMessage partnerMessage = new PartnerMessage(partner.getId(), partner.getName(), partner.getStatus(), true);
            kafkaMessageSender.send(new KafkaMessageData(partnerMessage, PartnerMessage.class.getSimpleName()));
//            this.messageSender.send(partnerMessage, RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public Partner getPartnerForAdd() {
        return new Partner();
    }

    public Partner getPartnerForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

    public LocalDate getNextBillDateForNepaliCalendar(LocalDate nextBillDate) {
        LocalDate nextBillDateInEnglish = LocalDate.now();


        return nextBillDateInEnglish;
    }

    @Transactional
    public Partner savePartner(Partner partner) throws Exception {
        String SUBMODULE = MODULE + " [savePartner()] ";
        String operation = "edit";
        try {
            if (partner != null && partner.getId() == null) {
                operation = "add";

                LocalDateTime nextBilldate = LocalDate.now().atStartOfDay();
                if (partner.getCalendarType().equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
                    NepaliDateDTO nepaliDateDTO = dateConverterService.getNepaliDateFromEnglishDate(nextBilldate.getDayOfMonth() + "-" + nextBilldate.getMonthValue() + "-" + nextBilldate.getYear() + " " + nextBilldate.getHour() + ":" + nextBilldate.getMinute() + ":" + nextBilldate.getSecond());
                    int monthDay = dateConverterService.getDaysInMonth(nepaliDateDTO.getSaal(), nepaliDateDTO.getMahina());
                    if (partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                        nextBilldate = nextBilldate.plusDays(monthDay - nepaliDateDTO.getGatey());
                    else nextBilldate = nextBilldate.plusDays(monthDay);
                } else {
                    if (partner.getPartnerType().equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                        nextBilldate = LocalDate.now().withDayOfMonth(LocalDate.now().getMonth().length(LocalDate.now().isLeapYear())).atStartOfDay();
                    else nextBilldate = LocalDate.now().atStartOfDay().plusDays(30);
                }
                partner.setNextbilldate(LocalDate.from(nextBilldate));
                if (!partner.getEmail().isEmpty()) {
                    QPartner qPartner = QPartner.partner;
                    BooleanExpression booleanExpression = qPartner.isDelete.eq(false).and(qPartner.isNotNull()).and(qPartner.email.equalsIgnoreCase(partner.getEmail().replaceAll("\\s", "")));
                    Optional<Partner> partner1 = entityRepository.findOne(booleanExpression);
                    if (partner1.isPresent()) {
                        throw new CustomValidationException(APIConstants.FAIL, "Partner is already added with same email.", null);
                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, "Please enter valid email address.", null);
                }
            }
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) != null) {
                // TODO: pass mvnoID manually 6/5/2025
                partner.setMvnoId(getMvnoIdFromCurrentStaff(null));
            }

            if (operation.equalsIgnoreCase("edit")) {
                Optional<Partner> partner1 = entityRepository.findById(partner.getId());
                if (partner1.isPresent()) {
                    if (partner1.get().getBalance() < partner.getBalance())
                        partnerLedgerDetailsService.reverseBalance(null, 0.0, partner.getBalance() - partner1.get().getBalance(), partner1.get().getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
                    else partner.setBalance(partner1.get().getBalance());
                }
            }
            partner.setResetDate(getResetDate(partner.getCalendarType(), LocalDate.now()));
            Partner save = entityRepository.save(partner);
            if (operation.equalsIgnoreCase("add") && partner.getBalance() > 0) {
                partnerLedgerDetailsService.reverseBalance(null, 0.0, save.getBalance(), save.getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");
            }


            if (save != null) {
                if ("add".equals(operation)) {
                    // businessUnitService.createPartnerbusinessUnit(save);
//                    staffUserService.createPartnerUser(save);
                    CommunicationHelper communicationHelper = new CommunicationHelper();
                    Map<String, String> map = new HashMap<>();
                    map.put(CommunicationConstant.EMAIL, partner.getEmail());
                    map.put(CommunicationConstant.DESTINATION, partner.getMobile());
                    communicationHelper.generateCommunicationDetails(8L, Collections.singletonList(map));
                }
            }
//                else if ("edit".equalsIgnoreCase(operation)) {
//                    Integer partnerId=partner.getId();
//                    QStaffUser qStaffUser=QStaffUser.staffUser;
//                    BooleanExpression exp = qStaffUser.isNotNull();
//
//                    QStaffUserServiceAreaMapping qStaffUserServiceAreaMapping=QStaffUserServiceAreaMapping.staffUserServiceAreaMapping;
//                    BooleanExpression exp1=qStaffUserServiceAreaMapping.isNotNull();
//
//                    exp=exp.and(qStaffUser.partnerid.eq(partnerId));
//                    List<StaffUser> staff= (List<StaffUser>) staffUserRepository.findAll(exp);
//                    try{
//                        staff.get(0).setUsername(partner.getEmail());
//                        staff.get(0).setPassword(CommonUtils.generateBcryptPassword(partner.getEmail()));
//                        staff.get(0).setPartnerid(partner.getId());
//                        staff.get(0).setEmail(partner.getEmail());
//                        staff.get(0).setPhone(partner.getMobile());
//                        staff.get(0).setFirstname(partner.getName());
//                        staff.get(0).setIsDelete(partner.getIsDelete());
//                        staff.get(0).setLastname(partner.getName());
//                        staff.get(0).setStatus(CommonConstants.ACTIVE_STATUS);
//                        staff.get(0).setMvnoId(partner.getMvnoId());
//                        staffUserRepository.save(staff.get(0));
//                    } catch (Exception ex) {
//                        ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//                        throw ex;
//                    }
//
//                    Integer staffId=staff.get(0).getId();
//                    exp1=exp1.and(qStaffUserServiceAreaMapping.staffId.eq(staffId));
//                    List<StaffUserServiceAreaMapping> oldserviceAreaMappings= (List<StaffUserServiceAreaMapping>) staffUserServiceAreaMappingRepository.findAll(exp1);
//                    staffUserServiceAreaMappingRepository.deleteAll(oldserviceAreaMappings);
//                    //updating service area list in staffuser table
//                    if (partner.getServiceAreaList().size() > 0) {
//                        partner.getServiceAreaList().forEach(serviceArea -> {
//                            StaffUserServiceAreaMapping staffUserServiceAreaMapping = new StaffUserServiceAreaMapping();
//                            staffUserServiceAreaMapping.setStaffId(staff.get(0).getId());
//                            staffUserServiceAreaMapping.setServiceId(Math.toIntExact(serviceArea.getId()));
//                            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//                            staffUserServiceAreaMapping.setLastmodifiedOn(LocalDateTime.now());
//                            staffUserServiceAreaMapping.setCreatedById(staff.get(0).getId());
//                            staffUserServiceAreaMapping.setLastModifiedById(staff.get(0).getId());
//                            staffUserServiceAreaMapping.setCreatedOn(LocalDateTime.now());
//                            staffUserServiceAreaMapping.setCreatedByName(staff.get(0).getUsername());
//                            staffUserServiceAreaMappingRepository.save(staffUserServiceAreaMapping);
//                        });
//                    }
//
//                }
//            }
            return save;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

//    public PartnerPojo save(PartnerPojo pojo) throws Exception {
//        String SUBMODULE = MODULE + " [save()] ";
//        Partner oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
//        try {
//            pojo.setMvnoId(getMvnoIdFromCurrentStaff());
//
//
//        	List<Customers> custList = new ArrayList<Customers>();
//            if(pojo.getServiceAreaIds() != null && pojo.getServiceAreaIds().size() > 0) {
//            	if (getBUIdsFromCurrentStaff().size() == 0)
//            		custList = customersRepository.findByServiceAreaIdIn(pojo.getServiceAreaIds(), Arrays.asList(getMvnoIdFromCurrentStaff(), 1));
//                else
//                	custList = customersRepository.findByServiceAreaIdIn(pojo.getServiceAreaIds(),getMvnoIdFromCurrentStaff(), getBUIdsFromCurrentStaff());
//            }
//            Partner obj = convertPartnerPojoToPartnerModel(pojo);
//            if(getBUIdsFromCurrentStaff().size() == 1)
//                obj.setBuId(getBUIdsFromCurrentStaff().get(0));
//            obj = savePartner(obj);
//            if(custList != null && custList.size() > 0) {
//            	for (Customers customers : custList) {
//            		customers.setPartner(obj);
//				}
//            	customersRepository.saveAll(custList);
//            }
//            partnerLedgerService.setPartnerLedger(obj.getId());
//            log.info("Partner update details: " + UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//            pojo = convertPartnerModelToPartnerPojo(obj);

    /// /            createDataSharedService.updateEntityDataForAllMicroService(obj);
//            //send message
//            PartnerMessage partnerMessage = new PartnerMessage(pojo.getId(),pojo.getName(),pojo.getStatus(),pojo.getIsDelete());
//            this.messageSender.send(partnerMessage, RabbitMqConstants.QUEUE_APIGW_SEND_PARTNER);
//            return pojo;
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }

//    public Partner convertPartnerPojoToPartnerModel(PartnerPojo partnerPojo) throws Exception {
//        String SUBMODULE = MODULE + " [convertPartnerPojoToPartnerModel()] ";
//        Partner partner = null;
//        try {
//            if (partnerPojo != null) {
//                partner = new Partner();
//                if (partnerPojo.getId() != null) {
//                    partner.setId(partnerPojo.getId());
//                }
//
//                partner.setBalance(partnerPojo.getOutcomeBalance());
//                partner.setName(partnerPojo.getName());
//                partner.setStatus(partnerPojo.getStatus());
//                partner.setAddress1(partnerPojo.getAddress1());
//                partner.setAddress2(partnerPojo.getAddress2());
//                partner.setAddresstype(partnerPojo.getAddresstype());
//                partner.setCredit(partnerPojo.getCredit());
//                partner.setCity(partnerPojo.getCity());
//                partner.setState(partnerPojo.getState());
//                partner.setCountry(partnerPojo.getCountry());
//                partner.setPincode(partnerPojo.getPincode());
//                partner.setTaxid(partnerPojo.getTaxid());
//                partner.setCommdueday(partnerPojo.getCommdueday());
//                partner.setCommtype(partnerPojo.getCommissionShareType());
//                partner.setNextbilldate(partnerPojo.getNextbilldate());
//                partner.setEmail(partnerPojo.getEmail());
//                partner.setMobile(partnerPojo.getMobile());
//                partner.setCountryCode(partnerPojo.getCountryCode());
//                partner.setLastbilldate(partnerPojo.getLastbilldate());
//                partner.setIsDelete(partnerPojo.getIsDelete());
//                partner.setCalendarType(partnerPojo.getCalendarType());
//                partner.setPrcode(partnerPojo.getPrcode());
//                partner.setPartnerType(partnerPojo.getPartnerType());
//                partner.setPanName(partnerPojo.getPanName());
//                partner.setCname(partnerPojo.getCname());
//                partner.setCpName(partnerPojo.getCpName());
//                partner.setBranch(partnerPojo.getBranch());
//                partner.setBussinessvertical(partnerPojo.getBussinessvertical());
//                partner.setRegion(partnerPojo.getRegion());
//
//                if (partnerPojo.getMvnoId() != null) {
//                    partner.setMvnoId(partnerPojo.getMvnoId());
//                }
//                partner.setServiceAreaList(serviceAreaRepository.findAllById(partnerPojo.getServiceAreaIds()));
//                if (partnerPojo.getParentpartnerid() != null) {
//                    partner.setParentPartner(this.get(partnerPojo.getParentpartnerid()));
//                }
//                if (partnerPojo.getPricebookId() != null) {
//                    PriceBook priceBook = priceBookRepository.getOne(partnerPojo.getPricebookId());
//                    partner.setPriceBookId(priceBook);
//                }
//                partner.setCommrelvalue(0.0);
//                partner.setCommissionShareType(partnerPojo.getCommissionShareType());
//                partner.setTotalCustomerCount(partnerPojo.getTotalCustomerCount());
//                partner.setRenewCustomerCount(partnerPojo.getRenewCustomerCount());
//                partner.setNewCustomerCount(partnerPojo.getNewCustomerCount());
//            }
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//        return partner;
//    }
    public PartnerPojo convertPartnerModelToPartnerPojo(Partner partner) throws Exception {
        String SUBMODULE = MODULE + " [convertPartnerModelToPartnerPojo()] ";
        PartnerPojo pojo = null;
        try {
            if (partner != null) {
                pojo = new PartnerPojo();
                QPartnerPayment qPartnerPayment = QPartnerPayment.partnerPayment;
                List<PartnerPayment> partnerPayment = partnerPaymentRepository.findAllByPartner_Id(partner.getId());
                BooleanExpression booleanExpression = qPartnerPayment.isDeleted.eq(false);
                //  booleanExpression=booleanExpression.and(qPartnerPayment.status.eq());
                if (partner.getBalance() != null) {
                    pojo.setBalance(partner.getBalance());
                    pojo.setOutcomeBalance(partner.getBalance());
                } else {
                    pojo.setOutcomeBalance(0.0);
                    pojo.setBalance(0.0);
                }

                pojo.setCommrelvalue(partner.getCommrelvalue());
                pojo.setId(partner.getId());
                pojo.setName(partner.getName());
                pojo.setStatus(partner.getStatus());
                pojo.setAddress1(partner.getAddress1());
                pojo.setAddress2(partner.getAddress2());
                pojo.setAddresstype(partner.getAddresstype());
                pojo.setCredit(partner.getCredit());
                pojo.setCity(partner.getCity());
                pojo.setState(partner.getState());
                pojo.setCountry(partner.getCountry());
                pojo.setPincode(partner.getPincode());
                pojo.setTaxid(partner.getTaxid());
                pojo.setCommdueday(partner.getCommdueday());
                pojo.setCommtype(partner.getCommissionShareType());
                pojo.setNextbilldate(partner.getNextbilldate());
                pojo.setEmail(partner.getEmail());
                pojo.setMobile(partner.getMobile());
                pojo.setCountryCode(partner.getCountryCode());
                pojo.setLastbilldate(partner.getLastbilldate());
                pojo.setIsDelete(partner.getIsDelete());
                pojo.setCreatedById(partner.getCreatedById());
                pojo.setCreatedate(partner.getCreatedate());
                pojo.setCreatedByName(partner.getCreatedByName());
                pojo.setLastModifiedById(partner.getLastModifiedById());
                pojo.setLastModifiedByName(partner.getLastModifiedByName());
                pojo.setUpdatedate(partner.getUpdatedate());
                pojo.setCommissionShareType(partner.getCommissionShareType());
                pojo.setCalendarType(partner.getCalendarType());
                pojo.setTotalCustomerCount(partner.getTotalCustomerCount());
                pojo.setRenewCustomerCount(partner.getRenewCustomerCount());
                pojo.setNewCustomerCount(partner.getNewCustomerCount());
                pojo.setPrcode(partner.getPrcode());
                pojo.setPartnerType(partner.getPartnerType());
                pojo.setCname(partner.getCname());
                pojo.setCpName(partner.getCpName());
                pojo.setPanName(partner.getPanName());
                pojo.setCreditConsume(partner.getCreditConsume());
                pojo.setDisplayId(partner.getId());
                pojo.setDisplayName(partner.getName());
                pojo.setBranch(partner.getBranch());
                pojo.setRegion(partner.getRegion());
                pojo.setBussinessvertical(partner.getBussinessvertical());

                if (partner.getMvnoId() != null) {
                    pojo.setMvnoId(partner.getMvnoId());
                }
                if (partner.getPriceBookId() != null) {
                    Long priceBookId = partner.getPriceBookId().getId();
                    pojo.setPricebookId(priceBookId);
                    pojo.setPricebookname(partner.getPriceBookId().getBookname());
                }
                /*PartnerLedger partnerLedger = partnerLedgerService.getByPartnerId(partner.getId());
                if (partnerLedger != null) {
                    if (partnerLedger.getTotaldue() == null) {
                        partnerLedger.setTotaldue(0.0);
                    }
                    if(partner.getCommissionShareType().equalsIgnoreCase("balance")){
                        pojo.setBalance(Double.parseDouble(new DecimalFormat("##.##").format(partnerLedger.getTotaldue())));
                        pojo.setCommrelvalue(0.0);
                    }
                    if(partner.getCommissionShareType().equalsIgnoreCase("Revenue")){
                        pojo.setCommrelvalue(Double.parseDouble(new DecimalFormat("##.##").format(partnerLedger.getTotaldue())));
                        pojo.setBalance(0.00);
                    }
                }*/

                if (null != partner.getServiceAreaList() && 0 < partner.getServiceAreaList().size()) {
                    pojo.setServiceAreaIds(partner.getServiceAreaList().stream().map(ServiceArea::getId).collect(Collectors.toList()));
                    pojo.setServiceAreaNameList(partner.getServiceAreaList().stream().map(ServiceArea::getName).collect(Collectors.toList()));
                }

                if (partner.getParentPartner() != null) {
                    pojo.setParentpartnerid(partner.getParentPartner().getId());
                    pojo.setParentPartnerName(partner.getParentPartner().getName());
                } else pojo.setParentPartnerName("-");

                if (null != partner.getCity()) {
                    City city = cityService.get(partner.getCity(),partner.getMvnoId());
                    pojo.setCityName(null != city ? city.getName() : "-");
                } else pojo.setCityName("-");

                if (null != partner.getCountry()) {
                    Country country = countryService.get(partner.getCountry(),partner.getMvnoId());
                    pojo.setCountryName(null != country ? country.getName() : "-");
                } else pojo.setCountryName("-");


                if (null != partner.getState()) {
                    State state = stateService.get(partner.getState(),partner.getMvnoId());
                    pojo.setStateName(null != state ? state.getName() : "-");
                } else pojo.setStateName("-");

                if (null != partner.getTaxid()) {
                    Tax tax = taxService.get(partner.getTaxid(),pojo.getMvnoId());
                    pojo.setTaxName(null != tax ? tax.getName() : "-");
                } else {
                    pojo.setTaxName("-");
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojo;
    }

    public List<PartnerPojo> convertResponseModelIntoPojo(List<Partner> partnerList) throws Exception {
        String SUBMODULE = MODULE + " [convertResponseModelIntoPojo()] ";
        List<PartnerPojo> pojoListRes = new ArrayList<PartnerPojo>();
        try {
            if (partnerList != null && partnerList.size() > 0) {
                for (Partner partner : partnerList) {
                    pojoListRes.add(convertPartnerModelToPartnerPojo(partner));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return pojoListRes;
    }

    public void validateRequest(PartnerPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }

        if (pojo != null && operation.equals(CommonConstants.OPERATION_ADD)) {
            if (pojo.getId() != null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
            }
        }

        if (pojo != null && (operation.equals(CommonConstants.OPERATION_UPDATE) || operation.equals(CommonConstants.OPERATION_DELETE)) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }

        if (!(pojo.getStatus().equalsIgnoreCase(CommonConstants.ACTIVE_STATUS) || pojo.getStatus().equalsIgnoreCase(CommonConstants.INACTIVE_STATUS))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.status"), null);
        }
        if (!(pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PERCUST_FLAT) || pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PERCUST_PERCENTAGE) || pojo.getCommtype().equalsIgnoreCase(CommonConstants.PART_COMMTYPE_PRICEBOOK))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.partner.commission.types.error"), null);
        }
        if (!(pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PRESENT) || pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PERMANENT) || pojo.getAddresstype().equalsIgnoreCase(SubscriberConstants.CUST_ADDRESS_PAYMENT))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.address.type"), null);
        }
        if (operation.equals(CommonConstants.OPERATION_ADD) || operation.equals(CommonConstants.OPERATION_UPDATE)) {
            if (pojo.getCountry() != null && countryService.get(pojo.getCountry(),pojo.getMvnoId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.found"), null);
            }
            if (pojo.getState() != null && stateService.get(pojo.getState(),pojo.getMvnoId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
            }
            if (pojo.getCity() != null && cityService.get(pojo.getCity(), pojo.getMvnoId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
            }
            if (pojo.getTaxid() != null && taxService.get(pojo.getTaxid(),pojo.getMvnoId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.tax.not.found"), null);
            }
            if (pojo.getParentpartnerid() != null && get(pojo.getParentpartnerid(),pojo.getMvnoId()) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.parent.partner.not.found"), null);
            }
        }
    }

    public List<Partner> getAllParentPartners(Integer id) {
        // TODO: pass mvnoID manually 6/5/2025
        return entityRepository.getAllParentPartners(id).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff(null) || partner.getMvnoId() == null).collect(Collectors.toList());
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Partner");
        List<PartnerPojo> partnerPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createExcel(workbook, sheet, PartnerPojo.class, partnerPojoList, getFields());
    }

    private Field[] getFields() throws NoSuchFieldException {
        return new Field[]{PartnerPojo.class.getDeclaredField("id"), PartnerPojo.class.getDeclaredField("name"), PartnerPojo.class.getDeclaredField("email"), PartnerPojo.class.getDeclaredField("mobile"), PartnerPojo.class.getDeclaredField("status"), PartnerPojo.class.getDeclaredField("serviceAreaNameList"),};
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<PartnerPojo> partnerPojoList = convertResponseModelIntoPojo(entityRepository.findAll());
        createPDF(doc, PartnerPojo.class, partnerPojoList, getFields());
    }

    @Override
    public Page<Partner> search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId) {
        String SUBMODULE = MODULE + " [search()] ";
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", sortOrder);
        try {
            for (GenericSearchModel searchModel : filterList) {
                if (searchModel.getFilterColumn().trim().equalsIgnoreCase(SearchConstants.ANY)) {
                    return getPartnerByNameOrEmailOrMobile(searchModel.getFilterValue(), searchModel.getFilterValue(), searchModel.getFilterValue(), pageRequest);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return null;
    }

    public Page<Partner> getPartnerByNameOrEmailOrMobile(String s1, String s2, String s3, PageRequest pageRequest) {
        QPartner qPartner = QPartner.partner;
        QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
        JPAQuery<Partner> query = new JPAQuery<>(entityManager);
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
        if (!s1.isEmpty()) {
            booleanExpression = booleanExpression.and((qPartner.name.likeIgnoreCase("%" + s1 + "%").or(qPartner.mobile.eq("%" + s1 + "%").or(qPartner.email.like("%" + s1 + "%")))));
        }
//        if (getLoggedInUserId() != 1) {
////            List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
//
////         JPAQuery<Integer> partnerId= query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping)
////                          .where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs));
////         partnerId.j(partnerId.select(qPartner.id.eq(1)));
////            List<Integer> partnerId = partnerServiceAreaMappingRepo.partnerIdList(serviceIDs);
////            System.out.println("++++++++++++++++");
////            System.out.println(partnerId);
////            partnerId.add(1);
////
////            booleanExpression = booleanExpression.and(qPartner.id.in(partnerId));
//
//        }
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(getMvnoIdFromCurrentStaff(null), 1));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));
        return entityRepository.findAll(booleanExpression, pageRequest);
    }

    public List<AuditForResponseModel> getPartnerListForAuditFor() {
        String SUBMODULE = MODULE + " [getPartnerListForAuditFor()] ";
        List<AuditForResponseModel> responseList = new ArrayList<>();
        try {
            // TODO: pass mvnoID manually 6/5/2025
            List<Partner> partnerList = getAllActiveEntities().stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff(null) || partner.getMvnoId() == null).collect(Collectors.toList());
            if (null != partnerList && 0 < partnerList.size()) {
                for (Partner customers : partnerList) {
                    AuditForResponseModel responseModel = new AuditForResponseModel();
                    responseModel.setId(customers.getId());
                    responseModel.setName(customers.getName());
                    responseList.add(responseModel);
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return responseList;
    }

//    public List<PartnerPojo> searchPartner(String searchText) {
//        String SUBMODULE = MODULE + " [searchCustomersCustom()] ";
//        try {
//            List<Partner> partnerList = entityRepository.searchPartner(searchText, searchText, searchText).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff() || partner.getMvnoId() == null).collect(Collectors.toList());
//            if (null != partnerList && 0 < partnerList.size()) {
//                return partnerList.stream().map(data -> {
//                    try {
//                        return convertPartnerModelToPartnerPojo(data);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }).collect(Collectors.toList());
//            }
//            return new ArrayList<>();
//        } catch (Exception ex) {
//            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
//            throw ex;
//        }
//    }

    public boolean checkPartnerUniqueEmail(String email, Integer partnerId) {
        if (partnerId != null) {
            Partner partner = entityRepository.getOne(partnerId);
            if (partner != null) {
                if (!"".equals(partner.getEmail()) && email.equalsIgnoreCase(partner.getEmail())) {
                    return false;
                }
            }
        }
        // TODO: pass mvnoID manually 6/5/2025
        List<Partner> partnerList = entityRepository.findAllByEmailAndIsDeleteIsFalseOrderByIdDesc(email).stream().filter(partner -> partner.getMvnoId() == getMvnoIdFromCurrentStaff(null) || partner.getMvnoId() == null).collect(Collectors.toList());
        return null != partnerList && 0 < partnerList.size();
    }

    public PartnerPaymentDTO addPartnerBalance(PartnerAddBalancePojo partnerAddBalancePojo) throws Exception {
        String msg = "Balance Added Successfully";
        if (partnerAddBalancePojo.getRemark().length() > 250) {
            throw new Exception("Please enter remark less than 250 character");
        }
        PartnerPaymentDTO partnerPaymentDTO = new PartnerPaymentDTO();

        if (partnerAddBalancePojo.getBalance() != null && partnerAddBalancePojo.getBalance() <= 0)
            throw new Exception("Applied Balance can not be Negative and Zero!");

        if (partnerAddBalancePojo.getCredit() != null && partnerAddBalancePojo.getCredit() < 0)
            throw new Exception("Applied Credit can not be Negative!");

        if (partnerAddBalancePojo.getPartnerId() != null) {
            Optional<Partner> partner = entityRepository.findById(partnerAddBalancePojo.getPartnerId());
            if (partner.isPresent()) {
                if (partnerAddBalancePojo.getBalance() != null) {
                    PartnerLedgerBalanceDTO ledgerBalanceDTO = new PartnerLedgerBalanceDTO();
                    ledgerBalanceDTO.setAmount(partnerAddBalancePojo.getBalance());
                    ledgerBalanceDTO.setCredit(partnerAddBalancePojo.getCredit());
                    ledgerBalanceDTO.setPartner_id(partnerAddBalancePojo.getPartnerId());
                    ledgerBalanceDTO.setPaymentmode(partnerAddBalancePojo.getPaymentMode());
                    ledgerBalanceDTO.setDescription(partnerAddBalancePojo.getRemark());
                    ledgerBalanceDTO.setPaymentdate(LocalDate.now());
                    ledgerBalanceDTO.setRefno(partnerAddBalancePojo.getReferenceno());
                    ledgerBalanceDTO.setBank_name("Paytm");
                    if (partnerAddBalancePojo.getDestinationBank() != null) {
                        ledgerBalanceDTO.setDestinationBank(partnerAddBalancePojo.getDestinationBank());
                    }
                    if (partnerAddBalancePojo.getSourceBank() != null) {
                        ledgerBalanceDTO.setSourceBank(partnerAddBalancePojo.getSourceBank());
                    }
                    if (partnerAddBalancePojo.getOnlinesource() != null) {
                        ledgerBalanceDTO.setOnlinesource(partnerAddBalancePojo.getOnlinesource());
                    }
                    if (partnerAddBalancePojo.getChequedate() != null) {
                        ledgerBalanceDTO.setChequedate(partnerAddBalancePojo.getChequedate());
                    }
                    if (partnerAddBalancePojo.getChequeno() != null) {
                        ledgerBalanceDTO.setChequenumber(partnerAddBalancePojo.getChequeno());
                    }
                    partnerPaymentDTO = partnerPaymentService.addBalance(ledgerBalanceDTO);
                } else throw new Exception("Invalid Transfer Amount");
            } else throw new Exception("Partner does not found");
        } else throw new Exception("Invalid PartnerId");
        return partnerPaymentDTO;
    }

    public String addPartnerBalanceOnline(PartnerAddBalancePojo partnerAddBalancePojo, String orderId, String transId, String status) throws Exception {
        String msg = "Balance Added Successfully";
        if (partnerAddBalancePojo.getPartnerId() != null) {
            Optional<Partner> partner = entityRepository.findById(partnerAddBalancePojo.getPartnerId());
            if (partner.isPresent()) {
                if (partnerAddBalancePojo.getBalance() != null) {
                    if (partner.get().getCreditConsume() == 0) {
                        partner.get().setBalance(partner.get().getBalance() + partnerAddBalancePojo.getBalance());
                    } else if (partner.get().getCreditConsume() < (partner.get().getBalance() + partnerAddBalancePojo.getBalance())) {
                        partner.get().setBalance(partner.get().getBalance() + partnerAddBalancePojo.getBalance() - partner.get().getCreditConsume());
                        partner.get().setCreditConsume(0.0d);
                    } else if (partner.get().getCreditConsume() > (partner.get().getBalance() + partnerAddBalancePojo.getBalance())) {
                        partner.get().setBalance(0.0d);
                        partner.get().setCreditConsume(partner.get().getCreditConsume() - (partner.get().getBalance() + partnerAddBalancePojo.getBalance()));
                    }
                    partner.get().setCredit(partnerAddBalancePojo.getCredit() + partner.get().getCredit());
                    entityRepository.save(partner.get());
                    PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
                    dto.setPartner_id(partnerAddBalancePojo.getPartnerId());
                    dto.setAmount(partnerAddBalancePojo.getBalance());
                    dto.setPaymentdate(LocalDate.now());
                    partnerLedgerService.addBalance(dto);
                    partnerLedgerDetailsService.reverseBalance(null, partnerAddBalancePojo.getBalance(), 0.0, partner.get().getId(), CommonConstants.TRANS_CATEGORY_ADD_BALANCE, "Add Balance in Partner wallet");

                    QPartnerPayment partnerPayment = QPartnerPayment.partnerPayment;
                    BooleanExpression expression = partnerPayment.isNotNull();
                    expression = expression.and(partnerPayment.orderid.eq(orderId));
                    List<PartnerPayment> list = (List<PartnerPayment>) partnerPaymentRepository.findAll(expression);
                    if (list != null && list.get(0) != null) {
                        list.get(0).setAmount(partnerAddBalancePojo.getBalance());
                        list.get(0).setPaymentmode(partnerAddBalancePojo.getPaymentMode());
                        list.get(0).setRemarks(partnerAddBalancePojo.getRemark());
                        list.get(0).setPaymentdate(LocalDate.now());
                        list.get(0).setRefno(transId);
                        list.get(0).setPaymentstatus(status);
                        if (partnerAddBalancePojo.getPaymentMode().equalsIgnoreCase("Online"))
                            list.get(0).setBank_name("Paytm");
                        partnerPaymentRepository.save(list.get(0));
                    }
                } else throw new Exception("Invalid Transfer Amount");
            } else throw new Exception("Partner does not found");
        } else throw new Exception("Invalid PartnerId");
        return msg;
    }

    public String transferPartnerBalance(PartnerTransferBalancePojo partnerTransferBalancePojo) throws Exception {
        String msg = "Amount Transfer Successfully";
        if (partnerTransferBalancePojo.getPartnerId() != null) {
            Optional<Partner> partner = entityRepository.findById(partnerTransferBalancePojo.getPartnerId());
            if (partner.isPresent()) {
                if (partnerTransferBalancePojo.getAmount() != null && partnerTransferBalancePojo.getAmount() >= 0.0) {
                    if (partnerTransferBalancePojo.getTransferFrom().equalsIgnoreCase(CommonConstants.TRANSFER_FROM_BALANCE)) {
                        if (partnerTransferBalancePojo.getAmount() <= partner.get().getBalance()) {
//                            partner.get().setBalance(partner.get().getBalance() - partnerTransferBalancePojo.getAmount());
//                            if (partner.get().getCommrelvalue() != null)
//                                partner.get().setCommrelvalue(partner.get().getCommrelvalue() + partnerTransferBalancePojo.getAmount());
//                            else partner.get().setCommrelvalue(partnerTransferBalancePojo.getAmount());
//                            entityRepository.save(partner.get());
//
//                            PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
//                            dto.setPartner_id(partnerTransferBalancePojo.getPartnerId());
//                            dto.setPaymentdate(LocalDate.now());
//                            dto.setAmount(partnerTransferBalancePojo.getAmount());
//                            partnerLedgerService.addBalance(dto);
                            PartnerPayment partnerPayment = new PartnerPayment();
                            partnerPayment.setNextStaff(getLoggedInUserId());
                            partnerPayment.setPartner(partner.get());
                            partnerPayment.setRefno(genRefno().toString());
                            partnerPayment.setPaymentdate(LocalDate.now());
                            partnerPayment.setTranscategory(CommonConstants.BALANCE_TRANSFER);
                            partnerPayment.setPaymentmode("Online");
                            partnerPayment.setStatus("NewActivation");
                            partnerPayment.setRemarks(partnerTransferBalancePojo.getRemarks());
                            partnerPayment.setAmount(partnerTransferBalancePojo.getAmount());
                            partnerPayment.setDeleteFlag(false);
                            partnerPaymentRepository.save(partnerPayment);
                            //partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerTransferBalancePojo.getAmount(), partner.get().getId(), CommonConstants.BALANCE_TRANSFER, "Add Balance in Partner Revenue Commission");
                            //partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerTransferBalancePojo.getAmount(), partner.get().getId(), CommonConstants.BALANCE_TRANSFER, "Deduct Balance From Partner Wallet");
                        } else throw new Exception("Transfer amount can not be more than current Balance Amount");
                    } else if (partnerTransferBalancePojo.getTransferFrom().equalsIgnoreCase(CommonConstants.TRANSFER_FROM_COMMISSION)) {
                        if (partnerTransferBalancePojo.getAmount() <= partner.get().getCommrelvalue()) {
//                            partner.get().setCommrelvalue(partner.get().getCommrelvalue() - partnerTransferBalancePojo.getAmount());
//                            partner.get().setBalance(partner.get().getBalance() + partnerTransferBalancePojo.getAmount());
//                            entityRepository.save(partner.get());
//                            PartnerLedgerBalanceDTO dto = new PartnerLedgerBalanceDTO();
//                            dto.setPartner_id(partnerTransferBalancePojo.getPartnerId());
//                            dto.setPaymentdate(LocalDate.now());
//                            dto.setAmount(partnerTransferBalancePojo.getAmount());
                            PartnerPayment partnerPayment = new PartnerPayment();
                            partnerPayment.setNextStaff(getLoggedInUserId());
                            partnerPayment.setPartner(partner.get());
                            partnerPayment.setRefno(genRefno().toString());
                            partnerPayment.setPaymentdate(LocalDate.now());
                            partnerPayment.setPaymentmode("Online");
                            partnerPayment.setStatus("NewActivation");
                            partnerPayment.setTranscategory(CommonConstants.COMMISSION_TRANSFER);
                            partnerPayment.setRemarks(partnerTransferBalancePojo.getRemarks());
                            partnerPayment.setAmount(partnerTransferBalancePojo.getAmount());
                            partnerPayment.setDeleteFlag(false);
                            partnerPaymentRepository.save(partnerPayment);
                            //partnerLedgerService.addBalance(dto);
                            //partnerLedgerDetailsService.reverseBalance(null, 0.0, partnerTransferBalancePojo.getAmount(), partner.get().getId(), CommonConstants.COMMISSION_TRANSFER, "Add Balance in Partner Wallet");
                            //partnerLedgerDetailsService.reverseBalance(null, 0.0, -partnerTransferBalancePojo.getAmount(), partner.get().getId(), CommonConstants.COMMISSION_TRANSFER, "Deduct Amount From Partner Revenue Commission");
                        } else throw new Exception("Transfer amount can not be more than current Commission Amount");
                    }
                } else throw new Exception("Invalid Transfer Amount");
            } else throw new Exception("Partner does not found");
        } else throw new Exception("Invalid PartnerId");
        return msg;
    }

    public String withdrawPartnerCommission(WithdrawCommissionDto withdrawCommissionDto) throws Exception {
        String msg = "Commission Withdraw Successfully";
        if (withdrawCommissionDto.getPartnerId() != null) {
            double commission_value = 0.00;
            Optional<Partner> partner = entityRepository.findById(withdrawCommissionDto.getPartnerId());
            if (partner.isPresent()) {
                if (withdrawCommissionDto.getWithdrawAmount() != null) {
                    if (partner.get().getCommrelvalue() > 0 && partner.get().getCommrelvalue() >= withdrawCommissionDto.getWithdrawAmount()) {
                        // partner.get().setCommrelvalue(partner.get().getCommrelvalue() - withdrawCommissionDto.getWithdrawAmount());
                        //entityRepository.save(partner.get());
                        QPartnerLedger qPartnerLedger = QPartnerLedger.partnerLedger;
                        BooleanExpression expression = qPartnerLedger.isNotNull();
                        expression = expression.and(qPartnerLedger.partner.id.eq(partner.get().getId()));
//                        Optional<PartnerLedger> partnerLedger = partnerLedgerRepository.findOne(expression);
//                        if (partnerLedger.isPresent()) {
//                            partnerLedger.get().setTotaldue(partnerLedger.get().getTotaldue() - withdrawCommissionDto.getWithdrawAmount());
//                            partnerLedger.get().setTotalpaid(partnerLedger.get().getTotalpaid() + withdrawCommissionDto.getWithdrawAmount());
//                            partnerLedger.get().setUpdatedate(LocalDate.now());
//                            partnerLedgerRepository.save(partnerLedger.get());
//                        }

                        //   partnerLedgerDetailsService.reverseBalance(null,withdrawCommissionDto.getWithdrawAmount(),0.0, partner.get().getId(), CommonConstants.WITHDRAW_COMMISSION, "Withdraw Commission");
                        PartnerPaymentDTO partnerPaymentDTOSaved = new PartnerPaymentDTO();
                        PartnerLedgerBalanceDTO ledgerBalanceDTO = new PartnerLedgerBalanceDTO();
                        ledgerBalanceDTO.setAmount(withdrawCommissionDto.getWithdrawAmount());
                        ledgerBalanceDTO.setPartner_id(withdrawCommissionDto.getPartnerId());
                        ledgerBalanceDTO.setPaymentmode(withdrawCommissionDto.getPaymentMode());
                        ledgerBalanceDTO.setDescription(withdrawCommissionDto.getRemark());
                        ledgerBalanceDTO.setPaymentdate(withdrawCommissionDto.getPaymentDate());
                        ledgerBalanceDTO.setRefno(withdrawCommissionDto.getRemark());
                        if (withdrawCommissionDto.getPaymentMode().equalsIgnoreCase("Online")) {
                            ledgerBalanceDTO.setBank_name(withdrawCommissionDto.getBank());
                            ledgerBalanceDTO.setBranch_name(withdrawCommissionDto.getBranch());
                        }
                        PartnerPayment partnerPayment = new PartnerPayment();
                        PartnerPaymentDTO partnerPaymentDTO = new PartnerPaymentDTO();
                        partnerPaymentDTO.setAmount(Double.parseDouble(new DecimalFormat("##.##").format(ledgerBalanceDTO.getAmount())));
                        partnerPaymentDTO.setPartnerId(ledgerBalanceDTO.getPartner_id());
                        partnerPaymentDTO.setPaymentmode(ledgerBalanceDTO.getPaymentmode());
                        if (partnerPaymentDTO.getPaymentmode().equalsIgnoreCase(CommonConstants.PAYMENT_MODE_TYPE_CHEQUE)) {
                            partnerPaymentDTO.setChequenumber(ledgerBalanceDTO.getChequenumber());
                            partnerPaymentDTO.setChequedate(ledgerBalanceDTO.getChequedate());
                            partnerPaymentDTO.setBank_name(ledgerBalanceDTO.getBank_name());
                            partnerPaymentDTO.setBranch_name(ledgerBalanceDTO.getBranch_name());
                        }
                        if (ledgerBalanceDTO.getPaymentdate() != null) {
                            partnerPaymentDTO.setPaymentdate(ledgerBalanceDTO.getPaymentdate());
                        }
                        partnerPaymentDTO.setRefno(ledgerBalanceDTO.getRefno());
                        partnerPaymentDTO.setCredit(ledgerBalanceDTO.getCredit());
                        partnerPaymentDTO.setRemarks(ledgerBalanceDTO.getDescription());
                        partnerPaymentDTO.setStatus("NewActivation");
                        partnerPayment = partnerPaymentMapper.dtoToDomain(partnerPaymentDTO, new CycleAvoidingMappingContext());
                        if (ledgerBalanceDTO.getCredit() != null && ledgerBalanceDTO.getCredit() > 0 && ledgerBalanceDTO.getAmount().doubleValue() == 0.0d) {

                            PartnerPayment partnerPayment1 = new PartnerPayment();
                            partnerPayment1.setTranscategory(CommonConstants.TRANS_CATEGORY_ADD_CREDIT);
                            partnerPayment1.setAmount(ledgerBalanceDTO.getCredit().doubleValue());
                            partnerPayment1.setPartner(partnerPayment.getPartner());
                            partnerPayment1.setPaymentdate(partnerPayment.getPaymentdate());
                            partnerPayment1.setNextStaff(partnerPayment.getNextStaff());
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
                            partnerPaymentDTOSaved = partnerPaymentMapper.domainToDTO(partnerPaymentRepository.save(partnerPayment1), new CycleAvoidingMappingContext());
                        }
                        partnerPayment.setNextStaff(getLoggedInUserId());
                        if (partnerPaymentDTO.getAmount() > 0) {
                            partnerPayment.setTranscategory(CommonConstants.WITHDRAW_COMMISSION);
                            partnerPaymentDTOSaved = partnerPaymentMapper.domainToDTO(partnerPaymentRepository.save(partnerPayment), new CycleAvoidingMappingContext());
                            // assignpayment(partnerPaymentDTOSaved,partner);
                        }
                        // partnerPaymentService.addBalance(ledgerBalanceDTO);
                    } else throw new Exception("Invalid Withdraw Commission Amount");
                } else throw new Exception("Invalid Withdraw Commission Amount");
            } else throw new Exception("Partner does not found");
        } else throw new Exception("Invalid PartnerId");
        return msg;
    }

    public void addBalance(String orderId, Boolean status, String txnAmount, String txnId) throws Exception {
        Integer partnerId;
        QPartnerPayment partnerPayment = QPartnerPayment.partnerPayment;
        BooleanExpression expression = partnerPayment.isNotNull();
        expression = expression.and(partnerPayment.orderid.eq(orderId));
        List<PartnerPayment> list = (List<PartnerPayment>) partnerPaymentRepository.findAll(expression);
        if (list != null && list.get(0) != null) partnerId = list.get(0).getPartner().getId();
        else throw new Exception("Partner does not exist with given OrderId");
        if (status) {
            PartnerAddBalancePojo addBalancePojo = new PartnerAddBalancePojo();
            addBalancePojo.setBalance(Double.parseDouble(txnAmount));
            addBalancePojo.setPartnerId(partnerId);
            addBalancePojo.setPaymentMode("Online");
            addBalancePojo.setRemark("Payment processed through PayTM");
            addPartnerBalanceOnline(addBalancePojo, orderId, txnAmount, "Success");
        } else {
            list.get(0).setPaymentstatus("Failed");
            list.get(0).setAmount(Double.parseDouble(txnAmount));
            list.get(0).setRefno(txnId);
            partnerPaymentRepository.save(list.get(0));
        }
    }

    @Override
    public boolean duplicateVerifyAtSave(String name) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count == 0) {
                flag = true;
            }
        }
        return flag;
    }

    public boolean isSameStaff(String name) throws Exception {
        boolean flag = true;
        Integer userId = getLoggedInUserId();
        // TODO: pass mvnoID manually 6/5/2025
        Integer mvnoId = getMvnoIdFromCurrentStaff(null);
        if (name != null) {
            name = name.trim();
            Integer createdById;
            if (getBUIdsFromCurrentStaff().size() == 0) createdById = entityRepository.getCreatedBy(name, mvnoId);
            else createdById = entityRepository.getCreatedBy(name, mvnoId, getBUIdsFromCurrentStaff());
            if (createdById != userId) {
                flag = false;
            }
        }
        return flag;
    }

    @Override
    public boolean duplicateVerifyAtEdit(String name, Integer id) throws Exception {
        boolean flag = false;
        if (name != null) {
            name = name.trim();
            Integer count;
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(null) == 1) count = entityRepository.duplicateVerifyAtSave(name);
            else {
                if (getBUIdsFromCurrentStaff().size() == 0)
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                else
                    // TODO: pass mvnoID manually 6/5/2025
                    count = entityRepository.duplicateVerifyAtSave(name, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
            }
            if (count >= 1) {
                Integer countEdit;
                // TODO: pass mvnoID manually 6/5/2025
                if (getMvnoIdFromCurrentStaff(null) == 1) countEdit = entityRepository.duplicateVerifyAtEdit(name, id);
                else {
                    if (getBUIdsFromCurrentStaff().size() == 0)
                        // TODO: pass mvnoID manually 6/5/2025
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, Arrays.asList(getMvnoIdFromCurrentStaff(null), 1));
                    else
                        countEdit = entityRepository.duplicateVerifyAtEdit(name, id, getMvnoIdFromCurrentStaff(null), getBUIdsFromCurrentStaff());
                }
                if (countEdit == 1) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    @Override
    public Partner get(Integer id,Integer mvnoId) {
        String cacheKey = cacheKeys.PARTNER + id; // Unique cache key
        Partner partner = null;

        try {
            partner = (Partner) cacheService.getFromCache(cacheKey, Partner.class);

            if (partner != null) {
                // TODO: pass mvnoID manually 6/5/2025
                if (getBUIdsFromCurrentStaff() != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (mvnoId == 1 || (partner.getMvnoId() == mvnoId.intValue() || partner.getMvnoId() == 1) && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)) {
                        return partner;
                    }
                } else {
                    return partner;
                }
                return null;
            }

            partner = super.get(id,mvnoId);

            if (partner != null) {
                // TODO: pass mvnoID manually 6/5/2025
                if (getBUIdsFromCurrentStaff() != null && getMvnoIdFromCurrentStaff(null) != null) {
                    // TODO: pass mvnoID manually 6/5/2025
                    if (getMvnoIdFromCurrentStaff(null) == 1 || (partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() || partner.getMvnoId() == 1) && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)) {
                        cacheService.putInCache(cacheKey, partner);
                        return partner;
                    }
                } else {
                    cacheService.putInCache(cacheKey, partner);
                    return partner;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Partner getEntityForUpdateAndDelete(Integer id ,Integer mvnoId) {
        Partner partner = get(id,mvnoId);
        // TODO: pass mvnoID manually 6/5/2025
        if (partner == null || !(getMvnoIdFromCurrentStaff(null) == 1 || getMvnoIdFromCurrentStaff(null).intValue() == partner.getMvnoId().intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()))))
            throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
        return partner;
    }

    public List<Partner> getPartnerByServiceAreaId(Integer serviceAreaId,Integer mvnoid) {
        QPartner qPartner = QPartner.partner;
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
//        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = new ArrayList<Integer>();
            serviceIDs.add(serviceAreaId);
            List<Integer> partnerId = partnerServiceAreaMappingRepo.partnerIdList(serviceIDs);
            booleanExpression = booleanExpression.and(qPartner.id.in(partnerId));
//        }
        // TODO: pass mvnoID manually 6/5/2025
        List<Integer> mvnoIds = new ArrayList<Integer>();
        mvnoIds.add(1);
        if (mvnoid != 1)
            mvnoIds.add(mvnoid);
            // TODO: pass mvnoID manually 6/5/2025
        booleanExpression = booleanExpression.and(qPartner.mvnoId.in(mvnoIds));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(mvnoid).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));

        List<Partner> list = (List<Partner>) entityRepository.findAll(booleanExpression);
        list.add(entityRepository.findById(1).get());
        return list;
    }

    public List<Partner> getPartnersByServiceAreaId(List<Integer> serviceAreaId,Integer mvnoId) {
        QPartner qPartner = QPartner.partner;
        BooleanExpression booleanExpression = qPartner.isNotNull().and(qPartner.isDelete.eq(false));
        if (getLoggedInUserId() != 1) {
            List<Integer> serviceIDs = new ArrayList<Integer>();
            serviceIDs.addAll(serviceAreaId);
            List<Integer> partnerId = partnerServiceAreaMappingRepo.partnerIdList(serviceIDs);
            booleanExpression = booleanExpression.and(qPartner.id.in(partnerId));
            booleanExpression = booleanExpression.and(qPartner.status.equalsIgnoreCase("ACTIVE"));
        }
        // TODO: pass mvnoID manually 6/5/2025
        if (mvnoId != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartner.mvnoId.in(mvnoId, 1));
        if (getBUIdsFromCurrentStaff().size() != 0)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qPartner.mvnoId.eq(1).or(qPartner.mvnoId.eq(mvnoId).and(qPartner.buId.in(getBUIdsFromCurrentStaff()))));

        List<Partner> list = (List<Partner>) entityRepository.findAll(booleanExpression);
        list.add(entityRepository.findById(1).get());
        return list;
    }

    public LocalDate getResetDate(String calendarType, LocalDate currentDate) {
        LocalDate resetDate = currentDate.withDayOfMonth(currentDate.getMonth().length(currentDate.isLeapYear()));
        if (calendarType.equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
            LocalDateTime date = LocalDateTime.now();
            String currentDateAndTime = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
            NepaliDateDTO resetDateDTO = dateConverterService.getNepaliDateFromEnglishDate(currentDateAndTime);
            int monthVal = dateConverterService.getDaysInMonth(resetDateDTO.getSaal(), resetDateDTO.getMahina());
            resetDate = resetDate.plusDays(monthVal - currentDate.getMonth().length(currentDate.isLeapYear()));
        }
        return resetDate;
    }

    public LocalDate getNextbillDate(String calendarType, LocalDate nextBillDate) {

        if (calendarType.equalsIgnoreCase(CommonConstants.CAL_TYPE_NEPALI)) {
            LocalDateTime date = LocalDateTime.now();
            String currentDateAndTime = date.getDayOfMonth() + "-" + date.getMonthValue() + "-" + date.getYear() + " " + date.getHour() + ":" + date.getMinute() + ":" + date.getSecond();
            NepaliDateDTO nepaliDateDTO = dateConverterService.getNepaliDateFromEnglishDate(currentDateAndTime);
            EnglishDateDTO englishEndDateDTO = dateConverterService.getEnglishDateDTOFromNepaliDate(nepaliDateDTO.toString());
            nextBillDate = LocalDate.of(englishEndDateDTO.getYear(), englishEndDateDTO.getMonth(), englishEndDateDTO.getDate());
        }

        return nextBillDate;
    }

    @Transient
    public void createInvoiceFunctionForPartner(PartnerPojo pojo) {
        try {

//            String queryForSequence = "CREATE TABLE `sequence_"+pojo.getId()+"` (" +
//                    "    `name` varchar(100) NOT NULL," +
//                    "    `increment` int(11) NOT NULL DEFAULT 1," +
//                    "    `min_value` int(11) NOT NULL DEFAULT 1," +
//                    "    `max_value` bigint(20) NOT NULL DEFAULT 9223372036854775807," +
//                    "    `cur_value` bigint(20) DEFAULT 1," +
//                    "    `cycle` boolean NOT NULL DEFAULT FALSE," +
//                    "    PRIMARY KEY (`name`)" +
//                    ");";

            String queryForInsertSequence = "INSERT INTO sequence" + "    ( name, increment, min_value, max_value, cur_value ) " + "VALUES " + "    ('invoiceno_" + pojo.getId() + "', 1, 1,9999999,1);";

            String queryForInvoiceNo = "CREATE FUNCTION `nextval_" + pojo.getId() + "` (`seq_name` varchar(100))" + "RETURNS bigint " + "BEGIN" + "    DECLARE cur_val bigint;" + "    SELECT" + "        cur_value INTO cur_val" + "    FROM" + "        sequence" + "    WHERE" + "        name = seq_name;" + "    IF cur_val IS NOT NULL THEN" + "        UPDATE" + "            sequence" + "        SET" + "            cur_value = IF (\n" + "                (cur_value + increment) > max_value OR (cur_value + increment) < min_value," + "                IF (" + "                    cycle = TRUE," + "                    IF (" + "                        (cur_value + increment) > max_value," + "                        min_value, " + "                        max_value " + "                    )," + "                    NULL" + "                )," + "                cur_value + increment" + "            )" + "        WHERE" + "            name = seq_name;" + "    END IF; " + "    RETURN cur_val;" + "END;";

//            jdbcTemplate.execute(queryForSequence);
            jdbcTemplate.execute(queryForInvoiceNo);
            jdbcTemplate.execute(queryForInsertSequence);
        } catch (Exception ex) {
            System.out.println("Error to create Partner Invoice no function" + ex.getMessage());
        }
    }


    public List<PartnerCreditDocument> getByLcoId(Integer partnerId) {

        List<PartnerCreditDocument> partnerCreditDocuments = partnerCreditDocumentRepository.getAllByLcoidAndPaytypeNotIgnoreCaseAndTypeNotIgnoreCaseOrderByIdDesc(partnerId, "CREDITNOTE", "creditnote");

        //setting invoice number
        for (int i = 0; i < partnerCreditDocuments.size(); i++) {
            QPartnerDebitDocument qPartnerDebitDocument = QPartnerDebitDocument.partnerDebitDocument;
            BooleanExpression booleanExpression = qPartnerDebitDocument.isDelete.eq(false).and(qPartnerDebitDocument.id.in(partnerCreditDocuments.get(i).getInvoiceId()));
            PartnerDebitDocument partnerDebitDocument = partnerCreditDocRepository.findOne(booleanExpression).get();
            partnerCreditDocuments.get(i).setInvoiceNumber(partnerDebitDocument.getDocnumber());
        }

        // List<PaymentHistoryDTO> paymentHistories = partnerCreditDocuments.stream().map(data -> creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return partnerCreditDocuments;
    }

    public List<PartnerDebitDocument> getByPartnerId(Integer partnerId, Integer mvnoId) {
        Partner partners = partnerService.get(partnerId,mvnoId);
        List<PartnerDebitDocument> partnerCreditDocuments = partnerCreditDocRepository.getAllByPartner(partners);
        // List<PaymentHistoryDTO> paymentHistories = partnerCreditDocuments.stream().map(data -> creditDocumentMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return partnerCreditDocuments;
    }

    public Integer genRefno() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

    public List<Partner> getAllTypePartner() {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));
            if (super.getLoggedInUserId() != 1) {
                List<Integer> serviceIDs = super.getServiceAreaIdList().stream().map(Long::intValue).collect(Collectors.toList());
                aBoolean = aBoolean.and(qPartner.id.in(query.select(qPartnerServiceAreaMapping.partnerId).from(qPartnerServiceAreaMapping).where(qPartnerServiceAreaMapping.serviceId.in(serviceIDs))));
            }
            aBoolean = aBoolean.or(qPartner.id.in(1));
            // TODO: pass mvnoID manually 6/5/2025
            return IterableUtils.toList(entityRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public List<Partner> getAllPartnersByPartnerType(String partnertype) {
        try {
            QPartner qPartner = QPartner.partner;
            QPartnerServiceAreaMapping qPartnerServiceAreaMapping = QPartnerServiceAreaMapping.partnerServiceAreaMapping;
            JPAQuery<?> query = new JPAQuery<>(entityManager);
            BooleanExpression aBoolean = qPartner.isNotNull().and(qPartner.isDelete.eq(false)).and(qPartner.status.eq(CommonConstants.ACTIVE_STATUS));

            if (partnertype.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_LCO))
                aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_LCO));

            if (partnertype.equalsIgnoreCase(CommonConstants.PARTNER_TYPE_FRANCHISE))
                aBoolean = aBoolean.and(qPartner.partnerType.eq(CommonConstants.PARTNER_TYPE_FRANCHISE));

            aBoolean = aBoolean.or(qPartner.id.in(1));
            // TODO: pass mvnoID manually 6/5/2025
            return IterableUtils.toList(entityRepository.findAll(aBoolean)).stream().filter(partner -> partner.getMvnoId() == 1 || getMvnoIdFromCurrentStaff(null) == 1 || partner.getMvnoId() == getMvnoIdFromCurrentStaff(null).intValue() && (partner.getMvnoId() == 1 || getBUIdsFromCurrentStaff().size() == 0 || getBUIdsFromCurrentStaff().contains(partner.getBuId()) || partner.getId() == 1)).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

//    public void sendCreateDataShared(Integer id, PartnerPojo pojo, Integer operation) throws Exception {
//        try {
//            Partner partnerEntity = convertPartnerPojoToPartnerModel(pojo);
//            partnerEntity.setCreatedById(getLoggedInUserId());
//            partnerEntity.setLastModifiedById(getLoggedInUserId());
//            if (operation.equals(CommonConstants.OPERATION_ADD)) {
//                createDataSharedService.sendEntitySaveDataForAllMicroService(partnerEntity);
//            } else if (operation.equals(CommonConstants.OPERATION_UPDATE)) {
//                createDataSharedService.updateEntityDataForAllMicroService(partnerEntity);
//            } else if (operation.equals(CommonConstants.OPERATION_DELETE)) {
//                Partner deletePartnerEntity = getEntityForUpdateAndDelete(id);
//                createDataSharedService.deleteEntityDataForAllMicroService(deletePartnerEntity);
//            }
//        } catch (CustomValidationException e) {
//            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), e.getMessage(), null);
//        }
//    }

    public void updateAmount(PartnerAmountMessage message) {
        Partner partner = entityRepository.findById(message.getPartnerId()).orElse(null);
        if (partner != null) {
            partner.setCreditConsume(message.getCreditconsume());
            partner.setCommrelvalue(message.getComrelval());
            partner.setBalance(message.getBalance());
            partner.setCredit(message.getCredit());
            if (message.getRenewcust_count() != null) {
                partner.setRenewCustomerCount(message.getRenewcust_count().longValue());
            }
            if (message.getNewCustomer_count() != null) {
                partner.setNewCustomerCount(message.getNewCustomer_count().longValue());
            }
            partner = entityRepository.save(partner);
            String cacheKey = cacheKeys.PARTNER + partner.getId();
            cacheService.putInCache(cacheKey, partner);

        }
    }


    public void savePartnerEntiry(SavePartnerSharedDataMessage message) throws Exception {
        try {
            Partner partner = new Partner();
            partner.setId(message.getId());
            partner.setName(message.getName());
            partner.setStatus(message.getStatus());
            partner.setCity(message.getCity());
            partner.setCountry(message.getCountry());
            partner.setState(message.getState());
            partner.setPincode(message.getPincode());
            partner.setEmail(message.getEmail());
            partner.setPartnerType(message.getPartnerType());
            if (message.getParentPartnerId() != null) {
                Partner parentPartner = entityRepository.findById(message.getParentPartnerId()).orElse(null);
                partner.setParentPartner(parentPartner);
            }
            partner.setServiceAreaList(message.getServiceAreaList());
            partner.setIsDelete(message.getIsDelete());
            partner.setCreatedById(message.getCreatedById());
            partner.setLastModifiedById(message.getLastModifiedById());
            partner.setBuId(message.getBuId());
            partner.setMvnoId(message.getMvnoId());
            partner.setBranch(message.getBranch());
            partner.setMobile(message.getMobile());
            partner.setTaxid(message.getTaxid());
            partner.setBalance(message.getBalance());
            partner.setCommrelvalue(message.getCommrelvalue());
            partner.setCreditConsume(message.getCreditConsume());
            partner.setCredit(message.getCredit());
            partner.setCommissionShareType(message.getCommissionShareType());
            partner.setCommtype(message.getCommtype());
            partner.setIsVisibleToIsp(message.getIsVisibleToIsp());
            if (message.getPriceBookId() != null) {
                PriceBook priceBook = priceBookRepository.findById(message.getPriceBookId()).orElse(null);
                if (priceBook != null) partner.setPriceBookId(priceBook);
            }

            Partner savedPartner = entityRepository.save(partner);
            if (savedPartner.getPartnerType().equals(CommonConstants.PARTNER_TYPE_LCO))
                numberSequenceUtil.createSequenceNumberFunctionForPartner(savedPartner);
            ApplicationLogger.logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create partner with name " + message.getName(), e.getMessage());
        }
    }

    public void updatePartnerData(UpdatePartnerSharedDataMessage message) {

        try {
            Partner partner = entityRepository.findById(message.getId()).orElse(null);
            if (partner != null && message.getIsDelete().equals(true)) {
                try {
                    deletePartner(partner.getId());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                partner.setName(message.getName());
                partner.setStatus(message.getStatus());
                partner.setCity(message.getCity());
                partner.setCountry(message.getCountry());
                partner.setState(message.getState());
                partner.setPincode(message.getPincode());
                partner.setEmail(message.getEmail());
                partner.setPartnerType(message.getPartnerType());
                if (message.getParentPartnerId() != null) {
                    Partner parentPartner = entityRepository.findById(message.getParentPartnerId()).orElse(null);
                    partner.setParentPartner(parentPartner);
                }
                partner.setServiceAreaList(message.getServiceAreaList());
                partner.setIsDelete(message.getIsDelete());
                partner.setCreatedById(message.getCreatedById());
                partner.setLastModifiedById(message.getLastModifiedById());
                partner.setBuId(message.getBuId());
                partner.setMvnoId(message.getMvnoId());
                partner.setBranch(message.getBranch());
                partner.setMobile(message.getMobile());
                partner.setTaxid(message.getTaxid());
                partner.setBalance(message.getBalance());
                partner.setCommrelvalue(message.getCommrelvalue());
                partner.setCreditConsume(message.getCreditConsume());
                partner.setCredit(message.getCredit());
                partner.setCommissionShareType(message.getCommissionShareType());
                partner.setCommtype(message.getCommtype());
                if (message.getPriceBookId() != null) {
                    PriceBook priceBook = priceBookRepository.findById(message.getPriceBookId()).orElse(null);
                    if (priceBook != null) partner.setPriceBookId(priceBook);
                }
                partner.setIsVisibleToIsp(message.getIsVisibleToIsp());
                Partner savedPartner = entityRepository.save(partner);

                List<Customers> custList = new ArrayList<Customers>();
                if (message.getServiceAreaIds() != null && message.getServiceAreaIds().size() > 0) {
                    if (message.getBuId() == null)
                        custList = customersRepository.findByServiceAreaIdIn(message.getServiceAreaIds(), Arrays.asList(message.getMvnoId(), 1));
                    else
                        custList = customersRepository.findByServiceAreaIdIn(message.getServiceAreaIds(), message.getMvnoId(), Collections.singletonList(message.getBuId()));

                    if (custList != null && custList.size() > 0) {
                        for (Customers customers : custList)
                            customers.setPartner(savedPartner);
                        try {
                            customersRepository.saveAll(custList);
                        } catch (Exception e) {
                            ApplicationLogger.logger.error("Unable to update customer with partner name " + message.getName(), e.getMessage());
                        }
                    }
                }
            }
            ApplicationLogger.logger.info("Partner created successfully with name " + message.getName());
        } catch (CustomValidationException e) {
            ApplicationLogger.logger.error("Unable to create partner with name " + message.getName(), e.getMessage());
        }
    }
}
