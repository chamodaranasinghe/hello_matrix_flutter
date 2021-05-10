package com.hello.hello_matrix_flutter.src.call;

import androidx.annotation.NonNull;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.session.call.TurnServerResponse;

import io.flutter.plugin.common.MethodChannel;

public class CallController {
    String _tag = "CallController";

    public void getTurnServerCredentials(@NonNull final MethodChannel.Result result) {
        final JSONObject tsCredentials = new JSONObject();
        final JSONArray tsUris = new JSONArray();

        SessionHolder.matrixSession.callSignalingService().getTurnServer(new MatrixCallback<TurnServerResponse>() {
            @Override
            public void onSuccess(TurnServerResponse response) {
                try {

                    tsUris.put(response.getUris().get(0));
                    tsUris.put(response.getUris().get(1));

                    tsCredentials.put("uris", tsUris);
                    tsCredentials.put("username", response.getUsername());
                    tsCredentials.put("password", response.getPassword());
                    result.success(tsCredentials.toString());
                } catch (Exception e) {
                    result.error("", "", "");
                }
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                result.error("", "", "");
            }
        });

    }

    /*public void createCall(@NonNull final MethodChannel.Result result, String roomId, String otherUserId, boolean isVideo) {
        MxCall mxCall = SessionHolder.matrixSession.callSignalingService().createOutgoingCall(roomId, otherUserId, isVideo);
        result.success(mxCall.getCallId());
        Log.i(_tag, mxCall.getCallId());
        mxCall.addListener(new MxCall.StateListener() {
            @Override
            public void onStateUpdate(@NotNull MxCall mxCall) {
                Log.i(_tag, "----Outgoing call after created-----");
                Log.i(_tag, mxCall.getState().toString());
            }
        });
    }*/

}
