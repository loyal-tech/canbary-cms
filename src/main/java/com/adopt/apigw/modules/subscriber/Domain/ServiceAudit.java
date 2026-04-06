package com.adopt.apigw.modules.subscriber.Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tbltserviceaudit")
//@SQLDelete(sql = "UPDATE tbltserviceaudit SET is_deleted = true WHERE sbvid=?")
//@Where(clause = "is_deleted=false")
public class ServiceAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "servicestoptime")
    private LocalDateTime serviceStopTime;
    @Column(name = "staffid")
    private Long staffId;
    @Column(name = "action")
    private String action;
    @Column(name = "cprid")
    private Long cprid;
    @Column(name = "reasonid")
    private String reasonId;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "servicestarttime")
    private LocalDateTime servicestarttime;

    @Transient
    private String reasonCategory;

    @Column(name= "createbyname")
    private String staffName;

    @Column(name = "custservicemappingid", nullable = false)
    private Integer custServiceMappingId;

    @Column(name= "reason")
    private String reason;

    @CreationTimestamp
    @Column(name = "auditdate", nullable = false)
    private LocalDate auditDate;


}
