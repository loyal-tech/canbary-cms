package com.adopt.apigw.controller.invoice.invoiceserver;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RestResponse {

  @JsonProperty
  private RestResponse RestResponse;
  
  public RestResponse getRestResponse() {
    return RestResponse;
  }

  public void setRestResponse(RestResponse restResponse) {
    RestResponse = restResponse;
  }

  public RestResponse(){
    
  }

  @Override
  public String toString() {
    return "Response [RestResponse=" + RestResponse + "]";
  }

}
