package com.business.support;


import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SignatureGenerate {

    private static final String ENCODING = "UTF-8";
    // 以下是一段计算签名的示例代码
    final static String ALGORITHM = "HmacSHA1";
    final static String HTTP_METHOD = "GET";

    private static String percentEncode(String value) throws UnsupportedEncodingException {
        return value != null ? URLEncoder.encode(value, ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~") : null;
    }

    private static final String ISO8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private static String formatIso8601Date(Date date) {
        SimpleDateFormat df = new SimpleDateFormat(ISO8601_DATE_FORMAT);
        df.setTimeZone(new SimpleTimeZone(0, "GMT"));
        return df.format(date);
    }


    public static String createSignature() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        Map<String, String> parameters = new HashMap<>();
// 输入请求参数
        parameters.put("Action", "DescribeRegions");
        parameters.put("Version", "2014-05-26");
        parameters.put("AccessKeyId", "testid");
        parameters.put("Timestamp", formatIso8601Date(new Date()));
        parameters.put("SignatureMethod", "HMAC-SHA1");
        parameters.put("SignatureVersion", "1.0");
        parameters.put("SignatureNonce", UUID.randomUUID().toString());
        parameters.put("Format", "XML");
// 排序请求参数
        String[] sortedKeys = parameters.keySet().toArray(new String[]{});
        Arrays.sort(sortedKeys);
        final String SEPARATOR = "&";
// 构造 stringToSign 字符串
        StringBuilder stringToSign = new StringBuilder();
        stringToSign.append(HTTP_METHOD).append(SEPARATOR);
        stringToSign.append(percentEncode("/")).append(SEPARATOR);
        StringBuilder canonicalizedQueryString = new StringBuilder();
        for (String key : sortedKeys) {
// 这里注意编码 key 和 value
            canonicalizedQueryString.append("&")
                    .append(percentEncode(key)).append("=")
                    .append(percentEncode(parameters.get(key)));
        }
// 这里注意编码 canonicalizedQueryString
        stringToSign.append(percentEncode(
                canonicalizedQueryString.toString().substring(1)));


        String key = "testsecret&";
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(key.getBytes(ENCODING), ALGORITHM));
        byte[] signData = mac.doFinal(stringToSign.toString().getBytes(ENCODING));
        return new String(java.util.Base64.getEncoder().encode(signData));
    }
}
