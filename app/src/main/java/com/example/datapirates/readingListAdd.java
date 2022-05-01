package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

public class readingListAdd extends AppCompatActivity {
    private FloatingActionButton savebtn;
    private EditText title, author;
    private CheckBox fantasy, romance, scifi, horror, mystery, nonfiction;
    private ImageView backArrow;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private Book book;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_list_add);

        savebtn = findViewById(R.id.rlSavebtn);
        backArrow = findViewById(R.id.backArrow);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        userId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Books").child(userId);
        book = new Book();

        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = findViewById(R.id.edtTitle);
                author = findViewById(R.id.edtAuthor);
                fantasy = findViewById(R.id.cbFantasy);
                romance = findViewById(R.id.cbRomance);
                scifi = findViewById(R.id.cbSciFi);
                horror = findViewById(R.id.cbHorror);
                mystery = findViewById(R.id.cbMystery);
                nonfiction = findViewById(R.id.cbNonFi);

                // get title, author and genres of the book
                String title_txt = title.getText().toString();
                String author_txt = author.getText().toString();
                String genre_txt = findSelectedGenres();

                if (validateInput(title_txt, author_txt, genre_txt)){
                    saveData(title_txt, author_txt,genre_txt);
                }

            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to reading list
                Intent readingListShow = new Intent(readingListAdd.this,readingListShow.class);
                startActivity(readingListShow);
                finish();
            }
        });


    }

    private void saveData(String title_txt, String author_txt, String genre_txt) {
        // add details to the book object
        book.setTitle(title_txt);
        book.setAuthor(author_txt);
        book.setGenre(genre_txt);

        // generate uniques id for a book
        DatabaseReference postRef = databaseReference.push();

        // add to database
        postRef.setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(readingListAdd.this,"Added to reading list", Toast.LENGTH_LONG).show();
                    // got to reading list
                    Intent readingListShow = new Intent(readingListAdd.this,readingListShow.class);
                    startActivity(readingListShow);
                    finish();
                }
                else{
                    Toast.makeText(readingListAdd.this, "An error occurred. Please try again later.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private  boolean validateInput(String title_txt, String author_txt, String genre_txt) {

        boolean isValid = false;

        if (TextUtils.isEmpty(title_txt)){
            Toast.makeText(readingListAdd.this,"Please enter title", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.isEmpty(author_txt)){
            Toast.makeText(readingListAdd.this,"Please enter author", Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(genre_txt)){
            Toast.makeText(readingListAdd.this,"Please select the genre", Toast.LENGTH_LONG).show();
        }
        else{
            isValid = true;
        }

        return isValid;
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


}