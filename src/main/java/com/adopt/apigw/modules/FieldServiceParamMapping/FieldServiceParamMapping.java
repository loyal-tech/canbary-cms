package com.adopt.apigw.modules.FieldServiceParamMapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.core.data.IBaseData2;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.modules.ServiceParameters.domain.ServiceParameter;
import com.adopt.apigw.modules.fieldMapping.Fields;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="tbltfieldserviceparamrel")
public class FieldServiceParamMapping extends Auditable implements IBaseData2, IBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(targetEntity = Fields.class)
    @JoinColumn(name = "fieldid", referencedColumnName = "id", updatable = true, insertable = true)
    private Fields fields;

    @ManyToOne(targetEntity = ServiceParameter.class)
    @JoinColumn(name = "serviceparamid", referencedColumnName = "id", updatable = true, insertable = true)
    private ServiceParameter serviceParameter;

    @Column(name = "is_mandatory")
    private Boolean is_mandatory;

    @Column(name = "module")
    private String module;

    @Column(name = "is_deleted")
    private Boolean is_deleted;

    @Override
    public Serializable getPrimaryKey() {
        return id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return is_deleted;
    }

    @Override
    public void setBuId(Long buId) {

    }
}
