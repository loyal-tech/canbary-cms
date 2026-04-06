package com.adopt.apigw.modules.CommonList.repository;

import com.adopt.apigw.modules.CommonList.domain.CommonList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommonListRepository extends JpaRepository<CommonList, Long>, QuerydslPredicateExecutor<CommonList> {
    List<CommonList> findAllByTypeAndStatusOrderByValueAsc(String type, String status);

    CommonList findByValue(String value);

    List<CommonList> findAllByTypeAndValue(String type, String value);

    List<CommonList> findAllByStatus(String status);

    List<CommonList> findAllByTypeAndStatusAndValue(String type, String status, String value);

    List<CommonList> findAllByTypeInAndStatus(List<String> type, String status);

    //List<CommonList> findByValueInAndType(List<String> reasonIdStrings, String deactivateReasonEzBill);

    List<CommonList> findAllByType(String deactivateReasonEzBill);

    @Query(value = "select * from tblcommonlist t where t.list_type = 'DEACTIVATE_REASON_EZ_BILL' and t.list_value in :reasonIds" , nativeQuery = true)
    List<CommonList>  findAllByDeactiveReason(@Param("reasonIds") List<String> reasonIds);

    @Query(value = "select t.list_text from tblcommonlist t where t.list_type = 'DEACTIVATE_REASON_EZ_BILL' and t.list_value  =:reasonId" , nativeQuery = true)
    String findByReasonId(@Param("reasonId") int reasonId);

    @Query(value = "select t.list_text from tblcommonlist t where t.list_type = 'DEACTIVATE_REASON_EZ_BILL' and t.list_value  =:reasonId" , nativeQuery = true)
    CommonList findCategoryReasonById(@Param("reasonId")int reasonId);
}
