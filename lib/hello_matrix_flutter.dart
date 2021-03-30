import 'dart:async';

import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';

class HelloMatrixFlutter {
  static const MethodChannel _channel =
      const MethodChannel('hello_matrix_flutter');

  static const EventChannel _channelRoomList =
      const EventChannel('hello_matrix_flutter/roomListEvents');

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

  /*static void listenRooms() async{
    _channelRoomList.receiveBroadcastStream().listen((event) {
      print('received event $event');
    });
  }*/

  static Stream get liveRoomList => _channelRoomList.receiveBroadcastStream();
}
