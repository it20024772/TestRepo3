package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EdgeEffect;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.datapirates.model.Goal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;

public class goalAdd extends AppCompatActivity {

    private EditText bookName;
    private TextInputEditText durationHours, durationMinutes;
    private Button btnSetTime;
    private CheckBox reminderBox;
    private FloatingActionButton savebtn;
    private ImageView backArrow;

    private Calendar c;
    private Goal goal;
    private int hour, minute;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goal_add);

        savebtn = findViewById(R.id.goalSavebtn);
        backArrow = findViewById(R.id.backArrow);
        btnSetTime = findViewById(R.id.btnSetTime);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Goal").child(userId);
        goal = new Goal();

        createNotificationChannel();

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        hour = selectedHour;
                        minute = selectedMinute;
                        btnSetTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));

                        c = Calendar.getInstance();
                        c.set(Calendar.HOUR_OF_DAY, hour);
                        c.set(Calendar.MINUTE, minute);
                        c.set(Calendar.SECOND, 0);
                    }
                };

                TimePickerDialog timePickerDialog = new TimePickerDialog(goalAdd.this, onTimeSetListener, hour, minute, true);
                timePickerDialog.setTitle("Select time");
                timePickerDialog.show();
            }
        });

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookName = findViewById(R.id.edtBookName);
                durationHours = findViewById(R.id.edtGoalHoursUpdate);
                durationMinutes = findViewById(R.id.edtGoalMinutesUpdate);
                reminderBox = findViewById(R.id.reminderCb);

                // set notification for reminder
                if (reminderBox.isChecked()) {
                    Intent intent = new Intent(goalAdd.this, reminderBroadcast.class);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(goalAdd.this, 0, intent, 0);
                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                    long nowtime = System.currentTimeMillis();
                    long add = 1000 * 10;
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                    Log.i("timecal", String.valueOf(c.getTimeInMillis()));
                }

                String bookName_txt = bookName.getText().toString();
                String durationHours_txt = durationHours.getText().toString();
                String durationMinutes_txt = durationMinutes.getText().toString();

                // validations
                if (TextUtils.isEmpty(bookName_txt)) {
                    Toast.makeText(goalAdd.this, "Please enter book name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(durationHours_txt)) {
                    Toast.makeText(goalAdd.this, "Please enter duration hours", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(durationMinutes_txt)) {
                    Toast.makeText(goalAdd.this, "Please enter duration minutes", Toast.LENGTH_SHORT).show();
                } else if (c == null) {
                    Toast.makeText(goalAdd.this, "Please set start time", Toast.LENGTH_SHORT).show();
                } else {
                    saveData(bookName_txt, durationHours_txt, durationMinutes_txt, hour, minute);
                }


            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent Dashboard = new Intent(goalAdd.this, Dashboard.class);
                startActivity(Dashboard);
                finish();
            }
        });


    }

    private void saveData(String bookName_txt, String durationHours_txt, String durationMinutes_txt, int hour, int minute) {

        String time = String.format(Locale.getDefault(), "%02d:%02d",hour,minute);

        // add details to the goal object
        goal.setName(bookName_txt);
        goal.setDurationHours(durationHours_txt);
        goal.setDurationMinutes(durationMinutes_txt);
        goal.setStartTime(time);

        if (reminderBox.isChecked()){
            goal.setRemind(true);
        }
        else {
            goal.setRemind(false);
        }

        // add to database
        databaseReference.setValue(goal).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(goalAdd.this, "Goal Created", Toast.LENGTH_SHORT).show();
                    // got to goal display page
                    Intent goalShow = new Intent(goalAdd.this,goalShow.class);
                    startActivity(goalShow);
                    finish();
                }
                else{
                    Toast.makeText(goalAdd.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BookwormReminderChannel";
            String description = "Channel to remind reading goal time";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyBookworm", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}