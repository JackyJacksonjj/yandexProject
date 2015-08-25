package com.example.myapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;

import static com.example.myapp.MainAuthenticationActivity.API_ROOT;
import static com.example.myapp.MainAuthenticationActivity.TOKEN;

public class DiskInfoActivity extends Activity implements AsyncResponse {

    private static final String TAG = "DiskInfoActivityTAG";
    private static TextView trashSizeVolume;
    private static TextView totalSizeVolume;
    private static TextView usedSpaceVolume;

    public static String diskInfo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disk_info);
        startUp();
    }

    public void startUp() {
        if (TOKEN == null) {

            new CodeRepository().getToken();

            new DiskI(new AsyncResponse(){
                @Override
                public void processFinish(Object output) {
                    diskInfo = getStr((String) output);
                    getDiskInfo(diskInfo);
                }
            }).execute();
        } else {
            new DiskI(new AsyncResponse(){
                @Override
                public void processFinish(Object output) {
                    diskInfo = getStr((String) output);
                    getDiskInfo(diskInfo);
                }
            }).execute();
        }
    }

    public void getDiskInfo(String string) {
        String balance = string;
        String[] array = balance.split(",");

        trashSizeVolume = (TextView) findViewById(R.id.trashSizeVolume);
        trashSizeVolume.append(String.format("%s", new BigDecimal(Double.parseDouble(array[0].split(":")[1]) / 1048576)
                .setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));

        totalSizeVolume = (TextView) findViewById(R.id.totalSpaceVolume);
        totalSizeVolume.append(String.format("%s", new BigDecimal(Double.parseDouble(array[1].split(":")[1]) / 1048576)
                .setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));

        usedSpaceVolume = (TextView) findViewById(R.id.usedSpaceVolume);
        usedSpaceVolume.append(String.format("%s", new BigDecimal(Double.parseDouble(array[2].split(":")[1]) / 1048576)
                .setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue()));
    }

    public ArrayList<String> getInfo(String string) {
        ArrayList<String> array = new ArrayList<>();
        String balance = string;
        while (balance.contains(",") || balance.contains("/")) {
            String[] s = balance.split(",");
            balance = s[1];
            String usedNow = s[0];
            while (usedNow.contains(":")) {
                usedNow = usedNow.split(":")[1];
            }
            if (usedNow.contains("\"") && usedNow.contains("/")) {
                usedNow = usedNow.split("\"")[0];
            }
            array.add(usedNow);
        }
        return array;
    }

    public String getStr(String it) {
        diskInfo.concat(it);
        return it;
    }

    @Override
    public void processFinish(Object output) {
        diskInfo = getStr((String) output);
    }

    class DiskI extends AsyncTask<Object, Void, String> {
        public AsyncResponse delegate = null;

        public DiskI(AsyncResponse delegate) {
            this.delegate = delegate;
        }

        @Override
        protected String doInBackground(Object... voids) {
            try {
                String info = "";

                HttpClient httpClient = new DefaultHttpClient();
                HttpGet request = new HttpGet(API_ROOT + "disk");
                request.addHeader("Authorization", "OAuth " + TOKEN);
                HttpResponse response = httpClient.execute(request);
                BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                info = result.toString();
                return info;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            delegate.processFinish(result);
        }
    }
}
