package com.adopt.apigw.modules.Cas.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Cas.Domain.CasMaster;
import com.adopt.apigw.modules.Cas.Model.CasMasterDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CasMapper extends IBaseMapper<CasMasterDTO, CasMaster> {
}