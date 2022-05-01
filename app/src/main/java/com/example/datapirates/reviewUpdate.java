package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.datapirates.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class reviewUpdate extends AppCompatActivity {

    private EditText bookName;
    private TextInputEditText description;
    private RatingBar ratingBar;
    private FloatingActionButton savebtn;
    private ImageView backArrow;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Review review;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_update);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Reviews").child(userId);
        review = new Review();

        backArrow = findViewById(R.id.backArrow);
        savebtn = findViewById(R.id.reviewSavebtn);

        bookName = findViewById(R.id.edtReviewBookNameUpdate);
        description = findViewById(R.id.edtReviewDescriptionUpdate);
        ratingBar = findViewById(R.id.reviewRatingBarUpdate);

        String reviewKey = getIntent().getStringExtra("reviewKey");
        String bookNameExtra = getIntent().getStringExtra("bookName");
        String descriptionExtra = getIntent().getStringExtra("description");
        String ratingBarExtra = getIntent().getStringExtra("rating");

        bookName.setText(bookNameExtra);
        description.setText(descriptionExtra);
        ratingBar.setRating(Float.parseFloat(ratingBarExtra));

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookName_txt = bookName.getText().toString();
                String description_txt = description.getText().toString();
                String ratingBar_txt = String.valueOf(ratingBar.getRating());

                if (TextUtils.isEmpty(bookName_txt)){
                    Toast.makeText(reviewUpdate.this,"Please enter book name", Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(description_txt)){
                    Toast.makeText(reviewUpdate.this,"Please enter description", Toast.LENGTH_LONG).show();
                }
                else{
                    updateData(reviewKey,bookName_txt, description_txt, ratingBar_txt);
                }
            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to review display
                Intent reviewShow = new Intent(reviewUpdate.this,reviewShow.class);
                startActivity(reviewShow);
                finish();
            }
        });

    }

    private void updateData(String reviewKey, String bookName_txt, String description_txt, String ratingBar_txt) {
        review.setBookName(bookName_txt);
        review.setDescription(description_txt);
        review.setRating(ratingBar_txt);

        DatabaseReference postRef = databaseReference.child(reviewKey);

        // add to database
        postRef.setValue(review).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(reviewUpdate.this, "Review details updated", Toast.LENGTH_SHORT).show();
                    // got to review display
                    Intent reviewShow = new Intent(reviewUpdate.this,reviewShow.class);
                    startActivity(reviewShow);
                    finish();
                }
                else{
                    Toast.makeText(reviewUpdate.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}