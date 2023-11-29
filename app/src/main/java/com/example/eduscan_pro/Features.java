package com.example.eduscan_pro;

import static android.widget.Toast.makeText;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class Features extends AppCompatActivity {

    GridLayout gridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);

        gridLayout = (GridLayout) findViewById(R.id.grid_layout);

        setSingleEvent(gridLayout);
    }

    private void setSingleEvent(GridLayout gridLayout) {
        for(int i=0 ; i<gridLayout.getChildCount();i++){
            CardView cardView = (CardView) gridLayout.getChildAt(i);
            final int  Final_I= i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Final_I == 0){
                        makeText(Features.this, "PDF Creating", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Features.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if(Final_I == 1){
                        makeText(Features.this, "Word File Creating", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Features.this, Word_File_Creation.class);
                        startActivity(intent);
                    }
                    else if(Final_I == 2){
                        makeText(Features.this, "Attendance Calculating.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Features.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        makeText(Features.this, "CT Mark Calculating.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Features.this, MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}