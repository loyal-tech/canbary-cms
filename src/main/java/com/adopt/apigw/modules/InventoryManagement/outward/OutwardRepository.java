package com.adopt.apigw.modules.InventoryManagement.outward;

import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.tickets.domain.Case;

import java.util.List;

@Repository
public interface OutwardRepository  extends JpaRepository<Outward, Long>, QuerydslPredicateExecutor<Outward> {
	Page<Outward> findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(String outwardNumber, Pageable pageable);
	List<Outward> findAllByoutwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(String outwardNumber,List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);
	@Query(value = "select * from tbltoutward t WHERE t.is_delete = false", nativeQuery = true
            , countQuery = "select count(*) from tbltoutward t WHERE t.is_delete = false")
    Page<Outward> findAll(Pageable pageable);
	Page<Outward> findAllByoutwardNumberContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String outwardNumber, Pageable pageable, List mvnoIds);

//	@Query(value = 	"select count(*) from tbltoutward t1 where t1.inward_id =:id and t1.is_deleted =false", nativeQuery = true)
//	Integer deleteVerify(@Param("id") Integer id);

	Page<Outward> findAllByIdIn(List<Long> ids, Pageable pageable);
	List<Outward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);

	Outward findTopByOrderByIdDesc();

//	Outward findByRequestInventoryProductId(Long id);

	List<Outward> findAllByIdIn(List<Long> ids);
}
