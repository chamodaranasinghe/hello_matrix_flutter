package com.hello.hello_matrix_flutter.src.rooms;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams;
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomPreset;
import org.matrix.android.sdk.api.session.room.model.message.MessageType;

import java.util.ArrayList;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;
import kotlin.Unit;

public class RoomController {


    String _tag = "RoomController";
    ChatTimeLine chatTimeLine;

    public void createDirectRoom(@NonNull final MethodChannel.Result result, final String userId, String roomName) {
        Room existingRoom = SessionHolder.matrixSession.getExistingDirectRoomWithUser(userId);
        if(existingRoom!=null){
            Log.i(_tag, "room already exist"+" "+existingRoom.getRoomId());
            result.success(existingRoom.getRoomId());
            return;
        }

        Log.i(_tag, userId);
        CreateRoomParams createRoomParams = new CreateRoomParams();
        createRoomParams.setName(roomName);
        //createRoomParams.enableEncryption();
        createRoomParams.setDirectMessage();
        createRoomParams.getInvitedUserIds().add(userId);
        createRoomParams.setEnableEncryptionIfInvitedUsersSupportIt(false);

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

    public void createTimeLine(String roomId){
        chatTimeLine = new ChatTimeLine(roomId);
    }

    public void joinRoom(@NonNull final MethodChannel.Result result,String roomId){
        Room room = SessionHolder.matrixSession.getRoom(roomId);
        room.join("", new ArrayList<String>(), new MatrixCallback<Unit>() {
            @Override
            public void onSuccess(Unit unit) {
                result.success(true);
                Log.i("Unit",unit.toString());
            }

            @Override
            public void onFailure(@NotNull Throwable throwable) {
                result.success(false);
            }
        });
    }

    public void destroyTimeLine(){
        chatTimeLine.destroyTimeLine();
        chatTimeLine = null;
    }

}
