import 'package:flutter/material.dart';
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
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Chat"),
      ),
      body: Container(
        margin: EdgeInsets.all(8),
        child: Column(
          children: [
            Expanded(child: Container(),),
            Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Expanded(child: TextField(
                  controller: textCtl,
                )),
                SizedBox(width: 5,),
                ElevatedButton(onPressed: ()async{
                  await HelloMatrixFlutter.sendSimpleTextMessage(widget.roomId, textCtl.text);
                  textCtl.clear();
                }, child: Text('Send'))
              ],)
          ],
        ),
      ),
    );
  }

  @override
  void dispose() {
    // Clean up the controller when the widget is disposed.
    textCtl.dispose();
    super.dispose();
  }
}
