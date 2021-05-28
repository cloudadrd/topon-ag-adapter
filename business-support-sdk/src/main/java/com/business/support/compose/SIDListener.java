package com.business.support.compose;

public interface SIDListener {

    void onSuccess(int score, String data);

    void onFailure(String msg);

}