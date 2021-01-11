package com.example.movie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movie.adapter.MovieAdapter;
import com.example.movie.api.Client;
import com.example.movie.api.Service;
import com.example.movie.classes.Movie;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWatchlist extends AppCompatActivity {

    Dialog sortDialog;

    Intent intent;
    // Logged in google account user id
    String userId;

    // RecyclerView information
    Boolean exists = false;
    private RecyclerView recyclerView;
    MovieAdapter movieAdapter;
    // Sorted movie list
    ArrayList<Movie> movieList;
    // Unchanged movie list ready for sorting movies
    ArrayList<Movie> baseMovieList;

    // Refrences to database
    FirebaseDatabase database;
    DatabaseReference reference;
    private ProgressDialog pd;

    private int i;

    // Sorting items
    RadioButton all, notWatched, watched,
            alphabet, date, rating,
            ascending, descending;

    String show = "all"; // Can be: all, notWatched, watched
    String orderBy = "alphabet"; // Can be: alphabet, date, rating
    boolean orderType = false; // Can be: true-ascending, false-descending

    // Progress bar
    ProgressBar progressBar;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My watchlist");

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_watchlist);
        progressBar = (ProgressBar) findViewById(R.id.watchlistProgressBar);
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching watchlist...");
        pd.setCancelable(false);
        pd.show();
        intent = getIntent();
        userId = intent.getStringExtra("USER");
        recyclerViewOrientation();


        sortDialog = new Dialog(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if(id == R.id.sort)
        {
            showPopup(getActivity().findViewById(R.id.sort));
        }
        if(id == android.R.id.home)
        {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
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

    // Set up recyclerView, adapter and format of displayed movies
    private void recyclerViewOrientation()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recycleViewerId);
        recyclerView.removeAllViewsInLayout();

        if(exists == false) {
            baseMovieList = new ArrayList<>();
            movieList = new ArrayList<>();
            exists = true;
        }
        movieAdapter = new MovieAdapter(this, movieList);
        if(userId != null)
            movieAdapter.setUser(userId);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        movieAdapter.setUser(userId);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
        getMovies();
    }

    // Getting unsorted user watchlist from database
    private void getMovies() {

        database = FirebaseDatabase.getInstance();
        reference = database.getReference().child(userId).child("movies");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                movieList.clear();
                baseMovieList.clear();
                i = (int) dataSnapshot.getChildrenCount();
                for (final DataSnapshot child : dataSnapshot.getChildren())
                    {
                        String id = child.getKey();
                        Client Client = new Client();
                        Service apiService =
                                Client.getClient().create(Service.class);
                        Call<Movie> call;
                        call = apiService.getDetails(id, BuildConfig.THE_MOVIE_DB_API_TOKEN);

                        call.enqueue(new Callback<Movie>() {
                            @Override
                            public void onResponse(Call<Movie> call, Response<Movie> response) {
                                Movie info;
                                info = response.body();
                                if(child.child("watched").getValue().equals("true"))
                                    info.setWatched(true);

                                baseMovieList.add(info);

                                i = i -1;
                                if(i <= 0) {
                                    pd.dismiss();
                                    progressBar();
                                    sort();
                                }
                            }

                            @Override
                            public void onFailure(Call<Movie> call, Throwable t) {
                                Toast.makeText(MyWatchlist.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                            }
                        } );

                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    // Creating dialog for sorting movie
    public boolean showPopup(View v){
        TextView txtclose;
        Button btnSort;

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            sortDialog.setContentView(R.layout.pop_up_sort_v);
        else sortDialog.setContentView(R.layout.pop_up_sort_h);
        LinearLayout linearLayout = sortDialog.findViewById(R.id.popUpSortLayout);
        linearLayout.setBackgroundResource(R.drawable.background);
        txtclose = (TextView) sortDialog.findViewById(R.id.txtclose);
        btnSort = (Button) sortDialog.findViewById(R.id.btnSort);

        all = sortDialog.findViewById(R.id.myWatchListAll);
        notWatched = sortDialog.findViewById(R.id.myWatchListNotWatched);
        watched = sortDialog.findViewById(R.id.myWatchListWatched);
        alphabet = sortDialog.findViewById(R.id.myWatchListAlphabet);
        date = sortDialog.findViewById(R.id.myWatchListDate);
        rating = sortDialog.findViewById(R.id.myWatchListRating);
        ascending = sortDialog.findViewById(R.id.myWatchListAscending);
        descending = sortDialog.findViewById(R.id.myWatchListDesceding);

        if(show.equals("all"))
            all.setChecked(true);
        else if(show.equals("notWatched"))
            notWatched.setChecked(true);
        else watched.setChecked(true);

        if(orderBy.equals("alphabet"))
            alphabet.setChecked(true);
        else if(orderBy.equals("date"))
            date.setChecked(true);
        else rating.setChecked(true);

        if(orderType)
            ascending.setChecked(true);
        else descending.setChecked(true);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortDialog.dismiss();
            }
        });
        btnSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(all.isChecked())
                    show = "all";
                else if(notWatched.isChecked())
                    show = "notWatched";
                else show = "watched";

                if(alphabet.isChecked())
                    orderBy = "alphabet";
                else if(date.isChecked())
                    orderBy = "date";
                else orderBy = "rating";

                if(ascending.isChecked())
                    orderType = true;
                else orderType = false;

                sortDialog.dismiss();
                sort();
            }
        });
        sortDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        sortDialog.show();
        return true;
    }

    // Sort movies based of information from dialog or default
    private void sort()
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Sorting movies...");
        pd.setCancelable(false);
        pd.show();

        movieList.clear();
        if(show.equals("notWatched"))
            watched(false);
        else if(show.equals("watched"))
            watched(true);
        else movieList.addAll(baseMovieList);

        if(orderBy.equals("alphabet"))
            sortAlphabet();
        else if(orderBy.equals("date"))
            sortDate();
        else sortRating();

        if(orderType)
            Collections.reverse(movieList);

        recyclerView.setAdapter(movieAdapter);
        pd.dismiss();
    }

    // Getting only watched or unwatched movies
    private void watched(boolean w){
        for (Movie temp:
             baseMovieList) {
            if(temp.isWatched() == w)
                movieList.add(temp);
        }
    }

    // Sorting movies by alphabet
    private void sortAlphabet()
    {
        for(int i = 0; i < movieList.size(); i++){
            for(int j = 1; j < movieList.size() - i; j++)
            {
                Movie a = movieList.get(j-1);
                Movie b = movieList.get(j);
                int result = a.getTitle().compareTo(b.getTitle());
                if(result > 0)
                {
                    movieList.set(j-1, b);
                    movieList.set(j, a);
                }
            }
        }
    }

    // Sorting movies by date and if date matched sort by alphabet
    private void sortDate()
    {
        for(int i = 0; i < movieList.size(); i++){
            for(int j = 1; j < movieList.size() - i; j++)
            {
                Movie a = movieList.get(j-1);
                Movie b = movieList.get(j);
                int result = a.getReleaseDate().compareTo(b.getReleaseDate());
                if( result < 0)
                {
                    movieList.set(j-1, b);
                    movieList.set(j, a);
                }
                else if(result == 0)
                {
                    result = a.getTitle().compareTo(b.getTitle());
                    if(result > 0)
                    {
                        movieList.set(j-1, b);
                        movieList.set(j, a);
                    }
                }
            }
        }
    }

    // Sorting movies by rating and if rating matches sort by alphabet
    private void sortRating()
    {
        for(int i = 0; i < movieList.size(); i++){
            for(int j = 1; j < movieList.size() - i; j++)
            {
                Movie a = movieList.get(j-1);
                Movie b = movieList.get(j);
                int result = a.getRating().compareTo(b.getRating());
                if(result < 0)
                {
                    movieList.set(j-1, b);
                    movieList.set(j, a);
                }
                else if(result == 0)
                {
                    result = a.getTitle().compareTo(b.getTitle());
                    if(result > 0)
                    {
                        movieList.set(j-1, b);
                        movieList.set(j, a);
                    }
                }
            }
        }
    }

    // Display how many of the movies from watchlist are marked as watched
    private void progressBar()
    {
        int amount = 0;
        for(Movie a:
            baseMovieList)
            if(a.isWatched())
                amount++;
        int fill = 100 / baseMovieList.size() * amount;
        progressBar.setProgress(fill);
    }
}