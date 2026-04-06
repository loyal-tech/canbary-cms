package com.adopt.apigw.model.postpaid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCprMessage implements Serializable {
    List<Map.Entry<Integer, String>> custPackAndEndDatePair;
}
