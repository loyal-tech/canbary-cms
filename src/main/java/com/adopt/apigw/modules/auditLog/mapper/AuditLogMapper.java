package com.adopt.apigw.modules.auditLog.mapper;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.adopt.apigw.core.mapper.IBaseMapper;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.auditLog.domain.AuditLogEntry;
import com.adopt.apigw.modules.auditLog.model.AuditLogEntryDTO;

@Mapper
public abstract class AuditLogMapper implements IBaseMapper<AuditLogEntryDTO, AuditLogEntry> {

    @AfterMapping
    void afterMapping(@MappingTarget AuditLogEntryDTO dto, AuditLogEntry data) {
        try {
            if (null != data.getOperation()) {
//                CacheManager cacheManager = CacheManager.getInstance();
//                Cache opCache = cacheManager.getCache("operationsCache");
//                Element el = opCache.get(data.getOperation());
                if (null != data.getOperation())
                    dto.setOperation((data.getOperation()));
                else
                    dto.setOperation("-");
            } else
                dto.setOperation("-");
        } catch (Exception ex) {
            ApplicationLogger.logger.error(" AuditLogMapper After Mapping " + ex.getMessage(), ex);
        }
    }
}
