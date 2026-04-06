package com.adopt.apigw.modules.MtnPayment.model;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class URL {
    @Value("${CURLSENDERURL}")
    private String CURL_URL;


    @Value("${groovy.file.path}")
    private String groovyFilPath;

    public String getGroovyFilPath() {
        return groovyFilPath;
    }

    public void setGroovyFilPath(String groovyFilPath) {
        this.groovyFilPath = groovyFilPath;
    }

    public String getCURL_URL() {
        return CURL_URL;
    }

    public void setCURL_URL(String CURL_URL) {
        this.CURL_URL = CURL_URL;
    }
}
