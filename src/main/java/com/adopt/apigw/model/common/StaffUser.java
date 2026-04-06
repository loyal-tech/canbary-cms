package com.adopt.apigw.model.common;

import com.adopt.apigw.modules.BusinessUnit.domain.BusinessUnit;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.adopt.apigw.modules.StaffUserService.domain.StaffUserServiceMapping1;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tblstaffuser")
@EntityListeners(AuditableListener.class)
public class StaffUser extends Auditable {

    @OneToMany(targetEntity = StaffUserServiceMapping1.class, cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinColumn(name = "staffmapping_id")
    List<StaffUserServiceMapping1> staffUserServiceMappings;

    @Id
    @Column(name = "staffid", nullable = false, length = 40)
    private Integer id;
    @Column(nullable = false, length = 40)
    private String username;
    @Column(nullable = false, length = 40)
    private String password;
    @Column(nullable = false, length = 40)
    private String firstname;
    @Column(nullable = false, length = 40)
    private String lastname;
    @Column(nullable = false, length = 40)
    private String email;
    @Column(nullable = false, length = 40)
    private String phone;
    private String countryCode;
    @Column(nullable = false, length = 40)
    private Integer failcount = 0;
    @Column(name = "sstatus", nullable = false, length = 40)
    private String status;

    /*@CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on")
    private LocalDateTime updatedate;*/
    @Column(nullable = false)
    private LocalDateTime last_login_time;
    @Column(name = "partnerid", nullable = false, length = 40)
    private Integer partnerid;
    @Transient
    private String newpassword;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tblstaffrolerel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "roleid")})
    private Set<Role> roles = new HashSet<>();
    private String otp;
    private LocalDateTime otpvalidate;
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(name = "tblteamusermapping", joinColumns = @JoinColumn(name = "staffid", referencedColumnName = "staffid"), inverseJoinColumns = @JoinColumn(name = "team_id", referencedColumnName = "team_id"))
    @ToString.Exclude
    @JsonIgnore
    private Set<Teams> team = new HashSet<>();
    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean sysstaff = false;
    @Transient
    private String fullName;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "service_area_id")
    private ServiceArea servicearea;
    //@JsonBackReference
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "businessunitid")
    private BusinessUnit businessUnit;
    @Column(name = "access_level_group_name")
    private String tacacsAccessLevelGroup;
    @JsonIgnore
    @DiffIgnore
    @ManyToOne
    @JoinColumn(name = "parent_staff_id")
    private StaffUser staffUserparent;
    @JsonIgnore
    @DiffIgnore
    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "staffUserparent")
    private List<StaffUser> staffUserChildList = new ArrayList<>();
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;
    @Column(name = "branchid", nullable = true, length = 40)
    private Integer branchId;
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblstaffservicearearel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "serviceareaid")})
    private List<ServiceArea> serviceAreaNameList = new ArrayList<>();
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @JoinTable(name = "tblstaffbusinessunitrel", joinColumns = {@JoinColumn(name = "staffid")}, inverseJoinColumns = {@JoinColumn(name = "businessunitid")})
    private List<BusinessUnit> businessUnitNameList = new ArrayList<>();
    @Column(name = "total_collected", nullable = false, length = 40)
    private Double totalCollected;

    @Column(name = "total_transferred", nullable = false, length = 40)
    private Double totalTransferred;

    @Column(name = "available_amount", nullable = false, length = 40)
    private Double availableAmount;

    @Column(name = "lcoid", nullable = false, length = 40, updatable = false)
    private Integer lcoId;
    @Column(name = "hrms_id", length = 50)
    private String hrmsId;
    @Lob
    @Column(name = "profile_image", columnDefinition = "BLOB", length = 30000)
    private byte[] profileImage;

    @Column(name = "department")
    private Integer department;

    @Column(name = "mvno_deactivation_flag")
    private Boolean mvnoDeactivationFlag;


    public StaffUser(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StaffUser other = (StaffUser) obj;
        return Objects.equals(this.id, other.id);
    }

    @PostLoad
    protected void defaultInitialize() {
        try {
            fullName = "";
            if (null != this.getFirstname() && !this.getFirstname().isEmpty() && this.getFirstname().trim().length() > 0) {
                fullName = this.getFirstname();
            }
            if (null != this.getLastname() && !this.getLastname().isEmpty() && this.getLastname().trim().length() > 0) {
                fullName += " " + this.getLastname();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "StaffUser [id=" + id + "]";
    }
}
