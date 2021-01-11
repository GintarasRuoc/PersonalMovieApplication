package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class VideoSnippet {

    @SerializedName("title")
    private String title;

    public VideoSnippet(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
