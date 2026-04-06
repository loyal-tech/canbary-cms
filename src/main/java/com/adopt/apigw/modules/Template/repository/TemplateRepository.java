package com.adopt.apigw.modules.Template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Template.domain.Template;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template,Long> {
    public List<Template> findAllByTypeAndStatus(String type, String status);
    public List<Template> findAllByStatus(String status);

    @Query(value = "Select * from TBLMTEMPLATE where templateName=:templateName", nativeQuery = true)
    Optional<Template> findByTemplateName(@Param("templateName") String templateName);
}
