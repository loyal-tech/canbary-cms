package com.adopt.apigw.modules.InventoryManagement.item;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ItemChangeTypeDto {
    private Long id;
    private Long itemId;
    private String condition;
    private String remarks;
    private String filename;
    private String uniquename;
    private String otherreason;
  //  private MultipartFile file;

}
