import 'dart:async';
import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';

class LiveDirectRooms {
  static const EventChannel _channelRoomList =
      const EventChannel('hello_matrix_flutter/roomListEvents');

  static Stream<List<DirectRoom>> get getStream =>
      _channelRoomList.receiveBroadcastStream().asyncMap((event) {
        if (event != null) {
          List<DirectRoom> directRooms = [];
          List<dynamic> rowDataArray = json.decode(event);
          rowDataArray.forEach((e) {
            DirectRoom directRoom = DirectRoom();
            directRoom.roomId = e['roomId'];
            directRoom.otherUserDisplayName = e['otherMemberDisplayName'];
            directRoom.otherUserThumbnailUrl = e['otherMemberThumbnail'];
            directRoom.localLastEventTs = e['localLastEventTs'];
            directRoom.originServerLastEventTs = e['originServerLastEventTs'];
            directRoom.notificationCount = e['notificationCount'];
            directRoom.lastContent = e['lastContent'];
            directRoom.membership = e['membership'];
            directRooms.add(directRoom);
          });
          return directRooms;
        } else {
          return [];
        }
      });
}
