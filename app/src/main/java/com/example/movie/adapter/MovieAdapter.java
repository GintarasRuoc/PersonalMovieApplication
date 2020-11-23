package com.example.movie.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.movie.MainActivity;
import com.example.movie.MovieDetails;
import com.example.movie.MyWatchlist;
import com.example.movie.R;
import com.example.movie.classes.Movie;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MyViewHolder> {

    private Context mContext;
    private List<Movie> movieList;
    private MainActivity mainActivity;
    private String user;
    private int totalResults;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_MORE = 1;

    RequestOptions option;
    public MovieAdapter(Context mContext, List<Movie> movieList, MainActivity _mainActivity){
        this.mContext = mContext;
        this.movieList = movieList;
        this.mainActivity =_mainActivity;

        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    public MovieAdapter(Context mContext, List<Movie> movieList){
        this.mContext = mContext;
        this.movieList = movieList;

        option = new RequestOptions().centerCrop().placeholder(R.drawable.loading_shape).error(R.drawable.loading_shape);
    }

    @Override
    public int getItemViewType(int position) {
        return (position == movieList.size()) ? VIEW_TYPE_MORE : VIEW_TYPE_ITEM ;
    }

    // Inflates button for more information or item for movies display
    @Override
    public MovieAdapter.MyViewHolder onCreateViewHolder(@NonNull  ViewGroup viewGroup, int i)
    {
        View view;
        boolean item;

        if(i == VIEW_TYPE_MORE) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.load_more_items, viewGroup, false);
            if(mainActivity == null || movieList.size() >= totalResults) {
                System.out.println(totalResults + "dydis");
                view.findViewById(R.id.moreButton).setVisibility(View.GONE);
            }
            item = false;
        } else {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.movie_row_item, viewGroup, false);
            item = true;
        }
        return new MyViewHolder(view, item);
    }

    // Fills movie items with information
    @Override
    public void onBindViewHolder(final MovieAdapter.MyViewHolder viewHolder, int i){
        if(movieList.size() != 0)
            if(i == movieList.size()) {
                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainActivity.preLoadJSON(mContext);
                    }
                });
            } else {
                viewHolder.title.setText(movieList.get(i).getTitle());
                if(movieList.get(i).getRating().equals("0"))
                    viewHolder.rating.setText("Rating: Not rated");
                else viewHolder.rating.setText("Rating: " + movieList.get(i).getRating());
                if(movieList.get(i).getRating().equals(""))
                    viewHolder.release_date.setText("Release date: Unknown");
                else viewHolder.release_date.setText("Release date: " + movieList.get(i).getReleaseDate());

                String poster = movieList.get(i).getImageurl();

                Glide.with(mContext).load(poster).apply(option).into(viewHolder.imageUrl);
            }

    }

    @Override
    public int getItemCount()
    {
        return movieList.size() + 1;
    }

    // Gets items for filling information about movies
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, rating, release_date;
        ImageView imageUrl;
        Button button;

        public MyViewHolder(@NonNull View itemView, boolean item) {
            super(itemView);
            title = itemView.findViewById(R.id.movie_name);
            imageUrl = itemView.findViewById(R.id.image);
            rating = itemView.findViewById(R.id.rating);
            release_date = itemView.findViewById(R.id.release_date);
            button = (Button) itemView.findViewById(R.id.moreButton);

            if(item)
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION)
                        {
                            Movie clickedDataitem = movieList.get(pos);
                            Intent intent = new Intent(mContext, MovieDetails.class);
                            intent.putExtra("ID", clickedDataitem.getId());
                            if(user != null)
                                intent.putExtra("USER", user);
                            else intent.putExtra("USER", "");
                            mContext.startActivity(intent);
                        }
                    }
                });
        }
    }
    public void clear()
    {
        movieList.clear();
        notifyDataSetChanged();
    }

    public void setUser(String _user)
    {
        user = _user;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }
}
