package com.adopt.apigw.modules.DunningHistory.service;

import com.adopt.apigw.constants.ClientServiceConstant;
import com.adopt.apigw.core.dto.PaginationRequestDTO;
import com.adopt.apigw.modules.DunningHistory.domain.DunningHistory;
import com.adopt.apigw.modules.DunningHistory.domain.QDunningHistory;
import com.adopt.apigw.modules.DunningHistory.repository.DunningHistoryRepository;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.adopt.apigw.service.radius.AbstractService;
import com.adopt.apigw.utils.CommonConstants;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DunningHistoryService{



//    public Integer  AGE = Integer.parseInt(clientServiceSrv.getClientSrvByName(ClientServiceConstant.DEFAULT_PAGE).get(0).getValue());
    @Autowired
    private DunningHistoryRepository dunningHistoryRepository;





    public Page<DunningHistory> findAllDunningHistory(PaginationRequestDTO requestDTO){
        QDunningHistory qDunningHistory = QDunningHistory.dunningHistory;
        BooleanExpression booleanExpression = qDunningHistory.isNotNull();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));;
        Page<DunningHistory> findAllDunningHistory = dunningHistoryRepository.findAll(booleanExpression , pageable);
        return findAllDunningHistory;
    }


    public Page<DunningHistory> findAllByPartnerOrCustomerDunningHistory(PaginationRequestDTO requestDTO){
        QDunningHistory qDunningHistory = QDunningHistory.dunningHistory;
        BooleanExpression booleanExpression = qDunningHistory.isNotNull();
        if(requestDTO.getPage() > 0){
            requestDTO.setPage(requestDTO.getPage()-1);
        }
        if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("customer")){
            booleanExpression = booleanExpression.and(qDunningHistory.custid.eq(Integer.parseInt(requestDTO.getFilters().get(0).getFilterValue())));
        }
        if(requestDTO.getFilters().get(0).getFilterColumn().equalsIgnoreCase("partner")){
            booleanExpression = booleanExpression.and(qDunningHistory.partnerid.eq(Long.parseLong(requestDTO.getFilters().get(0).getFilterValue())));
        }
        Pageable pageable = PageRequest.of(requestDTO.getPage(), requestDTO.getPageSize(), Sort.by(Sort.Direction.DESC, "id"));
        Page<DunningHistory> findAllDunningHistory = dunningHistoryRepository.findAll(booleanExpression , pageable);
        return findAllDunningHistory;
    }








}
