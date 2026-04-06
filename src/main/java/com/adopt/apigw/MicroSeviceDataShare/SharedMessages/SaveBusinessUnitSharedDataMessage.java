package com.adopt.apigw.MicroSeviceDataShare.SharedMessages;

import com.adopt.apigw.modules.InvestmentCode.Domain.InvestmentCode;
import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class SaveBusinessUnitSharedDataMessage {


    private Long id;

    private String buname;

    private String bucode;

    private String status;


    private String planBindingType;


    private Boolean isDeleted ;


    private Integer mvnoId;


    private List<InvestmentCode> investmentCodeid=new ArrayList<>();
    private Integer createdById;
    private Integer lastModifiedById;
    private String createdByName;

    private String lastModifiedByName;
}
