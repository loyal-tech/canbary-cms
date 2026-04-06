package com.adopt.apigw.model.common;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data
@ToString
@Table(name = "tblclientservice")
@NoArgsConstructor
public class ClientService extends Auditable {

    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "serviceid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false, length = 40)
    private String value;
    
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    public ClientService(String name, String value, Integer mvnoId) {
        this.name = name;
        this.value = value;
        this.mvnoId = mvnoId;
    }

    public ClientService(Integer id, String name, String value, Integer mvnoId) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.mvnoId = mvnoId;
    }
}
