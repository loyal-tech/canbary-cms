package com.adopt.apigw.modules.ResolutionReasons.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tbltrootcauseresolutionmapping")
public class RootCauseResolutionMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String rootCauseReason;

    @Column(name = "resolution_id")
    private Long resolutionId;
}
