package com.adopt.apigw.modules.Template.mapper;

import org.mapstruct.Mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Template.domain.Template;
import com.adopt.apigw.modules.Template.model.TemplateDTO;

@Mapper
public interface TemplateMapper extends IBaseMapper<TemplateDTO, Template> {
}
