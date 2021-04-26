import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter/src/models/profile.dart';

const MethodChannel _channel = const MethodChannel('hello_matrix_flutter');

class Auth {
  /*static Future<bool> login(
      String homeServer, String username, String password) async {
    //try login first
    try {
      bool loginResult = await _login(homeServer, username, password);
      if (loginResult) {
        //set display name
        Profile profile = await getHelloProfile();
        bool setDisplayNameResult = await _setDisplayName(profile.displayName);
        if (setDisplayNameResult) {
          //directory sync
          try {
            bool directorySyncResult = await Directory.updateDirectory();
            if (directorySyncResult) {
              //only when directory sync, return true
              return true;
            } else {
              //rollback
              await logout();
              return false;
            }
          } catch (e) {
            //rollback
            await logout();
            return false;
          }
        } else {
          //rollback the login
          await logout();
          return false;
        }
        //continue to directory
      } else {
        return false;
      }
    } catch (e) {
      return false;
    }
  }*/

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

  /*static Future<bool> _setDisplayName(String displayName) async {
    final bool result = await _channel.invokeMethod("login", {
      'displayName': displayName,
    });
    return result;
  }*/

  static Future<bool> checkSession() async {
    final bool result = await _channel.invokeMethod("checkSession");
    return result;
  }

  static Future<Profile> getHelloProfile() async {
    Profile profile = Profile();
    try {
      String data = await _channel.invokeMethod("getProfile");
      if (data == null) {
        return null;
      }
      //data retrieved, convert to json
      Map<String, dynamic> user = jsonDecode(data);
      print(user);
      profile.helloId = user['hello_id'];
      profile.firstName = user['first_name'];
      profile.lastName = user['last_name'];
      profile.displayName = '${user['first_name']} ${user['last_name']}';
      profile.email = user['email'];
      profile.contact = user['contact'];
      profile.jobTitle = user['job_title'];
      profile.photoUrl = user['photo'];
      profile.thumbnailUrl = user['thumbnail'];
      profile.orgPrefix = user['org_prefix'];
      profile.orgName = user['org_name'];
      profile.orgContact = user['org_contact'];
      profile.orgWebsite = user['org_website'];
    } catch (e) {
      debugPrint(e);
      return null;
    }
    return profile;
  }
}
