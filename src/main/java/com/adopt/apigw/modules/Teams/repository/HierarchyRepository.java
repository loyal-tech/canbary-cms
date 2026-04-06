package com.adopt.apigw.modules.Teams.repository;

import com.adopt.apigw.model.postpaid.Tax;
import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface HierarchyRepository extends JpaRepository<Hierarchy, Long>, QuerydslPredicateExecutor<Hierarchy> {

    Hierarchy findByEventName(String eventName);

    @Query(value = "select count(*) from tblmhierarchy c where c.event_id=:eventId and c.is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("eventId") Integer eventId);


    @Query(value = "select count(*) from tblmhierarchy c where c.event_id=:eventId and c.is_deleted=false and mvno_id in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("eventId") Integer eventId, @Param("mvnoIds") List mvnoIds);

    @Query(value = "select count(*) from tblmhierarchy c where c.event_id=:eventId and c.is_deleted=false and mvno_id in :mvnoIds AND c.BUID in :buIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("eventId") Integer eventId, @Param("mvnoIds") List mvnoIds, @Param("buIds") List buIds);


    @Query(value = "select * from tblmhierarchy where lower(hierarchyname) like '%' :search  '%' order by id AND mvno_id in :MVNOIDS",
            countQuery = "select count(*) from tblmhierarchy where lower(hierarchyname) like '%' :search '%' AND mvno_id in :MVNOIDS",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOIDS") List MVNOIDS);

    @Query(value = "select * from tblmhierarchy where lower(hierarchyname) like '%' :search  '%' order by id AND mvno_id in :MVNOIDS AND BUID in :buIds",
            countQuery = "select count(*) from tblmhierarchy where lower(hierarchyname) like '%' :search '%' AND mvno_id in :MVNOIDS AND BUID in :buIds",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable, @Param("MVNOIDS") List MVNOIDS, @Param("buIds") List buIds);

    @Query(value = "select * from tblmhierarchy where lower(hierarchyname) like '%' :search  '%' order by id",
            countQuery = "select count(*) from tblmhierarchy where lower(hierarchyname) like '%' :search '%'",
            nativeQuery = true)
    Page<Tax> searchEntity(@Param("search") String searchText, Pageable pageable);

    @Query(value = "select * from tblmhierarchy c where c.is_deleted=:isDelete AND c.BUID in :buId AND lcoid=:lcoId", nativeQuery = true)
    List<Hierarchy> findAllByIsDeletedAndBuIdIn(@Param("isDelete") boolean isDelete,@Param("buId") List<Long> buId,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblmhierarchy c where c.is_deleted=:isDelete AND c.BUID in :buId AND lcoid IS NULL", nativeQuery = true)
    List<Hierarchy> findAllByIsDeletedAndBuIdIn(@Param("isDelete") boolean isDelete,@Param("buId") List<Long> buId);

    @Query(value = "select * from tblmhierarchy c where c.is_deleted=:isDelete AND c.BUID IS NULL AND lcoid IS NULL", nativeQuery = true)
    List<Hierarchy> findAllByIsDeletedAndBuIdIsNull(@Param("isDelete") boolean isDelete);

    @Query(value = "select * from tblmhierarchy c where c.is_deleted=:isDelete AND c.BUID IS NULL AND lcoid=:lcoId", nativeQuery = true)
    List<Hierarchy> findAllByIsDeletedAndBuIdIsNull(@Param("isDelete") boolean isDelete,@Param("lcoId") Integer lcoId);

    boolean existsByMvnoIdInAndBuIdInAndEventNameAndIsDeleted(List<Integer> mvnoId, List<Long> buId, String eventName, boolean isDelete);

    //For admin and superadmin
    boolean existsByBuIdIsNullAndMvnoIdInAndEventNameAndIsDeleted(List<Integer> mvnoId, String eventName, boolean isDelete);

    Hierarchy findByMvnoIdAndBuIdInAndEventNameAndIsDeleted(Integer mvnoId, List<Long> buId, String eventName, boolean isDelete);

    Hierarchy findByBuIdIsNullAndMvnoIdAndEventNameAndIsDeleted(Integer mvnoId, String eventName, boolean isDelete);

    @Query(value = "select t.event_name from tblmhierarchy t where t.id in (:ids)", nativeQuery = true)
    List<String> findAllEventNamesByHierarchIds(@Param("ids") List<Long> ids);


}
