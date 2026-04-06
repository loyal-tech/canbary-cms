package com.adopt.apigw.model.common;

import com.adopt.apigw.constants.Constants;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable2<U> {

    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "CREATEDATE", nullable = false, updatable = false)
    private LocalDateTime createdate;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm a")
    @Column(name = "LASTMODIFIEDDATE")
    private LocalDateTime updatedate;

    @Column(name = "createbyname", nullable = false, length = 40, updatable = false)
    private String createdByName;

    @Column(name = "updatebyname", nullable = false, length = 40)
    private String lastModifiedByName;

    @Column(name = "CREATEDBYSTAFFID", nullable = false, length = 40, updatable = false)
    private Integer createdById;

    @Column(name = "LASTMODIFIEDBYSTAFFID", nullable = false, length = 40)
    private Integer lastModifiedById;

    @ApiModelProperty(hidden = true)
    @Column(name = "MVNOID", nullable = false, length = 40, updatable = false)
    private Integer mvnoId;

    /*public void setMvnoId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(request.getAttribute(Constants.MVNO_ID_FROM_APIGW) != null)
            this.mvnoId = Integer.parseInt(request.getAttribute(Constants.MVNO_ID_FROM_APIGW).toString());
    }
    
    public void setMvnoId(Integer mvnoId) {
    	HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(request.getAttribute(Constants.MVNO_ID_FROM_APIGW) != null)
            this.mvnoId = Integer.parseInt(request.getAttribute(Constants.MVNO_ID_FROM_APIGW).toString());
    }*/

    public Integer LoggedInUserMvnoId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(request.getAttribute(Constants.MVNO_ID_FROM_APIGW) != null)
            this.mvnoId = Integer.parseInt(request.getAttribute(Constants.MVNO_ID_FROM_APIGW).toString());
        return this.mvnoId;
    }
}
