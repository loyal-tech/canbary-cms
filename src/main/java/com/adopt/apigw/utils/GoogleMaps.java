package com.adopt.apigw.utils;

import com.adopt.apigw.constants.DocumentConstants;
import com.adopt.apigw.constants.MapConstants;
import com.adopt.apigw.modules.NetworkDevices.domain.NetworkDevices;
import com.adopt.apigw.modules.NetworkDevices.repository.NetworkDeviceRepository;
import com.adopt.apigw.pojo.LocationDto;
import com.adopt.apigw.pojo.LocationPlace;
import com.adopt.apigw.pojo.LocationVo;
import com.adopt.apigw.service.common.ClientServiceSrv;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class GoogleMaps {

    @Autowired
    private NetworkDeviceRepository networkDeviceRepository;
    private String googleMapKey1;

    @Autowired
    private  ClientServiceSrv clientServiceSrv;

//    @Autowired
//    GoogleMaps(ClientServiceSrv clientServiceServ){
//        this.googleMapKey1 = clientServiceServ.getByName("google_maps_key").getValue();
//    }

    public HashMap getPlaces(String query) {
        HashMap<String, Object> resp = new HashMap<>();
        List<LocationPlace> locationPlaceList = new ArrayList<>();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();
            String googleMapKey = clientServiceSrv.getByName("google_maps_key").getValue();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/queryautocomplete/json?input="+query+"&key="+googleMapKey)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);

//            StaffUser staffUser = new Gson().fromJson(response.body().string(), StaffUser.class);
            if(jobj.has(MapConstants.STATUS) && jobj.get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                if (jobj.has(MapConstants.PREDICTIONS)) {
                    for (JsonElement jsonElement : jobj.get(MapConstants.PREDICTIONS).getAsJsonArray()) {
                        LocationPlace locationPlace = new LocationPlace();
                        JsonObject jsonObject = jsonElement.getAsJsonObject();
                        if (jsonObject.has(MapConstants.DESCRIPTION))
                            locationPlace.setAddress(jsonObject.get(MapConstants.DESCRIPTION).getAsString());
                        if (jsonObject.has(MapConstants.PLACE_ID))
                            locationPlace.setPlaceId(jsonObject.get(MapConstants.PLACE_ID).getAsString());
                        if (jsonObject.has(MapConstants.STRUCTURED_FORMATTING))
                            locationPlace.setName(jsonObject.get(MapConstants.STRUCTURED_FORMATTING).getAsJsonObject().get(MapConstants.MAIN_TEXT).getAsString());
                        locationPlaceList.add(locationPlace);
                    }
                }
                resp.put(MapConstants.LOCATIONS, locationPlaceList);
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
            } else {
                resp.put("error", jobj.has("error_message") ? jobj.get("error_message").getAsString() : "Location not found '" + query + "', please try with another location");
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
            }
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    public HashMap getLatitudeAndLongitude(String placeId) {
        HashMap<String, Object> resp = new HashMap<>();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();
            String googleMapKey = clientServiceSrv.getByName("google_maps_key").getValue();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/place/details/json?fields=name,geometry,formatted_address&place_id="+placeId+"&key="+googleMapKey)
                    .method("GET", null)
                    .build();
            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);

            if(jobj.has(MapConstants.STATUS) && jobj.get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                if (jobj.has(MapConstants.RESULT)) {
                    LocationVo locationVo = new LocationVo();
                    JsonObject jsonObject = jobj.get(MapConstants.RESULT).getAsJsonObject();
                    if (jsonObject.has(MapConstants.GEOMETRY) && jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().has(MapConstants.LOCATION))
                        locationVo.setLatitude(jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LAT).getAsString());
                    if (jsonObject.has(MapConstants.GEOMETRY) && jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().has(MapConstants.LOCATION))
                        locationVo.setLongitude(jsonObject.get(MapConstants.GEOMETRY).getAsJsonObject().get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LNG).getAsString());
                    resp.put(MapConstants.LOCATION, locationVo);
                    resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
                }
            } else {
                resp.put("error", jobj.has("error_message") ? jobj.get("error_message").getAsString() : "Issue occurred while fetching location by placeId : "+placeId);
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
            }
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    /*

    public HashMap myLocation() {
        HashMap<String, Object> resp = new HashMap<>();
        LocationVo locationVo = new LocationVo();
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            Request request = new Request.Builder()
                    .url("https://www.googleapis.com/geolocation/v1/geolocate?key="+googleMapKey)
                    .post(okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ""))
                    .build();
            Response response = client.newCall(request).execute();
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);

            if(jobj.has(MapConstants.LOCATION) && jobj.get(MapConstants.LOCATION).getAsJsonObject().has(MapConstants.LAT) && jobj.get(MapConstants.LOCATION).getAsJsonObject().has(MapConstants.LNG)){
                locationVo.setLatitude(jobj.get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LAT).getAsString());
                locationVo.setLongitude(jobj.get(MapConstants.LOCATION).getAsJsonObject().get(MapConstants.LNG).getAsString());
                resp.put(MapConstants.LOCATION, locationVo);
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
            } else {
                String msg = jobj.has("error") && jobj.get("error").getAsJsonObject().has("message") ? jobj.get("error").getAsJsonObject().get("message").getAsString() : "Issue occurred while fetching location";
                resp.put("error", msg);
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
            }

            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

 */
    public HashMap getNearbyDevices(LocationVo locationVo) {
        HashMap<String, Object> resp = new HashMap<>();
        try {
            String formatedDestinations = "";
            String formatedOrigin = locationVo.getLatitude()+","+locationVo.getLongitude();
            List<LocationDto> locationDtoList = new ArrayList<>();
            List<NetworkDevices> networkDevices = networkDeviceRepository.getAllSplitters();
            // TODO: pass mvnoID manually 6/5/2025
            if(!CollectionUtils.isEmpty(networkDevices) && clientServiceSrv.getMvnoIdFromCurrentStaff(null) != null) {
                networkDevices = networkDevices.stream().filter(netDev -> Objects.equals(netDev.getMvnoId(), clientServiceSrv.getMvnoIdFromCurrentStaff(null))).collect(Collectors.toList());
            }
            List<String> formatedLatLng = new ArrayList<>();

            if(networkDevices.size() == 0){
                resp.put("error", "No Splitter Profile available");
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.INTERNAL_SERVER_ERROR.value());
                return resp;
            }
            for(NetworkDevices networkDevice : networkDevices){
                formatedLatLng.add(networkDevice.getLatitude()+","+networkDevice.getLongitude());
            }
            for(int i=0; i<formatedLatLng.size(); i++){
                if(i == 0)
                    formatedDestinations = formatedLatLng.get(i);
                else
                    formatedDestinations = formatedDestinations + "|" + formatedLatLng.get(i);
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .callTimeout(30, TimeUnit.SECONDS)
                    .build();

            String googleMapKey = clientServiceSrv.getByName("google_maps_key").getValue();
            Request request = new Request.Builder()
                    .url("https://maps.googleapis.com/maps/api/distancematrix/json?destinations="+formatedDestinations+"&origins="+formatedOrigin+"&key="+googleMapKey)
                    .post(okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), ""))
                    .build();
            Response response = client.newCall(request).execute();

            int index = 0;
            JsonObject jobj = new Gson().fromJson(response.body().string(), JsonObject.class);
            if(jobj.has(MapConstants.STATUS) && jobj.get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                JsonArray jsonArray = jobj.has(MapConstants.DESTINATION_ADDRESSES) ? jobj.get(MapConstants.DESTINATION_ADDRESSES).getAsJsonArray() : new JsonArray();
                for (int i = 0; i < jsonArray.size(); i++) {
                    if (jobj.has(MapConstants.ROWS) && jobj.get(MapConstants.ROWS).getAsJsonArray().get(0).getAsJsonObject().has(MapConstants.ELEMENTS)) {
                        for (JsonElement element : jobj.get(MapConstants.ROWS).getAsJsonArray().get(0).getAsJsonObject().get(MapConstants.ELEMENTS).getAsJsonArray()) {
                            if (element.getAsJsonObject().has(MapConstants.STATUS) && element.getAsJsonObject().get(MapConstants.STATUS).getAsString().equalsIgnoreCase("OK")) {
                                LocationDto locationDto = new LocationDto();
                                locationDto.setLatitude(networkDevices.get(index).getLatitude());
                                locationDto.setLongitude(networkDevices.get(index).getLongitude());
                                locationDto.setNetworkDeviceId(networkDevices.get(index).getId());
                                locationDto.setName(networkDevices.get(index).getName());
                                locationDto.setDistance(meterToKm(element.getAsJsonObject().get(MapConstants.DISTANCE).getAsJsonObject().get("value").getAsLong()));
                                locationDto.setAddress(jsonArray.get(index).getAsString());
                                locationDtoList.add(locationDto);
                            }
                            index += 1;
                        }
                        Collections.sort(locationDtoList);
                        locationDtoList = locationDtoList.stream().limit(10).collect(Collectors.toList());
                        resp.put("locations", locationDtoList);
                        resp.put(DocumentConstants.STATUS_CODE, HttpStatus.OK.value());
                        return resp;
                    }
                }
            } else {
                resp.put("error", jobj.has("error_message") ? jobj.get("error_message").getAsString() : "Issue occurred while executing the query : ");
                resp.put(DocumentConstants.STATUS_CODE, HttpStatus.UNPROCESSABLE_ENTITY.value());
            }
            return resp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private Long meterToKm(Long meter){
        Long rem = meter % 1000;
        Long result = meter / 1000;
        if(rem >= 500)
            result+=1;
        return result;
    }
}
