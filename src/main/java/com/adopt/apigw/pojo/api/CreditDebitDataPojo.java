package com.adopt.apigw.pojo.api;

import com.adopt.apigw.model.postpaid.CreditDocument;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class CreditDebitDataPojo {


   private Integer id;

   private Double amount;
}
