package com.berryacid.minitwitter.retrofit;

import com.berryacid.minitwitter.retrofit.request.RequestCreateTweet;
import com.berryacid.minitwitter.retrofit.request.RequestUserProfile;
import com.berryacid.minitwitter.retrofit.response.ResponseUploadPhoto;
import com.berryacid.minitwitter.retrofit.response.ResponseUserProfile;
import com.berryacid.minitwitter.retrofit.response.Tweet;
import com.berryacid.minitwitter.retrofit.response.TweetDelete;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AuthTwitterService {

    //Tweets
    @GET("tweets/all")
    Call<List<Tweet>> getAllTweets();

    @POST("tweets/create")
    Call<Tweet> createTweet(@Body RequestCreateTweet requestCreateTweet);

    @POST("tweets/like/{idTweet}")
    Call<Tweet> likeTweet(@Path("idTweet") int idTweet);

    @DELETE("tweets/{idTweet}")
    Call<TweetDelete> deleteTweet(@Path("idTweet") int idTweet);

    //Users
    @GET("users/profile")
    Call<ResponseUserProfile> getProfile();

    @PUT("users/profile")
    Call<ResponseUserProfile> updateProfile(@Body RequestUserProfile requestUserProfile);

    @POST("users/uploadprofilephoto")
    Call<ResponseUploadPhoto> uploadProfilePhoto(@Part("file\"; filename=\"photo.jpeg\" ") RequestBody file);

}
