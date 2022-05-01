package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signIn extends AppCompatActivity {

    private Button signInBtn;
    private TextView goToSignUp;
    private EditText email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        // get elements from UI
        signInBtn = findViewById(R.id.btnSignIn);
        goToSignUp = findViewById(R.id.goToSignUp);

        auth = FirebaseAuth.getInstance();

        // sign in button activity
        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = findViewById(R.id.edtSignInEmail);
                password = findViewById(R.id.edtSignInPassword);

                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                // check if inputs are empty
                if (TextUtils.isEmpty(email_txt)){
                    Toast.makeText(signIn.this,"Please enter your email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password_txt)){
                    Toast.makeText(signIn.this,"Please enter password", Toast.LENGTH_SHORT).show();
                }
                else{
                    //sign in
                    loginUser(email_txt,password_txt);
                }
            }
        });

        // go to sign up page
        goToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent signUp = new Intent(signIn.this,signUp.class);
                startActivity(signUp);
                finish();
            }
        });
    }

    private void loginUser(String email_txt, String password_txt) {
        auth.signInWithEmailAndPassword(email_txt,password_txt).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(signIn.this,"Login Successful!", Toast.LENGTH_SHORT).show();
                // got to dashboard
                Intent dashboard = new Intent(signIn.this,Dashboard.class);
                startActivity(dashboard);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(signIn.this,"Incorrect email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}