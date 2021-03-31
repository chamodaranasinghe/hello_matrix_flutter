package com.hello.hello_matrix_flutter;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.LoginController;
import com.hello.hello_matrix_flutter.src.rooms.RoomCreate;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class HelloMatrixFlutterPluginMethodChannel implements MethodChannel.MethodCallHandler {

    LoginController loginController;
    RoomCreate roomCreate;

    public HelloMatrixFlutterPluginMethodChannel() {
        this.loginController = new LoginController();
        this.roomCreate = new RoomCreate();
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {

        switch (call.method){
            case"checkSession":
                loginController.checkSession(result);
                break;
            case"login":
                loginController.login(result,call.argument("homeServer").toString(),call.argument("username").toString(),call.argument("password").toString());
                break;
            case"logout":
                loginController.logout(result);
                break;
            case"createDirectRoom":
                roomCreate.createDirectRoom(result,call.argument("userId").toString());
                break;
            default:
                result.notImplemented();
                return;
        }
    }
}
