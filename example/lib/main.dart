import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter_example/directory_list.dart';
import 'package:hello_matrix_flutter_example/voip_call/call_sample.dart';
import 'package:hello_matrix_flutter_example/voip_call/signaling.dart';
import 'room_details.dart';
import 'package:get_it/get_it.dart';
import 'package:flutter_callkeep/flutter_callkeep.dart';

GetIt getIt = GetIt.instance;

void main() async {
  //call keep
  WidgetsFlutterBinding.ensureInitialized();
  await CallKeep.setup();
  //call keep
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

  Future<void> displayIncomingCall() async {
    await CallKeep.askForPermissionsIfNeeded(context);
    final callUUID = '0783a8e5-8353-4802-9448-c6211109af51';
    final number = '+46 70 123 45 67';

    await CallKeep.displayIncomingCall(
        callUUID, number, number, HandleType.number, false);
  }

  @override
  void initState() {
    super.initState();
    checkSession();
  }

  Future<void> checkSession() async {
    bool status = await Auth.checkSession();
    Profile p = await Auth.getHelloProfile();
    print(status);
    setState(() {
      _sessionStatusBool = status;
      // ignore: unnecessary_statements
      if (status) {
        if (p != null) {
          //register call signalling singleton if logged in and already have a session and profile available
          if (!getIt.isRegistered<Signaling>()) {
            getIt.registerSingleton<Signaling>(
                Signaling('demo.cloudwebrtc.com', p.helloId));
            signallingInstance..connect();

            //registering call actions
            _registerCallActions();

          }
        }

        _sessionStatus = 'Logged in : True';
      } else {
        _sessionStatus = 'Logged in : False';
      }
    });
  }

  _registerCallActions(){
    CallKeep.didReceiveStartCallAction.listen((event) {
      //print('didReceiveStartCallAction $event');
      //print('didReceiveStartCallActionABC');
      signallingInstance.accept();
    });
    CallKeep.didActivateAudioSession.listen((event) {
      //print('didActivateAudioSessionABC $event');
    });
    CallKeep.didDeactivateAudioSession.listen((event) {
      //print('didDeactivateAudioSessionABC $event');
    });
    CallKeep.performEndCallAction.listen((event) {
      //print('performEndCallActionABC $event');
      //print('performEndCallAction $event');
      signallingInstance.reject();
    });
    CallKeep.performAnswerCallAction.listen((event) {
      //print('performAnswerCallActionABC $event');
      /*print('signallingInstance $signallingInstance');
      print('performAnswerCallAction');
      print('signallingInstance $signallingInstance');
      signallingInstance.accept();*/
      signallingInstance.accept();
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
                              'calluser1@mailinator.com', 'abc123');
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
                  ElevatedButton(
                      onPressed: () async {
                        Profile p = await Auth.getHelloProfile();
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                              builder: (context) => CallSample(
                                    /*host: 'demo.cloudwebrtc.com',
                                    helloId: p.helloId,*/
                                  )),
                        );
                      },
                      child: Icon(Icons.add_call)),
                  ElevatedButton(
                      onPressed: () async {
                        this.displayIncomingCall();
                      }, child: Icon(Icons.call))
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
                            print(room.otherUserThumbnailUrl);
                            var lastContent = room.lastContent;
                            var lastMsg = '';
                            if (lastContent != null) {
                              var lastContent = json.decode(room.lastContent);
                              lastMsg = lastContent['body'];
                            }
                            return ListTile(
                              leading: room.otherUserThumbnailUrl == null
                                  ? Container()
                                  : CircleAvatar(
                                      radius: 30.0,
                                      backgroundImage: NetworkImage(
                                          room.otherUserThumbnailUrl),
                                      backgroundColor: Colors.transparent,
                                    ),
                              onTap: () async {
                                if (room.membership == 'invite') {
                                  bool joinStatus =
                                      await HelloMatrixFlutter.joinRoom(
                                          room.roomId);
                                  //print('joinStatus $joinStatus');
                                  if (joinStatus) {
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(
                                          builder: (context) => RoomDetails(
                                                roomId: room.roomId.toString(),
                                                otherUserHelloId: room
                                                    .otherUserHelloId
                                                    .toString(),
                                              )),
                                    );
                                  }
                                } else {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (context) => RoomDetails(
                                              roomId: room.roomId.toString(),
                                              otherUserHelloId: room
                                                  .otherUserHelloId
                                                  .toString(),
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
                                    await HelloMatrixFlutter.createDirectRoom(
                                        user.mxUserId,
                                        user.firstName.toString());
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(
                                      builder: (context) => RoomDetails(
                                            roomId: result,
                                          )),
                                );
                                print(result);
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
