package com.adopt.apigw.modules.InventoryManagement.VendorManagment;

import com.adopt.apigw.core.data.IBaseData;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.security.AuditableListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="tblmvendor")
@SQLDelete(sql = "UPDATE tblmvendor SET is_deleted = true WHERE id=?")
@EntityListeners(AuditableListener.class)
public class Vendor extends Auditable implements IBaseData<Long> {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name="name")
    private String name;

    @Column(name="status")
    private String status;
    @Column(name="is_deleted")
    private boolean isDeleted;

    @Column(name="mvno_id")
    private Integer mvnoId;

    @Override
    public String toString() {
        return "Vendor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", isDeleted=" + isDeleted +
                ", mvnoId=" + mvnoId +
                '}';
    }


    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }
}
