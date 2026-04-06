package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.repository.postpaid.TaxRepository;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.pojo.api.ChargePojo;
import com.adopt.apigw.service.postpaid.TaxService;

@Mapper(componentModel = "spring", uses = {TaxMapper.class})
public abstract class ChargeMapper implements IBaseMapper<ChargePojo, Charge> {

    @Mapping(source = "data.tax", target = "taxid")
    @Override
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract ChargePojo domainToDTO(Charge data, @Context CycleAvoidingMappingContext context);

    @Mapping(source = "dtoData.taxid", target = "tax")
    @Override
    public abstract Charge dtoToDomain(ChargePojo dtoData, @Context CycleAvoidingMappingContext context);

    @Autowired
    TaxService taxService;
    @Autowired
    private TaxRepository taxRepository;

    Integer fromTax(Tax entity) {
        return entity == null ? null : entity.getId();
    }

    Tax fromTaxid(Integer taxId) {
        if (taxId == null) {
            return null;
        }
        Tax entity = null;
        try {
            entity = taxRepository.findById(taxId).get();
            entity.setId(taxId);
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }

    @AfterMapping
    public void commonAfterPostmapping(@MappingTarget ChargePojo dto, Charge domain) {
        if(domain.getTax()!=null){
            dto.setTaxName(domain.getTax().getName());
        }
    }
}
