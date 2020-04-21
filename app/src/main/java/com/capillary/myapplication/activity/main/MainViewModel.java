package com.capillary.myapplication.activity.main;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.capillary.myapplication.model.VideoListModel;
import com.capillary.myapplication.model.VideosModel;
import com.capillary.myapplication.util.Utils;
import com.google.gson.Gson;

import java.util.List;

public class MainViewModel extends ViewModel {


    public List<VideoListModel> fetchVideoData(Context context) {

        String videoDetails = Utils.getJsonFromAssets(context, "videodetails.json");

        return new Gson().fromJson(videoDetails, VideosModel.class).getVideos();
    }
}
