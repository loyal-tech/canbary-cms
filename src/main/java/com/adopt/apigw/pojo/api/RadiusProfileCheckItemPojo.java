package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RadiusProfileCheckItemPojo extends Auditable {

    private Integer id;

    private String checkitem;

    private Integer radiusProfileId;

    private Boolean isDeleted = false;

    @JsonManagedReference
    private List<CheckItemReplyItemPojo> replyItems = new ArrayList<>();

    @JsonBackReference
    private RadiusProfilePojo radiusProfile;

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

    public Integer getRadiusProfileId() {
        return radiusProfileId;
    }

    public void setRadiusProfileId(Integer radiusProfileId) {
        this.radiusProfileId = radiusProfileId;
    }

    public LocalDateTime getCreatedate() {
        return super.getCreatedate();
    }

    public void setCreatedate(LocalDateTime createdate) {
        super.setCreatedate(createdate);
    }

    public LocalDateTime getUpdatedate() {
        return super.getUpdatedate();
    }

    public void setUpdatedate(LocalDateTime updatedate) {
        super.setUpdatedate(updatedate);
    }

    public RadiusProfilePojo getRadiusProfile() {
        return radiusProfile;
    }

    public void setRadiusProfile(RadiusProfilePojo radiusProfile) {
        this.radiusProfile = radiusProfile;
    }

    public List<CheckItemReplyItemPojo> getReplyItems() {
        return replyItems;
    }

    public void setReplyItems(List<CheckItemReplyItemPojo> checkItemReplyItemPojoList) {
        this.replyItems = checkItemReplyItemPojoList;
    }

    @Override
    public String toString() {
        return "RadiusProfileCheckItemPojo [id=" + id + ", checkitem=" + checkitem + ", radiusProfileId="
                + radiusProfileId + ", createdate=" + getCreatedate() + ", updatedate=" + getUpdatedate()
                + ", checkItemReplyItemPojoList=" + replyItems + "]";
    }
}
