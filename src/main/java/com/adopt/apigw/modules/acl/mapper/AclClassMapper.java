package com.adopt.apigw.modules.acl.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.acl.domain.AclClass;
import com.adopt.apigw.modules.acl.model.AclClassDTO;

@Mapper
public interface AclClassMapper extends IBaseMapper<AclClassDTO, AclClass> {
}
