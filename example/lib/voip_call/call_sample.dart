import 'package:flutter/material.dart';
import 'package:hello_matrix_flutter/hello_matrix_flutter.dart';
import 'dart:core';
import 'package:flutter_webrtc/flutter_webrtc.dart';
import 'package:hello_matrix_flutter_example/voip_call/session.dart';
import 'package:hello_matrix_flutter_example/voip_call/signaling.dart';

class CallSample extends StatefulWidget {
  static String tag = 'call_sample';

 /* final String host;
  final String helloId;
*/
  /*CallSample({Key key, @required this.host, this.helloId}) : super(key: key);*/

  @override
  _CallSampleState createState() => _CallSampleState();
}

class _CallSampleState extends State<CallSample> {
  /*Signaling _signaling;*/
  //Signaling _signaling = signallingInstance;

  Widget _bodyWidget = Container();

  List<dynamic> _peers;
  var _selfId;
  RTCVideoRenderer _localRenderer = RTCVideoRenderer();
  RTCVideoRenderer _remoteRenderer = RTCVideoRenderer();

  /*bool _inCalling = false;*/
  CallState _callState;
  Session _session;

  // ignore: unused_element
  _CallSampleState({Key key});

  @override
  initState() {
    super.initState();
    initRenderers();
    _connect();
  }

  initRenderers() async {
    await _localRenderer.initialize();
    await _remoteRenderer.initialize();
  }

  @override
  deactivate() {
    super.deactivate();
    if (signallingInstance != null) signallingInstance.close();
    _localRenderer.dispose();
    _remoteRenderer.dispose();
  }

  void _connect() async {
    /*if (_signaling == null) {*/
    //_signaling = Signaling(widget.host, widget.helloId)..connect();
    //signallingInstance..connect();

    signallingInstance.onSignalingStateChange = (SignalingState state) {
      switch (state) {
        case SignalingState.ConnectionClosed:
        case SignalingState.ConnectionError:
        case SignalingState.ConnectionOpen:
          break;
      }
    };
    signallingInstance.onCallStateChange = (Session session, CallState state) {
      print('call state $state');
      switch (state) {
        case CallState.CallStateNew:
          print('CallState CallStateNew');
          setState(() {
            _callState = state;
            _session = session;
            //_inCalling = true;
            _bodyWidget = callUi();
          });
          break;
        case CallState.CallStateBye:
          print('CallState CallStateBye');
          setState(() {
            _callState = state;
            _localRenderer.srcObject = null;
            _remoteRenderer.srcObject = null;
            //_inCalling = false;
            _session = null;
            _bodyWidget = peersUi();
          });
          break;
        case CallState.CallStateInvite:
          print('CallState CallStateInvite');
          break;
        case CallState.CallStateConnected:
          print('CallState CallStateConnected');
          break;
        case CallState.CallStateRinging:
          print('CallState CallStateRinging');
          setState(() {
            _callState = state;
            _bodyWidget = ringingUi();
          });
          break;
        default:
          setState(() {
            _bodyWidget = peersUi();
          });
          break;
      }
    };

    signallingInstance.onPeersUpdate = ((event) {
      setState(() {
        _selfId = event['self'];
        _peers = event['peers'];
      });
    });

    signallingInstance.onLocalStream = ((_, stream) {
      _localRenderer.srcObject = stream;
    });

    signallingInstance.onAddRemoteStream = ((_, stream) {
      _remoteRenderer.srcObject = stream;
    });

    signallingInstance.onRemoveRemoteStream = ((_, stream) {
      _remoteRenderer.srcObject = null;
    });
    /*  }*/
  }

  _invitePeer(BuildContext context, String peerId, bool useScreen) async {
    if (signallingInstance != null && peerId != _selfId) {
      signallingInstance.invite(peerId, 'audio', useScreen);
    }
  }

  _hangUp() {
    if (signallingInstance != null) {
      signallingInstance.bye(_session.sid);
    }
  }

  _switchCamera() {
    signallingInstance.switchCamera();
  }

  _muteMic() {
    signallingInstance.muteMic();
  }

