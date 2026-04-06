package com.adopt.apigw.modules.custAccountProfile;


import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
@Table(name = "tbltcustprofile")
public class CustAccountProfile extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name ="prefix")
    private String prefix;

    @Column(name = "type")
    private String type;

    @Column(name="start_from")
    private String startFrom;

    @Column(name = "year")
    private boolean year;

    @Column(name = "month")
    private boolean month;

    @Column(name = "day")
    private boolean day;

    @Column(name = "status")
    private String status;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete = false;

    @DiffIgnore
    @Column(name = "mvno_id", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;


}
