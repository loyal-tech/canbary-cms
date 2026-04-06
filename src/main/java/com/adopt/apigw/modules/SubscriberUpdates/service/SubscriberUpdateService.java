package com.adopt.apigw.modules.SubscriberUpdates.service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.SubscriberUpdates.Utils.UpdateConstant;
import com.adopt.apigw.modules.SubscriberUpdates.domain.SubscriberUpdate;
import com.adopt.apigw.modules.SubscriberUpdates.mapper.SubscriberUpdateMapper;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateDTO;
import com.adopt.apigw.modules.SubscriberUpdates.model.SubscriberUpdateSearchDTO;
import com.adopt.apigw.modules.SubscriberUpdates.repository.SubscriberUpdateRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubscriberUpdateService extends ExBaseAbstractService<SubscriberUpdateDTO, SubscriberUpdate, Long> {
    @Autowired
    private SubscriberUpdateRepository repository;
    @Autowired
    private ObjectMapper Obj;

    @Autowired
    private SubscriberUpdateMapper mapper;

    static String reqDtoJson;
    static String domainJson;
    static String textVal;

    public SubscriberUpdateService(SubscriberUpdateRepository repository, SubscriberUpdateMapper mapper) {
        super(repository, mapper);
    }

    public void updateSubscriber(String entity, String operation, Object newReqDTO, Object oldEntity, Customers customers) throws JsonProcessingException {
        reqDtoJson = Obj.writeValueAsString(newReqDTO);
        domainJson = Obj.writeValueAsString(oldEntity);
        textVal = Obj.writeValueAsString(newReqDTO);
        List<Customers> customersList = new ArrayList<>();
        customersList.add(customers);
        switch (entity) {
            case "Subscriber":
                switch (operation) {
                    case "ChangeCharge":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "UpdateAddress":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "ChangeVoiceDetails":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "ChangeQuota":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "changeStatus":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "changeExpiry":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "ReverseCharge":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "ApplyCharge":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "updateMacDetails":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "updateContactDetails":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "updateBasicDetails":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                    case "updateNetworkDetails":
                        if (newReqDTO == null) {
                            break;
                        } else if (oldEntity == null) {
                            break;
                        } else {
                            SubscriberUpdate update = new SubscriberUpdate(operation, domainJson, reqDtoJson, entity, customers, textVal);
                            repository.save(update);
                        }
                }
        }
    }

    public List<SubscriberUpdateDTO> getAllByCustomer(Integer custId) {
        List<SubscriberUpdate> subscriberUpdates = repository.getAllByCustomers_IdOrderByCreatedateDesc(custId);
        List<SubscriberUpdateDTO> dtos = subscriberUpdates.stream().map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        return dtos;
    }

    public List<SubscriberUpdateDTO> getCustomerByTime(SubscriberUpdateSearchDTO dto) throws Exception {
        List<SubscriberUpdateDTO> dto1;
        List<SubscriberUpdate> subscriberUpdateList;
        if (dto.getSTART_DATE() != null && dto.getEND_DATE() != null) {
            if (dto.getOperation() != null) {
                if (dto.getOperation().equalsIgnoreCase(UpdateConstant.PLAN_CHANGE)) {
                    subscriberUpdateList = repository.findAllByCreatedateBetweenAndCustomers_IdAndOperation(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getCustomer_id(), dto.getOperation());
                } else if (dto.getOperation().equalsIgnoreCase(UpdateConstant.STATUS_CHANGE)) {
                    subscriberUpdateList = repository.findAllByCreatedateBetweenAndCustomers_IdAndOperation(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getCustomer_id(), dto.getOperation());
                } else {
                    subscriberUpdateList = repository.findByStartDateEndDate(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getCustomer_id());
                }
            } else {
                subscriberUpdateList = repository.findByStartDateEndDate(dto.getSTART_DATE(), dto.getEND_DATE(), dto.getCustomer_id());
            }
        } else {
            if (dto.getOperation() != null) {
                if (dto.getOperation().equalsIgnoreCase(UpdateConstant.PLAN_CHANGE)) {
                    subscriberUpdateList = repository.findByOperation(dto.getCustomer_id(), dto.getOperation());
                } else if (dto.getOperation().equalsIgnoreCase(UpdateConstant.STATUS_CHANGE)) {
                    subscriberUpdateList = repository.findByOperation(dto.getCustomer_id(), dto.getOperation());
                } else {
                    subscriberUpdateList = repository.getAllByCustomers_IdOrderByCreatedateDesc(dto.getCustomer_id());
                }
            } else {
                subscriberUpdateList = repository.getAllByCustomers_IdOrderByCreatedateDesc(dto.getCustomer_id());
            }
        }
        dto1 = subscriberUpdateList.stream().map(data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
        return dto1;
    }

    @Override
    public String getModuleNameForLog() {
        return "[SubscriberUpdate Service]";
    }

    @Override
    public void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception {
        Sheet sheet = workbook.createSheet("SubscriberUpdate");
        createExcel(workbook, sheet, SubscriberUpdateDTO.class, null,mvnoId);
    }

    @Override
    public void pdfGenerate(Document doc, Integer mvnoId) throws Exception {
        createPDF(doc, SubscriberUpdateDTO.class, null,mvnoId);
    }
}
