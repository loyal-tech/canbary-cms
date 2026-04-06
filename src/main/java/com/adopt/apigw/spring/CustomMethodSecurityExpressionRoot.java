package com.adopt.apigw.spring;

import com.adopt.apigw.service.radius.CustomACLService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.utils.CommonConstants;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CustomMethodSecurityExpressionRoot
        extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {

        super(authentication);

    }




//    public boolean validatePermission(String operationAll, String operation) {
//        boolean isPrivileged = false;
//        boolean isUnmappedModule = false;
//
//        CacheManager cacheManager = CacheManager.getInstance();
//        Cache aclCache = cacheManager.getCache("aclCache");
//        Object principal = authentication.getPrincipal();
//        if (principal.toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
//            isPrivileged = true;
//        }
////        List<String> roles = Arrays.stream(((LoggedInUser) authentication.getPrincipal())
////                .getRolesList()
////                .split(",")).collect(Collectors.toList());
//        else {
//            List<String> roles = Arrays.stream(((LoggedInUser) principal)
//                    .getRolesList()
//                    .split(",")).collect(Collectors.toList());
//            if (aclCache != null) {
//                for (String role : roles) {
//                    if(role.equalsIgnoreCase(CommonConstants.ADMIN_ROLE_ID.toString()))
//                        return true;
//                    Element element = aclCache.get(Integer.parseInt(role));
//                    if (element != null) {
//                        List<String> operationList = (ArrayList) element.getObjectValue();
//
//                        if (operationList.contains(operation) || operationList.contains(operationAll)) {
//                            ApplicationLogger.logger.info("Authorized against role:" + role + ",Required:" + operation + ",Granted: " + operation);
//                            isPrivileged = true;
//                            break;
//                        }
//                    }
//                }
//            } else {
//                isUnmappedModule = true;
//            }
//            if (!isPrivileged && isUnmappedModule) {
//                //This is newly added module for which there are no mapping hence no access given.
//                isPrivileged = false;
//            }
//        }
//        return isPrivileged;
//    }

    public boolean validatePermission(String operation, String... operationAll) {
        boolean isPrivileged = false;
        boolean isUnmappedModule = false;

        CacheManager cacheManager = CacheManager.getInstance();
        Cache permissionsCache = cacheManager.getCache("permissionsCache");
        Object principal = authentication.getPrincipal();
        if (principal.toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
            isPrivileged = true;
        }
        else {
            List<Long> roleIds  = getLoggedInUser().getRoleIds();
            for (Long roleId: roleIds) {
                List<String> operationList = CustomACLService.permissionsMap.get(roleId);
                if (operationList != null) {
                    if (operationList.contains(operation) || Arrays.asList(operationAll).stream().anyMatch(op -> operationList.contains(op))) {
                        ApplicationLogger.logger.info("Authorized against role:" + roleId + ",Required:" + operation + ",Granted: " + operation);
                        isPrivileged = true;
                        break;
                    }
                }
            }
        }
        return isPrivileged;
    }

    @Override
    public void setFilterObject(Object filterObject) {

    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public void setReturnObject(Object returnObject) {

    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public Object getThis() {
        return null;
    }

//    public boolean validatePermission(String operation, String... operationAll) {
//        boolean isPrivileged = false;
//        boolean isUnmappedModule = false;
//
//        CacheManager cacheManager = CacheManager.getInstance();
//        Cache aclCache = cacheManager.getCache("aclCache");
//        Object principal = authentication.getPrincipal();
//        if (principal.toString().equalsIgnoreCase(CommonConstants.ANONYMOUS_USER)) {
//            isPrivileged = true;
//        }
////        List<String> roles = Arrays.stream(((LoggedInUser) authentication.getPrincipal())
////                .getRolesList()
////                .split(",")).collect(Collectors.toList());
//        else {
//            List<String> roles = Arrays.stream(((LoggedInUser) principal)
//                    .getRolesList()
//                    .split(",")).collect(Collectors.toList());
//            if (aclCache != null) {
//                for (String role : roles) {
//                    if(role.equalsIgnoreCase(CommonConstants.ADMIN_ROLE_ID.toString()))
//                        return true;
//                    Element element = aclCache.get(Integer.parseInt(role));
//                    if (element != null) {
//                        List<String> operationList = (ArrayList) element.getObjectValue();
//
//                        if (operationList.contains(operation) || operationList.contains(operationAll)) {
//                            ApplicationLogger.logger.info("Authorized against role:" + role + ",Required:" + operation + ",Granted: " + operation);
//                            isPrivileged = true;
//                            break;
//                        }
//                    }
//                }
//            } else {
//                isUnmappedModule = true;
//            }
//            if (!isPrivileged && isUnmappedModule) {
//                //This is newly added module for which there are no mapping hence no access given.
//                isPrivileged = false;
//            }
//        }
//        return isPrivileged;
//    }

    public LoggedInUser getLoggedInUser() {
        LoggedInUser user = null;
        try {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (null != securityContext.getAuthentication()) {
                user = ((LoggedInUser) securityContext.getAuthentication().getPrincipal());
            }
        } catch (Exception e) {
            user = null;
        }
        return user;
    }

}
