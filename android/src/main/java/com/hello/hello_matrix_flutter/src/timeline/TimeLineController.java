package com.hello.hello_matrix_flutter.src.timeline;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.HelloMatrixFlutterPluginMethodChannel;
import com.hello.hello_matrix_flutter.PluginBindingHolder;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.timeline.Timeline;
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent;
import org.matrix.android.sdk.api.session.room.timeline.TimelineSettings;

import java.util.ArrayList;
import java.util.List;

import io.flutter.plugin.common.EventChannel;

public class TimeLineController implements Timeline.Listener, EventChannel.StreamHandler {

    String _tag = "ChatTimeLine";

    String roomId;
    Room room;
    private EventChannel timeLineEventChannel;
    EventChannel.EventSink eventSink;
    TypingUsersStreamHandler typingUsersStreamHandler;

    Timeline timeline;
    TimelineSettings timelineSettings = new TimelineSettings(
            10,
            true);

    public TimeLineController(String roomId) {
        this.roomId = roomId;
        this.init();
    }

    private void init() {
        room = SessionHolder.matrixSession.getRoom(roomId);
        timeline = room.createTimeline(null, timelineSettings);
        timeline.addListener(this);
        timeline.isLive();
        timeline.start();
        timeLineEventChannel = new EventChannel(PluginBindingHolder.flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter/timelineEvents");
        timeLineEventChannel.setStreamHandler(this);

        //start users typing tracker
        typingUsersStreamHandler = new TypingUsersStreamHandler(room);

    }


    @Override
    public void onNewTimelineEvents(@NotNull List<String> list) {
        Log.i(_tag, "onNewTimelineEvents");
    }

    @Override
    public void onTimelineFailure(@NotNull Throwable throwable) {
        Log.i(_tag, "onTimelineFailure");
        Log.i(_tag, new Gson().toJson(throwable));

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onTimelineUpdated(@NotNull List<TimelineEvent> list) {
        Log.i(_tag, "onTimelineUpdated");
        if (eventSink != null) {
            List<TimeLineEventLite> events = new ArrayList<>();
            for (TimelineEvent event : list) {
                Log.i(_tag, event.getRoot().getType());
                TimeLineEventLite timeLineEventLite = new TimeLineEventLite();
                timeLineEventLite.type = event.getRoot().getType();
                timeLineEventLite.localId = event.getLocalId();
                timeLineEventLite.eventId = event.getEventId();
                timeLineEventLite.originServerTs = event.getRoot().getOriginServerTs();
                timeLineEventLite.localTs = event.getRoot().getAgeLocalTs();
                if (event.getRoot().getType().equals("m.room.message")) {
                    timeLineEventLite.clearedContent = new JSONObject(event.getRoot().getClearContent()).toString();
                }
                if (event.getSenderInfo().getUserId().equals(SessionHolder.matrixSession.getMyUserId())) {
                    timeLineEventLite.direction = "sent";
                } else {
                    timeLineEventLite.direction = "received";
                }


                Log.i(_tag, "type " + event.getRoot().getType());
                //Log.i(_tag,"clear type "+event.getRoot().getType());
                //Log.i(_tag,"clear content "+event.getRoot().getClearContent());

                events.add(timeLineEventLite);
            }

            JSONArray jsonArrayEvents = new JSONArray();
            for (TimeLineEventLite timeLineEventLite : events) {
                JSONObject j = new JSONObject();
                try {
                    j.put("type", timeLineEventLite.type);
                    j.put("localId", timeLineEventLite.localId);
                    j.put("eventId", timeLineEventLite.eventId);
                    j.put("originServerTs", timeLineEventLite.originServerTs);
                    j.put("localTs", timeLineEventLite.localTs);
                    j.put("clearedContent", timeLineEventLite.clearedContent);
                    Log.i(_tag, "clear content " + timeLineEventLite.clearedContent);
                    j.put("direction", timeLineEventLite.direction);
                    jsonArrayEvents.put(j);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            eventSink.success(jsonArrayEvents.toString());
        }
    }

    public void destroyTimeLine() {
        Log.i(_tag, "destroyTimeLine");
        timeline.dispose();
        timeline.removeAllListeners();
        typingUsersStreamHandler.disposeTypingTracker();
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
        Log.i(_tag, "onListenEvents");
    }

    @Override
    public void onCancel(Object arguments) {
        eventSink = null;
        Log.i(_tag, "onCancelEvents");
    }

    //region Supportive methods
    public void paginateBackward() {
        //when this is called, paginate backwards and onTimeLineUpdated is called
        if (timeline.hasMoreToLoad(Timeline.Direction.BACKWARDS)) {
            timeline.paginate(Timeline.Direction.BACKWARDS, 10);
            HelloMatrixFlutterPluginMethodChannel.getInstance().result.success(true);
        } else {
            HelloMatrixFlutterPluginMethodChannel.getInstance().result.success(false);
        }
    }

    public void onStartTyping() {
        room.userIsTyping();
        HelloMatrixFlutterPluginMethodChannel.getInstance().result.success(true);
    }

    public void onStopTyping() {
        room.userStopsTyping();
        HelloMatrixFlutterPluginMethodChannel.getInstance().result.success(true);
    }
    //endregion
}

