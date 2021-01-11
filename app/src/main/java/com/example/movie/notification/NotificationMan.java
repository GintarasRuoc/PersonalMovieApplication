package com.example.movie.notification;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuView;

import com.example.movie.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class NotificationMan extends Service {

    private Context mContext;
    private String user;

    private int hour = 18;
    private List<Integer> week = new ArrayList<>();
    private boolean enabled = true;

    private int weekMillis = 1000*60*60*24*7;

    private String hourString = "hour";
    private String weekString = "week";
    private String disabledString = "enabled";

    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    // Dialog info
    Dialog notificationDialog;

    LinearLayout layoutNot, weekDaysNot;

    Switch disabledNot, weekDisabledNot;

    EditText hourNot;

    CheckBox mondayCheck, tuesdayCheck, wednesdayCheck, thursdayCheck, fridayCheck, saturdayCheck, sundayCheck;

    TextView cancelX;

    Button cancelNot, saveNot;

    public NotificationMan(){

    }

    public NotificationMan(Context mContext, String user){
        this.mContext = mContext;
        this.user = user;
    }


    NotificationMan getService() {
        return NotificationMan.this;
    }

    @Override
    public void onCreate() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(user).child("notification");

        // Start repeating notification
        getNotificationInfo();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        cancelNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void getNotificationInfo(){
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(dataSnapshot.child(disabledString).getValue().equals("true")) {
                        enabled = true;

                        if(dataSnapshot.child(weekString).exists())
                            for (DataSnapshot temp:
                                 dataSnapshot.child(weekString).getChildren()) {
                                week.add(Integer.parseInt(temp.getKey()));
                            }

                        hour = Integer.parseInt(dataSnapshot.child(hourString).getValue().toString());
                        setUpNotification();
                    }
                    else enabled = false;
                }
                else setNotificationInfo();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNotificationInfo(){
        databaseReference.removeValue();
        databaseReference.child(disabledString).setValue(enabled);
        if(enabled) {
            if (week != null)

                for (int temp :
                        week) {
                    databaseReference.child(weekString).child(String.valueOf(temp)).setValue("true");
                }

            databaseReference.child(hourString).setValue(hour);
        }
        setUpNotification();
    }

    private void setUpNotification(){
        cancelNotification();
        if(!enabled)
            return;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        if(week == null) {
            notificationFinnish(calendar, true);
        }
        else {
            for (int temp:
                 week) {
                calendar.set(Calendar.DAY_OF_WEEK, temp);
                notificationFinnish(calendar, false);
            }
        }
    }

    private void notificationFinnish(Calendar calendar, boolean daily)
    {
        Intent intent = new Intent(mContext, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        if(daily)
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        else alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), weekMillis, pendingIntent);

    }

    private void cancelNotification() {
        if(!enabled)
            return;
        if(week == null) {
            cancelNotificationFinnish();
        }
        else {
            for (int temp:
                    week) {
                cancelNotificationFinnish();
            }
        }
    }

    private void cancelNotificationFinnish(){
        Intent intent = new Intent(mContext, NotificationReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    public void getNotificationDialog()
    {
        cancelNotification();
        notificationDialog = new Dialog(mContext);
        notificationDialog.setContentView(R.layout.pop_up_notification_v);

        layoutNot = notificationDialog.findViewById(R.id.notificationLayout);
        weekDaysNot = notificationDialog.findViewById(R.id.weekDays);

        disabledNot = notificationDialog.findViewById(R.id.disableNotifications);
        weekDisabledNot = notificationDialog.findViewById(R.id.weekNotification);

        hourNot = notificationDialog.findViewById(R.id.hourNotification);

        mondayCheck = notificationDialog.findViewById(R.id.mondayCheck);
        tuesdayCheck = notificationDialog.findViewById(R.id.tuesdayCheck);
        wednesdayCheck= notificationDialog.findViewById(R.id.wednesdayCheck);
        thursdayCheck = notificationDialog.findViewById(R.id.thursdayCheck);
        fridayCheck = notificationDialog.findViewById(R.id.fridayCheck);
        saturdayCheck = notificationDialog.findViewById(R.id.saturdayCheck);
        sundayCheck = notificationDialog.findViewById(R.id.sundayCheck);

        cancelX = notificationDialog.findViewById(R.id.txtclose);
        cancelNot = notificationDialog.findViewById(R.id.cancelNotificationSettings);
        saveNot = notificationDialog.findViewById(R.id.changeNotificationSettings);

        disabledNot.setChecked(enabled);

        hourNot.setText(String.valueOf(hour));

        if(week == null)
        {
            weekDisabledNot.setChecked(false);
        }
        else {
            weekDisabledNot.setChecked(true);
            for (int temp:
                 week) {
                switch (temp)
                {
                    case 1:
                        mondayCheck.setChecked(true);
                        break;
                    case 2:
                        tuesdayCheck.setChecked(true);
                        break;
                    case 3:
                        wednesdayCheck.setChecked(true);
                        break;
                    case 4:
                        thursdayCheck.setChecked(true);
                        break;
                    case 5:
                        fridayCheck.setChecked(true);
                        break;
                    case 6:
                        saturdayCheck.setChecked(true);
                        break;
                    case 7:
                        sundayCheck.setChecked(true);
                        break;
                }
            }
        }

        setUpNotificationDialogButtons();
    }

    private void setUpNotificationDialogButtons()
    {
        cancelX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationDialog.dismiss();
                setNotificationInfo();
            }
        });

        cancelNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationDialog.dismiss();
                setNotificationInfo();
            }
        });

        saveNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(disabledNot.isChecked())
                {
                    enabled = true;

                    hour = Integer.parseInt(hourNot.getText().toString());
                    week = new ArrayList<>();
                    if(!weekDisabledNot.isChecked()){
                        if(mondayCheck.isChecked())
                            week.add(1);
                        if(tuesdayCheck.isChecked())
                            week.add(2);
                        if(wednesdayCheck.isChecked())
                            week.add(3);
                        if(thursdayCheck.isChecked())
                            week.add(4);
                        if(fridayCheck.isChecked())
                            week.add(5);
                        if(saturdayCheck.isChecked())
                            week.add(6);
                        if(sundayCheck.isChecked())
                            week.add(7);
                    }
                }
                else enabled = false;
                notificationDialog.dismiss();
                setNotificationInfo();
            }
        });

        notificationDialog.show();
    }
}
