package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;

    public Genre()
    {

    }

    public Genre(int _id, String _name) {
        id = _id;
        name = _name;
    }

    public void setId(int _id) {
        id = _id;
    }

    public void setName(String _name) {
        name = _name;
    }

    public int getId() {
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
