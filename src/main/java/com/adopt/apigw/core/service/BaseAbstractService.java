package com.adopt.apigw.core.service;

import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;

import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseAbstractService<DTO,DATA,ID> implements BaseService<DTO,ID> {

    private final JpaRepository<DATA,ID> repository;
    private final IBaseMapper<DTO,DATA> mapper;

    public BaseAbstractService(JpaRepository<DATA, ID> repository, IBaseMapper<DTO, DATA> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<DTO> getAllEntities() {
        return repository.findAll().stream()
                .map( data -> mapper.domainToDTO(data, new CycleAvoidingMappingContext()))
                .collect(Collectors.toList());
    }

    @Override
    public DTO getEntityById(ID id) {
        return mapper.domainToDTO(repository.findById(id).get(), new CycleAvoidingMappingContext());
    }

    @Override
    public DTO saveEntity(DTO entity) {
        DATA entityDomain = mapper.dtoToDomain(entity
                , new CycleAvoidingMappingContext());

        return mapper.domainToDTO(repository.save(entityDomain), new CycleAvoidingMappingContext());
    }

    @Override
    public void deleteEntity(DTO entity) {
        DATA entityDomain = mapper.dtoToDomain(entity
                , new CycleAvoidingMappingContext());
        repository.delete(entityDomain);
    }
}
