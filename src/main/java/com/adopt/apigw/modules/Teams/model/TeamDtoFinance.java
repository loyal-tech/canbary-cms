package com.adopt.apigw.modules.Teams.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class TeamDtoFinance {

    private Long id;
    @NotNull(message = "Please Enter Team Name")
    private String name;
    @NotNull(message = "Please Enter Status")
    private String status;
    private Boolean isDeleted = false;
    private Long partnerid;
    private String partnername;
    private Integer lcoId;

    private List<Long> staffUserIds = new ArrayList<>();
    private List<String> staffNameList = new ArrayList<>();

    private Long parentteamid;

    private String parentTeamName;

    private Integer mvnoId;
    //
//    private Integer lcoId;
    private Long displayId;
    private String displayName;
}
