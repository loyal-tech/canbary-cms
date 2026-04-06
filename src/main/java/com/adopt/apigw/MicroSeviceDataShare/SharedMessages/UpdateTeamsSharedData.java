package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;


import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.model.postpaid.Partner;
import com.adopt.apigw.modules.Teams.domain.Teams;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class UpdateTeamsSharedData {


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
