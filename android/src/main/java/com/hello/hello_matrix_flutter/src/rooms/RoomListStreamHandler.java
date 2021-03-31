package com.hello.hello_matrix_flutter.src.rooms;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.matrix.android.sdk.api.session.content.ContentUrlResolver;
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams;
import org.matrix.android.sdk.api.session.room.model.RoomSummary;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.EventChannel;

public class RoomListStreamHandler implements EventChannel.StreamHandler {

    EventChannel.EventSink eventSink;

    Observer observer = new Observer<List<RoomSummary>>() {
        @Override
        public void onChanged(List<RoomSummary> roomSummaries) {
            if (eventSink != null) {
                List<RoomSummaryLite> rooms = new ArrayList<>();
                for (RoomSummary room : roomSummaries) {
                    RoomSummaryLite roomSummaryLite = new RoomSummaryLite();
                    roomSummaryLite.roomId = room.getRoomId();
                    roomSummaryLite.roomName = room.getDisplayName();
                    roomSummaryLite.roomTopic = room.getTopic();
                    roomSummaryLite.isDirect = room.isDirect();
                    roomSummaryLite.notificationCount = room.getNotificationCount();
                    roomSummaryLite.avatarUrl = resolveAvatarUrl(room.getAvatarUrl());
                    rooms.add(roomSummaryLite);
                }
                eventSink.success(new Gson().toJson(rooms));
            }
        }
    };

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        eventSink = events;
        SessionHolder.matrixSession.getRoomSummariesLive(new RoomSummaryQueryParams.Builder().build()).observeForever(observer);
    }

    @Override
    public void onCancel(Object arguments) {
        SessionHolder.matrixSession.getRoomSummariesLive(new RoomSummaryQueryParams.Builder().build()).removeObserver(observer);
        eventSink = null;
    }
    
    private String resolveAvatarUrl(String url){
        if(url.isEmpty())
            return "";
        return SessionHolder.matrixSession.contentUrlResolver().resolveThumbnail(url,250,250, ContentUrlResolver.ThumbnailMethod.SCALE);
    }
}
