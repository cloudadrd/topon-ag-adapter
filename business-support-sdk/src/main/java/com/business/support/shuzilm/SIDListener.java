package com.business.support.shuzilm;

public interface SIDListener {

    void onSuccess(int score, String data);

    void onFailure(String msg);

}