  _buildRow(context, peer) {
    var self = (peer['id'] == _selfId);
    return ListBody(children: <Widget>[
      ListTile(
        title: Text(self
            ? peer['name'] + '[Your self]'
            : peer['name'] + '[' + peer['user_agent'] + ']'),
        onTap: null,
        trailing: SizedBox(
            width: 100.0,
            child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  IconButton(
                    icon: const Icon(Icons.videocam),
                    onPressed: () {
                      _invitePeer(context, peer['id'], false);
                    },
                    tooltip: 'Video calling',
                  ),
                  IconButton(
                    icon: const Icon(Icons.screen_share),
                    onPressed: () => _invitePeer(context, peer['id'], true),
                    tooltip: 'Screen sharing',
                  )
                ])),
        subtitle: Text('id: ' + peer['id']),
      ),
      Divider()
    ]);
  }

  @override
  Widget build(BuildContext context) {
    return getUi();
    /*_inCalling
          ? OrientationBuilder(builder: (context, orientation) {
              return Container(
                child: Stack(children: <Widget>[
                  Positioned(
                      left: 0.0,
                      right: 0.0,
                      top: 0.0,
                      bottom: 0.0,
                      child: Container(
                        margin: EdgeInsets.fromLTRB(0.0, 0.0, 0.0, 0.0),
                        width: MediaQuery.of(context).size.width,
                        height: MediaQuery.of(context).size.height,
                        child: RTCVideoView(_remoteRenderer),
                        decoration: BoxDecoration(color: Colors.black54),
                      )),
                  Positioned(
                    left: 20.0,
                    top: 20.0,
                    child: Container(
                      width: orientation == Orientation.portrait ? 90.0 : 120.0,
                      height:
                          orientation == Orientation.portrait ? 120.0 : 90.0,
                      child: RTCVideoView(_localRenderer, mirror: true),
                      decoration: BoxDecoration(color: Colors.black54),
                    ),
                  ),
                ]),
              );
            })
          : ListView.builder(
              shrinkWrap: true,
              padding: const EdgeInsets.all(0.0),
              itemCount: (_peers != null ? _peers.length : 0),
              itemBuilder: (context, i) {
                return _buildRow(context, _peers[i]);
              })*/
  }

  Widget getUi() {
    if (_callState != null) {
      if (_callState == CallState.CallStateRinging) {
        return ringingUi();
      } else if (_callState == CallState.CallStateInvite) {
        return callUi();
      } else {
        return peersUi();
      }
    } else {
      return peersUi();
    }
  }

  Widget peersUi() => Scaffold(
        appBar: AppBar(),
        body: Container(
          child: ListView.builder(
              shrinkWrap: true,
              padding: const EdgeInsets.all(0.0),
              itemCount: (_peers != null ? _peers.length : 0),
              itemBuilder: (context, i) {
                return _buildRow(context, _peers[i]);
              }),
        ),
      );

  Widget callUi() => Scaffold(
        appBar: AppBar(),
        floatingActionButtonLocation: FloatingActionButtonLocation.centerFloat,
        floatingActionButton: SizedBox(
            width: 200.0,
            child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: <Widget>[
                  FloatingActionButton(
                    child: const Icon(Icons.switch_camera),
                    onPressed: _switchCamera,
                  ),
                  FloatingActionButton(
                    onPressed: _hangUp,
                    tooltip: 'Hangup',
                    child: Icon(Icons.call_end),
                    backgroundColor: Colors.pink,
                  ),
                  FloatingActionButton(
                    child: const Icon(Icons.mic_off),
                    onPressed: _muteMic,
                  )
                ])),
        body: OrientationBuilder(builder: (context, orientation) {
          return Container(
            child: Stack(children: <Widget>[
              Positioned(
                  left: 0.0,
                  right: 0.0,
                  top: 0.0,
                  bottom: 0.0,
                  child: Container(
                    margin: EdgeInsets.fromLTRB(0.0, 0.0, 0.0, 0.0),
                    width: MediaQuery.of(context).size.width,
                    height: MediaQuery.of(context).size.height,
                    child: RTCVideoView(_remoteRenderer),
                    decoration: BoxDecoration(color: Colors.black54),
                  )),
              Positioned(
                left: 20.0,
                top: 20.0,
                child: Container(
                  width: orientation == Orientation.portrait ? 90.0 : 120.0,
                  height: orientation == Orientation.portrait ? 120.0 : 90.0,
                  child: RTCVideoView(_localRenderer, mirror: true),
                  decoration: BoxDecoration(color: Colors.black54),
                ),
              ),
            ]),
          );
        }),
      );

  Widget ringingUi() => Scaffold(
        appBar: AppBar(),
        body: OrientationBuilder(builder: (context, orientation) {
          return Container(
            child: Center(
              child: Row(
                children: [
                  ElevatedButton(
                      onPressed: () {
                        signallingInstance.accept();
                      },
                      child: Text('Answer')),
                  ElevatedButton(onPressed: () {}, child: Text('Reject')),
                ],
              ),
            ),
          );
        }),
      );
}
