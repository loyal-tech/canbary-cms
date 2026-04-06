package com.adopt.apigw.model.radius;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

@Entity
@Data
@Table(name = "tblradiusprofilecheckitm")
@EntityListeners(AuditableListener.class)
public class RadiusProfileCheckItem extends Auditable {

    public RadiusProfileCheckItem() {
        super();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "radiuscheckitmid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String checkitem;

    @JsonManagedReference
    @OneToMany(mappedBy = "radiusProfileCheckItem", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id asc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CheckItemReplyItem> replyItems = new ArrayList<>();

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "radiusprofileid")
    private RadiusProfile radiusProfile;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCheckitem() {
        return checkitem;
    }

    public void setCheckitem(String checkitem) {
        this.checkitem = checkitem;
    }

    public List<CheckItemReplyItem> getReplyItems() {
        return replyItems;
    }

    public void setReplyItems(List<CheckItemReplyItem> replyItems) {
        this.replyItems.clear();
        this.replyItems.addAll(replyItems);
        //this.replyItems = replyItems;
    }

    public RadiusProfile getRadiusProfile() {
        return radiusProfile;
    }

    public void setRadiusProfile(RadiusProfile radiusProfile) {
        this.radiusProfile = radiusProfile;
    }

    public RadiusProfileCheckItem(Integer radiusProfileId) {
        this.radiusProfile = new RadiusProfile(radiusProfileId);
    }

    @Override
    public String toString() {
        return "RadiusProfileCheckItem [id=" + id + ", checkitem=" + checkitem + ", createdate=" + getCreatedate()
                + ", updatedate=" + getUpdatedate() + ", replyItems=" + replyItems + "]";
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
        RadiusProfileCheckItem other = (RadiusProfileCheckItem) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
