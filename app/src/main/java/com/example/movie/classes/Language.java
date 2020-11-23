package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class Language {
    @SerializedName("iso_639_1")
    private String id;
    @SerializedName("name")
    private String name;

    public Language()
    {

    }

    public Language(String _id, String _name) {
        id = _id;
        name = _name;
    }

    public void setId(String _id) {
        id = _id;
    }

    public void setName(String _name) {
        name = _name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id= " + id +
                ", name= " + name + '\'' +
                '}';
    }
}
