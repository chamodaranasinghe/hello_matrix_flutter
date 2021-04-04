import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/rendering.dart';
import 'package:flutter/services.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';

class RoomDetails extends StatefulWidget {
  final String roomId;

  const RoomDetails({Key key, this.roomId}) : super(key: key);

  @override
  _RoomDetailsState createState() => _RoomDetailsState();
}

class _RoomDetailsState extends State<RoomDetails> {
  final textCtl = TextEditingController();

  @override
  void initState() {
    init();
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Chat"),
      ),
      body: Container(
        margin: EdgeInsets.all(8),
        child: Column(
          children: [
            Expanded(
              child: StreamBuilder(
                stream: HelloMatrixFlutter.liveTimeLine,
                builder:
                    (BuildContext context, AsyncSnapshot<dynamic> snapshot) {
                  if (snapshot == null || !snapshot.hasData) return Container();
                  List<dynamic> messages = json.decode(snapshot.data);
                  return ListView.builder(
                    itemCount: messages.length,
                    shrinkWrap: true,
                    padding: EdgeInsets.only(top: 10, bottom: 10),
                    reverse: true,
                    itemBuilder: (context, index) {
                      var message = messages[index];
                      String root = messages[index]['clearedContent'];
                      return root != null
                          ? Container(
                              padding: EdgeInsets.only(
                                  left: 14, right: 14, top: 10, bottom: 10),
                              child: Align(
                                alignment: (message['direction'] == "received"
                                    ? Alignment.topLeft
                                    : Alignment.topRight),
                                child: Container(
                                  decoration: BoxDecoration(
                                    borderRadius: BorderRadius.circular(20),
                                    color: (message['direction'] == "received"
                                        ? Colors.grey.shade200
                                        : Colors.blue[200]),
                                  ),
                                  padding: EdgeInsets.all(16),
                                  child: Text(
                                    json.decode(root)['body'].toString(),
                                    style: TextStyle(fontSize: 15),
                                  ),
                                ),
                              ),
                            )
                          : Container();
                    },
                  );
                },
              ),
            ),
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Expanded(
                    child: TextField(
                  controller: textCtl,
                )),
                SizedBox(
                  width: 5,
                ),
                ElevatedButton(
                    onPressed: () async {
                      await HelloMatrixFlutter.sendSimpleTextMessage(
                          widget.roomId, textCtl.text);
                      textCtl.clear();
                    },
                    child: Text('Send'))
              ],
            )
          ],
        ),
      ),
    );
  }

  init() async {
    await HelloMatrixFlutter.createTimeLine(widget.roomId);
    HelloMatrixFlutter.liveTimeLine.listen((event) {
      print(event);
    });
  }

  @override
  void dispose() {
    // Clean up the controller when the widget is disposed.
    textCtl.dispose();
    HelloMatrixFlutter.destroyTimeLine();
    super.dispose();
  }
}
