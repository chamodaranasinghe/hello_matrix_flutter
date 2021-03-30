import 'dart:convert';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';

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
                    await HelloMatrixFlutter.login('https://h1.hellodesk.app', 'chamodam@gmail.com', 'password');
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
                height: 400,
                child: StreamBuilder(
                    stream: HelloMatrixFlutter.liveRoomList,
                    builder: (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                      if(snapshot==null || !snapshot.hasData)
                        return Container();
                      List<dynamic> list = json.decode(snapshot.data);
                     return ListView.builder(
                         shrinkWrap: true,
                         itemCount: list.length,
                         itemBuilder: (context, i) {
                           var room = list[i];
                           List<dynamic> typingUsers = room['typingUsers'];
                           List<dynamic> aliases = room['aliases'];
                            return ListTile(
                              title: Text(room['displayName']),
                              subtitle: Text(room['breadcrumbsIndex'].toString()),
                            );
                         });
                    }),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
