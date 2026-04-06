package com.adopt.apigw.MicroSeviceDataShare.SharedServices;


import com.adopt.apigw.MicroSeviceDataShare.MessageSender.DataSharedMessageSender;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.*;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.*;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.BusinessVerticals.domain.BusinessVerticals;
import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import com.adopt.apigw.modules.ChangePlanDTOs.*;
import com.adopt.apigw.modules.InventoryManagement.Product_Plan_Mapping.dto.Productplanmappingdto;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.adopt.apigw.modules.PriceGroup.domain.ServiceCommission;
import com.adopt.apigw.modules.Region.domain.Region;
import com.adopt.apigw.modules.VoucherBatch.module.VoucherBatchDto;
import com.adopt.apigw.modules.qosPolicy.domain.QOSPolicy;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.Teams;

import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.SaveClientServMessge;
import com.adopt.apigw.pojo.AdditionalInformationDTO;
import com.adopt.apigw.pojo.FlagDTO;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.message.SavePricebookSharedMessage;
import com.adopt.apigw.rabbitMq.message.SaveVoucherBatchSharedDataMessage;
import com.adopt.apigw.rabbitMq.message.UpdatePricebookSharedMessage;
import com.adopt.apigw.repository.postpaid.CustomerChargeHistoryRepo;
import com.adopt.apigw.repository.postpaid.PostpaidPlanChargeRepo;
import com.adopt.apigw.repository.postpaid.PostpaidPlanRepo;
import com.adopt.apigw.repository.postpaid.ServiceAreaPlangroupMappingRepo;
import com.adopt.apigw.spring.LoggedInUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class CreateDataSharedService {

    @Autowired
    DataSharedMessageSender messageSender;

    static Javers javers = JaversBuilder.javers().build();
    @Autowired
    private PostpaidPlanChargeRepo postpaidPlanChargeRepo;

    @Autowired
    private CustomerChargeHistoryRepo customerChargeHistoryRepo;

    @Autowired
    private ServiceAreaPlangroupMappingRepo serviceAreaPlangroupMappingRepo;

    @Autowired
    private PlanGroupMappingChargeRelRepo planGroupMappingChargeRelRepo;
    @Autowired
    private PostpaidPlanRepo postpaidPlanRepo;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;



    /*
     * Below methods are common for all microservices date sharing, Do not change in the below methods
     * If any additional data is required to be sent to any microservice, prefer to create a constructor in message class
     */

    //SAVE ENTITY COMMON SERVICE
    public void sendEntitySaveDataForAllMicroService(Object object) throws JsonProcessingException {

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {

            //All data of State entity while saving
            SaveStateSharedDataMessage saveStateSharedDataMessage = new SaveStateSharedDataMessage();
            saveStateSharedDataMessage.setId(((State) object).getId());
            saveStateSharedDataMessage.setStatus(((State) object).getStatus());
            saveStateSharedDataMessage.setCountry(((State) object).getCountry());
            saveStateSharedDataMessage.setName(((State) object).getName());
            saveStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            saveStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            saveStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            saveStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            saveStateSharedDataMessage.setCreatedByName(((State) object).getCreatedByName());
            saveStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            saveStateSharedDataMessage.setLastModifiedByName(((State) object).getLastModifiedByName());


            // All the messages from microservies are to be sent from here
          /*  messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_INVENTORY);*/
            //messageSender.send(saveStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            kafkaMessageSender.send(new KafkaMessageData(saveStateSharedDataMessage,SaveStateSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {

            //All data of Country entity while saving
            SaveCountrySharedDataMessage saveCountrySharedDataMessage = new SaveCountrySharedDataMessage();
            saveCountrySharedDataMessage.setId(((Country) object).getId());
            saveCountrySharedDataMessage.setName(((Country) object).getName());
            saveCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            saveCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            saveCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            saveCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());
            saveCountrySharedDataMessage.setCreatedByName(((Country) object).getCreatedByName());
            saveCountrySharedDataMessage.setLastModifiedByName(((Country) object).getLastModifiedByName());

            // All the messages from microservies are to be sent from here
          /*  messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_INVENTORY); */
           // messageSender.send(saveCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            kafkaMessageSender.send(new KafkaMessageData(saveCountrySharedDataMessage,SaveCountrySharedDataMessage.class.getSimpleName()));


        } else if (Objects.nonNull(object) && object.getClass().equals(City.class)) {

            //All data of City entity while saving
            SaveCitySharedDataMessage saveCitySharedDataMessage = new SaveCitySharedDataMessage();
            saveCitySharedDataMessage.setId(((City) object).getId());
            saveCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            saveCitySharedDataMessage.setStatus(((City) object).getStatus());
            saveCitySharedDataMessage.setState(((City) object).getState());
            saveCitySharedDataMessage.setName(((City) object).getName());
            saveCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            saveCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            saveCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            saveCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
           /* messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_INVENTORY);*/
            //messageSender.send(saveCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveCitySharedDataMessage,SaveCitySharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)) {

            //All data of Pincode entity while saving
            SavePincodeSharedDataMessage savePincodeSharedDataMessage = new SavePincodeSharedDataMessage();
            savePincodeSharedDataMessage.setId(((Pincode) object).getId());
            savePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            savePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            savePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            savePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            savePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            savePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            savePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            savePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            savePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
           /* messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_TICKET);
            messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_INVENTORY);*/
            //messageSender.send(savePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(savePincodeSharedDataMessage,SavePincodeSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Area.class)) {

            //All data of Area entity while saving
            SaveAreaSharedDataMessage saveAreaSharedDataMessage = new SaveAreaSharedDataMessage();
            saveAreaSharedDataMessage.setId(((Area) object).getId());
            saveAreaSharedDataMessage.setName(((Area) object).getName());
            saveAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            saveAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            saveAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            saveAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            saveAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            saveAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            saveAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            saveAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            saveAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
           /* messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(saveAreaSharedDataMessage, SharedDataConstants.QUEUE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);*/
            kafkaMessageSender.send(new KafkaMessageData(saveAreaSharedDataMessage,SaveAreaSharedDataMessage.class.getSimpleName()));


        } else if (Objects.nonNull(object) && object.getClass().equals(ServiceArea.class)) {

            SaveServiceAreaSharedDataMessge saveServiceAreaSharedDataMessge = new SaveServiceAreaSharedDataMessge();

            //All data of ServiceArea entity while saving
            saveServiceAreaSharedDataMessge.setId(((ServiceArea) object).getId());
            saveServiceAreaSharedDataMessge.setAreaId(((ServiceArea) object).getAreaId());
            saveServiceAreaSharedDataMessge.setCityid(((ServiceArea) object).getCityid());
            saveServiceAreaSharedDataMessge.setLongitude(((ServiceArea) object).getLongitude());
            saveServiceAreaSharedDataMessge.setLatitude(((ServiceArea) object).getLatitude());
            saveServiceAreaSharedDataMessge.setName(((ServiceArea) object).getName());
            saveServiceAreaSharedDataMessge.setIsDeleted(((ServiceArea) object).getIsDeleted());
            saveServiceAreaSharedDataMessge.setPincodeList(((ServiceArea) object).getPincodeList());
            saveServiceAreaSharedDataMessge.setMvnoId(((ServiceArea) object).getMvnoId());
            saveServiceAreaSharedDataMessge.setStatus(((ServiceArea) object).getStatus());
            saveServiceAreaSharedDataMessge.setCreatedById(((ServiceArea) object).getCreatedById());

            // All the messages from microservies are to be sent from here
          /*  messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_INVENTORY);*/
            //messageSender.send(saveServiceAreaSharedDataMessge, SharedDataConstants.QUEUE_SERVICE_AREA_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveServiceAreaSharedDataMessge,SaveServiceAreaSharedDataMessge.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)) {

            SaveBusinessUnitSharedDataMessage saveBusinessUnitSharedDataMessge = new SaveBusinessUnitSharedDataMessage();

            //All data of BusinessUnit entity while saving
            saveBusinessUnitSharedDataMessge.setId(((BusinessUnit) object).getId());
            saveBusinessUnitSharedDataMessge.setBuname(((BusinessUnit) object).getBuname());
            saveBusinessUnitSharedDataMessge.setBucode(((BusinessUnit) object).getBucode());
            saveBusinessUnitSharedDataMessge.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            saveBusinessUnitSharedDataMessge.setMvnoId(((BusinessUnit) object).getMvnoId());
            saveBusinessUnitSharedDataMessge.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            saveBusinessUnitSharedDataMessge.setStatus(((BusinessUnit) object).getStatus());
            saveBusinessUnitSharedDataMessge.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            saveBusinessUnitSharedDataMessge.setCreatedById(((BusinessUnit) object).getCreatedById());
            saveBusinessUnitSharedDataMessge.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
            /*messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_INVENTORY);*/
            //messageSender.send(saveBusinessUnitSharedDataMessge, SharedDataConstants.QUEUE_BUSINESS_UNIT_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            kafkaMessageSender.send(new KafkaMessageData(saveBusinessUnitSharedDataMessge,SaveBusinessUnitSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Branch.class)) {
            SaveBranchSharedDataMessage saveBranchSharedDataMessage = new SaveBranchSharedDataMessage();

            //All this data for branch saving
            saveBranchSharedDataMessage.setId(((Branch) object).getId());
            saveBranchSharedDataMessage.setName(((Branch) object).getName());
            saveBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            saveBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            saveBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            saveBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            saveBranchSharedDataMessage.setServiceAreaNameList(((Branch) object).getServiceAreaNameList());
            saveBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            saveBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            saveBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            saveBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            saveBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            saveBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            saveBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());
            saveBranchSharedDataMessage.setCreatedByName(((Branch) object).getCreatedByName());
            saveBranchSharedDataMessage.setLastModifiedByName(((Branch) object).getLastModifiedByName());
            // All the messages from microservies are to be sent from here
           /* messageSender.send(saveBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_INVENTORY); */
            //messageSender.send(saveBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveBranchSharedDataMessage,SaveBranchSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Teams.class)) {
            SaveTeamsSharedSharedData saveTeamsSharedSharedData = new SaveTeamsSharedSharedData();

            //All this data for teams saving

            saveTeamsSharedSharedData.setId(((Teams) object).getId());
            saveTeamsSharedSharedData.setName(((Teams) object).getName());
            saveTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            saveTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            saveTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            saveTeamsSharedSharedData.setPartner(((Teams) object).getPartner());
            saveTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            saveTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            saveTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            saveTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            saveTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            saveTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
           /* messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_INVENTORY); */
            //messageSender.send(saveTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveTeamsSharedSharedData,SaveTeamsSharedSharedData.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Hierarchy.class)) {

            SaveHierarchyShareDataMessage saveHierarchyShareDataMessage = new SaveHierarchyShareDataMessage();

            saveHierarchyShareDataMessage.setId(((Hierarchy) object).getId());
            saveHierarchyShareDataMessage.setHierarchyName(((Hierarchy) object).getHierarchyName());
            saveHierarchyShareDataMessage.setEventName(((Hierarchy) object).getEventName());
            saveHierarchyShareDataMessage.setBuId(((Hierarchy) object).getBuId());
            saveHierarchyShareDataMessage.setLcoId(((Hierarchy) object).getLcoId());
            saveHierarchyShareDataMessage.setMvnoId(((Hierarchy) object).getMvnoId());
            saveHierarchyShareDataMessage.setTeamHierarchyMappingList(((Hierarchy) object).getTeamHierarchyMappingList());
            saveHierarchyShareDataMessage.setIsDeleted(((Hierarchy) object).getIsDeleted());
            saveHierarchyShareDataMessage.setCreatedById(((Hierarchy) object).getCreatedById());
            saveHierarchyShareDataMessage.setLastModifiedById(((Hierarchy) object).getLastModifiedById());
            /*  messageSender.send(saveHierarchyShareDataMessage, SharedDataConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_TICKET);
            messageSender.send(saveHierarchyShareDataMessage, SharedDataConstants.QUEUE_HIERARCHY_CREATE_DATA_SHARE_INVENTORY); */
            kafkaMessageSender.send(new KafkaMessageData(saveHierarchyShareDataMessage,SaveHierarchyShareDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {

//            All data of MVNO entity while saving

            SaveMvnoSharedDataMessage saveMvnoSharedDataMessage = new SaveMvnoSharedDataMessage();
            saveMvnoSharedDataMessage.setId(((Mvno) object).getId());
            saveMvnoSharedDataMessage.setName(((Mvno) object).getName());
            saveMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            saveMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            saveMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            saveMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            saveMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            saveMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            saveMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            saveMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            saveMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            saveMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            saveMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            saveMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            saveMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());

//            All the messages from microservies are to be sent from here
         /*   messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_TICKET);   */
            //messageSender.send(saveMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveMvnoSharedDataMessage,SaveMvnoSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {

//            All data of Role entity while saving

            SaveRoleSharedDataMessage saveRoleSharedDataMessage = new SaveRoleSharedDataMessage();
            saveRoleSharedDataMessage.setId(((Role) object).getId());
            saveRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            saveRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            saveRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
//            saveRoleSharedDataMessage.setAclEntry(((Role) object).getAclEntry());
            saveRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            saveRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            saveRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            saveRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            saveRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());
//            All the message from microservices are to be sent from here
         /*   messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_TICKET);   */
            //messageSender.send(saveRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveRoleSharedDataMessage,SaveRoleSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {

//            All data of Staff user entity while saving

            SaveStaffUserSharedDataMessage staffUserSharedDataMessage = new SaveStaffUserSharedDataMessage();
            staffUserSharedDataMessage.setId(((StaffUser) object).getId());
            staffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            staffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            staffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            staffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            staffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if (((StaffUser) object).getLast_login_time() != null) {
                staffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                staffUserSharedDataMessage.setLast_login_time(null);
            }
            staffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            staffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            staffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            staffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            staffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            staffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            staffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            staffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            staffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            staffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            staffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());
            if (((StaffUser) object).getStaffUserparent() != null) {
                staffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            staffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            staffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());

            //            All the message from microservices are to be sent from here


            SaveStaffUserSharedDataMessage ticketStaffData = new SaveStaffUserSharedDataMessage((StaffUser) object);
          /*  messageSender.send(ticketStaffData, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_TICKET);
            messageSender.send(ticketStaffData, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_INVENTORY);   */
            //messageSender.send(ticketStaffData, SharedDataConstants.QUEUE_STAFF_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            kafkaMessageSender.send(new KafkaMessageData(ticketStaffData,SaveStaffUserSharedDataMessage.class.getSimpleName()));


        } else if (Objects.nonNull(object) && object.getClass().equals(PlanService.class)) {

            //            All data of Services entity while saving
            SaveServicesSharedDataMessage saveServicesSharedDataMessage = new SaveServicesSharedDataMessage();
            saveServicesSharedDataMessage.setId(((PlanService) object).getId());
            saveServicesSharedDataMessage.setName(((PlanService) object).getName());
            saveServicesSharedDataMessage.setIcname(((PlanService) object).getIcname());
            saveServicesSharedDataMessage.setIccode(((PlanService) object).getIccode());
            saveServicesSharedDataMessage.setMvnoId(((PlanService) object).getMvnoId());
            saveServicesSharedDataMessage.setBuId(((PlanService) object).getBuId());
            saveServicesSharedDataMessage.setIsQoSV(((PlanService) object).getIsQoSV());
            saveServicesSharedDataMessage.setExpiry(((PlanService) object).getExpiry());
            saveServicesSharedDataMessage.setLedgerId(((PlanService) object).getLedgerId());
            saveServicesSharedDataMessage.setIs_dtv(((PlanService) object).getIs_dtv());
            saveServicesSharedDataMessage.setInvestmentid(((PlanService) object).getInvestmentid());
            saveServicesSharedDataMessage.setProductCategories(((PlanService) object).getProductCategories());
            saveServicesSharedDataMessage.setServiceParamMappingList(((PlanService) object).getServiceParamMappingList());
            saveServicesSharedDataMessage.setFeasibility(((PlanService) object).getFeasibility());
            saveServicesSharedDataMessage.setPoc(((PlanService) object).getPoc());
            saveServicesSharedDataMessage.setInstallation(((PlanService) object).getInstallation());
            saveServicesSharedDataMessage.setProvisioning(((PlanService) object).getProvisioning());
            saveServicesSharedDataMessage.setIsPriceEditable(((PlanService) object).getIsPriceEditable());
            saveServicesSharedDataMessage.setFeasibilityTeamId(((PlanService) object).getFeasibilityTeamId());
            saveServicesSharedDataMessage.setPocTeamId(((PlanService) object).getPocTeamId());
            saveServicesSharedDataMessage.setInstallationTeamId(((PlanService) object).getInstallationTeamId());
            saveServicesSharedDataMessage.setProvisioningTeamId(((PlanService) object).getProvisioningTeamId());
            saveServicesSharedDataMessage.setIsDeleted(((PlanService) object).getIsDeleted());
            saveServicesSharedDataMessage.setCreatedById(((PlanService) object).getCreatedById());
            saveServicesSharedDataMessage.setLastModifiedById(((PlanService) object).getLastModifiedById());

//            All the message from microservices are to be sent from here
//            messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_INVENTORY);
//            messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_TICKET);
            //messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_REVENUEMANAGEMENT);
//            messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_PARTNER);
            //messageSender.send(saveServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_CREATE_DATA_SHARE_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(saveServicesSharedDataMessage,saveServicesSharedDataMessage.getClass().getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            // All data of Region entity while saving
            SaveRegionSharedDataMessage saveRegionSharedDataMessage = new SaveRegionSharedDataMessage();
            saveRegionSharedDataMessage.setId(((Region) object).getId());
            saveRegionSharedDataMessage.setRname(((Region) object).getRname());
            saveRegionSharedDataMessage.setBranchidList(((Region) object).getBranchidList());
            saveRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            saveRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            saveRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            saveRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            saveRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            saveRegionSharedDataMessage.setCreatedByName(((Region) object).getCreatedByName());
            saveRegionSharedDataMessage.setLastModifiedByName(((Region) object).getLastModifiedByName());

            // All the messages from microservices are to be sent from here
/*
            messageSender.send(saveRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_CREATE_DATA_SHARE_TICKET);
*/
            kafkaMessageSender.send(new KafkaMessageData(saveRegionSharedDataMessage,SaveRegionSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {

            //            All data of Partner entity while saving
            SavePartnerSharedDataMessage savePartnerSharedDataMessage = new SavePartnerSharedDataMessage();
            savePartnerSharedDataMessage.setId(((Partner) object).getId());
            savePartnerSharedDataMessage.setName(((Partner) object).getName());
            savePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            savePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            savePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            savePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            savePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            savePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
            savePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()).toString());
            savePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()).toString());
            savePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            savePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            savePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            savePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            savePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            savePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            savePartnerSharedDataMessage.setState(((Partner) object).getState());
            savePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            savePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            savePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            savePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            savePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            savePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            savePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            savePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            savePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
            savePartnerSharedDataMessage.setServiceAreaList(((Partner) object).getServiceAreaList());
            savePartnerSharedDataMessage.setParentPartner(((Partner) object).getParentPartner());
            savePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            savePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            savePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            savePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            savePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            savePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            savePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            savePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            savePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            savePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            savePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            savePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            savePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            savePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            savePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            savePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            savePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            savePartnerSharedDataMessage.setPriceBookId(((Partner) object).getPriceBookId().getId());
            savePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            if(((Partner) object).getParentPartner()!=null)
                savePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                savePartnerSharedDataMessage.setParentPartnerId(null);
            savePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            savePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());
            // All the message from microservices are to be sent from here
            //messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_INVENTORY);
            //messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            //messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_API_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(savePartnerSharedDataMessage,savePartnerSharedDataMessage.getClass().getSimpleName(),KafkaConstant.CREATE_PARTNER));
