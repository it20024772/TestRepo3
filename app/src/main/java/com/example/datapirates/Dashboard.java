package com.example.datapirates;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{

    private CardView readingListCrd, goalsCrd, progressCrd, reviewsCrd, readingLevelCrd, readingSpeedCrd;
    private ImageView profileIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        readingListCrd = (CardView) findViewById(R.id.readinglistCrd);
        goalsCrd = (CardView) findViewById(R.id.goalsCrd);
        progressCrd = (CardView) findViewById(R.id.progressCrd);
        reviewsCrd = (CardView) findViewById(R.id.reviewsCrd);
        readingLevelCrd = (CardView) findViewById(R.id.readingLevelCrd);
        readingSpeedCrd = (CardView) findViewById(R.id.readingSpeedCrd);
        profileIcon = findViewById(R.id.profile_icon);


        readingListCrd.setOnClickListener(this);
        goalsCrd.setOnClickListener(this);
        progressCrd.setOnClickListener(this);
        reviewsCrd.setOnClickListener(this);
        readingLevelCrd.setOnClickListener(this);
        readingSpeedCrd.setOnClickListener(this);
        profileIcon.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        Intent i;

        switch (view.getId()){
            case R.id.readinglistCrd:
                i = new Intent(this, readingListShow.class);
                startActivity(i);
                break;
            case R.id.goalsCrd:
                i = new Intent(this, goalShow.class);
                startActivity(i);
                break;
            case R.id.progressCrd:
                i = new Intent(this, progressShow.class);
                startActivity(i);
                break;
            case R.id.reviewsCrd:
                i = new Intent(this, reviewShow.class);
                startActivity(i);
                break;
            case R.id.readingLevelCrd:
                i = new Intent(this, calcReadingLevel.class);
                startActivity(i);
                break;
            case R.id.readingSpeedCrd:
                i = new Intent(this, calcReadingTime.class);
                startActivity(i);
                break;
            case R.id.profile_icon:
                i = new Intent(this, profile.class);
                startActivity(i);
                break;
        }

    }
}