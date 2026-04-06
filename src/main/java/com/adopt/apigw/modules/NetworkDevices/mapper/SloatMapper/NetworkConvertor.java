package com.adopt.apigw.modules.NetworkDevices.mapper.SloatMapper;

import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDeviceBind;
import com.adopt.apigw.modules.NetworkDevices.model.NetworkDeviceBindDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NetworkConvertor {

    private ModelMapper modelMapper;

    @Autowired
    public NetworkConvertor(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public NetworkDeviceBind convertDtoToEntity(NetworkDeviceBindDTO dataStoreMappingDto){
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        NetworkDeviceBind dataStoreMapping= new NetworkDeviceBind();
        modelMapper.map(dataStoreMappingDto, dataStoreMapping);
        return dataStoreMapping;
    }

    public NetworkDeviceBindDTO convertEntityToDto(NetworkDeviceBind dataStoreMapping) {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        NetworkDeviceBindDTO dataStoreMappingDto = new NetworkDeviceBindDTO();
        modelMapper.map(dataStoreMapping, dataStoreMappingDto);
        return dataStoreMappingDto;
    }
}
