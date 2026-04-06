package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tblcaseupdatedetails")
public class CaseUpdateDetails implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "updatedtlsid")
    private Long id;
    private String operation;
    private String entitytype;
    private String oldvalue;
    private String newvalue;
    private String attachment;
    private String filename;
    @ManyToOne
    @JoinColumn(name = "resolutionid")
    private ResolutionReasons resolution;
    private String remarktype;
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "updateid")
    @ToString.Exclude
    private CaseUpdate caseUpdate;
    @OneToMany(targetEntity = CaseFeedbackRel.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "ticketid")
    List<CaseFeedbackRel> caseFeedbackRel;

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
