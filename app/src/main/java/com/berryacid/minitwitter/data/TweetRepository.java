package com.berryacid.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.berryacid.minitwitter.common.Constantes;
import com.berryacid.minitwitter.common.MyApp;
import com.berryacid.minitwitter.common.SharedPreferencesManager;
import com.berryacid.minitwitter.retrofit.AuthTwitterClient;
import com.berryacid.minitwitter.retrofit.AuthTwitterService;
import com.berryacid.minitwitter.retrofit.request.RequestCreateTweet;
import com.berryacid.minitwitter.retrofit.response.Like;
import com.berryacid.minitwitter.retrofit.response.Tweet;
import com.berryacid.minitwitter.retrofit.response.TweetDelete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TweetRepository {
    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;
    MutableLiveData<List<Tweet>> favTweets;
    String userName;

    TweetRepository(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
        userName = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
    }

    public MutableLiveData<List<Tweet>> getAllTweets(){
        if (allTweets == null){
            allTweets = new MutableLiveData<>();
        }

        Call<List<Tweet>> call = authTwitterService.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if (response.isSuccessful()) {
                    allTweets.setValue(response.body());

                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();

            }
        });

        return allTweets;
    }

    public MutableLiveData<List<Tweet>> getFavsAllTweets() {
        if (favTweets == null) {
            favTweets = new MutableLiveData<>();
        }

        List<Tweet> newFavList = new ArrayList<>();
        Iterator itTweets = allTweets.getValue().iterator();

        while(itTweets.hasNext()){
            Tweet current = (Tweet) itTweets.next();
            Iterator itLikes = current.getLikes().iterator();
            boolean enc = false;
            while (itLikes.hasNext() && !enc){
                Like like = (Like)itLikes.next();
                if(like.getUsername().equals(userName)){
                    enc = true;
                    newFavList.add(current);
                }
            }
        }

        favTweets.setValue(newFavList);

        return favTweets;

    }

    public void createTweet(String mensaje){

        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(mensaje);
        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);
        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()){
                    List<Tweet> listaClonada = new ArrayList<>();
                    //Añadimos en primer lugar el nuevo tweet que nos llega del servidor.
                    listaClonada.add(response.body());
                    for (int i=0; i < allTweets.getValue().size(); i++){
                        listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                    }
                    allTweets.setValue(listaClonada);
                }else {
                    Toast.makeText(MyApp.getContext(), "Algo ha dio mal, intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Algo ha ido mal, revise su conexión", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void deleteTweet(final int idTweet){
        Call<TweetDelete> call = authTwitterService.deleteTweet(idTweet);

        call.enqueue(new Callback<TweetDelete>() {
            @Override
            public void onResponse(Call<TweetDelete> call, Response<TweetDelete> response) {
                if (response.isSuccessful()){
                    List<Tweet> clonedTweets = new ArrayList<>();
                    for (int i=0; i < allTweets.getValue().size(); i++){
                        if (allTweets.getValue().get(i).getId() != idTweet){
                            clonedTweets.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }

                    allTweets.setValue(clonedTweets);
                    getFavsAllTweets();
                }else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal, intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDelete> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Algo ha ido mal, revise su conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeTweet(int idTweet){
        Call<Tweet> call = authTwitterService.likeTweet(idTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()){
                    List<Tweet> listaClonada = new ArrayList<>();

                    for(int i=0; i < allTweets.getValue().size(); i++){
                        if(allTweets.getValue().get(i).getId() == idTweet){
                        //Si hemos encontraso en la lista original el elemento,
                        //introducimos el elemento que nos ha llegado del
                        //servidor.
                        listaClonada.add(response.body());
                    }else{
                        listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    allTweets.setValue(listaClonada);

                    getFavsAllTweets();
                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha dio mal, intentelo de nuevo.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Algo ha ido mal, revise su conexión", Toast.LENGTH_SHORT).show();

            }
        });
    }
}

