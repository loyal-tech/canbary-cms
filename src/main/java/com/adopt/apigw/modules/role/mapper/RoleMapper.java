package com.adopt.apigw.modules.role.mapper;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.adopt.apigw.modules.acl.repository.CustomACLEntryRepository;
import com.adopt.apigw.modules.acl.service.AclService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.modules.role.model.RoleDTO;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper
public abstract interface RoleMapper extends IBaseMapper<RoleDTO, Role> {

//    @Autowired
//    CustomACLEntryRepository customAclEntryRepository = null;
//    @Override
////    @Mapping(source = "role.aclEntry", target = "aclEntryPojoList")
//    public abstract RoleDTO domainToDTO(Role role, @Context CycleAvoidingMappingContext context);
//
//    @Override
//    @Mapping(source = "dtoData.aclEntryPojoList", target = "aclEntry")
////    @Mapping(source = "dtoData.aclEntryPojoList.roleId", target = "aclEntry")
//    public abstract Role dtoToDomain(RoleDTO dtoData, @Context CycleAvoidingMappingContext context);
}
