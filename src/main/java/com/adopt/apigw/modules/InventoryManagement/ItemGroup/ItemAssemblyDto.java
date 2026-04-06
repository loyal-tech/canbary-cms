package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import com.adopt.apigw.core.dto.IBaseDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ItemAssemblyDto implements IBaseDto {

    private Long id;
    private String itemAssemblyName;
    private String status;

    private Long mvnoId;
    private Boolean isDeleted=false;


    private List<Long> itemListLongId=new ArrayList<>();

    private List<String> itemNameList=new ArrayList<>();


    @Override
    public Long getIdentityKey() {
        return id;
    }

    @Override
    public Integer getMvnoId() {
        return Math.toIntExact(mvnoId);
    }

    @Override
    public void setMvnoId(Integer mvnoId) {
        this.mvnoId= Long.valueOf(mvnoId);
    }

    @Override
    public String toString()
    {
        return "";
    }
}
