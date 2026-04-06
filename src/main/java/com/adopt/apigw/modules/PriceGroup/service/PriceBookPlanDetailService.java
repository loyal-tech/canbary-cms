package com.adopt.apigw.modules.PriceGroup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookPlanDetail;
import com.adopt.apigw.modules.PriceGroup.mapper.PriceBookPlanDtlMapper;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookPlanDetailDTO;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookPlanDtlRepository;

@Service
public class PriceBookPlanDetailService extends ExBaseAbstractService<PriceBookPlanDetailDTO, PriceBookPlanDetail,Long>
{
    @Autowired

    PriceBookPlanDtlRepository priceBookPlanDtlRepository;

    public PriceBookPlanDetailService(PriceBookPlanDtlRepository repository, PriceBookPlanDtlMapper mapper) {
        super(repository, mapper);
    }

    public void planIsDelete(PriceBookPlanDetail priceBookPlanDetail) throws Exception
    {
        priceBookPlanDetail.setDeleteFlag(true);
        priceBookPlanDetail.setId(priceBookPlanDetail.getId());
        priceBookPlanDtlRepository.save(priceBookPlanDetail);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBookPlanDetailsService]]";
    }
}
