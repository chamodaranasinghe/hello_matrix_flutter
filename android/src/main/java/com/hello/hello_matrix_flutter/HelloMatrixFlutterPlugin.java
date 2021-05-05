package com.hello.hello_matrix_flutter;

import android.util.Log;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;
import com.hello.hello_matrix_flutter.src.directory.DirectoryController;
import com.hello.hello_matrix_flutter.src.rooms.RoomListStreamHandler;
import com.hello.hello_matrix_flutter.src.users.UserListStreamHandler;
import com.hello.hello_matrix_flutter.src.util.RoomDisplayNameFallbackProviderImpl;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.Matrix;
import org.matrix.android.sdk.api.MatrixConfiguration;
import org.matrix.android.sdk.api.crypto.MXCryptoConfig;

import java.util.Arrays;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

/**
 * HelloMatrixFlutterPlugin
 */
public class HelloMatrixFlutterPlugin implements FlutterPlugin {
    private MethodChannel methodChannel;
    private EventChannel roomListEventChannel;
    private EventChannel userListEventChannel;

    static String _tag = "HMFP";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        Log.i(_tag, "on attached");
        //set plugin binding for the entire plugin
        PluginBindingHolder.flutterPluginBinding = flutterPluginBinding;

        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter");

        roomListEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter/roomListEvents");
        roomListEventChannel.setStreamHandler(new RoomListStreamHandler());

        userListEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "hello_matrix_flutter/userListEvents");
        userListEventChannel.setStreamHandler(new UserListStreamHandler());

        methodChannel.setMethodCallHandler(HelloMatrixFlutterPluginMethodChannel.getInstance());

        SessionHolder.appContext = flutterPluginBinding.getApplicationContext();

        String list[] = {
                "https://scalar.vector.im/_matrix/integrations/v1",
                "https://scalar.vector.im/api",
                "https://scalar-staging.vector.im/_matrix/integrations/v1",
                "https://scalar-staging.vector.im/api",
                "https://scalar-staging.riot.im/scalar/api"};


        Matrix.Companion.initialize(SessionHolder.appContext, new MatrixConfiguration(
                "Default-application-flavor",
                new MXCryptoConfig(),
                "https://scalar.vector.im/",
                "https://scalar.vector.im/api",
                Arrays.asList(list),
                null,
                false,
                new RoomDisplayNameFallbackProviderImpl()
        ));

        //Matrix matrix = Matrix.Companion.getInstance( flutterPluginBinding.getApplicationContext());


        SessionHolder.matrixSession = SessionHolder.getMatrixInstance().authenticationService().getLastAuthenticatedSession();
        if (SessionHolder.matrixSession != null && SessionHolder.matrixSession.isOpenable()) {
            Log.i(_tag, "Openable");
            SessionHolder.matrixSession.open();
            SessionHolder.matrixSession.startSync(true);

            //retrieve directory service
            new DirectoryController().retrieveDirectory(null);
        }else{

        }


    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        PluginBindingHolder.flutterPluginBinding = null;
        methodChannel.setMethodCallHandler(null);
        roomListEventChannel.setStreamHandler(null);
        userListEventChannel.setStreamHandler(null);
    }
}
