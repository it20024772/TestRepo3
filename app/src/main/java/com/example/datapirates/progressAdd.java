package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.datapirates.model.Progress;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class progressAdd extends AppCompatActivity {

    private EditText bookName, totalPages, currentPage;
    private FloatingActionButton savebtn;
    private ImageView backArrow;

    FirebaseAuth auth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Progress progress;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_add);

        savebtn = findViewById(R.id.progressSavebtn);
        backArrow = findViewById(R.id.backArrow);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Progress").child(userId);
        progress = new Progress();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bookName = findViewById(R.id.edtCurrentBookName);
                totalPages = findViewById(R.id.edtTotalPages);
                currentPage = findViewById(R.id.edtCurrentPage);

                String bookName_txt = bookName.getText().toString();
                String totalPages_txt =totalPages.getText().toString();
                String currentPge_txt = currentPage.getText().toString();

                // validations
                if (TextUtils.isEmpty(bookName_txt)) {
                    Toast.makeText(progressAdd.this, "Please enter book name", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(totalPages_txt)) {
                    Toast.makeText(progressAdd.this, "Please enter total pages", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(currentPge_txt)) {
                    Toast.makeText(progressAdd.this, "Please enter current page", Toast.LENGTH_SHORT).show();
                } else {
                    saveData(bookName_txt,totalPages_txt, currentPge_txt );
                }

            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent Dashboard = new Intent(progressAdd.this, Dashboard.class);
                startActivity(Dashboard);
                finish();
            }
        });
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
                    Toast.makeText(progressAdd.this, "Progress Created", Toast.LENGTH_SHORT).show();
                    // got to goal display page
                    Intent goalShow = new Intent(progressAdd.this,progressShow.class);
                    startActivity(goalShow);
                    finish();
                }
                else{
                    Toast.makeText(progressAdd.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}