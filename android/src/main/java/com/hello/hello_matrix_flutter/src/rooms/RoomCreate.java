package com.hello.hello_matrix_flutter.src.rooms;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.session.room.model.RoomDirectoryVisibility;
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;
import kotlin.Unit;

public class RoomCreate {

    String _tag = "RoomCreate";

    public void createDirectRoom(@NonNull final MethodChannel.Result result, final String userId) {

        Log.i(_tag, userId);

        String name = SessionHolder.matrixSession.getUser(userId).getBestName();
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.setDirect(true);
        createRoomParams.setName(name);
        createRoomParams.setVisibility(RoomDirectoryVisibility.PRIVATE);

        SessionHolder.matrixSession.createRoom(createRoomParams, new MatrixCallback<String>() {
            @Override
            public void onSuccess(String s) {
                Log.i(_tag, "on success create room");
                Log.i(_tag, s);
                inviteUser(result, s, userId);
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                Log.i(_tag, "on failed create room");
                result.error("", new Gson().toJson(throwable), null);
            }
        });
    }

    private void inviteUser(@NonNull final MethodChannel.Result result, String roomId, String userId) {
        SessionHolder.matrixSession.getRoom(roomId).invite(userId, null, new MatrixCallback<Unit>() {
            @Override
            public void onSuccess(Unit unit) {
                Log.i(_tag, "on success invite");
                result.success(true);
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                Log.i(_tag, "on failed invite");
                result.error("", new Gson().toJson(throwable), null);
            }
        });
    }
}
