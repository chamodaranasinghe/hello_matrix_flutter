package com.hello.hello_matrix_flutter.src.rooms;

import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent;

public class RoomSummaryLite implements Comparable<RoomSummaryLite> {
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
    String otherMemberDisplayName;
    String otherMemberThumbnail;
    String otherUserHelloId;
    String otherUserMatrixId;

    @Override
    public int compareTo(RoomSummaryLite old) {
        return Long.compare(this.originServerLastEventTs, old.originServerLastEventTs);
    }
}
