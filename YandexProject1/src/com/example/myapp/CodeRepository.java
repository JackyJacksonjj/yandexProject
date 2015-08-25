package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;

import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.myapp.MainAuthenticationActivity.AUTH_URL;
import static com.example.myapp.MainAuthenticationActivity.CLIENT_ID;

public class CodeRepository extends PreferenceActivity {
    public static String TOKEN;
    protected static final String TAG = "TokenRepositoryTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.tokenrepo);

        getToken();
        if (TOKEN != null) {
            if (TOKEN.length() <= 25) {
                getToken();
            } else {
                TOKEN = TOKEN.toString();
            }
        }

    }

    class cls extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                URL url = new URL(AUTH_URL + CLIENT_ID);
                HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                ucon.setInstanceFollowRedirects(false);
                URL secondURL = new URL(ucon.getHeaderField("Location"));
                HttpURLConnection urlconn = (HttpURLConnection) secondURL.openConnection();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlconn.getURL().toString()));
                startActivity(intent);
                return null;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void getToken() {
        if (TOKEN == null) {
            EditTextPreference edt = (EditTextPreference) findPreference("token");
            String text = edt.getText();
            if (text == null || text.length() < 2) {
                new cls().execute();
            } else {
                TOKEN = text;
            }
        } else {
            EditTextPreference edt = (EditTextPreference) findPreference("token");
            String text = edt.getText();
            TOKEN = text;
        }
    }
}
