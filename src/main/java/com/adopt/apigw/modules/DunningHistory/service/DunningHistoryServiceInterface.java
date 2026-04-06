package com.adopt.apigw.modules.DunningHistory.service;

import com.adopt.apigw.modules.DunningHistory.domain.DunningHistory;

import java.util.List;

public interface DunningHistoryServiceInterface {

    List<DunningHistory> findAllCustomerDunningHistory();
}
