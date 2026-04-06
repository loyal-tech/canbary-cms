package com.adopt.apigw.model.radius;

public enum PlanType {
    TIME("TIME"), VOLUME("VOLUME");
    private String value;

    PlanType(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
