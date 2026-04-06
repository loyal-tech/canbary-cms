package com.adopt.apigw.modules.FlutterWaveHelper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



@Component
public class FlutterWaveConstant {



    /**
     * This key is testing key
     * Please change live key before commit
     * **/
    @Value("${FLWKEY}")
    private String KEY;

    /** you can test this url using live or testing sendboxapi
     * Please change url with live before commiting code
     * **/
    @Value("${VERIFYURL}")
    private String VERIFY_URL;


    public String getKEY() {
        return KEY;
    }

    public void setKEY(String KEY) {
        this.KEY = KEY;
    }

    public String getVERIFY_URL() {
        return VERIFY_URL;
    }

    public void setVERIFY_URL(String VERIFY_URL) {
        this.VERIFY_URL = VERIFY_URL;
    }
}
