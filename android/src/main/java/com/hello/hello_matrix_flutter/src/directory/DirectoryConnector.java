package com.hello.hello_matrix_flutter.src.directory;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.hello.hello_matrix_flutter.src.auth.SessionHolder;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.List;
import java.util.stream.Collectors;

public class DirectoryConnector {
    static String _tag = "DirectoryConnector";

    public static UserProfile pullUserProfile(String mxUserId) {
        String homeServerHost = SessionHolder.matrixSession.getSessionParams().getHomeServerHost();
        final String helloId = mxUserId.replace(homeServerHost, "").replace(":", "").replace("@", "");
        List<UserProfile> directory = DirectoryInstance.getInstance().getDirectoryList();
        if (directory.isEmpty()) {
            return null;
        } else {
            UserProfile userProfile = IterableUtils.find(directory, new Predicate<UserProfile>() {
                @Override
                public boolean evaluate(UserProfile object) {
                    return object.helloId.equals(helloId);
                }
            });
            if (userProfile == null) {
                return null;
            } else {
                //user profile found, return
                return userProfile;

            }
        }
    }
}
