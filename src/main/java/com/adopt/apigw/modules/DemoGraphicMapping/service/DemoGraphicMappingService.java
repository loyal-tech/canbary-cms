package com.adopt.apigw.modules.DemoGraphicMapping.service;

import com.adopt.apigw.modules.DemoGraphicMapping.domain.DemoGraphicMappingTable;
import com.adopt.apigw.modules.DemoGraphicMapping.model.DemoGraphicMappingDTO;
import com.adopt.apigw.modules.DemoGraphicMapping.repository.DemoGraphicMappingRepository;
import com.adopt.apigw.service.radius.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DemoGraphicMappingService extends AbstractService<DemoGraphicMappingTable, DemoGraphicMappingDTO, Long> {

    @Autowired
    private DemoGraphicMappingRepository demoGraphicMappingRepository;

    @Override
    protected JpaRepository<DemoGraphicMappingTable, Long> getRepository() {
        return demoGraphicMappingRepository;
    }

    public List<DemoGraphicMappingTable> getAll(){
return demoGraphicMappingRepository.findAll();
    }

}
