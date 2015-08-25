package com.example.myapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;

import static com.example.myapp.MainAuthenticationActivity.TOKEN;

public class CreateUpdateDeleteResource extends Activity implements AsyncResponse, View.OnClickListener {
    private static final String TAG = "CreateResourceTAG";

    private static final String currentPath = "https://cloud-api.yandex.net:443/v1/disk";
    private static final String requestPath = "/resources?path=";
    String resourceName;
    private String nextOrPreviousPath = "/";

    private String resourceInfo;

    private ArrayList<String> filesNames = new ArrayList<>();
    private ArrayList<String> filesWeight = new ArrayList<>();
    private ArrayList<String> filesPath = new ArrayList<>();
    private ListAdapter adapter;
    private static ArrayList<SomeEntity> entities = new ArrayList<SomeEntity>();

    Button createNewFolder;
    Button refreshList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(TOKEN != null){
            setContentView(R.layout.file_worker);
            createNewFolder = (Button) findViewById(R.id.newFolderButton);
            refreshList = (Button) findViewById(R.id.refreshListButton);
            createNewFolder.setOnClickListener(this);
            refreshList.setOnClickListener(this);
            start(4);
        }
        else{
            Intent intent = new Intent(this, CodeRepository.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        if (nextOrPreviousPath != "/" && nextOrPreviousPath.contains("/")) {
            nextOrPreviousPath = nextOrPreviousPath.substring(0, nextOrPreviousPath.lastIndexOf("/"));
            start(4);
        } else {
            super.onBackPressed();
        }
    }

    public void startDialog() {
        final Dialog requestDialog = new Dialog(CreateUpdateDeleteResource.this);
        requestDialog.setContentView(R.layout.dialog_with_request);
        requestDialog.setTitle("Enter file name:");

        Button declineButton = (Button) requestDialog.findViewById(R.id.declineButton);
        Button acceptButton = (Button) requestDialog.findViewById(R.id.acceptButton);
        declineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDialog.dismiss();
            }
        });
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText dialogResult = (EditText) requestDialog.findViewById(R.id.fileNameContainer);
                if(resourceName == "/") {
                    resourceName +=  dialogResult.getText().toString();
                }else{
                    resourceName += "/" + dialogResult.getText().toString();
                }
                requestDialog.dismiss();
                start(1);
            }
        });
        requestDialog.show();
    }

    public void goNextButtonHandler(View v) {
        SomeEntity item = (SomeEntity) v.getTag();
        String path = item.getPath();
        if (item.getValue() < 0) {
            nextOrPreviousPath = path;
            start(4);
        }
    }

    public void refreshButtonHandler(View v) {
        SomeEntity item = (SomeEntity) v.getTag();
        resourceName = item.getPath();
        start(3);
    }

    public void createButtonHandler(View v) {
        SomeEntity item = (SomeEntity) v.getTag();
        resourceName = item.getPath();
        startDialog();

    }

    public void removeButtonHandler(View v) {
        SomeEntity item = (SomeEntity) v.getTag();
        resourceName = item.getPath();
        entities.remove(item);
        setupListViewAdapter();
        start(2);
    }

    private void setupListViewAdapter() {
        adapter = new ListAdapter(CreateUpdateDeleteResource.this, R.layout.row_layout, entities);
        ListView adapterListView = (ListView) findViewById(R.id.EntityList);
        adapterListView.setAdapter(adapter);
    }

    public void setList() {
        entities.clear();
        int firstListSize = filesNames.size();
        int secondListSize = filesWeight.size();
        filesNames.remove(firstListSize - 1);
        int x = 0;
        for (int i = 0; i < filesNames.size(); i++) {
            if (i >= firstListSize - secondListSize - 1) {
                double weight = new BigDecimal(Double.parseDouble(filesWeight.get(x)) / 1048576)
                        .setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                entities.add(new SomeEntity(filesNames.get(i), weight, filesPath.get(i)));
                x++;
            } else {
                entities.add(new SomeEntity(filesNames.get(i), -1, filesPath.get(i)));
            }
        }

    }

    private void start(int i) {
        if (i == 4) new ResourceCreator(new AsyncResponse(){
            @Override
            public void processFinish(Object output) {
                resourceInfo = getStr((String) output);
                getInformationAboutFiles(resourceInfo);
                setList();
                setupListViewAdapter();
                resourceName = "";
            }
        }, i).execute();
        else {
            new ResourceCreator(new AsyncResponse(){
                @Override
                public void processFinish(Object output) {
                    resourceInfo = getStr((String) output);
                    resourceName = "";
                }
            }, i).execute();
        }
    }

    public void getInformationAboutFiles(String resource){
        filesNames.clear();
        filesWeight.clear();
        filesPath.clear();
        String[] balance = resource.split(",");

        for(int i=0; i < balance.length; i++){
            if(balance[i].contains("\"path\":\"disk:")){
                String path = balance[i].split("\":\"")[1];
                if((path.length()) > 7) {
                    filesPath.add(path.substring(0,path.length() - 1));
                }
            }
            if(balance[i].contains("\"size\":")){
                String get = balance[i].split(":")[1];
                if(get.contains("}]")){
                    filesWeight.add(get.substring(0, get.length() - 2));
                }
                else{
                    filesWeight.add(get.substring(0, get.length() - 1));
                }
            }
            if(balance[i].contains("\"name\":\"")){
                String name = balance[i].split(":\"")[1];
                filesNames.add(name.substring(0, name.length()-1));
            }
        }
    }

    public String getStr(String it) {
        return it;
    }

    @Override
    public void processFinish(Object output) {
        resourceInfo = getStr((String) output);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newFolderButton :
                resourceName += nextOrPreviousPath;
                startDialog();
                break;
            case R.id.refreshListButton :
                start(4);
                break;
        }
    }

    class ResourceCreator extends AsyncTask<Object, Void, String> {
        public AsyncResponse delegate = null;
        public int methodCode;

        public ResourceCreator(AsyncResponse delegate, int i) {
            this.delegate = delegate;
            methodCode = i;
        }

        @Override
        protected String doInBackground(Object... voids) {
            try {
                String info = "";
                HttpClient httpClient = new DefaultHttpClient();
                if (methodCode == 1) {
                    HttpPut request = new HttpPut(currentPath + requestPath + URLEncoder.encode(resourceName, "UTF-8"));
                    request.addHeader("Authorization", "OAuth " + TOKEN);
                    httpClient.execute(request);
                } else if (methodCode == 2) {
                    HttpDelete request = new HttpDelete(currentPath + requestPath + URLEncoder.encode(resourceName, "UTF-8"));
                    request.addHeader("Authorization", "OAuth " + TOKEN);
                    httpClient.execute(request);

                } else if (methodCode == 3) {
                    HttpPatch request = new HttpPatch(currentPath + requestPath + URLEncoder.encode(resourceName, "UTF-8"));
                    request.addHeader("Authorization", "OAuth " + TOKEN);
                    httpClient.execute(request);
                } else if (methodCode == 4) {
                    HttpGet request = new HttpGet(currentPath + requestPath + URLEncoder.encode(nextOrPreviousPath, "UTF-8"));
                    request.addHeader("Authorization", "OAuth " + TOKEN);
                    HttpResponse response = httpClient.execute(request);
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = rd.readLine()) != null) {
                        result.append(line);
                    }
                    info = result.toString();
                }
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


    public class HttpPatch extends HttpPost {
        public static final String METHOD_PATCH = "PATCH";

        public HttpPatch(final String url) {
            super(url);
        }

        @Override
        public String getMethod() {
            return METHOD_PATCH;
        }
    }
}