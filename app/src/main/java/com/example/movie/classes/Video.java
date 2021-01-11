package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class Video {
    @SerializedName("id")
    private VideoId id;

    @SerializedName("snippet")
    private VideoSnippet snippet;

    public Video(VideoId id, VideoSnippet snippet) {
        this.id = id;
        this.snippet = snippet;
    }

    public VideoId getId() {
        return id;
    }

    public void setId(VideoId id) {
        this.id = id;
    }

    public VideoSnippet getSnippet() {
        return snippet;
    }

    public void setSnippet(VideoSnippet snippet) {
        this.snippet = snippet;
    }
}
