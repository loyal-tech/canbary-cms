package com.adopt.apigw.core.controller;

import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

public interface IBaseExController<T> {
    GenericDataDTO getAll(@RequestBody PaginationRequestDTO requestDTO, HttpServletRequest req, @RequestParam Integer mvnoId);

    GenericDataDTO getEntityById(@PathVariable String id,HttpServletRequest req,@RequestParam("mvnoId") Integer mvnoId)throws Exception;

    GenericDataDTO getAllWithoutPagination(@RequestParam Integer mvnoId);

    GenericDataDTO save(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication, HttpServletRequest req,@RequestParam Integer mvnoId) throws Exception;

    GenericDataDTO update(@Valid @RequestBody T entityDTO, BindingResult result, Authentication authentication,HttpServletRequest req,@RequestParam Integer mvnoId)throws Exception;

    GenericDataDTO delete(@RequestBody T entityDTO, Authentication authentication,HttpServletRequest req)throws Exception;

}
