package com.adopt.apigw.model.radius;

import javax.persistence.*;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;

/*
 * CREATE TABLE tblradiuscustomerreply(
    attributeid SERIAL PRIMARY KEY,
	custid INTEGER NOT NULL REFERENCES tblcustomers(custid),
    attribute VARCHAR(150),
    attributevalue VARCHAR(150)
 );

 */

@Entity
@Data
@Table(name = "tblradiusprofilereplyitm")
@EntityListeners(AuditableListener.class)
public class CheckItemReplyItem extends Auditable {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "radiusreplyitmid", nullable = false, length = 40)
    private Integer id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "radiuscheckitmid")
    private RadiusProfileCheckItem radiusProfileCheckItem;

    @Column(name = "attribute", nullable = false, length = 40)
    private String attribute;

    @Column(name = "radiusprofileid", nullable = false, length = 40)
    private Integer radiusprofileid;

    @Column(name = "attributevalue", nullable = false, length = 40)
    private String attributevalue;

    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getAttributevalue() {
        return attributevalue;
    }

    public void setAttributevalue(String attributevalue) {
        this.attributevalue = attributevalue;
    }

    public RadiusProfileCheckItem getRadiusProfileCheckItem() {
        return radiusProfileCheckItem;
    }

    public void setRadiusProfileCheckItem(RadiusProfileCheckItem radiusProfileCheckItem) {
        this.radiusProfileCheckItem = radiusProfileCheckItem;
    }

    public Integer getRadiusprofileid() {
        return radiusprofileid;
    }

    public void setRadiusprofileid(Integer radiusprofileid) {
        this.radiusprofileid = radiusprofileid;
    }

    @Override
    public String toString() {
        return "attribute=" + attribute + ", attributevalue=" + attributevalue;
    }

}
