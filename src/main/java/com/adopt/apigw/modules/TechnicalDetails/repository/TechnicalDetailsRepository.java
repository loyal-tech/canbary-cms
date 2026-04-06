package com.adopt.apigw.modules.TechnicalDetails.repository;

import com.adopt.apigw.modules.SubBusinessVertical.Domain.SubBusinessVertical;
import com.adopt.apigw.modules.SubBusinessVertical.Model.SubBusinessVerticalDTO;
import com.adopt.apigw.modules.TechnicalDetails.domain.TechnicalDetails;
import com.adopt.apigw.modules.TechnicalDetails.model.TechnicalDetailsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechnicalDetailsRepository extends JpaRepository<TechnicalDetails,Long>, QuerydslPredicateExecutor<TechnicalDetails> {

}
