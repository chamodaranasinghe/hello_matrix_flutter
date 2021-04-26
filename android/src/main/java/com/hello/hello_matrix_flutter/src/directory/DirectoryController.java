package com.hello.hello_matrix_flutter.src.directory;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.AnyThread;
import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.room.Room;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;
import com.hello.hello_matrix_flutter.src.storage.DataStorage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.MethodChannel;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class DirectoryController {

    String _tag = "DirectoryController";
    DirectoryDatabase db;

    public DirectoryDatabase initDB() {
        db = Room.databaseBuilder(SessionHolder.appContext,
                DirectoryDatabase.class, "hello-directory").allowMainThreadQueries().build();
        return db;
    }

    public void updateDirectory(@NonNull final MethodChannel.Result result) {

        initDB();

        DirectoryClient directoryClient = new DirectoryClient();
        DataStorage dataStorage = new DataStorage();

        String orgCode = "";
        String userCode = "";

        String profileData = dataStorage.getStringData(DataStorage.KEY_PROFILE_STORAGE);
        try {
            JSONObject profileDataJson = new JSONObject(profileData);
            orgCode = profileDataJson.getString("org_prefix");
            userCode = profileDataJson.getString("hello_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //setting the request body
        HttpUrl.Builder
                urlBuilder = HttpUrl.parse(directoryClient.getEndpoint("global_contacts")).newBuilder();
        urlBuilder.addQueryParameter("orgCode", orgCode);
        urlBuilder.addQueryParameter("userCode", userCode);

        directoryClient.okHttpClient.newCall(directoryClient.getRequest(urlBuilder.build().toString())).enqueue(new Callback() {
            @Override
            @UiThread
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e(_tag, "error in getting response using async okhttp call");
                handleErrorOnMainThread(result);
                return;
            }

            @Override
            @UiThread
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                if (!response.isSuccessful()) {
                    handleErrorOnMainThread(result);
                    return;
                }
                try {
                    String profileData = response.body().string();
                    saveNewDataInLocalDbAndUpdateInstance(result, profileData);
                    //profile data exist, continue Matrix login
                } catch (IOException | JSONException e) {
                    result.error("", "Error" + e.getMessage(), false);
                    e.printStackTrace();
                    return;
                }
            }
        });

    }

    public void retrieveDirectory(@NonNull final MethodChannel.Result result) {
        if (DirectoryInstance.getInstance().getDirectoryList().isEmpty()) {
            refreshDirectory();
        }
        List<UserProfile> list = DirectoryInstance.getInstance().getDirectoryList();
        JSONArray jsonArray = new JSONArray();
        for (UserProfile p : list) {
            JSONObject j = new JSONObject();
            try {
                j.put("hello_id", p.getHelloId());
                j.put("first_name", p.getFirstName());
                j.put("last_name", p.getLastName());
                j.put("email", p.getEmail());
                j.put("contact", p.getContact());
                j.put("job_title", p.getJobTitle());
                j.put("photo", p.getPhotoUrl());
                j.put("thumbnail", p.getPhotoThumbnail());
                j.put("org_prefix", p.getOrgPrefix());
                j.put("org_name", p.getOrgName());
                j.put("org_contact", p.getOrgContact());
                j.put("org_website", p.getOrgWebsite());
                jsonArray.put(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (result != null) {
            result.success(jsonArray.toString());
        }
        //add to stream every time when directory is refreshed
        //DirectoryStreamHandler.getInstance().AddToDirectoryStreamFromInstance(DirectoryInstance.getInstance().getDirectoryList());
    }

    //pull from local db and update current list
    private void refreshDirectory() {
        initDB();
        UserProfileDao userProfileDao = db.userProfileDao();
        DirectoryInstance.getInstance().setDirectoryList(userProfileDao.getAll());

        //add to stream every time when directory is refreshed
        //DirectoryStreamHandler.getInstance().AddToDirectoryStreamFromInstance(DirectoryInstance.getInstance().getDirectoryList());
    }

    public void eraseDirectory() {
        initDB();
        UserProfileDao userProfileDao = db.userProfileDao();
        userProfileDao.deleteAll();
        refreshDirectory();

    }

    @AnyThread
    private void handleErrorOnMainThread(@NonNull final MethodChannel.Result result) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                result.error("", "Unable to get directory data", false);
            }
        });
    }

    @AnyThread
    private void handleSuccessOnMainThread(@NonNull final MethodChannel.Result result) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                result.success(true);
            }
        });
    }

    public void saveNewDataInLocalDbAndUpdateInstance(@NonNull final MethodChannel.Result result, String json) throws JSONException {

        final List<UserProfile> userProfileList = new ArrayList<UserProfile>();
        JSONArray jsonArrayProfiles = new JSONArray(json);
        for (int i = 0; i < jsonArrayProfiles.length(); i++) {
            JSONObject j = jsonArrayProfiles.getJSONObject(i);
            UserProfile userProfile = new UserProfile();
            userProfile.setHelloId(j.getString("hello_id"));
            userProfile.setFirstName(j.getString("first_name"));
            userProfile.setLastName(j.getString("last_name"));
            userProfile.setEmail(j.getString("email"));
            userProfile.setContact(j.getString("contact"));
            userProfile.setJobTitle(j.getString("job_title"));
            userProfile.setPhotoUrl(j.getString("photo"));
            userProfile.setPhotoThumbnail(j.getString("thumbnail"));
            userProfile.setOrgPrefix(j.getString("org_prefix"));
            userProfile.setOrgName(j.getString("org_name"));
            userProfile.setOrgContact(j.getString("org_contact"));
            userProfile.setOrgWebsite(j.getString("org_website"));
            userProfileList.add(userProfile);
        }

        //get directory dao
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                UserProfileDao userProfileDao = db.userProfileDao();
                userProfileDao.deleteAll();
                userProfileDao.insertAll(userProfileList);
                List<UserProfile> directory = userProfileDao.getAll();
                DirectoryInstance.getInstance().setDirectoryList(directory);
                //update stream after updating the directory
                //DirectoryStreamHandler.getInstance().AddToDirectoryStreamFromInstance(DirectoryInstance.getInstance().getDirectoryList());
                if (result != null) {
                    handleSuccessOnMainThread(result);
                }
            }
        });


    }
}
