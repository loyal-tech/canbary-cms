package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.common.Auditable;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class RadiusProfilePojo extends Auditable {

    private Integer id;

    private String name;

    private String status;

    private Set<CustomersPojo> customers = new HashSet<>();
    private Boolean isDelete = false;

    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<RadiusProfileCheckItemPojo> checkItems = new ArrayList<>();

    @Override
    public String toString() {
        return "RadiusProfilePojo [id=" + id + ", name=" + name + ", status=" + status + "]";
    }

}
