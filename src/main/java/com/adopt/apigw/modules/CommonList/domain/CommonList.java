package com.adopt.apigw.modules.CommonList.domain;

import com.adopt.apigw.core.data.IBaseData;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "tblcommonlist")
public class CommonList implements IBaseData<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="list_item_id", nullable = false, length = 40)
    private Long id;
    @Column(name="list_text", nullable = false, length = 40)
    private String text;
    @Column(name="list_value", nullable = false, length = 40)
    private String value;
    @Column(name="list_type", nullable = false, length = 40)
    private String type;
    @Column(name="status", nullable = false, length = 1)
    private String status;
    
    @Column(name = "MVNOID", nullable = false, length = 40)
    private Integer mvnoId;

    @JsonIgnore
    @Override
    public Long getPrimaryKey() {
        return id;
    }

    @JsonIgnore
    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @JsonIgnore
    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CommonList that = (CommonList) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
