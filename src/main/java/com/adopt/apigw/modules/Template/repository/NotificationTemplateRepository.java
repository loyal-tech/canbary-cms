package com.adopt.apigw.modules.Template.repository;

import com.adopt.apigw.modules.Pincode.domain.Pincode;
import com.adopt.apigw.modules.Template.domain.Template;
import com.adopt.apigw.modules.Template.domain.TemplateNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface NotificationTemplateRepository extends JpaRepository<TemplateNotification,Long> , QuerydslPredicateExecutor<TemplateNotification> {

    @Query(value = "Select * from TBLNOTIFICATIONEMPLATE  where templateName=:templateName", nativeQuery = true)
    Optional<TemplateNotification> findByTemplateName(@Param("templateName") String templateName);

    Integer countByEventEventId(Long eventId);

    @Query(value = "Select * from TBLNOTIFICATIONEMPLATE  where templateName like '%templateName%'", nativeQuery = true)
    List<TemplateNotification> findByName(@Param("templateName") String templateName);

    List<TemplateNotification> findAllByTemplateNameContainingIgnoreCase(String templateName);



}
