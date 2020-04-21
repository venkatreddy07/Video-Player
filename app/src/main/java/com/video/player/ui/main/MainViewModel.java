package com.video.player.ui.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.video.player.responses.VideoListModel;
import com.video.player.responses.VideosModel;
import com.video.player.repositories.VideosRepository;
import com.google.gson.Gson;

import java.util.List;

public class MainViewModel extends ViewModel {


    public List<VideoListModel> fetchVideoData(Context context) {

        String videoDetails = VideosRepository.getJsonFromAssets(context, "videodetails.json");

        return new Gson().fromJson(videoDetails, VideosModel.class).getVideos();
    }
}
