import 'package:flutter_webrtc/flutter_webrtc.dart';

class Session {
  Session({this.sid, this.pid});

  String pid;
  String sid;
  RTCPeerConnection pc;
  RTCDataChannel dc;
  List<RTCIceCandidate> remoteCandidates = [];
}