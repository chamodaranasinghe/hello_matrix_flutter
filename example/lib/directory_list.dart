import 'package:flutter/material.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter_example/room_details.dart';

class DirectoryList extends StatefulWidget {
  @override
  _DirectoryListState createState() => _DirectoryListState();
}

class _DirectoryListState extends State<DirectoryList> {
  bool _loading = true;
  List<Profile> _directory = [];

  @override
  void initState() {
    loadDirectory();
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
                itemCount: _directory.length,
                itemBuilder: (context, i) {
                  var profile = _directory[i];
                  String userId = '@${profile.helloId}:h1.hellodesk.app';
                  print(profile.displayName);
                  return ListTile(
                    leading: CircleAvatar(
                      radius: 30.0,
                      backgroundImage:
                      NetworkImage(profile.thumbnailUrl),
                      backgroundColor: Colors.transparent,
                    ),
                    title: Text(profile.displayName),
                    subtitle: Text(profile.email),
                    onTap: () async {
                      String result = await HelloMatrixFlutter.createDirectRoom(
                          userId, profile.helloId);
                      Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => RoomDetails(
                              roomId: result,
                            ),
                          ));
                    },
                  );
                }),
          );
  }

  /*void loadContacts() async {
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
  }*/

  void loadDirectory() async {
    this._directory = await Directory.retrieveDirectory();
    setState(() {
      _loading = false;
    });
  }
}
