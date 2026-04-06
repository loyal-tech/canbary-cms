package com.adopt.apigw.modules.role.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.acl.domain.CustomACLEntry;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString
@Table(name = "tblroles")
@EntityListeners(AuditableListener.class)
public class Role extends Auditable implements IBaseData<Long> {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "roleid", nullable = false, length = 40)
    private Long id;

    @Column(nullable = false, length = 40)
    private String rolename;


    @Column(name = "rstatus", nullable = false, length = 100)
    private String status;

    @Column(name= "sysrole", columnDefinition = "Boolean default false")
    private Boolean sysRole = false;

    /*@CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on")
    private LocalDateTime updatedate;*/

   /* @JsonBackReference
    @DiffIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "roles")
    @ToString.Exclude
    private Set<StaffUser> staffusers = new HashSet<>();*/


//    @DiffIgnore
//    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("id asc")
//    private List<CustomACLEntry> aclEntry = new ArrayList<>();

//    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
//    @OrderBy("id asc")
//    private List<RoleACLEntry> roleAclEntry = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;

    public Role(String name, LocalDateTime createdate, LocalDateTime updatedate, String status) {
        super();
        this.rolename = name;
        //this.createdate = createdate;
        //this.updatedate = updatedate;
        this.status = status;
    }

    public Role() {

    }

    public Role(Long id) {
		this.id = id;
	}

	@JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDelete = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDelete;
    }
}
