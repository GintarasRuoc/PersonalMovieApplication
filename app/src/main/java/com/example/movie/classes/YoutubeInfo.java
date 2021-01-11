package com.example.movie.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class YoutubeInfo {
    @SerializedName("items")
    @Expose
    private List<Video> videoList;
    @Expose
    @SerializedName("error")
    private ErrorClass error;

    public YoutubeInfo(List<Video> videoList, ErrorClass error) {
        this.videoList = videoList;
        this.error = error;
    }

    public List<Video> getVideoList() {
        return videoList;
    }

    public void setVideoList(List<Video> videoList) {
        this.videoList = videoList;
    }

    public ErrorClass getError() {
        return error;
    }

    public void setError(ErrorClass error) {
        this.error = error;
    }
}
