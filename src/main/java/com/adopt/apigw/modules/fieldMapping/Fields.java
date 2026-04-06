package com.adopt.apigw.modules.fieldMapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="tblmfields")
public class Fields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "fieldname", nullable = false)
    private String fieldname;

    @Column(name = "name",length = 40)
    private String name;
    @Column(name = "data_type")
    private String dataType;

    @Transient
    private String defaultValue;

    @Transient
    private Boolean mandatoryFlag;
}
