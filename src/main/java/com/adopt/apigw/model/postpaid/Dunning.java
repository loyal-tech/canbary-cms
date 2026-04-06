
package com.adopt.apigw.model.postpaid;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Data


@ToString
@Table(name = "tblpartners")
public class Dunning {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, length = 40)
    private Integer id;

    @Column(name = "NAME", nullable = false, length = 40)
    private String name;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

}
