package com.hello.hello_matrix_flutter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.LoginController;
import com.hello.hello_matrix_flutter.src.directory.DirectoryController;
import com.hello.hello_matrix_flutter.src.directory.DirectoryInstance;
import com.hello.hello_matrix_flutter.src.rooms.RoomController;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class HelloMatrixFlutterPluginMethodChannel implements MethodChannel.MethodCallHandler {

    private static HelloMatrixFlutterPluginMethodChannel INSTANCE = null;

    public MethodChannel.Result result;

    LoginController loginController;
    RoomController roomController;
    DirectoryController directoryController;

    private HelloMatrixFlutterPluginMethodChannel() {
        this.loginController = new LoginController();
        this.roomController = new RoomController();
        this.directoryController = new DirectoryController();
    }

    public static HelloMatrixFlutterPluginMethodChannel getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new HelloMatrixFlutterPluginMethodChannel();
        }
        return (INSTANCE);
    }

    ///TODO arrange methods in a proper way
    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        this.result = result;
        switch (call.method) {
            /*auth methods*/
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

            /*room methods  */
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
            case "paginateBackward":
                roomController.getTimeLineController().paginateBackward();
                break;
            case "onStartTyping":
                roomController.getTimeLineController().onStartTyping();
                break;
            case "onStopTyping":
                roomController.getTimeLineController().onStopTyping();
                break;
            case "sendImageMessage":
                roomController.sendImageMessage(
                        call.argument("roomId").toString(),
                        Long.parseLong(call.argument("size").toString()),
                        Long.parseLong(call.argument("date").toString()),
                        Long.parseLong(call.argument("height").toString()),
                        Long.parseLong(call.argument("width").toString()),
                        call.argument("name").toString(),
                        call.argument("path").toString()
                );
                break;

            /*directory methods*/
            case "updateDirectory":
                directoryController.updateDirectory(result);
                break;
            case "retrieveDirectory":
                directoryController.retrieveDirectory(result);
                break;

            default:
                result.notImplemented();
                return;
        }
    }
}
