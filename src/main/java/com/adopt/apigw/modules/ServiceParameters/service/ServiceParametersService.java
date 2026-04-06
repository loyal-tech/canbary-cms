package com.adopt.apigw.modules.ServiceParameters.service;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService2;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.ServiceParameters.mapper.ServiceParametersMapper;
import com.adopt.apigw.modules.ServiceParameters.model.ServiceParametersDTO;
import com.adopt.apigw.modules.ServiceParameters.repository.ServcieParametersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceParametersService extends ExBaseAbstractService2<ServiceParametersDTO, ServiceParameter,Long> {

    public ServiceParametersService(ServcieParametersRepository repository, ServiceParametersMapper mapper) {
        super(repository, mapper);
    }
    private static final Logger logger = LoggerFactory.getLogger(ServiceParametersService.class);
    @Autowired
    ServcieParametersRepository servcieParametersRepository;

    public List<ServiceParameter> findall() {
        String SUBMODULE = getModuleNameForLog() + " [findall()] ";
        logger.info(getModuleNameForLog() + "--" + "  findall .Data[" + SUBMODULE.toString() + "]");
        try {
            return servcieParametersRepository.findAll();
        }catch (Exception exception){
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Override
    public String getModuleNameForLog() {
        return "[ServiceParametersService]";
    }

    @Override
    public ServiceParametersDTO getEntityForUpdateAndDelete(Long id,Integer mvnoId) throws Exception {
        return null;
    }
}
