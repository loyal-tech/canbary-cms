package com.adopt.apigw.converter;

import org.springframework.stereotype.Service;

@Service
public interface DtoConverter<D, M> {
    M convert(D dto, Class<M> modelClass);
}
