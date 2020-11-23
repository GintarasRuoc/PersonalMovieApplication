package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

public class Company {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("logo_path")
    private String logoPath;
    @SerializedName("origin_country")
    private String originCountry;

    public Company()
    {

    }

    public Company(int _id, String _name, String logoPath, String originCountry) {
        id = _id;
        name = _name;
        this.logoPath = logoPath;
        this.originCountry = originCountry;
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

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getOriginCountry() {
        return originCountry;
    }

    public void setOriginCountry(String originCountry) {
        this.originCountry = originCountry;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", logoPath='" + logoPath + '\'' +
                ", originCountry='" + originCountry + '\'' +
                '}';
    }
}
