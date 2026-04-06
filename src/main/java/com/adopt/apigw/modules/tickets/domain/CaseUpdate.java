package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "tblcaseupdates")
@EntityListeners(AuditableListener.class)
public class CaseUpdate extends Auditable implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "updateid")
    private Long id;
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "caseid")
    @ToString.Exclude
    private Case ticket;
    @JsonManagedReference
    @OneToMany(mappedBy = "caseUpdate", orphanRemoval = true, cascade = CascadeType.ALL)
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<CaseUpdateDetails> updateDetails = new ArrayList<>();
    @Column(name = "comment_by")
    private String commentBy;
    private String createby;
    private String updateby;
    @Column(name = "remarktype")
    private String remarkType;
    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
}
