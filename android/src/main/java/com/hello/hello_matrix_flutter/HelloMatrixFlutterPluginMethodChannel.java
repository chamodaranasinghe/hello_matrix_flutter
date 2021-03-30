package com.hello.hello_matrix_flutter;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.LoginController;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class HelloMatrixFlutterPluginMethodChannel implements MethodChannel.MethodCallHandler {

    LoginController loginController;

    public HelloMatrixFlutterPluginMethodChannel() {
        this.loginController = new LoginController();
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
            default:
                result.notImplemented();
                return;
        }
    }
}
