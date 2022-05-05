package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datapirates.model.Review;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class reviewShow extends AppCompatActivity {

    private FloatingActionButton addbtn;
    private RecyclerView recyclerView;
    private reviewListAdapter reviewListAdapter;
    private ArrayList<Review> list;
    private ImageView backArrow;
    private ProgressDialog dialog;
    private TextView avgRatingtxt;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_show);

        dialog = new ProgressDialog(reviewShow.this);
        dialog.setMessage("loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Reviews").child(userId);

        buildRecyclerView();

        addbtn = findViewById(R.id.addReviewBtn);
        backArrow = findViewById(R.id.backArrow);
        avgRatingtxt = findViewById(R.id.avgRatingtxt);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reviewAdd = new Intent(reviewShow.this, reviewAdd.class);
                startActivity(reviewAdd);
                finish();
            }
        });

        fetchDataAndDisplay();

        reviewListAdapter.setOnItemClickListener(new reviewListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(String reviewKey, String bookName, int position) {

                // add alert dialog to confirm
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(reviewShow.this);
                alertDialog.setTitle("Are you sure you want to delete "+bookName+" review ?");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        DatabaseReference newRef = databaseReference.child(reviewKey);
                        newRef.removeValue();
                        list.remove(position);
                        reviewListAdapter.notifyItemRemoved(position);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());
                        overridePendingTransition(0, 0);
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

            @Override
            public void onUpdate(String reviewKey, String bookName, String description, String rating, int position) {
                Intent reviewUpdate = new Intent(reviewShow.this,reviewUpdate.class);
                reviewUpdate.putExtra("reviewKey",reviewKey);
                reviewUpdate.putExtra("bookName", bookName);
                reviewUpdate.putExtra("description",description);
                reviewUpdate.putExtra("rating",rating);
                startActivity(reviewUpdate);

                finish();
            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent dashboard = new Intent(reviewShow.this,Dashboard.class);
                startActivity(dashboard);
                finish();
            }
        });


    }

    private void fetchDataAndDisplay() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                float avgRate = 0;
                float totRate = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Review review = dataSnapshot.getValue(Review.class);
                    review.setReviewId(dataSnapshot.getKey());

                    list.add(review);
                    totRate += Float.parseFloat(review.getRating());
                    count += 1;
                }

                avgRate = calcAvgRate(totRate, count);
                avgRatingtxt.setText(Float.toString(avgRate));

                reviewListAdapter.notifyDataSetChanged();
                dialog.hide();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static float calcAvgRate(float totRate, int count) {
        float avgRate = 0;
        avgRate = totRate / count;

        double roundOff = (double) Math.round(avgRate * 100) / 100;
        return (float) roundOff;
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.reviewlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        reviewListAdapter = new reviewListAdapter(this,list);
        recyclerView.setAdapter(reviewListAdapter);
    }
}