package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.logging.Logger;

public class signUp extends AppCompatActivity {

    private Button signUpBtn;
    private TextView goToSignIn;
    private EditText username, email, password;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);

        // get elements from UI
        signUpBtn = findViewById(R.id.btnSignUp);
        goToSignIn = findViewById(R.id.goToSignIn);

        auth = FirebaseAuth.getInstance();

        // sign up button activity
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                username = findViewById(R.id.edtSignUpName);
                email = findViewById(R.id.edtSignUpEmail);
                password = findViewById(R.id.edtSignUpPassword);

                String name_txt = username.getText().toString();
                String email_txt = email.getText().toString();
                String password_txt = password.getText().toString();

                // check if inputs are empty
                if (TextUtils.isEmpty(name_txt)){
                    Toast.makeText(signUp.this,"Please enter username", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(email_txt)){
                    Toast.makeText(signUp.this,"Please enter email", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(password_txt)){
                    Toast.makeText(signUp.this,"Please enter password", Toast.LENGTH_SHORT).show();
                }
                else if (password_txt.length() < 8){
                    Toast.makeText(signUp.this,"Password is too short", Toast.LENGTH_SHORT).show();
                }
                else{
                    //sign up
                    registerUser(name_txt,email_txt,password_txt);

                }
            }
        });

        // go to sign in page
        goToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent signIn = new Intent(signUp.this,signIn.class);
                startActivity(signIn);
                finish();
            }
        });
    }

    private void registerUser(String name_txt, String email, String password) {

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(signUp.this, new OnCompleteListener<AuthResult>() {
            @Override

            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name_txt).build();

                    user.updateProfile(profileUpdates);
                    Toast.makeText(signUp.this,"Registration Successful", Toast.LENGTH_SHORT).show();

                    // got to dashboard
                    Intent dashboard = new Intent(signUp.this,Dashboard.class);
                    startActivity(dashboard);
                    finish();

                }
                else {
                    Toast.makeText(signUp.this,"Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}