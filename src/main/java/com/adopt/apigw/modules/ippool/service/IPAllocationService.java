package com.adopt.apigw.modules.ippool.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.ippool.domain.IPAllocation;
import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.modules.ippool.mapper.IPAllocationMapper;
import com.adopt.apigw.modules.ippool.mapper.IPPoolDtlsMapper;
import com.adopt.apigw.modules.ippool.model.IPAllocationDTO;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.repository.IPAllocationRepository;
import com.adopt.apigw.modules.ippool.repository.IPPoolDtlsRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IPAllocationService extends ExBaseAbstractService<IPAllocationDTO, IPAllocation, Long> {

    @Autowired
    private IPAllocationRepository ipAllocationRepository;

    public IPAllocationService(IPAllocationRepository repository, IPAllocationMapper mapper) {
        super(repository, mapper);
    }

    public List<IPAllocationDTO> getIPAllocationByCustId(Long custId){
        return ipAllocationRepository.findAllByCustId(custId).stream().map(data-> getMapper().domainToDTO(data,new CycleAvoidingMappingContext())).collect(Collectors.toList());
    }

    @Override
    public String getModuleNameForLog() {
        return "[IPAllocationService]";
    }
}
