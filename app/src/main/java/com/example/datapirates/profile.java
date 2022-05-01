package com.example.datapirates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class profile extends AppCompatActivity {

    private TextView username, email;
    private Button logout;
    private FirebaseUser user;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        user = FirebaseAuth.getInstance().getCurrentUser();

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        logout = findViewById(R.id.logout);
        backArrow = findViewById(R.id.backArrow);

        username.setText(user.getDisplayName());
        email.setText(user.getEmail());

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(profile.this,"Logged out", Toast.LENGTH_SHORT).show();
                // got to launch page
                Intent launchScreen = new Intent(profile.this,launchScreen.class);
                startActivity(launchScreen);
                finish();
            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent Dashboard = new Intent(profile.this, Dashboard.class);
                startActivity(Dashboard);
                finish();
            }
        });

    }
}