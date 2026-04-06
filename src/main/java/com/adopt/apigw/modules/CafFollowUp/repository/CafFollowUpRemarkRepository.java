package com.adopt.apigw.modules.CafFollowUp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.CafFollowUp.domain.CafFollowUpRemark;

@Repository
public interface CafFollowUpRemarkRepository extends JpaRepository<CafFollowUpRemark, Long>{

	@Query(name = "select * from TBLTCAFFOLLOWUPREMARK where caf_follow_up_id=:cafFollowUpId")
	List<CafFollowUpRemark> findByCafFollowUpId(@Param("cafFollowUpId") Long cafFollowUpId);
}
