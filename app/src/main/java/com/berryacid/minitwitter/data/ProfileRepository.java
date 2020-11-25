package com.berryacid.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.berryacid.minitwitter.common.Constantes;
import com.berryacid.minitwitter.common.MyApp;
import com.berryacid.minitwitter.common.SharedPreferencesManager;
import com.berryacid.minitwitter.retrofit.AuthTwitterClient;
import com.berryacid.minitwitter.retrofit.AuthTwitterService;
import com.berryacid.minitwitter.retrofit.request.RequestUserProfile;
import com.berryacid.minitwitter.retrofit.response.ResponseUploadPhoto;
import com.berryacid.minitwitter.retrofit.response.ResponseUserProfile;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<ResponseUserProfile> userProfile;
    MutableLiveData<String> photoProfile;

    ProfileRepository(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        userProfile = getProfile();
        if (photoProfile == null) {
            photoProfile = new MutableLiveData<>();
        }
    }

    public MutableLiveData<String> getPhtotoProfile(){
        return photoProfile;
    }

    public MutableLiveData<ResponseUserProfile> getProfile() {
        if (userProfile == null) {
            userProfile = new MutableLiveData<>();
        }

        Call<ResponseUserProfile> call = authTwitterService.getProfile();
        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if (response.isSuccessful()){
                    userProfile.setValue(response.body());
                }else {
                    Toast.makeText(MyApp.getContext(),"Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(),"Algo ha ido mal, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        });


        return userProfile;
    }

    public MutableLiveData<ResponseUserProfile> updateProfile(RequestUserProfile requestUserProfile) {
        Call<ResponseUserProfile> call = authTwitterService.updateProfile(requestUserProfile);

        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if (response.isSuccessful()) {
                    userProfile.setValue(response.body());
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Algo ha ido mal, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        });

        return userProfile;
    }

    public void uploadPhoto(String photoPath){
        File file = new File(photoPath);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        Call<ResponseUploadPhoto> call = authTwitterService.uploadProfilePhoto(requestBody);

        call.enqueue(new Callback<ResponseUploadPhoto>() {
            @Override
            public void onResponse(Call<ResponseUploadPhoto> call, Response<ResponseUploadPhoto> response) {
                if (response.isSuccessful()){
                    SharedPreferencesManager.setSomeStringValue( Constantes.PREF_PHOTOURL, response.body().getFilename() );
                    photoProfile.setValue(response.body().getFilename());
                }else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUploadPhoto> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Algo ha ido mal, revise su conexión.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

