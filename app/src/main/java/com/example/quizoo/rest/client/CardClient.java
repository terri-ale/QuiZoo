package com.example.quizoo.rest.client;

import com.example.quizoo.rest.pojo.Card;
import com.example.quizoo.rest.pojo.DBResponse;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface CardClient {

    @GET("card")
    Call<ArrayList<Card>> getAllCards();

    //@GET("card/{id}")
    //Call<Card> getCard(@Path("id") long id);

    @POST("card")
    Call<DBResponse> addCard(@Body Card card);

    @PUT("card/{id}")
    Call<DBResponse> updateCard(@Path("id") long id, @Body Card card);

    @DELETE("card/{id}")
    Call<DBResponse> deleteCard(@Path("id") long id);

    @Multipart
    @POST("save_image")
    Call<DBResponse> postTest(@Part MultipartBody.Part image);

}
