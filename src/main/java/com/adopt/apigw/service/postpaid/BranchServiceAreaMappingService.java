package com.adopt.apigw.service.postpaid;

import com.adopt.apigw.model.common.BranchServiceAreaMapping;
import com.adopt.apigw.model.common.QBranchServiceAreaMapping;
import com.adopt.apigw.model.postpaid.BranchServiceAreaMappingPojo;
import com.adopt.apigw.modules.ServiceArea.domain.QServiceArea;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.ServiceArea.repository.ServiceAreaRepository;
import com.adopt.apigw.repository.common.BranchServiceAreaMappingRepository;
import com.adopt.apigw.service.radius.AbstractService;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class BranchServiceAreaMappingService extends AbstractService<BranchServiceAreaMapping, BranchServiceAreaMappingPojo, Long> {

    @Autowired
    BranchServiceAreaMappingRepository branchServiceAreaMappingRepository;
    @Autowired
    ServiceAreaRepository serviceAreaRepository;
    @Override
    protected JpaRepository<BranchServiceAreaMapping, Long> getRepository() {
        return null;
    }

     public List<ServiceArea> findServiceAreaByBranchId(Integer BrnachId) {
         try {
             QBranchServiceAreaMapping qBranchServiceAreaMapping = QBranchServiceAreaMapping.branchServiceAreaMapping;
             BooleanExpression exp = qBranchServiceAreaMapping.isNotNull().and(qBranchServiceAreaMapping.branchId.eq(BrnachId));
             List<BranchServiceAreaMapping> branchServiceAreaMappingList = (List<BranchServiceAreaMapping>) branchServiceAreaMappingRepository.findAll(exp);
             List<Long> number = new ArrayList<>();
             if (branchServiceAreaMappingList.size() > 0) {
                 for (BranchServiceAreaMapping ids : branchServiceAreaMappingList) {
                     Long num = Long.valueOf(ids.getServiceareaId());
                     number.add(num);
                 }
             }
             QServiceArea qServiceArea = QServiceArea.serviceArea;
             BooleanExpression exp1 = qServiceArea.isNotNull().and(qServiceArea.isDeleted.eq(false).and(qServiceArea.id.in(number)));
             List<ServiceArea> serviceAreasList = (List<ServiceArea>) serviceAreaRepository.findAll(exp1);
             List<ServiceArea> serviceArea = new ArrayList<>();
             if (serviceAreasList.size() > 0) {
                 for (ServiceArea serviceAreas : serviceAreasList) {
                     ServiceArea serviceArea1 = serviceAreas;
                     serviceArea.add(serviceArea1);
                 }
             }
             return serviceArea;
         } catch (Exception e) {
             throw new RuntimeException(e);
         }
     }

}

