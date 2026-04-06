package com.adopt.apigw.model.radius;

import lombok.Data;

import javax.persistence.*;

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
@Table(name = "tblradiuscustomerreply")
public class CustReplyItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attributeid", nullable = false, length = 40)
    private Integer id;
    private Integer custid;
    private String attribute;
    private String attributevalue;
    @Transient
    private String tempid;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

}