//            messageSender.send(savePartnerSharedDataMessage, SharedDataConstants.QUEUE_CREATE_PARTNER_REVENUE);
//            SavePartnerSharedDataMessage message = new SavePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_CREATE_DATA_SHARE_TICKET);
//            kafkaMessageSender.send(new KafkaMessageData(message,SavePartnerSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Tax.class)) {

            //            All data of Tax entity while saving
            SaveTaxSharedDataMessage saveTaxSharedDataMessage = new SaveTaxSharedDataMessage();
            saveTaxSharedDataMessage.setId(((Tax) object).getId());
            saveTaxSharedDataMessage.setName(((Tax) object).getName());
            saveTaxSharedDataMessage.setDesc(((Tax) object).getDesc());
            saveTaxSharedDataMessage.setTaxtype(((Tax) object).getTaxtype());
            saveTaxSharedDataMessage.setStatus(((Tax) object).getStatus());
            saveTaxSharedDataMessage.setMvnoId(((Tax) object).getMvnoId());
            saveTaxSharedDataMessage.setBuId(((Tax) object).getBuId());
            saveTaxSharedDataMessage.setTieredList(((Tax) object).getTieredList());
            saveTaxSharedDataMessage.setSlabList(((Tax) object).getSlabList());
            saveTaxSharedDataMessage.setIsDelete(((Tax) object).getIsDelete());
            saveTaxSharedDataMessage.setCreatedById(((Tax) object).getCreatedById());
            saveTaxSharedDataMessage.setLastModifiedById(((Tax) object).getLastModifiedById());
            //            All the message from microservices are to be sent from here
           /* messageSender.send(saveTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(saveTaxSharedDataMessage,SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_REVENUEMANAGEMENT);
            messageSender.send(saveTaxSharedDataMessage,SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER);*/

            kafkaMessageSender.send(new KafkaMessageData(saveTaxSharedDataMessage,SaveTaxSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(PostpaidPlan.class)) {
            //            All data of Plan entity while saving
            SavePlanSharedDataMessage savePlanSharedDataMessage = new SavePlanSharedDataMessage();
            savePlanSharedDataMessage.setId(((PostpaidPlan) object).getId());
            savePlanSharedDataMessage.setName(((PostpaidPlan) object).getName());
            savePlanSharedDataMessage.setDisplayName(((PostpaidPlan) object).getDisplayName());
            savePlanSharedDataMessage.setCode(((PostpaidPlan) object).getCode());
            savePlanSharedDataMessage.setDesc(((PostpaidPlan) object).getDesc());
            savePlanSharedDataMessage.setCategory(((PostpaidPlan) object).getCategory());
            savePlanSharedDataMessage.setMaxChild(((PostpaidPlan) object).getMaxChild());
            savePlanSharedDataMessage.setStartDate(String.valueOf(((PostpaidPlan) object).getStartDate()));
            savePlanSharedDataMessage.setEndDate(String.valueOf(((PostpaidPlan) object).getEndDate()));
            savePlanSharedDataMessage.setQuota(((PostpaidPlan) object).getQuota());
            savePlanSharedDataMessage.setQuotaUnit(((PostpaidPlan) object).getQuotaUnit());
            savePlanSharedDataMessage.setUploadQOS(((PostpaidPlan) object).getUploadQOS());
            savePlanSharedDataMessage.setDownloadQOS(((PostpaidPlan) object).getDownloadQOS());
            savePlanSharedDataMessage.setUploadTs(((PostpaidPlan) object).getUploadTs());
            savePlanSharedDataMessage.setDownloadTs(((PostpaidPlan) object).getDownloadTs());
            savePlanSharedDataMessage.setAllowOverUsage(((PostpaidPlan) object).getAllowOverUsage());
            savePlanSharedDataMessage.setStatus(((PostpaidPlan) object).getStatus());
            savePlanSharedDataMessage.setPlanStatus(((PostpaidPlan) object).getPlanStatus());
            savePlanSharedDataMessage.setChildQuota(((PostpaidPlan) object).getChildQuota());
            savePlanSharedDataMessage.setChildQuotaUnit(((PostpaidPlan) object).getChildQuotaUnit());
            savePlanSharedDataMessage.setSlice(((PostpaidPlan) object).getSlice());
            savePlanSharedDataMessage.setSliceUnit(((PostpaidPlan) object).getSliceUnit());
            savePlanSharedDataMessage.setAttachedToAllHotSpots(((PostpaidPlan) object).getAttachedToAllHotSpots());
            savePlanSharedDataMessage.setParam1(((PostpaidPlan) object).getParam1());
            savePlanSharedDataMessage.setParam2(((PostpaidPlan) object).getParam2());
            savePlanSharedDataMessage.setUsageQuotaType(((PostpaidPlan) object).getUsageQuotaType());
            savePlanSharedDataMessage.setMvnoId(((PostpaidPlan) object).getMvnoId());
            savePlanSharedDataMessage.setTaxId(((PostpaidPlan) object).getTaxId());
            savePlanSharedDataMessage.setServiceId(((PostpaidPlan) object).getServiceId());
            savePlanSharedDataMessage.setTimebasepolicyId(((PostpaidPlan) object).getTimebasepolicyId());
            savePlanSharedDataMessage.setPlantype(((PostpaidPlan) object).getPlantype());
            savePlanSharedDataMessage.setDbr(((PostpaidPlan) object).getDbr());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getChargeList())) {
                List<PostpaidPlanCharge> chargeList = ((PostpaidPlan) object).getChargeList().stream().map(postpaidPlanCharge -> new PostpaidPlanCharge(postpaidPlanCharge)).collect(Collectors.toList());
                savePlanSharedDataMessage.setChargeList(chargeList);
            }
            savePlanSharedDataMessage.setPlanGroup(((PostpaidPlan) object).getPlanGroup());
            savePlanSharedDataMessage.setValidity(((PostpaidPlan) object).getValidity());
            savePlanSharedDataMessage.setSaccode(((PostpaidPlan) object).getSaccode());
            savePlanSharedDataMessage.setMaxconcurrentsession(((PostpaidPlan) object).getMaxconcurrentsession());
            savePlanSharedDataMessage.setQuotatime(((PostpaidPlan) object).getQuotatime());
            savePlanSharedDataMessage.setQuotaunittime(((PostpaidPlan) object).getQuotaunittime());
            savePlanSharedDataMessage.setQuotatype(((PostpaidPlan) object).getQuotatype());
            savePlanSharedDataMessage.setOfferprice(((PostpaidPlan) object).getOfferprice());
            savePlanSharedDataMessage.setQuotadid(((PostpaidPlan) object).getQuotadid());
            savePlanSharedDataMessage.setQuotaintercom(((PostpaidPlan) object).getQuotaintercom());
            if(((PostpaidPlan) object).getQospolicy() != null) {
                savePlanSharedDataMessage.setQospolicy(new QOSPolicy(((PostpaidPlan) object).getQospolicy()));
                savePlanSharedDataMessage.setQospolicy_id(((PostpaidPlan) object).getQospolicy().getId());
                savePlanSharedDataMessage.setQospolicy_name(((PostpaidPlan) object).getQospolicy().getName());
            }
//            savePlanSharedDataMessage.setRadiusprofile(((PostpaidPlan) object).getRadiusprofile());
            savePlanSharedDataMessage.setIsDelete(((PostpaidPlan) object).getIsDelete());
            savePlanSharedDataMessage.setDataCategory(((PostpaidPlan) object).getDataCategory());
            savePlanSharedDataMessage.setTaxamount(((PostpaidPlan) object).getTaxamount());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getServiceAreaNameList())) {
                List<ServiceArea> serviceAreaList = ((PostpaidPlan) object).getServiceAreaNameList().stream().map(ServiceArea::new).collect(Collectors.toList());
                savePlanSharedDataMessage.setServiceAreaNameList(serviceAreaList);
            }
            savePlanSharedDataMessage.setQuotaResetInterval(((PostpaidPlan) object).getQuotaResetInterval());
            savePlanSharedDataMessage.setMode(((PostpaidPlan) object).getMode());
            savePlanSharedDataMessage.setUnitsOfValidity(((PostpaidPlan) object).getUnitsOfValidity());
            savePlanSharedDataMessage.setBuId(((PostpaidPlan) object).getBuId());
            savePlanSharedDataMessage.setNextTeamHierarchyMapping(((PostpaidPlan) object).getNextTeamHierarchyMapping());
            savePlanSharedDataMessage.setNextStaff(((PostpaidPlan) object).getNextStaff());
            savePlanSharedDataMessage.setNewOfferPrice(((PostpaidPlan) object).getNewOfferPrice());
            savePlanSharedDataMessage.setAccessibility(((PostpaidPlan) object).getAccessibility());
            savePlanSharedDataMessage.setProductId(((PostpaidPlan) object).getProductId());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getProductplanmappingList())) {
                List<Productplanmappingdto> list = ((PostpaidPlan) object).getProductplanmappingList().stream().map(Productplanmappingdto::new).collect(Collectors.toList());
                savePlanSharedDataMessage.setProductplanmappingList(list);
            }
            savePlanSharedDataMessage.setInvoiceToOrg(((PostpaidPlan) object).getInvoiceToOrg());
            savePlanSharedDataMessage.setRequiredApproval(((PostpaidPlan) object).getRequiredApproval());
//            savePlanSharedDataMessage.setPlanCasMappingList(((PostpaidPlan) object).getPlanCasMappingList());
            savePlanSharedDataMessage.setBandwidth(((PostpaidPlan) object).getBandwidth());
            savePlanSharedDataMessage.setLink_type(((PostpaidPlan) object).getLink_type());
            savePlanSharedDataMessage.setConnection_type(((PostpaidPlan) object).getConnection_type());
            savePlanSharedDataMessage.setDistance(((PostpaidPlan) object).getDistance());
            savePlanSharedDataMessage.setRam(((PostpaidPlan) object).getRam());
            savePlanSharedDataMessage.setCpu(((PostpaidPlan) object).getCpu());
            savePlanSharedDataMessage.setStorage(((PostpaidPlan) object).getStorage());
            savePlanSharedDataMessage.setStorage_type(((PostpaidPlan) object).getStorage_type());
            savePlanSharedDataMessage.setAuto_backup(((PostpaidPlan) object).getAuto_backup());
            savePlanSharedDataMessage.setCpanel(((PostpaidPlan) object).getCpanel());
            savePlanSharedDataMessage.setLocation(((PostpaidPlan) object).getLocation());
            savePlanSharedDataMessage.setQuantity(((PostpaidPlan) object).getQuantity());
            savePlanSharedDataMessage.setPackage_type(((PostpaidPlan) object).getPackage_type());
            savePlanSharedDataMessage.setNumber_of_days(((PostpaidPlan) object).getNumber_of_days());
            savePlanSharedDataMessage.setNo_of_users(((PostpaidPlan) object).getNo_of_users());
            savePlanSharedDataMessage.setRack_space(((PostpaidPlan) object).getRack_space());
            savePlanSharedDataMessage.setPower_consumption(((PostpaidPlan) object).getPower_consumption());
            savePlanSharedDataMessage.setNetwork_card(((PostpaidPlan) object).getNetwork_card());
            savePlanSharedDataMessage.setIp_or_ip_pool(((PostpaidPlan) object).getIp_or_ip_pool());
            savePlanSharedDataMessage.setNo_of_license(((PostpaidPlan) object).getNo_of_license());
            savePlanSharedDataMessage.setNo_of_email_user_license(((PostpaidPlan) object).getNo_of_email_user_license());
            savePlanSharedDataMessage.setNo_of_server_license(((PostpaidPlan) object).getNo_of_server_license());
            savePlanSharedDataMessage.setNo_of_user_license(((PostpaidPlan) object).getNo_of_user_license());
            savePlanSharedDataMessage.setNo_of_nodes(((PostpaidPlan) object).getNo_of_nodes());
            savePlanSharedDataMessage.setEvent_per_second(((PostpaidPlan) object).getEvent_per_second());
            savePlanSharedDataMessage.setNo_of_additional_server(((PostpaidPlan) object).getNo_of_additional_server());
            savePlanSharedDataMessage.setNo_of_additional_storage(((PostpaidPlan) object).getNo_of_additional_storage());
            savePlanSharedDataMessage.setAdditional_storage_type(((PostpaidPlan) object).getAdditional_storage_type());
            savePlanSharedDataMessage.setEps_License(((PostpaidPlan) object).getEps_License());
            savePlanSharedDataMessage.setNo_of_nodes_license(((PostpaidPlan) object).getNo_of_nodes_license());
            savePlanSharedDataMessage.setHardware_resource(((PostpaidPlan) object).getHardware_resource());
            savePlanSharedDataMessage.setMan_power(((PostpaidPlan) object).getMan_power());
            savePlanSharedDataMessage.setNo_of_domains(((PostpaidPlan) object).getNo_of_domains());
            savePlanSharedDataMessage.setSecurity_modules(((PostpaidPlan) object).getSecurity_modules());
            savePlanSharedDataMessage.setHardware_or_servers(((PostpaidPlan) object).getHardware_or_servers());
            savePlanSharedDataMessage.setCountry(((PostpaidPlan) object).getCountry());
            savePlanSharedDataMessage.setNo_of_vpn(((PostpaidPlan) object).getNo_of_vpn());
            savePlanSharedDataMessage.setDevice_throughput(((PostpaidPlan) object).getDevice_throughput());
            savePlanSharedDataMessage.setRetail(((PostpaidPlan) object).getRetail());
            savePlanSharedDataMessage.setBusinessType(((PostpaidPlan) object).getBusinessType());
            savePlanSharedDataMessage.setBasePlan(((PostpaidPlan) object).getBasePlan());
            savePlanSharedDataMessage.setTemplateId(((PostpaidPlan) object).getTemplateId());
            savePlanSharedDataMessage.setPlanQosMappingEntities(((PostpaidPlan) object).getPlanQosMappingEntities());
            savePlanSharedDataMessage.setCreatedById(((PostpaidPlan) object).getCreatedById());
            savePlanSharedDataMessage.setLastModifiedById(((PostpaidPlan) object).getLastModifiedById());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getPostPaidPlanServiceAreaMappingList())) {
                List<PostPaidPlanServiceAreaMapping> list = ((PostpaidPlan) object).getPostPaidPlanServiceAreaMappingList().stream().map(PostPaidPlanServiceAreaMapping::new).collect(Collectors.toList());
                savePlanSharedDataMessage.setPostPaidPlanServiceAreaMappingList(list);
            }
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getChargeList())) {
                List<PostpaidPlanCharge> chargeList = ((PostpaidPlan) object).getChargeList().stream().map(PostpaidPlanCharge::new).collect(Collectors.toList());
                savePlanSharedDataMessage.setChargeList(chargeList);
            }
            //            chargeList.stream().forEach(postpaidPlanCharge -> {
