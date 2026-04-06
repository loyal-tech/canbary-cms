package com.adopt.apigw.modules.subscriber.model;

import com.adopt.apigw.model.common.CustIpMapping;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class CustomerListPojo {
    private Integer id;
    private String name;
    private String username;
    private String mobile;
    private String serviceArea;
    private String acctno;

    private String status;

    private String connectionMode;

    private Integer nextTeamHierarchyMapping;

    private Long staffId;

    private String currentAssigneeName;

    private Integer currentStaff;

    private String password;

    private Integer slaTime;

    private String slaUnit;
    private LocalDate nextfollowupdate;
    private LocalTime nextfollowuptime;

    private Integer mvnoId;
    private String mvnoName;

    private  String email;

    private List<CustIpMapping> custIpMappingList;

    private String areaName;

    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId,Integer currentStaff,Integer slaTime,String slaUnit,LocalDate nextfollowupdate,LocalTime nextfollowuptime, Integer mvnoId, String mvnoName, String currentAssigneeName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.currentStaff = currentStaff;
        this.slaUnit=slaUnit;
        this.slaTime=slaTime;
        this.nextfollowupdate=nextfollowupdate;
        this.nextfollowuptime=nextfollowuptime;
        this.mvnoId= mvnoId;
        this.mvnoName = mvnoName;
        this.currentAssigneeName =currentAssigneeName;
    }
//for search customer
    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId, Integer mvnoId, String mvnoName, String currentAssigneeName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.mvnoId = mvnoId;
        this.mvnoName = mvnoName;
        this.currentAssigneeName = currentAssigneeName;
    }

    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId, Integer mvnoId,String email, String mvnoName,Integer currentStaff , String currentAssigneeName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.mvnoId = mvnoId;
        this.email = email;
        this.mvnoName = mvnoName;
        this.currentStaff=currentStaff;
        this.currentAssigneeName = currentAssigneeName;
    }

    public CustomerListPojo(Integer id, String name, String username  ,String password, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
    }

    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId,Integer currentStaff,Integer slaTime,String slaUnit,LocalDate nextfollowupdate,LocalTime nextfollowuptime, Integer mvnoId, String mvnoName, String currentAssigneeName,String areaName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.currentStaff = currentStaff;
        this.slaUnit=slaUnit;
        this.slaTime=slaTime;
        this.nextfollowupdate=nextfollowupdate;
        this.nextfollowuptime=nextfollowuptime;
        this.mvnoId= mvnoId;
        this.mvnoName = mvnoName;
        this.currentAssigneeName =currentAssigneeName;
        this.areaName=areaName;
    }

    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId, Integer mvnoId, String mvnoName, String currentAssigneeName,String areaName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.mvnoId = mvnoId;
        this.mvnoName = mvnoName;
        this.currentAssigneeName = currentAssigneeName;
        this.areaName=areaName;
    }

    public CustomerListPojo(Integer id, String name, String username, String mobile, String serviceArea, String acctno, String status,Integer nextTeamHierarchyMapping,Long staffId, Integer mvnoId,String email, String mvnoName,Integer currentStaff , String currentAssigneeName,String areaName) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.mobile = mobile;
        this.serviceArea = serviceArea;
        this.acctno = acctno;
        this.status = status;
        this.nextTeamHierarchyMapping = nextTeamHierarchyMapping;
        this.staffId = staffId;
        this.mvnoId = mvnoId;
        this.email = email;
        this.mvnoName = mvnoName;
        this.currentStaff=currentStaff;
        this.currentAssigneeName = currentAssigneeName;
        this.areaName=areaName;
    }
}
