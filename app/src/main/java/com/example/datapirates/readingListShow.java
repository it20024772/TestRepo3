package com.example.datapirates;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.datapirates.model.Book;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class readingListShow extends AppCompatActivity {

    private FloatingActionButton addbtn, emailbtn;
    private RecyclerView recyclerView;
    private readingListAdapter readingListAdapter;
    private ArrayList<Book> list;
    private ImageView backArrow;
    private ProgressDialog dialog;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String userId;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reading_list_show);


        dialog = new ProgressDialog(readingListShow.this);
        dialog.setMessage("loading...");
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Books").child(userId);

        buildRecyclerView();

        addbtn = findViewById(R.id.addbtn);
        emailbtn = findViewById(R.id.emailbtn);
        backArrow = findViewById(R.id.backArrow);

        addbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent readingListAdd = new Intent(readingListShow.this, readingListAdd.class);
                startActivity(readingListAdd);
                finish();
            }
        });

        fetchDataAndDisplay();


        readingListAdapter.setOnItemClickListener(new readingListAdapter.OnItemClickListener() {
            @Override
            public void onDelete(String bookKey, String title, int position) {

                // add alert dialog to confirm
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(readingListShow.this);
                alertDialog.setTitle("Are you sure you want to delete "+title+" ?");

                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        DatabaseReference newRef = databaseReference.child(bookKey);
                        newRef.removeValue();
                        list.remove(position);
                        readingListAdapter.notifyItemRemoved(position);
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
            public void onUpdate(String bookKey, String title, String author, String genre, int position) {
                Intent readingListUpdate = new Intent(readingListShow.this,readingListUpdate.class);
                readingListUpdate.putExtra("bookKey",bookKey);
                readingListUpdate.putExtra("title", title);
                readingListUpdate.putExtra("author", author);
                readingListUpdate.putExtra("genre",genre);
                startActivity(readingListUpdate);

                finish();
            }
        });

        emailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = "";

                for (int i=0; i < list.size(); i++){
                    Book b = list.get(i);
                    message += i+1 + ". " + b.getTitle() +"  by "+ b.getAuthor() + "\n";
                }

                composeEmail(user.getEmail(), "My Reading List", message);
            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent dashboard = new Intent(readingListShow.this,Dashboard.class);
                startActivity(dashboard);
                finish();
            }
        });



    }

    private void fetchDataAndDisplay() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Book book = dataSnapshot.getValue(Book.class);
                    book.setBookId(dataSnapshot.getKey());

                    list.add(book);
                }

                readingListAdapter.notifyDataSetChanged();
                dialog.hide();
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buildRecyclerView() {
        recyclerView = findViewById(R.id.booklist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        readingListAdapter = new readingListAdapter(this,list);
        recyclerView.setAdapter(readingListAdapter);
    }

    public void composeEmail(String addresses, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
        else{
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, user.getEmail());
            email.putExtra(Intent.EXTRA_SUBJECT, "My Reading List");
            email.putExtra(Intent.EXTRA_TEXT, message);
            email.setData(Uri.parse("mailto:"));
            startActivity(Intent.createChooser(email, "Send Mail Using :"));
        }
    }


}