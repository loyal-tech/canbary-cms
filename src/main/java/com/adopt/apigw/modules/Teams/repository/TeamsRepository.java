package com.adopt.apigw.modules.Teams.repository;

import com.adopt.apigw.modules.Teams.domain.TeamHierarchyMapping;
import com.adopt.apigw.modules.role.domain.Role;
import io.swagger.models.auth.In;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Teams.domain.Teams;

import java.util.List;

@Repository
public interface TeamsRepository extends JpaRepository<Teams, Long>, QuerydslPredicateExecutor<Teams> {

    List<Teams>findAllByIdIn(List<Long> ids);


    @Query(value = "SELECT * FROM tblteams t where t.is_deleted = false AND lcoid IS NULL"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblteams t where t.is_deleted = false AND lcoid IS NULL")
    Page<Teams> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM tblteams t where t.is_deleted = false AND lcoid=:lcoId"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblteams t where t.is_deleted = false AND lcoid=:lcoId")
    Page<Teams> findAll(Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(value = "SELECT * FROM tblteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid IS NULL"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid IS NULL")
    Page<Teams> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "SELECT * FROM tblteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid=:lcoId"
            , nativeQuery = true
            , countQuery = "SELECT count(*) FROM tblteams t where t.is_deleted = false and MVNOID in :mvnoIds AND lcoid=:lcoId")
    Page<Teams> findAll(Pageable pageable, @Param("mvnoIds")List mvnoIds,@Param("lcoId") Integer lcoId);

    List<Teams> findAllByIdInAndIsDeletedIsFalse(List<Long> id);

    @Query(value = "select * from tblteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid IS NULL", nativeQuery = true
            , countQuery = "select count(*) from tblteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid IS NULL")
    Page<Teams> findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(@Param("s1") Integer partnerid,Pageable pageable, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select * from tblteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid=:lcoId", nativeQuery = true
            , countQuery = "select count(*) from tblteams t where t.is_deleted = false and t.partnerid = :s1 and MVNOID in :mvnoIds AND lcoid=:lcoId")
    Page<Teams> findAllByPartner_IdAndIsDeletedIsFalseAndMvnoIdIn(@Param("s1") Integer partnerid,Pageable pageable, @Param("mvnoIds")List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid IS NULL", countQuery = "select count(*) from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid IS NULL", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    @Query(value = "select * from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid=:lcoId", countQuery = "select count(*) from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and t.lcoid=:lcoId", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid=:lcoId", countQuery = "select count(*) from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid=:lcoId", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds")List mvnoIds,@Param("lcoId") Integer lcoId);

    @Query(value = "select * from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid IS NULL", countQuery = "select count(*) from tblteams t \n" +
            "where (t.team_name like '%' :s1 '%' or t.team_status like '%' :s2 '%') \n" +
            "and t.is_deleted = 0 and MVNOID in :mvnoIds and t.lcoid IS NULL", nativeQuery = true)
    Page<Teams> findAllBy(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoIds")List mvnoIds);


    @Query(value="SELECT count(*) FROM tblteams t where t.parentteamid = :parentTeamId",nativeQuery = true)
	Long checkTeamIsAlreadyParentTeam(@Param("parentTeamId") Long parentTeamId);

	Teams findByParentTeams(Teams teams);
	
	@Query(value = "select count(*) from tblteams where team_name=:name and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblteams where team_name=:name and team_id =:id and is_deleted=false and MVNOID in :mvnoIds", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id, @Param("mvnoIds")List mvnoIds);

    @Query(value = "select count(*) from tblteams where team_name=:name and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtSave(@Param("name") String name);

    @Query(value = "select count(*) from tblteams where team_name=:name and team_id =:id and is_deleted=false", nativeQuery = true)
    Integer duplicateVerifyAtEdit(@Param("name") String name, @Param("id") Long id);

    List<Teams> findAllByNameContainingIgnoreCase(String name);

    @Query(value = "select team_id from tblteamusermapping where staffid =:staffid" , nativeQuery = true)
    List<Long> findAllByStaff(@Param("staffid") Integer staffid);

    @Query(value = "select t.name from Teams t where t.id IN (:teamids)")
    List<String> findRolenameByrolrids(List<Long> teamids);
    @Query(value = "select t.name  from Teams t where t.id= :id")
    String findTeamNameById(@Param("id") Long id);

}
