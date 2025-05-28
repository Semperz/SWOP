package com.example.swop.data.local;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

public class SecurePrefs {
    public static SharedPreferences get(Context ctx, String prefsName) {
        try {
            return EncryptedSharedPreferences.create(
                    prefsName,
                    MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                    ctx,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear EncryptedSharedPreferences", e);
        }
    }
}
