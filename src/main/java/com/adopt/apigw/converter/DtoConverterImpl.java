package com.adopt.apigw.converter;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;

@Service
public class DtoConverterImpl<D, M> implements DtoConverter<D, M> {

    @Override
    public M convert(D dtoClass, Class<M> modelClass) {
        M model = null;
        try {
            model = modelClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(dtoClass, model);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return model;
    }
}
