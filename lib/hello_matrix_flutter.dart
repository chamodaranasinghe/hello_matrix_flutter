import 'dart:async';
import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/src/auth/auth.dart';
export 'package:hello_matrix_flutter/src/rooms/live_direct_rooms.dart';
export 'package:hello_matrix_flutter/src/directory/live_directory.dart';
export 'package:hello_matrix_flutter/src/directory/directory_bloc.dart';
export 'package:hello_matrix_flutter/src/auth/auth.dart';
export 'package:hello_matrix_flutter/src/models/profile.dart';
export 'package:hello_matrix_flutter/src/models/direct_room.dart';

class HelloMatrixFlutter {
  static const MethodChannel _channel =
      const MethodChannel('hello_matrix_flutter');

  static const EventChannel _channelUserList =
      const EventChannel('hello_matrix_flutter/userListEvents');

  static const EventChannel _channelTimelineEvents =
      const EventChannel('hello_matrix_flutter/timelineEvents');

  static Future<String> createDirectRoom(String userId, String roomName) async {
    final String result = await _channel.invokeMethod(
        "createDirectRoom", {'userId': userId, 'roomName': roomName});
    return result;
  }

  static Future<bool> sendSimpleTextMessage(String roomId, String body) async {
    final bool result = await _channel.invokeMethod("sendSimpleTextMessage", {
      'roomId': roomId,
      'body': body,
    });
    return result;
  }

  static Future<bool> joinRoom(String roomId) async {
    final bool result =
        await _channel.invokeMethod("joinRoom", {'roomId': roomId});
    return result;
  }

  static Future<void> createTimeLine(String roomId) async {
    await _channel.invokeMethod("createTimeLine", {'roomId': roomId});
    return;
  }

  static Future<void> destroyTimeLine() async {
    await _channel.invokeMethod("destroyTimeLine");
    return;
  }

  static Stream get liveUserList => _channelUserList.receiveBroadcastStream();

  static Stream get liveTimeLine =>
      _channelTimelineEvents.receiveBroadcastStream();
}
