package com.adopt.apigw.modules.Area.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.postpaid.City;
import com.adopt.apigw.model.postpaid.Country;
import com.adopt.apigw.model.postpaid.State;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.model.AreaDTO;
import com.adopt.apigw.modules.Pincode.mapper.PincodeMapper;
import com.adopt.apigw.service.postpaid.CityService;
import com.adopt.apigw.service.postpaid.CountryService;
import com.adopt.apigw.service.postpaid.StateService;

@Mapper(uses = PincodeMapper.class)
public abstract class AreaMapper implements IBaseMapper<AreaDTO, Area> {
    String MODULE = " [AreaMapper] ";
    @Autowired
    StateService stateService;
    @Autowired
    CountryService countryService;
    @Autowired
    CityService cityService;

    @AfterMapping
    void afterMapping(@MappingTarget AreaDTO areaDTO, Area area) {
        try {
            if (area != null) {
                if (area.getCityId() != null) {
                    City city = cityService.get(area.getCityId(),area.getMvnoId());
                    areaDTO.setCityName(city.getName());
                }
                if (area.getStateId() != null) {
                    State state = stateService.get(area.getStateId(),area.getMvnoId());
                    areaDTO.setStateName(state.getName());
                }
                if (area.getCountryId() != null) {
                    Country country = countryService.get(area.getCountryId(),area.getMvnoId());
                    areaDTO.setCountryName(country.getName());
                }
                if (area.getPincode() != null) {
                    areaDTO.setPincodeId(area.getPincode().getId().intValue());
                    areaDTO.setCode(area.getPincode().getPincode());
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    @Mapping(target = "displayId", source = "id")
    @Mapping(target = "displayName", source = "name")
    public abstract AreaDTO domainToDTO(Area domain, @Context CycleAvoidingMappingContext context);

}
