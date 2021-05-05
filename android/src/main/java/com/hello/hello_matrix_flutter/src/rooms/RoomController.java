package com.hello.hello_matrix_flutter.src.rooms;

import android.media.ExifInterface;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.hello.hello_matrix_flutter.HelloMatrixFlutterPluginMethodChannel;
import com.hello.hello_matrix_flutter.src.auth.SessionHolder;
import com.hello.hello_matrix_flutter.src.timeline.ImagePicker;
import com.hello.hello_matrix_flutter.src.timeline.TimeLineController;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.Matrix;
import org.matrix.android.sdk.api.MatrixCallback;
import org.matrix.android.sdk.api.session.content.ContentAttachmentData;
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker;
import org.matrix.android.sdk.api.session.room.Room;
import org.matrix.android.sdk.api.session.room.model.create.CreateRoomParams;
import org.matrix.android.sdk.api.session.room.model.message.MessageType;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.flutter.Log;
import io.flutter.plugin.common.MethodChannel;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
//import org.matrix.android.sdk.internal.session.content.*;

public class RoomController {


    String _tag = "RoomController";
    TimeLineController timeLineController;

    public void createDirectRoom(@NonNull final MethodChannel.Result result, final String userId, String roomName) {
        String existingRoom = SessionHolder.matrixSession.getExistingDirectRoomWithUser(userId);
        if (existingRoom != null) {
            Log.i(_tag, "room already exist" + " " + existingRoom);
            result.success(existingRoom);
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

    public void sendSimpleTextMessage(@NonNull final MethodChannel.Result result, String roomId, String body) {
        Room room = SessionHolder.matrixSession.getRoom(roomId);
        room.sendTextMessage(body, MessageType.MSGTYPE_TEXT, false);
        result.success(true);
    }

    public void sendImageMessage(String roomId, long size, long date, long height, long width, String name, String path) {
        Room room = SessionHolder.matrixSession.getRoom(roomId);
        //setting content media
        ContentAttachmentData contentAttachmentData = new ContentAttachmentData(
                size,
                null,
                date,
                height,
                width,
                ExifInterface.ORIENTATION_UNDEFINED,
                name,
                Uri.parse(path),
                null,
                ContentAttachmentData.Type.IMAGE
        );
        Set<String> roomIds = new HashSet<>();
        //roomIds.add(roomId);

        if(SessionHolder.matrixSession.getHomeServerCapabilities().getMaxUploadFileSize()>size){
            room.sendMedia(contentAttachmentData, false, roomIds);
            HelloMatrixFlutterPluginMethodChannel.getInstance().result.success(true);
        }else{
            Log.i(_tag,"MaxUploadSizeReached");
        }


        /*SessionHolder.matrixSession.contentUploadProgressTracker().track("", new ContentUploadStateTracker.UpdateListener() {
            @Override
            public void onUpdate(ContentUploadStateTracker.@NotNull State state) {
                Log.i(_tag, state.toString());
            }
        });*/
    }

    public void createTimeLine(String roomId) {
        timeLineController = new TimeLineController(roomId);
    }

    public TimeLineController getTimeLineController() {
        return this.timeLineController;
    }

    public void joinRoom(@NonNull final MethodChannel.Result result, String roomId) {
        Room room = SessionHolder.matrixSession.getRoom(roomId);
        List<String> servers = new ArrayList<>();
        room.join("", servers, new Continuation<Unit>() {
            @Override
            public @NotNull CoroutineContext getContext() {
                return null;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                result.success(true);
            }
        });
    }

    public void destroyTimeLine() {
        timeLineController.destroyTimeLine();
        timeLineController = null;
    }

}
