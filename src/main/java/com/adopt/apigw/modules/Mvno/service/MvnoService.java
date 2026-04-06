package com.adopt.apigw.modules.Mvno.service;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveMvnoSharedDataMessage;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.UpdateMvnoSharedDataMessage;
import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.exceptions.DataNotFoundException;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.repository.CustomRepository;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.StaffUserMapper;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfile;
import com.adopt.apigw.modules.custAccountProfile.CustAccountProfileRepository;
import com.adopt.apigw.modules.role.service.RoleService;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.pojo.api.StaffUserPojo;
import com.adopt.apigw.pojo.api.VasPlanPojo;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustomMessage;
import com.adopt.apigw.rabbitMq.message.MvnoStatusMessage;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.VasPlanService;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.NumberSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.mapper.MvnoMapper;
import com.adopt.apigw.modules.Mvno.model.MvnoDTO;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MvnoService extends ExBaseAbstractService<MvnoDTO, Mvno, Long> {
    private static final Logger logger = LoggerFactory.getLogger(MvnoService.class);
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private StaffUserMapper staffUserMapper;
    @Autowired
    private MvnoMapper mapper;
    @Autowired
    private MvnoRepository mvnoRepository;
    @Autowired
    private ClientServiceSrv clientServiceSrv;
    @Autowired
    private ServiceAreaService serviceAreaService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private NumberSequenceUtil numberSequenceUtil;

    @Autowired
    private CustomersService customersService;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CustAccountProfileRepository custAccountProfileRepository;

    @Autowired
    MessageSender messageSender;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CacheService cacheService;

    @Autowired
    private VasPlanService vasPlanService;


    public MvnoService(MvnoRepository repository, MvnoMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[MvnoService]";
    }

    @Override
    @Transactional
    public MvnoDTO saveEntity(MvnoDTO entity) throws Exception {
        MvnoDTO mvno = super.saveEntity(entity);
        entity.setId(mvno.getId());
        staffUserService.saveWithMvno(mvnoToStaff(entity));
//        numberSequenceUtil.createSequenceNumberFunctionForMVNO(mvno);
        return mvno;
    }

    @Override
    @Transactional
    public MvnoDTO updateEntity(MvnoDTO entity) throws Exception {
        MvnoDTO mvnoDTO = super.updateEntity(entity);
        entity.setId(mvnoDTO.getId());
        staffUserService.saveWithMvno(mvnoToStaff(entity));
        return mvnoDTO;
    }

    private StaffUserPojo mvnoToStaff(MvnoDTO mvno) {
        StaffUserPojo staffPojo = new StaffUserPojo();
        try {
            StaffUser staff = staffUserService.getByUserName(mvno.getUsername());
            if (staff != null)
                staff.setId(staff.getId());
            staffPojo = staffUserMapper.domainToDTO(staff, new CycleAvoidingMappingContext());
            staffPojo.setUsername(mvno.getUsername());
            staffPojo.setPassword(mvno.getPassword());
            staffPojo.setFirstname(mvno.getName());
            staffPojo.setLastname(mvno.getName());
            staffPojo.setEmail(mvno.getEmail());
            staffPojo.setPhone(mvno.getPhone());
            staffPojo.setStatus(mvno.getStatus().toUpperCase());
            List<Integer> roles = new ArrayList();
            roles.add(1);
//            staffPojo.setServiceAreaNameList(serviceAreaService.getAllEntities().stream().map(serviceAreaDTO -> serviceAreaMapper.dtoToDomain(serviceAreaDTO, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
            staffPojo.setRoleIds(roles);
            staffPojo.setIsDelete(mvno.getIsDelete());
            staffPojo.setMvnoId(mvno.getId().intValue());
            staffPojo.setPartnerid(1);

        } catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return staffPojo;
    }

    @Override
    public List<MvnoDTO> getAllEntities(Integer mvnoId) throws Exception {
        try {
            return mvnoRepository.findAll().stream().filter(data -> !data.getDeleteFlag()).map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting list : " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public MvnoDTO getEntityById(Long id,Integer mvnoId) throws Exception {
        try {
            Mvno domain = (null == mvnoRepository.findById(id)) ? null : mvnoRepository.findById(id).get();
            if (null == domain || domain.getDeleteFlag()) {
                throw new DataNotFoundException(getModuleNameForLog() + "--" + "Data not found for id " + id);
            }
            MvnoDTO dto = mapper.domainToDTO(mvnoRepository.findById(id).get(), new CycleAvoidingMappingContext());
            if (dto != null)
                return dto;
            return null;
        } catch (Exception ex) {
            if (ex instanceof NoSuchElementException) {
                throw new DataNotFoundException();
            }
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while getting entity by id [" + id + " ]: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEntity(MvnoDTO entity) throws Exception {
        Mvno entityDomain = mapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
        ApplicationLogger.logger.info(getModuleNameForLog() + "--" + "deleting Entity. Data[" + entityDomain.toString() + "]");
        try {
            if (entityDomain.getDeleteFlag()) {
                throw new DataNotFoundException();
            }
            if(entity == null)
                throw new CustomValidationException(APIConstants.FAIL, Constants.MVNO_DELETE_UPDATE_ERROR_MSG, null);
            entityDomain.setDeleteFlag(true);
            mvnoRepository.save(entityDomain);
        } catch (Exception ex) {
            ApplicationLogger.logger.error(getModuleNameForLog() + "--" + ex.getMessage() + "Error while deleting Entity. Data[" + entityDomain.toString() + "]" + ex.getMessage(), ex);
            throw ex;
        }
    }

    //Pagination
    public GenericDataDTO getListByPagination(PageRequest pageRequest) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Page<Mvno> paginationList = getRepository().findAll(pageRequest);
        if (null != paginationList && 0 < paginationList.getSize()) {
            makeGenericResponse(genericDataDTO, paginationList);
        }
        return genericDataDTO;
    }

    @Override
    public GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) {
        String SUBMODULE = getModuleNameForLog() + "[getListByPageAndSizeAndSortByAndOrderBy()]";
        try {
            return getListByPagination(generatePageRequest(page, size, sortBy, sortOrder));
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
    }

    public GenericDataDTO makeGenericResponse(GenericDataDTO genericDataDTO, Page<Mvno> paginationList) {
        genericDataDTO.setDataList(paginationList.getContent().stream().map(data -> getMapper().domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList()));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }

    // Shared MVNO Data from Common APIGW to CMS
    public void saveMVNOEntity(SaveMvnoSharedDataMessage mvnoSharedDataMessage) throws Exception{
        try {
            Mvno mvno = new Mvno();
            mvno.setFullName(mvnoSharedDataMessage.getFullName());
            mvno.setId(mvnoSharedDataMessage.getId());
            mvno.setName(mvnoSharedDataMessage.getName());
            mvno.setUsername(mvnoSharedDataMessage.getUsername());
            mvno.setPassword(mvnoSharedDataMessage.getPassword());
            mvno.setSuffix(mvnoSharedDataMessage.getSuffix());
            mvno.setDescription(mvnoSharedDataMessage.getDescription());
            mvno.setEmail(mvnoSharedDataMessage.getEmail());
            mvno.setPhone(mvnoSharedDataMessage.getPhone());
            mvno.setStatus(mvnoSharedDataMessage.getStatus());
            mvno.setLogfile(mvnoSharedDataMessage.getLogfile());
            mvno.setMvnoHeader(mvnoSharedDataMessage.getMvnoHeader());
            mvno.setMvnoFooter(mvnoSharedDataMessage.getMvnoFooter());
            mvno.setIsDelete(mvnoSharedDataMessage.getIsDelete());
            mvno.setCreatedById(mvnoSharedDataMessage.getCreatedById());
            mvno.setLastModifiedById(mvnoSharedDataMessage.getLastModifiedById());
            mvno.setLogo_file_name(mvnoSharedDataMessage.getLogo_file_name());
            mvno.setProfileImage(mvnoSharedDataMessage.getProfileImage());
            mvno.setMvnoPaymentDueDays(mvnoSharedDataMessage.getMvnoPaymentDueDays());
            mvno.setIspBillDay(mvnoSharedDataMessage.getIspBillDay());
            mvno.setBillType(mvnoSharedDataMessage.getBillType());
            mvno.setIspCommissionPercentage(mvnoSharedDataMessage.getIspCommissionPercentage());
            mvno.setThreshold(mvnoSharedDataMessage.getThreshold());
            Long profileId = mvnoSharedDataMessage.getProfileId();
            if(profileId!=null){
                CustAccountProfile custAccountProfile = custAccountProfileRepository.findById(profileId).orElse(null);
            mvno.setCustAccountProfile(custAccountProfile);
            }
            mvno = mvnoRepository.save(mvno);
            numberSequenceUtil.createSequenceNumberFunctionForMVNO(mvno);
            //To add default path for mvno
            clientServiceSrv.addDefaultPathWhenMvnoCreated(mvno);
            CustomersPojo customersPojo = customersService.saveDefaultCustomerForMvno(mvno, mvnoSharedDataMessage);
            //to add default service pack path for mvno
            vasPlanService.saveDefaultVasForMvno(mvnoSharedDataMessage.getId());
            if(customersPojo != null) {
                mvno.setCustInvoiceRefId(customersPojo.getId());
                mvno = mvnoRepository.save(mvno);
                //kafka call to Common to save cust_ref_id in mvno
                CustomMessage customMessage = new CustomMessage(mvno.getId().intValue(),customersPojo.getId());
                kafkaMessageSender.send(new KafkaMessageData(customMessage, CustomMessage.class.getSimpleName(),CommonConstants.MVNO_CUST_REF));
            }
            logger.info("MVNO created successfully with name " + mvnoSharedDataMessage.getName());
        } catch (CustomValidationException e) {
            logger.error("Unable to create mvno with name " + mvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void updateMVNOEntity(UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage) throws Exception {
        try {
            Mvno mvno = mvnoRepository.findById(updateMvnoSharedDataMessage.getId()).orElse(null);
            if (mvno != null) {
                mvno.setId(updateMvnoSharedDataMessage.getId());
                mvno.setFullName(updateMvnoSharedDataMessage.getFullName());
                mvno.setName(updateMvnoSharedDataMessage.getName());
                mvno.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno.setPhone(updateMvnoSharedDataMessage.getPhone());
                if(!mvno.getStatus().equalsIgnoreCase(updateMvnoSharedDataMessage.getStatus())){
                    Set<Long> mvnoId = new HashSet<>();
                    mvnoId.add(mvno.getId());
                    changeMvnoStatus(mvnoId,updateMvnoSharedDataMessage.getStatus());
                    mvno.setStatus(updateMvnoSharedDataMessage.getStatus());
                }
                mvno.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                mvno.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvno.setProfileImage(updateMvnoSharedDataMessage.getProfileImage());
                mvno.setLogo_file_name(updateMvnoSharedDataMessage.getLogo_file_name());
                mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
                mvno.setIspBillDay(updateMvnoSharedDataMessage.getIspBillDay());
                mvno.setBillType(updateMvnoSharedDataMessage.getBillType());
                mvno.setIspCommissionPercentage(updateMvnoSharedDataMessage.getIspCommissionPercentage());
                mvno.setThreshold(updateMvnoSharedDataMessage.getThreshold());
                Long profileId = updateMvnoSharedDataMessage.getProfileId();
                if(profileId!=null){
                    CustAccountProfile custAccountProfile = custAccountProfileRepository.findById(profileId).orElse(null);
                    mvno.setCustAccountProfile(custAccountProfile);
                }
                mvnoRepository.save(mvno);

                customersService.updateMvnoCustomerAddress(mvno, updateMvnoSharedDataMessage);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            } else {
                Mvno mvno1 = new Mvno();
                mvno.setFullName(updateMvnoSharedDataMessage.getFullName());
                mvno1.setId(updateMvnoSharedDataMessage.getId());
                mvno1.setName(updateMvnoSharedDataMessage.getName());
                mvno1.setUsername(updateMvnoSharedDataMessage.getUsername());
                mvno1.setPassword(updateMvnoSharedDataMessage.getPassword());
                mvno1.setSuffix(updateMvnoSharedDataMessage.getSuffix());
                mvno1.setDescription(updateMvnoSharedDataMessage.getDescription());
                mvno1.setEmail(updateMvnoSharedDataMessage.getEmail());
                mvno1.setPhone(updateMvnoSharedDataMessage.getPhone());
                if(!mvno.getStatus().equalsIgnoreCase(updateMvnoSharedDataMessage.getStatus())){
                    Set<Long> mvnoId = new HashSet<>();
                    mvnoId.add(mvno.getId());
                    changeMvnoStatus(mvnoId,updateMvnoSharedDataMessage.getStatus());
                    mvno1.setStatus(updateMvnoSharedDataMessage.getStatus());
                }
                mvno1.setLogfile(updateMvnoSharedDataMessage.getLogfile());
                mvno1.setMvnoHeader(updateMvnoSharedDataMessage.getMvnoHeader());
                mvno1.setMvnoFooter(updateMvnoSharedDataMessage.getMvnoFooter());
                mvno1.setIsDelete(updateMvnoSharedDataMessage.getIsDelete());
                mvno1.setCreatedById(updateMvnoSharedDataMessage.getCreatedById());
                mvno1.setLastModifiedById(updateMvnoSharedDataMessage.getLastModifiedById());
                mvno.setMvnoPaymentDueDays(updateMvnoSharedDataMessage.getMvnoPaymentDueDays());
                mvno1.setIspBillDay(updateMvnoSharedDataMessage.getIspBillDay());
                mvno1.setBillType(updateMvnoSharedDataMessage.getBillType());
                mvno1.setIspCommissionPercentage(updateMvnoSharedDataMessage.getIspCommissionPercentage());
                Long profileId = updateMvnoSharedDataMessage.getProfileId();
                if(profileId!=null){
                    CustAccountProfile custAccountProfile = custAccountProfileRepository.findById(profileId).orElse(null);
                    mvno.setCustAccountProfile(custAccountProfile);
                }
                mvnoRepository.save(mvno1);
                logger.info("MVNO updated successfully with name " + updateMvnoSharedDataMessage.getName());
            }
        } catch (CustomValidationException e) {
            logger.error("Unable to update mvno with name " + updateMvnoSharedDataMessage.getName(), e.getMessage());
        }
    }

    public void changeMvnoStatus(Set<Long> mvnoIds, String status) {
        List<Integer> list = mvnoIds.stream().mapToInt(Long::intValue).boxed().collect(Collectors.toList());
        if (status.equalsIgnoreCase(CommonConstants.INACTIVE_STATUS)) {


            //disable mvno's
            Boolean flagMvno = customRepository.updateMvnoStatus(list, status);

            if (flagMvno = true) {

                logger.info("mvnos are deactivated successfully");

                //write a rabbitmq call to send data to other microservice
                MvnoStatusMessage mvnoStatusMessage = new MvnoStatusMessage(list, status,true);
//                messageSender.send(mvnoStatusMessage, RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE);
                kafkaMessageSender.send(new KafkaMessageData(mvnoStatusMessage,mvnoStatusMessage.getClass().getSimpleName(),"MVNO_STATUS_DUNNING"));

                //disable all staff
                Boolean flagStaff = staffUserService.changeStaffStatusByMvno(list, CommonConstants.INACTIVE_STATUS);
                if (flagStaff = true) {
                    logger.info("staffs related to mvnos are deactivated successfully");
                }

                //disable all customers
                Boolean flafCustomer = customersService.inActiveCustomersByMvnoList(list, CommonConstants.INACTIVE_STATUS);
                if (flafCustomer = true) {
                    logger.info("customer related to mvnos are deactivated successfully");
                }


            }

        }
        else if(status.equalsIgnoreCase(CommonConstants.ACTIVE_STATUS)){
            //activate mvno's
            Boolean flagMvno = customRepository.updateMvnoStatus(list, status);
            if (flagMvno = true) {
                logger.info("mvnos are activated successfully");
                //write a rabbitmq call to send data to other microservice
                MvnoStatusMessage mvnoStatusMessage = new MvnoStatusMessage(list, status,null);
               kafkaMessageSender.send(new KafkaMessageData(mvnoStatusMessage,MvnoStatusMessage.class.getSimpleName()));
//                messageSender.send(mvnoStatusMessage, RabbitMqConstants.QUEUE_SEND_MVNO_STATUS_DUNNING_MESSAGE);
                //disable all staff
                Boolean flagStaff = staffUserService.changeStaffStatusActiveByMvno(list.get(0), CommonConstants.ACTIVE_STATUS);
                if (flagStaff = true) {
                    logger.info("staffs related to mvnos are activated successfully");
                }
                //disable all customers
                Boolean flafCustomer = customersService.ActiveCustomersByMvnoList(list.get(0), CommonConstants.ACTIVE_STATUS);
                if (flafCustomer = true) {
                    logger.info("customer related to mvnos are activated successfully");
                }


            }

        }
    }
    @Transactional
    public void updateMvnoIdIsptoIsp(Integer oldMvno, Integer newMvno) {
        try {
            Mvno oldMvnoEntity = mvnoRepository.getOne(oldMvno.longValue());
            Mvno newMvnoEntity = mvnoRepository.getOne(newMvno.longValue());
            if (oldMvnoEntity.getStatus().equalsIgnoreCase("active") && newMvnoEntity.getStatus().equalsIgnoreCase("active")) {
                mvnoRepository.UpdateMvnoidISP(oldMvno, newMvno);
                logger.info("MVNO updated successfully " + oldMvno +" to "+newMvno);
            } else {
                logger.error("Unable to update MVNO ID "+ oldMvno);
            }
        } catch (Exception e) {
            logger.error("Unexpected error while updating MVNO ID "+ oldMvno+ e);
        }
    }

    public Long getProfileIdByMvnoId(Long mvnoId) {
        String cacheKey = cacheKeys.PROFILEID_MVNOID + mvnoId; // Create a unique cache key
        Long profileId = null;

        try {
            profileId = (Long) cacheService.getFromCache(cacheKey, Long.class);

            if (profileId != null) {
                return profileId;
            }
            profileId = mvnoRepository.findProfileIdByMvnoId(mvnoId).orElse(null);

            if (profileId != null) {
                cacheService.putInCache(cacheKey, profileId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return profileId;
    }

}
