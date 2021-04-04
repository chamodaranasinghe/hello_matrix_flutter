import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter_example/room_details.dart';
import 'package:http/http.dart';

class DirectoryList extends StatefulWidget {
  @override
  _DirectoryListState createState() => _DirectoryListState();
}

class _DirectoryListState extends State<DirectoryList> {
  bool _loading = true;
  List<dynamic> _contactList = [];

  @override
  void initState() {
    loadContacts();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return _loading
        ? Center(
            child: CircularProgressIndicator(),
          )
        : Container(
            padding: EdgeInsets.all(8),
            child: ListView.builder(
                shrinkWrap: true,
                itemCount: _contactList.length,
                itemBuilder: (context, i) {
                  var contact = _contactList[i];
                  String userId = '@${contact['unique_code']}:h1.hellodesk.app';
                  return ListTile(
                    title: Text(contact['unique_code']),
                    subtitle: Text(contact['email'].toString()),
                    onTap: () async {
                      String result = await HelloMatrixFlutter.createDirectRoom(userId,contact['unique_code']);
                      Navigator.push(
                        context,
                        MaterialPageRoute(builder: (context) => RoomDetails(roomId: result,),
                      ));
                    },
                  );
                }),
          );
  }

  void loadContacts() async {
    Response response = await get(Uri.parse(
        'https://admin.hellodesk.app/_ma1sd/backend/api/v1/directory_test'));
    if (response.statusCode == 200) {
      setState(() {
        _loading = false;
        List<dynamic> contacts = json.decode(response.body);
        contacts.forEach((c) {
          _contactList.add(c);
        });
      });
    }
  }
}
