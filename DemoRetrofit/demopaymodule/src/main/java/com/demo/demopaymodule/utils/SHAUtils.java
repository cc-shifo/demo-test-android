package com.demo.demopaymodule.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHAUtils {
    public static String sha256(String data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        digest.update(bytes);
        return HexUtil.encodeHexStr(digest.digest(), true);
    }

}
