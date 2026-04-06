package com.adopt.apigw.devCode;

import com.adopt.apigw.utils.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class TransactionUtil {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void updateCustomerChargeHistory(List<Integer> custChargehistoryIds ,Double amount  , Double taxAmount) {
        if (!custChargehistoryIds.isEmpty()) {
            entityManager.createQuery("UPDATE CustomerChargeHistory c SET c.chargeAmount = :chargeAmount, c.taxAmount =:taxAmount  WHERE c.id IN :custChargehistoryIds")
                    .setParameter("custChargehistoryIds",custChargehistoryIds)
                    .setParameter("chargeAmount", amount)
                    .setParameter("taxAmount", taxAmount)
                    .executeUpdate();
        }
    }

    @Transactional
    public void updateChildCustomerPassword(String newPassword,Long parentCustId) {
        if (!newPassword.isEmpty()) {
            entityManager.createQuery("UPDATE ChildCustomer c SET c.password = :newPassword WHERE c.parentCustId = :parentCustId")
                    .setParameter("newPassword",newPassword)
                    .setParameter("parentCustId", parentCustId)
                    .executeUpdate();
        }
    }
}
