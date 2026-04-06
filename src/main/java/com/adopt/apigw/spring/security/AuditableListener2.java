package com.adopt.apigw.spring.security;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Auditable2;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.PropertyReaderUtil;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Properties;

public class AuditableListener2 {

    public static final String MODULE = "[AuditableListener]";

    @PrePersist
    public void setAuditParamsForSave(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForSave()] ";
        LoggedInUser user;
        try {
            Auditable2 auditable = (Auditable2) obj;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                } else {
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    auditable.setCreatedById(user.getUserId());
                    auditable.setLastModifiedById(user.getUserId());
                    auditable.setCreatedByName(user.getFullName());
                    auditable.setLastModifiedByName(user.getFullName());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            user = null;
        }
    }

    @PreUpdate
    @PreRemove
    public void setAuditParamsForUpdate(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForUpdate()] ";
        LoggedInUser user;
        try {
            Auditable2 auditable = (Auditable2) obj;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                } else {
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    auditable.setLastModifiedById(user.getUserId());
                    auditable.setLastModifiedByName(user.getFullName());
                }
            }
        } catch (Exception e) {
            ApplicationLogger.logger.error(SUBMODULE + e.getMessage(), e);
            user = null;
        }
    }
}
