package com.adopt.apigw.modules.ServiceArea.service;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.adopt.apigw.modules.ServiceArea.mapper.ServiceAreaPincodeRelMapper;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaPincodeRelDTO;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaPincodeRelRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ServiceAreaPincodeRelService extends ExBaseAbstractService<ServiceAreaPincodeRelDTO, ServiceAreaPincodeRel, Long> {

    public ServiceAreaPincodeRelService(ServiceAreaPincodeRelRepository repository, ServiceAreaPincodeRelMapper mapper) {
        super(repository, mapper);
    }


    @Override
    public String getModuleNameForLog() { return "[ServiceAreaPincodeRelService]"; }
}
