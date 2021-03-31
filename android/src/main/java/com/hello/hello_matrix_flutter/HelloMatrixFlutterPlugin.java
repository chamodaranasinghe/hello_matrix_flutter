package com.hello.hello_matrix_flutter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;
import com.hello.hello_matrix_flutter.src.rooms.RoomListStreamHandler;
import com.hello.hello_matrix_flutter.src.users.UserListStreamHandler;

import org.matrix.android.sdk.api.Matrix;
import org.matrix.android.sdk.api.MatrixConfiguration;
import org.matrix.android.sdk.api.session.Session;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

/** HelloMatrixFlutterPlugin */
public class HelloMatrixFlutterPlugin implements FlutterPlugin {
  private MethodChannel methodChannel;
  private EventChannel roomListEventChannel;
  private EventChannel userListEventChannel;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter");

    roomListEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(),"hello_matrix_flutter/roomListEvents");
    roomListEventChannel.setStreamHandler(new RoomListStreamHandler());

    userListEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(),"hello_matrix_flutter/userListEvents");
    userListEventChannel.setStreamHandler(new UserListStreamHandler());

    methodChannel.setMethodCallHandler(new HelloMatrixFlutterPluginMethodChannel());
    SessionHolder.appContext = flutterPluginBinding.getApplicationContext();
    Matrix.Companion.initialize(SessionHolder.appContext,new MatrixConfiguration());
    SessionHolder.matrixSession = SessionHolder.getMatrixInstance().authenticationService().getLastAuthenticatedSession();
    if(SessionHolder.matrixSession!=null && SessionHolder.matrixSession.isOpenable()){
      SessionHolder.matrixSession.open();
      SessionHolder.matrixSession.startSync(true);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    methodChannel.setMethodCallHandler(null);
    roomListEventChannel.setStreamHandler(null);
    userListEventChannel.setStreamHandler(null);
  }
}
