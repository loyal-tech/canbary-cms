package com.adopt.apigw.model.tacacs;


import com.adopt.apigw.rabbitMq.message.ReceiveAccessLevelGroupTacacsMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tacacs_access_level_group")
public class AccessLevelGroupTacacs {

    @Id
    @Column(name = "access_level_id")
    private Long id;

    @Column(name = "access_level_group_name", nullable = false, unique = true)
    private String accessLevelGroupName;

    @Column(name = "access_level_group_id", unique = true, nullable = false)
    private String accessLevelGroupId;

    public AccessLevelGroupTacacs(ReceiveAccessLevelGroupTacacsMessage receiveAccessLevelGroupTacacsMessage){
        this.id = receiveAccessLevelGroupTacacsMessage.getId();
        this.accessLevelGroupId = receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupId();
        this.accessLevelGroupName = receiveAccessLevelGroupTacacsMessage.getAccessLevelGroupName();
    }
}
