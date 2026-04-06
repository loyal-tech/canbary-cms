package com.adopt.apigw.modules.TumilIdValidation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IdValidationRepository extends JpaRepository<IdValidationResponse,Integer> {

    @Query(value = "select * from tblmhhidvalidation t where t.email = :email and t.password = :password and t.mvno_id =:mvnoId  and t.is_deleted = false", nativeQuery = true)
    IdValidationResponse getByEmailAndPasswordAndMvnoId(@Param("email") String email, @Param("password") String password, @Param("mvnoId") Integer mvnoId);

    boolean existsByHouseholdIdAndIsDeletedFalse(String householdId);
}
