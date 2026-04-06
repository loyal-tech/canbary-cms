package com.adopt.apigw.core.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.adopt.apigw.core.dto.GenericDataDTO;

public interface IBaseController<T> {
    GenericDataDTO getAll();
    GenericDataDTO getEntityById(@PathVariable String id);
    GenericDataDTO save(@RequestBody T entityDTO);
    GenericDataDTO update(@RequestBody T entityDTO);
    GenericDataDTO delete(@RequestBody T entityDTO);
}
