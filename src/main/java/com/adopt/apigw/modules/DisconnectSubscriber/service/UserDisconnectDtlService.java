package com.adopt.apigw.modules.DisconnectSubscriber.service;

import org.springframework.stereotype.Service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.DisconnectSubscriber.domain.UserDisconnectDtl;
import com.adopt.apigw.modules.DisconnectSubscriber.mapper.UserDisconnectDtlMapper;
import com.adopt.apigw.modules.DisconnectSubscriber.model.UserDisconnectDtlDTO;
import com.adopt.apigw.modules.DisconnectSubscriber.repository.UserDisconnectDtlRepository;

@Service
public class UserDisconnectDtlService  extends ExBaseAbstractService<UserDisconnectDtlDTO, UserDisconnectDtl, Long> {
    public UserDisconnectDtlService(UserDisconnectDtlRepository repository, UserDisconnectDtlMapper mapper) {
        super(repository, mapper);
    }

    @Override
    public String getModuleNameForLog() {
        return "[UserDiconnectDtlService]";
    }
}
