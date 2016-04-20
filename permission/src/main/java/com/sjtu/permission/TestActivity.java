package com.sjtu.permission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * 对于6.0权限操作可按照以下TestActivity 进行权限申请
 * 注意：在Manifest中注册相应权限，因为6.0之前可以直接获取权限
 *
 * Created by CharlesZhu on 2016/4/20.
 */
public class TestActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1001; // 请求码

    // 所需的全部权限
    static final String[] PERMISSIONS = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.MODIFY_AUDIO_SETTINGS
    };

    private PermissionsChecker mPermissionsChecker; // 权限检测器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.main_t_toolbar);
        setSupportActionBar(toolbar);

        mPermissionsChecker = new PermissionsChecker(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 缺少权限时, 进入权限配置页面
        if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
            startPermissionsActivity();
        }
    }

    private void startPermissionsActivity() {
        PermissionsActivity.startActivityForResult(this, REQUEST_CODE, PERMISSIONS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == PermissionsActivity.PERMISSIONS_DENIED) {  // 拒绝时, 关闭页面, 缺少主要权限, 无法运行
                finish();
            } else {   //TODO 成功授权权限继续操作

            }
        }
    }
}
