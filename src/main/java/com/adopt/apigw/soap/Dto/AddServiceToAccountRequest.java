package com.adopt.apigw.soap.Dto;

import java.util.List;

public class AddServiceToAccountRequest {
    private String actionItem;
    private String requestId;
    private String userName;
    private String serviceId;

    public String getActionItem() {
        return actionItem;
    }

    public void setActionItem(String actionItem) {
        this.actionItem = actionItem;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}
