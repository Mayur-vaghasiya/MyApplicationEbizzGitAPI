package com.example.application.api;

import com.example.application.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiRequestData {
     @GET("users")
     Call<List<User>> getUserList();

     @GET("users/{username}")
     Call<User> getUser(@Path("username") String username);

}
