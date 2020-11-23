package com.example.movie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movie.api.Client;
import com.example.movie.api.Service;
import com.example.movie.classes.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetails extends AppCompatActivity {

    // Logged in google account user id
    String user;

    // Database reference to watchlist
    DatabaseReference add;
    FirebaseDatabase database;

    // Displayed information items
    TextView title, rating, votes, status, release_date, overview, genres, spokenLanguages, companies, countries;
    ImageView image;

    Button favorite, watched;

    // Movie information
    Movie details;

    Intent intent;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            setContentView(R.layout.movie_info_display_v);
        else setContentView(R.layout.movie_info_display_h);

        pd = new ProgressDialog(this);
        pd.setMessage("Getting movie info...");
        pd.setCancelable(false);
        pd.show();

        intent = getIntent();
        user = intent.getStringExtra("USER");
        database = FirebaseDatabase.getInstance();
        getLayoutIds();
    }

    // Menu item functionality
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    // Add to watchlist button functionality
    private void activeButtonFavorite(final boolean fav)
    {
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.equals("")) {
                    pd = new ProgressDialog(MovieDetails.this);
                    pd.setMessage("Working with watchlist...");
                    pd.setCancelable(false);
                    pd.show();
                    if(fav)
                    {
                        add.removeValue();
                    }
                    else {
                        add.child("watched").setValue("false");
                    }
                    setupButtons();
                }
                else Toast.makeText(MovieDetails.this, "You need to login, if you want to add this movie to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Watched button functionality
    private void activeButtonWatched(final boolean watch)
    {
        watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!user.equals("")) {
                    if(watch) {
                        add.child("watched").setValue("false");
                    }
                    else {
                        add.child("watched").setValue("true");
                    }
                    setupButtons();
                }
                else Toast.makeText(MovieDetails.this, "You need to login, if you want to add this movie to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Movie items set up
    private void getLayoutIds()
    {
        image = findViewById(R.id.movieImage);
        title = findViewById(R.id.movieTitle);
        rating = findViewById(R.id.movieRating);
        votes = findViewById(R.id.movieVotedCount);
        status = findViewById(R.id.movieReleaseStatus);
        genres = findViewById(R.id.movieGenres);
        release_date = findViewById(R.id.movieReleaseDate);
        overview = findViewById(R.id.movieOverview);
        spokenLanguages = findViewById(R.id.movieLanguages);
        companies = findViewById(R.id.movieCompanies);
        countries = findViewById(R.id.movieCountries);
        favorite = findViewById(R.id.movieAddToFavorite);
        watched = findViewById(R.id.movieMarkAsWatched);
        getMovieDetails();
    }

    // Getting movie information from database
    private void getMovieDetails()
    {
        String id = intent.getStringExtra("ID");
        Client Client = new Client();
        Service apiService =
                Client.getClient().create(Service.class);
        Call<Movie> call;
        call = apiService.getDetails(id, BuildConfig.THE_MOVIE_DB_API_TOKEN);

        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                details = response.body();
                fillInfo();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(MovieDetails.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
            }
        } );
    }

    // Filling movie items with information
    private void fillInfo()
    {
        String poster = details.getImageurl();
        RequestOptions option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
        Glide.with(this).load(poster).apply(option).into(image);
        title.setText(details.getTitle());
        if(details.getRating().equals("0"))
            rating.setText("Not rated");
        else rating.setText(details.getRating());
        votes.setText("( " + details.getVoted() + " voted)");
        status.setText(details.getStatus());
        genres.setText(details.getGenresString());
        if(details.getReleaseDate().equals(""))
            release_date.setText("Unknown");
        else release_date.setText(details.getReleaseDate());
        overview.setText(details.getOverview());
        spokenLanguages.setText(details.getSpokenLanguagesString());
        companies.setText(details.getProductionCompaniesString());
        countries.setText(details.getProductionCountriesString());

        setupButtons();
    }

    // Movie buttons based on information about users database branch
    private void setupButtons()
    {
        add = database.getReference().child(user).child("movies").child(details.getId());
        if(!user.equals("")) {
            favorite.setVisibility(View.VISIBLE);
            add.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean fav = snapshot.exists();
                    if (fav) {
                        boolean watch = snapshot.child("watched").getValue().equals("true");
                        favorite.setText("Remove from watchlist");
                        watched.setVisibility(View.VISIBLE);
                        activeButtonFavorite(true);
                        if(watch) {
                            watched.setText("Mark as not watched");
                            activeButtonWatched(true);
                        }
                        else {
                            watched.setText("Mark as watched");
                            activeButtonWatched(false);
                        }
                    }
                    else {
                        favorite.setText("Add to watchlist");
                        watched.setVisibility(View.GONE);
                        activeButtonFavorite(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public Activity getActivity()
    {
        Context context = this;
        while (context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}