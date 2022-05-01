package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class goalShow extends AppCompatActivity {

    private EditText bookName;
    private TextInputEditText durationHours, durationMinutes;
    private Button btnSetTime;
    private CheckBox reminderBox;
    private FloatingActionButton savebtn;
    private ImageView backArrow, addGoal, deleteGoal, editName, editDuration;
    private ProgressDialog dialog;

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

        dialog = new ProgressDialog(goalShow.this);
        dialog.setMessage("loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Goal").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    // got to add goals
                    Intent goalAdd = new Intent(goalShow.this,goalAdd.class);
                    startActivity(goalAdd);
                    finish();
                }
                else{
                    goal = snapshot.getValue(Goal.class);
                    setContentView(R.layout.goal_show);

                    dialog.hide();

                    savebtn = findViewById(R.id.goalUpdateBtn);
                    backArrow = findViewById(R.id.backArrow);
                    btnSetTime = findViewById(R.id.btnSetTimeUpdate);
                    addGoal = findViewById(R.id.addGoalBtn);
                    deleteGoal = findViewById(R.id.deleteGoalBtn);
                    editName = findViewById(R.id.goalBookNameEditImg);
                    editDuration = findViewById(R.id.durationEditImg);

                    bookName = findViewById(R.id.edtBookNameUpdate);
                    durationHours = findViewById(R.id.edtGoalHours);
                    durationMinutes = findViewById(R.id.edtGoalMinutes);
                    reminderBox = findViewById(R.id.reminderCbUpdate);

                    bookName.setText(goal.getName());
                    bookName.setEnabled(false);
                    durationHours.setText(goal.getDurationHours());
                    durationHours.setEnabled(false);
                    durationMinutes.setText(goal.getDurationMinutes());
                    durationMinutes.setEnabled(false);
                    btnSetTime.setText(String.format(Locale.getDefault(), "%s",goal.getStartTime()));

                    if (goal.isRemind()){
                        reminderBox.setChecked(true);
                    }

                    createNotificationChannel();

                    editName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bookName.setEnabled(true);
                            bookName.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(bookName, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    editDuration.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            durationHours.setEnabled(true);
                            durationMinutes.setEnabled(true);

                            durationHours.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(durationHours, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

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

                            TimePickerDialog timePickerDialog = new TimePickerDialog(goalShow.this, onTimeSetListener, hour, minute, true);
                            timePickerDialog.setTitle("Select time");
                            timePickerDialog.show();
                        }
                    });

                    savebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // set notification for reminder
                            if (c != null){
                                if (reminderBox.isChecked()) {
                                    Intent intent = new Intent(goalShow.this, reminderBroadcast.class);
                                    PendingIntent pendingIntent = PendingIntent.getBroadcast(goalShow.this, 0, intent, 0);
                                    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                                    long nowtime = System.currentTimeMillis();
                                    long add = 1000 * 10;
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
                                }
                            }

                            String bookName_txt = bookName.getText().toString();
                            String durationHours_txt = durationHours.getText().toString();
                            String durationMinutes_txt = durationMinutes.getText().toString();

                            // validations
                            if (TextUtils.isEmpty(bookName_txt)) {
                                Toast.makeText(goalShow.this, "Please enter book name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(durationHours_txt)) {
                                Toast.makeText(goalShow.this, "Please enter duration hours", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(durationMinutes_txt)) {
                                Toast.makeText(goalShow.this, "Please enter duration minutes", Toast.LENGTH_SHORT).show();
                            } else {
                                saveData(bookName_txt, durationHours_txt, durationMinutes_txt, hour, minute);
                            }


                        }
                    });

                    deleteGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(goalShow.this);
                            alertDialog.setTitle("Are you sure you want to delete your goal?");

                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    databaseReference.removeValue();
                                    // got to add goals
                                    Intent goalAdd = new Intent(goalShow.this,goalAdd.class);
                                    startActivity(goalAdd);
                                    finish();
                                }
                            });

                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked
                                }
                            });

                            alertDialog.show();

                        }
                    });

                    addGoal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(goalShow.this);
                            alertDialog.setTitle("Are you sure you want add a new goal?");
                            alertDialog.setMessage("Adding a new goal will delete the current goal");

                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    // got to add goals
                                    Intent goalAdd = new Intent(goalShow.this,goalAdd.class);
                                    startActivity(goalAdd);
                                    finish();
                                }
                            });

                            alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what should happen when negative button is clicked
                                }
                            });

                            alertDialog.show();


                        }
                    });

                    // back button functionality
                    backArrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // got to dashboard
                            Intent Dashboard = new Intent(goalShow.this, Dashboard.class);
                            startActivity(Dashboard);
                            finish();
                        }
                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveData(String bookName_txt, String durationHours_txt, String durationMinutes_txt, int hour, int minute) {
        String time;

        if (c==null){
            time = goal.getStartTime();
        }
        else {
            time = String.format(Locale.getDefault(), "%02d:%02d",hour,minute);
        }


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
                    Toast.makeText(goalShow.this, "Goal Updated", Toast.LENGTH_SHORT).show();
                    // got to goal display page
                    Intent goalShow = new Intent(goalShow.this,goalShow.class);
                    startActivity(goalShow);
                    finish();
                }
                else{
                    Toast.makeText(goalShow.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
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