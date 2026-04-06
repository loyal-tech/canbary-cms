package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemAssemblyRepo extends JpaRepository<ItemAssembly,Long> , QuerydslPredicateExecutor<ItemAssembly> {

    @Query(value = "select count(*) from tblmitemassembly m where m.itemassembly_name =:name and m.is_deleted =false",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name);

    @Query(value = "select count(*) from tblmitemassembly m where m.itemassembly_name =:name and m.is_deleted =false and mvno_id in :mvnoIds",nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmitemassembly t where id=:id and is_deleted=false " ,nativeQuery = true)
    Integer deleteVerify(@Param("id")Integer id);
}
