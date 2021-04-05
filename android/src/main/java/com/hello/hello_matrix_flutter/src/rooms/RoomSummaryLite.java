package com.hello.hello_matrix_flutter.src.rooms;

import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent;

public class RoomSummaryLite {
    String roomId;
    String roomName;
    String roomTopic;
    String avatarUrl;
    String membership;
    boolean isDirect;
    int notificationCount;
    long originServerLastEventTs;
    long localLastEventTs;
    TimelineEvent lastEvent;
}
