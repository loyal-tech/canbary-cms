
package com.adopt.apigw.model.postpaid;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "TBLMTAX")
@EntityListeners(AuditableListener.class)
public class Tax extends Auditable {
	
	
	/*
	 CREATE TABLE TBLMTAX
	  (
	    TAXID                 serial,
	    NAME                  VARCHAR(64) NOT NULL,
	    DESCRIPTION           VARCHAR(255),
	    TAXTYPE               VARCHAR(8),
	    STATUS                CHAR(1) DEFAULT 'Y' NOT NULL,
		    CREATEDATE            TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
		    CREATEDBYSTAFFID      NUMERIC(20),
		    LASTMODIFIEDBYSTAFFID NUMERIC(20),
		    LASTMODIFIEDDATE      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
		    MVNOID                bigint UNSIGNED,
	    PRIMARY KEY (TAXID),
	    FOREIGN KEY (MVNOID) REFERENCES TBLMMVNO (MVNOID)
	  );
	 */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAXID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;

    @Column(name = "DESCRIPTION", nullable = false, length = 150)
    private String desc;

    @Column(name = "TAXTYPE", nullable = false, length = 40)
    private String taxtype;

    @Column(name = "STATUS", nullable = false, length = 40)
    private String status;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeTier> tieredList = new ArrayList<>();

    @JsonManagedReference
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tax", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<TaxTypeSlab> slabList = new ArrayList<>();

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @Column(name = "mvnoName")
    private String mvnoName;


    public Tax() {
    }

    public Tax(Integer id) {
        this.id = id;
    }


    public Tax(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

}
