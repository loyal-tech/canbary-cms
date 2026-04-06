package com.adopt.apigw.repository.tacacs;

import com.adopt.apigw.model.tacacs.AccessLevelGroupTacacs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AccessLevelGroupTacacsRepository extends JpaRepository<AccessLevelGroupTacacs, Long> {
}
