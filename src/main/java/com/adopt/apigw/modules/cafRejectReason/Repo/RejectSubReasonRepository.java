package com.adopt.apigw.modules.cafRejectReason.Repo;

import com.adopt.apigw.modules.cafRejectReason.Entity.RejectSubReason;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@JaversSpringDataAuditable
public interface RejectSubReasonRepository extends JpaRepository<RejectSubReason, Long>{

}
