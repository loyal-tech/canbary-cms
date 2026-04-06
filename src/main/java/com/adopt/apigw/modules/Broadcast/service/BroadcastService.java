package com.adopt.apigw.modules.Broadcast.service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.postpaid.CustPlanMappping;
import com.adopt.apigw.model.postpaid.CustomerMapper;
import com.adopt.apigw.modules.Alert.emailSchedular.service.SchedulerService;
import com.adopt.apigw.modules.Alert.smsScheduler.service.SmsSchedulerService;
import com.adopt.apigw.modules.Broadcast.domain.Broadcast;
import com.adopt.apigw.modules.Broadcast.domain.BroadcastPorts;
import com.adopt.apigw.modules.Broadcast.mapper.BroadcastMapper;
import com.adopt.apigw.modules.Broadcast.model.BroadcastDTO;
import com.adopt.apigw.modules.Broadcast.model.BroadcastPortsDTO;
import com.adopt.apigw.modules.Broadcast.repository.BroadcastPortRepository;
import com.adopt.apigw.modules.Broadcast.repository.BroadcastRepository;
import com.adopt.apigw.modules.Communication.Constants.CommunicationConstant;
import com.adopt.apigw.modules.Communication.Helper.CommunicationHelper;
import com.adopt.apigw.modules.Notification.model.NotificationDTO;
import com.adopt.apigw.modules.Notification.service.NotificationService;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.pojo.api.CustomersPojo;
import com.adopt.apigw.repository.postpaid.CustPlanMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BroadcastService extends ExBaseAbstractService<BroadcastDTO, Broadcast, Long> {
    @Autowired
    private BroadcastRepository broadcastRepository;
    @Autowired
    private BroadcastPortRepository broadcastPortsRepository;
    @Autowired
    private BroadcastMapper mapper;
    @Autowired
    private CustomersRepository customersRepository;
    @Autowired
    private CustPlanMappingRepository custPlanMappingRepository;
    @Autowired
    private SchedulerService schedulerService;
    @Autowired
    private SmsSchedulerService smsSchedulerService;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private NotificationService notificationService;

    public BroadcastService(BroadcastRepository repository, BroadcastMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public BroadcastDTO saveEntity(BroadcastDTO entity) throws Exception {
        Broadcast broadcast = this.broadcastRepository.save(mapper.dtoToDomain(entity,
                new CycleAvoidingMappingContext()));
        for (BroadcastPortsDTO broadcastPorts1 : entity.getBroadcastPortsList()) {
            BroadcastPorts tempBroadcastPorts = new BroadcastPorts();
            tempBroadcastPorts.setBroadcast(broadcast);
            tempBroadcastPorts.setPortid(broadcastPorts1.getPortid());
            this.broadcastPortsRepository.save(tempBroadcastPorts);
        }
        this.validateRequest(entity);
        return entity;
    }

    public void validateRequest(BroadcastDTO entity) throws Exception {
        String type = entity.getType();
        Integer plan_id = entity.getPlanid();
        Integer servicearea_id = entity.getServiceareaid();
        Integer networkdevice_id = entity.getNetworkdeviceid();
        Integer slot_id = entity.getSlotid();
        Integer customer_id = entity.getCustomer_id();
        Long notification_id = entity.getTemplateid();
        List<BroadcastPortsDTO> portsList = entity.getBroadcastPortsList();
        LocalDate expirydate1 = entity.getExpirydate1();
        LocalDate expirydate2 = entity.getExpirydate2();
        String expirycondition = entity.getExpiry_condition();
        Integer exprywithin = entity.getExpirywithin();
//        NotificationDTO notificationDTO = notificationService.getEntityById(notification_id);
        List<Customers> customersList = null;

        if (entity.getCustcondition().equalsIgnoreCase("location")) {
            if (networkdevice_id == -1) {
                customersList = customersRepository.getAllCustomerByNetworkDevice(servicearea_id);
            } else if (slot_id == -1) {
                customersList = customersRepository.getAllCustomerBySlot(servicearea_id, networkdevice_id);
            } else {
                List<Integer> integerList = new ArrayList<>();
                if (portsList != null && portsList.size() > 0) {
                    for (BroadcastPortsDTO tempPort : portsList) {
                        integerList.add(tempPort.getPortid());
                    }
                }
                customersList = customersRepository.getAllCustomerForLocation(servicearea_id, networkdevice_id, slot_id, integerList);
            }
        }
        if (entity.getCustcondition().equalsIgnoreCase("plan")) {
            customersList = new ArrayList<>();
            List<CustPlanMappping> custPlanMapppingList = custPlanMappingRepository.findByPlanId(plan_id).stream().filter(data -> data.getCustomer().getStatus().equalsIgnoreCase("active")).collect(Collectors.toList());
            for (CustPlanMappping tempCustomerMapping : custPlanMapppingList) {
                customersList.add(tempCustomerMapping.getCustomer());
            }
        }
        if (entity.getCustcondition().equalsIgnoreCase("customer")) {
            List<Integer> integerList = new ArrayList<>();
            integerList.add(customer_id);
            customersList = this.customersRepository.getAllCustomersById(integerList);
        }
        if (entity.getCustcondition().equalsIgnoreCase("expiry")) {
            if (exprywithin != null && exprywithin != 0) {
                LocalDate currentDate = LocalDate.now();
                LocalDate pastDate = currentDate.minusDays(exprywithin);
                customersList = this.customersRepository.getAllCustomerByExpiryWithIn(pastDate, currentDate);
            } else if (expirycondition.equalsIgnoreCase("lessthan")) {
                customersList = this.customersRepository.getAllCustomerByExpiryLessthan(expirydate1);
            } else if (expirycondition.equalsIgnoreCase("greaterthan")) {
                customersList = this.customersRepository.getAllCustomerByExpiryGreaterthan(expirydate1);
            } else if (expirycondition.equalsIgnoreCase("equal")) {
                customersList = this.customersRepository.getAllCustomerByExpiryEqual(expirydate1);
            } else if (expirycondition.equalsIgnoreCase("between")) {
                customersList = this.customersRepository.getAllCustomerByExpiryWithIn(expirydate1, expirydate2);
            }
        }
        List<Map<String,String>> mapList = this.mapBuilder(customersList);
        CommunicationHelper communicationHelper = new CommunicationHelper();
        communicationHelper.generateCommunicationDetails(notification_id,mapList);
    }


    public List<Map<String,String>> mapBuilder(List<Customers> customers){
        List<Map<String,String>> mapList = new ArrayList<>();
        customers.forEach(data->{
            Map<String,String> sms = new HashMap<>();
            sms.put(CommunicationConstant.DESTINATION,data.getMobile());
            sms.put(CommunicationConstant.USERNAME,data.getFirstname());
            sms.put(CommunicationConstant.PASSWORD,"123456");
            sms.put(CommunicationConstant.EMAIL,data.getEmail());
            mapList.add(sms);
        });
        return mapList;
    }

    @Override
    public String getModuleNameForLog() {
        return "[broadcast Service]";
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("BroadCase");
        createExcel(workbook, sheet, BroadcastDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, BroadcastDTO.class, null,mvnoId);
    }
}
