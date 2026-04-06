package com.adopt.apigw.modules.Matrix.mapper;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.modules.Matrix.domain.Matrix;
import com.adopt.apigw.modules.Matrix.model.MatrixDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MatrixMapper extends IBaseMapper<MatrixDTO, Matrix> {

}
