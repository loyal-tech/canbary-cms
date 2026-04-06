package com.adopt.apigw.modules.childcustomer.repository;

import com.adopt.apigw.modules.childcustomer.entity.ChildCustomer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChildCustomerRepo extends JpaRepository<ChildCustomer,Long> {

    @Query(value = "select count(*) from tblchildcustomer t where t.user_name=:name and t.isdeleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblchildcustomer t where t.user_name=:name and t.isdeleted=false and mvno_Id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds") List mvnoIds);

    List<ChildCustomer> findByParentCustIdAndMvnoId(Long id, Long mvnoIds);
    long countByParentCustIdAndMvnoId(Long id, Long mvnoIds);
    List<ChildCustomer> findAllByMvnoIdIn(List<Long> mvnoIds);

    Optional<ChildCustomer> findByUserNameAndMvnoIdIn(String userName , List<Long> mvnoId);
    Optional<ChildCustomer> findByUserNameAndMvnoIdInAndIsdeleted(String userName , List<Long> mvnoId,Boolean isdelete);
    List<ChildCustomer> findByUserName(String userName);
    Page<ChildCustomer> findAllByMvnoIdAndIsdeletedOrderByIdDesc( Long mvnoId , Boolean isDelete,Pageable pageRequest );
    Optional<ChildCustomer> findByIdAndMvnoId(Long userName , Long mvnoId);
    Optional<ChildCustomer> findByParentCustIdAndUserNameAndMvnoId(Long parentCustId ,String userName , Long mvnoId);
    @Query("SELECT new ChildCustomer (c.id, c.userName, c.password) " +
            "FROM ChildCustomer c " +
            "WHERE c.mvnoId in :mvnoId AND c.parentCustId = :parentCustId")
    List<ChildCustomer> findBasicInfo(@Param("parentCustId") Long parentCustId,
                                             @Param("mvnoId") List<Long> mvnoIds);


    @Query("SELECT c FROM ChildCustomer c WHERE c.mobileNumber = :mobileNumber and c.isdeleted = false")
    List<ChildCustomer> findChildrenByMobileNumber(String  mobileNumber);

    @Query("SELECT c FROM ChildCustomer c WHERE c.mobileNumber = :mobileNumber and c.isdeleted = false and c.status = 'Active' and c.mvnoId =:mvnoId")
    List<ChildCustomer> findChildrenByMobileNumberAndMvnoId(@Param("mobileNumber") String mobileNumber,@Param("mvnoId") Long mvnoId);

    List<ChildCustomer> findAllByUserNameAndMobileNumberAndMvnoId(String username , String mobile , Long mvnoID);


    @Query("SELECT c.id, c.userName, c.firstName, c.lastName, c.status, c.email, c.mobileNumber, c.isdeleted, c.password, c.parentAccountNumber " +
            "FROM ChildCustomer c WHERE c.userName = :userName AND c.mvnoId = :mvnoId")
    Object[] findBasicChildCustomerInfoByUserNameAndMvnoId(@Param("userName") String userName, @Param("mvnoId") Long mvnoId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblchildcustomer SET isdeleted = false WHERE user_name = :username AND mvno_id = :mvnoId", nativeQuery = true)
    void updateIsDeleteFalseByUsernameAndMvnoId(@Param("username") String username, @Param("mvnoId") Long mvnoId);
    Optional<ChildCustomer> findTopByParentCustIdAndMvnoIdOrderByIdAsc(Long parentCustId, Long mvnoId);


}
