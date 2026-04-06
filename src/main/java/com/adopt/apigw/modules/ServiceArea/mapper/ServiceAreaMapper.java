package com.adopt.apigw.modules.ServiceArea.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Pincode.mapper.PincodeMapper;
import com.adopt.apigw.modules.Pincode.model.PincodeDTO;
import com.adopt.apigw.modules.Pincode.repository.PincodeRepository;
import com.adopt.apigw.modules.Pincode.service.PincodeService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceAreaPincodeRel;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaPincodeRelDTO;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.model.RoleDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Area.domain.Area;
import com.adopt.apigw.modules.Area.service.AreaService;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.model.ServiceAreaDTO;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.model.TeamsDTO;

@Mapper
public abstract class ServiceAreaMapper implements IBaseMapper<ServiceAreaDTO, ServiceArea>{

    @Autowired
    private PincodeService pincodeService;
    @Autowired
    PincodeRepository pincodeRepository;

    @Autowired
    private PincodeMapper pincodeMapper;

    @Override
    @Mapping(source = "dtoData.pincodes", target = "pincodeList")
    public abstract ServiceArea dtoToDomain(ServiceAreaDTO dtoData, CycleAvoidingMappingContext context);

    @Override
    @Mapping(source = "data.pincodeList", target = "pincodes")
    @Mapping(target = "displayId", source = "data.id")
    @Mapping(target = "displayName", source = "data.name")
    public abstract ServiceAreaDTO domainToDTO(ServiceArea data, CycleAvoidingMappingContext context);

    public abstract java.util.List<Pincode> mapPincodesToPincodeList(List<Integer> value);

    public abstract java.util.List<Integer> mapPincodeListToPincodes(List<Pincode> value);

    Integer fromPincodeToId(Pincode entity) {
        return entity == null ? null : entity.getId().intValue();
    }

    Pincode fromIdToPincode(Integer entityId) {
        if (entityId == null) {
            return null;
        }
        Pincode entity;
        try {
//            PincodeDTO entityDTO = pincodeService.getEntityById(entityId.longValue());
            entity = pincodeRepository.findById(entityId.longValue()).get();
            entity.setId(entityId.longValue());
        } catch (Exception e) {
            e.printStackTrace();
            entity = null;
        }
        return entity;
    }


}
