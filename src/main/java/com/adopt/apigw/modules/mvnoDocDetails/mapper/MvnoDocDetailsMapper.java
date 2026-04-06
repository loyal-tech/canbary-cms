package com.adopt.apigw.modules.mvnoDocDetails.mapper;

import com.adopt.apigw.modules.mvnoDocDetails.domain.MvnoDocDetails;
import com.adopt.apigw.modules.mvnoDocDetails.model.MvnoDocDetailsDTO;
import org.mapstruct.Mapper;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.model.MvnoDTO;

@Mapper
public abstract class MvnoDocDetailsMapper implements IBaseMapper<MvnoDocDetailsDTO, MvnoDocDetails> {

}
