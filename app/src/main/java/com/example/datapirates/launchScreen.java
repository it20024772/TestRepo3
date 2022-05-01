package com.example.datapirates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class launchScreen extends AppCompatActivity {

    private Button signInBtn, signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen);

        // get elements from UI
        signInBtn = findViewById(R.id.btnLaunchSignIn);
        signUpBtn = findViewById(R.id.btnLaunchSignUp);

        // sign up button activity
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent signUp = new Intent(launchScreen.this,signUp.class);
                startActivity(signUp);
                finish();
            }
        });

        // sign in button activity
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent signIn = new Intent(launchScreen.this,signIn.class);
                startActivity(signIn);
                finish();
            }
        });
    }
}