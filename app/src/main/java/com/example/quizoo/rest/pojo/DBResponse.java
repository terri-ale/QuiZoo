package com.example.quizoo.rest.pojo;

public class DBResponse {

    private long id;
    private String url;
    private boolean result;

    public DBResponse(){ }

    public long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public boolean getResult() {
        return result;
    }
}