//                postpaidPlanCharge.setCreatedate(null);
//            });
//            if(!CollectionUtils.isEmpty(chargeList))
//                savePlanSharedDataMessage.setChargeList(chargeList);
            savePlanSharedDataMessage.setIsApprove(false);

            //            All the message from microservices are to be sent from here
           /* messageSender.send(savePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(savePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_TICKET);
            messageSender.send(savePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_REVENUEMANAGEMENT);
            messageSender.send(savePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_CREATE_DATA_SHARE_PARTNER);*/

            kafkaMessageSender.send(new KafkaMessageData(savePlanSharedDataMessage,SavePlanSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanGroup.class)) {

            //            All data of Plangroup entity while saving
            SavePlanGroupSharedDataMessage savePlanGroupSharedDataMessage = new SavePlanGroupSharedDataMessage();
            savePlanGroupSharedDataMessage.setPlanGroupId(((PlanGroup) object).getPlanGroupId());
            savePlanGroupSharedDataMessage.setPlanGroupName(((PlanGroup) object).getPlanGroupName());
            savePlanGroupSharedDataMessage.setStatus(((PlanGroup) object).getStatus());
            savePlanGroupSharedDataMessage.setMvnoId(((PlanGroup) object).getMvnoId());
            savePlanGroupSharedDataMessage.setPlantype(((PlanGroup) object).getPlantype());
            savePlanGroupSharedDataMessage.setPlanMode(((PlanGroup) object).getPlanMode());
            savePlanGroupSharedDataMessage.setIsDelete(((PlanGroup) object).getIsDelete());

            List<PlanGroupMapping>  planGroupMapping = ((PlanGroup) object).getPlanMappingList();
            List<PlanGroupMapping> planGroupMappingList =  new ArrayList<>();
            for (PlanGroupMapping data : planGroupMapping){
                PlanGroupMapping planGroupMapping1 = new PlanGroupMapping(data);
                planGroupMappingList.add(planGroupMapping1);
            }
            savePlanGroupSharedDataMessage.setPlanMappingList(planGroupMappingList);

            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = serviceAreaPlangroupMappingRepo.findByPlanGroupAndServiceAreaIn(((PlanGroup) object),((PlanGroup) object).getServicearea());
            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList =  new ArrayList<>();
            for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
                ServiceAreaPlanGroupMapping planGroupMapping1 = new ServiceAreaPlanGroupMapping(data);
                serviceAreaPlanGroupMappingList.add(planGroupMapping1);
            }
            savePlanGroupSharedDataMessage.setServiceAreaPlanGroupMappingList(serviceAreaPlanGroupMappingList);

            List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = planGroupMappingChargeRelRepo.findAllByPlanGroupMappingIn(planGroupMapping);
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList =  new ArrayList<>();
            for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
                PlanGroupMappingChargeRel planGroupMapping1 = new PlanGroupMappingChargeRel(data);
                planGroupMappingChargeRelList.add(planGroupMapping1);
            }
            savePlanGroupSharedDataMessage.setPlanGroupMappingChargeRelsList(planGroupMappingChargeRelList);


            savePlanGroupSharedDataMessage.setDbr(((PlanGroup) object).getDbr());
            savePlanGroupSharedDataMessage.setPlanGroupType(((PlanGroup) object).getPlanGroupType());
            savePlanGroupSharedDataMessage.setCategory(((PlanGroup) object).getCategory());
            savePlanGroupSharedDataMessage.setNextTeamHierarchyMappingId(((PlanGroup) object).getNextTeamHierarchyMappingId());
            savePlanGroupSharedDataMessage.setNextStaff(((PlanGroup) object).getNextStaff());
            savePlanGroupSharedDataMessage.setAccessibility(((PlanGroup) object).getAccessibility());
            savePlanGroupSharedDataMessage.setAllowDiscount(((PlanGroup) object).getInvoiceToOrg());
            savePlanGroupSharedDataMessage.setOfferprice(((PlanGroup) object).getOfferprice());
            savePlanGroupSharedDataMessage.setServicearea(((PlanGroup) object).getServicearea());
            savePlanGroupSharedDataMessage.setProductPlanGroupMappingList(((PlanGroup) object).getProductPlanGroupMappingList());
            savePlanGroupSharedDataMessage.setTemplateId(((PlanGroup) object).getTemplateId());
            savePlanGroupSharedDataMessage.setInvoiceToOrg(((PlanGroup) object).getInvoiceToOrg());
            savePlanGroupSharedDataMessage.setRequiredApproval(((PlanGroup) object).getRequiredApproval());
            savePlanGroupSharedDataMessage.setCreatedById(((PlanGroup) object).getCreatedById());
            savePlanGroupSharedDataMessage.setLastModifiedById(((PlanGroup) object).getLastModifiedById());

            //            All the message from microservices are to be sent from here
            /*messageSender.send(savePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(savePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_PARTNER);
            messageSender.send(savePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_CREATE_DATA_SHARE_REVENUEMANAGEMENT);*/

            kafkaMessageSender.send(new KafkaMessageData(savePlanGroupSharedDataMessage,SavePlanGroupSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {
            // All data of BusinessVerticals entity while saving
            SaveBusinessVerticalSharedDataMessage saveBusinessVerticalsSharedDataMessage = new SaveBusinessVerticalSharedDataMessage();
            saveBusinessVerticalsSharedDataMessage.setId(((BusinessVerticals) object).getId());
            saveBusinessVerticalsSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            saveBusinessVerticalsSharedDataMessage.setBuregionidList(((BusinessVerticals) object).getBuregionidList());
            saveBusinessVerticalsSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            saveBusinessVerticalsSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            saveBusinessVerticalsSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            saveBusinessVerticalsSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            saveBusinessVerticalsSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());
            saveBusinessVerticalsSharedDataMessage.setCreatedByName(((BusinessVerticals) object).getCreatedByName());
            saveBusinessVerticalsSharedDataMessage.setLastModifiedByName(((BusinessVerticals) object).getLastModifiedByName());
            // Send the message
//            messageSender.send(saveBusinessVerticalsSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_CREATE_DATA_SHARE_TICKET);

            kafkaMessageSender.send(new KafkaMessageData(saveBusinessVerticalsSharedDataMessage,SaveBusinessVerticalSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Charge.class)) {

            //            All data of Charge entity while saving
            SaveChargeSharedDataMessage saveChargeSharedDataMessage = new SaveChargeSharedDataMessage();
            saveChargeSharedDataMessage.setId(((Charge) object).getId());
            saveChargeSharedDataMessage.setName(((Charge) object).getName());
            saveChargeSharedDataMessage.setChargetype(((Charge) object).getChargetype());
            saveChargeSharedDataMessage.setPrice(((Charge) object).getPrice());
            saveChargeSharedDataMessage.setTaxId(((Charge) object).getTax().getId());
            saveChargeSharedDataMessage.setDesc(((Charge) object).getDesc());
//            saveChargeSharedDataMessage.setTax(((Charge) object).getTax());
            saveChargeSharedDataMessage.setDbr(((Charge) object).getDbr());
            saveChargeSharedDataMessage.setDiscountid(((Charge) object).getDiscountid());
            saveChargeSharedDataMessage.setIsDelete(((Charge) object).getIsDelete());
            saveChargeSharedDataMessage.setSaccode(((Charge) object).getSaccode());
            saveChargeSharedDataMessage.setServiceList(((Charge) object).getServiceList());
            saveChargeSharedDataMessage.setMvnoId(((Charge) object).getMvnoId());
            saveChargeSharedDataMessage.setBuId(((Charge) object).getBuId());
            saveChargeSharedDataMessage.setService(((Charge) object).getService());
            saveChargeSharedDataMessage.setStatus(((Charge) object).getStatus());
            saveChargeSharedDataMessage.setLedgerId(((Charge) object).getLedgerId());
            saveChargeSharedDataMessage.setRoyalty_payable(((Charge) object).getRoyalty_payable());
            saveChargeSharedDataMessage.setBusinessType(((Charge) object).getBusinessType());
            saveChargeSharedDataMessage.setPushableLedgerId(((Charge) object).getPushableLedgerId());
            saveChargeSharedDataMessage.setCreatedById(((Charge) object).getCreatedById());
            saveChargeSharedDataMessage.setLastModifiedById(((Charge) object).getLastModifiedById());
            saveChargeSharedDataMessage.setProductId(((Charge) object).getProductId());
            saveChargeSharedDataMessage.setInventoryChargeType(((Charge) object).getInventoryChargeType());
            saveChargeSharedDataMessage.setChargecategory(((Charge) object).getChargecategory());
            saveChargeSharedDataMessage.setIsinventorycharge(((Charge) object).getIsinventorycharge());
            saveChargeSharedDataMessage.setActualprice(((Charge) object).getActualprice());
            saveChargeSharedDataMessage.setTaxamount(((Charge) object).getTaxamount());
//            All the message from microservices are to be sent from here
           /* messageSender.send(saveChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_INVENTORY);
            messageSender.send(saveChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_PARTNER);
            messageSender.send(saveChargeSharedDataMessage,SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT);*/

            kafkaMessageSender.send(new KafkaMessageData(saveChargeSharedDataMessage,SaveChargeSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {

            SaveClientServMessge clientServMessge = new SaveClientServMessge();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());
//            messageSender.send(clientServMessge, SharedDataConstants.QUEUE_CLIENT_SERV_SAVE_DATA_SHARE_TICKET_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,SaveClientServMessge.class.getSimpleName(),KafkaConstant.CREATE_SERVICE_CONFIG));



        }
        else if (Objects.nonNull(object) && object.getClass().equals(Tax.class)) {

          SaveTaxSharedDataMessage saveTaxSharedDataMessage=new SaveTaxSharedDataMessage();
          saveTaxSharedDataMessage.setId(((Tax) object).getId());
          saveTaxSharedDataMessage.setName(((Tax) object).getName());
          saveTaxSharedDataMessage.setDesc(((Tax) object).getDesc());
          saveTaxSharedDataMessage.setTaxtype(((Tax) object).getTaxtype());
          saveTaxSharedDataMessage.setStatus(((Tax) object).getStatus());
          saveTaxSharedDataMessage.setMvnoId(((Tax) object).getMvnoId());
          saveTaxSharedDataMessage.setBuId(((Tax) object).getBuId());
          saveTaxSharedDataMessage.setIsDelete(((Tax) object).getIsDelete());
          saveTaxSharedDataMessage.setSlabList(((Tax) object).getSlabList());
          saveTaxSharedDataMessage.setTieredList(((Tax) object).getTieredList());
          /*  messageSender.send(saveTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE);
              messageSender.send(saveTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_CREATE_DATA_SHARE_PARTNER);*/

            kafkaMessageSender.send(new KafkaMessageData(saveTaxSharedDataMessage,SaveTaxSharedDataMessage.class.getSimpleName()));




        } else if (Objects.nonNull(object) && object.getClass().equals(Discount.class)) {

            SaveDiscountSharedMessage saveDiscountSharedMessage=new SaveDiscountSharedMessage();
            saveDiscountSharedMessage.setId(((Discount) object).getId());
            saveDiscountSharedMessage.setName(((Discount) object).getName());
            saveDiscountSharedMessage.setStatus(((Discount) object).getStatus());
            saveDiscountSharedMessage.setMvnoId(((Discount) object).getMvnoId());
           //saveDiscountSharedMessage.setDiscMappingList(((Discount) object).getDiscMappingList());
           List<DiscountMapping> discountMappingList=new ArrayList<>();
           for(DiscountMapping  list : ((Discount) object).getDiscMappingList()){
               discountMappingList.add(new DiscountMapping(list.getId(),list.getDiscountType(),list.getAmount(),list.getValidFrom().toString(),list.getValidUPTO().toString(),list.getDiscount()));
           }
           saveDiscountSharedMessage.setDiscMappingList(discountMappingList);

            saveDiscountSharedMessage.setPlanMappingList(((Discount) object).getPlanMappingList());
            saveDiscountSharedMessage.setBuId(((Discount) object).getBuId());
//            messageSender.send(saveDiscountSharedMessage, SharedDataConstants.QUEUE_DISCOUNT_SAVE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(saveDiscountSharedMessage,SaveDiscountSharedMessage.class.getSimpleName()));

        }else if (Objects.nonNull(object) && object.getClass().equals(PriceBook.class))  {
            SavePricebookSharedMessage savePricebookSharedMessage=new SavePricebookSharedMessage();
            savePricebookSharedMessage.setId(((PriceBook) object).getId());
            savePricebookSharedMessage.setBookname(((PriceBook) object).getBookname());
            if(((PriceBook) object).getValidfrom()!=null){
                savePricebookSharedMessage.setValidfrom(((PriceBook) object).getValidfrom().toString());
            }
            if(((PriceBook) object).getValidto()!=null) {
                savePricebookSharedMessage.setValidto(((PriceBook) object).getValidto().toString());
            }
            savePricebookSharedMessage.setStatus(((PriceBook) object).getStatus());
            savePricebookSharedMessage.setDescription(((PriceBook) object).getDescription());
            savePricebookSharedMessage.setCommission_on(((PriceBook) object).getCommission_on());
            savePricebookSharedMessage.setIsAllPlanSelected(((PriceBook) object).getIsAllPlanSelected());
            savePricebookSharedMessage.setIsAllPlanGroupSelected(((PriceBook) object).getIsAllPlanGroupSelected());
            savePricebookSharedMessage.setRevenueSharePercentage(((PriceBook) object).getRevenueSharePercentage());
            List<PriceBookPlanDetail> priceBookPlanDetails=new ArrayList<>();
            for(PriceBookPlanDetail priceBookPlanDetail:((PriceBook) object).getPriceBookPlanDetailList() ){
                priceBookPlanDetails.add(new PriceBookPlanDetail(priceBookPlanDetail));
            }
            savePricebookSharedMessage.setPriceBookPlanDetailList(priceBookPlanDetails);
            List<ServiceCommission> serviceCommissionList=new ArrayList<>();

            for(ServiceCommission comission: ((PriceBook) object).getServiceCommissionList()){
                serviceCommissionList.add(new ServiceCommission(comission));
            }
            savePricebookSharedMessage.setServiceCommissionList(serviceCommissionList);

            savePricebookSharedMessage.setIsDeleted(((PriceBook) object).getIsDeleted());

            savePricebookSharedMessage.setMvnoId(((PriceBook) object).getMvnoId());
            savePricebookSharedMessage.setAgrPercentage(((PriceBook) object).getAgrPercentage());

            savePricebookSharedMessage.setTdsPercentage(((PriceBook) object).getTdsPercentage());
            List<PriceBookSlabDetails> pricebooklist=new ArrayList<>();
            savePricebookSharedMessage.setBuId(((PriceBook) object).getBuId());
            for(PriceBookSlabDetails pricebook :((PriceBook) object).getPriceBookSlabDetailsList()){
                pricebooklist.add(new PriceBookSlabDetails(pricebook));
            }
            savePricebookSharedMessage.setPriceBookSlabDetailsList(pricebooklist);
            savePricebookSharedMessage.setCreatedBYId(((PriceBook) object).getCreatedById());
            savePricebookSharedMessage.setLastModifiedByname(((PriceBook) object).getLastModifiedByName());

            savePricebookSharedMessage.setRevenueType(((PriceBook) object).getRevenueType());
           /* messageSender.send(savePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_REVENUE);
            messageSender.send(savePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_CREATE_DATA_PARTNER);*/

            kafkaMessageSender.send(new KafkaMessageData(savePricebookSharedMessage,SavePricebookSharedMessage.class.getSimpleName(),"PRICEBOOK_CREATE"));

        } else if (Objects.nonNull(object) && object.getClass().equals(CasMaster.class)) {
            SaveCasMasterSharedDataMessage saveCasMasterSharedDataMessage = new SaveCasMasterSharedDataMessage();
            saveCasMasterSharedDataMessage.setId(((CasMaster) object).getId());
            saveCasMasterSharedDataMessage.setCasname(((CasMaster) object).getCasname());
            saveCasMasterSharedDataMessage.setEndpoint(((CasMaster) object).getEndpoint());
            saveCasMasterSharedDataMessage.setBuId(((CasMaster) object).getBuId());
            saveCasMasterSharedDataMessage.setStatus(((CasMaster) object).getStatus());
            saveCasMasterSharedDataMessage.setIsDeleted(((CasMaster) object).getIsDeleted());
            saveCasMasterSharedDataMessage.setMvnoId(((CasMaster) object).getMvnoId());
            saveCasMasterSharedDataMessage.setCreatedById(((CasMaster) object).getCreatedById());
            saveCasMasterSharedDataMessage.setLastModifiedById(((CasMaster) object).getLastModifiedById());
            saveCasMasterSharedDataMessage.setCasParameterMappings(((CasMaster) object).getCasParameterMappings());
//            messageSender.send(saveCasMasterSharedDataMessage, SharedDataConstants.QUEUE_CASMASTER_CREATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(saveCasMasterSharedDataMessage,SaveCasMasterSharedDataMessage.class.getSimpleName()));

        } else if(Objects.nonNull(object) && object.getClass().equals(VasPlan.class)) {
            SaveVasSharedDataMessage saveVasSharedDataMessage = new SaveVasSharedDataMessage();
            saveVasSharedDataMessage.setId(((VasPlan)object).getId());
            saveVasSharedDataMessage.setName(((VasPlan)object).getName());
            saveVasSharedDataMessage.setPauseDaysLimit(((VasPlan)object).getPauseDaysLimit());
            saveVasSharedDataMessage.setPauseTimeLimit(((VasPlan)object).getPauseTimeLimit());
            saveVasSharedDataMessage.setTatId(((VasPlan)object).getTatId());
            saveVasSharedDataMessage.setInventoryReplaceAfterYears(((VasPlan)object).getInventoryReplaceAfterYears());
            saveVasSharedDataMessage.setInventoryPaidMonths(((VasPlan)object).getInventoryPaidMonths());
            saveVasSharedDataMessage.setInventoryCount(((VasPlan)object).getInventoryCount());
            saveVasSharedDataMessage.setShiftLocationYears(((VasPlan)object).getShiftLocationYears());
            saveVasSharedDataMessage.setShiftLocationMonths(((VasPlan)object).getShiftLocationMonths());
            saveVasSharedDataMessage.setShiftLocationCount(((VasPlan)object).getShiftLocationCount());
            saveVasSharedDataMessage.setPaymentType(((VasPlan)object).getPaymentType());
            saveVasSharedDataMessage.setVasAmount(((VasPlan)object).getVasAmount());
            saveVasSharedDataMessage.setMvnoId(((VasPlan)object).getMvnoId());
            saveVasSharedDataMessage.setIsdelete(((VasPlan)object).getIsdelete());
            saveVasSharedDataMessage.setIsdefault(((VasPlan)object).getIsdefault());
            saveVasSharedDataMessage.setChargeList(((VasPlan)object).getChargeList());

            kafkaMessageSender.send(new KafkaMessageData(saveVasSharedDataMessage,SaveVasSharedDataMessage.class.getSimpleName()));


        }
    }



    //UPDATE ENTITY COMMON SERVICE

    public void updateEntityDataForAllMicroService(Object object) {

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {

            //All data of State entity while updating
            UpdateStateSharedDataMessage updateStateSharedDataMessage = new UpdateStateSharedDataMessage();

            updateStateSharedDataMessage.setId(((State) object).getId());
            updateStateSharedDataMessage.setStatus(((State) object).getStatus());
            updateStateSharedDataMessage.setCountry(((State) object).getCountry());
            updateStateSharedDataMessage.setName(((State) object).getName());
            updateStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            updateStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            updateStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            updateStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());
            updateStateSharedDataMessage.setCreatedByName(((State) object).getCreatedByName());
            updateStateSharedDataMessage.setLastModifiedByName(((State) object).getLastModifiedByName());

            // All the messages from microservies are to be sent from here

//            messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);

            kafkaMessageSender.send(new KafkaMessageData(updateStateSharedDataMessage,UpdateStateSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {

            //All data of Country entity while updating
            UpdateCountrySharedDataMessage updateCountrySharedDataMessage = new UpdateCountrySharedDataMessage();

            updateCountrySharedDataMessage.setId(((Country) object).getId());
            updateCountrySharedDataMessage.setName(((Country) object).getName());
            updateCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            updateCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            updateCountrySharedDataMessage.setIsDelete(((Country) object).getIsDelete());
            updateCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            updateCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCountrySharedDataMessage,UpdateCountrySharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(City.class)) {

            //All data of City entity while updating
            UpdateCitySharedDataMessage updateCitySharedDataMessage = new UpdateCitySharedDataMessage();

            updateCitySharedDataMessage.setId(((City) object).getId());
            updateCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            updateCitySharedDataMessage.setStatus(((City) object).getStatus());
            updateCitySharedDataMessage.setState(((City) object).getState());
            updateCitySharedDataMessage.setName(((City) object).getName());
            updateCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            updateCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            updateCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            updateCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCitySharedDataMessage,UpdateCitySharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {
//            All data of MVNO entity while updating

            UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage = new UpdateMvnoSharedDataMessage();

            updateMvnoSharedDataMessage.setId(((Mvno) object).getId());
            updateMvnoSharedDataMessage.setName(((Mvno) object).getName());
            updateMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            updateMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            updateMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            updateMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            updateMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            updateMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            updateMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            updateMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            updateMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            updateMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            updateMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            updateMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            updateMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateMvnoSharedDataMessage,UpdateMvnoSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {

//            All data of Role entity while updating

            UpdateRoleSharedDataMessage updateRoleSharedDataMessage = new UpdateRoleSharedDataMessage();
            updateRoleSharedDataMessage.setId(((Role) object).getId());
            updateRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            updateRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            updateRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
//            updateRoleSharedDataMessage.setAclEntry(((Role) object).getAclEntry());
            updateRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            updateRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            updateRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            updateRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            updateRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());

//            All the message from microservices are to be sent from here
//            messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateRoleSharedDataMessage,UpdateRoleSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {

//            All data of Staff user entity while updating

            UpdateStaffUserSharedDataMessage updateStaffUserSharedDataMessage = new UpdateStaffUserSharedDataMessage();
            updateStaffUserSharedDataMessage.setId(((StaffUser) object).getId());
            updateStaffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            updateStaffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            updateStaffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            updateStaffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            updateStaffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if (((StaffUser) object).getLast_login_time() != null) {
                updateStaffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                updateStaffUserSharedDataMessage.setLast_login_time(null);
            }
            updateStaffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            updateStaffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            updateStaffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            updateStaffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            updateStaffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            updateStaffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            updateStaffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            updateStaffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            updateStaffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            updateStaffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            updateStaffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());
            if (((StaffUser) object).getStaffUserparent() != null) {
                updateStaffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            updateStaffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            updateStaffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());

            //            All the message from microservices are to be sent from here

            UpdateStaffUserSharedDataMessage ticktStaffUpdateData = new UpdateStaffUserSharedDataMessage((StaffUser) object);
//            messageSender.send(ticktStaffUpdateData, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(ticktStaffUpdateData, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(ticktStaffUpdateData, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(ticktStaffUpdateData,UpdateStaffUserSharedDataMessage.class.getSimpleName()));

        }
         else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)) {

            //All data of City entity while updating
            UpdatePincodeSharedDataMessage updatePincodeSharedDataMessage = new UpdatePincodeSharedDataMessage();

            updatePincodeSharedDataMessage.setId(((Pincode) object).getId());
            updatePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            updatePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            updatePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            updatePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            updatePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            updatePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            updatePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            updatePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            updatePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updatePincodeSharedDataMessage,UpdatePincodeSharedDataMessage.class.getSimpleName()));

        }
         else if (Objects.nonNull(object) && object.getClass().equals(Area.class)) {

            //All data of Area entity while updating
            UpdateAreaSharedDataMessage updateAreaSharedDataMessage = new UpdateAreaSharedDataMessage();
            updateAreaSharedDataMessage.setId(((Area) object).getId());
            updateAreaSharedDataMessage.setName(((Area) object).getName());
            updateAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            updateAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            updateAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            updateAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            updateAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            updateAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            updateAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            updateAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            updateAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());


            // All the messages from microservies are to be sent from here
//            messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateAreaSharedDataMessage,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateAreaSharedDataMessage,UpdateAreaSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanService.class)) {

//            All data of Services entity while updating
            UpdateServicesSharedDataMessage updateServicesSharedDataMessage = new UpdateServicesSharedDataMessage();
            updateServicesSharedDataMessage.setId(((PlanService) object).getId());
            updateServicesSharedDataMessage.setName(((PlanService) object).getName());
            updateServicesSharedDataMessage.setIcname(((PlanService) object).getIcname());
            updateServicesSharedDataMessage.setIccode(((PlanService) object).getIccode());
            updateServicesSharedDataMessage.setMvnoId(((PlanService) object).getMvnoId());
            updateServicesSharedDataMessage.setBuId(((PlanService) object).getBuId());
            updateServicesSharedDataMessage.setIsQoSV(((PlanService) object).getIsQoSV());
            updateServicesSharedDataMessage.setExpiry(((PlanService) object).getExpiry());
            updateServicesSharedDataMessage.setLedgerId(((PlanService) object).getLedgerId());
            updateServicesSharedDataMessage.setIs_dtv(((PlanService) object).getIs_dtv());
            updateServicesSharedDataMessage.setInvestmentid(((PlanService) object).getInvestmentid());
            updateServicesSharedDataMessage.setProductCategories(((PlanService) object).getProductCategories());
            updateServicesSharedDataMessage.setServiceParamMappingList(((PlanService) object).getServiceParamMappingList());
            updateServicesSharedDataMessage.setFeasibility(((PlanService) object).getFeasibility());
            updateServicesSharedDataMessage.setPoc(((PlanService) object).getPoc());
            updateServicesSharedDataMessage.setInstallation(((PlanService) object).getInstallation());
            updateServicesSharedDataMessage.setProvisioning(((PlanService) object).getProvisioning());
            updateServicesSharedDataMessage.setIsPriceEditable(((PlanService) object).getIsPriceEditable());
            updateServicesSharedDataMessage.setFeasibilityTeamId(((PlanService) object).getFeasibilityTeamId());
            updateServicesSharedDataMessage.setPocTeamId(((PlanService) object).getPocTeamId());
            updateServicesSharedDataMessage.setInstallationTeamId(((PlanService) object).getInstallationTeamId());
            updateServicesSharedDataMessage.setProvisioningTeamId(((PlanService) object).getProvisioningTeamId());
            updateServicesSharedDataMessage.setIsDeleted(((PlanService) object).getIsDeleted());
            updateServicesSharedDataMessage.setCreatedById(((PlanService) object).getCreatedById());
            updateServicesSharedDataMessage.setLastModifiedById(((PlanService) object).getLastModifiedById());

//            All the message from microservices are to be sent from here
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET);
           // messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER);
            //messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(updateServicesSharedDataMessage,updateServicesSharedDataMessage.getClass().getSimpleName()));
        }
        else if(Objects.nonNull(object) && object.getClass().equals(ServiceArea.class)){

            UpdateServiceAreaSharedDataMessage updateServiceAreaSharedDataMessage = new UpdateServiceAreaSharedDataMessage();

            //All data of ServiceArea entity while updating
            updateServiceAreaSharedDataMessage.setId(((ServiceArea) object).getId());
            updateServiceAreaSharedDataMessage.setAreaId(((ServiceArea) object).getAreaId());
            updateServiceAreaSharedDataMessage.setCityid(((ServiceArea) object).getCityid());
            updateServiceAreaSharedDataMessage.setLongitude(((ServiceArea) object).getLongitude());
            updateServiceAreaSharedDataMessage.setLatitude(((ServiceArea) object).getLatitude());
            updateServiceAreaSharedDataMessage.setName(((ServiceArea) object).getName());
            updateServiceAreaSharedDataMessage.setIsDeleted(((ServiceArea) object).getIsDeleted());
            updateServiceAreaSharedDataMessage.setPincodeList(((ServiceArea) object).getPincodeList());
            updateServiceAreaSharedDataMessage.setMvnoId(((ServiceArea) object).getMvnoId());
            updateServiceAreaSharedDataMessage.setStatus(((ServiceArea) object).getStatus());
            updateServiceAreaSharedDataMessage.setUpdatedById(((ServiceArea) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY);
           // messageSender.send(updateServiceAreaSharedDataMessage, SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateServiceAreaSharedDataMessage,UpdateServiceAreaSharedDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(Teams.class)){
            UpdateTeamsSharedData updateTeamsSharedSharedData = new UpdateTeamsSharedData();

            //All this data for branch saving

            updateTeamsSharedSharedData.setId(((Teams) object).getId());
            updateTeamsSharedSharedData.setName(((Teams) object).getName());
            updateTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            updateTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            updateTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            updateTeamsSharedSharedData.setPartner(((Teams) object).getPartner());
            updateTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            updateTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            updateTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            updateTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            updateTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            updateTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateTeamsSharedSharedData,SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateTeamsSharedSharedData,UpdateTeamsSharedData.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(Hierarchy.class)){
            UpdateHierarchyShareDataMessage updateHierarchyShareDataMessage = new UpdateHierarchyShareDataMessage();

            updateHierarchyShareDataMessage.setId(((Hierarchy) object).getId());
            updateHierarchyShareDataMessage.setHierarchyName(((Hierarchy) object).getHierarchyName());
            updateHierarchyShareDataMessage.setEventName(((Hierarchy) object).getEventName());
            updateHierarchyShareDataMessage.setBuId(((Hierarchy) object).getBuId());
            updateHierarchyShareDataMessage.setLcoId(((Hierarchy) object).getLcoId());
            updateHierarchyShareDataMessage.setMvnoId(((Hierarchy) object).getMvnoId());
            updateHierarchyShareDataMessage.setTeamHierarchyMappingList(((Hierarchy) object).getTeamHierarchyMappingList());
            updateHierarchyShareDataMessage.setIsDeleted(((Hierarchy) object).getIsDeleted());
            updateHierarchyShareDataMessage.setCreatedById(((Hierarchy) object).getCreatedById());
            updateHierarchyShareDataMessage.setLastModifiedById(((Hierarchy) object).getLastModifiedById());

//            messageSender.send(updateHierarchyShareDataMessage,SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateHierarchyShareDataMessage, SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateHierarchyShareDataMessage,UpdateHierarchyShareDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)){

            UpdateBusinessUnitSharedDataMessage updateBusinessUnitSharedDataMessage = new UpdateBusinessUnitSharedDataMessage();


            //All data of BusinessUnit entity while updating
            updateBusinessUnitSharedDataMessage.setId(((BusinessUnit) object).getId());
            updateBusinessUnitSharedDataMessage.setBuname(((BusinessUnit) object).getBuname());
            updateBusinessUnitSharedDataMessage.setBucode(((BusinessUnit) object).getBucode());
            updateBusinessUnitSharedDataMessage.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            updateBusinessUnitSharedDataMessage.setMvnoId(((BusinessUnit) object).getMvnoId());
            updateBusinessUnitSharedDataMessage.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            updateBusinessUnitSharedDataMessage.setStatus(((BusinessUnit) object).getStatus());
            updateBusinessUnitSharedDataMessage.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            updateBusinessUnitSharedDataMessage.setCreatedById(((BusinessUnit) object).getCreatedById());
            updateBusinessUnitSharedDataMessage.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBusinessUnitSharedDataMessage, SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessUnitSharedDataMessage,UpdateBusinessUnitSharedDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(Branch.class)){
            UpdateBranchSharedData updateBranchSharedDataMessage = new UpdateBranchSharedData();

            //All this data for branch updating
            updateBranchSharedDataMessage.setId(((Branch) object).getId());
            updateBranchSharedDataMessage.setName(((Branch) object).getName());
            updateBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            updateBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            updateBranchSharedDataMessage.setServiceAreaNameList(((Branch) object).getServiceAreaNameList());
            updateBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            updateBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            updateBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            updateBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            updateBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());
            updateBranchSharedDataMessage.setCreatedByName(((Branch) object).getCreatedByName());
            updateBranchSharedDataMessage.setLastModifiedByName(((Branch) object).getLastModifiedByName());
            // All the messages from microservies are to be sent from here
//            messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBranchSharedDataMessage, SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBranchSharedDataMessage,UpdateBranchSharedData.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {

            //            All data of Partner entity while updating
            UpdatePartnerSharedDataMessage updatePartnerSharedDataMessage = new UpdatePartnerSharedDataMessage();
            updatePartnerSharedDataMessage.setId(((Partner) object).getId());
            updatePartnerSharedDataMessage.setName(((Partner) object).getName());
            updatePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            updatePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            updatePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            updatePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            updatePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            updatePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
            updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()));
            updatePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()));
            updatePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            updatePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            updatePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            updatePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            updatePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            updatePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            updatePartnerSharedDataMessage.setState(((Partner) object).getState());
            updatePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            updatePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            updatePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            updatePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            updatePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            updatePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            updatePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            updatePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            updatePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
            updatePartnerSharedDataMessage.setServiceAreaList(((Partner) object).getServiceAreaList());
            updatePartnerSharedDataMessage.setParentPartner(((Partner) object).getParentPartner());
            updatePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            updatePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            updatePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            updatePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            updatePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            updatePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            updatePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            updatePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            updatePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            updatePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            updatePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            updatePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            updatePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            updatePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            updatePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            updatePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            updatePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            updatePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            if(((Partner) object).getParentPartner()!=null)
                updatePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                updatePartnerSharedDataMessage.setParentPartnerId(null);
            updatePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            updatePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());
            //            All the message from microservices are to be sent from here
            //   messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
            //messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(updatePartnerSharedDataMessage,updatePartnerSharedDataMessage.getClass().getSimpleName(),KafkaConstant.UPDATE_PARTNER));
            UpdatePartnerSharedDataMessage message = new UpdatePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET);
