package com.adopt.apigw.modules.Broadcast.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

import com.adopt.apigw.core.data.IBaseData;

@Data
@Entity
@Table(name = "tblbroadcastports")
public class BroadcastPorts  implements IBaseData<Long> {

    @Id
    @Column(name = "broadportid")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer portid;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne
    @JoinColumn(name = "broadcast_id",insertable=true, updatable=false,
            nullable=true)
    private Broadcast broadcast;

    @Override
    public Long getPrimaryKey() {
        return this.id;
    }

    @Override
    public void setDeleteFlag(boolean deleteFlag) {

    }

    @Override
    public boolean getDeleteFlag() {
        return false;
    }

    //private Long broadcastid;
//    @ManyToOne
//    @JoinColumn(name = "planid")
//    private PostpaidPlan postpaidPlan;
}
