package com.adopt.apigw.service.radius;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.repository.radius.ACLRepositroy;
import com.adopt.apigw.utils.UtilsCommon;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CustomACLService {

    private static final Logger logger = LoggerFactory.getLogger(CustomACLService.class);

    @Autowired
    private ACLRepositroy repository;

    private CacheManager cacheManager;

    public static ConcurrentHashMap<Long, List<String>> permissionsMap = new ConcurrentHashMap<>();

    public CustomACLService() {
        ApplicationLogger.logger.info("Constructor called");
//        cacheManager = CacheManager.getInstance();
        //repository = new ACLRepositroy();
//         initCache();
    }
	
//	/*private  Map<String,Map<String,Integer>>  aclMap = null;
//
//	public synchronized Map<String,Map<String,Integer>>  getACLMap(){
//		if(aclMap == null){
//			aclMap=repository.generateACLMap();
//		}
//		return aclMap;
//	}*/
//
//    @Autowired
//    public void initCache() {
//        String METHOD = "initCache()";
//        Cache aclCache = null;
//        Cache domainsCache = null;
//        Cache operationsCache = null;
//        Cache permissionsCache = null;
//
//        try {
//            ApplicationLogger.logger.info(METHOD + " Started");
//            aclCache = cacheManager.getCache("aclCache");
//
//
//            List<Object[]> resultSet = repository.generateACLMap();
//
//            Object[] resultFields = null;
//            List<String> operationList = null;
//
//            Integer role = 0;
//            String operation = "";
//            Element element = null;
//            if (resultSet != null && resultSet.size() > 0) {
//                for (int i = 0; i < resultSet.size(); i++) {
//                    operationList = null;
//                    role = 0;
//                    operation = "";
//
//                    resultFields = resultSet.get(i);
//                    if(resultFields[0] == null)
//                        continue;
//                    role = Integer.parseInt(resultFields[0].toString());
//                    operation = resultFields[1].toString();
//
//                    ApplicationLogger.logger.info("Role: " + role + " Operation: " + operation);
//
//                    element = aclCache.get(role);
//                    if (element != null) {
//                        operationList = (List<String>) element.getObjectValue();
//                    }
//                    if (operationList == null) {
//                        operationList = new ArrayList<>();
//                    }
//                    operationList.add(operation);
//
//                    aclCache.put(new Element(role, operationList));
//                }
//            }
//
//            //Fetching Domains cache
////            domainsCache = cacheManager.getCache("domainsCache");
////
////            element = null;
////            resultSet = null;
////            resultFields = null;
////            String domainName = null;
////            resultSet = repository.fetchDomains();
////            Integer classid = -1;
////
////            if (resultSet != null && resultSet.size() > 0) {
////                for (int i = 0; i < resultSet.size(); i++) {
////                    domainName = null;
////                    classid = -1;
////                    resultFields = resultSet.get(i);
////
////                    classid = Integer.parseInt(resultFields[0].toString());
////                    domainName = resultFields[1].toString();
////                    domainsCache.put(new Element(classid, domainName));
////                }
////            }
//
////            operationsCache = cacheManager.getCache("operationsCache");
////            resultSet = null;
////            resultFields = null;
////            resultSet = repository.fetchOperations();
////
////            if (resultSet != null && resultSet.size() > 0) {
////                for (int i = 0; i < resultSet.size(); i++) {
////                    Integer opid = -1;
////                    String opname = null;
////                    resultFields = resultSet.get(i);
////
////                    opid = Integer.parseInt(resultFields[0].toString());
////                    opname = resultFields[1].toString();
////                    operationsCache.put(new Element(opid, opname));
////                }
////            }
//
//            permissionsCache = cacheManager.getCache("permissionsCache");
//            resultSet = null;
//            resultFields = null;
//            resultSet = repository.fetchPermissions();
//
//            if (resultSet != null && resultSet.size() > 0) {
//                for (int i = 0; i < resultSet.size(); i++) {
//                    operationList = null;
//                    Long roleId = 0L;
//                    operation = "";
//
//                    resultFields = resultSet.get(i);
//                    if(resultFields[0] == null)
//                        continue;
//                    roleId = Long.valueOf(resultFields[0].toString());
//                    operation = resultFields[1].toString();
//
//                    ApplicationLogger.logger.info("Role: " + roleId + " Operation: " + operation);
//
//                    element = permissionsCache.get(roleId);
//                    if (element != null) {
//                        operationList = (List<String>) element.getObjectValue();
//                    }
//                    if (operationList == null) {
//                        operationList = new ArrayList<>();
//                    }
//                    operationList.add(operation);
//
//                    permissionsCache.put(new Element(roleId, operationList));
//                }
//            }
//
//            ApplicationLogger.logger.info(METHOD + " Ended");
//        } catch (Exception e) {
//            e.printStackTrace();
//            if (aclCache != null) {
//                aclCache.flush();
//                domainsCache.flush();
//            }
//        }
//    }
@Autowired
public void initCache() {
    String METHOD = "initCache()";
    try {
        ApplicationLogger.logger.info(METHOD + " Started");

        List<Object[]> resultSet = repository.fetchPermissions();
        List<String> operationList = null;
        String operation =null;
        Long roleId = null;
        if (resultSet != null && !resultSet.isEmpty()) {
            for (Object[] resultFields : resultSet) {
                BigInteger roleIdBigInteger = (BigInteger) resultFields[0];
                roleId = roleIdBigInteger.longValue(); // Convert BigInteger to Long
                operation = (String) resultFields[1];
                if (!permissionsMap.containsKey(roleId)) {
                    permissionsMap.put(roleId, new ArrayList<>()); // Create a new list if roleId doesn't exist
                }
                permissionsMap.get(roleId).add(operation);
            }

        }

        ApplicationLogger.logger.info(METHOD + " Ended");
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void updateCache(Long roleId) {
        String METHOD = "UpdateCache()";
        try {
            ApplicationLogger.logger.info(METHOD + " Started");

            List<String> resultSet = repository.fetchPermissions(roleId);

            if (resultSet != null) {
                permissionsMap.put(roleId, resultSet);
            } else {
                // Handle the case when the result set is null
                ApplicationLogger.logger.warn("Result set from repository.fetchPermissions(roleId) is null for roleId: " + roleId);
            }

            ApplicationLogger.logger.info(METHOD + " Ended");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void reloadCache() {

        Cache aclCache = null;
        Cache domainsCache = null;
        Cache operationsCache = null;
        Cache permissionsCache = null;
        try {
            aclCache = cacheManager.getCache("aclCache");
            domainsCache = cacheManager.getCache("domainsCache");
            operationsCache = cacheManager.getCache("operationsCache");
            permissionsCache = cacheManager.getCache("permissionsCache");

            aclCache.flush();
            domainsCache.flush();
            operationsCache.flush();
            permissionsCache.flush();
           initCache();
            UtilsCommon.resetCachedObjects();
        } catch (Exception e) {

        }
    }
}
