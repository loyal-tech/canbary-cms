package com.adopt.apigw.dialShreeModule;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DynamicDataDTO {
    @JsonProperty("lead_id")
    private String leadId;

    @JsonProperty("disposition")
    private String disposition;

    @JsonProperty("callback_date")
    private String callbackDate;

    @JsonProperty("callback_datetime")
    private String callbackDatetime;

    @JsonProperty("my_callback")
    private String myCallback;

    @JsonProperty("select_country")
    private String selectCountry;

    @JsonProperty("select_state")
    private String selectState;

    @JsonProperty("select_city")
    private String selectCity;

    @JsonProperty("DialButtonField")
    private String dialButtonField;


}
