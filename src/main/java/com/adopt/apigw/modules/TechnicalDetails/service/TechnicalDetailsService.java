package com.adopt.apigw.modules.TechnicalDetails.service;

import com.adopt.apigw.constants.Constants;
import com.adopt.apigw.constants.SearchConstants;
import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.exception.CustomValidationException;
import com.adopt.apigw.modules.SubBusinessVertical.Domain.QSubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.adopt.apigw.modules.TechnicalDetails.domain.TechnicalDetails;
import com.adopt.apigw.modules.TechnicalDetails.mapper.TechnicalDetailsMapper;
import com.adopt.apigw.modules.TechnicalDetails.model.TechnicalDetailsDto;
import com.adopt.apigw.modules.TechnicalDetails.repository.TechnicalDetailsRepository;
import com.adopt.apigw.utils.APIConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TechnicalDetailsService extends ExBaseAbstractService2<TechnicalDetailsDto, TechnicalDetails, Long> {
    public TechnicalDetailsService(TechnicalDetailsRepository repository, TechnicalDetailsMapper mapper) {
        super(repository, mapper);
    }
    @Autowired
    private TechnicalDetailsRepository technicalDetailsRepository;

    @Autowired
    private TechnicalDetailsMapper technicalDetailsMapper;

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    public List<TechnicalDetails> getAll(){
        List<TechnicalDetails> technicalDetails = technicalDetailsRepository.findAll();
        return technicalDetails;
    }

    @Override
    public TechnicalDetailsDto getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}
