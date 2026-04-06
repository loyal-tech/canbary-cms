package com.adopt.apigw.modules.childcustomer.parentchildmappingrel;

import org.apache.kafka.common.protocol.types.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentChildMappingRepo extends JpaRepository<ParentChildMappingRel,Long> {
    List<ParentChildMappingRel> findByChildUsernameAndMvno(String userName , Long mvnoId);

    List<ParentChildMappingRel> findAllById(Long id);
    List<ParentChildMappingRel> findByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(String username,Long parentId,Long mvnoId);
    Long countByChildUsernameAndParentCustomerAndMvnoAndIsDeleteIsFalse(String username, Long parentId, Long mvnoId);



    List<ParentChildMappingRel> findAllByChildCustomer(Long id);
    List<ParentChildMappingRel> findAllByChildCustomerAndIsDelete(Long id,Boolean isDelete);

    Optional<ParentChildMappingRel> findByChildUsernameAndMvnoAndIsparent(String userName , Long mvnoId, Boolean isParent);

    @Query("select c from ParentChildMappingRel c where c.parentCustomer =:parentId AND c.isDelete = false")
    List<ParentChildMappingRel> findByparentCustomer(Long parentId);

    @Query("select c from ParentChildMappingRel c where c.parentCustomer =:parentId AND c.childUsername =:username AND c.childMobile =:mobileNumber AND c.isDelete = false")
    List<ParentChildMappingRel> findByparentCustomerAndMobileNumberAndUsername(Long parentId,String mobileNumber , String username);

    @Query("select c from ParentChildMappingRel c where c.childUsername =:username AND c.childMobile =:mobileNumber AND c.isDelete = false")
    List<ParentChildMappingRel> findByMobileNumberAndUsername(String mobileNumber , String username);

    @Query("SELECT COUNT(DISTINCT p.parentCustomer) FROM ParentChildMappingRel p " +
            "WHERE p.childUsername = :username AND p.mvno = :mvnoId")
    Long countDistinctParentsForUser(@Param("username") String username, @Param("mvnoId") Long mvnoId);

    List<ParentChildMappingRel> findByChildMobile(String mobile);

    List<ParentChildMappingRel> findByChildMobileAndMvnoIn(String mobile,List<Long> mvnoIds);

    @Query("SELECT c.id, c.childUsername, c.parentAccountNumber, c.childMobile, c.isDelete,c.status,c.parentCustomer " +
            "FROM ParentChildMappingRel c WHERE c.childUsername = :username AND c.mvno = :mvnoId AND c.isparent = :isParent AND c.isDelete = false")
    Object[] findBasicParentChildInfoByUserNameAndMvnoIdAndIsParent(@Param("username") String username, @Param("mvnoId") Long mvnoId, @Param("isParent") Boolean isParent);

    @Query(value = "SELECT COUNT(*) FROM tblparentchildmappingrel " +
            "WHERE child_username = :username " +
            "AND mvno_id IN (:mvnoIds)", nativeQuery = true)
    Integer countByUsernameAndMvnoNative(@Param("username") String username, @Param("mvnoIds") List<Long> mvnoIds);

    @Query(value = " SELECT * FROM tblparentchildmappingrel t WHERE t.parent_cust_id = :custId AND t.mvno_id = :mvnoId AND t.isparent = true", nativeQuery = true)
    Optional<ParentChildMappingRel> findLatestParentMapping(
            @Param("custId") Integer custId,
            @Param("mvnoId") Integer mvnoId
    );

}
