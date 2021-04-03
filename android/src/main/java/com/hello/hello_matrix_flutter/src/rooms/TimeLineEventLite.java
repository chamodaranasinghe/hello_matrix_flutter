package com.hello.hello_matrix_flutter.src.rooms;

import java.util.Map;

public class TimeLineEventLite {
    String type;
    String eventId;
    long localId;
    long originServerTs;
    long localTs;
    Map<String,Object> clearedContent;
    String direction;
}
