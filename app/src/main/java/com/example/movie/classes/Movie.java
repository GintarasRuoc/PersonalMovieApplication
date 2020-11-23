package com.example.movie.classes;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Movie{
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;
    @SerializedName("poster_path")
    private String imageurl;
    @SerializedName("vote_average")
    private String rating;
    @SerializedName("vote_count")
    private String voted;
    @SerializedName("release_date")
    private String releaseDate;
    @SerializedName("status")
    private String status;
    @SerializedName("genres")
    private List<Genre> genres = new ArrayList<Genre>();
    @SerializedName("overview")
    private String overview;
    @SerializedName("runtime")
    private String runtime;
    @SerializedName("production_companies")
    private List<Company> productionCompanies;
    @SerializedName("production_countries")
    private List<Country> productionCountries;
    @SerializedName("spoken_languages")
    private List<Language> spokenLanguages;

    private boolean watched;

    public Movie()
    {

    }

    public Movie(String id, String title, String imageurl, String rating, String voted,
                 String releaseDate, String status, List<Genre> genres, String overview,
                 String runtime, List<Company> productionCompanies, List<Country> productionCountries, List<Language> spokenLanguages, boolean watched)
    {
        this.id = id;
        this.title = title;
        this.imageurl = imageurl;
        this.rating = rating;
        this.voted = voted;
        this.releaseDate = releaseDate;
        this.status = status;
        this.genres = genres;
        this.overview = overview;
        this.runtime = runtime;
        this.productionCompanies = productionCompanies;
        this.productionCountries = productionCountries;
        this.spokenLanguages = spokenLanguages;
        this.watched = watched;
    }

    String baseImageUrl = "https://image.tmdb.org/t/p/w500";

    public String getId() {return id;}

    public void setId(String _id) {id = _id;}

    public String getTitle() {return title;}

    public void setTitle(String _title) {title = _title;}

    public String getImageurl() {return baseImageUrl + imageurl;}

    public void setImageurl(String _imageurl) {imageurl = _imageurl;}

    public String getRating() {return rating;}

    public void setRating(String _rating) {rating = _rating;}

    public String getVoted() {return voted;}

    public void setVoted(String _voted) {voted = _voted;}

    public String getReleaseDate() {return releaseDate;}

    public void setReleaseDate(String _releaseDate) {releaseDate = _releaseDate;}

    public String getGenresString()
    {
        String list = "";
        if(genres != null)
            for (Genre a :
                    genres) {
                list += a.getName() + ", ";
            }
        if(list != "")
            list = list.substring(0, list.length() - 2);
        return list;
    }

    public List<Genre> getGenresList()
    {
        return genres;
    }

    public void setGenres( Genre _genre)
    {
        genres.add(_genre);
    }

    public void setOverview(String _overview)
    {
        overview = _overview;
    }

    public String getOverview()
    {
        return overview;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String _runtime) {
        runtime = _runtime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String _status) {
        status = _status;
    }

    public List<Company> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<Company> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public String getProductionCompaniesString()
    {
        String list = "";
        if(productionCompanies != null)
            for (Company a :
                    productionCompanies) {
                list += a.getName() + ", ";
            }
        if(list != "")
            list = list.substring(0, list.length() - 2);
        return list;
    }

    public List<Country> getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(List<Country> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public String getProductionCountriesString()
    {
        String list = "";
        if(productionCountries != null)
            for (Country a :
                    productionCountries) {
                list += a.getName() + ", ";
            }
        if(list != "")
            list = list.substring(0, list.length() - 2);
        return list;
    }

    public List<Language> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<Language> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getSpokenLanguagesString()
    {
        String list = "";
        if(spokenLanguages != null)
            for (Language a :
                    spokenLanguages) {
                list += a.getId() + ", ";
            }
        if(list != "")
            list = list.substring(0, list.length() - 2);
        return list;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", imageurl='" + imageurl + '\'' +
                ", rating='" + rating + '\'' +
                ", voted='" + voted + '\'' +
                ", releaseDate='" + releaseDate + '\'' +
                ", status='" + status + '\'' +
                ", genres=" + genres +
                ", overview='" + overview + '\'' +
                ", runtime='" + runtime + '\'' +
                ", productionCompanies=" + productionCompanies +
                ", productionCountries=" + productionCountries +
                ", spokenLanguages=" + spokenLanguages +
                ", baseImageUrl='" + baseImageUrl + '\'' +
                '}';
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }
}

