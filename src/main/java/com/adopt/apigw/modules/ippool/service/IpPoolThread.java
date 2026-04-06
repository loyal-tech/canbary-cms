package com.adopt.apigw.modules.ippool.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.ippool.domain.IPPoolDtls;
import com.adopt.apigw.modules.ippool.mapper.IPPoolDtlsMapper;
import com.adopt.apigw.modules.ippool.model.IPPoolDtlsDTO;
import com.adopt.apigw.modules.ippool.repository.IPPoolDtlsRepository;

public class IpPoolThread implements Runnable{

    private List<IPPoolDtlsDTO> ipPoolDtlsDTO;
    private IPPoolDtlsMapper ipPoolDtlsMapper;
    private IPPoolDtlsRepository ipPoolDtlsRepository;

    public IpPoolThread(List<IPPoolDtlsDTO> ipPoolDtlsDTOList,IPPoolDtlsMapper ipPoolDtlsMapper,IPPoolDtlsRepository ipPoolDtlsRepository)
    {
        this.ipPoolDtlsDTO=ipPoolDtlsDTOList;
        this.ipPoolDtlsMapper=ipPoolDtlsMapper;
        this.ipPoolDtlsRepository=ipPoolDtlsRepository;
    }

    @Override
    public void run() {
        List<IPPoolDtls> ipPoolDtlsList=new ArrayList<>();
        for(int i=0;i<ipPoolDtlsDTO.size();i++)
        {
          IPPoolDtls ipPoolDtls=  ipPoolDtlsMapper.dtoToDomain(ipPoolDtlsDTO.get(i),new CycleAvoidingMappingContext());
            ipPoolDtlsList.add(ipPoolDtls);
        }
      //  List<IPPoolDtls> ipPoolDtlsList=  ipPoolDtlsDTO.stream().map(data->ipPoolDtlsMapper.dtoToDomain(data,new CycleAvoidingMappingContext())).collect(Collectors.toList());
        ipPoolDtlsRepository.saveAll(ipPoolDtlsList);
    }
}
