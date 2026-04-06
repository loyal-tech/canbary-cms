package com.adopt.apigw.modules.ServiceParameterMapping.Service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.ServiceParameterMapping.domain.ServiceParamMapping;
import com.adopt.apigw.modules.ServiceParameterMapping.mapper.ServiceParamMappingMapper;
import com.adopt.apigw.modules.ServiceParameterMapping.model.ServiceParamMappingDTO;
import com.adopt.apigw.modules.ServiceParameterMapping.repository.ServiceParamMappingRepository;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.ServiceParameters.repository.ServcieParametersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceParamMappingService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceParamMappingService.class);

    public String getModuleNameForLog() {
        return "[ServiceParamMappingService]";
    }
    @Autowired
    ServiceParamMappingRepository serviceParamMappingRepository;

    @Autowired
    ServiceParamMappingMapper serviceParamMappingMapper;

    @Autowired
    ServcieParametersRepository servcieParametersRepository;

    public List<ServiceParamMappingDTO> getParamsByServiceId(Long serviceId) {
        String SUBMODULE = getModuleNameForLog() + " [getParamsByServiceId()] ";
        logger.info(getModuleNameForLog() + "--" + " Fetching Params By ServiceId .Data[" + SUBMODULE.toString() + "]");
        try {
            List<ServiceParamMapping> serviceParamMappings = serviceParamMappingRepository.findByServiceid(serviceId);
            return serviceParamMappings.stream().map(serviceParamMapping -> serviceParamMappingMapper.domainToDTO(serviceParamMapping, new CycleAvoidingMappingContext())).collect(Collectors.toList());
        }
        catch (Exception exception){
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    public ServiceParamMapping saveServiceParamValue(ServiceParamMappingDTO serviceParamMappingDTO) {
        String SUBMODULE = getModuleNameForLog() + " [saveServiceParamValue()] ";
        logger.info(getModuleNameForLog() + "--" + " save ServiceParam Value .Data[" + SUBMODULE.toString() + "]");
        try {
            ServiceParamMapping serviceParamMapping = new ServiceParamMapping();
            serviceParamMapping.setId(serviceParamMappingDTO.getId());
            serviceParamMapping.setServiceid(serviceParamMappingDTO.getServiceid());
            ServiceParameter serviceParameter = servcieParametersRepository.findById(serviceParamMappingDTO.getServiceParamId()).get();
            serviceParamMapping.setServiceParamId(serviceParameter.getId());
            serviceParamMapping.setValue(serviceParamMappingDTO.getValue());
            serviceParamMappingRepository.save(serviceParamMapping);
            return serviceParamMapping;
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }

    public List<ServiceParamMapping> saveServiceParamValueList(List<ServiceParamMappingDTO> serviceParamMappingDTO) {
        String SUBMODULE = getModuleNameForLog() + " [saveServiceParamValueList()] ";
        logger.info(getModuleNameForLog() + "--" + "save ServiceParam Value List .DataList[" + SUBMODULE.toString() + "]");
        try {
            List<ServiceParamMapping> serviceParamMapping = new ArrayList<>();
            for (ServiceParamMappingDTO serviceParamMappingDTOList : serviceParamMappingDTO) {
                serviceParamMapping.add(saveServiceParamValue(serviceParamMappingDTOList));
            }
            return serviceParamMapping;
        } catch (Exception exception) {
            logger.error(getModuleNameForLog() + "Error : " + exception.getMessage(), exception);
            throw new RuntimeException(exception.getMessage());
        }
    }
}
