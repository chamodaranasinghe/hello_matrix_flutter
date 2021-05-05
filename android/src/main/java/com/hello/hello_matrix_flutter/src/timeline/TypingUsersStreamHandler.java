package com.hello.hello_matrix_flutter.src.timeline;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.hello.hello_matrix_flutter.PluginBindingHolder;
import com.hello.hello_matrix_flutter.src.directory.DirectoryConnector;
import com.hello.hello_matrix_flutter.src.directory.DirectoryInstance;
import com.hello.hello_matrix_flutter.src.directory.UserProfile;

import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.model.RoomSummary;
import org.matrix.android.sdk.api.session.room.sender.SenderInfo;
import org.matrix.android.sdk.api.util.Optional;

import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.EventChannel;

public class TypingUsersStreamHandler implements EventChannel.StreamHandler {

    String _tag = "TypingUsersStreamHandler";
    Room room;
    private EventChannel typingUserStreamHandlerChannel;
    EventChannel.EventSink eventSink;

    Observer observer = new Observer<Optional<RoomSummary>>() {
        @Override
        public void onChanged(Optional<RoomSummary> roomSummaryOptional) {

            RoomSummary summary = roomSummaryOptional.getOrNull();
            if (summary != null) {
                Log.i(_tag, "not null");
                List<String> typingUserNames = new ArrayList<>();

                if (summary.getTypingUsers().size() == 0) {
                    typingUserNames.clear();
                    if (eventSink != null) {
                        eventSink.success(typingUserNames);
                        return;
                    }
                    return;
                }

                for (SenderInfo sender : summary.getTypingUsers()) {
                    UserProfile profile = DirectoryConnector.pullUserProfile(sender.getUserId());
                    if (profile != null) {
                        String fullName = DirectoryConnector.pullUserProfile(sender.getUserId()).firstName + " " + DirectoryConnector.pullUserProfile(sender.getUserId()).lastName;
                        typingUserNames.add(fullName);
                        if (eventSink != null) {
                            eventSink.success(typingUserNames);
                            return;
                        }
                    } else {
                        typingUserNames.add(sender.getDisplayName());
                    }
                }
            } else {
                Log.i(_tag, "null");
                List<String> typingUserNames = new ArrayList<>();
                if (eventSink != null) {
                    eventSink.success(typingUserNames);
                    return;
                }
                return;
            }
        }
    };

    public TypingUsersStreamHandler(Room room) {
        this.room = room;
        init();
    }

    private void init() {
        typingUserStreamHandlerChannel = new EventChannel(PluginBindingHolder.flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter/typingUsersEvents");
        typingUserStreamHandlerChannel.setStreamHandler(this);
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
        room.getRoomSummaryLive().observeForever(observer);
    }

    @Override
    public void onCancel(Object arguments) {
        room.getRoomSummaryLive().removeObserver(observer);
        eventSink = null;
    }

    public void disposeTypingTracker() {
        room.getRoomSummaryLive().removeObserver(observer);
        typingUserStreamHandlerChannel.setStreamHandler(null);
    }
}
