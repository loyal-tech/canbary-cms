package com.adopt.apigw.modules.PriceGroup.service;

import com.adopt.apigw.core.service.ExBaseAbstractService;
import com.adopt.apigw.modules.PriceGroup.domain.PriceBookSlabDetails;
import com.adopt.apigw.modules.PriceGroup.mapper.PriceBookSlabDetailsMapper;
import com.adopt.apigw.modules.PriceGroup.model.PriceBookSlabDetailsDTO;
import com.adopt.apigw.modules.PriceGroup.repository.PriceBookSlabDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PriceBookSlabDetailsService extends ExBaseAbstractService<PriceBookSlabDetailsDTO, PriceBookSlabDetails,Long> {

    @Autowired
    private PriceBookSlabDetailsRepository priceBookSlabDetailsRepository;

    public PriceBookSlabDetailsService(PriceBookSlabDetailsRepository repository, PriceBookSlabDetailsMapper mapper) {
        super(repository, mapper);
    }

    public void IsDelete(PriceBookSlabDetails priceBookSlabDetail) throws Exception
    {
        priceBookSlabDetail.setDeleteFlag(true);
        priceBookSlabDetail.setId(priceBookSlabDetail.getId());
        priceBookSlabDetailsRepository.save(priceBookSlabDetail);
    }

    @Override
    public String getModuleNameForLog() {
        return "[PriceBookSlabDetailsService]]";
    }
}
