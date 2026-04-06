package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@ToString
@Table(name = "tbltemplatemanagement")
@EntityListeners(AuditableListener.class)
public class XsltManagement extends Auditable {

    public XsltManagement() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "templateid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String templatename;

    @Column(nullable = false, length = 40)
    private String templatetype;

    @Column(nullable = false, length = 40)
    private String status;

    @Column(name = "jrxmlfile", nullable = false)
    private String jrxmlfile;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @Column(name="lcoid", length = 40)
    private Integer lcoid;

}
