package com.upc.software.upcmem;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView inManage,outManage,upgrade,version,about;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initViews();
    }

    private void initViews() {
        inManage = (TextView) findViewById(R.id.inmanage);
        outManage = (TextView) findViewById(R.id.outmanage);
        upgrade = (TextView) findViewById(R.id.settingUpgrade);
        version = (TextView) findViewById(R.id.versionname);
        about = (TextView) findViewById(R.id.settingabout);
        inManage.setOnClickListener(this);
        outManage.setOnClickListener(this);
        about.setOnClickListener(this);
        version.setText(getAppVersionName(this));

    }
    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.inmanage:
                Intent intent1 = new Intent(SettingActivity.this,OutKindsEditActivity.class);
                startActivity(intent1);
                break;
            case R.id.outmanage:
                Intent intent2 = new Intent(SettingActivity.this,InKindsEditActivity.class);
                startActivity(intent2);
                break;
            case R.id.settingabout:
                Intent intent3 = new Intent(SettingActivity.this,AboutActivity.class);
                startActivity(intent3);
                break;
            default:break;
        }
    }
    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode = 1;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }
}
