/*
package com.hello.hello_matrix_flutter.src.directory;

import android.util.Log;

import com.hello.hello_matrix_flutter.PluginBindingHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.flutter.plugin.common.EventChannel;

public class DirectoryStreamHandler implements EventChannel.StreamHandler {
    static String _tag = "DirectoryStreamHandler";
    static EventChannel.EventSink eventSink;
    private EventChannel directoryListEventChannel;
    private static DirectoryStreamHandler INSTANCE = null;

    public DirectoryStreamHandler() {
        directoryListEventChannel = new EventChannel(PluginBindingHolder.flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter/directoryListEvents");
        directoryListEventChannel.setStreamHandler(this);
    }

   */
/* public static DirectoryStreamHandler getInstance() {
        if (INSTANCE == null) {
            Log.i(_tag, "new instance created");
            INSTANCE = new DirectoryStreamHandler();
        }
        return (INSTANCE);
    }*//*


   */
/* public static void AddToDirectoryStreamFromInstance(List<UserProfile> currentDirectory) {
        if (eventSink == null) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for (UserProfile p : currentDirectory) {
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
        Log.i(_tag,jsonArray.toString());
        eventSink.success(jsonArray.toString());
    }*//*


    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        Log.i(_tag, "On listen called on directory stream handler");
        eventSink = events;
    }

    @Override
    public void onCancel(Object arguments) {

        //should come last
        eventSink = null;
    }
}
*/
