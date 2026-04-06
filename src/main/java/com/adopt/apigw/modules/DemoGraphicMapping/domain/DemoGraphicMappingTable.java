package com.adopt.apigw.modules.DemoGraphicMapping.domain;

import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name= "tblmdemographicmapping")
public class DemoGraphicMappingTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", nullable = false, length = 40)
    private Long id;
    @Column(name="current_name", nullable = false, length = 200)
    private String currentName;
    @Column(name="new_name", nullable = false, length = 200)
    private String newName;
    @Column(name="validation_regex", nullable = true, length = 200)
    private String validationRegex;

}
