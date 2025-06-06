package com.example.swop.login;

public class LoginResult {
    public final boolean success;
    public final String message;

    public LoginResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static LoginResult success() {
        return new LoginResult(true, "Login exitoso");
    }

    public static LoginResult failure(String msg) {
        return new LoginResult(false, msg);
    }
}
