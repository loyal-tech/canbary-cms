package com.adopt.apigw.modules.LocationMaster.domain;


import com.adopt.apigw.modules.LocationMaster.module.LocationMasterMappingDto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tblmlocationmastermapping")
public class LocationMasterMapping{

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "locationmappingid", nullable = false)
    private Long mappingId;

//    @Column(name = "locationid", length = 250)
//    private Long locationId ;

    @ApiModelProperty( required = true)
    @Column(name = "mac", length = 250)
    private String mac ;

    @ApiModelProperty(required = false)
    @Column(name = "identity", length = 250)
    private String identity ;

//    @JsonBackReference
//    @ManyToOne
//    @JoinColumn(name = "locationid")
//    private LocationMaster locationMaster;

    @DiffIgnore
    @Column(name = "locationid")
    private Long locationMasterId;

    @DiffIgnore
    @Column(name = "is_used")
    private Boolean isUsed;

    @Column(name = "location_name")
    private String locationName;


//    public LocationMasterMapping(LocationMasterMappingDto locationmastermappingdto, LocationMaster locationMaster)
//    {
//        this.mac = locationmastermappingdto.getMac();
//        this.identity = locationmastermappingdto.getIdentity();
//        this.locationMasterId = locationMaster.getLocationMasterId();
//        if(locationmastermappingdto.getIsUsed() != null)
//            this.isUsed = locationmastermappingdto.getIsUsed();
//        else
//            this.isUsed = false;
//        this.locationName = locationMaster.getName();
//    }

    public LocationMasterMapping(LocationMasterMappingDto locationmastermappingdto, LocationMaster locationMaster) {
        this.mac = locationmastermappingdto.getMac();
        this.identity = locationmastermappingdto.getIdentity();
        this.isUsed = locationmastermappingdto.getIsUsed() != null ? locationmastermappingdto.getIsUsed() : false;
        this.locationName = locationMaster.getName();

        // Set the locationid to the associated LocationMaster's ID
        this.locationMasterId = locationMaster.getLocationMasterId();
    }


}

