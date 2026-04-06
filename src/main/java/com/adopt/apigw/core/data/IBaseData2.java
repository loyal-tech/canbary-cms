package com.adopt.apigw.core.data;


import java.io.Serializable;

public interface IBaseData2<K extends Serializable> {
    K getPrimaryKey();
    void setDeleteFlag(boolean deleteFlag);
    boolean getDeleteFlag();
    void setBuId(Long buId);
}

