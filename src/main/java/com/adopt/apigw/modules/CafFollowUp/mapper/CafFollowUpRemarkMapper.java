package com.adopt.apigw.modules.CafFollowUp.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUp;
import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpRemark;
import com.adopt.apigw.modules.CafFollowUp.model.CafFollowUpRemarkDTO;
import com.adopt.apigw.modules.CafFollowUp.service.CafFollowUpService;

@Mapper
public abstract class CafFollowUpRemarkMapper implements IBaseMapper<CafFollowUpRemarkDTO, CafFollowUpRemark>{

	String MODULE = " [CafFollowUpRemarkMapper] ";

	@Autowired
	private CafFollowUpService cafFollowUpService;

	@Mapping(source = "cafFollowUp", target = "cafFollowUpId")
	@Mapping(source = "cafFollowUp", target = "cafFollowUpName")
	@Override
	public abstract CafFollowUpRemarkDTO domainToDTO(CafFollowUpRemark data, @Context CycleAvoidingMappingContext context);

	@Mapping(source = "cafFollowUpId", target = "cafFollowUp")
	@Override
	public abstract CafFollowUpRemark dtoToDomain(CafFollowUpRemarkDTO dtoData, @Context CycleAvoidingMappingContext context);

	Long fromCafFollowUpToCafFollowUpId(CafFollowUp entity) {
		return entity == null ? null : entity.getId();
	}
	
	String fromCafFollowUpToCafFollowUpName(CafFollowUp entity) {
		return entity == null ? null : entity.getFollowUpName();
	}

	CafFollowUp fromCafFollowUpIdToCafFollowUp(Long entityId) {
		if (entityId == null) {
			return null;
		}
		CafFollowUp entity;
		try {
			entity = cafFollowUpService.get(entityId);
		} catch (Exception e) {
			e.printStackTrace();
			entity = null;
		}
		return entity;
	}

	@AfterMapping
	void afterMapping(@MappingTarget CafFollowUpRemarkDTO cafFollowUpRemarkDTO, CafFollowUpRemark cafFollowUpRemark) {
		try {
			if (cafFollowUpRemark != null) {
				if (cafFollowUpRemark.getCafFollowUp() != null) {
					cafFollowUpRemarkDTO.setCafFollowUpId(cafFollowUpRemark.getCafFollowUp().getId());
					cafFollowUpRemarkDTO.setCafFollowUpName(cafFollowUpRemark.getCafFollowUp().getFollowUpName());
				}
			}
		} catch (Exception ex) {
			ApplicationLogger.logger.error(MODULE + " After Mapping " + ex.getMessage(), ex);
			ex.printStackTrace();
		}
	}
}
