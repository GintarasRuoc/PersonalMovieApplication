package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class VideoId {

    @SerializedName("videoId")
    private String id;


    public VideoId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
