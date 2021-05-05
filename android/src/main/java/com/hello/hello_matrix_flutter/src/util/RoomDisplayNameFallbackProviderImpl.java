package com.hello.hello_matrix_flutter.src.util;

import org.jetbrains.annotations.NotNull;
import org.matrix.android.sdk.api.RoomDisplayNameFallbackProvider;

import java.util.List;

public class RoomDisplayNameFallbackProviderImpl implements RoomDisplayNameFallbackProvider {
    @Override
    public @NotNull String getNameFor1member(@NotNull String s) {
        return s;
    }

    @Override
    public @NotNull String getNameFor2members(@NotNull String s, @NotNull String s1) {
        return s + " and " + s1;
    }

    @Override
    public @NotNull String getNameFor3members(@NotNull String s, @NotNull String s1, @NotNull String s2) {
        return s + ", " + s1 + ", and " + s2;
    }

    @Override
    public @NotNull String getNameFor4members(@NotNull String s, @NotNull String s1, @NotNull String s2, @NotNull String s3) {
        return s + ", " + s1 + ",  " + s2 + " and " + s3;
    }

    @Override
    public @NotNull String getNameFor4membersAndMore(@NotNull String s, @NotNull String s1, @NotNull String s2, int i) {
        return s + ", " + s1 + ",  " + s2 + " and " + i + " others";
    }

    @Override
    public @NotNull String getNameForEmptyRoom(boolean b, @NotNull List<String> list) {
        return "Empty room";
    }

    @Override
    public @NotNull String getNameForRoomInvite() {
        return "Room invite";
    }
}
