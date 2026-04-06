package com.adopt.apigw.spring.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.model.common.Auditable;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.adopt.apigw.utils.PropertyReaderUtil;

import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import java.util.Properties;

@Slf4j
public class AuditableListener {

    public static final String MODULE = "[AuditableListener]";

    @PrePersist
    public void setAuditParamsForSave(Object obj) {
        String SUBMODULE = MODULE + " [setAuditParamsForSave()] ";
        LoggedInUser user;
        try {
            log.debug(SUBMODULE + ": Entering audit method with object: {}", obj);
            Auditable auditable = (Auditable) obj;
            SecurityContext securityContext = SecurityContextHolder.getContext();
            log.debug("SecurityContext retrieved : {}, SUBMODULE: {} ", securityContext,SUBMODULE);
            if (null != securityContext.getAuthentication()) {
                log.debug(SUBMODULE + "Authentication found: {}", securityContext.getAuthentication());
                log.debug(SUBMODULE + "Authentication Principal: {}", securityContext.getAuthentication().getPrincipal());
                if (securityContext.getAuthentication().getPrincipal().toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
                   log.debug(SUBMODULE + "Anonymous user detected. Loading properties from file: {}", PGConstants.PGCONFIG_FILE);
                    Properties properties = PropertyReaderUtil.getPropValues(PGConstants.PGCONFIG_FILE);
                    log.debug("Properties retrieved : {}, SUBMODULE: {} ", properties,SUBMODULE);
                    log.debug(SUBMODULE + " Properties loaded successfully System StaffId: {}", properties.getProperty(PGConstants.PG_USER_STAFFID));
                    log.debug(SUBMODULE + " Properties loaded successfully System StaffName: {}", properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setLastModifiedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                    auditable.setLastModifiedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedByName(properties.getProperty(PGConstants.PG_USER_STAFFNAME));
                    auditable.setCreatedById(Integer.valueOf(properties.getProperty(PGConstants.PG_USER_STAFFID)));
                } else {
                    log.debug(SUBMODULE + "Authenticated user detected. Extracting LoggedInUser");
                    user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
                    log.debug("LoggedInUser retrieved : {}.  UserId: {}, FullName: {}, SUBMODULE: {} ", user,user.getUserId(), user.getFullName(),SUBMODULE);
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
            Auditable auditable = (Auditable) obj;
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
