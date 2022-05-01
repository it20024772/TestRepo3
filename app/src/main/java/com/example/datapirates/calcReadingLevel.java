package com.example.datapirates;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class calcReadingLevel extends AppCompatActivity {
    private TextInputEditText edtExtract;
    private Button calculate;
    private TextView gradeTxt;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calc_reading_level);

        calculate = findViewById(R.id.btnCalculateGrade);
        gradeTxt = findViewById(R.id.gradeTxt);
        backArrow = findViewById(R.id.backArrow);

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int letters = 0;
                int words = 0;
                int sentences = 0;

                edtExtract = findViewById(R.id.edtSentences);

                String Extract_txt = edtExtract.getText().toString();

                if (TextUtils.isEmpty(Extract_txt)) {
                    Toast.makeText(calcReadingLevel.this, "Enter several sentences from book", Toast.LENGTH_SHORT).show();
                }
                else{
                    for (int i=0; i < Extract_txt.length(); i++){
                        char c = Extract_txt.charAt(i);

                        if (c == '.' || c == '!' || c== '?'){
                            sentences += 1;
                        }
                        else if(c == ' '){
                            words += 1;
                        }
                        else if (Character.isLetter(c)){
                            letters += 1;
                        }
                    }
                    words += 1;
                    if (sentences == 0){
                        Toast.makeText(calcReadingLevel.this, "Enter at least one sentence", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        float L = ((float) letters / words) * 100; // letters per hundred words
                        float S = ((float) sentences / words) * 100; // sentences per hundred words

                        long index =  Math.round(0.0588 * L - 0.296 * S - 15.8);

                        if (index >= 16){
                            gradeTxt.setText("Grade 16+");
                        }
                        else if (index < 1){
                            gradeTxt.setText("Before Grade 1");
                        }
                        else {
                            gradeTxt.setText("Grade " + index);
                        }

                        Toast.makeText(calcReadingLevel.this, "Grade Calculated", Toast.LENGTH_SHORT).show();

                    }


                }

            }
        });

        // back button functionality
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // got to dashboard
                Intent Dashboard = new Intent(calcReadingLevel.this, Dashboard.class);
                startActivity(Dashboard);
                finish();
            }
        });

    }
}