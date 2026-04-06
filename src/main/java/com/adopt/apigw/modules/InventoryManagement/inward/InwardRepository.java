package com.adopt.apigw.modules.InventoryManagement.inward;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface InwardRepository extends JpaRepository<Inward, Long>, QuerydslPredicateExecutor<Inward> {

	Inward findByRmsInwardId(String rmsInwardId);

	//List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(String inwardNumber, List<Long> destinationIds, String destinationType);
	List<Inward> findAllByinwardNumberContainingIgnoreCaseAndDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(String inwardNumber,List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);

	@Query(nativeQuery = true, value = "select t.inward_id from adoptconvergebss.tbltinward t where t.outward_id =:id")
	Integer findInwardIdByOutwardId(@Param("id") Integer id);

	@Query(nativeQuery = true, value = "select * from adoptconvergebss.tbltinward t where t.product_id =:id")
	List<Inward> findAllByProductId(@Param("id") Integer id);
	@Query(value = "select sum(tbl.tab) from(\n" +
			"select count(*) as tab from tbltoutward t1 where t1.inward_id =:id and t1.is_deleted =false\n" +
			"union all\n" +
			"select count(*) as tab from tblmcustomer_inventory_mapping t2 where t2.inward_id =:id and t2.is_deleted =false\n" +
			"union all\n" +
			"select count(*) as tab from tblnetworkdevices t3 where t3.inward_id =:id and t3.is_deleted =false\n" +
			")tbl",nativeQuery = true)
	Integer deleteVerify(@Param("id") Integer id);
	Page<Inward> findAllByIdIn(List<Long> ids, Pageable pageable);
	//List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalse(List<Long> destinationIds, String destinationType);
	List<Inward> findAllByDestinationIdInAndDestinationTypeAndIsDeletedIsFalseAndMvnoIdIn(List<Long> destinationIds, String destinationType, List<Integer> mvno_ids);
	Page<Inward> findAllByinwardNumberContainingIgnoreCaseAndIsDeletedIsFalse(String inwardNumber, Pageable pageable);

	 Inward findTopByOrderByIdDesc();


	@Query(value = "select count(*) as tab from tbltinward t  where t.destination_id =:warehouseid" ,nativeQuery = true)
	Integer deleteVerifyWareHouse(@Param("warehouseid")Integer warehouseId);

	List<Inward> findAllByIdIn(List<Long> ids);

}
