package com.adopt.apigw.modules.PartnerLedger.domain;

import java.time.LocalDate;

import javax.persistence.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.postpaid.Partner;
import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name = "tblmpartnerledger")
public class PartnerLedger implements IBaseData<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partnerledger_id")
    private Long id;
    private Double totaldue = 0.0;
    private Double totalpaid = 0.0;


    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partner_id")
    @ToString.Exclude
    private Partner partner;

    @CreationTimestamp
    @Column(name = "CREATEDATE", nullable = false)
    private LocalDate createdate;

    @UpdateTimestamp
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDate updatedate;

    @Column(name = "is_deleted", columnDefinition = "Boolean default false")
    private Boolean isDeleted = false;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }
    
	@Override
	public String toString() {
		return "PartnerLedger []";
	}
}
