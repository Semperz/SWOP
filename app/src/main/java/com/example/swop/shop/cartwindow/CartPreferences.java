// CartPreferences.java
package com.example.swop.shop.cartwindow;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.swop.data.local.SecurePrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CartPreferences {
    private static final String PREF_NAME = "cart_prefs";

    private static String getCartKey(String userId) {
        return "cart_items_" + userId;
    }

    public static void addProduct(Context context, String userId, long productId, int quantity) {
        Map<Long, Integer> cart = getCart(context, userId);
        cart.put(productId, quantity);
        saveCart(context, userId, cart);
    }

    public static boolean containsProduct(Context context, String userId, long productId) {
        return getCart(context, userId).containsKey(productId);
    }

    public static Map<Long, Integer> getCart(Context context, String userId) {
        SharedPreferences prefs = SecurePrefs.get(context, PREF_NAME);
        String json = prefs.getString(getCartKey(userId), "{}");
        Map<Long, Integer> cart = new HashMap<>();
        try {
            JSONObject obj = new JSONObject(json);
            Iterator<String> keys = obj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                cart.put(Long.parseLong(key), obj.getInt(key));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cart;
    }

    public static void saveCart(Context context, String userId, Map<Long, Integer> cart) {
        JSONObject obj = new JSONObject();
        for (Map.Entry<Long, Integer> entry : cart.entrySet()) {
            try {
                obj.put(String.valueOf(entry.getKey()), entry.getValue());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        SharedPreferences prefs = SecurePrefs.get(context, PREF_NAME);
        prefs.edit().putString(getCartKey(userId), obj.toString()).apply();
    }

    public static void clearCart(Context context, String userId) {
        SharedPreferences prefs = SecurePrefs.get(context, PREF_NAME);
        prefs.edit().remove(getCartKey(userId)).apply();
    }
}