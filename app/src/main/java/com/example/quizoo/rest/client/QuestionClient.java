package com.example.quizoo.rest.client;

import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;
import com.example.quizoo.rest.pojo.Question;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface QuestionClient {

    @GET("questions_of/{idCard}")
    Call<ArrayList<Question>> getQuestionsOf(@Path("idCard") long idCard);

    @POST("question")
    Call<DBResponse> addQuestion(@Body Question question);

    @PUT("question/{id}")
    Call<DBResponse> updateQuestion(@Path("id") long id, @Body Question question);

    @DELETE("question/{id}")
    Call<DBResponse> deleteQuestion(@Path("id") long id);
}
