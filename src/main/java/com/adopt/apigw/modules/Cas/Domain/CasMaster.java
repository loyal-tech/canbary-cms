package com.adopt.apigw.modules.Cas.Domain;

import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.Data;
import org.javers.core.metamodel.annotation.DiffIgnore;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "tbltcasmaster")
@EntityListeners(AuditableListener.class)
public class CasMaster extends Auditable implements IBaseData2<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    Long id;
    @Column(name = "casname")
    String casname;
    @Column(name = "status")
    String status;

    @DiffIgnore
    @Column(name = "BUID", nullable = false, length = 40, updatable = false)
    private Long buId;

    @DiffIgnore
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    @Column(columnDefinition = "Boolean default false", nullable = false)
    private Boolean isDeleted = false;

    @DiffIgnore
    @OneToMany(targetEntity = CasPackageMapping.class, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "casepackage_mapping_id")
    List<CasPackageMapping> casPackageMappings;

    @DiffIgnore
    @OneToMany(targetEntity = CasParameterMapping.class, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "cas_param_mapping_id")
    List<CasParameterMapping> casParameterMappings;

    @Column(name="endpointurl")
    private String endpoint;

    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {
        this.isDeleted = deleteFlag;
    }

    @Override
    public boolean getDeleteFlag() {
        return isDeleted;
    }


}