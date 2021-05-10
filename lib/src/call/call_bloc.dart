import 'dart:convert';

import 'package:flutter/services.dart';

class MxCallSignalling {
  static const MethodChannel _channel =
      const MethodChannel('hello_matrix_flutter');

  static Future<Map<String, dynamic>> getTurnServerCredentials() async {
    final String result =
        await _channel.invokeMethod("getTurnServerCredentials");
    //print('getTurnServerCredentials $result');
    return jsonDecode(result);
  }
}
