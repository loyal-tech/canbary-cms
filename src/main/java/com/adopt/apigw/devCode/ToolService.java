package com.adopt.apigw.devCode;

import com.adopt.apigw.utils.APIConstants;
import groovy.lang.GroovyClassLoader;
import org.slf4j.MDC;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

@Service
public class ToolService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#&!";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    /** This service is for using common code that allow to create tool that can be used in other module**/
    /**Predicate here is not predicate of querydsl**/

    public  <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        final Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public JSONObject runWithGroovyClassLoader(String methodName, String path, String amount , String transactionId , String currency , String fri) {
        try {
            File tempFile = new File(path);
            System.out.println();
            if(tempFile.exists()) {
                Class scriptClass = new GroovyClassLoader().parseClass(new File(path));
                //parameter method

                    if(scriptClass.getMethod(methodName, String.class, String.class, String.class , String.class) != null) {

                        Object scriptInstance = scriptClass.newInstance();
                        Object response = scriptClass.getDeclaredMethod(methodName, String.class, String.class, String.class , String.class).invoke(scriptInstance, amount , transactionId ,currency,fri);
                        JSONObject json = XML.toJSONObject(response.toString());
                        System.out.println("groovy response: "+json.toString());
                        return json;
                    }

            } else {
                System.out.println("Groovy file not found: "+path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
        return null;

    }

    public String runWithGroovyClassLoaderWithString(String methodName, String path, String amount , String transactionId , String currency , String fri) {
        try {
            File tempFile = new File(path);
            System.out.println();
            if(tempFile.exists()) {
                Class scriptClass = new GroovyClassLoader().parseClass(new File(path));
                //parameter method

                if(scriptClass.getMethod(methodName, String.class, String.class, String.class , String.class) != null) {

                    Object scriptInstance = scriptClass.newInstance();
                    Object response = scriptClass.getDeclaredMethod(methodName, String.class, String.class, String.class , String.class).invoke(scriptInstance, amount , transactionId ,currency,fri);
                    System.out.println("groovy response: "+response.toString());
                    return response.toString();
                }

            } else {
                System.out.println("Groovy file not found: "+path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            MDC.remove(APIConstants.TYPE);
        }
        return null;

    }


    public boolean isJSONValid(String jsonStr) {
        try {
            new JSONObject(jsonStr);
        } catch (JSONException ex) {
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(jsonStr);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }


    public String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
