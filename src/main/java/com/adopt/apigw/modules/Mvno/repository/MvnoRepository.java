package com.adopt.apigw.modules.Mvno.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.adopt.apigw.modules.Mvno.domain.Mvno;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MvnoRepository extends JpaRepository<Mvno, Long> {

    Page<Mvno> findAll(Pageable pageable);
    @Query("select t.name from Mvno t where t.id=:id")
    String findMvnoNameById(Long id);

    @Modifying
    @Query(value = "CALL updates_mvnoid(:oldMvnoid, :newMvnoid)", nativeQuery = true)
    void UpdateMvnoidISP(@Param("oldMvnoid") Integer oldMvnoid, @Param("newMvnoid") Integer newMvnoid);
    @Query("select t.id from Mvno t where t.name=:mvnoName")
    Integer findMvnoIdByName(String mvnoName);


//    @Query("select t.id from Mvno t where t.ispBillDay=:ispDay")
//    List<Integer> findGenerationdayformonthly(Integer ispDay);
    @Query("SELECT t.id FROM Mvno t WHERE t.billType = 'Monthly' AND t.ispBillDay = :ispDay")
    List<Integer> findGenerationdayformonthly(@Param("ispDay") Integer ispDay);


    @Query("SELECT t.id FROM Mvno t WHERE t.billType = 'Bi-Monthly' AND (t.ispBillDay = :ispDay)")
    List<Integer> findGenerationdayforBiMonthly(@Param("ispDay") Integer ispDay);



    @Query("select t.ispCommissionPercentage from Mvno t where t.id=:id")
    Integer findispCommissionBymvnoId(Long id);

    @Query("SELECT t.id FROM Mvno t WHERE t.ispBillDay IS NOT NULL")
    List<Integer> findGenerationdayofAllMvno();

    List<Mvno>findAllByCustInvoiceRefIdNotNullAndStatusEquals(String status);

    @Query(nativeQuery = true,value = "select t.profile_id from tblmmvno t where t.MVNOID = :id")
    Optional<Long> findProfileIdByMvnoId(Long id);



    @Query("SELECT DISTINCT t.ispBillDay FROM Mvno t WHERE t.ispBillDay = 2")
    Integer findIspDay();


    @Query("SELECT m.threshold FROM Mvno m WHERE m.id = :mvnoId")
    Long findThresholdById(@Param("mvnoId") Long mvnoId);

    @Query("select distinct t.id from Mvno t")
    List<Long> findAllMvnoId();
}
