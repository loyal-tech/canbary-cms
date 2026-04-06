package com.adopt.apigw.modules.TimeBasePolicy.service;

import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.modules.TimeBasePolicy.domain.TimeBasePolicyDetails;
import com.adopt.apigw.modules.TimeBasePolicy.mapper.CustomerTimebasePolicyDetailsMapper;
import com.adopt.apigw.modules.TimeBasePolicy.module.TimeBasePolicyDetailsDTO;
import com.adopt.apigw.modules.TimeBasePolicy.repository.CustomerTimeBasePolicyDetailsRepository;
import com.adopt.apigw.rabbitMq.MessageSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CustomerTimeBasePolicyDetailsService extends ExBaseAbstractService2<TimeBasePolicyDetailsDTO, TimeBasePolicyDetails, Long> {
    public CustomerTimeBasePolicyDetailsService(CustomerTimeBasePolicyDetailsRepository repository, CustomerTimebasePolicyDetailsMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return null;
    }

    @Autowired
    private MessageSender messageSender;

    @Override
    public TimeBasePolicyDetailsDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}
