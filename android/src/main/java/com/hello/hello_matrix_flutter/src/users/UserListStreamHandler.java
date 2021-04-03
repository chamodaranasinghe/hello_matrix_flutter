package com.hello.hello_matrix_flutter.src.users;

import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

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
                        j.put("displayName", user.getDisplayName());
                        j.put("userId", user.getUserId());
                        jsonArrayUsers.put(j);
                    }catch (Exception e){
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
