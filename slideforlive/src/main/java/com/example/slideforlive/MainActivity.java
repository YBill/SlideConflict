package com.example.slideforlive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button firstBtn;
    private Button secondBtn;
    private LiveSlideView liveSlideView;
    private Button statusBtn;
    private Button showBtn;
    private Button hideBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        liveSlideView = (LiveSlideView) findViewById(R.id.view_group);
        firstBtn = (Button) findViewById(R.id.btn_click);
        secondBtn = (Button) findViewById(R.id.btn_nb);
        statusBtn = (Button) findViewById(R.id.btn_status);
        showBtn = (Button) findViewById(R.id.btn_show);
        hideBtn = (Button) findViewById(R.id.btn_hide);
        firstBtn.setOnClickListener(this);
        secondBtn.setOnClickListener(this);
        statusBtn.setOnClickListener(this);
        showBtn.setOnClickListener(this);
        hideBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == firstBtn) {
            Toast.makeText(MainActivity.this, "" + firstBtn.getText().toString(), Toast.LENGTH_SHORT).show();
        } else if (v == secondBtn) {
            Toast.makeText(MainActivity.this, "" + secondBtn.getText().toString(), Toast.LENGTH_SHORT).show();
        } else if (v == statusBtn) {
            Toast.makeText(MainActivity.this, "" + liveSlideView.isViewShow(), Toast.LENGTH_SHORT).show();
        } else if (v == showBtn) {
            liveSlideView.setViewShow(true);
        } else if (v == hideBtn) {
            liveSlideView.setViewShow(false);
        }
    }
}
