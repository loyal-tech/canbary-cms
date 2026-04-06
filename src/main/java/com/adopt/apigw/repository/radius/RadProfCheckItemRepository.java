package com.adopt.apigw.repository.radius;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.model.radius.RadiusProfile;
import com.adopt.apigw.model.radius.RadiusProfileCheckItem;

import java.util.List;

//@JaversSpringDataAuditable
@Repository
public interface RadProfCheckItemRepository extends JpaRepository<RadiusProfileCheckItem, Integer> {

    @Query(value = "delete from tblradiusprofilereplyitm where radiuscheckitmid = :search",
            nativeQuery = true)
    void deleteReplteItems(@Param("search") Integer id);

    List<RadiusProfileCheckItem> findAllByRadiusProfileAndIsDeletedFalse(RadiusProfile radiusProfile);
}
