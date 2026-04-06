package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@Data
public class SaveTeamsSharedSharedData {


    private Long id;


    private String name;


    private String status;


    private Set<StaffUser> staffUser = new HashSet<>();


    private Boolean isDeleted = false;


    private Partner partner;


    private Integer mvnoId;


    private Teams parentTeams;

    private String cafStatus;


    private Integer lcoId;
    private Integer createdById;
    private Integer lastModifiedById;
    private String teamType;

}


