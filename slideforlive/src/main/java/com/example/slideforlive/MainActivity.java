package com.example.slideforlive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button firstBtn;
    private Button secondBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firstBtn = (Button) findViewById(R.id.btn_click);
        secondBtn = (Button) findViewById(R.id.btn_nb);
        firstBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "" + firstBtn.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });
        secondBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "" + secondBtn.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}
