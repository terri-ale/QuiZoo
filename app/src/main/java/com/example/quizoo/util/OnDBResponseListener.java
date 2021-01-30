package com.example.quizoo.util;

import com.example.quizoo.rest.pojo.DBResponse;

public interface OnDBResponseListener {
    public void onSuccess(DBResponse response);
    public void onFailed();
}
