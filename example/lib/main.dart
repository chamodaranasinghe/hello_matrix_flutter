import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter_example/directory_list.dart';
import 'package:cached_network_image/cached_network_image.dart';
import 'room_details.dart';

void main() {
  runApp(MyApp());

  runApp(
    MaterialApp(
      debugShowCheckedModeBanner: false,
      supportedLocales: [
        const Locale('en', 'US'),
      ],
      home: MyApp(),
    ),
  );
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String _sessionStatus = 'Unknown';
  bool _sessionStatusBool = false;

  @override
  void initState() {
    super.initState();
    checkSession();
  }

  Future<void> checkSession() async {
    bool status = await Auth.checkSession();

    //print(status);
    setState(() {
      _sessionStatusBool = status;
      // ignore: unnecessary_statements
      if (status) {
        _sessionStatus = 'Logged in : True';
      } else {
        _sessionStatus = 'Logged in : False';
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text(_sessionStatus),
              ElevatedButton(
                  child: Text('Login'),
                  onPressed: !_sessionStatusBool
                      ? () async {
                          await Auth.login('https://h1.hellodesk.app',
                              'chamodar@gmail.com', 'abc123');
                          await checkSession();
                        }
                      : null),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  ElevatedButton(
                      child: Icon(Icons.logout),
                      onPressed: _sessionStatusBool
                          ? () async {
                              await Auth.logout();
                              await checkSession();
                            }
                          : null),
                  ElevatedButton(
                      child: Icon(Icons.person),
                      onPressed: _sessionStatusBool
                          ? () async {
                              Profile profile = await Auth.getHelloProfile();
                              if (profile != null) {
                                showDialog(
                                    context: context,
                                    barrierDismissible: true,
                                    builder: (BuildContext context) {
                                      return AlertDialog(
                                        title: Text('Profile'),
                                        content: SingleChildScrollView(
                                          child: ListBody(
                                            children: <Widget>[
                                              Text(
                                                  'Hello id : ${profile.helloId}'),
                                              Text(
                                                  'Display name : ${profile.displayName}'),
                                              Text('Email : ${profile.email}'),
                                              Text(
                                                  'Contact : ${profile.contact}'),
                                              Text('Org. : ${profile.orgName}'),
                                              Text(
                                                  'Photo. : ${profile.photoUrl}'),
                                            ],
                                          ),
                                        ),
                                      );
                                    });
                              }
                            }
                          : null),
                  ElevatedButton(
                      child: Icon(Icons.sync),
                      onPressed: _sessionStatusBool
                          ? () async {
                              bool b = await Directory.updateDirectory();
                              print(b);
                            }
                          : null),
                ],
              ),
              Text('Rooms'),
              Container(
                height: 150,
                child: StreamBuilder(
                    stream: LiveDirectRooms.getStream,
                    builder: (BuildContext context,
                        AsyncSnapshot<List<DirectRoom>> snapshot) {
                      if (snapshot == null || !snapshot.hasData)
                        return Container();
                      List<DirectRoom> list = snapshot.data;
                      return ListView.builder(
                          shrinkWrap: true,
                          itemCount: list.length,
                          itemBuilder: (context, i) {
                            DirectRoom room = list[i];
                            var lastContent = room.lastContent;
                            var lastMsg = '';
                            if (lastContent != null) {
                              var lastContent = json.decode(room.lastContent);
                              lastMsg = lastContent['body'];
                            }
                            return ListTile(
                              leading: CachedNetworkImage(
                                imageUrl: room.otherUserThumbnailUrl,
                                imageBuilder: (context, imageProvider) =>
                                    Container(
                                  height: 50,
                                  width: 50,
                                  decoration: BoxDecoration(
                                    borderRadius:
                                        BorderRadius.all(Radius.circular(200)),
                                    image: DecorationImage(
                                      image: imageProvider,
                                      fit: BoxFit.cover,
                                    ),
                                  ),
                                ),
                                placeholder: (context, url) =>
                                    CircularProgressIndicator(),
                                errorWidget: (context, url, error) =>
                                    Icon(Icons.error),
                              ),
                              onTap: () async {
                                print(room.roomId);
                                if (room.membership == 'invite') {
                                  bool joinStatus =
                                      await RoomController.joinRoom(
                                          room.roomId);
                                  //print('joinStatus $joinStatus');
                                  if (joinStatus) {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => RoomDetails(
                                                roomId: room.roomId.toString(),
                                              )),
                                    );
                                  }
                                } else {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => RoomDetails(
                                              roomId: room.roomId.toString(),
                                            )),
                                  );
                                }
                              },
                              title: Text(room.otherUserDisplayName.toString()),
                              subtitle: Text(lastMsg),
                            );
                          });
                    }),
              ),
              Text('Users'),
              Container(
                height: 150,
                child: StreamBuilder<List<Profile>>(
                    stream: HelloMatrixFlutter.liveUserList,
                    builder: (BuildContext context,
                        AsyncSnapshot<dynamic> snapshot) {
                      if (snapshot == null || !snapshot.hasData)
                        return Container();
                      List<Profile> list = snapshot.data;
                      return ListView.builder(
                          shrinkWrap: true,
                          itemCount: list.length,
                          itemBuilder: (context, i) {
                            Profile user = list[i];
                            return ListTile(
                              title: Text('${user.firstName} ${user.lastName}'),
                              subtitle: Text(user.email),
                              onTap: () async {
                                String result =
                                    await RoomController.createDirectRoom(
                                        user.mxUserId,
                                        user.firstName.toString());
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) => RoomDetails(
                                            roomId: result,
                                          )),
                                );
                                //print(result);
                              },
                            );
                          });
                    }),
              ),
              Text('Directory'),
              Container(
                height: 150,
                child: DirectoryList(),
              )
            ],
          ),
        ),
      ),
    );
  }
}
