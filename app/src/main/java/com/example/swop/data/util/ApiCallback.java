package com.example.swop.data.util;

public interface ApiCallback<T> {
    void onSuccess(T response);
    void onFailure(Throwable throwable);

}
