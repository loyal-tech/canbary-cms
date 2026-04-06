package com.adopt.apigw.model.radius;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.model.common.Customers;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "tblradiusprofile")
@EntityListeners(AuditableListener.class)
public class RadiusProfile extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "radiusprofileid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "status", nullable = false, length = 40)
    private String status;

    @JsonManagedReference
    @OneToMany(mappedBy = "radiusProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<RadiusProfileCheckItem> checkItems = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "radiusProfiles")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Customers> customers = new ArrayList<>();

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    public RadiusProfile() {
    }

    public RadiusProfile(Integer id) {
        this.id = id;
    }

    public RadiusProfile(String name, String status) {
        super();
        this.name = name;
        this.status = status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RadiusProfile other = (RadiusProfile) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RadiusProfile [id=" + id + ", name=" + name + ", createdate=" + getCreatedate() + ", updatedate="
                + getUpdatedate() + ", status=" + status + ", checkItems=" + checkItems + "]";
    }

}
