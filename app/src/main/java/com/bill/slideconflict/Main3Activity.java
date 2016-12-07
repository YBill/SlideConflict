package com.bill.slideconflict;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class Main3Activity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private MyViewPager myViewPager;
    // 图片资源
    private int[] imageRes = new int[]{R.drawable.a1, R.drawable.a2,
            R.drawable.a3, R.drawable.a4, R.drawable.a5, R.drawable.a6};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        myViewPager = (MyViewPager) findViewById(R.id.myViewPager);

        View tempView = LayoutInflater.from(this).inflate(R.layout.temp, null);
        ViewGroup listView = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.content_layout, null);

        ImageView view;
        for (int i = 0; i < imageRes.length; i++) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(i);
            radioGroup.addView(radioButton);
            if (i == 0) {
                radioButton.setChecked(true);
            }

            if (i == 2) {
                myViewPager.addView(tempView);
            } else if (i == 4) {
                createList(listView);
                myViewPager.addView(listView);
            } else {
                view = new ImageView(this);
                view.setBackgroundResource(imageRes[i]);
                myViewPager.addView(view);
            }
        }

        myViewPager.setOnPageChangedListener(new MyViewPager.PageChangedListener() {
            @Override
            public void moveToDest(int position) {
                ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                myViewPager.moveToDest(checkedId);
            }
        });

    }

    private void createList(ViewGroup layout) {
        ListView listView = (ListView) layout.findViewById(R.id.list);
        ArrayList<String> datas = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            datas.add("name " + i);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, datas);
        listView.setAdapter(adapter);
    }

}
