package com.adopt.apigw.model.postpaid;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AssignServiceArea {

    private Long serviceAreaId;

    private List<Integer> planIds;
}
