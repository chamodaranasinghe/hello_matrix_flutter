import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class HelloMatrixFlutter {
  static const MethodChannel _channel =
      const MethodChannel('hello_matrix_flutter');

  static const EventChannel _channelRoomList =
      const EventChannel('hello_matrix_flutter/roomListEvents');

  static const EventChannel _channelUserList =
  const EventChannel('hello_matrix_flutter/userListEvents');

  static const EventChannel _channelTimelineEvents =
  const EventChannel('hello_matrix_flutter/timelineEvents');

  static Future<bool> checkSession() async {
    final bool result = await _channel.invokeMethod("checkSession");
    return result;
  }

  static Future<bool> login(
      String homeServer, String username, String password) async {
    final bool result = await _channel.invokeMethod("login", {
      'homeServer': homeServer,
      'username': username,
      'password': password,
    });
    return result;
  }

  static Future<bool> logout() async {
    final bool result = await _channel.invokeMethod("logout");
    return result;
  }

  static Future<String> createDirectRoom(
      String userId) async {
    final String result = await _channel.invokeMethod("createDirectRoom", {
      'userId': userId
    });
    return result;
  }

  static Future<bool> sendSimpleTextMessage(
      String roomId, String body) async {
    final bool result = await _channel.invokeMethod("sendSimpleTextMessage", {
      'roomId': roomId,
      'body': body,
    });
    return result;
  }

  static Future<void> createTimeLine(String roomId) async{
    await _channel.invokeMethod("createTimeLine", {
      'roomId': roomId
    });
    return;
  }

  static Future<void> destroyTimeLine() async{
    await _channel.invokeMethod("destroyTimeLine");
    return;
  }

  static Stream get liveRoomList => _channelRoomList.receiveBroadcastStream();

  static Stream get liveUserList => _channelUserList.receiveBroadcastStream();

  static Stream get liveTimeLine => _channelTimelineEvents.receiveBroadcastStream();
}
