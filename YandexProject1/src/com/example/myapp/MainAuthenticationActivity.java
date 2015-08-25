package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;


public class MainAuthenticationActivity extends Activity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    public static final String API_ROOT = "https://cloud-api.yandex.net:443/v1/";
    public static final String CLIENT_ID = "1b2a534dae0d4927adc4fdbac53d5076";
    public static final String AUTH_URL = "https://oauth.yandex.ru/authorize?response_type=token&client_id=";

    public static String TOKEN;
    private String TAG = "AuthenticationActivityTAG";
    public static SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        TOKEN = prefs.getString("token", null);

        Button diskInfo = (Button) findViewById(R.id.openDiskInfo);
        Button resourceList = (Button) findViewById(R.id.resourceList);
        Button codeButton = (Button) findViewById(R.id.codeRepo);
        Button refreshButton = (Button) findViewById(R.id.mainRefreshButton);
        refreshButton.setOnClickListener(this);
        codeButton.setOnClickListener(this);
        diskInfo.setOnClickListener(this);
        resourceList.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.openDiskInfo:
                Intent intent = new Intent(this, DiskInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.resourceList:
                Intent intent2 = new Intent(this, CreateUpdateDeleteResource.class);
                startActivity(intent2);
                break;
            case R.id.codeRepo:
                Intent intent3 = new Intent(this, CodeRepository.class);
                startActivity(intent3);
                break;
            case R.id.mainRefreshButton:
                TOKEN = prefs.getString("token", null);
                break;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }
}