//           messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(message,UpdatePartnerSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Tax.class)) {

            //            All data of Tax entity while updating
            UpdateTaxSharedDataMessage updateTaxSharedDataMessage = new UpdateTaxSharedDataMessage();
            updateTaxSharedDataMessage.setId(((Tax) object).getId());
            updateTaxSharedDataMessage.setName(((Tax) object).getName());
            updateTaxSharedDataMessage.setDesc(((Tax) object).getDesc());
            updateTaxSharedDataMessage.setTaxtype(((Tax) object).getTaxtype());
            updateTaxSharedDataMessage.setStatus(((Tax) object).getStatus());
            updateTaxSharedDataMessage.setMvnoId(((Tax) object).getMvnoId());
            updateTaxSharedDataMessage.setBuId(((Tax) object).getBuId());
            updateTaxSharedDataMessage.setTieredList(((Tax) object).getTieredList());
            updateTaxSharedDataMessage.setSlabList(((Tax) object).getSlabList());
            updateTaxSharedDataMessage.setIsDelete(((Tax) object).getIsDelete());
            updateTaxSharedDataMessage.setCreatedById(((Tax) object).getCreatedById());
            updateTaxSharedDataMessage.setLastModifiedById(((Tax) object).getLastModifiedById());
//            messageSender.send(updateTaxSharedDataMessage,SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);
//            messageSender.send(updateTaxSharedDataMessage,SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER);
            //            All the message from microservices are to be sent from here
//            messageSender.send(updateTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateTaxSharedDataMessage,UpdateTaxSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PostpaidPlan.class)) {

            //            All data of Plan entity while updating
            UpdatePlanSharedDataMessage updatePlanSharedDataMessage = new UpdatePlanSharedDataMessage();
            updatePlanSharedDataMessage.setId(((PostpaidPlan) object).getId());
            updatePlanSharedDataMessage.setName(((PostpaidPlan) object).getName());
            updatePlanSharedDataMessage.setDisplayName(((PostpaidPlan) object).getDisplayName());
            updatePlanSharedDataMessage.setCode(((PostpaidPlan) object).getCode());
            updatePlanSharedDataMessage.setDesc(((PostpaidPlan) object).getDesc());
            updatePlanSharedDataMessage.setCategory(((PostpaidPlan) object).getCategory());
            updatePlanSharedDataMessage.setMaxChild(((PostpaidPlan) object).getMaxChild());
            updatePlanSharedDataMessage.setStartDate(String.valueOf(((PostpaidPlan) object).getStartDate()));
            updatePlanSharedDataMessage.setEndDate(String.valueOf(((PostpaidPlan) object).getEndDate()));
            updatePlanSharedDataMessage.setQuota(((PostpaidPlan) object).getQuota());
            updatePlanSharedDataMessage.setQuotaUnit(((PostpaidPlan) object).getQuotaUnit());
            updatePlanSharedDataMessage.setUploadQOS(((PostpaidPlan) object).getUploadQOS());
            updatePlanSharedDataMessage.setDownloadQOS(((PostpaidPlan) object).getDownloadQOS());
            updatePlanSharedDataMessage.setUploadTs(((PostpaidPlan) object).getUploadTs());
            updatePlanSharedDataMessage.setDownloadTs(((PostpaidPlan) object).getDownloadTs());
            updatePlanSharedDataMessage.setAllowOverUsage(((PostpaidPlan) object).getAllowOverUsage());
            updatePlanSharedDataMessage.setStatus(((PostpaidPlan) object).getStatus());
            updatePlanSharedDataMessage.setPlanStatus(((PostpaidPlan) object).getPlanStatus());
            updatePlanSharedDataMessage.setChildQuota(((PostpaidPlan) object).getChildQuota());
            updatePlanSharedDataMessage.setChildQuotaUnit(((PostpaidPlan) object).getChildQuotaUnit());
            updatePlanSharedDataMessage.setSlice(((PostpaidPlan) object).getSlice());
            updatePlanSharedDataMessage.setSliceUnit(((PostpaidPlan) object).getSliceUnit());
            updatePlanSharedDataMessage.setAttachedToAllHotSpots(((PostpaidPlan) object).getAttachedToAllHotSpots());
            updatePlanSharedDataMessage.setParam1(((PostpaidPlan) object).getParam1());
            updatePlanSharedDataMessage.setParam2(((PostpaidPlan) object).getParam2());
            updatePlanSharedDataMessage.setMvnoId(((PostpaidPlan) object).getMvnoId());
            updatePlanSharedDataMessage.setTaxId(((PostpaidPlan) object).getTaxId());
            updatePlanSharedDataMessage.setServiceId(((PostpaidPlan) object).getServiceId());
            updatePlanSharedDataMessage.setTimebasepolicyId(((PostpaidPlan) object).getTimebasepolicyId());
            updatePlanSharedDataMessage.setPlantype(((PostpaidPlan) object).getPlantype());
            updatePlanSharedDataMessage.setDbr(((PostpaidPlan) object).getDbr());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getChargeList())) {
                List<PostpaidPlanCharge> chargeList = ((PostpaidPlan) object).getChargeList().stream().map(PostpaidPlanCharge::new).collect(Collectors.toList());
                updatePlanSharedDataMessage.setChargeList(chargeList);
            }
            updatePlanSharedDataMessage.setPlanGroup(((PostpaidPlan) object).getPlanGroup());
            updatePlanSharedDataMessage.setValidity(((PostpaidPlan) object).getValidity());
            updatePlanSharedDataMessage.setSaccode(((PostpaidPlan) object).getSaccode());
            updatePlanSharedDataMessage.setMaxconcurrentsession(((PostpaidPlan) object).getMaxconcurrentsession());
            updatePlanSharedDataMessage.setQuotatime(((PostpaidPlan) object).getQuotatime());
            updatePlanSharedDataMessage.setQuotaunittime(((PostpaidPlan) object).getQuotaunittime());
            updatePlanSharedDataMessage.setQuotatype(((PostpaidPlan) object).getQuotatype());
            updatePlanSharedDataMessage.setOfferprice(((PostpaidPlan) object).getOfferprice());
            updatePlanSharedDataMessage.setQuotadid(((PostpaidPlan) object).getQuotadid());
            updatePlanSharedDataMessage.setQuotaintercom(((PostpaidPlan) object).getQuotaintercom());
            updatePlanSharedDataMessage.setQospolicy(((PostpaidPlan) object).getQospolicy());
          if(((PostpaidPlan) object).getQospolicy()!=null) {
              updatePlanSharedDataMessage.setQospolicy_id(((PostpaidPlan) object).getQospolicy().getId());
              updatePlanSharedDataMessage.setQospolicy_name(((PostpaidPlan) object).getQospolicy().getName());
          }
//            updatePlanSharedDataMessage.setRadiusprofile(((PostpaidPlan) object).getRadiusprofile());
            updatePlanSharedDataMessage.setIsDelete(((PostpaidPlan) object).getIsDelete());
            updatePlanSharedDataMessage.setDataCategory(((PostpaidPlan) object).getDataCategory());
            updatePlanSharedDataMessage.setTaxamount(((PostpaidPlan) object).getTaxamount());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getServiceAreaNameList())) {
                List<ServiceArea> serviceAreaList = ((PostpaidPlan) object).getServiceAreaNameList().stream().map(ServiceArea::new).collect(Collectors.toList());
                updatePlanSharedDataMessage.setServiceAreaNameList(serviceAreaList);
            }
            updatePlanSharedDataMessage.setQuotaResetInterval(((PostpaidPlan) object).getQuotaResetInterval());
            updatePlanSharedDataMessage.setMode(((PostpaidPlan) object).getMode());
            updatePlanSharedDataMessage.setUnitsOfValidity(((PostpaidPlan) object).getUnitsOfValidity());
            updatePlanSharedDataMessage.setBuId(((PostpaidPlan) object).getBuId());
            updatePlanSharedDataMessage.setNextTeamHierarchyMapping(((PostpaidPlan) object).getNextTeamHierarchyMapping());
            updatePlanSharedDataMessage.setNextStaff(((PostpaidPlan) object).getNextStaff());
            updatePlanSharedDataMessage.setNewOfferPrice(((PostpaidPlan) object).getNewOfferPrice());
            updatePlanSharedDataMessage.setAccessibility(((PostpaidPlan) object).getAccessibility());
            updatePlanSharedDataMessage.setProductId(((PostpaidPlan) object).getProductId());
            if(!CollectionUtils.isEmpty(((PostpaidPlan) object).getProductplanmappingList())) {
                List<Productplanmappingdto> list = ((PostpaidPlan) object).getProductplanmappingList().stream().map(Productplanmappingdto::new).collect(Collectors.toList());
                updatePlanSharedDataMessage.setProductplanmappingList(list);
            }
            updatePlanSharedDataMessage.setInvoiceToOrg(((PostpaidPlan) object).getInvoiceToOrg());
            updatePlanSharedDataMessage.setRequiredApproval(((PostpaidPlan) object).getRequiredApproval());
//            updatePlanSharedDataMessage.setPlanCasMappingList(((PostpaidPlan) object).getPlanCasMappingList());
            updatePlanSharedDataMessage.setBandwidth(((PostpaidPlan) object).getBandwidth());
            updatePlanSharedDataMessage.setLink_type(((PostpaidPlan) object).getLink_type());
            updatePlanSharedDataMessage.setConnection_type(((PostpaidPlan) object).getConnection_type());
            updatePlanSharedDataMessage.setDistance(((PostpaidPlan) object).getDistance());
            updatePlanSharedDataMessage.setRam(((PostpaidPlan) object).getRam());
            updatePlanSharedDataMessage.setCpu(((PostpaidPlan) object).getCpu());
            updatePlanSharedDataMessage.setStorage(((PostpaidPlan) object).getStorage());
            updatePlanSharedDataMessage.setStorage_type(((PostpaidPlan) object).getStorage_type());
            updatePlanSharedDataMessage.setAuto_backup(((PostpaidPlan) object).getAuto_backup());
            updatePlanSharedDataMessage.setCpanel(((PostpaidPlan) object).getCpanel());
            updatePlanSharedDataMessage.setLocation(((PostpaidPlan) object).getLocation());
            updatePlanSharedDataMessage.setQuantity(((PostpaidPlan) object).getQuantity());
            updatePlanSharedDataMessage.setPackage_type(((PostpaidPlan) object).getPackage_type());
            updatePlanSharedDataMessage.setNumber_of_days(((PostpaidPlan) object).getNumber_of_days());
            updatePlanSharedDataMessage.setNo_of_users(((PostpaidPlan) object).getNo_of_users());
            updatePlanSharedDataMessage.setRack_space(((PostpaidPlan) object).getRack_space());
            updatePlanSharedDataMessage.setPower_consumption(((PostpaidPlan) object).getPower_consumption());
            updatePlanSharedDataMessage.setNetwork_card(((PostpaidPlan) object).getNetwork_card());
            updatePlanSharedDataMessage.setIp_or_ip_pool(((PostpaidPlan) object).getIp_or_ip_pool());
            updatePlanSharedDataMessage.setNo_of_license(((PostpaidPlan) object).getNo_of_license());
            updatePlanSharedDataMessage.setNo_of_email_user_license(((PostpaidPlan) object).getNo_of_email_user_license());
            updatePlanSharedDataMessage.setNo_of_server_license(((PostpaidPlan) object).getNo_of_server_license());
            updatePlanSharedDataMessage.setNo_of_user_license(((PostpaidPlan) object).getNo_of_user_license());
            updatePlanSharedDataMessage.setNo_of_nodes(((PostpaidPlan) object).getNo_of_nodes());
            updatePlanSharedDataMessage.setEvent_per_second(((PostpaidPlan) object).getEvent_per_second());
            updatePlanSharedDataMessage.setNo_of_additional_server(((PostpaidPlan) object).getNo_of_additional_server());
            updatePlanSharedDataMessage.setNo_of_additional_storage(((PostpaidPlan) object).getNo_of_additional_storage());
            updatePlanSharedDataMessage.setAdditional_storage_type(((PostpaidPlan) object).getAdditional_storage_type());
            updatePlanSharedDataMessage.setEps_License(((PostpaidPlan) object).getEps_License());
            updatePlanSharedDataMessage.setNo_of_nodes_license(((PostpaidPlan) object).getNo_of_nodes_license());
            updatePlanSharedDataMessage.setHardware_resource(((PostpaidPlan) object).getHardware_resource());
            updatePlanSharedDataMessage.setMan_power(((PostpaidPlan) object).getMan_power());
            updatePlanSharedDataMessage.setNo_of_domains(((PostpaidPlan) object).getNo_of_domains());
            updatePlanSharedDataMessage.setSecurity_modules(((PostpaidPlan) object).getSecurity_modules());
            updatePlanSharedDataMessage.setHardware_or_servers(((PostpaidPlan) object).getHardware_or_servers());
            updatePlanSharedDataMessage.setCountry(((PostpaidPlan) object).getCountry());
            updatePlanSharedDataMessage.setNo_of_vpn(((PostpaidPlan) object).getNo_of_vpn());
            updatePlanSharedDataMessage.setDevice_throughput(((PostpaidPlan) object).getDevice_throughput());
            updatePlanSharedDataMessage.setRetail(((PostpaidPlan) object).getRetail());
            updatePlanSharedDataMessage.setBusinessType(((PostpaidPlan) object).getBusinessType());
            updatePlanSharedDataMessage.setBasePlan(((PostpaidPlan) object).getBasePlan());
            updatePlanSharedDataMessage.setTemplateId(((PostpaidPlan) object).getTemplateId());
