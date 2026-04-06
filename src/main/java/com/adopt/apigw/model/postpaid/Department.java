package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable2;
import com.adopt.apigw.pojo.api.CountryPojo;
import com.adopt.apigw.pojo.api.DepartmentPojo;
import com.adopt.apigw.spring.security.AuditableListener2;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tblmdepartment")
public class Department extends Auditable2 {


	/*
	 CREATE TABLE tblmdepartment
  (
    COUNTRYID serial,
    NAME      VARCHAR(64) NOT NULL,
    STATUS    CHAR(1) DEFAULT 'Y' NOT NULL,
    CREATEDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CREATEDBYSTAFFID      NUMERIC(20),
    LASTMODIFIEDBYSTAFFID NUMERIC(20),
    LASTMODIFIEDDATE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT PK_MCOUNTRY PRIMARY KEY (COUNTRYID)
  );

	 */

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 40)
    private Integer id;

    @Column(name = "name", nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @ToString.Exclude
    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartmentPlanMapping> departmentPlanMappings = new ArrayList<>();
    public Department(DepartmentPojo pojo, Integer id) {
        this.id=pojo.getId();
        this.name=pojo.getName();
        this.isDelete=pojo.getIsDelete();
        this.status=pojo.getStatus();
        List<DepartmentPlanMapping> planMappings=new ArrayList<>();
        for(Integer planid:pojo.getPlanIds()){
            DepartmentPlanMapping departmentPlanMapping=new DepartmentPlanMapping();
            departmentPlanMapping.setDepartment(this);
            departmentPlanMapping.setPlanId(new PostpaidPlan(planid));
            planMappings.add(departmentPlanMapping);
        }
        this.setDepartmentPlanMappings(planMappings);

    }

    public Department() {
    }
//    @Column(name = "MVNOID", nullable = false, length = 40)
//    private Integer mvnoId;
}
