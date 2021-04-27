package com.hello.hello_matrix_flutter.src.users;

import androidx.lifecycle.Observer;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;
import com.hello.hello_matrix_flutter.src.directory.DirectoryConnector;
import com.hello.hello_matrix_flutter.src.directory.UserProfile;

import org.json.JSONArray;
import org.json.JSONObject;
import org.matrix.android.sdk.api.session.user.model.User;

import java.util.List;

import io.flutter.plugin.common.EventChannel;

public class UserListStreamHandler implements EventChannel.StreamHandler {

    EventChannel.EventSink eventSink;

    Observer observer = new Observer<List<User>>() {
        @Override
        public void onChanged(List<User> users) {
            if (eventSink != null) {

                JSONArray jsonArrayUsers = new JSONArray();

                for (User user : users) {
                    JSONObject j = new JSONObject();
                    try {
                        if (user.getUserId().equals(SessionHolder.matrixSession.getMyUserId())) {

                        } else {
                            UserProfile p = DirectoryConnector.pullUserProfile(user.getUserId());
                            if(p!=null){
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
                                j.put("mxUserId", user.getUserId());
                                jsonArrayUsers.put(j);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                eventSink.success(jsonArrayUsers.toString());
            }
        }
    };

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        if (SessionHolder.matrixSession == null) {
            return;
        }
        eventSink = events;
        SessionHolder.matrixSession.getUsersLive().observeForever(observer);
    }

    @Override
    public void onCancel(Object arguments) {
        if (SessionHolder.matrixSession == null) {
            return;
        }
        SessionHolder.matrixSession.getUsersLive().removeObserver(observer);
        eventSink = null;

    }
}
