package com.adopt.apigw.modules.purchaseDetails.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericIdModel;
import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.modules.purchaseDetails.domain.PurchaseDetails;
import com.adopt.apigw.modules.purchaseDetails.mapper.PurchaseDetailsMapper;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseDetailsDTO;
import com.adopt.apigw.modules.purchaseDetails.model.PurchaseHistoryReqDTO;
import com.adopt.apigw.modules.purchaseDetails.queryScript.PurchaseHistoryQueryScript;
import com.adopt.apigw.modules.purchaseDetails.repository.PurchaseDetailsRepo;
import com.adopt.apigw.modules.subscriber.queryScript.SubscriberSearchQueryScript;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseDetailsService extends ExBaseAbstractService<PurchaseDetailsDTO, PurchaseDetails, Long> {
    @Autowired
    private PurchaseDetailsRepo repository;
    @Autowired
    private PurchaseDetailsMapper mapper;
    @PersistenceContext
    private EntityManager entityManager;

    public PurchaseDetailsService(PurchaseDetailsRepo repository, PurchaseDetailsMapper mapper) {
        super(repository, mapper);
        sortColMap.put("id", "purchaseid");
    }

    public PurchaseDetailsDTO getPurchaseBYTxnId(String txnId) {
        return mapper.domainToDTO(this.repository.findByTransid(txnId), new CycleAvoidingMappingContext());
    }

    @Override
    public String getModuleNameForLog() {
        return "[PurchaseDetailsService]";
    }

    public GenericDataDTO getAllPurchaseHistoryByParam(PurchaseHistoryReqDTO reqDTO) {
        String SUBMODULE = getModuleNameForLog() + " [getAllPurchaseHistoryByParam()] ";
        GenericDataDTO genericDataDTO = new GenericDataDTO();
        Pageable pageable = generatePageRequest(reqDTO.getPage(), reqDTO.getPageSize(), reqDTO.getSortBy()
                , reqDTO.getSortOrder());
        StringBuilder commonQuery = new StringBuilder(PurchaseHistoryQueryScript.COMMON_QUERY);
        StringBuilder whereCondition = new StringBuilder("");
        StringBuilder join = new StringBuilder("");
        String finalQuery = "";
        try {
            boolean flag = false;
            if (null != reqDTO) {

               /* if (getLoggedInUserPartnerId() != CommonConstants.DEFAULT_PARTNER_ID) {
                    String[] roleList = getLoggedInUser().getRolesList().split(",");
                    if (roleList.length > 0 && Arrays.stream(roleList).anyMatch("8"::equalsIgnoreCase)) {
                        whereCondition.append(PurchaseHistoryQueryScript.CUST_ID + " = " + getLoggedInUserId());
                    } else {
                        join.append(PurchaseHistoryQueryScript.CUST_JOIN + getLoggedInUserPartnerId());
                        whereCondition.append(PurchaseHistoryQueryScript.PARTNER_ID + " = " + getLoggedInUserPartnerId());
                    }
                    flag = true;
                }*/
                if (null != reqDTO.getStartDate() && null != reqDTO.getEndDate()) {
                    whereCondition.append(PurchaseHistoryQueryScript.PURCHASE_DATE_BETWEEN + PurchaseHistoryQueryScript.CONCAT + " ('" + reqDTO.getStartDate() + "', ' 00:00:00 ' )" + " and "
                            + PurchaseHistoryQueryScript.CONCAT + "('" + reqDTO.getEndDate() + "', ' 23:59:59 ' )");
                    flag = true;
                }
                if (reqDTO.getCustFlag()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.PARTNER_ID + PurchaseHistoryQueryScript.IS_NULL);
                    flag = true;
                }
                if (reqDTO.getPartnerFlag()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.CUST_ID + PurchaseHistoryQueryScript.IS_NULL);
                    flag = true;
                }
                if (null != reqDTO.getPartnerId()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.PARTNER_ID + " = " + reqDTO.getPartnerId());
                    flag = true;
                }
                if (null != reqDTO.getCustId()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.CUST_ID + " = " + reqDTO.getCustId());
                    flag = true;
                }
                if (null != reqDTO.getPaymentStatus()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.PAYMENT_STATUS + " = '" + reqDTO.getPaymentStatus() + "'");
                    flag = true;
                }
                if (null != reqDTO.getPurchaseStatus()) {
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.PURCHASE_STATUS + " = '" + reqDTO.getPurchaseStatus() + "'");
                    flag = true;
                }
                if (null != reqDTO.getPgId()) {
                    join.append(PurchaseHistoryQueryScript.PG_JOIN);
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.PG_ID + " = " + reqDTO.getPgId() + "");
                    flag = true;
                }
                if (null != reqDTO.getOrderType()) {
                    join.append(PurchaseHistoryQueryScript.ORDER_JOIN);
                    if (flag)
                        whereCondition.append(PurchaseHistoryQueryScript.AND);
                    whereCondition.append(PurchaseHistoryQueryScript.ORDER_TYPE + " = '" + reqDTO.getOrderType() + "'");
                }

                commonQuery.append(" " + (join.length() > 0 ? join : " ") + SubscriberSearchQueryScript.WHERE + whereCondition);
                finalQuery = commonQuery.toString();
                Query q = entityManager.createNativeQuery(finalQuery, GenericIdModel.class);
                List<GenericIdModel> resultList = q.getResultList();
                if (null != resultList && 0 < resultList.size()) {
                    List<String> idList = new ArrayList<>();
                    for (GenericIdModel idModel : resultList) {
                        idList.add(idModel.getId().toString());
                    }
                    return makeGenericResponse(genericDataDTO, repository.findAllBy(pageable, idList));
                }
            }
        } catch (Exception ex) {
            ApplicationLogger.logger.error(SUBMODULE + ex.getMessage(), ex);
            throw ex;
        }
        return genericDataDTO;
    }


}
