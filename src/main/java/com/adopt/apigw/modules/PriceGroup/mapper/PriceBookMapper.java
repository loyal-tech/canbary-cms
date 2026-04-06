package com.adopt.apigw.modules.PriceGroup.mapper;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBook;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookDTO;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookRepository;
import com.adopt.apigw.modules.PriceGroup.service.PriceBookService;

@Mapper
public abstract class PriceBookMapper implements IBaseMapper<PriceBookDTO, PriceBook> {

    @Autowired
    private PriceBookRepository priceBookRepository;

    @Override
    @Mapping(source = "priceBook.validfrom", target = "validFromString", dateFormat = "dd-MM-yyyy")
    @Mapping(source = "priceBook.validto", target = "validToString", dateFormat = "dd-MM-yyyy")
    //@Mapping(source = "priceBook.priceBookSlabDetailsList", target = "priceBookSlabDetailsList")
    public abstract PriceBookDTO domainToDTO(PriceBook priceBook, @Context CycleAvoidingMappingContext context);

    @Override
    public abstract PriceBook dtoToDomain(PriceBookDTO dtoData, @Context CycleAvoidingMappingContext context);

    @AfterMapping
    void afterMapping(@MappingTarget PriceBookDTO priceBookDTO, PriceBook priceBook) {
        try {
            if (priceBook.getId() != null) {
                priceBookDTO.setNoPartnerAssociate(priceBookRepository.countPartnerByPriceBook(priceBook.getId().intValue()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
