package com.hello.hello_matrix_flutter.src.rooms;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams;
import org.matrix.android.sdk.api.session.room.model.message.MessageType;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;

public class RoomController {


    String _tag = "RoomController";

    public void createDirectRoom(@NonNull final MethodChannel.Result result, final String userId) {

        Room existingRoom = SessionHolder.matrixSession.getExistingDirectRoomWithUser(userId);
        if(existingRoom!=null){
            Log.i(_tag, "room already exist"+" "+existingRoom.getRoomId());
            result.success(existingRoom.getRoomId());
            return;
        }

        Log.i(_tag, userId);

        String name = SessionHolder.matrixSession.getUser(userId).getBestName();
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.setName(name);
        createRoomParams.setDirectMessage();
        createRoomParams.getInvitedUserIds().add(userId);

        SessionHolder.matrixSession.createRoom(createRoomParams, new MatrixCallback<String>() {
            @Override
            public void onSuccess(String roomId) {
                Log.i(_tag, "on success create room");
                Log.i(_tag, roomId);
                result.success(roomId);
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                Log.i(_tag, "on failed create room");
                result.error("", new Gson().toJson(throwable), null);
            }
        });
    }

    public void sendSimpleTextMessage(@NonNull final MethodChannel.Result result, String roomId, String body){
        Room room = SessionHolder.matrixSession.getRoom(roomId);
        room.sendTextMessage(body, MessageType.MSGTYPE_TEXT,false);
        result.success(true);
    }

}
