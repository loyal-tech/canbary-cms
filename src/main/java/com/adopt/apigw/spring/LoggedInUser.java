package com.adopt.apigw.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/*
 * 	private final Set<GrantedAuthority> authorities;
	private final boolean accountNonExpired;
	private final boolean accountNonLocked;
	private final boolean credentialsNonExpired;
	private final boolean enabled;

 */
@JsonIgnoreProperties({"password", "authorities", "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled", "lastLoginTime", "fullName"})
public class LoggedInUser extends User {

    private static final long serialVersionUID = -3531439484732724601L;

    private String firstName;
    private String lastName;
    private LocalDateTime lastLoginTime;
    private int userId;
    private int partnerId;
    private String rolesList;
    private Long serviceAreaId;
    private Integer mvnoId;
    private List<Integer> serviceAreaIdList=new ArrayList<>();
    private Integer staffId;
    private List<Long> buIds;

    private Boolean isLco;

    private List<String> Teams;

    private List<Long> roleIds;
    private  String mvnoName;

    private List<Long> teamIds;
    private List<Long> assignableRoleIds;

    private List<String> assignableRoleNames;
    public List<String> getTeams() {
        return Teams;
    }

    public void setTeams(List<String> teams) {
        Teams = teams;
    }

    public LoggedInUser(String username, String password, boolean enabled,
                        boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked,
                        Collection<GrantedAuthority> authorities,
                        String firstName, String lastName,
                        LocalDateTime lastLoginTime,
                        int userId,
                        int partnerid,
                        String rolesList,
                        Long serviceAreaId,
                        Integer mvnoId, List<Integer> serviceAreaIdList,
                        Integer staffId,
                        List<Long> buIds,
                        Boolean isLco , List<String> teams, List<Long> roleIds,String mvnoName,List<Long> assignableRoleIds, List<String> assignableRoleNames ) {
        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);

        new LoggedInUser(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked
                , authorities,firstName,lastName,lastLoginTime,userId,partnerid,rolesList,serviceAreaId,mvnoId,serviceAreaIdList, staffId,buIds,isLco,teams,roleIds,mvnoName, null, assignableRoleIds, assignableRoleNames);
    }
    public LoggedInUser(String username, String password, boolean enabled,
                        boolean accountNonExpired, boolean credentialsNonExpired,
                        boolean accountNonLocked,
                        Collection<GrantedAuthority> authorities,
                        String firstName, String lastName,
                        LocalDateTime lastLoginTime,
                        int userId,
                        int partnerid,
                        String rolesList,
                        Long serviceAreaId,
                        Integer mvnoId, List<Integer> serviceAreaIdList,
                        Integer staffId,
                        List<Long> buIds,
                        Boolean isLco , List<String> teams, List<Long> roleIds,String mvnoName,List<Long> teamids,  List<Long> assignableRoleIds, List<String> assignableRoleNames) {

        super(username, password, enabled, accountNonExpired,
                credentialsNonExpired, accountNonLocked, authorities);

        this.firstName = firstName;
        this.lastName = lastName;
        this.lastLoginTime = lastLoginTime;
        this.partnerId = partnerid;
        this.rolesList = rolesList;
        this.serviceAreaId= serviceAreaId;
        this.mvnoId= mvnoId;
        this.serviceAreaIdList=serviceAreaIdList;
        setUserId(userId);
        this.staffId=staffId;
        this.buIds = buIds;
        this.isLco=isLco;
        this.Teams = teams;
        this.roleIds = roleIds;
        this.mvnoName=mvnoName;
        this.teamIds=teamids;
        this.assignableRoleIds = assignableRoleIds;
        this.assignableRoleNames = assignableRoleNames;
    }


    public LoggedInUser() {
        super("usrename", "password", new ArrayList<GrantedAuthority>());
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public String getFullName() {
        return new StringBuffer().append(null != getFirstName() ? getFirstName() : "").append(null != getLastName() ? " " + getLastName() : "").toString();
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(int partnerId) {
        this.partnerId = partnerId;
    }

    public String getRolesList() {
        return rolesList;
    }

    public void setRolesList(String rolesList) {
        this.rolesList = rolesList;
    }

    public Long getServiceAreaId() {
        return serviceAreaId;
    }


    public void setServiceAreaId(Long serviceAreaId) {
        this.serviceAreaId = serviceAreaId;
    }
    public Integer getMvnoId() {
        return mvnoId;
    }


    public void setMvnoId(Integer mvnoId) {
        this.mvnoId = mvnoId;
    }

    public List<Integer> getServiceAreaIdList() {
        return serviceAreaIdList;
    }

    public void setServiceAreaIdList(List<Integer> serviceAreaIdList) {
        this.serviceAreaIdList = serviceAreaIdList;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public List<Long> getBuIds() {
        return buIds;
    }

    public void setBuIds(List<Long> buIds) {
        this.buIds = buIds;
    }

    public Boolean getLco() {return isLco;}

    public void setLco(Boolean lco) {isLco = lco;}

    public List<Long> getRoleIds() {
        return roleIds;
    }

    public String getMvnoName() {
        return mvnoName;
    }

    public void setMvnoName(String mvnoName) {
        this.mvnoName = mvnoName;
    }

    public void setRoleIds(List<Long> roleIds) {
        this.roleIds = roleIds;
    }


    public List<Long> getTeamIds() {
        return teamIds;
    }

    public void setTeamIds(List<Long> teamIds) {
        this.teamIds = teamIds;
    }

    public List<Long> getAssignableRoleIds() {
        return assignableRoleIds;
    }

    public void setAssignableRoleIds(List<Long> assignableRoleIds) {
        this.assignableRoleIds = assignableRoleIds;
    }

    public List<String> getAssignableRoleNames() {
        return assignableRoleNames;
    }

    public void setAssignableRoleNames(List<String> assignableRoleNames) {
        this.assignableRoleNames = assignableRoleNames;
    }

    @Override
    public String toString() {
        return "LoggedInUser [firstName=" + firstName + ", lastName=" + lastName + ", lastLoginTime=" + lastLoginTime
                + ", userId=" + userId + ", partnerId=" + partnerId+ ", staffId=" + staffId + "]";
    }


}