//            updatePlanSharedDataMessage.setPlanQosMappingEntities(((PostpaidPlan) object).getPlanQosMappingEntities());
            updatePlanSharedDataMessage.setCreatedById(((PostpaidPlan) object).getCreatedById());
            updatePlanSharedDataMessage.setLastModifiedById(((PostpaidPlan) object).getLastModifiedById());
            if (((PostpaidPlan) object).getIsApprove() == false) {
                updatePlanSharedDataMessage.setIsApprove(false);
            } else if (((PostpaidPlan) object).getIsApprove() == true) {
                updatePlanSharedDataMessage.setIsApprove(true);
            }
            //            All the message from microservices are to be sent from here
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePlanSharedDataMessage,UpdatePlanSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanGroup.class)) {

            //            All data of Plangroup entity while updating
            UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage = new UpdatePlanGroupSharedDataMessage();
            updatePlanGroupSharedDataMessage.setPlanGroupId(((PlanGroup) object).getPlanGroupId());
            updatePlanGroupSharedDataMessage.setPlanGroupName(((PlanGroup) object).getPlanGroupName());
            updatePlanGroupSharedDataMessage.setStatus(((PlanGroup) object).getStatus());
            updatePlanGroupSharedDataMessage.setMvnoId(((PlanGroup) object).getMvnoId());
            updatePlanGroupSharedDataMessage.setPlantype(((PlanGroup) object).getPlantype());
            updatePlanGroupSharedDataMessage.setPlanMode(((PlanGroup) object).getPlanMode());
            updatePlanGroupSharedDataMessage.setIsDelete(((PlanGroup) object).getIsDelete());
//            updatePlanGroupSharedDataMessage.setPlanMappingList(((PlanGroup) object).getPlanMappingList());
            updatePlanGroupSharedDataMessage.setDbr(((PlanGroup) object).getDbr());
            updatePlanGroupSharedDataMessage.setPlanGroupType(((PlanGroup) object).getPlanGroupType());
            updatePlanGroupSharedDataMessage.setCategory(((PlanGroup) object).getCategory());
            updatePlanGroupSharedDataMessage.setNextTeamHierarchyMappingId(((PlanGroup) object).getNextTeamHierarchyMappingId());
            updatePlanGroupSharedDataMessage.setNextStaff(((PlanGroup) object).getNextStaff());
            updatePlanGroupSharedDataMessage.setAccessibility(((PlanGroup) object).getAccessibility());
            updatePlanGroupSharedDataMessage.setAllowDiscount(((PlanGroup) object).getInvoiceToOrg());
            updatePlanGroupSharedDataMessage.setOfferprice(((PlanGroup) object).getOfferprice());
            updatePlanGroupSharedDataMessage.setServicearea(((PlanGroup) object).getServicearea());
            updatePlanGroupSharedDataMessage.setProductPlanGroupMappingList(((PlanGroup) object).getProductPlanGroupMappingList());
            updatePlanGroupSharedDataMessage.setTemplateId(((PlanGroup) object).getTemplateId());
            updatePlanGroupSharedDataMessage.setInvoiceToOrg(((PlanGroup) object).getInvoiceToOrg());
            updatePlanGroupSharedDataMessage.setRequiredApproval(((PlanGroup) object).getRequiredApproval());
            updatePlanGroupSharedDataMessage.setCreatedById(((PlanGroup) object).getCreatedById());
            updatePlanGroupSharedDataMessage.setLastModifiedById(((PlanGroup) object).getLastModifiedById());


            List<PlanGroupMapping>  planGroupMapping = ((PlanGroup) object).getPlanMappingList();
            List<PlanGroupMapping> planGroupMappingList =  new ArrayList<>();
            for (PlanGroupMapping data : planGroupMapping){
                PlanGroupMapping planGroupMapping1 = new PlanGroupMapping(data);
                planGroupMappingList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setPlanMappingList(planGroupMappingList);

            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = serviceAreaPlangroupMappingRepo.findByPlanGroupAndServiceAreaIn(((PlanGroup) object),((PlanGroup) object).getServicearea());
            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList =  new ArrayList<>();
            for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
                ServiceAreaPlanGroupMapping planGroupMapping1 = new ServiceAreaPlanGroupMapping(data);
                serviceAreaPlanGroupMappingList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setServiceAreaPlanGroupMappingList(serviceAreaPlanGroupMappingList);

            List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = planGroupMappingChargeRelRepo.findAllByPlanGroupMappingIn(planGroupMapping);
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList =  new ArrayList<>();
            for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
                PlanGroupMappingChargeRel planGroupMapping1 = new PlanGroupMappingChargeRel(data);
                planGroupMappingChargeRelList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setPlanGroupMappingChargeRelsList(planGroupMappingChargeRelList);


            //            All the message from microservices are to be sent from here
           /* messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_INVENTORY);
            if (((PlanGroup) object).getStatus().equalsIgnoreCase("Active")) {
                messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER);
                messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);

            }*/
            kafkaMessageSender.send(new KafkaMessageData(updatePlanGroupSharedDataMessage,UpdatePlanGroupSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Charge.class)) {

            //            All data of Charge entity while updating
            UpdateChargeSharedDataMessage updateChargeSharedDataMessage = new UpdateChargeSharedDataMessage();
            updateChargeSharedDataMessage.setId(((Charge) object).getId());
            updateChargeSharedDataMessage.setName(((Charge) object).getName());
            updateChargeSharedDataMessage.setChargetype(((Charge) object).getChargetype());
            updateChargeSharedDataMessage.setPrice(((Charge) object).getPrice());
            updateChargeSharedDataMessage.setTaxId(((Charge) object).getTax().getId());
            updateChargeSharedDataMessage.setDbr(((Charge) object).getDbr());
            updateChargeSharedDataMessage.setDiscountid(((Charge) object).getDiscountid());
            updateChargeSharedDataMessage.setIsDelete(((Charge) object).getIsDelete());
            updateChargeSharedDataMessage.setSaccode(((Charge) object).getSaccode());
            updateChargeSharedDataMessage.setServiceList(((Charge) object).getServiceList());
            updateChargeSharedDataMessage.setMvnoId(((Charge) object).getMvnoId());
            updateChargeSharedDataMessage.setBuId(((Charge) object).getBuId());
            updateChargeSharedDataMessage.setService(((Charge) object).getService());
            updateChargeSharedDataMessage.setStatus(((Charge) object).getStatus());
            updateChargeSharedDataMessage.setLedgerId(((Charge) object).getLedgerId());
            updateChargeSharedDataMessage.setRoyalty_payable(((Charge) object).getRoyalty_payable());
            updateChargeSharedDataMessage.setBusinessType(((Charge) object).getBusinessType());
            updateChargeSharedDataMessage.setPushableLedgerId(((Charge) object).getPushableLedgerId());
            updateChargeSharedDataMessage.setCreatedById(((Charge) object).getCreatedById());
            updateChargeSharedDataMessage.setLastModifiedById(((Charge) object).getLastModifiedById());
            updateChargeSharedDataMessage.setProductId(((Charge) object).getProductId());
            updateChargeSharedDataMessage.setInventoryChargeType(((Charge) object).getInventoryChargeType());
            updateChargeSharedDataMessage.setChargecategory(((Charge) object).getChargecategory());
            updateChargeSharedDataMessage.setIsinventorycharge(((Charge) object).getIsinventorycharge());
            updateChargeSharedDataMessage.setActualprice(((Charge) object).getActualprice());
            updateChargeSharedDataMessage.setTaxamount(((Charge) object).getTaxamount());

//            All the message from microservices are to be sent from here
//            messageSender.send(updateChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateChargeSharedDataMessage,SharedDataConstants.QUEUE_CHARGE_CREATE_DATA_SHARE_REVENUEMANAGEMENT);
//            messageSender.send(updateChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateChargeSharedDataMessage,UpdateChargeSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            // All data of Region entity while saving
            UpdateRegionSharedDataMessage updateRegionSharedDataMessage = new UpdateRegionSharedDataMessage();
            updateRegionSharedDataMessage.setId(((Region) object).getId());
            updateRegionSharedDataMessage.setRname(((Region) object).getRname());
            updateRegionSharedDataMessage.setBranchidList(((Region) object).getBranchidList());
            updateRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            updateRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            updateRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            updateRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            updateRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            updateRegionSharedDataMessage.setCreatedByName(((Region) object).getCreatedByName());
            updateRegionSharedDataMessage.setLastModifiedByName(((Region) object).getLastModifiedByName());

            // All the messages from microservices are to be sent from here
//            messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET);

            kafkaMessageSender.send(new KafkaMessageData(updateRegionSharedDataMessage,UpdateRegionSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {
            // All data of BusinessVerticals entity while saving
            UpdateBusinessVerticalSharedDataMessage updateBusinessVerticalSharedDataMessage = new UpdateBusinessVerticalSharedDataMessage();
            updateBusinessVerticalSharedDataMessage.setId(((BusinessVerticals) object).getId());
            updateBusinessVerticalSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            updateBusinessVerticalSharedDataMessage.setBuregionidList(((BusinessVerticals) object).getBuregionidList());
            updateBusinessVerticalSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            updateBusinessVerticalSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            updateBusinessVerticalSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            updateBusinessVerticalSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            updateBusinessVerticalSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());
            updateBusinessVerticalSharedDataMessage.setCreatedByName(((BusinessVerticals) object).getCreatedByName());
            updateBusinessVerticalSharedDataMessage.setLastModifiedByName(((BusinessVerticals) object).getLastModifiedByName());

            // Send the message
//            messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessVerticalSharedDataMessage,UpdateBusinessVerticalSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {
            UpdateClientServMessage clientServMessge = new UpdateClientServMessage();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());
//            messageSender.send(clientServMessge, SharedDataConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,UpdateClientServMessage.class.getSimpleName(),KafkaConstant.UPDATE_SERVICE_CONFIG));


        }else if (Objects.nonNull(object) && object.getClass().equals(PriceBook.class))  {
            UpdatePricebookSharedMessage updatePricebookSharedMessage=new UpdatePricebookSharedMessage();
            updatePricebookSharedMessage.setId(((PriceBook) object).getId());
            updatePricebookSharedMessage.setBookname(((PriceBook) object).getBookname());
            if(((PriceBook) object).getValidfrom()!=null){
                updatePricebookSharedMessage.setValidfrom(((PriceBook) object).getValidfrom().toString());
            }
            if(((PriceBook) object).getValidto()!=null) {
                updatePricebookSharedMessage.setValidto(((PriceBook) object).getValidto().toString());
            }
            updatePricebookSharedMessage.setStatus(((PriceBook) object).getStatus());
            updatePricebookSharedMessage.setDescription(((PriceBook) object).getDescription());
            updatePricebookSharedMessage.setCommission_on(((PriceBook) object).getCommission_on());
            updatePricebookSharedMessage.setIsAllPlanSelected(((PriceBook) object).getIsAllPlanSelected());
            updatePricebookSharedMessage.setIsAllPlanGroupSelected(((PriceBook) object).getIsAllPlanGroupSelected());
            updatePricebookSharedMessage.setRevenueSharePercentage(((PriceBook) object).getRevenueSharePercentage());
            List<PriceBookPlanDetail> priceBookPlanDetails=new ArrayList<>();
            for(PriceBookPlanDetail priceBookPlanDetail:((PriceBook) object).getPriceBookPlanDetailList() ){
                priceBookPlanDetails.add(new PriceBookPlanDetail(priceBookPlanDetail));
            }
            updatePricebookSharedMessage.setPriceBookPlanDetailList(priceBookPlanDetails);
            List<ServiceCommission> serviceCommissionList=new ArrayList<>();

            for(ServiceCommission comission: ((PriceBook) object).getServiceCommissionList()){
                serviceCommissionList.add(new ServiceCommission(comission));
            }
            updatePricebookSharedMessage.setServiceCommissionList(serviceCommissionList);

            updatePricebookSharedMessage.setIsDeleted(((PriceBook) object).getIsDeleted());

            updatePricebookSharedMessage.setMvnoId(((PriceBook) object).getMvnoId());
            updatePricebookSharedMessage.setAgrPercentage(((PriceBook) object).getAgrPercentage());

            updatePricebookSharedMessage.setTdsPercentage(((PriceBook) object).getTdsPercentage());
            List<PriceBookSlabDetails> pricebooklist=new ArrayList<>();
            updatePricebookSharedMessage.setBuId(((PriceBook) object).getBuId());
            for(PriceBookSlabDetails pricebook :((PriceBook) object).getPriceBookSlabDetailsList()){
                pricebooklist.add(new PriceBookSlabDetails(pricebook));
            }
            updatePricebookSharedMessage.setPriceBookSlabDetailsList(pricebooklist);
            updatePricebookSharedMessage.setCreatedBYId(((PriceBook) object).getCreatedById());
            updatePricebookSharedMessage.setLastModifiedByname(((PriceBook) object).getLastModifiedByName());

            updatePricebookSharedMessage.setRevenueType(((PriceBook) object).getRevenueType());
//            messageSender.send(updatePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE);
//            messageSender.send(updatePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePricebookSharedMessage,UpdatePricebookSharedMessage.class.getSimpleName(),"PRICEBOOK_UPDATE"));

        } else if (Objects.nonNull(object) && object.getClass().equals(CasMaster.class)) {
            UpdateCasMasterSharedDataMessage updateCasMasterSharedDataMessage = new UpdateCasMasterSharedDataMessage();
            updateCasMasterSharedDataMessage.setId(((CasMaster) object).getId());
            updateCasMasterSharedDataMessage.setCasname(((CasMaster) object).getCasname());
            updateCasMasterSharedDataMessage.setEndpoint(((CasMaster) object).getEndpoint());
            updateCasMasterSharedDataMessage.setBuId(((CasMaster) object).getBuId());
            updateCasMasterSharedDataMessage.setStatus(((CasMaster) object).getStatus());
            updateCasMasterSharedDataMessage.setIsDeleted(((CasMaster) object).getIsDeleted());
            updateCasMasterSharedDataMessage.setMvnoId(((CasMaster) object).getMvnoId());
            updateCasMasterSharedDataMessage.setCasParameterMappings(((CasMaster) object).getCasParameterMappings());
            updateCasMasterSharedDataMessage.setCreatedById(((CasMaster) object).getCreatedById());
            updateCasMasterSharedDataMessage.setLastModifiedById(((CasMaster) object).getLastModifiedById());
//            messageSender.send(updateCasMasterSharedDataMessage, SharedDataConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateCasMasterSharedDataMessage,UpdateCasMasterSharedDataMessage.class.getSimpleName()));
        }else if (Objects.nonNull(object) && object.getClass().equals(VasPlan.class)) {
            UpdateVasSharedDataMessage updateVasSharedDataMessage = new UpdateVasSharedDataMessage();
            updateVasSharedDataMessage.setId(((VasPlan)object).getId());
            updateVasSharedDataMessage.setName(((VasPlan)object).getName());
            updateVasSharedDataMessage.setPauseDaysLimit(((VasPlan)object).getPauseDaysLimit());
            updateVasSharedDataMessage.setPauseTimeLimit(((VasPlan)object).getPauseTimeLimit());
            updateVasSharedDataMessage.setTatId(((VasPlan)object).getTatId());
            updateVasSharedDataMessage.setInventoryReplaceAfterYears(((VasPlan)object).getInventoryReplaceAfterYears());
            updateVasSharedDataMessage.setInventoryPaidMonths(((VasPlan)object).getInventoryPaidMonths());
            updateVasSharedDataMessage.setInventoryCount(((VasPlan)object).getInventoryCount());
            updateVasSharedDataMessage.setShiftLocationYears(((VasPlan)object).getShiftLocationYears());
            updateVasSharedDataMessage.setShiftLocationMonths(((VasPlan)object).getShiftLocationMonths());
            updateVasSharedDataMessage.setShiftLocationCount(((VasPlan)object).getShiftLocationCount());
            updateVasSharedDataMessage.setPaymentType(((VasPlan)object).getPaymentType());
            updateVasSharedDataMessage.setVasAmount(((VasPlan)object).getVasAmount());
            updateVasSharedDataMessage.setMvnoId(((VasPlan)object).getMvnoId());
            updateVasSharedDataMessage.setIsdelete(((VasPlan)object).getIsdelete());
            updateVasSharedDataMessage.setIsdefault(((VasPlan)object).getIsdefault());
            updateVasSharedDataMessage.setChargeList(((VasPlan)object).getChargeList());


            kafkaMessageSender.send(new KafkaMessageData(updateVasSharedDataMessage,UpdateVasSharedDataMessage.class.getSimpleName()));

        }
    }


    //DELETE ENTITY COMMON SERVICE
    public void deleteEntityDataForAllMicroService(Object object){

        if (Objects.nonNull(object) && object.getClass().equals(State.class)) {

            //All data of State entity while deleting
            UpdateStateSharedDataMessage updateStateSharedDataMessage = new UpdateStateSharedDataMessage();
            updateStateSharedDataMessage.setId(((State) object).getId());
            updateStateSharedDataMessage.setStatus(((State) object).getStatus());
            updateStateSharedDataMessage.setCountry(((State) object).getCountry());
            updateStateSharedDataMessage.setName(((State) object).getName());
            updateStateSharedDataMessage.setMvnoId(((State) object).getMvnoId());
            updateStateSharedDataMessage.setIsDeleted(((State) object).getIsDeleted());
            updateStateSharedDataMessage.setCreatedById(((State) object).getCreatedById());
            updateStateSharedDataMessage.setLastModifiedById(((State) object).getLastModifiedById());


            // All the messages from microservies are to be sent from here
//            messageSender.send(updateStateSharedDataMessage,SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateStateSharedDataMessage, SharedDataConstants.QUEUE_STATE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateStateSharedDataMessage,UpdateStateSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Country.class)) {

            //All data of Country entity while deleting
            UpdateCountrySharedDataMessage updateCountrySharedDataMessage = new UpdateCountrySharedDataMessage();
            updateCountrySharedDataMessage.setId(((Country) object).getId());
            updateCountrySharedDataMessage.setName(((Country) object).getName());
            updateCountrySharedDataMessage.setStatus(((Country) object).getStatus());
            updateCountrySharedDataMessage.setMvnoId(((Country) object).getMvnoId());
            updateCountrySharedDataMessage.setIsDelete(((Country) object).getIsDelete());
            updateCountrySharedDataMessage.setCreatedById(((Country) object).getCreatedById());
            updateCountrySharedDataMessage.setLastModifiedById(((Country) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCountrySharedDataMessage, SharedDataConstants.QUEUE_COUNTRY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCountrySharedDataMessage,UpdateCountrySharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(City.class)){

            //All data of City entity while deleting
            UpdateCitySharedDataMessage updateCitySharedDataMessage = new UpdateCitySharedDataMessage();
            updateCitySharedDataMessage.setId(((City) object).getId());
            updateCitySharedDataMessage.setCountryId(((City) object).getCountryId());
            updateCitySharedDataMessage.setStatus(((City) object).getStatus());
            updateCitySharedDataMessage.setState(((City) object).getState());
            updateCitySharedDataMessage.setName(((City) object).getName());
            updateCitySharedDataMessage.setMvnoId(((City) object).getMvnoId());
            updateCitySharedDataMessage.setIsDelete(((City) object).getIsDelete());
            updateCitySharedDataMessage.setCreatedById(((City) object).getCreatedById());
            updateCitySharedDataMessage.setLastModifiedById(((City) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateCitySharedDataMessage,SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateCitySharedDataMessage, SharedDataConstants.QUEUE_CITY_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateCitySharedDataMessage,UpdateCitySharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Mvno.class)) {
//            All data of MVNO entity while deleting

            UpdateMvnoSharedDataMessage updateMvnoSharedDataMessage = new UpdateMvnoSharedDataMessage();

            updateMvnoSharedDataMessage.setId(((Mvno) object).getId());
            updateMvnoSharedDataMessage.setName(((Mvno) object).getName());
            updateMvnoSharedDataMessage.setUsername(((Mvno) object).getUsername());
            updateMvnoSharedDataMessage.setPassword(((Mvno) object).getPassword());
            updateMvnoSharedDataMessage.setSuffix(((Mvno) object).getSuffix());
            updateMvnoSharedDataMessage.setDescription(((Mvno) object).getDescription());
            updateMvnoSharedDataMessage.setEmail(((Mvno) object).getEmail());
            updateMvnoSharedDataMessage.setPhone(((Mvno) object).getPhone());
            updateMvnoSharedDataMessage.setStatus(((Mvno) object).getStatus());
            updateMvnoSharedDataMessage.setLogfile(((Mvno) object).getLogfile());
            updateMvnoSharedDataMessage.setMvnoHeader(((Mvno) object).getMvnoHeader());
            updateMvnoSharedDataMessage.setMvnoFooter(((Mvno) object).getMvnoFooter());
            updateMvnoSharedDataMessage.setIsDelete(((Mvno) object).getIsDelete());
            updateMvnoSharedDataMessage.setCreatedById(((Mvno) object).getCreatedById());
            updateMvnoSharedDataMessage.setLastModifiedById(((Mvno) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
            // messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateMvnoSharedDataMessage, SharedDataConstants.QUEUE_MVNO_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateMvnoSharedDataMessage,UpdateMvnoSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Role.class)) {

//            All data of Role entity while deleting

            UpdateRoleSharedDataMessage updateRoleSharedDataMessage = new UpdateRoleSharedDataMessage();
            updateRoleSharedDataMessage.setId(((Role) object).getId());
            updateRoleSharedDataMessage.setRolename(((Role) object).getRolename());
            updateRoleSharedDataMessage.setStatus(((Role) object).getStatus());
            updateRoleSharedDataMessage.setSysRole(((Role) object).getSysRole());
//            updateRoleSharedDataMessage.setAclEntry(((Role) object).getAclEntry());
            updateRoleSharedDataMessage.setIsDelete(((Role) object).getIsDelete());
            updateRoleSharedDataMessage.setMvnoId(((Role) object).getMvnoId());
            updateRoleSharedDataMessage.setLcoId(((Role) object).getLcoId());
            updateRoleSharedDataMessage.setCreatedById(((Role) object).getCreatedById());
            updateRoleSharedDataMessage.setLastModifiedById(((Role) object).getLastModifiedById());

//            All the message from microservices are to be sent from here
//            messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateRoleSharedDataMessage, SharedDataConstants.QUEUE_ROLE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateRoleSharedDataMessage,UpdateRoleSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(StaffUser.class)) {

//            All data of Staff user entity while deleting

            UpdateStaffUserSharedDataMessage updateStaffUserSharedDataMessage = new UpdateStaffUserSharedDataMessage();
            updateStaffUserSharedDataMessage.setId(((StaffUser) object).getId());
            updateStaffUserSharedDataMessage.setUsername(((StaffUser) object).getUsername());
            updateStaffUserSharedDataMessage.setPassword(((StaffUser) object).getPassword());
            updateStaffUserSharedDataMessage.setFirstname(((StaffUser) object).getFirstname());
            updateStaffUserSharedDataMessage.setLastname(((StaffUser) object).getLastname());
            updateStaffUserSharedDataMessage.setStatus(((StaffUser) object).getStatus());
            if(((StaffUser) object).getLast_login_time() != null) {
                updateStaffUserSharedDataMessage.setLast_login_time(((StaffUser) object).getLast_login_time().toString());
            } else {
                updateStaffUserSharedDataMessage.setLast_login_time(null);
            }
            updateStaffUserSharedDataMessage.setPartnerid(((StaffUser) object).getPartnerid());
            updateStaffUserSharedDataMessage.setRoles(((StaffUser) object).getRoles());
            updateStaffUserSharedDataMessage.setTeam(((StaffUser) object).getTeam());
            updateStaffUserSharedDataMessage.setIsDelete(((StaffUser) object).getIsDelete());
            updateStaffUserSharedDataMessage.setMvnoId(((StaffUser) object).getMvnoId());
            updateStaffUserSharedDataMessage.setBranchId(((StaffUser) object).getBranchId());
            updateStaffUserSharedDataMessage.setServiceAreaNameList(((StaffUser) object).getServiceAreaNameList());
            updateStaffUserSharedDataMessage.setBusinessUnitNameList(((StaffUser) object).getBusinessUnitNameList());
            updateStaffUserSharedDataMessage.setEmail(((StaffUser) object).getEmail());
            updateStaffUserSharedDataMessage.setPhone(((StaffUser) object).getPhone());
            updateStaffUserSharedDataMessage.setCountryCode(((StaffUser) object).getCountryCode());

            if(((StaffUser) object).getStaffUserparent().getId()!=null){
                updateStaffUserSharedDataMessage.setParentStaffId(((StaffUser) object).getStaffUserparent().getId());
            }
            updateStaffUserSharedDataMessage.setCreatedById(((StaffUser) object).getCreatedById());
            updateStaffUserSharedDataMessage.setLastModifiedById(((StaffUser) object).getLastModifiedById());
            // All the message from microservices are to be sent from here
//            messageSender.send(updateStaffUserSharedDataMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateStaffUserSharedDataMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateStaffUserSharedDataMessage, SharedDataConstants.QUEUE_STAFF_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateStaffUserSharedDataMessage,UpdateStaffUserSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Pincode.class)){

            //All data of Pincode entity while deleting
            UpdatePincodeSharedDataMessage updatePincodeSharedDataMessage = new UpdatePincodeSharedDataMessage();

            updatePincodeSharedDataMessage.setId(((Pincode) object).getId());
            updatePincodeSharedDataMessage.setPincode(((Pincode) object).getPincode());
            updatePincodeSharedDataMessage.setCityId(((Pincode) object).getCityId());
            updatePincodeSharedDataMessage.setMvnoId(((Pincode) object).getMvnoId());
            updatePincodeSharedDataMessage.setStatus(((Pincode) object).getStatus());
            updatePincodeSharedDataMessage.setStateId(((Pincode) object).getStateId());
            updatePincodeSharedDataMessage.setIsDeleted(((Pincode) object).getIsDeleted());
            updatePincodeSharedDataMessage.setCountryId(((Pincode) object).getCountryId());
            updatePincodeSharedDataMessage.setCreatedById(((Pincode) object).getCreatedById());
            updatePincodeSharedDataMessage.setLastModifiedById(((Pincode) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updatePincodeSharedDataMessage,SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePincodeSharedDataMessage,SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatePincodeSharedDataMessage, SharedDataConstants.QUEUE_PINCODE_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updatePincodeSharedDataMessage,UpdatePincodeSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Area.class)){

            //All data of Area entity while deleting
            UpdateAreaSharedDataMessage updateAreaSharedDataMessage = new UpdateAreaSharedDataMessage();
            updateAreaSharedDataMessage.setId(((Area) object).getId());
            updateAreaSharedDataMessage.setName(((Area) object).getName());
            updateAreaSharedDataMessage.setMvnoId(((Area) object).getMvnoId());
            updateAreaSharedDataMessage.setCountryId(((Area) object).getCountryId());
            updateAreaSharedDataMessage.setStateId(((Area) object).getStateId());
            updateAreaSharedDataMessage.setCityId(((Area) object).getCityId());
            updateAreaSharedDataMessage.setPincode(((Area) object).getPincode());
            updateAreaSharedDataMessage.setStatus(((Area) object).getStatus());
            updateAreaSharedDataMessage.setIsDeleted(((Area) object).getIsDeleted());
            updateAreaSharedDataMessage.setCreatedById(((Area) object).getCreatedById());
            updateAreaSharedDataMessage.setLastModifiedById(((Area) object).getLastModifiedById());


            // All the messages from microservies are to be sent from here
            UpdateStaffUserSharedDataMessage updatedStaffData = new UpdateStaffUserSharedDataMessage((StaffUser) object);
//            messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatedStaffData,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updatedStaffData,UpdateStaffUserSharedDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(ServiceArea.class)){

            UpdateServiceAreaSharedDataMessage updateServiceAreaSharedDataMessage = new UpdateServiceAreaSharedDataMessage();

            //All data of ServiceArea entity while deleting
            updateServiceAreaSharedDataMessage.setId(((ServiceArea) object).getId());
            updateServiceAreaSharedDataMessage.setAreaId(((ServiceArea) object).getAreaId());
            updateServiceAreaSharedDataMessage.setCityid(((ServiceArea) object).getCityid());
            updateServiceAreaSharedDataMessage.setLongitude(((ServiceArea) object).getLongitude());
            updateServiceAreaSharedDataMessage.setLatitude(((ServiceArea) object).getLatitude());
            updateServiceAreaSharedDataMessage.setName(((ServiceArea) object).getName());
            updateServiceAreaSharedDataMessage.setIsDeleted(((ServiceArea) object).getIsDeleted());
            updateServiceAreaSharedDataMessage.setPincodeList(((ServiceArea) object).getPincodeList());
            updateServiceAreaSharedDataMessage.setMvnoId(((ServiceArea) object).getMvnoId());
            updateServiceAreaSharedDataMessage.setStatus(((ServiceArea) object).getStatus());
            updateServiceAreaSharedDataMessage.setUpdatedById(((ServiceArea) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateServiceAreaSharedDataMessage,SharedDataConstants.QUEUE_SERVICE_AREA_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateServiceAreaSharedDataMessage,UpdateServiceAreaSharedDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(BusinessUnit.class)){

            UpdateBusinessUnitSharedDataMessage updateBusinessUnitSharedDataMessage = new UpdateBusinessUnitSharedDataMessage();


            //All data of BusinessUnit entity while deleting
            updateBusinessUnitSharedDataMessage.setId(((BusinessUnit) object).getId());
            updateBusinessUnitSharedDataMessage.setBuname(((BusinessUnit) object).getBuname());
            updateBusinessUnitSharedDataMessage.setBucode(((BusinessUnit) object).getBucode());
            updateBusinessUnitSharedDataMessage.setInvestmentCodeid(((BusinessUnit) object).getInvestmentCodeid());
            updateBusinessUnitSharedDataMessage.setMvnoId(((BusinessUnit) object).getMvnoId());
            updateBusinessUnitSharedDataMessage.setIsDeleted(((BusinessUnit) object).getIsDeleted());
            updateBusinessUnitSharedDataMessage.setStatus(((BusinessUnit) object).getStatus());
            updateBusinessUnitSharedDataMessage.setPlanBindingType(((BusinessUnit) object).getPlanBindingType());
            updateBusinessUnitSharedDataMessage.setCreatedById(((BusinessUnit) object).getCreatedById());
            updateBusinessUnitSharedDataMessage.setLastModifiedById(((BusinessUnit) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBusinessUnitSharedDataMessage,SharedDataConstants.QUEUE_BUSINESS_UNIT_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBusinessUnitSharedDataMessage,UpdateBusinessUnitSharedDataMessage.class.getSimpleName()));

        }
        else if(Objects.nonNull(object) && object.getClass().equals(Branch.class)){

            UpdateBranchSharedData updateBranchSharedDataMessage = new UpdateBranchSharedData();

            //All this data for branch deleting
            updateBranchSharedDataMessage.setId(((Branch) object).getId());
            updateBranchSharedDataMessage.setName(((Branch) object).getName());
            updateBranchSharedDataMessage.setBranch_code(((Branch) object).getBranch_code());
            updateBranchSharedDataMessage.setRevenue_sharing(((Branch) object).getRevenue_sharing());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setBranchServiceMappingEntityList(((Branch) object).getBranchServiceMappingEntityList());
            updateBranchSharedDataMessage.setServiceAreaNameList(((Branch) object).getServiceAreaNameList());
            updateBranchSharedDataMessage.setIsDeleted(((Branch) object).getIsDeleted());
            updateBranchSharedDataMessage.setMvnoId(((Branch) object).getMvnoId());
            updateBranchSharedDataMessage.setDunningDays(((Branch) object).getDunningDays());
            updateBranchSharedDataMessage.setSharing_percentage(((Branch) object).getSharing_percentage());
            updateBranchSharedDataMessage.setStatus(((Branch) object).getStatus());
            updateBranchSharedDataMessage.setCreatedById(((Branch) object).getCreatedById());
            updateBranchSharedDataMessage.setLastModifiedById(((Branch) object).getLastModifiedById());


            // All the messages from microservies are to be sent from here
//            messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateBranchSharedDataMessage,SharedDataConstants.QUEUE_BRANCH_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateBranchSharedDataMessage,UpdateBranchSharedData.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanService.class)) {

//            All data of Services entity while deleting
            UpdateServicesSharedDataMessage updateServicesSharedDataMessage = new UpdateServicesSharedDataMessage();
            updateServicesSharedDataMessage.setId(((PlanService) object).getId());
            updateServicesSharedDataMessage.setName(((PlanService) object).getName());
            updateServicesSharedDataMessage.setIcname(((PlanService) object).getIcname());
            updateServicesSharedDataMessage.setIccode(((PlanService) object).getIccode());
            updateServicesSharedDataMessage.setMvnoId(((PlanService) object).getMvnoId());
            updateServicesSharedDataMessage.setBuId(((PlanService) object).getBuId());
            updateServicesSharedDataMessage.setIsQoSV(((PlanService) object).getIsQoSV());
            updateServicesSharedDataMessage.setExpiry(((PlanService) object).getExpiry());
            updateServicesSharedDataMessage.setLedgerId(((PlanService) object).getLedgerId());
            updateServicesSharedDataMessage.setIs_dtv(((PlanService) object).getIs_dtv());
            updateServicesSharedDataMessage.setInvestmentid(((PlanService) object).getInvestmentid());
            updateServicesSharedDataMessage.setProductCategories(((PlanService) object).getProductCategories());
            updateServicesSharedDataMessage.setServiceParamMappingList(((PlanService) object).getServiceParamMappingList());
            updateServicesSharedDataMessage.setFeasibility(((PlanService) object).getFeasibility());
            updateServicesSharedDataMessage.setPoc(((PlanService) object).getPoc());
            updateServicesSharedDataMessage.setInstallation(((PlanService) object).getInstallation());
            updateServicesSharedDataMessage.setProvisioning(((PlanService) object).getProvisioning());
            updateServicesSharedDataMessage.setIsPriceEditable(((PlanService) object).getIsPriceEditable());
            updateServicesSharedDataMessage.setFeasibilityTeamId(((PlanService) object).getFeasibilityTeamId());
            updateServicesSharedDataMessage.setPocTeamId(((PlanService) object).getPocTeamId());
            updateServicesSharedDataMessage.setInstallationTeamId(((PlanService) object).getInstallationTeamId());
            updateServicesSharedDataMessage.setProvisioningTeamId(((PlanService) object).getProvisioningTeamId());
            updateServicesSharedDataMessage.setIsDeleted(((PlanService) object).getIsDeleted());
            updateServicesSharedDataMessage.setCreatedById(((PlanService) object).getCreatedById());
            updateServicesSharedDataMessage.setLastModifiedById(((PlanService) object).getLastModifiedById());

//            All the message from microservices are to be sent from here
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_TICKET);
            //messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_PARTNER);

            //messageSender.send(updateServicesSharedDataMessage, SharedDataConstants.QUEUE_SERVICES_UPDATE_DATA_SHARE_COMMON);
            kafkaMessageSender.send(new KafkaMessageData(updateServicesSharedDataMessage,updateServicesSharedDataMessage.getClass().getSimpleName()));
        }
        else if (Objects.nonNull(object) && object.getClass().equals(Region.class)) {
            // All data of Region entity while saving
            UpdateRegionSharedDataMessage updateRegionSharedDataMessage = new UpdateRegionSharedDataMessage();
            updateRegionSharedDataMessage.setId(((Region) object).getId());
            updateRegionSharedDataMessage.setRname(((Region) object).getRname());
            updateRegionSharedDataMessage.setBranchidList(((Region) object).getBranchidList());
            updateRegionSharedDataMessage.setStatus(((Region) object).getStatus());
            updateRegionSharedDataMessage.setIsDeleted(((Region) object).getIsDeleted());
            updateRegionSharedDataMessage.setMvnoId(((Region) object).getMvnoId());
            updateRegionSharedDataMessage.setCreatedById(((Region) object).getCreatedById());
            updateRegionSharedDataMessage.setLastModifiedById(((Region) object).getLastModifiedById());
            // All the messages from microservices are to be sent from here
//            messageSender.send(updateRegionSharedDataMessage, SharedDataConstants.QUEUE_REGION_UPDATE_DATA_SHARE_TICKET);

            kafkaMessageSender.send(new KafkaMessageData(updateRegionSharedDataMessage,UpdateRegionSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(BusinessVerticals.class)) {

            // All data of BusinessVerticals entity while saving
            UpdateBusinessVerticalSharedDataMessage updateBusinessVerticalSharedDataMessage = new UpdateBusinessVerticalSharedDataMessage();
            updateBusinessVerticalSharedDataMessage.setId(((BusinessVerticals) object).getId());
            updateBusinessVerticalSharedDataMessage.setVname(((BusinessVerticals) object).getVname());
            updateBusinessVerticalSharedDataMessage.setBuregionidList(((BusinessVerticals) object).getBuregionidList());
            updateBusinessVerticalSharedDataMessage.setStatus(((BusinessVerticals) object).getStatus());
            updateBusinessVerticalSharedDataMessage.setIsDeleted(((BusinessVerticals) object).getIsDeleted());
            updateBusinessVerticalSharedDataMessage.setMvnoId(((BusinessVerticals) object).getMvnoId());
            updateBusinessVerticalSharedDataMessage.setCreatedById(((BusinessVerticals) object).getCreatedById());
            updateBusinessVerticalSharedDataMessage.setLastModifiedById(((BusinessVerticals) object).getLastModifiedById());

            // Send the message
//            messageSender.send(updateBusinessVerticalSharedDataMessage, SharedDataConstants.QUEUE_BUSINESSVERTICALS_UPDATE_DATA_SHARE_TICKET);
            kafkaMessageSender.send(new KafkaMessageData(updateBusinessVerticalSharedDataMessage,UpdateBusinessVerticalSharedDataMessage.class.getSimpleName()));


        }
        else if (Objects.nonNull(object) && object.getClass().equals(Partner.class)) {

            //            All data of Partner entity while deleting
            UpdatePartnerSharedDataMessage updatePartnerSharedDataMessage = new UpdatePartnerSharedDataMessage();
            updatePartnerSharedDataMessage.setId(((Partner) object).getId());
            updatePartnerSharedDataMessage.setName(((Partner) object).getName());
            updatePartnerSharedDataMessage.setPrcode(((Partner) object).getPrcode());
            updatePartnerSharedDataMessage.setStatus(((Partner) object).getStatus());
            updatePartnerSharedDataMessage.setCommtype(((Partner) object).getCommtype());
            updatePartnerSharedDataMessage.setCommrelvalue(((Partner) object).getCommrelvalue());
            updatePartnerSharedDataMessage.setBalance(((Partner) object).getBalance());
            updatePartnerSharedDataMessage.setCommdueday(((Partner) object).getCommdueday());
            updatePartnerSharedDataMessage.setNextbilldate(String.valueOf(((Partner) object).getNextbilldate()));
            updatePartnerSharedDataMessage.setLastbilldate(String.valueOf(((Partner) object).getLastbilldate()));
            updatePartnerSharedDataMessage.setTaxid(((Partner) object).getTaxid());
            updatePartnerSharedDataMessage.setAddresstype(((Partner) object).getAddresstype());
            updatePartnerSharedDataMessage.setAddress1(((Partner) object).getAddress1());
            updatePartnerSharedDataMessage.setAddress2(((Partner) object).getAddress2());
            updatePartnerSharedDataMessage.setCredit(((Partner) object).getCredit());
            updatePartnerSharedDataMessage.setCity(((Partner) object).getCity());
            updatePartnerSharedDataMessage.setState(((Partner) object).getState());
            updatePartnerSharedDataMessage.setCountry(((Partner) object).getCountry());
            updatePartnerSharedDataMessage.setPincode(((Partner) object).getPincode());
            updatePartnerSharedDataMessage.setMobile(((Partner) object).getMobile());
            updatePartnerSharedDataMessage.setCountryCode(((Partner) object).getCountryCode());
            updatePartnerSharedDataMessage.setEmail(((Partner) object).getEmail());
            updatePartnerSharedDataMessage.setPartnerType(((Partner) object).getPartnerType());
            updatePartnerSharedDataMessage.setCpName(((Partner) object).getCpName());
            updatePartnerSharedDataMessage.setCname(((Partner) object).getCname());
            updatePartnerSharedDataMessage.setPanName(((Partner) object).getPanName());
            updatePartnerSharedDataMessage.setServiceAreaList(((Partner) object).getServiceAreaList());
            updatePartnerSharedDataMessage.setParentPartner(((Partner) object).getParentPartner());
            updatePartnerSharedDataMessage.setPartnerLedgerDetails(((Partner) object).getPartnerLedgerDetails());
            updatePartnerSharedDataMessage.setPartnerPayments(((Partner) object).getPartnerPayments());
            updatePartnerSharedDataMessage.setIsDelete(((Partner) object).getIsDelete());
            updatePartnerSharedDataMessage.setMvnoId(((Partner) object).getMvnoId());
            updatePartnerSharedDataMessage.setCommissionShareType(((Partner) object).getCommissionShareType());
            updatePartnerSharedDataMessage.setBuId(((Partner) object).getBuId());
            updatePartnerSharedDataMessage.setNewCustomerCount(((Partner) object).getNewCustomerCount());
            updatePartnerSharedDataMessage.setRenewCustomerCount(((Partner) object).getRenewCustomerCount());
            updatePartnerSharedDataMessage.setTotalCustomerCount(((Partner) object).getTotalCustomerCount());
            updatePartnerSharedDataMessage.setCalendarType(((Partner) object).getCalendarType());
            updatePartnerSharedDataMessage.setResetDate(String.valueOf(((Partner) object).getResetDate()));
            updatePartnerSharedDataMessage.setCreditConsume(((Partner) object).getCreditConsume());
            updatePartnerSharedDataMessage.setRegion(((Partner) object).getRegion());
            updatePartnerSharedDataMessage.setBranch(((Partner) object).getBranch());
            updatePartnerSharedDataMessage.setDunningActivateFor(((Partner) object).getDunningActivateFor());
            updatePartnerSharedDataMessage.setLastDunningDate(String.valueOf(((Partner) object).getLastDunningDate()));
            updatePartnerSharedDataMessage.setIsDunningEnable(((Partner) object).getIsDunningEnable());
            updatePartnerSharedDataMessage.setDunningAction(((Partner) object).getDunningAction());
            if(((Partner) object).getParentPartner()!=null)
                updatePartnerSharedDataMessage.setParentPartnerId(((Partner) object).getParentPartner().getId());
            else
                updatePartnerSharedDataMessage.setParentPartnerId(null);
            updatePartnerSharedDataMessage.setCreatedById(((Partner) object).getCreatedById());
            updatePartnerSharedDataMessage.setLastModifiedById(((Partner) object).getLastModifiedById());

            //            All the message from microservices are to be sent from here
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_API_COMMON);
            UpdatePartnerSharedDataMessage message = new UpdatePartnerSharedDataMessage((Partner) object);
//            messageSender.send(message, SharedDataConstants.QUEUE_PARTNER_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePartnerSharedDataMessage, SharedDataConstants.QUEUE_UPDATE_PARTNER_REVENUE);

            kafkaMessageSender.send(new KafkaMessageData(message,UpdatePartnerSharedDataMessage.class.getSimpleName()));

            kafkaMessageSender.send(new KafkaMessageData(updatePartnerSharedDataMessage,UpdatePartnerSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Tax.class)) {

            //            All data of Tax entity while deleting
            UpdateTaxSharedDataMessage updateTaxSharedDataMessage = new UpdateTaxSharedDataMessage();
            updateTaxSharedDataMessage.setId(((Tax) object).getId());
            updateTaxSharedDataMessage.setName(((Tax) object).getName());
            updateTaxSharedDataMessage.setDesc(((Tax) object).getDesc());
            updateTaxSharedDataMessage.setTaxtype(((Tax) object).getTaxtype());
            updateTaxSharedDataMessage.setStatus(((Tax) object).getStatus());
            updateTaxSharedDataMessage.setMvnoId(((Tax) object).getMvnoId());
            updateTaxSharedDataMessage.setBuId(((Tax) object).getBuId());
            updateTaxSharedDataMessage.setTieredList(((Tax) object).getTieredList());
            updateTaxSharedDataMessage.setSlabList(((Tax) object).getSlabList());
            updateTaxSharedDataMessage.setIsDelete(((Tax) object).getIsDelete());
            updateTaxSharedDataMessage.setCreatedById(((Tax) object).getCreatedById());
            updateTaxSharedDataMessage.setLastModifiedById(((Tax) object).getLastModifiedById());

            //            All the message from microservices are to be sent from here
//            messageSender.send(updateTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updateTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateTaxSharedDataMessage,UpdateTaxSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PostpaidPlan.class)) {

            //            All data of Plan entity while deleting
            UpdatePlanSharedDataMessage updatePlanSharedDataMessage = new UpdatePlanSharedDataMessage();
            updatePlanSharedDataMessage.setId(((PostpaidPlan) object).getId());
            updatePlanSharedDataMessage.setName(((PostpaidPlan) object).getName());
            updatePlanSharedDataMessage.setDisplayName(((PostpaidPlan) object).getDisplayName());
            updatePlanSharedDataMessage.setCode(((PostpaidPlan) object).getCode());
            updatePlanSharedDataMessage.setDesc(((PostpaidPlan) object).getDesc());
            updatePlanSharedDataMessage.setCategory(((PostpaidPlan) object).getCategory());
            updatePlanSharedDataMessage.setMaxChild(((PostpaidPlan) object).getMaxChild());
            updatePlanSharedDataMessage.setStartDate(String.valueOf(((PostpaidPlan) object).getStartDate()));
            updatePlanSharedDataMessage.setEndDate(String.valueOf(((PostpaidPlan) object).getEndDate()));
            updatePlanSharedDataMessage.setQuota(((PostpaidPlan) object).getQuota());
            updatePlanSharedDataMessage.setQuotaUnit(((PostpaidPlan) object).getQuotaUnit());
            updatePlanSharedDataMessage.setUploadQOS(((PostpaidPlan) object).getUploadQOS());
            updatePlanSharedDataMessage.setDownloadQOS(((PostpaidPlan) object).getDownloadQOS());
            updatePlanSharedDataMessage.setUploadTs(((PostpaidPlan) object).getUploadTs());
            updatePlanSharedDataMessage.setDownloadTs(((PostpaidPlan) object).getDownloadTs());
            updatePlanSharedDataMessage.setAllowOverUsage(((PostpaidPlan) object).getAllowOverUsage());
            updatePlanSharedDataMessage.setStatus(((PostpaidPlan) object).getStatus());
            updatePlanSharedDataMessage.setPlanStatus(((PostpaidPlan) object).getPlanStatus());
            updatePlanSharedDataMessage.setChildQuota(((PostpaidPlan) object).getChildQuota());
            updatePlanSharedDataMessage.setChildQuotaUnit(((PostpaidPlan) object).getChildQuotaUnit());
            updatePlanSharedDataMessage.setSlice(((PostpaidPlan) object).getSlice());
            updatePlanSharedDataMessage.setSliceUnit(((PostpaidPlan) object).getSliceUnit());
            updatePlanSharedDataMessage.setAttachedToAllHotSpots(((PostpaidPlan) object).getAttachedToAllHotSpots());
            updatePlanSharedDataMessage.setParam1(((PostpaidPlan) object).getParam1());
            updatePlanSharedDataMessage.setParam2(((PostpaidPlan) object).getParam2());
            updatePlanSharedDataMessage.setMvnoId(((PostpaidPlan) object).getMvnoId());
            updatePlanSharedDataMessage.setTaxId(((PostpaidPlan) object).getTaxId());
            updatePlanSharedDataMessage.setServiceId(((PostpaidPlan) object).getServiceId());
            updatePlanSharedDataMessage.setTimebasepolicyId(((PostpaidPlan) object).getTimebasepolicyId());
            updatePlanSharedDataMessage.setPlantype(((PostpaidPlan) object).getPlantype());
            updatePlanSharedDataMessage.setDbr(((PostpaidPlan) object).getDbr());
            updatePlanSharedDataMessage.setChargeList(((PostpaidPlan) object).getChargeList());
            updatePlanSharedDataMessage.setPlanGroup(((PostpaidPlan) object).getPlanGroup());
            updatePlanSharedDataMessage.setValidity(((PostpaidPlan) object).getValidity());
            updatePlanSharedDataMessage.setSaccode(((PostpaidPlan) object).getSaccode());
            updatePlanSharedDataMessage.setMaxconcurrentsession(((PostpaidPlan) object).getMaxconcurrentsession());
            updatePlanSharedDataMessage.setQuotatime(((PostpaidPlan) object).getQuotatime());
            updatePlanSharedDataMessage.setQuotaunittime(((PostpaidPlan) object).getQuotaunittime());
            updatePlanSharedDataMessage.setQuotatype(((PostpaidPlan) object).getQuotatype());
            updatePlanSharedDataMessage.setOfferprice(((PostpaidPlan) object).getOfferprice());
            updatePlanSharedDataMessage.setQuotadid(((PostpaidPlan) object).getQuotadid());
            updatePlanSharedDataMessage.setQuotaintercom(((PostpaidPlan) object).getQuotaintercom());
            if(((PostpaidPlan) object).getQospolicy() != null) {
                updatePlanSharedDataMessage.setQospolicy(new QOSPolicy(((PostpaidPlan) object).getQospolicy()));
                updatePlanSharedDataMessage.setQospolicy_id(((PostpaidPlan) object).getQospolicy().getId());
                updatePlanSharedDataMessage.setQospolicy_name(((PostpaidPlan) object).getQospolicy().getName());
            }
            updatePlanSharedDataMessage.setRadiusprofile(((PostpaidPlan) object).getRadiusprofile());
            updatePlanSharedDataMessage.setIsDelete(((PostpaidPlan) object).getIsDelete());
            updatePlanSharedDataMessage.setDataCategory(((PostpaidPlan) object).getDataCategory());
            updatePlanSharedDataMessage.setTaxamount(((PostpaidPlan) object).getTaxamount());
            updatePlanSharedDataMessage.setServiceAreaNameList(((PostpaidPlan) object).getServiceAreaNameList());
            updatePlanSharedDataMessage.setQuotaResetInterval(((PostpaidPlan) object).getQuotaResetInterval());
            updatePlanSharedDataMessage.setMode(((PostpaidPlan) object).getMode());
            updatePlanSharedDataMessage.setUnitsOfValidity(((PostpaidPlan) object).getUnitsOfValidity());
            updatePlanSharedDataMessage.setBuId(((PostpaidPlan) object).getBuId());
            updatePlanSharedDataMessage.setNextTeamHierarchyMapping(((PostpaidPlan) object).getNextTeamHierarchyMapping());
            updatePlanSharedDataMessage.setNextStaff(((PostpaidPlan) object).getNextStaff());
            updatePlanSharedDataMessage.setNewOfferPrice(((PostpaidPlan) object).getNewOfferPrice());
            updatePlanSharedDataMessage.setAccessibility(((PostpaidPlan) object).getAccessibility());
            updatePlanSharedDataMessage.setProductId(((PostpaidPlan) object).getProductId());
            updatePlanSharedDataMessage.setProductplanmappingList(((PostpaidPlan) object).getProductplanmappingList());
            updatePlanSharedDataMessage.setInvoiceToOrg(((PostpaidPlan) object).getInvoiceToOrg());
            updatePlanSharedDataMessage.setRequiredApproval(((PostpaidPlan) object).getRequiredApproval());
            updatePlanSharedDataMessage.setPlanCasMappingList(((PostpaidPlan) object).getPlanCasMappingList());
            updatePlanSharedDataMessage.setBandwidth(((PostpaidPlan) object).getBandwidth());
            updatePlanSharedDataMessage.setLink_type(((PostpaidPlan) object).getLink_type());
            updatePlanSharedDataMessage.setConnection_type(((PostpaidPlan) object).getConnection_type());
            updatePlanSharedDataMessage.setDistance(((PostpaidPlan) object).getDistance());
            updatePlanSharedDataMessage.setRam(((PostpaidPlan) object).getRam());
            updatePlanSharedDataMessage.setCpu(((PostpaidPlan) object).getCpu());
            updatePlanSharedDataMessage.setStorage(((PostpaidPlan) object).getStorage());
            updatePlanSharedDataMessage.setStorage_type(((PostpaidPlan) object).getStorage_type());
            updatePlanSharedDataMessage.setAuto_backup(((PostpaidPlan) object).getAuto_backup());
            updatePlanSharedDataMessage.setCpanel(((PostpaidPlan) object).getCpanel());
            updatePlanSharedDataMessage.setLocation(((PostpaidPlan) object).getLocation());
            updatePlanSharedDataMessage.setQuantity(((PostpaidPlan) object).getQuantity());
            updatePlanSharedDataMessage.setPackage_type(((PostpaidPlan) object).getPackage_type());
            updatePlanSharedDataMessage.setNumber_of_days(((PostpaidPlan) object).getNumber_of_days());
            updatePlanSharedDataMessage.setNo_of_users(((PostpaidPlan) object).getNo_of_users());
            updatePlanSharedDataMessage.setRack_space(((PostpaidPlan) object).getRack_space());
            updatePlanSharedDataMessage.setPower_consumption(((PostpaidPlan) object).getPower_consumption());
            updatePlanSharedDataMessage.setNetwork_card(((PostpaidPlan) object).getNetwork_card());
            updatePlanSharedDataMessage.setIp_or_ip_pool(((PostpaidPlan) object).getIp_or_ip_pool());
            updatePlanSharedDataMessage.setNo_of_license(((PostpaidPlan) object).getNo_of_license());
            updatePlanSharedDataMessage.setNo_of_email_user_license(((PostpaidPlan) object).getNo_of_email_user_license());
            updatePlanSharedDataMessage.setNo_of_server_license(((PostpaidPlan) object).getNo_of_server_license());
            updatePlanSharedDataMessage.setNo_of_user_license(((PostpaidPlan) object).getNo_of_user_license());
            updatePlanSharedDataMessage.setNo_of_nodes(((PostpaidPlan) object).getNo_of_nodes());
            updatePlanSharedDataMessage.setEvent_per_second(((PostpaidPlan) object).getEvent_per_second());
            updatePlanSharedDataMessage.setNo_of_additional_server(((PostpaidPlan) object).getNo_of_additional_server());
            updatePlanSharedDataMessage.setNo_of_additional_storage(((PostpaidPlan) object).getNo_of_additional_storage());
            updatePlanSharedDataMessage.setAdditional_storage_type(((PostpaidPlan) object).getAdditional_storage_type());
            updatePlanSharedDataMessage.setEps_License(((PostpaidPlan) object).getEps_License());
            updatePlanSharedDataMessage.setNo_of_nodes_license(((PostpaidPlan) object).getNo_of_nodes_license());
            updatePlanSharedDataMessage.setHardware_resource(((PostpaidPlan) object).getHardware_resource());
            updatePlanSharedDataMessage.setMan_power(((PostpaidPlan) object).getMan_power());
            updatePlanSharedDataMessage.setNo_of_domains(((PostpaidPlan) object).getNo_of_domains());
            updatePlanSharedDataMessage.setSecurity_modules(((PostpaidPlan) object).getSecurity_modules());
            updatePlanSharedDataMessage.setHardware_or_servers(((PostpaidPlan) object).getHardware_or_servers());
            updatePlanSharedDataMessage.setCountry(((PostpaidPlan) object).getCountry());
            updatePlanSharedDataMessage.setNo_of_vpn(((PostpaidPlan) object).getNo_of_vpn());
            updatePlanSharedDataMessage.setDevice_throughput(((PostpaidPlan) object).getDevice_throughput());
            updatePlanSharedDataMessage.setRetail(((PostpaidPlan) object).getRetail());
            updatePlanSharedDataMessage.setBusinessType(((PostpaidPlan) object).getBusinessType());
            updatePlanSharedDataMessage.setBasePlan(((PostpaidPlan) object).getBasePlan());
            updatePlanSharedDataMessage.setTemplateId(((PostpaidPlan) object).getTemplateId());
            updatePlanSharedDataMessage.setPlanQosMappingEntities(((PostpaidPlan) object).getPlanQosMappingEntities());
            updatePlanSharedDataMessage.setCreatedById(((PostpaidPlan) object).getCreatedById());
            updatePlanSharedDataMessage.setLastModifiedById(((PostpaidPlan) object).getLastModifiedById());
            updatePlanSharedDataMessage.setIsApprove(false);
            //            All the message from microservices are to be sent from here
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updatePlanSharedDataMessage, SharedDataConstants.QUEUE_PLAN_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePlanSharedDataMessage,UpdatePlanSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PlanGroup.class)) {

            //            All data of Plangroup entity while delting
            UpdatePlanGroupSharedDataMessage updatePlanGroupSharedDataMessage = new UpdatePlanGroupSharedDataMessage();
            updatePlanGroupSharedDataMessage.setPlanGroupId(((PlanGroup) object).getPlanGroupId());
            updatePlanGroupSharedDataMessage.setPlanGroupName(((PlanGroup) object).getPlanGroupName());
            updatePlanGroupSharedDataMessage.setStatus(((PlanGroup) object).getStatus());
            updatePlanGroupSharedDataMessage.setMvnoId(((PlanGroup) object).getMvnoId());
            updatePlanGroupSharedDataMessage.setPlantype(((PlanGroup) object).getPlantype());
            updatePlanGroupSharedDataMessage.setPlanMode(((PlanGroup) object).getPlanMode());
            updatePlanGroupSharedDataMessage.setIsDelete(((PlanGroup) object).getIsDelete());
//            updatePlanGroupSharedDataMessage.setPlanMappingList(((PlanGroup) object).getPlanMappingList());
            updatePlanGroupSharedDataMessage.setDbr(((PlanGroup) object).getDbr());
            updatePlanGroupSharedDataMessage.setPlanGroupType(((PlanGroup) object).getPlanGroupType());
            updatePlanGroupSharedDataMessage.setCategory(((PlanGroup) object).getCategory());
            updatePlanGroupSharedDataMessage.setNextTeamHierarchyMappingId(((PlanGroup) object).getNextTeamHierarchyMappingId());
            updatePlanGroupSharedDataMessage.setNextStaff(((PlanGroup) object).getNextStaff());
            updatePlanGroupSharedDataMessage.setAccessibility(((PlanGroup) object).getAccessibility());
            updatePlanGroupSharedDataMessage.setAllowDiscount(((PlanGroup) object).getInvoiceToOrg());
            updatePlanGroupSharedDataMessage.setOfferprice(((PlanGroup) object).getOfferprice());
            updatePlanGroupSharedDataMessage.setServicearea(((PlanGroup) object).getServicearea());
            updatePlanGroupSharedDataMessage.setProductPlanGroupMappingList(((PlanGroup) object).getProductPlanGroupMappingList());
            updatePlanGroupSharedDataMessage.setTemplateId(((PlanGroup) object).getTemplateId());
            updatePlanGroupSharedDataMessage.setInvoiceToOrg(((PlanGroup) object).getInvoiceToOrg());
            updatePlanGroupSharedDataMessage.setRequiredApproval(((PlanGroup) object).getRequiredApproval());
            updatePlanGroupSharedDataMessage.setCreatedById(((PlanGroup) object).getCreatedById());
            updatePlanGroupSharedDataMessage.setLastModifiedById(((PlanGroup) object).getLastModifiedById());


            List<PlanGroupMapping>  planGroupMapping = ((PlanGroup) object).getPlanMappingList();
            List<PlanGroupMapping> planGroupMappingList =  new ArrayList<>();
            for (PlanGroupMapping data : planGroupMapping){
                PlanGroupMapping planGroupMapping1 = new PlanGroupMapping(data);
                planGroupMappingList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setPlanMappingList(planGroupMappingList);

            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappings = serviceAreaPlangroupMappingRepo.findByPlanGroupAndServiceAreaIn(((PlanGroup) object),((PlanGroup) object).getServicearea());
            List<ServiceAreaPlanGroupMapping> serviceAreaPlanGroupMappingList =  new ArrayList<>();
            for (ServiceAreaPlanGroupMapping data : serviceAreaPlanGroupMappings){
                ServiceAreaPlanGroupMapping planGroupMapping1 = new ServiceAreaPlanGroupMapping(data);
                serviceAreaPlanGroupMappingList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setServiceAreaPlanGroupMappingList(serviceAreaPlanGroupMappingList);

            List<PlanGroupMappingChargeRel> planGroupMappingChargeRels = planGroupMappingChargeRelRepo.findAllByPlanGroupMappingIn(planGroupMapping);
            List<PlanGroupMappingChargeRel> planGroupMappingChargeRelList =  new ArrayList<>();
            for (PlanGroupMappingChargeRel data : planGroupMappingChargeRels){
                PlanGroupMappingChargeRel planGroupMapping1 = new PlanGroupMappingChargeRel(data);
                planGroupMappingChargeRelList.add(planGroupMapping1);
            }
            updatePlanGroupSharedDataMessage.setPlanGroupMappingChargeRelsList(planGroupMappingChargeRelList);

            //            All the message from microservices are to be sent from here
//            messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_INVENTORY);
//            messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_PARTNER);
//            messageSender.send(updatePlanGroupSharedDataMessage, SharedDataConstants.QUEUE_PLANGROUP_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);

            kafkaMessageSender.send(new KafkaMessageData(updatePlanGroupSharedDataMessage,UpdatePlanGroupSharedDataMessage.class.getSimpleName()));

        } else if (Objects.nonNull(object) && object.getClass().equals(Charge.class)) {

            //            All data of Charge entity while deleting
            UpdateChargeSharedDataMessage updateChargeSharedDataMessage = new UpdateChargeSharedDataMessage();
            updateChargeSharedDataMessage.setId(((Charge) object).getId());
            updateChargeSharedDataMessage.setName(((Charge) object).getName());
            updateChargeSharedDataMessage.setChargetype(((Charge) object).getChargetype());
            updateChargeSharedDataMessage.setPrice(((Charge) object).getPrice());
            updateChargeSharedDataMessage.setTaxId(((Charge) object).getTax().getId());
            updateChargeSharedDataMessage.setDbr(((Charge) object).getDbr());
            updateChargeSharedDataMessage.setDiscountid(((Charge) object).getDiscountid());
            updateChargeSharedDataMessage.setIsDelete(((Charge) object).getIsDelete());
            updateChargeSharedDataMessage.setSaccode(((Charge) object).getSaccode());
            updateChargeSharedDataMessage.setServiceList(((Charge) object).getServiceList());
            updateChargeSharedDataMessage.setMvnoId(((Charge) object).getMvnoId());
            updateChargeSharedDataMessage.setBuId(((Charge) object).getBuId());
            updateChargeSharedDataMessage.setService(((Charge) object).getService());
            updateChargeSharedDataMessage.setStatus(((Charge) object).getStatus());
            updateChargeSharedDataMessage.setLedgerId(((Charge) object).getLedgerId());
            updateChargeSharedDataMessage.setRoyalty_payable(((Charge) object).getRoyalty_payable());
            updateChargeSharedDataMessage.setBusinessType(((Charge) object).getBusinessType());
            updateChargeSharedDataMessage.setPushableLedgerId(((Charge) object).getPushableLedgerId());
            updateChargeSharedDataMessage.setCreatedById(((Charge) object).getCreatedById());
            updateChargeSharedDataMessage.setLastModifiedById(((Charge) object).getLastModifiedById());
            updateChargeSharedDataMessage.setActualprice(((Charge) object).getActualprice());
            updateChargeSharedDataMessage.setChargecategory(((Charge) object).getChargecategory());
            updateChargeSharedDataMessage.setTaxamount(((Charge) object).getTaxamount());
//            messageSender.send(updateChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_PARTNER);
//            All the message from microservices are to be sent from here
//            messageSender.send(updateChargeSharedDataMessage, SharedDataConstants.QUEUE_CHARGE_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateChargeSharedDataMessage,UpdateChargeSharedDataMessage.class.getSimpleName()));

        }else if(Objects.nonNull(object) && object.getClass().equals(Teams.class)){
            UpdateTeamsSharedData updateTeamsSharedSharedData = new UpdateTeamsSharedData();

            //All this data for branch saving

            updateTeamsSharedSharedData.setId(((Teams) object).getId());
            updateTeamsSharedSharedData.setName(((Teams) object).getName());
            updateTeamsSharedSharedData.setParentTeams(((Teams) object).getParentTeams());
            updateTeamsSharedSharedData.setLcoId(((Teams) object).getLcoId());
            updateTeamsSharedSharedData.setStatus(((Teams) object).getStatus());
            updateTeamsSharedSharedData.setPartner(((Teams) object).getPartner());
            updateTeamsSharedSharedData.setCafStatus(((Teams) object).getCafStatus());
            updateTeamsSharedSharedData.setIsDeleted(((Teams) object).getIsDeleted());
            updateTeamsSharedSharedData.setMvnoId(((Teams) object).getMvnoId());
            updateTeamsSharedSharedData.setStaffUser(((Teams) object).getStaffUser());
            updateTeamsSharedSharedData.setCreatedById(((Teams) object).getCreatedById());
            updateTeamsSharedSharedData.setLastModifiedById(((Teams) object).getLastModifiedById());

            // All the messages from microservies are to be sent from here
//            messageSender.send(updateTeamsSharedSharedData,SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_INVENTORY);
            //messageSender.send(updateTeamsSharedSharedData, SharedDataConstants.QUEUE_TEAMS_UPDATE_DATA_SHARE_SAMPLE_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateTeamsSharedSharedData,UpdateTeamsSharedData.class.getSimpleName()));

        }else if(Objects.nonNull(object) && object.getClass().equals(Hierarchy.class)){
            UpdateHierarchyShareDataMessage updateHierarchyShareDataMessage = new UpdateHierarchyShareDataMessage();

            updateHierarchyShareDataMessage.setId(((Hierarchy) object).getId());
            updateHierarchyShareDataMessage.setHierarchyName(((Hierarchy) object).getHierarchyName());
            updateHierarchyShareDataMessage.setEventName(((Hierarchy) object).getEventName());
            updateHierarchyShareDataMessage.setBuId(((Hierarchy) object).getBuId());
            updateHierarchyShareDataMessage.setLcoId(((Hierarchy) object).getLcoId());
            updateHierarchyShareDataMessage.setMvnoId(((Hierarchy) object).getMvnoId());
            updateHierarchyShareDataMessage.setTeamHierarchyMappingList(((Hierarchy) object).getTeamHierarchyMappingList());
            updateHierarchyShareDataMessage.setIsDeleted(((Hierarchy) object).getIsDeleted());
            updateHierarchyShareDataMessage.setCreatedById(((Hierarchy) object).getCreatedById());
            updateHierarchyShareDataMessage.setLastModifiedById(((Hierarchy) object).getLastModifiedById());

//            messageSender.send(updateHierarchyShareDataMessage,SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_TICKET);
//            messageSender.send(updateHierarchyShareDataMessage, SharedDataConstants.QUEUE_HIERARCHY_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateHierarchyShareDataMessage,UpdateHierarchyShareDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {

            UpdateClientServMessage clientServMessge = new UpdateClientServMessage();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());
//            messageSender.send(clientServMessge, SharedDataConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,UpdateClientServMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Tax.class)) {

            UpdateTaxSharedDataMessage updateTaxSharedDataMessage=new UpdateTaxSharedDataMessage();
            updateTaxSharedDataMessage.setId(((Tax) object).getId());
            updateTaxSharedDataMessage.setName(((Tax) object).getName());
            updateTaxSharedDataMessage.setDesc(((Tax) object).getDesc());
            updateTaxSharedDataMessage.setTaxtype(((Tax) object).getTaxtype());
            updateTaxSharedDataMessage.setStatus(((Tax) object).getStatus());
            updateTaxSharedDataMessage.setMvnoId(((Tax) object).getMvnoId());
            updateTaxSharedDataMessage.setBuId(((Tax) object).getBuId());
            updateTaxSharedDataMessage.setIsDelete(((Tax) object).getIsDelete());
            updateTaxSharedDataMessage.setSlabList(((Tax) object).getSlabList());
            updateTaxSharedDataMessage.setTieredList(((Tax) object).getTieredList());
//            messageSender.send(updateTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE);
//            messageSender.send(updateTaxSharedDataMessage, SharedDataConstants.QUEUE_TAX_UPDATE_DATA_SHARE_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updateTaxSharedDataMessage,UpdateTaxSharedDataMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(Discount.class)) {

            UpdateDiscountSharedMessage updateDiscountSharedMessage=new UpdateDiscountSharedMessage();
            updateDiscountSharedMessage.setId(((Discount) object).getId());
            updateDiscountSharedMessage.setName(((Discount) object).getName());
            updateDiscountSharedMessage.setStatus(((Discount) object).getStatus());
            updateDiscountSharedMessage.setMvnoId(((Discount) object).getMvnoId());
            updateDiscountSharedMessage.setDiscMappingList(((Discount) object).getDiscMappingList());

            updateDiscountSharedMessage.setPlanMappingList(((Discount) object).getPlanMappingList());
            updateDiscountSharedMessage.setBuId(((Discount) object).getBuId());
//            messageSender.send(updateDiscountSharedMessage, SharedDataConstants.QUEUE_DISCOUNT_UPDATE_DATA_SHARE_REVENUEMANAGEMENT_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(updateDiscountSharedMessage,UpdateDiscountSharedMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(ClientService.class)) {
            UpdateClientServMessage clientServMessge = new UpdateClientServMessage();
            clientServMessge.setId(((ClientService) object).getId());
            clientServMessge.setValue(((ClientService) object).getValue());
            clientServMessge.setName(((ClientService) object).getName());
            clientServMessge.setMvnoId(((ClientService) object).getMvnoId());
//            messageSender.send(clientServMessge, SharedDataConstants.QUEUE_CLIENT_SERV_UPDATE_DATA_SHARE_TICKET_MICROSERVICE);

            kafkaMessageSender.send(new KafkaMessageData(clientServMessge,UpdateClientServMessage.class.getSimpleName()));

        }
        else if (Objects.nonNull(object) && object.getClass().equals(PriceBook.class))  {
            UpdatePricebookSharedMessage updatePricebookSharedMessage=new UpdatePricebookSharedMessage();
            updatePricebookSharedMessage.setId(((PriceBook) object).getId());
            updatePricebookSharedMessage.setBookname(((PriceBook) object).getBookname());
            if(((PriceBook) object).getValidfrom()!=null){
                updatePricebookSharedMessage.setValidfrom(((PriceBook) object).getValidfrom().toString());
            }
            if(((PriceBook) object).getValidto()!=null) {
                updatePricebookSharedMessage.setValidto(((PriceBook) object).getValidto().toString());
            }
            updatePricebookSharedMessage.setStatus(((PriceBook) object).getStatus());
            updatePricebookSharedMessage.setDescription(((PriceBook) object).getDescription());
            updatePricebookSharedMessage.setCommission_on(((PriceBook) object).getCommission_on());
            updatePricebookSharedMessage.setIsAllPlanSelected(((PriceBook) object).getIsAllPlanSelected());
            updatePricebookSharedMessage.setIsAllPlanGroupSelected(((PriceBook) object).getIsAllPlanGroupSelected());
            updatePricebookSharedMessage.setRevenueSharePercentage(((PriceBook) object).getRevenueSharePercentage());
            List<PriceBookPlanDetail> priceBookPlanDetails=new ArrayList<>();
            for(PriceBookPlanDetail priceBookPlanDetail:((PriceBook) object).getPriceBookPlanDetailList() ){
                priceBookPlanDetails.add(new PriceBookPlanDetail(priceBookPlanDetail));
            }
            updatePricebookSharedMessage.setPriceBookPlanDetailList(priceBookPlanDetails);
            List<ServiceCommission> serviceCommissionList=new ArrayList<>();

            for(ServiceCommission comission: ((PriceBook) object).getServiceCommissionList()){
                serviceCommissionList.add(new ServiceCommission(comission));
            }
            updatePricebookSharedMessage.setServiceCommissionList(serviceCommissionList);

            updatePricebookSharedMessage.setIsDeleted(((PriceBook) object).getIsDeleted());

            updatePricebookSharedMessage.setMvnoId(((PriceBook) object).getMvnoId());
            updatePricebookSharedMessage.setAgrPercentage(((PriceBook) object).getAgrPercentage());

            updatePricebookSharedMessage.setTdsPercentage(((PriceBook) object).getTdsPercentage());
            List<PriceBookSlabDetails> pricebooklist=new ArrayList<>();
            updatePricebookSharedMessage.setBuId(((PriceBook) object).getBuId());
            for(PriceBookSlabDetails pricebook :((PriceBook) object).getPriceBookSlabDetailsList()){
                pricebooklist.add(new PriceBookSlabDetails(pricebook));
            }
            updatePricebookSharedMessage.setPriceBookSlabDetailsList(pricebooklist);
            updatePricebookSharedMessage.setCreatedBYId(((PriceBook) object).getCreatedById());
            updatePricebookSharedMessage.setLastModifiedByname(((PriceBook) object).getLastModifiedByName());

            updatePricebookSharedMessage.setRevenueType(((PriceBook) object).getRevenueType());
//            messageSender.send(updatePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_REVENUE);
//            messageSender.send(updatePricebookSharedMessage,SharedDataConstants.QUEUE_PRICEBOOK_UPDATE_DATA_PARTNER);

            kafkaMessageSender.send(new KafkaMessageData(updatePricebookSharedMessage,UpdatePricebookSharedMessage.class.getSimpleName(),"PRICEBOOK_UPDATE"));

        } else if (Objects.nonNull(object) && object.getClass().equals(CasMaster.class)) {
            UpdateCasMasterSharedDataMessage updateCasMasterSharedDataMessage = new UpdateCasMasterSharedDataMessage();
            updateCasMasterSharedDataMessage.setId(((CasMaster) object).getId());
            updateCasMasterSharedDataMessage.setCasname(((CasMaster) object).getCasname());
            updateCasMasterSharedDataMessage.setEndpoint(((CasMaster) object).getEndpoint());
            updateCasMasterSharedDataMessage.setBuId(((CasMaster) object).getBuId());
            updateCasMasterSharedDataMessage.setStatus(((CasMaster) object).getStatus());
            updateCasMasterSharedDataMessage.setIsDeleted(((CasMaster) object).getIsDeleted());
            updateCasMasterSharedDataMessage.setMvnoId(((CasMaster) object).getMvnoId());
            updateCasMasterSharedDataMessage.setCreatedById(((CasMaster) object).getCreatedById());
            updateCasMasterSharedDataMessage.setLastModifiedById(((CasMaster) object).getLastModifiedById());
            updateCasMasterSharedDataMessage.setCasParameterMappings(((CasMaster) object).getCasParameterMappings());
//            messageSender.send(updateCasMasterSharedDataMessage, SharedDataConstants.QUEUE_CASMASTER_UPDATE_DATA_SHARE_INVENTORY);

            kafkaMessageSender.send(new KafkaMessageData(updateCasMasterSharedDataMessage,UpdateCasMasterSharedDataMessage.class.getSimpleName()));

        }
    }


    public void sendCustomerEntityForAllMicroService(Object object, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList, List<CustomerChargeHistory> customerChargeHistories , CustomersPojo pojo,Boolean isCaptiveportal, List<CustChargeInstallment> custChargeInstallments){
        SaveCustomerDataShareMessage saveCustomerDataShareMessage = new SaveCustomerDataShareMessage((Customers) object,custPlanMapppingList, customerServiceMappingList, customerChargeHistories,pojo.getPaymentDetails(), pojo.getRefMvno(),isCaptiveportal,pojo.getReferenceNo(), custChargeInstallments);
//        messageSender.send(saveCustomerDataShareMessage, SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_TICKET);
//        messageSender.send(saveCustomerDataShareMessage, SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_INVENTORY);
//        //messageSender.send(saveCustomerDataShareMessage, SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_COMMON);
        kafkaMessageSender.sendCustomerData(new KafkaMessageData(saveCustomerDataShareMessage,saveCustomerDataShareMessage.getClass().getSimpleName()));
//        messageSender.send(saveCustomerDataShareMessage, SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_REVENUEMANAGEMENT);
//        messageSender.send(saveCustomerDataShareMessage, SharedDataConstants.QUEUE_CUSTOMERS_CREATE_DATA_SHARE_API_PARTNER);

    }

    public void updateCustomerEntityForAllMicroServce(Object object, List<CustPlanMappping> custPlanMapppingList, List<CustomerServiceMapping> customerServiceMappingList){
        UpdateCustomerShareDataMessage updateCustomerShareDataMessage = new UpdateCustomerShareDataMessage((Customers) object,custPlanMapppingList, customerServiceMappingList);

        //messageSender.send(updateCustomerShareDataMessage, SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_COMMON);
        kafkaMessageSender.sendCustomerData(new KafkaMessageData(updateCustomerShareDataMessage,updateCustomerShareDataMessage.getClass().getSimpleName()));
//        messageSender.send(updateCustomerShareDataMessage, SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_INVENTORY);
//        messageSender.send(updateCustomerShareDataMessage, SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_TICKET);
//        messageSender.send(updateCustomerShareDataMessage, SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_REVENUEMANAGEMENT);
//        messageSender.send(updateCustomerShareDataMessage, RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_SALESCRM);
//        messageSender.send(updateCustomerShareDataMessage, RabbitMqConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_RADIUS);
//        messageSender.send(updateCustomerShareDataMessage, SharedDataConstants.QUEUE_CUSTOMERS_UPDATE_DATA_SHARE_API_PARTNER);

    }

    public void updateCustomerCPREntityForAllMicroServce(Object object, List<CustPlanMappping> custPlanMapppingList){
        UpdateCustomerCprDateAndStatus updateCustomerCprDateAndStatus = new UpdateCustomerCprDateAndStatus(((Customers) object).getId(), custPlanMapppingList);
//        messageSender.send(updateCustomerCprDateAndStatus, SharedDataConstants.QUEUE_CPR_UPDATE_DATE_SHARE_REVENUEMANAGEMENT);
        kafkaMessageSender.send(new KafkaMessageData(updateCustomerCprDateAndStatus,UpdateCustomerCprDateAndStatus.class.getSimpleName()));

    }


    public void saveEntityList(List<Object> objectList) {
        if (!objectList.isEmpty() && objectList.get(0) instanceof CustPlanMappping) {
            List<CustPlanMappping> custPlanMappings = new ArrayList<>();
            for (Object obj : objectList) {
                custPlanMappings.add((CustPlanMappping) obj);
            }
            SaveCustplanMappingSharedData saveCustplanMappingSharedData = new SaveCustplanMappingSharedData(custPlanMappings);
        }
    }

    public void sendChangePlanForAllMicroService(List<CustPlanMappping> newCustPlanMapppingList, List<CustPlanMappping> oldCustPlanMapppingList, Set<CustomerChargeHistory> customerChargeHistories,
                                                 String type, Integer renewalId, List<CustomerServiceMapping> customerServiceMappingList, Integer parentId, List<Integer> childIds, String paysource, RecordPaymentPojo recordPaymentDTO, AdditionalInformationDTO additionalInformationDTO, List<Integer> overrideCharges, boolean changePlanNextBillDate, Boolean isAutoPaymentRequired, List<CreditDocumentPaymentPojo> paymentPojoList, Integer payingCustId, List<CustChargeInstallment> custChargeInstallments , FlagDTO flagDTO) {
        try {

            ChangePlanMessage changePlanMessage = new ChangePlanMessage();
            changePlanMessage.setType(type);
            changePlanMessage.setIsAutoPaymentRequired(isAutoPaymentRequired);
            changePlanMessage.setCreditDocumentPaymentPojoList(paymentPojoList);
            changePlanMessage.setRenewalId(renewalId);
            changePlanMessage.setChangePlanNextBillDate(changePlanNextBillDate);
            if (flagDTO != null){
                changePlanMessage.setFlagDTO(flagDTO);
            }
            List<CustPlanMappingRevenue> custPlanMappingRevenues = new ArrayList<>();
            List<PostpaidPlan> oldplanList=new ArrayList<>();
            List<PostpaidPlan> newPlanList=new ArrayList<>();
            for (CustPlanMappping data : newCustPlanMapppingList) {
                if(data.getPlanId() != null) {
                    newPlanList.add(postpaidPlanRepo.findById(data.getPlanId()).orElse(null));
                }
                CustPlanMappingRevenue custPlanMappingRevenue = new CustPlanMappingRevenue(data);
                custPlanMappingRevenues.add(custPlanMappingRevenue);
            }
            changePlanMessage.setNewCustPlanMappingRevenues(custPlanMappingRevenues);


            List<CustPlanMappingRevenue> oldCustPlanMappingRevenues = new ArrayList<>();
            if (oldCustPlanMapppingList!=null) {
                for (CustPlanMappping data : oldCustPlanMapppingList) {
                    if(data.getPlanId() != null) {
                        oldplanList.add(postpaidPlanRepo.findById(data.getPlanId()).orElse(null));
                    }
                    CustPlanMappingRevenue custPlanMappingRevenue = new CustPlanMappingRevenue(data.getId(), data.getEndDate(), data.getExpiryDate(), data.getCustPlanStatus(),data.getVasId(),data.getStatus());
                    oldCustPlanMappingRevenues.add(custPlanMappingRevenue);
                }
            }
            changePlanMessage.setOldCustPlanMappingRevenues(oldCustPlanMappingRevenues);

            List<CustomerServiceMappingRevenue> customerServiceMappingRevenues = new ArrayList<>();
            if (customerServiceMappingList!=null) {
                for (CustomerServiceMapping data : customerServiceMappingList) {
                    CustomerServiceMappingRevenue customerServiceMappingRevenue = new CustomerServiceMappingRevenue(data);
                    customerServiceMappingRevenues.add(customerServiceMappingRevenue);
                }
            }
            changePlanMessage.setCustomerServiceMappingRevenues(customerServiceMappingRevenues);

            List<CustomerChargeHistoryRevenue> customerChargeHistoryRevenues = new ArrayList<>();
            for (CustomerChargeHistory data : customerChargeHistories) {
                CustomerChargeHistoryRevenue customerServiceMappingRevenue = new CustomerChargeHistoryRevenue(data);
                customerChargeHistoryRevenues.add(customerServiceMappingRevenue);
            }
            changePlanMessage.setCustomerChargeHistoryRevenues(customerChargeHistoryRevenues);
            changePlanMessage.setCreatedById(getLoggedInUser().getUserId());
            changePlanMessage.setParentId(parentId);
            changePlanMessage.setChildIds(childIds);
            if(paysource != null){
                changePlanMessage.setPaySource(paysource);
            }
            Integer mvnoId = getLoggedInUser().getMvnoId();
            if(getLoggedInUser().getBuIds() != null && !getLoggedInUser().getBuIds().isEmpty()) {
                List<Long> buId = getLoggedInUser().getBuIds();
                changePlanMessage.setBuId(buId);
            }
            Integer partnerId = getLoggedInUser().getPartnerId();
            changePlanMessage.setMvnoId(mvnoId);
            changePlanMessage.setLcoId(partnerId);
            changePlanMessage.setIsLco(getLoggedInUser().getLco());
            changePlanMessage.setGetCreatedById(getLoggedInUser().getUserId());
            changePlanMessage.setGetCreatedByName(getLoggedInUser().getFirstName());

            if(recordPaymentDTO != null) {
                if(recordPaymentDTO.getChequedate() != null) {
                    recordPaymentDTO.setChequedatestr(recordPaymentDTO.getChequedate().toString());
                    recordPaymentDTO.setChequedate(null);
                }
                if(recordPaymentDTO.getPaymentdate() != null) {
                    recordPaymentDTO.setPaymentdatestr(recordPaymentDTO.getPaymentdate().toString());
                    recordPaymentDTO.setPaymentdate(null);
                }
                changePlanMessage.setRecordPaymentDTO(recordPaymentDTO);
            }
            if(additionalInformationDTO != null){
                changePlanMessage.setAdditionalInformationDTO(additionalInformationDTO);
            }
            if(!CollectionUtils.isEmpty(overrideCharges))
                changePlanMessage.setOverrideChargeIds(overrideCharges);
            if(payingCustId != null){
                changePlanMessage.setPayingChildId(payingCustId);
            }
            changePlanMessage.setCustChargeInstallments(custChargeInstallments);

//            messageSender.send(changePlanMessage, SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_REVENUE);
//            messageSender.send(changePlanMessage, SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_TICKET);
            kafkaMessageSender.sendCustomerChangePlanData(new KafkaMessageData(changePlanMessage,ChangePlanMessage.class.getSimpleName()));

            Customers customers=newCustPlanMapppingList.get(0).getCustomer();
            for (int i = 0; i < newPlanList.size(); i++) {
                oldplanList.stream()
                        .filter(map -> map.getPlanGroup().equalsIgnoreCase("Registration and Renewal") ||
                                map.getPlanGroup().equalsIgnoreCase("Registration"))
                        .collect(Collectors.toList());
                Double validity=newPlanList.get(i).getValidity();
                String validityUnit=newPlanList.get(i).getUnitsOfValidity();
                Long buId = customers.getBuId();
                Integer buIdValue = (buId != null) ? buId.intValue() : null;
                if(oldplanList.size()>0) {
                    ChangePlanNotification changePlanNotification = new ChangePlanNotification(customers.getEmail(), customers.getMobile(), customers.getUsername(), LocalDate.now().plusDays(validity.longValue()).toString(), customers.getAltemail(), customers.getCountryCode(), customers.getMvnoId(), buIdValue, oldplanList.get(i).getName(), newPlanList.get(i).getName(), validity.intValue(), validityUnit, customers.getId(),getLoggedInUser().getStaffId().longValue(),newCustPlanMapppingList.get(0).getCustPlanStatus());
//              messageSender.send(changePlanNotification,SharedDataConstants.QUEUE_CHANGE_PLAN_DATA_SHARE_NOTIFICATION);
                    kafkaMessageSender.send(new KafkaMessageData(changePlanNotification, ChangePlanNotification.class.getSimpleName()));
                }

            }
//            for (CustPlanMappping data : newCustPlanMapppingList) {
//               for(CustQuotaDetails quotaDtlsPojo:data.getQuotaList() ){
//
//               }
//            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

    public void sendVoucherBatchForAllMicroService(VoucherBatchDto voucherBatchDto, Integer partnerId) {
        SaveVoucherBatchSharedDataMessage message=new SaveVoucherBatchSharedDataMessage(voucherBatchDto,partnerId);
//        messageSender.send(message,SharedDataConstants.QUEUE_SAVE_VOUCHER_BATCH_DATA_SHARE_TO_REVENUEMANAGEMENT);

        kafkaMessageSender.send(new KafkaMessageData(message,SaveVoucherBatchSharedDataMessage.class.getSimpleName()));

    }

    public void sendGeneratedConnectionNumber(Integer id, Integer custId, String connectionNo, Integer partnerId, Integer mvnoId) {
        UpdateGeneratedConnectionNumberMessage updateGeneratedConnectionNumberMessage = new UpdateGeneratedConnectionNumberMessage();
        updateGeneratedConnectionNumberMessage.setId(id);
        updateGeneratedConnectionNumberMessage.setCustomerId(custId);
        updateGeneratedConnectionNumberMessage.setConnectionNo(connectionNo);
        updateGeneratedConnectionNumberMessage.setPartnerId(partnerId);
        updateGeneratedConnectionNumberMessage.setMvnoId(mvnoId);
        kafkaMessageSender.sendCustomerData(new KafkaMessageData(updateGeneratedConnectionNumberMessage, updateGeneratedConnectionNumberMessage.getClass().getSimpleName()));
    }
}
