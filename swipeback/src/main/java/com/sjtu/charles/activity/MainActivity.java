package com.sjtu.charles.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hhl.swipebacksample.R;

public class MainActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void nextPage(View v){
        startActivity(new Intent(this,NextActivity.class));
    }
}
