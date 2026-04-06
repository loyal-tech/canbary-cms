package com.adopt.apigw.modules.acl.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.adopt.apigw.modules.acl.model.CustomACLEntryDTO;

@Mapper
public interface CustomACLEntryMapper extends IBaseMapper<CustomACLEntryDTO, CustomACLEntry> {
}
