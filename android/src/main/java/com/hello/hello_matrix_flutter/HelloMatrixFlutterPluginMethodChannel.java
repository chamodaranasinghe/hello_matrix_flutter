package com.hello.hello_matrix_flutter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.LoginController;
import com.hello.hello_matrix_flutter.src.call.CallController;
import com.hello.hello_matrix_flutter.src.directory.DirectoryController;
import com.hello.hello_matrix_flutter.src.rooms.RoomController;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class HelloMatrixFlutterPluginMethodChannel implements MethodChannel.MethodCallHandler {

    LoginController loginController;
    RoomController roomController;
    DirectoryController directoryController;
    CallController callController;

    public HelloMatrixFlutterPluginMethodChannel() {
        this.loginController = new LoginController();
        this.roomController = new RoomController();
        this.directoryController = new DirectoryController();
        this.callController = new CallController();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

        switch (call.method) {
            case "checkSession":
                loginController.checkSession(result);
                break;
            case "login":
                loginController.login(result, call.argument("homeServer").toString(), call.argument("username").toString(), call.argument("password").toString());
                break;
            case "getProfile":
                loginController.getProfile(result);
                break;
            case "logout":
                loginController.logout(result);
                break;
            case "createDirectRoom":
                roomController.createDirectRoom(result, call.argument("userId").toString(), call.argument("roomName").toString());
                break;
            case "sendSimpleTextMessage":
                roomController.sendSimpleTextMessage(result, call.argument("roomId").toString(), call.argument("body").toString());
                break;
            case "createTimeLine":
                roomController.createTimeLine(call.argument("roomId").toString());
                break;
            case "destroyTimeLine":
                roomController.destroyTimeLine();
                break;
            case "joinRoom":
                roomController.joinRoom(result, call.argument("roomId").toString());
                break;
            case "updateDirectory":
                directoryController.updateDirectory(result);
                break;
            case "retrieveDirectory":
                directoryController.retrieveDirectory(result);
                break;

            //calling
            case "getTurnServerCredentials":
                callController.getTurnServerCredentials(result);
                break;

            default:
                result.notImplemented();
                return;
        }
    }
}
