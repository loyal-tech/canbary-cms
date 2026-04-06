package com.adopt.apigw.modules.Cas.Domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface CasPackageMappingRepository extends JpaRepository<CasPackageMapping, Long> {

    @Modifying
    void deleteByCasMasterId(Long cpmappingid);
}