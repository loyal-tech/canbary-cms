package com.adopt.apigw.modules.LocationMaster.service;

import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.LocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.domain.QLocationMaster;
import com.adopt.apigw.modules.LocationMaster.domain.QLocationMasterMapping;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterMappingDto;
import com.adopt.apigw.modules.LocationMaster.module.UpdateLocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterMappingRepository;
import com.adopt.apigw.modules.LocationMaster.repository.LocationMasterRepository;
import com.adopt.apigw.modules.Reseller.mapper.PageableResponse;
import com.adopt.apigw.modules.Reseller.mapper.WifiUtils;
import com.adopt.apigw.modules.Reseller.service.ResellerService;
import com.adopt.apigw.modules.Voucher.module.PaginationDTO;
import com.adopt.apigw.modules.Voucher.module.SNMPCounters;
import com.adopt.apigw.modules.Voucher.module.ValidateCrudTransactionData;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustPayDTOMessage;
import com.adopt.apigw.rabbitMq.message.LocationMessage;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.APIConstants;

import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LocationMasterServiceImpl implements LocationMasterService {

    final Logger log = LoggerFactory.getLogger(LocationMasterServiceImpl.class);

    @Autowired
    private LocationMasterRepository locationMasterRepository;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;
    @Autowired
    private ResellerService resellerService;
    @Autowired
    private LocationMasterMappingRepository locationMasterMappingRepository;

    private final SNMPCounters snmpCounters = new SNMPCounters();


    @Transactional
    public LocationMaster saveLocationMaster(LocationMasterDto locationmasterDto, Long mvnoId) {

        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            LocationMaster locationMaster = new LocationMaster(locationmasterDto, mvnoId);
            List<LocationMasterMapping> locationMasterMappings = locationMaster.getLocationMasterMappings();
            validateLocationMasterDetail(locationMaster, false);
            checkForUniqueLocationMaster(locationMaster, false);
            locationMaster.setLocationMasterMappings(null);
            LocationMaster locationMasterVo = locationMasterRepository.save(locationMaster);
            if(!CollectionUtils.isEmpty(locationMasterMappings)) {
                locationMasterMappings = locationMasterMappings.stream().peek(locationMasterMapping -> locationMasterMapping.setLocationMasterId(locationMasterVo.getLocationMasterId())).collect(Collectors.toList());
                locationMasterMappingRepository.saveAll(locationMasterMappings);
            }
            LocationMessage locationDto = convertEntityToMessage(locationMasterVo);

//            messageSender.send(locationDto, RabbitMqConstants.QUEUE_SEND_LOCATION_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(locationDto, locationDto.getClass().getSimpleName()));
            return locationMasterVo;
        } catch (Throwable e) {
            log.error("Error while save location: "+e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

//    private void sendSaveLocationMessage(LocationMaster locationMaster, Boolean isUpdate, boolean isDelete) {
//        LocationMessage locationMessage = new LocationMessage(locationMaster, isUpdate, isDelete);
//        messageSender.send(locationMessage, RabbitMqConstants.QUEUE_LOCATION_MASTER);
//    }

    private void checkForUniqueLocationMaster(LocationMaster locationMaster, boolean isUpdate) {
        try {
            String message = "Location exist with the same name : '" + locationMaster.getName() + "'";
            QLocationMaster qLocatonMaster = QLocationMaster.locationMaster;
            BooleanExpression boolExp = qLocatonMaster.isNotNull();
            if (isUpdate) {
                if(!CollectionUtils.isEmpty(locationMaster.getLocationMasterMappings())) {
                    List<String> macs = locationMaster.getLocationMasterMappings().stream().map(LocationMasterMapping::getMac).collect(Collectors.toList());
                    for(String mac: macs) {
                        if(locationMasterMappingRepository.existsByMacAndLocationMasterNotIn(locationMaster.getLocationMasterId(), mac) > 0) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Mac already Used: "+mac, null);
                        }
                    }
                }
                boolExp = boolExp.and(qLocatonMaster.locationMasterId.ne(locationMaster.getLocationMasterId()));
            } else if(!CollectionUtils.isEmpty(locationMaster.getLocationMasterMappings())) {
                if(!CollectionUtils.isEmpty(locationMaster.getLocationMasterMappings())) {
                    List<String> macs = locationMaster.getLocationMasterMappings().stream().map(LocationMasterMapping::getMac).collect(Collectors.toList());
                    for(String mac: macs) {
                        if(locationMasterMappingRepository.existsByMac(mac)) {
                            throw new CustomValidationException(HttpStatus.EXPECTATION_FAILED.value(), "Mac already Used: "+mac, null);
                        }
                    }
                }
            }

            if (locationMaster.getMvnoId() == 1) {
                boolExp = boolExp.and(qLocatonMaster.name.eq(locationMaster.getName()));
                List<LocationMaster> locationList = (List<LocationMaster>) locationMasterRepository.findAll(boolExp);
                if (!locationList.isEmpty()) {
                    throw new IllegalArgumentException(message);
                }
            } else {
                boolExp = boolExp.and(qLocatonMaster.name.eq(locationMaster.getName())).and((qLocatonMaster.mvnoId.eq(locationMaster.getMvnoId())).or(qLocatonMaster.mvnoId.eq(1L)));
                Optional<LocationMaster> optionalLocation = locationMasterRepository.findOne(boolExp);
                if (optionalLocation.isPresent()) {
                    throw new IllegalArgumentException(message);
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void validateLocationMasterDetail(LocationMaster locationMaster, boolean isUpdate) {
        try {
            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(locationMaster.getName()))
                throw new RuntimeException("LocationMaster name is mandatory. Please enter valid LocationMaster name");
            else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(locationMaster.getStatus()) || (!locationMaster.getStatus().equals(CommonConstants.ACTIVE_STATUS) && !locationMaster.getStatus().equals(CommonConstants.INACTIVE_STATUS))) {
                throw new RuntimeException("Status is mandatory. Please enter valid status. It should be '" + CommonConstants.ACTIVE_STATUS + "' or '" + CommonConstants.INACTIVE_STATUS + "'");
            } else if (locationMaster.getCheckItem() != null && locationMaster.getCheckItem().equalsIgnoreCase(APIConstants.BLANK_STRING)) {
                locationMaster.setCheckItem(null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public PageableResponse<LocationMaster> findAllLocationMaster(Long mvnoId, String name, PaginationDTO paginationDTO) {
        try {
            QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
            BooleanExpression booleanExpression = qLocationMaster.isNotNull();
            PageableResponse<LocationMaster> pageableResponse = new PageableResponse<>();
            if (mvnoId != 1) {
                booleanExpression = booleanExpression.and(qLocationMaster.mvnoId.in(mvnoId, 1));
            }
            if (paginationDTO.getPage() > 0) {
                paginationDTO.setPage(paginationDTO.getPage() - 1);
            }
            Pageable pageable = PageRequest.of(paginationDTO.getPage(), paginationDTO.getSize(), Sort.by(Sort.Direction.DESC, "lastmodifiedDate"));
            if (!(StringUtils.isBlank(paginationDTO.getFromDate()) || paginationDTO.getFromDate().equalsIgnoreCase("null"))) {
                booleanExpression = booleanExpression.and(qLocationMaster.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00")).or(qLocationMaster.lastmodifiedDate.after(Timestamp.valueOf(paginationDTO.getFromDate() + " 00:00:00"))));
            }
            if (!(StringUtils.isBlank(paginationDTO.getToDate()) || paginationDTO.getToDate().equalsIgnoreCase("null"))) {
                booleanExpression = booleanExpression.and(qLocationMaster.lastmodifiedDate.eq(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59")).or(qLocationMaster.lastmodifiedDate.before(Timestamp.valueOf(paginationDTO.getToDate() + " 23:59:59"))));
            }

            //check Serarch filter
            if (!StringUtils.isBlank(name))
                booleanExpression = booleanExpression.and(qLocationMaster.name.like("%" + name + "%"));
            Page<LocationMaster> page = locationMasterRepository.findAll(booleanExpression, pageable);
            return pageableResponse.convert(new PageImpl<>(page.getContent(), pageable, page.getTotalElements()));
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public LocationMaster findlocationMasterById(Long locationMasterId, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else if (!ValidateCrudTransactionData.validateLongTypeFieldValue(locationMasterId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid locationMaster id.");
            } else {
                return findByLocationMasterIdAndMvnoId(locationMasterId, mvnoId, false);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public LocationMaster updateLocation(UpdateLocationMasterDto locationDto, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_UPDATE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            LocationMaster locationM = new LocationMaster(locationDto, mvnoId);
            LocationMaster location = findByLocationMasterIdAndMvnoId(locationDto.getLocationMasterId(), mvnoId, true);
            String updatedValues = WifiUtils.getUpdatedDiff(location, locationM);
            locationM.setName(location.getName());
            locationM.setMvnoId(location.getMvnoId());
            validateLocationMasterDetail(locationM, true);
            List<LocationMasterMapping> locationMasterMappings = locationMasterMappingRepository.findAllByLocationMasterId(location.getLocationMasterId());
            if(!CollectionUtils.isEmpty(locationMasterMappings)) {
                locationMasterMappingRepository.deleteInBatch(locationMasterMappings);
            }
            checkForUniqueLocationMaster(locationM, true);
//            log.info("Location has been update successfully by " + MDC.get("userName") + " the difference is " + updatedValues);
            List<LocationMasterMapping> newLocationMasterMap = locationM.getLocationMasterMappings();
            locationM.setLocationMasterMappings(null);
            LocationMaster locationMasterVo = locationMasterRepository.save(locationM);

            LocationMessage UpdatelocationDto = convertEntityToMessage(locationMasterVo);
//            messageSender.send(UpdatelocationDto, RabbitMqConstants.QUEUE_SEND_LOCATION_TO_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(UpdatelocationDto, LocationMessage.class.getSimpleName()));
            if(!CollectionUtils.isEmpty(newLocationMasterMap)) {
                newLocationMasterMap = newLocationMasterMap.stream().peek(locationMasterMapping -> locationMasterMapping.setLocationMasterId(locationM.getLocationMasterId())).collect(Collectors.toList());
                locationMasterMappingRepository.saveAll(newLocationMasterMap);
            }
            locationMasterVo.setLocationMasterMappings(newLocationMasterMap);
            //sendSaveLocationMessage(locationMasterVo, true, false);
            return locationMasterVo;
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("Error while update location: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
    }

    @Override
    public List<LocationMasterMapping> saveLocationMasterMapping(List<LocationMasterMappingDto> locationMasterMappingDtos, LocationMaster locationMaster) {
        List<LocationMasterMapping> existingLocationMasterMappings = locationMasterMappingRepository.findAllByLocationMasterId(locationMaster.getLocationMasterId());
        if(!CollectionUtils.isEmpty(existingLocationMasterMappings)) {
            existingLocationMasterMappings.forEach(locationMasterMapping -> {
                locationMasterMappingRepository.deleteById(locationMasterMapping.getMappingId());
            });
        }
        List<LocationMasterMapping> locationMasterMappings = locationMasterMappingDtos.stream().map(l -> new LocationMasterMapping(l, locationMaster)).collect(Collectors.toList());
        return locationMasterMappingRepository.saveAll(locationMasterMappings);
    }

    private LocationMasterMapping findExistingMapping(List<LocationMasterMapping> mappings, LocationMasterMappingDto dto) {
        for (LocationMasterMapping existingMapping : mappings) {
            if (existingMapping.getMappingId().equals(dto.getMappingId())) {
                return existingMapping;
            }
        }
        return null;
    }



    @Override
    public void deleteLocationById(Long locationMasterId, Long mvnoId) {
        MDC.put(APIConstants.TYPE, APIConstants.TYPE_DELETE);
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            }
            if (resellerService.countByLocationId(locationMasterId) > 0)
                throw new IllegalArgumentException("This operation will not allow as there are some Resellers available related to this Location .");
            LocationMaster locationMaster = findByLocationMasterIdAndMvnoId(locationMasterId, mvnoId, true);
//            log.info("Location has been deleted successfully: " + locationMaster.getName() + " by " + MDC.get("username"));
            //checkDependancyOnPlanLocationId(locationMasterId);
            //sendSaveLocationMessage(locationMaster, false, true);
            locationMasterRepository.delete(locationMaster);
        } catch (Throwable e) {
            log.error("Error while delete location: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

    }

//    private void checkDependancyOnPlanLocationId(Long locationMasterId) {
//        try {
//            QPlanLocation qPlanLocation = QPlanLocation.planLocation;
//            BooleanExpression expForPlanLocation = qPlanLocation.isNotNull().and(qPlanLocation.locationId.eq(locationMasterId));
//            List<PlanLocation> planLocationList = (List<PlanLocation>) planLocationRepository.findAll(expForPlanLocation);
//            if (planLocationList.size() > 0) {
//                throw new IllegalArgumentException("This operation will not allow as there are some Plans available for this Location .");
//            }
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e.getMessage());
//        }
//    }

    @Override
    public List<LocationMaster> findLocation(String name, Long mvnoId) {
        try {
            if (!ValidateCrudTransactionData.validateLongTypeFieldValue(mvnoId)) {
                throw new IllegalArgumentException(APIConstants.BASIC_NUMERIC_MSG + "Please enter valid mvno id.");
            } else {
                QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
                BooleanExpression booleanExpression = qLocationMaster.isNotNull();
                if (StringUtils.isBlank(name) || name.equalsIgnoreCase("null")) {
                    if (mvnoId == 1) {
                        return locationMasterRepository.findAll();
                    } else {
                        booleanExpression = booleanExpression.and(qLocationMaster.mvnoId.eq(mvnoId)).or(qLocationMaster.mvnoId.eq(1L));
                        return (List<LocationMaster>) locationMasterRepository.findAll(booleanExpression);
                    }
                } else {
                    if (mvnoId == 1) {
                        booleanExpression = booleanExpression.and(qLocationMaster.name.contains(name));
                        return (List<LocationMaster>) locationMasterRepository.findAll(booleanExpression);
                    } else {
                        booleanExpression = booleanExpression.and(qLocationMaster.name.contains(name)).and(qLocationMaster.mvnoId.in(mvnoId, 1));
                        return (List<LocationMaster>) locationMasterRepository.findAll(booleanExpression);
                    }
                }
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public String updateLocationStatus(String name, String status, Long mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name)) {
                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "LocationName is mandatory. Please enter valid location name.");
            } else if (!ValidateCrudTransactionData.validateStringTypeFieldValue(status)) {
                throw new IllegalArgumentException(APIConstants.BASIC_STRING_MSG + "Location status is mandatory. Please enter valid location status.");
            } else if (!status.equals(CommonConstants.ACTIVE_STATUS) && !status.equals(CommonConstants.INACTIVE_STATUS)) {
                throw new IllegalArgumentException("Please enter valid location status. It should be '" + CommonConstants.ACTIVE_STATUS + "' or '" + CommonConstants.INACTIVE_STATUS + "'");
            }

            LocationMaster locationMaster = validateLocationForUpdateOrDelete(name, mvnoId);

            locationMaster.setStatus(status);
            //sendSaveLocationMessage(locationMaster, true, false);
            locationMasterRepository.save(locationMaster);

            String msg = "";
            if (status.equals(CommonConstants.ACTIVE_STATUS)) {
                msg = "LocationMaster '" + locationMaster.getName() + "' has been activated successfully.";
            } else {
                msg = "LocationMaster '" + locationMaster.getName() + "' has been inactivated successfully.";
            }
            return msg;
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public List<LocationMasterMappingDto> getAllMacFromLocations(List<Long> locationIds, Boolean isParentLocation) {
        QLocationMasterMapping locationMasterMapping = QLocationMasterMapping.locationMasterMapping;
        BooleanExpression expression = locationMasterMapping.isNotNull().and(locationMasterMapping.locationMasterId.in(locationIds));
        if(isParentLocation)
            expression = expression.and(locationMasterMapping.isUsed.eq(false));

        List<LocationMasterMapping> locationMasterMappings = (List<LocationMasterMapping>) locationMasterMappingRepository.findAll(expression);
        return locationMasterMappings.stream().map(locationMasterMap -> new LocationMasterMappingDto(locationMasterMap.getLocationName(), locationMasterMap.getMac(), locationMasterMap.getLocationMasterId())).collect(Collectors.toList());
    }

    @Override
    public List<LocationMaster> getLocationFromMac(String mac) {
        QLocationMasterMapping locationMasterMapping = QLocationMasterMapping.locationMasterMapping;
        BooleanExpression expression = locationMasterMapping.isNotNull().and(locationMasterMapping.mac.equalsIgnoreCase(mac));
        List<LocationMasterMapping> locationMasterMappings = (List<LocationMasterMapping>) locationMasterMappingRepository.findAll(expression);
        List<Long> locationMasterIds = locationMasterMappings.stream().map(locationMasterMapping1 -> locationMasterMapping1.getLocationMasterId()).collect(Collectors.toList());
        QLocationMaster locationMaster = QLocationMaster.locationMaster;
        BooleanExpression locationbooleanexpression = locationMaster.isNotNull().and(locationMaster.locationMasterId.in(locationMasterIds));
        List<LocationMaster> locationMasters = IterableUtils.toList(locationMasterRepository.findAll(locationbooleanexpression));
        return locationMasters;
    }

    private LocationMaster validateLocationForUpdateOrDelete(String name, Long mvnoId) {
        try {

            if (!ValidateCrudTransactionData.validateStringTypeFieldValue(name))
                throw new IllegalArgumentException("Please enter valid Location name.");
            QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
            BooleanExpression boolExp = qLocationMaster.isNotNull();
            if (mvnoId == null || mvnoId != 1) boolExp = boolExp.and(qLocationMaster.mvnoId.eq(mvnoId));
            boolExp = boolExp.and(qLocationMaster.name.eq(name));

            Optional<LocationMaster> optionalLocation = locationMasterRepository.findOne(boolExp);
            if (!optionalLocation.isPresent()) {
                throw new IllegalArgumentException("You do not have access/No records found to update or delete this record.");
            }
            return optionalLocation.get();
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private LocationMaster findByLocationMasterIdAndMvnoId(Long locationId, Long mvnoId, Boolean isUpdateOrDelete) {
        QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
        BooleanExpression boolExp = qLocationMaster.isNotNull();
        boolExp = boolExp.and(qLocationMaster.locationMasterId.eq(locationId));
        if (mvnoId != 1) {
            if (isUpdateOrDelete) boolExp = boolExp.and(qLocationMaster.mvnoId.eq(mvnoId));
            else boolExp = boolExp.and(qLocationMaster.mvnoId.in(mvnoId, 1));
        }
        Optional<LocationMaster> locationMasterOptional = locationMasterRepository.findOne(boolExp);
        if (locationMasterOptional.isPresent()){
            List<LocationMasterMapping> locationMasterMappings = locationMasterMappingRepository.findAllByLocationMasterId(locationMasterOptional.get().getLocationMasterId());
            locationMasterOptional.get().setLocationMasterMappings(locationMasterMappings);
            return locationMasterOptional.get();
        }
        else
            throw new IllegalArgumentException("No record found for location master with id : '" + locationId + "'. Please enter valid location master id OR You do not have access to update or delete this record");
    }

    public int getLoggedInMvnoId() {
        int loggedInMvnoId = -1;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                loggedInMvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            loggedInMvnoId = -1;
        }
        return loggedInMvnoId;
    }
    
//    @Override
//	public List<LocationMaster> findLocationByPlan(Long planId, Long mvnoId) throws DataNotFoundException {
//    	if (!ValidateCrudTransactionData.validateLongTypeFieldValue(planId))
//    		throw new IllegalArgumentException("Please enter valid Plan Id.");
//    	List<PlanLocation> planLocationList = planLocationRepository.findByPlanId(planId);
//
//    	List<Long> locationIds = planLocationList.stream().map(l -> l.getLocationId()).collect(Collectors.toList());
//    	QLocationMaster qLocationMaster = QLocationMaster.locationMaster;
//        BooleanExpression boolExp = qLocationMaster.isNotNull();
//        boolExp = boolExp.and(qLocationMaster.locationMasterId.in(locationIds));
//        if (mvnoId != 1) {
//            boolExp = boolExp.and(qLocationMaster.mvnoId.in(mvnoId, 1));
//        }
//        List<LocationMaster> locationMasters = (List<LocationMaster>) locationMasterRepository.findAll(boolExp);
//        return locationMasters;
//
//    }

    public LocationMessage convertEntityToMessage(LocationMaster locationMaster) {
        LocationMessage location = new LocationMessage();

        location.setLocationMasterId(locationMaster.getLocationMasterId());
        location.setName(locationMaster.getName());
        location.setCheckItem(locationMaster.getCheckItem());
        location.setStatus(locationMaster.getStatus());
        location.setMvnoId(locationMaster.getMvnoId());
        location.setLocationIdentifyAttribute(locationMaster.getLocationIdentifyAttribute());
        location.setLastmodifiedDate(locationMaster.getLastmodifiedDate());
        location.setLocationIdentifyValue(locationMaster.getLocationIdentifyValue());


        return location;
    }
}
