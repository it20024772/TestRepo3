package com.example.datapirates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // check if a user is logged in
        if (user == null) {
            // got o launch page
            Intent launchScreen = new Intent(MainActivity.this,launchScreen.class);
            startActivity(launchScreen);
            finish();
        }
        else{
            // go to dashboard
            Intent Dashboard = new Intent(MainActivity.this,Dashboard.class);
            startActivity(Dashboard);
            finish();
        }

    }
}