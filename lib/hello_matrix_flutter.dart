import 'dart:async';
import 'dart:convert';
import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/src/auth/auth.dart';
import 'package:hello_matrix_flutter/src/models/profile.dart';
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

  static Stream<List<Profile>> get liveUserList =>
      _channelUserList.receiveBroadcastStream().asyncMap((event) {
        if (event != null) {
          List<Profile> users = [];
          List<dynamic> rowDataArray = json.decode(event);
          rowDataArray.forEach((p) {
            Profile profile = Profile();
            profile.helloId = p['hello_id'];
            profile.firstName = p['first_name'];
            profile.lastName = p['last_name'];
            profile.displayName = '${p['first_name']} ${p['last_name']}';
            profile.email = p['email'];
            profile.contact = p['contact'];
            profile.jobTitle = p['job_title'];
            profile.photoUrl = p['photo'];
            profile.thumbnailUrl = p['thumbnail'];
            profile.orgPrefix = p['org_prefix'];
            profile.orgName = p['org_name'];
            profile.orgContact = p['org_contact'];
            profile.orgWebsite = p['org_website'];
            profile.mxUserId = p['mxUserId'];
            users.add(profile);
          });
          return users;
        } else {
          return [];
        }
      });

  static Stream get liveTimeLine =>
      _channelTimelineEvents.receiveBroadcastStream();
}
