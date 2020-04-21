package com.capillary.myapplication.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideosModel {
    @SerializedName("videos")
    @Expose
    private List<VideoListModel> videos = null;

    public List<VideoListModel> getVideos() {
        return videos;
    }

    public void setVideos(List<VideoListModel> videos) {
        this.videos = videos;
    }
}
