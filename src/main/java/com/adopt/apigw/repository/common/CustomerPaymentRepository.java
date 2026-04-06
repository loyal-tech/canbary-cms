package com.adopt.apigw.repository.common;

import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.model.postpaid.Tax;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerPaymentRepository extends JpaRepository<CustomerPayment, Long>, QuerydslPredicateExecutor<CustomerPayment> {
    List<CustomerPayment> findAllByPgTransactionId(String id);

    List<CustomerPayment> findAllByOrderIdAndStatusContainingIgnoreCase(Long orderId , String name);


    List<CustomerPayment> findCustomerPaymentByCustId(Integer custId);

    List<CustomerPayment> findCustomerPaymentByPartnerId(Integer custId);



    @Query(nativeQuery = true,value = "select * from tbltpayment t1 where  t1.MVNOID in :MVNOIDS"
            ,countQuery = "select count(*) from tbltpayment t1 where t1.MVNOID in :MVNOIDS")
    Page<CustomerPayment> findAll(Pageable pageable, @Param("MVNOIDS") List MVNOIDS);

    @Query(nativeQuery = true,value = "select * from tbltpayment t1 where (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))"
            ,countQuery = "select count(*) from tbltpayment t1 where  (t1.MVNOID = 1 or (t1.MVNOID = :mvnoId and t1.BUID in :buIds))")
    Page<CustomerPayment> findAll(Pageable pageable, @Param("mvnoId") Integer mvnoId, @Param("buIds") List buIds);

    // Method 1: findAllByCustomerUsernameAndOrderId
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2")
    Page<CustomerPayment> findAllByCustomerUsernameAndOrderId(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable);

    // Method 2: findAllByCustomerUsernameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // Method 3: findAllByCustomerUsernameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') and t1.orderid = :s2 " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidInAndBuidIn(@Param("s1") String s1, @Param("s2") String s2, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    // New Method 1: findAllByCustomerUsername
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByCustomerUsername(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByCustomerUsernameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByCustomerUsernameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.customer_user_name like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByCustomerUsernameAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);



    // serach using status


    // New Method 1: findAllByStatus
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByStatus(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByStatusAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByStatusAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByStatusAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.status like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.status like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByStatusAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);



    //find all by orderid


    // New Method 1: findAllByOrderid
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByOrderid(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByOrderidAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByOrderidAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByOrderIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.orderid like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.orderid like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByOrderidAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);


    //find all by merchantname


    // New Method 1: findAllByMerchantName
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByMerchantName(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByMerchantNameAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByMerchantNameAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByMerchantNameAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.merchant_name like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.merchant_name like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByMerchantNameAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);

    CustomerPayment findByCustIdAndAndOrderId(Integer custId,Long orderId);

    CustomerPayment findByOrderId(Long orderId);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearch(@Param("s1") String s1, Pageable pageable);

    // New Method 2: findAllByPgTransactIdAndMvnoidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearchAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByPgTransactIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.pgtransactionid like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByPgTransactionIdWithSearchAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);


    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%')",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%')")
    Page<CustomerPayment> findAllByAccountNumberWithSearch(@Param("s1") String s1, Pageable pageable);

    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%') " +
            "and t1.MVNOID in (:mvnoids)",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%') " +
                    "and t1.MVNOID in (:mvnoids)")
    Page<CustomerPayment> findAllByAccountNumberWithSearchAndMvnoidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoids") List<Integer> mvnoids);

    // New Method 3: findAllByPgTransactIdAndMvnoidInAndBuidIn
    @Query(nativeQuery = true, value = "select * from tbltpayment t1 " +
            "where t1.account_number like CONCAT('%', :s1, '%') " +
            "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))",
            countQuery = "select count(*) from tbltpayment t1 " +
                    "where t1.account_number like CONCAT('%', :s1, '%') " +
                    "and (t1.MVNOID = 1 or (t1.MVNOID = :mvnoid and t1.BUID in (:buids)))")
    Page<CustomerPayment> findAllByAccountNumberWithSearchAndMvnoidInAndBuidIn(@Param("s1") String s1, Pageable pageable, @Param("mvnoid") Integer mvnoid, @Param("buids") List<Long> buids);
}
