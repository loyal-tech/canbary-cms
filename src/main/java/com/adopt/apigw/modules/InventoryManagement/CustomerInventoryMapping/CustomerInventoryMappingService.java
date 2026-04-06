package com.adopt.apigw.modules.InventoryManagement.CustomerInventoryMapping;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.kafka.KafkaMessageData;
import com.adopt.apigw.kafka.KafkaMessageSender;
import com.adopt.apigw.model.common.ClientService;
import com.adopt.apigw.model.common.CustomerServiceMapping;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.InventoryManagement.item.Item;
import com.adopt.apigw.modules.InventoryManagement.item.ItemRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import com.adopt.apigw.rabbitMq.message.CustomerInventoryMappingMessage;
import com.adopt.apigw.rabbitMq.message.InventorySerialNumberMessage;
import com.adopt.apigw.repository.common.ClientServiceRepository;
import com.adopt.apigw.repository.radius.CustomerServiceMappingRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.utils.CommonConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

/// /TODO: Remove ExBaseAbstractService and add AbstractService
@Service
public class CustomerInventoryMappingService extends ExBaseAbstractService<CustomerInventoryMappingDto, CustomerInventoryMapping, Long> {
    @Override
    public String getModuleNameForLog() {
        return "[CustomerInventoryMappingService]";
    }

    public CustomerInventoryMappingService(CustomerInventoryMappingRepo repository, CustomerInventoryMappingMapper mapper) {
        super(repository, mapper);
    }

    @Autowired
    private CustomerInventoryMappingRepo customerInventoryMappingRepo;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CustomersRepository customersRepository;

    @Autowired
    CustomersService customersService;

    @Autowired
    CustomerServiceMappingRepository customerServiceMappingRepository;

