package com.adopt.apigw.modules.fieldMapping;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@AllArgsConstructor
@EntityListeners(AuditableListener.class)
@Table(name = "tbltfieldsbuidmapping")
public class FieldsBuidMapping extends Auditable implements IBaseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "field_id")
    private Long fieldId;

    @Column(name = "buid")
    private Long buid;

    @Column(name="is_mandatory")
    private Boolean isMandatory;
    @Column(name = "screen")
    private String screen;
    @Column(name = "module")
    private String module;

    @Column(name="is_deleted")
    private Boolean isDeleted;

    @Column(name="data_type")
    private String dataType;

    @Column(name="field_name")
    private String fieldName;

    @Column(name = "serviceparamid")
    private Long serviceParamId;

    @Column(name = "default_mandatory")
    private Boolean defaultMandatory;

    @Override
    public Serializable getPrimaryKey() {
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
    public FieldsBuidMapping(){}

    public FieldsBuidMapping(FieldsBuidMapping fieldsBuidMapping){
        this.fieldId= fieldsBuidMapping.getFieldId();
        this.isMandatory= fieldsBuidMapping.getIsMandatory();
        this.screen= fieldsBuidMapping.getScreen();
        this.module= fieldsBuidMapping.getModule();
        this.dataType= fieldsBuidMapping.getDataType();
        this.fieldName= fieldsBuidMapping.getFieldName();
        this.defaultMandatory=fieldsBuidMapping.getDefaultMandatory();
    }
}
