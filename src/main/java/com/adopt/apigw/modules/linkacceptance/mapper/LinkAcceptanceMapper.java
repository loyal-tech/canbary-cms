package com.adopt.apigw.modules.linkacceptance.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.linkacceptance.domain.LinkAcceptance;
import com.adopt.apigw.modules.linkacceptance.model.LinkAcceptanceDTO;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public abstract class LinkAcceptanceMapper implements IBaseMapper<LinkAcceptanceDTO, LinkAcceptance> {
//    @Autowired
//    CustomersService customersService;

    public abstract LinkAcceptance dtoToDomain(LinkAcceptanceDTO linkAcceptanceDTO, CycleAvoidingMappingContext context);

    public abstract LinkAcceptanceDTO domainToDTO(LinkAcceptance linkAcceptance, CycleAvoidingMappingContext context);

    public abstract List<LinkAcceptanceDTO> domainToDTO(List<LinkAcceptance> linkAcceptanceList, @Context CycleAvoidingMappingContext context);

    public abstract List<LinkAcceptance> dtoToDomain(List<LinkAcceptanceDTO> linkAcceptanceDTOList, @Context CycleAvoidingMappingContext context);


//    Integer fromCustomersToId(Customers customers) {
//        return customers == null ? null : customers.getId();
//    }
//
//    Customers fromCustomersIdToCustomers(Integer entityId) {
//        if (entityId == null) {
//            return null;
//        }
//        Customers entity;
//        try {
//            entity = customersService.getById(entityId);
//            // entity = customerMapper.dtoToDomain(entity, new CycleAvoidingMappingContext());
//            entity.setId(Math.toIntExact(entityId));
//        } catch (Exception e) {
//            e.printStackTrace();
//            entity = null;
//        }
//        return entity;
//    }
}
