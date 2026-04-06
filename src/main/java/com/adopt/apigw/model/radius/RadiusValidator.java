package com.adopt.apigw.model.radius;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class RadiusValidator {

    private String status;
    private String acctPort;
    private String authPort;
    private String responseCode;

    @JsonIgnore
    @Override
    public String toString() {
        return "Value{" +
                "status=" + status +
                ", responseCode='" + responseCode + '\'' +
                ", authport='" + authPort + '\'' +
                ", acctport='" + acctPort + '\'' +
                '}';
    }

}
