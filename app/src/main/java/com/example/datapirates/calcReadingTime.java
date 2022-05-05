package com.example.datapirates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class calcReadingTime extends AppCompatActivity {

    private TextInputEditText edtPagesRead, edtTotalPages, edtTimeTaken;
    private Button calculate;
    private TextView hoursTxt, minutesTxt;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_reading_time);

        calculate = findViewById(R.id.btnCalculate);
        hoursTxt = findViewById(R.id.hoursTxt);
        minutesTxt = findViewById(R.id.minutesTxt);
        backArrow = findViewById(R.id.backArrow);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPagesRead = findViewById(R.id.edtNumOfPages);
                edtTotalPages = findViewById(R.id.edtTotalNumOfPages);
                edtTimeTaken = findViewById(R.id.edtTimeTaken);

                String edtPagesRead_txt = edtPagesRead.getText().toString();
                String edtTotalPages_txt = edtTotalPages.getText().toString();
                String edtTimeTaken_txt = edtTimeTaken.getText().toString();

                if (TextUtils.isEmpty(edtPagesRead_txt)) {
                    Toast.makeText(calcReadingTime.this, "Enter number of pages read", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtTotalPages_txt)) {
                    Toast.makeText(calcReadingTime.this, "Enter total number of pages", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(edtTimeTaken_txt)) {
                    Toast.makeText(calcReadingTime.this, "Enter time taken", Toast.LENGTH_SHORT).show();
                }else{
                    int pagesRead = Integer.parseInt(edtPagesRead_txt);
                    int totalPages = Integer.parseInt(edtTotalPages_txt);
                    int timeTaken = Integer.parseInt(edtTimeTaken_txt);

                    int [] results = findReadingTime(pagesRead, totalPages,timeTaken);
                    hoursTxt.setText(String.valueOf(results[0]));
                    minutesTxt.setText(String.valueOf(results[1]));
                    Toast.makeText(calcReadingTime.this, "Time Calculated", Toast.LENGTH_SHORT).show();

                }



            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent Dashboard = new Intent(calcReadingTime.this, Dashboard.class);
                startActivity(Dashboard);
                finish();
            }
        });

    }

    public static int[] findReadingTime(int pagesRead, int totalPages, int timeTaken) {
        float pagesPerMinute = (float) timeTaken / pagesRead;
        int totalTime = Math.round(pagesPerMinute * totalPages);
        int results [] = {0,0};

        int hours = totalTime / 60;
        int minutes = totalTime % 60;

        results[0] = hours;
        results[1] = minutes;

        return results;


    }
}