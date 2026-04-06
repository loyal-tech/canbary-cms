package com.adopt.apigw.modules.acl.domain;

import com.adopt.apigw.modules.role.domain.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "tblaclentry")
public class CustomACLEntry implements Comparable<CustomACLEntry>, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aclid", nullable = false, length = 40)
    private Integer id;

    @Column(nullable = false)
    private int classid;

//    @ManyToOne
//    @JoinColumn(name = "roleid")
//    @ToString.Exclude
//    private Role role;

    @Column(nullable = false)
    private int permit;

    public CustomACLEntry() {
    }

    /**
     *
     */

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + classid;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + permit;
//        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @JsonIgnore
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        CustomACLEntry other = (CustomACLEntry) obj;
        if (classid != other.classid)
            return false;
        return true;
    }

    public CustomACLEntry(int classid, int permit) {
        super();
        this.classid = classid;
//        this.role = role;
        this.permit = permit;
    }

    @Override
    public int compareTo(CustomACLEntry obj) {
        int objClassId = obj.getClassid();
        /* For Ascending order*/
        return this.classid - objClassId;
    }

}
