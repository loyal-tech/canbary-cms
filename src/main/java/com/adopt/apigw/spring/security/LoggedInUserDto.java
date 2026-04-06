package com.adopt.apigw.spring.security;

import com.adopt.apigw.model.common.StaffRoleRel;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.role.domain.Role;
import io.swagger.models.auth.In;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class LoggedInUserDto {

    private String password;

    private String roleId ;

    private List<Long> roleList;

    private String firstName;

    private String lastName;

    private LocalDateTime lastLoginTime;

    private Integer staffId;

    private Integer partnerId;

    private ServiceArea serviceAreaId;

    private Integer mvnoId;

    private List<Integer> serviceAreaIdList=new ArrayList<>();

    private List<Long> buIds;

    public LoggedInUserDto(String password, String firstName, String lastName, LocalDateTime lastLoginTime, Integer staffId, Integer partnerId, Integer mvnoId) {
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginTime = lastLoginTime;
        this.staffId = staffId;
        this.partnerId = partnerId;
        this.mvnoId = mvnoId;
    }
}
