package com.example.movie.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.movie.MainActivity;
import com.example.movie.MovieDetails;
import com.example.movie.MyWatchlist;
import com.example.movie.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationReceiver extends BroadcastReceiver {

    List<String> movieIds;
    NotificationCompat.Builder builder;

    @Override
    public void onReceive(final Context context, Intent intent) {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(user.getUid()).child("movies");

        movieIds = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot temp:
                        dataSnapshot.getChildren()) {
                    if(temp.child("watched").getValue() == "false")
                        movieIds.add(temp.getKey());
                }

                Random random = new Random();
                int rand = random.nextInt(movieIds.size());

                Intent repeating_intent = new Intent(context, MovieDetails.class);
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra("ID", movieIds.get(rand));
                intent.putExtra("USER", user.getUid());
                repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder = new NotificationCompat.Builder(context, "My Notification")
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Want to watch a movie?")
                        .setContentText("Click for random movie from your watchlist")
                        .setSmallIcon(R.drawable.ic_movie)
                        .setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                managerCompat.notify(100, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void testNotification(final Context context)
    {
        FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child(user.getUid()).child("movies");

        movieIds = new ArrayList<>();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot temp:
                        dataSnapshot.getChildren()) {
                    if(temp.child("watched").getValue().equals("false"))
                        movieIds.add(temp.getKey());
                }
                Random random = new Random();
                int rand = random.nextInt(movieIds.size());

                Intent repeating_intent = new Intent(context, MovieDetails.class);
                String id = movieIds.get(rand);
                repeating_intent.putExtra("ID", id);
                repeating_intent.putExtra("USER", user.getUid());
                repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                builder = new NotificationCompat.Builder(context, "My Notification")
                        .setContentIntent(pendingIntent)
                        .setContentTitle("Want to watch a movie?")
                        .setContentText("Click for random movie from your watchlist")
                        .setSmallIcon(R.drawable.ic_movie)
                        .setAutoCancel(true);

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(100, builder.build());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
