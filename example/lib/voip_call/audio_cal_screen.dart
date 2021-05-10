import 'package:flutter/material.dart';
import 'package:hello_matrix_flutter_example/voip_call/session.dart';
import 'package:hello_matrix_flutter_example/voip_call/signaling.dart';

class AudioCallScreen extends StatefulWidget {
  @override
  _AudioCallScreenState createState() => _AudioCallScreenState();
}

class _AudioCallScreenState extends State<AudioCallScreen> {
  Session _session;

  @override
  void initState() {
    _initCallStateHandling();
    super.initState();
  }

  _initCallStateHandling() {
    signallingInstance.onCallStateChange = (Session session, CallState state) {
      print('call state $state');
      switch (state) {
        case CallState.CallStateNew:
          print('CallState CallStateNew');
          _session = session;
          break;
        case CallState.CallStateBye:
          print('CallState CallStateBye');
          Navigator.pop(context);
          _session = null;
          break;
        case CallState.CallStateInvite:
          print('CallState CallStateInvite');
          break;
        case CallState.CallStateConnected:
          print('CallState CallStateConnected');
          break;
        case CallState.CallStateRinging:
          break;
        default:
          break;
      }
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(),
      body: SafeArea(
        child: Center(
          child: Row(
            children: [
              ElevatedButton(
                  onPressed: () {
                    if (signallingInstance != null) {
                      print('Disconnect clicked');
                      signallingInstance.bye(_session.sid);
                    }
                    Navigator.pop(context);
                  },
                  child: Text('Disconnect')),
              ElevatedButton(
                  onPressed: () {
                    signallingInstance.toggleMute();
                  },
                  child: Text('Mute Mic')),
            ],
          ),
        ),
      ),
    );
  }
}
