package com.adopt.apigw.modules.fieldMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="tbltscreenfieldsmapping")
public class ScreenFieldMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(targetEntity = Screens.class)
    @JoinColumn(name = "screenid", referencedColumnName = "id", updatable = true, insertable = true)
    private Screens screens;

    @ManyToOne(targetEntity = Fields.class)
    @JoinColumn(name = "fieldid", referencedColumnName = "id", updatable = true, insertable = true)
    private Fields fields;

    @ManyToOne(targetEntity = Fields.class)
    @JoinColumn(name = "parentfieldid", referencedColumnName = "id", updatable = true, insertable = true)
    private Fields parentfields;

    @Column(name = "index")
    private Long indexing;

    @Column(name = "fieldtype")
    private String fieldType;
    @Column(name = "endpoint")
    private String endpoint;
    @Column(name = "dependantfieldname")
    private String dependantfieldName;
    @Column(name = "backendrequired")
    private String backendrequired;
    @Column(name = "isdependant")
    private Boolean isdependant = false;
    @Column(name = "ispostrequest")
    private Boolean ispostrequest = false;
    @Column(name = "regex")
    private String regex;
}
