package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.constants.SubscriberConstants;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.modules.subscriber.model.PaymentHistoryDTO;
import com.adopt.apigw.repository.common.StaffUserRepository;
import com.adopt.apigw.repository.postpaid.CreditDocRepository;
import com.adopt.apigw.repository.radius.CustomersRepository;
import com.adopt.apigw.service.common.CustomersService;
import com.adopt.apigw.model.common.StaffUser;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper()
public abstract class CreditDocumentMapper implements IBaseMapper<PaymentHistoryDTO, CreditDocument> {

    @Mapping(source = "customer", target = "custId")
    @Mapping(source = "reciptNo", target = "receiptNo")
    @Mapping(source = "type", target="type")
    @Mapping(source = "nextTeamHierarchyMappingId",target = "nextTeamHierarchyMappingId")
    public abstract PaymentHistoryDTO domainToDTO(CreditDocument data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "custId", target = "customer")
    @Mapping(source = "receiptNo", target = "referenceno")
    @Mapping(source = "type", target="type")

    public abstract CreditDocument dtoToDomain(PaymentHistoryDTO dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    CustomersService customersService;

    @Autowired
    CustomerMapper customerMapper;

    @Autowired
    CreditDocRepository creditDocRepository;

    @Autowired
    StaffUserRepository staffUserRepository;

    CreditDocument fromId(Integer id) {
        if (id == null) {
            return null;
        }
        final CreditDocument creditDocument = new CreditDocument();
        creditDocument.setId(id);
        return creditDocument;
    }

    Integer fromCustomers(Customers customers) {
        return customers == null ? null : customers.getId();
    }

    @Autowired
    CustomersRepository customersRepository;

    Customers fromCustomerId(Integer custId) {
        if (custId == null) {
            return null;
        }
        Customers entity = null;
        try {
            entity =  customersRepository.findById(custId).get();
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    public void loadPaymentHistory(CreditDocument domain, @MappingTarget PaymentHistoryDTO dto) {
        String status = creditDocRepository.getOne(domain.getId()).getStatus();
//        if (status.equalsIgnoreCase(SubscriberConstants.PAYMENT_STATUS_APPROVED)) {
//            dto.setStatus("complete");
//        }
        StaffUser staffUser = null;
        if (domain.getCreatedById() != null && domain.getCreatedById() != 0) {
            staffUser = staffUserRepository.getOne(domain.getCreatedById());
            dto.setPaymentBy(staffUser.getFullName());
        }

    }
}
