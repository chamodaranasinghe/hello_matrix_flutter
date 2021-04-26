import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';

const MethodChannel _channel = const MethodChannel('hello_matrix_flutter');

class Directory {
  static Future<bool> updateDirectory() async {
    bool result = await _channel.invokeMethod("updateDirectory");
    return result;
  }

  static Future<List<Profile>> retrieveDirectory() async {
    List<dynamic> list;
    List<Profile> directory = [];
    try {
      String result = await _channel.invokeMethod("retrieveDirectory");
      print(result);
      list = jsonDecode(result);
    } catch (e) {
      debugPrint(e);
      return null;
    }
    list.forEach((p) {
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
      directory.add(profile);
    });
    return directory;
  }
}
