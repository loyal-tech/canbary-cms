package com.adopt.apigw.model.common;

import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tblstaffservicearearel")
@Data
@NoArgsConstructor
@EntityListeners(AuditableListener.class)
public class StaffUserServiceAreaMapping extends Auditable {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "staffid", nullable = false, length = 40)
    private Integer staffId;

    @Column(name = "serviceareaid", nullable = false, length = 40)
    private  Integer serviceId;

    @Column(name = "created_on", nullable = false, length = 40)
    private LocalDateTime createdOn;
    
    @Column(name = "lastmodified_on", nullable = false, length = 40)
    private LocalDateTime lastmodifiedOn ;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


}
