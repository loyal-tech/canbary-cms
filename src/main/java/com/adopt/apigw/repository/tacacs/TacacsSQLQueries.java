package com.adopt.apigw.repository.tacacs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TacacsSQLQueries {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TacacsSQLQueries(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void updateData(String updatedaccessLevelGroupName, String existingaccessLevelGroupName) {

        //Query for updating existing access level group in staffusertbl
        String sqlQuery =  "UPDATE tblstaffuser SET access_level_group_name = ? WHERE access_level_group_name = ?";

        Object[] params = {updatedaccessLevelGroupName, existingaccessLevelGroupName};
        int rowsAffected = jdbcTemplate.update(sqlQuery, params);

        log.info("Rows affected: {}", rowsAffected);
    }

    public void deleteData(String accessLevelGroupName) {

        //Query for deleting existing access level group in staffusertbl
        String sqlQuery = "UPDATE tblstaffuser " +
                "SET access_level_group_name = NULL " +
                "WHERE access_level_group_name = ? ";

        Object[] params = {accessLevelGroupName};
        int rowsAffected = jdbcTemplate.update(sqlQuery, params);

        log.info("Rows affected: {}", rowsAffected);
    }


}
