package com.adopt.apigw.modules.Voucher.module;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable<U> {

    @CreationTimestamp
    @Column(name = "createdate", nullable = false, updatable = false)
    @JsonProperty("createDate")
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Column(name = "lastmodificationdate", nullable = true, updatable = true)
    @JsonProperty("lastModificationDate")
    private LocalDateTime lastModifiedOn;

    @CreatedBy
    @Column(name = "createdby", nullable = false, length = 40, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "lastmodifiedby", nullable = false, length = 40)
    private String lastModifiedBy;
}

