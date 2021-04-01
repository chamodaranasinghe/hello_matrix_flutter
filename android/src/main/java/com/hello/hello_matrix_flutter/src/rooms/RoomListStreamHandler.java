package com.hello.hello_matrix_flutter.src.rooms;

import android.util.Log;

import androidx.lifecycle.Observer;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.matrix.android.sdk.api.session.content.ContentUrlResolver;
import org.matrix.android.sdk.api.session.crypto.MXCryptoError;
import org.matrix.android.sdk.api.session.events.model.Event;
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams;
import org.matrix.android.sdk.api.session.room.model.RoomSummary;
import org.matrix.android.sdk.internal.crypto.MXEventDecryptionResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
                    if(room.getLatestPreviewableEvent().getRoot()!=null){
                        roomSummaryLite.originServerLastEventTs = room.getLatestPreviewableEvent().getRoot().getOriginServerTs();
                    }else{
                        roomSummaryLite.originServerLastEventTs = 0;
                    }
                    roomSummaryLite.localLastEventTs = room.getLatestPreviewableEvent().getRoot().getAgeLocalTs();
                    rooms.add(roomSummaryLite);
                }
                Collections.sort(rooms, new Comparator<RoomSummaryLite>() {
                    @Override
                    public int compare(RoomSummaryLite roomSummaryLite, RoomSummaryLite t1) {
                        return Long.compare(roomSummaryLite.originServerLastEventTs, t1.originServerLastEventTs);
                    }
                });
                eventSink.success(new Gson().toJson(rooms));
            }
        }
    };

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        if(SessionHolder.matrixSession==null){
            return;
        }
        eventSink = events;
        SessionHolder.matrixSession.getRoomSummariesLive(new RoomSummaryQueryParams.Builder().build()).observeForever(observer);
    }

    @Override
    public void onCancel(Object arguments) {
        if(SessionHolder.matrixSession==null){
            return;
        }
        SessionHolder.matrixSession.getRoomSummariesLive(new RoomSummaryQueryParams.Builder().build()).removeObserver(observer);
        eventSink = null;
    }
    
    private String resolveAvatarUrl(String url){
        if(url.isEmpty())
            return "";
        return SessionHolder.matrixSession.contentUrlResolver().resolveThumbnail(url,250,250, ContentUrlResolver.ThumbnailMethod.SCALE);
    }
}
