package com.adopt.apigw.modules.Communication.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.adopt.apigw.modules.Communication.domain.Communication;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface CommunicationRepository extends JpaRepository<Communication, Long> {
    @Transactional
    @Modifying
    @Query(value = "UPDATE communication p set is_sended =:is_sended , error =:error  where p.uuid =:uuid",
            nativeQuery = true)
    void updateCommunication(@Param("uuid") String uuid, @Param("is_sended") Boolean is_sended, @Param("error") String error);

    @Query(value = "SELECT * FROM communication c WHERE c.is_sended = FALSE and error IS NULL;",
            nativeQuery = true)
    List<Communication> getBySendedAndError();
}
