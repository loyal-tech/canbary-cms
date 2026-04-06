package com.adopt.apigw.modules.Reseller.repository;

import com.adopt.apigw.modules.Reseller.domain.Reseller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResellerRepository extends JpaRepository<Reseller, Long>, QuerydslPredicateExecutor<Reseller> {
	List<Reseller> findByDistributerId(Long distributerId);
	Optional<Reseller> findByUsernameAndMvnoId(String username, Long mvnoId);
	Integer countByLocationMasterLocationMasterId(Long locationId);
}
