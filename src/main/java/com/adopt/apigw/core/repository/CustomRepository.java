package com.adopt.apigw.core.repository;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Component
public class CustomRepository<T> {
    @Autowired
    private EntityManager em;

    private ObjectMapper mapper = new ObjectMapper();

    public List<T> getResultOfQuery(String argQueryString, Class<T> valueType) {
        try {
            Query query = em.createNativeQuery(argQueryString);
            NativeQueryImpl nativeQuery = (NativeQueryImpl) query;
            nativeQuery.setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
            List<Map<String, Object>> result = nativeQuery.getResultList();
            List<T> resultList = result.stream()
                    .map(o -> {
                        try {
                            return mapper.readValue(mapper.writeValueAsString(o),valueType);
                        } catch (Exception e) {
                            ApplicationLogger.logger.error(e.getMessage(), e);
                        }
                        return null;
                    }).collect(Collectors.toList());
            return resultList;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    public void updateQuery(String query, String key, List<Integer> list) {
        try {
            em.createQuery("UPDATE Mvno m SET m.status = 'Inactive' WHERE m.id IN :mvnoIds")
                    .setParameter(key, list)
                    .executeUpdate();

            em.flush();
            em.clear();
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }

    }


    public boolean updateMvnoStatus(List<Long> mvnoIds, String status) {
        if(status.equalsIgnoreCase("active")){
            String query = "UPDATE tblmmvno SET status = :status, mvno_deactivation_flag =0 WHERE MVNOID IN (:mvnoIds)";
            return  updateQueryNative(query, "status", status, "mvnoIds", mvnoIds);
        }else {
            String query = "UPDATE tblmmvno SET status = :status, mvno_deactivation_flag = true WHERE MVNOID IN (:mvnoIds)";
            return  updateQueryNative(query, "status", status, "mvnoIds", mvnoIds);
        }


    }

    public boolean updateQueryNative(String query, String statusParam, String statusValue, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return true;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }



    public Boolean updateMvnoStatusForStaff(List<Long> staffid, String status) {
        if(status.equalsIgnoreCase("active")){
            String query = "UPDATE tblstaffuser SET sstatus = :status, mvno_deactivation_flag =false WHERE staffid IN (:staffid)";
            return updateQueryNativeForStaff(query, "status", status, "staffid", staffid);
        }else{
            String query = "UPDATE tblstaffuser SET sstatus = :status, mvno_deactivation_flag = true  WHERE staffid IN (:staffid)";
            return updateQueryNativeForStaff(query, "status", status, "staffid", staffid);
        }


    }

    public boolean updateQueryNativeForStaff(String query, String statusParam, String statusValue, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return true;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }



    public boolean updateMvnoStatusForCustomer(List<Long> custid, String status) {
        if(status.equalsIgnoreCase("active")){
            String query = "UPDATE tblcustomers SET cstatus = :status, mvno_deactivation_flag = null WHERE custid IN (:custid) and mvno_deactivation_flag = true";
            return  updateQueryNativeForCustomer(query, "status", status, "custid", custid);
        }else{
            String query = "UPDATE tblcustomers SET cstatus = :status, mvno_deactivation_flag = true  WHERE custid IN (:custid)";
            return  updateQueryNativeForCustomer(query, "status", status, "custid", custid);
        }

    }

    public boolean updateQueryNativeForCustomer(String query, String statusParam, String statusValue, String idsParam, List<Long> ids) {
        try {
            em.createNativeQuery(query)
                    .setParameter(statusParam, statusValue)
                    .setParameter(idsParam, ids)
                    .executeUpdate();

            em.flush();
            em.clear();
            return  true;
        } catch (Exception ex) {
            ApplicationLogger.logger.error(ex.getMessage(), ex);
        }
        return false;
    }

}

