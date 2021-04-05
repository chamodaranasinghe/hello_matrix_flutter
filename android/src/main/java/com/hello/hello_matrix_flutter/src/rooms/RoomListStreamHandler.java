package com.hello.hello_matrix_flutter.src.rooms;

import androidx.lifecycle.Observer;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.json.JSONArray;
import org.json.JSONObject;
import org.matrix.android.sdk.api.session.content.ContentUrlResolver;
import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.RoomSummaryQueryParams;
import org.matrix.android.sdk.api.session.room.model.RoomSummary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
                    if(room.getLatestPreviewableEvent()!=null){
                        roomSummaryLite.originServerLastEventTs = room.getLatestPreviewableEvent().getRoot().getOriginServerTs();
                        roomSummaryLite.localLastEventTs = room.getLatestPreviewableEvent().getRoot().getAgeLocalTs();
                    }else{
                        roomSummaryLite.originServerLastEventTs = 0;
                        roomSummaryLite.localLastEventTs = 0;
                    }
                    roomSummaryLite.membership = room.getMembership().getValue();
                    roomSummaryLite.lastEvent = room.getLatestPreviewableEvent();

                    rooms.add(roomSummaryLite);
                }
                Collections.sort(rooms, new Comparator<RoomSummaryLite>() {
                    @Override
                    public int compare(RoomSummaryLite roomSummaryLite, RoomSummaryLite t1) {
                        return Long.compare(roomSummaryLite.originServerLastEventTs, t1.originServerLastEventTs);
                    }
                });

                JSONArray jsonArrayRooms = new JSONArray();
                for (RoomSummaryLite roomLite : rooms) {
                    JSONObject j = new JSONObject();
                    try {
                        j.put("roomId", roomLite.roomId);
                        j.put("roomName", roomLite.roomName);
                        j.put("roomTopic", roomLite.roomTopic);
                        j.put("isDirect", roomLite.isDirect);
                        j.put("notificationCount", roomLite.notificationCount);
                        j.put("avatarUrl", roomLite.avatarUrl);
                        j.put("originServerLastEventTs", roomLite.originServerLastEventTs);
                        j.put("localLastEventTs", roomLite.localLastEventTs);
                        j.put("membership", roomLite.membership);
                        j.put("isEncrypted", SessionHolder.matrixSession.getRoom(roomLite.roomId).isEncrypted());
                        j.put("encryptionAlgorithm", SessionHolder.matrixSession.getRoom(roomLite.roomId).encryptionAlgorithm());
                        j.put("shouldEncryptForInvitedMembers", SessionHolder.matrixSession.getRoom(roomLite.roomId).shouldEncryptForInvitedMembers());
                        if(roomLite.lastEvent!=null){
                            if(roomLite.lastEvent.getRoot().getClearType().equals("m.room.message")){
                                j.put("lastContent",new JSONObject(roomLite.lastEvent.getRoot().getClearContent()).toString());
                            }

                        }
                        
                        jsonArrayRooms.put(j);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                eventSink.success(jsonArrayRooms.toString());
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
