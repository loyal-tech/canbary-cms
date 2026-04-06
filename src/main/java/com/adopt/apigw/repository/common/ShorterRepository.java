package com.adopt.apigw.repository.common;


import com.adopt.apigw.model.common.Shorter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShorterRepository extends CrudRepository<Shorter, Long> {
    Optional<Shorter> findByHash(String hash);

    @Query("SELECT s FROM Shorter s WHERE s.custId = :custId AND s.ishashused = false ORDER BY s.id DESC")
    List<Shorter> findLatestShorterByCustIdAndIshashusedIsFalse(@Param("custId") Integer custId);

}