    @Autowired
    ClientServiceRepository clientServiceRepository;
    @Autowired
    MessageSender messageSender;
    @Autowired
    private KafkaMessageSender kafkaMessageSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerInventoryMappingService.class);

    public List<CustomerInventorySerialnumberDto> getActiveSerialnumberByConnectionNo(String connectionNo, Integer customerId) {
        List<Object[]> customerInventoryMappings = new ArrayList<>();
        // TODO: pass mvnoID manually 6/5/2025
        if (getMvnoIdFromCurrentStaff(customerId) != null) {
            // TODO: pass mvnoID manually 6/5/2025
            if (getMvnoIdFromCurrentStaff(customerId) == 1) {
                customerInventoryMappings = customerInventoryMappingRepo.findByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatus(connectionNo, customerId, CommonConstants.ACTIVE_STATUS);
            } else {
                customerInventoryMappings = customerInventoryMappingRepo.findAllByConnectionNoAndIsDeletedIsFalseAndCustomerIdAndStatusAndMvnoIdIn(connectionNo, customerId, CommonConstants.ACTIVE_STATUS, Arrays.asList(getMvnoIdFromCurrentStaff(customerId), 1));
            }
        }
        List<CustomerInventorySerialnumberDto> customerInventorySerialnumberDtoList = new ArrayList<>();
        customerInventoryMappings.stream().forEach(customerInventoryMapping -> {
                    CustomerInventorySerialnumberDto customerInventorySerialnumberDto = new CustomerInventorySerialnumberDto();
                    customerInventorySerialnumberDto.setCustomerId(((BigInteger) customerInventoryMapping[1]).intValue());
                    customerInventorySerialnumberDto.setConnectionNo((String) customerInventoryMapping[2]);
//                    Optional<Item> item = itemRepository.findById((Long) customerInventoryMapping[3]);
                    List<Object[]> item = Optional.ofNullable(customerInventoryMapping[3])
                            .map(obj -> (BigInteger) obj)
                            .map(BigInteger::longValue)
                            .map(id -> itemRepository.findItemIdById(id))
                            .orElse(Collections.emptyList());
                    Object[] itemData = item.get(0);
                    customerInventorySerialnumberDto.setSerialNumber((String) itemData[1]);
//                    customerInventorySerialnumberDto.setProductName(customerInventoryMapping.getProduct().getName());
                    customerInventorySerialnumberDto.setItemId(((BigInteger) itemData[0]).longValue());
                    customerInventorySerialnumberDto.setCustInventoryMappingId(((BigInteger) customerInventoryMapping[0]).longValue());
//                    if (customerInventoryMapping.getProduct().getProductCategory().isHasCas()) {
//                        customerInventorySerialnumberDto.setDtvCategory(customerInventoryMapping.getProduct().getProductCategory().getDtvCategory());
//                    }
                    customerInventorySerialnumberDtoList.add(customerInventorySerialnumberDto);
                }
        );
        List<CustomerInventorySerialnumberDto> finalCustomerInventorySerialNumberDto = customerInventorySerialnumberDtoList.stream().sorted(Comparator.comparing(CustomerInventorySerialnumberDto::getCustInventoryMappingId).reversed()).collect(Collectors.toList());
        if (!finalCustomerInventorySerialNumberDto.isEmpty())
            finalCustomerInventorySerialNumberDto.get(0).setPrimary(true);
        return finalCustomerInventorySerialNumberDto;
    }

    public void saveInventoryData(InventorySerialNumberMessage message) {
        LOGGER.info("Starting Save Assign Inventory Data from Kafka with Customer Id: " + message.getCustId() + " , Mac Address: " + message.getMacAddress() + " ,and Serial Number: " + message.getSerialNumber());
        try {
            if (message.getOperation().equalsIgnoreCase(CommonConstants.ASSIGN_INVETORIES)) {
                assignInventory(message);
            } else if (message.getOperation().equalsIgnoreCase(CommonConstants.REPLACE_INVETORIES)) {
                replaceInventory(message);
            } else if (message.getOperation().equalsIgnoreCase(CommonConstants.REMOVE_INVETORIES)) {
                removeInventory(message);
            }
            LOGGER.info("Ending Save Assign Inventory Data from Kafka with Customer Id: " + message.getCustId() + " , Mac Address: " + message.getMacAddress() + " ,and Serial Number: " + message.getSerialNumber());
        } catch (Exception e) {
            LOGGER.error("Unable to Save Inventory Data with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private void removeInventory(InventorySerialNumberMessage message) {
        try {
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNoAndCustId(message.getConnectionNo(), message.getCustId());
            Optional<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findById(message.getCustInventoryId());
            if (customerInventoryMapping.isPresent()) {
                updateCustInveMappingAtRemove(message, customerInventoryMapping.get(), customerServiceMapping);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Remove Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private void updateCustInveMappingAtRemove(InventorySerialNumberMessage message, CustomerInventoryMapping customerInventoryMapping, CustomerServiceMapping customerServiceMapping) {
        try {
            customerInventoryMapping.setId(message.getCustInventoryId());
            Customers customers = customersRepository.findById(message.getCustId()).orElse(null);
            if (customers != null) {
                customerInventoryMapping.setCustomer(customers);
            }
            customerInventoryMapping.setIsDeleted(true);
            customerInventoryMapping.setPlanId(message.getPlanId());
            customerInventoryMapping.setPlanGroupId(message.getPlanGroupId());
            customerInventoryMapping.setItemId(message.getItemId());
            customerInventoryMapping.setStatus(message.getStatus());
            customerInventoryMapping.setMvnoId(message.getMvnoId());
            customerInventoryMapping.setConnectionNo(message.getConnectionNo());
            customerInventoryMapping.setQty(message.getQty());
            customerInventoryMapping.setVendorId(message.getVendorId());
            if (customerServiceMapping.getUuid() != null) {
                customersService.sendDeleteRequestForNMS(customerServiceMapping.getCustId(), customerServiceMapping.getId());
            }
            CustomerInventoryMapping inventoryMapping = customerInventoryMappingRepo.save(customerInventoryMapping);
            ApplicationLogger.logger.info("Customer inventory mapping save at remove inventory with item id " + message.getItemId());
        } catch (Exception e) {
            LOGGER.error("Unable to Update Customer Inventory Mapping at Remove Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private void replaceInventory(InventorySerialNumberMessage message) {
        try {
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNoAndCustId(message.getConnectionNo(), message.getCustId());
            saveSerializedItem(message);
            Optional<CustomerInventoryMapping> customerInventoryMapping = customerInventoryMappingRepo.findById(message.getCustInventoryId());
            if (customerInventoryMapping.isPresent()) {
                updateCustInventoryMappingAtReplace(customerInventoryMapping.get(), message, customerServiceMapping);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Replace Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private void updateCustInventoryMappingAtReplace(CustomerInventoryMapping customerInventoryMapping, InventorySerialNumberMessage message, CustomerServiceMapping customerServiceMapping) {
        try {
            customerInventoryMapping.setId(message.getCustInventoryId());
            Customers customers = customersRepository.findById(message.getCustId()).orElse(null);
            if (customers != null) {
                customerInventoryMapping.setCustomer(customers);
            }
            customerInventoryMapping.setPlanId(message.getPlanId());
            customerInventoryMapping.setPlanGroupId(message.getPlanGroupId());
            customerInventoryMapping.setItemId(message.getItemId());
            customerInventoryMapping.setConnectionNo(message.getConnectionNo());
            customerInventoryMapping.setStatus(message.getStatus());
            customerInventoryMapping.setMvnoId(message.getMvnoId());
            customerInventoryMapping.setQty(message.getQty());
            customerInventoryMapping.setVendorId(message.getVendorId());
            CustomerInventoryMapping inventoryMapping = customerInventoryMappingRepo.save(customerInventoryMapping);
            if (customerServiceMapping.getUuid() != null) {
                customersService.sendDeleteRequestForNMS(customerServiceMapping.getCustId(), customerServiceMapping.getId());
            }
            verifyAndInitiateHsnCreateRequst(customers.getId(), customers.getUsername(), message.getLoggedInUserName(), message.getMvnoId(), inventoryMapping.getConnectionNo());
            ApplicationLogger.logger.info("Customer inventory mapping save at replace inventory with item id " + message.getItemId());
        } catch (Exception e) {
            LOGGER.error("Unable to Update Customer Inventory Mapping At Replace with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private void assignInventory(InventorySerialNumberMessage message) {
        try {
            /** Get Customer Service Mapping */
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNoAndCustId(message.getConnectionNo(), message.getCustId());
            saveSerializedItem(message);
            Optional<CustomerInventoryMapping> existingCustomerInventoryMapping = customerInventoryMappingRepo.findById(message.getCustInventoryId());
            List<Long> custinventorymappingId = new ArrayList<>();
            if (existingCustomerInventoryMapping.isPresent()) {
                custinventorymappingId.addAll(updateExistCustInventoryMapping(existingCustomerInventoryMapping, message, custinventorymappingId));
            } else {
                custinventorymappingId.addAll(saveNewCustInvenMapping(message, custinventorymappingId, customerServiceMapping));
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Assign Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    private List<Long> saveNewCustInvenMapping(InventorySerialNumberMessage message, List<Long> custinventorymappingId, CustomerServiceMapping customerServiceMapping) {
        try {
            CustomerInventoryMapping customerInventoryMapping = new CustomerInventoryMapping();
            customerInventoryMapping.setId(message.getCustInventoryId());
            Customers customers = customersRepository.findById(message.getCustId()).orElse(null);
            if (customers != null) {
                customerInventoryMapping.setCustomer(customers);
            }
            customerInventoryMapping.setPlanId(message.getPlanId());
            customerInventoryMapping.setPlanGroupId(message.getPlanGroupId());
            customerInventoryMapping.setItemId(message.getItemId());
            customerInventoryMapping.setConnectionNo(message.getConnectionNo());
            customerInventoryMapping.setStatus(message.getStatus());
            customerInventoryMapping.setMvnoId(message.getMvnoId());
            customerInventoryMapping.setQty(message.getQty());
            customerInventoryMapping.setVendorId(message.getVendorId());
            CustomerInventoryMapping SavedCustomerInventoryMapping = customerInventoryMappingRepo.save(customerInventoryMapping);
            custinventorymappingId.add(SavedCustomerInventoryMapping.getId());
            verifyAndInitiateHsnCreateRequst(customers.getId(), customers.getUsername(), message.getLoggedInUserName(), message.getMvnoId(), SavedCustomerInventoryMapping.getConnectionNo());
            if (customerServiceMapping.getUuid() != null) {
                CustomerInventoryMappingMessage inventoryMappingMessage = new CustomerInventoryMappingMessage(SubscriberConstants.ACTIVATION_PENDING, custinventorymappingId);
//                    messageSender.send(inventoryMappingMessage, RabbitMqConstants.QUEUE_SEND_CMS_UPDATE_STATUS_INVENTORY);
                kafkaMessageSender.send(new KafkaMessageData(inventoryMappingMessage, CustomerInventoryMappingMessage.class.getSimpleName()));
            }
            ApplicationLogger.logger.info("Customer inventory mapping save at assign inventory with item id " + message.getItemId());
        } catch (Exception e) {
            LOGGER.error("Unable to Save Customer Inventory Mapping at Assign Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
        return custinventorymappingId;
    }

    private List<Long> updateExistCustInventoryMapping(Optional<CustomerInventoryMapping> existingCustomerInventoryMapping, InventorySerialNumberMessage message, List<Long> custinventorymappingId) {
        try {
            existingCustomerInventoryMapping.get().setStatus(message.getStatus());
            existingCustomerInventoryMapping.get().setQty(message.getQty());
            CustomerInventoryMapping SavedCustomerInventoryMapping = customerInventoryMappingRepo.save(existingCustomerInventoryMapping.get());
            //add nms service request
            verifyAndInitiateHsnCreateRequst(existingCustomerInventoryMapping.get().getCustomer().getId(), existingCustomerInventoryMapping.get().getCustomer().getUsername(), message.getLoggedInUserName(), message.getMvnoId(), SavedCustomerInventoryMapping.getConnectionNo());
            custinventorymappingId.add(existingCustomerInventoryMapping.get().getId());
            CustomerInventoryMappingMessage inventoryMappingMessage = new CustomerInventoryMappingMessage(message.getStatus(), custinventorymappingId);
            kafkaMessageSender.send(new KafkaMessageData(inventoryMappingMessage, CustomerInventoryMappingMessage.class.getSimpleName()));
            ApplicationLogger.logger.info("Customer inventory mapping update at assign inventory with item id " + message.getItemId());
        } catch (Exception e) {
            LOGGER.error("Unable to Update Customer Inventory Mapping at Assign Inventory with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
        return custinventorymappingId;
    }

    private void saveSerializedItem(InventorySerialNumberMessage message) {
        try {
            Optional<Item> itemOptional = itemRepository.findById(message.getItemId());
            if (!itemOptional.isPresent()) {
                Item item = new Item();
                item.setId(message.getItemId());
                item.setName(message.getItemName());
                item.setMacAddress(message.getMacAddress());
                item.setSerialNumber(message.getSerialNumber());
                itemRepository.save(item);
                ApplicationLogger.logger.info("Serialized item with item name " + message.getItemName());
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Save Serialized Item with Item id: " + message.getItemId() + " , Customer Id: " + message.getCustId() + " , Serial Number: " + message.getSerialNumber() + " and Mac address: " + message.getMacAddress() + " errormessage: " + e.getMessage());
        }
    }

    public void verifyAndInitiateHsnCreateRequst(Integer custId, String custName, String loggedInUserName, Integer mvnoId, String connectionNumber) {
        try {
            ClientService clientService = clientServiceRepository.getByNameAndMvnoId(CommonConstants.NMS_CONSTANTS.CONFIG_NAME, mvnoId);
            CustomerServiceMapping customerServiceMapping = customerServiceMappingRepository.findByConnectionNoAndCustId(connectionNumber, custId);
            if (customerServiceMapping != null) {
                String loggedinUsername = null;
                if (Objects.nonNull(getLoggedInUser())) {
                    loggedinUsername = getLoggedInUser().getUsername();
                } else {
                    loggedinUsername = loggedInUserName;
                }
                //send hsn call
                if (clientService != null) {
                    if (clientService.getValue().equalsIgnoreCase("true")) {

                        //calling below function to create hsn service when customer is get activated
                        boolean isSuccess = customersService.createHSNService(custId, custName, loggedinUsername, mvnoId, custId, customerServiceMapping.getId());
                        ;
                        if (isSuccess) {
                            if (!customerServiceMapping.getStatus().equals(SubscriberConstants.ACTIVE)) {
                                customerServiceMapping.setStatus(SubscriberConstants.ACTIVATION_PENDING);
                            }
                        }

                        customerServiceMappingRepository.save(customerServiceMapping);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Unable to Verify Initiate HsnCreate Request with Customer Id: " + custId + " errormessage: " + e.getMessage());
        }
    }
}
