package com.sjtu.custom.activity;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sjtu.custom.R;
import com.sjtu.custom.view.ScriptCardChineseView;


public class MainActivity extends AppCompatActivity {

    ScriptCardChineseView tv_name_chinese;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_name_chinese = (ScriptCardChineseView) findViewById(R.id.tv_name_chinese);
        tv_name_chinese.setStr("更新后可能启动不了，这时要在网上寻找更");
        tv_name_chinese.setTextSizePinyin(18);
        tv_name_chinese.setTextSizeHanzi(22);
        tv_name_chinese.setTextColor(ContextCompat.getColor(this, R.color.clr_09C0CE));
        tv_name_chinese.setVisibility(View.VISIBLE);
    }
}
