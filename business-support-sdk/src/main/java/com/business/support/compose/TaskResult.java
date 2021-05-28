package com.business.support.compose;

public class TaskResult {

    public boolean isError;

    private int score;

    private String data;

    private SdkType sdkType;

    public TaskResult(boolean isError, int score, String data, SdkType sdkType) {
        this.isError = isError;
        this.score = score;
        this.data = data;
        this.sdkType = sdkType;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public SdkType getSdkType() {
        return sdkType;
    }

    public void setSdkType(SdkType sdkType) {
        this.sdkType = sdkType;
    }
}
