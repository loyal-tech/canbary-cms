package com.adopt.apigw.pojo.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class AuthDriverPojo extends ParentPojo {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    private String drivertype;

    private String ldapurl;

    private String ldapauthtype;

    private String ldapusername;

    private String ldappassword;

    private String ldapsearchparams;

    @CreationTimestamp
    private LocalDateTime createdate;

    @UpdateTimestamp
    private LocalDateTime updatedate;

    @JsonIgnore
    @Override
    public String toString() {
        return "AuthDriverPojo [id=" + id + ", name=" + name + ", status=" + status + ", drivertype=" + drivertype
                + ", ldapurl=" + ldapurl + ", ldapauthtype=" + ldapauthtype + ", ldapusername=" + ldapusername
                + ", ldappassword=" + ldappassword + ", ldapsearchparams=" + ldapsearchparams + ", createdate="
                + createdate + ", updatedate=" + updatedate + "]";
    }
}
