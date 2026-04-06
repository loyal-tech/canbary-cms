package com.adopt.apigw.modules.MvnoDiscountManagement;

import com.adopt.apigw.modules.Mvno.domain.Mvno;
import com.adopt.apigw.modules.Mvno.repository.MvnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Named;

@Component
public class MvnoMapperHelper {

    @Autowired
    private MvnoRepository mvnoRepository;

    @Named("mvnoFromId")
    public Mvno mvnoFromId(Long mvnoId) {
        return mvnoId != null ? mvnoRepository.findById(mvnoId).orElse(null) : null;
    }

    @Named("idFromMvno")
    public Long idFromMvno(Mvno mvno) {
        return mvno != null ? mvno.getId() : null;
    }
}
