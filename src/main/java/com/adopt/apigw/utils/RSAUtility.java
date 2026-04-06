package com.adopt.apigw.utils;


import java.util.*;

import com.adopt.apigw.constants.PGConstants;
import com.adopt.apigw.core.utillity.log.ApplicationLogger;

public class RSAUtility {

    public static String encrypt(String key, String plainText) {
        try {
            AesCryptUtil aesUtil = new AesCryptUtil(key);
            String encRequest = aesUtil.encrypt(plainText);
            return encRequest;
        } catch (Exception e) {
            ApplicationLogger.logger.error("RSAUtility Encrypt " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static Map decrypt(String key, String encResp) {
        try {
            AesCryptUtil aesUtil = new AesCryptUtil(key);
            String decResp = aesUtil.decrypt(encResp);
            StringTokenizer tokenizer = new StringTokenizer(decResp, "&");
            Hashtable hs = new Hashtable();
            String pair = null, pname = null, pvalue = null;
            while (tokenizer.hasMoreTokens()) {
                pair = (String) tokenizer.nextToken();
                if (pair != null) {
                    StringTokenizer strTok = new StringTokenizer(pair, "=");
                    pname = "";
                    pvalue = "";
                    if (strTok.hasMoreTokens()) {
                        pname = (String) strTok.nextToken();
                        if (strTok.hasMoreTokens())
                            pvalue = (String) strTok.nextToken();
                        hs.put(pname, pvalue);
                    }
                }
            }
            return converHashTableToHashMap(hs);
        } catch (Exception e) {
            ApplicationLogger.logger.error("RSAUtility Encrypt " + e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    public static String addToPostParams(String paramKey, String paramValue) {
        if (paramValue != null)
            return paramKey.concat(PGConstants.PARAMETER_EQUALS).concat(paramValue)
                    .concat(PGConstants.PARAMETER_SEP);
        return "";
    }

    public static Map converHashTableToHashMap(Hashtable data) {
        if (data == null) {
            return null;
        }/*ww w.  j  a  v  a  2  s. co m*/
        HashMap retHashMap = new HashMap();
        Iterator iter = data.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = String.valueOf(entry.getValue());
            retHashMap.put(key, value);
        }
        return retHashMap;
    }
}
