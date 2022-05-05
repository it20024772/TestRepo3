package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datapirates.model.Progress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Locale;

public class progressShow extends AppCompatActivity {
    private EditText bookName, totalPages, currentPage;
    private FloatingActionButton savebtn;
    private ProgressBar progressBar;
    private TextView progressPercentTxt;
    private ImageView backArrow, addProgress, deleteProgress, editBookName, editTotalPages, editCurrentPage;
    private ProgressDialog dialog;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String userId;
    Progress progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dialog = new ProgressDialog(progressShow.this);
        dialog.setMessage("loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Progress").child(userId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()){
                    // got to add progress
                    Intent progressAdd = new Intent(progressShow.this,progressAdd.class);
                    startActivity(progressAdd);
                    finish();
                }
                else{
                    progress = snapshot.getValue(Progress.class);
                    setContentView(R.layout.progress_show);

                    dialog.hide();

                    savebtn = findViewById(R.id.saveProgressUpdateBtn);
                    backArrow = findViewById(R.id.backArrow);
                    addProgress = findViewById(R.id.addProgressBtn);
                    deleteProgress = findViewById(R.id.deleteProgressBtn);
                    editBookName = findViewById(R.id.progressBookNameEditImg);
                    editTotalPages = findViewById(R.id.totalPagesEditImg);
                    editCurrentPage = findViewById(R.id.currentPageditImg);

                    bookName = findViewById(R.id.edtProgressBookNameUpdate);
                    totalPages = findViewById(R.id.edtTotalPagesUpdate);
                    currentPage = findViewById(R.id.edtCurrentPageUpdate);
                    progressBar = findViewById(R.id.progressBar);
                    progressPercentTxt = findViewById(R.id.progressPercentTxt);


                    bookName.setText(progress.getBookName());
                    bookName.setEnabled(false);

                    int savedTotalPages = progress.getTotalPages();
                    int savedCurrentPage = progress.getCurrentPage();
                    totalPages.setText(String.valueOf(savedTotalPages));
                    totalPages.setEnabled(false);
                    currentPage.setText(String.valueOf(savedCurrentPage));
                    currentPage.setEnabled(false);

                    int progressPercentage = calcPercentage(savedCurrentPage,savedTotalPages);

                    progressBar.setProgress(progressPercentage);
                    progressPercentTxt.setText(String.valueOf(progressPercentage));


                    editBookName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bookName.setEnabled(true);
                            bookName.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(bookName, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    editTotalPages.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            totalPages.setEnabled(true);

                            totalPages.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(totalPages, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });

                    editCurrentPage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            currentPage.setEnabled(true);

                            currentPage.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(currentPage, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });


                    savebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            String bookName_txt = bookName.getText().toString();
                            String totalPages_txt =totalPages.getText().toString();
                            String currentPge_txt = currentPage.getText().toString();

                            // validations
                            if (TextUtils.isEmpty(bookName_txt)) {
                                Toast.makeText(progressShow.this, "Please enter book name", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(totalPages_txt)) {
                                Toast.makeText(progressShow.this, "Please enter total pages", Toast.LENGTH_SHORT).show();
                            } else if (TextUtils.isEmpty(currentPge_txt)) {
                                Toast.makeText(progressShow.this, "Please enter current page", Toast.LENGTH_SHORT).show();
                            } else {
                                saveData(bookName_txt,totalPages_txt, currentPge_txt );
                            }


                        }
                    });

                    deleteProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(progressShow.this);
                            alertDialog.setTitle("Are you sure you want to delete your progress?");

                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    databaseReference.removeValue();
                                    // got to add progress
                                    Intent goalAdd = new Intent(progressShow.this,progressAdd.class);
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

                    addProgress.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(progressShow.this);
                            alertDialog.setTitle("Are you sure you want add new progress?");
                            alertDialog.setMessage("Adding new progress will delete current progress");

                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //set what would happen when positive button is clicked
                                    // got to add progress
                                    Intent goalAdd = new Intent(progressShow.this,progressAdd.class);
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
                            Intent Dashboard = new Intent(progressShow.this, Dashboard.class);
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

    public static int calcPercentage(int savedCurrentPage, int savedTotalPages) {
        int progressPercentage = Math.round(((float)savedCurrentPage / savedTotalPages) * 100);
        return progressPercentage;
    }

    private void saveData(String bookName_txt, String totalPages_txt, String currentPge_txt) {
        int totalPages_int = Integer.parseInt(totalPages_txt);
        int currentPage_int = Integer.parseInt(currentPge_txt);

        progress.setBookName(bookName_txt);
        progress.setTotalPages(totalPages_int);
        progress.setCurrentPage(currentPage_int);

        // add to database
        databaseReference.setValue(progress).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(progressShow.this, "Progress Updated", Toast.LENGTH_SHORT).show();
                    // got to progress display page
                    Intent progressShow = new Intent(progressShow.this,progressShow.class);
                    startActivity(progressShow);
                    finish();
                }
                else{
                    Toast.makeText(progressShow.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}