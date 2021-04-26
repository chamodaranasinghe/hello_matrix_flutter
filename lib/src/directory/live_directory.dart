import 'dart:convert';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';

class LiveDirectory {
  static const EventChannel _channelDirectoryList =
      const EventChannel('hello_matrix_flutter/directoryListEvents');

  /*static Stream<List<Profile>> get getStream =>
      _channelDirectoryList.receiveBroadcastStream().asyncMap((event) {
        if (event != null) {
          List<Profile> directory = [];
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
            directory.add(profile);
          });
          return directory;
        } else {
          return [];
        }
      });*/

static Stream get getStream => _channelDirectoryList.receiveBroadcastStream();
}
