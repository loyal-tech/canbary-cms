package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.role.domain.Role;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class UpdateStaffUserSharedDataMessage {
    private Integer id;
    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String status;
    private String last_login_time;
    private Integer partnerid;
    private Set<Role> roles = new HashSet<>();
    private Set<Teams> team = new HashSet<>();
    private Boolean isDelete = false;
    private ServiceArea servicearea;
    private BusinessUnit businessUnit;
    private Integer mvnoId;
    private Integer branchId;
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();

    private Integer parentStaffId;
    private String email;
    private String phone;
    private Integer lcoId;
    private String countryCode;
    private Integer createdById;
    private Integer lastModifiedById;
    private List<Teams> teamsList = new ArrayList<>();

    private Integer departmentId;



    public  UpdateStaffUserSharedDataMessage(StaffUser staffUser){

        this.id = staffUser.getId();
        this.username = staffUser.getUsername();
        this.password = staffUser.getPassword();
        this.status = staffUser.getStatus();
        this.branchId = staffUser.getBranchId();
        this.mvnoId = staffUser.getMvnoId();
        this.last_login_time = String.valueOf(staffUser.getLast_login_time());
        this.createdById = staffUser.getCreatedById();
        this.lastModifiedById = staffUser.getLastModifiedById();
        this.businessUnitNameList = staffUser.getBusinessUnitNameList();
        this.serviceAreaNameList = staffUser.getServiceAreaNameList();
        this.countryCode = staffUser.getCountryCode();
        this.phone = staffUser.getPhone();
        this.email =staffUser.getEmail();
        if(staffUser.getStaffUserparent()!=null){
            this.parentStaffId = staffUser.getStaffUserparent().getId();
        }
        if(staffUser.getPartnerid()!=null){
            this.partnerid = staffUser.getPartnerid();
        }
        this.isDelete = staffUser.getIsDelete();
        this.lcoId = staffUser.getLcoId();
        if(staffUser.getTeam()!=null){
            for(Teams teams : staffUser.getTeam()){
                Teams teamsobj = new Teams();
                teamsobj.setId(teams.getId());
                teamsobj.setName(teams.getName());
                teamsobj.setMvnoId(teams.getMvnoId());
                teamsobj.setStatus(teams.getStatus());
                teamsobj.setMvnoId(teams.getMvnoId());
                teamsobj.setLcoId(teams.getLcoId());
                this.teamsList.add(teamsobj);
            }
        }
        this.isDelete = staffUser.getIsDelete();
        this.mvnoId =staffUser.getMvnoId();
        this.firstname = staffUser.getFirstname();
        this.lastname = staffUser.getLastname();
        if(staffUser.getLast_login_time()!=null) {
            this.last_login_time = staffUser.getLast_login_time().toString();
        }
        this.status = staffUser.getStatus();
        if(staffUser.getRoles()!=null){
            this.roles = staffUser.getRoles();
        }
    }

    public UpdateStaffUserSharedDataMessage() {
    }

}
