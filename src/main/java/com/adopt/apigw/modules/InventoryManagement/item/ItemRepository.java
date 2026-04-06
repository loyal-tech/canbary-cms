package com.adopt.apigw.modules.InventoryManagement.item;

import com.adopt.apigw.modules.InventoryManagement.inward.Inward;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item> {
	Page<Item> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalse(String name, Pageable pageable);
	Page<Item> findAllByNameContainingIgnoreCaseAndIsDeletedIsFalseAndMvnoIdIn(String name, Pageable pageable, List mvnoIds);
	@Query(value = "select count(*) from tblmserializeditem m where m.name=:name and m.is_deleted=false",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name);

	@Query(value = "select count(*) from tblmserializeditem m where m.name=:name and m.is_deleted=false and mvno_id in :mvnoIds",nativeQuery = true)
	Integer duplicateVerifyAtSave(@Param("name")String name, @Param("mvnoIds") List mvnoIds);

	@Query(value = "select count(*) from tblmserializeditem where id =:id and is_deleted=false " ,nativeQuery = true)
	Integer deleteVerify(@Param("id")Integer id);

	@Query(value = "select count(*) from tblmserializeditem t where t.id =:id and t.name =:name and t.is_deleted =false", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id);

	@Query(value = "select count(*) from tblmserializeditem t where t.id =:id and  t.name =:name and t.is_deleted =false and mvno_id in :mvnoIds", nativeQuery = true)
	Integer duplicateVerifyAtEdit(@Param("name")String name, @Param("id") Integer id, @Param("mvnoIds") List mvnoids);

	//List<Item> findAllByCurrentInwardIdAndProductId(Long inwardId, Long productId);

	@Query(value = "select * from tblmserializeditem t where t.current_inward_id in :id and t.mac in :mac and t.is_deleted =false", nativeQuery = true)
	List<Item> findByCurrentId(@Param("id") List<Long> id,@Param("mac") List<String> mac);

	@Query(value = "select * from tblmserializeditem t where t.external_item_id in :id and t.mac in :mac and t.is_deleted =false", nativeQuery = true)
	List<Item> findByCurrentExternalItemId(@Param("id") List<Long> id,@Param("mac") List<String> mac);
	List<Item> findByMacAddress(String mac);
	@Query(value = "select * from tblmserializeditem t where t.warranty='InWarranty'",nativeQuery = true)
	List<Item> findBywarranty();

	@Query(value = "select * from tblmserializeditem t where id =:id",nativeQuery = true)
	List<Item> getall(@Param("id") Long id);

 	List<Item> findAllByIdIn(List<Long> id);
	Item findTopByOrderByIdDesc();

	 List<Item> findAllByProductIdIn(List<Long> productId);
	Item findByIsDeletedIsFalseAndMacAddress(String macAddress);
	Item findByIsDeletedIsFalseAndMacAddressAndMvnoIdIn(String macAddress, List mvnoIds);

	List<Item> findAllItemBySerialNumberAndIsDeletedFalse(String deviceSerialNumber);


	@Query(value = "SELECT i.id ,i.serial_number FROM tblmserializeditem i WHERE i.id = :id", nativeQuery = true)
	List<Object[]> findItemIdById(@Param("id") Long id);


    @Query(value = "SELECT serial_number FROM tblmserializeditem t WHERE t.id = :id AND t.is_deleted = false", nativeQuery = true)
    String getSerialNumber(@Param("id") Long id);
	@Query(value = "SELECT serial_number FROM tblmserializeditem t WHERE t.id IN (:id)", nativeQuery = true)
	List<String> getSerialNumberListByItemId(@Param("id") List<Long> id);

}
