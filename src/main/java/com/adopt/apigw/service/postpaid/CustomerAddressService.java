package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.MicroSeviceDataShare.SharedDataConstants.SharedDataConstants;
import com.adopt.apigw.MicroSeviceDataShare.SharedMessages.ShiftlocationMessage;
import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.controller.api.APIController;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.kafka.KafkaConstant;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.*;
import com.adopt.apigw.modules.Area.domain.QArea;
import com.adopt.apigw.modules.Area.repository.AreaRepository;
import com.adopt.apigw.modules.Branch.domain.Branch;
import com.adopt.apigw.modules.Branch.repository.BranchRepository;
import com.adopt.apigw.modules.BuildingMgmt.Domain.BuildingManagement;
import com.adopt.apigw.modules.BuildingMgmt.Domain.QBuildingManagement;
import com.adopt.apigw.modules.BuildingMgmt.Repository.BuildingMgmtRepository;
import com.adopt.apigw.modules.Pincode.domain.QPincode;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.modules.ServiceArea.service.ServiceAreaService;
import com.adopt.apigw.modules.SubArea.Domain.QSubArea;
import com.adopt.apigw.modules.SubArea.Repository.SubAreaRepository;
import com.adopt.apigw.modules.Teams.service.HierarchyService;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import com.adopt.apigw.modules.Template.repository.NotificationTemplateRepository;
import com.adopt.apigw.pojo.CustChargeOverride;
import com.adopt.apigw.pojo.api.*;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.CustAddressShiftingMsg;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CustomerAddressRepository;
import com.adopt.apigw.repository.postpaid.PartnerRepository;
import com.adopt.apigw.repository.postpaid.PartnerServiceAreaMappingRepo;
import com.adopt.apigw.repository.postpaid.ShiftLocationRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.service.common.WorkflowAuditService;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.spring.MessagesPropertyConfig;
import com.adopt.apigw.utils.APIConstants;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.UpdateDiffFinder;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.collections4.IterableUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerAddressService extends AbstractService<CustomerAddress, CustomerAddressPojo, Integer> {

    @Autowired
    private MessagesPropertyConfig messagesProperty;

    @Autowired
    StaffUserRepository staffUserRepository;

    @Autowired
    private CustomerAddressRepository entityRepository;

    @Autowired
    private CustomersService custService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StateService stateService;
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private CityService cityService;

    @Autowired
    private PartnerService partnerService;

    @Autowired
    private ServiceAreaService serviceAreaService;

    @Autowired
    private PartnerServiceAreaMappingRepo partnerServiceAreaMappingRepo;

    @Autowired
    private CustomersRepository customersRepository;

    @Autowired
    private PincodeRepository pincodeRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private ClientServiceSrv clientServiceSrv;

    @Autowired
    HierarchyService hierarchyService;

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    WorkflowAuditService workflowAuditService;

    @Autowired
    DbrService dbrService;

    @Autowired
    CustomerAddressRepository customerAddressRepository;

    @Autowired
    DebitDocService debitDocService;

    @Autowired
    ShiftLocationRepository shiftLocationRepository;

    @Autowired
    CustChargeService custChargeService;

    @Autowired
    PartnerCommissionService partnerCommissionService;

    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    @Autowired
    NotificationTemplateRepository templateRepository;
   
    @Autowired
    PartnerRepository partnerRepository;

    @Autowired
    ServiceAreaRepository serviceAreaRepository;

    @Autowired
    BranchRepository branchRepository;

    @Autowired
    private SubAreaRepository subAreaRepository;

    @Autowired
    private BuildingMgmtRepository buildingMgmtRepository;

    private static final Logger log = LoggerFactory.getLogger(APIController.class);

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CustomerAddress', '1')")
    public Page<CustomerAddress> searchByCustomer(Customers cust, Integer pageNumber, int pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, pageSize);
        return entityRepository.findByCustomer(cust, pageRequest);
    }

    @Override
    protected JpaRepository<CustomerAddress, Integer> getRepository() {
        return entityRepository;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddress> findAllByCustomers(Customers customer) {
        return entityRepository.findAllByCustomer(customer);
    }

    public CustomerAddress findByAddressTypeAndCustomer(String addressType, Customers customer, String version) {
        return entityRepository.findByAddressTypeAndCustomerAndVersion(addressType, customer, version);
    }

    public CustomerAddress findByAddressTypeAndCustomer(String addressType, Customers customer) {
        return entityRepository.findByAddressTypeAndCustomerAndVersion(addressType, customer, "NEW");
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '4')")
    public void deleteCustomerAddress(Integer id) throws Exception {
        entityRepository.deleteById(id);
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '2')")
    public CustomerAddress getCustomerAddressForAdd(Integer custid) throws Exception {
        CustomerAddress add = new CustomerAddress();
        Customers cust =  customersRepository.findById(custid).get();
        add.setCustomer(cust);
        return add;
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '2')")
    public CustomerAddress getCustomerAddressForEdit(Integer id) throws Exception {
        return entityRepository.getOne(id);
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '2')")
    public CustomerAddress saveCustomerAddress(CustomerAddress customerAddress) throws Exception {
        customerAddress.setCity(cityService.get(customerAddress.getCityId(),getMvnoIdFromCurrentStaff(customerAddress.getCustomer().getMvnoId())));
        customerAddress.setState(stateService.get(customerAddress.getStateId(),getMvnoIdFromCurrentStaff(customerAddress.getCustomer().getMvnoId())));
        customerAddress.setCountry(countryService.get(customerAddress.getCountryId(),getMvnoIdFromCurrentStaff(customerAddress.getCustomer().getMvnoId())));
        CustomerAddress save = entityRepository.save(customerAddress);
        return save;
    }

//    @PreAuthorize("hasPermission('com.adopt.apigw.controller.common.CustomerAddress', '2')")
    public CustomerAddressPojo save(CustomerAddressPojo pojo) throws Exception {
        CustomerAddress oldObj = null;
//        if (pojo.getId() != null) {
//            oldObj = get(pojo.getId());
//        }
        CustomerAddress obj = convertCustomerAddressPojoToCustomerAddressModel(pojo);
//        if(oldObj!=null) {
//            log.info("CustomerAddress update details "+ UpdateDiffFinder.getUpdatedDiff(oldObj, obj));
//        }
        obj = saveCustomerAddress(obj);
        pojo = convertCustomerAddressModelToCustomerAddressPojo(obj);
        return pojo;
    }

    public CustomerAddress convertCustomerAddressPojoToCustomerAddressModel(CustomerAddressPojo customerAddressPojo) throws Exception {
        CustomerAddress customerAddress = null;
        if (customerAddressPojo != null) {
            customerAddress = new CustomerAddress();
            if (customerAddressPojo.getId() != null) {
                customerAddress.setId(customerAddressPojo.getId());
            }
            customerAddress.setLandmark(customerAddressPojo.getLandmark());
            customerAddress.setLandmark1(customerAddressPojo.getLandmark1());
            customerAddress.setAddress1(customerAddressPojo.getAddress1());
            customerAddress.setAddress2(customerAddressPojo.getAddress2());
            customerAddress.setAddressType(customerAddressPojo.getAddressType());
            customerAddress.setCityId(customerAddressPojo.getCityId());
            customerAddress.setStateId(customerAddressPojo.getStateId());
            customerAddress.setCountryId(customerAddressPojo.getCountryId());
            customerAddress.setPincodeId(customerAddressPojo.getPincodeId());
            customerAddress.setPincode(pincodeRepository.getOne(customerAddressPojo.getPincodeId().longValue()));
            customerAddress.setAreaId(customerAddressPojo.getAreaId());
            customerAddress.setArea(areaRepository.getOne(customerAddressPojo.getAreaId().longValue()));
            customerAddress.setFullAddress(customerAddressPojo.getFullAddress());
            customerAddress.setCity(cityService.get(customerAddressPojo.getCityId(),getMvnoIdFromCurrentStaff(customerAddressPojo.getCustomerId())));
            customerAddress.setState(stateService.get(customerAddressPojo.getStateId(),getMvnoIdFromCurrentStaff(customerAddressPojo.getCustomerId())));
            customerAddress.setCountry(countryService.get(customerAddressPojo.getCountryId(),getMvnoIdFromCurrentStaff(customerAddressPojo.getCustomerId())));
            customerAddress.setIsDelete(customerAddressPojo.getIsDelete());
            customerAddress.setNextTeamHierarchyMappingId(customerAddressPojo.getNextTeamHierarchyMappingId());
            customerAddress.setNextStaff(customerAddressPojo.getNextStaff());
            customerAddress.setStatus(customerAddressPojo.getStatus());
            customerAddress.setVersion(customerAddressPojo.getVersion());
            customerAddress.setShiftId(customerAddressPojo.getShiftId());
            // customerAddress.setShiftedPartnerId(customerAddressPojo.getShiftedPartnerId());
            // customerAddress.setShitedServiceAreaId(customerAddressPojo.getShiftedServiceAreaId());
            if(customerAddressPojo.getBuilding_mgmt_id() != null){
                customerAddress.setBuildingManagement(buildingMgmtRepository.findById(customerAddressPojo.getBuilding_mgmt_id()).orElse(null));
            }
            if(customerAddressPojo.getBuildingNumber() != null){
                customerAddress.setBuildingNumber(customerAddressPojo.getBuildingNumber());
            }
            if(customerAddressPojo.getSubareaId() != null){
                customerAddress.setSubarea(subAreaRepository.findById(customerAddressPojo.getSubareaId()).orElse(null));
            }

            if (null != customerAddressPojo.getCustomerId())
                customerAddress.setCustomer( customersRepository.findById(customerAddressPojo.getCustomerId()).get());
        }
        return customerAddress;
    }

    public CustomerAddressPojo convertCustomerAddressModelToCustomerAddressPojo(CustomerAddress customerAddress) throws Exception {
        CustomerAddressPojo pojo = null;
        if (customerAddress != null) {
            pojo = new CustomerAddressPojo();
            pojo.setId(customerAddress.getId());
            pojo.setLandmark(customerAddress.getLandmark());
            pojo.setLandmark1(customerAddress.getLandmark1());
            pojo.setAddress1(customerAddress.getAddress1());
            pojo.setAddress2(customerAddress.getAddress2());
            pojo.setAddressType(customerAddress.getAddressType());
            pojo.setCityId(customerAddress.getCityId());
            pojo.setStateId(customerAddress.getStateId());
            pojo.setCountryId(customerAddress.getCountryId());
            pojo.setPincodeId(customerAddress.getPincodeId());
            pojo.setAreaId(customerAddress.getAreaId());
            pojo.setFullAddress(customerAddress.getFullAddress());
            pojo.setCustomerId(customerAddress.getCustomer().getId());
            pojo.setIsDelete(customerAddress.getIsDelete());
            pojo.setNextTeamHierarchyMappingId(customerAddress.getNextTeamHierarchyMappingId());
            pojo.setNextStaff(customerAddress.getNextStaff());
            pojo.setStatus(customerAddress.getStatus());
            pojo.setVersion(customerAddress.getVersion());
            pojo.setShiftId(customerAddress.getShiftId());
            pojo.setRequestedDate(customerAddress.getRequestedDate());
            pojo.setRequestedByName(customerAddress.getRequestedByName());
            if(customerAddress.getBuildingManagement() != null){
                pojo.setBuilding_mgmt_id(customerAddress.getBuildingManagement().getBuildingMgmtId());
            }
            if(customerAddress.getBuildingNumber() != null){
                pojo.setBuildingNumber(customerAddress.getBuildingNumber());
            }
            if(customerAddress.getSubarea() != null){
                pojo.setSubareaId(customerAddress.getSubarea().getId());
            }
        }
        return pojo;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddressPojo> convertResponseModelIntoPojo(List<CustomerAddress> customerAddressList) throws Exception {
        List<CustomerAddressPojo> pojoListRes = new ArrayList<CustomerAddressPojo>();
        if (customerAddressList != null && customerAddressList.size() > 0) {
            for (CustomerAddress customerAddress : customerAddressList) {
                if (customerAddress.getVersion().equalsIgnoreCase("NEW")) {
                    pojoListRes.add(convertCustomerAddressModelToCustomerAddressPojo(customerAddress));
                }
            }
        }
        return pojoListRes;
    }

    @PreAuthorize("hasPermission('com.adopt.apigw.model.postpaid.CustomerAddress', '1')")
    public List<CustomerAddress> convertPojoListIntoResponseModelList(List<CustomerAddressPojo> customerAddressList) throws Exception {
        List<CustomerAddress> pojoListRes = new ArrayList<CustomerAddress>();
        if (customerAddressList != null && customerAddressList.size() > 0) {
            for (CustomerAddressPojo customerAddress : customerAddressList) {
                if (customerAddress.getVersion().equalsIgnoreCase("NEW")) {
                    pojoListRes.add(convertCustomerAddressPojoToCustomerAddressModel(customerAddress));
                }
            }
        }
        return pojoListRes;
    }

    public void validateRequest(CustomerAddressPojo pojo, Integer operation) {

        if (pojo == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.required.object.not.set"), null);
        }
        if (pojo != null && operation == CommonConstants.OPERATION_ADD) {
            if (pojo.getId() != null)
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.validation"), null);
        }
        if (pojo.getCountryId() != null) {
            if (countryService.get(pojo.getCountryId(),getMvnoIdFromCurrentStaff(pojo.getCustomer().getMvnoId())) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.country.not.found"), null);
            }
        }
        if (pojo.getStateId() != null) {
            if (stateService.get(pojo.getStateId(),getMvnoIdFromCurrentStaff(pojo.getCustomer().getMvnoId())) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.state.not.found"), null);
            }
        }
        if (pojo.getCityId() != null) {
            if (cityService.get(pojo.getCityId(),getMvnoIdFromCurrentStaff(pojo.getCustomer().getMvnoId())) == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.city.not.found"), null);
            }
        }
        if (pojo.getCustomerId() != null) {
            if ( customersRepository.findById(pojo.getCustomerId()).get() == null) {
                throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.customer.not.found"), null);
            }
        }
        if (!(pojo.getAddressType().equalsIgnoreCase("home") || pojo.getAddressType().equalsIgnoreCase("office") || pojo.getAddressType().equalsIgnoreCase("other"))) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.inproper.value.for.address.type"), null);
        }
        if (operation != CommonConstants.OPERATION_DELETE && findByAddressTypeAndCustomer(pojo.getAddressType(),  customersRepository.findById(pojo.getCustomerId()).get()) != null) {
            throw new CustomValidationException(APIConstants.FAIL, pojo.getAddressType() + " " + messagesProperty.get("api.address.already.avl"), null);
        }
        if (pojo != null && (operation == CommonConstants.OPERATION_UPDATE || operation == CommonConstants.OPERATION_DELETE) && pojo.getId() == null) {
            throw new CustomValidationException(APIConstants.FAIL, messagesProperty.get("api.id.cannot.set.null"), null);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Customer Address");
        List<CustomerAddressPojo> customerAddressPojoList = new ArrayList<>();
        List<CustomerAddress> customerAddressList = entityRepository.findAll();
        for (CustomerAddress address : customerAddressList)
            customerAddressPojoList.add(convertCustomerAddressModelToCustomerAddressPojo(address));
        createExcel(workbook, sheet, CustomerAddressPojo.class, customerAddressPojoList, null);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustomerAddressPojo> customerAddressPojoList = new ArrayList<>();
        List<CustomerAddress> customerAddressList = entityRepository.findAll();
        for (CustomerAddress address : customerAddressList)
            customerAddressPojoList.add(convertCustomerAddressModelToCustomerAddressPojo(address));
        createPDF(doc, CustomerAddressPojo.class, customerAddressPojoList, null);
    }

    //
    public ShiftLocationDTO shiftCustomerLocation(ShiftLocationDTO shiftLocationDTO, Integer custId) throws Exception {
        if (shiftLocationDTO != null && custId != null) {
            ServiceArea serviceArea = null;
            if (custId != null)
            {
                Customers customers =  customersRepository.findById(custId).get();
                if (customers != null)
                {
                    {
                        Partner oldPartner=customers.getPartner();

                        if(shiftLocationDTO.getTransferableBalance()==null)
                            shiftLocationDTO.setTransferableBalance(0.0);

                        if(shiftLocationDTO.getShiftPartnerid()!=null)
                        {
                            Optional<Partner> newPartner= partnerRepository.findById(shiftLocationDTO.getShiftPartnerid());
                            if(newPartner.isPresent() && shiftLocationDTO.getTransferableBalance()>0.0)
                            {
                                if(shiftLocationDTO.getShiftPartnerid()!=null && newPartner.get().getId() != CommonConstants.DEFAULT_PARTNER_ID && newPartner.get().getBalance()<shiftLocationDTO.getTransferableBalance())
                                {
                                    throw new CustomValidationException(APIConstants.FAIL, "Partner has insufficient balance for location shift for customerId " + custId, null);
                                }
                            }
                        }

                        if(shiftLocationDTO.getShiftPartnerid()!=null && oldPartner.getId() != CommonConstants.DEFAULT_PARTNER_ID && (oldPartner.getBalance()+shiftLocationDTO.getTransferableBalance())<shiftLocationDTO.getTransferableCommission())
                        {
                            throw new CustomValidationException(APIConstants.FAIL, "Partner has insufficient balance for location shift for customerId " + custId, null);
                        }

                        ShiftLocation shiftLocation=new ShiftLocation();
                        shiftLocation.setCustomerId(custId);
                        shiftLocation.setShiftPartnerId(shiftLocationDTO.getShiftPartnerid());
                        shiftLocation.setUpdateAddressServiceAreaId(shiftLocationDTO.getUpdateAddressServiceAreaId());
                        shiftLocation.setTransferableBalance(shiftLocationDTO.getTransferableBalance());
                        shiftLocation.setTransferableCommission(shiftLocationDTO.getTransferableCommission());

                        if(shiftLocationDTO.getCustChargeOverrideDTO()!=null && customers.getStatus() != null && (!customers.getStatus().equalsIgnoreCase("NewActivation")))
                        {
                            CustChargeOverrideDTO dto=shiftLocationDTO.getCustChargeOverrideDTO();
                            if(dto!=null)
                            {
                                List<CustChargeDetailsPojo> detailsPojoList=dto.getCustChargeDetailsPojoList();
                                if(detailsPojoList!=null && !detailsPojoList.isEmpty())
                                {
                                    shiftLocation.setBillableCustomerId(dto.getBillableCustomerId());
                                    if(dto.getPaymentOwnerId()!=null)
                                    {
                                        shiftLocation.setPaymentOwnerId(dto.getPaymentOwnerId());
                                        StaffUser staffUser=staffUserRepository.getOne(dto.getPaymentOwnerId());
                                        if(staffUser!=null)
                                            shiftLocation.setPaymentOwner(staffUser.getUsername());
                                    }

                                    shiftLocation.setChargeId(detailsPojoList.get(0).getChargeid());
                                    shiftLocation.setAmount(detailsPojoList.get(0).getPrice());
                                    if(detailsPojoList.get(0).getDiscount()!=null)
                                        shiftLocation.setDiscount(detailsPojoList.get(0).getDiscount());
                                    else
                                        shiftLocation.setDiscount(0.0);
                                }
                            }
                        }
                        String name = staffUserRepository.findStaffFullNameById(shiftLocationDTO.getRequestedById());
                        shiftLocation.setRequestedByName(name);
                        shiftLocation.setRequestedById(shiftLocationDTO.getRequestedById());
                        shiftLocation.setRequestedDate(LocalDateTime.now());
                        shiftLocation.setBranchId(shiftLocationDTO.getBranchID());

                        // setting latitude and longitude for shift location
                        if (shiftLocationDTO.getAddressDetails() != null) {
                            if(shiftLocationDTO.getAddressDetails().getLatitude() != null){
                                shiftLocation.setLatitude(shiftLocationDTO.getAddressDetails().getLatitude());
                            }

                            if(shiftLocationDTO.getAddressDetails().getLongitude() != null){
                                shiftLocation.setLongitude(shiftLocationDTO.getAddressDetails().getLongitude());
                            }
                        }

                        shiftLocation=shiftLocationRepository.save(shiftLocation);

                        /* method called for send notification*/
                        sendCustOpenAddressShiftingMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId() , customers.getBuId(),(long)custService.getLoggedInStaffId(),customers.getCusttype(),customers.getId());

                        if (shiftLocationDTO.getAddressDetails() != null) {
                            List<CustomerAddress> customerAddressList = entityRepository.findAllByCustomer(customers).stream().filter(add -> !add.getIsDelete()).collect(Collectors.toList());

//                            for (CustomerAddress customerAddress : customerAddressList) {
//                                customerAddress.setVersion("OLD");
//                                customerAddress.setStatus(null);
//                                save(customerAddress);
//                            }

                            CustomerAddressPojo customerAddressPojo = shiftLocationDTO.getAddressDetails();
                            customerAddressPojo.setCustomerId(custId);
                            customerAddressPojo.setIsDelete(false);
                            customerAddressPojo.setAddressType("Present");
                            customerAddressPojo.setVersion("IN_TRANSIT");
                            customerAddressPojo.setStatus("NewActivation");
                            customerAddressPojo.setShiftId(shiftLocation.getId());
                            CustomerAddressPojo customerAddressPojo1 = save(customerAddressPojo);
                            if (shiftLocationDTO.getIsPermanentAddress() == true) {
                                customerAddressPojo.setAddressType("Permanent");
                                save(customerAddressPojo);
                            }
                            if (shiftLocationDTO.getIsPaymentAddresSame() == true) {
                                customerAddressPojo.setAddressType("Payment");
                                save(customerAddressPojo);
                            }
                            if(shiftLocationDTO.getPopid() != null){
                                customers.setPopid(shiftLocationDTO.getPopid());
                            }
                            if(shiftLocationDTO.getOltid() != null){
                                customers.setOltid(shiftLocationDTO.getOltid());
                            }
//                            if(shiftLocationDTO.getBranchID() != null){
//                                customers.setBranch(shiftLocationDTO.getBranchID());
//                            }

                            customersRepository.save(customers);

                            if (customerAddressPojo1.getNextTeamHierarchyMappingId() == null) {
                                if (customerAddressPojo1.getStatus() != null && !"".equals(customerAddressPojo1.getStatus())) {
                                    if (customerAddressPojo1.getStatus().equalsIgnoreCase("NewActivation")) {
                                        String workflow_client = clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN);
                                        if (workflow_client != null && workflow_client.equals("TRUE")) {
                                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, false, true, customerAddressPojo1);
                                            int staffId = 0;
                                            if (map.containsKey("staffId") && map.containsKey("nextTatMappingId")) {
                                                staffId = Integer.parseInt(map.get("staffId"));
                                                StaffUser assignedStaff = staffUserRepository.getOne(staffId);
                                                customerAddressPojo1.setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                                customerAddressPojo1.setNextStaff(staffId);
                                                String action = CommonConstants.WORKFLOW_MSG_ACTION.SHIFT_LOCATION + " for customer : " + " ' " + customers.getFullName() + " '";
                                                hierarchyService.sendWorkflowAssignActionMessage(assignedStaff.getCountryCode(), assignedStaff.getPhone(), assignedStaff.getEmail(), assignedStaff.getMvnoId(), assignedStaff.getFullName(), action, (long) staffId);
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressPojo1.getId(), customers.getFullName(), staffId, assignedStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + assignedStaff.getUsername());
                                            } else {
                                                StaffUser currentStaff = staffUserRepository.getOne(getLoggedInUserId());
                                                customerAddressPojo1.setNextTeamHierarchyMappingId(null);
                                                customerAddressPojo1.setNextStaff(currentStaff.getId());
                                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressPojo1.getId(), customers.getFullName(), staffId, currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                            }
                                        } else {
                                            Map<String, Object> map = hierarchyService.getTeamForNextApprove(customers.getMvnoId(), customers.getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, false, true, customerAddressPojo1);
                                            if (map.containsKey("assignableStaff")) {
                                                StaffUser currentStaff = staffUserRepository.getOne(getLoggedInUserId());
                                                customerAddressPojo1.setNextTeamHierarchyMappingId(null);
                                                customerAddressPojo1.setNextStaff(currentStaff.getId());
                                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressPojo1.getId(), customers.getFullName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                            } else {
                                                StaffUser currentStaff = staffUserRepository.getOne(getLoggedInUserId());
                                                customerAddressPojo1.setNextTeamHierarchyMappingId(null);
                                                customerAddressPojo1.setNextStaff(currentStaff.getId());
                                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressPojo1.getId(), customers.getFullName(), currentStaff.getId(), currentStaff.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Assigned to :- " + currentStaff.getUsername());
                                            }
                                        }
                                    }
                                    save(customerAddressPojo1);
                                }
                            }

                        } else {
                            throw new CustomValidationException(APIConstants.FAIL, "Address details can't be null or empty", null);
                        }
                    }
//                    else {
//                        throw new CustomValidationException(APIConstants.EXPECTATION_FAILED, "Invoices must be cleared for location shift", null);
//                    }
                } else {
                    throw new CustomValidationException(APIConstants.FAIL, "Customers is not found for customerId " + custId, null);
                }
            }
        }
        return shiftLocationDTO;
    }

    public CustomerAddress findByAddressTypeAndCustomerId(String addressType, Integer customerId) {
        QCustomerAddress qCustomerAddress = QCustomerAddress.customerAddress;
        BooleanExpression booleanExpression = qCustomerAddress.isNotNull().and(qCustomerAddress.addressType.eq(addressType)).and(qCustomerAddress.customer.id.eq(customerId));
        booleanExpression = booleanExpression.and(qCustomerAddress.version.equalsIgnoreCase("NEW"));
        List<CustomerAddress>customerAddressList= IterableUtils.toList(entityRepository.findAll(booleanExpression,qCustomerAddress.id.desc()));
        return customerAddressList.get(0);
    }

    public GenericDataDTO updateCustomerAddressAssignment(CustomerCafAssignmentPojo pojo) throws Exception {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        if (pojo.getAddressId() != null && pojo.getStaffId() != null) {
            CustomerAddress customerAddress = getCustomerAddressForEdit(pojo.getAddressId());
            Customers customers = customersRepository.findByIdAndIsDeletedIsFalse(customerAddress.getCustomer().getId());
            Integer oldPartnerId=customers.getPartner().getId();
            List<CustomerAddress> customerAddressList = customerAddressRepository.findAllByCustomerAndVersion(customers, "IN_TRANSIT");
            List<CustomerAddress> oldCustomerAdressList = customerAddressRepository.findAllByCustomerAndVersion(customers , "NEW");

            StaffUser staffUser = staffUserRepository.getOne(pojo.getStaffId());
            StaffUser loggedInUser = staffUserRepository.getOne(getLoggedInUserId());
            StringBuilder approvedByName = new StringBuilder();
            if (!staffUser.getUsername().equalsIgnoreCase("admin")) {
                if (clientServiceSrv.getValueByName(ClientServiceConstant.WORKFLOW_AUTOMATIC_ASSIGN,customers.getMvnoId()).equals("TRUE")) {
                    if (customerAddressList.size() != 0 || customerAddressList != null) {
                        Boolean flag=false;
                        for (int i = 0; i < customerAddressList.size(); i++) {
                            Map<String, String> map = hierarchyService.getTeamForNextApproveForAuto(customerAddressList.get(i).getCustomer().getMvnoId(), customerAddressList.get(i).getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"), false, convertCustomerAddressModelToCustomerAddressPojo(customerAddressList.get(i)));
                            if (!map.containsKey("staffId") && !map.containsKey("nextTatMappingId")) {
                                customerAddressList.get(i).setNextTeamHierarchyMappingId(null);
                                customerAddressList.get(i).setNextStaff(null);
                                if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                                    if(!flag) {
                                        Optional<ShiftLocation> shiftLocation=shiftLocationRepository.findById(customerAddress.getShiftId());
                                        ServiceArea serviceArea=serviceAreaService.getByID(shiftLocation.get().getUpdateAddressServiceAreaId());
                                        customers.setServicearea(serviceArea);
                                        if(shiftLocation.get().getShiftPartnerId()!=null && shiftLocation.get().getShiftPartnerId()!=1){
                                            Partner partner=partnerService.get(shiftLocation.get().getShiftPartnerId(),customers.getMvnoId());
                                            customers.setPartner(partner);
                                            if(customers.getBranch()!=null)
                                                customers.setBranch(null);
                                        }
                                        if(shiftLocation.get().getBranchId()!=null){
                                            Branch branch =branchRepository.getOne(shiftLocation.get().getBranchId());
                                            customers.setBranch(branch.getId());
                                            if(customers.getPartner()!=null) {
                                                Partner partner=partnerService.get(1,customers.getMvnoId());
                                                customers.setPartner(partner);
                                            }
                                        }
                                        oldCustomerAdressList.forEach(customerAddress1 -> customerAddress1.setVersion("OLD"));
                                        customerAddressRepository.saveAll(oldCustomerAdressList);
                                        if(shiftLocation.get().getLatitude() != null){
                                            customers.setLatitude(shiftLocation.get().getLatitude());
                                        }
                                        if(shiftLocation.get().getLongitude() != null){
                                            customers.setLongitude(shiftLocation.get().getLongitude());
                                        }
                                        customersRepository.save(customers);
                                        customerAddressList.get(i).setStatus(SubscriberConstants.ACTIVE);
                                        customerAddressList.get(i).setVersion(SubscriberConstants.SUBSCRIBER_ADDRESS_NEW_STATUS);
                                        customerAddressRepository.save(customerAddressList.get(i));
//                                        if(shiftLocation.get().getTransferableCommission()!=null && shiftLocation.get().getTransferableCommission()>0.0)
//                                            partnerCommissionService.transferCommissionFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableCommission(),customers);
//                                        if(shiftLocation.get().getTransferableBalance()!=null && shiftLocation.get().getTransferableBalance()>0.0)
//                                            partnerCommissionService.transferBalanceFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableBalance(),customers);
                                        if(shiftLocation.get().getChargeId()!=null)
                                            custChargeService.createCustomerChargeOverrideForShiftLocation(customers.getId(),shiftLocation.get().getChargeId(),shiftLocation.get().getAmount(),shiftLocation.get().getBillableCustomerId(),shiftLocation.get().getPaymentOwner(),shiftLocation.get().getPaymentOwnerId(),shiftLocation.get().getDiscount());
                                        //dbrService.updateServiceAreaIdForCustomer(customers.getId(), serviceArea, LocalDate.now());
                                        flag=true;
                                        ShiftlocationMessage shiftlocationMessage=new ShiftlocationMessage();
                                        shiftlocationMessage.setAmount(shiftLocation.get().getAmount());
                                        shiftlocationMessage.setDiscount(shiftLocation.get().getDiscount());
                                        shiftlocationMessage.setChargeId(shiftLocation.get().getChargeId());
                                        shiftlocationMessage.setBillableCustomerId(shiftLocation.get().getBillableCustomerId());
                                        shiftlocationMessage.setOldpartnerId(oldPartnerId);
                                        shiftlocationMessage.setNewPartnerId(shiftLocation.get().getShiftPartnerId());
                                        shiftlocationMessage.setServiceAreaId(serviceArea.getId());
                                        shiftlocationMessage.setCustId(customers.getId());
                                        shiftlocationMessage.setTranferConnission(shiftLocation.get().getTransferableCommission());
                                        shiftlocationMessage.setTransferBalance(shiftLocation.get().getTransferableBalance());
                                        shiftlocationMessage.setPaymentowner(shiftLocation.get().getPaymentOwner());
//                                        messageSender.send(shiftlocationMessage, SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE);
                                        kafkaMessageSender.send(new KafkaMessageData(shiftlocationMessage,ShiftlocationMessage.class.getSimpleName()));

                                    }
                                }
                                else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
                                    customerAddressList.get(i).setStatus(SubscriberConstants.REJECT);
                                    customerAddressList.get(i).setVersion(SubscriberConstants.REJECT);
                                    customerAddressRepository.save(customerAddressList.get(i));
                                }
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressList.get(i).getId(), customerAddressList.get(i).getCustomer().getFullName(), staffUser.getId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                                save(convertCustomerAddressModelToCustomerAddressPojo(customerAddressList.get(i)));
                            }
                            else {
                                customerAddressList.get(i).setNextTeamHierarchyMappingId(Integer.valueOf(map.get("nextTatMappingId")));
                                customerAddressList.get(i).setNextStaff(Integer.valueOf(map.get("staffId")));
                                StaffUser assigned = staffUserRepository.getOne(Integer.valueOf(map.get("staffId")));
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressList.get(i).getId(), customerAddressList.get(i).getCustomer().getFullName(), staffUser.getId(), assigned.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.ASSIGNED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + "Assigned to :- " + assigned.getUsername());
                                workflowAuditService.saveAudit(map.containsKey("eventId") ? Integer.parseInt(map.get("eventId")) : null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressList.get(i).getId(), customerAddressList.get(i).getCustomer().getFullName(), loggedInUser.getId(), loggedInUser.getUsername(), CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " by :- " + staffUser.getUsername());
                                save(convertCustomerAddressModelToCustomerAddressPojo(customerAddressList.get(i)));
                            }
                        }
                    }
                }
                else
                {
                    if(pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected") && customerAddress.getNextTeamHierarchyMappingId()==null){
                        hierarchyService.rejectDirectFromCreatedStaff(CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddress.getId());
                        customerAddress.setNextStaff(null);
                        customerAddress.setStatus(SubscriberConstants.REJECT);
                        customerAddress.setVersion(SubscriberConstants.REJECT);
                        workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddress.getId(), customerAddress.getCustomer().getFullName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                    }
                    else{
                    Map<String, Object> map;
                    map = hierarchyService.getTeamForNextApprove(customerAddress.getCustomer().getMvnoId(), customerAddress.getCustomer().getBuId(), CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, CommonConstants.HIERARCHY_TYPE, pojo.getFlag().equalsIgnoreCase("approved"), customerAddress.getNextTeamHierarchyMappingId() == null, convertCustomerAddressModelToCustomerAddressPojo(customerAddress));
                    if (customerAddressList.size() != 0 || customerAddressList != null) {
                        Boolean flag=false;
                        for (int i = 0; i < customerAddressList.size(); i++) {
                            if (map.containsKey("assignableStaff")) {
                                genericDataDTO.setDataList((List<StaffUserPojo>) map.get("assignableStaff"));
                                workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressList.get(i).getId(), customerAddressList.get(i).getCustomer().getFullName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                            }
                            else {
                                customerAddressList.get(i).setNextStaff(null);
                                customerAddressList.get(i).setNextTeamHierarchyMappingId(null);
                                if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                                    if(!flag) {
                                        Optional<ShiftLocation> shiftLocation=shiftLocationRepository.findById(customerAddress.getShiftId());
                                        ServiceArea serviceArea=serviceAreaService.getByID(shiftLocation.get().getUpdateAddressServiceAreaId());
                                        customers.setServicearea(serviceArea);
                                        if(shiftLocation.get().getShiftPartnerId()!=null && shiftLocation.get().getShiftPartnerId()!=1){
                                            Partner partner=partnerService.get(shiftLocation.get().getShiftPartnerId(),customers.getMvnoId());
                                            customers.setPartner(partner);
                                            if(customers.getBranch()!=null)
                                                customers.setBranch(null);
                                        }
                                        if(shiftLocation.get().getBranchId()!=null){
                                            Branch branch =branchRepository.getOne(shiftLocation.get().getBranchId());
                                            customers.setBranch(branch.getId());
                                            if(customers.getPartner()!=null) {
                                                Partner partner=partnerService.get(1,customers.getMvnoId());
                                                customers.setPartner(partner);
                                            }
                                        }
                                       oldCustomerAdressList.forEach(customerAddress1 -> customerAddress1.setVersion("OLD"));
                                        customerAddressRepository.saveAll(oldCustomerAdressList);
                                        if(shiftLocation.get().getLatitude() != null){
                                            customers.setLatitude(shiftLocation.get().getLatitude());
                                        }
                                        if(shiftLocation.get().getLongitude() != null){
                                            customers.setLongitude(shiftLocation.get().getLongitude());
                                        }
                                        customersRepository.save(customers);
                                        customerAddressList.get(i).setStatus(SubscriberConstants.ACTIVE);
                                        customerAddressList.get(i).setVersion(SubscriberConstants.SUBSCRIBER_ADDRESS_NEW_STATUS);
                                        customerAddressRepository.save(customerAddressList.get(i));
//                                        if(shiftLocation.get().getTransferableCommission()!=null && shiftLocation.get().getTransferableCommission()>0.0)
//                                            partnerCommissionService.transferCommissionFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableCommission(),customers);
//                                        if(shiftLocation.get().getTransferableBalance()!=null && shiftLocation.get().getTransferableBalance()>0.0)
//                                            partnerCommissionService.transferBalanceFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableBalance(),customers);
                                        if(shiftLocation.get().getChargeId()!=null)
                                            custChargeService.createCustomerChargeOverrideForShiftLocation(customers.getId(),shiftLocation.get().getChargeId(),shiftLocation.get().getAmount(),shiftLocation.get().getBillableCustomerId(),shiftLocation.get().getPaymentOwner(),shiftLocation.get().getPaymentOwnerId(), shiftLocation.get().getDiscount());
                                   //     dbrService.updateServiceAreaIdForCustomer(customers.getId(), serviceArea, LocalDate.now());
                                        flag=true;
                                        ShiftlocationMessage shiftlocationMessage=new ShiftlocationMessage();
                                        shiftlocationMessage.setAmount(shiftLocation.get().getAmount());
                                        shiftlocationMessage.setDiscount(shiftLocation.get().getDiscount());
                                        shiftlocationMessage.setChargeId(shiftLocation.get().getChargeId());
                                        shiftlocationMessage.setBillableCustomerId(shiftLocation.get().getBillableCustomerId());
                                        shiftlocationMessage.setOldpartnerId(oldPartnerId);
                                        shiftlocationMessage.setNewPartnerId(shiftLocation.get().getShiftPartnerId());
                                        shiftlocationMessage.setServiceAreaId(serviceArea.getId());
                                        shiftlocationMessage.setCustId(customers.getId());
                                        shiftlocationMessage.setTranferConnission(shiftLocation.get().getTransferableCommission());
                                        shiftlocationMessage.setTransferBalance(shiftLocation.get().getTransferableBalance());
                                        shiftlocationMessage.setPaymentowner(shiftLocation.get().getPaymentOwner());
//                                        messageSender.send(shiftlocationMessage, SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE);
                                        kafkaMessageSender.send(new KafkaMessageData(shiftlocationMessage,ShiftlocationMessage.class.getSimpleName()));
                                    }
                                } else if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("rejected")) {
                                    customerAddressList.get(i).setStatus(SubscriberConstants.REJECT);
                                    customerAddressList.get(i).setVersion(SubscriberConstants.REJECT);
                                    customerAddressRepository.save(customerAddressList.get(i));
                                }
                                save(convertCustomerAddressModelToCustomerAddressPojo(customerAddressList.get(i)));
                            }
                            workflowAuditService.saveAudit(null, CommonConstants.WORKFLOW_EVENT_NAME.SHIFT_LOCATION, customerAddressList.get(i).getId(), customerAddressList.get(i).getCustomer().getFullName(), getLoggedInUserId(), staffUser.getUsername(), pojo.getFlag().equalsIgnoreCase("approved") ? CommonConstants.WORKFLOW_AUDIT_ACTION.APPROVED : CommonConstants.WORKFLOW_AUDIT_ACTION.REJECTED, LocalDateTime.now(), "Remarks  : " + pojo.getRemark() + "\n" + pojo.getFlag() + " By :- " + staffUser.getUsername());
                        }
                    }
                }
                }
            } else {
                approvedByName.append("Administrator");
                if (customerAddressList.size() != 0 || customerAddressList != null) {
                    for (int i = 0; i < customerAddressList.size(); i++) {
                        if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                            oldCustomerAdressList.forEach(customerAddress1 -> customerAddress1.setVersion("OLD"));
                            customerAddressRepository.saveAll(oldCustomerAdressList);
                            customerAddressList.get(i).setStatus(SubscriberConstants.ACTIVE);
                            customerAddressList.get(i).setVersion(SubscriberConstants.SUBSCRIBER_ADDRESS_NEW_STATUS);
                        } else {
                            customerAddressList.get(i).setStatus(SubscriberConstants.REJECT);
                            customerAddressList.get(i).setVersion(SubscriberConstants.REJECT);

                        }
                        oldCustomerAdressList.forEach(customerAddress1 -> customerAddress1.setVersion("OLD"));
                        customerAddressRepository.saveAll(oldCustomerAdressList);
                        customerAddressList.get(i).setNextTeamHierarchyMappingId(null);
                        customerAddressList.get(i).setNextStaff(null);
                        customerAddressList.get(i).setStatus(SubscriberConstants.ACTIVE);
                        customerAddressList.get(i).setVersion(SubscriberConstants.SUBSCRIBER_ADDRESS_NEW_STATUS);
                        customerAddressRepository.save(customerAddressList.get(i));
                        save(convertCustomerAddressModelToCustomerAddressPojo(customerAddressList.get(i)));
                    }

                    if (pojo.getFlag() != null && !"".equals(pojo.getFlag()) && pojo.getFlag().equalsIgnoreCase("approved")) {
                        Optional<ShiftLocation> shiftLocation=shiftLocationRepository.findById(customerAddress.getShiftId());
                        ServiceArea serviceArea=serviceAreaService.getByID(shiftLocation.get().getUpdateAddressServiceAreaId());
                        customers.setServicearea(serviceArea);
                        if(shiftLocation.get().getShiftPartnerId()!=null && shiftLocation.get().getShiftPartnerId()!=1){
                            Partner partner=partnerService.get(shiftLocation.get().getShiftPartnerId(),customers.getMvnoId());
                            customers.setPartner(partner);
                            if(customers.getBranch()!=null)
                                customers.setBranch(null);
                        }
                        if(shiftLocation.get().getBranchId()!=null){
                            Branch branch =branchRepository.getOne(shiftLocation.get().getBranchId());
                            customers.setBranch(branch.getId());
                            if(customers.getPartner()!=null) {
                                Partner partner=partnerService.get(1,customers.getMvnoId());
                                customers.setPartner(partner);
                            }
                        }
                        if(shiftLocation.get().getLatitude() != null){
                            customers.setLatitude(shiftLocation.get().getLatitude());
                        }
                        if(shiftLocation.get().getLongitude() != null){
                            customers.setLongitude(shiftLocation.get().getLongitude());
                        }
                        customersRepository.save(customers);
//                        if(shiftLocation.get().getTransferableCommission()!=null && shiftLocation.get().getTransferableCommission()>0.0)
//                            partnerCommissionService.transferCommissionFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableCommission(),customers);
//                        if(shiftLocation.get().getTransferableBalance()!=null && shiftLocation.get().getTransferableBalance()>0.0)
//                            partnerCommissionService.transferBalanceFromOnePartnerToAnotherPartner(oldPartnerId,shiftLocation.get().getShiftPartnerId(),shiftLocation.get().getTransferableBalance(),customers);
                        if(shiftLocation.get().getChargeId()!=null)
                            custChargeService.createCustomerChargeOverrideForShiftLocation(customers.getId(),shiftLocation.get().getChargeId(),shiftLocation.get().getAmount(),shiftLocation.get().getBillableCustomerId(),shiftLocation.get().getPaymentOwner(),shiftLocation.get().getPaymentOwnerId(), shiftLocation.get().getDiscount());
                      //  dbrService.updateServiceAreaIdForCustomer(customers.getId(), serviceArea, LocalDate.now());
                        ShiftlocationMessage shiftlocationMessage=new ShiftlocationMessage();
                        shiftlocationMessage.setAmount(shiftLocation.get().getAmount());
                        shiftlocationMessage.setDiscount(shiftLocation.get().getDiscount());
                        shiftlocationMessage.setChargeId(shiftLocation.get().getChargeId());
                        shiftlocationMessage.setBillableCustomerId(shiftLocation.get().getBillableCustomerId());
                        shiftlocationMessage.setOldpartnerId(oldPartnerId);
                        shiftlocationMessage.setNewPartnerId(shiftLocation.get().getShiftPartnerId());
                        shiftlocationMessage.setServiceAreaId(serviceArea.getId());
                        shiftlocationMessage.setCustId(customers.getId());
                        shiftlocationMessage.setTranferConnission(shiftLocation.get().getTransferableCommission());
                        shiftlocationMessage.setTransferBalance(shiftLocation.get().getTransferableBalance());
                        shiftlocationMessage.setPaymentowner(shiftLocation.get().getPaymentOwner());
//                        messageSender.send(shiftlocationMessage, SharedDataConstants.QUEUE_PARTNER_SHIFT_LOCATION_SHARE_REVENUE);
                        kafkaMessageSender.send(new KafkaMessageData(shiftlocationMessage,ShiftlocationMessage.class.getSimpleName()));


                    }
                }
            }
            /*called method for close address shifting*/
            sendCustCloseAddressShiftingMessage(customers.getUsername(), customers.getMobile(), customers.getEmail(), customers.getStatus(), customers.getMvnoId(),customers.getBuId(),(long) getLoggedInStaffId(),customers.getCusttype(),customers.getId());


        }
        return genericDataDTO;
    }

    public List<CustomerAddress> findNewAddressByCustomer(Integer customerId) {
        QCustomerAddress qCustomerAddress = QCustomerAddress.customerAddress;
        BooleanExpression booleanExpression = qCustomerAddress.isNotNull().and(qCustomerAddress.customer.id.eq(customerId).and(qCustomerAddress.addressType.eq(("Present"))));
        List<CustomerAddress> all = (List<CustomerAddress>) entityRepository.findAll(booleanExpression , qCustomerAddress.id.desc());
        return all;
    }

    public CustomerAddress findAddressByCustomerId(Integer customerId) {
        QCustomerAddress qCustomerAddress = QCustomerAddress.customerAddress;
        BooleanExpression booleanExpression = qCustomerAddress.isNotNull().and(qCustomerAddress.customer.id.eq(customerId));
        return entityRepository.findOne(booleanExpression).orElse(null);
    }

    public GenericDataDTO getShiftLocationApprovals(List<GenericSearchModel> filters, Integer page, Integer pageSize, String sortBy, Integer sortOrder) {
        PageRequest pageRequest = generatePageRequest(page, pageSize, "createdate", CommonConstants.SORT_ORDER_DESC);
        QCustomerAddress qCustomerAddress = QCustomerAddress.customerAddress;
        GenericDataDTO genericDataDTO = new GenericDataDTO();

        BooleanExpression booleanExpression = qCustomerAddress.isNotNull().and(qCustomerAddress.isDelete.eq(false)).and(qCustomerAddress.status.eq(SubscriberConstants.NEW_ACTIVATION)).and(qCustomerAddress.nextStaff.eq(getLoggedInUserId()));
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(null) != 1)
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCustomerAddress.customer.mvnoId.in(1, getMvnoIdFromCurrentStaff(null)));
        if (getBUIdsFromCurrentStaff() != null && getBUIdsFromCurrentStaff().size() > 0) {
            // TODO: pass mvnoID manually 6/5/2025
            booleanExpression = booleanExpression.and(qCustomerAddress.customer.mvnoId.eq(1).or(qCustomerAddress.customer.mvnoId.eq(getMvnoIdFromCurrentStaff(null)).and(qCustomerAddress.customer.buId.in(getBUIdsFromCurrentStaff()))));
        }

        Page<CustomerAddress> paginationList = entityRepository.findAll(booleanExpression, pageRequest);
        genericDataDTO.setDataList(paginationList.getContent());
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage(HttpStatus.OK.getReasonPhrase());
        genericDataDTO.setTotalRecords(paginationList.getTotalElements());
        genericDataDTO.setPageRecords(paginationList.getNumberOfElements());
        genericDataDTO.setCurrentPageNumber(paginationList.getNumber() + 1);
        genericDataDTO.setTotalPages(paginationList.getTotalPages());
        return genericDataDTO;
    }


   /* method for send notification open address shifting*/
   public void sendCustOpenAddressShiftingMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Long buId,Long staffId,String custtype,Integer custId) {
       try {
           Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_OPEN_ADDRESS_SHIFTING_TEMPLATE);
           if (optionalTemplate.isPresent()) {
               if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                   CustAddressShiftingMsg custAddressShiftingMsg = new CustAddressShiftingMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_OPEN_ADDRESS_SHIFTING_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId,custtype,custId);
                   Gson gson = new Gson();
                   gson.toJson(custAddressShiftingMsg);
//                   messageSender.send(custAddressShiftingMsg, RabbitMqConstants.QUEUE_CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION);
                   kafkaMessageSender.send(new KafkaMessageData(custAddressShiftingMsg,CustAddressShiftingMsg.class.getSimpleName(), KafkaConstant.CUSTOMER_OPEN_ADDRESS_SHIFTING_NOTIFICATION));
               }
           } else {
               System.out.println("Message of Customer Open Address Shifting is not sent because template is not present.");
           }
       } catch (Throwable e) {
           throw new RuntimeException(e.getMessage());
       }
   }



    /* method for send notification close address shifting*/
    public void sendCustCloseAddressShiftingMessage(String username, String mobileNumber, String emailId, String status, Integer mvnoId,Long buId,Long staffId,String custtype,Integer custId) {
        try {
            Optional<TemplateNotification> optionalTemplate = templateRepository.findByTemplateName(RabbitMqConstants.CUSTOMER_CLOSE_ADDRESS_SHIFTING_TEMPLATE);
            if (optionalTemplate.isPresent()) {
                if (optionalTemplate.get().isSmsEventConfigured() || optionalTemplate.get().isEmailEventConfigured()) {
                    CustAddressShiftingMsg custAddressShiftingMsg = new CustAddressShiftingMsg(username, mobileNumber, emailId, status, mvnoId, RabbitMqConstants.CUSTOMER_CLOSE_ADDRESS_SHIFTING_EVENT, optionalTemplate.get(), RabbitMqConstants.SOURCE_NAME_ADOPT_BSS_GATEWAY,buId,staffId,custtype,custId);
                    Gson gson = new Gson();
                    gson.toJson(custAddressShiftingMsg);
//                    messageSender.send(custAddressShiftingMsg, RabbitMqConstants.QUEUE_CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION);
                    kafkaMessageSender.send(new KafkaMessageData(custAddressShiftingMsg,CustAddressShiftingMsg.class.getSimpleName(),KafkaConstant.CUSTOMER_CLOSE_ADDRESS_SHIFTING_NOTIFICATION));
                }
            } else {
                System.out.println("Message of Customer Close Address Shifting is not sent because template is not present.");
            }
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }



    public GenericDataDTO getListOfUsedBuildingNumber(Integer buildingMgmgId){
        try{
            GenericDataDTO dataDTO = new GenericDataDTO();
            List<String> objectList = customerAddressRepository.findUsedBuildingNumbers(buildingMgmgId);

            dataDTO.setDataList(objectList);
            return dataDTO;
        }catch (Exception e){
            e.printStackTrace();
        }
        return  null;
    }
    public static List<String> extractOldAddressValues(CustomerAddress address) {
        List<String> values = new ArrayList<>();
        if (address == null) return values;
        values.add(address.getLandmark());
        values.add(address.getArea() != null ? address.getArea().getName() : null);
        values.add(address.getState() != null ? address.getState().getName() : null);
        values.add(address.getPincode() != null ? address.getPincode().getPincode() : null);
        values.add(address.getCity() != null ? address.getCity().getName() : null);
        values.add(address.getCountry() != null ? address.getCountry().getName() : null);
        values.add(address.getBuildingNumber());
        values.add(address.getSubarea() != null ? address.getSubarea().getName() : null);
        values.add(address.getRequestedByName());
        values.add(address.getAddressType());
        values.add(address.getBuildingManagement() != null ? address.getBuildingManagement().getBuildingName() : null);
        return values;
    }
    public List<String> extractNewAddressValues(CustomerAddressPojo address) {
        List<String> values = new ArrayList<>();
        if (address == null) return values;
        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        String areaName = (address.getAreaId() != null) ? queryFactory.select(QArea.area.name).from(QArea.area).where(QArea.area.id.eq(address.getAreaId().longValue())).fetchOne() : null;
        String stateName = (address.getStateId() != null) ? queryFactory.select(QState.state.name).from(QState.state).where(QState.state.id.eq(address.getStateId())).fetchOne() : null;
        String cityName = (address.getCityId() != null) ? queryFactory.select(QCity.city.name).from(QCity.city).where(QCity.city.id.eq(address.getCityId())).fetchOne() : null;
        String countryName = (address.getCountryId() != null) ? queryFactory.select(QCountry.country.name).from(QCountry.country).where(QCountry.country.id.eq(address.getCountryId())).fetchOne() : null;
        String pincode = (address.getPincodeId() != null) ? queryFactory.select(QPincode.pincode1.pincode).from(QPincode.pincode1).where(QPincode.pincode1.id.eq(address.getPincodeId().longValue())).fetchOne() : null;
        String subareaName = (address.getSubareaId() != null) ? queryFactory.select(QSubArea.subArea.name).from(QSubArea.subArea).where(QSubArea.subArea.id.eq(address.getSubareaId())).fetchOne() : null;
        String buildingName = (address.getBuilding_mgmt_id() != null) ? queryFactory.select(QBuildingManagement.buildingManagement.buildingName).from(QBuildingManagement.buildingManagement).where(QBuildingManagement.buildingManagement.buildingMgmtId.eq(address.getBuilding_mgmt_id())).fetchOne() : null;
        // Add values (nulls are allowed)
        values.add(address.getLandmark());           // 1
        values.add(areaName);                        // 2
        values.add(stateName);                       // 3
        values.add(pincode);                         // 4
        values.add(cityName);                        // 5
        values.add(countryName);                     // 6
        values.add(address.getBuildingNumber());     // 7
        values.add(subareaName);                     // 8
        values.add(address.getRequestedByName());    // 9
        values.add(address.getAddressType());        // 10
        values.add(buildingName);                    // 11
        return values;
    }
}
