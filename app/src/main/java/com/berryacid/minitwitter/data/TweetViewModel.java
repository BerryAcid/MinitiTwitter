package com.berryacid.minitwitter.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.berryacid.minitwitter.retrofit.response.Tweet;
import com.berryacid.minitwitter.ui.tweets.BottomModalTweetFragment;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {
    private TweetRepository tweetRepository;
    private LiveData<List<Tweet>> tweets;
    private LiveData<List<Tweet>> favTweets;
    
    public TweetViewModel(@NonNull Application application) {
        super(application);
        tweetRepository = new TweetRepository();
        tweets = tweetRepository.getAllTweets();
    }

    public LiveData<List<Tweet>> getTweets(){
        return tweets;
    }

    public void openDialogTweetMenu(Context context, int idTweet) {
        BottomModalTweetFragment dialogTweet = BottomModalTweetFragment.newInstance(idTweet);
        dialogTweet.show(((AppCompatActivity)context).getSupportFragmentManager(), "BottomModalTweetFragment");
    }

    public LiveData<List<Tweet>> getFavTweets(){
        favTweets = tweetRepository.getFavsAllTweets();
        return favTweets;
    }

    public LiveData<List<Tweet>> getNewTweets(){
        tweets = tweetRepository.getAllTweets();
        return tweets;
    }

    public LiveData<List<Tweet>> getNewFavTweets(){
        getNewTweets();
        return getFavTweets();
    }

    public void insertTweet(String mensaje){
        tweetRepository.createTweet(mensaje);
    }

    public void deleteTweet(int idTweet){
        tweetRepository.deleteTweet(idTweet);
    }

    public void likeTweet(int idTweet){
        tweetRepository.likeTweet(idTweet);
    }
}
