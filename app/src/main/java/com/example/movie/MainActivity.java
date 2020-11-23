package com.example.movie;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.movie.adapter.MovieAdapter;
import com.example.movie.api.Client;
import com.example.movie.api.Service;
import com.example.movie.classes.Movie;
import com.example.movie.classes.MoviesInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    // Google login variables
    FirebaseUser user;

    MenuItem accountPopup;
    MenuItem signIn;
    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;


    // Shown movies in recyclerView variables
    private ArrayList<Movie> movieInfoList;
    private RecyclerView recyclerView;
    private MovieAdapter movieAdapter;

    // Info about current shown movies
    private String type = "popular";
    private String searchQuery;
    private int currentPage = 1;
    private boolean exists = false;

    ProgressDialog pd;

    // When phone is rotated, change displayed information format
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        recyclerViewOrientation();
    }

    // Inflate menu and check if there's user logged in
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        accountPopup = menu.findItem(R.id.accountPopUp);
        signIn = menu.findItem(R.id.signIn);

        user = mAuth.getCurrentUser();
        if(user != null)
        {
            setVisibility(true);
            movieAdapter.setUser(user.getUid());
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewOrientation();
        preLoadJSON(this);

        mAuth = FirebaseAuth.getInstance();

        createRequest();
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
            movieInfoList = new ArrayList<>();
            exists = true;
        }
        movieAdapter = new MovieAdapter(this, movieInfoList, this);
        if(user != null)
            movieAdapter.setUser(user.getUid());

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(movieAdapter);
        movieAdapter.notifyDataSetChanged();
    }

    // Initiate load for movies by creating progress dialog, whilst movie are being loaded
    public void preLoadJSON(Context mContext)
    {
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching movies...");
        pd.setCancelable(false);
        pd.show();

        loadJSON(mContext);
    }

    // Creates a link for getting file of movies and add them to movie display
    private void loadJSON(Context mContext){

        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext(), "Please obtain API Key firstly from themoviedb.org", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }
            final String pageString = String.valueOf(currentPage++);

            Client Client = new Client();
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MoviesInfo> call;
            switch (type) {
                case "upcoming":
                    call = apiService.getUpcomingMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, pageString);
                    break;
                case "topRated":
                    call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, pageString);
                    break;
                case "search":
                    call = apiService.searchMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, searchQuery, pageString);
                    break;
                default: call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN, pageString);
            }
            call.enqueue(new Callback<MoviesInfo>() {
                @Override
                public void onResponse(Call<MoviesInfo> call, Response<MoviesInfo> response) {
                    List<Movie> movies;
                    movies = response.body().getResults();
                    int scrollPosition = movieInfoList.size();
                    movieInfoList.addAll(movies);
                    if(movieInfoList.size() != 0) {
                        movieAdapter.setTotalResults(response.body().getTotalResults());
                        recyclerView.setAdapter(movieAdapter);
                        recyclerView.scrollToPosition(scrollPosition);
                    }
                    else Toast.makeText(getActivity(),"No movies were found", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesInfo> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Toast.makeText(MainActivity.this, "Error Fetching Data!", Toast.LENGTH_SHORT).show();
                }
            } );
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    // Changes type of movies displayed
    private void changeRecyclerView(String searchType)
    {
        if(type != searchType || searchType == "search") {
            type = searchType;
            exists = false;
            recyclerViewOrientation();
            currentPage = 1;
            preLoadJSON(this);
        } else Toast.makeText(this, "This search type is already selected", Toast.LENGTH_SHORT).show();;
    }

    // Menu item functionality
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.accountPopUp:
                loginAccount(getActivity().findViewById(R.id.accountPopUp));
                return true;
            case R.id.search:
                searchPopUp(getActivity().findViewById(R.id.search));
                //Intent intent = new Intent(this, MainActivityDefense.class);
                //startActivityForResult(intent, 1);
                Toast.makeText(this, "search selected", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.signIn:
                signIn();
                return true;

            case R.id.mostPopular:
                changeRecyclerView("popular");
                return true;

            case R.id.topRated:
                changeRecyclerView("topRated");
                return true;
            case R.id.upcoming:
                changeRecyclerView("upcoming");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Changes shown menu items, when logged in or offline
    private void setVisibility(boolean type)
    {
        if(type){
            signIn.setVisible(false);
            accountPopup.setTitle(user.getEmail());
            accountPopup.setVisible(true);
        }
        else {
            accountPopup.setVisible(false);
            signIn.setVisible(true);
        }
    }

    // Dropdown list of account for watchlist and logging out
    public void loginAccount(View view){
        PopupMenu popup = new PopupMenu(getActivity(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.login_popup, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId() == R.id.myWatchlist)
                {
                    Intent intent = new Intent(getActivity(), MyWatchlist.class);
                    intent.putExtra("USER", user.getUid());
                    startActivity(intent);
                }
                else if(item.getItemId() == R.id.logOut)
                    signOut();
                return true;
            }
        });
        popup.show();
    }

    // Dialog popup for searching movies
    public boolean searchPopUp(View v){
        final Dialog searchDialog = new Dialog(this);
        TextView txtclose;
        final EditText inputText;
        final Button searchBtn;
        searchDialog.setContentView(R.layout.search);
        txtclose = (TextView) searchDialog.findViewById(R.id.txtclose);
        inputText = searchDialog.findViewById(R.id.searchInput);
        searchBtn = searchDialog.findViewById(R.id.btnSearch);

        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchDialog.dismiss();
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchQuery = inputText.getText().toString();
                if(searchQuery.isEmpty())
                    Toast.makeText(getActivity(), "Movie input is empty", Toast.LENGTH_SHORT).show();
                else {
                    changeRecyclerView("search");
                    searchDialog.dismiss();
                }
            }
        });

        searchDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        searchDialog.show();
        return true;
    }

    // Google account login functionality
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        /*if (requestCode == 1) {
            String temp = data.getStringExtra("TEXT");
            if(temp.equals("Hello")) {
                View view = this.getWindow().getDecorView();
                view.setBackgroundColor(Color.RED);
            }
        }*/


    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            user = mAuth.getCurrentUser();
                            setVisibility(true);
                            movieAdapter.setUser(user.getUid());
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(MainActivity.this, "Sorry authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void signOut() {
        // Firebase sign out
        mAuth.signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        setVisibility(false);
                    }
                });
    }
}