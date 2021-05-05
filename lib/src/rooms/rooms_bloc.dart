
import 'package:flutter/services.dart';

const MethodChannel _channel = const MethodChannel('hello_matrix_flutter');

class RoomController {
    static Future<bool> joinRoom(String roomId) async {
    final bool result =
        await _channel.invokeMethod("joinRoom", {'roomId': roomId});
    return result;
  }

  static Future<String> createDirectRoom(String userId, String roomName) async {
    final String result = await _channel.invokeMethod(
        "createDirectRoom", {'userId': userId, 'roomName': roomName});
    return result;
  }
}
