package com.adopt.apigw.modules.LocationMaster.domain;

import com.adopt.apigw.model.common.CustomerPayment;
import com.adopt.apigw.modules.LocationMaster.module.LocationMasterDto;
import com.adopt.apigw.modules.LocationMaster.module.UpdateLocationMasterDto;
import com.adopt.apigw.modules.ServiceArea.domain.ServiceArea;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.UpdateTimestamp;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@JaversSpringDataAuditable
@Table(name = "TBLMLOCATIONMASTER")
@ApiModel(value = "LocationMaster Entity", description = "This is LocationMaster entity ")
public class LocationMaster {

    @Id
    @DiffIgnore
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(notes = "The database generated Location Master Id")
    @Column(name = "locationid", nullable = false)
    private Long locationMasterId;

    @ApiModelProperty(notes = "This is Location Master Name")
    @Column(name = "name", nullable = false, length = 250)
    private String name;

    @ApiModelProperty(notes = "Check item for location master", required = false)
    @Column(name = "checkitem", length = 250)
    private String checkItem;

    private String status;

    @DiffIgnore
    @ApiModelProperty(notes = "This is mvnoid")
    @Column(name = "mvnoid", nullable = false, length = 10)
    private Long mvnoId;

    @ApiModelProperty(notes = "This is Location Master locationIdentifyAttribute.", required = false)
    @Column(name = "location_identify_attribute", nullable = false, length = 10)
    private String locationIdentifyAttribute;

//    @ManyToMany(mappedBy = "locations")
//    private Set<ServiceArea> serviceAreas = new HashSet<>();


    @UpdateTimestamp
    @DiffIgnore
    @ApiModelProperty(hidden = true)
    @Column(name = "lastmodificationdate", length = 100)
    @JsonProperty("lastModificationDate")
    private Timestamp lastmodifiedDate;
    
    @ApiModelProperty(notes = "This is Location Master locationValue.", required = false)
    @Column(name = "location_identify_value", nullable = false, length = 10)
    private String locationIdentifyValue;

    @JsonManagedReference
    @LazyCollection(LazyCollectionOption.FALSE)
    @DiffIgnore
//    @JoinColumn(name = "locationid", referencedColumnName = "locationid")
    @OneToMany(targetEntity = LocationMasterMapping.class, cascade = CascadeType.ALL, mappedBy = "mappingId")
    private List<LocationMasterMapping> locationMasterMappings;

    public LocationMaster() {
        super();

    }

    public LocationMaster(LocationMasterDto locationMasterDto, Long mvnoId) {

        this.name = locationMasterDto.getName();
        this.checkItem = locationMasterDto.getCheckItem();
        this.status = locationMasterDto.getStatus();
        this.mvnoId = mvnoId;
        this.locationIdentifyAttribute = locationMasterDto.getLocationIdentifyAttribute();
        this.locationIdentifyValue = locationMasterDto.getLocationIdentifyValue();
        if(!CollectionUtils.isEmpty(locationMasterDto.getLocationMasterMapping())) {
            List<LocationMasterMapping> locationMasterMappings = locationMasterDto.getLocationMasterMapping()
                    .stream().map(mappingDto -> new LocationMasterMapping(mappingDto, this)).collect(Collectors.toList());
            this.locationMasterMappings = locationMasterMappings;
        }
    }

    public LocationMaster(UpdateLocationMasterDto locationMasterDto, Long mvnoId) {
        this.locationMasterId = locationMasterDto.getLocationMasterId();
        this.name = locationMasterDto.getName();
        this.checkItem = locationMasterDto.getCheckItem();
        this.status = locationMasterDto.getStatus();
        this.mvnoId = mvnoId;
        this.locationIdentifyAttribute = locationMasterDto.getLocationIdentifyAttribute();
        this.locationIdentifyValue = locationMasterDto.getLocationIdentifyValue();
        if(!CollectionUtils.isEmpty(locationMasterDto.getLocationMasterMapping())) {
            List<LocationMasterMapping> locationMasterMappings = locationMasterDto.getLocationMasterMapping()
                    .stream().map(mappingDto -> new LocationMasterMapping(mappingDto, this)).collect(Collectors.toList());
            this.locationMasterMappings = locationMasterMappings;
        }
    }

}
