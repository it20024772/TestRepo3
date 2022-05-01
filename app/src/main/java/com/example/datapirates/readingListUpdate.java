package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.datapirates.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class readingListUpdate extends AppCompatActivity {

    private EditText title, author;
    private CheckBox fantasy, romance, scifi, horror, mystery, nonfiction;
    private ImageView backArrow;
    private FloatingActionButton savebtn;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Book book;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_list_update);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Books").child(userId);
        book = new Book();

        backArrow = findViewById(R.id.backArrow);
        savebtn = findViewById(R.id.rluSavebtn);

        title = findViewById(R.id.edtTitleUpdate);
        author = findViewById(R.id.edtAuthorUpdate);
        fantasy = findViewById(R.id.cbFantasyUpdate);
        romance = findViewById(R.id.cbRomanceUpdate);
        scifi = findViewById(R.id.cbSciFiUpdate);
        horror = findViewById(R.id.cbHorrorUpdate);
        mystery = findViewById(R.id.cbMysteryUpdate);
        nonfiction = findViewById(R.id.cbNonFiUpdate);

        String bookKey = getIntent().getStringExtra("bookKey");
        String bookExtra = getIntent().getStringExtra("title");
        String authorExtra = getIntent().getStringExtra("author");
        String genre = getIntent().getStringExtra("genre");

        title.setText(bookExtra);
        author.setText(authorExtra);

        String [] genres = genre.split(", ");

        checkCheckBoxes(genres);

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title_txt = title.getText().toString();
                String author_txt = author.getText().toString();
                String genre_txt = findSelectedGenres();

                if (validateInput(title_txt, author_txt, genre_txt)){
                    updateData(bookKey, title_txt, author_txt,genre_txt);
                }
            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to reading list
                Intent readingListShow = new Intent(readingListUpdate.this,readingListShow.class);
                startActivity(readingListShow);
                finish();
            }
        });



    }

    private void updateData(String bookKey, String title_txt, String author_txt, String genre_txt) {

        // add details to the book object
        book.setTitle(title_txt);
        book.setAuthor(author_txt);
        book.setGenre(genre_txt);

        DatabaseReference postRef = databaseReference.child(bookKey);
        // add to database
        postRef.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(readingListUpdate.this, "Book details updated", Toast.LENGTH_SHORT).show();
                    // got to reading list
                    Intent readinglist = new Intent(readingListUpdate.this,readingListShow.class);
                    startActivity(readinglist);
                    finish();
                }
                else{
                    Toast.makeText(readingListUpdate.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkCheckBoxes(String[] genres) {

        for (String word: genres){

            if (word.equals(fantasy.getText().toString())){
                fantasy.setChecked(true);
            }
            if (word.equals(romance.getText().toString())){
                romance.setChecked(true);
            }
            if (word.equals(scifi.getText().toString())){
                scifi.setChecked(true);
            }
            if (word.equals(horror.getText().toString())){
                horror.setChecked(true);
            }
            if (word.equals(mystery.getText().toString())){
                mystery.setChecked(true);
            }
            if (word.equals(nonfiction.getText().toString())){
                nonfiction.setChecked(true);
            }
        }
    }

    private String findSelectedGenres(){
        ArrayList<String> genres = new ArrayList<String>();
        String genre_string = "";

        if (fantasy.isChecked()){
            genres.add(fantasy.getText().toString());
        }
        if (romance.isChecked()){
            genres.add(romance.getText().toString());
        }
        if (scifi.isChecked()){
            genres.add(scifi.getText().toString());
        }
        if (horror.isChecked()){
            genres.add(horror.getText().toString());
        }
        if (mystery.isChecked()){
            genres.add(mystery.getText().toString());
        }
        if (nonfiction.isChecked()){
            genres.add(nonfiction.getText().toString());
        }

        for (int i=0; i < genres.size(); i++){

            if (i != genres.size()-1){
                genre_string += genres.get(i) + ", ";
            }
            else {
                genre_string += genres.get(i);
            }
        }

        return genre_string;

    }

    private  boolean validateInput(String title_txt, String author_txt, String genre_txt) {

        boolean isValid = false;

        if (TextUtils.isEmpty(title_txt)){
            Toast.makeText(readingListUpdate.this,"Please enter title", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(author_txt)){
            Toast.makeText(readingListUpdate.this,"Please enter author", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(genre_txt)){
            Toast.makeText(readingListUpdate.this,"Please select the genre", Toast.LENGTH_LONG).show();
        }
        else{
            isValid = true;
        }

        return isValid;
    }
}