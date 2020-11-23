package com.example.movie.classes;

import com.example.movie.classes.Movie;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoviesInfo {
    private int page;

    private List<Movie> results;

    @SerializedName("total_results")
    private int totalResults;

    private int totalPages;

    public int getPage()
    {
        return page;
    }

    public void setPage(int _page)
    {
        this.page = _page;
    }

    public List<Movie> getResults()
    {
        return results;
    }

    public List<Movie> getMovies()
    {
        return results;
    }

    public void setResults (List<Movie> _results)
    {
        this.results = results;
    }

    public void setMovies(List<Movie> _results)
    {
        this.results = _results;
    }

    public int getTotalResults()
    {
        return totalResults;
    }

    public void setTotalResults(int _totalResults)
    {
        this.totalResults = _totalResults;
    }

    public int getTotalPages()
    {
        return totalPages;
    }

    public void setTotalPages(int _totalPages)
    {
        this.totalPages = _totalPages;
    }
}
