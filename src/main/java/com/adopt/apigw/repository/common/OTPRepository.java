package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface OTPRepository extends JpaRepository<OTP,Long>, QuerydslPredicateExecutor<OTP> {
    @Modifying
    @Query(value = "update tbltotp u set u.generated_time= :generatedDate, u.valid_till_time = :updatedValidTillTime where u.otp= :otp",nativeQuery = true)
    int updateValidTillTime(@Param("generatedDate") ZonedDateTime generatedDate, @Param("updatedValidTillTime") ZonedDateTime updatedValidTillTime, @Param("otp") String otp);

    @Query(value = "select * from tbltotp t where t.otp =:otp",nativeQuery = true)
    List<OTP> getOTPListByOtp(@Param("otp") String otp);


    @Query(value = "select * from tbltotp t where t.mobile_email =:mobileEmail and t.status =:otpStatus and t.otp =:otp and t.valid_till_time > :currentTime order by t.id desc",nativeQuery = true)
    List<OTP> validateOtp(@Param("mobileEmail") String mobileEmail , @Param("otpStatus") String otpStatus , @Param("currentTime") ZonedDateTime currentTime , @Param("otp") String otp);

}
