import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'package:hello_matrix_flutter_example/directory_list.dart';
import 'room_details.dart';

void main() {
  runApp(MyApp());
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

  Future<void> checkSession() async{
    bool status = await HelloMatrixFlutter.checkSession();

    print(status);
    setState(() {
      _sessionStatusBool = status;
      // ignore: unnecessary_statements
      if(status){
        _sessionStatus = 'Logged in : True';
      }else{
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
                  onPressed: !_sessionStatusBool?  ()async{
                    await HelloMatrixFlutter.login('https://h1.hellodesk.app', 'user1@gmail.com', 'abc123');
                    await checkSession();
                  }:null),
              ElevatedButton(
                  child: Text('Logout'),
                  onPressed: _sessionStatusBool? ()async{
                    await HelloMatrixFlutter.logout();
                    await checkSession();
                  }:null),
              Text('Rooms'),
              Container(
                height: 150,
                child: StreamBuilder(
                    stream: HelloMatrixFlutter.liveRoomList,
                    builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                      if(snapshot==null || !snapshot.hasData)
                        return Container();
                      List<dynamic> list = json.decode(snapshot.data);
                      //print(list);
                     return ListView.builder(
                         shrinkWrap: true,
                         itemCount: list.length,
                         itemBuilder: (context, i) {
                           var room = list[i];
                            return ListTile(
                              onTap: ()async{
                                if(room['membership']=='invite'){
                                  bool joinStatus =  await HelloMatrixFlutter.joinRoom(room['roomId']);
                                  //print('joinStatus $joinStatus');
                                  if(joinStatus){
                                    Navigator.push(
                                      context,
                                      MaterialPageRoute(builder: (context) => RoomDetails(roomId: room['roomId'].toString(),)),
                                    );
                                  }
                                }else {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(builder: (context) =>
                                        RoomDetails(
                                          roomId: room['roomId'].toString(),)),
                                  );
                                }
                              },
                             title: Text(room['roomName'].toString()),
                              subtitle: Text(room['roomTopic'].toString()),
                            );
                         });
                    }),
              ),
              Text('Users'),
              Container(
                height: 150,
                child: StreamBuilder(
                    stream: HelloMatrixFlutter.liveUserList,
                    builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                      if(snapshot==null || !snapshot.hasData)
                        return Container();
                      List<dynamic> list = json.decode(snapshot.data);
                      return ListView.builder(
                          shrinkWrap: true,
                          itemCount: list.length,
                          itemBuilder: (context, i) {
                            var user = list[i];
                            return ListTile(
                              title: user['displayName']!=null?Text(user['displayName']):Text('N/A'),
                              subtitle: Text(user['userId'].toString()),
                              onTap: ()async{
                                String result = await HelloMatrixFlutter.createDirectRoom(user['userId'].toString(),user['displayName'].toString());
                                Navigator.push(
                                  context,
                                  MaterialPageRoute(builder: (context) => RoomDetails(roomId: result,)),
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
