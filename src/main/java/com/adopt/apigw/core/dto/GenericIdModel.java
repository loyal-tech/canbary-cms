package com.adopt.apigw.core.dto;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

@Data
@Entity
public class GenericIdModel {
    @Id
    private Long id;
}
