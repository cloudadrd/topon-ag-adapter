package com.business.support.compose;

public class TaskResult {

    public boolean isError;

    private int score;

    private String data;

    private SdkType sdkType;

    //1 mission timeout ,2 network timeout,3 exception, 4,retry 2
    private int errorType;

    public TaskResult(boolean isError, int score, String data, SdkType sdkType, int errorType) {
        this.isError = isError;
        this.score = score;
        this.data = data;
        this.sdkType = sdkType;
        this.errorType = errorType;
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

    public int getErrorType() {
        return errorType;
    }

    public void setErrorType(int errorType) {
        this.errorType = errorType;
    }
}
