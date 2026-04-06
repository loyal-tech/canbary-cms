package com.adopt.apigw.modules.FlutterWaveHelper;

import com.adopt.apigw.modules.PaymentConfig.service.PaymentConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class FlutterWaveAuthentication {
    /** This Service is for FlutterwaveAuthetication Do not add any other authetication**/

    @Autowired
    private FlutterWaveConstant flutterWaveConstant;

    protected  String getKey(){
        return flutterWaveConstant.getKEY();
    }

    protected String getURL(){
        return flutterWaveConstant.getVERIFY_URL();
    }





}
