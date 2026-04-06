package com.adopt.apigw.service.common;

import com.adopt.apigw.exception.CustomValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class WifiService {
    private static final Logger logger = LoggerFactory.getLogger(WifiService.class);

    @Value("${awfis.member.login}")
    private String memberLoginURL;

    @Value("${awfis.resetpassword.api}")
    private String resetPasswordURL;

    @Value("${awfis.guest.login}")
    private String guestLoginURL;

    @Value("${awfis.guest.appsecret}")
    private String appSecrete;

    public String preprocess(String username, String password, String cid, String password_confirm) {
        String result = "{\"status\": 0}";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            if(password_confirm != null) {
                result = resetPassword(httpclient, username, password, password_confirm);
            } else {
                if(username.contains("@")) {
                    result = getMemberLogin(httpclient, username, password, cid);
                } else {
                    result = getGuestLogin(httpclient, username, password, cid);
                }
            }

        } catch (URISyntaxException uriSyntaxException) {
            throw new CustomValidationException(uriSyntaxException.hashCode(), "Exception at wifiservice: ", uriSyntaxException);
        } catch (IOException ioException) {
            throw new CustomValidationException(ioException.hashCode(), "Exception at wifiservice: ", ioException);
        }finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public String getMemberLogin(CloseableHttpClient httpclient, String username, String password, String cid) throws URISyntaxException, IOException {
        String result = "";
        try {
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(memberLoginURL+cid))
                    .addParameter("app-secret",appSecrete)
                    .addParameter("username", username)
                    .addParameter("password", password)
                    .build();

            logger.info("Member login api call: "+httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("customer login preprocess parameterised: "+username);
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
            System.out.println("member login: "+result);
        }catch (Exception ex) {
            logger.error("Error in getMemberLogin: "+ex.getMessage());
        }
        return result;
    }

    public String resetPassword(CloseableHttpClient httpclient, String username, String password, String password_confirm) throws URISyntaxException, IOException {
        String result = "";
        try {
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(resetPasswordURL))
                    .addParameter("app-secret",appSecrete)
                    .addParameter("username", username)
                    .addParameter("password", password)
                    .addParameter("password_confirm", password_confirm)
                    .build();
            logger.info("Reset password api call: "+httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("reset Password response: "+response);
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
            System.out.println("member login: "+result);
            logger.info("member login response: "+result+" for username: "+username);
        } catch (Exception ex) {
            logger.error("Exception in resetPassword: "+ex.getMessage());
        }
        return result;
    }

    public String getGuestLogin(CloseableHttpClient httpclient, String username, String password, String cid) throws URISyntaxException, IOException {
        String result = "";
        try {
            if(username.contains("$")) {
                username = username.substring(0, username.indexOf("$"));
            }
            System.out.println("API:"+guestLoginURL);
            HttpUriRequest httppost = RequestBuilder.post()
                    .setUri(new URI(guestLoginURL+cid))
                    .addParameter("meeting_id", username)
                    .addParameter("app-secret",appSecrete)
                    .addParameter("password", password)
                    .build();

            logger.info("Guest Login api call: "+httppost.getURI());
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println("guest login");
                result = EntityUtils.toString(response.getEntity());
            } finally {
                response.close();
            }
            System.out.println("guest login: "+result);
            logger.info("guest login response: "+result);
        } catch (Exception ex) {
            logger.error("Exception in getGuestLogin: "+ex.getMessage());
        }
        return result;
    }

    public static String postprocess(String username, String password, String cid, String password_confirm) {
        System.out.println("customer login postprocess parameterised: "+username);
        return "Postprocess called success for customer login";
    }
}
