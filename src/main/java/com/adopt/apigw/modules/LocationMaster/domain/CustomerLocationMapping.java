package com.adopt.apigw.modules.LocationMaster.domain;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBLTCUSTOMERLOCATIONMAPPING")
//@EntityListeners(AuditableListener.class)
public class CustomerLocationMapping {

    @Id
    @Column(name = "customerlocationid", nullable = false)
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @ApiModelProperty(notes = "This is Customer Id")
    @Column(name = "custid", nullable = false)
    private Long custId;

    @ApiModelProperty(notes = "Location id from plan")
    @Column(name = "locationid")
    private Long locationId;

    @ApiModelProperty(notes = "Location name from plan")
    @Column(name = "locationname")
    private String locationName;

    @Column(name = "is_deleted")
    private Boolean isDelete;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_parent_location")
    private Boolean isParentLocation;

    @Column(name = "mac")
    private String mac;

    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public CustomerLocationMapping(Boolean isParentLocation, Long locationId, String mac, String locationName) {
        this.isParentLocation = isParentLocation;
        this.locationId = locationId;
        this.mac = mac;
        this.locationName = locationName;
    }
}
