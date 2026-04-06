package com.adopt.apigw.modules.SectorMaster.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.SectorMaster.Domain.SectorMaster;
import com.adopt.apigw.modules.SectorMaster.Model.SectorMasterDTO;
import org.mapstruct.Mapper;

@Mapper
public interface SectorMasterMapper extends IBaseMapper<SectorMasterDTO, SectorMaster> {
}
