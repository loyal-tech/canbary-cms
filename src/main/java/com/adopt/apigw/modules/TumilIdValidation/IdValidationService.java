package com.adopt.apigw.modules.TumilIdValidation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IdValidationService {

    @Autowired
    IdValidationRepository  idValidationRepository;

    public void save(IdValidationResponse idValidationResponse) {
        if (idValidationResponse != null) {
            idValidationRepository.save(idValidationResponse);
        } else {
            log.warn("Failed To save HouseHold Id Validation Response");
            throw new RuntimeException("Failed To save HouseHold Id Validation Response");
        }
    }


}
