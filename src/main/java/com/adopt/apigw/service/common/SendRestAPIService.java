package com.adopt.apigw.service.common;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class SendRestAPIService {
    @Autowired
    private RestTemplate restTemplate;

    public String sendPatchRequest(String jsonPayload , String endpoint) {
        try {
            // Convert JSON string to JSONObject
            JSONObject jsonObject = new JSONObject(jsonPayload);

            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Create the request
            HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), headers);

            // URL of the external API
            String url = endpoint;

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, request, String.class);

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
