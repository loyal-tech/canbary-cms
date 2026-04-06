package com.adopt.apigw.modules.InventoryManagement.generateremoveInventoryRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
@Data
@Entity
@Table(name = "tblmgenerateremoverequest")
@NoArgsConstructor
@AllArgsConstructor
public class GenerateRemoveRequest  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "macmappingid", nullable = false)
    private Long macmappingid;

    @Column(name = "customerinventoryid", nullable = false)
    private Long customerinventoryId;

    @Column(name = "customerid")
    private Long customerid;

    @Column(name = "staffid")
    private Long staffid;

    @Column(name= "isflag")
    private boolean isFlag;

    @Column(name = "requeststatus")
    private String requestStatus;

    @Column(name = "revisedcharge")
    private Long revisedcharge;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    public GenerateRemoveRequest(Long id) {
        this.id = id;
    }
}
