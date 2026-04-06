package com.adopt.apigw.pojo;

import lombok.Data;

@Data
public class LocationDto implements Comparable<LocationDto>{
    private String address;
    private String latitude;
    private String longitude;
    private String name;
    private Long distance;
    private Long networkDeviceId;

    @Override
    public int compareTo(LocationDto locationDto) {
        return this.distance.intValue() - locationDto.getDistance().intValue();
    }
}
