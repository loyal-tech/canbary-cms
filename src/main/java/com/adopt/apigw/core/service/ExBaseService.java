package com.adopt.apigw.core.service;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchModel;
import com.itextpdf.text.Document;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ExBaseService<T, K> {
    List<T> getAllEntities(Integer mvnoId) throws Exception;

    T getEntityById(K id,Integer mvnoId) throws Exception;

    T getEntityById(K id, boolean flag) throws Exception;

    T getEntityForUpdateAndDelete(K id,Integer mvnoId) throws Exception;

    T saveEntity(T entity) throws Exception;

    T updateEntity(T entity) throws Exception;

    void deleteEntity(T entity) throws Exception;

    GenericDataDTO getListByPageAndSizeAndSortByAndOrderBy(Integer page, Integer size, String sortBy, Integer sortOrder, List<GenericSearchModel> filterList,Integer mvnoId) throws Exception;

    GenericDataDTO search(List<GenericSearchModel> filterList, Integer page, Integer pageSize, String sortBy, Integer sortOrder,Integer mvnoId);

    void excelGenerate(Workbook workbook, Integer mvnoId) throws Exception;

    void pdfGenerate(Document doc, Integer mvnoId) throws Exception;

    boolean deleteVerification(Integer id)throws Exception;
}
