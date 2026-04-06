package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.constants.cacheKeys;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.mapper.NetworkDeviceMapper;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceDTO;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.modules.NetworkDevices.service.NetworkDeviceService;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.SubArea.Domain.SubArea;
import com.adopt.apigw.modules.customerDocDetails.mapper.CustomerDocDetailsMapper;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.*;
import com.adopt.apigw.service.CacheService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.postpaid.*;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.spring.LoggedInUser;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Mapper(componentModel = "spring", uses = {PostpaidPlanMapper.class, CustomerDocDetailsMapper.class})
public abstract class CustomerMapper implements IBaseMapper<CustomersPojo, Customers> {

    @Mapping(source = "servicearea", target = "serviceareaName")
    @Mapping(source = "servicearea", target = "serviceareaid")
    @Mapping(source = "networkdevices", target = "networkdevicesid")
    @Mapping(source = "radiusProfiles", target = "radiusprofileIds")
    @Mapping(source = "partner", target = "partnerid")
    @Mapping(source = "salesrep", target = "salesrepid")
    @Mapping(source = "createdate", target = "createDateString", dateFormat = "dd/MM/yyyy HH:mm a")
    @Mapping(source = "updatedate", target = "updateDateString", dateFormat = "dd/MM/yyyy HH:mm a")
    @Mapping(source = "last_password_change", target = "lastpasswordchangestring", dateFormat = "dd/MM/yyyy HH:mm a")
    @Mapping(source = "plangroup", target = "plangroupid")
    @Mapping(source = "valleyType", target = "valleyType")
    @Mapping(source = "customerArea", target = "customerArea")
    @Mapping(source = "customerType", target = "customerType")
    @Mapping(source = "customerSubType", target = "customerSubType")
    @Mapping(source = "customerSector", target = "customerSector")
    @Mapping(source = "customerSubSector", target = "customerSubSector")
//    @Mapping(source = "aadhar", target = "aadhar", qualifiedByName = "maskAadhar")
//    @Mapping(source = "pan", target = "pan", qualifiedByName = "maskPan")
//    @Mapping(source = "gst", target = "gst", qualifiedByName = "maskGst")
    @Override
    public abstract CustomersPojo domainToDTO(Customers data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "serviceareaid", target = "servicearea")
    @Mapping(source = "networkdevicesid", target = "networkdevices")
    @Mapping(source = "radiusprofileIds", target = "radiusProfiles")
    @Mapping(source = "partnerid", target = "partner")
    @Mapping(source = "salesrepid", target = "salesrep")
    @Mapping(source = "plangroupid", target = "plangroup")
    @Mapping(source = "valleyType", target = "valleyType")
    @Mapping(source = "customerArea", target = "customerArea")
    @Mapping(source = "customerType", target = "customerType")
    @Mapping(source = "customerSubType", target = "customerSubType")
    @Mapping(source = "customerSector", target = "customerSector")
    @Mapping(source = "customerSubSector", target = "customerSubSector")
    @Override
    public abstract Customers dtoToDomain(CustomersPojo dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private ServiceAreaRepository serviceAreaRepository;

    @Autowired
    private NetworkDeviceService networkDeviceService;
    @Autowired
    private CityRepository cityService;
    @Autowired
    private StateRepository stateService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PincodeService pincodeService;
    @Autowired
    private CountryRepository countryService;
    @Autowired
    private ServiceAreaMapper serviceAreaMapper;
    @Autowired
    private NetworkDeviceMapper networkDeviceMapper;
    @Autowired
    private PostpaidPlanRepo planService;
    @Autowired
    private StaffUserService staffUserService;
    @Autowired
    private PartnerService partnerService;
    @Autowired
    private PlanGroupService planGroupService;
    @Autowired
    private PlanGroupRepository planGroupRepository;
    @Autowired
    CacheService cacheService;
    @Autowired
    PincodeRepository pincodeRepository;
    @Autowired
    AreaRepository areaRepository;


    @Autowired
    PartnerRepository partnerRepository;
    @Autowired
    private StaffUserRepository staffUserRepository;
    @Autowired
    NetworkDeviceRepository networkDeviceRepository;

    @Named("maskAadhar")
    String maskAadhar(String string) throws Exception {
        if(!StringUtils.isEmpty(string)) {
            String lastFourDigits = string.substring(8);
            return DocumentConstants.AADHAR_STAR_PATTERN + lastFourDigits;
        }
        return string;
    }

    @Named("maskPan")
    String maskPan(String string) throws Exception {
        if(!StringUtils.isEmpty(string)) {
            String lastFourDigits = string.substring(6);
            return DocumentConstants.PAN_STAR_PATTERN + lastFourDigits;
        }
        return string;
    }

    @Named("maskGst")
    String maskGst(String string) throws Exception {
        if(!StringUtils.isEmpty(string)) {
            String lastFiveDigits = string.substring(9);
            return DocumentConstants.GST_STAR_PATTERN + lastFiveDigits;
        }
        return string;
    }

    String fromServiceAreaToName(ServiceArea entity) {
        return entity == null ? null : entity.getName();
    }
    Long fromServiceAreaToId(ServiceArea entity) {
    	return entity == null ? null : entity.getId();
    }

    ServiceArea fromServiceAreaIdToServiceArea(Long entityId) {
        if (entityId == null) {
            return null;
        }
        ServiceArea entity;
        String cacheKey = cacheKeys.SERVICEAREA + entityId;
        try {
            entity = (ServiceArea) cacheService.getFromCache(cacheKey, ServiceArea.class);

            if (entity != null) {
                ApplicationLogger.logger.info("ServiceArea from cache ::::::::::::::: " + entityId + " ::::: Name :::::: " + entity.getName());
                entity.setId(entityId);
                return entity;
            }
//            entity = serviceAreaRepository.findById(entityId).orElse(null);//serviceAreaMapper.dtoToDomain(entityDTO, new CycleAvoidingMappingContext());
            entity = serviceAreaService.get(entityId);
            if (entity != null) {
                ApplicationLogger.logger.info("ServiceArea from DB ::::::::::::::: " + entityId + " ::::: Name :::::: " + entity.getName());

                // Put into cache with 1-minute expiration
                cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES);
                entity.setId(entityId);
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Long fromNetworkDeviceToId(NetworkDevices entity) {
        return entity == null ? null : entity.getId();
    }

    NetworkDevices fromNetworkDeviceIdToNetworkDevice(Long entityId) {
        if (entityId == null) {
            return null;
        }
        NetworkDevices entity;
        String cacheKey = cacheKeys.NETWORK_DEVICE + entityId;
        try {
            entity = (NetworkDevices) cacheService.getFromCache(cacheKey, NetworkDevices.class);
            if (entity != null) {
                ApplicationLogger.logger.info("NetworkDevice from cache ::::: " + entityId + " ::::: Name ::::: " + entity.getName());
                entity.setId(entityId);
                return entity;
            }
            Optional<NetworkDevices> deviceOptional = networkDeviceRepository.findById(entityId);

            if (deviceOptional.isPresent()) {
                entity = deviceOptional.get();

                Integer currentMvnoId = getMvnoIdFromCurrentStaff(); // current user's MVNO
                Integer entityMvnoId = entity.getMvnoId();           // entity's MVNO

                if (currentMvnoId == 1 || entityMvnoId == 1 || currentMvnoId.equals(entityMvnoId)) {
                    ApplicationLogger.logger.info("NetworkDevice from DB ::::: " + entityId + " ::::: Name ::::: " + entity.getName());

                    cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES); // Cache for 1 minute

                    entity.setId(entityId);
                    return entity;
                } else {
                    // MVNO mismatch, access denied
                    return null;
                }
            }
            else
            {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromSalesRepToId(StaffUser staffUser) {
        return staffUser == null ? null : staffUser.getId();
    }

    StaffUser fromSalesRepIdToSalesRep(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        StaffUser entity;
        String cacheKey = cacheKeys.STAFFUSER + entityId;

        try {
            entity = (StaffUser) cacheService.getFromCache(cacheKey, StaffUser.class);
            if (entity != null) {
                ApplicationLogger.logger.info("SalesRep from cache ::::: " + entityId + " ::::: Name ::::: " + entity.getFullName());
                entity.setId(entityId);
                return entity;
            }

            entity = staffUserRepository.findById(entityId).get();
            if (entity != null) {
                ApplicationLogger.logger.info("SalesRep from DB ::::: " + entityId + " ::::: Name ::::: " + entity.getFullName());
                cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES); // cache for 1 minute
                entity.setId(entityId);
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    Integer fromPartnerToId(Partner partner) {
        return partner == null ? null : partner.getId();
    }

    Partner fromPartnerIdToPartner(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Partner entity=null;
        String cacheKey = cacheKeys.PARTNER + entityId;
        try {
            entity = (Partner) cacheService.getFromCache(cacheKey, Partner.class);
            if (entity != null) {
                ApplicationLogger.logger.info("Partner from cache ::::: " + entityId + " ::::: Name ::::: " + entity.getName());
                entity.setId(entityId);
                return entity;
            }

            Optional<Partner> partner = partnerRepository.findById(entityId);
            if (partner.isPresent()) {
                entity = partner.get();
                ApplicationLogger.logger.info("Partner from DB ::::: " + entityId + " ::::: Name ::::: " + entity.getName());
                cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES);
                entity.setId(entityId);
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
    
    Integer fromPlanGroupToId(PlanGroup plangroup) {
        return plangroup == null ? null : plangroup.getPlanGroupId();
    }

    PlanGroup fromPlanGroupIdToPlanGroup(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PlanGroup entity;
        String cacheKey = cacheKeys.PLANGROUP + entityId;
        try {
            entity = (PlanGroup) cacheService.getFromCache(cacheKey, PlanGroup.class);
            if (entity != null) {
                ApplicationLogger.logger.info("PlanGroup from cache ::::: " + entityId + " ::::: Name ::::: " + entity.getPlanGroupName());
                entity.setPlanGroupId(entityId);
                return entity;
            }
            entity = planGroupRepository.findById(entityId).get();
            if (entity != null) {
                ApplicationLogger.logger.info("PlanGroup from DB ::::: " + entityId + " ::::: Name ::::: " + entity.getPlanGroupName());
                cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES);
                entity.setPlanGroupId(entityId);
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @Mapping(target = "city", source = "cityId")
    @Mapping(target = "country", source = "countryId")
    @Mapping(target = "state", source = "stateId")
    @Mapping(target = "area", source = "areaId")
    @Mapping(target = "pincode", source = "pincodeId")
    @Mapping(source = "subareaId", target = "subarea", qualifiedByName = "mapSubAreaById")
    @Mapping(source = "building_mgmt_id", target = "buildingManagement", qualifiedByName = "mapByBuildingManagementId")
    protected abstract CustomerAddress mapAddressPojoToAddress(CustomerAddressPojo addressPojo, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "cityId", source = "city")
    @Mapping(target = "countryId", source = "country")
    @Mapping(target = "stateId", source = "state")
    @Mapping(target = "areaId", source = "area")
    @Mapping(target = "pincodeId", source = "pincode")
    @Mapping(target = "subareaId", source="subarea" ,qualifiedByName = "mapSubAreaToId")
    @Mapping(target = "building_mgmt_id",source = "buildingManagement", qualifiedByName = "mapByBuildingManagementToId")
    protected abstract CustomerAddressPojo mapAddressToAddressPojo(CustomerAddress address, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "qospolicyId", source = "qospolicy")
    public abstract CustPlanMapppingPojo mapCustPlanMapToCustPlanMapPojo(CustPlanMappping planMapping, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "qospolicy", source = "qospolicyId")
    @Mapping(target = "planGroup", source = "plangroupid")
    public abstract CustPlanMappping mapCustPlanMapPojoToCustPlanMap(CustPlanMapppingPojo planMapppingPojo, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "planId", source = "postpaidPlan")
    protected abstract CustQuotaDtlsPojo mapCustQuotaToCustQuotaPojo(CustQuotaDetails custQuotaDetails, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "postpaidPlan", source = "planId")
    protected abstract CustQuotaDetails mapCustQuotaPojoToCustQuota(CustQuotaDtlsPojo custQuotaDetailsPojo, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "planId", source = "postpaidPlan")
    protected abstract DebitDocumentPojo mapDebitDocToDebitDocPojo(DebitDocument debitDocument, @Context CycleAvoidingMappingContext context);

    @Mapping(target = "postpaidPlan", source = "planId")
    protected abstract DebitDocument mapDebitDocPojoToDebitDoc(DebitDocumentPojo debitDocumentPojo, @Context CycleAvoidingMappingContext context);

    public abstract List<CustPlanMapppingPojo> mapCustPlanMapListToCustPlanMapPojoList(List<CustPlanMappping> planMapping, @Context CycleAvoidingMappingContext context);


    protected City fromCityIdToCity(Integer cityId) {
        if (cityId == null) {
            return null;
        }
        String cacheKey = cacheKeys.CITY + cityId;
        City city;
        try {
            city = (City) cacheService.getFromCache(cacheKey, City.class);

            if (city != null) {
                ApplicationLogger.logger.info("City from cache ::::::::::::::: " + cityId + " ::::: Name :::::: " + city.getName());
                city.setId(cityId);
                return city;
            }

            // If not in cache, fetch from DB
            city = cityService.findById(cityId).get();
            if (city!=null) {
                ApplicationLogger.logger.info("City from DB ::::::::::::::: " + cityId + " ::::: Name :::::: " + city.getName());

                // Put in cache
                cacheService.putInCacheWithdynamicExpire(cacheKey, city, 10, TimeUnit.MINUTES);
                city.setId(cityId);
                return city;
            }

        } catch (Exception e) {
            e.printStackTrace();
            city = null;
        }
        return city;
    }

    protected Area fromAreaIdToArea(Integer areaId) {
        if (areaId == null) {
            return null;
        }
        String cacheKey = cacheKeys.AREA + areaId;
        Area area;
        try {
            area = (Area) cacheService.getFromCache(cacheKey, Area.class);

            if (area != null) {
                ApplicationLogger.logger.info("Area from cache ::::::::::::::: " + areaId + " ::::: Name :::::: " + area.getName());
                area.setId(areaId.longValue());
                return area;
            }

//            area = areaService.getMapper().dtoToDomain(areaService.getEntityById(areaId.longValue()), new CycleAvoidingMappingContext());

            Optional<Area> areaOptional = areaRepository.findById(areaId.longValue());

            if (areaOptional.isPresent()) {
                area = areaOptional.get();

                Integer currentMvnoId = getMvnoIdFromCurrentStaff(); // get current MVNO
                Integer entityMvnoId = area.getMvnoId();              // MVNO from entity

                if (currentMvnoId == 1 || entityMvnoId == 1 || currentMvnoId.equals(entityMvnoId)) {
                    ApplicationLogger.logger.info("Area from DB ::::::::::::::: " + areaId + " ::::: Name :::::: " + area.getName());

                    cacheService.putInCacheWithdynamicExpire(cacheKey, area, 10, TimeUnit.MINUTES);

                    area.setId(areaId.longValue());
                    return area;
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            area = null;
        }
        return area;
    }

    Integer fromAreaToAreaId(Area entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    protected Pincode fromPincodeIdToPincode(Integer pincodeId) {
        if (pincodeId == null) {
            return null;
        }
        Pincode pincode;
        String cacheKey = cacheKeys.PINCODE + pincodeId;
        try {
            pincode = (Pincode) cacheService.getFromCache(cacheKey, Pincode.class);

            if (pincode != null) {
                ApplicationLogger.logger.info("Pincode from cache ::::::::::::::: " + pincodeId + " ::::: Code :::::: " + pincode.getPincode());
                pincode.setId(pincodeId.longValue());
                return pincode;
            }

//            pincode = pincodeService.getMapper().dtoToDomain(pincodeService.getEntityById(pincodeId.longValue()), new CycleAvoidingMappingContext());
            Optional<Pincode> pincodeOptional = pincodeRepository.findById(pincodeId.longValue());
            if (pincodeOptional.isPresent()) {
                pincode = pincodeOptional.get();

                Integer currentMvnoId = getMvnoIdFromCurrentStaff(); // fetch current staff MVNO
                Integer entityMvnoId = pincode.getMvnoId();           // from entity

                if (currentMvnoId == 1 || entityMvnoId == 1 || currentMvnoId.equals(entityMvnoId)) {
                    ApplicationLogger.logger.info("Pincode from DB ::::::::::::::: " + pincodeId + " ::::: Code :::::: " + pincode.getPincode());
                    cacheService.putInCacheWithdynamicExpire(cacheKey, pincode, 10, TimeUnit.MINUTES);
                    pincode.setId(pincodeId.longValue());
                    return pincode;
                } else {
                    return null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            pincode = null;
        }
        return pincode;
    }

    Integer fromPincodeToPincodeId(Pincode entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    protected State fromStateIdToState(Integer stateId) {
        if (stateId == null) {
            return null;
        }
        String cacheKey = cacheKeys.STATE + stateId;
        State state;
        try {
            state = (State) cacheService.getFromCache(cacheKey, State.class);

            if (state != null) {
                ApplicationLogger.logger.info("State from cache ::::::::::::::: " + stateId + " ::::: Name :::::: " + state.getName());
                state.setId(stateId);
                return state;
            }
            state = stateService.findById(stateId).get();
            if (state != null) {
                ApplicationLogger.logger.info("State from DB ::::::::::::::: " + stateId + " ::::: Name :::::: " + state.getName());

                // Put into cache with 1-minute expiry
                cacheService.putInCacheWithdynamicExpire(cacheKey, state, 10, TimeUnit.MINUTES);
                state.setId(stateId);
                return state;
            }
        } catch (Exception e) {
            e.printStackTrace();
            state = null;
        }
        return state;
    }

    protected Country fromCountryIdToCountry(Integer countryId) {
        if (countryId == null) {
            return null;
        }
        Country country;
        String cacheKey = cacheKeys.COUNTRY + countryId;
        try {
            country = (Country) cacheService.getFromCache(cacheKey, Country.class);

            if (country != null) {
                ApplicationLogger.logger.info("Country from cache ::::::::::::::: " + countryId + " ::::: Name :::::: " + country.getName());
                country.setId(countryId);
                return country;
            }
            country = countryService.findById(countryId).get();
            if(country!=null) {
                ApplicationLogger.logger.info("Country from DB ::::::::::::::: " + countryId + " ::::: Name :::::: " + country.getName());
                cacheService.putInCacheWithdynamicExpire(cacheKey, country, 10, TimeUnit.MINUTES);
                country.setId(countryId);
                return country;
            }
        } catch (Exception e) {
            e.printStackTrace();
            country = null;
        }
        return country;
    }
    @Named("mapSubAreaById")
    SubArea mapSubAreaById(Long subareaId) {
        return (subareaId != null) ? new SubArea(subareaId) : null;
    }
    @Named("mapByBuildingManagementId")
    BuildingManagement mapByBuildingManagementId(Long buildingmanagementid) {
        return (buildingmanagementid != null) ? new BuildingManagement(buildingmanagementid) : null;
    }
    Integer fromCityToCityId(City entity) {
        return entity == null ? null : entity.getId();
    }

    Integer fromStateToStateId(State entity) {
        return entity == null ? null : entity.getId();
    }

    Integer fromCountryToCountryId(Country entity) {
        return entity == null ? null : entity.getId();
    }

    Integer fromPostPaidPlanToId(PostpaidPlan entity) {
        return entity == null ? null : entity.getId();
    }

    PostpaidPlan fromIdToPostPaidPlan(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        PostpaidPlan entity;
        String cacheKey = cacheKeys.POSTPAIDPLAN + entityId;
        try {

            entity = (PostpaidPlan) cacheService.getFromCache(cacheKey, PostpaidPlan.class);

            if (entity != null) {
                ApplicationLogger.logger.info("PostpaidPlan from cache ::::::::::::::: " + entityId + " ::::: Name :::::: " + entity.getName());
                entity.setId(entityId);
                return entity;
            }
            entity = planService.findById(entityId).get();
            if (entity != null) {
                ApplicationLogger.logger.info("PostpaidPlan from DB ::::::::::::::: " + entityId + " ::::: Name :::::: " + entity.getName());

                // Cache with 1-minute expiry
                cacheService.putInCacheWithdynamicExpire(cacheKey, entity, 10, TimeUnit.MINUTES);
                entity.setId(entityId);
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }
    @Named("mapSubAreaToId")
     Long mapAreaToId(SubArea subArea) {
        return (subArea != null) ? subArea.getId() : null;
    }
    @Named("mapByBuildingManagementToId")
    Long mapToBuildingmanagemntToId(BuildingManagement buildingManagement){
        return (buildingManagement!=null)?buildingManagement.getBuildingMgmtId():null;
    }

    public Integer getMvnoIdFromCurrentStaff() {
        Integer mvnoId = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                mvnoId = ((LoggedInUser) securityContext.getAuthentication().getPrincipal()).getMvnoId();
            }
        } catch (Exception e) {
            //        ApplicationLogger.logger.error("MVNO - getMvnoIdFromCurrentStaff" + e.getMessage(), e);
        }
        return mvnoId;
    }


}
