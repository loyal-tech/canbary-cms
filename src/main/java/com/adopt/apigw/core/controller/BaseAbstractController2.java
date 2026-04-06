package com.adopt.apigw.core.controller;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.IBaseDto2;
import com.adopt.apigw.core.service.BaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseAbstractController2<DTO extends IBaseDto2> implements IBaseController<DTO> {

    private BaseService<DTO, Long> service;

    public BaseAbstractController2(BaseService service) {
        this.service = service;
    }

    @Override
    @GetMapping
    public GenericDataDTO getAll() {
        List<DTO> list = service.getAllEntities();
        List<DTO> sortedList = list.stream().sorted(Comparator.comparing(DTO::getIdentityKey).reversed()).collect(Collectors.toList());
        GenericDataDTO genericDataDTO = GenericDataDTO.getGenericDataDTO(sortedList);
        return genericDataDTO;
    }

    @Override
    @GetMapping("{id}")
    public GenericDataDTO getEntityById(@PathVariable String id) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");

        try {
            genericDataDTO.setData(service.getEntityById(new Long(id)));

        } catch (Exception e) {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
            genericDataDTO.setTotalRecords(0);
            genericDataDTO.setDataList(null);
        }
        return genericDataDTO;
    }

    @Override
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO save(@RequestBody DTO entityDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        genericDataDTO.setData(service.saveEntity(entityDTO));
        genericDataDTO.setResponseCode(HttpStatus.OK.value());
        genericDataDTO.setResponseMessage("Success");
        return genericDataDTO;
    }

    @Override
    @PostMapping(value = "/update", consumes = MediaType.APPLICATION_JSON_VALUE)
    public GenericDataDTO update(@RequestBody DTO entityDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        DTO dtoData = service.getEntityById(entityDTO.getIdentityKey());
        if (dtoData != null) {
            genericDataDTO.setData(service.saveEntity(entityDTO));
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
        }
        return genericDataDTO;
    }

    @Override
    @PostMapping(value = "/delete", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public GenericDataDTO delete(@RequestBody DTO entityDTO) {
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        DTO dtoData = service.getEntityById(entityDTO.getIdentityKey());
        if (dtoData != null) {
            service.deleteEntity(entityDTO);
            genericDataDTO.setData(entityDTO);
            genericDataDTO.setResponseCode(HttpStatus.OK.value());
            genericDataDTO.setResponseMessage("Success");
        } else {
            genericDataDTO.setResponseCode(HttpStatus.NOT_FOUND.value());
            genericDataDTO.setResponseMessage("Not Found");
        }
        return genericDataDTO;
    }
}
