package com.adopt.apigw.modules.subscriber.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentTypeModel {

    private String text;
    private String value;
    private List<DocumentTypeModel> subTypeList = new ArrayList<>();
}
