package com.adopt.apigw.modules.InventoryManagement.ItemGroup;

import lombok.Data;

import java.util.List;

@Data
public class ItemAssemblyProductMappingDto {

    private Long id;

    private Long itemid;

    private List<ItemAssemblyDto>  itemGroup;


}
