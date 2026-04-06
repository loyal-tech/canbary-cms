package com.adopt.apigw.service.common;

import com.adopt.apigw.model.postpaid.CustQuotaDetails;
import com.adopt.apigw.model.postpaid.CustServiceChargeIPDetails;
import com.adopt.apigw.pojo.api.CustQuotaDtlsPojo;
import com.adopt.apigw.pojo.api.CustServiceChargeIPDetailsPojo;
import com.adopt.apigw.repository.common.CustQuotaRepository;
import com.adopt.apigw.repository.common.CustServiceChargeIPDetailsRepo;
import com.adopt.apigw.service.radius.AbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CustServiceChargeIPDetailsService extends AbstractService<CustServiceChargeIPDetails, CustServiceChargeIPDetailsPojo, Integer> {
    @Autowired
    private CustServiceChargeIPDetailsRepo entityRepository;

    @Override
    protected JpaRepository<CustServiceChargeIPDetails, Integer> getRepository() {
        return entityRepository;
    }
}
