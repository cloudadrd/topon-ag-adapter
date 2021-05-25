package com.business.support.reallycheck;

public class ResultData {

    private boolean isError;

    private String errorMessage;

    private int score;

    public ResultData(boolean isError, String errorMessage, int score) {
        this.isError = isError;
        this.errorMessage = errorMessage;
        this.score = score;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
