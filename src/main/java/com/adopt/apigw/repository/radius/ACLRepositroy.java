package com.adopt.apigw.repository.radius;


import org.javers.spring.annotation.JaversSpringDataAuditable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

//@JaversSpringDataAuditable
@Repository
public class ACLRepositroy {

    @PersistenceContext
    EntityManager entityManager;

    public List<Object[]> generateACLMap() {

        List<Object[]> resultList = null;
        try {

            String strQuery = "select t.roleid, t2.opid\n" +
                    "from tblaclentry t\n" +
                    "inner join tblacloperations t2 on t2.opid = t.permit";
            Query query = entityManager.createNativeQuery(strQuery);

//            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

    public List<Object[]> generateACLList() {

        List<Object[]> resultList = null;
        try {

            String strQuery = "select * from tblaclclass";

            Query query = entityManager.createNativeQuery(strQuery);

            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

    public List<Object[]> fetchDomains() {

        List<Object[]> resultList = null;
        try {

            String strQuery = "select classid,dispname"
                    + " FROM tblaclclass"
                    + " ORDER BY disporder";
            Query query = entityManager.createNativeQuery(strQuery);

            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

    public List<Object[]> fetchOperations(){
        List<Object[]> resultList = null;
        try {

            String strQuery = "select opid,opname"
                    + " FROM tblacloperations";
            Query query = entityManager.createNativeQuery(strQuery);

            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

    @Transactional
    public List<Object[]> fetchPermissions(){
        List<Object[]> resultList = null;
        try {

            String strQuery = "select roleid,code"
                    + " FROM tblmaclentry";
            Query query = entityManager.createNativeQuery(strQuery);

            resultList = query.getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

    @Transactional
    public List<String> fetchPermissions(Long roleId){
        List<String> resultList = null;
        try {
            String strQuery = "SELECT code FROM tblmaclentry WHERE roleid = :roleId"; // corrected the SQL query
            Query query = entityManager.createNativeQuery(strQuery);
            query.setParameter("roleId", roleId);
            resultList = query.getResultList();
            System.out.println("value is: "+ resultList);
        } catch (Exception e) {
            e.printStackTrace();
            resultList = null;
        }
        return resultList;
    }

}
