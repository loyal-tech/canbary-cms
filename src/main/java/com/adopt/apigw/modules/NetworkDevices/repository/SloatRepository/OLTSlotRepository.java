package com.adopt.apigw.modules.NetworkDevices.repository.SloatRepository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.adopt.apigw.modules.NetworkDevices.domain.Oltslots;

import javax.transaction.Transactional;
import java.util.List;

public interface OLTSlotRepository extends JpaRepository<Oltslots,Long> {

    List<Oltslots> findAllByNetworkDevices_Id(Long networkDeviceId);

    @Query(value = "select count(*) from tbloltslots t where t.slotname =:name and t.deviceid=:deviceId and t.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("deviceId")Integer deviceId,@Param("name")String name);

    @Query(value = "select count(*) from tbloltslots t where t.slotname =:name and t.deviceid=:deviceId and t.slotid !=:sloatId and t.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("deviceId")Integer deviceId,@Param("name")String name,@Param("sloatId")Integer sloatId);

//    @Modifying
//    @Transactional
//    @Query(value = "update tbloltslots t set t.status ='Inactive' where t.slotid =:id",nativeQuery = true)
//    void inActiveSlot(@Param("id")Integer id);

    @Query(value = "select sum(tal.tab) from\n" +
            "(select count(*) as tab from tblcustomers t2 where t2.oltslotid =:id and t2.is_deleted =false\n" +
            "union all\n" +
            "select count(*) as tab from tbloltportdetails t where t.slotid =:id and t.is_deleted =false\n" +
            ")tal",nativeQuery = true)
    Integer deleteVerifySlot(@Param("id")Integer id);
}
