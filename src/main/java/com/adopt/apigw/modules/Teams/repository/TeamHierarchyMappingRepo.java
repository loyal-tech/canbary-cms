package com.adopt.apigw.modules.Teams.repository;

import com.adopt.apigw.modules.Teams.domain.Hierarchy;
import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamHierarchyMappingRepo extends JpaRepository<TeamHierarchyMapping,Integer>, QuerydslPredicateExecutor<TeamHierarchyMapping> {
//    Long findById(List<Long> ids);

    TeamHierarchyMapping findByOrderNumberAndHierarchyId(Integer orderNumber, Integer hierarchyId);

    List<TeamHierarchyMapping> findByTeamId(Integer teamidlist);
    List<TeamHierarchyMapping> findAllByHierarchyId(Integer hierarchy_id);
    @Query(value = "SELECT t.hierarchy_id \n" +
            "FROM adoptconvergebss.tbltteamhierarchymapping t \n" +
            "JOIN adoptconvergebss.tblmhierarchy t2 \n" +
            "ON t.hierarchy_id = t2.id \n" +
            "WHERE t.team_action = :actionName \n" +
            "AND t.is_deleted = FALSE  and t2.is_deleted= FALSE \n" +
            "AND t2.mvno_id = :mvnoId", nativeQuery = true)
    List<Long> findHierarchyIdByActionNameAndMvnoId(@Param("actionName") String actionName,@Param("mvnoId") Integer mvnoId);

    @Query(value = "SELECT t.hierarchy_id \n" +
            "FROM adoptconvergebss.tbltteamhierarchymapping t \n" +
            "JOIN adoptconvergebss.tblmhierarchy t2 \n" +
            "ON t.hierarchy_id = t2.id \n" +
            "WHERE t.team_action = :actionName \n" +
            "AND t.is_deleted = FALSE \n" +
            "AND t2.mvno_id = :mvnoId  and t2.is_deleted= FALSE \n" +
            "AND t2.BUID IN = :buIds)", nativeQuery = true)
    List<Long> findHierarchyIdByActionNameAndMvnoIdAndBuIds(@Param("actionName") String actionName,@Param("mvnoId") Integer mvnoId,@Param("buIds") Integer buIds);

}
