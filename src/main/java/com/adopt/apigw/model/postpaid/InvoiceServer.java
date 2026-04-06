package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data

@ToString
@Table(name = "tblserverdetail")
public class InvoiceServer {

    public InvoiceServer() {
    }

    public InvoiceServer(String serverip, String webport, String status,String servertype) {
        super();
        this.serverip = serverip;
        this.webport = webport;
        this.status = status;
        this.servertype=servertype;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serverid", nullable = false, length = 40)
    private Integer id;


    @Column(nullable = false, length = 40)
    private String serverip;

    @Column(nullable = false, length = 40)
    private String webport;

    private String servertype;

    @Column(nullable = false, length = 40)
    private String status;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @Column(name = "lastmodified_on", nullable = true, updatable = true)
    private LocalDateTime updatedate;

    @Column(name = "is_delete", columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDelete;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;
}
