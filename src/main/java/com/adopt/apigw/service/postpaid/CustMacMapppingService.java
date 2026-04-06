package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.mapper.postpaid.CustMacMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.model.common.QCustomers;
import com.adopt.apigw.model.postpaid.CustMacMappping;
import com.adopt.apigw.model.postpaid.CustMacMapppingPojo;
import com.adopt.apigw.model.postpaid.QCustMacMappping;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.RabbitMqConstants;
import com.adopt.apigw.rabbitMq.message.*;
import com.adopt.apigw.repository.postpaid.CustMacMapppingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.itextpdf.text.Document;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.swing.text.html.Option;
import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustMacMapppingService extends AbstractService<CustMacMappping, CustMacMapppingPojo, Integer> {


    @Autowired
    private CustMacMapppingRepository custMacMapppingRepository;
    @Autowired
    private CustMacMapper custMacMapper;
    @Autowired
    private MessageSender messageSender;

    @Autowired
    private CustomersRepository custRepository;

    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private static final Logger log = LoggerFactory.getLogger(CustMacMapppingService.class);
    @Override
    protected JpaRepository<CustMacMappping, Integer> getRepository() {
        return custMacMapppingRepository;
    }

    public void saveAll(List<CustMacMappping> custMacMapppingList) {
        custMacMapppingRepository.saveAll(custMacMapppingList);
    }

    public List<CustMacMappping> findMacAddressByCustomerId(Integer custId) {
        return custMacMapppingRepository.findByCustomerIdAndIsDeletedIsFalse(custId);
    }

    public void deleteByCustomerId(Integer custId) {
        List<CustMacMappping> custMacMapppingList = custMacMapppingRepository.findByCustomerId(custId);
        for (CustMacMappping custMacMappping : custMacMapppingList) {
            custMacMappping.setIsDeleted(true);
            custMacMapppingRepository.save(custMacMappping);
        }
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("Mac");
        List<CustMacMapppingPojo> custMacMapppingPojoList = custMacMapppingRepository.findAll().stream()
                .map(data -> custMacMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        Field[] printFields = {
                CustMacMapppingPojo.class.getDeclaredField("id"),
                CustMacMapppingPojo.class.getDeclaredField("custid"),
                CustMacMapppingPojo.class.getDeclaredField("macAddress"),
                CustMacMapppingPojo.class.getDeclaredField("isDeleted")
        };
        createExcel(workbook, sheet, CustMacMapppingPojo.class, custMacMapppingPojoList, printFields);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        List<CustMacMapppingPojo> custMacMapppingPojoList = custMacMapppingRepository.findAll().stream()
                .map(data -> custMacMapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        Field[] printFields = {
                CustMacMapppingPojo.class.getDeclaredField("id"),
                CustMacMapppingPojo.class.getDeclaredField("custid"),
                CustMacMapppingPojo.class.getDeclaredField("macAddress"),
                CustMacMapppingPojo.class.getDeclaredField("isDeleted")
        };
        createPDF(doc, CustMacMapppingPojo.class, custMacMapppingPojoList, null);
    }

    @Override
    public CustMacMappping save(CustMacMappping entity) {
        CustMacMappping custMacMappping = super.save(entity);
        Customers customer = custRepository.findByIdAndIsDeletedIsFalse(entity.getCustomer().getId());
        CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping, customer.getMvnoId(), customer.getUsername());
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));

        return custMacMappping;
    }

    @Override
    public CustMacMappping update(CustMacMappping entity) {
        CustMacMappping custMacMappping = super.update(entity);
        Customers customer = custRepository.findByIdAndIsDeletedIsFalse(entity.getCustomer().getId());
        CustMacMappingMessage message = new CustMacMappingMessage(custMacMappping, customer.getMvnoId(), customer.getUsername());
        //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
        kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));

        return custMacMappping;
    }

    @Transactional
    public void deleteByMacAddress(String macAddress, Integer customerId) {
        QCustMacMappping qCustMacMappping = QCustMacMappping.custMacMappping;
        Customers customer = custRepository.findByIdAndIsDeletedIsFalse(customerId);
        BooleanExpression booleanExpression = qCustMacMappping.isNotNull();
        booleanExpression = booleanExpression.and(qCustMacMappping.macAddress.eq(macAddress)).and(qCustMacMappping.customer.id.eq(customerId));
        List<CustMacMappping> customerMacMapping = (List<CustMacMappping>) custMacMapppingRepository.findAll(booleanExpression);
//        List<String> macAddresses = customerMacMapping.stream().map(entry -> entry.getMacAddress()).collect(Collectors.toList());
        customerMacMapping.forEach(s -> {
            s.setIsDeleted(true);
            CustMacMappingMessage message = new CustMacMappingMessage(s, customer.getMvnoId(), customer.getUsername());
            //messageSender.send(message, RabbitMqConstants.QUEUE_APIGW_CUSTOMER_MAC_MAPPING);
            kafkaMessageSender.send(new KafkaMessageData(message,message.getClass().getSimpleName()));
            custMacMapppingRepository.save(s);
        });
    }

    @Transactional
    public void deleteMacByMacId(MacAddressMappingMessage message){
        if (!CollectionUtils.isEmpty(message.getMacAddress())) {
            List<HashMap<String, Object>> macs = message.getMacAddress();
            macs.forEach(m -> {
                deleteMacAddressByUserNameAndMac(m.get("userName").toString(), m.get("macAddress").toString(), Long.valueOf(m.get("mvnoId").toString()));
            });

        }
    }

    @Transactional
    public List<CustMacMappping> deleteMacAddressByUserNameAndMac(String userName, String macs, Long mvnoId) {
        try {
            Customers customer = validateMacAddressMappingByCustomerUserName(userName, mvnoId);
            List<CustMacMappping> macList = custMacMapppingRepository.findByCustomerId(customer.getId());
            if(CollectionUtils.isEmpty(macList)) {
                throw new IllegalArgumentException("Mac address not available for given customer, username: "+userName);
            }
            List<String> oldMacs = macList.stream().map(CustMacMappping::getMacAddress)
                    .filter(m -> macs.equals(m)).distinct().collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(oldMacs))
                custMacMapppingRepository.deleteByCustomerIdAndMacAddressIn(customer.getId(), oldMacs);
            return custMacMapppingRepository.findByCustomerId(customer.getId());
        }catch (Exception ex) {
            //skipp
        }
        return null;
    }

    private Customers validateMacAddressMappingByCustomerUserName(String userName, Long mvnoId)
    {
        try {
            if(userName == null || userName.length() == 0) {
                throw new IllegalArgumentException("Please enter valid Customer username.");
            }
            QCustomers qCustomer = QCustomers.customers;
            BooleanExpression boolExp = qCustomer.isNotNull();
            boolExp = boolExp.and(qCustomer.username.eq(userName));
            if(mvnoId != 1) {
                boolExp = boolExp.and(qCustomer.mvnoId.eq(Math.toIntExact(mvnoId)));
            }
            Optional<Customers> customerOptional = custRepository.findOne(boolExp);
            return customerOptional.get();
        }
        catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Customers updateCustomerConcurrency(CustomerUpdateMessage updateCustomerDto) {
        Map<String, Object> map = updateCustomerDto.getCustomerData();
        if(map.containsKey("id") && map.containsKey("maxconcurrentsession")) {
            Optional<Customers> customers = custRepository.findById(Integer.valueOf(map.get("id").toString()));
            customers.get().setMaxconcurrentsession(Integer.valueOf(map.get("maxconcurrentsession").toString()));
            return custRepository.save(customers.get());
        } else {
            System.out.println("Id or maxconcurrentsession can not be null");
        }
        return null;
    }

    public void deleteMacFromRadius(CustMacMessage custMacMessage) {
        List<MacAddressMapping> macAddressMappings = custMacMessage.getMacAddressMappings();
        if(!CollectionUtils.isEmpty(macAddressMappings)) {
            log.debug("In Delete Mac from Radius Scheduler count: "+macAddressMappings.size());
            for(MacAddressMapping macAddressMapping: macAddressMappings) {
                try {
                    Long custId = macAddressMapping.getCustomerId();
                    String mac = macAddressMapping.getMacAddress();
                    custMacMapppingRepository.deleteByCustomerIdAndMac(custId.intValue(),mac);
                } catch (Exception ex) {
                    log.error("Error while delte mac from Radius: "+ex.getMessage());
                }
            }

        }
    }
}
