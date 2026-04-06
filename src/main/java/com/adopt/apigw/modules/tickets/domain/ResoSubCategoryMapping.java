package com.adopt.apigw.modules.tickets.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ResolutionReasons.domain.ResolutionReasons;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import javax.persistence.*;

@Data
@Entity
@Table(name = "tbltresosubcategorymapping")
public class ResoSubCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="res_id")
    private Long resId;
    @Column(name = "subcate_id")
    private Long subcateId;


}