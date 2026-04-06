package com.adopt.apigw.modules.MvnoDiscountManagement.MvnoDicountImpl;


import com.adopt.apigw.core.mapper.CycleAvoidingMappingContext;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import com.adopt.apigw.modules.MvnoDiscountManagement.*;
import com.adopt.apigw.pojo.api.ChargePojo;
import com.adopt.apigw.repository.postpaid.ChargeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MvnoDicountServiceImpl implements MvnoDiscountService {

    @Autowired
    private MvnoDiscountMappingRepository discountMappingRepository;

    @Autowired
    private MvnoRepository mvnoRepository;

    @Autowired
    private MvnoDiscountMapper mvnoDiscountMapper;

    @Autowired
    private ChargeRepository chargeRepository;

    @Override
    public MvnoDiscountDTO saveMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO) {
        try {
            List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountDTO.getMvnoDiscountMappings();
            if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                List<MvnoDiscountMapping> mvnoDiscountMappings = new ArrayList<>();//mvnoDiscountMapper.dtoToDomain(mvnoDiscountMappingDTOS, new CycleAvoidingMappingContext());
                for (MvnoDiscountMappingDTO discountMapping: mvnoDiscountMappingDTOS) {
                    discountMapping.setChargeName(chargeRepository.findChargeNameById(discountMapping.getChargeId().intValue()));
                    mvnoDiscountMappings.add(mvnoDiscountMapper.dtoToDomain(discountMapping, new CycleAvoidingMappingContext()));
                }
                mvnoDiscountMappings = discountMappingRepository.saveAll(mvnoDiscountMappings);
                mvnoDiscountMappingDTOS = mvnoDiscountMapper.domainToDTO(mvnoDiscountMappings, new CycleAvoidingMappingContext());
                mvnoDiscountDTO.setMvnoDiscountMappings(mvnoDiscountMappingDTOS);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable To save Mvno Discount");
        }
        return mvnoDiscountDTO;
    }

    @Override
    public MvnoDiscountDTO updateMvnoDiscount(MvnoDiscountDTO mvnoDiscountDTO) {
        try {
            if(deleteAllMvnoDiscountByMvnoId(mvnoDiscountDTO.getMvnoId())) {
                List<MvnoDiscountMappingDTO> mvnoDiscountMappingDTOS = mvnoDiscountDTO.getMvnoDiscountMappings();
                if(!CollectionUtils.isEmpty(mvnoDiscountMappingDTOS)) {
                    saveMvnoDiscount(mvnoDiscountDTO);
                }
            } else {
                throw new RuntimeException("Unable To Update Mvno Discount");
            }
        } catch (Exception ex) {
            throw new RuntimeException("Unable To Update Mvno Discount, Please retry after some time..!");
        }
        return mvnoDiscountDTO;
    }

    @Override
    public boolean deleteAllMvnoDiscountByMvnoId(Long mvnoId) {
        try {
            discountMappingRepository.deleteAllByMvnoId(mvnoId);
            return true;
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public List<MvnoDiscountMappingDTO> fetchAllMvnoDiscountDetailByMvnoId(Long mvnoId) {
        List<MvnoDiscountMapping> mvnoDiscountMappings = discountMappingRepository.findAllByMvnoId(mvnoId);
        if(!CollectionUtils.isEmpty(mvnoDiscountMappings)) {
            return mvnoDiscountMapper.domainToDTO(mvnoDiscountMappings, new CycleAvoidingMappingContext());
        } else {
            return null;
        }
    }
}
