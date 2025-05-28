package com.example.swop.data.util;

import com.auth0.android.jwt.JWT;

import java.util.Date;

public class JwtUtils {
    public static boolean isExpired(String token) {
        try {
            JWT jwt = new JWT(token);
            Date exp = jwt.getExpiresAt();
            return exp != null && exp.before(new Date());
        } catch (Exception e) {
            return true; // token corrupto tratado como expirado
        }
    }
}

