package com.adopt.apigw.core.service;

import java.util.List;

public interface BaseService<T,K> {
    List<T> getAllEntities();
    T getEntityById(K id);
    T saveEntity(T entity);
    void deleteEntity(T entity);
}
