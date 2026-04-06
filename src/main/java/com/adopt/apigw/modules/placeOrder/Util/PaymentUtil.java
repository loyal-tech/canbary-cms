package com.adopt.apigw.modules.placeOrder.Util;

import com.google.common.hash.Hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class PaymentUtil {
    public static String payUTransformPayment(Map<String, Object> payu) {
        String hashSequence = "key|txnid|amount|productinfo|firstname|email|udf1|udf2|udf3|udf4|udf5||||||SALT";
        hashSequence = hashSequence.replace("key", payu.get("key").toString());
        hashSequence = hashSequence.replace("txnid", payu.get("txnid").toString());
        hashSequence = hashSequence.replace("amount", payu.get("amount").toString());
        hashSequence = hashSequence.replace("productinfo", payu.get("productinfo").toString());
        hashSequence = hashSequence.replace("firstname", payu.get("firstname").toString());
        hashSequence = hashSequence.replace("email", payu.get("email").toString());
        hashSequence = hashSequence.replace("SALT", payu.get("salt").toString());
        hashSequence = hashSequence.replace("udf1", "");
        hashSequence = hashSequence.replace("udf2", "");
        hashSequence = hashSequence.replace("udf3", "");
        hashSequence = hashSequence.replace("udf4", "");
        hashSequence = hashSequence.replace("udf5", "");
        String hash = "";
        hash = hashCal(hashSequence);
        return hash;
    }

    public static String hashCal(String str) {
        return Hashing.sha512().hashString(str, StandardCharsets.UTF_8).toString();
    }
}

