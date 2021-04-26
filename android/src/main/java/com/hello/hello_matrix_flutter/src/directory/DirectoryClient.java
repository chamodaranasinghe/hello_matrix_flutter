package com.hello.hello_matrix_flutter.src.directory;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DirectoryClient {
    String TAG = "DirectoryClient";
    String BASE_URL = "https://admin.hellodesk.app/_client/";
    String TOKEN = "P8qTgBpTKAwFpzhBlxw7AA==";
    public OkHttpClient okHttpClient;

    public DirectoryClient(){
        okHttpClient = new OkHttpClient();
    }
    
    public String getEndpoint(String endpoint){
        return BASE_URL+endpoint;
    }

    public Request getRequest(String endPoint){
        Request request = new Request.Builder()
                .url(endPoint)
                .header("Basic-Token",TOKEN)
                .build();
        return request;
    }
}
