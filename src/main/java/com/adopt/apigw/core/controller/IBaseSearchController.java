package com.adopt.apigw.core.controller;

import com.adopt.apigw.core.dto.GenericDataDTO;
import com.adopt.apigw.core.dto.GenericSearchDTO;

public interface IBaseSearchController {
    GenericDataDTO search(GenericSearchDTO filterList);
}
