package com.adopt.apigw.modules.BranchService.repository;
import com.adopt.apigw.modules.BranchService.model.BranchServiceMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface BranchServiceMappingRepository extends JpaRepository<BranchServiceMappingEntity , Long> {
    List<BranchServiceMappingEntity> findAllByBranchId(Long branchId);

}
